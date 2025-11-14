/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata;

import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import java.util.List;

public class StaticScores {
    public static final int VIEW_POINT_WHITE = 0;
    public static final int VIEW_POINT_BLACK = 1;
    public static final int SLOT_MATERIAL = 0;
    public static final int SLOT_MATERIAL_WIN = 1;
    public static final int SLOT_MATERIAL_LOSS = 2;
    public static final int SLOT_CONTROL = 3;
    public static final int SLOT_ATTACK = 4;
    public static final int SLOT_ALL = 5;
    public static final int SLOT_MOBILITY = 6;
    private static final int BONUS_ATTACK_QUEEN = 1;
    private static final int BONUS_ATTACK_ROOK = 2;
    private static final int BONUS_ATTACK_MINOR = 3;
    private static final int BONUS_CONTROL_QUEEN = 1;
    private static final int BONUS_CONTROL_ROOK = 3;
    private static final int BONUS_CONTROL_MINOR = 5;
    private FieldStateMachine machine;
    private List<FieldState> states;
    private int[][][] scores;

    public StaticScores(FieldStateMachine _machine) {
        this.machine = _machine;
        this.states = this.machine.getStates();
        this.scores = new int[3][7][this.states.size()];
        System.out.print("Start static scores calculation ... ");
        long start = System.currentTimeMillis();
        this.init();
        long end = System.currentTimeMillis();
        System.out.println("Ready! Time: " + (end - start) + " ms");
    }

    public int getScore(int colour, int slot, int stateID) {
        if (colour == 0) {
            return this.scores[0][slot][stateID];
        }
        return this.scores[1][slot][stateID];
    }

    private void addScores(int viewpoint, int slot, int stateID, int _scores) {
        int[] nArray = this.scores[viewpoint][slot];
        int n = stateID;
        nArray[n] = nArray[n] + _scores;
        int[] nArray2 = this.scores[viewpoint][5];
        int n2 = stateID;
        nArray2[n2] = nArray2[n2] + _scores;
    }

    private void init() {
        List<FieldState> states = this.machine.getStates();
        for (int i = 0; i < states.size(); ++i) {
            FieldState currentState = states.get(i);
            int figColour = currentState.getFigureOnFieldColour();
            int figType = currentState.getFigureOnFieldType();
            FieldAttacks whiteA = currentState.getWhiteAttacks();
            FieldAttacks blackA = currentState.getBlackAttacks();
            if (figColour != -1) {
                int blocked;
                int see_win;
                int materialScores = this.calcMaterialScores(figType);
                if (figColour == 0) {
                    this.addScores(0, 0, i, materialScores);
                    this.addScores(1, 0, i, -materialScores);
                } else {
                    this.addScores(1, 0, i, materialScores);
                    this.addScores(0, 0, i, -materialScores);
                }
                if (figColour == 0) {
                    see_win = -this.calcSEEScores(figType, whiteA, blackA);
                    if (see_win > 0) {
                        this.addScores(1, 1, i, see_win);
                        this.addScores(0, 2, i, -40);
                        continue;
                    }
                    blocked = this.calcBlockedPiecesScores(figType, whiteA, blackA);
                    this.addScores(0, 2, i, -blocked);
                    continue;
                }
                see_win = -this.calcSEEScores(figType, blackA, whiteA);
                if (see_win > 0) {
                    this.addScores(0, 1, i, see_win);
                    this.addScores(1, 2, i, -40);
                    continue;
                }
                blocked = this.calcBlockedPiecesScores(figType, blackA, whiteA);
                this.addScores(1, 2, i, -blocked);
                continue;
            }
            int w_safeAttacks = this.calcSafeAttacksScores(whiteA, blackA);
            int b_safeAttacks = this.calcSafeAttacksScores(blackA, whiteA);
            this.addScores(0, 6, i, w_safeAttacks);
            this.addScores(0, 6, i, -b_safeAttacks);
            this.addScores(1, 6, i, b_safeAttacks);
            this.addScores(1, 6, i, -w_safeAttacks);
            int w_nonSafeAttacks = this.calcAttacksScores(whiteA);
            int b_nonSafeAttacks = this.calcAttacksScores(blackA);
            this.addScores(0, 6, i, w_nonSafeAttacks);
            this.addScores(0, 6, i, -b_nonSafeAttacks);
            this.addScores(1, 6, i, b_nonSafeAttacks);
            this.addScores(1, 6, i, -w_nonSafeAttacks);
        }
    }

    private int calcMaterialScores(int figType) {
        return BaseEvalWeights.getFigureCost(figType);
    }

    private int calcSEEScores(int figType, FieldAttacks attacked, FieldAttacks toPlay) {
        byte result = 0;
        result = -SeeMetadata.FIELD_SEE[figType][attacked.getId()][toPlay.getId()];
        return result;
    }

    private int calcBlockedPiecesScores(int figType, FieldAttacks attacked, FieldAttacks toPlay) {
        int points;
        int result = 0;
        FieldAttacks curAttacked_backup = null;
        FieldAttacks curAttacked = attacked;
        while ((points = -this.calcSEEScores(figType, curAttacked, toPlay)) <= 0) {
            int typeToRemove = curAttacked.getMaxType();
            if (typeToRemove == 0) {
                return 0;
            }
            int curAttackedState = curAttacked.getId();
            curAttackedState = FieldAttacksStateMachine.getInstance().getMachine()[1][typeToRemove][curAttackedState];
            curAttacked_backup = curAttacked;
            curAttacked = FieldAttacksStateMachine.getInstance().getAllStatesList()[curAttackedState];
        }
        if (curAttacked_backup != null) {
            result += 10 * curAttacked_backup.qaCount();
            result += 14 * curAttacked_backup.raCount();
            result += 18 * curAttacked_backup.maCount();
        }
        return result;
    }

    private int calcSafeAttacksScores(FieldAttacks toMove, FieldAttacks other) {
        boolean safeToGo;
        int result = 0;
        if (toMove.maCount() > 0) {
            boolean bl = safeToGo = -SeeMetadata.getSingleton().seeMove(3, 3, toMove.getId(), other.getId()) >= 0;
            if (safeToGo) {
                result += 5 * toMove.maCount();
            }
        }
        if (toMove.raCount() > 0) {
            boolean bl = safeToGo = -SeeMetadata.getSingleton().seeMove(4, 4, toMove.getId(), other.getId()) >= 0;
            if (safeToGo) {
                result += 3 * toMove.raCount();
            }
        }
        if (toMove.qaCount() > 0) {
            boolean bl = safeToGo = -SeeMetadata.getSingleton().seeMove(5, 5, toMove.getId(), other.getId()) >= 0;
            if (safeToGo) {
                result += 1 * toMove.qaCount();
            }
        }
        return result;
    }

    private int calcAttacksScores(FieldAttacks state) {
        int result = 0;
        result += 3 * state.maCount();
        result += 2 * state.raCount();
        return result += 1 * state.qaCount();
    }
}

