/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DawnSwirlEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.DawnFireballProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class DawnSetBonusBuff
extends SetBonusBuff {
    public FloatUpgradeValue damage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(40.0f).setUpgradedValue(1.0f, 40.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        Mob buffOwner = buff.owner;
        Level level = buffOwner.getLevel();
        if (level != null && buffOwner.isVisible() && !level.getWorldEntity().isNight()) {
            level.entityManager.addParticle(buffOwner.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), buffOwner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle(GameRandom.globalRandom.getIntBetween(0, 360), 3.0f).flameColor(45.0f).givesLight(50.0f, 1.0f).height(16.0f);
        }
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        super.onItemAttacked(buff, targetX, targetY, attackerMob, attackHeight, item, slot, animAttack, attackMap);
        Level level = buff.owner.getLevel();
        if (!level.getWorldEntity().isNight() && level.isServer() && item.item instanceof ToolItem) {
            ToolItem toolItem = (ToolItem)item.item;
            if (toolItem.getDamageType(item) == DamageTypeRegistry.MELEE) {
                this.addDawnSwirl(buff, attackerMob, level, targetX, targetY);
            } else if (toolItem.getDamageType(item) == DamageTypeRegistry.RANGED) {
                this.shootDawnFireball(buff, attackerMob, level, targetX, targetY);
            }
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dawnset"), 400);
        return tooltips;
    }

    private void addDawnSwirl(ActiveBuff buff, ItemAttackerMob attackerMob, Level level, int targetX, int targetY) {
        String shotTimeKey = "dawnswirl";
        long shotTime = buff.getGndData().getLong(shotTimeKey);
        if (shotTime + 500L < level.getWorldEntity().getTime()) {
            buff.getGndData().setLong(shotTimeKey, level.getWorldEntity().getTime());
            GameRandom random = GameRandom.globalRandom;
            float damageModifier = attackerMob.buffManager.getModifier(BuffModifiers.ALL_DAMAGE).floatValue();
            damageModifier += attackerMob.buffManager.getModifier(BuffModifiers.MELEE_DAMAGE).floatValue();
            damageModifier = BuffModifiers.ALL_DAMAGE.finalLimit(Float.valueOf(damageModifier)).floatValue();
            float damage = this.damage.getValue(buff.getUpgradeTier()).floatValue() * damageModifier;
            int armorPen = attackerMob.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT);
            GameDamage gameDamage = new GameDamage(damage, (float)armorPen);
            DawnSwirlEvent event = new DawnSwirlEvent(attackerMob, attackerMob.getX(), attackerMob.getY(), random, gameDamage);
            level.entityManager.events.add(event);
        }
    }

    private void shootDawnFireball(ActiveBuff buff, ItemAttackerMob attackerMob, Level level, int targetX, int targetY) {
        String shotTimeKey = "dawnfireball";
        long shotTime = buff.getGndData().getLong(shotTimeKey);
        float totalModifier = attackerMob.buffManager.getModifier(BuffModifiers.ATTACK_SPEED).floatValue();
        totalModifier += attackerMob.buffManager.getModifier(BuffModifiers.RANGED_ATTACK_SPEED).floatValue();
        int cooldown = Math.round(500.0f * (1.0f / (totalModifier = BuffModifiers.ATTACK_SPEED.finalLimit(Float.valueOf(totalModifier)).floatValue())));
        if (shotTime + (long)cooldown < level.getWorldEntity().getTime()) {
            buff.getGndData().setLong(shotTimeKey, level.getWorldEntity().getTime());
            Point2D.Float dir = GameMath.normalize(attackerMob.x - (float)targetX, attackerMob.y - (float)targetY);
            float angle = GameMath.getAngle(dir);
            float velocity = 100.0f * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue();
            float damageModifier = attackerMob.buffManager.getModifier(BuffModifiers.ALL_DAMAGE).floatValue();
            damageModifier += attackerMob.buffManager.getModifier(BuffModifiers.MELEE_DAMAGE).floatValue();
            damageModifier = BuffModifiers.ALL_DAMAGE.finalLimit(Float.valueOf(damageModifier)).floatValue();
            float damage = this.damage.getValue(buff.getUpgradeTier()).floatValue() * damageModifier;
            int armorPen = attackerMob.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT);
            GameDamage gameDamage = new GameDamage(damage, (float)armorPen);
            DawnFireballProjectile projectile1 = new DawnFireballProjectile(level, attackerMob, attackerMob.x, attackerMob.y, angle - 100.0f, velocity, 750, gameDamage, 0, false);
            DawnFireballProjectile projectile2 = new DawnFireballProjectile(level, attackerMob, attackerMob.x, attackerMob.y, angle - 80.0f, velocity, 750, gameDamage, 0, false);
            level.entityManager.projectiles.add(projectile1);
            level.entityManager.projectiles.add(projectile2);
        }
    }
}

