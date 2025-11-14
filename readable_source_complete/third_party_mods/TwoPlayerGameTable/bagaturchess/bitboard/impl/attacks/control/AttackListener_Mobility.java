/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control;

import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.common.Utils;

public class AttackListener_Mobility
implements IAttackListener,
IMobility {
    private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
    private IBoardConfig boardConfig;
    private int w_mobility_o;
    private int w_mobility_e;
    private int b_mobility_o;
    private int b_mobility_e;

    public AttackListener_Mobility(IBoardConfig _boardConfig) {
        this.boardConfig = _boardConfig;
    }

    @Override
    public int getMobility_o() {
        return this.w_mobility_o - this.b_mobility_o;
    }

    @Override
    public int getMobility_e() {
        return this.w_mobility_e - this.b_mobility_e;
    }

    @Override
    public void addAttack(int colour, int type, int fieldID, long fieldBitboard) {
        if (colour == 0) {
            this.w_mobility_o += this.getScores_O(colour, type, fieldID);
            this.w_mobility_e += this.getScores_E(colour, type, fieldID);
        } else {
            this.b_mobility_o += this.getScores_O(colour, type, fieldID);
            this.b_mobility_e += this.getScores_E(colour, type, fieldID);
        }
    }

    @Override
    public void removeAttack(int colour, int type, int fieldID, long fieldBitboard) {
        if (colour == 0) {
            this.w_mobility_o -= this.getScores_O(colour, type, fieldID);
            this.w_mobility_e -= this.getScores_E(colour, type, fieldID);
        } else {
            this.b_mobility_o -= this.getScores_O(colour, type, fieldID);
            this.b_mobility_e -= this.getScores_E(colour, type, fieldID);
        }
    }

    private int getScores_O(int colour, int type, int fieldID) {
        int result = 0;
        if (colour == 1) {
            fieldID = HORIZONTAL_SYMMETRY[fieldID];
        }
        switch (type) {
            case 1: {
                break;
            }
            case 2: {
                return (int)this.boardConfig.getPST_KNIGHT_O()[fieldID];
            }
            case 3: {
                return (int)this.boardConfig.getPST_BISHOP_O()[fieldID];
            }
            case 4: {
                return (int)this.boardConfig.getPST_ROOK_O()[fieldID];
            }
            case 5: {
                return (int)this.boardConfig.getPST_QUEEN_O()[fieldID];
            }
            case 6: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure type " + type + " is undefined!");
            }
        }
        return result;
    }

    private int getScores_E(int colour, int type, int fieldID) {
        int result = 0;
        if (colour == 1) {
            fieldID = HORIZONTAL_SYMMETRY[fieldID];
        }
        switch (type) {
            case 1: {
                break;
            }
            case 2: {
                return (int)this.boardConfig.getPST_KNIGHT_E()[fieldID];
            }
            case 3: {
                return (int)this.boardConfig.getPST_BISHOP_E()[fieldID];
            }
            case 4: {
                return (int)this.boardConfig.getPST_ROOK_E()[fieldID];
            }
            case 5: {
                return (int)this.boardConfig.getPST_QUEEN_E()[fieldID];
            }
            case 6: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure type " + type + " is undefined!");
            }
        }
        return result;
    }
}

