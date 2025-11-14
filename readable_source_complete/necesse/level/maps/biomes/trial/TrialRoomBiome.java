/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.trial;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class TrialRoomBiome
extends Biome {
    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        return new MobSpawnTable();
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        return new MobSpawnTable();
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(MusicRegistry.Kronos);
    }
}

