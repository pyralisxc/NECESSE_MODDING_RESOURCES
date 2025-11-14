/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormContainerCraftingList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.CraftFilterForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.RecipeFilter;
import necesse.level.maps.hudManager.HudDrawElement;

public class CraftingStationContainerFormOld<T extends CraftingStationContainer>
extends ContainerFormSwitcher<T> {
    public Form craftingForm = this.addComponent(new Form(408, Settings.hasCraftingListExpanded.get() != false ? 300 : 180));
    public SettlementObjectStatusFormManager settlementObjectFormManager;
    protected FormContainerCraftingList craftList;
    protected FormLocalCheckBox onlyCraftable;
    protected FormLocalCheckBox useNearby;
    protected FormContentIconToggleButton filterButton;
    protected CraftFilterForm filterForm;
    protected HudDrawElement rangeElement;

    public CraftingStationContainerFormOld(Client client, T container) {
        super(client, container);
        this.craftingForm.addComponent(new FormLocalLabel(((CraftingStationContainer)container).header, new FontOptions(20), -1, 5, 5));
        this.craftList = this.craftingForm.addComponent(new FormContainerCraftingList(0, 24, this.craftingForm.getWidth(), this.craftingForm.getHeight() - 50, client, false, false, ((CraftingStationContainer)container).techs));
        this.onlyCraftable = this.craftingForm.addComponent(new FormLocalCheckBox("ui", "filteronlycraftable", 5, this.craftingForm.getHeight() - 32 - 6, (boolean)Settings.craftingListOnlyCraftable.get()), 100);
        this.onlyCraftable.onClicked(e -> {
            Settings.craftingListOnlyCraftable.set(((FormCheckBox)e.from).checked);
            Settings.saveClientSettings();
        });
        Settings.craftingListOnlyCraftable.addChangeListener(v -> {
            this.onlyCraftable.checked = v;
            this.craftList.setOnlyCraftable((boolean)v);
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.craftList.setShowHidden(true);
        this.craftList.setOnlyCraftable(this.onlyCraftable.checked);
        this.useNearby = this.craftingForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, this.craftingForm.getHeight() - 16 - 4, Settings.craftingUseNearby.get()){

            @Override
            public GameTooltips getTooltip() {
                return new StringTooltips().add(Localization.translate("ui", "usenearbyinvtip"), 400);
            }
        }, 100);
        Settings.craftingUseNearby.addChangeListener(v -> {
            this.useNearby.checked = v;
            GlobalData.updateCraftable();
        }, this::isDisposed);
        this.useNearby.onClicked(e -> Settings.craftingUseNearby.set(((FormCheckBox)e.from).checked));
        int craftingObjectID = ((CraftingStationContainer)container).craftingStationObject.getCraftingObjectID();
        RecipeFilter filter = Settings.getRecipeFilterSetting(ObjectRegistry.getObject(craftingObjectID));
        this.craftList.setFilter(filter);
        FormFlow iconFlow = new FormFlow(this.craftingForm.getWidth() - 4);
        this.filterButton = this.craftingForm.addComponent(new FormContentIconToggleButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_search_24, this.getInterfaceStyle().button_search_24, new LocalMessage("ui", "craftfilters")));
        this.filterButton.useDownTexture = false;
        this.filterButton.onToggled(e -> {
            Settings.hasCraftingFilterExpanded.set(((FormButtonToggle)e.from).isToggled());
            Settings.saveClientSettings();
        });
        this.filterButton.setToggled(Settings.hasCraftingFilterExpanded.get());
        FormContentIconToggleButton expandButton = this.craftingForm.addComponent(new FormContentIconToggleButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_expanded_24, this.getInterfaceStyle().button_expanded_24, new GameMessage[0]){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                if (this.isToggled()) {
                    GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("ui", "collapse")), TooltipLocation.FORM_FOCUS);
                } else {
                    GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("ui", "expand")), TooltipLocation.FORM_FOCUS);
                }
            }
        }.onMirrorY());
        expandButton.useDownTexture = false;
        expandButton.onToggled(e -> {
            Settings.hasCraftingListExpanded.set(((FormButtonToggle)e.from).isToggled());
            Settings.saveClientSettings();
            this.getManager().setNextControllerFocus(expandButton);
            ControllerInput.submitNextRefreshFocusEvent();
        });
        expandButton.setToggled(Settings.hasCraftingListExpanded.get());
        Settings.hasCraftingListExpanded.addChangeListener(v -> {
            this.craftingForm.setHeight(v != false ? 300 : 180);
            this.craftList.setHeight(this.craftingForm.getHeight() - 46);
            this.onlyCraftable.setPosition(5, this.craftingForm.getHeight() - 32);
            this.useNearby.setPosition(5, this.craftingForm.getHeight() - 16);
            this.onWindowResized(WindowManager.getWindow());
        }, this::isDisposed);
        this.settlementObjectFormManager = ((CraftingStationContainer)container).settlementObjectManager.getFormManager(this, this.craftingForm, client);
        this.settlementObjectFormManager.addConfigButtonRow(this.craftingForm, iconFlow, 4, -1);
        this.makeCurrent(this.craftingForm);
        this.filterForm = new CraftFilterForm("filtersForm", 148, 0, 180, filter);
        this.filterForm.setPosition(new FormRelativePosition((FormPositionContainer)this.craftingForm, this.craftingForm.getWidth() + this.getInterfaceStyle().formSpacing, 0));
        this.filterForm.setHidden(!this.filterButton.isToggled());
    }

    @Override
    protected void init() {
        super.init();
        this.getManager().addComponent(this.filterForm);
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
        this.rangeElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!CraftingStationContainerFormOld.this.useNearby.isHovering()) {
                    return;
                }
                final SharedTextureDrawOptions options = ((CraftingStationContainer)((CraftingStationContainerFormOld)CraftingStationContainerFormOld.this).container).range.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((CraftingStationContainer)((CraftingStationContainerFormOld)CraftingStationContainerFormOld.this).container).objectX, ((CraftingStationContainer)((CraftingStationContainerFormOld)CraftingStationContainerFormOld.this).container).objectY, camera);
                if (options != null) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    });
                }
            }
        };
        this.client.getLevel().hudManager.addElement(this.rangeElement);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isCurrent(this.craftingForm) && event.state && WindowManager.getWindow().isKeyDown(341) && event.getID() == 70) {
            event.use();
            this.filterButton.setToggled(true);
            Settings.hasCraftingFilterExpanded.set(true);
            this.filterForm.selectAllTextAndSetTypingTrue();
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.craftingForm);
        this.settlementObjectFormManager.onWindowResized();
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.filterForm.setHidden(!this.isCurrent(this.craftingForm) || Settings.hasCraftingFilterExpanded.get() == false);
        this.settlementObjectFormManager.updateButtons();
        if (this.useNearby.isHovering()) {
            Renderer.hudManager.fadeHUD();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.filterForm != null) {
            this.getManager().removeComponent(this.filterForm);
            this.filterForm = null;
        }
        Settings.hasCraftingListExpanded.cleanListeners();
        Settings.hasCraftingFilterExpanded.cleanListeners();
        if (this.rangeElement != null) {
            this.rangeElement.remove();
        }
    }
}

