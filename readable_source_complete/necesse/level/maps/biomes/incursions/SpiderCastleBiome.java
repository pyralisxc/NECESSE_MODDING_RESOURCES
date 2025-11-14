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

public class SpiderCastleBiome
extends Biome {
    public static MobSpawnTable critters = new MobSpawnTable().include(defaultCaveCritters);
    public static MobSpawnTable mobs = new MobSpawnTable().add(50, "webspinner").add(30, "bloatedspider").add(30, "spiderkin").add(75, "spiderkinwarrior").add(75, "spiderkinarcher").add(50, "spiderkinmage");

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
        return new MusicList(MusicRegistry.VenomousReckoning);
    }
}

