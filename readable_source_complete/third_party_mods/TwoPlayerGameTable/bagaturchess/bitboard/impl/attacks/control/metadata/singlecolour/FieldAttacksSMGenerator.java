/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksFactory;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class FieldAttacksSMGenerator {
    FieldAttacksSMGenerator() {
    }

    static void createStateMachine(FieldAttacksStateMachine result) {
        FieldAttacks[] allStatesList = result.allStatesList;
        result.machine = new int[2][7][allStatesList.length];
        int[][][] machine = result.machine;
        for (int i = allStatesList.length - 1; i >= 0; --i) {
            FieldAttacks cur = allStatesList[i];
            int curID = cur.id;
            if (cur.pa_count > 2 || cur.pa_count < 0) {
                throw new IllegalStateException();
            }
            machine[0][1][curID] = cur.pa_count == 0 || cur.pa_count == 1 ? FieldAttacksSMGenerator.getID(result, 0, 1, cur) : -1;
            machine[1][1][curID] = cur.pa_count == 2 || cur.pa_count == 1 ? FieldAttacksSMGenerator.getID(result, 1, 1, cur) : -1;
            if (cur.ma_count < 3 || FieldAttacksSMGenerator.onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < 1) {
                machine[0][2][curID] = FieldAttacksSMGenerator.getID(result, 0, 2, cur);
                machine[0][3][curID] = FieldAttacksSMGenerator.getID(result, 0, 3, cur);
            } else {
                machine[0][2][curID] = -1;
                machine[0][3][curID] = -1;
            }
            if (cur.ma_count == 3 && cur.xa_count > 0) {
                machine[1][2][curID] = FieldAttacksSMGenerator.getID(result, 1, 2, cur);
                machine[1][3][curID] = FieldAttacksSMGenerator.getID(result, 1, 3, cur);
            } else if (cur.ma_count > 0) {
                machine[1][2][curID] = FieldAttacksSMGenerator.getID(result, 1, 2, cur);
                machine[1][3][curID] = FieldAttacksSMGenerator.getID(result, 1, 3, cur);
            } else {
                machine[1][2][curID] = -1;
                machine[1][3][curID] = -1;
            }
            machine[0][4][curID] = cur.ra_count < 3 || FieldAttacksSMGenerator.onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < 1 ? FieldAttacksSMGenerator.getID(result, 0, 4, cur) : -1;
            machine[1][4][curID] = cur.ra_count == 3 && cur.xa_count > 0 ? FieldAttacksSMGenerator.getID(result, 1, 4, cur) : (cur.ra_count > 0 ? FieldAttacksSMGenerator.getID(result, 1, 4, cur) : -1);
            machine[0][5][curID] = cur.qa_count < 4 || FieldAttacksSMGenerator.onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < 1 ? FieldAttacksSMGenerator.getID(result, 0, 5, cur) : -1;
            machine[1][5][curID] = cur.qa_count == 4 && cur.xa_count > 0 ? FieldAttacksSMGenerator.getID(result, 1, 5, cur) : (cur.qa_count > 0 ? FieldAttacksSMGenerator.getID(result, 1, 5, cur) : -1);
            if (cur.ka_count > 1 || cur.ka_count < 0) {
                throw new IllegalStateException();
            }
            machine[0][6][curID] = cur.ka_count == 0 ? FieldAttacksSMGenerator.getID(result, 0, 6, cur) : -1;
            machine[1][6][curID] = cur.ka_count == 1 ? FieldAttacksSMGenerator.getID(result, 1, 6, cur) : -1;
        }
    }

    private static int getID(FieldAttacksStateMachine result, int operation, int type, FieldAttacks current) {
        FieldAttacks modified = FieldAttacksFactory.modify(operation, type, current);
        return FieldAttacksSMGenerator.getID(result, modified);
    }

    private static int getID(FieldAttacksStateMachine result, FieldAttacks toSearch) {
        FieldAttacks found = result.allStatesMap.get(toSearch);
        if (found != null) {
            return found.id;
        }
        return -1;
    }

    static void getAllFieldsAttacks(FieldAttacksStateMachine result) {
        int size = 1000;
        ArrayList<FieldAttacks> all = new ArrayList<FieldAttacks>(size);
        result.allStatesMap = new HashMap<FieldAttacks, FieldAttacks>(2 * size);
        HashMap<FieldAttacks, FieldAttacks> allStatesMap = result.allStatesMap;
        int max_pa = 3;
        int max_kna = 3;
        int max_oa = 3;
        int max_ma = 4;
        int max_ra = 4;
        int max_qa = 5;
        int max_ka = 2;
        int max_xa = 2;
        for (int pa = 0; pa < max_pa; ++pa) {
            for (int ra = 0; ra < max_ra; ++ra) {
                for (int qa = 0; qa < max_qa; ++qa) {
                    for (int ka = 0; ka < max_ka; ++ka) {
                        for (int ma = 0; ma < max_ma; ++ma) {
                            if (FieldAttacksSMGenerator.onlyOneMaxAttack_exceptKingAndPawns(pa, -1, -1, ma, ra, qa, ka)) {
                                for (int xa = 0; xa < max_xa; ++xa) {
                                    FieldAttacks cur = FieldAttacksFactory.create(pa, -1, -1, ma, ra, qa, ka, xa);
                                    if (!cur.isConsistent()) {
                                        throw new IllegalStateException();
                                    }
                                    all.add(cur);
                                    allStatesMap.put(cur, cur);
                                }
                                continue;
                            }
                            FieldAttacks cur = FieldAttacksFactory.create(pa, -1, -1, ma, ra, qa, ka, 0);
                            if (!cur.isConsistent()) {
                                throw new IllegalStateException();
                            }
                            all.add(cur);
                            allStatesMap.put(cur, cur);
                        }
                    }
                }
            }
        }
        result.allStatesList = all.toArray(new FieldAttacks[0]);
        Arrays.sort(result.allStatesList);
        for (int i = result.allStatesList.length - 1; i >= 0; --i) {
            result.allStatesList[i].id = i;
        }
    }

    private static boolean onlyOneMaxAttack_exceptKingAndPawns(FieldAttacks state) {
        return FieldAttacksSMGenerator.onlyOneMaxAttack_exceptKingAndPawns(state.pa_count, state.kna_count, state.oa_count, state.ma_count, state.ra_count, state.qa_count, state.ka_count);
    }

    private static boolean onlyOneMaxAttack_exceptKingAndPawns(int pa, int kna, int oa, int ma, int ra, int qa, int ka) {
        if (pa >= 3) {
            throw new IllegalStateException();
        }
        if (ma >= 4) {
            throw new IllegalStateException();
        }
        if (ra >= 4) {
            throw new IllegalStateException();
        }
        if (qa >= 5) {
            throw new IllegalStateException();
        }
        if (ka >= 2) {
            throw new IllegalStateException();
        }
        if (ma == 3 && ra < 3 && qa < 4) {
            return true;
        }
        if (ma < 3 && ra == 3 && qa < 4) {
            return true;
        }
        return ma < 3 && ra < 3 && qa == 4;
    }

    public static void main(String[] args) {
    }
}

