/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HomestoneObjectEntity
extends ObjectEntity {
    private final ObjectDamagedTextureArray mapTexture;

    public HomestoneObjectEntity(Level level, int x, int y, ObjectDamagedTextureArray mapTexture) {
        super(level, "homestone", x, y);
        this.mapTexture = mapTexture;
    }

    @Override
    public void serverTick() {
        Point homestoneTile;
        ServerSettlementData settlement;
        if (this.getLevel().tickManager().getTick() == 1 && (settlement = SettlementsWorldData.getSettlementsData(this.getLevel()).getServerDataAtTile(this.getLevel().getIdentifier(), this.tileX, this.tileY)) != null && (homestoneTile = settlement.getHomestoneTile()) == null) {
            settlement.setHomestoneTile(new Point(this.tileX, this.tileY));
        }
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-24, -24, 48, 48);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        GameTexture texture = this.mapTexture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(0, 0, 64, texture.getHeight()).size(48).draw(x - 24, y - 24 - 16);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getObject().getDisplayName());
    }
}

