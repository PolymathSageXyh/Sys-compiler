package lightllr.optimization;

import lightllr.*;
import lightllr.Module;

import java.util.*;

public class ConstPropagation extends Pass {

    public ConstPropagation(Module module) {
        super(module);
    }

    @Override
    public void run() {
        ConstFolder CF = new ConstFolder(module);
        for (Function func : module.getFunctions()) {
            if (func.getNumBasicBlocks() == 0) continue;
            HashSet<BasicBlock> visitedBB = new HashSet<>();
            ArrayDeque<BasicBlock> bbQueue = new ArrayDeque<>();
            bbQueue.push(func.getEntryBB());
            visitedBB.add(func.getEntryBB());
            while (!bbQueue.isEmpty()) {
                BasicBlock bb = bbQueue.pollFirst();
                if (bb.isInstrListEmpty()) continue;
                HashSet<Instruction> instrToBeDelete = new HashSet<>();
                HashMap<Value, Stack<Value>> pointerConst = new HashMap<>();
                for (Instruction instr: bb.getInstrList()) {
                    if (instr.isBinary()) {
                        Instruction.OpID op = instr.getInstrType();
                        Value lhs = instr.getOperand(0);
                        Value rhs = instr.getOperand(1);
                        if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
                            instr.replace_all_use_with(CF.compute(op, (ConstantInt) lhs, (ConstantInt) rhs));
                            instrToBeDelete.add(instr);
                        }
                    } else if (instr.isZext()) {
                        if ((instr.getOperand(0)) instanceof ConstantInt) {
                            instr.replace_all_use_with(ConstantInt.get
                                    (((ConstantInt)instr.getOperand(0)).getTruth(), module));
                            instrToBeDelete.add(instr);
                        }
                    } else if (instr.isCmp()) {
                        Value lhs = instr.getOperand(0);
                        Value rhs = instr.getOperand(1);
                        if (lhs instanceof ConstantInt && rhs instanceof ConstantInt) {
                            instr.replace_all_use_with(CF.compute_comp(((CmpInstr)instr).getCmpOp(), (ConstantInt) lhs, (ConstantInt) rhs));
                            instrToBeDelete.add(instr);
                        }
                    } else if (instr.isBr()) {
                        if (((BranchInstr)instr).is_cond_br()) {
                            BasicBlock trueBB =  ((BasicBlock)(instr.getOperand(1)));
                            BasicBlock falseBB = ((BasicBlock)(instr.getOperand(2)));
                            if (!visitedBB.contains(trueBB)) {
                                visitedBB.add(trueBB);
                                bbQueue.push(trueBB);
                            }
                            if (!visitedBB.contains(falseBB)) {
                                visitedBB.add(falseBB);
                                bbQueue.push(falseBB);
                            }
                        } else {
                            BasicBlock trueBB = (BasicBlock)(instr.getOperand(0));
                            if (!visitedBB.contains(trueBB)) {
                                visitedBB.add(trueBB);
                                bbQueue.push(trueBB);
                            }
                        }

                    }
                }
                for (Instruction instr : instrToBeDelete){
                    bb.deleteInstr(instr);
                }

            }

        }

    }

    public class ConstFolder {
        private Module module;

        public ConstFolder(Module module) {
            this.module = module;
        }

        public ConstantInt compute(Instruction.OpID op, ConstantInt value1, ConstantInt value2) {
            int c_value1 = value1.getTruth();
            int c_value2 = value2.getTruth();
            return switch (op) {
                case add -> ConstantInt.get(c_value1 + c_value2, module);
                case sub -> ConstantInt.get(c_value1 - c_value2, module);
                case mul -> ConstantInt.get(c_value1 * c_value2, module);
                case sdiv -> ConstantInt.get(c_value1 / c_value2, module);
                case srem -> ConstantInt.get(c_value1 % c_value2, module);
                default -> null;
            };
        }

        public ConstantInt compute_comp(CmpInstr.CmpOp op, ConstantInt value1, ConstantInt value2) {
            int c_value1 = value1.getTruth();
            int c_value2 = value2.getTruth();
            return switch (op) {
                case EQ -> ConstantInt.get(c_value1 == c_value2, module);
                case NE -> ConstantInt.get(c_value1 != c_value2, module);
                case GT -> ConstantInt.get(c_value1 > c_value2, module);
                case GE -> ConstantInt.get(c_value1 >= c_value2, module);
                case LT -> ConstantInt.get(c_value1 < c_value2, module);
                case LE -> ConstantInt.get(c_value1 <= c_value2, module);
            };
        }

    }

}
