/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class EnumCustomAction<T extends Enum<T>>
extends ContainerCustomAction {
    protected final Class<T> enumClass;

    public EnumCustomAction(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public void runAndSend(T value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextShortUnsigned(((Enum)value).ordinal());
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        Enum[] constants;
        int ordinal = reader.getNextShortUnsigned();
        if (ordinal >= (constants = (Enum[])this.enumClass.getEnumConstants()).length) {
            throw new IllegalStateException("Could not find enum with ordinal " + ordinal + " from " + this.enumClass.getSimpleName());
        }
        this.run(constants[ordinal]);
    }

    protected abstract void run(T var1);
}

