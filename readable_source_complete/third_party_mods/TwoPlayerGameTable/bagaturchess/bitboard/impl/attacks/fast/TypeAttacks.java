/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.AttacksBuilder;
import bagaturchess.bitboard.impl.state.PiecesList;

public class TypeAttacks {
    private int pid;
    private int colour;
    public int type;
    private Board bitboard;
    public long[] unintersected;
    public int unintersected_size;
    private PiecesList aliveFigureIDs;
    private long[] attacksByFieldID;
    private FieldsStateMachine fieldAttacksCollector;

    public TypeAttacks(int _pid, Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
        this.pid = _pid;
        this.colour = Figures.getFigureColour(this.pid);
        this.type = Figures.getFigureType(this.pid);
        this.bitboard = _bitboard;
        this.fieldAttacksCollector = _fieldAttacksCollector;
        this.aliveFigureIDs = this.bitboard.pieces.getPieces(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[this.colour][this.type]);
        this.attacksByFieldID = new long[64];
        this.init();
    }

    private void init() {
        this.unintersected = new long[8];
        this.clear();
        this.genAllAttacks();
    }

    private void clear() {
        this.unintersected_size = 0;
        this.unintersected[0] = 0L;
    }

    private void genAllAttacks() {
        int size = this.aliveFigureIDs.getDataSize();
        int[] ids = this.aliveFigureIDs.getData();
        for (int i = 0; i < size; ++i) {
            int curFieldID = ids[i];
            if (this.fieldAttacksCollector != null) {
                this.fieldAttacksCollector.addFigure(this.colour, this.type, curFieldID);
            }
            long attacks = this.buildFigureAttacks(curFieldID, -1, -1, true);
            this.addFigureAttacks(curFieldID, attacks, false);
        }
    }

    private long buildFigureAttacks(int fieldID, int dirID, int dirType, boolean add) {
        if (this.fieldAttacksCollector != null) {
            return AttacksBuilder.genAttacks(this.bitboard, this.colour, this.type, fieldID, dirID, dirType, this.fieldAttacksCollector, add);
        }
        return AttacksBuilder.genAttacks(this.bitboard, this.colour, this.type, fieldID, dirID, dirType);
    }

    public void addFigure(int fieldID, int dirID, int dirType) {
        long attacks = this.buildFigureAttacks(fieldID, dirID, dirType, true);
        this.addFigureAttacks(fieldID, attacks, dirID != -1);
    }

    public void removeFigure(int fieldID, int dirID, int dirType) {
        long attacks = this.buildFigureAttacks(fieldID, dirID, dirType, false);
        this.removeFigureAttacks(fieldID, attacks, dirID != -1);
    }

    private void addFigureAttacks(int fieldID, long attacks, boolean partial) {
        if (partial) {
            int n = fieldID;
            this.attacksByFieldID[n] = this.attacksByFieldID[n] | attacks;
        } else {
            this.attacksByFieldID[fieldID] = attacks;
        }
        long allBackup = this.unintersected[0];
        long intersection = attacks & allBackup;
        if (intersection != 0L) {
            if (this.unintersected_size == 0) {
                this.unintersected[this.unintersected_size++] = attacks;
                this.unintersected[this.unintersected_size++] = intersection;
            } else {
                for (int i = 0; i < this.unintersected_size; ++i) {
                    long cur = this.unintersected[i];
                    intersection = attacks & cur;
                    this.unintersected[i] = cur | attacks;
                    attacks = intersection;
                    if (attacks != 0L) {
                        if (i != this.unintersected_size - 1) continue;
                        this.unintersected[this.unintersected_size++] = attacks;
                    }
                    break;
                }
            }
        } else {
            if (this.unintersected_size == 0) {
                ++this.unintersected_size;
            }
            this.unintersected[0] = this.unintersected[0] | attacks;
        }
    }

    private void removeFigureAttacks(int fieldID, long attacks, boolean partial) {
        if (this.unintersected_size < 1) {
            throw new IllegalStateException();
        }
        if (partial) {
            int n = fieldID;
            this.attacksByFieldID[n] = this.attacksByFieldID[n] & (attacks ^ 0xFFFFFFFFFFFFFFFFL);
        } else {
            this.attacksByFieldID[fieldID] = 0L;
        }
        int size = this.unintersected_size;
        for (int i = size - 1; i >= 0; --i) {
            long cur = this.unintersected[i];
            long intersection = attacks & cur;
            this.unintersected[i] = this.unintersected[i] & (attacks ^ 0xFFFFFFFFFFFFFFFFL);
            if (this.unintersected[i] == 0L) {
                --this.unintersected_size;
            }
            if ((attacks &= intersection ^ 0xFFFFFFFFFFFFFFFFL) == 0L) break;
        }
    }

    public int attacksByTypeUnintersectedSize() {
        return this.unintersected_size;
    }

    public long[] attacksByTypeUnintersected() {
        return this.unintersected;
    }

    public long attacksByFieldID(int fieldID) {
        return this.attacksByFieldID[fieldID];
    }

    public long allAttacks() {
        if (this.unintersected_size < 1 && this.unintersected[0] != 0L) {
            throw new IllegalStateException();
        }
        return this.unintersected[0];
    }

    public void checkConsistency() {
        long testAll;
        if (this.unintersected_size < 0) {
            throw new IllegalStateException();
        }
        if (this.type != 1) {
            testAll = 0L;
            int size = this.aliveFigureIDs.getDataSize();
            int[] ids = this.aliveFigureIDs.getData();
            for (int i = 0; i < size; ++i) {
                int curFigureID = ids[i];
                long curAttacks = this.attacksByFieldID[curFigureID];
                if (curAttacks == 0L) {
                    throw new IllegalStateException("attacks is 0");
                }
                testAll |= curAttacks;
            }
            if (testAll != this.unintersected[0]) {
                throw new IllegalStateException();
            }
        }
        if (this.unintersected[0] != 0L && this.unintersected_size <= 0) {
            throw new IllegalStateException();
        }
        if (this.unintersected_size > 0) {
            int i;
            testAll = 0L;
            for (i = 0; i < this.unintersected_size; ++i) {
                long curAttacks = this.unintersected[i];
                if (curAttacks == 0L) {
                    throw new IllegalStateException("attacks is 0");
                }
                testAll |= curAttacks;
            }
            if (testAll != this.unintersected[0]) {
                throw new IllegalStateException();
            }
            for (i = this.unintersected_size - 1; i >= 1; --i) {
                long iAttacks = this.unintersected[i];
                if (iAttacks == 0L) {
                    throw new IllegalStateException("attacks is 0");
                }
                long beforiAttacks = this.unintersected[i - 1];
                if (beforiAttacks == 0L) {
                    throw new IllegalStateException("attacks is 0");
                }
                if ((iAttacks & beforiAttacks) == iAttacks) continue;
                throw new IllegalStateException();
            }
        }
    }
}

