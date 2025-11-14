/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import java.util.HashSet;
import java.util.function.BooleanSupplier;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class EventSubscribeEmptyCustomAction
extends ContainerCustomAction {
    protected HashSet<Integer> active = new HashSet();

    public int subscribe() {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        int uniqueID = GameRandom.globalRandom.nextInt();
        writer.putNextInt(uniqueID);
        writer.putNextBoolean(true);
        this.runAndSendAction(content);
        return uniqueID;
    }

    public void unsubscribe(int subscriptionID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(subscriptionID);
        writer.putNextBoolean(false);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int subscriptionID = reader.getNextInt();
        boolean active = reader.getNextBoolean();
        if (active) {
            this.active.add(subscriptionID);
        } else {
            this.active.remove(subscriptionID);
        }
        if (active) {
            this.onSubscribed(() -> this.active.contains(subscriptionID));
        }
    }

    public abstract void onSubscribed(BooleanSupplier var1);
}

