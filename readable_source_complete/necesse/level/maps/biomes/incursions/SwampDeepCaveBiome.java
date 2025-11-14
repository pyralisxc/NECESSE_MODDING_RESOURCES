/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.incursions;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobSpawnTable;

public class SwampDeepCaveBiome
extends Biome {
    public static MobSpawnTable critters = new MobSpawnTable().include(Biome.defaultCaveCritters);
    public static MobSpawnTable mobs = new MobSpawnTable().add(70, "ancientskeleton").add(25, "ancientskeletonthrower").add(30, "swampskeleton").add(40, "swampdweller").add(70, "giantswampslime");

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("biome", "deepcave", "biome", new LocalMessage("biome", "swamp"));
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
        return new MusicList(MusicRegistry.SwampCavern);
    }
}

