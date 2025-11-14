/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class GraveyardBiome
extends Biome {
    public static MobSpawnTable critters = new MobSpawnTable().include(Biome.defaultCaveCritters);
    public static MobSpawnTable mobs = new MobSpawnTable().add(100, "cryptbat").add(75, "cryptvampire").add(50, "phantom");

    @Override
    public boolean canRain(Level level) {
        return false;
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        return critters;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        return mobs;
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(MusicRegistry.InvasionoftheCrypt);
    }
}

