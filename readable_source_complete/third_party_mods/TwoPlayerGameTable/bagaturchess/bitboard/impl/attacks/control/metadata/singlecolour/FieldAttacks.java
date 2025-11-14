/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import java.io.Serializable;

public class FieldAttacks
implements Comparable<FieldAttacks>,
Cloneable,
Serializable {
    private static final long serialVersionUID = -1691481162448968174L;
    int id;
    int pa_count;
    int kna_count;
    int oa_count;
    int ma_count;
    int ra_count;
    int qa_count;
    int ka_count;
    int xa_count;
    public static final int PA_MASK = 1;
    public static final int MA_MASK = 2;
    public static final int RA_MASK = 4;
    public static final int QA_MASK = 8;
    public static final int KA_MASK = 16;
    int pattern;

    public FieldAttacks(int pa_count, int kna_count, int oa_count, int ma_count, int ra_count, int qa_count, int ka_count, int xa_count) {
        this.pa_count = pa_count;
        this.kna_count = kna_count;
        this.oa_count = oa_count;
        this.ma_count = ma_count;
        this.ra_count = ra_count;
        this.qa_count = qa_count;
        this.ka_count = ka_count;
        this.xa_count = xa_count;
        this.pattern = 0;
        if (pa_count > 0) {
            this.pattern |= 1;
        }
        if (kna_count > 0 || oa_count > 0 || ma_count > 0) {
            this.pattern |= 2;
        }
        if (ra_count > 0) {
            this.pattern |= 4;
        }
        if (qa_count > 0) {
            this.pattern |= 8;
        }
        if (ka_count > 0) {
            this.pattern |= 0x10;
        }
        if (this.pattern < 0 || this.pattern > 31) {
            throw new IllegalStateException();
        }
    }

    public int getPattern() {
        return this.pattern;
    }

    public boolean hasAttacksFrom(int figureType) {
        boolean result = false;
        switch (figureType) {
            case 1: {
                result = this.pa_count > 0;
                break;
            }
            case 3: {
                result = this.ma_count > 0;
                break;
            }
            case 2: {
                result = this.ma_count > 0;
                break;
            }
            case 4: {
                result = this.ra_count > 0;
                break;
            }
            case 5: {
                result = this.qa_count > 0;
                break;
            }
            case 6: {
                result = this.ka_count > 0;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return result;
    }

    public int paCount() {
        int pa = this.pa_count;
        return pa;
    }

    public int maCount() {
        int ma = this.ma_count;
        if (this.ma_count == 3) {
            ma += this.xa_count;
        }
        return ma;
    }

    public int knaCount() {
        throw new IllegalStateException();
    }

    public int oaCount() {
        throw new IllegalStateException();
    }

    public int raCount() {
        int ra = this.ra_count;
        if (this.ra_count == 3) {
            ra += this.xa_count;
        }
        return ra;
    }

    public int qaCount() {
        int qa = this.qa_count;
        if (this.qa_count == 4) {
            qa += this.xa_count;
        }
        return qa;
    }

    public int kaCount() {
        int ka = this.ka_count;
        return ka;
    }

    public boolean hasNonKingAttack() {
        if (this.qa_count > 0) {
            return true;
        }
        if (this.ra_count > 0) {
            return true;
        }
        if (this.ma_count > 0) {
            return true;
        }
        return this.pa_count > 0;
    }

    public int getMaxType() {
        if (this.qa_count > 0) {
            return 5;
        }
        if (this.ra_count > 0) {
            return 4;
        }
        if (this.ma_count > 0) {
            return 3;
        }
        return 0;
    }

    @Override
    public int compareTo(FieldAttacks other) {
        int p_delta = this.paCount() - other.paCount();
        if (p_delta != 0) {
            return p_delta;
        }
        int m_delta = -1;
        m_delta = this.maCount() - other.maCount();
        if (m_delta != 0) {
            return m_delta;
        }
        int r_delta = this.raCount() - other.raCount();
        if (r_delta != 0) {
            return r_delta;
        }
        int q_delta = this.qaCount() - other.qaCount();
        if (q_delta != 0) {
            return q_delta;
        }
        int k_delta = this.kaCount() - other.kaCount();
        if (k_delta != 0) {
            return k_delta;
        }
        return -1;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        FieldAttacks other = (FieldAttacks)o;
        return this.pa_count == other.pa_count && this.ma_count == other.ma_count && this.ra_count == other.ra_count && this.qa_count == other.qa_count && this.ka_count == other.ka_count && this.xa_count == other.xa_count;
    }

    public int hashCode() {
        int hash = -1;
        hash = this.pa_count + (this.ma_count << 4) + (this.ra_count << 8) + (this.qa_count << 12) + (this.ka_count << 16) + (this.xa_count << 20);
        return hash;
    }

    public String toString() {
        String result = "id:" + this.id + "->";
        result = result + this.makeString(this.pa_count, "P");
        result = result + this.makeString(this.ma_count, "M");
        result = result + this.makeString(this.ra_count, "R");
        result = result + this.makeString(this.qa_count, "Q");
        result = result + this.makeString(this.ka_count, "K");
        result = result + this.makeString(this.xa_count, "X");
        return result;
    }

    public FieldAttacks clone() {
        FieldAttacks result = null;
        try {
            result = (FieldAttacks)super.clone();
            result.id = this.id;
            result.pa_count = this.pa_count;
            result.kna_count = this.kna_count;
            result.oa_count = this.oa_count;
            result.ma_count = this.ma_count;
            result.ra_count = this.ra_count;
            result.qa_count = this.qa_count;
            result.ka_count = this.ka_count;
            result.xa_count = this.xa_count;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String makeString(int number, String symbol) {
        Object result = "";
        for (int i = 0; i < number; ++i) {
            result = (String)result + symbol;
        }
        return result;
    }

    public boolean isConsistent() {
        if (this.pa_count >= 3) {
            throw new IllegalStateException();
        }
        if (this.ma_count >= 4) {
            throw new IllegalStateException();
        }
        if (this.ra_count >= 4) {
            throw new IllegalStateException();
        }
        if (this.qa_count >= 5) {
            throw new IllegalStateException();
        }
        if (this.ka_count >= 2) {
            throw new IllegalStateException();
        }
        if (this.xa_count >= 2) {
            throw new IllegalStateException();
        }
        if (this.pa_count < 0) {
            throw new IllegalStateException();
        }
        if (this.ma_count < 0) {
            throw new IllegalStateException();
        }
        if (this.ra_count < 0) {
            throw new IllegalStateException();
        }
        if (this.qa_count < 0) {
            throw new IllegalStateException();
        }
        if (this.ka_count < 0) {
            throw new IllegalStateException();
        }
        if (this.xa_count < 0) {
            throw new IllegalStateException();
        }
        int countMaxAttacks = 0;
        if (this.ma_count == 3) {
            ++countMaxAttacks;
        }
        if (this.ra_count == 3) {
            ++countMaxAttacks;
        }
        if (this.qa_count == 4) {
            ++countMaxAttacks;
        }
        if (countMaxAttacks == 0 && this.xa_count > 0) {
            throw new IllegalStateException();
        }
        return countMaxAttacks <= 1 || this.xa_count == 0;
    }

    public int getId() {
        return this.id;
    }
}

