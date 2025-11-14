/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.achievements.AchievementProviderInterface;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class BoolAchievement
extends Achievement {
    protected boolean completed = false;

    public BoolAchievement(String stringID, GameMessage name, GameMessage description) {
        super(stringID, name, description);
    }

    public BoolAchievement(String stringID, String nameLocalKey, String descriptionLocalKey) {
        super(stringID, nameLocalKey, descriptionLocalKey);
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public void runStatsUpdate(ServerClient client) {
    }

    public void markCompleted(ServerClient client) {
        if (!this.isCompleted()) {
            if (!client.getServer().world.settings.achievementsEnabled()) {
                return;
            }
            this.completed = true;
            this.updateTimeCompleted();
            client.getServer().network.sendToAllClients(new PacketAchievementUpdate(client, this, true));
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
        String s = Localization.translate("achievement", this.isCompleted() ? "complete" : "incomplete");
        Color col = this.isCompleted() ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
        FontOptions options = new FontOptions(16).outline(outlined).color(col);
        int sWidth = FontManager.bit.getWidthCeil(s, options);
        int drawX = x + width - sWidth - 10;
        FontManager.bit.drawString(drawX, y, s, options);
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
        writer.putNextBoolean(this.completed);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.completed = reader.getNextBoolean();
    }
}

