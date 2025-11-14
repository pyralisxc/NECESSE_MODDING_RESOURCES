/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormAchievementComponent;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ContentBoxListManager;
import necesse.gfx.forms.presets.DisabledPreForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelected;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.ui.ButtonColor;

public class AchievementsContentForm
extends Form
implements PlayerStatsSelected {
    private PlayerStatsSelectorForm selectorForm;
    private FormLocalCheckBox achievementsShowCompleted;
    private FormCustomDraw progressBar;
    private ContentBoxListManager achievementList;
    private Form disabledContent;

    public AchievementsContentForm(String name, int width, int height, PlayerStatsSelectorForm selectorForm) {
        super(name, width, height);
        this.selectorForm = selectorForm;
        this.drawBase = false;
        this.progressBar = this.addComponent(new FormCustomDraw(0, 0, width, 20){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                int completed = GlobalData.achievements().getCompleted();
                int total = GlobalData.achievements().getTotal();
                float completePercent = (float)completed / (float)total;
                String s = completed + "/" + total;
                Achievement.drawProgressbarText(this.getX() + 10, this.getY(), AchievementsContentForm.this.getWidth() - 20, 5, completePercent, s, this.getInterfaceStyle().activeTextColor);
            }
        });
        this.achievementList = this.addComponent(new FormContentBox(0, 20, width, height - 50)).listManager();
        this.achievementsShowCompleted = this.addComponent(new FormLocalCheckBox("ui", "showcompleted", 10, height - 20, true));
        this.achievementsShowCompleted.onClicked(e -> this.updateList());
        this.updateList();
    }

    private void updateList() {
        this.achievementList.clear();
        ArrayList<Achievement> achievements = new ArrayList<Achievement>(GlobalData.achievements().getAchievements());
        achievements.sort(Comparator.comparing(a -> a.name.translate()));
        for (Achievement achievement : achievements) {
            if (!this.achievementsShowCompleted.checked && achievement.isCompleted()) continue;
            this.achievementList.add(new FormAchievementComponent(5, 0, this.getWidth() - 10, achievement));
            if (!achievement.isCompleted()) {
                this.achievementList.add(new FormLocalCheckBox((GameMessage)new LocalMessage("achievement", "track", "achievement", achievement.name), 55, 0, TrackedSidebarForm.isAchievementTracked(achievement)), 5).onClicked(e -> {
                    if (((FormCheckBox)e.from).checked) {
                        TrackedSidebarForm.addTrackedAchievement(achievement);
                    } else {
                        TrackedSidebarForm.removeTrackedAchievement(achievement);
                    }
                });
            }
            if (achievement != GlobalData.achievements().OBTAIN_ITEMS) continue;
            this.achievementList.add(new FormLocalTextButton(new LocalMessage("stats", "show_item_list"), 55, 0, this.achievementList.contentBox.getWidth() - 75, FormInputSize.SIZE_20, ButtonColor.BASE), 5).onClicked(e -> {
                this.selectorForm.playerStatsOption.makeCurrent();
                this.selectorForm.playerStats.switcher.makeCurrent(this.selectorForm.playerStats.itemsObtained);
                this.selectorForm.playerStats.subMenuBackPressed = () -> this.selectorForm.achievementsOption.makeCurrent();
            });
        }
        this.achievementList.fit(5, 5, 5, 5);
    }

    public void removeDisabledTip() {
        if (this.disabledContent != null) {
            this.removeComponent(this.disabledContent);
        }
        this.progressBar.setPosition(0, 0);
        this.achievementsShowCompleted.setPosition(10, this.getHeight() - 20);
        this.achievementList.contentBox.setPosition(0, 20);
        this.achievementList.contentBox.setHeight(this.getHeight() - 20);
    }

    public void disableAchievements() {
        if (this.disabledContent != null) {
            this.removeComponent(this.disabledContent);
        }
        this.disabledContent = this.addComponent(new DisabledPreForm(this.getWidth(), new LocalMessage("ui", "achdisabled"), new LocalMessage("ui", "achdisabledhelp")));
        this.progressBar.setPosition(0, this.disabledContent.getHeight());
        this.achievementsShowCompleted.setPosition(10, this.getHeight() - 20);
        this.achievementList.contentBox.setPosition(0, 20 + this.disabledContent.getHeight());
        this.achievementList.contentBox.setHeight(this.getHeight() - 50 - this.disabledContent.getHeight());
    }

    @Override
    public void onSelected() {
        this.updateList();
    }
}

