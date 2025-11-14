/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public interface DrawOnMapEntity {
    default public boolean shouldDrawOnMap() {
        return false;
    }

    public boolean isVisibleOnMap(Client var1, ClientDiscoveredMap var2);

    default public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-8, -8, 16, 16);
    }

    public Point getMapPos();

    default public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
    }

    default public GameTooltips getMapTooltips() {
        return null;
    }

    default public String getMapInteractTooltip() {
        return null;
    }

    default public void onMapInteract(InputEvent event, PlayerMob perspective) {
    }
}

