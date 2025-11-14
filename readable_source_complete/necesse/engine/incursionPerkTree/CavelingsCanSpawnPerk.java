/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.AltarData;

public class CavelingsCanSpawnPerk
extends IncursionPerk {
    public CavelingsCanSpawnPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        super.onIncursionLevelGenerated(level, altarData, modifierIndex);
        if (Objects.equals(level.incursionData.getStringID(), "trial")) {
            return;
        }
        int spawnAttempts = 100;
        int cavelingsToSpawn = 7;
        GameRandom random = new GameRandom(level.getSeed() + 323L);
        block0: for (int i = 0; i < cavelingsToSpawn; ++i) {
            Mob incursionCaveling = MobRegistry.getMob("incursioncaveling", (Level)level);
            incursionCaveling.canDespawn = false;
            incursionCaveling.shouldSave = true;
            int levelTileWidth = level.tileWidth;
            int levelTileHeight = level.tileHeight;
            for (int j = 1; j <= spawnAttempts; ++j) {
                int rndY;
                int rndX = random.getIntBetween(0, levelTileWidth) * 32 + 16;
                if (!level.isSolidTile(rndX, rndY = random.getIntBetween(0, levelTileHeight) * 32 + 16) && !incursionCaveling.collidesWith(level, rndX, rndY)) {
                    incursionCaveling.onSpawned(rndX, rndY);
                    level.entityManager.addMob(incursionCaveling, rndX, rndY);
                    continue block0;
                }
                if (j != 50) continue;
                GameLog.warn.println("No spawn position found for: " + incursionCaveling.getDisplayName());
            }
        }
    }
}

