/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerInventoryList
extends FormGeneralGridList<ContainerSlotElement> {
    protected Client client;

    public FormContainerInventoryList(int x, int y, int width, int height, Client client) {
        super(x, y, width, height, 40, 40);
        this.client = client;
    }

    public FormContainerInventoryList(int x, int y, int width, int height, Client client, int startSlotIndex, int endSlotIndex) {
        super(x, y, width, height, 40, 40);
        this.client = client;
        this.addSlots(startSlotIndex, endSlotIndex);
    }

    public void addSlots(int startSlotIndex, int endSlotIndex) {
        for (int i = startSlotIndex; i <= endSlotIndex; ++i) {
            this.addSlot(i);
        }
    }

    public void addSlot(int slotIndex) {
        this.elements.add(new ContainerSlotElement(slotIndex));
    }

    public Container getContainer() {
        return this.client.getContainer();
    }

    public ContainerSlotElement getSlotElement(int index) {
        return (ContainerSlotElement)this.elements.get(index);
    }

    public ContainerSlotElement getSlotElementByContainerIndex(int index) {
        for (ContainerSlotElement slot : this.elements) {
            if (slot.containerSlotIndex != index) continue;
            return slot;
        }
        return null;
    }

    public class ContainerSlotElement
    extends FormListGridElement<FormContainerInventoryList> {
        public final int containerSlotIndex;
        private GameSprite decal;

        public ContainerSlotElement(int containerSlotIndex) {
            this.containerSlotIndex = containerSlotIndex;
        }

        @Override
        protected void draw(FormContainerInventoryList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            InventoryItem item;
            ContainerSlot slot = this.getContainerSlot();
            if (this.isMouseOver(parent) && !slot.isClear()) {
                if (Control.INV_QUICK_TRASH.isDown() && !slot.isItemLocked()) {
                    Renderer.setCursor(GameWindow.CURSOR.TRASH);
                } else if (Control.INV_LOCK.isDown() && slot.canLockItem()) {
                    if (slot.isItemLocked()) {
                        Renderer.setCursor(GameWindow.CURSOR.UNLOCK);
                    } else {
                        Renderer.setCursor(GameWindow.CURSOR.LOCK);
                    }
                }
            }
            Color drawCol = FormContainerInventoryList.this.getInterfaceStyle().activeElementColor;
            if (this.isMouseOver(parent)) {
                drawCol = FormContainerInventoryList.this.getInterfaceStyle().highlightElementColor;
            }
            HoverStateTextures slotTextures = (item = this.getContainerSlot().getItem()) != null && item.isNew() ? FormContainerInventoryList.this.getInterfaceStyle().inventoryslot_big_new : FormContainerInventoryList.this.getInterfaceStyle().inventoryslot_big;
            GameTexture slotTexture = this.isMouseOver(parent) ? slotTextures.highlighted : slotTextures.active;
            slotTexture.initDraw().color(drawCol).draw(0, 0);
            if (this.decal != null) {
                this.decal.initDraw().color(drawCol).draw(20 + this.decal.width / 2, 20 + this.decal.height / 2);
            }
            if (!slot.isClear()) {
                slot.getItem().draw(perspective, 4, 4);
                if (this.isMouseOver(parent) && !WindowManager.getWindow().isKeyDown(-100) && !WindowManager.getWindow().isKeyDown(-99)) {
                    GameTooltipManager.addTooltip(slot.getItem().getTooltip(perspective, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                }
            }
        }

        @Override
        protected void onClick(FormContainerInventoryList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            ContainerAction action = null;
            if (event.getID() == -100) {
                action = Control.INV_QUICK_MOVE.isDown() ? ContainerAction.QUICK_MOVE : (Control.INV_QUICK_TRASH.isDown() ? ContainerAction.QUICK_TRASH : (Control.INV_QUICK_DROP.isDown() ? ContainerAction.QUICK_DROP : (Control.INV_LOCK.isDown() ? ContainerAction.TOGGLE_LOCKED : ContainerAction.LEFT_CLICK)));
            } else if (event.getID() == -99) {
                if (Control.INV_QUICK_MOVE.isDown()) {
                    int itemID;
                    ContainerSlot containerSlot = this.getContainerSlot();
                    InventoryItem item = containerSlot.getItem();
                    int n = itemID = item == null ? -1 : item.item.getID();
                    if (event.getID() == -99 || event.isRepeatEvent(new Object[]{this, ContainerAction.TAKE_ONE, itemID})) {
                        if (itemID != -1) {
                            event.startRepeatEvents(new Object[]{this, ContainerAction.TAKE_ONE, itemID});
                        }
                        action = ContainerAction.TAKE_ONE;
                    }
                } else if (Control.INV_QUICK_TRASH.isDown()) {
                    action = ContainerAction.QUICK_TRASH_ONE;
                } else if (Control.INV_QUICK_DROP.isDown()) {
                    action = ContainerAction.QUICK_DROP_ONE;
                } else if (Control.INV_LOCK.isDown()) {
                    action = ContainerAction.TOGGLE_LOCKED;
                } else {
                    action = ContainerAction.RIGHT_CLICK;
                    if (!this.getContainerSlot().isClear()) {
                        InventoryItem invItem = this.getContainerSlot().getItem();
                        Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            action = ContainerAction.RIGHT_CLICK_ACTION;
                        }
                    }
                }
            } else if (event.getID() == -102) {
                action = ContainerAction.QUICK_MOVE_ONE;
            } else if (event.getID() == -103) {
                action = ContainerAction.QUICK_GET_ONE;
            }
            if (action != null) {
                ContainerActionResult result = FormContainerInventoryList.this.getContainer().applyContainerAction(this.containerSlotIndex, action);
                FormContainerInventoryList.this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
                if (result.error != null) {
                    InputPosition mousePos = WindowManager.getWindow().mousePos();
                    Renderer.hudManager.addElement(new UniqueScreenFloatText(mousePos.hudX, mousePos.hudY, result.error, new FontOptions(16).outline(), "slotError"));
                }
                if ((result.value != 0 || result.error != null) && event.shouldSubmitSound()) {
                    FormContainerInventoryList.this.playTickSound();
                }
            }
        }

        @Override
        protected void onControllerEvent(FormContainerInventoryList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() == ControllerInput.MENU_SELECT) {
                this.runAction(ContainerAction.LEFT_CLICK, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) {
                ControllerFocus currentFocus;
                if (!this.getContainerSlot().isClear() && (currentFocus = FormContainerInventoryList.this.getManager().getCurrentFocus()) != null) {
                    InventoryItem invItem = this.getContainerSlot().getItem();
                    SelectionFloatMenu menu = new SelectionFloatMenu(parent){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!FormContainerInventoryList.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }
                            super.draw(tickManager, perspective);
                        }
                    };
                    menu.add(Localization.translate("ui", "slottransfer"), () -> {
                        this.runAction(ContainerAction.QUICK_MOVE, false);
                        menu.remove();
                    });
                    Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                    if (rAction != null) {
                        String tip = invItem.item.getInventoryRightClickControlTip(FormContainerInventoryList.this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (tip != null) {
                            menu.add(tip, () -> {
                                this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                menu.remove();
                            });
                        } else {
                            menu.add(Localization.translate("ui", "slotuse"), () -> {
                                this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                menu.remove();
                            });
                        }
                    } else {
                        menu.add(Localization.translate("ui", "slotsplit"), () -> {
                            this.runAction(ContainerAction.RIGHT_CLICK, false);
                            menu.remove();
                        });
                    }
                    menu.add(Localization.translate("ui", this.getContainerSlot().isItemLocked() ? "slotunlock" : "slotlock"), () -> {
                        this.runAction(ContainerAction.TOGGLE_LOCKED, false);
                        menu.remove();
                    });
                    if (!this.getContainerSlot().isItemLocked()) {
                        menu.add(Localization.translate("ui", "slottrash"), () -> {
                            this.runAction(ContainerAction.QUICK_TRASH, false);
                            menu.remove();
                        });
                    }
                    menu.add(Localization.translate("ui", "slottakeone"), () -> {
                        this.runAction(ContainerAction.TAKE_ONE, false);
                        menu.remove();
                    });
                    if (!this.getContainerSlot().isItemLocked()) {
                        menu.add(Localization.translate("ui", "slotdrop"), () -> {
                            this.runAction(ContainerAction.QUICK_DROP, false);
                            menu.remove();
                        });
                    }
                    FormContainerInventoryList.this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                    FormContainerInventoryList.this.playTickSound();
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_INTERACT_ITEM) {
                if (!this.getContainerSlot().isClear()) {
                    InventoryItem invItem = this.getContainerSlot().getItem();
                    Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                    if (rAction != null) {
                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, event.shouldSubmitSound());
                    } else {
                        this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                    }
                } else {
                    this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_QUICK_TRANSFER) {
                this.runAction(ContainerAction.QUICK_MOVE, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_QUICK_TRASH) {
                this.runAction(ContainerAction.QUICK_TRASH, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_DROP_ITEM) {
                this.runAction(ContainerAction.QUICK_DROP, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_LOCK_ITEM) {
                this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_MOVE_ONE_ITEM) {
                this.runAction(ContainerAction.QUICK_MOVE_ONE, event.shouldSubmitSound());
                event.use();
            } else if (event.getState() == ControllerInput.MENU_GET_ONE_ITEM) {
                this.runAction(ContainerAction.QUICK_GET_ONE, event.shouldSubmitSound());
                event.use();
            }
        }

        protected void runAction(ContainerAction action, boolean playSound) {
            ContainerActionResult result = FormContainerInventoryList.this.getContainer().applyContainerAction(this.containerSlotIndex, action);
            FormContainerInventoryList.this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
            if (result.error != null) {
                ControllerFocus currentFocus = FormContainerInventoryList.this.getManager().getCurrentFocus();
                Renderer.hudManager.addElement(new UniqueScreenFloatText(currentFocus.boundingBox.x + currentFocus.boundingBox.width / 2, currentFocus.boundingBox.y, result.error, new FontOptions(16).outline(), "slotError"));
            }
            if ((result.value != 0 || result.error != null) && playSound) {
                FormContainerInventoryList.this.playTickSound();
            }
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "slotactions"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
        }

        @Override
        public boolean isMouseOver(FormContainerInventoryList parent) {
            if (parent.isControllerFocus(this)) {
                return true;
            }
            InputEvent event = this.getMoveEvent();
            if (event == null) {
                return false;
            }
            return new Rectangle(2, 2, parent.elementWidth - 4, parent.elementHeight - 4).contains(event.pos.hudX, event.pos.hudY);
        }

        public ContainerSlot getContainerSlot() {
            return FormContainerInventoryList.this.getContainer().getSlot(this.containerSlotIndex);
        }

        public void setDecal(GameSprite sprite) {
            this.decal = sprite;
        }

        public void setDecal(GameTexture texture) {
            this.setDecal(new GameSprite(texture));
        }
    }
}

