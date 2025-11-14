/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.achievements.AchievementProviderInterface;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class IntAchievement
extends Achievement {
    protected final int min;
    protected final int max;
    protected final DrawMode drawMode;
    protected boolean completed = false;
    protected final Supplier<Integer> progressGetter;

    public IntAchievement(String stringID, GameMessage name, GameMessage description, Supplier<Integer> progressGetter, int min, int max, DrawMode drawMode) {
        super(stringID, name, description);
        if (min >= max) {
            throw new IllegalArgumentException("Achievement min must be lower than max");
        }
        this.min = min;
        this.max = max;
        this.drawMode = drawMode;
        this.progressGetter = progressGetter;
    }

    public IntAchievement(String stringID, String nameLocalKey, String descriptionLocalKey, Supplier<Integer> progressGetter, int min, int max, DrawMode drawMode) {
        this(stringID, new LocalMessage("achievement", nameLocalKey), new LocalMessage("achievement", descriptionLocalKey), progressGetter, min, max, drawMode);
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public void runStatsUpdate(ServerClient client) {
        if (!this.isCompleted()) {
            if (!client.getServer().world.settings.achievementsEnabled()) {
                return;
            }
            boolean bl = this.completed = this.progressGetter.get() >= this.max;
            if (this.isCompleted()) {
                this.updateTimeCompleted();
                System.out.println("Completed " + this.stringID + " achievement");
                client.getServer().network.sendToAllClients(new PacketAchievementUpdate(client, this, true));
            }
        }
    }

    @Override
    public void loadFromPlatform(AchievementProviderInterface achievementProvider) {
        AchievementProviderInterface.AchievementState ach = achievementProvider.getAchievementState(this.stringID);
        if (ach != null) {
            this.completed = ach.completed;
            if (ach.completedTime != null) {
                this.completedTime = ach.completedTime;
            }
        }
    }

    @Override
    public void drawProgress(int x, int y, int width, boolean outlined) {
        int absMax = this.max - this.min;
        int complete = this.isCompleted() ? absMax : Math.max(0, this.progressGetter.get() - this.min);
        float perc = (float)complete / (float)absMax;
        String progressString = "N/A";
        boolean drawBar = false;
        switch (this.drawMode) {
            case NORMAL: {
                progressString = complete + "/" + absMax;
                drawBar = true;
                break;
            }
            case PERCENT: {
                progressString = (int)(perc * 100.0f) + "%";
                drawBar = true;
                break;
            }
            case BOOL: {
                progressString = Localization.translate("achievement", this.isCompleted() ? "complete" : "incomplete");
                drawBar = false;
            }
        }
        if (drawBar) {
            Color col = perc == 1.0f ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
            FontOptions options = new FontOptions(16).outline(outlined).color(col);
            Achievement.drawProgressbarText(x, y, width, 5, perc, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, progressString, options);
        } else {
            Color col = this.isCompleted() ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
            FontOptions options = new FontOptions(16).outline(outlined).color(col);
            int sWidth = FontManager.bit.getWidthCeil(progressString, options);
            int drawX = x + width - sWidth - 10;
            FontManager.bit.drawString(drawX, y, progressString, options);
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.isCompleted()) {
            save.addInt("completed", 1);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        if (save.hasLoadDataByName("completed")) {
            this.completed = true;
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextBoolean(this.isCompleted());
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.completed = reader.getNextBoolean();
    }

    public static enum DrawMode {
        NORMAL,
        PERCENT,
        BOOL;

    }
}

