/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.util.IntRange;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentListTyped;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementPrivateForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlersToggleFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.missionBoard.AllMissionsUpdateEvent;
import necesse.inventory.container.object.missionBoard.AvailableExpeditionSettlersResponseEvent;
import necesse.inventory.container.object.missionBoard.DeletedMissionUpdateEvent;
import necesse.inventory.container.object.missionBoard.MissionBoardContainer;
import necesse.inventory.container.object.missionBoard.MissionBoardSlotsUpdateEvent;
import necesse.inventory.container.object.missionBoard.NetworkMissionBoardMission;
import necesse.inventory.container.object.missionBoard.SingleMissionUpdateEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerListEvent;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionRegistry;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class MissionBoardContainerForm<T extends MissionBoardContainer>
extends ContainerFormSwitcher<T> {
    public int typeWidth = 250;
    public int settlersWidth = 150;
    public int priceWidth = 150;
    public int successRateWidth = 150;
    public int conditionsWidth = 250;
    public int configButtonsWidth = 50;
    protected SettlementPrivateForm privateForm;
    protected Form missionBoardForm;
    protected FormContentBox missionBoardContent;
    protected int missionBoardContentStartY;
    protected ArrayList<FormBreakLine> verticalBreakLines = new ArrayList();
    protected FormComponentListTyped<FormBreakLine> horizontalBreakLines;
    protected FormLocalLabel emptyListLabel;
    protected ArrayList<ExpeditionForm> expeditionForms = new ArrayList();
    protected int currentConditionMissionUniqueID = -1;
    protected Form conditionConfigForm;
    protected ArrayList<Runnable> conditionConfigUpdateListeners = new ArrayList();
    protected FormLabel missionSlotsLabel;
    protected FormLocalTextButton addNewMissionButton;
    protected FormLocalTextButton buyMoreSlotsButton;
    public Form newMissionSelect;
    protected int requestedAvailableSettlersMissionUniqueID = -1;
    protected FormComponent requestedAvailableSettlersFrom;
    protected InputEvent requestedAvailableSettlersEvent;

    public MissionBoardContainerForm(Client client, T container) {
        super(client, container);
        int fullWidth = this.typeWidth + this.settlersWidth + this.priceWidth + this.successRateWidth + this.conditionsWidth + this.configButtonsWidth;
        this.privateForm = this.addComponent(new SettlementPrivateForm(((MissionBoardContainer)container).settlementData, ((MissionBoardContainer)container).requestJoinSettlementAction));
        this.missionBoardForm = this.addComponent(new Form("missionBoard", fullWidth, 400));
        FormFlow flow = new FormFlow(10);
        this.missionBoardForm.addComponent(flow.nextY(new FormLocalLabel("ui", "missionboardheader", new FontOptions(32), 0, this.missionBoardForm.getWidth() / 2, 0), 5));
        this.missionBoardForm.addComponent(new FormContentIconButton(this.missionBoardForm.getWidth() - 28, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "closebutton"))).onClicked(e -> client.closeContainer(true));
        int contentY = flow.next();
        this.missionBoardContent = this.missionBoardForm.addComponent(new FormContentBox(0, contentY, this.missionBoardForm.getWidth(), this.missionBoardForm.getHeight() - contentY));
        this.updateMissionBoardFull();
        this.newMissionSelect = this.addComponent(new Form("newMissionSelect", 0, 0));
        this.conditionConfigForm = this.addComponent(new Form("conditionConfig", 0, 0));
        this.makeCurrent(this.missionBoardForm);
        this.updatePrivateFormActive();
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    protected void init() {
        super.init();
        ((MissionBoardContainer)this.container).onEvent(SettlementDataEvent.class, e -> {
            this.privateForm.updateContent((SettlementDataEvent)e);
            this.updatePrivateFormActive();
        });
        ((MissionBoardContainer)this.container).onEvent(SettlementSettlerListEvent.class, e -> {
            for (ExpeditionForm form : this.expeditionForms) {
                form.updateContent(false);
            }
        });
        ((MissionBoardContainer)this.container).onEvent(MissionBoardSlotsUpdateEvent.class, e -> this.updateMissionSlotsContent());
        ((MissionBoardContainer)this.container).onEvent(AllMissionsUpdateEvent.class, e -> {
            this.updateMissionBoardFull();
            if (this.isCurrent(this.conditionConfigForm)) {
                NetworkMissionBoardMission newMission = ((MissionBoardContainer)this.container).missionBoardMissions.stream().filter(m -> m.uniqueID == this.currentConditionMissionUniqueID).findFirst().orElse(null);
                if (newMission != null) {
                    this.setupConditionConfig(newMission);
                } else {
                    this.makeCurrent(this.missionBoardForm);
                }
            }
        });
        ((MissionBoardContainer)this.container).onEvent(SingleMissionUpdateEvent.class, e -> {
            boolean updateOrder = false;
            boolean found = false;
            for (int i = 0; i < this.expeditionForms.size(); ++i) {
                ExpeditionForm expeditionForm = this.expeditionForms.get(i);
                if (expeditionForm.mission.uniqueID != e.mission.uniqueID) continue;
                expeditionForm.mission = e.mission;
                if (e.slot != -1 && i != e.slot) {
                    updateOrder = true;
                }
                expeditionForm.updateContent(false);
                found = true;
                break;
            }
            if (!found) {
                updateOrder = true;
                ExpeditionForm form = new ExpeditionForm(this.missionBoardContent.getWidth() - this.missionBoardContent.getScrollBarWidth(), e.slot, e.mission);
                this.missionBoardContent.addComponent(form);
                this.expeditionForms.add(form);
            }
            if (updateOrder) {
                this.updateMissionsOrder();
            }
            if (this.isCurrent(this.conditionConfigForm) && this.currentConditionMissionUniqueID == e.mission.uniqueID) {
                for (Runnable listener : this.conditionConfigUpdateListeners) {
                    listener.run();
                }
            }
        });
        ((MissionBoardContainer)this.container).onEvent(DeletedMissionUpdateEvent.class, e -> {
            for (int i = 0; i < this.expeditionForms.size(); ++i) {
                ExpeditionForm expeditionForm = this.expeditionForms.get(i);
                if (expeditionForm.mission.uniqueID != e.missionUniqueID) continue;
                this.missionBoardContent.removeComponent(expeditionForm);
                this.expeditionForms.remove(i);
                this.updateMissionsOrder();
                break;
            }
            if (this.isCurrent(this.conditionConfigForm) && e.missionUniqueID == this.currentConditionMissionUniqueID) {
                this.makeCurrent(this.missionBoardForm);
            }
        });
        ((MissionBoardContainer)this.container).onEvent(AvailableExpeditionSettlersResponseEvent.class, e -> {
            NetworkMissionBoardMission mission;
            if (this.requestedAvailableSettlersMissionUniqueID == e.missionUniqueID && (mission = (NetworkMissionBoardMission)((MissionBoardContainer)this.container).missionBoardMissions.stream().filter(m -> m.uniqueID == e.missionUniqueID).findFirst().orElse(null)) != null) {
                SettlersToggleFloatMenu menu = new SettlersToggleFloatMenu(this.requestedAvailableSettlersFrom, 300, 300, this.client.getLevel(), e.validSettlerUniqueIDs, mission.allSettlersAssigned, mission.assignedSettlers){

                    @Override
                    public void onAllSettlersToggled(boolean toggled) {
                        ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).assignSettlerAction.runAndSend(mission.uniqueID, -1, toggled);
                    }

                    @Override
                    public void onSettlerToggled(int mobUniqueID, boolean toggled) {
                        ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).assignSettlerAction.runAndSend(mission.uniqueID, mobUniqueID, toggled);
                    }
                };
                if (this.requestedAvailableSettlersEvent.isControllerEvent() || this.requestedAvailableSettlersEvent.wasControllerEvent()) {
                    this.getManager().openFloatMenu(menu);
                } else {
                    this.getManager().openFloatMenu((FloatMenu)menu, this.requestedAvailableSettlersFrom, this.requestedAvailableSettlersEvent);
                }
            }
            this.requestedAvailableSettlersMissionUniqueID = -1;
            this.requestedAvailableSettlersFrom = null;
            this.requestedAvailableSettlersEvent = null;
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if (event.state && event.getID() == 256 && (this.isCurrent(this.newMissionSelect) || this.isCurrent(this.conditionConfigForm))) {
            this.refreshExpeditionsForms();
            this.updateMissionsOrder();
            this.makeCurrent(this.missionBoardForm);
            event.use();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && event.buttonState && (this.isCurrent(this.newMissionSelect) || this.isCurrent(this.conditionConfigForm))) {
            this.refreshExpeditionsForms();
            this.updateMissionsOrder();
            this.makeCurrent(this.missionBoardForm);
            event.use();
        }
    }

    public void updateMissionsOrder() {
        FormFlow flow = new FormFlow(this.missionBoardContentStartY);
        int sectionHeight = flow.next();
        if (!((MissionBoardContainer)this.container).missionBoardMissions.isEmpty() && this.emptyListLabel != null) {
            this.missionBoardContent.removeComponent(this.emptyListLabel);
            this.emptyListLabel = null;
        } else if (((MissionBoardContainer)this.container).missionBoardMissions.isEmpty() && this.emptyListLabel == null) {
            flow.next(10);
            this.emptyListLabel = this.missionBoardContent.addComponent(flow.nextY(new FormLocalLabel("ui", "missionboardempty", new FontOptions(16), 0, this.missionBoardContent.getWidth() / 2, 0, this.missionBoardContent.getWidth() - 20), 5));
        }
        for (ExpeditionForm expeditionForm : this.expeditionForms) {
            boolean found = false;
            for (int i = 0; i < ((MissionBoardContainer)this.container).missionBoardMissions.size(); ++i) {
                if (((MissionBoardContainer)this.container).missionBoardMissions.get((int)i).uniqueID != expeditionForm.mission.uniqueID) continue;
                expeditionForm.slot = i;
                found = true;
                break;
            }
            if (found) continue;
            this.updateMissionBoardFull();
            break;
        }
        ArrayList<ExpeditionForm> newOrder = new ArrayList<ExpeditionForm>();
        for (ExpeditionForm expeditionForm : this.expeditionForms) {
            if (expeditionForm.slot >= newOrder.size()) {
                newOrder.add(expeditionForm);
                continue;
            }
            newOrder.add(expeditionForm.slot, expeditionForm);
        }
        this.expeditionForms = newOrder;
        this.horizontalBreakLines.clearComponents();
        boolean bl = true;
        for (ExpeditionForm expeditionForm : this.expeditionForms) {
            boolean bl2;
            if (!bl2) {
                this.horizontalBreakLines.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, flow.next(), this.missionBoardContent.getWidth() - 8, true));
                flow.next(2);
            }
            flow.nextY(expeditionForm, 2);
            bl2 = false;
            sectionHeight = flow.next() - 4;
        }
        for (FormBreakLine breakLine : this.verticalBreakLines) {
            breakLine.length = sectionHeight;
        }
        flow.next(10);
        flow.nextY(this.missionSlotsLabel, 5);
        int buttonsY = flow.next(30);
        this.buyMoreSlotsButton.setY(buttonsY);
        this.addNewMissionButton.setY(buttonsY);
        this.missionBoardContent.setContentBox(new Rectangle(this.missionBoardContent.getWidth(), flow.next()));
        this.updateMissionSlotsContent();
    }

    public void refreshExpeditionsForms() {
        for (ExpeditionForm expeditionForm : this.expeditionForms) {
            expeditionForm.updateContent(false);
        }
    }

    public void updateMissionBoardFull() {
        this.missionBoardContent.clearComponents();
        this.expeditionForms = new ArrayList();
        this.verticalBreakLines = new ArrayList();
        this.horizontalBreakLines = this.missionBoardContent.addComponent(new FormComponentListTyped());
        FormFlow flow = new FormFlow(5);
        int headersY = flow.next(28);
        this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, flow.next(), this.missionBoardContent.getWidth() - 8, true));
        flow.next(2);
        this.missionBoardContentStartY = flow.next();
        for (int i = 0; i < ((MissionBoardContainer)this.container).missionBoardMissions.size(); ++i) {
            NetworkMissionBoardMission mission = ((MissionBoardContainer)this.container).missionBoardMissions.get(i);
            ExpeditionForm expeditionForm = this.missionBoardContent.addComponent(flow.nextY(new ExpeditionForm(this.missionBoardContent.getWidth() - this.missionBoardContent.getScrollBarWidth(), i, mission), 2));
            this.expeditionForms.add(expeditionForm);
        }
        int currentSectionX = 0;
        FontOptions headerFontOptions = new FontOptions(16);
        TypeParser[] headerParsers = new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(headerFontOptions.getSize(), false, FairItemGlyph::dontShowTooltip), TypeParsers.MobIcon(headerFontOptions.getSize()), TypeParsers.InputIcon(headerFontOptions)};
        GameMessageBuilder typeHeader = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("mapfragment"))).append("ui", "missiontypeheader");
        this.missionBoardContent.addComponent(new FormFairTypeLabel(typeHeader, headerFontOptions, FairType.TextAlign.CENTER, currentSectionX + this.typeWidth / 2, headersY).setParsers(headerParsers).setMax(this.typeWidth, 1, true, true));
        this.verticalBreakLines.add(this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, currentSectionX += this.typeWidth, 0, 0, false)));
        GameMessageBuilder settlersHeader = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("ironhelmet"))).append("ui", "missionsettlersheader");
        this.missionBoardContent.addComponent(new FormFairTypeLabel(settlersHeader, headerFontOptions, FairType.TextAlign.CENTER, currentSectionX + this.settlersWidth / 2, headersY).setParsers(headerParsers).setMax(this.settlersWidth, 1, true, true));
        this.verticalBreakLines.add(this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, currentSectionX += this.settlersWidth, 0, 0, false)));
        GameMessageBuilder priceHeader = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("coinstack"))).append("ui", "missionpriceheader");
        this.missionBoardContent.addComponent(new FormFairTypeLabel(priceHeader, headerFontOptions, FairType.TextAlign.CENTER, currentSectionX + this.priceWidth / 2, headersY).setParsers(headerParsers).setMax(this.priceWidth, 1, true, true));
        this.verticalBreakLines.add(this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, currentSectionX += this.priceWidth, 0, 0, false)));
        GameMessageBuilder successRateHeader = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("oakclock"))).append("ui", "missionsucessrateheader");
        this.missionBoardContent.addComponent(new FormFairTypeLabel(successRateHeader, headerFontOptions, FairType.TextAlign.CENTER, currentSectionX + this.successRateWidth / 2, headersY).setParsers(headerParsers).setMax(this.successRateWidth, 1, true, true));
        this.verticalBreakLines.add(this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, currentSectionX += this.successRateWidth, 0, 0, false)));
        GameMessageBuilder conditionHeader = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("wrench"))).append("ui", "missionconditionsheader");
        this.missionBoardContent.addComponent(new FormFairTypeLabel(conditionHeader, headerFontOptions, FairType.TextAlign.CENTER, currentSectionX + this.conditionsWidth / 2, headersY).setParsers(headerParsers).setMax(this.conditionsWidth, 1, true, true));
        this.verticalBreakLines.add(this.missionBoardContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, currentSectionX += this.conditionsWidth, 0, 0, false)));
        this.missionSlotsLabel = this.missionBoardContent.addComponent(flow.nextY(new FormLabel("N/A", new FontOptions(16), 0, this.missionBoardContent.getWidth() / 2, 0), 5));
        int buttonsWidth = 200;
        int buttonsPadding = 10;
        this.addNewMissionButton = this.missionBoardContent.addComponent(new FormLocalTextButton("ui", "addnewmission", this.missionBoardContent.getWidth() / 2 - buttonsWidth - buttonsPadding / 2, 0, buttonsWidth, FormInputSize.SIZE_24, ButtonColor.GREEN));
        this.addNewMissionButton.onClicked(e -> {
            this.setupMissionSelect(null, null);
            this.makeCurrent(this.newMissionSelect);
        });
        this.buyMoreSlotsButton = this.missionBoardContent.addComponent(new FormLocalTextButton("ui", "buymissionslot", this.missionBoardContent.getWidth() / 2 + buttonsPadding / 2, 0, buttonsWidth, FormInputSize.SIZE_24, ButtonColor.BASE){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                ListGameTooltips tooltips = new ListGameTooltips();
                if (((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).nextSlotCost < 0) {
                    tooltips.add(new LocalMessage("ui", "maxmissionslotsreached"));
                } else {
                    tooltips.add(new LocalMessage("ui", "missionslotcost"));
                    Ingredient[] ingredients = new Ingredient[]{new Ingredient("coin", ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).nextSlotCost)};
                    CanCraft canCraft = ((MissionBoardContainer)MissionBoardContainerForm.this.container).canCraftRecipe(ingredients, ((MissionBoardContainer)MissionBoardContainerForm.this.container).getCraftInventories(), true);
                    for (int i = 0; i < ingredients.length; ++i) {
                        tooltips.add(ingredients[i].getTooltips(canCraft.haveIngredients[i], true));
                    }
                }
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
        });
        this.buyMoreSlotsButton.onClicked(e -> {
            ((MissionBoardContainer)this.container).buyMoreSlotsAction.runAndSend();
            this.buyMoreSlotsButton.startCooldown(1000);
        });
        this.updateMissionsOrder();
        this.tryPrioritizeControllerFocus(this.addNewMissionButton);
    }

    public void updateMissionSlotsContent() {
        if (this.missionSlotsLabel != null) {
            this.missionSlotsLabel.setText(new LocalMessage("ui", "missionslots", "slots", ((MissionBoardContainer)this.container).missionBoardMissions.size() + "/" + ((MissionBoardContainer)this.container).missionBoardSlots));
        }
        if (this.addNewMissionButton != null) {
            this.addNewMissionButton.setActive(((MissionBoardContainer)this.container).missionBoardMissions.size() < ((MissionBoardContainer)this.container).missionBoardSlots);
        }
        this.updateSlotButtons();
    }

    public void updateSlotButtons() {
        if (this.buyMoreSlotsButton != null) {
            if (((MissionBoardContainer)this.container).nextSlotCost == -1) {
                this.buyMoreSlotsButton.setActive(false);
            } else {
                Ingredient[] ingredients = new Ingredient[]{new Ingredient("coin", ((MissionBoardContainer)this.container).nextSlotCost)};
                CanCraft canCraft = ((MissionBoardContainer)this.container).canCraftRecipe(ingredients, ((MissionBoardContainer)this.container).getCraftInventories(), true);
                this.buyMoreSlotsButton.setActive(canCraft.canCraft());
            }
        }
    }

    public void setupMissionSelect(String displayCategoryStringID, GameMessage subtitle) {
        this.newMissionSelect.clearComponents();
        this.newMissionSelect.setWidth(300);
        int maxContentHeight = 400;
        FormContentBox content = new FormContentBox(0, 0, this.newMissionSelect.getWidth(), maxContentHeight);
        FormFlow contentFlow = new FormFlow(4);
        HashSet<String> addedCategories = new HashSet<String>();
        FormLocalTextButton firstComponent = null;
        Iterator iterator = ((MissionBoardContainer)this.container).validExpeditionIDs.iterator();
        while (iterator.hasNext()) {
            int expeditionID = (Integer)iterator.next();
            SettlerExpedition expedition = ExpeditionMissionRegistry.getExpedition(expeditionID);
            String expeditionCategoryStringID = expedition.getCategoryStringID();
            if (expeditionCategoryStringID != null && displayCategoryStringID == null) {
                if (addedCategories.contains(expeditionCategoryStringID)) continue;
                GameMessage displayName = ExpeditionMissionRegistry.categoryDisplayNames.getOrDefault(expeditionCategoryStringID, new StaticMessage("Unknown category"));
                FormLocalTextButton button = content.addComponent(contentFlow.nextY(new FormLocalTextButton(displayName, 4, 0, this.newMissionSelect.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 4));
                button.onClicked(e -> this.setupMissionSelect(expeditionCategoryStringID, displayName));
                if (firstComponent == null) {
                    firstComponent = button;
                }
                addedCategories.add(expeditionCategoryStringID);
                continue;
            }
            if (!Objects.equals(expedition.getCategoryStringID(), displayCategoryStringID)) continue;
            FormLocalTextButton button = content.addComponent(contentFlow.nextY(new FormLocalTextButton(expedition.getDisplayName(), 4, 0, this.newMissionSelect.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 4));
            button.onClicked(e -> {
                ((MissionBoardContainer)this.container).addNewMissionAction.runAndSend(expedition.getID());
                this.makeCurrent(this.missionBoardForm);
            });
            if (firstComponent != null) continue;
            firstComponent = button;
        }
        if (firstComponent != null) {
            this.tryPrioritizeControllerFocus(firstComponent);
        }
        if (contentFlow.next() <= maxContentHeight) {
            content.setHeight(contentFlow.next());
        } else {
            this.newMissionSelect.setWidth(content.getWidth() + content.getScrollBarWidth());
            content.setWidth(this.newMissionSelect.getWidth());
        }
        content.setContentBox(new Rectangle(content.getWidth(), contentFlow.next()));
        FormFlow flow = new FormFlow(4);
        this.newMissionSelect.addComponent(flow.nextY(new FormLocalLabel("ui", "selectmission", new FontOptions(20), 0, this.newMissionSelect.getWidth() / 2, 0), 5));
        if (subtitle != null) {
            this.newMissionSelect.addComponent(flow.nextY(new FormLocalLabel(subtitle, new FontOptions(16), 0, this.newMissionSelect.getWidth() / 2, 0), 5));
        }
        content.setY(flow.next());
        this.newMissionSelect.addComponent(content);
        flow.next(content.getHeight() + 4);
        this.newMissionSelect.addComponent(flow.nextY(new FormLocalTextButton("ui", "backbutton", 4, 0, this.newMissionSelect.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 4)).onClicked(e -> {
            if (displayCategoryStringID == null) {
                this.makeCurrent(this.missionBoardForm);
            } else {
                this.setupMissionSelect(null, null);
            }
        });
        flow.next(4);
        this.newMissionSelect.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    public void setupConditionConfig(NetworkMissionBoardMission mission) {
        this.conditionConfigForm.clearComponents();
        this.conditionConfigUpdateListeners = new ArrayList();
        Form conditionForm = mission.condition.getConfigurationForm(this.client, 300, (type, packet) -> ((MissionBoardContainer)this.container).updateConditionAction.runAndSend(mission, type, packet), this.conditionConfigUpdateListeners, () -> this.setupConditionConfig(mission));
        conditionForm.drawBase = false;
        this.conditionConfigForm.setWidth(conditionForm.getWidth());
        FormFlow flow = new FormFlow(10);
        conditionForm.setY(flow.next(conditionForm.getHeight()));
        flow.next(10);
        this.conditionConfigForm.addComponent(conditionForm);
        FormLocalTextButton backButton = this.conditionConfigForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "backbutton", 4, 0, this.conditionConfigForm.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 4));
        backButton.onClicked(e -> {
            this.updateMissionBoardFull();
            this.makeCurrent(this.missionBoardForm);
        });
        flow.next(4);
        this.tryPrioritizeControllerFocus(backButton);
        this.conditionConfigForm.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    public void updatePrivateFormActive() {
        if (((MissionBoardContainer)this.container).hasSettlementAccess(this.client)) {
            if (this.isCurrent(this.privateForm)) {
                this.makeCurrent(this.missionBoardForm);
            }
        } else if (!this.isCurrent(this.privateForm)) {
            this.makeCurrent(this.privateForm);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.missionBoardForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.newMissionSelect.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.conditionConfigForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updatePrivateFormActive();
        this.updateSlotButtons();
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }

    public class ExpeditionForm
    extends Form {
        public int slot;
        protected NetworkMissionBoardMission mission;
        protected FormContentIconButton moveUpButton;
        protected FormContentIconButton moveDownButton;
        protected Form settlerIcons;
        protected FormContentIconButton configSettlersButton;
        protected FormFairTypeLabel priceLabel;
        protected FormLabel successRateLabel;
        protected FormFairTypeLabel conditionLabel;
        protected FormContentIconButton changeConditionButton;
        protected FormContentIconButton configConditionButton;

        public ExpeditionForm(int width, int slot, NetworkMissionBoardMission mission) {
            super(width, 44);
            this.slot = slot;
            this.mission = mission;
            this.drawBase = false;
            this.shouldLimitDrawArea = false;
            this.updateContent(true);
        }

        public void updateContent(boolean fullRefresh) {
            String priceRangeString;
            Object nameLabel;
            if (fullRefresh) {
                this.clearComponents();
            }
            int currentContentX = 0;
            if (fullRefresh) {
                String category = this.mission.expedition.getCategoryStringID();
                this.moveUpButton = this.addComponent(new FormContentIconButton(currentContentX + 4, this.getHeight() / 2 - 16 - 1, FormInputSize.SIZE_16, ButtonColor.BASE, this.getInterfaceStyle().button_expanded_16, new LocalMessage("ui", "moveupbutton")));
                this.moveUpButton.mirrorY();
                this.moveUpButton.onClicked(e -> {
                    Input input = WindowManager.getWindow().getInput();
                    if (input.isKeyDown(340) || input.isKeyDown(344)) {
                        this.updateAndSendContainerToSlot(0);
                    } else {
                        this.updateAndSendContainerToSlot(Math.max(this.slot - 1, 0));
                    }
                });
                this.moveDownButton = this.addComponent(new FormContentIconButton(currentContentX + 4, this.getHeight() / 2 + 1, FormInputSize.SIZE_16, ButtonColor.BASE, this.getInterfaceStyle().button_expanded_16, new LocalMessage("ui", "movedownbutton")));
                this.moveDownButton.onClicked(e -> {
                    Input input = WindowManager.getWindow().getInput();
                    if (input.isKeyDown(340) || input.isKeyDown(344)) {
                        this.updateAndSendContainerToSlot(((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.size() - 1);
                    } else {
                        this.updateAndSendContainerToSlot(Math.min(this.slot + 1, ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.size() - 1));
                    }
                });
                int nameX = currentContentX + 4 + 16 + 4;
                FontOptions nameFontOptions = new FontOptions(20);
                nameLabel = new FormFairTypeLabel(new StaticMessage(""), nameFontOptions, FairType.TextAlign.LEFT, nameX, 4);
                ((FormFairTypeLabel)nameLabel).setMax(MissionBoardContainerForm.this.typeWidth - nameX - 4 - 16, 1, true, true);
                List<InventoryItem> itemIcons = this.mission.expedition.getItemIcons();
                if (itemIcons != null && !itemIcons.isEmpty()) {
                    FairType nameLabelFairType = new FairType();
                    nameLabelFairType.append(new FairItemGlyph(16, itemIcons).onlyShowNameTooltip());
                    nameLabelFairType.append(nameFontOptions, " " + this.mission.expedition.getDisplayName().translate());
                    ((FormFairTypeLabel)nameLabel).setCustomFairType(nameLabelFairType);
                } else {
                    ((FormFairTypeLabel)nameLabel).setText(this.mission.expedition.getDisplayName());
                }
                this.addComponent(nameLabel);
                if (category != null) {
                    nameLabel.setY(4);
                    GameMessage categoryDisplayName = ExpeditionMissionRegistry.categoryDisplayNames.getOrDefault(category, new StaticMessage("N/A category"));
                    this.addComponent(new FormFairTypeLabel(categoryDisplayName, new FontOptions(16), FairType.TextAlign.LEFT, nameX, 24).setMax(MissionBoardContainerForm.this.typeWidth - nameX - 4, 1, true, true));
                } else {
                    nameLabel.setY(this.getHeight() / 2 - 10);
                }
            }
            this.moveUpButton.setActive(this.slot > 0);
            this.moveDownButton.setActive(this.slot < ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.size() - 1);
            currentContentX += MissionBoardContainerForm.this.typeWidth;
            if (fullRefresh) {
                this.settlerIcons = this.addComponent(new Form(MissionBoardContainerForm.this.settlersWidth, this.getHeight()));
                this.settlerIcons.drawBase = false;
                this.settlerIcons.shouldLimitDrawArea = false;
            }
            this.settlerIcons.clearComponents();
            int settlerMaxWidth = MissionBoardContainerForm.this.settlersWidth - 40 - 24;
            if (this.mission.allSettlersAssigned) {
                this.settlerIcons.addComponent(new FormLocalLabel("ui", "settlereveryoneselectedtip", new FontOptions(16), -1, 0, 0));
            } else {
                boolean addedAnySettlers = false;
                int currentSettlerX = 0;
                nameLabel = this.mission.assignedSettlers.iterator();
                while (nameLabel.hasNext()) {
                    SettlerMob mob;
                    int mobUniqueID = (Integer)nameLabel.next();
                    SettlementSettlerData settler = ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).settlers.stream().filter(s -> s.mobUniqueID == mobUniqueID).findFirst().orElse(null);
                    if (settler == null || (mob = settler.getSettlerMob(MissionBoardContainerForm.this.client.getLevel())) == null) continue;
                    FormSettlerIcon settlerIcon = new FormSettlerIcon(currentSettlerX + 4, 0, settler.settler, mob.getMob(), null){

                        @Override
                        public GameTooltips getTooltips() {
                            return null;
                        }
                    };
                    settlerIcon.zIndex = Integer.MIN_VALUE;
                    settlerIcon.canBePutOnTopByClick = false;
                    this.settlerIcons.addComponent(settlerIcon);
                    addedAnySettlers = true;
                    if ((currentSettlerX += 12) < settlerMaxWidth) continue;
                    break;
                }
                if (!addedAnySettlers) {
                    if (this.mission.assignedSettlers.isEmpty()) {
                        FontOptions emptyFontOptions = new FontOptions(16).color(this.getInterfaceStyle().errorTextColor);
                        this.settlerIcons.addComponent(new FormLocalLabel("ui", "settlernoneselectedtip", emptyFontOptions, -1, 0, 0));
                    } else {
                        this.settlerIcons.addComponent(new FormLabel("?", new FontOptions(16), -1, 0, 0));
                    }
                }
            }
            if (fullRefresh) {
                this.configSettlersButton = this.addComponent(new FormContentIconButton(0, this.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_config, new LocalMessage("ui", "configurebutton")));
                this.configSettlersButton.onClicked(e -> {
                    MissionBoardContainerForm.this.requestedAvailableSettlersEvent = e.event;
                    MissionBoardContainerForm.this.requestedAvailableSettlersFrom = e.from;
                    MissionBoardContainerForm.this.requestedAvailableSettlersMissionUniqueID = this.mission.uniqueID;
                    ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).requestAvailableSettlersAction.runAndSend(this.mission.uniqueID);
                });
            }
            this.settlerIcons.setPosition(0, 0);
            Rectangle settlersBoundingBox = this.settlerIcons.getBoundingBox();
            this.configSettlersButton.setX(settlersBoundingBox.x + settlersBoundingBox.width + 6);
            Rectangle configButtonBox = this.configSettlersButton.getBoundingBox();
            Rectangle finalSettlersBoundingBox = settlersBoundingBox.union(new Rectangle(configButtonBox.x, settlersBoundingBox.y, configButtonBox.width, 0));
            this.settlerIcons.setPosition(currentContentX + MissionBoardContainerForm.this.settlersWidth / 2 - finalSettlersBoundingBox.width / 2, this.getHeight() / 2 - finalSettlersBoundingBox.height / 2);
            this.configSettlersButton.setX(this.settlerIcons.getX() + settlersBoundingBox.width + 6);
            currentContentX += MissionBoardContainerForm.this.settlersWidth;
            if (this.mission.basePrice == 0) {
                priceRangeString = "" + this.mission.basePrice;
            } else {
                IntRange range = this.mission.expedition.getCostRange(this.mission.basePrice);
                priceRangeString = range.min + " - " + range.max;
            }
            GameMessageBuilder priceMessage = new GameMessageBuilder().append(TypeParsers.getItemParseString(new InventoryItem("coin"))).append(priceRangeString);
            if (fullRefresh) {
                FontOptions priceFontOptions = new FontOptions(16);
                this.priceLabel = new FormFairTypeLabel(priceMessage, priceFontOptions, FairType.TextAlign.CENTER, currentContentX + MissionBoardContainerForm.this.priceWidth / 2, 0);
                this.priceLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(priceFontOptions.getSize(), false, FairItemGlyph::dontShowTooltip), TypeParsers.MobIcon(priceFontOptions.getSize()), TypeParsers.InputIcon(priceFontOptions));
                this.priceLabel.setMax(MissionBoardContainerForm.this.priceWidth, 1, true, true);
                this.addComponent(this.priceLabel);
            } else {
                this.priceLabel.setText(priceMessage);
            }
            this.priceLabel.setPosition(currentContentX + MissionBoardContainerForm.this.priceWidth / 2, this.getHeight() / 2 - this.priceLabel.getBoundingBox().height / 2);
            currentContentX += MissionBoardContainerForm.this.priceWidth;
            if (fullRefresh) {
                this.successRateLabel = this.addComponent(new FormLabel("", new FontOptions(16), 0, currentContentX + MissionBoardContainerForm.this.successRateWidth / 2, this.getHeight() / 2 - 8));
            }
            this.successRateLabel.setText((int)(this.mission.successChance * 100.0f) + "%");
            currentContentX += MissionBoardContainerForm.this.successRateWidth;
            if (fullRefresh) {
                FontOptions conditionFontOptions = new FontOptions(16);
                this.conditionLabel = this.mission.condition.getSelectedLabel(conditionFontOptions, (type, packet) -> ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).updateConditionAction.runAndSend(this.mission, type, packet), () -> this.updateContent(false));
                this.conditionLabel.setMax(MissionBoardContainerForm.this.conditionsWidth - 8 - 24 - 8 - 24, 1, true, true);
                this.addComponent(this.conditionLabel);
            } else {
                this.mission.condition.updateSelectedLabel(this.conditionLabel, (type, packet) -> ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).updateConditionAction.runAndSend(this.mission, type, packet), () -> this.updateContent(false));
            }
            if (fullRefresh) {
                this.changeConditionButton = this.addComponent(new FormContentIconButton(0, this.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_collapsed_24, new LocalMessage("ui", "changebutton")));
                this.changeConditionButton.onClicked(e -> {
                    SelectionFloatMenu menu = new SelectionFloatMenu(this.changeConditionButton, SelectionFloatMenu.Solid(new FontOptions(12)), 200);
                    for (Map.Entry<String, Function<NetworkMissionBoardMission, JobCondition>> entry : SettlementMissionBoardMission.conditionLoaders.entrySet()) {
                        menu.add(JobConditionRegistry.getJobConditionListedMessage(entry.getKey()).translate(), () -> {
                            if (!this.mission.condition.getStringID().equals(entry.getKey())) {
                                JobCondition newCondition = (JobCondition)((Function)entry.getValue()).apply(this.mission);
                                ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).setConditionAction.runAndSend(this.mission.uniqueID, newCondition);
                                this.mission.condition = newCondition;
                                this.updateContent(false);
                            }
                            menu.remove();
                        });
                    }
                    if (e.event.isControllerEvent()) {
                        this.getManager().openFloatMenu(menu);
                    } else {
                        this.getManager().openFloatMenu(menu, e.from, e.event, -4, 0);
                    }
                });
                this.configConditionButton = this.addComponent(new FormContentIconButton(0, this.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_config, new LocalMessage("ui", "configurebutton")));
                this.configConditionButton.onClicked(e -> {
                    MissionBoardContainerForm.this.setupConditionConfig(this.mission);
                    MissionBoardContainerForm.this.currentConditionMissionUniqueID = this.mission.uniqueID;
                    MissionBoardContainerForm.this.makeCurrent(MissionBoardContainerForm.this.conditionConfigForm);
                });
            }
            Rectangle conditionBoundingBox = this.conditionLabel.getBoundingBox();
            int totalWidth = 58 + conditionBoundingBox.width;
            this.changeConditionButton.setX(currentContentX + MissionBoardContainerForm.this.conditionsWidth / 2 - totalWidth / 2);
            this.configConditionButton.setX(currentContentX + MissionBoardContainerForm.this.conditionsWidth / 2 - totalWidth / 2 + 24 + 4);
            this.conditionLabel.setPosition(currentContentX + MissionBoardContainerForm.this.conditionsWidth / 2 - totalWidth / 2 + 24 + 4 + 24 + 6, this.getHeight() / 2 - conditionBoundingBox.height / 2);
            currentContentX += MissionBoardContainerForm.this.conditionsWidth;
            if (fullRefresh) {
                this.addComponent(new FormContentIconButton(currentContentX + 8, this.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.RED, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "deletebutton"))).onClicked(e -> {
                    ConfirmationForm confirmationForm = new ConfirmationForm("confirmDeleteMission", 400, 400);
                    confirmationForm.setupConfirmation(new LocalMessage("ui", "confirmdeletemission", "mission", this.mission.expedition.getDisplayName()), () -> {
                        ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).deleteMissionAction.runAndSend(this.mission.uniqueID);
                        MissionBoardContainerForm.this.makeCurrent(MissionBoardContainerForm.this.missionBoardForm);
                    }, () -> MissionBoardContainerForm.this.makeCurrent(MissionBoardContainerForm.this.missionBoardForm));
                    MissionBoardContainerForm.this.addAndMakeCurrentTemporary(confirmationForm);
                });
            }
            WindowManager.getWindow().getInput().submitNextMoveEvent();
        }

        protected void updateAndSendContainerToSlot(int slot) {
            this.slot = slot;
            ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).setMissionSlotAction.runAndSend(this.mission, slot);
            for (int i = 0; i < ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.size(); ++i) {
                NetworkMissionBoardMission m = ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.get(i);
                if (m.uniqueID != this.mission.uniqueID) continue;
                ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.remove(i);
                ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.add(Math.min(slot, ((MissionBoardContainer)((MissionBoardContainerForm)MissionBoardContainerForm.this).container).missionBoardMissions.size()), m);
                MissionBoardContainerForm.this.updateMissionsOrder();
                MissionBoardContainerForm.this.refreshExpeditionsForms();
                break;
            }
        }

        @Override
        public List<Rectangle> getHitboxes() {
            return ExpeditionForm.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
    }
}

