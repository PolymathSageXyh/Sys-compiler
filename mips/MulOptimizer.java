package mips;

import paser.Mypair;

import java.util.ArrayList;

public class MulOptimizer {
    private int steps;
    private int multiplier;
    private final ArrayList<Mypair<Boolean, Integer>> items;

    public MulOptimizer(int... shifts) {
        multiplier = 0;
        items = new ArrayList<>();
        for (int shift : shifts) {
            if (shift >= 0) {
                multiplier += 1 << shift;
                items.add(Mypair.of(true, shift & Integer.MAX_VALUE));
            }
            else {
                multiplier -= 1 << (shift & Integer.MAX_VALUE);
                items.add(Mypair.of(false, shift & Integer.MAX_VALUE));
            }
        }
        steps = items.get(0).getFirst() || items.get(0).getSecond() == 0 ? 1 : 2;
        for (int i = 1; i < items.size(); i++) {
            steps += items.get(i).getSecond() == 0 ? 1 : 2;
        }
    }

    public ArrayList<Mypair<Boolean, Integer>> getItems() {
        return items;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getSteps() {
        return steps;
    }
}

