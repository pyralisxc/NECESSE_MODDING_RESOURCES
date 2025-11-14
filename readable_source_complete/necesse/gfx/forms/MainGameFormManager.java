/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketOpenJournal;
import necesse.engine.network.packet.PacketOpenPartyConfig;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketOpenQuests;
import necesse.engine.network.packet.PacketPlayerAutoOpenDoors;
import necesse.engine.network.packet.PacketPlayerHotbarLocked;
import necesse.engine.network.packet.PacketPlayerRespawnRequest;
import necesse.engine.network.packet.PacketRequestTravel;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.util.PointTreeSet;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.PositionedDrawOptionsBox;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormBuffHud;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContainerCraftingListContentBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormContentIconVarToggleButton;
import necesse.gfx.forms.components.FormExpressionWheel;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTabTextComponent;
import necesse.gfx.forms.components.containerSlot.FormContainerMountSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerPlayerArmorSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerToolbarSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerTrashSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerTrinketSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.ButtonToolbarForm;
import necesse.gfx.forms.presets.ChatBoxForm;
import necesse.gfx.forms.presets.CurrentModifiersForm;
import necesse.gfx.forms.presets.MapForm;
import necesse.gfx.forms.presets.PauseMenuForm;
import necesse.gfx.forms.presets.ScoreboardForm;
import necesse.gfx.forms.presets.SettlementNotificationForm;
import necesse.gfx.forms.presets.TestCrashReportForm;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.creative.CreativeMenuForm;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.sidebar.SidebarComponent;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;

public class MainGameFormManager
extends FormManager
implements ContinueComponentManager {
    private static final FontOptions totalArmorFontOptions = new FontOptions(12).color(Color.WHITE);
    private final MainGame mainGame;
    private final Client client;
    private boolean lastShowToolbar;
    private boolean lastInventoryExtended;
    private boolean lastIsDead;
    private boolean lastShowMap;
    private boolean lastIsRunning;
    private boolean lastCreativeMenuReplacesInventory;
    private TravelDir lastTravelDir;
    public Form inventory;
    public Form toolbar;
    public Form equipment;
    public Form leftQuickbar;
    public Form crafting;
    public Form travel;
    public Form death;
    public Form creative;
    public FormContentIconToggleButton creativeMenuToggle;
    public FormContentIconToggleButton inventoryToggle;
    public FormContentIconToggleButton godmodeToggle;
    private FormContentIconButton restockButton;
    private FormContentIconButton stackButton;
    private FormContentIconButton sortButton;
    public FormComponentList equipmentSetButtons;
    public ButtonToolbarForm rightQuickbar;
    public FormContentButton journalButton;
    public ChatBoxForm chat;
    public SettlementNotificationForm settlementNotifications;
    public ContainerComponent<?> focus;
    public FormComponentList buffsContainer;
    public FormBuffHud importantBuffs;
    public FormBuffHud unimportantBuffs;
    public PauseMenuForm pauseMenu;
    public ScoreboardForm scoreboard;
    private FormContentBox sidebarBox;
    private final LinkedList<SidebarComponent> sidebar;
    public MapForm minimap;
    public MapForm fullMap;
    private TutorialSidebarForm tutorialSidebar;
    private CurrentModifiersForm modifiersForm;
    private static Point lastModifiersFormPos;
    private final LinkedHashMap<String, ContinueComponent> continueForms = new LinkedHashMap();
    private FormExpressionWheel expressionWheel;
    public DebugForm debugForm;
    public CreativeMenuForm creativeMenu;
    private FormLocalTextButton travelButton;
    private FormLocalTextButton respawnButton;
    private long respawnButtonCD;
    private InventoryItem lastCheckedItemSidebar;

    public MainGameFormManager(MainGame mainGame, Client client) {
        this.mainGame = mainGame;
        this.client = client;
        this.sidebar = new LinkedList();
    }

    private void removeFocusForm(boolean updateInventoryForm) {
        if (this.focus != null) {
            this.removeComponent((FormComponent)((Object)this.focus));
            this.focus.onContainerClosed();
            if (updateInventoryForm) {
                this.setupInventoryForm(this.client.getContainer());
            }
        }
        this.focus = null;
        this.updateActive(true);
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void removeFocusForm() {
        this.removeFocusForm(true);
    }

    public FormComponent getFocusForm() {
        return (FormComponent)((Object)this.focus);
    }

    public ContainerComponent<?> getFocusComp() {
        return this.focus;
    }

    public boolean hasFocusForm() {
        return this.focus != null;
    }

    public void setFocusForm(ContainerComponent<?> component) {
        this.removeFocusForm(false);
        this.focus = component;
        this.addComponent((FormComponent)((Object)this.focus));
        ((FormComponent)((Object)this.focus)).tryPutOnTop();
        component.onWindowResized(WindowManager.getWindow());
        this.setupInventoryForm((Container)component.getContainer());
        this.updateActive(true);
    }

    public void addSidebar(SidebarComponent form) {
        if (this.sidebarBox.hasComponent((FormComponent)((Object)form))) {
            return;
        }
        this.sidebarBox.addComponent((FormComponent)((Object)form));
        this.sidebar.add(form);
        form.onAdded(this.client);
        this.fixSidebar();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void removeSidebar(SidebarComponent form) {
        if (this.sidebar.contains(form)) {
            form.onRemoved(this.client);
            this.sidebar.remove(form);
            this.sidebarBox.removeComponent((FormComponent)((Object)form));
            this.fixSidebar();
            ControllerInput.submitNextRefreshFocusEvent();
        }
    }

    @Override
    public void addContinueForm(String key, ContinueComponent component) {
        ContinueComponent last;
        if (key == null) {
            key = UUID.randomUUID().toString();
        }
        if ((last = this.continueForms.put(key, component)) != null) {
            this.removeComponent((FormComponent)((Object)last));
        }
        FormComponent formComponent = (FormComponent)((Object)component);
        this.addComponent(formComponent, Integer.MAX_VALUE);
        formComponent.onWindowResized(WindowManager.getWindow());
        String finalKey = key;
        component.onContinue(() -> {
            this.removeComponent(formComponent);
            this.continueForms.remove(finalKey, component);
            ControllerInput.submitNextRefreshFocusEvent();
        });
        ControllerInput.submitNextRefreshFocusEvent();
    }

    @Override
    public void removeContinueForm(String key) {
        ContinueComponent last = this.continueForms.get(key);
        if (last != null) {
            this.removeComponent((FormComponent)((Object)last));
            ControllerInput.submitNextRefreshFocusEvent();
        }
    }

    @Override
    public boolean hasContinueForms() {
        return !this.continueForms.isEmpty();
    }

    public int getSidebarWidth() {
        return this.sidebarBox.getWidth() - 10;
    }

    @Override
    public void frameTick(TickManager tickManager) {
        super.frameTick(tickManager);
        this.updateActive(false);
    }

    public void updateActive(boolean forceUpdate) {
        PlayerMob player = this.client.getPlayer();
        TravelDir travelDir = this.mainGame.canTravel(this.client, player) ? TravelContainer.getTravelDir(player) : null;
        boolean invExtended = player.isInventoryExtended() && (this.focus == null || this.focus.shouldShowInventory());
        boolean showToolbar = this.focus == null || this.focus.shouldShowToolbar();
        boolean isDead = this.client.isDead;
        boolean showMap = this.mainGame.showMap();
        boolean isRunning = this.mainGame.isRunning();
        boolean showCreativeMenu = player.isCreativeMenuExtended() && invExtended && !isDead && !this.hasFocusForm();
        boolean creativeMenuReplacesInventory = this.creativeMenu.isReplacingInventory();
        if (this.focus != null) {
            this.focus.setHidden(showCreativeMenu);
        }
        this.godmodeToggle.setToggled(player.buffManager.hasBuff(BuffRegistry.GODMODE_BUFF));
        this.creativeMenuToggle.setToggled(player.isCreativeMenuExtended() || creativeMenuReplacesInventory);
        this.inventoryToggle.setToggled(!player.isCreativeMenuExtended() || !creativeMenuReplacesInventory);
        ArrayList<SidebarComponent> removes = new ArrayList<SidebarComponent>();
        this.sidebar.stream().filter(f -> !f.isValid(this.client)).forEach(removes::add);
        removes.forEach(this::removeSidebar);
        InventoryItem selectedItem = player.getSelectedItem();
        if (selectedItem != this.lastCheckedItemSidebar) {
            SidebarForm newForm;
            if (selectedItem != null && (newForm = selectedItem.item.getSidebar(selectedItem)) != null) {
                this.addSidebar(newForm);
            }
            this.lastCheckedItemSidebar = selectedItem;
        }
        if (forceUpdate || this.lastTravelDir != travelDir || this.lastShowMap != showMap) {
            this.lastTravelDir = travelDir;
            if (travelDir != null && !this.hasFocusForm() && isRunning && !showMap) {
                if (this.travel.isHidden()) {
                    this.setNextControllerFocus(this.travelButton);
                    ControllerInput.submitNextRefreshFocusEvent();
                }
                this.travel.setHidden(false);
                this.travelButton.setLocalization(travelDir.travelMessage);
            } else {
                this.travel.setHidden(true);
            }
        }
        if (forceUpdate || this.lastShowToolbar != showToolbar || this.lastInventoryExtended != invExtended || this.lastIsDead != isDead || this.lastShowMap != showMap || this.lastIsRunning != isRunning || this.lastCreativeMenuReplacesInventory != creativeMenuReplacesInventory) {
            this.lastShowToolbar = showToolbar;
            this.lastInventoryExtended = invExtended;
            this.lastIsDead = isDead;
            this.lastShowMap = showMap;
            this.lastIsRunning = isRunning;
            this.lastCreativeMenuReplacesInventory = creativeMenuReplacesInventory;
            if (this.focus != null) {
                this.focus.setHidden(showMap);
            }
            this.toolbar.setHidden(!isRunning || isDead || showMap || !showToolbar);
            this.inventory.setHidden(this.toolbar.isHidden() || !invExtended || creativeMenuReplacesInventory);
            this.crafting.setHidden(this.toolbar.isHidden() || !invExtended || this.client.worldSettings.creativeMode);
            this.creative.setHidden(this.toolbar.isHidden() || !invExtended || !this.client.worldSettings.creativeMode);
            this.settlementNotifications.setHidden(!isRunning || isDead || showMap);
            if (this.modifiersForm != null) {
                this.modifiersForm.setHidden(!isRunning || showMap);
            }
            this.buffsContainer.setHidden(!isRunning || showMap);
            this.minimap.setHidden(!isRunning || showMap);
            this.fullMap.setHidden(!isRunning || !showMap);
            this.death.setHidden(!isDead);
            if (isDead && !this.mainGame.isRunning()) {
                this.mainGame.setRunning(true);
            }
            if (creativeMenuReplacesInventory) {
                this.restockButton.setTooltips(new LocalMessage("ui", "creativedisabledwheninventoryhidden"));
                this.stackButton.setTooltips(new LocalMessage("ui", "creativedisabledwheninventoryhidden"));
                this.sortButton.setTooltips(new LocalMessage("ui", "creativedisabledwheninventoryhidden"));
            } else {
                this.restockButton.setTooltips(new LocalMessage("ui", "restocktip"));
                this.stackButton.setTooltips(new LocalMessage("ui", "quickstacktip"));
                this.sortButton.setTooltips(new LocalMessage("ui", "sorttip"));
            }
            this.restockButton.setActive(!creativeMenuReplacesInventory);
            this.stackButton.setActive(!creativeMenuReplacesInventory);
            this.sortButton.setActive(!creativeMenuReplacesInventory);
        }
        if (!this.death.isHidden()) {
            if (this.respawnButtonCD < System.currentTimeMillis()) {
                this.respawnButton.setActive(this.client.canRespawn() || this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE);
            } else {
                this.respawnButton.setActive(false);
            }
            if (this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE) {
                this.respawnButton.setLocalization("ui", "disconnectbutton");
            } else if (this.client.canRespawn()) {
                this.respawnButton.setLocalization("ui", "respawn");
            } else {
                this.respawnButton.setLocalization(new LocalMessage("ui", "respawnin", "seconds", this.client.getRespawnTimeLeft() / 1000L + 1L));
            }
        }
        this.creativeMenu.setHidden(this.toolbar.isHidden() || !showCreativeMenu);
        if (this.toolbar.isHidden()) {
            this.rightQuickbar.setHidden(true);
        } else {
            this.rightQuickbar.updateButtons(!this.inventory.isHidden() || creativeMenuReplacesInventory || Settings.alwaysShowQuickbar);
        }
    }

    public void tickExpressionWheel(boolean shouldBeActive, Consumer<FormExpressionWheel.Expression> onSelected) {
        if (shouldBeActive) {
            if (this.expressionWheel == null) {
                int startY;
                int startX;
                if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                    startX = WindowManager.getWindow().getHudWidth() / 2;
                    startY = WindowManager.getWindow().getHudHeight() / 2;
                } else {
                    InputPosition mousePos = WindowManager.getWindow().mousePos();
                    startX = mousePos.hudX;
                    startY = mousePos.hudY;
                }
                this.expressionWheel = this.addComponent(new FormExpressionWheel(startX, startY, FormExpressionWheel.Expression.values()));
            }
        } else if (this.expressionWheel != null) {
            FormExpressionWheel.Expression selected = this.expressionWheel.getSelected();
            this.removeComponent(this.expressionWheel);
            this.expressionWheel = null;
            if (selected != null) {
                onSelected.accept(selected);
            }
        }
    }

    public void fixSidebar() {
        GameWindow window = WindowManager.getWindow();
        this.sidebarBox.setWidth(Math.max(100, window.getHudWidth() / 3));
        int y = 0;
        for (SidebarComponent comp : this.sidebar) {
            comp.onSidebarUpdate(5, y);
            y += ((FormComponent)((Object)comp)).getBoundingBox().height + 10;
        }
        Rectangle fitBox = this.sidebarBox.getContentBoxToFitComponents();
        this.sidebarBox.setContentBox(new Rectangle(0, -5, fitBox.width, fitBox.height + 15));
        this.sidebarBox.setHeight(Math.max(100, Math.min(this.sidebarBox.getContentBox().height, window.getHudHeight() / 2)));
    }

    public void setTutorialContent(GameMessage content, GameMessage buttonText) {
        this.setTutorialContent(content, buttonText, null);
    }

    public void setTutorialContent(GameMessage content, GameMessage buttonText, FormEventListener<FormInputEvent> buttonListener) {
        this.tutorialSidebar.setContent(content, buttonText);
        this.tutorialSidebar.buttonEvent = buttonListener;
        this.addSidebar(this.tutorialSidebar);
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void clearTutorial() {
        if (this.tutorialSidebar != null) {
            this.removeSidebar(this.tutorialSidebar);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        window.submitNextMoveEvent();
        this.updateInventoryFormPositions();
        this.rightQuickbar.setPosition(this.toolbar.getX() + this.toolbar.getWidth() + Settings.UI.formSpacing, this.toolbar.getY() + this.toolbar.getHeight() - this.rightQuickbar.getHeight());
        this.crafting.setPosition(this.inventory.getX() + this.inventory.getWidth() + Settings.UI.formSpacing, this.inventory.getY());
        this.death.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2 + 100);
        this.sidebarBox.setPosition(10, 30);
        this.fixSidebar();
    }

    public void resetTravelCooldown() {
        this.travelButton.stopCooldown();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        if (!this.equipment.isHidden()) {
            int xOffset = 0;
            if (this.client.getPlayer().getInv().equipment.getTotalSets() > 1) {
                int setPadding = (Settings.UI.formtabedge_16.active.getWidth() - Settings.UI.formtab_16.active.getWidth()) / 2;
                PlayerMob player = this.client.getPlayer();
                xOffset = 4 + (player == null ? 0 : player.getInv().equipment.getTotalSets()) * (16 + setPadding);
            }
            String armorString = Localization.translate("ui", "totalarmor", "armor", (Object)Math.round(perspective.getArmor()));
            FontManager.bit.drawString(this.equipment.getX() + xOffset, this.equipment.getY() - 16 - Settings.UI.formSpacing + Settings.UI.form.edgeMargin, armorString, totalArmorFontOptions);
        }
        super.draw(tickManager, perspective);
        PositionedDrawOptionsBox inDevelopmentDrawBox = GameInfo.getInDevelopmentDrawBox(true, !this.pauseMenu.isHidden());
        if (inDevelopmentDrawBox != null) {
            Rectangle boundingBox;
            if (!this.pauseMenu.isHidden()) {
                boundingBox = this.pauseMenu.isCurrent(this.pauseMenu.main) ? this.pauseMenu.mainForm.getBoundingBox() : this.pauseMenu.getBoundingBox();
            } else {
                boundingBox = this.importantBuffs.getBoundingBox();
                if (boundingBox.isEmpty()) {
                    boundingBox = this.mainGame.getStatusDrawBox(perspective).getBoundingBox();
                } else {
                    boundingBox.height += 10;
                }
            }
            inDevelopmentDrawBox.draw(boundingBox.x + boundingBox.width / 2, boundingBox.y + (!this.pauseMenu.isHidden() ? -4 : boundingBox.height + 4));
        }
    }

    private GameMessageBuilder getControlKeyTip(Control control) {
        GameMessageBuilder builder = new GameMessageBuilder().append(control.text);
        if (control.getKey() != -1) {
            builder.append("\n").append(new LocalMessage("ui", "hotkeytip", "hotkey", "[input=" + control.id + "]"));
        }
        return builder;
    }

    public void setup() {
        PlayerMob player = this.client.getPlayer();
        GameWindow window = WindowManager.getWindow();
        if (GameLaunch.launchOptions.containsKey("testcrash")) {
            this.addComponent(new TestCrashReportForm());
        }
        this.chat = this.addComponent(new ChatBoxForm(this.client, "chat"), -1);
        this.chat.onWindowResized(window);
        this.settlementNotifications = this.addComponent(new SettlementNotificationForm(this.client, "notifications"), -1);
        this.settlementNotifications.onWindowResized(WindowManager.getWindow());
        this.settlementNotifications.showSeverityAbove(SettlementNotificationSeverity.URGENT);
        this.debugForm = new DebugForm("debug", this.client, this.mainGame);
        this.addComponent(this.debugForm);
        this.pauseMenu = this.addComponent(new PauseMenuForm(this.mainGame, this.client));
        this.pauseMenu.setHidden(this.mainGame.isRunning());
        this.rightQuickbar = this.addComponent(new ButtonToolbarForm("rightQuickbar"));
        this.rightQuickbar.addButton("quickbarParty", Settings.UI.party_inventory_icon, e -> {
            ((FormButton)e.from).startCooldown(500);
            this.client.network.sendPacket(new PacketOpenPartyConfig());
        }, this.getControlKeyTip(Control.OPEN_ADVENTURE_PARTY), () -> !this.client.adventureParty.isEmpty() || player.getInv().hasPartyItems());
        this.rightQuickbar.addButton("quickbarTeams", Settings.UI.quickbar_teams, e -> {
            ((FormButton)e.from).startCooldown(500);
            this.client.network.sendPacket(new PacketOpenPvPTeams());
            PvPTeamsContainerForm.pauseGameOnClose = false;
        }, new LocalMessage("ui", "pvpandteams"));
        this.rightQuickbar.addButton("quickbarSettlement", Settings.UI.quickbar_settlement, e -> {
            this.client.network.sendPacket(new PacketSettlementOpen());
            ((FormButton)e.from).startCooldown(500);
        }, this.getControlKeyTip(Control.OPEN_SETTLEMENT));
        this.rightQuickbar.addButton("quickbarQuests", Settings.UI.quickbar_quests, e -> {
            ((FormButton)e.from).startCooldown(500);
            this.client.network.sendPacket(new PacketOpenQuests());
        }, new LocalMessage("ui", "quests"));
        this.journalButton = new FormContentButton(0, 0, FormInputSize.SIZE_32.height, FormInputSize.SIZE_32, ButtonColor.BASE){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                if (Settings.displayJournalNotifications && ((MainGameFormManager)MainGameFormManager.this).client.hasNewJournalEntry) {
                    int flickerTime = 1000;
                    this.color = System.currentTimeMillis() % (long)flickerTime < (long)(flickerTime / 2) ? ButtonColor.GREEN : ButtonColor.BASE;
                } else {
                    this.color = ButtonColor.BASE;
                }
                super.draw(tickManager, perspective, renderBox);
            }

            @Override
            protected void drawContent(int x, int y, int width, int height) {
                ButtonIcon icon = Settings.UI.quickbar_journal;
                Color color = (Color)icon.colorGetter.apply(this.getButtonState());
                icon.texture.initDraw().color(color).posMiddle(x + width / 2, y + height / 2).draw();
            }

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                ListGameTooltips tooltips = new ListGameTooltips();
                tooltips.add(Localization.translate("journal", "journal"));
                if (((MainGameFormManager)MainGameFormManager.this).client.hasNewJournalEntry) {
                    tooltips.add(Localization.translate("journal", "newentrytip"));
                }
                if (Control.OPEN_ADVENTURE_JOURNAL.getKey() != -1) {
                    tooltips.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.OPEN_ADVENTURE_JOURNAL.id + "]"));
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
        };
        this.journalButton.controllerFocusHashcode = "quickbarJournal";
        this.journalButton.onClicked(e -> {
            ((FormButton)e.from).startCooldown(500);
            this.client.network.sendPacket(new PacketOpenJournal());
            this.client.hasNewJournalEntry = false;
        });
        this.rightQuickbar.addButton(this.journalButton, () -> Settings.displayJournalNotifications && this.client.hasNewJournalEntry);
        this.rightQuickbar.addButton("quickbarIslandmap", Settings.UI.quickbar_island_map, e -> {
            ((FormButton)e.from).startCooldown(500);
            this.mainGame.setShowMap(true);
        }, this.getControlKeyTip(Control.SHOW_MAP));
        FormContentIconVarToggleButton smartMiningButton = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> Settings.smartMining, Settings.UI.quickbar_mining_icon, this.getControlKeyTip(Control.SMART_MINING));
        smartMiningButton.controllerFocusHashcode = "quickbarSmartMining";
        smartMiningButton.onClicked(e -> {
            Settings.smartMining = !Settings.smartMining;
            Settings.saveClientSettings();
        });
        this.rightQuickbar.addButton(smartMiningButton);
        FormContentIconVarToggleButton autoDoorButton = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> this.client.getPlayer().autoOpenDoors, Settings.UI.quickbar_door_on, Settings.UI.quickbar_door_off, new LocalMessage("ui", "toggleautodoor"));
        autoDoorButton.controllerFocusHashcode = "quickbarAutodoor";
        autoDoorButton.onClicked(e -> {
            this.client.getPlayer().autoOpenDoors = !this.client.getPlayer().autoOpenDoors;
            this.client.network.sendPacket(new PacketPlayerAutoOpenDoors(this.client.getSlot(), this.client.getPlayer().autoOpenDoors));
        });
        this.rightQuickbar.addButton(autoDoorButton);
        FormContentIconVarToggleButton hotbarLockButton = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> this.client.getPlayer().hotbarLocked, Settings.UI.hotbar_locked, Settings.UI.hotbar_unlocked, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips() {
                ListGameTooltips tooltips = new ListGameTooltips();
                if (this.isToggled()) {
                    tooltips.add(new LocalMessage("ui", "unlockhotbar"));
                } else {
                    tooltips.add(new LocalMessage("ui", "lockhotbar"));
                }
                tooltips.add(new StringTooltips(Localization.translate("ui", "hotbarlockedtip"), GameColor.LIGHT_GRAY, 350));
                return tooltips;
            }
        };
        hotbarLockButton.controllerFocusHashcode = "quickbarHotbarlock";
        hotbarLockButton.onClicked(e -> {
            this.client.getPlayer().hotbarLocked = !this.client.getPlayer().hotbarLocked;
            this.client.network.sendPacket(new PacketPlayerHotbarLocked(this.client.getSlot(), this.client.getPlayer().hotbarLocked));
        });
        this.rightQuickbar.addButton(hotbarLockButton);
        FormContentIconVarToggleButton statsButton = new FormContentIconVarToggleButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, () -> this.modifiersForm != null, Settings.UI.quickbar_stats_icon, new GameMessage[0]){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                super.addTooltips(perspective);
                if (perspective != null && MainGameFormManager.this.modifiersForm == null) {
                    ListGameTooltips tooltips = CurrentModifiersForm.getTooltips(perspective);
                    tooltips.add(new InputTooltip(-100, Localization.translate("ui", "anchortip")));
                    GameTooltipManager.addTooltip(tooltips, GameBackground.itemTooltip, TooltipLocation.FORM_FOCUS);
                }
            }
        };
        autoDoorButton.controllerFocusHashcode = "quickbarStats";
        statsButton.onClicked(e -> {
            if (this.modifiersForm != null) {
                this.removeComponent(this.modifiersForm);
                this.modifiersForm = null;
                lastModifiersFormPos = null;
            } else {
                Point pos;
                this.modifiersForm = this.addComponent(new CurrentModifiersForm(){

                    @Override
                    public void onRemove() {
                        MainGameFormManager.this.removeComponent(this);
                        MainGameFormManager.this.modifiersForm = null;
                        lastModifiersFormPos = null;
                    }
                });
                this.modifiersForm.update(this.client.getPlayer());
                ControllerFocus currentFocus = this.getCurrentFocus();
                if (currentFocus != null) {
                    pos = new Point(currentFocus.boundingBox.x, currentFocus.boundingBox.y);
                } else {
                    InputPosition mousePos = window.mousePos();
                    pos = new Point(mousePos.hudX, mousePos.hudY);
                }
                GameBackground background = GameBackground.itemTooltip;
                this.modifiersForm.setPosition(Math.min(pos.x + background.getContentPadding() + 1, window.getHudWidth() - this.modifiersForm.getWidth() - background.getContentPadding() - 4), pos.y - this.modifiersForm.getHeight() - 11 - 20);
            }
        });
        this.rightQuickbar.addButton(statsButton);
        if (lastModifiersFormPos != null) {
            this.modifiersForm = this.addComponent(new CurrentModifiersForm(){

                @Override
                public void onRemove() {
                    MainGameFormManager.this.removeComponent(this);
                    MainGameFormManager.this.modifiersForm = null;
                    lastModifiersFormPos = null;
                }
            });
            this.modifiersForm.setPosition(MainGameFormManager.lastModifiersFormPos.x, MainGameFormManager.lastModifiersFormPos.y);
            this.modifiersForm.update(player);
        }
        this.setupInventoryForm(this.client.getContainer());
        this.crafting = this.addComponent(new Form("crafting", 128, this.inventory.getHeight() + (this.toolbar.getHeight() - this.rightQuickbar.getHeight())));
        this.crafting.addComponent(new FormLocalLabel(new LocalMessage("jobs", "craftingname"), new FontOptions(16), 0, this.crafting.getWidth() / 2, 5));
        this.crafting.addComponent(new FormContainerCraftingListContentBox(0, 24, this.crafting.getWidth(), this.crafting.getHeight() - 24, this.client, false, true, false){

            @Override
            public Stream<ContainerRecipe> streamAllRecipes() {
                return this.client.getContainer().streamRecipes(RecipeTechRegistry.NONE);
            }
        });
        this.creative = this.addComponent(new Form("creative", 40, this.inventory.getHeight() + (this.toolbar.getHeight() - this.rightQuickbar.getHeight())));
        FormFlow creativeFlow = new FormFlow(6);
        this.creative.setPosition(new FormRelativePosition((FormPositionContainer)this.crafting, () -> this.lastCreativeMenuReplacesInventory ? this.crafting.getWidth() + Settings.UI.formSpacing : 0, () -> this.lastCreativeMenuReplacesInventory ? this.crafting.getHeight() - this.creative.getHeight() - 8 : 0));
        this.creativeMenuToggle = this.creative.addComponent(creativeFlow.nextY(new FormContentIconToggleButton(4, 0, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.creative_menu_toggle, Settings.UI.creative_menu_toggle, new LocalMessage("ui", "creativetogglecreativemenu")), 4));
        this.creativeMenuToggle.onToggled(event -> {
            ((FormButtonToggle)event.from).startCooldown(200);
            if (this.mainGame.formManager.hasFocusForm()) {
                this.mainGame.getClient().closeContainer(true);
                this.creativeMenuToggle.setToggled(true);
                this.client.getPlayer().setCreativeMenuExtended(true);
                this.client.getPlayer().setInventoryExtended(true);
            } else {
                this.client.getPlayer().setCreativeMenuExtended(this.creativeMenuToggle.isToggled());
            }
            if (!this.client.getPlayer().isCreativeMenuExtended()) {
                this.creativeMenu.shouldReplaceInventory(false);
            }
        });
        this.inventoryToggle = this.creative.addComponent(creativeFlow.nextY(new FormContentIconToggleButton(4, 0, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.creative_inventory_toggle, Settings.UI.creative_inventory_toggle, new LocalMessage("ui", "creativetoggleinventory")), 4));
        this.inventoryToggle.onToggled(e -> {
            ((FormButtonToggle)e.from).startCooldown(200);
            if (this.mainGame.formManager.hasFocusForm()) {
                this.mainGame.getClient().closeContainer(true);
                this.inventoryToggle.setToggled(true);
                this.creativeMenu.shouldReplaceInventory(false);
                this.client.getPlayer().setInventoryExtended(true);
            } else if (this.inventoryToggle.isToggled()) {
                this.creativeMenu.shouldReplaceInventory(false);
            } else {
                this.client.getPlayer().setCreativeMenuExtended(true);
                this.creativeMenu.shouldReplaceInventory(true);
            }
        });
        this.godmodeToggle = this.creative.addComponent(creativeFlow.nextY(new FormContentIconToggleButton(4, 0, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.godmode_on, Settings.UI.godmode_off, new LocalMessage("ui", "creativetogglegodmode")), 4));
        this.godmodeToggle.onToggled(event -> {
            if (this.client.getPlayer() != null) {
                this.client.getPlayer().setGodModeInCreative(this.godmodeToggle.isToggled());
            }
            ((FormButtonToggle)event.from).startCooldown(200);
        });
        this.creative.setHeight(creativeFlow.next());
        this.travel = this.addComponent(new Form("travelbutton", 200, 40));
        this.travelButton = this.travel.addComponent(new FormLocalTextButton("ui", "travelbutton", 4, 0, this.travel.getWidth() - 8));
        this.travelButton.onClicked(e -> {
            if (this.lastTravelDir != null) {
                this.client.network.sendPacket(new PacketRequestTravel(this.lastTravelDir));
                this.travelButton.setLocalization("ui", "loadingdotdot");
            }
        });
        this.travelButton.setCooldown(5000);
        this.travelButton.controllerFocusHashcode = "travelButton";
        this.death = this.addComponent(new Form("death", 200, 80));
        this.death.addComponent(new FormLocalLabel("ui", "diedlabel", new FontOptions(20), 0, this.death.getWidth() / 2, 10));
        this.respawnButton = this.death.addComponent(new FormLocalTextButton("ui", "respawn", 4, 40, this.death.getWidth() - 8));
        this.respawnButton.onClicked(e -> {
            if (this.client.worldSettings.deathPenalty == GameDeathPenalty.HARDCORE) {
                this.mainGame.disconnect("Quit");
            } else {
                this.client.network.sendPacket(new PacketPlayerRespawnRequest());
            }
            this.respawnButtonCD = System.currentTimeMillis() + 5000L;
        });
        this.respawnButton.controllerFocusHashcode = "respawnButton";
        this.buffsContainer = this.addComponent(new FormComponentList());
        this.importantBuffs = this.buffsContainer.addComponent(new FormBuffHud(0, 0, 7, FairType.TextAlign.CENTER, player, activeBuff -> activeBuff.buff.isImportant((ActiveBuff)activeBuff)));
        this.unimportantBuffs = this.buffsContainer.addComponent(new FormBuffHud(0, 0, 7, FairType.TextAlign.LEFT, player, activeBuff -> !activeBuff.buff.isImportant((ActiveBuff)activeBuff)));
        this.minimap = this.addComponent(MapForm.getMiniMapForm("minimap", this.client, 200));
        this.minimap.setMapHidden(Settings.minimapHidden);
        this.fullMap = this.addComponent(MapForm.getFullMapForm("fullmap", this.client));
        this.addMapshotButton();
        this.fullMap.setHidden(!this.mainGame.showMap());
        this.scoreboard = this.addComponent(new ScoreboardForm("scoreboard", this.client), Integer.MAX_VALUE);
        this.scoreboard.setHidden(!this.mainGame.showScoreboard());
        this.sidebarBox = this.addComponent(new FormContentBox(10, 30, 5, 5){

            @Override
            public boolean isMouseOver(InputEvent event) {
                if (this.hasScrollbarX() && this.isMouseOverScrollbarX(event)) {
                    return true;
                }
                if (this.hasScrollbarY() && this.isMouseOverScrollbarY(event)) {
                    return true;
                }
                InputEvent offsetEvent = this.getComponentList().offsetEvent(event, false);
                return this.getComponents().stream().anyMatch(c -> c.isMouseOver(offsetEvent));
            }

            @Override
            public boolean shouldDraw() {
                return !MainGameFormManager.this.client.isDrawingCredits();
            }
        });
        this.sidebarBox.drawHorizontalOnTop = true;
        this.sidebarBox.drawVerticalOnLeft = true;
        this.sidebarBox.hitboxFullSize = false;
        this.tutorialSidebar = new TutorialSidebarForm(this);
        this.addSidebar(new TrackedSidebarForm(this));
        this.creativeMenu = this.addComponent(new CreativeMenuForm(this, 684, 264, this.client));
        this.onWindowResized(window);
        this.updateActive(true);
    }

    private void addMapshotButton() {
        FormLocalTextButton mapshotButton = this.fullMap.addButton(new LocalMessage("ui", "startscreenshottool"), 302);
        mapshotButton.onClicked(e -> {
            if (this.fullMap.canClickButtons()) {
                this.fullMap.map.startScreenshotMode();
                this.fullMap.removeButton(mapshotButton);
                FormLocalTextButton confirm = this.fullMap.addButton(new LocalMessage("ui", "confirmbutton"), 150);
                confirm.color = ButtonColor.GREEN;
                FormLocalTextButton cancel = this.fullMap.addButton(new LocalMessage("ui", "cancelbutton"), 150);
                cancel.color = ButtonColor.RED;
                confirm.onClicked(event -> {
                    this.fullMap.removeButton(confirm);
                    this.fullMap.removeButton(cancel);
                    Rectangle screenshotBounds = this.fullMap.map.getScreenshotBounds();
                    Renderer.takeMapshot(this.client.getLevel(), this.client, new GameCamera(screenshotBounds));
                    this.fullMap.map.stopScreenshotMode();
                    this.addMapshotButton();
                    ControllerInput.submitNextRefreshFocusEvent();
                });
                cancel.onClicked(event -> {
                    this.fullMap.removeButton(confirm);
                    this.fullMap.removeButton(cancel);
                    this.fullMap.map.stopScreenshotMode();
                    this.addMapshotButton();
                    ControllerInput.submitNextRefreshFocusEvent();
                });
            }
        });
    }

    private void setupInventoryForm(Container container) {
        int i;
        int i2;
        PlayerMob player = this.client.getPlayer();
        if (this.inventory != null) {
            this.removeComponent(this.inventory);
        }
        this.inventory = this.addComponent(new Form("inventory", 408, 168){

            @Override
            public void setHidden(boolean hidden) {
                super.setHidden(hidden);
                MainGameFormManager.this.equipment.setHidden(hidden);
                MainGameFormManager.this.leftQuickbar.setHidden(hidden & !MainGameFormManager.this.lastCreativeMenuReplacesInventory);
            }
        });
        for (i2 = container.CLIENT_INVENTORY_START; i2 <= container.CLIENT_INVENTORY_END; ++i2) {
            int y = (i2 - container.CLIENT_INVENTORY_START) / (this.inventory.getWidth() / 40);
            int x = (i2 - container.CLIENT_INVENTORY_START) % (this.inventory.getWidth() / 40);
            FormContainerSlot containerSlot = this.inventory.addComponent(new FormContainerSlot(this.client, container, i2, 4 + x * 40, 4 + y * 40));
            containerSlot.controllerFocusHashcode = "playerInventorySlot" + i2;
        }
        if (this.toolbar != null) {
            this.removeComponent(this.toolbar);
        }
        this.toolbar = this.addComponent(new Form("toolbar", 408, 48){

            @Override
            public void setHidden(boolean hidden) {
                super.setHidden(hidden);
                MainGameFormManager.this.sidebarBox.setHidden(hidden);
            }
        });
        for (i2 = container.CLIENT_HOTBAR_START; i2 <= container.CLIENT_HOTBAR_END; ++i2) {
            int x = i2 - container.CLIENT_HOTBAR_START;
            FormContainerToolbarSlot toolbarSlot = this.toolbar.addComponent(new FormContainerToolbarSlot(this.client, container, i2, 4 + x * 40, 4, player));
            toolbarSlot.controllerDownFocus = toolbarSlot;
            toolbarSlot.controllerFocusHashcode = "playerToolbarSlot" + i2;
            if (i2 != container.CLIENT_HOTBAR_START) continue;
            this.tryPrioritizeControllerFocus(toolbarSlot);
        }
        PointTreeSet equipmentTiles = Zoning.getNewZoneSet();
        for (int i3 = 0; i3 < this.client.getPlayer().getInv().equipment.getTrinketSlotsSize() + 8; ++i3) {
            int x = -i3 / 4;
            int y = i3 % 4;
            equipmentTiles.add(x, y);
        }
        if (this.equipment != null) {
            this.removeComponent(this.equipment);
        }
        this.equipment = this.addComponent(new Form("equipment", equipmentTiles, 40, 4));
        FormContainerPlayerArmorSlot headSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_HELMET_SLOT, this.equipment.getWidth() - 48, 0, ArmorItem.ArmorType.HEAD, false));
        headSlot.controllerFocusHashcode = "playerHeadSlot";
        FormContainerPlayerArmorSlot chestSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_CHEST_SLOT, this.equipment.getWidth() - 48, 40, ArmorItem.ArmorType.CHEST, false));
        chestSlot.controllerFocusHashcode = "playerChestSlot";
        FormContainerPlayerArmorSlot feetSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_FEET_SLOT, this.equipment.getWidth() - 48, 80, ArmorItem.ArmorType.FEET, false));
        feetSlot.controllerFocusHashcode = "playerFeetSlot";
        FormContainerPlayerArmorSlot cosmeticHeadSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_COSM_HELMET_SLOT, this.equipment.getWidth() - 88, 0, ArmorItem.ArmorType.HEAD, true));
        cosmeticHeadSlot.controllerFocusHashcode = "playerCosmeticHeadSlot";
        FormContainerPlayerArmorSlot cosmeticChestSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_COSM_CHEST_SLOT, this.equipment.getWidth() - 88, 40, ArmorItem.ArmorType.CHEST, true));
        cosmeticChestSlot.controllerFocusHashcode = "playerCosmeticChestSlot";
        FormContainerPlayerArmorSlot cosmeticFeetSlot = this.equipment.addComponent(new FormContainerPlayerArmorSlot(this.client, container, container.CLIENT_COSM_FEET_SLOT, this.equipment.getWidth() - 88, 80, ArmorItem.ArmorType.FEET, true));
        cosmeticFeetSlot.controllerFocusHashcode = "playerCosmeticFeetSlot";
        FormContainerMountSlot mountSlot = this.equipment.addComponent(new FormContainerMountSlot(this.client, container, container.CLIENT_MOUNT_SLOT, this.equipment.getWidth() - 48, 120));
        mountSlot.controllerFocusHashcode = "playerMountSlot";
        FormContainerTrinketSlot trinketAbilitySlot = this.equipment.addComponent(new FormContainerTrinketSlot(this.client, container, container.CLIENT_TRINKET_ABILITY_SLOT, this.equipment.getWidth() - 88, 120, true));
        trinketAbilitySlot.controllerFocusHashcode = "playerTrinketAbilitySlot";
        if (this.client.getPlayer().getInv().equipment.getTrinketSlotsSize() > 0) {
            for (int slotIndex = container.CLIENT_TRINKET_START; slotIndex <= container.CLIENT_TRINKET_END; ++slotIndex) {
                i = slotIndex - container.CLIENT_TRINKET_START;
                int y = i % 4 * 40;
                int x = this.equipment.getWidth() - 120 - i / 4 * 40 - 8;
                FormContainerTrinketSlot trinketSlot = this.equipment.addComponent(new FormContainerTrinketSlot(this.client, container, slotIndex, x, y, false));
                trinketSlot.controllerFocusHashcode = "playerTrinketSlot" + i;
            }
        }
        if (this.equipmentSetButtons != null) {
            this.removeComponent(this.equipmentSetButtons);
        }
        this.equipmentSetButtons = this.addComponent(new FormComponentList(){

            @Override
            public boolean isHidden() {
                return super.isHidden() || MainGameFormManager.this.equipment.isHidden();
            }
        });
        if (this.client.getPlayer().getInv().equipment.getTotalSets() > 1) {
            int setPadding = (Settings.UI.formtabedge_16.active.getWidth() - Settings.UI.formtab_16.active.getWidth()) / 2;
            for (i = 0; i < this.client.getPlayer().getInv().equipment.getTotalSets(); ++i) {
                final int setIndex = i;
                FormTabTextComponent button = this.equipmentSetButtons.addComponent(new FormTabTextComponent("" + (i + 1), null, this.equipment, 4 + i * (16 + setPadding), FormInputSize.SIZE_16, 16){

                    @Override
                    public boolean isSelected() {
                        return ((MainGameFormManager)MainGameFormManager.this).client.getPlayer().getInv().equipment.getSelectedSet() == setIndex;
                    }
                });
                button.onClicked(e -> {
                    this.client.getPlayer().getInv().equipment.setSelectedSet(setIndex);
                    this.client.sendMovementPacket(false);
                });
            }
        }
        if (this.leftQuickbar != null) {
            this.removeComponent(this.leftQuickbar);
        }
        this.leftQuickbar = this.addComponent(new Form("leftQuickbar", 128, 48));
        this.leftQuickbar.addComponent(new FormContainerTrashSlot(this.client, container, container.CLIENT_TRASH_SLOT, this.leftQuickbar.getWidth() - 44, 4));
        this.restockButton = this.leftQuickbar.addComponent(new FormContentIconButton(6, 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new LocalMessage("ui", "restocktip")));
        this.restockButton.onClicked(e -> this.client.network.sendPacket(new PacketContainerAction(-4, ContainerAction.LEFT_CLICK, 1)));
        this.restockButton.setCooldown(500);
        this.restockButton.controllerFocusHashcode = "quickbarRestock";
        this.stackButton = this.leftQuickbar.addComponent(new FormContentIconButton(32, 12, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)Settings.UI.inventory_quickstack_out, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                if (!this.isActive()) {
                    return super.getTooltips(perspective);
                }
                StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "quickstacktip"));
                GameWindow window = WindowManager.getWindow();
                if (window.isKeyDown(340) || window.isKeyDown(344)) {
                    tooltips.add(Localization.translate("ui", "quickstacktipinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), GameColor.LIGHT_GRAY, 400);
                } else {
                    tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                }
                return tooltips;
            }
        });
        this.stackButton.onClicked(e -> this.client.network.sendPacket(new PacketContainerAction(-3, ContainerAction.LEFT_CLICK, 1)));
        this.stackButton.setCooldown(500);
        this.stackButton.controllerFocusHashcode = "quickbarStack";
        this.sortButton = this.leftQuickbar.addComponent(new FormContentIconButton(58, 12, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)Settings.UI.inventory_sort, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                if (!this.isActive()) {
                    return super.getTooltips(perspective);
                }
                StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "sorttip"));
                GameWindow window = WindowManager.getWindow();
                if (window.isKeyDown(340) || window.isKeyDown(344)) {
                    tooltips.add(Localization.translate("ui", "sorttipinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), GameColor.LIGHT_GRAY, 400);
                } else {
                    tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                }
                return tooltips;
            }
        });
        this.sortButton.onClicked(e -> this.client.network.sendPacket(new PacketContainerAction(-2, ContainerAction.LEFT_CLICK, 1)));
        this.sortButton.setCooldown(500);
        this.sortButton.controllerFocusHashcode = "quickbarSort";
        this.updateInventoryFormPositions();
    }

    protected void updateInventoryFormPositions() {
        GameWindow window = WindowManager.getWindow();
        this.toolbar.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() - this.toolbar.getHeight() / 2 - Settings.UI.formSpacing - 20);
        this.inventory.setPosition(this.toolbar.getX(), this.toolbar.getY() - this.inventory.getHeight() - Settings.UI.formSpacing);
        this.equipment.setPosition(this.inventory.getX() - this.equipment.getWidth() - Settings.UI.formSpacing, this.inventory.getY());
        this.leftQuickbar.setPosition(this.inventory.getX() - this.leftQuickbar.getWidth() - Settings.UI.formSpacing, this.equipment.getY() + this.equipment.getHeight() + Settings.UI.formSpacing);
    }

    public void updateInventoryForm() {
        this.setupInventoryForm(this.client.getContainer());
        this.updateActive(true);
    }

    public boolean isInventoryHidden() {
        return this.inventory.isHidden();
    }

    @Override
    public void dispose() {
        this.sidebar.forEach(f -> f.onRemoved(this.client));
        if (this.modifiersForm != null && !this.modifiersForm.isDisposed()) {
            lastModifiersFormPos = new Point(this.modifiersForm.getX(), this.modifiersForm.getY());
        }
        super.dispose();
        if (!this.tutorialSidebar.isDisposed()) {
            this.tutorialSidebar.dispose();
        }
    }

    private static class TutorialSidebarForm
    extends SidebarForm {
        private final MainGameFormManager mainFormManager;
        private final FormFairTypeLabel objectiveText;
        private FormLocalTextButton button;
        private GameMessage objective;
        private GameMessage buttonText;
        public FormEventListener<FormInputEvent> buttonEvent;

        public TutorialSidebarForm(MainGameFormManager mainFormManager) {
            super("tutorialsidebar", 240, 10);
            this.mainFormManager = mainFormManager;
            this.objective = null;
            this.buttonText = null;
            this.addComponent(new FormLocalLabel("ui", "tutoriallabel", new FontOptions(20), -1, 10, 8));
            this.addComponent(new FormLocalTextButton("tutorials", "skip", this.getWidth() - 80 - 4, 4, 80, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> ((MainGameFormManager)mainFormManager).client.tutorial.endTutorial());
            FontOptions options = new FontOptions(16);
            this.objectiveText = this.addComponent(new FormFairTypeLabel("", 10, 40).setFontOptions(options).setMaxWidth(this.getWidth() - 20).setTextAlign(FairType.TextAlign.LEFT).setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(options), TypeParsers.ItemIcon(16), TypeParsers.MobIcon(16)));
            this.objectiveText.onUpdated(e -> this.updateHeight());
            this.setContent(this.objective, this.buttonText);
        }

        public void setContent(GameMessage objective, GameMessage buttonText) {
            if (this.objective != null && this.objective.equals(objective) && this.buttonText != null && this.buttonText.equals(buttonText)) {
                return;
            }
            this.objective = objective;
            this.buttonText = buttonText;
            this.objectiveText.setText(objective);
            if (this.button != null) {
                this.removeComponent(this.button);
            }
            if (buttonText != null) {
                this.button = this.addComponent(new FormLocalTextButton(buttonText, 4, this.getHeight() - 40, this.getWidth() - 8));
                this.button.onClicked(e -> {
                    if (this.buttonEvent != null) {
                        this.buttonEvent.onEvent((FormInputEvent)e);
                    }
                });
            }
            this.updateHeight();
        }

        public void updateHeight() {
            int height = 60;
            height += this.objectiveText.getBoundingBox().height;
            if (this.buttonText != null) {
                height += 40;
            }
            this.setHeight(height);
            if (this.button != null) {
                this.button.setPosition(4, this.getHeight() - 40);
            }
            this.mainFormManager.fixSidebar();
        }

        @Override
        public boolean isValid(Client client) {
            return true;
        }
    }
}

