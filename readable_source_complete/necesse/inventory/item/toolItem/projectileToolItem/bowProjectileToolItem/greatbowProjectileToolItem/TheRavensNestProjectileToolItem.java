/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.TheRavensNestProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.IncursionGreatswordWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class TheRavensNestProjectileToolItem
extends GreatbowProjectileToolItem {
    public TheRavensNestProjectileToolItem() {
        super(1900, IncursionGreatswordWeaponsLootTable.incursionGreatswordWeapons);
        this.attackAnimTime.setBaseValue(600);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(55.0f).setUpgradedValue(1.0f, 64.16669f);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(1400);
        this.attackXOffset = 12;
        this.attackYOffset = 38;
        this.particleColor = new Color(169, 150, 236);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.2f;
        this.useForRaidsOnlyIfObtained = true;
        this.defaultLootTier = 1.0f;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraBowTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "theravensnesttip"), 400);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new TheRavensNestProjectile(level, owner, owner.x, owner.y, x, y, velocity, range, damage, knockback);
    }

    @Override
    protected void fireProjectiles(Level level, final int x, final int y, final ItemAttackerMob attackerMob, final InventoryItem item, final int seed, final ArrowItem arrow, final boolean dropItem, final GNDItemMap mapContent) {
        final GameRandom random = new GameRandom(seed);
        for (int i = 0; i < 3; ++i) {
            level.entityManager.events.addHidden(new WaitForSecondsEvent((float)i * 0.1f){

                @Override
                public void onWaitOver() {
                    int rndY = random.getIntBetween(-45, 45);
                    Projectile projectile = TheRavensNestProjectileToolItem.this.getProjectile(this.level, x, y + rndY, attackerMob, item, seed, arrow, dropItem, mapContent);
                    projectile.setModifier(new ResilienceOnHitProjectileModifier(TheRavensNestProjectileToolItem.this.getResilienceGain(item)));
                    projectile.dropItem = dropItem;
                    projectile.getUniqueID(random);
                    attackerMob.addAndSendAttackerProjectile(projectile);
                }
            });
        }
    }
}

