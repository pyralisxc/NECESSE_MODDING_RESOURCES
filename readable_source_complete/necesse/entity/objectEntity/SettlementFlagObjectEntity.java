/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.light.GameLight;

public class SettlementFlagObjectEntity
extends ObjectEntity {
    private final ObjectDamagedTextureArray mapTexture;

    public SettlementFlagObjectEntity(Level level, int x, int y, ObjectDamagedTextureArray mapTexture) {
        super(level, "settlement", x, y);
        this.mapTexture = mapTexture;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug && this.isServer()) {
            ServerSettlementData serverData = SettlementsWorldData.getSettlementsData(this).getServerDataAtTile(this.getLevel().getIdentifier(), this.tileX, this.tileY);
            if (serverData != null) {
                GameTooltipManager.addTooltip(serverData.getDebugTooltips(), TooltipLocation.INTERACT_FOCUS);
            } else {
                GameTooltipManager.addTooltip(new StringTooltips("No settlement found here"), TooltipLocation.INTERACT_FOCUS);
            }
        }
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        if (isMinimap) {
            return new Rectangle(-20, -20, 40, 40);
        }
        return new Rectangle(-32, -32, 64, 64);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        int lookSize;
        int objectSize;
        int yLookOffset;
        int xLookOffset;
        int yDrawOffset;
        int xDrawOffset;
        if (isMinimap) {
            xDrawOffset = -20;
            yDrawOffset = -30;
            xLookOffset = -8;
            yLookOffset = 14;
            objectSize = 40;
            lookSize = 16;
        } else {
            xDrawOffset = -32;
            yDrawOffset = -48;
            xLookOffset = -11;
            yLookOffset = 5;
            objectSize = 64;
            lookSize = 24;
        }
        int drawX = x + xDrawOffset;
        int drawY = y + yDrawOffset;
        GameTexture texture = this.mapTexture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(0, 0, 32, texture.getHeight()).size(objectSize).draw(drawX, drawY);
        NetworkSettlementData networkData = SettlementsWorldData.getSettlementsData(this).getNetworkDataAtTile(this.getLevel().getIdentifier(), this.tileX, this.tileY);
        HumanLook humanLook = networkData == null ? null : networkData.getLook();
        DrawOptions lookOptions = humanLook != null ? SettlementFlagObject.getHumanLookDrawOptions(x + xLookOffset, y + yLookOffset, lookSize, 1.0f, new GameLight(150.0f), humanLook) : texture.initDraw().sprite(1, 0, 32, texture.getHeight()).size(objectSize).light(new GameLight(150.0f)).pos(drawX, drawY - 2);
        lookOptions.draw();
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getObject().getDisplayName());
    }
}

