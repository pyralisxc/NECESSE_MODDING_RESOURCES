/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Settings;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.quest.Quest;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;

public class QuestManager {
    private final ArrayList<Quest> quests = new ArrayList();
    private Client client;
    private Server server;
    private boolean dirty;

    public QuestManager(Client client) {
        this.client = client;
    }

    public QuestManager(Server server) {
        this.server = server;
    }

    public void addQuest(Quest quest, boolean isNew) {
        if (this.quests.contains(quest)) {
            return;
        }
        this.removeQuest(quest.getUniqueID());
        this.quests.add(quest);
        quest.init(this);
        if (this.server != null) {
            this.server.network.sendPacket((Packet)new PacketQuest(quest, isNew), c -> quest.isActiveFor(c.authentication));
        } else if (this.client != null && isNew && Settings.trackNewQuests.get().booleanValue()) {
            this.client.trackedQuests.add(quest.getUniqueID());
            TrackedSidebarForm.updateTrackedList();
        }
    }

    public Quest getQuest(int uniqueID) {
        return this.quests.stream().filter(q -> q.getUniqueID() == uniqueID).findFirst().orElse(null);
    }

    public boolean removeQuest(int uniqueID) {
        boolean changed = false;
        Iterator<Quest> it = this.quests.iterator();
        while (it.hasNext()) {
            Quest quest = it.next();
            if (quest.getUniqueID() != uniqueID) continue;
            it.remove();
            quest.remove();
            if (this.server != null) {
                this.server.network.sendToAllClients(new PacketQuestRemove(uniqueID));
            } else if (this.client != null) {
                this.client.trackedQuests.remove(quest.getUniqueID());
                TrackedSidebarForm.removeCachedTrackedQuest(this.client, quest.getUniqueID());
            }
            changed = true;
        }
        return changed;
    }

    public boolean removeQuest(Quest quest) {
        boolean removed;
        if (!quest.isRemoved()) {
            quest.remove();
        }
        if ((removed = this.quests.remove(quest)) && this.server != null) {
            this.server.network.sendPacket((Packet)new PacketQuestRemove(quest), c -> quest.isActiveFor(c.authentication));
        }
        return removed;
    }

    public void removeAll() {
        while (!this.quests.isEmpty()) {
            this.removeQuest(this.quests.get(0));
        }
    }

    public void clearQuests() {
        this.quests.clear();
    }

    public Iterable<Quest> getQuests() {
        return this.quests;
    }

    public int getTotalQuests() {
        return this.quests.size();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void cleanAll() {
        this.quests.forEach(Quest::clean);
        this.dirty = false;
    }
}

