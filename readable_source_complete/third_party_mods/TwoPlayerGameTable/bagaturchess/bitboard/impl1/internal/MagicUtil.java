/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.Util;

public final class MagicUtil {
    private static final long[] rookMovementMasks = new long[64];
    private static final long[] bishopMovementMasks = new long[64];
    private static final long[] rookMagicNumbers = new long[]{-6809440297970302416L, 18031991769407488L, 36038143404675074L, 36037603858059264L, 4755805742261731336L, 324270168337547392L, 288249070030688264L, 36029485304252544L, 1158410273640464400L, 141012502578304L, 281548010029248L, 9570429053182464L, 562984615223308L, 2317665028167565316L, 36310276294967300L, 4611826770948146432L, 35734170910721L, 40532672598138888L, 146508275402153985L, 2306408158734600512L, -9203104738884648955L, 7503138266510533120L, 756609135579115537L, 49898036700188740L, 4756997477301784224L, -5763974197892087420L, 2314885394989056896L, 0x10080080100080L, 2451092895445287936L, 180322057977984L, 145247702234960049L, 39443356148842756L, -9115285370915774464L, 5227623773657632769L, 4618617341884764164L, 1310547633382625288L, -8573587018750098432L, 18577401109821456L, 37163566100426770L, 6764614326944769L, 2467981942197485568L, 4504836850630660L, 721139440361275424L, 4503805936861280L, 1126449796907028L, 36030996109361280L, 180709134088306816L, 5764609177138364420L, 72625084065399296L, 4611721271523148224L, 7074258976014848L, 144167968935291136L, 2253449752477824L, 38320179284836480L, 4556479306859520L, 14401680344089088L, 36169538976354881L, 282025005154337L, 1134833455624769L, 148900881174103305L, -9217460993586358271L, 576742261706982915L, 867224549276820481L, 281483572970051L};
    private static final long[] bishopMagicNumbers = new long[]{2958870736342630660L, 148625593733349760L, 6350640762115325952L, 3217827023294562336L, 145245632058099968L, 2306425819699871746L, -9205286152370650904L, -9139490286097856448L, -9221938198462595000L, 1153416293563564289L, 1153211784333631744L, 1733894672109148160L, 27588963480963584L, 5775871126377857056L, -4609422931399782398L, 621507748057711104L, 666532762307662354L, 6764205368148496L, 291612620082513304L, 4767062406775980112L, 144255942758760704L, 147211490934526016L, 2544278522825728L, 20618119521894664L, 9042934735577472L, 4508548690608640L, 2326111407164358672L, 72831925168964096L, 145250022789750800L, 162429753293308097L, 1164744005599371520L, 666817518372952134L, 163273113042158080L, 4611844417090225814L, 43997713661984L, 576465287925465344L, 153122954266837024L, 288794983963100361L, 9852740876502528L, 18300272070689538L, 293314586705266712L, -9213793048448712184L, 1441469649287399424L, 9610015228887168L, 1226390875936915984L, 2323866787944595585L, -9114017898925390848L, 4612918572052652544L, 288795131136444432L, 5048817791164088328L, -9223359925029960702L, 9007509054949888L, -9079256710765084416L, 2607045165514856L, 1171149242833600576L, 577745067834485312L, 2594108732980871168L, 146457156588405252L, 291608076442742816L, 2305860876281972224L, 288230386353579024L, 576569827428140288L, 39599733347344L, 87855381971216640L};
    private static final long[][] rookMagicMoves = new long[64][];
    private static final long[][] bishopMagicMoves = new long[64][];
    private static final int[] rookShifts = new int[64];
    private static final int[] bishopShifts = new int[64];

    public static long getRookMoves(int fromIndex, long allPieces) {
        return rookMagicMoves[fromIndex][(int)((allPieces & rookMovementMasks[fromIndex]) * rookMagicNumbers[fromIndex] >>> rookShifts[fromIndex])];
    }

    public static long getBishopMoves(int fromIndex, long allPieces) {
        return bishopMagicMoves[fromIndex][(int)((allPieces & bishopMovementMasks[fromIndex]) * bishopMagicNumbers[fromIndex] >>> bishopShifts[fromIndex])];
    }

    public static long getQueenMoves(int fromIndex, long allPieces) {
        return rookMagicMoves[fromIndex][(int)((allPieces & rookMovementMasks[fromIndex]) * rookMagicNumbers[fromIndex] >>> rookShifts[fromIndex])] | bishopMagicMoves[fromIndex][(int)((allPieces & bishopMovementMasks[fromIndex]) * bishopMagicNumbers[fromIndex] >>> bishopShifts[fromIndex])];
    }

    public static long getRookMovesEmptyBoard(int fromIndex) {
        return rookMagicMoves[fromIndex][0];
    }

    public static long getBishopMovesEmptyBoard(int fromIndex) {
        return bishopMagicMoves[fromIndex][0];
    }

    public static long getQueenMovesEmptyBoard(int fromIndex) {
        return bishopMagicMoves[fromIndex][0] | rookMagicMoves[fromIndex][0];
    }

    private static void generateShiftArrys() {
        for (int i = 0; i < 64; ++i) {
            MagicUtil.rookShifts[i] = 64 - Long.bitCount(rookMovementMasks[i]);
            MagicUtil.bishopShifts[i] = 64 - Long.bitCount(bishopMovementMasks[i]);
        }
    }

    private static long[][] calculateVariations(long[] movementMasks) {
        long[][] occupancyVariations = new long[64][];
        for (int index = 0; index < 64; ++index) {
            int variationCount = (int)Util.POWER_LOOKUP[Long.bitCount(movementMasks[index])];
            occupancyVariations[index] = new long[variationCount];
            for (int variationIndex = 1; variationIndex < variationCount; ++variationIndex) {
                long currentMask = movementMasks[index];
                for (int i = 0; i < 32 - Integer.numberOfLeadingZeros(variationIndex); ++i) {
                    if ((Util.POWER_LOOKUP[i] & (long)variationIndex) != 0L) {
                        long[] lArray = occupancyVariations[index];
                        int n = variationIndex;
                        lArray[n] = lArray[n] | Long.lowestOneBit(currentMask);
                    }
                    currentMask &= currentMask - 1L;
                }
            }
        }
        return occupancyVariations;
    }

    private static void calculateRookMovementMasks() {
        for (int index = 0; index < 64; ++index) {
            int j;
            for (j = index + 8; j < 56; j += 8) {
                int n = index;
                rookMovementMasks[n] = rookMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
            for (j = index - 8; j >= 8; j -= 8) {
                int n = index;
                rookMovementMasks[n] = rookMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
            j = index + 1;
            while (j % 8 != 0 && j % 8 != 7) {
                int n = index;
                rookMovementMasks[n] = rookMovementMasks[n] | Util.POWER_LOOKUP[j];
                ++j;
            }
            for (j = index - 1; j % 8 != 7 && j % 8 != 0 && j > 0; --j) {
                int n = index;
                rookMovementMasks[n] = rookMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
        }
    }

    private static void calculateBishopMovementMasks() {
        for (int index = 0; index < 64; ++index) {
            int j;
            for (j = index + 7; j < 57 && j % 8 != 7 && j % 8 != 0; j += 7) {
                int n = index;
                bishopMovementMasks[n] = bishopMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
            for (j = index + 9; j < 55 && j % 8 != 7 && j % 8 != 0; j += 9) {
                int n = index;
                bishopMovementMasks[n] = bishopMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
            for (j = index - 9; j >= 9 && j % 8 != 7 && j % 8 != 0; j -= 9) {
                int n = index;
                bishopMovementMasks[n] = bishopMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
            for (j = index - 7; j >= 7 && j % 8 != 7 && j % 8 != 0; j -= 7) {
                int n = index;
                bishopMovementMasks[n] = bishopMovementMasks[n] | Util.POWER_LOOKUP[j];
            }
        }
    }

    private static void generateRookMoveDatabase(long[][] rookOccupancyVariations) {
        for (int index = 0; index < 64; ++index) {
            MagicUtil.rookMagicMoves[index] = new long[rookOccupancyVariations[index].length];
            for (int variationIndex = 0; variationIndex < rookOccupancyVariations[index].length; ++variationIndex) {
                int j;
                long validMoves = 0L;
                int magicIndex = (int)(rookOccupancyVariations[index][variationIndex] * rookMagicNumbers[index] >>> rookShifts[index]);
                for (j = index + 8; j < 64; j += 8) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                for (j = index - 8; j >= 0; j -= 8) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                j = index + 1;
                while (j % 8 != 0) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                    ++j;
                }
                for (j = index - 1; j % 8 != 7 && j >= 0; --j) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                MagicUtil.rookMagicMoves[index][magicIndex] = validMoves;
            }
        }
    }

    private static void generateBishopMoveDatabase(long[][] bishopOccupancyVariations) {
        for (int index = 0; index < 64; ++index) {
            MagicUtil.bishopMagicMoves[index] = new long[bishopOccupancyVariations[index].length];
            for (int variationIndex = 0; variationIndex < bishopOccupancyVariations[index].length; ++variationIndex) {
                int j;
                long validMoves = 0L;
                int magicIndex = (int)(bishopOccupancyVariations[index][variationIndex] * bishopMagicNumbers[index] >>> bishopShifts[index]);
                for (j = index + 7; j % 8 != 7 && j < 64; j += 7) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                for (j = index + 9; j % 8 != 0 && j < 64; j += 9) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                for (j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                for (j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
                    validMoves |= Util.POWER_LOOKUP[j];
                    if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0L) break;
                }
                MagicUtil.bishopMagicMoves[index][magicIndex] = validMoves;
            }
        }
    }

    static {
        MagicUtil.calculateBishopMovementMasks();
        MagicUtil.calculateRookMovementMasks();
        MagicUtil.generateShiftArrys();
        long[][] bishopOccupancyVariations = MagicUtil.calculateVariations(bishopMovementMasks);
        long[][] rookOccupancyVariations = MagicUtil.calculateVariations(rookMovementMasks);
        MagicUtil.generateBishopMoveDatabase(bishopOccupancyVariations);
        MagicUtil.generateRookMoveDatabase(rookOccupancyVariations);
    }
}

