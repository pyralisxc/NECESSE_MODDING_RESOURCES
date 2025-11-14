/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameColor;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;

public class PacketMobChat
extends Packet {
    public final int mobUniqueID;
    public final GameMessage message;

    public PacketMobChat(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.message = GameMessage.fromContentPacket(reader.getNextContentPacket());
    }

    public PacketMobChat(int mobUniqueID, GameMessage message) {
        this.mobUniqueID = mobUniqueID;
        this.message = message;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextContentPacket(message.getContentPacket());
    }

    public PacketMobChat(int mobID, String message) {
        this(mobID, new StaticMessage(message));
    }

    public PacketMobChat(int mobID, String category, String translationKey) {
        this(mobID, new LocalMessage(category, translationKey));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob chatter = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (chatter != null) {
            chatter.getLevel().hudManager.addElement(new ChatBubbleText(chatter, GameColor.stripCodes(this.message.translate())));
        }
    }
}

