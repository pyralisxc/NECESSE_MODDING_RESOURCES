/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Rectangle;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.Zoning;

public class ZoningChange {
    private Packet packet;

    private ZoningChange(Packet packet) {
        this.packet = packet;
    }

    public boolean applyTo(Zoning zone) {
        if (zone == null) {
            return false;
        }
        PacketReader reader = new PacketReader(this.packet);
        ChangeType[] types = ChangeType.values();
        int typeIndex = reader.getNextMaxValue(types.length + 1);
        if (typeIndex < 0 || typeIndex >= types.length) {
            GameLog.warn.println("Tried to apply invalid ItemCategoriesFilterChange to type index " + typeIndex);
            return false;
        }
        ChangeType type = types[typeIndex];
        switch (type) {
            case EXPAND: {
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                int width = reader.getNextInt();
                int height = reader.getNextInt();
                return zone.addRectangle(new Rectangle(x, y, width, height));
            }
            case SHRINK: {
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                int width = reader.getNextInt();
                int height = reader.getNextInt();
                return zone.removeRectangle(new Rectangle(x, y, width, height));
            }
            case INVERT: {
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                int width = reader.getNextInt();
                int height = reader.getNextInt();
                zone.invert(new Rectangle(x, y, width, height));
                return true;
            }
            case INVERT_FULL: {
                zone.invert();
                return true;
            }
            case FULL: {
                zone.readZonePacket(reader);
                return true;
            }
        }
        return false;
    }

    public void write(PacketWriter writer) {
        writer.putNextContentPacket(this.packet);
    }

    public static ZoningChange fromPacket(PacketReader reader) {
        return new ZoningChange(reader.getNextContentPacket());
    }

    public static ZoningChange expand(Rectangle rectangle) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.EXPAND.ordinal(), ChangeType.values().length + 1);
        writer.putNextInt(rectangle.x);
        writer.putNextInt(rectangle.y);
        writer.putNextInt(rectangle.width);
        writer.putNextInt(rectangle.height);
        return new ZoningChange(packet);
    }

    public static ZoningChange shrink(Rectangle rectangle) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.SHRINK.ordinal(), ChangeType.values().length + 1);
        writer.putNextInt(rectangle.x);
        writer.putNextInt(rectangle.y);
        writer.putNextInt(rectangle.width);
        writer.putNextInt(rectangle.height);
        return new ZoningChange(packet);
    }

    public static ZoningChange invert(Rectangle rectangle) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.INVERT.ordinal(), ChangeType.values().length + 1);
        writer.putNextInt(rectangle.x);
        writer.putNextInt(rectangle.y);
        writer.putNextInt(rectangle.width);
        writer.putNextInt(rectangle.height);
        return new ZoningChange(packet);
    }

    public static ZoningChange fullInvert() {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.INVERT_FULL.ordinal(), ChangeType.values().length + 1);
        return new ZoningChange(packet);
    }

    public static ZoningChange full(Zoning zone) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.FULL.ordinal(), ChangeType.values().length + 1);
        zone.writeZonePacket(writer);
        return new ZoningChange(packet);
    }

    private static enum ChangeType {
        EXPAND,
        SHRINK,
        INVERT,
        INVERT_FULL,
        FULL;

    }
}

