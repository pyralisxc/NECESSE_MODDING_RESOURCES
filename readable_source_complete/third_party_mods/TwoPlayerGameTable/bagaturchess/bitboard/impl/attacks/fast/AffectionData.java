/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.datastructs.numbers.IndexNumberSet;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.state.PiecesList;

public class AffectionData {
    int[] removed;
    int[] introduced;
    int[] affected;
    int[][] affected_dirs;
    int[][] affected_dirs_types;
    int[] removed_pids;
    int[] introduced_pids;
    int[] affected_pids;
    private IndexNumberSet affectedQueens;
    boolean queenSetCleared = false;
    private IBitBoard bitboard;
    private IPiecesLists piecesLists;
    private IPlayerAttacks whiteAttacks;
    private IPlayerAttacks blackAttacks;
    private IndexNumberSet removedSet;
    private IndexNumberSet introducedSet;
    private IndexNumberSet affectedSet;
    private IndexNumberSet affectedDirsSet;

    public AffectionData(IBitBoard _bitboard, IPlayerAttacks _whiteAttacks, IPlayerAttacks _blackAttacks) {
        this.bitboard = _bitboard;
        this.piecesLists = this.bitboard.getPiecesLists();
        this.whiteAttacks = _whiteAttacks;
        this.blackAttacks = _blackAttacks;
        this.removed = new int[4];
        this.introduced = new int[4];
        this.affected = new int[16];
        this.removed_pids = new int[4];
        this.introduced_pids = new int[4];
        this.affected_pids = new int[16];
        this.affected_dirs = new int[64][8];
        this.affected_dirs_types = new int[64][8];
        this.affectedQueens = new IndexNumberSet(64);
    }

    public void clear() {
        this.removed[0] = 0;
        this.introduced[0] = 0;
        this.affected[0] = 0;
        this.queenSetCleared = false;
    }

    private void addRemoved(int pid, int fieldID) {
        int size = this.removed[0] = this.removed[0] + 1;
        this.removed[size] = fieldID;
        this.removed_pids[size] = pid;
    }

    private void addIntroduced(int pid, int fieldID) {
        int size = this.introduced[0] = this.introduced[0] + 1;
        this.introduced[size] = fieldID;
        this.introduced_pids[size] = pid;
    }

    public void addPromotionFigureID_OnForwardMove(int pid, int fieldID) {
        this.addIntroduced(pid, fieldID);
    }

    public void fillRemovedIntroduced_OnForwardMove(int move) {
        long allAffectedFields = 0L;
        long shortAffectedFields = 0L;
        int pid = MoveInt.getFigurePID(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        boolean capture = MoveInt.isCapture(move);
        boolean enpass = MoveInt.isEnpassant(move);
        long toFieldBitboard = MoveInt.getToFieldBitboard(move);
        allAffectedFields |= MoveInt.getFromFieldBitboard(move);
        allAffectedFields |= toFieldBitboard;
        this.addRemoved(pid, fromFieldID);
        if (MoveInt.isCastling(move)) {
            int castleID = MoveInt.getCastlingRookPID(move);
            int castleFrom = MoveInt.getCastlingRookFromID(move);
            int castleTo = MoveInt.getCastlingRookToID(move);
            this.addRemoved(castleID, castleFrom);
            this.addIntroduced(castleID, castleTo);
            this.addIntroduced(pid, toFieldID);
            long fromCastleBoard = Fields.ALL_ORDERED_A1H1[castleFrom];
            long toCastleBoard = Fields.ALL_ORDERED_A1H1[castleTo];
            allAffectedFields |= fromCastleBoard;
            allAffectedFields |= toCastleBoard;
        } else if (enpass) {
            int enpassFieldID = MoveInt.getEnpassantCapturedFieldID(move);
            long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[enpassFieldID];
            this.addRemoved(MoveInt.getCapturedFigurePID(move), enpassFieldID);
            this.addIntroduced(pid, toFieldID);
            allAffectedFields |= opponentPawnBitboard;
        } else {
            if (!MoveInt.isPromotion(move)) {
                this.addIntroduced(pid, toFieldID);
            }
            if (capture) {
                this.addRemoved(MoveInt.getCapturedFigurePID(move), toFieldID);
            }
        }
        shortAffectedFields = allAffectedFields;
        if (capture && !enpass) {
            shortAffectedFields &= toFieldBitboard ^ 0xFFFFFFFFFFFFFFFFL;
        }
        if ((this.whiteAttacks.attacksByType(3) & shortAffectedFields) != 0L) {
            this.fillOfficersAffections(shortAffectedFields, allAffectedFields, this.whiteAttacks, 0, 3);
        }
        if ((this.blackAttacks.attacksByType(3) & shortAffectedFields) != 0L) {
            this.fillOfficersAffections(shortAffectedFields, allAffectedFields, this.blackAttacks, 1, 3);
        }
        if ((this.whiteAttacks.attacksByType(5) & shortAffectedFields) != 0L) {
            this.fillOfficersAffections(shortAffectedFields, allAffectedFields, this.whiteAttacks, 0, 5);
            this.fillCastlesAffections(shortAffectedFields, allAffectedFields, this.whiteAttacks, 0, 5);
        }
        if ((this.blackAttacks.attacksByType(5) & shortAffectedFields) != 0L) {
            this.fillOfficersAffections(shortAffectedFields, allAffectedFields, this.blackAttacks, 1, 5);
            this.fillCastlesAffections(shortAffectedFields, allAffectedFields, this.blackAttacks, 1, 5);
        }
        if ((this.whiteAttacks.attacksByType(4) & shortAffectedFields) != 0L) {
            this.fillCastlesAffections(shortAffectedFields, allAffectedFields, this.whiteAttacks, 0, 4);
        }
        if ((this.blackAttacks.attacksByType(4) & shortAffectedFields) != 0L) {
            this.fillCastlesAffections(shortAffectedFields, allAffectedFields, this.blackAttacks, 1, 4);
        }
    }

    private void fillOfficersAffections(long affectedFields, long allAffectedFields, IPlayerAttacks attacks, int colour, int figtype) {
        int pid = Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][figtype];
        PiecesList figs = this.piecesLists.getPieces(pid);
        int size = figs.getDataSize();
        int[] ids = figs.getData();
        for (int i = 0; i < size; ++i) {
            long dir3;
            long dir2;
            long dir1;
            int fieldID = ids[i];
            long pos = Fields.ALL_ORDERED_A1H1[fieldID];
            if ((allAffectedFields & pos) != 0L) continue;
            long curAttacks = attacks.attacksByFieldID(figtype, fieldID);
            if (figtype != 3) {
                curAttacks &= OfficerPlies.ALL_OFFICER_MOVES[fieldID];
            }
            if ((curAttacks & affectedFields) == 0L) continue;
            int[] dirs = this.affected_dirs[fieldID];
            int[] types = this.affected_dirs_types[fieldID];
            dirs[0] = 0;
            types[0] = 0;
            if (figtype == 5) {
                if (!this.queenSetCleared) {
                    this.affectedQueens.clear();
                    this.queenSetCleared = true;
                }
                this.affectedQueens.add(fieldID);
            }
            int cur_affected = this.affected[0] = this.affected[0] + 1;
            this.affected[cur_affected] = fieldID;
            this.affected_pids[cur_affected] = pid;
            long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[fieldID];
            if ((dir0 & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 0;
                types[0] = types[0] + 1;
                types[count] = 3;
            }
            if (((dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[fieldID]) & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 1;
                types[0] = types[0] + 1;
                types[count] = 3;
            }
            if (((dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[fieldID]) & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 2;
                types[0] = types[0] + 1;
                types[count] = 3;
            }
            if (((dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[fieldID]) & affectedFields) == 0L) continue;
            int count = dirs[0] = dirs[0] + 1;
            dirs[count] = 3;
            types[0] = types[0] + 1;
            types[count] = 3;
        }
    }

    private void fillCastlesAffections(long affectedFields, long allAffectedFields, IPlayerAttacks attacks, int colour, int figtype) {
        int pid = Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][figtype];
        PiecesList figs = this.piecesLists.getPieces(pid);
        int size = figs.getDataSize();
        int[] ids = figs.getData();
        for (int i = 0; i < size; ++i) {
            long dir3;
            long dir2;
            long dir1;
            long dir0;
            int fieldID = ids[i];
            long pos = Fields.ALL_ORDERED_A1H1[fieldID];
            if ((allAffectedFields & pos) != 0L) continue;
            long curAttacks = attacks.attacksByFieldID(figtype, fieldID);
            if (figtype != 4) {
                curAttacks &= CastlePlies.ALL_CASTLE_MOVES[fieldID];
            }
            if ((curAttacks & affectedFields) == 0L) continue;
            int[] dirs = this.affected_dirs[fieldID];
            int[] types = this.affected_dirs_types[fieldID];
            if (!this.queenSetCleared || !this.affectedQueens.contains(fieldID)) {
                types[0] = 0;
                dirs[0] = 0;
                this.affected[0] = this.affected[0] + 1;
                int cur_affected = this.affected[0];
                if (cur_affected > 7) {
                    boolean bl = false;
                }
                this.affected[cur_affected] = fieldID;
                this.affected_pids[cur_affected] = pid;
            }
            if (((dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[fieldID]) & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 0;
                types[0] = types[0] + 1;
                types[count] = 4;
            }
            if (((dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[fieldID]) & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 1;
                types[0] = types[0] + 1;
                types[count] = 4;
            }
            if (((dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[fieldID]) & affectedFields) != 0L) {
                int count = dirs[0] = dirs[0] + 1;
                dirs[count] = 2;
                types[0] = types[0] + 1;
                types[count] = 4;
            }
            if (((dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[fieldID]) & affectedFields) == 0L) continue;
            int count = dirs[0] = dirs[0] + 1;
            dirs[count] = 3;
            types[0] = types[0] + 1;
            types[count] = 4;
        }
    }

    public void checkConsistency() {
        this.removedSet.clear();
        this.introducedSet.clear();
        this.affectedSet.clear();
        this.checkUnique(this.removed, this.removedSet);
        this.checkUnique(this.introduced, this.introducedSet);
        this.checkUnique(this.affected, this.affectedSet);
    }

    private void checkUnique(int[] ids, IndexNumberSet set) {
        int size = ids[0];
        for (int i = 0; i < size; ++i) {
            set.add(ids[i + 1]);
        }
    }
}

