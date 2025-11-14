/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.LineHitbox
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.AttackAnimMob
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions$AttackItemSprite
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import aphorea.packets.AphCustomPushPacket;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class MagicalBroom
extends AphSwordToolItem {
    public static GameTexture worldTexture;
    int currentA;

    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(worldTexture);
    }

    public MagicalBroom() {
        super(650);
        this.setItemCategory(new String[]{"equipment", "weapons", "magicweapons"});
        this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "magicweapons"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"equipment", "weapons", "magicweapons"});
        this.damageType = DamageTypeRegistry.MAGIC;
        this.width = 15.0f;
        this.showAttackAllDirections = true;
        this.resilienceGain.setBaseValue(2.0f);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 82.0f);
        this.attackRange.setBaseValue(160);
        this.knockback.setBaseValue(250);
        this.manaCost.setBaseValue(1.0f);
        this.attackYOffset = 155;
        this.attackXOffset = 30;
        this.currentA = 0;
        this.keyWords.remove("sword");
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, (Mob)perspective, forceAdd);
        this.addKnockbackTip(list, currentItem, lastItem, (Attacker)perspective);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, (Mob)perspective);
    }

    public int getFlatItemCooldownTime(InventoryItem item) {
        return (int)((float)this.getFlatAttackAnimTime(item) * 1.5f);
    }

    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        int n = attackProgress < 0.3f ? (this.currentA == 0 ? 9 : 4) : (attackProgress < 0.45f ? (this.currentA == 0 ? 8 : 3) : (attackProgress < 0.55f ? (this.currentA == 0 ? 7 : 2) : (attackProgress < 0.7f ? (this.currentA == 0 ? 6 : 1) : (this.currentA == 0 ? 5 : 0))));
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.attackTexture, n, 0, 320);
        itemSprite.itemRotatePoint(options.dir == 2 ? this.attackXOffset + 25 : this.attackXOffset, options.dir == 2 ? this.attackYOffset - 5 : this.attackYOffset);
        if (itemColor != null) {
            itemSprite.itemColor(itemColor);
        }
        if (options.dir == 0 || options.dir == 2) {
            itemSprite.itemRotateOffset(-45.0f);
        }
        return itemSprite.itemEnd();
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(drawOptions.dir == 0 || drawOptions.dir == 2 ? 0.0f : attackDirX, drawOptions.dir == 0 || drawOptions.dir == 2 ? attackDirY : 0.0f, this.getSwingRotationOffset(item, drawOptions.dir, this.getSwingRotationAngle(item, drawOptions.dir)));
    }

    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return 180.0f;
    }

    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return 180.0f;
    }

    public float getSwingRotationOffset(InventoryItem item, int dir, float swingAngle) {
        if (dir == 0 || dir == 2) {
            return (swingAngle - 90.0f) / 2.0f;
        }
        return 0.0f;
    }

    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<Shape>();
        int attackRange = this.getAttackRange(item);
        float lastProgress = event.lastHitboxProgress;
        float nextProgress = mob.getAttackAnimProgress();
        float circumference = (float)(Math.PI * (double)attackRange);
        float percPerWidth = Math.max(10.0f, this.width) / circumference;
        Point2D.Float base = new Point2D.Float(mob.x, mob.y);
        int attackDir = mob.getDir();
        if (attackDir == 0) {
            base.x += 8.0f;
        } else if (attackDir == 2) {
            base.x -= 8.0f;
        }
        for (float progress = lastProgress; progress <= nextProgress; progress += percPerWidth) {
            float angle = ((Float)this.getSwingDirection(item, mob).apply(Float.valueOf(progress))).floatValue();
            Point2D.Float dir = GameMath.getAngleDir((float)angle);
            Line2D.Float attackLine = new Line2D.Float(base.x, base.y, dir.x * (float)attackRange + mob.x, dir.y * (float)attackRange + mob.y);
            if (this.width > 0.0f) {
                out.add((Shape)new LineHitbox((Line2D)attackLine, this.width));
            } else {
                out.add(attackLine);
            }
            if (forDebug) continue;
            event.lastHitboxProgress = progress;
        }
        return out;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (animAttack == 0) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();
            if (attackerMob.isServer()) {
                int strength = 50;
                Point2D.Float dir = GameMath.normalize((float)((float)x - attackerMob.x), (float)((float)y - attackerMob.y));
                level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, dir.x, dir.y, strength), level);
            } else if (attackerMob.isClient()) {
                this.currentA = this.currentA == 0 ? 1 : 0;
                this.animInverted = this.currentA == 1;
            }
            int animTime = this.getAttackAnimTime(item, attackerMob);
            int aimX = x - attackerMob.getX();
            int aimY = y - attackerMob.getY() + attackHeight;
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent((AttackAnimMob)attackerMob, seed, item, aimX, aimY, animTime, animTime);
            level.entityManager.events.addHidden((LevelEvent)event);
            this.consumeMana(attackerMob, item);
        }
        return item;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"magicweapon");
    }
}

