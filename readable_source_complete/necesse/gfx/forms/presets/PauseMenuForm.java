/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.GameInfo;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.HostSettingsForm;
import necesse.engine.network.NetworkManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketServerLevelStats;
import necesse.engine.network.packet.PacketServerWorldStats;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.ExternalLinkForm;
import necesse.gfx.forms.presets.FeedbackForm;
import necesse.gfx.forms.presets.PatchNotesForm;
import necesse.gfx.forms.presets.SettingsForm;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;

public class PauseMenuForm
extends FormSwitcher {
    protected boolean hidden;
    public SettingsForm settings;
    public FormComponentList main;
    public Form mainForm;
    public Form mainSideButtons;
    public HostSettingsForm hostWorldForm;
    public Form inviteFriendsForm;
    public FormGeneralList inviteForm;
    public PlayerStatsSelectorForm stats;
    public Consumer<PlayerStats> onServerLevelStatsLoad;
    public Consumer<PlayerStats> onServerWorldStatsLoad;
    public FeedbackForm feedback;
    public PatchNotesForm patchNotes;
    public MainGame mainGame;
    public Client client;

    public PauseMenuForm(MainGame mainGame, Client client) {
        this.mainGame = mainGame;
        this.client = client;
        final PauseMenuForm self = this;
        this.main = this.addComponent(new FormComponentList());
        this.mainForm = this.main.addComponent(new Form("main", 400, 200));
        this.setupMainForm();
        this.mainSideButtons = this.main.addComponent(new Form("mainButtons", 32, 32));
        this.mainSideButtons.setPosition(new FormRelativePosition((FormPositionContainer)this.mainForm, () -> this.mainForm.getWidth() + this.getInterfaceStyle().formSpacing, () -> this.mainForm.getHeight() - this.mainSideButtons.getHeight()));
        FormFlow sideButtonsFlow = new FormFlow();
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
        NetworkManager networkManager = Platform.getNetworkManager();
        if (networkManager.allowsHosting()) {
            this.hostWorldForm = this.addComponent(networkManager.getHostSettingsForm(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.main), () -> {
                this.makeCurrent(this.main);
                this.setupMainForm();
            }));
            if (networkManager.allowsFriendJoining()) {
                this.inviteFriendsForm = this.addComponent(new Form(300, 400));
                this.inviteForm = this.inviteFriendsForm.addComponent(networkManager.getInviteForm(client, 0, 0, this.inviteFriendsForm.getWidth(), this.inviteFriendsForm.getHeight() - 40));
                this.inviteFriendsForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.inviteFriendsForm.getHeight() - 40, this.inviteFriendsForm.getWidth() - 8)).onClicked(e -> this.makeCurrent(this.main));
            }
        }
        this.stats = this.addComponent(new PlayerStatsSelectorForm(true){

            @Override
            public void backPressed() {
                PauseMenuForm.this.makeCurrent(PauseMenuForm.this.main);
            }
        });
        this.stats.addStatsOption((GameMessage)new LocalMessage("ui", "characterstats"), (GameMessage)new LocalMessage("ui", "characterstatstip"), client.characterStats);
        if (GlobalData.isDevMode()) {
            this.onServerLevelStatsLoad = this.stats.addStatsOption((GameMessage)new StaticMessage("DEBUGGING: Level stats"), (GameMessage)new StaticMessage("Player stats happened on this level"), () -> client.network.sendPacket(new PacketServerLevelStats()));
        }
        this.onServerWorldStatsLoad = this.stats.addStatsOption((GameMessage)new LocalMessage("ui", "worldstats"), (GameMessage)new LocalMessage("ui", "worldstatstip"), () -> client.network.sendPacket(new PacketServerWorldStats()));
        if (!client.worldSettings.achievementsEnabled()) {
            this.disableAchievements();
        }
        this.settings = this.addComponent(new SettingsForm(client, mainGame.formManager){

            @Override
            public void backPressed() {
                self.makeCurrent(PauseMenuForm.this.main);
                Settings.loadClientSettings();
            }
        }, (f, active) -> {
            if (active.booleanValue()) {
                f.load();
            }
        });
        this.feedback = this.addComponent(new FeedbackForm("MainGame", 400, 200){

            @Override
            public void backPressed() {
                self.makeCurrent(PauseMenuForm.this.main);
            }
        });
        this.patchNotes = this.addComponent(new PatchNotesForm(1000, 800){

            @Override
            public void backPressed() {
                self.makeCurrent(PauseMenuForm.this.main);
            }
        });
        this.onWindowResized(WindowManager.getWindow());
    }

    private void openUrlOrShowExternalLink(GameMessage title, String url, GameTexture qrCodeTexture) {
        if (PlatformManager.getPlatform().canOpenURLs()) {
            GameUtils.openURL(url);
        } else {
            ExternalLinkForm externalLink = new ExternalLinkForm(qrCodeTexture, url, title, () -> this.makeCurrent(this.main));
            this.addAndMakeCurrentTemporary(externalLink);
        }
    }

    public void setupMainForm() {
        NetworkManager networkManager = Platform.getNetworkManager();
        this.mainForm.clearComponents();
        FormFlow flow = new FormFlow();
        this.mainForm.addComponent(new FormLocalTextButton("ui", "resumegame", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> this.mainGame.setRunning(true));
        if (this.client.isSingleplayer() && networkManager.allowsHosting()) {
            this.mainForm.addComponent(new FormLocalTextButton("ui", "hostworld", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> {
                this.hostWorldForm.reset(this.client.worldSettings);
                this.makeCurrent(this.hostWorldForm);
            });
        } else {
            this.mainForm.addComponent(new FormLocalTextButton("ui", "pvpandteams", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> {
                ((FormButton)e.from).startCooldown(500);
                this.mainGame.setRunning(true);
                this.client.network.sendPacket(new PacketOpenPvPTeams());
                PvPTeamsContainerForm.pauseGameOnClose = true;
            });
            if (networkManager.allowsFriendJoining()) {
                this.mainForm.addComponent(new FormLocalTextButton("ui", "invitefriends", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> {
                    this.inviteForm.reset();
                    this.makeCurrent(this.inviteFriendsForm);
                });
            }
        }
        this.mainForm.addComponent(new FormLocalTextButton("ui", "achievementsandstats", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> {
            this.makeCurrent(this.stats);
            this.stats.reset();
        });
        this.mainForm.addComponent(new FormLocalTextButton("settingsui", "front", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> this.makeCurrent(this.settings));
        this.mainForm.addComponent(new FormLocalTextButton("ui", "givefeedback", 4, flow.next(40), this.mainForm.getWidth() - 8)).onClicked(e -> this.makeCurrent(this.feedback));
        FormLocalTextButton exitButton = this.mainForm.addComponent(new FormLocalTextButton("ui", "tomain", 4, flow.next(40), this.mainForm.getWidth() - 8));
        exitButton.onClicked(e -> this.mainGame.disconnect("Quit"));
        exitButton.controllerDownFocus = exitButton;
        this.mainForm.setHeight(flow.next());
    }

    public void submitEscapeEvent(InputEvent event) {
        if (event.isUsed()) {
            return;
        }
        if (this.isCurrent(this.settings)) {
            this.settings.submitEscapeEvent(event);
            if (event.isUsed()) {
                return;
            }
            this.makeCurrent(this.main);
            Settings.loadClientSettings();
            event.use();
        } else if (this.isCurrent(this.stats)) {
            this.stats.submitEscapeEvent(event);
            if (event.isUsed()) {
                return;
            }
            this.makeCurrent(this.main);
            event.use();
        } else if (this.isCurrent(this.hostWorldForm)) {
            this.makeCurrent(this.main);
            event.use();
        } else if (Platform.getNetworkManager().allowsFriendJoining() && this.isCurrent(this.inviteFriendsForm)) {
            this.makeCurrent(this.main);
            event.use();
        } else if (this.isCurrent(this.feedback)) {
            this.feedback.submitEscapeEvent(event);
        } else if (this.isCurrent(this.patchNotes)) {
            this.patchNotes.submitEscapeEvent(event);
        } else if (this.isCurrent(this.main)) {
            this.mainGame.setRunning(true);
            Settings.loadClientSettings();
            event.use();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.hostWorldForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        if (this.inviteFriendsForm != null) {
            this.inviteFriendsForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        }
        this.mainForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.isHidden();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (!hidden) {
            this.makeCurrent(this.main);
        }
    }

    public void disableAchievements() {
        this.stats.disableAchievements();
    }

    public void applyServerLevelStatsPacket(PacketServerLevelStats p) {
        this.onServerLevelStatsLoad.accept(p.stats);
    }

    public void applyServerWorldStatsPacket(PacketServerWorldStats p) {
        this.onServerWorldStatsLoad.accept(p.stats);
    }
}

