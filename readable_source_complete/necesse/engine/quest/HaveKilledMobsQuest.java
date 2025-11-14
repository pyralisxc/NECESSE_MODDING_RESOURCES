/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.gameFont.FontOptions;

public abstract class HaveKilledMobsQuest
extends Quest {
    protected ArrayList<HaveKilledObjective> objectives = new ArrayList();

    public HaveKilledMobsQuest() {
    }

    public HaveKilledMobsQuest(HaveKilledObjective firstObjective, HaveKilledObjective ... extraObjectives) {
        this.objectives = new ArrayList();
        this.objectives.add(firstObjective);
        this.objectives.addAll(Arrays.asList(extraObjectives));
    }

    public HaveKilledMobsQuest(String mobStringID, int mobsToKill) {
        this(new HaveKilledObjective(mobStringID, mobsToKill), new HaveKilledObjective[0]);
    }

    public HaveKilledMobsQuest(int mobID, int mobsToKill) {
        this(new HaveKilledObjective(mobID, mobsToKill), new HaveKilledObjective[0]);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        for (HaveKilledObjective objective : this.objectives) {
            SaveData objectiveData = new SaveData("objective");
            objectiveData.addUnsafeString("mobStringID", MobRegistry.getMobStringID(objective.mobID));
            objectiveData.addInt("mobsToKill", objective.mobsToKill);
            save.addSaveData(objectiveData);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.objectives.clear();
        String mobStringID = save.getUnsafeString("mobStringID", null, false);
        if (mobStringID != null) {
            int mobID = MobRegistry.getMobID(mobStringID);
            if (mobID == -1) {
                throw new IllegalStateException("Could not find mob with stringID " + mobStringID);
            }
            int mobsToKill = save.getInt("mobsToKill", 1);
            this.objectives.add(new HaveKilledObjective(mobID, mobsToKill));
        }
        for (LoadData objectiveData : save.getLoadDataByName("objective")) {
            String mobStringID2 = objectiveData.getUnsafeString("mobStringID", null, false);
            if (mobStringID2 == null) continue;
            int mobID = MobRegistry.getMobID(mobStringID2);
            if (mobID == -1) {
                throw new IllegalStateException("Could not find mob with stringID " + mobStringID2);
            }
            int mobsToKill = objectiveData.getInt("mobsToKill", 1);
            this.objectives.add(new HaveKilledObjective(mobID, mobsToKill));
        }
        if (this.objectives.isEmpty()) {
            throw new IllegalStateException("Could not find any objectives");
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.objectives.size());
        for (HaveKilledObjective objective : this.objectives) {
            writer.putNextInt(objective.mobID);
            writer.putNextShortUnsigned(objective.mobsToKill);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.objectives.clear();
        int total = reader.getNextShortUnsigned();
        for (int i = 0; i < total; ++i) {
            int mobID = reader.getNextInt();
            int mobsToKill = reader.getNextShortUnsigned();
            this.objectives.add(new HaveKilledObjective(mobID, mobsToKill));
        }
    }

    @Override
    public void tick(ServerClient client) {
    }

    @Override
    public GameMessage getDescription() {
        return null;
    }

    public GameMessage getCompleteHint() {
        return null;
    }

    @Override
    public boolean canComplete(NetworkClient client) {
        for (HaveKilledObjective objective : this.objectives) {
            if (objective.getCurrentKills(client) >= objective.mobsToKill) continue;
            return false;
        }
        return true;
    }

    @Override
    public DrawOptionsBox getProgressDrawBox(NetworkClient client, final int x, final int y, final int width, Color textColor, boolean outlined) {
        GameMessage completeHint;
        final DrawOptionsList drawOptions = new DrawOptionsList();
        int currentHeight = 0;
        boolean canComplete = client != null && this.canComplete(client);
        GameMessage gameMessage = completeHint = canComplete ? this.getCompleteHint() : null;
        if (completeHint != null) {
            FontOptions completeHintFontOptions = new FontOptions(12).outline(outlined);
            if (textColor != null) {
                completeHintFontOptions.color(textColor);
            }
            ArrayList<String> lines = GameUtils.breakString(completeHint.translate(), completeHintFontOptions, width);
            for (String line : lines) {
                drawOptions.add(new StringDrawOptions(completeHintFontOptions, line).pos(x, y + currentHeight + 2));
                currentHeight += 12;
            }
        }
        for (HaveKilledObjective objective : this.objectives) {
            String hint;
            String str = Localization.translate("quests", "killmob", "mob", MobRegistry.getDisplayName(objective.mobID));
            FontOptions fontOptions = new FontOptions(16).outline(outlined);
            if (textColor != null) {
                fontOptions.color(textColor);
            }
            drawOptions.add(new StringDrawOptions(fontOptions, str).pos(x, y + currentHeight));
            currentHeight += 16;
            String string = hint = canComplete ? null : MobRegistry.getKillHint(objective.mobID);
            if (hint != null) {
                FontOptions killHintFontOptions = new FontOptions(12).outline(outlined);
                if (textColor != null) {
                    killHintFontOptions.color(textColor);
                }
                ArrayList<String> lines = GameUtils.breakString(hint, killHintFontOptions, width);
                for (String line : lines) {
                    drawOptions.add(new StringDrawOptions(killHintFontOptions, line).pos(x, y + currentHeight + 2));
                    currentHeight += 12;
                }
                currentHeight += 2;
            }
            int currentKills = client == null ? 0 : objective.getCurrentKills(client);
            float progress = objective.mobsToKill == 0 ? 1.0f : (float)currentKills / (float)objective.mobsToKill;
            Color col = progress == 1.0f ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
            FontOptions progressFontOptions = new FontOptions(16).outline(outlined).color(col);
            DrawOptionsBox progressBox = Achievement.getProgressbarTextDrawBox(x, y + currentHeight, width, 5, progress, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, currentKills + "/" + objective.mobsToKill, progressFontOptions);
            drawOptions.add(progressBox);
            currentHeight += progressBox.getBoundingBox().height;
        }
        final int finalHeight = currentHeight;
        return new DrawOptionsBox(){

            @Override
            public Rectangle getBoundingBox() {
                return new Rectangle(x, y, width, finalHeight);
            }

            @Override
            public void draw() {
                drawOptions.draw();
            }
        };
    }

    public static class HaveKilledObjective {
        public int mobID;
        public int mobsToKill;

        public HaveKilledObjective(int mobID, int mobsToKill) {
            if (mobID < 0) {
                throw new IllegalArgumentException("mobID cannot be negative");
            }
            if (mobsToKill < 1) {
                throw new IllegalArgumentException("mobsToKill cannot be less than 1");
            }
            this.mobID = mobID;
            this.mobsToKill = mobsToKill;
        }

        public HaveKilledObjective(String mobStringID, int mobsToKill) {
            this(MobRegistry.getMobID(mobStringID), mobsToKill);
        }

        public int getCurrentKills(NetworkClient client) {
            int currentKills = 0;
            if (client.isServer()) {
                currentKills = client.getServerClient().characterStats().mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
            } else if (client.isClient()) {
                currentKills = client.getClientClient().getClient().characterStats.mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
            }
            return Math.min(currentKills, this.mobsToKill);
        }
    }
}

