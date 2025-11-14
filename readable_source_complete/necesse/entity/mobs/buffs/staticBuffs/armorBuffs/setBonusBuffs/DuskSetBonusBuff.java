/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DuskMoonDiscFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.DuskVolleyProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class DuskSetBonusBuff
extends SetBonusBuff {
    public FloatUpgradeValue moonDiscDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(60.0f).setUpgradedValue(1.0f, 60.0f);
    public FloatUpgradeValue duskVolleyDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(25.0f).setUpgradedValue(1.0f, 25.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, 400);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        Mob buffOwner = buff.owner;
        Level level = buffOwner.getLevel();
        if (level != null && buffOwner.isVisible() && level.getWorldEntity().isNight()) {
            level.entityManager.addParticle(buffOwner.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), buffOwner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle(GameRandom.globalRandom.getIntBetween(0, 360), 3.0f).givesLight().height(16.0f);
        }
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        super.onItemAttacked(buff, targetX, targetY, attackerMob, attackHeight, item, slot, animAttack, attackMap);
        Level level = buff.owner.getLevel();
        if (level.getWorldEntity().isNight() && level.isServer() && item.item instanceof ToolItem) {
            ToolItem toolItem = (ToolItem)item.item;
            if (toolItem.getDamageType(item) == DamageTypeRegistry.MAGIC) {
                this.fireDuskVolley(buff, attackerMob, level, targetX, targetY);
            } else if (toolItem.getDamageType(item) == DamageTypeRegistry.SUMMON) {
                this.summonCrescentMoon(buff, attackerMob, level, targetX, targetY);
            }
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "duskset"), 400);
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    private void fireDuskVolley(ActiveBuff buff, ItemAttackerMob attackerMob, Level level, int targetX, int targetY) {
        String shotTimeKey = "duskvolley";
        long shotTime = buff.getGndData().getLong(shotTimeKey);
        float totalModifier = attackerMob.buffManager.getModifier(BuffModifiers.ATTACK_SPEED).floatValue();
        totalModifier += attackerMob.buffManager.getModifier(BuffModifiers.MAGIC_ATTACK_SPEED).floatValue();
        int cooldown = Math.round(500.0f * (1.0f / (totalModifier = BuffModifiers.ATTACK_SPEED.finalLimit(Float.valueOf(totalModifier)).floatValue())));
        if (shotTime + (long)cooldown < level.getWorldEntity().getTime()) {
            buff.getGndData().setLong(shotTimeKey, level.getWorldEntity().getTime());
            GameRandom random = GameRandom.globalRandom;
            float velocity = 250.0f * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue();
            GameDamage gameDamage = new GameDamage(this.duskVolleyDamage.getValue(buff.getUpgradeTier()).floatValue() * GameDamage.getDamageModifier(attackerMob, DamageTypeRegistry.MAGIC));
            for (int i = 0; i < 5; ++i) {
                DuskVolleyProjectile projectile = new DuskVolleyProjectile(level, attackerMob, attackerMob.x, attackerMob.y, random.getIntBetween(0, 360), velocity, 1500, gameDamage, 0);
                level.entityManager.projectiles.add(projectile);
            }
        }
    }

    private void summonCrescentMoon(ActiveBuff buff, ItemAttackerMob attackerMob, Level level, int targetX, int targetY) {
        String shotTimeKey = "summonmoon";
        long shotTime = buff.getGndData().getLong(shotTimeKey);
        float totalModifier = attackerMob.buffManager.getModifier(BuffModifiers.ATTACK_SPEED).floatValue();
        totalModifier += attackerMob.buffManager.getModifier(BuffModifiers.SUMMON_ATTACK_SPEED).floatValue();
        int cooldown = Math.round(500.0f * (1.0f / (totalModifier = BuffModifiers.ATTACK_SPEED.finalLimit(Float.valueOf(totalModifier)).floatValue())));
        if (shotTime + (long)cooldown < level.getWorldEntity().getTime()) {
            buff.getGndData().setLong(shotTimeKey, level.getWorldEntity().getTime());
            GameDamage damage = new GameDamage(this.moonDiscDamage.getValue(buff.getUpgradeTier()).floatValue() * GameDamage.getDamageModifier(attackerMob, DamageTypeRegistry.SUMMON));
            DuskMoonDiscFollowingMob mob = new DuskMoonDiscFollowingMob();
            attackerMob.serverFollowersManager.addFollower("duskmoondisc", (Mob)mob, FollowPosition.FLYING_CIRCLE, "summonedmob", 1.0f, 10, null, false);
            Point2D.Float spawnPoint = DuskSetBonusBuff.findSpawnLocation(mob, attackerMob.getLevel(), attackerMob.x, attackerMob.y);
            mob.updateDamage(damage);
            mob.setRemoveWhenNotInInventory(ItemRegistry.getItem("duskhelmet"), CheckSlotType.HELMET);
            attackerMob.getLevel().entityManager.addMob(mob, spawnPoint.x, spawnPoint.y);
        }
    }

    public static Point2D.Float findSpawnLocation(Mob mob, Level level, float centerX, float centerY) {
        ArrayList<Point2D.Float> possibleSpawns = new ArrayList<Point2D.Float>();
        for (int cX = -1; cX <= 1; ++cX) {
            for (int cY = -1; cY <= 1; ++cY) {
                float posY;
                float posX;
                if (cX == 0 && cY == 0 || mob.collidesWith(level, (int)(posX = centerX + (float)(cX * 32)), (int)(posY = centerY + (float)(cY * 32)))) continue;
                possibleSpawns.add(new Point2D.Float(posX, posY));
            }
        }
        if (possibleSpawns.size() > 0) {
            return (Point2D.Float)possibleSpawns.get(GameRandom.globalRandom.nextInt(possibleSpawns.size()));
        }
        return new Point2D.Float(centerX, centerY);
    }
}

