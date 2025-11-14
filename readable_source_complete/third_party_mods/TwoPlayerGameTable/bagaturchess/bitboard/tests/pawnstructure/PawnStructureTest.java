/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.tests.pawnstructure;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.eval.pawns.model.ModelBuilder;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;

public abstract class PawnStructureTest
extends Fields {
    private PawnsModel model;
    protected IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(this.getFEN());

    public PawnStructureTest() {
        this.model = ModelBuilder.build(this.bitboard);
    }

    public abstract String getFEN();

    public abstract void validate();

    protected void validatePassers(int colour, long fields, int ranks) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        int all_ranks = 0;
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isPassed()) continue;
            ver |= p[i].getField();
            all_ranks += p[i].getRank();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
        if (all_ranks != ranks) {
            throw new IllegalStateException("ranks=" + ranks + ", all_ranks=" + all_ranks);
        }
    }

    protected void validateUnstoppablePassers(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isPassedUnstoppable()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateGuards(int colour, long fields, int distance) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        int all_distance = 0;
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isGuard()) continue;
            ver |= p[i].getField();
            all_distance += p[i].getGuardRemoteness();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
        if (all_distance != distance) {
            throw new IllegalStateException("all_distance=" + all_distance + ", distance=" + distance);
        }
    }

    protected void validateStorms(int colour, long fields, int distance) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        int all_distance = 0;
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isStorm()) continue;
            ver |= p[i].getField();
            all_distance += p[i].getStormCloseness();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
        if (all_distance != distance) {
            throw new IllegalStateException("all_distance=" + all_distance + ", distance=" + distance);
        }
    }

    protected void validateDoubled(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isDoubled()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateBackward(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isBackward()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateIsolated(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isIsolated()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateSupported(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].isSupported()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateCannotBeSupported(int colour, long fields) {
        int count = -1;
        Pawn[] p = null;
        if (colour == 0) {
            count = this.model.getWCount();
            p = this.model.getWPawns();
        } else {
            count = this.model.getBCount();
            p = this.model.getBPawns();
        }
        long ver = 0L;
        for (int i = 0; i < count; ++i) {
            if (!p[i].cannotBeSupported()) continue;
            ver |= p[i].getField();
        }
        if (ver != fields) {
            throw new IllegalStateException("ver=" + ver + ", fields=" + fields);
        }
    }

    protected void validateIslandsCount(int colour, int expected) {
        int count = 0;
        count = colour == 0 ? this.model.getWIslandsCount() : this.model.getBIslandsCount();
        if (count != expected) {
            throw new IllegalStateException("count" + count + ", expected=" + expected);
        }
    }

    protected void validateHalfOpenedFiles(int colour, long verticals) {
        if (colour == 0) {
            if (verticals != this.model.getWHalfOpenedFiles()) {
                throw new IllegalStateException("white" + verticals + ", model.getWHalfOpennedFiles()=" + this.model.getWHalfOpenedFiles());
            }
        } else if (verticals != this.model.getBHalfOpenedFiles()) {
            throw new IllegalStateException("black" + verticals + ", model.getBHalfOpennedFiles()=" + this.model.getBHalfOpenedFiles());
        }
    }

    protected void validateOpenedFiles(long verticals) {
        if (verticals != this.model.getOpenedFiles()) {
            throw new IllegalStateException("white" + verticals + ", model.getOpenedFiles()=" + this.model.getOpenedFiles());
        }
    }

    protected void validateKingVerticals(int colour, long verticals) {
        if (colour == 0) {
            if (verticals != this.model.getWKingVerticals()) {
                throw new IllegalStateException("white" + verticals + ", model.getWKingVerticals()=" + this.model.getWKingVerticals());
            }
        } else if (verticals != this.model.getBKingVerticals()) {
            throw new IllegalStateException("black" + verticals + ", model.getBKingVerticals()=" + this.model.getBKingVerticals());
        }
    }

    protected void validateKingOpenedAndSemiOpened(int colour, int openedCount, int selfSemiOpenedCount, int opSemiOpenedCount) {
        if (colour == 0) {
            if (openedCount != this.model.getWKingOpenedFiles()) {
                throw new IllegalStateException("openedCount=" + openedCount + ", model.getWKingOpenedFiles()=" + this.model.getWKingOpenedFiles());
            }
            if (selfSemiOpenedCount != this.model.getWKingSemiOwnOpenedFiles()) {
                throw new IllegalStateException("selfSemiOpenedCount=" + openedCount + ", model.getWKingSemiOwnOpenedFiles()=" + this.model.getWKingSemiOwnOpenedFiles());
            }
            if (opSemiOpenedCount != this.model.getWKingSemiOpOpenedFiles()) {
                throw new IllegalStateException("opSemiOpenedCount=" + openedCount + ", model.getWKingSemiOpOpenedFiles()=" + this.model.getWKingSemiOpOpenedFiles());
            }
        } else {
            if (openedCount != this.model.getBKingOpenedFiles()) {
                throw new IllegalStateException("openedCount=" + openedCount + ", model.getBKingOpenedFiles()=" + this.model.getBKingOpenedFiles());
            }
            if (selfSemiOpenedCount != this.model.getBKingSemiOwnOpenedFiles()) {
                throw new IllegalStateException("selfSemiOpenedCount=" + openedCount + ", model.getBKingSemiOwnOpenedFiles()=" + this.model.getBKingSemiOwnOpenedFiles());
            }
            if (opSemiOpenedCount != this.model.getBKingSemiOpOpenedFiles()) {
                throw new IllegalStateException("opSemiOpenedCount=" + openedCount + ", model.getBKingSemiOpOpenedFiles()=" + this.model.getBKingSemiOpOpenedFiles());
            }
        }
    }

    protected void validateWeakFields(int colour, long weak) {
        if (colour == 0) {
            if (weak != (long)this.model.getWWeakFields()) {
                throw new IllegalStateException("weak=" + weak + ", model.getWWeakFields()=" + this.model.getWWeakFields());
            }
        } else if (weak != (long)this.model.getBWeakFields()) {
            throw new IllegalStateException("weak=" + weak + ", model.getBWeakFields()=" + this.model.getBWeakFields());
        }
    }
}

