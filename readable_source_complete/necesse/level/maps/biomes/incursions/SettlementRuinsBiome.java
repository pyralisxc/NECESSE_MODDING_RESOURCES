/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.incursions;

import java.awt.Color;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class SettlementRuinsBiome
extends Biome {
    public static MobSpawnTable critters = new MobSpawnTable().include(Biome.defaultCaveCritters);
    public static MobSpawnTable mobs = new MobSpawnTable().add(100, "possessedsettler");

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        return critters;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        return mobs;
    }

    @Override
    public float getWindModifier(Level level, int tileX, int tileY) {
        return 0.5f;
    }

    @Override
    public double getWindProgressDivider(Level level) {
        return 10.0;
    }

    @Override
    public float getWindSpeedParticleLimit(Level level) {
        return super.getWindSpeedParticleLimit(level) / 2.0f;
    }

    @Override
    public float getWindAmountParticleLimit(Level level) {
        return super.getWindAmountParticleLimit(level) / 2.0f;
    }

    @Override
    public float getWindParticleBufferModifier(Level level) {
        return super.getWindParticleBufferModifier(level) / 5.0f;
    }

    @Override
    public Color getWindColor(Level level) {
        return new Color(204, 33, 218);
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(MusicRegistry.SettlementofHauntedDreams);
    }

    @Override
    public float getSpawnRateMod(Level level) {
        return super.getSpawnRateMod(level) * 0.3f;
    }

    @Override
    public float getSpawnCapMod(Level level) {
        return super.getSpawnCapMod(level) * 0.5f;
    }
}

