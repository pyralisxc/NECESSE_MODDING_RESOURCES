/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.Platform
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import necesse.engine.GameCache;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameDifficulty;
import necesse.engine.GameRaidFrequency;
import necesse.engine.GlobalData;
import necesse.engine.SceneColorSetting;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputManager;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.DisplayMode;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.World;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCursorPreview;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.lists.FormControlListPopulator;
import necesse.gfx.forms.components.lists.FormLanguageList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.DifficultySelectForm;
import necesse.gfx.forms.presets.ModsForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.GameInterfaceStyle;
import necesse.level.maps.Level;
import org.lwjgl.system.Platform;

public class SettingsForm
extends FormSwitcher {
    private final ConfirmationForm confirmation;
    private final Client client;
    private final ContinueComponentManager continueComponentManager;
    public boolean reloadedInterface;
    private boolean hidden;
    private Form mainMenu;
    private Form world;
    private Form general;
    private Form language;
    private Form controlType;
    private Form controls;
    private Form interf;
    private Form graphics;
    private Form sound;
    private ModsForm mods;
    private DifficultySelectForm difficultyForm;
    private long timerConfirmEnd;
    private Runnable checkConfirmTimerComplete;
    private FormLocalTextButton mainMenuWorld;
    private Runnable customSave;
    private Runnable customLoad;
    private boolean saveActive;
    private FormContentBox worldContent;
    private int worldContentHeight;
    private FormLocalTextButton worldSave;
    private FormLocalTextButton worldBack;
    private FormLocalLabel difficultyLabel;
    private FormDropdownSelectionButton<GameDeathPenalty> deathPenalty;
    private FormDropdownSelectionButton<GameRaidFrequency> raidFrequency;
    private FormLocalCheckBox creativeMode;
    private FormLocalCheckBox survivalMode;
    private FormLocalCheckBox playerHunger;
    private FormLocalCheckBox allowOutsideCharacters;
    private FormLocalCheckBox forcedPvP;
    private FormContentBox generalContent;
    private int generalContentHeight;
    private FormLocalTextButton generalSave;
    private FormLocalTextButton generalBack;
    private FormSlider sceneSize;
    private FormLocalCheckBox adjustZoomOnHighResolution;
    private FormLocalCheckBox limitCameraToLevelBounds;
    private FormLocalCheckBox pauseOnFocusLoss;
    private FormLocalCheckBox savePerformanceOnFocusLoss;
    private FormLocalCheckBox alwaysSkipTutorial;
    private FormLocalCheckBox showSettlerHeadArmor;
    private FormLocalTextButton clearCache;
    private FormLocalTextButton languageHelp;
    private FormLocalTextButton languageSave;
    private FormLocalTextButton languageBack;
    private Language prevLanguage;
    private FormLanguageList languageList;
    private FormLocalTextButton controlTypeBack;
    private FormLocalTextButton controlsSave;
    private FormLocalTextButton controlsBack;
    private FormContentBox interfaceContent;
    private int interfaceContentHeight;
    private FormLocalTextButton interfaceSave;
    private FormLocalTextButton interfaceBack;
    private FormHorizontalScroll<Float> interfaceSize;
    private FormDropdownSelectionButton<GameInterfaceStyle> interfaceStyle;
    private FormLocalCheckBox adjustInterfaceOnHighResolution;
    private FormLocalCheckBox pixelFont;
    private FormLocalCheckBox showDebugInfo;
    private FormLocalCheckBox showQuestMarkers;
    private FormLocalCheckBox showTeammateMarkers;
    private FormLocalCheckBox showPickupText;
    private FormLocalCheckBox showDamageText;
    private FormLocalCheckBox showDoTText;
    private FormLocalCheckBox showBossHealthBars;
    private FormLocalCheckBox showMobHealthBars;
    private FormLocalCheckBox showIngredientsAvailable;
    private FormLocalCheckBox showItemTooltipBackground;
    private FormLocalCheckBox showBasicTooltipBackground;
    private FormLocalCheckBox bigTooltipText;
    private FormLocalCheckBox showControlTips;
    private FormLocalCheckBox showLogicGateTooltips;
    private FormLocalCheckBox alwaysShowQuickbar;
    private FormLocalCheckBox drawCursorManually;
    private FormLocalSlider cursorRed;
    private FormLocalSlider cursorGreen;
    private FormLocalSlider cursorBlue;
    private FormHorizontalIntScroll cursorSize;
    private boolean changedCursorColor;
    private boolean changedCursorSize;
    private FormCursorPreview cursorPreview;
    private FormContentBox graphicsContent;
    private int graphicsContentHeight;
    private FormLocalTextButton graphicsSave;
    private FormLocalTextButton graphicsBack;
    private FormLocalCheckBox smoothLighting;
    private FormLocalCheckBox wavyGrass;
    private FormLocalCheckBox denseGrass;
    private FormLocalCheckBox windEffects;
    private FormLocalCheckBox cameraShake;
    private FormLocalCheckBox vSyncEnabled;
    private FormLocalCheckBox reduceUIFramerate;
    private FormContentBox displayModeContentBox;
    private FormDropdownSelectionButton<DisplayMode> displayMode;
    private FormLabel borderlessWarningLabel;
    private FormHorizontalIntScroll monitorScroll;
    private FormDropdownSelectionButton<SceneColorSetting> sceneColors;
    private FormLocalSlider brightness;
    private FormDropdownSelectionButton<Settings.LightSetting> lights;
    private FormDropdownSelectionButton<Settings.ParticleSetting> particles;
    private FormDropdownSelectionButton<Integer> maxFPS;
    private FormDropdownSelectionButton<Dimension> displayScroll;
    private boolean displayChanged;
    private int displayScrollY;
    private FormContentBox soundContent;
    private int soundContentHeight;
    private FormLocalTextButton soundSave;
    private FormLocalTextButton soundBack;
    private FormDropdownSelectionButton<String> outputDevice;
    private List<String> outputDeviceNames;
    private FormLocalSlider masterVolume;
    private FormLocalSlider effectsVolume;
    private FormLocalSlider weatherVolume;
    private FormLocalSlider UIVolume;
    private FormLocalSlider musicVolume;
    private FormLocalCheckBox muteOnFocusLoss;
    private FormControlListPopulator currentControlList;

    public SettingsForm(Client client, ContinueComponentManager continueComponentManager) {
        this.client = client;
        this.continueComponentManager = continueComponentManager;
        this.setupMenuForm();
        this.updateWorldForm();
        this.updateGeneralForm();
        this.updateLanguageForm();
        this.updateControlTypeForm();
        this.updateControlsForm();
        this.updateInterfaceForm();
        this.updateGraphicsForm();
        this.updateSoundForm();
        this.updateModsForm();
        this.confirmation = this.addComponent(new ConfirmationForm("confirmation"));
        this.onWindowResized(WindowManager.getWindow());
        this.makeCurrent(this.mainMenu);
    }

    private void setupMenuForm() {
        FormFlow flow = new FormFlow(10);
        this.mainMenu = this.addComponent(new Form("mainMenu", 400, 100));
        this.mainMenu.addComponent(new FormLocalLabel("settingsui", "front", new FontOptions(20), 0, this.mainMenu.getWidth() / 2, flow.next(30)));
        this.mainMenuWorld = this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "world", 4, flow.next(40), this.mainMenu.getWidth() - 8));
        this.mainMenuWorld.onClicked(e -> this.makeWorldCurrent(true));
        this.updateWorldButtonActive();
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "general", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeGeneralCurrent());
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "language", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeLanguageCurrent());
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "controls", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeControlTypeCurrent());
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "interface", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeInterfaceCurrent());
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "graphics", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeGraphicsCurrent());
        this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "sound", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.makeSoundCurrent());
        if (GlobalData.isDevMode() || !ModLoader.getAllMods().isEmpty()) {
            FormLocalTextButton modsButton = this.mainMenu.addComponent(new FormLocalTextButton("ui", "mods", 4, flow.next(40), this.mainMenu.getWidth() - 8));
            modsButton.onClicked(e -> this.makeModsCurrent());
            if (this.client != null) {
                modsButton.setActive(false);
                modsButton.setLocalTooltip("settingsui", "modsonlyinmain");
            }
        }
        this.mainMenu.addComponent(new FormLocalTextButton("ui", "backbutton", 4, flow.next(40), this.mainMenu.getWidth() - 8)).onClicked(e -> this.backPressed());
        this.mainMenu.setHeight(flow.next());
    }

    public void makeWorldCurrent(boolean setSaveInactive) {
        this.customSave = () -> {
            if (this.creativeMode.checked && !this.client.worldSettings.creativeMode) {
                this.confirmation.setupConfirmation(content -> content.addComponent(new FormLocalLabel("ui", "creativemodeconfirm", new FontOptions(16), 0, this.confirmation.getWidth() / 2, 10, this.confirmation.getWidth() - 20)), () -> {
                    Settings.saveClientSettings();
                    this.saveWorldSettings(setSaveInactive);
                    this.makeCurrent(this.world);
                    this.creativeMode.setActive(!this.client.worldSettings.creativeMode);
                }, () -> {
                    this.load();
                    this.makeCurrent(this.world);
                });
                this.makeCurrent(this.confirmation);
            } else {
                this.saveWorldSettings(setSaveInactive);
            }
        };
        this.creativeMode.setActive(!this.client.worldSettings.creativeMode);
        this.customLoad = null;
        this.makeCurrent(this.world);
    }

    private void saveWorldSettings(boolean setSaveInactive) {
        if (this.client != null) {
            this.client.worldSettings.difficulty = this.difficultyForm.selectedDifficulty;
            this.client.worldSettings.deathPenalty = this.deathPenalty.getSelected();
            this.client.worldSettings.raidFrequency = this.raidFrequency.getSelected();
            this.client.worldSettings.playerHunger = this.playerHunger.checked;
            this.client.worldSettings.survivalMode = this.survivalMode.checked;
            if (this.creativeMode.checked && !this.client.worldSettings.creativeMode) {
                this.client.worldSettings.enableCreativeMode(false);
            }
            this.client.worldSettings.allowOutsideCharacters = this.allowOutsideCharacters.checked;
            this.client.worldSettings.forcedPvP = this.forcedPvP.checked;
            this.client.network.sendPacket(new PacketSettings(this.client.worldSettings));
            if (setSaveInactive) {
                this.setSaveActive(false);
            }
        }
    }

    private void updateWorldForm() {
        if (this.world == null) {
            this.world = this.addComponent(new Form("world", 400, 40));
        } else {
            this.world.clearComponents();
        }
        this.world.addComponent(new FormLocalLabel("settingsui", "world", new FontOptions(20), 0, this.world.getWidth() / 2, 5));
        this.worldContent = this.world.addComponent(new FormContentBox(0, 30, this.world.getWidth(), this.world.getHeight() - 40));
        FormFlow worldFlow = new FormFlow(5);
        int selectorWidth = Math.min(Math.max(this.worldContent.getWidth() - 100, 300), this.worldContent.getWidth());
        int selectorX = this.worldContent.getWidth() / 2 - selectorWidth / 2;
        this.worldContent.addComponent(new FormLocalLabel("ui", "difficulty", new FontOptions(20), 0, this.worldContent.getWidth() / 2, worldFlow.next(24)));
        this.difficultyLabel = this.worldContent.addComponent(new FormLocalLabel(GameDifficulty.CLASSIC.displayName, new FontOptions(16), 0, this.worldContent.getWidth() / 2, worldFlow.next(20)));
        this.worldContent.addComponent(new FormLocalTextButton("ui", "changedifficulty", selectorX, worldFlow.next(32), selectorWidth, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> this.makeCurrent(this.difficultyForm));
        worldFlow.next(10);
        this.worldContent.addComponent(new FormLocalLabel("ui", "deathpenalty", new FontOptions(20), 0, this.worldContent.getWidth() / 2, worldFlow.next(24)));
        this.deathPenalty = this.worldContent.addComponent(new FormDropdownSelectionButton(selectorX, worldFlow.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, selectorWidth));
        for (GameDeathPenalty gameDeathPenalty : GameDeathPenalty.values()) {
            this.deathPenalty.options.add(gameDeathPenalty, gameDeathPenalty.displayName, () -> value.description);
        }
        this.deathPenalty.setSelected(GameDeathPenalty.NONE, null);
        this.deathPenalty.onSelected(e -> this.setSaveActive(true));
        worldFlow.next(10);
        this.worldContent.addComponent(new FormLocalLabel("ui", "raidfrequency", new FontOptions(20), 0, this.worldContent.getWidth() / 2, worldFlow.next(24)));
        this.raidFrequency = this.worldContent.addComponent(new FormDropdownSelectionButton(selectorX, worldFlow.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, selectorWidth));
        for (Enum enum_ : GameRaidFrequency.values()) {
            this.raidFrequency.options.add(enum_, ((GameRaidFrequency)enum_).displayName, () -> SettingsForm.lambda$updateWorldForm$16((GameRaidFrequency)enum_));
        }
        this.raidFrequency.onSelected(e -> this.setSaveActive(true));
        worldFlow.next(10);
        this.creativeMode = this.worldContent.addComponent(worldFlow.nextY(new FormLocalCheckBox("ui", "creativemode", 10, 0, this.worldContent.getWidth() - 20){

            @Override
            public GameTooltips getTooltip() {
                if (((SettingsForm)SettingsForm.this).client.worldSettings.creativeMode) {
                    return new StringTooltips(Localization.translate("ui", "creativemodeenabledtip"), 400);
                }
                return super.getTooltip();
            }
        }.useButtonTexture(), 4));
        this.creativeMode.onClicked(e -> this.setSaveActive(true));
        this.raidFrequency.controllerDownFocus = this.creativeMode;
        this.creativeMode.controllerUpFocus = this.raidFrequency;
        this.worldContent.addComponent(worldFlow.nextY(new FormLocalLabel("ui", "creativemodetip", new FontOptions(12), -1, 10, 0, this.worldContent.getWidth() - 30), 8));
        this.survivalMode = this.worldContent.addComponent(worldFlow.nextY(new FormLocalCheckBox("ui", "survivalmode", 10, 0, this.worldContent.getWidth() - 20).useButtonTexture(), 4));
        this.survivalMode.onClicked(e -> {
            this.setSaveActive(true);
            this.playerHunger.setActive(!((FormCheckBox)e.from).checked);
            if (!this.playerHunger.checked) {
                this.playerHunger.checked = ((FormCheckBox)e.from).checked;
            }
        });
        this.creativeMode.controllerDownFocus = this.survivalMode;
        this.survivalMode.controllerUpFocus = this.creativeMode;
        this.worldContent.addComponent(worldFlow.nextY(new FormLocalLabel("ui", "survivalmodetip", new FontOptions(12), -1, 10, 0, this.worldContent.getWidth() - 30), 8));
        this.playerHunger = this.worldContent.addComponent(worldFlow.nextY(new FormLocalCheckBox("ui", "playerhungerbox", 10, 0, this.worldContent.getWidth() - 20).useButtonTexture(), 8));
        this.playerHunger.onClicked(e -> this.setSaveActive(true));
        this.playerHunger.controllerUpFocus = this.survivalMode;
        this.survivalMode.controllerDownFocus = this.playerHunger;
        this.allowOutsideCharacters = this.worldContent.addComponent(worldFlow.nextY(new FormLocalCheckBox("ui", "allowoutsidecharactersbox", 10, 0, this.worldContent.getWidth() - 20).useButtonTexture(), 8));
        this.allowOutsideCharacters.onClicked(e -> this.setSaveActive(true));
        this.forcedPvP = this.worldContent.addComponent(worldFlow.nextY(new FormLocalCheckBox("ui", "forcedpvpbox", 10, 0, this.worldContent.getWidth() - 20).useButtonTexture(), 8));
        this.forcedPvP.onClicked(e -> this.setSaveActive(true));
        worldFlow.next(5);
        this.worldContentHeight = worldFlow.next();
        this.worldSave = this.world.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.world.getHeight() - 40, this.world.getWidth() / 2 - 6));
        this.worldSave.onClicked(e -> this.savePressed());
        this.worldBack = this.world.addComponent(new FormLocalTextButton("ui", "backbutton", this.world.getWidth() / 2 + 2, this.world.getHeight() - 40, this.world.getWidth() / 2 - 6));
        this.worldBack.onClicked(e -> this.subMenuBackPressed());
        this.updateWorldHeight();
        if (this.difficultyForm == null) {
            this.difficultyForm = this.addComponent(new DifficultySelectForm(null, ButtonOptions.backButton(() -> {
                this.difficultyLabel.setLocalization(this.difficultyForm.selectedDifficulty.displayName);
                this.makeWorldCurrent(false);
                if (this.client.worldSettings.difficulty != this.difficultyForm.selectedDifficulty) {
                    this.setSaveActive(true);
                }
            }), null));
        } else {
            this.difficultyForm.clearComponents();
        }
    }

    public void updateWorldHeight() {
        int maxHeight = Math.max(100, WindowManager.getWindow().getHudHeight() - 100);
        int height = Math.min(this.worldContentHeight + 40 + this.worldContent.getY(), maxHeight);
        this.world.setHeight(height);
        this.worldContent.setHeight(this.world.getHeight() - 40 - this.worldContent.getY());
        this.worldContent.setContentBox(new Rectangle(this.world.getWidth(), this.worldContentHeight));
        this.worldSave.setY(this.world.getHeight() - 40);
        this.worldBack.setY(this.world.getHeight() - 40);
    }

    public void makeGeneralCurrent() {
        this.customSave = null;
        this.customLoad = null;
        this.makeCurrent(this.general);
    }

    private void updateGeneralForm() {
        if (this.general == null) {
            this.general = this.addComponent(new Form("general", 400, 40));
        } else {
            this.general.clearComponents();
        }
        this.general.addComponent(new FormLocalLabel("settingsui", "general", new FontOptions(20), 0, this.general.getWidth() / 2, 5));
        this.generalContent = this.general.addComponent(new FormContentBox(0, 30, this.general.getWidth(), this.general.getHeight() - 40));
        FormFlow generalFlow = new FormFlow(5);
        this.generalContent.addComponent(new FormLocalLabel("settingsui", "zoomlevel", new FontOptions(16), 0, this.generalContent.getWidth() / 2, generalFlow.next(20)));
        this.sceneSize = this.generalContent.addComponent(generalFlow.nextY(new FormSlider("", 5, 0, (int)(Settings.sceneSize * 100.0f), (int)(GameWindow.minSceneSize * 100.0f), (int)(GameWindow.maxSceneSize * 100.0f), this.generalContent.getWidth() - 10){

            @Override
            public String getValueText() {
                return this.getValue() + "%";
            }
        }));
        this.sceneSize.onChanged(e -> {
            Settings.sceneSize = GameMath.toDecimals((float)((FormSlider)e.from).getValue() / 100.0f, 2);
            WindowManager.getWindow().updateSceneSize();
            this.setSaveActive(true);
        });
        generalFlow.next(15);
        this.adjustZoomOnHighResolution = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "adjustzoom", 10, 0, this.generalContent.getWidth() - 20){

            @Override
            public GameTooltips getTooltip() {
                return new StringTooltips(Localization.translate("settingsui", "adjustzoomtip"), 400);
            }
        }, 8));
        this.adjustZoomOnHighResolution.onClicked(e -> {
            Settings.adjustZoomOnHighResolution = ((FormCheckBox)e.from).checked;
            WindowManager.getWindow().updateSceneSize();
            this.setSaveActive(true);
        });
        this.limitCameraToLevelBounds = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "limitcamera", 10, 0, this.generalContent.getWidth() - 20), 8));
        this.limitCameraToLevelBounds.onClicked(e -> {
            Settings.limitCameraToLevelBounds = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        generalFlow.next(5);
        this.pauseOnFocusLoss = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "pausefocus", 10, 0, this.generalContent.getWidth() - 20), 8));
        this.pauseOnFocusLoss.onClicked(e -> {
            Settings.pauseOnFocusLoss = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.savePerformanceOnFocusLoss = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "perffocus", 10, 0, this.generalContent.getWidth() - 20), 8));
        this.savePerformanceOnFocusLoss.onClicked(e -> {
            Settings.savePerformanceOnFocusLoss = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.alwaysSkipTutorial = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "skiptutorial", 10, 0, this.generalContent.getWidth() - 20), 8));
        this.alwaysSkipTutorial.onClicked(e -> {
            Settings.alwaysSkipTutorial = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showSettlerHeadArmor = this.generalContent.addComponent(generalFlow.nextY(new FormLocalCheckBox("settingsui", "showsettlerheadarmor", 10, 0, this.generalContent.getWidth() - 20), 8));
        this.showSettlerHeadArmor.onClicked(e -> {
            Settings.showSettlerHeadArmor = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        generalFlow.next(40);
        FormLocalTextButton openSaves = this.generalContent.addComponent(new FormLocalTextButton("settingsui", "opensaves", 10, generalFlow.next(24), this.generalContent.getWidth() - 20, FormInputSize.SIZE_20, ButtonColor.BASE));
        openSaves.onClicked(e -> {
            Thread thread = new Thread(() -> {
                try {
                    File file = new File(World.getSavesPath());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (file.isDirectory() && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            thread.start();
        });
        openSaves.setCooldown(1000);
        this.clearCache = this.generalContent.addComponent(new FormLocalTextButton("settingsui", "resetcache", 10, generalFlow.next(24), this.generalContent.getWidth() - 20, FormInputSize.SIZE_20, ButtonColor.BASE));
        this.clearCache.onClicked(e -> {
            this.confirmation.setupConfirmation(new LocalMessage("settingsui", "cacheconfirm"), () -> {
                GameCache.clearCacheFolder("client");
                this.clearCache.setActive(false);
                this.makeCurrent(this.general);
            }, () -> this.makeCurrent(this.general));
            this.makeCurrent(this.confirmation);
        });
        if (this.client != null) {
            this.clearCache.setActive(false);
            this.clearCache.setLocalTooltip("settingsui", "onlyinmenu");
        }
        generalFlow.next(5);
        this.generalContentHeight = generalFlow.next();
        this.generalSave = this.general.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.general.getHeight() - 40, this.general.getWidth() / 2 - 6));
        this.generalSave.onClicked(e -> this.savePressed());
        this.generalBack = this.general.addComponent(new FormLocalTextButton("ui", "backbutton", this.general.getWidth() / 2 + 2, this.general.getHeight() - 40, this.general.getWidth() / 2 - 6));
        this.generalBack.onClicked(e -> this.subMenuBackPressed());
        this.updateGeneralHeight();
    }

    public void updateGeneralHeight() {
        int maxHeight = Math.max(100, WindowManager.getWindow().getHudHeight() - 100);
        int height = Math.min(this.generalContentHeight + 40 + this.generalContent.getY(), maxHeight);
        this.general.setHeight(height);
        this.generalContent.setHeight(this.general.getHeight() - 40 - this.generalContent.getY());
        this.generalContent.setContentBox(new Rectangle(this.general.getWidth(), this.generalContentHeight));
        this.generalSave.setY(this.general.getHeight() - 40);
        this.generalBack.setY(this.general.getHeight() - 40);
    }

    public void makeLanguageCurrent() {
        this.customSave = null;
        this.customLoad = () -> {
            if (this.prevLanguage != null) {
                this.prevLanguage.setCurrent();
                Settings.language = this.prevLanguage.stringID;
            }
        };
        this.makeCurrent(this.language);
        this.languageList.reset();
    }

    private void updateLanguageForm() {
        if (this.language == null) {
            this.language = this.addComponent(new Form("language", 500, 40));
        } else {
            this.language.clearComponents();
        }
        this.language.addComponent(new FormLocalLabel("settingsui", "language", new FontOptions(20), 0, this.language.getWidth() / 2, 5));
        this.languageList = this.language.addComponent(new FormLanguageList(0, 35, this.language.getWidth(), 40)).onLanguageSelect(e -> {
            e.language.setCurrent();
            Settings.language = e.language.stringID;
            this.setSaveActive(true);
        });
        this.languageHelp = this.language.addComponent(new FormLocalTextButton("settingsui", "helptranslate", 4, this.language.getHeight() - 60, this.language.getWidth() - 8, FormInputSize.SIZE_20, ButtonColor.BASE));
        this.languageHelp.onClicked(e -> GameUtils.openURL("https://steamcommunity.com/app/1169040/discussions/0/4345408777056877000/"));
        this.languageSave = this.language.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.language.getHeight() - 40, this.language.getWidth() / 2 - 6));
        this.languageSave.onClicked(e -> this.savePressed());
        this.languageBack = this.language.addComponent(new FormLocalTextButton("ui", "backbutton", this.language.getWidth() / 2 + 2, this.language.getHeight() - 40, this.language.getWidth() / 2 - 6));
        this.languageBack.onClicked(e -> this.subMenuBackPressed());
        this.updateLanguageHeight();
    }

    public void updateLanguageHeight() {
        int maxHeight = Math.max(140, WindowManager.getWindow().getHudHeight() - 140);
        int desiredContentHeight = Math.min(maxHeight, 400);
        int height = Math.min(desiredContentHeight + 60 + 30, maxHeight);
        this.language.setHeight(height);
        this.languageList.setHeight(this.language.getHeight() - 60 - this.languageList.getY());
        this.languageHelp.setY(this.language.getHeight() - 60);
        this.languageSave.setY(this.language.getHeight() - 40);
        this.languageBack.setY(this.language.getHeight() - 40);
    }

    private void updateControlTypeForm() {
        ArrayList<InputSource> inputSources = InputManager.getInputSources();
        ArrayList controlSettings = new ArrayList();
        inputSources.forEach(x -> controlSettings.addAll(x.getControlSettings()));
        if (this.controlType == null) {
            this.controlType = this.addComponent(new Form("controlType", 400, 600));
        } else {
            this.controlType.clearComponents();
        }
        FormFlow flow = new FormFlow(5);
        this.controlType.addComponent(new FormLocalLabel("settingsui", "controls", new FontOptions(20), 0, this.controlType.getWidth() / 2, flow.next(30)));
        for (InputSource.ControlSettings settings : controlSettings) {
            FormLocalTextButton button = this.controlType.addComponent(new FormLocalTextButton(settings.gameMessage, 4, flow.next(40), this.controlType.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE));
            button.onClicked(e -> {
                if (settings.runWhenSettingsClicked != null) {
                    settings.runWhenSettingsClicked.run();
                }
                if (settings.controlList != null) {
                    this.setControlsFormList(settings.controlList);
                    this.makeControlsCurrent();
                }
            });
        }
        this.controlTypeBack = this.controlType.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.controlType.getHeight() - 40, this.controlType.getWidth() - 8));
        this.controlTypeBack.onClicked(e -> this.subMenuBackPressed());
        this.updateControlTypesHeight();
    }

    public void updateControlTypesHeight() {
        ArrayList<InputSource> inputSources = InputManager.getInputSources();
        ArrayList controlSettings = new ArrayList();
        inputSources.forEach(x -> controlSettings.addAll(x.getControlSettings()));
        int maxHeight = Math.max(120, WindowManager.getWindow().getHudHeight() - 120);
        int desiredContentHeight = Math.min(maxHeight, 10 + FormInputSize.SIZE_32.height * controlSettings.size() + (40 - FormInputSize.SIZE_32.height) * (controlSettings.size() - 1));
        int height = Math.min(desiredContentHeight + 40 + 30, maxHeight);
        this.controlType.setHeight(height);
        this.controlTypeBack.setY(this.controlType.getHeight() - 40);
    }

    public void makeControlTypeCurrent() {
        ArrayList<InputSource> inputSources = InputManager.getInputSources();
        ArrayList controlSettings = new ArrayList();
        inputSources.forEach(x -> controlSettings.addAll(x.getControlSettings()));
        if (controlSettings.size() == 1) {
            InputSource.ControlSettings settings = (InputSource.ControlSettings)controlSettings.get(0);
            if (settings.runWhenSettingsClicked != null) {
                settings.runWhenSettingsClicked.run();
            }
            if (settings.controlList != null) {
                this.setControlsFormList(settings.controlList);
                this.makeControlsCurrent();
            }
        } else {
            this.updateControlTypeForm();
            this.makeCurrent(this.controlType);
        }
    }

    public void makeControlsCurrent() {
        this.customSave = null;
        this.customLoad = null;
        this.makeCurrent(this.controls);
    }

    private void setControlsFormList(FormControlListPopulator formControlListPopulator) {
        this.updateControlsForm();
        this.currentControlList = formControlListPopulator;
        formControlListPopulator.populateForm(this.controls, 4, 30, this.controls.getWidth() - 8, this.controls.getHeight() - 40 - 30, () -> this.setControlsFormList(formControlListPopulator));
        formControlListPopulator.runOnBindChanged(() -> this.setSaveActive(true));
        this.updateControlsHeight();
    }

    private void updateControlsForm() {
        if (this.controls == null) {
            this.controls = this.addComponent(new Form("controls", 400, 40));
        } else {
            this.controls.clearComponents();
        }
        this.controls.addComponent(new FormLocalLabel("settingsui", "controls", new FontOptions(20), 0, this.controls.getWidth() / 2, 5));
        this.controlsSave = this.controls.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.controls.getHeight() - 40, this.controls.getWidth() / 2 - 6));
        this.controlsSave.onClicked(e -> this.savePressed());
        this.controlsSave.setActive(this.saveActive);
        this.controlsBack = this.controls.addComponent(new FormLocalTextButton("ui", "backbutton", this.controls.getWidth() / 2 + 2, this.controls.getHeight() - 40, this.controls.getWidth() / 2 - 6));
        this.controlsBack.onClicked(e -> this.subMenuBackPressed(this.mainMenu));
        this.updateControlsHeight();
    }

    public void updateControlsHeight() {
        int maxHeight = Math.max(120, WindowManager.getWindow().getHudHeight() - 120);
        int desiredContentHeight = Math.min(maxHeight, 400);
        int height = Math.min(desiredContentHeight + 40 + 30, maxHeight);
        this.controls.setHeight(height);
        this.controlsSave.setY(this.controls.getHeight() - 40);
        this.controlsBack.setY(this.controls.getHeight() - 40);
    }

    public void makeInterfaceCurrent() {
        this.customSave = null;
        this.customLoad = () -> {
            if (this.reloadedInterface) {
                GlobalData.getCurrentState().reloadInterfaceFromSettings(false);
            }
        };
        this.updateMonitorScroll();
        this.updateDisplayScrollComponent();
        this.makeCurrent(this.interf);
    }

    private void updateInterfaceForm() {
        if (this.interf == null) {
            this.interf = this.addComponent(new Form("interface", 400, 40));
        } else {
            this.interf.clearComponents();
        }
        this.interf.addComponent(new FormLocalLabel("settingsui", "interface", new FontOptions(20), 0, this.interf.getWidth() / 2, 5));
        this.interfaceContent = this.interf.addComponent(new FormContentBox(0, 30, this.interf.getWidth(), this.interf.getHeight() - 40));
        FormFlow interfaceFlow = new FormFlow(5);
        int selectorWidth = Math.min(Math.max(this.interfaceContent.getWidth() - 100, 200), this.interfaceContent.getWidth());
        int selectorX = this.interfaceContent.getWidth() / 2 - selectorWidth / 2;
        this.interfaceContent.addComponent(new FormLocalLabel("settingsui", "interfacesize", new FontOptions(16), 0, this.interfaceContent.getWidth() / 2, interfaceFlow.next(20)));
        FormHorizontalScroll.ScrollElement[] interfaceSizes = (FormHorizontalScroll.ScrollElement[])IntStream.range(0, GameWindow.interfaceSizes.length).mapToObj(i -> new FormHorizontalScroll.ScrollElement<Float>(Float.valueOf(GameWindow.interfaceSizes[i]), new StaticMessage((int)(GameWindow.interfaceSizes[i] * 100.0f) + "%"))).toArray(FormHorizontalScroll.ScrollElement[]::new);
        this.interfaceSize = this.interfaceContent.addComponent(new FormHorizontalScroll(this.interfaceContent.getWidth() / 2 - 75, interfaceFlow.next(30), 150, FormHorizontalScroll.DrawOption.string, 0, interfaceSizes));
        this.interfaceSize.onChanged(e -> {
            Settings.interfaceSize = ((Float)this.interfaceSize.getCurrent().value).floatValue();
            WindowManager.getWindow().updateHudSize();
            this.setSaveActive(true);
        });
        this.adjustInterfaceOnHighResolution = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "adjustinterfacesize", 10, 0, this.interfaceContent.getWidth() - 20){

            @Override
            public GameTooltips getTooltip() {
                return new StringTooltips(Localization.translate("settingsui", "adjustinterfacesizetip"), 400);
            }
        }, 8));
        this.adjustInterfaceOnHighResolution.onClicked(e -> {
            Settings.adjustInterfaceOnHighResolution = ((FormCheckBox)e.from).checked;
            WindowManager.getWindow().updateHudSize();
            this.setSaveActive(true);
        });
        if (GameInterfaceStyle.styles.size() > 1) {
            this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalLabel("settingsui", "interfacestyle", new FontOptions(12), -1, 10, 0, this.interfaceContent.getWidth() - 20), 8));
            this.interfaceStyle = this.interfaceContent.addComponent(new FormDropdownSelectionButton(selectorX, interfaceFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
            for (GameInterfaceStyle style : GameInterfaceStyle.styles) {
                this.interfaceStyle.options.add(style, style.displayName);
            }
            this.interfaceStyle.setSelected(this.getInterfaceStyle(), this.getInterfaceStyle().displayName);
            this.interfaceStyle.onSelected(e -> {
                Settings.UI = (GameInterfaceStyle)e.value;
                GlobalData.getCurrentState().reloadInterfaceFromSettings(true);
                this.setSaveActive(true);
            });
            this.interfaceStyle.controllerUpFocus = this.adjustInterfaceOnHighResolution;
            this.adjustInterfaceOnHighResolution.controllerDownFocus = this.interfaceStyle;
        }
        this.interfaceSize.controllerDownFocus = this.adjustInterfaceOnHighResolution;
        this.pixelFont = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "pixelfont", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.pixelFont.onClicked(e -> {
            Settings.pixelFont = ((FormCheckBox)e.from).checked;
            GlobalData.getCurrentState().reloadInterfaceFromSettings(true);
            this.setSaveActive(true);
        });
        if (this.interfaceStyle != null) {
            this.interfaceStyle.controllerDownFocus = this.pixelFont;
            this.pixelFont.controllerUpFocus = this.interfaceStyle;
        }
        this.showItemTooltipBackground = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showitemtooltipsbackground", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showItemTooltipBackground.onClicked(e -> {
            Settings.showItemTooltipBackground = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showItemTooltipBackground.controllerUpFocus = this.pixelFont;
        this.pixelFont.controllerDownFocus = this.showBasicTooltipBackground;
        this.showBasicTooltipBackground = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showbasictooltipsbackground", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showBasicTooltipBackground.onClicked(e -> {
            Settings.showBasicTooltipBackground = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.bigTooltipText = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "bigtooltiptext", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.bigTooltipText.onClicked(e -> {
            Settings.tooltipTextSize = ((FormCheckBox)e.from).checked ? 20 : 16;
            this.setSaveActive(true);
        });
        this.showIngredientsAvailable = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showingredientsavailable", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showIngredientsAvailable.onClicked(e -> {
            Settings.showIngredientsAvailable = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showQuestMarkers = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showquest", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showQuestMarkers.onClicked(e -> {
            Settings.showQuestMarkers = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showTeammateMarkers = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showteammates", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showTeammateMarkers.onClicked(e -> {
            Settings.showTeammateMarkers = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showMobHealthBars = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showmobhealthbars", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showMobHealthBars.onClicked(e -> {
            Settings.showMobHealthBars = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showBossHealthBars = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showbosshealthbars", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showBossHealthBars.onClicked(e -> {
            Settings.showBossHealthBars = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showControlTips = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showcontroltips", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showControlTips.onClicked(e -> {
            Settings.showControlTips = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showPickupText = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showpickup", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showPickupText.onClicked(e -> {
            Settings.showPickupText = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showDamageText = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showdamage", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showDamageText.onClicked(e -> {
            Settings.showDamageText = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showDoTText = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showdot", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showDoTText.onClicked(e -> {
            Settings.showDoTText = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showLogicGateTooltips = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showlogicgate", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showLogicGateTooltips.onClicked(e -> {
            Settings.showLogicGateTooltips = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.showDebugInfo = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "showdebug", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.showDebugInfo.onClicked(e -> {
            Settings.showDebugInfo = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.alwaysShowQuickbar = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "alwaysshowquickbar", 10, 0, this.interfaceContent.getWidth() - 20), 8));
        this.alwaysShowQuickbar.onClicked(e -> {
            Settings.alwaysShowQuickbar = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        if (PlatformManager.getPlatform().isSteamDeck()) {
            this.drawCursorManually = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalCheckBox("settingsui", "manuallydrawcursor", 10, 0, this.interfaceContent.getWidth() - 20), 6));
            this.drawCursorManually.onClicked(e -> {
                Settings.drawCursorManually = ((FormCheckBox)e.from).checked;
                this.setSaveActive(true);
            });
            this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalLabel("settingsui", "manuallydrawcursorinfo", new FontOptions(12), -1, 14, 0, this.interfaceContent.getWidth() - 24), 8));
        }
        FormFlow cursorFlow = interfaceFlow.split(5);
        FontOptions cursorSliderFontOptions = new FontOptions(12);
        this.interfaceContent.addComponent(new FormLocalLabel("settingsui", "cursorcolor", new FontOptions(16), -1, 8, interfaceFlow.next(20)));
        this.cursorRed = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalSlider("ui", "colorred", 8, 0, Settings.cursorColor.getRed(), 50, 255, this.interfaceContent.getWidth() - 100, cursorSliderFontOptions)));
        this.cursorRed.onChanged(e -> {
            this.changedCursorColor = true;
            Settings.cursorColor = new Color(((FormSlider)e.from).getValue(), Settings.cursorColor.getGreen(), Settings.cursorColor.getBlue());
            this.cursorPreview.setColor(Settings.cursorColor);
            this.setSaveActive(true);
        });
        this.cursorGreen = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalSlider("ui", "colorgreen", 8, 0, Settings.cursorColor.getGreen(), 50, 255, this.interfaceContent.getWidth() - 100, cursorSliderFontOptions)));
        this.cursorGreen.onChanged(e -> {
            this.changedCursorColor = true;
            Settings.cursorColor = new Color(Settings.cursorColor.getRed(), ((FormSlider)e.from).getValue(), Settings.cursorColor.getBlue());
            this.cursorPreview.setColor(Settings.cursorColor);
            this.setSaveActive(true);
        });
        this.cursorBlue = this.interfaceContent.addComponent(interfaceFlow.nextY(new FormLocalSlider("ui", "colorblue", 8, 0, Settings.cursorColor.getBlue(), 50, 255, this.interfaceContent.getWidth() - 100, cursorSliderFontOptions)));
        this.cursorBlue.onChanged(e -> {
            this.changedCursorColor = true;
            Settings.cursorColor = new Color(Settings.cursorColor.getRed(), Settings.cursorColor.getGreen(), ((FormSlider)e.from).getValue());
            this.cursorPreview.setColor(Settings.cursorColor);
            this.setSaveActive(true);
        });
        interfaceFlow.next(5);
        int selectColorY = interfaceFlow.next(20);
        this.interfaceContent.addComponent(new FormLocalTextButton("ui", "selectcolor", 8, selectColorY - 2, this.interfaceContent.getWidth() - 165, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> {
            final Color startColor = new Color(Settings.cursorColor.getRGB());
            ((FormButton)e.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(e.from, startColor){

                @Override
                public void onApplied(Color color) {
                    if (color != null) {
                        color = new Color(GameMath.limit(color.getRed(), 50, 255), GameMath.limit(color.getGreen(), 50, 255), GameMath.limit(color.getBlue(), 50, 255));
                        SettingsForm.this.changedCursorColor = true;
                        Settings.cursorColor = color;
                        SettingsForm.this.cursorRed.setValue(Settings.cursorColor.getRed());
                        SettingsForm.this.cursorGreen.setValue(Settings.cursorColor.getGreen());
                        SettingsForm.this.cursorBlue.setValue(Settings.cursorColor.getBlue());
                        SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
                        SettingsForm.this.setSaveActive(true);
                    } else {
                        Settings.cursorColor = startColor;
                        SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
                    }
                }

                @Override
                public void onSelected(Color color) {
                    Settings.cursorColor = color = new Color(GameMath.limit(color.getRed(), 50, 255), GameMath.limit(color.getGreen(), 50, 255), GameMath.limit(color.getBlue(), 50, 255));
                    SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
                }
            });
        });
        this.cursorSize = this.interfaceContent.addComponent(new FormHorizontalIntScroll(this.interfaceContent.getWidth() - 150, selectColorY, 140, FormHorizontalScroll.DrawOption.string, new LocalMessage("settingsui", "cursorsize"), Settings.cursorSize, -Renderer.cursorSizeOffset, Renderer.cursorSizes.length - Renderer.cursorSizeOffset - 1));
        this.cursorSize.onChanged(e -> {
            this.changedCursorSize = true;
            Settings.cursorSize = (Integer)((FormHorizontalScroll)e.from).getCurrent().value;
            this.cursorPreview.setSize(Settings.cursorSize);
            this.setSaveActive(true);
        });
        cursorFlow.next(50);
        this.cursorPreview = this.interfaceContent.addComponent(new FormCursorPreview(this.interfaceContent.getWidth() - 65, cursorFlow.next(40), Settings.cursorColor, Settings.cursorSize));
        interfaceFlow.next(5);
        this.interfaceContentHeight = interfaceFlow.next();
        this.interfaceSave = this.interf.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.interf.getHeight() - 40, this.interf.getWidth() / 2 - 6));
        this.interfaceSave.onClicked(e -> this.savePressed());
        this.interfaceBack = this.interf.addComponent(new FormLocalTextButton("ui", "backbutton", this.interf.getWidth() / 2 + 2, this.interf.getHeight() - 40, this.interf.getWidth() / 2 - 6));
        this.interfaceBack.onClicked(e -> this.subMenuBackPressed());
        this.updateInterfaceHeight();
    }

    public void updateInterfaceHeight() {
        int maxHeight = Math.max(100, WindowManager.getWindow().getHudHeight() - 100);
        int height = Math.min(this.interfaceContentHeight + 40 + this.interfaceContent.getY(), maxHeight);
        this.interf.setHeight(height);
        this.interfaceContent.setHeight(this.interf.getHeight() - 40 - this.interfaceContent.getY());
        this.interfaceContent.setContentBox(new Rectangle(this.interf.getWidth(), this.interfaceContentHeight));
        this.interfaceSave.setY(this.interf.getHeight() - 40);
        this.interfaceBack.setY(this.interf.getHeight() - 40);
    }

    public void makeGraphicsCurrent() {
        this.customSave = null;
        this.customLoad = null;
        this.updateMonitorScroll();
        this.updateDisplayScrollComponent();
        this.makeCurrent(this.graphics);
    }

    private void updateGraphicsForm() {
        if (this.graphics == null) {
            this.graphics = this.addComponent(new Form("graphics", 400, 40));
        } else {
            this.graphics.clearComponents();
            this.displayScroll = null;
        }
        this.graphics.addComponent(new FormLocalLabel("settingsui", "graphics", new FontOptions(20), 0, this.graphics.getWidth() / 2, 5));
        this.graphicsContent = this.graphics.addComponent(new FormContentBox(0, 30, this.graphics.getWidth(), this.graphics.getHeight() - 40));
        FormFlow graphicsFlow = new FormFlow(5);
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "displaymode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        int selectorWidth = Math.min(Math.max(this.graphicsContent.getWidth() - 100, 200), this.graphicsContent.getWidth());
        int selectorX = this.graphicsContent.getWidth() / 2 - selectorWidth / 2;
        this.displayModeContentBox = this.graphicsContent.addComponent(new FormContentBox(0, graphicsFlow.next(24), this.graphicsContent.getWidth(), FormInputSize.SIZE_20.height));
        this.displayMode = this.displayModeContentBox.addComponent(new FormDropdownSelectionButton(selectorX, 0, FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        this.displayMode.options.add(DisplayMode.Windowed, DisplayMode.Windowed.displayName);
        this.displayMode.options.add(DisplayMode.Borderless, DisplayMode.Borderless.displayName);
        this.displayMode.options.add(DisplayMode.Fullscreen, DisplayMode.Fullscreen.displayName);
        this.displayMode.setSelected(Settings.displayMode, Settings.displayMode.displayName);
        this.displayMode.onSelected(e -> {
            this.displayScroll.setActive(((DisplayMode)((Object)((Object)((Object)e.value)))).canSelectSize);
            this.displayChanged = true;
            this.updateDisplayScrollComponent();
            this.setSaveActive(true);
            this.updateBorderlessWarning();
        });
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "monitor", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.monitorScroll = this.graphicsContent.addComponent(new FormHorizontalIntScroll(selectorX, graphicsFlow.next(20), selectorWidth, FormHorizontalScroll.DrawOption.value, "", Settings.monitor, 0, 0));
        this.monitorScroll.onChanged(e -> {
            this.displayChanged = true;
            this.updateDisplayScrollComponent();
            this.setSaveActive(true);
            this.updateBorderlessWarning();
        });
        this.updateMonitorScroll();
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "displayres", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.displayScrollY = graphicsFlow.next(24);
        this.updateDisplayScrollComponent();
        this.vSyncEnabled = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "vsync", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.vSyncEnabled.onClicked(e -> {
            Settings.vSyncEnabled = ((FormCheckBox)e.from).checked;
            WindowManager.getWindow().setVSync(Settings.vSyncEnabled);
            this.setSaveActive(true);
        });
        this.displayScroll.controllerDownFocus = this.vSyncEnabled;
        this.vSyncEnabled.controllerUpFocus = this.displayScroll;
        if (Platform.get() == Platform.MACOSX) {
            this.graphicsContent.clearComponents();
            graphicsFlow = new FormFlow(5);
        }
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "maxfps", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.maxFPS = this.graphicsContent.addComponent(new FormDropdownSelectionButton(selectorX, graphicsFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        this.maxFPS.options.add(30, new StaticMessage("30"));
        this.maxFPS.options.add(60, new StaticMessage("60"));
        this.maxFPS.options.add(120, new StaticMessage("120"));
        this.maxFPS.options.add(144, new StaticMessage("144"));
        this.maxFPS.options.add(0, new LocalMessage("settingsui", "fpsnocap"));
        this.maxFPS.setSelected(Settings.maxFPS, Settings.maxFPS == 0 ? new LocalMessage("settingsui", "fpsnocap") : new StaticMessage(String.valueOf(Settings.maxFPS)));
        this.maxFPS.onSelected(e -> {
            Settings.maxFPS = (Integer)e.value;
            this.setSaveActive(true);
        });
        this.maxFPS.controllerUpFocus = this.vSyncEnabled;
        this.vSyncEnabled.controllerDownFocus = this.maxFPS;
        this.reduceUIFramerate = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "reduceuiframerate", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.reduceUIFramerate.onClicked(e -> {
            Settings.reduceUIFramerate = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.maxFPS.controllerDownFocus = this.reduceUIFramerate;
        this.reduceUIFramerate.controllerUpFocus = this.maxFPS;
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "colormode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.sceneColors = this.graphicsContent.addComponent(new FormDropdownSelectionButton(selectorX, graphicsFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        for (SceneColorSetting sceneColorSetting : SceneColorSetting.values()) {
            this.sceneColors.options.add(sceneColorSetting, sceneColorSetting.displayName);
        }
        this.sceneColors.setSelected(Settings.sceneColors, Settings.sceneColors.displayName);
        this.sceneColors.onSelected(e -> {
            Settings.sceneColors = (SceneColorSetting)((Object)((Object)((Object)e.value)));
            this.setSaveActive(true);
        });
        this.reduceUIFramerate.controllerDownFocus = this.sceneColors;
        this.sceneColors.controllerUpFocus = this.reduceUIFramerate;
        this.brightness = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalSlider("settingsui", "brightness", 10, 0, 100, 50, 200, this.graphicsContent.getWidth() - 20){

            @Override
            public String getValueText() {
                return this.getValue() + "%";
            }
        }, 8));
        this.brightness.onChanged(e -> {
            Level level;
            Settings.brightness = (float)((FormSlider)e.from).getValue() / 100.0f;
            if (this.client != null && (level = this.client.getLevel()) != null) {
                level.lightManager.updateAmbientLight();
            }
            this.setSaveActive(true);
        });
        this.sceneColors.controllerDownFocus = this.smoothLighting;
        this.brightness.controllerUpFocus = this.sceneColors;
        this.smoothLighting = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "smoothlight", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.smoothLighting.onClicked(e -> {
            Settings.smoothLighting = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.brightness.controllerDownFocus = this.smoothLighting;
        this.smoothLighting.controllerUpFocus = this.brightness;
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "lightmode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.lights = this.graphicsContent.addComponent(new FormDropdownSelectionButton(selectorX, graphicsFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        for (Enum enum_ : Settings.LightSetting.values()) {
            this.lights.options.add(enum_, ((Settings.LightSetting)enum_).displayName);
        }
        this.lights.setSelected(Settings.lights, Settings.lights.displayName);
        this.lights.onSelected(e -> {
            Level level;
            Settings.lights = (Settings.LightSetting)((Object)((Object)((Object)e.value)));
            if (this.client != null && (level = this.client.getLevel()) != null) {
                level.lightManager.ensureSetting(Settings.lights);
                level.lightManager.updateAmbientLight();
            }
            this.setSaveActive(true);
        });
        this.smoothLighting.controllerDownFocus = this.lights;
        this.lights.controllerUpFocus = this.smoothLighting;
        this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalLabel("settingsui", "particles", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.particles = this.graphicsContent.addComponent(new FormDropdownSelectionButton(selectorX, graphicsFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        this.particles.options.add(Settings.ParticleSetting.Minimal, Settings.ParticleSetting.Minimal.displayName);
        this.particles.options.add(Settings.ParticleSetting.Decreased, Settings.ParticleSetting.Decreased.displayName);
        this.particles.options.add(Settings.ParticleSetting.Maximum, Settings.ParticleSetting.Maximum.displayName);
        this.particles.setSelected(Settings.particles, Settings.particles.displayName);
        this.particles.onSelected(e -> {
            Settings.particles = (Settings.ParticleSetting)((Object)((Object)((Object)e.value)));
            this.setSaveActive(true);
        });
        this.wavyGrass = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "wavygrass", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.wavyGrass.onClicked(e -> {
            Settings.wavyGrass = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.particles.controllerDownFocus = this.wavyGrass;
        this.wavyGrass.controllerUpFocus = this.particles;
        this.denseGrass = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "densegrass", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.denseGrass.onClicked(e -> {
            Settings.denseGrass = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.windEffects = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "windeffects", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.windEffects.onClicked(e -> {
            Settings.windEffects = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.cameraShake = this.graphicsContent.addComponent(graphicsFlow.nextY(new FormLocalCheckBox("settingsui", "camerashake", 10, 0, this.graphicsContent.getWidth() - 20), 8));
        this.cameraShake.onClicked(e -> {
            Settings.cameraShake = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        graphicsFlow.next(5);
        this.graphicsContentHeight = graphicsFlow.next();
        this.graphicsSave = this.graphics.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.graphics.getHeight() - 40, this.graphics.getWidth() / 2 - 6));
        this.graphicsSave.onClicked(e -> this.savePressed());
        this.graphicsBack = this.graphics.addComponent(new FormLocalTextButton("ui", "backbutton", this.graphics.getWidth() / 2 + 2, this.graphics.getHeight() - 40, this.graphics.getWidth() / 2 - 6));
        this.graphicsBack.onClicked(e -> this.subMenuBackPressed());
        this.updateGraphicsHeight();
        this.updateBorderlessWarning();
    }

    private void updateBorderlessWarning() {
        GameMessage borderlessWarning = null;
        if (this.displayMode.getSelected() == DisplayMode.Borderless) {
            borderlessWarning = necesse.engine.platforms.Platform.getWindowManager().getBorderlessDisplayMessage();
        }
        if (borderlessWarning != null) {
            if (this.borderlessWarningLabel == null) {
                int selectorWidth = Math.min(Math.max(this.graphicsContent.getWidth() - 100, 200), this.graphicsContent.getWidth());
                int selectorX = this.graphicsContent.getWidth() / 2 - selectorWidth / 2;
                this.borderlessWarningLabel = this.displayModeContentBox.addComponent(new FormLocalLabel(borderlessWarning, new FontOptions(12).color(this.getInterfaceStyle().warningTextColor), 0, selectorX + selectorWidth / 2, this.displayModeContentBox.getHeight() + 5, selectorWidth));
                int labelHeight = this.borderlessWarningLabel.getHeight() + 15;
                this.displayModeContentBox.setHeight(this.displayModeContentBox.getHeight() + labelHeight);
                this.graphicsContentHeight += labelHeight;
                for (FormComponent formComponent : this.graphicsContent.getComponentList()) {
                    FormPositionContainer formPos;
                    if (!(formComponent instanceof FormPositionContainer) || (formPos = (FormPositionContainer)((Object)formComponent)).getY() <= this.displayModeContentBox.getY()) continue;
                    ((FormPositionContainer)((Object)formComponent)).addPosition(0, labelHeight);
                }
                this.updateGraphicsHeight();
            }
        } else if (this.borderlessWarningLabel != null) {
            int labelHeight = this.borderlessWarningLabel.getHeight() + 15;
            this.displayModeContentBox.setHeight(this.displayModeContentBox.getHeight() - labelHeight);
            this.graphicsContentHeight -= labelHeight;
            for (FormComponent formComponent : this.graphicsContent.getComponentList()) {
                FormPositionContainer formPos;
                if (!(formComponent instanceof FormPositionContainer) || (formPos = (FormPositionContainer)((Object)formComponent)).getY() <= this.displayModeContentBox.getY()) continue;
                ((FormPositionContainer)((Object)formComponent)).addPosition(0, -labelHeight);
            }
            this.displayModeContentBox.removeComponent(this.borderlessWarningLabel);
            this.borderlessWarningLabel = null;
            this.updateGraphicsHeight();
        }
    }

    public void updateGraphicsHeight() {
        int maxHeight = Math.max(100, WindowManager.getWindow().getHudHeight() - 100);
        int height = Math.min(this.graphicsContentHeight + 40 + this.graphicsContent.getY(), maxHeight);
        this.graphics.setHeight(height);
        this.graphicsContent.setHeight(this.graphics.getHeight() - 40 - this.graphicsContent.getY());
        this.graphicsContent.setContentBox(new Rectangle(this.graphics.getWidth(), this.graphicsContentHeight));
        this.graphicsSave.setY(this.graphics.getHeight() - 40);
        this.graphicsBack.setY(this.graphics.getHeight() - 40);
    }

    public void makeSoundCurrent() {
        this.customSave = null;
        this.customLoad = null;
        this.updateOutputDevices();
        this.makeCurrent(this.sound);
    }

    private void updateSoundForm() {
        if (this.sound == null) {
            this.sound = this.addComponent(new Form("sound", 400, 40));
        } else {
            this.sound.clearComponents();
        }
        this.sound.addComponent(new FormLocalLabel("settingsui", "sound", new FontOptions(20), 0, this.sound.getWidth() / 2, 5));
        this.soundContent = this.sound.addComponent(new FormContentBox(0, 30, this.sound.getWidth(), this.sound.getHeight() - 40));
        FormFlow soundFlow = new FormFlow(5);
        this.masterVolume = this.soundContent.addComponent(soundFlow.nextY(new FormLocalSlider("settingsui", "mastervolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
        this.masterVolume.onChanged(e -> {
            Settings.masterVolume = ((FormSlider)e.from).getPercentage();
            SoundManager.updateVolume();
            this.setSaveActive(true);
        });
        this.effectsVolume = this.soundContent.addComponent(soundFlow.nextY(new FormLocalSlider("settingsui", "effectsvolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
        this.effectsVolume.onChanged(e -> {
            Settings.effectsVolume = ((FormSlider)e.from).getPercentage();
            SoundManager.updateVolume();
            this.setSaveActive(true);
        });
        this.weatherVolume = this.soundContent.addComponent(soundFlow.nextY(new FormLocalSlider("settingsui", "weathervolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
        this.weatherVolume.onChanged(e -> {
            Settings.weatherVolume = ((FormSlider)e.from).getPercentage();
            SoundManager.updateVolume();
            this.setSaveActive(true);
        });
        this.UIVolume = this.soundContent.addComponent(soundFlow.nextY(new FormLocalSlider("settingsui", "uivolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
        this.UIVolume.onChanged(e -> {
            Settings.UIVolume = ((FormSlider)e.from).getPercentage();
            SoundManager.updateVolume();
            this.setSaveActive(true);
        });
        this.musicVolume = this.soundContent.addComponent(soundFlow.nextY(new FormLocalSlider("settingsui", "musicvolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
        this.musicVolume.onChanged(e -> {
            Settings.musicVolume = ((FormSlider)e.from).getPercentage();
            SoundManager.updateVolume();
            this.setSaveActive(true);
        });
        this.muteOnFocusLoss = this.soundContent.addComponent(soundFlow.nextY(new FormLocalCheckBox("settingsui", "mutefocus", 10, 0, this.soundContent.getWidth() - 20), 8));
        this.muteOnFocusLoss.onClicked(e -> {
            Settings.muteOnFocusLoss = ((FormCheckBox)e.from).checked;
            this.setSaveActive(true);
        });
        this.musicVolume.controllerDownFocus = this.muteOnFocusLoss;
        this.muteOnFocusLoss.controllerUpFocus = this.musicVolume;
        this.soundContent.addComponent(soundFlow.nextY(new FormLocalLabel("settingsui", "outputdevice", new FontOptions(12), -1, 10, 0, this.soundContent.getWidth() - 20), 8));
        int selectorWidth = Math.min(Math.max(this.soundContent.getWidth() - 100, 300), this.soundContent.getWidth());
        int selectorX = this.soundContent.getWidth() / 2 - selectorWidth / 2;
        this.outputDevice = this.soundContent.addComponent(new FormDropdownSelectionButton(selectorX, soundFlow.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
        this.updateOutputDevices();
        this.outputDevice.onSelected(e -> {
            if (!Objects.equals(e.value, Settings.outputDevice)) {
                this.setSaveActive(true);
            }
        });
        this.outputDevice.controllerUpFocus = this.muteOnFocusLoss;
        this.muteOnFocusLoss.controllerDownFocus = this.outputDevice;
        soundFlow.next(5);
        this.soundContentHeight = soundFlow.next();
        this.soundSave = this.sound.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.sound.getHeight() - 40, this.sound.getWidth() / 2 - 6));
        this.soundSave.onClicked(e -> this.savePressed());
        this.soundBack = this.sound.addComponent(new FormLocalTextButton("ui", "backbutton", this.sound.getWidth() / 2 + 2, this.sound.getHeight() - 40, this.sound.getWidth() / 2 - 6));
        this.soundBack.onClicked(e -> this.subMenuBackPressed());
        this.updateSoundHeight();
    }

    public void updateSoundHeight() {
        int maxHeight = Math.max(100, WindowManager.getWindow().getHudHeight() - 100);
        int height = Math.min(this.soundContentHeight + 40 + this.soundContent.getY(), maxHeight);
        this.sound.setHeight(height);
        this.soundContent.setHeight(this.sound.getHeight() - 40 - this.soundContent.getY());
        this.soundContent.setContentBox(new Rectangle(this.sound.getWidth(), this.soundContentHeight));
        this.soundSave.setY(this.sound.getHeight() - 40);
        this.soundBack.setY(this.sound.getHeight() - 40);
    }

    private void updateModsForm() {
        if (this.mods != null) {
            return;
        }
        this.mods = this.addComponent(new ModsForm("mods", this.continueComponentManager){

            @Override
            public void backPressed() {
                SettingsForm.this.makeCurrent(SettingsForm.this.mainMenu);
            }
        }, (c, isCurrent) -> {
            if (isCurrent.booleanValue()) {
                c.resetCurrent();
            }
        });
    }

    public void makeModsCurrent() {
        this.mods.resetModsList();
        this.makeCurrent(this.mods);
    }

    private void savePressed() {
        if (this.customSave != null) {
            this.customSave.run();
        }
        if (this.displayChanged) {
            GameWindow window = WindowManager.getWindow();
            Dimension oldDisplaySize = new Dimension(window.getWidth(), window.getHeight());
            DisplayMode oldDisplayMode = Settings.displayMode;
            int oldMonitor = Settings.monitor;
            Dimension nextSize = new Dimension(oldDisplaySize);
            Dimension dimensionValue = this.displayScroll.getSelected();
            if (dimensionValue != null) {
                nextSize = dimensionValue;
            }
            Dimension newDisplaySize = nextSize;
            DisplayMode newDisplayMode = this.displayMode.getSelected();
            int newMonitor = (Integer)this.monitorScroll.getValue();
            if (newDisplayMode != oldDisplayMode || !newDisplaySize.equals(oldDisplaySize) || newMonitor != oldMonitor) {
                Settings.displaySize = newDisplaySize;
                Settings.displayMode = newDisplayMode;
                Settings.monitor = newMonitor;
                necesse.engine.platforms.Platform.getWindowManager().updateDisplayModeAfterTick();
                this.startTimerConfirm(25, () -> {
                    Settings.saveClientSettings();
                    this.makeCurrent(this.graphics);
                }, () -> {
                    Settings.displaySize = oldDisplaySize;
                    Settings.displayMode = oldDisplayMode;
                    Settings.monitor = oldMonitor;
                    necesse.engine.platforms.Platform.getWindowManager().updateDisplayModeAfterTick();
                    this.makeCurrent(this.graphics);
                    this.displayChanged = true;
                    this.setSaveActive(true);
                });
            }
            this.displayChanged = false;
        }
        this.prevLanguage = Localization.getCurrentLang();
        String newOutputDevice = this.outputDevice.getSelected();
        if (!Objects.equals(newOutputDevice, Settings.outputDevice)) {
            Settings.outputDevice = newOutputDevice;
            necesse.engine.platforms.Platform.getSoundManager().setAudioDeviceFromSettings();
        }
        Settings.saveClientSettings();
        if (this.changedCursorColor) {
            Renderer.setCursorColor(Settings.cursorColor);
            this.changedCursorColor = false;
        }
        if (this.changedCursorSize) {
            Renderer.setCursorSize(Settings.cursorSize);
            this.changedCursorSize = false;
        }
        this.setSaveActive(false);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.getCurrent() == this.controls && this.currentControlList != null) {
            this.currentControlList.handleInputEvent(event, this.controls, () -> this.setControlsFormList(this.currentControlList));
            if (event.isUsed()) {
                return;
            }
        }
        super.handleInputEvent(event, tickManager, perspective);
        if (event.state && event.getID() == 256) {
            this.submitEscapeEvent(event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.getCurrent() == this.controls && this.currentControlList != null) {
            this.currentControlList.handleControllerEvent(event, this.controls, () -> this.setControlsFormList(this.currentControlList));
            if (event.isUsed()) {
                return;
            }
        }
        super.handleControllerEvent(event, tickManager, perspective);
        if (event.getState() == ControllerInput.MENU_BACK && ControllerInput.MENU_BACK.isJustPressed() || event.getState() == ControllerInput.MAIN_MENU && ControllerInput.MAIN_MENU.isJustPressed()) {
            this.submitEscapeEvent(InputEvent.ControllerButtonEvent(event, tickManager));
        }
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                SettingsForm.this.updateWorldForm();
                SettingsForm.this.updateGeneralForm();
                SettingsForm.this.updateLanguageForm();
                SettingsForm.this.updateControlsForm();
                SettingsForm.this.updateControlTypeForm();
                SettingsForm.this.updateInterfaceForm();
                SettingsForm.this.updateGraphicsForm();
                SettingsForm.this.updateSoundForm();
                SettingsForm.this.updateComponents();
                SettingsForm.this.onWindowResized(WindowManager.getWindow());
            }

            @Override
            public boolean isDisposed() {
                return SettingsForm.this.isDisposed();
            }
        });
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.timerConfirmEnd != 0L && this.isCurrent(this.confirmation)) {
            this.checkConfirmTimerComplete.run();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.isHidden();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        boolean prevHidden = this.isHidden();
        this.hidden = hidden;
        if (!hidden && prevHidden) {
            this.load();
            this.makeCurrent(this.mainMenu);
            this.updateWorldButtonActive();
            if (this.client != null && !this.client.isSingleplayer()) {
                this.pauseOnFocusLoss.setActive(false);
                this.pauseOnFocusLoss.checked = false;
            }
        }
    }

    public void load() {
        if (this.customLoad != null) {
            this.customLoad.run();
        }
        this.setSaveActive(false);
        this.updateComponents();
    }

    public void updateWorldButtonActive() {
        if (this.client == null) {
            this.mainMenuWorld.setActive(false);
            this.mainMenuWorld.setLocalTooltip(null);
        } else if (this.client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            this.mainMenuWorld.setActive(false);
            this.mainMenuWorld.setLocalTooltip("settingsui", "nopermission");
        } else {
            this.mainMenuWorld.setActive(true);
            this.mainMenuWorld.setLocalTooltip(null);
        }
    }

    public void setSaveActive(boolean value) {
        this.saveActive = value;
        this.worldSave.setActive(value);
        this.generalSave.setActive(value);
        this.languageSave.setActive(value);
        this.controlsSave.setActive(value);
        this.interfaceSave.setActive(value);
        this.graphicsSave.setActive(value);
        this.soundSave.setActive(value);
    }

    public void updateComponents() {
        if (this.client != null) {
            this.difficultyForm.selectedDifficulty = this.client.worldSettings.difficulty;
            this.difficultyLabel.setLocalization(this.client.worldSettings.difficulty.displayName);
            this.difficultyForm.updateDifficultyContent();
            this.deathPenalty.setSelected(this.client.worldSettings.deathPenalty, this.client.worldSettings.deathPenalty.displayName);
            this.raidFrequency.setSelected(this.client.worldSettings.raidFrequency, this.client.worldSettings.raidFrequency.displayName);
            this.creativeMode.checked = this.client.worldSettings.creativeMode;
            this.survivalMode.checked = this.client.worldSettings.survivalMode;
            this.playerHunger.checked = this.client.worldSettings.playerHunger;
            this.playerHunger.setActive(!this.survivalMode.checked);
            this.playerHunger.checked = this.survivalMode.checked || this.client.worldSettings.playerHunger;
            this.allowOutsideCharacters.checked = this.client.worldSettings.allowOutsideCharacters;
            this.forcedPvP.checked = this.client.worldSettings.forcedPvP;
        }
        this.sceneSize.setValue((int)(Settings.sceneSize * 100.0f));
        this.adjustZoomOnHighResolution.checked = Settings.adjustZoomOnHighResolution;
        this.limitCameraToLevelBounds.checked = Settings.limitCameraToLevelBounds;
        this.pauseOnFocusLoss.checked = Settings.pauseOnFocusLoss;
        this.savePerformanceOnFocusLoss.checked = Settings.savePerformanceOnFocusLoss;
        this.alwaysSkipTutorial.checked = Settings.alwaysSkipTutorial;
        this.showSettlerHeadArmor.checked = Settings.showSettlerHeadArmor;
        this.prevLanguage = Localization.getCurrentLang();
        this.interfaceSize.setElement(new FormHorizontalScroll.ScrollElement<Float>(Float.valueOf(Settings.interfaceSize), new StaticMessage((int)(Settings.interfaceSize * 100.0f) + "%")));
        this.adjustInterfaceOnHighResolution.checked = Settings.adjustInterfaceOnHighResolution;
        if (this.interfaceStyle != null) {
            this.interfaceStyle.setSelected(Settings.UI, Settings.UI.displayName);
        }
        this.pixelFont.checked = Settings.pixelFont;
        this.showDebugInfo.checked = Settings.showDebugInfo;
        this.showIngredientsAvailable.checked = Settings.showIngredientsAvailable;
        this.showQuestMarkers.checked = Settings.showQuestMarkers;
        this.showTeammateMarkers.checked = Settings.showTeammateMarkers;
        this.showPickupText.checked = Settings.showPickupText;
        this.showDamageText.checked = Settings.showDamageText;
        this.showDoTText.checked = Settings.showDoTText;
        this.showMobHealthBars.checked = Settings.showMobHealthBars;
        this.showBossHealthBars.checked = Settings.showBossHealthBars;
        this.showControlTips.checked = Settings.showControlTips;
        this.showItemTooltipBackground.checked = Settings.showItemTooltipBackground;
        this.showBasicTooltipBackground.checked = Settings.showBasicTooltipBackground;
        this.bigTooltipText.checked = Settings.tooltipTextSize >= 20;
        this.showLogicGateTooltips.checked = Settings.showLogicGateTooltips;
        this.alwaysShowQuickbar.checked = Settings.alwaysShowQuickbar;
        if (this.drawCursorManually != null) {
            this.drawCursorManually.checked = Settings.drawCursorManually;
        }
        this.changedCursorColor = false;
        this.cursorRed.setValue(Settings.cursorColor.getRed());
        this.cursorGreen.setValue(Settings.cursorColor.getGreen());
        this.cursorBlue.setValue(Settings.cursorColor.getBlue());
        this.cursorSize.setValue(Settings.cursorSize);
        this.cursorPreview.setColor(Settings.cursorColor);
        this.cursorPreview.setSize(Settings.cursorSize);
        this.smoothLighting.checked = Settings.smoothLighting;
        this.wavyGrass.checked = Settings.wavyGrass;
        this.denseGrass.checked = Settings.denseGrass;
        this.windEffects.checked = Settings.windEffects;
        this.cameraShake.checked = Settings.cameraShake;
        this.vSyncEnabled.checked = Settings.vSyncEnabled;
        this.maxFPS.setSelected(Settings.maxFPS, Settings.maxFPS == 0 ? new LocalMessage("settingsui", "fpsnocap") : new StaticMessage(String.valueOf(Settings.maxFPS)));
        this.reduceUIFramerate.checked = Settings.reduceUIFramerate;
        if (!this.monitorScroll.setValue(Settings.monitor)) {
            this.monitorScroll.setElement(new FormHorizontalScroll.ScrollElement<Integer>(Integer.valueOf(Settings.monitor), ""));
        }
        this.displayMode.setSelected(Settings.displayMode, Settings.displayMode.displayName);
        this.sceneColors.setSelected(Settings.sceneColors, Settings.sceneColors.displayName);
        this.brightness.setValue((int)(Settings.brightness * 100.0f));
        this.lights.setSelected(Settings.lights, Settings.lights.displayName);
        this.particles.setSelected(Settings.particles, Settings.particles.displayName);
        this.updateOutputDevices();
        this.masterVolume.setPercentage(Settings.masterVolume);
        this.effectsVolume.setPercentage(Settings.effectsVolume);
        this.weatherVolume.setPercentage(Settings.weatherVolume);
        this.UIVolume.setPercentage(Settings.UIVolume);
        this.musicVolume.setPercentage(Settings.musicVolume);
        this.muteOnFocusLoss.checked = Settings.muteOnFocusLoss;
    }

    private void updateOutputDevices() {
        this.outputDeviceNames = necesse.engine.platforms.Platform.getSoundManager().getOutputDevices();
        this.outputDevice.options.clear();
        if (this.outputDeviceNames == null || this.outputDeviceNames.isEmpty()) {
            this.outputDevice.setSelected(null, new LocalMessage("settingsui", "outputdevicenone"));
            this.outputDevice.setActive(false);
        } else {
            this.outputDevice.setActive(true);
            this.outputDevice.options.add(null, new LocalMessage("settingsui", "outputdevicedefault"));
            for (String deviceName : this.outputDeviceNames) {
                this.outputDevice.options.add(deviceName, new StaticMessage(this.getOutputDeviceDisplayName(deviceName)));
            }
            if (Settings.outputDevice == null) {
                this.outputDevice.setSelected(null, new LocalMessage("settingsui", "outputdevicedefault"));
            } else {
                this.outputDevice.setSelected(Settings.outputDevice, new StaticMessage(this.getOutputDeviceDisplayName(Settings.outputDevice)));
            }
        }
    }

    private String getOutputDeviceDisplayName(String deviceName) {
        String displayName = deviceName;
        if (displayName.startsWith("OpenAL Soft on ")) {
            displayName = displayName.substring("OpenAL Soft on ".length());
        }
        return displayName;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.mainMenu.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateWorldHeight();
        this.world.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.difficultyForm.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateGeneralHeight();
        this.general.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateLanguageHeight();
        this.language.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateControlTypesHeight();
        this.controlType.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        if (this.controls != null) {
            this.updateControlsHeight();
            this.controls.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        }
        this.updateInterfaceHeight();
        this.interf.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateGraphicsHeight();
        this.graphics.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.updateSoundHeight();
        this.sound.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        this.confirmation.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (!event.isUsed() && !this.isCurrent(this.mainMenu)) {
            if (this.isCurrent(this.difficultyForm)) {
                this.difficultyLabel.setLocalization(this.difficultyForm.selectedDifficulty.displayName);
                this.makeWorldCurrent(false);
                if (this.client.worldSettings.difficulty != this.difficultyForm.selectedDifficulty) {
                    this.setSaveActive(true);
                }
            } else if (this.isCurrent(this.confirmation)) {
                this.confirmation.submitBackEvent();
            } else if (this.isCurrent(this.controls)) {
                this.subMenuBackPressed(this.mainMenu);
            } else {
                this.subMenuBackPressed();
            }
            event.use();
        }
    }

    public void subMenuBackPressed() {
        this.subMenuBackPressed(this.mainMenu);
    }

    public void subMenuBackPressed(Form nextCurrent) {
        if (this.saveActive) {
            this.confirmation.setupConfirmation(new LocalMessage("settingsui", "saveconfirm"), (GameMessage)new LocalMessage("ui", "savebutton"), (GameMessage)new LocalMessage("ui", "dontsavebutton"), () -> {
                this.makeCurrent(nextCurrent);
                this.savePressed();
            }, () -> {
                Settings.loadClientSettings();
                this.load();
                this.makeCurrent(nextCurrent);
            });
            this.makeCurrent(this.confirmation);
        } else {
            Settings.loadClientSettings();
            this.load();
            this.makeCurrent(nextCurrent);
        }
    }

    private void updateMonitorScroll() {
        this.monitorScroll.set("", (int)((Integer)this.monitorScroll.getValue()), 0, necesse.engine.platforms.Platform.getWindowManager().getMonitors().length - 1);
    }

    private void updateDisplayScrollComponent() {
        Dimension[] sizes;
        if (this.displayScroll == null) {
            int selectorWidth = Math.min(Math.max(this.graphicsContent.getWidth() - 100, 200), this.graphicsContent.getWidth());
            int selectorX = this.graphicsContent.getWidth() / 2 - selectorWidth / 2;
            this.displayScroll = this.graphicsContent.addComponent(new FormDropdownSelectionButton(selectorX, this.displayScrollY, FormInputSize.SIZE_20, ButtonColor.BASE, selectorWidth));
            this.displayScroll.onSelected(e -> {
                this.displayScroll.setActive(this.displayMode.getSelected().canSelectSize);
                this.displayChanged = true;
                this.setSaveActive(true);
            });
        }
        long monitor = WindowManager.getMonitor((Integer)this.monitorScroll.getValue());
        try {
            sizes = necesse.engine.platforms.Platform.getWindowManager().getVideoModes(monitor);
        }
        catch (Exception e2) {
            sizes = new Dimension[]{new Dimension(1280, 720), new Dimension(1920, 1080)};
        }
        this.displayScroll.options.clear();
        for (Dimension size : sizes) {
            this.displayScroll.options.add(new FormDropdownSelectionButton.Option<Dimension>(size, new StaticMessage(size.width + "x" + size.height)));
        }
        this.displayScroll.setActive(this.displayMode.getSelected().canSelectSize);
        switch (this.displayMode.getSelected()) {
            case Windowed: {
                GameWindow window = WindowManager.getWindow();
                this.displayScroll.setSelected(new Dimension(window.getWidth(), window.getHeight()), new StaticMessage(window.getWidth() + "x" + window.getHeight()));
                break;
            }
            case Fullscreen: 
            case Borderless: {
                GameWindow window;
                if (monitor == 0L) {
                    window = WindowManager.getWindow();
                    this.displayScroll.setSelected(new Dimension(window.getWidth(), window.getHeight()), new StaticMessage("Monitor not found"));
                    System.err.println("Could not find monitor");
                    break;
                }
                try {
                    Dimension vidMode = necesse.engine.platforms.Platform.getWindowManager().getVideoMode(monitor);
                    if (vidMode == null) {
                        throw new NullPointerException("Could not find monitor video mode");
                    }
                    this.displayScroll.setSelected(new Dimension(vidMode.width, vidMode.height), new StaticMessage(vidMode.width + "x" + vidMode.height));
                    break;
                }
                catch (Exception e3) {
                    GameWindow window2 = WindowManager.getWindow();
                    this.displayScroll.setSelected(new Dimension(window2.getWidth(), window2.getHeight()), new StaticMessage("VideoMode not found"));
                    System.err.println("Could not find monitor video mode");
                }
            }
        }
    }

    public void startTimerConfirm(int seconds, Runnable confirmPressed, Runnable backPressed) {
        this.timerConfirmEnd = System.currentTimeMillis() + (long)seconds * 1000L;
        this.confirmation.setupConfirmation(content -> {
            FormLocalLabel confirmLabel = content.addComponent(new FormLocalLabel("settingsui", "confirm", new FontOptions(20), 0, this.confirmation.getWidth() / 2, 10, this.confirmation.getWidth() - 20));
            FormLabel timerLabel = content.addComponent(new FormLabel("", new FontOptions(20), 0, this.confirmation.getWidth() / 2, 10 + confirmLabel.getHeight() + 5));
            this.checkConfirmTimerComplete = () -> {
                long timeLeft = this.timerConfirmEnd - System.currentTimeMillis();
                if (timeLeft < 0L) {
                    this.timerConfirmEnd = 0L;
                    backPressed.run();
                } else {
                    timerLabel.setText((int)Math.ceil((double)timeLeft / 1000.0) + "");
                }
            };
            this.checkConfirmTimerComplete.run();
        }, () -> {
            this.timerConfirmEnd = 0L;
            confirmPressed.run();
        }, () -> {
            this.timerConfirmEnd = 0L;
            backPressed.run();
        });
        this.makeCurrent(this.confirmation);
    }

    public void backPressed() {
    }

    private static /* synthetic */ GameMessage lambda$updateWorldForm$16(GameRaidFrequency value) {
        return value.description;
    }
}

