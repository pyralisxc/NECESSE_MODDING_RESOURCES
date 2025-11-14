/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerInventorySetProxy;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorSlot;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public class FormContainerPlayerArmorSlot
extends FormContainerArmorSlot {
    public FormContainerPlayerArmorSlot(Client client, Container container, int containerSlotIndex, int x, int y, ArmorItem.ArmorType armorType, boolean isCosmetic) {
        super(client, container, containerSlotIndex, x, y, armorType, isCosmetic);
    }

    @Override
    public Mob getEquippedMob(PlayerMob perspective) {
        return perspective;
    }

    @Override
    public ListGameTooltips getSetBonusTooltips(GameBlackboard blackboard) {
        return this.getContainer().getClient().playerMob.equipmentBuffManager.getSetBonusBuffTooltip(blackboard);
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
    }

    @Override
    public GameTooltips getClearTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        GameTooltips superTip = super.getClearTooltips();
        if (superTip != null) {
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
    public void addItemTooltips(InventoryItem item, PlayerMob perspective) {
        String rightControlTip;
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(this.getItemTooltip(item, perspective));
        if (!this.addUsingItemSetTooltip(tooltips) && Settings.showControlTips && (rightControlTip = item.item.getInventoryRightClickControlTip(this.getContainer(), item, this.containerSlotIndex, this.getContainerSlot())) != null) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_INTERACT_ITEM, rightControlTip));
            } else {
                tooltips.add(new InputTooltip(-99, rightControlTip));
            }
        }
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
                if (this.isCosmetic) {
                    return manager.equipment.cosmetic.get(manager.equipment.getSelectedSet());
                }
                return manager.equipment.armor.get(manager.equipment.getSelectedSet());
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

