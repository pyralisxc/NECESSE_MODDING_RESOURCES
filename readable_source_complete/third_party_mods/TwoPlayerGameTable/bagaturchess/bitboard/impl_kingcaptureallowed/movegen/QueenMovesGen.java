/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.plies.checking.QueenUniqueChecks;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.OfficerMovesGen;

public class QueenMovesGen
extends QueenUniqueChecks {
    public static final void genAllMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        OfficerMovesGen.genAllMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
        CastleMovesGen.genAllMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
    }

    public static final void genCaptureMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        OfficerMovesGen.genCaptureMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
        CastleMovesGen.genCaptureMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
    }
}

