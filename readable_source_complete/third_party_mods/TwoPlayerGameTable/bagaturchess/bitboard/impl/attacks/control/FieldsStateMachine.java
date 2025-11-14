/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control;

import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateMachine;

public class FieldsStateMachine
implements IFieldsAttacks {
    private static final boolean SUPPORT_TOTAL_STATES = false;
    private static final boolean TRACE_TRANSISIONS = false;
    private int[][][] machine = FieldAttacksStateMachine.getInstance().getMachine();
    private FieldAttacks[] states = FieldAttacksStateMachine.getInstance().getAllStatesList();
    int[] KS_TABLE = new int[]{0, 2, 3, 6, 12, 18, 25, 37, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 525, 550, 575, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600};
    int[] KS_TABLE_FLAGS = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 2, 2, 2, 3, 3, 3, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3};
    private int[][] controlPointsByColourAndField;
    private long[] controlBitboards;
    private long[][] hangingBitboards;
    private Board bitboard;
    FieldStateMachine totalMachine;
    private int[] totalFieldsStates;
    private int total_whiteScores = 0;
    private int total_blackScores = 0;
    private int[][] scores = new int[2][7];
    private IAttackListener attackListner;

    public FieldsStateMachine(Board _bitboard, IAttackListener _attackListner) {
        this.bitboard = _bitboard;
        this.attackListner = _attackListner;
        this.controlPointsByColourAndField = new int[Figures.COLOUR_MAX][64];
        this.controlBitboards = new long[Figures.COLOUR_MAX];
        this.hangingBitboards = new long[Figures.COLOUR_MAX][7];
    }

    public void removeAttack(int colour, int type, int fieldID, long fieldBitboard) {
        if (this.attackListner != null) {
            this.attackListner.removeAttack(colour, type, fieldID, fieldBitboard);
        }
        byte opColour = Figures.OPPONENT_COLOUR[colour];
        int[] alist = this.controlPointsByColourAndField[colour];
        int[] oalist = this.controlPointsByColourAndField[opColour];
        int currentState = alist[fieldID];
        int nextState = this.machine[1][type][currentState];
        if (nextState == -1) {
            String msg = "Current state: " + String.valueOf(this.states[currentState]) + ", type " + Figures.TYPES_SIGN[type];
            throw new IllegalStateException(msg);
        }
        alist[fieldID] = nextState;
        int opState = oalist[fieldID];
        if (nextState < opState) {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] | fieldBitboard;
        } else if (nextState > opState) {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] | fieldBitboard;
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
        } else {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public void addAttack(int colour, int type, int fieldID, long fieldBitboard) {
        if (this.attackListner != null) {
            this.attackListner.addAttack(colour, type, fieldID, fieldBitboard);
        }
        byte opColour = Figures.OPPONENT_COLOUR[colour];
        int[] alist = this.controlPointsByColourAndField[colour];
        int[] oalist = this.controlPointsByColourAndField[opColour];
        int currentState = alist[fieldID];
        int nextState = this.machine[0][type][currentState];
        if (nextState == -1) {
            String msg = "Current state: " + String.valueOf(this.states[currentState]) + ", type " + Figures.TYPES_SIGN[type];
            throw new IllegalStateException(msg);
        }
        alist[fieldID] = nextState;
        int opState = oalist[fieldID];
        if (nextState > opState) {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] | fieldBitboard;
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
        } else if (nextState < opState) {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] | fieldBitboard;
        } else {
            int n = colour;
            this.controlBitboards[n] = this.controlBitboards[n] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
            byte by = opColour;
            this.controlBitboards[by] = this.controlBitboards[by] & (fieldBitboard ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public void removeFigure(int colour, int type, int fieldID) {
    }

    public void addFigure(int colour, int type, int fieldID) {
    }

    private void updateTotalMachine(int fieldID, int oldState, int newState) {
        this.updateTotalMachineSlot(1, 5, oldState, newState);
        this.updateTotalMachineSlot(0, 5, oldState, newState);
        this.updateTotalMachineSlot(1, 6, oldState, newState);
        this.updateTotalMachineSlot(0, 6, oldState, newState);
    }

    private void updateTotalMachineSlot(int colour, int slot, int oldState, int newState) {
        int[] nArray = this.scores[colour];
        int n = slot;
        nArray[n] = nArray[n] - this.totalMachine.getScores().getScore(colour, slot, oldState);
        int[] nArray2 = this.scores[colour];
        int n2 = slot;
        nArray2[n2] = nArray2[n2] + this.totalMachine.getScores().getScore(colour, slot, newState);
    }

    @Override
    public int getScore_BeforeMove(int colour) {
        return this.scores[colour][5];
    }

    @Override
    public int getScore_AfterMove(int colour) {
        byte opColour = Figures.OPPONENT_COLOUR[colour];
        return -this.scores[opColour][5];
    }

    @Override
    public int getScore_ForEval(int colour) {
        if (colour == 0) {
            return this.scores[0][6];
        }
        return 0;
    }

    public void clearScore() {
        throw new IllegalStateException();
    }

    public String transToString(int state, int op) {
        Object result = FieldStateMachine.TRANSITION_SIGN[op];
        result = (String)result + " | " + this.totalMachine.stateToString(state);
        return result;
    }

    @Override
    public int[] getControlArray(int colour) {
        return this.controlPointsByColourAndField[colour];
    }

    @Override
    public long getControlBitboard(int colour) {
        return this.controlBitboards[colour];
    }

    public long getPotentiallyHangingPieces(int colour, int type) {
        throw new IllegalStateException();
    }

    public void checkConsistency() {
        IPlayerAttacks whiteAttacks = this.bitboard.getPlayerAttacks(0);
        IPlayerAttacks blackAttacks = this.bitboard.getPlayerAttacks(1);
        for (int fieldID = 0; fieldID < Fields.ALL_A1H1.length; ++fieldID) {
            long field = Fields.ALL_A1H1[fieldID];
            int whiteState = this.controlPointsByColourAndField[0][fieldID];
            int blackState = this.controlPointsByColourAndField[1][fieldID];
            FieldAttacks whiteObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteState];
            FieldAttacks blackObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[blackState];
            this.check(whiteObj, whiteAttacks, 1, field);
            this.checkMinors(whiteObj, whiteAttacks, field);
            this.check(whiteObj, whiteAttacks, 4, field);
            this.check(whiteObj, whiteAttacks, 5, field);
            this.check(whiteObj, whiteAttacks, 6, field);
            this.check(blackObj, blackAttacks, 1, field);
            this.checkMinors(blackObj, blackAttacks, field);
            this.check(blackObj, blackAttacks, 4, field);
            this.check(blackObj, blackAttacks, 5, field);
            this.check(blackObj, blackAttacks, 6, field);
        }
    }

    private void check(FieldAttacks state, IPlayerAttacks playerAttacks, int type, long field) {
        int fromA_count = playerAttacks.countAttacks(type, field);
        int fromF_count = -1;
        switch (type) {
            case 1: {
                fromF_count = state.paCount();
                break;
            }
            case 2: {
                fromF_count = state.knaCount();
                break;
            }
            case 3: {
                fromF_count = state.oaCount();
                break;
            }
            case 4: {
                fromF_count = state.raCount();
                break;
            }
            case 5: {
                fromF_count = state.qaCount();
                break;
            }
            case 6: {
                fromF_count = state.kaCount();
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        if (fromA_count != fromF_count) {
            throw new IllegalStateException(Figures.TYPES_SIGN[type] + ": fromA_count=" + fromA_count + ", fromF_count=" + fromF_count);
        }
    }

    private void checkMinors(FieldAttacks state, IPlayerAttacks playerAttacks, long field) {
        int fromF_count;
        int fromA_count = playerAttacks.countAttacks(3, field) + playerAttacks.countAttacks(2, field);
        if (fromA_count != (fromF_count = state.maCount())) {
            throw new IllegalStateException("Minors: fromA_count=" + fromA_count + ", fromF_count=" + fromF_count);
        }
    }
}

