/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public abstract class KillMobsQuest
extends Quest {
    protected ArrayList<KillObjective> objectives = new ArrayList();
    protected GameMessage description;

    public KillMobsQuest() {
    }

    public KillMobsQuest(KillObjective firstObjective, KillObjective ... extraObjectives) {
        this.objectives = new ArrayList();
        this.objectives.add(firstObjective);
        this.objectives.addAll(Arrays.asList(extraObjectives));
    }

    public KillMobsQuest(String mobStringID, int mobsToKill) {
        this(new KillObjective(mobStringID, mobsToKill), new KillObjective[0]);
    }

    public KillMobsQuest(int mobID, int mobsToKill) {
        this(new KillObjective(mobID, mobsToKill), new KillObjective[0]);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        for (KillObjective objective : this.objectives) {
            SaveData objectiveData = new SaveData("objective");
            objectiveData.addUnsafeString("mobStringID", MobRegistry.getMobStringID(objective.mobID));
            objectiveData.addInt("mobsToKill", objective.mobsToKill);
            objectiveData.addInt("currentKills", objective.currentKills);
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
            KillObjective objective = new KillObjective(mobID, mobsToKill);
            objective.currentKills = save.getInt("currentKills", objective.currentKills);
            this.objectives.add(objective);
        }
        for (LoadData objectiveData : save.getLoadDataByName("objective")) {
            String mobStringID2 = objectiveData.getUnsafeString("mobStringID", null, false);
            if (mobStringID2 == null) continue;
            int mobID = MobRegistry.getMobID(mobStringID2);
            if (mobID == -1) {
                throw new IllegalStateException("Could not find mob with stringID " + mobStringID2);
            }
            int mobsToKill = objectiveData.getInt("mobsToKill", 1);
            KillObjective objective = new KillObjective(mobID, mobsToKill);
            objective.currentKills = objectiveData.getInt("currentKills", objective.currentKills);
            this.objectives.add(objective);
        }
        if (this.objectives.isEmpty()) {
            throw new IllegalStateException("Could not find any objectives");
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.objectives.size());
        for (KillObjective objective : this.objectives) {
            writer.putNextInt(objective.mobID);
            writer.putNextShortUnsigned(objective.mobsToKill);
        }
        super.setupSpawnPacket(writer);
        this.setupPacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        this.objectives.clear();
        int total = reader.getNextShortUnsigned();
        for (int i = 0; i < total; ++i) {
            int mobID = reader.getNextInt();
            int mobsToKill = reader.getNextShortUnsigned();
            this.objectives.add(new KillObjective(mobID, mobsToKill));
        }
        super.applySpawnPacket(reader);
        this.applyPacket(reader);
    }

    @Override
    public void setupPacket(PacketWriter writer) {
        super.setupPacket(writer);
        for (KillObjective objective : this.objectives) {
            writer.putNextShortUnsigned(objective.currentKills);
        }
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (KillObjective objective : this.objectives) {
            objective.currentKills = reader.getNextShortUnsigned();
        }
    }

    @Override
    public void tick(ServerClient client) {
        for (KillObjective objective : this.objectives) {
            objective.tick(this, client);
        }
    }

    @Override
    public GameMessage getDescription() {
        return null;
    }

    @Override
    public boolean canComplete(NetworkClient client) {
        for (KillObjective objective : this.objectives) {
            if (objective.currentKills >= objective.mobsToKill) continue;
            return false;
        }
        return true;
    }

    public void addExtraKills(int mobID, int kills) {
        for (KillObjective objective : this.objectives) {
            if (mobID != -1 && objective.mobID != mobID) continue;
            objective.addExtraKills(this, kills);
        }
    }

    @Override
    public DrawOptionsBox getProgressDrawBox(NetworkClient client, final int x, final int y, final int width, Color textColor, boolean outlined) {
        final DrawOptionsList drawOptions = new DrawOptionsList();
        int currentHeight = 0;
        for (KillObjective objective : this.objectives) {
            String str = Localization.translate("quests", "killmob", "mob", MobRegistry.getDisplayName(objective.mobID));
            FontOptions fontOptions = new FontOptions(16).outline(outlined);
            if (textColor != null) {
                fontOptions.color(textColor);
            }
            drawOptions.add(new StringDrawOptions(fontOptions, str).pos(x, y + currentHeight));
            currentHeight += 16;
            String killHint = MobRegistry.getKillHint(objective.mobID);
            if (killHint != null) {
                FontOptions killHintFontOptions = new FontOptions(12).outline(outlined);
                if (textColor != null) {
                    killHintFontOptions.color(textColor);
                }
                ArrayList<String> lines = GameUtils.breakString(killHint, killHintFontOptions, width);
                for (String line : lines) {
                    drawOptions.add(new StringDrawOptions(killHintFontOptions, line).pos(x, y + currentHeight + 2));
                    currentHeight += 12;
                }
                currentHeight += 2;
            }
            float progress = objective.mobsToKill == 0 ? 1.0f : (float)objective.currentKills / (float)objective.mobsToKill;
            Color col = progress == 1.0f ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
            FontOptions progressFontOptions = new FontOptions(16).outline(outlined).color(col);
            DrawOptionsBox progressBox = Achievement.getProgressbarTextDrawBox(x, y + currentHeight, width, 5, progress, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, objective.currentKills + "/" + objective.mobsToKill, progressFontOptions);
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

    public static class KillObjective {
        public int mobID;
        public int mobsToKill;
        protected int currentKills;
        protected HashMap<Long, Integer> prevClientKills = new HashMap();

        public KillObjective(int mobID, int mobsToKill) {
            if (mobID < 0) {
                throw new IllegalArgumentException("mobID cannot be negative");
            }
            if (mobsToKill < 1) {
                throw new IllegalArgumentException("mobsToKill cannot be less than 1");
            }
            this.mobID = mobID;
            this.mobsToKill = mobsToKill;
        }

        public KillObjective(String mobStringID, int mobsToKill) {
            this(MobRegistry.getMobID(mobStringID), mobsToKill);
        }

        public void tick(KillMobsQuest quest, ServerClient client) {
            int newKills;
            int clientKills = client.characterStats().mob_kills.getKills(MobRegistry.getMobStringID(this.mobID));
            int lastKills = this.prevClientKills.getOrDefault(client.authentication, -1);
            if (lastKills != -1 && (newKills = Math.max(0, clientKills - lastKills)) > 0 && this.currentKills < this.mobsToKill) {
                this.addExtraKills(quest, newKills);
            }
            this.prevClientKills.put(client.authentication, clientKills);
        }

        public void addExtraKills(KillMobsQuest quest, int kills) {
            this.currentKills = Math.min(this.mobsToKill, this.currentKills + kills);
            quest.markDirty();
        }
    }
}

