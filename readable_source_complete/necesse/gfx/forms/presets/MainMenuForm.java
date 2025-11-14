/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.engine.GameInfo;
import necesse.engine.Settings;
import necesse.engine.dlc.DLC;
import necesse.engine.dlc.DLCProvider;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.credits.FinalGamesCredits;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.CreditsDisplayAndControlsFormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.ExternalLinkForm;
import necesse.gfx.forms.presets.PatchNotesForm;
import necesse.gfx.forms.presets.PlayMultiplayerForm;
import necesse.gfx.forms.presets.PlaySingleplayerForm;
import necesse.gfx.forms.presets.SettingsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;

public class MainMenuForm
extends FormSwitcher {
    public MainMenu mainMenu;
    public PlaySingleplayerForm playSingleplayerForm;
    public PlayMultiplayerForm playMultiplayerForm;
    public PatchNotesForm patchNotes;
    public SettingsForm settings;
    public FormComponentList main;
    public Form mainForm;
    public Form mainSideButtons;
    public Form dlcSideButtons;
    public PlayerStatsSelectorForm stats;
    public CreditsDisplayAndControlsFormComponent creditsDisplay;
    protected boolean hidden;
    protected FormLocalTextButton continueButton;
    protected Runnable continueLast;

    public MainMenuForm(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.main = this.addComponent(new FormComponentList(), (form, active) -> {
            if (active.booleanValue()) {
                this.updateContinueButton();
            }
        });
        this.mainForm = this.main.addComponent(new Form("main", 400, 40));
        int buttonsWidth = this.mainForm.getWidth() - 8;
        FormFlow mainFlow = new FormFlow();
        this.continueButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "continueworld", 4, mainFlow.next(40), buttonsWidth));
        this.continueButton.onClicked(e -> this.continueLast());
        this.continueButton.prioritizeControllerFocus();
        this.mainForm.addComponent(new FormLocalTextButton("ui", "playsingleplayer", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> {
            this.makeCurrent(this.playSingleplayerForm);
            this.playSingleplayerForm.makeMainMenuCurrent();
        });
        this.mainForm.addComponent(new FormLocalTextButton("ui", "playmultiplayer", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> {
            this.makeCurrent(this.playMultiplayerForm);
            this.playMultiplayerForm.makeMainMenuCurrent();
        });
        this.mainForm.addComponent(new FormLocalTextButton("ui", "settings", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeCurrent(this.settings));
        this.mainForm.addComponent(new FormLocalTextButton("ui", "achievementsandstats", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> {
            this.makeCurrent(this.stats);
            this.stats.reset();
        });
        this.mainForm.addComponent(new FormLocalTextButton("ui", "credits", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> {
            this.makeCurrent(this.creditsDisplay);
            this.creditsDisplay.restart();
        });
        FormLocalTextButton exitButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "exit", 4, mainFlow.next(40), buttonsWidth));
        exitButton.onClicked(e -> WindowManager.getWindow().requestClose());
        exitButton.controllerDownFocus = exitButton;
        this.mainForm.setHeight(mainFlow.next());
        this.mainSideButtons = this.main.addComponent(new Form("mainButtons", 32, 32));
        this.mainSideButtons.setPosition(new FormRelativePosition((FormPositionContainer)this.mainForm, () -> this.mainForm.getWidth() + this.getInterfaceStyle().formSpacing, () -> this.mainForm.getHeight() - this.mainSideButtons.getHeight()));
        FormFlow sideButtonsFlow = new FormFlow();
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().camera_pan, new LocalMessage("ui", "camerapantoggle"))).onClicked(e -> mainMenu.toggleCameraPanSetting());
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().quickbar_quests, new GameMessage[]{new LocalMessage("misc", "patchnotes")}){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                if (GameInfo.shouldPatchNotesBlink()) {
                    int blinkTime = 500;
                    long time = System.currentTimeMillis() % (long)(blinkTime * 2);
                    this.color = time <= (long)blinkTime ? ButtonColor.GREEN : ButtonColor.BASE;
                } else {
                    this.color = ButtonColor.BASE;
                }
                super.draw(tickManager, perspective, renderBox);
            }
        }).onClicked(e -> {
            this.makeCurrent(this.patchNotes);
            GameInfo.refreshPatchNotesOpen();
        });
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().steam_logo, new LocalMessage("misc", "followsteamnews"))).onClicked(e -> this.openUrlOrShowExternalLink(new LocalMessage("misc", "followsteamnews"), "https://store.steampowered.com/news/app/1169040", this.getInterfaceStyle().qr_steam_news));
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().discord_logo, new LocalMessage("misc", "joindiscord"))).onClicked(e -> this.openUrlOrShowExternalLink(new LocalMessage("misc", "joindiscord"), "https://discord.gg/YBhNh52dpy", this.getInterfaceStyle().qr_discord));
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().x_logo, new LocalMessage("misc", "followx"))).onClicked(e -> this.openUrlOrShowExternalLink(new LocalMessage("misc", "followxfull"), "https://x.com/NecesseGame", this.getInterfaceStyle().qr_x_necessegame));
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().youtube_logo, new LocalMessage("misc", "subscribeyoutube"))).onClicked(e -> this.openUrlOrShowExternalLink(new LocalMessage("misc", "subscribeyoutubefull"), "https://www.youtube.com/@Necesse?sub_confirmation=1", this.getInterfaceStyle().qr_youtube_necesse));
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().reddit_logo, new LocalMessage("misc", "joinreddit"))).onClicked(e -> this.openUrlOrShowExternalLink(new LocalMessage("misc", "joinredditfull"), "https://reddit.com/r/Necesse", this.getInterfaceStyle().qr_reddit_necesse));
        this.mainSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().world_icon, new LocalMessage("settingsui", "language"))).onClicked(e -> {
            this.makeCurrent(this.settings);
            this.settings.makeLanguageCurrent();
        });
        this.mainSideButtons.setHeight(sideButtonsFlow.next());
        DLCProvider.instance.checkForNewlyInstalledDLCs();
        this.dlcSideButtons = this.main.addComponent(new Form("mainButtons", 32, 32));
        this.dlcSideButtons.setPosition(new FormRelativePosition((FormPositionContainer)this.mainForm, () -> -Settings.UI.formSpacing - this.dlcSideButtons.getWidth(), () -> this.mainForm.getHeight() - this.dlcSideButtons.getHeight()));
        sideButtonsFlow = new FormFlow();
        if (DLCProvider.getInstalledDLCs().contains(DLC.SUPPORTER_PACK)) {
            ButtonTexture buttonTexture = new ButtonTexture(Settings.UI, ItemRegistry.getItem((String)"supporterhelmet").getItemSprite(null, null).texture.resize(24, 24), false);
            this.dlcSideButtons.addComponent(new FormContentIconButton(0, sideButtonsFlow.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, buttonTexture, new LocalMessage("ui", "supporterpackinstalled")));
        }
        this.dlcSideButtons.setHidden(this.dlcSideButtons.getComponentList().size() == 0);
        this.dlcSideButtons.setHeight(sideButtonsFlow.next());
        this.playSingleplayerForm = this.addComponent(new PlaySingleplayerForm(mainMenu){

            @Override
            public void onBackPressed() {
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        });
        this.playMultiplayerForm = this.addComponent(new PlayMultiplayerForm(mainMenu){

            @Override
            public void onBackPressed() {
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        });
        this.settings = this.addComponent(new SettingsForm(null, mainMenu.getFormManager()){

            @Override
            public void backPressed() {
                Settings.loadClientSettings();
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        }, (f, active) -> {
            if (active.booleanValue()) {
                f.load();
            }
        });
        this.stats = this.addComponent(new PlayerStatsSelectorForm(true){

            @Override
            public void backPressed() {
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        });
        this.creditsDisplay = this.addComponent(new CreditsDisplayAndControlsFormComponent(WindowManager.getWindow(), new FinalGamesCredits(true)){

            @Override
            public void onBackPressed() {
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        });
        this.patchNotes = this.addComponent(new PatchNotesForm(1000, 800){

            @Override
            public void backPressed() {
                MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
            }
        });
        this.onWindowResized(WindowManager.getWindow());
        this.makeCurrent(this.main);
    }

    private void openUrlOrShowExternalLink(GameMessage title, String url, GameTexture qrCodeTexture) {
        if (PlatformManager.getPlatform().canOpenURLs()) {
            GameUtils.openURL(url);
        } else {
            ExternalLinkForm externalLink = new ExternalLinkForm(qrCodeTexture, url, title, () -> this.makeCurrent(this.main));
            this.addAndMakeCurrentTemporary(externalLink);
        }
    }

    public void updateContinueButton() {
        ObjectValue<GameMessage, Runnable> continueCache = this.mainMenu.loadContinueCacheSave();
        if (continueCache == null) {
            this.continueButton.setLocalization(new LocalMessage("ui", "continueworld"));
            this.continueButton.setActive(false);
            this.continueLast = null;
        } else {
            this.continueButton.setLocalization((GameMessage)continueCache.object);
            this.continueButton.setActive(true);
            this.continueLast = (Runnable)continueCache.value;
        }
    }

    public void continueLast() {
        if (this.continueLast != null) {
            this.continueLast.run();
        }
    }

    public void submitEscapeEvent(InputEvent event) {
        if (event.isUsed()) {
            return;
        }
        if (this.isCurrent(this.settings)) {
            this.makeCurrent(this.main);
            Settings.loadClientSettings();
            event.use();
        } else if (this.isCurrent(this.playSingleplayerForm)) {
            this.playSingleplayerForm.submitEscapeEvent(event);
        } else if (this.isCurrent(this.playMultiplayerForm)) {
            this.playMultiplayerForm.submitEscapeEvent(event);
        } else if (this.isCurrent(this.stats)) {
            this.stats.submitEscapeEvent(event);
            if (event.isUsed()) {
                return;
            }
            this.makeCurrent(this.main);
            event.use();
        } else if (this.isCurrent(this.patchNotes)) {
            this.patchNotes.submitEscapeEvent(event);
        } else {
            this.makeCurrent(this.main);
            event.use();
        }
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.isHidden();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.creditsDisplay) && this.creditsDisplay.isDone()) {
            this.makeCurrent(this.main);
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.mainForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (!hidden) {
            this.setConnectedFromCurrent();
        }
    }

    public void setConnectedFromCurrent() {
        if (this.mainMenu.connectedFrom != null) {
            switch (this.mainMenu.connectedFrom) {
                case SingleplayerMenu: {
                    this.makeCurrent(this.playSingleplayerForm);
                    this.playSingleplayerForm.makeMainMenuCurrent();
                    break;
                }
                case SinglePlayerLoadWorld: {
                    this.makeCurrent(this.playSingleplayerForm);
                    this.playSingleplayerForm.makeLoadWorldCurrent();
                    break;
                }
                case MultiplayerMenu: {
                    this.makeCurrent(this.playMultiplayerForm);
                    this.playMultiplayerForm.makeMainMenuCurrent();
                    break;
                }
                case MultiplayerHostWorld: {
                    this.makeCurrent(this.playMultiplayerForm);
                    this.playMultiplayerForm.makeHostWorldCurrent();
                    break;
                }
                case MultiplayerJoinFriend: {
                    this.makeCurrent(this.playMultiplayerForm);
                    this.playMultiplayerForm.makeJoinFriendCurrent();
                    break;
                }
                case MultiplayerJoinServer: {
                    this.makeCurrent(this.playMultiplayerForm);
                    this.playMultiplayerForm.makeJoinServerCurrent();
                    break;
                }
                default: {
                    this.makeCurrent(this.main);
                    break;
                }
            }
        } else {
            this.makeCurrent(this.main);
        }
    }
}

