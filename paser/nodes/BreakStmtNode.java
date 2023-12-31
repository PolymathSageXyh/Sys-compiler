package paser.nodes;

import error.ErrorCheckContext;
import error.ErrorCheckReturn;
import error.ErrorType;
import lightllr.AstVisitor;
import paser.Mypair;

import java.util.ArrayList;

public class BreakStmtNode extends Node {
    @Override
    public StringBuilder getPaserLog() {
        for (Node child : children) {
            this.paserLog.append(child.getPaserLog());
        }
        return this.paserLog;
    }

    @Override
    public void checkError(ArrayList<Mypair<ErrorType, Integer>> errorList, ErrorCheckContext ctx, ErrorCheckReturn ret) {
        if (ctx.loopLevel == 0) {
            errorList.add(Mypair.of(ErrorType.BREAK_CONTINUE_OUT_LOOP, children.get(0).getStartLine()));
        }
    }

    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

}
