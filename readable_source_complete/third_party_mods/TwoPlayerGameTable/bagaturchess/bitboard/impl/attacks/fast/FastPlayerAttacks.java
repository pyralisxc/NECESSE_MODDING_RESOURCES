/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.TypeAttacks;

public class FastPlayerAttacks
implements IPlayerAttacks {
    private int colour;
    private Board bitboard;
    private TypeAttacks king;
    private TypeAttacks pawns;
    private TypeAttacks knights;
    private TypeAttacks officers;
    private TypeAttacks castles;
    private TypeAttacks queens;
    private FieldsStateMachine fieldAttacksCollector;

    public FastPlayerAttacks(int _colour, Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
        this.colour = _colour;
        this.bitboard = _bitboard;
        this.fieldAttacksCollector = _fieldAttacksCollector;
        if (this.colour == 0) {
            this.king = new TypeAttacks(6, this.bitboard, this.fieldAttacksCollector);
            this.pawns = new TypeAttacks(1, this.bitboard, this.fieldAttacksCollector);
            this.knights = new TypeAttacks(2, this.bitboard, this.fieldAttacksCollector);
            this.officers = new TypeAttacks(3, this.bitboard, this.fieldAttacksCollector);
            this.castles = new TypeAttacks(4, this.bitboard, this.fieldAttacksCollector);
            this.queens = new TypeAttacks(5, this.bitboard, this.fieldAttacksCollector);
        } else {
            this.king = new TypeAttacks(12, this.bitboard, this.fieldAttacksCollector);
            this.pawns = new TypeAttacks(7, this.bitboard, this.fieldAttacksCollector);
            this.knights = new TypeAttacks(8, this.bitboard, this.fieldAttacksCollector);
            this.officers = new TypeAttacks(9, this.bitboard, this.fieldAttacksCollector);
            this.castles = new TypeAttacks(10, this.bitboard, this.fieldAttacksCollector);
            this.queens = new TypeAttacks(11, this.bitboard, this.fieldAttacksCollector);
        }
    }

    private TypeAttacks getTypeAttacks(int pid) {
        int type = Figures.getFigureType(pid);
        return this.getTypeAttacksByType(type);
    }

    private TypeAttacks getTypeAttacksByType(int type) {
        switch (type) {
            case 6: {
                return this.king;
            }
            case 2: {
                return this.knights;
            }
            case 1: {
                return this.pawns;
            }
            case 3: {
                return this.officers;
            }
            case 4: {
                return this.castles;
            }
            case 5: {
                return this.queens;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public long allAttacks() {
        return this.pawns.allAttacks() | this.knights.allAttacks() | this.officers.allAttacks() | this.castles.allAttacks() | this.queens.allAttacks() | this.king.allAttacks();
    }

    @Override
    public long attacksByType(int type) {
        return this.getTypeAttacksByType(type).allAttacks();
    }

    @Override
    public long[] attacksByTypeUnintersected(int type) {
        return this.getTypeAttacksByType(type).attacksByTypeUnintersected();
    }

    @Override
    public int attacksByTypeUnintersectedSize(int type) {
        return this.getTypeAttacksByType(type).attacksByTypeUnintersectedSize();
    }

    @Override
    public long attacksByFieldID(int type, int figureID) {
        switch (type) {
            case 1: {
                return this.pawns.attacksByFieldID(figureID);
            }
            case 2: {
                return this.knights.attacksByFieldID(figureID);
            }
            case 3: {
                return this.officers.attacksByFieldID(figureID);
            }
            case 4: {
                return this.castles.attacksByFieldID(figureID);
            }
            case 5: {
                return this.queens.attacksByFieldID(figureID);
            }
            case 6: {
                return this.king.attacksByFieldID(figureID);
            }
        }
        throw new IllegalStateException();
    }

    public void removeFigure(int pid, int fieldID, int dirID, int dirType) {
        TypeAttacks attacks = this.getTypeAttacks(pid);
        attacks.removeFigure(fieldID, dirID, dirType);
        if (this.fieldAttacksCollector != null) {
            this.fieldAttacksCollector.removeFigure(this.colour, Figures.getFigureType(pid), fieldID);
        }
    }

    public void introduceFigure(int pid, int fieldID, int dirID, int dirType) {
        TypeAttacks attacks = this.getTypeAttacks(pid);
        attacks.addFigure(fieldID, dirID, dirType);
        if (this.fieldAttacksCollector != null) {
            this.fieldAttacksCollector.addFigure(this.colour, Figures.getFigureType(pid), fieldID);
        }
    }

    @Override
    public int countAttacks(int type, long field) {
        int size = this.attacksByTypeUnintersectedSize(type);
        long[] attacks = this.attacksByTypeUnintersected(type);
        int count = 0;
        for (int i = 0; i < size; ++i) {
            long a = attacks[i];
            if ((a & field) == 0L) continue;
            ++count;
        }
        return count;
    }

    @Override
    public void checkConsistency() {
        this.king.checkConsistency();
        this.pawns.checkConsistency();
        this.knights.checkConsistency();
        this.officers.checkConsistency();
        this.castles.checkConsistency();
        this.queens.checkConsistency();
    }
}

