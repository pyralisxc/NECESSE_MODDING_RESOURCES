/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEventAction;
import necesse.entity.levelEvent.LevelEvent;

public abstract class LevelEventAction {
    private LevelEvent event;
    private int id = -1;

    protected void onRegister(LevelEvent event, int id) {
        if (this.event != null) {
            throw new IllegalStateException("Cannot register same action twice");
        }
        this.event = event;
        this.id = id;
    }

    public LevelEvent getEvent() {
        return this.event;
    }

    public abstract void executePacket(PacketReader var1);

    protected void runAndSendAction(Packet content) {
        if (this.event != null) {
            if (this.event.isServer()) {
                this.executePacket(new PacketReader(content));
                this.event.level.getServer().network.sendToClientsWithEntity(new PacketLevelEventAction(this.event, this.id, content), this.event);
            } else if (!this.event.level.isClient()) {
                this.executePacket(new PacketReader(content));
            } else {
                System.err.println("Cannot send level event actions from client. Only server handles when level event actions are ran");
            }
        } else {
            System.err.println("Cannot run level event action that hasn't been registered");
        }
    }
}

