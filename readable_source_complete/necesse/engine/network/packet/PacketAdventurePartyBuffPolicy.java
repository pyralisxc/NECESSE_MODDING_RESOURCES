/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyBuffPolicy
extends Packet {
    public final AdventureParty.BuffPotionPolicy policy;

    public PacketAdventurePartyBuffPolicy(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.policy = reader.getNextEnum(AdventureParty.BuffPotionPolicy.class);
    }

    public PacketAdventurePartyBuffPolicy(AdventureParty.BuffPotionPolicy policy) {
        this.policy = policy;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextEnum(policy);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.adventureParty.setBuffPotionPolicy(this.policy, false);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.adventureParty.setBuffPotionPolicy(this.policy, false);
    }
}

