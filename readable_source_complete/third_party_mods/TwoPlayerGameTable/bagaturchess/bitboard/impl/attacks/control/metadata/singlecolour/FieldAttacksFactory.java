/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import java.io.Serializable;

public class FieldAttacksFactory
implements Serializable {
    public static FieldAttacks create(int pa, int kna, int oa, int ma, int ra, int qa, int ka, int xa) {
        return new FieldAttacks(pa, kna, oa, ma, ra, qa, ka, xa);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static FieldAttacks modify(int operation, int figureType, FieldAttacks source) {
        FieldAttacks result;
        block30: {
            block29: {
                result = source.clone();
                if (!result.isConsistent()) {
                    throw new IllegalStateException();
                }
                if (operation != 0) break block29;
                switch (figureType) {
                    case 1: {
                        if (result.pa_count >= 2) throw new IllegalStateException("More than two pawn attacks");
                        ++result.pa_count;
                        break block30;
                    }
                    case 2: {
                        if (result.ma_count < 3) {
                            ++result.ma_count;
                        } else {
                            ++result.xa_count;
                        }
                        break block30;
                    }
                    case 3: {
                        if (result.ma_count < 3) {
                            ++result.ma_count;
                        } else {
                            ++result.xa_count;
                        }
                        break block30;
                    }
                    case 4: {
                        if (result.ra_count < 3) {
                            ++result.ra_count;
                        } else {
                            ++result.xa_count;
                        }
                        break block30;
                    }
                    case 5: {
                        if (result.qa_count < 4) {
                            ++result.qa_count;
                        } else {
                            ++result.xa_count;
                        }
                        break block30;
                    }
                    case 6: {
                        if (result.ka_count >= 1) throw new IllegalStateException("More than one king attack");
                        ++result.ka_count;
                        break block30;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
            if (operation != 1) throw new IllegalStateException();
            switch (figureType) {
                case 1: {
                    if (result.pa_count <= 0) throw new IllegalStateException();
                    --result.pa_count;
                    break;
                }
                case 2: {
                    if (result.xa_count > 0 && result.ma_count == 3) {
                        --result.xa_count;
                        break;
                    }
                    if (result.ma_count <= 0) throw new IllegalStateException();
                    --result.ma_count;
                    break;
                }
                case 3: {
                    if (result.xa_count > 0 && result.ma_count == 3) {
                        --result.xa_count;
                        break;
                    }
                    if (result.ma_count <= 0) throw new IllegalStateException();
                    --result.ma_count;
                    break;
                }
                case 4: {
                    if (result.xa_count > 0 && result.ra_count == 3) {
                        --result.xa_count;
                        break;
                    }
                    if (result.ra_count <= 0) throw new IllegalStateException();
                    --result.ra_count;
                    break;
                }
                case 5: {
                    if (result.xa_count > 0 && result.qa_count == 4) {
                        --result.xa_count;
                        break;
                    }
                    if (result.qa_count <= 0) throw new IllegalStateException();
                    --result.qa_count;
                    break;
                }
                case 6: {
                    if (result.ka_count <= 0) throw new IllegalStateException();
                    --result.ka_count;
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        if (!result.isConsistent()) return null;
        return result;
    }
}

