/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.playerStats;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.GlobalData;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.AchievementsContentForm;
import necesse.gfx.forms.presets.GlobalStatsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelected;
import necesse.gfx.gameFont.FontOptions;

public class PlayerStatsSelectorForm
extends FormSwitcher {
    private Form menuForm;
    private Form contentForm;
    private FormLocalLabel contentLabel;
    private FormComponent loadingTip;
    private FormContentBox contentBox;
    private FormSwitcher contentSwitcher;
    private LinkedList<StatsOption> options = new LinkedList();
    private StatsOption currentSelected = null;
    public StatsOption achievementsOption;
    public AchievementsContentForm achievements = null;
    public StatsOption playerStatsOption;
    public PlayerStatsForm playerStats = null;

    public PlayerStatsSelectorForm(int menuWidth, int width, int height, boolean addDefaultOptions) {
        this.menuForm = this.addComponent(new Form(menuWidth, 0));
        this.updateMenu();
        this.contentForm = this.addComponent(new Form(width, height));
        this.contentForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.contentForm.getHeight() - 40, this.contentForm.getWidth() - 8)).onClicked(e -> {
            if (this.currentSelected != null && this.currentSelected.statsComponent instanceof PlayerStatsSelected && ((PlayerStatsSelected)((Object)this.currentSelected.statsComponent)).backPressed()) {
                return;
            }
            this.makeCurrent(this.menuForm);
        });
        this.contentLabel = this.contentForm.addComponent(new FormLocalLabel(new StaticMessage("N/A"), new FontOptions(20), 0, this.contentForm.getWidth() / 2, 5));
        this.contentBox = this.contentForm.addComponent(new FormContentBox(0, 30, this.getContentWidth(), this.getContentHeight()));
        this.contentSwitcher = this.contentBox.addComponent(new FormSwitcher());
        this.loadingTip = this.contentSwitcher.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(20), 0, this.contentForm.getWidth() / 2, 30, this.contentForm.getWidth() - 10));
        if (addDefaultOptions) {
            this.addDefaultOptions();
        }
        this.makeCurrent(this.menuForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    public PlayerStatsSelectorForm(boolean addDefaultOptions) {
        this(400, 400, 480, addDefaultOptions);
    }

    private void updateMenu() {
        this.menuForm.clearComponents();
        int y = 0;
        for (StatsOption option : this.options) {
            FormLocalTextButton button = this.menuForm.addComponent(new FormLocalTextButton(option.displayName, 4, y, this.menuForm.getWidth() - 8));
            if (option.tooltip != null) {
                button.setLocalTooltip(option.tooltip);
            }
            button.onClicked(e -> option.makeCurrent());
            y += 40;
        }
        this.menuForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, y, this.menuForm.getWidth() - 8)).onClicked(e -> this.backPressed());
        this.menuForm.setHeight(y += 40);
    }

    public void addDefaultOptions() {
        this.achievements = new AchievementsContentForm("achievements", this.getContentWidth(), this.getContentHeight(), this);
        this.achievementsOption = this.addComponentOption((GameMessage)new LocalMessage("ui", "achievements"), null, this.achievements);
        this.playerStats = (PlayerStatsForm)this.fromStatsToComponent(GlobalData.stats());
        this.playerStatsOption = this.addComponentOption((GameMessage)new LocalMessage("ui", "playerstats"), (GameMessage)new LocalMessage("ui", "playerstatstip"), this.playerStats);
        if (GlobalData.isDevMode()) {
            this.addComponentOption(new StaticMessage("DEBUGGING: " + Localization.translate("ui", "globalstats")), null, new GlobalStatsForm(0, 0, this.getContentWidth(), this.getContentHeight()), () -> Platform.getStatsProvider().updateGlobalStats());
        }
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.currentSelected != null && this.currentSelected.statsComponent instanceof PlayerStatsSelected && ((PlayerStatsSelected)((Object)this.currentSelected.statsComponent)).backPressed()) {
            event.use();
            return;
        }
        if (!this.isCurrent(this.menuForm)) {
            this.makeCurrent(this.menuForm);
            event.use();
        }
    }

    public Consumer<PlayerStats> addStatsOption(GameMessage displayName, GameMessage tooltip, Runnable startLoading) {
        StatsOption option = this.addOption(new StatsOption(displayName, tooltip, startLoading));
        return stats -> option.loadStats.accept(this.fromStatsToComponent((PlayerStats)stats));
    }

    public StatsOption addStatsOption(GameMessage displayName, GameMessage tooltip, PlayerStats stats) {
        return this.addOption(new StatsOption(displayName, tooltip, () -> {}, this.fromStatsToComponent(stats)));
    }

    public StatsOption addComponentOption(GameMessage displayName, GameMessage tooltip, FormComponent component) {
        return this.addOption(new StatsOption(displayName, tooltip, () -> {}, component));
    }

    public StatsOption addComponentOption(GameMessage displayName, GameMessage tooltip, FormComponent component, Runnable onSelected) {
        return this.addOption(new StatsOption(displayName, tooltip, onSelected, component));
    }

    public Consumer<FormComponent> addComponentOption(GameMessage displayName, GameMessage tooltip, Runnable startLoading) {
        StatsOption out = this.addOption(new StatsOption(displayName, tooltip, startLoading));
        return out.loadStats;
    }

    private StatsOption addOption(StatsOption option) {
        this.options.add(option);
        this.updateMenu();
        this.onWindowResized(WindowManager.getWindow());
        return option;
    }

    public FormComponent fromStatsToComponent(PlayerStats stats) {
        if (stats != null && stats.mode != EmptyStats.Mode.WRITE_ONLY) {
            return new PlayerStatsForm(0, 0, this.getContentWidth(), this.getContentHeight(), stats);
        }
        return new FormLocalLabel("ui", "statsnotfound", new FontOptions(20), 0, this.getContentWidth() / 2, 10, this.getContentWidth() - 10);
    }

    public int getContentWidth() {
        return this.contentForm.getWidth();
    }

    public int getContentHeight() {
        return this.contentForm.getHeight() - 70;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.menuForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.contentForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void backPressed() {
    }

    public void reset() {
        this.options.forEach(StatsOption::reset);
        this.makeCurrent(this.menuForm);
    }

    public void disableAchievements() {
        if (this.achievements != null) {
            this.achievements.disableAchievements();
        }
        if (this.playerStats != null) {
            this.playerStats.setDisabledTip(new LocalMessage("ui", "psdisabled"), new LocalMessage("ui", "achdisabledhelp"));
        }
    }

    public class StatsOption {
        public final GameMessage displayName;
        public final GameMessage tooltip;
        private boolean resetComponent = true;
        private FormComponent statsComponent = null;
        private Runnable startLoading;
        public final Consumer<FormComponent> loadStats;

        public StatsOption(GameMessage displayName, GameMessage tooltip, Runnable startLoading) {
            this.displayName = displayName;
            this.tooltip = tooltip;
            this.startLoading = startLoading;
            this.loadStats = component -> {
                this.reset();
                if (this.resetComponent) {
                    this.statsComponent = PlayerStatsSelectorForm.this.contentSwitcher.addComponent(component);
                }
                if (PlayerStatsSelectorForm.this.currentSelected == this) {
                    this.makeCurrent();
                }
            };
        }

        public StatsOption(GameMessage displayName, GameMessage tooltip, Runnable startLoading, FormComponent component) {
            this(displayName, tooltip, startLoading);
            this.statsComponent = this$0.contentSwitcher.addComponent(component);
            this.resetComponent = false;
        }

        public void reset() {
            if (this.resetComponent && this.statsComponent != null) {
                PlayerStatsSelectorForm.this.contentSwitcher.removeComponent(this.statsComponent);
                this.statsComponent = null;
            }
        }

        public void makeCurrent() {
            PlayerStatsSelectorForm.this.contentLabel.setLocalization(this.displayName);
            PlayerStatsSelectorForm.this.makeCurrent(PlayerStatsSelectorForm.this.contentForm);
            PlayerStatsSelectorForm.this.currentSelected = this;
            if (this.statsComponent == null) {
                PlayerStatsSelectorForm.this.contentSwitcher.makeCurrent(PlayerStatsSelectorForm.this.loadingTip);
                PlayerStatsSelectorForm.this.contentBox.setContentBox(new Rectangle(PlayerStatsSelectorForm.this.getContentWidth(), PlayerStatsSelectorForm.this.getContentHeight()));
                this.startLoading.run();
            } else {
                PlayerStatsSelectorForm.this.contentSwitcher.makeCurrent(this.statsComponent);
                PlayerStatsSelectorForm.this.contentBox.fitContentBoxToComponents();
                PlayerStatsSelectorForm.this.contentBox.centerContentHorizontal();
                if (this.statsComponent instanceof PlayerStatsSelected) {
                    ((PlayerStatsSelected)((Object)this.statsComponent)).onSelected();
                }
            }
        }
    }
}

