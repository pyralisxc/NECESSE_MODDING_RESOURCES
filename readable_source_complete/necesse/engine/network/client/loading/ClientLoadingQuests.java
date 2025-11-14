/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.engine.network.packet.PacketQuests;
import necesse.engine.network.packet.PacketRequestQuests;
import necesse.engine.quest.Quest;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;

public class ClientLoadingQuests
extends ClientLoadingAutoPhase {
    private boolean loaded = false;

    public ClientLoadingQuests(ClientLoading loading) {
        super(loading, false);
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new LocalMessage("loading", "connectquests");
    }

    @Override
    public void tick() {
        if (this.loaded) {
            this.markDone();
        } else {
            if (this.isWaiting()) {
                return;
            }
            this.client.network.sendPacket(new PacketRequestQuests());
            this.setWait(250);
        }
    }

    public void submitQuestsPacket(PacketQuests packet) {
        this.client.quests.clearQuests();
        this.client.trackedQuests.clear();
        packet.readQuests((quest, tracked) -> {
            this.client.quests.addQuest((Quest)quest, false);
            if (tracked.booleanValue()) {
                this.client.trackedQuests.add(quest.getUniqueID());
            } else {
                this.client.trackedQuests.remove(quest.getUniqueID());
            }
        });
        TrackedSidebarForm.updateTrackedList();
        this.loaded = true;
    }

    @Override
    public void end() {
    }
}

