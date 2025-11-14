/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.util.function.BooleanSupplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.settlementData.zones.SettlementTileTickZone;

public class SettlementFertilizeZone
extends SettlementTileTickZone {
    @Override
    protected void handleTile(Point tile) {
        SeedObjectEntity seedEnt;
        ObjectEntity objEnt = this.manager.data.getLevel().entityManager.getObjectEntity(tile.x, tile.y);
        if (objEnt instanceof SeedObjectEntity && !(seedEnt = (SeedObjectEntity)objEnt).isFertilized()) {
            this.manager.data.getLevel().jobsLayer.addJob(new FertilizeLevelJob(tile.x, tile.y, this, seedEnt.fertilizeReservable));
        }
    }

    @Override
    public boolean isHiddenSetting() {
        return Settings.hideSettlementFertilizeZones.get();
    }

    @Override
    public GameMessage getDefaultName(int number) {
        return new LocalMessage("ui", "settlementfertilizezonedefname", "number", number);
    }

    @Override
    public GameMessage getAbstractName() {
        return new LocalMessage("ui", "settlementfertilizezone");
    }

    @Override
    public HudDrawElement getHudDrawElement(int drawPriority, BooleanSupplier overrideShow) {
        return this.getHudDrawElement(drawPriority, overrideShow, new Color(67, 217, 33, 150), new Color(36, 118, 18, 75));
    }
}

