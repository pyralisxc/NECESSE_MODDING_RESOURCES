/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.players.MobInteractEvent;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class PacketPlayerMobInteract
extends Packet {
    public final int slot;
    public final int targetMobUniqueID;

    public PacketPlayerMobInteract(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.targetMobUniqueID = reader.getNextInt();
    }

    public PacketPlayerMobInteract(int slot, int interactMobID) {
        this.slot = slot;
        this.targetMobUniqueID = interactMobID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(this.targetMobUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            Mob interactMob = GameUtils.getLevelMob(this.targetMobUniqueID, client.getLevel());
            if (interactMob != null) {
                GameEvents.triggerEvent(new MobInteractEvent(interactMob, player), e -> interactMob.interact(player));
            } else {
                client.network.sendPacket(new PacketRequestMobData(this.targetMobUniqueID));
            }
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot != client.slot || !client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        Level level = server.world.getLevel(client);
        Mob interactMob = GameUtils.getLevelMob(this.targetMobUniqueID, level);
        if (interactMob != null) {
            if (interactMob.inInteractRange(client.playerMob)) {
                if (interactMob.canInteract(client.playerMob)) {
                    GameEvents.triggerEvent(new MobInteractEvent(interactMob, client.playerMob), e -> interactMob.interact(client.playerMob));
                    server.network.sendToClientsWithAnyRegion(this, client.playerMob.getRegionPositionsCombined(interactMob));
                } else {
                    GameLog.warn.println("Client tried to interact with not interactable mob " + interactMob.getStringID() + ", " + interactMob.getUniqueID());
                }
            }
        } else {
            server.network.sendPacket((Packet)new PacketRemoveMob(this.targetMobUniqueID), client);
        }
    }
}

