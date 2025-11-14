/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.HaveKilledMobsSettlementQuest;
import necesse.engine.quest.KillMobsSettlementQuest;
import necesse.engine.quest.KillMobsTitleQuest;
import necesse.engine.quest.Quest;
import necesse.engine.registries.EmptyConstructorGameRegistry;

public class QuestRegistry
extends EmptyConstructorGameRegistry<Quest> {
    public static final QuestRegistry instance = new QuestRegistry();

    private QuestRegistry() {
        super("Quest", 32762);
    }

    @Override
    public void registerCore() {
        QuestRegistry.registerQuest("killmobstitle", KillMobsTitleQuest.class);
        QuestRegistry.registerQuest("killmobssettlement", KillMobsSettlementQuest.class);
        QuestRegistry.registerQuest("deliveritemssettlement", DeliverItemsSettlementQuest.class);
        QuestRegistry.registerQuest("havekilledmobssettlement", HaveKilledMobsSettlementQuest.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static void registerQuest(String stringID, Class<? extends Quest> questClass) {
        instance.registerClass(stringID, questClass);
    }

    public static Quest getNewQuest(int id) {
        return (Quest)instance.getNewInstance(id);
    }

    public static int getQuestID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getQuestID(Class<? extends Quest> clazz) {
        return instance.getElementID(clazz);
    }

    public static Quest getNewQuest(String stringID) {
        return (Quest)instance.getNewInstance(stringID);
    }
}

