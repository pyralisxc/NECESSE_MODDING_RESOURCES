/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public abstract class FormContainerArmorSlot
extends FormContainerSlot {
    protected ArmorItem.ArmorType armorType;
    protected boolean isCosmetic;

    public FormContainerArmorSlot(Client client, Container container, int containerSlotIndex, int x, int y, ArmorItem.ArmorType armorType, boolean isCosmetic) {
        super(client, container, containerSlotIndex, x, y);
        this.armorType = armorType;
        this.isCosmetic = isCosmetic;
        if (armorType == ArmorItem.ArmorType.HEAD) {
            if (isCosmetic) {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_hat);
            } else {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_helmet);
            }
        }
        if (armorType == ArmorItem.ArmorType.CHEST) {
            if (isCosmetic) {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_shirt);
            } else {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_chestplate);
            }
        }
        if (armorType == ArmorItem.ArmorType.FEET) {
            if (isCosmetic) {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_shoes);
            } else {
                this.setDecal(this.getInterfaceStyle().inventoryslot_icon_boots);
            }
        }
    }

    @Deprecated
    public FormContainerArmorSlot(Client client, int containerSlotIndex, int x, int y, ArmorItem.ArmorType armorType, boolean isCosmetic) {
        this(client, null, containerSlotIndex, x, y, armorType, isCosmetic);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.draw(tickManager, perspective, renderBox);
        InventoryItem item = this.getContainerSlot().getItem();
        if (this.isCosmetic && item != null && item.item instanceof ArmorItem && ((ArmorItem)item.item).getTotalArmorValue(item, perspective) != 0) {
            this.getInterfaceStyle().note_disabled.initDraw().draw(this.getX() + 5, this.getY() + 5);
        }
    }

    @Override
    public GameTooltips getClearTooltips() {
        if (this.armorType == ArmorItem.ArmorType.HEAD) {
            return new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "headslot"));
        }
        if (this.armorType == ArmorItem.ArmorType.CHEST) {
            return new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "chestslot"));
        }
        if (this.armorType == ArmorItem.ArmorType.FEET) {
            return new StringTooltips(Localization.translate("itemtooltip", (this.isCosmetic ? "cosmetic" : "") + "feetslot"));
        }
        return null;
    }

    @Override
    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        GameBlackboard buffBlackboard = new GameBlackboard();
        buffBlackboard.set("setItem", item);
        GameBlackboard blackboard = new GameBlackboard().set("isCosmeticSlot", this.isCosmetic).set("equippedMob", this.getEquippedMob(perspective)).set("setBonus", this.getSetBonusTooltips(buffBlackboard));
        return item.item.getTooltips(item, perspective, blackboard);
    }

    public abstract Mob getEquippedMob(PlayerMob var1);

    public abstract ListGameTooltips getSetBonusTooltips(GameBlackboard var1);
}

