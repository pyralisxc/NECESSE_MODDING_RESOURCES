/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.witches;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.witches.EvilWitchMob;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.Waystone;

public class EvilWitchStartMob
extends Mob {
    public EvilWitchStartMob() {
        super(100000);
        this.isHostile = true;
    }

    @Override
    public void init() {
        super.init();
        Level level = this.getLevel();
        if (this.isServer()) {
            Mob evilWitch = MobRegistry.getMob("evilwitchflask", level);
            Point spawnPos = Waystone.findTeleportLocation(level, this.getTileX(), this.getTileY(), evilWitch);
            this.getLevel().entityManager.addMob(evilWitch, spawnPos.x, spawnPos.y);
            evilWitch = MobRegistry.getMob("evilwitchbow", level);
            spawnPos = Waystone.findTeleportLocation(level, this.getTileX(), this.getTileY(), evilWitch);
            this.getLevel().entityManager.addMob(evilWitch, spawnPos.x, spawnPos.y);
            evilWitch = MobRegistry.getMob("evilwitchgreatsword", level);
            spawnPos = Waystone.findTeleportLocation(level, this.getTileX(), this.getTileY(), evilWitch);
            this.getLevel().entityManager.addMob(evilWitch, spawnPos.x, spawnPos.y);
        }
        this.remove();
    }

    @Override
    public LootTable getLootTable() {
        return EvilWitchMob.lootTable;
    }
}

