/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import java.util.ArrayList;
import java.util.HashSet;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public abstract class NetworkFieldRegistry {
    private boolean registryOpen = true;
    private final ArrayList<NetworkField<?>> fields = new ArrayList();
    private final HashSet<Integer> dirty = new HashSet();

    protected void markDirty(int id) {
        this.dirty.add(id);
    }

    public void writeSpawnPacket(PacketWriter writer) {
        for (NetworkField<?> field : this.fields) {
            field.writeUpdatePacket(writer);
        }
    }

    public void readSpawnPacket(PacketReader reader) {
        for (NetworkField<?> field : this.fields) {
            field.readUpdatePacket(reader);
        }
    }

    public void tickSync() {
        if (!this.dirty.isEmpty()) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextShortUnsigned(this.dirty.size());
            for (int id : this.dirty) {
                NetworkField<?> field = this.fields.get(id);
                writer.putNextShortUnsigned(id);
                field.writeUpdatePacket(writer);
            }
            this.dirty.clear();
            this.sendUpdatePacket(content);
        }
    }

    public void readUpdatePacket(PacketReader reader) {
        int totalFields = reader.getNextShortUnsigned();
        for (int i = 0; i < totalFields; ++i) {
            int id = reader.getNextShortUnsigned();
            this.fields.get(id).readUpdatePacket(reader);
        }
    }

    public void closeRegistry() {
        this.registryOpen = false;
        this.dirty.clear();
    }

    public abstract void sendUpdatePacket(Packet var1);

    public abstract String getDebugIdentifierString();

    protected String getClosedErrorMessage() {
        return "Cannot register network fields after initialization, must be done in constructor";
    }

    public final <T extends NetworkField<?>> T registerField(T field) {
        if (!this.registryOpen) {
            throw new IllegalStateException(this.getClosedErrorMessage());
        }
        if (this.fields.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more network fields for " + this.getDebugIdentifierString());
        }
        this.fields.add(field);
        field.onRegister(this, this.fields.size() - 1);
        return field;
    }
}

