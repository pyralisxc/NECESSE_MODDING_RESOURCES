/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToTile;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.SettlerMission;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class RunOutMission
extends SettlerMission {
    protected boolean isOut;
    protected Point moveOutPoint;

    @Override
    public void start(HumanMob mob) {
        this.isOut = false;
        this.moveOutPoint = mob.getNewEdgeOfSettlementTile();
    }

    @Override
    public void addSaveData(HumanMob mob, SaveData save) {
        save.addBoolean("isOut", this.isOut);
        save.addPoint("moveOutPoint", this.moveOutPoint);
    }

    @Override
    public void applySaveData(HumanMob mob, LoadData save) {
        this.isOut = save.getBoolean("isOut", this.isOut);
        this.moveOutPoint = save.getPoint("moveOutPoint", this.moveOutPoint);
        if (this.moveOutPoint == null) {
            this.moveOutPoint = mob.getNewEdgeOfSettlementTile();
        }
    }

    @Override
    public void setupMovementPacket(HumanMob mob, PacketWriter writer) {
        writer.putNextBoolean(this.isOut);
    }

    @Override
    public void applyMovementPacket(HumanMob mob, PacketReader reader) {
        this.isOut = reader.getNextBoolean();
    }

    @Override
    public MoveToTile getMoveOutPoint(final HumanMob mob) {
        if (this.isOut) {
            return null;
        }
        if (mob.isAtEdgeOfSettlement()) {
            this.isOut = true;
            mob.stopMoving();
            mob.sendMovementPacket(true);
            return null;
        }
        return new MoveToTile(this.moveOutPoint, true){

            @Override
            public boolean moveIfPathFailed(float tileDistance) {
                return tileDistance >= 30.0f;
            }

            @Override
            public boolean isAtLocation(float tileDistance, boolean foundPath) {
                if (foundPath) {
                    return tileDistance < 2.0f;
                }
                return tileDistance < 20.0f;
            }

            @Override
            public void onArrivedAtLocation() {
                RunOutMission.this.isOut = true;
                mob.sendMovementPacket(true);
            }
        };
    }

    @Override
    public boolean isMobVisible(HumanMob mob) {
        return !this.isOut;
    }

    @Override
    public boolean isMobIdle(HumanMob mob) {
        return this.isOut;
    }

    @Override
    public void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("isOut: " + this.isOut);
        if (this.moveOutPoint != null) {
            tooltips.add("moveOutPoint: [" + this.moveOutPoint.x + ", " + this.moveOutPoint.y + "]");
        }
    }
}

