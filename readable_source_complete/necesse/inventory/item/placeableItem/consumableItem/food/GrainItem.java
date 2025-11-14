/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.food;

import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodMatItem;
import necesse.level.maps.Level;

public class GrainItem
extends FoodMatItem
implements ItemInteractAction {
    public GrainItem(int stackSize, String ... globalIngredients) {
        super(stackSize, globalIngredients);
    }

    public GrainItem(int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
    }

    public GrainItem(int stackSize, Item.Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
    }

    public GrainItem(int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(new LocalMessage("itemtooltip", "graintip"));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public boolean canMobInteract(Level level, Mob mob, ItemAttackerMob attackerMob, InventoryItem item) {
        return mob instanceof HusbandryMob && ((HusbandryMob)mob).canFeed(item) && mob.inInteractRange(attackerMob);
    }

    @Override
    public InventoryItem onMobInteract(Level level, Mob targetMob, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        HusbandryMob hMob;
        if (targetMob instanceof HusbandryMob && (hMob = (HusbandryMob)targetMob).canFeed(item)) {
            return hMob.onFed(item);
        }
        return item;
    }

    @Override
    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    @Override
    public boolean onMouseHoverMob(InventoryItem item, GameCamera camera, PlayerMob perspective, Mob mob, boolean isDebug) {
        boolean sup = super.onMouseHoverMob(item, camera, perspective, mob, isDebug);
        if (mob instanceof HusbandryMob && ((HusbandryMob)mob).canFeed(item)) {
            if (mob.inInteractRange(perspective)) {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "feedtip")), TooltipLocation.INTERACT_FOCUS);
            } else {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "feedtip"), 0.5f), TooltipLocation.INTERACT_FOCUS);
            }
            return true;
        }
        return sup;
    }
}

