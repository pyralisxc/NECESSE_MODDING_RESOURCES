/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import java.io.Serializable;

public class FieldState
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -5974777238348745497L;
    int id;
    int figureOnFieldColour;
    int figureOnFieldType;
    FieldAttacks whiteAttacks;
    FieldAttacks blackAttacks;

    public FieldState(int id, int figureOnFieldColour, int figureOnFieldType, FieldAttacks whiteAttacks, FieldAttacks blackAttacks) {
        this.id = id;
        this.figureOnFieldColour = figureOnFieldColour;
        this.figureOnFieldType = figureOnFieldType;
        this.whiteAttacks = whiteAttacks;
        this.blackAttacks = blackAttacks;
    }

    public String toString() {
        Object result = "";
        result = this.figureOnFieldType == 0 ? (String)result + "OnField: -- " : (String)result + "OnField: " + Figures.COLOURS_SIGN[this.figureOnFieldColour] + Figures.TYPES_SIGN[this.figureOnFieldType] + " ";
        result = (String)result + ", WhiteAttacks: " + String.valueOf(this.whiteAttacks) + " ";
        result = (String)result + ", BlackAttacks: " + String.valueOf(this.blackAttacks) + " ";
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        FieldState other = (FieldState)o;
        return this.figureOnFieldColour == other.figureOnFieldColour && this.figureOnFieldType == other.figureOnFieldType && this.whiteAttacks.equals(other.whiteAttacks) && this.blackAttacks.equals(other.blackAttacks);
    }

    public int hashCode() {
        int hash = (this.figureOnFieldColour << 29) + (this.figureOnFieldType << 26) + (this.whiteAttacks.hashCode() ^ this.blackAttacks.hashCode());
        return hash;
    }

    public FieldState clone() {
        FieldState result = null;
        try {
            result = (FieldState)super.clone();
            result.id = this.id;
            result.figureOnFieldColour = this.figureOnFieldColour;
            result.figureOnFieldType = this.figureOnFieldType;
            result.whiteAttacks = this.whiteAttacks;
            result.blackAttacks = this.blackAttacks;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public FieldAttacks getBlackAttacks() {
        return this.blackAttacks;
    }

    public int getId() {
        return this.id;
    }

    public FieldAttacks getWhiteAttacks() {
        return this.whiteAttacks;
    }

    public int getFigureOnFieldColour() {
        return this.figureOnFieldColour;
    }

    public int getFigureOnFieldType() {
        return this.figureOnFieldType;
    }
}

