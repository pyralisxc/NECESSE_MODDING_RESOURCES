/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TopazGlyphEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.LifeEssenceStacksBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class TopazStaffProjectileToolItem
extends MagicProjectileToolItem
implements ItemInteractAction {
    protected int rightClickLifeEssenceCost = LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE;
    protected IntUpgradeValue ricochets = new IntUpgradeValue(0, 0.0f);
    protected int maxActiveBoomerangs = 12;

    public TopazStaffProjectileToolItem() {
        super(1550, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(350);
        this.attackDamage.setBaseValue(75.0f).setUpgradedValue(1.0f, 128.80003f);
        this.velocity.setBaseValue(75);
        this.attackXOffset = 14;
        this.attackYOffset = 4;
        this.attackRange.setBaseValue(1100);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(1.25f).setUpgradedValue(1.0f, 1.25f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.ricochets.setBaseValue(1).setUpgradedValue(1.0f, 3);
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        if (this.attackTexture != null) {
            return new GameSprite(this.attackTexture);
        }
        return new GameSprite(this.getItemSprite(item, player), 24);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.getBoomerangsUsage() < this.maxActiveBoomerangs ? null : "";
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        int maxWidth = 400;
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "topazstafftip1"), maxWidth);
        tooltips.add(Localization.translate("itemtooltip", "topazstafftip2", "value", (Object)(this.rightClickLifeEssenceCost / LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE)), maxWidth);
        tooltips.add(Localization.translate("itemtooltip", "topazstafftip3"), maxWidth);
        return tooltips;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient() && !mapContent.getBoolean("glyphAttack")) {
            SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.6f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.0f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        if (!attackerMob.isPlayer && this.canCastGlyph(attackerMob) && attackerMob instanceof HumanMob) {
            int checkForMobsRange = 384;
            Mob targetChosen = Stream.concat(level.entityManager.mobs.streamInRegionsInRange(attackerMob.x, attackerMob.y, checkForMobsRange).filter(m -> m.isHuman).map(m -> (HumanMob)m).filter(hm -> hm.isFriendlyHuman((HumanMob)attackerMob)), level.entityManager.players.streamInRegionsInRange(attackerMob.x, attackerMob.y, checkForMobsRange).filter(p -> !p.canBeTargeted(attackerMob, attackerMob.getPvPOwner()))).filter(m -> m.getDistance(attackerMob) <= (float)checkForMobsRange).min(Comparator.comparingInt(rec$ -> ((Mob)rec$).getHealth())).orElse(null);
            Point2D.Float targetPos = targetChosen != null ? Projectile.getPredictedTargetPos(targetChosen, attackerMob.x, attackerMob.y, 1000.0f, -10.0f) : new Point2D.Float(attackerMob.x, attackerMob.y);
            TopazGlyphEvent topazGlyphEvent = this.getTopazGlyphEvent(level, (int)targetPos.x, (int)targetPos.y, attackerMob, item, random);
            attackerMob.addAndSendAttackerLevelEvent(topazGlyphEvent);
            mapContent.setBoolean("glyphAttack", true);
        } else {
            Projectile projectile = ProjectileRegistry.getProjectile("topazstaff", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            attackerMob.boomerangs.add(projectile);
            projectile.resetUniqueID(new GameRandom(seed));
            attackerMob.addAndSendAttackerProjectile(projectile);
            this.consumeMana(attackerMob, item);
        }
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canCastGlyph(attackerMob);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        TopazGlyphEvent event = this.getTopazGlyphEvent(level, x, y, attackerMob, item, random);
        attackerMob.addAndSendAttackerLevelEvent(event);
        return item;
    }

    public boolean canCastGlyph(Mob mob) {
        return mob.buffManager.getStacks(BuffRegistry.LIFE_ESSENCE) >= this.rightClickLifeEssenceCost;
    }

    public TopazGlyphEvent getTopazGlyphEvent(Level level, int x, int y, Mob player, InventoryItem item, GameRandom random) {
        if (level.isServer()) {
            for (int i = 0; i < this.rightClickLifeEssenceCost; ++i) {
                player.buffManager.removeStack(BuffRegistry.LIFE_ESSENCE, true, true);
            }
        }
        Point2D.Float targetPoints = new Point2D.Float(x, y);
        Point2D.Float normalizedVector = GameMath.normalize(targetPoints.x - player.x, targetPoints.y - player.y);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)player.x, (double)player.y, (double)normalizedVector.x, (double)normalizedVector.y, targetPoints.distance(player.x, player.y), 0, new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock));
        if (!hits.isEmpty()) {
            Ray first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            targetPoints.y = (float)first.y2;
        }
        return new TopazGlyphEvent(player, (int)targetPoints.x, (int)targetPoints.y, random, this.getUpgradeLevel(item));
    }
}

