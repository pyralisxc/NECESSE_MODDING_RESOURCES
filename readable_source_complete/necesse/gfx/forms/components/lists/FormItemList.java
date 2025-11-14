/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public abstract class FormItemList
extends FormGeneralGridList<ItemElement> {
    protected LinkedList<InventoryItem> allItems;
    protected Predicate<InventoryItem> filter = null;
    public UpdateMode updateMode;
    private Thread filterUpdater;
    protected boolean sorted = true;

    public FormItemList(int x, int y, int width, int height, UpdateMode updateMode) {
        super(x, y, width, height, 36, 36);
        this.updateMode = updateMode;
        this.reset();
    }

    @Override
    public void reset() {
        this.allItems = new LinkedList();
        this.resetScroll();
    }

    public void populateIfNotAlready() {
        if (this.allItems.isEmpty()) {
            this.addAllItems(this.allItems);
            this.setFilter(this.filter, false);
            this.resetScroll();
        }
    }

    public abstract void addAllItems(List<InventoryItem> var1);

    public void setFilter(Predicate<InventoryItem> filter) {
        this.setFilter(filter, true);
    }

    private void setFilter(Predicate<InventoryItem> filter, boolean limitScroll) {
        this.filter = filter;
        this.updateList(limitScroll);
    }

    public void setSorted(boolean sorted) {
        if (this.sorted != sorted) {
            this.sorted = sorted;
            this.updateList(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void updateList(final boolean limitScroll) {
        if (this.filterUpdater != null) {
            this.filterUpdater.interrupt();
            this.filterUpdater = null;
        }
        final Predicate<InventoryItem> finalFilter = this.filter;
        if (this.updateMode == UpdateMode.CONCURRENT_CONTINUOUS) {
            this.elements = new ArrayList();
            final int lastScroll = this.scroll;
            this.filterUpdater = new Thread("item-list-filter-continuous"){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    super.run();
                    for (InventoryItem invItem : FormItemList.this.allItems) {
                        if (this.isInterrupted()) {
                            return;
                        }
                        if (finalFilter == null || finalFilter.test(invItem)) {
                            FormItemList formItemList;
                            if (FormItemList.this.sorted) {
                                formItemList = FormItemList.this;
                                synchronized (formItemList) {
                                    GameUtils.insertSortedList(FormItemList.this.elements, new ItemElement(invItem), Comparator.comparing(i -> i.item));
                                }
                            }
                            formItemList = FormItemList.this;
                            synchronized (formItemList) {
                                FormItemList.this.elements.add(new ItemElement(invItem));
                            }
                        }
                        if (!limitScroll) continue;
                        FormItemList.this.scroll = lastScroll;
                        FormItemList.this.limitMaxScroll();
                    }
                }
            };
            this.filterUpdater.start();
        } else if (this.updateMode == UpdateMode.CONCURRENT_FULL) {
            this.filterUpdater = new Thread("item-list-filter-wait"){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    super.run();
                    ArrayList<ItemElement> list = new ArrayList<ItemElement>(FormItemList.this.allItems.size());
                    for (InventoryItem invItem : FormItemList.this.allItems) {
                        if (this.isInterrupted()) {
                            return;
                        }
                        if (finalFilter != null && !finalFilter.test(invItem)) continue;
                        list.add(new ItemElement(invItem));
                    }
                    FormItemList formItemList = FormItemList.this;
                    synchronized (formItemList) {
                        if (this.isInterrupted()) {
                            return;
                        }
                        if (FormItemList.this.sorted) {
                            list.sort(Comparator.comparing(i -> i.item));
                        }
                        FormItemList.this.elements = new ArrayList();
                        FormItemList.this.elements.addAll(list);
                        if (limitScroll) {
                            FormItemList.this.limitMaxScroll();
                        }
                    }
                }
            };
            this.filterUpdater.start();
        } else {
            FormItemList formItemList = this;
            synchronized (formItemList) {
                this.elements = new ArrayList();
                for (InventoryItem invItem : this.allItems) {
                    if (finalFilter != null && !finalFilter.test(invItem)) continue;
                    if (this.sorted) {
                        GameUtils.insertSortedList(this.elements, new ItemElement(invItem), Comparator.comparing(i -> i.item));
                        continue;
                    }
                    this.elements.add(new ItemElement(invItem));
                }
                if (this.sorted) {
                    this.elements.sort(Comparator.comparing(i -> i.item));
                }
                if (limitScroll) {
                    this.limitMaxScroll();
                }
            }
        }
    }

    public abstract void onItemClicked(InventoryItem var1, InputEvent var2);

    public void addTooltips(InventoryItem item, PlayerMob perspective) {
        GameTooltipManager.addTooltip(item.getTooltip(perspective, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
    }

    public static enum UpdateMode {
        CONCURRENT_CONTINUOUS,
        CONCURRENT_FULL,
        WAIT_FULl;

    }

    public class ItemElement
    extends FormListGridElement<FormItemList> {
        public final InventoryItem item;

        public ItemElement(InventoryItem item) {
            this.item = item;
        }

        @Override
        protected void draw(FormItemList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            if (this.isMouseOver(parent)) {
                FormItemList.this.getInterfaceStyle().inventoryslot_small.highlighted.initDraw().color(FormItemList.this.getInterfaceStyle().highlightElementColor).draw(2, 2);
                FormItemList.this.addTooltips(this.item, perspective);
            } else {
                FormItemList.this.getInterfaceStyle().inventoryslot_small.active.initDraw().color(FormItemList.this.getInterfaceStyle().activeElementColor).draw(2, 2);
            }
            this.item.draw(perspective, 2, 2);
        }

        @Override
        protected void onClick(FormItemList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            FormItemList.this.onItemClicked(this.item, event);
        }

        @Override
        protected void onControllerEvent(FormItemList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormItemList.this.onItemClicked(this.item, InputEvent.ControllerButtonEvent(event, tickManager));
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

