/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class QuestSave {
    public static Quest loadSave(LoadData save) {
        try {
            String type = save.getUnsafeString("stringID");
            Quest quest = QuestRegistry.getNewQuest(type);
            quest.applyLoadData(save);
            return quest;
        }
        catch (Exception e) {
            String type = save.getUnsafeString("stringID", "N/A", false);
            System.err.println("Could not load quest with stringID " + type);
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(Quest quest) {
        SaveData save = new SaveData("QUEST");
        save.addUnsafeString("stringID", quest.getStringID());
        quest.addSaveData(save);
        return save;
    }
}

