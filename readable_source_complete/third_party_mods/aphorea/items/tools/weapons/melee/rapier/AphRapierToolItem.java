/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LineHitbox
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.AttackAnimMob
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.upgradeUtils.IntUpgradeValue
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.rapier;

import aphorea.items.tools.weapons.melee.rapier.logic.RapierDashAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphSpearToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class AphRapierToolItem
extends AphSpearToolItem {
    public IntUpgradeValue dashRange;
    public IntUpgradeValue dashAnimTime;
    private static final int MAX_COMBO = 20;
    private static final long COMBO_TIMEOUT = 4L;

    public AphRapierToolItem(int enchantCost) {
        super(enchantCost);
        this.keyWords.add("rapier");
        this.attackAnimTime.setBaseValue(100);
        this.knockback.setBaseValue(20);
        this.dashRange = new IntUpgradeValue(200, 0.0f);
        this.dashRange.setBaseValue(200);
        this.dashAnimTime = new IntUpgradeValue(1000, 0.0f);
        this.dashAnimTime.setBaseValue(600);
    }

    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        GameSprite attackSprite = this.getAttackSprite(item, perspective);
        return attackSprite != null && Math.max(attackSprite.width, attackSprite.height) >= 32 ? attackSprite : this.getItemSprite(item, perspective);
    }

    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<Shape>();
        float attackRange = this.getAttackRange(item);
        Point2D.Float dir = GameMath.normalize((float)aimX, (float)aimY);
        float yOffset = Math.min(mob.getCurrentAttackDrawYOffset() + mob.getStartAttackHeight(), 0);
        Line2D.Float attackLine = new Line2D.Float(mob.x, mob.y, mob.x + dir.x * attackRange, mob.y + dir.y * attackRange + yOffset);
        if (this.width > 0.0f) {
            out.add((Shape)new LineHitbox((Line2D)attackLine, this.width));
        } else {
            out.add(attackLine);
        }
        return out;
    }

    public int getDashAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("chargeAnimTime") ? gndData.getInt("chargeAnimTime") : this.dashAnimTime.getValue(this.getUpgradeTier(item)).intValue();
    }

    public float getDashDamageMultiplier(InventoryItem item) {
        return 5.0f;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int combo = this.getComboAndCalc(item, attackerMob);
        if (combo == 19) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();
            if (attackerMob.isServer()) {
                int strength = 100;
                Point2D.Float dir = GameMath.normalize((float)(attackerMob.x - (float)x), (float)(attackerMob.y - (float)y));
                level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, dir.x, dir.y, strength), level);
            }
            int animTime = (int)((float)this.getDashAnimTime(item, attackerMob));
            mapContent.setBoolean("charging", true);
            attackerMob.startAttackHandler((AttackHandler)new RapierDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed));
            return item;
        }
        item.getGndData().setBoolean("charging", false);
        float dx = (float)x - attackerMob.x;
        float dy = (float)y - attackerMob.y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        float angle = (float)Math.atan2(dy, dx);
        float angleOffset = (float)Math.toRadians(GameRandom.globalRandom.getFloatOffset(0.0f, 15.0f));
        int aimX = (int)((double)attackerMob.x + Math.cos(angle += angleOffset) * (double)distance);
        int aimY = (int)((double)attackerMob.y + Math.sin(angle) * (double)distance);
        float dirX = (float)aimX - attackerMob.x;
        float dirY = (float)aimY - attackerMob.y;
        float magnitude = (float)Math.sqrt(dirX * dirX + dirY * dirY);
        if (magnitude != 0.0f) {
            dirX /= magnitude;
            dirY /= magnitude;
        }
        item.getGndData().setFloat("attackDirX", dirX);
        item.getGndData().setFloat("attackDirY", dirY);
        return super.onAttack(level, aimX, aimY, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("charged") && !item.getGndData().getBoolean("charging")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (item.getGndData().getBoolean("charging")) {
            drawOptions.pointRotation(attackDirX, attackDirY);
            drawOptions.thrustOffsets(0.0f, 0.0f, attackProgress);
        } else {
            float newAttackDirX = item.getGndData().getFloat("attackDirX");
            float newAttackDirY = item.getGndData().getFloat("attackDirY");
            drawOptions.pointRotation(newAttackDirX, newAttackDirY);
            drawOptions.thrustOffsets(newAttackDirX, newAttackDirY, attackProgress);
        }
    }

    public int getComboAndCalc(InventoryItem item, ItemAttackerMob attackerMob) {
        int returnValue;
        int combo = item.getGndData().getInt("combo");
        if (combo == 0) {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        } else if (item.getGndData().getLong("lastAttack") + (long)this.getAttackAnimTime(item, attackerMob) * 4L > attackerMob.getTime()) {
            item.getGndData().setInt("combo", combo == 20 ? 0 : combo + 1);
            returnValue = combo;
        } else {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        }
        item.getGndData().setLong("lastAttack", attackerMob.getTime());
        return returnValue;
    }
}

