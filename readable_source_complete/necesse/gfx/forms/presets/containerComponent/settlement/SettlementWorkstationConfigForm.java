/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import necesse.engine.ClipboardTracker;
import necesse.engine.GameLog;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairButtonGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.floatMenu.SettlementRecipeFloatMenu;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public abstract class SettlementWorkstationConfigForm
extends FormSwitcher {
    public final Point tile;
    public final Client client;
    private final Form mainForm;
    private final Form recipeConfigForm;
    private ClipboardTracker<ConfigData> listClipboard;
    private final FormContentIconButton pasteButton;
    private final FormLocalTextButton addRecipeButton;
    private final FormContentBox recipeContent;
    private FormFlow recipeContentFlow;
    private final SettlementWorkstationLevelObject workstationObject;
    private final ArrayList<RecipeForm> recipes = new ArrayList();

    public SettlementWorkstationConfigForm(String name, int width, int height, Point tile, Client client, GameMessage header, PlayerMob perspective, SettlementWorkstationLevelObject workstationObject, ArrayList<SettlementWorkstationRecipe> recipes) {
        this.tile = tile;
        this.client = client;
        this.workstationObject = workstationObject;
        this.mainForm = this.addComponent(new Form(name, width, height));
        FormFlow flow = new FormFlow(5);
        if (header != null) {
            this.mainForm.addComponent(new FormLocalLabel(header, new FontOptions(20), -1, 5, flow.next(30)));
        }
        int buttonsY = flow.next(28);
        int buttonX = this.mainForm.getWidth() - 28;
        this.mainForm.addComponent(new FormContentIconButton(buttonX, buttonsY, FormInputSize.SIZE_24, ButtonColor.RED, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "settlementremoveworkstation"))).onClicked(e -> this.onRemove());
        this.pasteButton = this.mainForm.addComponent(new FormContentIconButton(buttonX -= 26, buttonsY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
        this.pasteButton.onClicked(e -> {
            ConfigData data = this.listClipboard.getValue();
            if (data != null) {
                for (SettlementWorkstationRecipe recipe : data.recipes) {
                    this.addRecipe(recipe);
                }
                this.updateRecipesContent();
            }
        });
        this.listClipboard = new ClipboardTracker<ConfigData>(){

            @Override
            public ConfigData parse(String clipboard) {
                try {
                    return new ConfigData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(ConfigData value) {
                SettlementWorkstationConfigForm.this.pasteButton.setActive(value != null && !value.recipes.isEmpty());
            }
        };
        this.mainForm.addComponent(new FormContentIconButton(buttonX -= 26, buttonsY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
            ArrayList list = this.recipes.stream().map(c -> c.element).collect(Collectors.toCollection(ArrayList::new));
            SaveData save = new ConfigData(list).getSaveData();
            WindowManager.getWindow().putClipboard(save.getScript());
            this.listClipboard.forceUpdate();
        });
        int addWidth = Math.min(200, (buttonX -= 26) - 8);
        this.addRecipeButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "workstationaddrecipe", 4, buttonsY, addWidth, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.addRecipeButton.onClicked(e -> {
            SettlementRecipeFloatMenu menu = new SettlementRecipeFloatMenu(this, addWidth - 4, 200, perspective, workstationObject){

                @Override
                public void onRecipeClicked(Recipe recipe, PlayerMob perspective) {
                    SettlementWorkstationConfigForm.this.addRecipe(recipe);
                    SettlementWorkstationConfigForm.this.updateRecipesContent();
                    SettlementWorkstationConfigForm.this.playTickSound();
                    this.remove();
                }
            };
            this.getManager().openFloatMenu((FloatMenu)menu, this.addRecipeButton.getX() - e.event.pos.hudX + 2, this.addRecipeButton.getY() - e.event.pos.hudY - 200 + 22);
        });
        int contentY = flow.next();
        this.recipeContent = this.mainForm.addComponent(new FormContentBox(0, contentY, width, height - contentY - 32));
        this.recipeContentFlow = new FormFlow();
        this.setRecipes(recipes);
        this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", width / 2 - 4, this.mainForm.getHeight() - 28, width / 2, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> this.onBack());
        this.recipeConfigForm = this.addComponent(new Form(name + "-config", width, height));
        this.makeCurrent(this.mainForm);
    }

    public void addRecipe(SettlementWorkstationRecipe recipe) {
        if (this.recipes.size() < SettlementWorkstation.maxRecipes) {
            int nextUniqueID;
            do {
                nextUniqueID = GameRandom.globalRandom.nextInt();
            } while (!this.recipes.stream().noneMatch(c -> c.element.uniqueID == nextUniqueID));
            int uniqueID = nextUniqueID;
            int index = this.recipes.size();
            Packet packet = new Packet();
            recipe.writePacket(new PacketWriter(packet));
            SettlementWorkstationRecipe settlementRecipe = new SettlementWorkstationRecipe(uniqueID, new PacketReader(packet));
            this.recipes.add(index, new RecipeForm(this.recipeContent, this.recipes.size(), settlementRecipe));
            this.onSubmitUpdate(index, settlementRecipe);
        }
    }

    public void addRecipe(Recipe recipe) {
        this.addRecipe(new SettlementWorkstationRecipe(-1, recipe));
    }

    public void onRecipeRemove(SettlementWorkstationRecipeRemoveEvent event) {
        boolean changed = false;
        for (int i = 0; i < this.recipes.size(); ++i) {
            RecipeForm recipeForm = this.recipes.get(i);
            if (recipeForm.element.uniqueID != event.recipeUniqueID) continue;
            this.recipes.remove(i);
            this.recipeContent.removeComponent(recipeForm);
            changed = true;
            --i;
        }
        if (changed) {
            this.updateRecipesContent();
        }
    }

    public void onRecipeUpdate(SettlementWorkstationRecipeUpdateEvent event) {
        this.onRecipeUpdate(event.index, event.recipeUniqueID, new PacketReader(event.recipeContent), true);
    }

    private void onRecipeUpdate(int index, int uniqueID, PacketReader reader, boolean updateContent) {
        if (index >= 0 && index > this.recipes.size()) {
            GameLog.warn.println("Received invalid settlement recipe index");
            this.onBack();
            return;
        }
        int currentIndex = -1;
        for (int i = 0; i < this.recipes.size(); ++i) {
            if (this.recipes.get((int)i).element.uniqueID != uniqueID) continue;
            currentIndex = i;
            break;
        }
        if (currentIndex != -1) {
            if (index < 0) {
                this.recipes.get(currentIndex).update(reader);
            } else if (currentIndex == index) {
                this.recipes.get(currentIndex).update(reader);
            } else {
                RecipeForm last = this.recipes.remove(currentIndex);
                this.recipes.add(index, last);
                last.update(reader);
                if (updateContent) {
                    this.updateRecipesContent();
                }
            }
        } else if (index < 0) {
            GameLog.warn.println("Could not find recipe for update");
        } else if (index == this.recipes.size()) {
            RecipeForm component = this.recipeContentFlow.nextY(new RecipeForm(this.recipeContent, index, new SettlementWorkstationRecipe(uniqueID, reader)));
            this.recipes.add(index, component);
            this.recipeContent.addComponent(component);
            this.recipeContent.setContentBox(new Rectangle(this.recipeContent.getWidth(), this.recipeContentFlow.next()));
        } else {
            this.recipes.add(index, new RecipeForm(this.recipeContent, index, new SettlementWorkstationRecipe(uniqueID, reader)));
            if (updateContent) {
                this.updateRecipesContent();
            }
        }
    }

    public void setRecipes(ArrayList<SettlementWorkstationRecipe> recipes) {
        for (int i = 0; i < Math.max(recipes.size(), this.recipes.size()); ++i) {
            if (i < recipes.size()) {
                SettlementWorkstationRecipe recipe = recipes.get(i);
                Packet content = new Packet();
                recipe.writePacket(new PacketWriter(content));
                this.onRecipeUpdate(i, recipe.uniqueID, new PacketReader(content), false);
                continue;
            }
            RecipeForm recipeForm = this.recipes.remove(i);
            this.recipeContent.removeComponent(recipeForm);
            --i;
        }
        this.updateRecipesContent();
    }

    public void updateRecipesContent() {
        this.recipeContentFlow = new FormFlow();
        int recipesSize = this.recipes.size();
        for (int i = 0; i < recipesSize; ++i) {
            RecipeForm recipeForm = this.recipes.get(i);
            recipeForm.index = i;
            if (!this.recipeContent.hasComponent(recipeForm)) {
                this.recipeContent.addComponent(this.recipeContentFlow.nextY(recipeForm));
                continue;
            }
            recipeForm.setPosition(0, this.recipeContentFlow.next(recipeForm.getHeight()));
        }
        for (RecipeForm recipe : this.recipes) {
            recipe.updateIndex(recipe.index);
        }
        this.recipeContent.setContentBox(new Rectangle(this.recipeContent.getWidth(), this.recipeContentFlow.next()));
        WindowManager.getWindow().submitNextMoveEvent();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        this.addRecipeButton.setActive(this.recipes.size() < SettlementWorkstation.maxRecipes);
        super.draw(tickManager, perspective, renderBox);
    }

    public void setPosFocus() {
        ContainerComponent.setPosFocus(this.mainForm);
        ContainerComponent.setPosFocus(this.recipeConfigForm);
    }

    public void setPosInventory() {
        ContainerComponent.setPosInventory(this.mainForm);
        ContainerComponent.setPosInventory(this.recipeConfigForm);
    }

    public abstract void onSubmitRemove(int var1);

    public abstract void onSubmitUpdate(int var1, SettlementWorkstationRecipe var2);

    public abstract void onRemove();

    public abstract void onBack();

    private class RecipeForm
    extends Form {
        public int index;
        public SettlementWorkstationRecipe element;
        public FormFairTypeLabel subtitle;
        public FormLabelEdit label;
        public FormIconButton moveUpButton;
        public FormIconButton moveDownButton;
        public FormContentIconButton renameButton;

        public RecipeForm(FormContentBox contentBox, int index, final SettlementWorkstationRecipe recipe) {
            super(contentBox.getWidth(), 44);
            this.index = index;
            this.element = recipe;
            this.drawBase = false;
            int buttonX = this.getWidth() - 26 - 8;
            this.addComponent(new FormContentIconButton(buttonX, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "removebutton"))).onClicked(e -> SettlementWorkstationConfigForm.this.onSubmitRemove(this.element.uniqueID));
            this.addComponent(new FormContentIconButton(buttonX -= 26, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
                ArrayList<SettlementWorkstationRecipe> list = new ArrayList<SettlementWorkstationRecipe>(1);
                list.add(this.element);
                SaveData save = new ConfigData(list).getSaveData();
                WindowManager.getWindow().putClipboard(save.getScript());
                SettlementWorkstationConfigForm.this.listClipboard.forceUpdate();
            });
            buttonX -= 26;
            if (recipe.canConfigureIngredientFilter()) {
                FormContentIconButton configureButton = this.addComponent(new FormContentIconButton(buttonX, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_config, new LocalMessage("ui", "recipeconfigureingredients")));
                configureButton.onClicked(configureEvent -> {
                    Form configureForm = new Form(this.getWidth() - 4, 400);
                    final FormContentBox filterContent = configureForm.addComponent(new FormContentBox(0, 32, configureForm.getWidth(), configureForm.getHeight() - 32));
                    final ItemCategoriesFilter ingredientFilter = recipe.ingredientFilter;
                    ItemCategoryExpandedSetting expandedSetting = Settings.getItemCategoryExpandedSetting(recipe.recipe);
                    ItemCategoriesFilterForm filterForm = filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, ingredientFilter, ItemCategoriesFilterForm.Mode.ONLY_ALLOWED, expandedSetting, SettlementWorkstationConfigForm.this.client.characterStats.items_obtained, true){

                        @Override
                        public void onItemsChanged(Item[] items, boolean allowed) {
                            recipe.ingredientFilter = ingredientFilter;
                            SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                        }

                        @Override
                        public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                        }

                        @Override
                        public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                            recipe.ingredientFilter = ingredientFilter;
                            SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                        }

                        @Override
                        public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                        }

                        @Override
                        public void onDimensionsChanged(int width, int height) {
                            filterContent.setContentBox(new Rectangle(0, 0, Math.max(this.getWidth(), width), this.getY() + height));
                        }
                    });
                    filterContent.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, filterContent.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
                        if (!ingredientFilter.master.isAllAllowed()) {
                            ingredientFilter.master.setAllowed(true);
                            filterForm.updateAllButtons();
                            filterForm.onCategoryChanged(filterForm.filter.master, true);
                        }
                    });
                    filterContent.addComponent(new FormLocalTextButton("ui", "clearallbutton", filterContent.getWidth() / 2 + 2, 0, filterContent.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> {
                        if (ingredientFilter.master.isAnyAllowed()) {
                            ingredientFilter.master.setAllowed(false);
                            filterForm.updateAllButtons();
                            filterForm.onCategoryChanged(filterForm.filter.master, false);
                        }
                    });
                    FormTextInput searchInput = configureForm.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, filterContent.getWidth() - 8, -1, 500));
                    searchInput.placeHolder = new LocalMessage("ui", "searchtip");
                    searchInput.onChange(e -> filterForm.setSearch(searchInput.getText()));
                    FormFloatMenu menu = new FormFloatMenu(configureButton, configureForm);
                    this.getManager().openFloatMenu((FloatMenu)menu, this.getX() - configureEvent.event.pos.hudX + 2, this.getY() - configureEvent.event.pos.hudY + 2);
                });
                buttonX -= 26;
            }
            this.moveUpButton = this.addComponent(new FormIconButton(5, this.getHeight() / 2 - 13, this.getInterfaceStyle().button_moveup, 16, 13, new LocalMessage("ui", "moveupbutton")));
            this.moveUpButton.onClicked(e -> {
                Input input = WindowManager.getWindow().getInput();
                if (input.isKeyDown(340) || input.isKeyDown(344)) {
                    SettlementWorkstationConfigForm.this.onSubmitUpdate(0, this.element);
                } else {
                    SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index - 1, this.element);
                }
            });
            this.moveDownButton = this.addComponent(new FormIconButton(5, this.getHeight() / 2, this.getInterfaceStyle().button_movedown, 16, 13, new LocalMessage("ui", "movedownbutton")));
            this.moveDownButton.onClicked(e -> {
                Input input = WindowManager.getWindow().getInput();
                if (input.isKeyDown(340) || input.isKeyDown(344)) {
                    SettlementWorkstationConfigForm.this.onSubmitUpdate(SettlementWorkstationConfigForm.this.recipes.size() - 1, this.element);
                } else {
                    SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index + 1, this.element);
                }
            });
            final Object repeatEventInc = new Object();
            final Object repeatEventDec = new Object();
            this.addComponent(new FormCustomDraw(25, this.getHeight() / 2 - 16, 32, 32){

                @Override
                public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
                    if (this.isControllerFocus()) {
                        if (event.getState() == ControllerInput.MENU_SELECT) {
                            if (!event.buttonState) {
                                this.playTickSound();
                                SelectionFloatMenu menu = new SelectionFloatMenu(RecipeForm.this);
                                menu.add(SettlementWorkstationRecipe.Mode.DO_COUNT.countMessageFunction.apply("X").translate(), () -> {
                                    if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_COUNT) {
                                        RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_COUNT;
                                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                        RecipeForm.this.updateSubtitleText();
                                    }
                                    menu.remove();
                                });
                                menu.add(SettlementWorkstationRecipe.Mode.DO_UNTIL.countMessageFunction.apply("X").translate(), () -> {
                                    if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_UNTIL) {
                                        RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_UNTIL;
                                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                        RecipeForm.this.updateSubtitleText();
                                    }
                                    menu.remove();
                                });
                                menu.add(SettlementWorkstationRecipe.Mode.DO_FOREVER.countMessageFunction.apply("X").translate(), () -> {
                                    if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_FOREVER) {
                                        RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_FOREVER;
                                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                        RecipeForm.this.updateSubtitleText();
                                    }
                                    menu.remove();
                                });
                                ControllerFocus currentFocus = this.getManager().getCurrentFocus();
                                if (currentFocus != null) {
                                    this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + 32);
                                } else {
                                    this.getManager().openFloatMenuAt(menu, 0, 0);
                                }
                                event.use();
                            }
                        } else if (event.getState() == ControllerInput.MENU_NEXT || event.isRepeatEvent(repeatEventInc)) {
                            if (event.buttonState) {
                                ControllerInput.startRepeatEvents(event, repeatEventInc);
                                RecipeForm.this.element.modeCount = Math.min(65535, RecipeForm.this.element.modeCount + 1);
                                SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                if (event.shouldSubmitSound()) {
                                    this.playTickSound();
                                }
                                RecipeForm.this.updateSubtitleText();
                            }
                            event.use();
                        } else if (event.getState() == ControllerInput.MENU_PREV || event.isRepeatEvent(repeatEventDec)) {
                            if (event.buttonState) {
                                ControllerInput.startRepeatEvents(event, repeatEventDec);
                                RecipeForm.this.element.modeCount = Math.max(0, RecipeForm.this.element.modeCount - 1);
                                SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                if (event.shouldSubmitSound()) {
                                    this.playTickSound();
                                }
                                RecipeForm.this.updateSubtitleText();
                            }
                            event.use();
                        }
                    }
                }

                @Override
                public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
                    ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
                }

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    RecipeForm.this.element.recipe.draw(this.getX(), this.getY(), perspective);
                    if (this.isHovering()) {
                        ListGameTooltips tooltips = new ListGameTooltips(RecipeForm.this.element.recipe.getTooltip(null, perspective, new GameBlackboard()));
                        if (Input.lastInputIsController) {
                            tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "configurebutton")));
                            tooltips.add(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "increasebutton")));
                            tooltips.add(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "decreasebutton")));
                        }
                        GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                    }
                }
            });
            String defaultName = this.element.recipe.resultItem.getItemDisplayName();
            this.label = this.addComponent(new FormLabelEdit(this.element.name == null ? defaultName : this.element.name, new FontOptions(20), this.getInterfaceStyle().activeTextColor, 57, 0, 100, 30), -1000);
            this.renameButton = this.addComponent(new FormContentIconButton(buttonX, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_rename, new GameMessage[0]));
            AtomicBoolean isTyping = new AtomicBoolean(false);
            this.label.onMouseChangedTyping(e -> {
                isTyping.set(this.label.isTyping());
                this.runRenameUpdate();
            });
            this.label.onSubmit(e -> {
                isTyping.set(this.label.isTyping());
                this.runRenameUpdate();
            });
            this.renameButton.onClicked(e -> {
                isTyping.set(!this.label.isTyping());
                this.label.setTyping(!this.label.isTyping());
                this.runRenameUpdate();
            });
            this.runRenameUpdate();
            this.label.setWidth((buttonX -= 26) - 4 - 32);
            FontOptions subtitleOptions = new FontOptions(16);
            this.subtitle = this.addComponent(new FormFairTypeLabel("", 57, 22).setFontOptions(subtitleOptions));
            this.subtitle.setParsers(TypeParsers.replaceParser("[[-]]", new FairButtonGlyph(16, 16){

                @Override
                public void handleEvent(float drawX, float drawY, InputEvent event) {
                    if ((event.getID() == -100 || event.isRepeatEvent(repeatEventDec)) && event.state) {
                        event.startRepeatEvents(repeatEventDec);
                        int amount = 1;
                        if (Control.INV_QUICK_MOVE.isDown()) {
                            amount = 10;
                        } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                            amount = 100;
                        }
                        RecipeForm.this.element.modeCount = Math.max(0, RecipeForm.this.element.modeCount - amount);
                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                        if (event.shouldSubmitSound()) {
                            RecipeForm.this.playTickSound();
                        }
                        RecipeForm.this.updateSubtitleText();
                    }
                }

                @Override
                public void draw(float x, float y, Color defaultColor) {
                    Color color = this.isHovering() ? (Color)RecipeForm.this.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)RecipeForm.this.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.ACTIVE);
                    RecipeForm.this.getInterfaceStyle().button_minus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                    if (this.isHovering()) {
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }), TypeParsers.replaceParser("[[+]]", new FairButtonGlyph(16, 16){

                @Override
                public void handleEvent(float drawX, float drawY, InputEvent event) {
                    if ((event.getID() == -100 || event.isRepeatEvent(repeatEventInc)) && event.state) {
                        event.startRepeatEvents(repeatEventInc);
                        int amount = 1;
                        if (Control.INV_QUICK_MOVE.isDown()) {
                            amount = 10;
                        } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                            amount = 100;
                        }
                        RecipeForm.this.element.modeCount = Math.min(65535, RecipeForm.this.element.modeCount + amount);
                        SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                        if (event.shouldSubmitSound()) {
                            RecipeForm.this.playTickSound();
                        }
                        RecipeForm.this.updateSubtitleText();
                    }
                }

                @Override
                public void draw(float x, float y, Color defaultColor) {
                    Color color = this.isHovering() ? (Color)RecipeForm.this.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)RecipeForm.this.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.ACTIVE);
                    RecipeForm.this.getInterfaceStyle().button_plus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                    if (this.isHovering()) {
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }), TypeParsers.replaceParser("[[v]]", new FairButtonGlyph(16, 16){

                @Override
                public void handleEvent(float drawX, float drawY, InputEvent event) {
                    if (event.getID() == -100) {
                        if (event.state) {
                            RecipeForm.this.playTickSound();
                            SelectionFloatMenu menu = new SelectionFloatMenu(RecipeForm.this);
                            menu.add(SettlementWorkstationRecipe.Mode.DO_COUNT.countMessageFunction.apply("X").translate(), () -> {
                                if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_COUNT) {
                                    RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_COUNT;
                                    SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                    RecipeForm.this.updateSubtitleText();
                                }
                                menu.remove();
                            });
                            menu.add(SettlementWorkstationRecipe.Mode.DO_UNTIL.countMessageFunction.apply("X").translate(), () -> {
                                if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_UNTIL) {
                                    RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_UNTIL;
                                    SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                    RecipeForm.this.updateSubtitleText();
                                }
                                menu.remove();
                            });
                            menu.add(SettlementWorkstationRecipe.Mode.DO_FOREVER.countMessageFunction.apply("X").translate(), () -> {
                                if (RecipeForm.this.element.mode != SettlementWorkstationRecipe.Mode.DO_FOREVER) {
                                    RecipeForm.this.element.mode = SettlementWorkstationRecipe.Mode.DO_FOREVER;
                                    SettlementWorkstationConfigForm.this.onSubmitUpdate(RecipeForm.this.index, RecipeForm.this.element);
                                    RecipeForm.this.updateSubtitleText();
                                }
                                menu.remove();
                            });
                            RecipeForm.this.getManager().openFloatMenu((FloatMenu)menu, (int)drawX - event.pos.hudX, (int)drawY - event.pos.hudY - 12);
                        }
                        event.use();
                    }
                }

                @Override
                public void draw(float x, float y, Color defaultColor) {
                    Color color;
                    GameTexture texture;
                    if (this.isHovering()) {
                        texture = RecipeForm.this.getInterfaceStyle().config_icon.highlighted;
                        color = RecipeForm.this.getInterfaceStyle().highlightTextColor;
                    } else {
                        texture = RecipeForm.this.getInterfaceStyle().config_icon.active;
                        color = RecipeForm.this.getInterfaceStyle().activeTextColor;
                    }
                    texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                    if (this.isHovering()) {
                        GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("ui", "configurebutton")), TooltipLocation.FORM_FOCUS);
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }));
            this.updateSubtitleText();
            this.updateIndex(index);
        }

        private void updateSubtitleText() {
            GameMessageBuilder text = new GameMessageBuilder().append("[[v]] ").append(this.element.mode.countMessageFunction.apply("[[-]] " + this.element.modeCount + " [[+]]"));
            this.subtitle.setText(text);
        }

        private void runRenameUpdate() {
            if (this.label.isTyping()) {
                this.renameButton.setIcon(this.getInterfaceStyle().container_rename_save);
                this.renameButton.setTooltips(new LocalMessage("ui", "recipesavename"));
            } else {
                String name;
                String defaultName = this.element.recipe.resultItem.getItemDisplayName();
                String string = name = this.element.name == null ? defaultName : this.element.name;
                if (!this.label.getText().equals(name)) {
                    if (this.label.getText().isEmpty() || this.label.getText().equals(defaultName)) {
                        this.label.setText(defaultName);
                        if (this.element.name != null) {
                            this.element.name = null;
                            SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index, this.element);
                        }
                    } else {
                        this.element.name = this.label.getText();
                        SettlementWorkstationConfigForm.this.onSubmitUpdate(this.index, this.element);
                    }
                }
                this.renameButton.setIcon(this.getInterfaceStyle().container_rename);
                this.renameButton.setTooltips(new LocalMessage("ui", "recipechangename"));
            }
        }

        public void update(PacketReader reader) {
            this.element.applyPacket(reader);
            this.updateSubtitleText();
            if (!this.label.isTyping()) {
                String defaultName = this.element.recipe.resultItem.getItemDisplayName();
                this.label.setText(this.element.name == null ? defaultName : this.element.name);
            }
        }

        public void updateIndex(int newIndex) {
            this.index = newIndex;
            this.moveUpButton.setActive(this.index > 0);
            this.moveDownButton.setActive(this.index < SettlementWorkstationConfigForm.this.recipes.size() - 1);
        }
    }

    private class ConfigData {
        public final ArrayList<SettlementWorkstationRecipe> recipes;

        public ConfigData(ArrayList<SettlementWorkstationRecipe> recipes) {
            this.recipes = recipes;
        }

        public ConfigData(LoadData save) {
            this.recipes = new ArrayList();
            for (LoadData data : save.getLoadDataByName("RECIPE")) {
                SettlementWorkstationRecipe recipe = new SettlementWorkstationRecipe(data, false);
                if (!SettlementWorkstationConfigForm.this.workstationObject.streamSettlementRecipes().anyMatch(r -> r.getRecipeHash() == recipe.recipe.getRecipeHash())) continue;
                this.recipes.add(recipe);
            }
        }

        public SaveData getSaveData() {
            SaveData save = new SaveData("config");
            for (SettlementWorkstationRecipe recipe : this.recipes) {
                SaveData recipeData = new SaveData("RECIPE");
                recipe.addSaveData(recipeData, false);
                save.addSaveData(recipeData);
            }
            return save;
        }
    }
}

