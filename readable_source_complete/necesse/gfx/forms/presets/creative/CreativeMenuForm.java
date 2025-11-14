/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.creative;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.TabbedFormPreset;
import necesse.gfx.forms.presets.creative.CreativeItemsTab;
import necesse.gfx.forms.presets.creative.CreativeSettingsTab;
import necesse.gfx.forms.presets.creative.CreativeTab;
import necesse.gfx.forms.presets.creative.CreativeToolsTab;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;

public class CreativeMenuForm
extends FormSwitcher {
    protected final MainGameFormManager formManager;
    protected FormComponentList creativeComponents;
    protected TabbedFormPreset tabForm;
    protected ArrayList<CreativeTab> tabs = new ArrayList();
    protected CreativeTab currentTab;
    private boolean hidden = false;
    protected boolean replaceInventory = false;

    public CreativeMenuForm(MainGameFormManager formManager, int width, int height, Client playerClient) {
        this.formManager = formManager;
        this.creativeComponents = this.addComponent(new FormComponentList());
        this.tabForm = this.creativeComponents.addComponent(new TabbedFormPreset(0, TabbedFormPreset.TabStyle.Fill, width, height));
        this.tabForm.onTabChanged(e -> {
            int tabIndex = ((TabbedFormPreset)e.from).getCurrentTabIndex();
            if (tabIndex < this.tabs.size()) {
                if (this.currentTab != null) {
                    this.currentTab.onTabUnfocused();
                }
                this.currentTab = this.tabs.get(tabIndex);
                this.currentTab.tabFocused();
            }
        });
        Form placeablesTabContent = this.tabForm.addLocalizedTab(new LocalMessage("ui", "creativeplaceablestab"), null);
        this.tabs.add(new CreativeItemsTab(placeablesTabContent, playerClient, "creativeplaceablestab", "tiles", "objects", "wiring"));
        this.currentTab = this.tabs.get(0);
        Form itemsTabContent = this.tabForm.addLocalizedTab(new LocalMessage("ui", "creativeitemstab"), null);
        this.tabs.add(new CreativeItemsTab(itemsTabContent, playerClient, "creativeitemstab", "equipment", "consumable", "materials", "misc"));
        Form mobsTab = this.tabForm.addLocalizedTab(new LocalMessage("ui", "creativemobstab"), null);
        this.tabs.add(new CreativeItemsTab(mobsTab, playerClient, "creativemobstab", "mobs"));
        Form settingsTab = this.tabForm.addLocalizedTab(new LocalMessage("ui", "creativesettingstab"), null);
        this.tabs.add(new CreativeSettingsTab(settingsTab, playerClient));
        Form toolsTab = this.tabForm.addLocalizedTab(new LocalMessage("ui", "creativetoolstab"), null);
        this.tabs.add(new CreativeToolsTab(toolsTab, playerClient));
        this.shouldReplaceInventory(false);
        this.makeCurrent(this.creativeComponents);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!event.isUsed() && this.checkForClickAndRemoveItem(perspective, event)) {
            event.use();
            return;
        }
        this.currentTab.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        this.currentTab.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.currentTab.updateBeforeDraw(tickManager);
        if (renderBox == null) {
            renderBox = this.getBoundingBox();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.hidden;
    }

    private boolean checkForClickAndRemoveItem(PlayerMob perspective, InputEvent event) {
        if (event.state && (event.getID() == -100 || event.getID() == -99) && !this.isHidden() && perspective != null && perspective.getDraggingItem() != null && this.isMouseOver(event)) {
            this.removeDragItem(perspective.getClient());
            return true;
        }
        return false;
    }

    private void removeDragItem(Client client) {
        ContainerActionResult result = client.getContainer().applyContainerAction(-1, ContainerAction.RIGHT_CLICK);
        client.network.sendPacket(new PacketContainerAction(-1, ContainerAction.RIGHT_CLICK, result.value));
    }

    public void shouldReplaceInventory(boolean replaceInventory) {
        this.replaceInventory = replaceInventory;
        if (replaceInventory) {
            this.tabForm.form.setPosition(new FormRelativePosition((FormPositionContainer)this.formManager.toolbar, (this.formManager.toolbar.getWidth() - this.tabForm.form.getWidth()) / 2, -this.tabForm.form.getHeight() - this.getInterfaceStyle().formSpacing));
        } else {
            this.tabForm.form.setPosition(new FormRelativePosition((FormPositionContainer)this.formManager.inventory, (this.formManager.inventory.getWidth() - this.tabForm.form.getWidth()) / 2, -this.tabForm.form.getHeight() - this.getInterfaceStyle().formSpacing));
        }
    }

    public boolean isReplacingInventory() {
        return this.replaceInventory && !this.isHidden();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        if (this.hidden && !hidden) {
            this.currentTab.tabFocused();
        } else if (!this.hidden && hidden) {
            this.currentTab.onTabUnfocused();
        }
        this.hidden = hidden;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (CreativeTab tab : this.tabs) {
            tab.dispose();
        }
    }
}

