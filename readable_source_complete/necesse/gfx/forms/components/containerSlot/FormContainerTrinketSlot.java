/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerInventorySetProxy;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerTrinketSlot
extends FormContainerSlot {
    public boolean isAbilitySlot;

    public FormContainerTrinketSlot(Client client, Container container, int containerSlotIndex, int x, int y, boolean isAbilitySlot) {
        super(client, container, containerSlotIndex, x, y);
        this.isAbilitySlot = isAbilitySlot;
        this.setDecal(isAbilitySlot ? this.getInterfaceStyle().inventoryslot_icon_trinket_ability : this.getInterfaceStyle().inventoryslot_icon_trinket);
    }

    @Override
    protected void handleActionInputEvents(InputEvent event) {
        this.updateActive();
        if (!this.isActive()) {
            if (event.state && event.isMouseClickEvent() && this.isMouseOver(event)) {
                PlayerEquipmentInventory actualInventory = this.getActualInventory();
                actualInventory.setProxy(this.getContainerSlot().getInventorySlot(), actualInventory.setIndex);
                this.client.network.sendPacket(new PacketPlayerInventorySetProxy(this.client, actualInventory.getInventoryID(), this.getContainerSlot().getInventorySlot(), actualInventory.setIndex));
                this.playTickSound();
            }
        } else {
            super.handleActionInputEvents(event);
        }
    }

    @Override
    protected void handleActionControllerEvents(ControllerEvent event) {
        this.updateActive();
        if (!this.isActive()) {
            if (this.isControllerFocus() && event.buttonState && (event.getState() == ControllerInput.MENU_SELECT || event.getState() == ControllerInput.MENU_INTERACT_ITEM)) {
                PlayerEquipmentInventory actualInventory = this.getActualInventory();
                actualInventory.setProxy(this.getContainerSlot().getInventorySlot(), actualInventory.setIndex);
                this.client.network.sendPacket(new PacketPlayerInventorySetProxy(this.client, actualInventory.getInventoryID(), this.getContainerSlot().getInventorySlot(), actualInventory.setIndex));
                this.playTickSound();
            }
        } else {
            super.handleActionControllerEvents(event);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateActive();
        super.draw(tickManager, perspective, renderBox);
        ArrayList<Integer> disables = this.getDisables(perspective);
        if (!disables.isEmpty()) {
            this.getInterfaceStyle().note_disabled.initDraw().draw(this.getX() + 5, this.getY() + 5);
        }
    }

    @Override
    public GameTooltips getClearTooltips() {
        GameTooltips superTip;
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", this.isAbilitySlot ? "trinketabilityslot" : "trinketslot"));
        if (this.isAbilitySlot) {
            tooltips.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
        }
        if ((superTip = super.getClearTooltips()) != null) {
            tooltips.add(superTip);
        }
        if (!this.addUsingItemSetTooltip(tooltips) && this.container.getClientDraggingSlot().isClear() && this.container.client.playerMob.getInv().equipment.getTotalSets() > 1) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "useitemsetproxy")));
            } else {
                tooltips.add(new InputTooltip(-100, Localization.translate("ui", "useitemsetproxy")));
            }
        }
        return tooltips;
    }

    @Override
    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        GameBlackboard blackboard = new GameBlackboard().set("equipped", true).set("isAbilitySlot", this.isAbilitySlot);
        ListGameTooltips tooltips = item.item.getTooltips(item, perspective, blackboard);
        ArrayList<Integer> disables = this.getDisables(perspective);
        if (disables.size() > 0) {
            StringBuilder items = new StringBuilder();
            for (int i = 0; i < disables.size(); ++i) {
                items.append(ItemRegistry.getDisplayName(disables.get(i)));
                if (i >= disables.size() - 1) continue;
                items.append(", ");
            }
            tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "trinketdisabled", "items", items.toString())));
        }
        return tooltips;
    }

    public ArrayList<Integer> getDisables(PlayerMob perspective) {
        ContainerSlot slot = this.getContainerSlot();
        return perspective.equipmentBuffManager.getTrinketBuffDisables(slot.getItem());
    }

    @Override
    public void addItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(this.getItemTooltip(item, perspective));
        this.addUsingItemSetTooltip(tooltips);
        GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
    }

    private boolean addUsingItemSetTooltip(ListGameTooltips tooltips) {
        if (!this.isActive()) {
            int usingSet;
            boolean addedSpacing = false;
            PlayerEquipmentInventory actualInventory = this.getActualInventory();
            if (actualInventory != null && (usingSet = actualInventory.getProxy(this.getContainerSlot().getInventorySlot())) != actualInventory.setIndex) {
                tooltips.add(new SpacerGameTooltip(10));
                addedSpacing = true;
                tooltips.add(Localization.translate("ui", "usingitemsetnum", "number", (Object)(usingSet + 1)));
            }
            if (Settings.showControlTips) {
                if (!addedSpacing) {
                    tooltips.add(new SpacerGameTooltip(10));
                }
                if (Input.lastInputIsController) {
                    tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "clearitemsetproxy")));
                } else {
                    tooltips.add(new InputTooltip(-100, Localization.translate("ui", "clearitemsetproxy")));
                }
            }
            return true;
        }
        return false;
    }

    private void updateActive() {
        boolean active = true;
        Inventory inventory = this.getContainerSlot().getInventory();
        if (inventory instanceof PlayerEquipmentInventory) {
            PlayerEquipmentInventory playerInventory = (PlayerEquipmentInventory)inventory;
            if (playerInventory.player.getInv().equipment.getSelectedSet() != playerInventory.setIndex) {
                active = false;
            }
        }
        if (active != this.isActive()) {
            this.setActive(active);
        }
    }

    public PlayerEquipmentInventory getActualInventory() {
        Inventory inventory = this.getContainerSlot().getInventory();
        if (inventory instanceof PlayerEquipmentInventory) {
            PlayerEquipmentInventory playerInventory = (PlayerEquipmentInventory)inventory;
            PlayerInventoryManager manager = playerInventory.player.getInv();
            if (playerInventory.player.getInv().equipment.getSelectedSet() != playerInventory.setIndex) {
                if (this.isAbilitySlot) {
                    return manager.equipment.equipment.get(manager.equipment.getSelectedSet());
                }
                return manager.equipment.trinkets.get(manager.equipment.getSelectedSet());
            }
            return playerInventory;
        }
        return null;
    }

    @Override
    protected void addLeftClickActions(SelectionFloatMenu menu) {
        PlayerEquipmentInventory actualInventory;
        super.addLeftClickActions(menu);
        if (this.getContainerSlot().isClear() && this.container.getClientDraggingSlot().isClear() && (actualInventory = this.getActualInventory()) != null) {
            if (actualInventory == this.getContainerSlot().getInventory()) {
                if (actualInventory.player.getInv().equipment.getTotalSets() > 1) {
                    for (int i = 0; i < actualInventory.player.getInv().equipment.getTotalSets(); ++i) {
                        int finalI = i;
                        if (finalI == actualInventory.setIndex) continue;
                        menu.add(Localization.translate("ui", "useitemsetnum", "number", (Object)(i + 1)), () -> {
                            actualInventory.setProxy(this.getContainerSlot().getInventorySlot(), finalI);
                            this.client.network.sendPacket(new PacketPlayerInventorySetProxy(this.client, actualInventory.getInventoryID(), this.getContainerSlot().getInventorySlot(), finalI));
                            menu.remove();
                        });
                    }
                }
            } else {
                menu.add(Localization.translate("ui", "clearitemsetproxy"), () -> {
                    actualInventory.setProxy(this.getContainerSlot().getInventorySlot(), actualInventory.setIndex);
                    this.client.network.sendPacket(new PacketPlayerInventorySetProxy(this.client, actualInventory.getInventoryID(), this.getContainerSlot().getInventorySlot(), actualInventory.setIndex));
                    menu.remove();
                });
            }
        }
    }
}

