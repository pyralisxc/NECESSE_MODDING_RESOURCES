/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormExpressionWheel;

public class PacketStartExpression
extends Packet {
    public final int slot;
    public final FormExpressionWheel.Expression expression;

    public PacketStartExpression(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.expression = reader.getNextEnum(FormExpressionWheel.Expression.class);
    }

    public PacketStartExpression(int slot, FormExpressionWheel.Expression expression) {
        this.slot = slot;
        this.expression = expression;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextEnum(expression);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot == client.slot) {
            client.playerMob.startExpression(this.expression);
            server.network.sendToClientsWithEntityExcept(this, client.playerMob, client);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            player.startExpression(this.expression);
        }
    }
}

