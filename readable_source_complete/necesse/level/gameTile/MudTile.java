/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class MudTile
extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public MudTile() {
        super(false, "mud");
        this.mapColor = new Color(121, 73, 42);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(MudTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }

    @Override
    public ModifierValue<Float> getSlowModifier(Mob mob) {
        if (mob.isFlying()) {
            return super.getSpeedModifier(mob);
        }
        return new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.25f));
    }
}

