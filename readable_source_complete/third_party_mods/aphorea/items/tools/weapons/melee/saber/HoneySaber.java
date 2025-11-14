/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.projectiles.toolitem.BlueBerryProjectile;
import aphorea.projectiles.toolitem.HoneyProjectile;
import aphorea.registry.AphBuffs;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class HoneySaber
extends AphSaberToolItem {
    static float angleStep = 0.19635f;

    public HoneySaber() {
        super(850);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(34.0f).setUpgradedValue(1.0f, 70.0f);
        this.knockback.setBaseValue(75);
        this.attackRange.setBaseValue(60);
    }

    @Override
    public Projectile[] getProjectiles(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        float precision = powerPercent / 1.6f + 0.375f;
        int extraProjectiles = Math.round((precision - 0.5f) * 6.0f);
        ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
        float baseAngle = (float)Math.atan2(targetY - y, targetX - x);
        float centralAngle = baseAngle + (float)Math.toRadians(GameRandom.globalRandom.getFloatOffset(0.0f, 3.0f));
        int centralX = (int)(Math.cos(centralAngle) * 100.0) + x;
        int centralY = (int)(Math.sin(centralAngle) * 100.0) + y;
        projectiles.add(this.getProjectile(level, x, y, centralX, centralY, attackerMob, item, powerPercent, true));
        for (int i = 0; i < extraProjectiles; ++i) {
            boolean isHoney = i % 2 == 1;
            float angleLeft = baseAngle - (float)(i + 1) * angleStep + (float)Math.toRadians(GameRandom.globalRandom.getFloatOffset(0.0f, 3.0f));
            float angleRight = baseAngle + (float)(i + 1) * angleStep + (float)Math.toRadians(GameRandom.globalRandom.getFloatOffset(0.0f, 3.0f));
            int leftX = (int)(Math.cos(angleLeft) * 100.0) + x;
            int leftY = (int)(Math.sin(angleLeft) * 100.0) + y;
            projectiles.add(this.getProjectile(level, x, y, leftX, leftY, attackerMob, item, powerPercent, isHoney));
            int rightX = (int)(Math.cos(angleRight) * 100.0) + x;
            int rightY = (int)(Math.sin(angleRight) * 100.0) + y;
            projectiles.add(this.getProjectile(level, x, y, rightX, rightY, attackerMob, item, powerPercent, isHoney));
        }
        for (Projectile projectile : projectiles) {
            projectile.moveDist((double)GameRandom.globalRandom.getFloatBetween(0.0f, 10.0f));
        }
        return projectiles.toArray(new Projectile[0]);
    }

    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, boolean isHoney) {
        if (isHoney) {
            return new HoneyProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 150.0f * powerPercent, (int)(300.0f * powerPercent), this.getAttackDamage(item).modDamage(powerPercent * 0.25f), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent * 0.25f));
        }
        return new BlueBerryProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 150.0f * powerPercent, (int)(300.0f * powerPercent), this.getAttackDamage(item).modDamage(powerPercent * 0.3f), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent * 0.3f));
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        return this.getProjectile(level, x, y, targetX, targetY, attackerMob, item, powerPercent, true);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.removeLast();
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"honeysaber"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"saberdash"));
        return tooltips;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.HONEYED, target, 2000, (Attacker)attacker), true);
    }
}

