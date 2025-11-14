/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTabContentComponent;
import necesse.gfx.forms.components.FormTabTextComponent;
import necesse.gfx.forms.components.localComponents.FormTabLocalTextComponent;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;

public class TabbedFormPreset
extends FormComponentList {
    public final Form form;
    protected TabStyle tabStyle;
    protected final int defaultWidth;
    protected final int defaultHeight;
    protected final int tabSpacing;
    protected final FormSwitcher formSwitcher;
    protected final List<TabData> tabs;
    protected int currentTabIndex = -1;
    protected boolean tabsDirty = true;
    protected FormEventsHandler<FormEvent<TabbedFormPreset>> tabChangedEvents = new FormEventsHandler();

    public TabbedFormPreset(int tabSpacing, TabStyle tabStyle) {
        this(tabSpacing, tabStyle, 0, 0);
    }

    public TabbedFormPreset(int tabSpacing, TabStyle tabStyle, int defaultWidth, int defaultHeight) {
        this.tabStyle = tabStyle;
        this.tabSpacing = tabSpacing;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.form = this.addComponent(new Form(defaultWidth, defaultHeight));
        this.formSwitcher = this.form.addComponent(new FormSwitcher());
        this.tabs = new ArrayList<TabData>();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.tabsDirty) {
            this.recalculateTabPositionAndWidth();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public Form addTab(String text, String tooltip) {
        return this.addTab(text, tooltip, this.defaultWidth, this.defaultHeight);
    }

    public Form addTab(String text, String tooltip, int width, int height) {
        FormTabTextComponent tab = this.addComponent(new FormTabTextComponent(text, tooltip, this.form, 0, FormInputSize.SIZE_20, 0){

            @Override
            public boolean isSelected() {
                return !TabbedFormPreset.this.tabs.isEmpty() && TabbedFormPreset.this.currentTabIndex >= 0 && TabbedFormPreset.this.tabs.get((int)TabbedFormPreset.this.currentTabIndex).tab == this;
            }
        });
        return this.addTab(tab, width, height);
    }

    public Form addLocalizedTab(GameMessage text, GameMessage tooltip) {
        return this.addLocalizedTab(text, tooltip, this.defaultWidth, this.defaultHeight);
    }

    public Form addLocalizedTab(GameMessage text, GameMessage tooltip, int width, int height) {
        FormTabLocalTextComponent tab = this.addComponent(new FormTabLocalTextComponent(text, tooltip, this.form, 0, FormInputSize.SIZE_20){

            @Override
            public boolean isSelected() {
                return !TabbedFormPreset.this.tabs.isEmpty() && TabbedFormPreset.this.currentTabIndex >= 0 && TabbedFormPreset.this.tabs.get((int)TabbedFormPreset.this.currentTabIndex).tab == this;
            }
        });
        return this.addTab(tab, width, height);
    }

    protected Form addTab(FormTabContentComponent tab, int width, int height) {
        TabData tabData = new TabData(tab, this.formSwitcher, width, height);
        this.tabs.add(tabData);
        tab.onClicked(e -> this.selectTab(this.tabs.indexOf(tabData)));
        tab.onWantedWidthChanged(newWidth -> {
            this.tabsDirty = true;
        });
        if (this.tabs.size() == 1) {
            this.selectTab(0);
        }
        this.tabsDirty = true;
        return tabData.content;
    }

    protected void recalculateTabPositionAndWidth() {
        int wantedTotalTabWidth = this.tabs.stream().reduce(0, (sum, tab) -> sum + tab.tab.getWantedWidth(), Integer::sum);
        int maxTotalTabWidth = this.form.getWidth() - (2 + this.tabSpacing) * (this.tabs.size() - 1);
        FormTabContentComponent[] sortedTabs = (FormTabContentComponent[])this.tabs.stream().sorted(Comparator.comparing(o -> o.tab.getWantedWidth(), Comparator.reverseOrder())).map(tabData -> tabData.tab).toArray(FormTabContentComponent[]::new);
        for (int i = 0; i < sortedTabs.length; ++i) {
            FormTabContentComponent tab2 = sortedTabs[i];
            if (this.tabStyle == TabStyle.Fill) {
                int tabSize = maxTotalTabWidth / this.tabs.size();
                int newTotalTabSize = tabSize * sortedTabs.length;
                int missingSpace = maxTotalTabWidth - newTotalTabSize;
                if (i < missingSpace) {
                    ++tabSize;
                }
                tab2.setWidth(tabSize);
                wantedTotalTabWidth = maxTotalTabWidth;
                continue;
            }
            tab2.setWidth(tab2.getWantedWidth());
        }
        if (wantedTotalTabWidth > maxTotalTabWidth) {
            int savingsNeeded = wantedTotalTabWidth - maxTotalTabWidth;
            block5: for (int i = 0; i < sortedTabs.length && savingsNeeded > 0; ++i) {
                int currenTabWidth = sortedTabs[i].getWidth();
                int nextTabWidth = i < sortedTabs.length - 1 ? sortedTabs[i + 1].getWidth() : 1;
                int possibleSavings = (currenTabWidth - nextTabWidth) * (i + 1);
                int neededToSavePerTab = GameMath.ceil((float)Math.min(savingsNeeded, possibleSavings) / ((float)i + 1.0f));
                int newTabWidth = currenTabWidth - neededToSavePerTab;
                int missingSpace = neededToSavePerTab * (i + 1) - savingsNeeded;
                for (int j = 0; j <= i; ++j) {
                    int extra = j < missingSpace ? 1 : 0;
                    sortedTabs[j].setWidth(newTabWidth + extra);
                    if ((savingsNeeded -= neededToSavePerTab - extra) <= 0) continue block5;
                }
            }
        }
        int nextTabX = 0;
        int totalTabWidth = GameMath.min(maxTotalTabWidth, wantedTotalTabWidth);
        switch (this.tabStyle) {
            case Right: {
                nextTabX = maxTotalTabWidth - totalTabWidth;
                break;
            }
            case Center: {
                nextTabX = (maxTotalTabWidth - totalTabWidth) / 2;
            }
        }
        for (TabData tab3 : this.tabs) {
            tab3.tab.setX(nextTabX);
            nextTabX += tab3.tab.getWidth() + 2 + this.tabSpacing;
        }
        this.tabsDirty = false;
    }

    public void selectTab(int index) {
        if (index < 0 || index >= this.tabs.size() || index == this.currentTabIndex) {
            return;
        }
        this.currentTabIndex = index;
        FormTabContentComponent newTab = this.tabs.get((int)this.currentTabIndex).tab;
        Form newContent = this.tabs.get((int)this.currentTabIndex).content;
        this.formSwitcher.makeCurrent(newContent);
        this.form.setWidth(newContent.getWidth());
        this.form.setHeight(newContent.getHeight());
        for (int i = 0; i < this.tabs.size(); ++i) {
            this.tabs.get((int)i).tab.zIndex = i;
        }
        newTab.zIndex = this.tabs.size();
        this.tabChangedEvents.onEvent(new FormEvent<TabbedFormPreset>(this));
    }

    public int getCurrentTabIndex() {
        return this.currentTabIndex;
    }

    public int getTabCount() {
        return this.tabs.size();
    }

    public TabbedFormPreset onTabChanged(FormEventListener<FormEvent<TabbedFormPreset>> listener) {
        this.tabChangedEvents.addListener(listener);
        return this;
    }

    public static enum TabStyle {
        Left,
        Right,
        Center,
        Fill;

    }

    protected static class TabData {
        public final FormTabContentComponent tab;
        public final Form content;

        public TabData(FormTabContentComponent tab, FormSwitcher formSwitcher, int width, int height) {
            this.tab = tab;
            this.content = new Form(width, height);
            this.content.drawBase = false;
            formSwitcher.addComponent(this.content);
        }
    }
}

