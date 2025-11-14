/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.Optional;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class EraserToolItem
extends ToolDamageItem
implements ItemInteractAction {
    public EraserToolItem() {
        super(0, null);
        this.setItemCategory("equipment", "tools", "creative");
        this.keyWords.add("creative");
        this.keyWords.add("pickaxe");
        this.keyWords.add("axe");
        this.toolType = ToolType.ALL;
        this.toolDps.setBaseValue(10000);
        this.toolTier.setBaseValue(9001.0f);
        this.hungerUsage = 0.0f;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(EraserToolItem.getToolTip());
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    @Override
    public Mob getMobHitOption(Level level, int levelX, int levelY, PlayerMob player, Line2D playerPositionLine, InventoryItem item) {
        Optional<Mob> mobAtTile = level.entityManager.mobs.streamInRegionsInTileRange(levelX, levelY, 5).filter(mob -> !mob.isPlayer && mob.getSelectBox().contains(levelX, levelY)).findFirst();
        return mobAtTile.orElseGet(() -> super.getMobHitOption(level, levelX, levelY, player, playerPositionLine, item));
    }

    @Override
    public boolean canDamageTile(Level level, int layerID, int tileX, int tileY, ItemAttackerMob attackerMob, InventoryItem item) {
        GameObject object = layerID >= 0 ? level.getObject(layerID, tileX, tileY) : level.getObject(tileX, tileY);
        return object.getID() != 0;
    }

    @Override
    public int getAttackHandlerDamageCooldown(InventoryItem item, ItemAttackerMob attackerMob) {
        if (attackerMob != null && attackerMob.isPlayer && ((PlayerMob)attackerMob).hasGodMode()) {
            return 0;
        }
        return super.getAttackHandlerDamageCooldown(item, attackerMob);
    }

    @Override
    public ToolDamageItem.SmartMineTarget getFirstSmartHitTile(Level level, PlayerMob player, InventoryItem attackItem, int mouseX, int mouseY) {
        if (attackItem.item == this) {
            boolean controllerIsAiming;
            boolean isController = Input.lastInputIsController && !ControllerInput.isCursorVisible();
            boolean bl = controllerIsAiming = Math.abs(ControllerInput.getAimX()) > 0.0f || Math.abs(ControllerInput.getAimY()) > 0.0f;
            if (isController && !controllerIsAiming) {
                return null;
            }
            LevelObject hitObject = GameUtils.getInteractObjectHit(level, mouseX, mouseY, 0, lo -> {
                if (lo.object.getID() == 0) {
                    return false;
                }
                if (!this.isTileInRange(level, lo.tileX, lo.tileY, player, null, attackItem)) {
                    return false;
                }
                return this.canDamageTile(level, lo.layerID, lo.tileX, lo.tileY, player, attackItem);
            }, null);
            if (hitObject != null && this.toolType.canDealDamageTo(hitObject.object.toolType)) {
                return new ToolDamageItem.SmartMineTarget(level, hitObject.tileX, hitObject.tileY, true, hitObject.layerID);
            }
        }
        return null;
    }

    @Override
    public InventoryItem runMobToolDamageHit(Level level, Mob targetMob, ItemAttackerMob attackerMob, Line2D attackerPositionLine, InventoryItem item, int animAttack, GNDItemMap mapContent) {
        targetMob.remove();
        return super.runMobToolDamageHit(level, targetMob, attackerMob, attackerPositionLine, item, animAttack, mapContent);
    }

    public static GameMessage getToolTip() {
        GameMessageBuilder builder = new GameMessageBuilder();
        builder.append(new LocalMessage("itemtooltip", "erasertip"));
        builder.append(new StaticMessage("\n"));
        boolean bound = Input.lastInputIsController ? ControllerInput.getStateGlyph(ControllerInput.ERASER) != null : Control.ERASER.getKey() != -1;
        LocalMessage bindTip = bound ? new LocalMessage("ui", "erasertip", "key", TypeParsers.getInputParseString(Control.ERASER)) : new LocalMessage("ui", "eraserunboundtip", "controlname", Control.ERASER.text);
        builder.append(bindTip);
        return builder;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob instanceof PlayerMob) {
            PlayerMob player = (PlayerMob)attackerMob;
            PlayerInventory trashInv = player.getInv().trash;
            InventoryItem trashItem = trashInv.getItem(0);
            InventoryItem draggingItem = player.getDraggingItem();
            if (draggingItem != null && draggingItem == item && trashItem != null) {
                trashInv.setItem(0, draggingItem);
                return trashItem.item.getID() == this.getID() ? null : trashItem;
            }
        }
        return ItemInteractAction.super.onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, seed, mapContent);
    }

    @Override
    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        return null;
    }
}

