/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class PathFinders
extends Fields {
    public static boolean findKnightPaths(int maxmoves, int fromFieldID, int toFieldID) {
        int dirID;
        int cur_toFieldID;
        boolean result = false;
        if (maxmoves == 0) {
            return fromFieldID == toFieldID;
        }
        int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
        int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
        int size = validDirIDs.length;
        for (int i = 0; i < size && !(result = PathFinders.findKnightPaths(maxmoves - 1, cur_toFieldID = dirs_ids[dirID = validDirIDs[i]][0], toFieldID)); ++i) {
        }
        return result;
    }

    public static long findKnightPaths(int maxmoves, int fromFieldID) {
        long result = 0L;
        if (maxmoves == 0) {
            return result;
        }
        int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
        int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID = dirs_ids[dirID][0];
            long board = dirs[dirID][0];
            result |= board;
            result |= PathFinders.findKnightPaths(maxmoves - 1, toFieldID);
        }
        return result;
    }

    public static void genAll() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = PathFinders.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                boolean res;
                int toID = PathFinders.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID || !(res = PathFinders.findKnightPaths(2, fromID, toID))) continue;
                System.out.println("*********************************");
                System.out.println(Bits.toBinaryStringMatrix(ALL_A1H1[fromID]));
                System.out.println(Bits.toBinaryStringMatrix(ALL_A1H1[toID]));
                System.out.println("*********************************");
            }
        }
    }

    public static void main(String[] args) {
        int from = PathFinders.get67IDByBitboard(0x800000000L);
        long res = PathFinders.findKnightPaths(2, from);
        String matrix = Bits.toBinaryStringMatrix(res);
        System.out.println(matrix);
    }
}

