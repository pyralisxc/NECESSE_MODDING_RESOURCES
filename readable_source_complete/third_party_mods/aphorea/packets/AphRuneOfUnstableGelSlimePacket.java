/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.client.ClientClient
 *  necesse.engine.network.packet.PacketRequestPlayerData
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 */
package aphorea.packets;

import aphorea.utils.AphColors;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;

public class AphRuneOfUnstableGelSlimePacket
extends Packet {
    public final int slot;
    public final int targetX;
    public final int targetY;

    public AphRuneOfUnstableGelSlimePacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.slot = reader.getNextByteUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    public AphRuneOfUnstableGelSlimePacket(int slot, int targetX, int targetY) {
        this.slot = slot;
        this.targetX = targetX;
        this.targetY = targetY;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(targetX);
        writer.putNextInt(targetY);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            ClientClient target = client.getClient(this.slot);
            if (target != null && target.isSamePlace(client.getLevel())) {
                PlayerMob player = target.playerMob;
                player.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(player.getLevel(), player.x, player.y, AphColors.unstableGel), Particle.GType.CRITICAL);
                player.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(player.getLevel(), (float)this.targetX, (float)this.targetY, AphColors.unstableGel), Particle.GType.CRITICAL);
                player.setPos((float)this.targetX, (float)this.targetY, true);
            } else {
                client.network.sendPacket((Packet)new PacketRequestPlayerData(this.slot));
            }
        }
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            client.playerMob.setPos((float)this.targetX, (float)this.targetY, true);
        }
    }
}

