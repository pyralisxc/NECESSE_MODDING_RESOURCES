/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;

public class BluebirdMob
extends BirdMob {
    @Override
    protected GameTexture getTexture() {
        return MobRegistry.Textures.bluebird;
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }
}

