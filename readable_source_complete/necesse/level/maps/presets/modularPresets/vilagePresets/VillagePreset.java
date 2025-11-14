/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class VillagePreset
extends ModularPreset {
    public int shoreTileID = TileRegistry.sandID;
    public GameRandom random;
    public final int path;
    public final int grass;
    private final boolean isPath;

    private VillagePreset(int sectionWidth, int sectionHeight, boolean isPath, int path, int grass, GameRandom random, boolean setup) {
        super(sectionWidth, sectionHeight, 3, 3, 1);
        this.isPath = isPath;
        this.path = path;
        this.grass = grass;
        if (isPath) {
            this.openObject = -1;
            this.openTile = -1;
            this.closeObject = -1;
            this.closeTile = -1;
        } else {
            this.openObject = -1;
            this.openTile = -1;
            this.closeObject = -1;
            this.closeTile = -1;
        }
        if (setup) {
            if (random != null) {
                this.random = new GameRandom(random.nextLong());
            }
            this.addCustomPreApplyRectEach(0, 0, this.width, this.height, 0, (level, levelX, levelY, dir, blackboard) -> {
                if (this.shoreTileID > 0 && level.getTile((int)levelX, (int)levelY).isLiquid) {
                    level.setTile(levelX, levelY, this.shoreTileID);
                }
                return null;
            });
        } else {
            this.random = random;
        }
    }

    public VillagePreset(int sectionWidth, int sectionHeight, boolean isPath, int path, int grass, GameRandom random) {
        this(sectionWidth, sectionHeight, isPath, path, grass, random, true);
    }

    public VillagePreset(int sectionWidth, int sectionHeight, boolean isPath, GameRandom random) {
        this(sectionWidth, sectionHeight, isPath, TileRegistry.getTileID("stonepathtile"), TileRegistry.grassID, random);
    }

    public VillagePreset(int sectionWidth, int sectionHeight, boolean isPath) {
        this(sectionWidth, sectionHeight, isPath, null);
    }

    @Override
    protected VillagePreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new VillagePreset(sectionWidth, sectionHeight, this.isPath, this.path, this.grass, this.random, false);
    }

    public boolean isPath() {
        return this.isPath;
    }

    public void addHumanMob(int tileX, int tileY, float chance, String ... mobStringIDs) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (!level.isClient() && this.random.getChance(chance) && mobStringIDs.length > 0) {
                HumanMob mob = (HumanMob)MobRegistry.getMob(this.random.getOneOf(mobStringIDs), level);
                mob.setSettlerSeed(this.random.nextInt(), true);
                mob.setHome(levelX, levelY);
                mob.villagerData = new OneWorldNPCVillageData.NPCVillagerData(levelX, levelY, new ArrayList<String>(Arrays.asList(mobStringIDs)));
                level.entityManager.addMob(mob, levelX * 32 + 16, levelY * 32 + 16);
                return (level1, presetX, presetY) -> {
                    mob.villagerData = null;
                    mob.remove();
                };
            }
            return null;
        }, false);
    }

    public void addHumanMob(int tileX, int tileY, String ... mobStringIDs) {
        this.addHumanMob(tileX, tileY, 1.0f, mobStringIDs);
    }
}

