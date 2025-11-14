/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.gameTool.GameTool
 *  necesse.engine.gameTool.GameToolManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.MainGame
 *  necesse.engine.state.State
 *  necesse.engine.util.GameMath
 *  necesse.engine.window.GameWindow
 *  necesse.engine.window.WindowManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.Renderer
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormCheckBox
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormContentIconButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.FormLabelEdit
 *  necesse.gfx.forms.components.FormSlider
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 *  necesse.gfx.ui.ButtonTexture
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import medievalsim.MedievalSimSettings;
import medievalsim.buildmode.BuildModeManager;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import medievalsim.packets.PacketConfigureProtectedZone;
import medievalsim.packets.PacketConfigurePvPZone;
import medievalsim.packets.PacketDeleteZone;
import medievalsim.packets.PacketForceClean;
import medievalsim.packets.PacketRenameZone;
import medievalsim.packets.PacketRequestPlayerList;
import medievalsim.ui.CommandCenterPanel;
import medievalsim.ui.PlayerDropdownEntry;
import medievalsim.util.ModLogger;
import medievalsim.util.ResponsiveButtonHelper;
import medievalsim.zones.AdminZone;
import medievalsim.zones.CreateOrExpandZoneTool;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import medievalsim.zones.ZoneVisualizationHud;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.level.maps.hudManager.HudDrawElement;

public class AdminToolsHudForm
extends Form {
    private static final int FORM_WIDTH = 300;
    private static final int FORM_HEIGHT = 350;
    private static final int MINIMIZED_WIDTH = 180;
    private static final int TITLE_BAR_HEIGHT = 30;
    private static final int BUTTON_SIZE = 20;
    private Client client;
    private Form mainMenuForm;
    private Form buildToolsForm;
    private Form zoneToolsForm;
    private Form protectedZonesForm;
    private Form pvpZonesForm;
    private CommandCenterPanel commandCenterPanel;
    private FormContentIconButton minimizeButton;
    private FormLabel minimizedTitleLabel;
    private boolean isMinimized = false;
    private boolean wasShowingBuildTools = false;
    private boolean wasShowingZoneTools = false;
    private boolean wasShowingProtectedZones = false;
    private boolean wasShowingPvPZones = false;
    private boolean wasShowingCommandCenter = false;
    private boolean commandCenterBuilt = false;
    private int savedWidth;
    private int savedHeight;
    private Map<Integer, ProtectedZone> protectedZones = new HashMap<Integer, ProtectedZone>();
    private Map<Integer, PvPZone> pvpZones = new HashMap<Integer, PvPZone>();
    private Set<Integer> expandedConfigZones = new HashSet<Integer>();
    private ZoneVisualizationHud protectedZonesHud;
    private ZoneVisualizationHud pvpZonesHud;
    private FormCheckBox buildModeToggle;
    private FormCheckBox[] lineTypeCheckboxes;
    private FormCheckBox[] shapeCheckboxes;
    private FormCheckBox hollowCheckbox;
    private List<PlayerDropdownEntry> cachedPlayerList = new ArrayList<PlayerDropdownEntry>();
    private FormContentBox activePlayerDropdown = null;
    private FormLabelEdit activeOwnerInput = null;
    private int activeDropdownZoneID = -1;
    private static final FontOptions WHITE_TEXT_20 = new FontOptions(20).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_16 = new FontOptions(16).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_14 = new FontOptions(14).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_11 = new FontOptions(11).color(Color.WHITE);
    private static final FontOptions WHITE_TEXT_10 = new FontOptions(10).color(Color.WHITE);

    public AdminToolsHudForm(Client client) {
        super("admintoolshud", 300, 350);
        this.client = client;
        int marginX = 20;
        int marginY = 100;
        this.setPosition(marginX, WindowManager.getWindow().getHudHeight() - 350 - marginY);
        this.setDraggingBox(new Rectangle(0, 0, 300, 350));
        this.onDragged(e -> {
            GameWindow window = WindowManager.getWindow();
            e.x = GameMath.limit((int)e.x, (int)(-this.getWidth() + 50), (int)(window.getHudWidth() - 50));
            e.y = GameMath.limit((int)e.y, (int)(-this.getHeight() + 50), (int)(window.getHudHeight() - 50));
        });
        this.createMainMenu();
        this.createBuildToolsMenu();
        this.createZoneToolsMenu();
        this.createProtectedZonesForm();
        this.createPvPZonesForm();
        this.createCommandCenterMenu();
        this.createControlButtons();
        this.showMainMenu();
    }

    public void drawBase(TickManager tickManager) {
        if (this.wasShowingCommandCenter && this.commandCenterPanel != null) {
            this.commandCenterPanel.tick(tickManager);
        }
        Renderer.initQuadDraw((int)this.getWidth(), (int)this.getHeight()).color(0.0f, 0.0f, 0.0f, 0.3f).draw(this.getX(), this.getY());
    }

    private void createMainMenu() {
        String[] buttonTexts;
        this.mainMenuForm = (Form)this.addComponent((FormComponent)new Form("mainmenu", 360, 350));
        this.mainMenuForm.drawBase = false;
        int currentY = 10;
        this.mainMenuForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"admintools"), WHITE_TEXT_20, -1, 10, currentY));
        int buttonMargin = 16;
        int maxButtonWidth = this.mainMenuForm.getWidth() - buttonMargin * 2;
        for (String text : buttonTexts = new String[]{Localization.translate((String)"ui", (String)"buildtools"), Localization.translate((String)"ui", (String)"zonetools"), Localization.translate((String)"ui", (String)"commandcenter")}) {
            ResponsiveButtonHelper.doesTextFit(text, maxButtonWidth);
        }
        FormTextButton buildToolsButton = ResponsiveButtonHelper.createLocalizedButton("ui", "buildtools", buttonMargin, currentY += 35, maxButtonWidth);
        this.mainMenuForm.addComponent((FormComponent)buildToolsButton);
        buildToolsButton.onClicked(e -> this.showBuildTools());
        FormTextButton zoneToolsButton = ResponsiveButtonHelper.createLocalizedButton("ui", "zonetools", buttonMargin, currentY += 40, maxButtonWidth);
        this.mainMenuForm.addComponent((FormComponent)zoneToolsButton);
        zoneToolsButton.onClicked(e -> this.showZoneTools());
        FormTextButton commandCenterButton = ResponsiveButtonHelper.createLocalizedButton("ui", "commandcenter", buttonMargin, currentY += 40, maxButtonWidth);
        this.mainMenuForm.addComponent((FormComponent)commandCenterButton);
        commandCenterButton.onClicked(e -> this.showCommandCenter());
    }

    private void createBuildToolsMenu() {
        int formWidth = 460;
        int formHeight = 480;
        this.buildToolsForm = (Form)this.addComponent((FormComponent)new Form("buildtools", formWidth, formHeight));
        this.buildToolsForm.drawBase = false;
        int margin = 10;
        int contentWidth = formWidth - margin * 2;
        int currentY = 10;
        this.buildToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"buildtools"), WHITE_TEXT_20, -1, margin, currentY));
        BuildModeManager manager = BuildModeManager.getInstance(this.client);
        String buildModeText = manager.buildModeEnabled ? Localization.translate((String)"ui", (String)"buildmodeon") : Localization.translate((String)"ui", (String)"buildmodeoff");
        this.buildModeToggle = (FormCheckBox)this.buildToolsForm.addComponent((FormComponent)new WhiteTextCheckBox(buildModeText, margin, currentY += 32, contentWidth, manager.buildModeEnabled, WHITE_TEXT_14));
        this.buildModeToggle.onClicked(e -> {
            manager.setBuildModeEnabled(this.buildModeToggle.checked);
            String text = this.buildModeToggle.checked ? Localization.translate((String)"ui", (String)"buildmodeon") : Localization.translate((String)"ui", (String)"buildmodeoff");
            this.buildModeToggle.setText(text, contentWidth);
        });
        this.buildToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"linetypes") + ":", WHITE_TEXT_14, -1, margin, currentY += 30));
        currentY += 22;
        this.lineTypeCheckboxes = new FormCheckBox[5];
        this.shapeCheckboxes = new FormCheckBox[5];
        int[] lineTypeShapes = new int[]{0, 1, 2, 3, 4};
        String[] lineTypeNames = new String[]{Localization.translate((String)"ui", (String)"shapenormal"), Localization.translate((String)"ui", (String)"shapeline"), Localization.translate((String)"ui", (String)"shapecross"), Localization.translate((String)"ui", (String)"shapel"), Localization.translate((String)"ui", (String)"shapet")};
        int buttonWidth = (contentWidth - 20) / 5;
        int buttonX = margin;
        for (int i = 0; i < lineTypeShapes.length; ++i) {
            int shapeIndex = lineTypeShapes[i];
            this.lineTypeCheckboxes[i] = (FormCheckBox)this.buildToolsForm.addComponent((FormComponent)new WhiteTextCheckBox(lineTypeNames[i], buttonX, currentY, buttonWidth, manager.selectedShape == shapeIndex, WHITE_TEXT_10));
            this.lineTypeCheckboxes[i].onClicked(e -> {
                for (FormCheckBox cb : this.lineTypeCheckboxes) {
                    if (cb == null) continue;
                    cb.checked = false;
                }
                for (FormCheckBox cb : this.shapeCheckboxes) {
                    if (cb == null) continue;
                    cb.checked = false;
                }
                ((FormCheckBox)e.from).checked = true;
                manager.setShape(shapeIndex);
                if (this.hollowCheckbox != null) {
                    this.updateHollowCheckbox(this.hollowCheckbox);
                }
            });
            buttonX += buttonWidth + 4;
        }
        this.buildToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"shapes") + ":", WHITE_TEXT_14, -1, margin, currentY += 26));
        currentY += 22;
        int[] shapeTypes = new int[]{5, 6, 7, 8, 9};
        String[] shapeNames = new String[]{Localization.translate((String)"ui", (String)"shapesquare"), Localization.translate((String)"ui", (String)"shapecircle"), Localization.translate((String)"ui", (String)"shapediamond"), Localization.translate((String)"ui", (String)"shapehalfcircle"), Localization.translate((String)"ui", (String)"shapetriangle")};
        buttonWidth = (contentWidth - 16) / 5;
        buttonX = margin;
        for (int i = 0; i < shapeTypes.length; ++i) {
            int shapeIndex = shapeTypes[i];
            this.shapeCheckboxes[i] = (FormCheckBox)this.buildToolsForm.addComponent((FormComponent)new WhiteTextCheckBox(shapeNames[i], buttonX, currentY, buttonWidth, manager.selectedShape == shapeIndex, WHITE_TEXT_10));
            this.shapeCheckboxes[i].onClicked(e -> {
                for (FormCheckBox cb : this.lineTypeCheckboxes) {
                    if (cb == null) continue;
                    cb.checked = false;
                }
                for (FormCheckBox cb : this.shapeCheckboxes) {
                    if (cb == null) continue;
                    cb.checked = false;
                }
                ((FormCheckBox)e.from).checked = true;
                manager.setShape(shapeIndex);
                if (this.hollowCheckbox != null) {
                    this.updateHollowCheckbox(this.hollowCheckbox);
                }
            });
            buttonX += buttonWidth + 4;
        }
        this.hollowCheckbox = (FormCheckBox)this.buildToolsForm.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"hollow"), margin, currentY += 26, contentWidth, manager.isHollow, WHITE_TEXT_11));
        this.hollowCheckbox.onClicked(e -> manager.setHollow(this.hollowCheckbox.checked));
        this.updateHollowCheckbox(this.hollowCheckbox);
        FormSlider lineLengthSlider = (FormSlider)this.buildToolsForm.addComponent((FormComponent)new FormSlider(Localization.translate((String)"ui", (String)"linelengthfull"), margin, currentY += 30, manager.lineLength, 1, 50, contentWidth, WHITE_TEXT_11));
        lineLengthSlider.drawValueInPercent = false;
        lineLengthSlider.onChanged(e -> manager.setLineLength(lineLengthSlider.getValue()));
        FormSlider squareSizeSlider = (FormSlider)this.buildToolsForm.addComponent((FormComponent)new FormSlider(Localization.translate((String)"ui", (String)"squaresizefull"), margin, currentY += lineLengthSlider.getTotalHeight() + 8, manager.squareSize, 1, 25, contentWidth, WHITE_TEXT_11));
        squareSizeSlider.drawValueInPercent = false;
        squareSizeSlider.onChanged(e -> manager.setSquareSize(squareSizeSlider.getValue()));
        FormSlider circleRadiusSlider = (FormSlider)this.buildToolsForm.addComponent((FormComponent)new FormSlider(Localization.translate((String)"ui", (String)"circleradiusfull"), margin, currentY += squareSizeSlider.getTotalHeight() + 8, manager.circleRadius, 1, 25, contentWidth, WHITE_TEXT_11));
        circleRadiusSlider.drawValueInPercent = false;
        circleRadiusSlider.onChanged(e -> manager.setCircleRadius(circleRadiusSlider.getValue()));
        FormSlider spacingSlider = (FormSlider)this.buildToolsForm.addComponent((FormComponent)new FormSlider(Localization.translate((String)"ui", (String)"spacingfull"), margin, currentY += circleRadiusSlider.getTotalHeight() + 8, manager.spacing, 1, 10, contentWidth, WHITE_TEXT_11));
        spacingSlider.drawValueInPercent = false;
        spacingSlider.onChanged(e -> manager.setSpacing(spacingSlider.getValue()));
        this.buildToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"direction") + ":", WHITE_TEXT_14, -1, margin, currentY += spacingSlider.getTotalHeight() + 8));
        currentY += 22;
        String[] directionNames = new String[]{Localization.translate((String)"ui", (String)"directionup"), Localization.translate((String)"ui", (String)"directiondown"), Localization.translate((String)"ui", (String)"directionleft"), Localization.translate((String)"ui", (String)"directionright")};
        int[] directionValues = new int[]{0, 1, 2, 3};
        FormCheckBox[] directionCheckboxes = new FormCheckBox[4];
        buttonWidth = (contentWidth - 12) / 4;
        buttonX = margin;
        for (int i = 0; i < directionValues.length; ++i) {
            int dirValue = directionValues[i];
            directionCheckboxes[i] = (FormCheckBox)this.buildToolsForm.addComponent((FormComponent)new WhiteTextCheckBox(directionNames[i], buttonX, currentY, buttonWidth, manager.direction == dirValue, WHITE_TEXT_10));
            directionCheckboxes[i].onClicked(e -> {
                for (FormCheckBox cb : directionCheckboxes) {
                    if (cb == null) continue;
                    cb.checked = false;
                }
                ((FormCheckBox)e.from).checked = true;
                manager.setDirection(dirValue);
            });
            buttonX += buttonWidth + 4;
        }
        this.buildToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"rotationinfo"), WHITE_TEXT_10, -1, margin, currentY += 26));
        FormTextButton backButton = (FormTextButton)this.buildToolsForm.addComponent((FormComponent)new FormTextButton(Localization.translate((String)"ui", (String)"backtomenu"), margin, currentY += 22, contentWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        backButton.onClicked(e -> this.showMainMenu());
    }

    private void createZoneToolsMenu() {
        int formWidth = 400;
        int formHeight = 400;
        this.zoneToolsForm = (Form)this.addComponent((FormComponent)new Form("zonetools", formWidth, formHeight));
        this.zoneToolsForm.drawBase = false;
        int margin = 10;
        int contentWidth = formWidth - margin * 2;
        int currentY = 10;
        this.zoneToolsForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"zonetools"), WHITE_TEXT_20, -1, margin, currentY));
        FormTextButton protectedZonesButton = (FormTextButton)this.zoneToolsForm.addComponent((FormComponent)new FormTextButton(Localization.translate((String)"ui", (String)"protectedzones"), margin, currentY += 35, contentWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        protectedZonesButton.onClicked(e -> this.showProtectedZonesList());
        FormTextButton pvpZonesButton = (FormTextButton)this.zoneToolsForm.addComponent((FormComponent)new FormTextButton(Localization.translate((String)"ui", (String)"pvpzones"), margin, currentY += 40, contentWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        pvpZonesButton.onClicked(e -> this.showPvPZonesList());
        FormTextButton backButton = (FormTextButton)this.zoneToolsForm.addComponent((FormComponent)new FormTextButton(Localization.translate((String)"ui", (String)"backtomenu"), margin, currentY += 60, contentWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        backButton.onClicked(e -> this.showMainMenu());
    }

    private void createCommandCenterMenu() {
        this.commandCenterPanel = new CommandCenterPanel(this.client, () -> this.showMainMenu());
        this.commandCenterBuilt = false;
    }

    private void createProtectedZonesForm() {
        int formWidth = 600;
        int formHeight = 500;
        this.protectedZonesForm = (Form)this.addComponent((FormComponent)new Form("protectedzones", formWidth, formHeight));
        this.protectedZonesForm.drawBase = false;
        this.protectedZonesForm.setHidden(true);
    }

    private void createPvPZonesForm() {
        int formWidth = 600;
        int formHeight = 500;
        this.pvpZonesForm = (Form)this.addComponent((FormComponent)new Form("   ", formWidth, formHeight));
        this.pvpZonesForm.drawBase = false;
        this.pvpZonesForm.setHidden(true);
    }

    public void updateZones(Map<Integer, ProtectedZone> newProtectedZones, Map<Integer, PvPZone> newPvPZones) {
        ModLogger.info("Updating local zone storage - " + newProtectedZones.size() + " protected, " + newPvPZones.size() + " PVP");
        this.protectedZones = new HashMap<Integer, ProtectedZone>(newProtectedZones);
        this.pvpZones = new HashMap<Integer, PvPZone>(newPvPZones);
        if (this.activePlayerDropdown != null) {
            ModLogger.info("Skipping zone list refresh - player dropdown is open");
            return;
        }
        if (!this.protectedZonesForm.isHidden()) {
            ModLogger.info("Refreshing protected zones list after sync");
            this.refreshZoneList(true);
        } else if (!this.pvpZonesForm.isHidden()) {
            ModLogger.info("Refreshing PVP zones list after sync");
            this.refreshZoneList(false);
        }
    }

    @Deprecated
    public void onZonesUpdated() {
        if (!this.protectedZonesForm.isHidden()) {
            ModLogger.info("Refreshing protected zones list after sync");
            this.refreshZoneList(true);
        } else if (!this.pvpZonesForm.isHidden()) {
            ModLogger.info("Refreshing PVP zones list after sync");
            this.refreshZoneList(false);
        } else {
            ModLogger.info("Zone sync received but no zone list is currently visible");
        }
    }

    public void onZoneChanged(AdminZone zone, boolean isProtectedZone) {
        ModLogger.info("Zone changed - " + zone.uniqueID + " (" + zone.name + ")");
        if (isProtectedZone) {
            this.protectedZones.put(zone.uniqueID, (ProtectedZone)zone);
        } else {
            this.pvpZones.put(zone.uniqueID, (PvPZone)zone);
        }
        if (this.activePlayerDropdown != null) {
            ModLogger.info("Skipping zone list refresh - player dropdown is open");
            return;
        }
        if (isProtectedZone && !this.protectedZonesForm.isHidden()) {
            this.refreshZoneList(true);
        } else if (!isProtectedZone && !this.pvpZonesForm.isHidden()) {
            this.refreshZoneList(false);
        }
    }

    public void onZoneRemoved(int uniqueID, boolean isProtectedZone) {
        System.out.println("MedievalSim: Zone removed - " + uniqueID);
        if (isProtectedZone) {
            this.protectedZones.remove(uniqueID);
        } else {
            this.pvpZones.remove(uniqueID);
        }
        if (isProtectedZone && !this.protectedZonesForm.isHidden()) {
            this.refreshZoneList(true);
        } else if (!isProtectedZone && !this.pvpZonesForm.isHidden()) {
            this.refreshZoneList(false);
        }
    }

    private void refreshZoneList(boolean isProtectedZones) {
        Form targetForm = isProtectedZones ? this.protectedZonesForm : this.pvpZonesForm;
        targetForm.clearComponents();
        int margin = 10;
        int contentWidth = targetForm.getWidth() - margin * 2;
        int currentY = 10;
        String titleKey = isProtectedZones ? "protectedzones" : "pvpzones";
        targetForm.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)titleKey), WHITE_TEXT_20, -1, margin, currentY));
        int scrollHeight = targetForm.getHeight() - (currentY += 35) - 70;
        FormContentBox scrollContentBox = (FormContentBox)targetForm.addComponent((FormComponent)new FormContentBox(margin, currentY, contentWidth, scrollHeight));
        currentY += scrollHeight + 10;
        int yPos = 10;
        if (isProtectedZones) {
            ModLogger.info("refreshZoneList - found " + this.protectedZones.size() + " protected zones in local storage");
            if (this.protectedZones.isEmpty()) {
                scrollContentBox.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"nozonescreated"), WHITE_TEXT_16, -1, scrollContentBox.getWidth() / 2, yPos));
            } else {
                for (ProtectedZone protectedZone : this.protectedZones.values()) {
                    yPos = this.addZoneEntry(scrollContentBox, protectedZone, yPos, isProtectedZones);
                }
            }
        } else {
            ModLogger.info("refreshZoneList - found " + this.pvpZones.size() + " PVP zones in local storage");
            if (this.pvpZones.isEmpty()) {
                scrollContentBox.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"nozonescreated"), WHITE_TEXT_16, -1, scrollContentBox.getWidth() / 2, yPos));
            } else {
                for (PvPZone pvPZone : this.pvpZones.values()) {
                    yPos = this.addZoneEntry(scrollContentBox, pvPZone, yPos, isProtectedZones);
                }
            }
        }
        int contentHeight = Math.max(yPos + 10, scrollContentBox.getHeight());
        scrollContentBox.setContentBox(new Rectangle(0, 0, scrollContentBox.getWidth(), contentHeight));
        int controlsY = currentY - 26;
        String createZoneText = isProtectedZones ? Localization.translate((String)"ui", (String)"createprotectedzone") : Localization.translate((String)"ui", (String)"createpvpzone");
        FormTextButton formTextButton = ResponsiveButtonHelper.createButton(createZoneText, margin, currentY, targetForm.getWidth() - margin * 2);
        targetForm.addComponent((FormComponent)formTextButton);
        formTextButton.onClicked(e -> this.startZoneCreation(isProtectedZones));
        if (!isProtectedZones) {
            int savedRadius = 50;
            try {
                LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> m.id.equals("medieval.sim")).findFirst().orElse(null);
                if (mod != null && mod.getSettings() instanceof MedievalSimSettings) {
                    MedievalSimSettings s = (MedievalSimSettings)mod.getSettings();
                    savedRadius = s.pvpForceCleanRadius;
                }
            }
            catch (Exception mod) {
                // empty catch block
            }
            int btnWidth = 180;
            int btnXCentered = (targetForm.getWidth() - btnWidth) / 2;
            int btnY = controlsY;
            FormTextButton forceCleanAllButton = (FormTextButton)targetForm.addComponent((FormComponent)new FormTextButton("Force Clean Here", btnXCentered, btnY, btnWidth, FormInputSize.SIZE_32, ButtonColor.RED));
            targetForm.addComponent((FormComponent)new FormLabel("Removes stray barrier objects within the chosen radius around your current location.  ", WHITE_TEXT_11, -1, targetForm.getWidth() / 5, btnY - 18));
            int sliderWidth = btnWidth;
            int sliderX = btnXCentered;
            int sliderY = btnY + 36;
            FormSlider cleanSlider = (FormSlider)targetForm.addComponent((FormComponent)new FormSlider("Radius: " + savedRadius, sliderX, sliderY, savedRadius, 10, 500, sliderWidth, WHITE_TEXT_10));
            cleanSlider.drawValueInPercent = false;
            forceCleanAllButton.onClicked(e -> {
                if (this.client != null && this.client.getPlayer() != null) {
                    int tileX = (int)(this.client.getPlayer().x / 32.0f);
                    int tileY = (int)(this.client.getPlayer().y / 32.0f);
                    int radius = cleanSlider.getValue();
                    this.client.network.sendPacket((Packet)new PacketForceClean(tileX, tileY, radius));
                    try {
                        LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m0 -> m0.id.equals("medieval.sim")).findFirst().orElse(null);
                        if (mod != null && mod.getSettings() instanceof MedievalSimSettings) {
                            MedievalSimSettings s = (MedievalSimSettings)mod.getSettings();
                            s.pvpForceCleanRadius = Math.max(10, Math.min(500, radius));
                            Settings.saveClientSettings();
                        }
                    }
                    catch (Exception ex) {
                        System.err.println("MedievalSim: WARNING - Failed to save force-clean radius: " + ex.getMessage());
                    }
                    ModLogger.info("Sent force-clean request for radius " + radius + " at (" + tileX + "," + tileY + ")");
                }
            });
        }
        FormTextButton backButton = ResponsiveButtonHelper.createLocalizedButton("ui", "back", targetForm.getWidth() - margin - 240, currentY, 240);
        targetForm.addComponent((FormComponent)backButton);
        backButton.onClicked(e -> this.showZoneTools());
    }

    private int addZoneEntry(FormContentBox scrollContentBox, AdminZone zone, int yPos, boolean isProtectedZone) {
        int baseEntryHeight = 90;
        int configHeight = !isProtectedZone ? 80 : 300;
        boolean showConfig = this.expandedConfigZones.contains(zone.uniqueID);
        int entryHeight = baseEntryHeight + (showConfig ? configHeight : 0);
        int margin = 5;
        int xPos = 10;
        FormContentBox entryBox = (FormContentBox)scrollContentBox.addComponent((FormComponent)new FormContentBox(margin, yPos, scrollContentBox.getWidth() - margin * 2 - 20, entryHeight));
        FontOptions labelOptions = new FontOptions(16).color(Color.WHITE);
        FormLabelEdit nameLabel = (FormLabelEdit)entryBox.addComponent((FormComponent)new FormLabelEdit(zone.name.isEmpty() ? "Unnamed Zone" : zone.name, labelOptions, Color.WHITE, xPos, 5, entryBox.getWidth() - 80, 24));
        FormContentIconButton[] renameButton = new FormContentIconButton[]{(FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(entryBox.getWidth() - 60, 5, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().container_rename, new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"renamebutton"))}))};
        Runnable updateRename = () -> {
            if (nameLabel.isTyping()) {
                renameButton[0].setIcon(this.getInterfaceStyle().container_rename_save);
                renameButton[0].setTooltips(new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"savebutton"))});
            } else {
                if (!nameLabel.getText().equals(zone.name)) {
                    String newName = nameLabel.getText();
                    this.client.network.sendPacket((Packet)new PacketRenameZone(zone.uniqueID, isProtectedZone, (GameMessage)new StaticMessage(newName)));
                    System.out.println("Sent rename request for zone " + zone.uniqueID + " to: " + newName);
                }
                renameButton[0].setIcon(this.getInterfaceStyle().container_rename);
                renameButton[0].setTooltips(new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"renamebutton"))});
                nameLabel.setText(zone.name);
            }
        };
        nameLabel.onMouseChangedTyping(e -> updateRename.run());
        nameLabel.onSubmit(e -> updateRename.run());
        renameButton[0].onClicked(e -> {
            if (nameLabel.isTyping()) {
                nameLabel.setTyping(false);
            } else {
                nameLabel.setTyping(true);
            }
        });
        String info = "ID: " + zone.uniqueID + " | Tiles: " + zone.zoning.size();
        entryBox.addComponent((FormComponent)new FormLabel(info, WHITE_TEXT_11, -1, xPos, 32));
        int buttonY = 55;
        int buttonSpacing = 28;
        int buttonX = xPos;
        FormContentIconButton expandButton = (FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(buttonX, buttonY, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().config_button_32, new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"expandzone"))}));
        expandButton.onClicked(e -> this.startZoneEdit(zone, isProtectedZone));
        FormContentIconButton deleteButton = (FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(buttonX += buttonSpacing, buttonY, FormInputSize.SIZE_24, ButtonColor.RED, (ButtonTexture)this.getInterfaceStyle().container_storage_remove, new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"deletezone"))}));
        deleteButton.onClicked(e -> this.deleteZone(zone, isProtectedZone));
        buttonX += buttonSpacing;
        if (!isProtectedZone) {
            PvPZone pvpZone = (PvPZone)zone;
            FormContentIconButton configButton = (FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(buttonX, buttonY, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)(showConfig ? this.getInterfaceStyle().button_collapsed_16 : this.getInterfaceStyle().button_expanded_16), new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"configurebutton"))}));
            configButton.onClicked(e -> {
                try {
                    ModLogger.info("Config button clicked for zone " + zone.uniqueID + " (wasExpanded=" + this.expandedConfigZones.contains(zone.uniqueID) + ")");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (this.expandedConfigZones.contains(zone.uniqueID)) {
                    this.expandedConfigZones.remove(zone.uniqueID);
                } else {
                    this.expandedConfigZones.add(zone.uniqueID);
                }
                this.refreshZoneList(false);
            });
            if (showConfig) {
                int configY = baseEntryHeight + 5;
                int sliderWidth = entryBox.getWidth() - xPos * 2;
                int damageValue = (int)(pvpZone.damageMultiplier * 1000.0f);
                FormSlider[] damageSliderRef = new FormSlider[]{(FormSlider)entryBox.addComponent((FormComponent)new FormSlider("Damage: " + String.format("%.1f%%", Float.valueOf(pvpZone.damageMultiplier * 100.0f)), xPos, configY, damageValue, 1, 100, sliderWidth, WHITE_TEXT_10))};
                damageSliderRef[0].drawValueInPercent = false;
                damageSliderRef[0].onChanged(e -> {
                    float newMultiplier;
                    pvpZone.damageMultiplier = newMultiplier = (float)damageSliderRef[0].getValue() / 1000.0f;
                    this.refreshZoneList(false);
                    this.client.network.sendPacket((Packet)new PacketConfigurePvPZone(pvpZone.uniqueID, newMultiplier, pvpZone.combatLockSeconds, pvpZone.dotDamageMultiplier, pvpZone.dotIntervalMultiplier));
                });
                FormSlider[] combatLockSliderRef = new FormSlider[]{(FormSlider)entryBox.addComponent((FormComponent)new FormSlider("Combat Lock: " + pvpZone.combatLockSeconds + "s", xPos, configY += damageSliderRef[0].getTotalHeight() + 5, pvpZone.combatLockSeconds, 0, 10, sliderWidth, WHITE_TEXT_10))};
                combatLockSliderRef[0].drawValueInPercent = false;
                combatLockSliderRef[0].onChanged(e -> {
                    int newCombatLock;
                    pvpZone.combatLockSeconds = newCombatLock = combatLockSliderRef[0].getValue();
                    this.refreshZoneList(false);
                    this.client.network.sendPacket((Packet)new PacketConfigurePvPZone(pvpZone.uniqueID, pvpZone.damageMultiplier, newCombatLock, pvpZone.dotDamageMultiplier, pvpZone.dotIntervalMultiplier));
                });
                FormSlider dotDamageSlider = (FormSlider)entryBox.addComponent((FormComponent)new FormSlider("DoT Damage Mult: " + String.format("%.2fx", Float.valueOf(pvpZone.dotDamageMultiplier)), xPos, configY += combatLockSliderRef[0].getTotalHeight() + 6, (int)(pvpZone.dotDamageMultiplier * 100.0f), 1, 200, sliderWidth, WHITE_TEXT_10));
                dotDamageSlider.drawValueInPercent = false;
                dotDamageSlider.onChanged(e -> {
                    float v;
                    pvpZone.dotDamageMultiplier = v = (float)dotDamageSlider.getValue() / 100.0f;
                    this.refreshZoneList(false);
                    this.client.network.sendPacket((Packet)new PacketConfigurePvPZone(pvpZone.uniqueID, pvpZone.damageMultiplier, pvpZone.combatLockSeconds, pvpZone.dotDamageMultiplier, pvpZone.dotIntervalMultiplier));
                });
                FormSlider dotIntervalSlider = (FormSlider)entryBox.addComponent((FormComponent)new FormSlider("DoT Interval Mult: " + String.format("%.2fx", Float.valueOf(pvpZone.dotIntervalMultiplier)), xPos, configY += dotDamageSlider.getTotalHeight() + 6, (int)(pvpZone.dotIntervalMultiplier * 100.0f), 25, 400, sliderWidth, WHITE_TEXT_10));
                dotIntervalSlider.drawValueInPercent = false;
                dotIntervalSlider.onChanged(e -> {
                    float v;
                    pvpZone.dotIntervalMultiplier = v = (float)dotIntervalSlider.getValue() / 100.0f;
                    this.refreshZoneList(false);
                    this.client.network.sendPacket((Packet)new PacketConfigurePvPZone(pvpZone.uniqueID, pvpZone.damageMultiplier, pvpZone.combatLockSeconds, pvpZone.dotDamageMultiplier, pvpZone.dotIntervalMultiplier));
                });
            }
        } else {
            ProtectedZone protectedZone = (ProtectedZone)zone;
            FormContentIconButton configButton = (FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(buttonX, buttonY, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)(showConfig ? this.getInterfaceStyle().button_collapsed_16 : this.getInterfaceStyle().button_expanded_16), new GameMessage[]{new StaticMessage(Localization.translate((String)"ui", (String)"configurebutton"))}));
            configButton.onClicked(e -> {
                try {
                    ModLogger.info("Config button clicked for protected zone " + zone.uniqueID + " (wasExpanded=" + this.expandedConfigZones.contains(zone.uniqueID) + ")");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (this.expandedConfigZones.contains(zone.uniqueID)) {
                    this.expandedConfigZones.remove(zone.uniqueID);
                } else {
                    this.expandedConfigZones.add(zone.uniqueID);
                }
                this.refreshZoneList(true);
            });
            if (showConfig) {
                int configY = baseEntryHeight + 5;
                int labelX = xPos + 10;
                int inputWidth = entryBox.getWidth() - xPos * 2 - 10;
                FormLabel explanationLabel = (FormLabel)entryBox.addComponent((FormComponent)new FormLabel("Checking permissions below grants access to team members", WHITE_TEXT_10, 0, labelX + 10, configY));
                FormLabel ownerLabel = (FormLabel)entryBox.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"ownerlabel"), WHITE_TEXT_10, 0, labelX, configY += explanationLabel.getHeight() + 8));
                int ownerInputY = configY += ownerLabel.getHeight() + 2;
                FormLabelEdit ownerInput = (FormLabelEdit)entryBox.addComponent((FormComponent)new FormLabelEdit(protectedZone.getOwnerDisplayName(), WHITE_TEXT_10, Color.WHITE, labelX, ownerInputY, inputWidth - 25, 20));
                FormContentIconButton dropdownButton = (FormContentIconButton)entryBox.addComponent((FormComponent)new FormContentIconButton(labelX + inputWidth - 20, ownerInputY, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().button_expanded_16, new GameMessage[]{new StaticMessage("Select player")}));
                dropdownButton.onClicked(e -> this.togglePlayerDropdown(entryBox, protectedZone, ownerInput, labelX, ownerInputY + 22, inputWidth + 40, 200));
                ownerInput.onSubmit(e -> {
                    String newOwner = ownerInput.getText();
                    if (newOwner != null && !newOwner.trim().isEmpty()) {
                        this.sendProtectedZoneConfigPacket(protectedZone, newOwner.trim());
                    }
                });
                FormCheckBox allowTeamCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"allowownsteam"), labelX, configY += 28, inputWidth, protectedZone.getAllowOwnerTeam(), WHITE_TEXT_10));
                allowTeamCheckbox.onClicked(e -> {
                    protectedZone.setAllowOwnerTeam(allowTeamCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormLabel permLabel = (FormLabel)entryBox.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"teampermissions"), WHITE_TEXT_10, 0, labelX, configY += 24));
                FormCheckBox canBreakCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"canbreak"), labelX + 10, configY += permLabel.getHeight() + 4, inputWidth - 10, protectedZone.getCanBreak(), WHITE_TEXT_10));
                canBreakCheckbox.onClicked(e -> {
                    protectedZone.setCanBreak(canBreakCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canPlaceCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"canplace"), labelX + 10, configY += 20, inputWidth - 10, protectedZone.getCanPlace(), WHITE_TEXT_10));
                canPlaceCheckbox.onClicked(e -> {
                    protectedZone.setCanPlace(canPlaceCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormLabel interactionsLabel = (FormLabel)entryBox.addComponent((FormComponent)new FormLabel(Localization.translate((String)"ui", (String)"interactions"), WHITE_TEXT_10, 0, labelX + 20, configY += 20));
                FormCheckBox canInteractDoorsCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractdoors"), labelX + 20, configY += interactionsLabel.getHeight() + 4, inputWidth - 20, protectedZone.getCanInteractDoors(), WHITE_TEXT_10));
                canInteractDoorsCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractDoors(canInteractDoorsCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canInteractContainersCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractcontainers"), labelX + 20, configY += 20, inputWidth - 20, protectedZone.getCanInteractContainers(), WHITE_TEXT_10));
                canInteractContainersCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractContainers(canInteractContainersCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canInteractStationsCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractstations"), labelX + 20, configY += 20, inputWidth - 20, protectedZone.getCanInteractStations(), WHITE_TEXT_10));
                canInteractStationsCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractStations(canInteractStationsCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canInteractSignsCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractsigns"), labelX + 20, configY += 20, inputWidth - 20, protectedZone.getCanInteractSigns(), WHITE_TEXT_10));
                canInteractSignsCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractSigns(canInteractSignsCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canInteractSwitchesCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractswitches"), labelX + 20, configY += 20, inputWidth - 20, protectedZone.getCanInteractSwitches(), WHITE_TEXT_10));
                canInteractSwitchesCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractSwitches(canInteractSwitchesCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
                FormCheckBox canInteractFurnitureCheckbox = (FormCheckBox)entryBox.addComponent((FormComponent)new WhiteTextCheckBox(Localization.translate((String)"ui", (String)"caninteractfurniture"), labelX + 20, configY += 20, inputWidth - 20, protectedZone.getCanInteractFurniture(), WHITE_TEXT_10));
                canInteractFurnitureCheckbox.onClicked(e -> {
                    protectedZone.setCanInteractFurniture(canInteractFurnitureCheckbox.checked);
                    this.sendProtectedZoneConfigPacket(protectedZone, null);
                });
            }
        }
        return yPos + entryHeight + margin;
    }

    private void sendProtectedZoneConfigPacket(ProtectedZone zone, String ownerName) {
        String ownerToSend = ownerName != null ? ownerName : zone.getOwnerDisplayName();
        this.client.network.sendPacket((Packet)new PacketConfigureProtectedZone(zone.uniqueID, ownerToSend, zone.getAllowOwnerTeam(), zone.getCanBreak(), zone.getCanPlace(), zone.getCanInteractDoors(), zone.getCanInteractContainers(), zone.getCanInteractStations(), zone.getCanInteractSigns(), zone.getCanInteractSwitches(), zone.getCanInteractFurniture()));
    }

    private void startZoneCreation(boolean isProtectedZone) {
        boolean wasCleared = GameToolManager.clearGameTools((Object)((Object)this));
        if (!wasCleared) {
            GameToolManager.setGameTool((GameTool)new CreateOrExpandZoneTool(this.client, isProtectedZone, () -> isProtectedZone ? this.protectedZones : this.pvpZones), (Object)((Object)this));
        }
    }

    private void startZoneEdit(AdminZone zone, boolean isProtectedZone) {
        this.startZoneCreation(isProtectedZone);
    }

    private void deleteZone(AdminZone zone, boolean isProtectedZone) {
        this.client.network.sendPacket((Packet)new PacketDeleteZone(zone.uniqueID, isProtectedZone));
        System.out.println("Sent delete zone request for " + zone.name);
    }

    private void updateHollowCheckbox(FormCheckBox hollowCheckbox) {
        BuildModeManager manager = BuildModeManager.getInstance();
        boolean canBeHollow = manager != null && manager.canBeHollow();
        hollowCheckbox.setActive(canBeHollow);
        if (!canBeHollow) {
            hollowCheckbox.checked = false;
            manager.setHollow(false);
        }
    }

    private void createControlButtons() {
        this.minimizeButton = (FormContentIconButton)this.addComponent((FormComponent)new FormContentIconButton(this.getWidth() - 20 - 5, 5, FormInputSize.SIZE_16, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().button_collapsed_16, new GameMessage[]{new StaticMessage("Minimize")}));
        this.minimizeButton.onClicked(e -> this.toggleMinimize());
        this.minimizedTitleLabel = (FormLabel)this.addComponent((FormComponent)new FormLabel("", WHITE_TEXT_16, 0, this.getWidth() / 2, 7));
    }

    private void toggleMinimize() {
        boolean bl = this.isMinimized = !this.isMinimized;
        if (this.isMinimized) {
            this.savedWidth = this.getWidth();
            this.savedHeight = this.getHeight();
            this.wasShowingBuildTools = !this.buildToolsForm.isHidden();
            this.wasShowingZoneTools = !this.zoneToolsForm.isHidden();
            this.wasShowingProtectedZones = !this.protectedZonesForm.isHidden();
            this.wasShowingPvPZones = !this.pvpZonesForm.isHidden();
            this.wasShowingCommandCenter = this.commandCenterBuilt;
            if (this.commandCenterBuilt) {
                this.commandCenterPanel.removeComponents(this);
                this.commandCenterBuilt = false;
            }
            this.setWidth(160);
            this.setHeight(30);
            this.mainMenuForm.setHidden(true);
            this.buildToolsForm.setHidden(true);
            this.zoneToolsForm.setHidden(true);
            this.protectedZonesForm.setHidden(true);
            this.pvpZonesForm.setHidden(true);
            String title = this.wasShowingBuildTools ? Localization.translate((String)"ui", (String)"buildtools") : (this.wasShowingZoneTools ? Localization.translate((String)"ui", (String)"zonetools") : (this.wasShowingProtectedZones ? Localization.translate((String)"ui", (String)"protectedzones") : (this.wasShowingPvPZones ? Localization.translate((String)"ui", (String)"pvpzones") : (this.wasShowingCommandCenter ? Localization.translate((String)"ui", (String)"commandcenter") : Localization.translate((String)"ui", (String)"admintools")))));
            this.minimizedTitleLabel.setText(title);
            this.minimizedTitleLabel.setX(53);
            this.minimizeButton.setIcon(this.getInterfaceStyle().button_expanded_16);
            this.minimizeButton.setTooltips(new GameMessage[]{new StaticMessage("Expand")});
        } else {
            this.setHeight(this.savedHeight);
            this.setWidth(this.savedWidth);
            this.minimizedTitleLabel.setText("");
            if (this.wasShowingBuildTools) {
                this.mainMenuForm.setHidden(true);
                this.buildToolsForm.setHidden(false);
                this.zoneToolsForm.setHidden(true);
                this.protectedZonesForm.setHidden(true);
                this.pvpZonesForm.setHidden(true);
            } else if (this.wasShowingZoneTools) {
                this.mainMenuForm.setHidden(true);
                this.buildToolsForm.setHidden(true);
                this.zoneToolsForm.setHidden(false);
                this.protectedZonesForm.setHidden(true);
                this.pvpZonesForm.setHidden(true);
            } else if (this.wasShowingProtectedZones) {
                this.mainMenuForm.setHidden(true);
                this.buildToolsForm.setHidden(true);
                this.zoneToolsForm.setHidden(true);
                this.protectedZonesForm.setHidden(false);
                this.pvpZonesForm.setHidden(true);
            } else if (this.wasShowingPvPZones) {
                this.mainMenuForm.setHidden(true);
                this.buildToolsForm.setHidden(true);
                this.zoneToolsForm.setHidden(true);
                this.protectedZonesForm.setHidden(true);
                this.pvpZonesForm.setHidden(false);
            } else if (this.wasShowingCommandCenter) {
                this.mainMenuForm.setHidden(true);
                this.buildToolsForm.setHidden(true);
                this.zoneToolsForm.setHidden(true);
                this.protectedZonesForm.setHidden(true);
                this.pvpZonesForm.setHidden(true);
                int commandCenterWidth = 600;
                int commandCenterHeight = 700;
                this.commandCenterPanel.buildComponents(this, 0, 0, commandCenterWidth, commandCenterHeight);
                this.commandCenterBuilt = true;
            } else {
                this.mainMenuForm.setHidden(false);
                this.buildToolsForm.setHidden(true);
                this.zoneToolsForm.setHidden(true);
                this.protectedZonesForm.setHidden(true);
                this.pvpZonesForm.setHidden(true);
            }
            this.minimizeButton.setIcon(this.getInterfaceStyle().button_collapsed_16);
            this.minimizeButton.setTooltips(new GameMessage[]{new StaticMessage("Minimize")});
        }
        this.setDraggingBox(new Rectangle(0, 0, this.getWidth(), this.isMinimized ? 30 : this.getHeight()));
        this.updateButtonPositions();
    }

    private void updateButtonPositions() {
        if (this.minimizeButton != null) {
            this.minimizeButton.setPosition(this.getWidth() - 20 - 5, 5);
        }
    }

    private void showMainMenu() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.removeZoneVisualization();
        if (this.commandCenterBuilt) {
            this.commandCenterPanel.removeComponents(this);
            this.commandCenterBuilt = false;
        }
        if (!this.isMinimized) {
            this.mainMenuForm.setHidden(false);
        }
        this.buildToolsForm.setHidden(true);
        this.zoneToolsForm.setHidden(true);
        this.protectedZonesForm.setHidden(true);
        this.pvpZonesForm.setHidden(true);
        this.wasShowingBuildTools = false;
        this.wasShowingZoneTools = false;
        this.wasShowingProtectedZones = false;
        this.wasShowingPvPZones = false;
        this.wasShowingCommandCenter = false;
        this.setWidth(300);
        if (!this.isMinimized) {
            this.setHeight(150);
            this.savedHeight = 150;
        }
        this.savedWidth = 300;
        this.setDraggingBox(new Rectangle(0, 0, 300, this.isMinimized ? 30 : 150));
        this.updateButtonPositions();
    }

    private void showBuildTools() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.removeZoneVisualization();
        if (this.commandCenterBuilt) {
            this.commandCenterPanel.removeComponents(this);
            this.commandCenterBuilt = false;
        }
        this.mainMenuForm.setHidden(true);
        this.buildToolsForm.setHidden(false);
        this.zoneToolsForm.setHidden(true);
        this.protectedZonesForm.setHidden(true);
        this.pvpZonesForm.setHidden(true);
        this.wasShowingBuildTools = true;
        this.wasShowingZoneTools = false;
        this.wasShowingProtectedZones = false;
        this.wasShowingPvPZones = false;
        this.wasShowingCommandCenter = false;
        if (!this.isMinimized) {
            this.buildToolsForm.setHidden(false);
        }
        this.setWidth(460);
        if (!this.isMinimized) {
            this.setHeight(480);
            this.savedHeight = 480;
        }
        this.savedWidth = 460;
        this.setDraggingBox(new Rectangle(0, 0, 460, this.isMinimized ? 30 : 480));
        this.updateButtonPositions();
    }

    private void showZoneTools() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.removeZoneVisualization();
        if (this.commandCenterBuilt) {
            this.commandCenterPanel.removeComponents(this);
            this.commandCenterBuilt = false;
        }
        this.mainMenuForm.setHidden(true);
        this.buildToolsForm.setHidden(true);
        this.zoneToolsForm.setHidden(false);
        this.protectedZonesForm.setHidden(true);
        this.pvpZonesForm.setHidden(true);
        this.wasShowingBuildTools = false;
        this.wasShowingZoneTools = true;
        this.wasShowingProtectedZones = false;
        this.wasShowingPvPZones = false;
        this.wasShowingCommandCenter = false;
        if (!this.isMinimized) {
            this.zoneToolsForm.setHidden(false);
        }
        this.setWidth(400);
        if (!this.isMinimized) {
            this.setHeight(400);
            this.savedHeight = 400;
        }
        this.savedWidth = 400;
        this.setDraggingBox(new Rectangle(0, 0, 400, this.isMinimized ? 30 : 400));
        this.updateButtonPositions();
    }

    private void showCommandCenter() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.removeZoneVisualization();
        this.mainMenuForm.setHidden(true);
        this.buildToolsForm.setHidden(true);
        this.zoneToolsForm.setHidden(true);
        this.protectedZonesForm.setHidden(true);
        this.pvpZonesForm.setHidden(true);
        this.wasShowingBuildTools = false;
        this.wasShowingZoneTools = false;
        this.wasShowingProtectedZones = false;
        this.wasShowingPvPZones = false;
        this.wasShowingCommandCenter = true;
        int commandCenterWidth = 600;
        int commandCenterHeight = 700;
        this.setWidth(commandCenterWidth);
        this.savedWidth = commandCenterWidth;
        if (!this.isMinimized) {
            this.setHeight(commandCenterHeight);
            this.savedHeight = commandCenterHeight;
            if (!this.commandCenterBuilt) {
                this.commandCenterPanel.buildComponents(this, 0, 0, commandCenterWidth, commandCenterHeight);
                this.commandCenterBuilt = true;
            }
        }
        this.setDraggingBox(new Rectangle(0, 0, commandCenterWidth, this.isMinimized ? 30 : commandCenterHeight));
        this.updateButtonPositions();
    }

    private void removeZoneVisualization() {
        if (this.protectedZonesHud != null) {
            this.protectedZonesHud.remove();
            this.protectedZonesHud = null;
        }
        if (this.pvpZonesHud != null) {
            this.pvpZonesHud.remove();
            this.pvpZonesHud = null;
        }
    }

    private void showProtectedZonesList() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.refreshZoneList(true);
        if (this.commandCenterBuilt) {
            this.commandCenterPanel.removeComponents(this);
            this.commandCenterBuilt = false;
        }
        if (this.client.getLevel() != null) {
            if (this.protectedZonesHud != null) {
                this.protectedZonesHud.remove();
            }
            this.protectedZonesHud = new ZoneVisualizationHud(() -> this.protectedZones, () -> this.pvpZones, true, false);
            this.client.getLevel().hudManager.addElement((HudDrawElement)this.protectedZonesHud);
        }
        this.mainMenuForm.setHidden(true);
        this.buildToolsForm.setHidden(true);
        this.zoneToolsForm.setHidden(true);
        this.protectedZonesForm.setHidden(false);
        this.pvpZonesForm.setHidden(true);
        this.wasShowingBuildTools = false;
        this.wasShowingZoneTools = false;
        this.wasShowingProtectedZones = true;
        this.wasShowingPvPZones = false;
        this.wasShowingCommandCenter = false;
        this.setWidth(600);
        if (!this.isMinimized) {
            this.setHeight(500);
            this.savedHeight = 500;
        }
        this.savedWidth = 600;
        this.setDraggingBox(new Rectangle(0, 0, 600, this.isMinimized ? 30 : 500));
        this.updateButtonPositions();
    }

    private void showPvPZonesList() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.refreshZoneList(false);
        if (this.commandCenterBuilt) {
            this.commandCenterPanel.removeComponents(this);
            this.commandCenterBuilt = false;
        }
        if (this.client.getLevel() != null) {
            if (this.pvpZonesHud != null) {
                this.pvpZonesHud.remove();
            }
            this.pvpZonesHud = new ZoneVisualizationHud(() -> this.protectedZones, () -> this.pvpZones, false, true);
            this.client.getLevel().hudManager.addElement((HudDrawElement)this.pvpZonesHud);
        }
        this.mainMenuForm.setHidden(true);
        this.buildToolsForm.setHidden(true);
        this.zoneToolsForm.setHidden(true);
        this.protectedZonesForm.setHidden(true);
        this.pvpZonesForm.setHidden(false);
        this.wasShowingBuildTools = false;
        this.wasShowingZoneTools = false;
        this.wasShowingProtectedZones = false;
        this.wasShowingPvPZones = true;
        this.wasShowingCommandCenter = false;
        this.setWidth(600);
        if (!this.isMinimized) {
            this.setHeight(500);
            this.savedHeight = 500;
        }
        this.savedWidth = 600;
        this.setDraggingBox(new Rectangle(0, 0, 600, this.isMinimized ? 30 : 500));
        this.updateButtonPositions();
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.syncWithBuildModeManager();
        WorldClickIntegration.updateHoverPosition();
        super.draw(tickManager, perspective, renderBox);
    }

    private void syncWithBuildModeManager() {
        try {
            int i;
            BuildModeManager manager = BuildModeManager.getInstance();
            if (this.buildModeToggle != null && this.buildModeToggle.checked != manager.buildModeEnabled) {
                this.buildModeToggle.checked = manager.buildModeEnabled;
                String text = this.buildModeToggle.checked ? Localization.translate((String)"ui", (String)"buildmodeon") : Localization.translate((String)"ui", (String)"buildmodeoff");
                this.buildModeToggle.setText(text, 440);
            }
            if (this.lineTypeCheckboxes != null) {
                int[] lineTypeShapes = new int[]{0, 1, 2, 3, 4};
                for (i = 0; i < this.lineTypeCheckboxes.length; ++i) {
                    if (this.lineTypeCheckboxes[i] == null) continue;
                    this.lineTypeCheckboxes[i].checked = manager.selectedShape == lineTypeShapes[i];
                }
            }
            if (this.shapeCheckboxes != null) {
                int[] shapeTypes = new int[]{5, 6, 7, 8, 9};
                for (i = 0; i < this.shapeCheckboxes.length; ++i) {
                    if (this.shapeCheckboxes[i] == null) continue;
                    this.shapeCheckboxes[i].checked = manager.selectedShape == shapeTypes[i];
                }
            }
            if (this.hollowCheckbox != null && this.hollowCheckbox.checked != manager.isHollow) {
                this.hollowCheckbox.checked = manager.isHollow;
            }
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public int getX() {
        return super.getX();
    }

    public int getY() {
        return super.getY();
    }

    public boolean shouldDraw() {
        State currentState = GlobalData.getCurrentState();
        if (currentState instanceof MainGame && !((MainGame)currentState).isRunning()) {
            return false;
        }
        return super.shouldDraw() && !this.isHidden();
    }

    public boolean isVisible() {
        return !this.isHidden();
    }

    public void setVisible(boolean visible) {
        this.setHidden(!visible);
        if (!visible) {
            this.removeZoneVisualization();
        }
    }

    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            this.removeZoneVisualization();
        }
    }

    private void togglePlayerDropdown(FormContentBox parent, ProtectedZone zone, FormLabelEdit ownerInput, int x, int y, int width, int maxHeight) {
        if (this.activePlayerDropdown != null && this.activeDropdownZoneID == zone.uniqueID) {
            this.hidePlayerDropdown();
            return;
        }
        this.hidePlayerDropdown();
        this.client.network.sendPacket((Packet)new PacketRequestPlayerList());
        this.activePlayerDropdown = new FormContentBox(x, y, width, Math.min(this.cachedPlayerList.size() * 24 + 10, maxHeight));
        this.activeOwnerInput = ownerInput;
        this.activeDropdownZoneID = zone.uniqueID;
        parent.addComponent((FormComponent)this.activePlayerDropdown);
        this.populatePlayerDropdown(zone);
    }

    private void populatePlayerDropdown(ProtectedZone zone) {
        if (this.activePlayerDropdown == null) {
            return;
        }
        this.activePlayerDropdown.clearComponents();
        String filterText = this.activeOwnerInput != null ? this.activeOwnerInput.getText().toLowerCase() : "";
        int buttonY = 5;
        int buttonCount = 0;
        for (PlayerDropdownEntry player : this.cachedPlayerList) {
            if (!filterText.isEmpty() && !player.matchesFilter(filterText)) continue;
            String buttonText = (player.isOnline ? "\u25cf " : "  ") + player.characterName;
            String tooltipText = "Steam ID: " + player.steamAuth;
            if (!player.isOnline && player.lastLogin > 0L) {
                long daysSinceLogin = (System.currentTimeMillis() - player.lastLogin) / 86400000L;
                tooltipText = tooltipText + "\nLast Login: " + daysSinceLogin + " days ago";
            }
            FormTextButton playerButton = new FormTextButton(buttonText, tooltipText, 5, buttonY, this.activePlayerDropdown.getWidth() - 10, FormInputSize.SIZE_20, ButtonColor.BASE);
            playerButton.onClicked(e -> {
                this.activeOwnerInput.setText(player.characterName);
                this.sendProtectedZoneConfigPacket(zone, player.characterName);
                this.hidePlayerDropdown();
            });
            this.activePlayerDropdown.addComponent((FormComponent)playerButton);
            buttonY += 22;
            ++buttonCount;
        }
        int contentHeight = Math.min(buttonCount * 22 + 10, 200);
        this.activePlayerDropdown.setHeight(contentHeight);
        if (buttonCount == 0) {
            FormLabel noPlayersLabel = new FormLabel("No players found", WHITE_TEXT_10, 0, 5, 5);
            this.activePlayerDropdown.addComponent((FormComponent)noPlayersLabel);
            this.activePlayerDropdown.setHeight(30);
        }
    }

    private void hidePlayerDropdown() {
        if (this.activePlayerDropdown != null) {
            this.refreshZoneList(true);
        }
        this.activePlayerDropdown = null;
        this.activeOwnerInput = null;
        this.activeDropdownZoneID = -1;
    }

    public void updatePlayerList(List<PlayerDropdownEntry> players) {
        ProtectedZone zone;
        this.cachedPlayerList = new ArrayList<PlayerDropdownEntry>(players);
        if (this.activePlayerDropdown != null && this.activeDropdownZoneID != -1 && (zone = this.protectedZones.get(this.activeDropdownZoneID)) != null) {
            this.populatePlayerDropdown(zone);
        }
    }

    public void dispose() {
        GameToolManager.clearGameTools((Object)((Object)this));
        this.removeZoneVisualization();
        super.dispose();
    }

    private static class WhiteTextCheckBox
    extends FormCheckBox {
        public WhiteTextCheckBox(String text, int x, int y, int maxWidth, boolean checked, FontOptions fontOptions) {
            super(text, x, y, maxWidth, checked);
            try {
                Field field = FormCheckBox.class.getDeclaredField("fontOptions");
                field.setAccessible(true);
                field.set((Object)this, fontOptions);
                this.setText(text, maxWidth);
            }
            catch (Exception e) {
                System.err.println("MedievalSim: ERROR - Failed to set white text on checkbox: " + e.getMessage());
            }
        }
    }
}

