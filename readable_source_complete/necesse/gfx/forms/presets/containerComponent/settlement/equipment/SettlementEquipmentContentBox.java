/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement.equipment;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSettlersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.equipment.SettlementEquipmentForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementEquipmentContentBox
extends FormContentBox {
    public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
    public final SettlementEquipmentForm<?> equipmentsForm;
    public ArrayList<SettlementEquipmentForm.PasteButton> pasteButtons = new ArrayList();
    public int contentHeight;

    public SettlementEquipmentContentBox(SettlementEquipmentForm<?> equipmentsForm, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.equipmentsForm = equipmentsForm;
    }

    public void updateEquipmentsContent() {
        this.clearComponents();
        this.pasteButtons.clear();
        FormFlow flow = new FormFlow(0);
        boolean hasAnySettlers = false;
        int settlersOutside = 0;
        Comparator<SettlementSettlerData> comparing = Comparator.comparing(s -> s.settler.getID());
        comparing = comparing.thenComparing(s -> s.mobUniqueID);
        this.equipmentsForm.settlers.sort(comparing);
        for (final SettlementSettlerEquipmentFilterData data : this.equipmentsForm.settlers) {
            SettlerMob settlerMob = data.getSettlerMob(this.equipmentsForm.client.getLevel());
            if (settlerMob != null) {
                final Mob mob = settlerMob.getMob();
                if (!(mob instanceof HumanMob)) continue;
                final HumanMob humanMob = (HumanMob)mob;
                hasAnySettlers = true;
                int padding = SettlementSettlersForm.SETTLER_LIST_PADDING;
                int height = 32 + padding * 2;
                int startY = flow.next(height);
                int y = startY + padding;
                int filterButtonWidth = 24;
                int buttonX = this.getWidth() - filterButtonWidth - this.getScrollBarWidth() - 2;
                FormContentIconButton configureFilterButton = this.addComponent(new FormContentIconButton(buttonX, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_storage_config, new GameMessage[]{new LocalMessage("ui", "settlerfilterequipment")}){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        this.setActive((Boolean)humanMob.selfManageEquipment.get());
                        super.draw(tickManager, perspective, renderBox);
                    }
                });
                configureFilterButton.onClicked(e -> {
                    EquipmentFiltersForm filterForm = new EquipmentFiltersForm("changeequipmentfilter", 408, 300, mob, data.equipmentFilter, () -> data.preferArmorSets, this.equipmentsForm.client){

                        @Override
                        public void onSetPreferArmorSets(boolean preferArmorSets) {
                            data.preferArmorSets = preferArmorSets;
                            ((SettlementContainer)SettlementEquipmentContentBox.this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendPreferArmorSets(mob.getUniqueID(), preferArmorSets);
                        }

                        @Override
                        public void onItemsChanged(Item[] items, boolean allowed) {
                            ((SettlementContainer)SettlementEquipmentContentBox.this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                        }

                        @Override
                        public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                            ((SettlementContainer)SettlementEquipmentContentBox.this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                        }

                        @Override
                        public void onFullChange(ItemCategoriesFilter filter) {
                            ((SettlementContainer)SettlementEquipmentContentBox.this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(filter));
                        }

                        @Override
                        public void onButtonPressed() {
                            SettlementEquipmentContentBox.this.equipmentsForm.makeCurrent(SettlementEquipmentContentBox.this.equipmentsForm.mainForm);
                        }

                        @Override
                        public void onWindowResized(GameWindow window) {
                            super.onWindowResized(window);
                            ContainerComponent.setPosInventory(this);
                        }
                    };
                    this.equipmentsForm.addAndMakeCurrentTemporary(filterForm);
                    filterForm.onWindowResized(WindowManager.getWindow());
                });
                FormContentIconButton pasteButton = this.addComponent(new FormContentIconButton(buttonX -= 24, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}){

                    @Override
                    public boolean isActive() {
                        return super.isActive() && (Boolean)humanMob.selfManageEquipment.get() != false;
                    }
                });
                pasteButton.onClicked(e -> {
                    EquipmentFiltersForm.EquipmentFilterData equipmentFilterData = this.equipmentsForm.listClipboard.getValue();
                    if (equipmentFilterData != null) {
                        SaveData save = new SaveData("");
                        equipmentFilterData.filter.addSaveData(save);
                        data.preferArmorSets = equipmentFilterData.preferArmorSets;
                        data.equipmentFilter.applyLoadData(save.toLoadData());
                        ((SettlementContainer)this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendPreferArmorSets(mob.getUniqueID(), data.preferArmorSets);
                        ((SettlementContainer)this.equipmentsForm.container).setSettlerEquipmentFilter.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(data.equipmentFilter));
                        pasteButton.setActive(false);
                    }
                });
                pasteButton.setupDragPressOtherButtons("equipmentPasteButton");
                this.pasteButtons.add(new SettlementEquipmentForm.PasteButton(pasteButton, () -> data.preferArmorSets, data.equipmentFilter));
                FormContentIconButton copyButton = this.addComponent(new FormContentIconButton(buttonX -= 24, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        this.setActive((Boolean)humanMob.selfManageEquipment.get());
                        super.draw(tickManager, perspective, renderBox);
                    }
                });
                copyButton.onClicked(e -> {
                    EquipmentFiltersForm.EquipmentFilterData equipmentFilterData = new EquipmentFiltersForm.EquipmentFilterData(data.preferArmorSets, data.equipmentFilter);
                    WindowManager.getWindow().putClipboard(equipmentFilterData.getSaveData().getScript());
                    this.equipmentsForm.listClipboard.forceUpdate();
                });
                FormContentIconToggleButton selfManageButton = this.addComponent(new FormContentIconToggleButton(buttonX -= 24, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, this.getInterfaceStyle().button_escaped_20, new GameMessage[]{new LocalMessage("ui", "settlerselfmanagequipment")}){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        this.setToggled((Boolean)humanMob.selfManageEquipment.get());
                        super.draw(tickManager, perspective, renderBox);
                    }
                });
                selfManageButton.onToggled(e -> {
                    humanMob.selfManageEquipment.set(((FormButtonToggle)e.from).isToggled());
                    ((SettlementContainer)this.equipmentsForm.container).setSettlerSelfManageEquipment.runAndSend(mob.getUniqueID(), ((FormButtonToggle)e.from).isToggled());
                });
                selfManageButton.setupDragToOtherButtons("selfManageEquipment");
                buttonX -= 4;
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 5, ArmorItem.ArmorType.FEET, true);
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 4, ArmorItem.ArmorType.CHEST, true);
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 3, ArmorItem.ArmorType.HEAD, true);
                this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, buttonX -= 4, y + 4, 24, false));
                buttonX -= 4;
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 2, ArmorItem.ArmorType.FEET, false);
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 1, ArmorItem.ArmorType.CHEST, false);
                this.addArmorItemPreview(humanMob, buttonX -= 32, y, 0, ArmorItem.ArmorType.HEAD, false);
                this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, buttonX -= 4, y + 4, 24, false));
                buttonX -= 4;
                this.addWeaponItemPreview(humanMob, buttonX -= 32, y, 6);
                String settlerName = settlerMob.getSettlerName();
                this.addComponent(new FormSettlerIcon(5, y, data.settler, mob, this.equipmentsForm.containerForm));
                int namesX = 37;
                int namesWidth = buttonX - namesX;
                FontOptions nameFontOptions = new FontOptions(16);
                this.addComponent(new FormLabel(GameUtils.maxString(settlerName, nameFontOptions, namesWidth), nameFontOptions, -1, namesX, y, namesWidth));
                FontOptions settlerOptions = new FontOptions(12);
                this.addComponent(new FormLabel(GameUtils.maxString(data.settler.getGenericMobName(), settlerOptions, namesWidth), settlerOptions, -1, namesX, y + 16));
                continue;
            }
            ++settlersOutside;
        }
        this.equipmentsForm.listClipboard.forceUpdate();
        this.equipmentsForm.listClipboard.onUpdate(this.equipmentsForm.listClipboard.getValue());
        if (!hasAnySettlers) {
            this.alwaysShowVerticalScrollBar = false;
            flow.next(16);
            this.addComponent(flow.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
        } else {
            this.alwaysShowVerticalScrollBar = true;
        }
        if (settlersOutside > 0) {
            this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", "count", settlersOutside), new FontOptions(16), -1, 10, 0), 5));
        }
        this.contentHeight = Math.max(flow.next(), 70);
        this.equipmentsForm.updateSize();
        if (!this.equipmentsForm.settlers.isEmpty()) {
            lastScroll.load(this);
        }
    }

    private void addArmorItemPreview(final HumanMob mob, int x, int y, final int inventorySlot, final ArmorItem.ArmorType armorType, final boolean isCosmeticSlot) {
        final GameTexture emptyIcon = armorType == ArmorItem.ArmorType.HEAD ? (isCosmeticSlot ? this.getInterfaceStyle().inventoryslot_icon_hat : this.getInterfaceStyle().inventoryslot_icon_helmet) : (armorType == ArmorItem.ArmorType.CHEST ? (isCosmeticSlot ? this.getInterfaceStyle().inventoryslot_icon_shirt : this.getInterfaceStyle().inventoryslot_icon_chestplate) : (armorType == ArmorItem.ArmorType.FEET ? (isCosmeticSlot ? this.getInterfaceStyle().inventoryslot_icon_shoes : this.getInterfaceStyle().inventoryslot_icon_boots) : null));
        this.addComponent(new FormItemPreview(x, y, 32, null){

            @Override
            public InventoryItem getDrawItem(PlayerMob perspective) {
                return mob.equipmentInventory.getItem(inventorySlot);
            }

            @Override
            public void drawEmpty(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                if (emptyIcon != null) {
                    emptyIcon.initDraw().color(this.getInterfaceStyle().inactiveTextColor).draw(this.getX(), this.getY());
                }
            }

            @Override
            public boolean allowControllerFocus() {
                return true;
            }

            @Override
            public void addTooltips(InventoryItem item, PlayerMob perspective) {
                ListGameTooltips tooltips = new ListGameTooltips();
                if (item != null) {
                    if (item.item.isArmorItem()) {
                        GameBlackboard buffBlackboard = new GameBlackboard();
                        buffBlackboard.set("setItem", item);
                        GameBlackboard blackboard = new GameBlackboard().set("isCosmeticSlot", isCosmeticSlot).set("equippedMob", mob).set("setBonus", mob.equipmentBuffManager.getSetBonusBuffTooltip(buffBlackboard));
                        tooltips.add(item.item.getTooltips(item, perspective, blackboard));
                    } else {
                        tooltips.add(Localization.translate("ui", "settlercantuseitem"));
                    }
                } else if (armorType == ArmorItem.ArmorType.HEAD) {
                    tooltips.add(Localization.translate("itemtooltip", (isCosmeticSlot ? "cosmetic" : "") + "headslot"));
                } else if (armorType == ArmorItem.ArmorType.CHEST) {
                    tooltips.add(Localization.translate("itemtooltip", (isCosmeticSlot ? "cosmetic" : "") + "chestslot"));
                } else if (armorType == ArmorItem.ArmorType.FEET) {
                    tooltips.add(Localization.translate("itemtooltip", (isCosmeticSlot ? "cosmetic" : "") + "feetslot"));
                }
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
        });
    }

    private void addWeaponItemPreview(final HumanMob mob, int x, int y, final int inventorySlot) {
        this.addComponent(new FormItemPreview(x, y, 32, null){

            @Override
            public InventoryItem getDrawItem(PlayerMob perspective) {
                return mob.equipmentInventory.getItem(inventorySlot);
            }

            @Override
            public void drawEmpty(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.getInterfaceStyle().inventoryslot_icon_weapon.initDraw().color(this.getInterfaceStyle().inactiveTextColor).draw(this.getX(), this.getY());
            }

            @Override
            public boolean allowControllerFocus() {
                return true;
            }

            @Override
            public void addTooltips(InventoryItem item, PlayerMob perspective) {
                ListGameTooltips tooltips = new ListGameTooltips();
                if (item != null) {
                    if (item.item instanceof ItemAttackerWeaponItem) {
                        GameBlackboard blackboard = new GameBlackboard().set("perspective", mob);
                        tooltips.add(item.item.getTooltips(item, null, blackboard));
                    } else {
                        tooltips.add(Localization.translate("ui", "settlercantuseitem"));
                    }
                } else {
                    tooltips.add(Localization.translate("itemtooltip", "weaponslot"));
                }
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
        });
    }

    public void updatePasteButtons(EquipmentFiltersForm.EquipmentFilterData value) {
        for (SettlementEquipmentForm.PasteButton pasteButton : this.pasteButtons) {
            pasteButton.updateActive(value);
        }
    }

    @Override
    public void dispose() {
        lastScroll.save(this);
        super.dispose();
    }
}

