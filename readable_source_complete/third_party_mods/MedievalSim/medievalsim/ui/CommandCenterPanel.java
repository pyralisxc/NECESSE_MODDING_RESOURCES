/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.world.WorldSettings
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import medievalsim.MedievalSimSettings;
import medievalsim.commandcenter.CommandCategory;
import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import medievalsim.commandcenter.wrapper.NecesseCommandMetadata;
import medievalsim.commandcenter.wrapper.NecesseCommandRegistry;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.CoordinatePairWidget;
import medievalsim.commandcenter.wrapper.widgets.CoordinatePairWrapperWidget;
import medievalsim.commandcenter.wrapper.widgets.MultiChoiceWidget;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidgetFactory;
import medievalsim.commandcenter.wrapper.widgets.PlayerDropdownWidget;
import medievalsim.commandcenter.wrapper.widgets.RelativeIntInputWidget;
import medievalsim.packets.PacketExecuteCommand;
import medievalsim.ui.SearchableDropdown;
import medievalsim.util.ModLogger;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class CommandCenterPanel {
    private static final FontOptions WHITE_TEXT_20 = new FontOptions(20).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_14 = new FontOptions(14).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_11 = new FontOptions(11).color(Color.WHITE);
    private static final int MARGIN = 10;
    private final Client client;
    private final Runnable onBackCallback;
    private MedievalSimSettings settings;
    private Form parentForm;
    private FormLabel titleLabel;
    private FormTextButton consoleCommandsTabButton;
    private FormTextButton modSettingsTabButton;
    private FormTextButton commandHistoryTabButton;
    private SearchableDropdown<NecesseCommandMetadata> commandDropdown;
    private FormDropdownSelectionButton<CommandCategory> categoryFilter;
    private FormContentBox favoriteButtonsBox;
    private FormLabel commandInfoLabel;
    private FormTextButton favoriteToggleButton;
    private FormContentBox parameterScrollArea;
    private FormTextButton clearButton;
    private FormTextButton executeButton;
    private FormTextButton backButton;
    private NecesseCommandMetadata currentCommand;
    private Map<String, Map<String, String>> commandParameterCache;
    private List<ParameterWidget> currentParameterWidgets;
    private List<FormComponent> allComponents;

    public CommandCenterPanel(Client client, Runnable onBackCallback) {
        this.client = client;
        this.onBackCallback = onBackCallback;
        this.commandParameterCache = new HashMap<String, Map<String, String>>();
        this.currentParameterWidgets = new ArrayList<ParameterWidget>();
        this.allComponents = new ArrayList<FormComponent>();
        LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> m.id.equals("medieval.sim")).findFirst().orElse(null);
        this.settings = mod != null ? (MedievalSimSettings)mod.getSettings() : new MedievalSimSettings();
    }

    public void tick(TickManager tickManager) {
        if (this.commandDropdown != null) {
            this.commandDropdown.tick(tickManager);
        }
        if (this.currentParameterWidgets != null) {
            for (ParameterWidget widget : this.currentParameterWidgets) {
                if (widget instanceof MultiChoiceWidget) {
                    ((MultiChoiceWidget)widget).updateSelectionIfChanged();
                    continue;
                }
                if (!(widget instanceof PlayerDropdownWidget)) continue;
                ((PlayerDropdownWidget)widget).tick(tickManager);
            }
        }
        if (this.clearButton != null && this.executeButton != null) {
            this.updateButtonStates();
        }
    }

    public void buildComponents(Form parentForm, int startX, int startY, int width, int height) {
        this.parentForm = parentForm;
        int currentY = startY + 10;
        int contentWidth = width - 20;
        this.titleLabel = new FormLabel("Command Center", WHITE_TEXT_20, -1, startX + 10, currentY, contentWidth);
        parentForm.addComponent((FormComponent)this.titleLabel);
        this.allComponents.add((FormComponent)this.titleLabel);
        int tabY = currentY += 35;
        int tabX = startX + 10;
        int tabWidth = 150;
        int tabSpacing = 5;
        this.consoleCommandsTabButton = new FormTextButton("Console Commands", tabX, tabY, tabWidth, FormInputSize.SIZE_32, this.settings.activeTab == 0 ? ButtonColor.RED : ButtonColor.BASE);
        this.consoleCommandsTabButton.onClicked(e -> this.switchToTab(0, parentForm, startX, startY, width, height));
        parentForm.addComponent((FormComponent)this.consoleCommandsTabButton);
        this.allComponents.add((FormComponent)this.consoleCommandsTabButton);
        this.modSettingsTabButton = new FormTextButton("Mod Settings", tabX + tabWidth + tabSpacing, tabY, tabWidth, FormInputSize.SIZE_32, this.settings.activeTab == 1 ? ButtonColor.RED : ButtonColor.BASE);
        this.modSettingsTabButton.onClicked(e -> this.switchToTab(1, parentForm, startX, startY, width, height));
        parentForm.addComponent((FormComponent)this.modSettingsTabButton);
        this.allComponents.add((FormComponent)this.modSettingsTabButton);
        this.commandHistoryTabButton = new FormTextButton("Command History", tabX + (tabWidth + tabSpacing) * 2, tabY, tabWidth, FormInputSize.SIZE_32, this.settings.activeTab == 2 ? ButtonColor.RED : ButtonColor.BASE);
        this.commandHistoryTabButton.onClicked(e -> this.switchToTab(2, parentForm, startX, startY, width, height));
        parentForm.addComponent((FormComponent)this.commandHistoryTabButton);
        this.allComponents.add((FormComponent)this.commandHistoryTabButton);
        this.buildCurrentTabContent(parentForm, startX, currentY += 45, width, height);
    }

    private void switchToTab(int tabIndex, Form parentForm, int startX, int startY, int width, int height) {
        if (this.settings.activeTab == tabIndex) {
            return;
        }
        this.settings.activeTab = tabIndex;
        Settings.saveClientSettings();
        this.removeComponents(parentForm);
        this.buildComponents(parentForm, startX, startY, width, height);
    }

    private void buildCurrentTabContent(Form parentForm, int startX, int startY, int width, int height) {
        switch (this.settings.activeTab) {
            case 0: {
                this.buildConsoleCommandsTab(parentForm, startX, startY, width, height);
                break;
            }
            case 1: {
                this.buildModSettingsTab(parentForm, startX, startY, width, height);
                break;
            }
            case 2: {
                this.buildCommandHistoryTab(parentForm, startX, startY, width, height);
            }
        }
    }

    private void clearTabContent(Form parentForm) {
        if (this.categoryFilter != null) {
            parentForm.removeComponent(this.categoryFilter);
            this.categoryFilter = null;
        }
        if (this.commandDropdown != null) {
            this.commandDropdown.removeFromForm(parentForm);
            this.commandDropdown = null;
        }
        if (this.favoriteButtonsBox != null) {
            parentForm.removeComponent((FormComponent)this.favoriteButtonsBox);
            this.favoriteButtonsBox = null;
        }
        if (this.commandInfoLabel != null) {
            parentForm.removeComponent((FormComponent)this.commandInfoLabel);
            this.commandInfoLabel = null;
        }
        if (this.favoriteToggleButton != null) {
            parentForm.removeComponent((FormComponent)this.favoriteToggleButton);
            this.favoriteToggleButton = null;
        }
        if (this.parameterScrollArea != null) {
            parentForm.removeComponent((FormComponent)this.parameterScrollArea);
            this.parameterScrollArea = null;
        }
        if (this.clearButton != null) {
            parentForm.removeComponent((FormComponent)this.clearButton);
            this.clearButton = null;
        }
        if (this.executeButton != null) {
            parentForm.removeComponent((FormComponent)this.executeButton);
            this.executeButton = null;
        }
        if (this.backButton != null) {
            parentForm.removeComponent((FormComponent)this.backButton);
            this.backButton = null;
        }
        this.clearParameters();
    }

    private void buildConsoleCommandsTab(Form parentForm, int startX, int startY, int width, int height) {
        int currentY = startY;
        int contentWidth = width - 20;
        FormLabel categoryLabel = new FormLabel("Filter by category:", WHITE_TEXT_11, -1, startX + 10, currentY, 120);
        parentForm.addComponent((FormComponent)categoryLabel);
        this.allComponents.add((FormComponent)categoryLabel);
        this.categoryFilter = new FormDropdownSelectionButton(startX + 10 + 130, currentY - 5, FormInputSize.SIZE_20, ButtonColor.BASE, 150, (GameMessage)new StaticMessage("All Categories"));
        this.categoryFilter.options.add(null, (GameMessage)new StaticMessage("All Categories"));
        if (!NecesseCommandRegistry.isInitialized()) {
            ModLogger.warn("Command Registry not initialized when building Command Center - displaying empty interface");
        }
        ArrayList<NecesseCommandMetadata> allCommands = new ArrayList<NecesseCommandMetadata>(NecesseCommandRegistry.getAllCommands());
        HashSet<CommandCategory> categoriesWithCommands = new HashSet<CommandCategory>();
        for (NecesseCommandMetadata cmd2 : allCommands) {
            if (!cmd2.isAvailableInWorld(this.client)) continue;
            categoriesWithCommands.add(cmd2.getCategory());
        }
        for (CommandCategory cat : CommandCategory.values()) {
            if (!categoriesWithCommands.contains((Object)cat)) continue;
            this.categoryFilter.options.add((Object)cat, (GameMessage)new StaticMessage(cat.getDisplayName()));
        }
        if (this.settings.lastSelectedCategory != null && !this.settings.lastSelectedCategory.isEmpty()) {
            try {
                CommandCategory savedCategory = CommandCategory.valueOf(this.settings.lastSelectedCategory);
                this.categoryFilter.setSelected((Object)savedCategory, (GameMessage)new StaticMessage(savedCategory.getDisplayName()));
            }
            catch (IllegalArgumentException savedCategory) {
                // empty catch block
            }
        }
        this.categoryFilter.onSelected(event -> {
            CommandCategory selectedCategory = (CommandCategory)((Object)((Object)event.value));
            this.settings.lastSelectedCategory = selectedCategory != null ? selectedCategory.name() : "";
            Settings.saveClientSettings();
            this.rebuildCommandDropdown(parentForm, startX, contentWidth, selectedCategory);
        });
        parentForm.addComponent(this.categoryFilter);
        this.allComponents.add((FormComponent)this.categoryFilter);
        currentY += 30;
        CommandCategory initialCategory = null;
        if (this.settings.lastSelectedCategory != null && !this.settings.lastSelectedCategory.isEmpty()) {
            try {
                initialCategory = CommandCategory.valueOf(this.settings.lastSelectedCategory);
            }
            catch (IllegalArgumentException cmd2) {
                // empty catch block
            }
        }
        List<NecesseCommandMetadata> displayCommands = this.getFilteredCommands(initialCategory);
        displayCommands.sort((a, b) -> a.getId().compareTo(b.getId()));
        this.commandDropdown = new SearchableDropdown<NecesseCommandMetadata>(startX + 10, currentY, contentWidth, 200, "Search commands...", displayCommands, cmd -> cmd.getId(), selectedCmd -> {
            if (selectedCmd != null) {
                this.loadCommand((NecesseCommandMetadata)selectedCmd);
            }
        });
        this.commandDropdown.addToForm(parentForm);
        this.favoriteButtonsBox = new FormContentBox(startX + 10, currentY += 40, contentWidth, 40);
        parentForm.addComponent((FormComponent)this.favoriteButtonsBox);
        this.allComponents.add((FormComponent)this.favoriteButtonsBox);
        this.buildFavoriteButtons();
        this.commandInfoLabel = new FormLabel("Select a command...", WHITE_TEXT_11, -1, startX + 10, currentY += 50, contentWidth - 120);
        parentForm.addComponent((FormComponent)this.commandInfoLabel);
        this.allComponents.add((FormComponent)this.commandInfoLabel);
        this.favoriteToggleButton = new FormTextButton("Add to Favorites", startX + width - 10 - 100, currentY - 5, 100, FormInputSize.SIZE_16, ButtonColor.BASE);
        this.favoriteToggleButton.onClicked(e -> this.toggleFavorite());
        this.favoriteToggleButton.setActive(false);
        parentForm.addComponent((FormComponent)this.favoriteToggleButton);
        this.allComponents.add((FormComponent)this.favoriteToggleButton);
        int buttonRowHeight = 40;
        int buttonY = height - 10 - buttonRowHeight;
        int buttonWidth = (contentWidth - 10) / 3;
        int availableHeight = buttonY - (currentY += 35) - 10;
        this.parameterScrollArea = new FormContentBox(startX + 10, currentY, contentWidth, availableHeight);
        parentForm.addComponent((FormComponent)this.parameterScrollArea);
        this.allComponents.add((FormComponent)this.parameterScrollArea);
        this.clearButton = new FormTextButton("Clear", startX + 10, buttonY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
        this.clearButton.onClicked(e -> this.clearParameters());
        parentForm.addComponent((FormComponent)this.clearButton);
        this.allComponents.add((FormComponent)this.clearButton);
        this.executeButton = new FormTextButton("Execute", startX + 10 + buttonWidth + 5, buttonY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
        this.executeButton.onClicked(e -> this.executeCommand());
        parentForm.addComponent((FormComponent)this.executeButton);
        this.allComponents.add((FormComponent)this.executeButton);
        this.backButton = new FormTextButton("Back", startX + 10 + buttonWidth * 2 + 10, buttonY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
        this.backButton.onClicked(e -> {
            if (this.onBackCallback != null) {
                this.onBackCallback.run();
            }
        });
        parentForm.addComponent((FormComponent)this.backButton);
        this.allComponents.add((FormComponent)this.backButton);
        this.updateButtonStates();
    }

    public void removeComponents(Form parentForm) {
        if (this.commandDropdown != null) {
            this.commandDropdown.removeFromForm(parentForm);
        }
        for (FormComponent component : this.allComponents) {
            parentForm.removeComponent(component);
        }
        this.allComponents.clear();
    }

    private void buildModSettingsTab(Form parentForm, int startX, int startY, int width, int height) {
        int currentY = startY;
        int contentWidth = width - 20;
        FormLabel placeholderLabel = new FormLabel("Mod Settings Tab - Coming Soon", WHITE_TEXT_14, -1, startX + 10, currentY, contentWidth);
        parentForm.addComponent((FormComponent)placeholderLabel);
        this.allComponents.add((FormComponent)placeholderLabel);
        FormLabel infoLabel = new FormLabel("This tab will display editable settings for Medieval Sim mod.", WHITE_TEXT_11, -1, startX + 10, currentY += 30, contentWidth);
        parentForm.addComponent((FormComponent)infoLabel);
        this.allComponents.add((FormComponent)infoLabel);
    }

    private void buildCommandHistoryTab(Form parentForm, int startX, int startY, int width, int height) {
        int currentY = startY;
        int contentWidth = width - 20;
        FormLabel titleLabel = new FormLabel("Command History (Last 20)", WHITE_TEXT_14, -1, startX + 10, currentY, contentWidth - 100);
        parentForm.addComponent((FormComponent)titleLabel);
        this.allComponents.add((FormComponent)titleLabel);
        FormTextButton clearHistoryButton = new FormTextButton("Clear History", startX + width - 10 - 100, currentY - 5, 100, FormInputSize.SIZE_20, ButtonColor.RED);
        clearHistoryButton.onClicked(e -> {
            this.settings.commandHistory.clear();
            Settings.saveClientSettings();
            this.clearTabContent(parentForm);
            this.buildCommandHistoryTab(parentForm, startX, startY, width, height);
        });
        parentForm.addComponent((FormComponent)clearHistoryButton);
        this.allComponents.add((FormComponent)clearHistoryButton);
        int availableHeight = height - (currentY += 35) - 10;
        FormContentBox historyBox = new FormContentBox(startX + 10, currentY, contentWidth, availableHeight);
        parentForm.addComponent((FormComponent)historyBox);
        this.allComponents.add((FormComponent)historyBox);
        if (this.settings.commandHistory == null || this.settings.commandHistory.isEmpty()) {
            FormLabel emptyLabel = new FormLabel("No commands executed yet. Execute a command from the Console Commands tab.", WHITE_TEXT_11, -1, 10, 20, contentWidth - 20);
            historyBox.addComponent((FormComponent)emptyLabel);
        } else {
            int entryY = 10;
            int index = 1;
            for (String commandString : this.settings.commandHistory) {
                Object displayText = commandString;
                if (((String)displayText).length() > 60) {
                    displayText = ((String)displayText).substring(0, 57) + "...";
                }
                FormLabel commandLabel = new FormLabel("#" + index + ": " + (String)displayText, WHITE_TEXT_11, -1, 10, entryY, contentWidth - 120);
                historyBox.addComponent((FormComponent)commandLabel);
                FormTextButton reExecuteButton = new FormTextButton("Re-Execute", contentWidth - 100, entryY - 3, 90, FormInputSize.SIZE_16, ButtonColor.BASE);
                String cmdToExecute = commandString;
                reExecuteButton.onClicked(e -> this.client.network.sendPacket((Packet)new PacketExecuteCommand(cmdToExecute)));
                historyBox.addComponent((FormComponent)reExecuteButton);
                entryY += 25;
                ++index;
            }
            int contentHeight = Math.max(entryY + 10, historyBox.getHeight());
            historyBox.setContentBox(new Rectangle(0, 0, contentWidth, contentHeight));
        }
    }

    private List<NecesseCommandMetadata> getFilteredCommands(CommandCategory category) {
        ArrayList<NecesseCommandMetadata> allCommands = new ArrayList<NecesseCommandMetadata>(NecesseCommandRegistry.getAllCommands());
        ArrayList<NecesseCommandMetadata> filtered = new ArrayList<NecesseCommandMetadata>();
        for (NecesseCommandMetadata cmd : allCommands) {
            if (!cmd.isAvailableInWorld(this.client) || category != null && cmd.getCategory() != category) continue;
            filtered.add(cmd);
        }
        return filtered;
    }

    private void rebuildCommandDropdown(Form parentForm, int startX, int contentWidth, CommandCategory category) {
        if (this.commandDropdown == null) {
            return;
        }
        List<NecesseCommandMetadata> filteredCommands = this.getFilteredCommands(category);
        filteredCommands.sort((a, b) -> a.getId().compareTo(b.getId()));
        this.commandDropdown.beginUpdate();
        this.commandDropdown.updateItems(filteredCommands);
        this.commandDropdown.endUpdate();
    }

    private void buildFavoriteButtons() {
        int spacing;
        this.favoriteButtonsBox.clearComponents();
        if (this.settings == null || this.settings.favoriteCommands.isEmpty()) {
            this.favoriteButtonsBox.addComponent((FormComponent)new FormLabel("No favorites yet. Select a command and click 'Add to Favorites'.", WHITE_TEXT_11, -1, 5, 10, this.favoriteButtonsBox.getWidth() - 10));
            return;
        }
        int buttonWidth = 80;
        int x = spacing = 5;
        for (String favoriteId : this.settings.favoriteCommands) {
            NecesseCommandMetadata cmd = this.findCommandById(favoriteId);
            if (cmd == null) continue;
            FormTextButton favBtn = new FormTextButton(favoriteId, x, 5, buttonWidth, FormInputSize.SIZE_16, ButtonColor.BASE);
            favBtn.onClicked(e -> {
                this.commandDropdown.setSelectedItem(cmd);
                this.loadCommand(cmd);
            });
            this.favoriteButtonsBox.addComponent((FormComponent)favBtn);
            if ((x += buttonWidth + spacing) + buttonWidth <= this.favoriteButtonsBox.getWidth()) continue;
            break;
        }
        this.favoriteButtonsBox.setContentBox(new Rectangle(0, 0, this.favoriteButtonsBox.getWidth(), 40));
    }

    private void loadCommand(NecesseCommandMetadata command) {
        if (command == null) {
            return;
        }
        this.currentCommand = command;
        this.commandInfoLabel.setText(command.getAction());
        boolean isFavorite = this.settings != null && this.settings.favoriteCommands.contains(command.getId());
        this.favoriteToggleButton.setText(isFavorite ? "Remove Favorite" : "Add to Favorites");
        this.favoriteToggleButton.setActive(true);
        this.cleanupParameterWidgets();
        this.parameterScrollArea.clearComponents();
        this.currentParameterWidgets.clear();
        List<ParameterMetadata> params = command.getParameters();
        if (params == null || params.isEmpty()) {
            this.parameterScrollArea.addComponent((FormComponent)new FormLabel("This command has no parameters.", WHITE_TEXT_11, -1, 10, 10, this.parameterScrollArea.getWidth() - 20));
        } else {
            int yPos = 10;
            int widgetX = 10;
            int widgetWidth = this.parameterScrollArea.getWidth() - 20;
            int i = 0;
            while (i < params.size()) {
                int actualWidgetY;
                int actualWidgetX;
                boolean needsParentFormCoordinates;
                ParameterMetadata param = params.get(i);
                if (!param.isPartOfUsage()) {
                    ++i;
                    continue;
                }
                boolean isCoordinatePair = false;
                ParameterMetadata nextParam = null;
                if (i + 1 < params.size()) {
                    nextParam = params.get(i + 1);
                    isCoordinatePair = this.isXYCoordinatePair(param, nextParam);
                }
                if (isCoordinatePair) {
                    this.addCoordinatePairWidget(param, nextParam, widgetX, yPos, this.parameterScrollArea);
                    yPos += 80;
                    i += 2;
                    continue;
                }
                Object labelText = param.getDisplayName();
                labelText = param.isRequired() ? (String)labelText + " (Required)" : (String)labelText + " (Optional)";
                FormLabel paramLabel = new FormLabel((String)labelText, WHITE_TEXT_14, -1, widgetX, yPos, widgetWidth);
                this.parameterScrollArea.addComponent((FormComponent)paramLabel);
                String hintText = this.getParameterHint(param);
                FormLabel hintLabel = new FormLabel(hintText, WHITE_TEXT_11, -1, widgetX, yPos += 20, widgetWidth);
                this.parameterScrollArea.addComponent((FormComponent)hintLabel);
                yPos += 18;
                boolean bl = needsParentFormCoordinates = param.getHandlerType() == ParameterMetadata.ParameterHandlerType.ITEM || param.getHandlerType() == ParameterMetadata.ParameterHandlerType.BUFF || param.getHandlerType() == ParameterMetadata.ParameterHandlerType.ENCHANTMENT || param.getHandlerType() == ParameterMetadata.ParameterHandlerType.BIOME || param.getHandlerType() == ParameterMetadata.ParameterHandlerType.TILE;
                if (needsParentFormCoordinates) {
                    actualWidgetX = this.parameterScrollArea.getX() + widgetX;
                    actualWidgetY = this.parameterScrollArea.getY() + yPos;
                } else {
                    actualWidgetX = widgetX;
                    actualWidgetY = yPos;
                }
                ParameterWidget widget = ParameterWidgetFactory.createWidget(param, actualWidgetX, actualWidgetY, this.client, command.getId());
                widget.setOnValueChanged(() -> {
                    widget.validate();
                    this.updateButtonStates();
                });
                Map<String, String> cachedValues = this.commandParameterCache.get(command.getId());
                if (cachedValues != null && cachedValues.containsKey(param.getName())) {
                    widget.setValue(cachedValues.get(param.getName()));
                }
                if (widget instanceof MultiChoiceWidget) {
                    MultiChoiceWidget multiWidget = (MultiChoiceWidget)widget;
                    multiWidget.setParentForm(this.parameterScrollArea);
                    this.parameterScrollArea.addComponent(multiWidget.getComponent());
                    FormComponent subComponent = multiWidget.getSelectedSubComponent();
                    if (subComponent != null) {
                        this.parameterScrollArea.addComponent(subComponent);
                    }
                } else if (widget instanceof PlayerDropdownWidget) {
                    PlayerDropdownWidget playerWidget = (PlayerDropdownWidget)widget;
                    this.parameterScrollArea.addComponent((FormComponent)playerWidget.getTextInput());
                    this.parameterScrollArea.addComponent(playerWidget.getDropdown());
                    yPos += 25;
                } else if (widget instanceof RelativeIntInputWidget) {
                    RelativeIntInputWidget coordWidget = (RelativeIntInputWidget)widget;
                    coordWidget.setClient(this.client);
                    this.parameterScrollArea.addComponent(coordWidget.getComponent());
                    this.parameterScrollArea.addComponent((FormComponent)coordWidget.getClickWorldButton());
                    this.parameterScrollArea.addComponent((FormComponent)coordWidget.getUseCurrentButton());
                } else {
                    this.parameterScrollArea.addComponent(widget.getComponent());
                }
                this.currentParameterWidgets.add(widget);
                yPos += 45;
                ++i;
            }
        }
        int contentHeight = Math.max(params != null ? params.size() * 85 + 20 : 50, this.parameterScrollArea.getHeight());
        this.parameterScrollArea.setContentBox(new Rectangle(0, 0, this.parameterScrollArea.getWidth(), contentHeight));
        this.updateButtonStates();
    }

    private boolean isXYCoordinatePair(ParameterMetadata param1, ParameterMetadata param2) {
        if (param1.getHandlerType() != ParameterMetadata.ParameterHandlerType.RELATIVE_INT || param2.getHandlerType() != ParameterMetadata.ParameterHandlerType.RELATIVE_INT) {
            return false;
        }
        String name1 = param1.getName().toLowerCase();
        String name2 = param2.getName().toLowerCase();
        return name1.contains("x") && name2.contains("y") || name1.equals("tilex") && name2.equals("tiley") || name1.equals("tilexoffset") && name2.equals("tileyoffset");
    }

    private void addCoordinatePairWidget(ParameterMetadata xParam, ParameterMetadata yParam, int widgetX, int yPos, FormContentBox scrollArea) {
        String labelText = "Coordinates (X, Y)" + (xParam.isRequired() || yParam.isRequired() ? " *" : "");
        FormLabel paramLabel = new FormLabel(labelText, WHITE_TEXT_14, -1, widgetX, yPos, scrollArea.getWidth() - 20);
        scrollArea.addComponent((FormComponent)paramLabel);
        FormLabel hintLabel = new FormLabel("Enter coordinates or use buttons to select from map (supports %+N relative syntax)", WHITE_TEXT_11, -1, widgetX, yPos += 20, scrollArea.getWidth() - 20);
        scrollArea.addComponent((FormComponent)hintLabel);
        CoordinatePairWidget coordWidget = new CoordinatePairWidget(widgetX, yPos += 18, xParam.getName(), yParam.getName(), xParam.isRequired(), yParam.isRequired());
        coordWidget.setClient(this.client);
        for (FormComponent component : coordWidget.getComponents()) {
            scrollArea.addComponent(component);
        }
        CoordinatePairWrapperWidget xWrapper = new CoordinatePairWrapperWidget(xParam, coordWidget, true);
        CoordinatePairWrapperWidget yWrapper = new CoordinatePairWrapperWidget(yParam, coordWidget, false);
        Map<String, String> cachedValues = this.commandParameterCache.get(this.currentCommand.getId());
        if (cachedValues != null) {
            if (cachedValues.containsKey(xParam.getName())) {
                coordWidget.setXValue(cachedValues.get(xParam.getName()));
            }
            if (cachedValues.containsKey(yParam.getName())) {
                coordWidget.setYValue(cachedValues.get(yParam.getName()));
            }
        }
        this.currentParameterWidgets.add(xWrapper);
        this.currentParameterWidgets.add(yWrapper);
    }

    private String getParameterHint(ParameterMetadata param) {
        switch (param.getHandlerType()) {
            case SERVER_CLIENT: {
                return "Select a player or enter username (use 'self' for yourself)";
            }
            case ITEM: {
                return "Enter an item ID (e.g., woodsword) or search below";
            }
            case BUFF: {
                return "Enter a buff ID (e.g., fire) or search below";
            }
            case RELATIVE_INT: {
                return "Enter a number (use %+10 or %-5 for relative to current position)";
            }
            case INT: {
                String intDefault = this.getParameterDefaultHint(param);
                if (param.isOptional() && intDefault != null) {
                    return "Enter a whole number (e.g., 100) - Default: " + intDefault;
                }
                return param.isOptional() ? "Enter a whole number (e.g., 100) - Leave empty to use default" : "Enter a whole number (e.g., 100) - Required";
            }
            case FLOAT: {
                String floatDefault = this.getParameterDefaultHint(param);
                if (param.isOptional() && floatDefault != null) {
                    return "Enter a decimal number (e.g., 1.5) - Default: " + floatDefault;
                }
                return param.isOptional() ? "Enter a decimal number (e.g., 1.5) - Leave empty to use default" : "Enter a decimal number (e.g., 1.5) - Required";
            }
            case BOOL: {
                String currentValue = this.getCurrentBooleanValue(param.getName());
                if (currentValue != null) {
                    return "Current value: " + currentValue + " - Toggle to change";
                }
                return "Click checkbox to enable, uncheck to disable";
            }
            case STRING: {
                if (param.hasPresets()) {
                    return "Select from dropdown or enter text manually";
                }
                String stringDefault = this.getParameterDefaultHint(param);
                if (param.isOptional() && stringDefault != null) {
                    return "Enter text - Default: \"" + stringDefault + "\"";
                }
                return param.isOptional() ? "Enter text - Leave empty to use default" : "Enter text - Required";
            }
            case MULTI: {
                return "Select one option from the dropdown";
            }
            case ENUM: {
                return "Select a value from the dropdown";
            }
            case TEAM: {
                return "Enter team name (or select if dropdown available)";
            }
            case UNBAN: 
            case STORED_PLAYER: {
                return "Enter player name (supports offline players)";
            }
        }
        return param.isOptional() ? "Enter a value for " + param.getName() + " - Leave empty to use default" : "Enter a value for " + param.getName() + " - Required";
    }

    private String getParameterDefaultHint(ParameterMetadata param) {
        String name;
        if (param == null || !param.isOptional()) {
            return null;
        }
        switch (name = param.getName().toLowerCase()) {
            case "amount": 
            case "count": 
            case "quantity": {
                return "1";
            }
            case "damage": 
            case "health": 
            case "durability": {
                return "100";
            }
            case "radius": 
            case "range": 
            case "distance": {
                return "10";
            }
            case "speed": 
            case "velocity": {
                return "1.0";
            }
            case "time": 
            case "duration": 
            case "delay": {
                return "60 seconds";
            }
            case "level": 
            case "tier": {
                return "1";
            }
            case "permission": 
            case "permlevel": {
                return "PLAYER";
            }
            case "world": 
            case "worldname": {
                return "Current World";
            }
            case "team": {
                return "No Team";
            }
        }
        switch (param.getHandlerType()) {
            case INT: {
                return "0";
            }
            case FLOAT: {
                return "0.0";
            }
            case BOOL: {
                return "false";
            }
            case STRING: {
                return "empty";
            }
        }
        return null;
    }

    private String getCurrentBooleanValue(String paramName) {
        if (this.client == null || this.client.worldSettings == null) {
            return null;
        }
        WorldSettings settings = this.client.worldSettings;
        try {
            switch (paramName.toLowerCase()) {
                case "pausewhenempty": 
                case "pause": {
                    return "Not directly exposed";
                }
                case "creative": 
                case "creativemode": {
                    return settings.creativeMode ? "Enabled (Creative)" : "Disabled (Survival)";
                }
                case "cheats": 
                case "allowcheats": {
                    return settings.allowCheats ? "Enabled" : "Disabled";
                }
                case "hunger": 
                case "playerhunger": {
                    return settings.playerHunger ? "Enabled" : "Disabled";
                }
                case "mobspawns": 
                case "disablemobspawns": {
                    return settings.disableMobSpawns ? "Disabled" : "Enabled";
                }
                case "survivalmode": 
                case "survival": {
                    return settings.survivalMode ? "Enabled" : "Disabled";
                }
                case "pvp": 
                case "forcedpvp": {
                    return settings.forcedPvP ? "Forced" : "Optional";
                }
                case "mobai": 
                case "disablemobai": {
                    return settings.disableMobAI ? "Disabled" : "Enabled";
                }
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private void clearParameters() {
        if (this.currentCommand == null) {
            return;
        }
        for (ParameterWidget widget : this.currentParameterWidgets) {
            widget.setValue("");
        }
        this.commandParameterCache.remove(this.currentCommand.getId());
    }

    private void cleanupParameterWidgets() {
        if (this.parentForm == null) {
            return;
        }
        for (ParameterWidget widget : this.currentParameterWidgets) {
            WorldClickHandler handler;
            if (!(widget instanceof RelativeIntInputWidget) || !(handler = WorldClickHandler.getInstance()).isActive()) continue;
            handler.stopSelection();
            WorldClickIntegration.stopIntegration();
        }
    }

    private void executeCommand() {
        if (this.currentCommand == null) {
            return;
        }
        try {
            StringBuilder cmd = new StringBuilder("/");
            cmd.append(this.currentCommand.getId());
            for (int i = 0; i < this.currentParameterWidgets.size(); ++i) {
                ParameterWidget widget = this.currentParameterWidgets.get(i);
                String value = widget.getValue();
                if (value == null || value.trim().isEmpty()) continue;
                cmd.append(" ").append(value.trim());
            }
            String finalCommand = cmd.toString();
            this.client.network.sendPacket((Packet)new PacketExecuteCommand(finalCommand));
        }
        catch (Exception e) {
            ModLogger.error("Error building command: " + e.getMessage());
            this.client.chat.addMessage("\u00a7c[Command Center] Error: " + e.getMessage());
        }
    }

    private void toggleFavorite() {
        if (this.currentCommand == null || this.settings == null) {
            return;
        }
        String cmdId = this.currentCommand.getId();
        if (this.settings.favoriteCommands.contains(cmdId)) {
            this.settings.favoriteCommands.remove(cmdId);
            this.favoriteToggleButton.setText("Add to Favorites");
        } else {
            if (this.settings.favoriteCommands.size() >= 10) {
                System.out.println("[CommandCenterPanel] Max favorites reached (10)");
                return;
            }
            this.settings.favoriteCommands.add(cmdId);
            this.favoriteToggleButton.setText("Remove Favorite");
        }
        Settings.saveClientSettings();
        this.buildFavoriteButtons();
    }

    private void updateButtonStates() {
        boolean hasCommand;
        boolean canExecute = hasCommand = this.currentCommand != null;
        if (hasCommand && !this.currentParameterWidgets.isEmpty()) {
            for (ParameterWidget widget : this.currentParameterWidgets) {
                String value;
                if (!widget.getParameter().isRequired() || (value = widget.getValue()) != null && !value.trim().isEmpty()) continue;
                canExecute = false;
                break;
            }
        }
        if (this.clearButton != null) {
            this.clearButton.setActive(hasCommand && !this.currentParameterWidgets.isEmpty());
        }
        if (this.executeButton != null) {
            this.executeButton.setActive(canExecute);
        }
    }

    private NecesseCommandMetadata findCommandById(String id) {
        Collection<NecesseCommandMetadata> allCommands = NecesseCommandRegistry.getAllCommands();
        for (NecesseCommandMetadata cmd : allCommands) {
            if (!cmd.getId().equals(id)) continue;
            return cmd;
        }
        return null;
    }
}

