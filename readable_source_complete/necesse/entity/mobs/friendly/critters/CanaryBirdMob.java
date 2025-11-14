/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;

public class CanaryBirdMob
extends BirdMob {
    @Override
    protected GameTexture getTexture() {
        return MobRegistry.Textures.canaryBird;
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }
}

