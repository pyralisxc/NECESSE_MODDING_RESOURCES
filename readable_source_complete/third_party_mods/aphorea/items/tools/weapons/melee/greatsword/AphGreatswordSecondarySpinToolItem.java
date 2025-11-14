/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.attackHandler.GreatswordAttackHandler
 *  necesse.entity.mobs.attackHandler.GreatswordChargeLevel
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemControllerInteract
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.greatsword;

import aphorea.items.tools.weapons.melee.greatsword.logic.GreatswordSecondarySpinAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.registry.AphBuffs;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;

public abstract class AphGreatswordSecondarySpinToolItem
extends AphGreatswordToolItem
implements ItemInteractAction {
    Color spinAttackColor;
    boolean secondaryAttack = false;

    public AphGreatswordSecondarySpinToolItem(int enchantCost, int attackAnimTime, GreatswordChargeLevel[] chargeLevels, Color spinAttackColor) {
        super(enchantCost, chargeLevels);
        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.spinAttackColor = spinAttackColor;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"spinsecondaryattack"));
        return tooltips;
    }

    public float getSwingRotationOffset(InventoryItem item, int dir, float swingAngle) {
        float offset = super.getSwingRotationOffset(item, dir, swingAngle);
        if (this.secondaryAttack) {
            offset = dir == 1 || dir == 3 ? (offset -= 180.0f) : (offset -= 90.0f);
        }
        return offset;
    }

    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target, 5.0f);
    }

    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return !mob.isPlayer && this.canDash(mob) ? 200 : super.getItemAttackerAttackRange(mob, item);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            mapContent.setBoolean("chargeUp", true);
            this.secondaryAttack = true;
            attackerMob.startAttackHandler(new GreatswordSecondarySpinAttackHandler<AphGreatswordSecondarySpinToolItem>(attackerMob, slot, item, this, 1000, this.spinAttackColor, seed));
        } else {
            item.getGndData().setBoolean("chargeUp", false);
            this.secondaryAttack = false;
            if (animAttack == 0) {
                attackerMob.startAttackHandler((AttackHandler)new GreatswordAttackHandler(attackerMob, slot, item, (GreatswordToolItem)this, seed, x, y, this.chargeLevels));
            }
        }
        return item;
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        mapContent.setBoolean("chargeUp", true);
        this.secondaryAttack = true;
        attackerMob.startAttackHandler(new GreatswordSecondarySpinAttackHandler<AphGreatswordSecondarySpinToolItem>(attackerMob, slot, item, this, 2000, this.spinAttackColor, seed).startFromInteract());
        return item;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return this.secondaryAttack ? 360.0f : 150.0f;
    }

    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return this.secondaryAttack ? 360.0f : 150.0f;
    }
}

