/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;

public class FieldStateFactory {
    private static FieldAttacksStateMachine attacksMachine = FieldAttacksStateMachine.getInstance();

    public static FieldState modify(int operation, int figureColour, int figureType, FieldState source) {
        FieldState result = source.clone();
        switch (operation) {
            case 0: {
                FieldStateFactory.modify_addAttack(result, figureColour, figureType);
                break;
            }
            case 1: {
                FieldStateFactory.modify_remAttack(result, figureColour, figureType);
                break;
            }
            case 2: {
                FieldStateFactory.modify_addFigure(result, figureColour, figureType);
                break;
            }
            case 3: {
                FieldStateFactory.modify_remFigure(result, figureColour, figureType);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return result;
    }

    public static FieldState modify_addAttack(FieldState result, int figureColour, int figureType) {
        if (figureColour == 0) {
            int currentWhiteAttackID = result.whiteAttacks.getId();
            int nextWhiteAttackID = attacksMachine.getMachine()[0][figureType][currentWhiteAttackID];
            if (nextWhiteAttackID == -1) {
                result = null;
            } else {
                FieldAttacks nextAttacksObj;
                result.whiteAttacks = nextAttacksObj = attacksMachine.getAllStatesList()[nextWhiteAttackID];
            }
        } else if (figureColour == 1) {
            int currentBlackAttackID = result.blackAttacks.getId();
            int nextBlackAttackID = attacksMachine.getMachine()[0][figureType][currentBlackAttackID];
            if (nextBlackAttackID == -1) {
                result = null;
            } else {
                FieldAttacks nextAttacksObj;
                result.blackAttacks = nextAttacksObj = attacksMachine.getAllStatesList()[nextBlackAttackID];
            }
        } else {
            throw new IllegalStateException();
        }
        return result;
    }

    public static FieldState modify_remAttack(FieldState result, int figureColour, int figureType) {
        if (figureColour == 0) {
            int currentWhiteAttackID = result.whiteAttacks.getId();
            int nextWhiteAttackID = attacksMachine.getMachine()[1][figureType][currentWhiteAttackID];
            if (nextWhiteAttackID == -1) {
                result = null;
            } else {
                FieldAttacks nextAttacksObj;
                result.whiteAttacks = nextAttacksObj = attacksMachine.getAllStatesList()[nextWhiteAttackID];
            }
        } else if (figureColour == 1) {
            int currentBlackAttackID = result.blackAttacks.getId();
            int nextBlackAttackID = attacksMachine.getMachine()[1][figureType][currentBlackAttackID];
            if (nextBlackAttackID == -1) {
                result = null;
            } else {
                FieldAttacks nextAttacksObj;
                result.blackAttacks = nextAttacksObj = attacksMachine.getAllStatesList()[nextBlackAttackID];
            }
        } else {
            throw new IllegalStateException();
        }
        return result;
    }

    public static FieldState modify_addFigure(FieldState result, int figureColour, int figureType) {
        byte opColour = Figures.OPPONENT_COLOUR[figureColour];
        if (result.figureOnFieldColour == -1 || result.figureOnFieldColour == opColour) {
            result.figureOnFieldColour = figureColour;
            result.figureOnFieldType = figureType;
        } else {
            result = null;
        }
        return result;
    }

    public static FieldState modify_remFigure(FieldState result, int figureColour, int figureType) {
        if (result.figureOnFieldColour != -1 && result.figureOnFieldColour == figureColour) {
            result.figureOnFieldColour = -1;
            result.figureOnFieldType = 0;
        } else {
            result = null;
        }
        return result;
    }
}

