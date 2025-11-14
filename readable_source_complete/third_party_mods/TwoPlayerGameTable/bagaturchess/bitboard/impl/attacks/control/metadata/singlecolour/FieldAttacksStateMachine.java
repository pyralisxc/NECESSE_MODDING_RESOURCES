/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksSMGenerator;
import java.util.HashMap;

public class FieldAttacksStateMachine {
    FieldAttacks[] allStatesList;
    HashMap<FieldAttacks, FieldAttacks> allStatesMap;
    int[][][] machine;
    private static FieldAttacksStateMachine singleton;

    private FieldAttacksStateMachine() {
        FieldAttacksSMGenerator.getAllFieldsAttacks(this);
        FieldAttacksSMGenerator.createStateMachine(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final FieldAttacksStateMachine getInstance() {
        if (singleton != null) return singleton;
        Class<FieldAttacksStateMachine> clazz = FieldAttacksStateMachine.class;
        synchronized (FieldAttacksStateMachine.class) {
            if (singleton != null) return singleton;
            singleton = new FieldAttacksStateMachine();
            // ** MonitorExit[var0] (shouldn't be in output)
            return singleton;
        }
    }

    public FieldAttacks[] getAllStatesList() {
        return this.allStatesList;
    }

    public int[][][] getMachine() {
        return this.machine;
    }
}

