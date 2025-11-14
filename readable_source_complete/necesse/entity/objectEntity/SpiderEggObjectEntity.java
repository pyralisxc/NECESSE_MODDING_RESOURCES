/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class SpiderEggObjectEntity
extends ObjectEntity {
    private final String[] mobList = new String[]{"webspinner", "bloatedspider", "spiderkin"};
    public boolean isBroken = false;

    public SpiderEggObjectEntity(Level level, int x, int y) {
        super(level, "spideregg", x, y);
    }

    public void breakEgg() {
        if (!this.isBroken) {
            if (!this.getLevel().objectLayer.isPlayerPlaced(this.tileX, this.tileY)) {
                this.spawnContainedMob(this.getLevel());
            }
            this.isBroken = true;
        }
    }

    private void spawnContainedMob(Level level) {
        for (int i = 0; i < GameRandom.globalRandom.getIntBetween(3, 5); ++i) {
            String containedMob = GameRandom.globalRandom.getOneOf(this.mobList);
            level.entityManager.addMob(MobRegistry.getMob(containedMob, level), this.tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), this.tileY * 32 + GameRandom.globalRandom.getIntBetween(8, 24));
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("isBroken", this.isBroken);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.isBroken = save.getBoolean("isBroken", false);
    }
}

