/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;

public class Pawn
extends PawnStructureConstants {
    int fieldID;
    int rank;
    long field;
    long vertical;
    long attacks;
    long front;
    long front_immediate;
    long front_neighbour;
    long front_passer;
    int mykingDistance;
    int opkingDistance;
    boolean isolated;
    boolean backward;
    boolean doubled;
    boolean supported;
    boolean cannot_be_supported;
    boolean candidate;
    boolean guard;
    boolean storm;
    boolean passed;
    boolean passed_unstoppable;
    boolean passer_hidden_couple_participant;
    int guard_remoteness;
    int storm_closeness;

    void initialize(int pawnColour, int colourToMove, int pawnFieldID, long myPawns, long opPawns, int myKingFieldID, int opKingFieldID) {
        long doubledBoard;
        int doubledHits;
        this.isolated = false;
        this.backward = false;
        this.doubled = false;
        this.supported = false;
        this.cannot_be_supported = false;
        this.candidate = false;
        this.guard = false;
        this.storm = false;
        this.passed = false;
        this.passed_unstoppable = false;
        this.passer_hidden_couple_participant = false;
        this.guard_remoteness = 0;
        this.storm_closeness = 0;
        this.attacks = 0L;
        this.fieldID = pawnFieldID;
        this.field = Fields.ALL_ORDERED_A1H1[pawnFieldID];
        this.vertical = LETTERS_BY_FIELD_ID[this.fieldID];
        if (pawnColour == 0) {
            long opkingfront;
            long mykingfront;
            long passedBoard;
            int passedHits;
            long r_supporters;
            long l_supporters;
            int supportersHits;
            this.rank = DIGITS[this.fieldID];
            this.attacks = (this.field & 0x8080808080808080L) == 0L ? this.field >> 7 : 0L;
            this.attacks |= (this.field & 0x101010101010101L) == 0L ? this.field >> 9 : 0L;
            this.front_immediate = this.field >> 8;
            this.front = WHITE_FRONT_FULL[this.fieldID];
            this.front_neighbour = (this.front ^ 0xFFFFFFFFFFFFFFFFL) & WHITE_PASSED[this.fieldID];
            backwardBoard = WHITE_BACKWARD[pawnFieldID] & myPawns;
            backwardHits = Utils.countBits(backwardBoard);
            if (backwardHits == 0) {
                this.backward = true;
            }
            if ((supportersHits = Utils.countBits(supportersBoard = (supporters = WHITE_SUPPORT[pawnFieldID]) & myPawns)) > 0) {
                this.supported = true;
            }
            if (((l_supporters = WHITE_CAN_BE_SUPPORTED_LEFT[pawnFieldID]) & myPawns) != 0L && (opPawns & l_supporters) == 0L) {
                this.cannot_be_supported = true;
            }
            if (!this.cannot_be_supported && ((r_supporters = WHITE_CAN_BE_SUPPORTED_RIGHT[pawnFieldID]) & myPawns) != 0L && (opPawns & r_supporters) == 0L) {
                this.cannot_be_supported = true;
            }
            if ((passedHits = Utils.countBits(passedBoard = WHITE_PASSED[pawnFieldID] & opPawns)) == 0) {
                this.passed = true;
                if (colourToMove == 0) {
                    perimeter = WHITE_PASSER_PARAM[pawnFieldID];
                    opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
                    this.passed_unstoppable = (perimeter & opKing) == 0L;
                } else {
                    perimeter = WHITE_PASSER_EXT_PARAM[pawnFieldID];
                    opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
                    boolean bl = this.passed_unstoppable = (perimeter & opKing) == 0L;
                }
            }
            if (!this.passed && (this.front & opPawns) == 0L && Utils.countBits_less1s(myPawns & (BLACK_FRONT_FULL[this.fieldID] ^ 0xFFFFFFFFFFFFFFFFL) & (BLACK_PASSED[pawnFieldID] | WHITE_SUPPORT[pawnFieldID])) >= Utils.countBits_less1s(opPawns & WHITE_PASSED[pawnFieldID])) {
                this.candidate = true;
            }
            if ((this.field & (mykingfront = WHITE_PASSED[myKingFieldID])) != 0L) {
                this.guard = true;
                this.guard_remoteness = this.getDigitsDiff(pawnFieldID, 0);
            }
            if ((this.field & (opkingfront = BLACK_PASSED[opKingFieldID])) != 0L) {
                this.storm = true;
                this.storm_closeness = this.getDigitsDiff(pawnFieldID, opKingFieldID);
            }
            this.mykingDistance = this.getDistance(pawnFieldID, myKingFieldID);
            this.opkingDistance = this.getDistance(pawnFieldID, opKingFieldID);
        } else {
            long opkingfront;
            long mykingfront;
            long passedBoard;
            int passedHits;
            long r_supporters;
            long l_supporters;
            int supportersHits;
            this.rank = 7 - DIGITS[this.fieldID];
            this.attacks = (this.field & 0x101010101010101L) == 0L ? this.field << 7 : 0L;
            this.attacks |= (this.field & 0x8080808080808080L) == 0L ? this.field << 9 : 0L;
            this.front_immediate = this.field << 8;
            this.front = BLACK_FRONT_FULL[this.fieldID];
            this.front_neighbour = (this.front ^ 0xFFFFFFFFFFFFFFFFL) & BLACK_PASSED[this.fieldID];
            backwardBoard = BLACK_BACKWARD[pawnFieldID] & myPawns;
            backwardHits = Utils.countBits(backwardBoard);
            if (backwardHits == 0) {
                this.backward = true;
            }
            if ((supportersHits = Utils.countBits(supportersBoard = (supporters = BLACK_SUPPORT[pawnFieldID]) & myPawns)) > 0) {
                this.supported = true;
            }
            if (((l_supporters = BLACK_CAN_BE_SUPPORTED_LEFT[pawnFieldID]) & myPawns) != 0L && (opPawns & l_supporters) == 0L) {
                this.cannot_be_supported = true;
            }
            if (!this.cannot_be_supported && ((r_supporters = BLACK_CAN_BE_SUPPORTED_RIGHT[pawnFieldID]) & myPawns) != 0L && (opPawns & r_supporters) == 0L) {
                this.cannot_be_supported = true;
            }
            if ((passedHits = Utils.countBits(passedBoard = BLACK_PASSED[pawnFieldID] & opPawns)) == 0) {
                this.passed = true;
                if (colourToMove == 1) {
                    perimeter = BLACK_PASSER_PARAM[pawnFieldID];
                    opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
                    this.passed_unstoppable = (perimeter & opKing) == 0L;
                } else {
                    perimeter = BLACK_PASSER_EXT_PARAM[pawnFieldID];
                    opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
                    boolean bl = this.passed_unstoppable = (perimeter & opKing) == 0L;
                }
            }
            if (!this.passed && (this.front & opPawns) == 0L && Utils.countBits_less1s(myPawns & (WHITE_FRONT_FULL[this.fieldID] ^ 0xFFFFFFFFFFFFFFFFL) & (WHITE_PASSED[pawnFieldID] | BLACK_SUPPORT[pawnFieldID])) >= Utils.countBits_less1s(opPawns & BLACK_PASSED[pawnFieldID])) {
                this.candidate = true;
            }
            if ((this.field & (mykingfront = BLACK_PASSED[myKingFieldID])) != 0L) {
                this.guard = true;
                this.guard_remoteness = this.getDigitsDiff(pawnFieldID, 63);
            }
            if ((this.field & (opkingfront = WHITE_PASSED[opKingFieldID])) != 0L) {
                this.storm = true;
                this.storm_closeness = this.getDigitsDiff(pawnFieldID, opKingFieldID);
            }
            this.mykingDistance = this.getDistance(pawnFieldID, myKingFieldID);
            this.opkingDistance = this.getDistance(pawnFieldID, opKingFieldID);
        }
        this.front_passer = this.front | this.front_neighbour;
        long isolatedBoard = LETTERS_NEIGHBOURS_BY_FIELD_ID[pawnFieldID] & myPawns;
        int isolatedHits = Utils.countBits(isolatedBoard);
        if (isolatedHits == 0) {
            this.isolated = true;
        }
        if ((doubledHits = Utils.countBits(doubledBoard = this.front & myPawns)) > 0) {
            this.doubled = true;
        }
    }

    public static void main(String[] args) {
        long field = 0x1000000000000L;
        System.out.println(Bits.toBinaryStringMatrix(field));
        long left = (field & 0x101010101010101L) == 0L ? field << 7 : 0L;
        System.out.println(Bits.toBinaryStringMatrix(left));
        long right = (field & 0x8080808080808080L) == 0L ? field << 9 : 0L;
        System.out.println(Bits.toBinaryStringMatrix(right));
    }

    public long getField() {
        return this.field;
    }

    public int getFieldID() {
        return this.fieldID;
    }

    public boolean isPassed() {
        return this.passed;
    }

    public boolean isCandidate() {
        return this.candidate;
    }

    public boolean isPassedUnstoppable() {
        return this.passed_unstoppable;
    }

    public boolean isGuard() {
        return this.guard;
    }

    public int getGuardRemoteness() {
        return this.guard_remoteness;
    }

    public boolean isStorm() {
        return this.storm;
    }

    public int getStormCloseness() {
        return this.storm_closeness;
    }

    public int getRank() {
        return this.rank;
    }

    public boolean isDoubled() {
        return this.doubled;
    }

    public boolean isIsolated() {
        return this.isolated;
    }

    public boolean isBackward() {
        return this.backward;
    }

    public boolean cannotBeSupported() {
        return this.cannot_be_supported;
    }

    public boolean isSupported() {
        return this.supported;
    }

    public long getVertical() {
        return this.vertical;
    }

    public long getFront() {
        return this.front;
    }
}

