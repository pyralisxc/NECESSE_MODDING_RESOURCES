/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class DesertDeepCaveBiome
extends Biome {
    public static MobSpawnTable critters = new MobSpawnTable().include(Biome.defaultCaveCritters);
    public static MobSpawnTable mobs = new MobSpawnTable().add(100, "ancientskeleton").add(40, "ancientskeletonthrower").add(50, "desertcrawler").addLimited(6, "sandworm", 1, Mob.MOB_SPAWN_AREA.maxSpawnDistance * 2);

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("biome", "deepcave", "biome", new LocalMessage("biome", "desert"));
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
        return new MusicList(MusicRegistry.SandCatacombs);
    }
}

