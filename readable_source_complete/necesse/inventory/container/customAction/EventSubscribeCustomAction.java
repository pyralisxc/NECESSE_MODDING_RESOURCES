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

public abstract class EventSubscribeCustomAction<T>
extends ContainerCustomAction {
    protected HashSet<Integer> activeIDs = new HashSet();

    public int subscribe(T data) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        int uniqueID = GameRandom.globalRandom.nextInt();
        writer.putNextInt(uniqueID);
        writer.putNextBoolean(true);
        this.writeData(writer, data);
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
            this.activeIDs.add(subscriptionID);
        } else {
            this.activeIDs.remove(subscriptionID);
        }
        if (active) {
            T data = this.readData(reader);
            this.onSubscribed(() -> this.isActive(subscriptionID), data);
        }
    }

    public boolean isActive(int subscriptionID) {
        return this.activeIDs.contains(subscriptionID);
    }

    public abstract void writeData(PacketWriter var1, T var2);

    public abstract T readData(PacketReader var1);

    public abstract void onSubscribed(BooleanSupplier var1, T var2);
}

