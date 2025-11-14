/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.GameCache;
import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormAchievementTrackedComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormQuestTrackedComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.PauseMenuForm;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class TrackedSidebarForm
extends SidebarForm {
    private static final LinkedList<TrackedSidebarForm> forms = new LinkedList();
    private static final LinkedList<Integer> trackedQuests = new LinkedList();
    private static final LinkedList<Achievement> trackedAchievements = new LinkedList();
    private final MainGameFormManager mainFormManager;
    private Client client;
    private final LinkedList<Quest> quests = new LinkedList();

    @Deprecated
    public static void removeTrackedQuest(Client client, Quest quest) {
        TrackedSidebarForm.removeTrackedQuest(client, quest.getUniqueID());
    }

    @Deprecated
    public static void removeTrackedQuest(Client client, int questUniqueID) {
        trackedQuests.remove((Object)questUniqueID);
        TrackedSidebarForm.updateTrackedList();
        TrackedSidebarForm.saveTrackedQuests(client);
    }

    public static boolean isCachedQuestTracked(Quest quest) {
        return trackedQuests.contains(quest.getUniqueID());
    }

    public static void removeCachedTrackedQuest(Client client, int questUniqueID) {
        trackedQuests.remove((Object)questUniqueID);
        TrackedSidebarForm.saveTrackedQuests(client);
    }

    public static void addTrackedAchievement(Achievement achievement) {
        if (trackedAchievements.contains(achievement)) {
            return;
        }
        trackedAchievements.add(achievement);
        TrackedSidebarForm.updateTrackedList();
        TrackedSidebarForm.saveTrackedAchievements();
    }

    public static void removeTrackedAchievement(Achievement achievement) {
        trackedAchievements.remove(achievement);
        TrackedSidebarForm.updateTrackedList();
        TrackedSidebarForm.saveTrackedAchievements();
    }

    public static boolean isAchievementTracked(Achievement achievement) {
        return trackedAchievements.contains(achievement);
    }

    public static void loadTrackedQuests(Client client) {
        trackedQuests.clear();
        LoadData questsSave = GameCache.getSave(client.loading.getClientCachePath("TrackedQuests"));
        if (questsSave == null) {
            return;
        }
        for (int questID : questsSave.getIntArray("quests", new int[0])) {
            trackedQuests.add(questID);
        }
        TrackedSidebarForm.updateTrackedList();
    }

    private static void saveTrackedQuests(Client client) {
        trackedQuests.removeIf(i -> client.quests.getQuest((int)i) == null);
        SaveData questsSave = new SaveData("TrackedQuests");
        questsSave.addIntArray("quests", trackedQuests.stream().mapToInt(i -> i).toArray());
        GameCache.cacheSave(questsSave, client.loading.getClientCachePath("TrackedQuests"));
    }

    public static void loadTrackedAchievements() {
        LoadData achievementsSave = GameCache.getSave("trackedachievements");
        if (achievementsSave == null) {
            return;
        }
        for (String achievementStringID : achievementsSave.getStringArray("achievements", new String[0])) {
            GlobalData.achievements().getAchievements().stream().filter(a -> a.stringID.equals(achievementStringID)).findFirst().ifPresent(a -> trackedAchievements.add((Achievement)a));
        }
    }

    private static void saveTrackedAchievements() {
        SaveData achievementsSave = new SaveData("TrackedAchievements");
        achievementsSave.addStringArray("achievements", (String[])trackedAchievements.stream().map(a -> a.stringID).toArray(String[]::new));
        GameCache.cacheSave(achievementsSave, "trackedachievements");
    }

    public static void updateTrackedList() {
        forms.forEach(f -> f.updateList(true));
    }

    public TrackedSidebarForm(MainGameFormManager mainFormManager) {
        super("trackedquests", mainFormManager.getSidebarWidth(), 0);
        this.mainFormManager = mainFormManager;
        this.drawBase = false;
    }

    @Override
    public void onSidebarUpdate(int x, int y) {
        super.onSidebarUpdate(x, y);
        this.updateList(false);
    }

    public void updateList(boolean updateSidebar) {
        boolean addDivider;
        Quest quest;
        this.quests.clear();
        for (Integer trackedQuest : trackedQuests.toArray(new Integer[0])) {
            quest = this.client.quests.getQuest(trackedQuest);
            if (quest == null) {
                TrackedSidebarForm.removeCachedTrackedQuest(this.client, trackedQuest);
                return;
            }
            this.quests.add(quest);
        }
        for (Integer trackedQuest : this.client.trackedQuests.toArray(new Integer[0])) {
            quest = this.client.quests.getQuest(trackedQuest);
            if (quest == null || this.quests.contains(quest)) {
                TrackedSidebarForm.removeCachedTrackedQuest(this.client, trackedQuest);
                return;
            }
            this.quests.add(quest);
        }
        this.clearComponents();
        this.setWidth(this.mainFormManager.getSidebarWidth());
        FormFlow flow = new FormFlow(0);
        if (!trackedAchievements.isEmpty()) {
            this.addComponent(new FormLocalLabel("ui", "achievements", new FontOptions(20).outline().color(new Color(200, 200, 200)), -1, 5, flow.next(20)));
            addDivider = false;
            for (Achievement achievement : trackedAchievements) {
                if (addDivider) {
                    flow.next(10);
                }
                this.addComponent(flow.nextY(new FormAchievementTrackedComponent(0, 0, this.getWidth(), Math.min(this.getWidth(), 200), achievement)));
                if (achievement == GlobalData.achievements().OBTAIN_ITEMS) {
                    this.addComponent(new FormLocalTextButton(new LocalMessage("stats", "show_item_list"), 0, flow.next(24), Math.min(this.getWidth(), 200), FormInputSize.SIZE_20, ButtonColor.BASE), 5).onClicked(e -> {
                        State state = GlobalData.getCurrentState();
                        if (state instanceof MainGame) {
                            state.setRunning(false);
                            PauseMenuForm pauseMenu = ((MainGame)state).formManager.pauseMenu;
                            pauseMenu.makeCurrent(pauseMenu.stats);
                            pauseMenu.stats.reset();
                            pauseMenu.stats.playerStatsOption.makeCurrent();
                            pauseMenu.stats.playerStats.switcher.makeCurrent(pauseMenu.stats.playerStats.itemsObtained);
                            pauseMenu.stats.playerStats.subMenuBackPressed = () -> pauseMenu.stats.achievementsOption.makeCurrent();
                        }
                    });
                }
                addDivider = true;
            }
            flow.next(5);
        }
        if (!this.quests.isEmpty()) {
            flow.next(5);
            this.addComponent(new FormLocalLabel("ui", "quests", new FontOptions(20).outline().color(new Color(200, 200, 200)), -1, 5, flow.next(20)));
            addDivider = false;
            for (Quest quest2 : this.quests) {
                if (addDivider) {
                    flow.next(10);
                }
                this.addComponent(flow.nextY(new FormQuestTrackedComponent(10, 0, Math.min(this.getWidth(), 300), Math.min(this.getWidth(), 200), this.client.getClient(), quest2)));
                addDivider = true;
            }
            flow.next(5);
        }
        this.setHeight(flow.next());
        if (updateSidebar) {
            this.mainFormManager.fixSidebar();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.quests.stream().anyMatch(Quest::isRemoved)) {
            this.updateList(true);
        }
        if (trackedAchievements.removeIf(Achievement::isCompleted)) {
            TrackedSidebarForm.saveTrackedAchievements();
            this.updateList(true);
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return super.getHitboxes();
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return false;
    }

    @Override
    public boolean isValid(Client client) {
        return true;
    }

    @Override
    public void onAdded(Client client) {
        super.onAdded(client);
        this.client = client;
        this.updateList(true);
        forms.add(this);
    }

    @Override
    public void onRemoved(Client client) {
        super.onRemoved(client);
        forms.remove(this);
    }
}

