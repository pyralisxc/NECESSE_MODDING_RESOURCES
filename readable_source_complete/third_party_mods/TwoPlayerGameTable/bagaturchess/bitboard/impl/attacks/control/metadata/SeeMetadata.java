/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata;

import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;

public class SeeMetadata {
    private static final int COSTS_MULTIPLIER = 100;
    private static int STATES_COUNT = FieldAttacksStateMachine.getInstance().getAllStatesList().length;
    public static final byte[][][] FIELD_SEE = new byte[7][STATES_COUNT][STATES_COUNT];
    private static final boolean init_field_blocked = false;
    public static int[][][] FIELD_BLOCKED;
    public static final int[][] TYPE_LISTS;
    public static final int[][] COST_LISTS;
    private static SeeMetadata singleton;

    private SeeMetadata() {
        SeeMetadata.init();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final SeeMetadata getSingleton() {
        if (singleton != null) return singleton;
        Class<SeeMetadata> clazz = SeeMetadata.class;
        synchronized (SeeMetadata.class) {
            if (singleton != null) return singleton;
            singleton = new SeeMetadata();
            // ** MonitorExit[var0] (shouldn't be in output)
            return singleton;
        }
    }

    private static void init() {
        for (int figType = 0; figType < 7; ++figType) {
            if (figType != 1 && figType != 3 && figType != 2 && figType != 4 && figType != 5 && figType != 6) continue;
            for (int state = 0; state < STATES_COUNT; ++state) {
                FieldAttacks stateObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[state];
                SeeMetadata.TYPE_LISTS[state] = SeeMetadata.buildTypeList(stateObj);
                SeeMetadata.COST_LISTS[state] = SeeMetadata.buildCostList(TYPE_LISTS[state]);
            }
            for (int mystate = 0; mystate < STATES_COUNT; ++mystate) {
                for (int toPlayState = 0; toPlayState < STATES_COUNT; ++toPlayState) {
                    int see = -SeeMetadata.seeField(figType, mystate, toPlayState);
                    SeeMetadata.FIELD_SEE[figType][mystate][toPlayState] = (byte)(see / 100);
                    if (see >= 0 || FIELD_SEE[figType][mystate][toPlayState] <= 0) continue;
                    throw new IllegalStateException();
                }
            }
        }
    }

    public int seeMove(int typeToExclude, int myFigType, int mystate, int toPlayState) {
        int value = 0;
        if (typeToExclude != 0) {
            mystate = FieldAttacksStateMachine.getInstance().getMachine()[1][typeToExclude][mystate];
        }
        if (mystate < 0 || toPlayState < 0 || myFigType < 0) {
            System.out.println("myFigType=" + myFigType + ", mystate=" + mystate + ", toPlayState=" + toPlayState);
        }
        value = 100 * FIELD_SEE[myFigType][mystate][toPlayState];
        return value;
    }

    public static int seeField(int myFigType, int mystate, int toPlayState) {
        int value = 0;
        int[] opcosts = COST_LISTS[toPlayState];
        int[] mycosts = COST_LISTS[mystate];
        value = -SeeMetadata.see(SeeMetadata.getCost(myFigType), 0, opcosts.length, opcosts, 0, mycosts.length, mycosts);
        return value;
    }

    private static int getCost(int type) {
        if (type == 6) {
            return 1500;
        }
        return BaseEvalWeights.getFigureMaterialSEE(type);
    }

    private static int fieldBlock(int figType, int mystate, int toPlayState) {
        if (FIELD_SEE[figType][mystate][toPlayState] >= 0) {
            return 0;
        }
        if (FIELD_SEE[figType][mystate][toPlayState] >= 0) {
            throw new IllegalStateException();
        }
        int result = 0;
        int[] opcosts = COST_LISTS[toPlayState];
        int[] mycosts = COST_LISTS[mystate];
        return result;
    }

    private static int see(int currentPieceOnFieldCost, int playing_cur1, int playing_size1, int[] playing_l1, int cur2, int size2, int[] l2) {
        int value = 0;
        if (playing_size1 == 0 || playing_cur1 >= playing_size1) {
            return 0;
        }
        int curValue = currentPieceOnFieldCost - SeeMetadata.see(playing_l1[playing_cur1], cur2, size2, l2, playing_cur1 + 1, playing_size1, playing_l1);
        if (curValue > value) {
            value = curValue;
        }
        return value;
    }

    private static int[] buildCostList(int[] typeList) {
        int[] result = new int[typeList.length];
        for (int i = 0; i < typeList.length; ++i) {
            result[i] = SeeMetadata.getCost(typeList[i]);
        }
        return result;
    }

    private static int[] buildTypeList(FieldAttacks fa) {
        int i;
        int size = -1;
        size = fa.paCount() + fa.maCount() + fa.raCount() + fa.qaCount() + fa.kaCount();
        int[] result = new int[size];
        int count = 0;
        for (i = 0; i < fa.paCount(); ++i) {
            result[count++] = 1;
        }
        for (i = 0; i < fa.maCount(); ++i) {
            result[count++] = 2;
        }
        for (i = 0; i < fa.raCount(); ++i) {
            result[count++] = 4;
        }
        for (i = 0; i < fa.qaCount(); ++i) {
            result[count++] = 5;
        }
        for (i = 0; i < fa.kaCount(); ++i) {
            result[count++] = 6;
        }
        return result;
    }

    static {
        TYPE_LISTS = new int[STATES_COUNT][];
        COST_LISTS = new int[STATES_COUNT][];
    }
}

