/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class PacketOpenContainer
extends Packet {
    public final int containerID;
    public final int uniqueSeed;
    public final Packet content;
    public final Object serverObject;

    public PacketOpenContainer(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.containerID = reader.getNextShortUnsigned();
        this.uniqueSeed = reader.getNextInt();
        this.content = reader.getNextContentPacket();
        this.serverObject = null;
    }

    public PacketOpenContainer(int containerID, Packet content, Object serverObject) {
        this.containerID = containerID;
        this.uniqueSeed = GameRandom.globalRandom.nextInt();
        this.content = content;
        this.serverObject = serverObject;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(containerID);
        writer.putNextInt(this.uniqueSeed);
        writer.putNextContentPacket(content);
    }

    public PacketOpenContainer(int containerID, Packet content) {
        this(containerID, content, null);
    }

    public PacketOpenContainer(int containerID) {
        this(containerID, new Packet());
    }

    public static PacketOpenContainer SettlementObjectEntity(int containerID, ServerSettlementData settlement, ObjectEntity oe, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet extraContent = new Packet();
        PacketWriter writer = new PacketWriter(extraContent);
        if (settlement != null) {
            writer.putNextBoolean(true);
            new SettlementDataEvent(settlement).write(writer);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(oe.tileX);
        writer.putNextInt(oe.tileY);
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, extraContent, serverObject);
    }

    public static PacketOpenContainer SettlementObjectEntity(int containerID, ServerSettlementData settlement, ObjectEntity oe, Packet content) {
        return PacketOpenContainer.SettlementObjectEntity(containerID, settlement, oe, content, null);
    }

    public static PacketOpenContainer SettlementObjectEntity(int containerID, ServerSettlementData settlement, ObjectEntity oe) {
        return PacketOpenContainer.SettlementObjectEntity(containerID, settlement, oe, null);
    }

    public static PacketOpenContainer SettlementMob(int containerID, ServerSettlementData settlement, Mob mob, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet extraContent = new Packet();
        PacketWriter writer = new PacketWriter(extraContent);
        if (settlement != null) {
            writer.putNextBoolean(true);
            new SettlementDataEvent(settlement).write(writer);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(mob.getUniqueID());
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, extraContent, serverObject);
    }

    public static PacketOpenContainer SettlementMob(int containerID, ServerSettlementData settlement, Mob mob, Packet content) {
        return PacketOpenContainer.SettlementMob(containerID, settlement, mob, content, null);
    }

    public static PacketOpenContainer SettlementMob(int containerID, ServerSettlementData settlement, Mob mob) {
        return PacketOpenContainer.SettlementMob(containerID, settlement, mob, null);
    }

    public static PacketOpenContainer SettlementLevelObject(int containerID, ServerSettlementData settlement, int tileX, int tileY, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet actualContent = new Packet();
        PacketWriter writer = new PacketWriter(actualContent);
        if (settlement != null) {
            writer.putNextBoolean(true);
            new SettlementDataEvent(settlement).write(writer);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, actualContent, serverObject);
    }

    public static PacketOpenContainer SettlementLevelObject(int containerID, ServerSettlementData settlement, int tileX, int tileY, Packet content) {
        return PacketOpenContainer.SettlementLevelObject(containerID, settlement, tileX, tileY, content, null);
    }

    public static PacketOpenContainer SettlementLevelObject(int containerID, ServerSettlementData settlement, int tileX, int tileY) {
        return PacketOpenContainer.SettlementLevelObject(containerID, settlement, tileX, tileY, null);
    }

    public static PacketOpenContainer Settlement(int containerID, ServerSettlementData settlement, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet actualContent = new Packet();
        PacketWriter writer = new PacketWriter(actualContent);
        if (settlement != null) {
            writer.putNextBoolean(true);
            new SettlementDataEvent(settlement).write(writer);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, actualContent, serverObject);
    }

    public static PacketOpenContainer Settlement(int containerID, ServerSettlementData settlement, Packet content) {
        return PacketOpenContainer.Settlement(containerID, settlement, content, null);
    }

    public static PacketOpenContainer Settlement(int containerID, ServerSettlementData settlement) {
        return PacketOpenContainer.Settlement(containerID, settlement, null);
    }

    public static PacketOpenContainer ObjectEntity(int containerID, ObjectEntity oe, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet extraContent = new Packet();
        PacketWriter writer = new PacketWriter(extraContent);
        writer.putNextInt(oe.tileX);
        writer.putNextInt(oe.tileY);
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, extraContent, serverObject);
    }

    public static PacketOpenContainer ObjectEntity(int containerID, ObjectEntity oe, Packet content) {
        return PacketOpenContainer.ObjectEntity(containerID, oe, content, null);
    }

    public static PacketOpenContainer ObjectEntity(int containerID, ObjectEntity oe) {
        return PacketOpenContainer.ObjectEntity(containerID, oe, null);
    }

    public static PacketOpenContainer Mob(int containerID, Mob mob, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet extraContent = new Packet();
        PacketWriter writer = new PacketWriter(extraContent);
        writer.putNextInt(mob.getUniqueID());
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, extraContent, serverObject);
    }

    public static PacketOpenContainer Mob(int containerID, Mob mob, Packet content) {
        return PacketOpenContainer.Mob(containerID, mob, content, null);
    }

    public static PacketOpenContainer Mob(int containerID, Mob mob) {
        return PacketOpenContainer.Mob(containerID, mob, null);
    }

    public static PacketOpenContainer LevelObject(int containerID, int tileX, int tileY, Packet content, Object serverObject) {
        if (content == null) {
            content = new Packet();
        }
        Packet actualContent = new Packet();
        PacketWriter writer = new PacketWriter(actualContent);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextContentPacket(content);
        return new PacketOpenContainer(containerID, actualContent, serverObject);
    }

    public static PacketOpenContainer LevelObject(int containerID, int tileX, int tileY, Packet content) {
        return PacketOpenContainer.LevelObject(containerID, tileX, tileY, content, null);
    }

    public static PacketOpenContainer LevelObject(int containerID, int tileX, int tileY) {
        return PacketOpenContainer.LevelObject(containerID, tileX, tileY, null);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ContainerRegistry.openContainer(this.containerID, client, this.uniqueSeed, this.content);
    }
}

