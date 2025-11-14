/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierContainerLimits;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class DeepIceTile
extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public DeepIceTile() {
        super(false, "deepice");
        this.mapColor = new Color(38, 179, 198);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(DeepIceTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 200;
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (mob.isPlayer && ((PlayerMob)mob).isServerClient()) {
            ModifierContainerLimits<Float> limits;
            ServerClient serverClient = ((PlayerMob)mob).getServerClient();
            JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.WALK_ON_DEEP_ICE_ID);
            if (!challenge.isCompleted(serverClient) && challenge.isJournalEntryDiscovered(serverClient) && (limits = mob.buffManager.getLimits(BuffModifiers.FRICTION)).hasMin() && limits.min().floatValue() >= 1.0f) {
                challenge.markCompleted(serverClient);
                serverClient.forceCombineNewStats();
            }
        }
    }

    @Override
    public ModifierValue<Float> getSpeedModifier(Mob mob) {
        if (mob.isFlying()) {
            return super.getSpeedModifier(mob);
        }
        return new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.25f));
    }

    @Override
    public ModifierValue<Float> getFrictionModifier(Mob mob) {
        if (mob.isFlying()) {
            return super.getFrictionModifier(mob);
        }
        return new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(-0.85f));
    }

    @Override
    public String canPlace(Level level, int x, int y, boolean byPlayer) {
        if (level.getTileID(x, y) != TileRegistry.waterID) {
            return "notwater";
        }
        boolean hasShore = !level.getTile((int)(x - 1), (int)y).isLiquid;
        hasShore = hasShore || !level.getTile((int)(x + 1), (int)y).isLiquid;
        hasShore = hasShore || !level.getTile((int)x, (int)(y - 1)).isLiquid;
        boolean bl = hasShore = hasShore || !level.getTile((int)x, (int)(y + 1)).isLiquid;
        if (!hasShore) {
            return "notshore";
        }
        return null;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "icetip"));
        return tooltips;
    }

    @Override
    public int getDestroyedTile() {
        return TileRegistry.waterID;
    }

    @Override
    public boolean canBePlacedOn(Level level, int tileX, int tileY, GameTile placing) {
        return false;
    }
}

