/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.AffectionData;
import bagaturchess.bitboard.impl.attacks.fast.FastPlayerAttacks;

public class FastPlayersAttacks
implements MoveListener {
    private Board bitboard;
    private FastPlayerAttacks white;
    private FastPlayerAttacks black;
    private int playedMoveIndex = 0;
    private AffectionData[] leveldata;
    private FieldsStateMachine fieldAttacksCollector;

    public FastPlayersAttacks(Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
        this.bitboard = _bitboard;
        this.fieldAttacksCollector = _fieldAttacksCollector;
        this.white = new FastPlayerAttacks(0, this.bitboard, this.fieldAttacksCollector);
        this.black = new FastPlayerAttacks(1, this.bitboard, this.fieldAttacksCollector);
        this.leveldata = new AffectionData[2000];
        for (int i = 0; i < 2000; ++i) {
            this.leveldata[i] = new AffectionData(this.bitboard, this.white, this.black);
        }
    }

    public IPlayerAttacks getWhiteAttacks() {
        return this.white;
    }

    public IPlayerAttacks getBlackAttacks() {
        return this.black;
    }

    @Override
    public void preForwardMove(int color, int move) {
        ++this.playedMoveIndex;
        AffectionData data = this.leveldata[this.playedMoveIndex - 1];
        data.clear();
        data.fillRemovedIntroduced_OnForwardMove(move);
        this.remove(data.removed, data.removed_pids);
        this.removeAffected(data);
    }

    @Override
    public void addPiece_Special(int promotedPID, int fieldID) {
        AffectionData data = this.leveldata[this.playedMoveIndex - 1];
        data.addPromotionFigureID_OnForwardMove(promotedPID, fieldID);
    }

    @Override
    public void initially_addPiece(int promotedPID, int fieldID, long bb_pieces) {
        AffectionData data = this.leveldata[this.playedMoveIndex - 1];
        data.addPromotionFigureID_OnForwardMove(promotedPID, fieldID);
    }

    @Override
    public void postForwardMove(int color, int move) {
        AffectionData data = this.leveldata[this.playedMoveIndex - 1];
        this.introduce(data.introduced, data.introduced_pids);
        this.introduceAffected(data);
    }

    @Override
    public void preBackwardMove(int color, int move) {
        --this.playedMoveIndex;
        AffectionData data = this.leveldata[this.playedMoveIndex];
        this.remove(data.introduced, data.introduced_pids);
        this.removeAffected(data);
    }

    @Override
    public void postBackwardMove(int color, int move) {
        AffectionData data = this.leveldata[this.playedMoveIndex];
        this.introduce(data.removed, data.removed_pids);
        this.introduceAffected(data);
    }

    private void remove(int[] removed, int[] removed_pids) {
        int size = removed[0];
        for (int i = 0; i < size; ++i) {
            int fieldID = removed[i + 1];
            int pid = removed_pids[i + 1];
            int colour = Figures.getFigureColour(pid);
            if (colour == 0) {
                this.white.removeFigure(pid, fieldID, -1, -1);
                continue;
            }
            this.black.removeFigure(pid, fieldID, -1, -1);
        }
    }

    private void removeAffected(AffectionData data) {
        int[] affected = data.affected;
        int[] affected_pids = data.affected_pids;
        int size = affected[0];
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                int j;
                int fieldID = affected[i + 1];
                int pid = affected_pids[i + 1];
                int[] dirs = data.affected_dirs[fieldID];
                int[] types = data.affected_dirs_types[fieldID];
                int colour = Figures.getFigureColour(pid);
                if (colour == 0) {
                    for (j = 0; j < dirs[0]; ++j) {
                        this.white.removeFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
                    }
                    continue;
                }
                for (j = 0; j < dirs[0]; ++j) {
                    this.black.removeFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
                }
            }
        }
    }

    private void introduce(int[] introduced, int[] introduced_pids) {
        int size = introduced[0];
        for (int i = 0; i < size; ++i) {
            int fieldID = introduced[i + 1];
            int pid = introduced_pids[i + 1];
            int colour = Figures.getFigureColour(pid);
            if (colour == 0) {
                this.white.introduceFigure(pid, fieldID, -1, -1);
                continue;
            }
            this.black.introduceFigure(pid, fieldID, -1, -1);
        }
    }

    private void introduceAffected(AffectionData data) {
        int[] affected = data.affected;
        int[] affected_pids = data.affected_pids;
        int size = affected[0];
        if (size > 0) {
            for (int i = 0; i < size; ++i) {
                int j;
                int fieldID = affected[i + 1];
                int pid = affected_pids[i + 1];
                int[] dirs = data.affected_dirs[fieldID];
                int[] types = data.affected_dirs_types[fieldID];
                int colour = Figures.getFigureColour(pid);
                if (colour == 0) {
                    for (j = 0; j < dirs[0]; ++j) {
                        this.white.introduceFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
                    }
                    continue;
                }
                for (j = 0; j < dirs[0]; ++j) {
                    this.black.introduceFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
                }
            }
        }
    }

    public FieldsStateMachine getFieldAttacksCollector() {
        return this.fieldAttacksCollector;
    }

    public void checkConsistency() {
        this.white.checkConsistency();
        this.black.checkConsistency();
    }
}

