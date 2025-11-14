/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateFactory;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateMachine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

class FieldSMGenerator
implements Serializable {
    private final int SIZE = 12 * FieldAttacksStateMachine.getInstance().getAllStatesList().length * FieldAttacksStateMachine.getInstance().getAllStatesList().length;
    private ArrayList<FieldState> states = new ArrayList(this.SIZE);
    private HashMap<FieldState, FieldState> allStatesMap;

    FieldSMGenerator() {
        this.init();
    }

    public int statesCount() {
        return this.states.size();
    }

    public void generateStates() {
        int idSeq = 0;
        for (int colour = -1; colour < Figures.COLOUR_MAX; ++colour) {
            if (colour == -1) {
                for (int whiteAttacksID = 0; whiteAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length; ++whiteAttacksID) {
                    for (int blackAttacksID = 0; blackAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length; ++blackAttacksID) {
                        FieldState state = new FieldState(idSeq++, -1, 0, FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteAttacksID], FieldAttacksStateMachine.getInstance().getAllStatesList()[blackAttacksID]);
                        this.states.add(state);
                    }
                }
                continue;
            }
            for (int type = 1; type < 7; ++type) {
                for (int whiteAttacksID = 0; whiteAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length; ++whiteAttacksID) {
                    for (int blackAttacksID = 0; blackAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length; ++blackAttacksID) {
                        FieldState state = new FieldState(idSeq++, colour, type, FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteAttacksID], FieldAttacksStateMachine.getInstance().getAllStatesList()[blackAttacksID]);
                        this.states.add(state);
                    }
                }
            }
        }
    }

    public void indexingStates() {
        this.allStatesMap = new HashMap(2 * this.states.size());
        for (int i = 0; i < this.states.size(); ++i) {
            FieldState cur = this.states.get(i);
            this.allStatesMap.put(cur, cur);
            if (i % 100000 != 0) continue;
            System.out.print(".");
        }
        System.out.print(" ");
    }

    public FieldStateMachine createStateMachine() {
        FieldStateMachine machine = FieldStateMachine.getInstanceForGen(this.states.size());
        for (int i = 0; i < this.states.size(); ++i) {
            FieldState cur = this.states.get(i);
            this.processSingleState(machine, 0, 0, cur);
            this.processSingleState(machine, 0, 1, cur);
            this.processSingleState(machine, 1, 0, cur);
            this.processSingleState(machine, 1, 1, cur);
            this.processSingleState(machine, 2, 0, cur);
            this.processSingleState(machine, 2, 1, cur);
            this.processSingleState(machine, 3, 0, cur);
            this.processSingleState(machine, 3, 1, cur);
            if (i % 100000 != 0) continue;
            System.out.print(".");
        }
        System.out.print(" ");
        return machine;
    }

    private void processSingleState(FieldStateMachine machine, int op, int colour, FieldState cur) {
        for (int type = 1; type < 7; ++type) {
            FieldState next = FieldStateFactory.modify(op, colour, type, cur);
            if (next != null) {
                next = this.allStatesMap.get(next);
                machine.getMachine()[colour][type][op][cur.id] = next.id;
                continue;
            }
            machine.getMachine()[colour][type][op][cur.id] = -1;
        }
    }

    public void init() {
        System.out.print("Start states generation ... ");
        long start = System.currentTimeMillis();
        this.generateStates();
        long end = System.currentTimeMillis();
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long usedMemory = max - (free + (max - total));
        System.out.println("Ready!");
        System.out.println("Time " + (end - start) + "ms");
        System.out.println("Used memory " + usedMemory / 0x100000L + "MB");
        System.out.println("Total States Count: " + this.states.size());
        System.out.print("\r\n");
        System.out.print("Indexing ");
        start = System.currentTimeMillis();
        this.indexingStates();
        end = System.currentTimeMillis();
        max = Runtime.getRuntime().maxMemory();
        total = Runtime.getRuntime().totalMemory();
        free = Runtime.getRuntime().freeMemory();
        usedMemory = max - (free + (max - total));
        System.out.println("Ready!");
        System.out.println("Time " + (end - start) + "ms");
        System.out.println("Used memory " + usedMemory / 0x100000L + "MB");
        System.out.println("Total States Count: " + this.allStatesMap.size());
        System.out.print("\r\n");
        System.out.print("Creating StateMachine ");
        FieldStateMachine machine = this.createStateMachine();
        machine.setStates(this.states);
        System.out.println("Ready!");
        System.out.print("Serializing ... ");
        machine.serialize();
        System.out.println("Ready!");
    }

    public static void main(String[] args) {
        FieldSMGenerator generator = new FieldSMGenerator();
    }
}

