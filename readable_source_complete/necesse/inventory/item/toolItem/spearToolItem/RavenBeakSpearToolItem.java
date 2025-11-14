/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RavenBeakSpearProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.IncursionSpearWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class RavenBeakSpearToolItem
extends SpearToolItem
implements ItemInteractAction {
    public int defaultFeatherStrikeCount = 3;
    public int rightClickFeatherCount = 30;

    public RavenBeakSpearToolItem() {
        super(1900, IncursionSpearWeaponsLootTable.incursionSpearWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(45.0f).setUpgradedValue(1.0f, 52.500015f);
        this.attackRange.setBaseValue(120);
        this.knockback.setBaseValue(50);
        this.resilienceGain.setBaseValue(2.5f);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
        this.defaultLootTier = 1.0f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ravenbeakspeartip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "ravenbeakspearsecondarytip"), 400);
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, final ItemAttackerMob attackerMob, int attackHeight, final InventoryItem item, ItemAttackSlot slot, int animAttack, final int seed, GNDItemMap mapContent) {
        InventoryItem out = super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        if (animAttack == 0) {
            final GameRandom random = new GameRandom(seed);
            final Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y + (float)attackHeight);
            Mob mount = attackerMob.getMount();
            if (mount != null) {
                attackHeight -= mount.getRiderDrawYOffset();
            }
            final int finalAttackHeight = attackHeight;
            final float playerStartX = attackerMob.x;
            final float playerStartY = attackerMob.y;
            for (int i = 0; i < this.defaultFeatherStrikeCount; ++i) {
                level.entityManager.events.addHidden(new WaitForSecondsEvent(0.1f * (float)i){

                    @Override
                    public void onWaitOver() {
                        int rndX = random.getOneOf(random.getIntBetween(-10, -5), random.getIntBetween(5, 10));
                        int rndY = random.getOneOf(random.getIntBetween(-10, -5), random.getIntBetween(5, 10));
                        GameDamage spectralStrikeAttackDamage = RavenBeakSpearToolItem.this.getAttackDamage(item).modFinalMultiplier(0.5f);
                        RavenBeakSpearProjectile projectile = new RavenBeakSpearProjectile(this.level, playerStartX + (float)rndX - dir.x * 25.0f, playerStartY + (float)rndY - dir.y * 25.0f, playerStartX + (float)rndX + dir.x * 90.0f, playerStartY + (float)rndY + dir.y * 90.0f, 40.0f, 80, spectralStrikeAttackDamage, attackerMob, finalAttackHeight);
                        projectile.setLevel(this.level);
                        projectile.resetUniqueID(new GameRandom(seed));
                        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, () -> {
                            projectile.moveDist(RavenBeakSpearToolItem.this.getAttackRange(item) - 35);
                            projectile.traveledDistance = 0.0f;
                            projectile.setAngle(projectile.getAngle() + RavenBeakSpearToolItem.this.getRandomAngleOffsetBasedOnDirection(dir2.x, dir2.y, rndX, rndY));
                        });
                    }
                });
            }
        }
        return out;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.ravenBeakSpear);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.buffManager.hasBuff(BuffRegistry.Debuffs.RAVENBEAK_SPEAR_COOLDOWN);
    }

    @Override
    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds(BuffRegistry.Debuffs.RAVENBEAK_SPEAR_COOLDOWN) / 12.0f;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, final ItemAttackerMob attackerMob, int attackHeight, final InventoryItem item, ItemAttackSlot slot, final int seed, GNDItemMap mapContent) {
        InventoryItem out = super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, 0, seed, mapContent);
        final GameRandom random = new GameRandom(seed);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.RAVENBEAK_SPEAR_COOLDOWN, (Mob)attackerMob, 12.0f, null), false);
        final Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y + (float)attackHeight);
        Mob mount = attackerMob.getMount();
        if (mount != null) {
            attackHeight -= mount.getRiderDrawYOffset();
        }
        final int finalAttackHeight = attackHeight;
        final float playerStartX = attackerMob.x;
        final float playerStartY = attackerMob.y;
        for (int i = 0; i < this.rightClickFeatherCount; ++i) {
            level.entityManager.events.addHidden(new WaitForSecondsEvent(0.05f * (float)i){

                @Override
                public void onWaitOver() {
                    int rndX = random.getOneOf(random.getIntBetween(-10, 10));
                    int rndY = random.getOneOf(random.getIntBetween(-10, 10));
                    GameDamage spectralStrikeAttackDamage = RavenBeakSpearToolItem.this.getAttackDamage(item).modFinalMultiplier(0.25f);
                    RavenBeakSpearProjectile projectile = new RavenBeakSpearProjectile(this.level, playerStartX + (float)rndX - dir.x * 25.0f, playerStartY + (float)rndY - dir.y * 25.0f, playerStartX + (float)rndX + dir.x * 90.0f, playerStartY + (float)rndY + dir.y * 90.0f, 40.0f, 80, spectralStrikeAttackDamage, attackerMob, finalAttackHeight);
                    projectile.setLevel(this.level);
                    projectile.resetUniqueID(new GameRandom(seed));
                    attackerMob.addAndSendAttackerProjectile((Projectile)projectile, () -> {
                        projectile.moveDist(RavenBeakSpearToolItem.this.getAttackRange(item) - 35);
                        projectile.traveledDistance = 0.0f;
                        projectile.setAngle(projectile.getAngle() + RavenBeakSpearToolItem.this.getRandomAngleOffsetBasedOnDirection(dir2.x, dir2.y, rndX, rndY));
                    });
                }
            });
        }
        return out;
    }

    public float getRandomAngleOffsetBasedOnDirection(float dirX, float dirY, float randomXAngle, float randomYAngle) {
        if (dirX < 0.5f && dirX > -0.5f && dirY > 0.0f) {
            return randomXAngle;
        }
        if (dirX < 0.5f && dirX > -0.5f && dirY < 0.0f) {
            return randomXAngle;
        }
        if (dirX > 0.0f && dirY < 0.5f && dirY > -0.5f) {
            return randomYAngle;
        }
        if (dirX < 0.0f && dirY < 0.5f && dirY > -0.5f) {
            return randomYAngle;
        }
        return 0.0f;
    }
}

