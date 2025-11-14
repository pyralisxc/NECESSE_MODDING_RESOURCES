/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ExpeditionMissionRegistry;

public class ContainerExpedition {
    public final SettlerExpedition expedition;
    public final boolean available;
    public final float successChance;
    public final int price;

    public ContainerExpedition(SettlerExpedition expedition, boolean available, float successChance, int price) {
        this.expedition = expedition;
        this.available = available;
        this.successChance = successChance;
        this.price = price;
    }

    public ContainerExpedition(SettlerExpedition expedition) {
        this(expedition, false, 0.0f, 0);
    }

    public ContainerExpedition(PacketReader reader) {
        this.expedition = ExpeditionMissionRegistry.getExpedition(reader.getNextShortUnsigned());
        this.available = reader.getNextBoolean();
        if (this.available) {
            this.successChance = reader.getNextFloat();
            this.price = reader.getNextInt();
        } else {
            this.successChance = 0.0f;
            this.price = 0;
        }
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.expedition.getID());
        writer.putNextBoolean(this.available);
        if (this.available) {
            writer.putNextFloat(this.successChance);
            writer.putNextInt(this.price);
        }
    }
}

