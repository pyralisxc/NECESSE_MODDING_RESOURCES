/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class LevelTile {
    public final GameTile tile;
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final boolean isPlayerPlaced;

    private LevelTile(Level level, int tileX, int tileY, GameTile tile, boolean isPlayerPlaced) {
        this.tile = tile;
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isPlayerPlaced = isPlayerPlaced;
    }

    public LevelTile(Level level, int tileX, int tileY) {
        this(level, tileX, tileY, level.getTile(tileX, tileY), level.tileLayer.isPlayerPlaced(tileX, tileY));
    }

    public static LevelTile custom(Level level, int tileX, int tileY, GameTile tile, boolean isPlayerPlaced) {
        return new LevelTile(level, tileX, tileY, tile, isPlayerPlaced);
    }

    public String canPlace() {
        return this.tile.canPlace(this.level, this.tileX, this.tileY, false);
    }

    public boolean isValid() {
        return this.tile.isValid(this.level, this.tileX, this.tileY);
    }

    public void checkAround() {
        this.tile.checkAround(this.level, this.tileX, this.tileY);
    }

    public void attemptPlace(PlayerMob player, String message) {
        this.tile.attemptPlace(this.level, this.tileX, this.tileY, player, message);
    }

    public void onTileDestroyed(Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        this.tile.onDestroyed(this.level, this.tileX, this.tileY, attacker, client, itemsDropped);
    }

    public void tick(Mob mob) {
        this.tile.tick(mob, this.level, this.tileX, this.tileY);
    }

    public void tick() {
        this.tile.tick(this.level, this.tileX, this.tileY);
    }

    public void tickValid(boolean underGeneration) {
        this.tile.tickValid(this.level, this.tileX, this.tileY, underGeneration);
    }

    public void onDamaged(int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        this.tile.onDamaged(this.level, this.tileX, this.tileY, damage, attacker, client, showEffect, mouseX, mouseY);
    }

    public GameTooltips getMapTooltips() {
        return this.tile.getMapTooltips(this.level, this.tileX, this.tileY);
    }

    public int getLiquidBobbing() {
        return this.tile.getLiquidBobbing(this.level, this.tileX, this.tileY);
    }

    public int getHeight() {
        return this.level.liquidManager.getHeight(this.tileX, this.tileY);
    }

    public String toString() {
        return super.toString() + "{" + this.tileX + "x" + this.tileY + ", " + this.level.getHostString() + ", " + this.tile.getDisplayName() + "}";
    }
}

