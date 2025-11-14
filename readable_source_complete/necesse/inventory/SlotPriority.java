/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.engine.util.ComparableSequence;

public class SlotPriority {
    public final int slot;
    public final ComparableSequence<Integer> comparable;

    public SlotPriority(int slot, ComparableSequence<Integer> comparable) {
        this.slot = slot;
        this.comparable = comparable;
    }
}

