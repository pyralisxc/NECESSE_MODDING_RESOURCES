/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkFieldRegistry;

public abstract class NetworkField<T> {
    private NetworkFieldRegistry registry;
    private int id = -1;
    private T value;

    public NetworkField(T startValue) {
        this.value = startValue;
    }

    protected void onRegister(NetworkFieldRegistry registry, int id) {
        if (this.registry != null) {
            throw new IllegalStateException("Cannot register same network field twice");
        }
        this.registry = registry;
        this.id = id;
    }

    public void set(T value) {
        if (!this.isSameValue(this.value, value)) {
            this.value = value;
            this.onChanged(value);
            this.markDirty();
        }
    }

    public void onChanged(T value) {
    }

    public void markDirty() {
        if (this.registry != null) {
            this.registry.markDirty(this.id);
        }
    }

    protected boolean isSameValue(T o1, T o2) {
        return Objects.equals(o1, o2);
    }

    public T get() {
        return this.value;
    }

    public final void writeUpdatePacket(PacketWriter writer) {
        this.writePacket(this.value, writer);
    }

    public final void readUpdatePacket(PacketReader reader) {
        this.value = this.readPacket(reader);
        this.onChanged(this.value);
    }

    protected abstract void writePacket(T var1, PacketWriter var2);

    protected abstract T readPacket(PacketReader var1);
}

