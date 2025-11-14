/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.levelEvent.mobAbilityLevelEvent.EmeraldGlyphEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.LifeEssenceStacksBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.EmeraldStaffProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class EmeraldStaffProjectileToolItem
extends MagicProjectileToolItem
implements ItemInteractAction {
    protected int rightClickLifeEssenceCost = LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE;

    public EmeraldStaffProjectileToolItem() {
        super(1600, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(550);
        this.attackDamage.setBaseValue(25.0f).setUpgradedValue(1.0f, 46.200016f);
        this.velocity.setBaseValue(75);
        this.attackXOffset = 14;
        this.attackYOffset = 4;
        this.attackRange.setBaseValue(600);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(5.0f).setUpgradedValue(1.0f, 5.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "emeraldstafftip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "emeraldstafftip2", "value", (Object)(this.rightClickLifeEssenceCost / LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE)), 400);
        tooltips.add(Localization.translate("itemtooltip", "emeraldstafftip3"), 400);
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        if (!attackerMob.isPlayer && this.canCastGlyph(attackerMob)) {
            int checkForMobsRange = 384;
            Mob targetChosen = level.entityManager.players.streamInRegionsInRange(attackerMob.x, attackerMob.y, checkForMobsRange).filter(p -> !p.canBeTargeted(attackerMob, attackerMob.getPvPOwner())).filter(m -> m.getDistance(attackerMob) <= (float)checkForMobsRange).max(Comparator.comparing(Mob::getCritDamageModifier)).orElse(null);
            Point2D.Float targetPos = targetChosen != null ? Projectile.getPredictedTargetPos(targetChosen, attackerMob.x, attackerMob.y, 1000.0f, -10.0f) : new Point2D.Float(attackerMob.x, attackerMob.y);
            EmeraldGlyphEvent glyphEvent = this.getGlyphEvent(level, (int)targetPos.x, (int)targetPos.y, attackerMob, random);
            attackerMob.addAndSendAttackerLevelEvent(glyphEvent);
            mapContent.setBoolean("glyphAttack", true);
        } else {
            AtomicInteger givesLifeEssence = new AtomicInteger(3);
            for (int i = -1; i <= 1; ++i) {
                EmeraldStaffProjectile projectile = new EmeraldStaffProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), givesLifeEssence);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.resetUniqueID(random);
                attackerMob.addAndSendAttackerProjectile(projectile, 70, 10 * i);
            }
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
        EmeraldGlyphEvent event = this.getGlyphEvent(level, x, y, attackerMob, random);
        attackerMob.addAndSendAttackerLevelEvent(event);
        return item;
    }

    public boolean canCastGlyph(Mob mob) {
        return mob.buffManager.getStacks(BuffRegistry.LIFE_ESSENCE) >= this.rightClickLifeEssenceCost;
    }

    public EmeraldGlyphEvent getGlyphEvent(Level level, int x, int y, Mob player, GameRandom random) {
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
        return new EmeraldGlyphEvent(player, (int)targetPoints.x, (int)targetPoints.y, random);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.emeraldStaff).volume(0.35f).basePitch(1.4f).pitchVariance(0.01f);
    }
}

