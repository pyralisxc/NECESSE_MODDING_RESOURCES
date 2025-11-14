/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
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
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerSlot
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private boolean active;
    protected Client client;
    protected Container container;
    protected int containerSlotIndex;
    public boolean isSelected;
    protected GameSprite decal;
    public boolean drawDecalWhenOccupied = false;
    private boolean isHovering;

    public FormContainerSlot(Client client, Container container, int containerSlotIndex, int x, int y) {
        this.client = client;
        this.container = container;
        this.containerSlotIndex = containerSlotIndex;
        if (container != null && this.getContainerSlot() == null) {
            throw new IllegalArgumentException("Container slot with index " + containerSlotIndex + " does not exist in container");
        }
        this.position = new FormFixedPosition(x, y);
        this.setActive(true);
    }

    @Deprecated
    public FormContainerSlot(Client client, int containerSlotIndex, int x, int y) {
        this(client, null, containerSlotIndex, x, y);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        this.handleMouseMoveEvent(event);
        this.handleActionInputEvents(event);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        this.handleActionControllerEvents(event);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    protected void handleMouseMoveEvent(InputEvent event) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
    }

    protected void handleActionInputEvents(InputEvent event) {
        SelectionFloatMenu menu;
        if (!event.state || event.isKeyboardEvent()) {
            return;
        }
        GameWindow window = WindowManager.getWindow();
        ContainerAction action = null;
        if (event.getID() == -100) {
            if (!(!this.isMouseOver(event) || window.isKeyDown(340) && FormTypingComponent.appendItemToTyping(this.getContainerSlot().getItem()))) {
                if (Control.INV_QUICK_MOVE.isDown()) {
                    this.runAction(ContainerAction.QUICK_MOVE, event.shouldSubmitSound());
                } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
                    this.runAction(ContainerAction.QUICK_TRASH, event.shouldSubmitSound());
                } else if (Control.INV_QUICK_DROP.isDown() && !this.getContainerSlot().isItemLocked()) {
                    this.runAction(ContainerAction.QUICK_DROP, event.shouldSubmitSound());
                } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                    this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                } else {
                    menu = new SelectionFloatMenu(this){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }
                            super.draw(tickManager, perspective);
                        }
                    };
                    menu.setCreateEvent(event);
                    this.addLeftClickActions(menu);
                    if (!menu.isEmpty()) {
                        if (!this.getContainerSlot().isClear()) {
                            menu.add(Localization.translate("ui", "slottakefull"), () -> {
                                this.runAction(ContainerAction.LEFT_CLICK, false);
                                menu.remove();
                            });
                        }
                        this.getManager().openFloatMenu(menu);
                        this.playTickSound();
                    } else {
                        this.runAction(ContainerAction.LEFT_CLICK, event.shouldSubmitSound());
                    }
                }
            }
        } else if (event.getID() == -99 || event.isRepeatEvent((Object)this)) {
            if (this.isMouseOver(event)) {
                if (Control.INV_QUICK_MOVE.isDown()) {
                    int itemID;
                    ContainerSlot containerSlot = this.getContainerSlot();
                    InventoryItem item = containerSlot.getItem();
                    int n = itemID = item == null ? -1 : item.item.getID();
                    if (event.getID() == -99 || event.isRepeatEvent(new Object[]{this, ContainerAction.TAKE_ONE, itemID})) {
                        if (itemID != -1) {
                            event.startRepeatEvents(new Object[]{this, ContainerAction.TAKE_ONE, itemID});
                        }
                        this.runAction(ContainerAction.TAKE_ONE, event.shouldSubmitSound());
                    }
                } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
                    this.runAction(ContainerAction.QUICK_TRASH_ONE, event.shouldSubmitSound());
                } else if (Control.INV_QUICK_DROP.isDown() && !this.getContainerSlot().isItemLocked()) {
                    this.runAction(ContainerAction.QUICK_DROP_ONE, event.shouldSubmitSound());
                } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                    this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                } else {
                    menu = new SelectionFloatMenu(this){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }
                            super.draw(tickManager, perspective);
                        }
                    };
                    menu.setCreateEvent(event);
                    this.addRightClickActions(menu);
                    if (!menu.isEmpty()) {
                        if (!this.getContainerSlot().isClear()) {
                            InventoryItem invItem = this.getContainerSlot().getItem();
                            Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                            if (rAction != null) {
                                String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
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
                        }
                        this.getManager().openFloatMenu(menu);
                        this.playTickSound();
                    } else {
                        Supplier<ContainerActionResult> rAction;
                        InventoryItem invItem = this.getContainerSlot().getItem();
                        Supplier<ContainerActionResult> supplier = rAction = invItem == null ? null : invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            this.runAction(ContainerAction.RIGHT_CLICK_ACTION, event.shouldSubmitSound());
                        } else {
                            this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                        }
                    }
                }
            }
        } else if (event.getID() == -102) {
            if (this.isMouseOver(event)) {
                this.runAction(ContainerAction.QUICK_MOVE_ONE, event.shouldSubmitSound());
            }
        } else if (event.getID() == -103 && this.isMouseOver(event)) {
            this.runAction(ContainerAction.QUICK_GET_ONE, event.shouldSubmitSound());
        }
        if (action != null) {
            ContainerActionResult result = this.getContainer().applyContainerAction(this.containerSlotIndex, action);
            this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
            if (result.error != null) {
                Renderer.hudManager.addElement(new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, result.error, new FontOptions(16).outline(), "slotError"));
            }
            if ((result.value != 0 || result.error != null) && event.shouldSubmitSound()) {
                this.playTickSound();
            }
        }
    }

    protected void handleActionControllerEvents(ControllerEvent event) {
        if (!event.buttonState) {
            return;
        }
        if (this.isControllerFocus()) {
            if (event.getState() == ControllerInput.MENU_SELECT) {
                SelectionFloatMenu menu = new SelectionFloatMenu(this){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective) {
                        if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                            this.remove();
                        }
                        super.draw(tickManager, perspective);
                    }
                };
                this.addLeftClickActions(menu);
                if (!menu.isEmpty()) {
                    ControllerFocus currentFocus;
                    if (!this.getContainerSlot().isClear()) {
                        menu.add(Localization.translate("ui", "slottakefull"), () -> {
                            this.runAction(ContainerAction.LEFT_CLICK, false);
                            menu.remove();
                        });
                    }
                    if ((currentFocus = this.getManager().getCurrentFocus()) != null) {
                        this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                    } else {
                        this.getManager().openFloatMenu(menu);
                    }
                } else {
                    this.runAction(ContainerAction.LEFT_CLICK, event.shouldSubmitSound());
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) {
                ControllerFocus currentFocus = this.getManager().getCurrentFocus();
                if (currentFocus != null) {
                    SelectionFloatMenu menu = new SelectionFloatMenu(this){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }
                            super.draw(tickManager, perspective);
                        }
                    };
                    this.addRightClickActions(menu);
                    if (!this.getContainerSlot().isClear()) {
                        menu.add(Localization.translate("ui", "slottransfer"), () -> {
                            this.runAction(ContainerAction.QUICK_MOVE, false);
                            menu.remove();
                        });
                        InventoryItem invItem = this.getContainerSlot().getItem();
                        Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
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
                    }
                    if (!menu.isEmpty()) {
                        this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                        this.playTickSound();
                    }
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_INTERACT_ITEM) {
                SelectionFloatMenu menu = new SelectionFloatMenu(this){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective) {
                        if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                            this.remove();
                        }
                        super.draw(tickManager, perspective);
                    }
                };
                this.addRightClickActions(menu);
                if (!menu.isEmpty()) {
                    ControllerFocus currentFocus;
                    if (!this.getContainerSlot().isClear()) {
                        InventoryItem invItem = this.getContainerSlot().getItem();
                        Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
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
                    }
                    if ((currentFocus = this.getManager().getCurrentFocus()) != null) {
                        this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                    } else {
                        this.getManager().openFloatMenu(menu);
                    }
                    this.playTickSound();
                } else if (!this.getContainerSlot().isClear()) {
                    InventoryItem invItem = this.getContainerSlot().getItem();
                    Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
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
    }

    protected void runAction(ContainerAction action, boolean playSound) {
        ContainerActionResult result = this.getContainer().applyContainerAction(this.containerSlotIndex, action);
        this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
        if (result.error != null) {
            ControllerFocus currentFocus = this.getManager().getCurrentFocus();
            if (currentFocus != null && Input.lastInputIsController) {
                Renderer.hudManager.addElement(new UniqueScreenFloatText(currentFocus.boundingBox.x + currentFocus.boundingBox.width / 2, currentFocus.boundingBox.y, result.error, new FontOptions(16).outline(), "slotError"));
            } else {
                GameWindow window = WindowManager.getWindow();
                Renderer.hudManager.addElement(new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, result.error, new FontOptions(16).outline(), "slotError"));
            }
        }
        if ((result.value != 0 || result.error != null) && playSound) {
            this.playTickSound();
        }
    }

    protected void addLeftClickActions(SelectionFloatMenu menu) {
    }

    protected void addRightClickActions(SelectionFloatMenu menu) {
    }

    public boolean canCurrentlyLockItem() {
        return true;
    }

    public boolean canCurrentlyQuickTrash() {
        return true;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isHovering() && !this.getContainerSlot().isClear()) {
            if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash() && !this.getContainerSlot().isItemLocked()) {
                Renderer.setCursor(GameWindow.CURSOR.TRASH);
            } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem() && this.getContainerSlot().canLockItem()) {
                if (this.getContainerSlot().isItemLocked()) {
                    Renderer.setCursor(GameWindow.CURSOR.UNLOCK);
                } else {
                    Renderer.setCursor(GameWindow.CURSOR.LOCK);
                }
            }
        }
        Color drawCol = this.getDrawColor();
        InventoryItem item = this.getContainerSlot().getItem();
        HoverStateTextures slotTextures = item != null && item.isNew() ? this.getInterfaceStyle().inventoryslot_big_new : this.getInterfaceStyle().inventoryslot_big;
        GameTexture slotTexture = this.isHovering() ? slotTextures.highlighted : slotTextures.active;
        slotTexture.initDraw().color(drawCol).draw(this.getX(), this.getY());
        this.drawDecal(perspective);
        if (item != null) {
            item.draw(perspective, this.getX() + 4, this.getY() + 4);
            if (this.isHovering()) {
                item.setNew(false);
                Input input = WindowManager.getWindow().getInput();
                if (!input.isKeyDown(-100) && !input.isKeyDown(-99)) {
                    this.addItemTooltips(item, perspective);
                }
            }
        } else if (this.isHovering()) {
            this.addClearTooltips(perspective);
        }
        if (this.getContainerSlot().isItemLocked()) {
            this.getInterfaceStyle().note_locked.initDraw().draw(this.getX() + 5, this.getY() + 35 - this.getInterfaceStyle().note_locked.getHeight());
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "slotactions"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
    }

    public Color getDecalDrawColor() {
        if (!this.isActive()) {
            return this.getInterfaceStyle().inactiveFadedTextColor;
        }
        if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
            return this.getInterfaceStyle().inactiveFadedTextColor;
        }
        if (this.isHovering() || this.isSelected) {
            return this.getInterfaceStyle().highlightFadedTextColor;
        }
        return this.getInterfaceStyle().activeFadedTextColor;
    }

    public void drawDecal(PlayerMob perspective) {
        if (this.decal != null) {
            if (!this.drawDecalWhenOccupied && !this.getContainerSlot().isClear()) {
                return;
            }
            this.decal.initDraw().color(this.getDecalDrawColor()).draw(this.getX() + 20 - this.decal.width / 2, this.getY() + 20 - this.decal.height / 2);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormContainerSlot.singleBox(new Rectangle(this.getX() + 2, this.getY() + 2, 36, 36));
    }

    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        return item.getTooltip(perspective, new GameBlackboard());
    }

    public void addItemTooltips(InventoryItem item, PlayerMob perspective) {
        String rightControlTip;
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(this.getItemTooltip(item, perspective));
        if (Settings.showControlTips && (rightControlTip = item.item.getInventoryRightClickControlTip(this.getContainer(), item, this.containerSlotIndex, this.getContainerSlot())) != null) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_INTERACT_ITEM, rightControlTip));
            } else {
                tooltips.add(new InputTooltip(-99, rightControlTip));
            }
        }
        GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
    }

    public GameTooltips getClearTooltips() {
        return null;
    }

    public void addClearTooltips(PlayerMob perspective) {
        GameTooltips clearTooltips = this.getClearTooltips();
        if (clearTooltips != null) {
            GameTooltipManager.addTooltip(clearTooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public Color getDrawColor() {
        if (!this.isActive()) {
            return this.getInterfaceStyle().inactiveElementColor;
        }
        if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
            return this.getInterfaceStyle().inactiveElementColor;
        }
        if (this.isHovering() || this.isSelected) {
            return this.getInterfaceStyle().highlightElementColor;
        }
        return this.getInterfaceStyle().activeElementColor;
    }

    public FormContainerSlot setDecal(GameSprite sprite) {
        this.decal = sprite;
        return this;
    }

    public FormContainerSlot setDecal(GameTexture texture) {
        this.setDecal(new GameSprite(texture));
        return this;
    }

    public Container getContainer() {
        if (this.container != null) {
            return this.container;
        }
        return this.client.getContainer();
    }

    public ContainerSlot getContainerSlot() {
        return this.getContainer().getSlot(this.containerSlotIndex);
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

