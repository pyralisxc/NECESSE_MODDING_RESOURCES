/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class SwampCaveSpiderMob
extends GiantCaveSpiderMob {
    public static LootTable lootTable = new LootTable(LootItem.between("cavespidergland", 1, 2, MultiTextureMatItem.getGNDData(2)));

    public SwampCaveSpiderMob() {
        super(GiantCaveSpiderMob.Variant.SWAMP, 350, new GameDamage(38.0f), new GameDamage(30.0f));
        this.setSpeed(35.0f);
        this.setArmor(10);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }
}

