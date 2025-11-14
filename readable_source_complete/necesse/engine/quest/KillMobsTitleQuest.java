/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.quest.KillMobsQuest;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.fairType.FairType;

public class KillMobsTitleQuest
extends KillMobsQuest {
    protected GameMessage title;

    public KillMobsTitleQuest() {
    }

    public KillMobsTitleQuest(GameMessage title, KillMobsQuest.KillObjective firstObjective, KillMobsQuest.KillObjective ... extraObjectives) {
        super(firstObjective, extraObjectives);
        this.title = title;
    }

    public KillMobsTitleQuest(GameMessage title, String mobStringID, int mobsToKill) {
        super(mobStringID, mobsToKill);
        this.title = title;
    }

    public KillMobsTitleQuest(int mobID, int mobsToKill, GameMessage title) {
        super(mobID, mobsToKill);
        this.title = title;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.title != null) {
            save.addSaveData(this.title.getSaveData("title"));
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData title = save.getFirstLoadDataByName("title");
        if (title != null) {
            try {
                this.title = GameMessage.loadSave(title);
            }
            catch (Exception e) {
                GameLog.warn.println("Could not load kill mobs quest title");
                this.title = new LocalMessage("quests", "genericquest");
            }
        } else {
            this.title = new LocalMessage("quests", "genericquest");
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.title != null);
        if (this.title != null) {
            writer.putNextContentPacket(this.title.getContentPacket());
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        if (reader.getNextBoolean()) {
            this.title = GameMessage.fromContentPacket(reader.getNextContentPacket());
        }
    }

    @Override
    public GameMessage getTitle() {
        if (this.title == null) {
            return new LocalMessage("quests", "genericquest");
        }
        return this.title;
    }

    @Override
    public FairType getRewardType(NetworkClient client, boolean outlined) {
        return null;
    }

    @Override
    public FairType getHandInType(NetworkClient client, boolean outlined) {
        return null;
    }
}

