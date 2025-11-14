/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class SiphonShieldTrinketItem
extends ShieldTrinketItem {
    public SiphonShieldTrinketItem(Item.Rarity rarity, int enchantCost) {
        super(rarity, 5, 0.5f, 3000, 0.1f, 50, 360.0f, enchantCost, TrinketsLootTable.trinkets);
    }

    @Override
    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "siphonshieldtip"));
        return tooltips;
    }

    @Override
    public void onShieldHit(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        if (mob.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
            mob.isManaExhausted = false;
            mob.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
        }
        if (mob.isServer()) {
            float restoreAmount = (float)hitEvent.damage / (float)mob.getMaxHealth();
            mob.setMana(mob.getMana() + restoreAmount * (float)mob.getMaxMana());
        } else {
            this.drawBlockParticles(mob);
        }
        super.onShieldHit(item, mob, hitEvent);
    }

    private void drawBlockParticles(Mob mob) {
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        int particleCount = 10;
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(25, 50);
            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(25, 50);
            mob.getLevel().entityManager.addParticle(mob, typeSwitcher.next()).color(new Color(131, 198, 247)).movesFriction(dx, dy, 0.8f).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(30, 40).givesLight(180.0f, 200.0f).lifeTime(500);
        }
    }
}

