/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.ArrayList;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
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
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

public class ShearsItem
extends Item
implements ItemInteractAction {
    public ShearsItem() {
        super(1);
        this.setItemCategory("equipment", "tools", "misc");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools");
        this.attackXOffset = 8;
        this.attackYOffset = 20;
        this.dropsAsMatDeathPenalty = false;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "shearstip"), 300));
        return tooltips;
    }

    @Override
    public boolean canMobInteract(Level level, Mob mob, ItemAttackerMob attackerMob, InventoryItem item) {
        return mob instanceof HusbandryMob && ((HusbandryMob)mob).canShear(item) && mob.inInteractRange(attackerMob);
    }

    @Override
    public InventoryItem onMobInteract(Level level, Mob targetMob, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        HusbandryMob hMob;
        if (targetMob instanceof HusbandryMob && (hMob = (HusbandryMob)targetMob).canShear(item)) {
            ArrayList<InventoryItem> products = new ArrayList<InventoryItem>();
            InventoryItem out = hMob.onShear(item, products);
            if (!level.isClient()) {
                for (InventoryItem product : products) {
                    level.entityManager.pickups.add(product.getPickupEntity(level, hMob.x, hMob.y));
                }
            }
            return out;
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
        if (mob instanceof HusbandryMob && ((HusbandryMob)mob).canShear(item)) {
            if (mob.inInteractRange(perspective)) {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "sheartip")), TooltipLocation.INTERACT_FOCUS);
            } else {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "sheartip"), 0.5f), TooltipLocation.INTERACT_FOCUS);
            }
            return true;
        }
        return sup;
    }
}

