/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.temple;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.temple.TempleArenaLevel;

public class TempleBiome
extends ForestBiome {
    public static MobSpawnTable templeMobs = new MobSpawnTable().add(50, "ancientskeleton").add(50, "ancientarmoredskeleton").add(40, "ancientskeletonthrower").add(30, "ancientskeletonmage");

    @Override
    public float getSpawnRateMod(Level level) {
        return super.getSpawnRateMod(level) * 0.75f;
    }

    @Override
    public float getSpawnCapMod(Level level) {
        return super.getSpawnCapMod(level) * 0.75f;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (level instanceof TempleArenaLevel) {
            return new MobSpawnTable();
        }
        return templeMobs;
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            return defaultCaveCritters;
        }
        return defaultSurfaceCritters;
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(MusicRegistry.LostTemple);
    }
}

