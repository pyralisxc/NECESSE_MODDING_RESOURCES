/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSettlersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietFilterForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietsForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.container.settlement.data.SettlementSettlerDietsData;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementDietsContentBox
extends FormContentBox {
    public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
    public final SettlementDietsForm<?> dietsForm;
    public ArrayList<SettlementDietsForm.PasteButton> pasteButtons = new ArrayList();
    public int contentHeight;

    public SettlementDietsContentBox(SettlementDietsForm<?> dietsForm, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.dietsForm = dietsForm;
    }

    public void updateDietsContent() {
        this.clearComponents();
        this.pasteButtons.clear();
        FormFlow flow = new FormFlow(0);
        boolean hasAnySettlers = false;
        int settlersOutside = 0;
        Comparator<SettlementSettlerData> comparing = Comparator.comparing(s -> s.settler.getID());
        comparing = comparing.thenComparing(s -> s.mobUniqueID);
        this.dietsForm.settlers.sort(comparing);
        for (SettlementSettlerDietsData data : this.dietsForm.settlers) {
            SettlerMob settlerMob = data.getSettlerMob(this.dietsForm.client.getLevel());
            if (settlerMob != null) {
                final Mob mob = settlerMob.getMob();
                if (!(mob instanceof EntityJobWorker)) continue;
                hasAnySettlers = true;
                int padding = SettlementSettlersForm.SETTLER_LIST_PADDING;
                int height = 32 + padding * 2;
                int startY = flow.next(height);
                int y = startY + padding;
                int dietButtonWidth = 150;
                int buttonX = this.getWidth() - dietButtonWidth - this.getScrollBarWidth() - 2;
                this.addComponent(new FormLocalTextButton("ui", "settlementchangediet", buttonX, y + 3, dietButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
                    SettlementDietFilterForm filterForm = new SettlementDietFilterForm("changediet", 408, 250, mob, data.dietFilter, this.dietsForm.client){

                        @Override
                        public void onItemsChanged(Item[] items, boolean allowed) {
                            ((SettlementContainer)SettlementDietsContentBox.this.dietsForm.container).setSettlerDiet.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                        }

                        @Override
                        public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                            ((SettlementContainer)SettlementDietsContentBox.this.dietsForm.container).setSettlerDiet.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                        }

                        @Override
                        public void onFullChange(ItemCategoriesFilter filter) {
                            ((SettlementContainer)SettlementDietsContentBox.this.dietsForm.container).setSettlerDiet.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(filter));
                        }

                        @Override
                        public void onButtonPressed() {
                            SettlementDietsContentBox.this.dietsForm.makeCurrent(SettlementDietsContentBox.this.dietsForm.mainForm);
                        }

                        @Override
                        public void onWindowResized(GameWindow window) {
                            super.onWindowResized(window);
                            ContainerComponent.setPosInventory(this);
                        }
                    };
                    this.dietsForm.addAndMakeCurrentTemporary(filterForm);
                    filterForm.onWindowResized(WindowManager.getWindow());
                });
                FormContentIconButton pasteButton = this.addComponent(new FormContentIconButton(buttonX -= 24, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
                pasteButton.onClicked(e -> {
                    SettlementDietFilterForm.DietData dietData = this.dietsForm.listClipboard.getValue();
                    if (dietData != null) {
                        SaveData save = new SaveData("");
                        dietData.filter.addSaveData(save);
                        data.dietFilter.applyLoadData(save.toLoadData());
                        ((SettlementContainer)this.dietsForm.container).setSettlerDiet.runAndSendChange(mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(data.dietFilter));
                        pasteButton.setActive(false);
                    }
                });
                pasteButton.setupDragPressOtherButtons("dietPasteButton");
                this.pasteButtons.add(new SettlementDietsForm.PasteButton(pasteButton, data.dietFilter));
                this.addComponent(new FormContentIconButton(buttonX -= 24, y + 3, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
                    SettlementDietFilterForm.DietData dietData = new SettlementDietFilterForm.DietData(data.dietFilter);
                    WindowManager.getWindow().putClipboard(dietData.getSaveData().getScript());
                    this.dietsForm.listClipboard.forceUpdate();
                });
                String settlerName = settlerMob.getSettlerName();
                this.addComponent(new FormSettlerIcon(5, y, data.settler, mob, this.dietsForm.containerForm));
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
        this.dietsForm.listClipboard.forceUpdate();
        this.dietsForm.listClipboard.onUpdate(this.dietsForm.listClipboard.getValue());
        if (!hasAnySettlers) {
            this.alwaysShowVerticalScrollBar = false;
            flow.next(16);
            this.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementnoeatingsettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
        } else {
            this.alwaysShowVerticalScrollBar = true;
        }
        if (settlersOutside > 0) {
            this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", "count", settlersOutside), new FontOptions(16), -1, 10, 0), 5));
        }
        this.contentHeight = Math.max(flow.next(), 70);
        this.dietsForm.updateSize();
        if (!this.dietsForm.settlers.isEmpty()) {
            lastScroll.load(this);
        }
    }

    public void updatePasteButtons(SettlementDietFilterForm.DietData value) {
        for (SettlementDietsForm.PasteButton pasteButton : this.pasteButtons) {
            pasteButton.updateActive(value);
        }
    }

    @Override
    public void dispose() {
        lastScroll.save(this);
        super.dispose();
    }
}

