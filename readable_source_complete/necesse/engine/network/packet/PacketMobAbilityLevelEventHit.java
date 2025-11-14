/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketMobAbilityLevelEventHit
extends Packet {
    public final int eventUniqueID;
    public final int targetUniqueID;
    public final Packet content;

    public PacketMobAbilityLevelEventHit(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventUniqueID = reader.getNextInt();
        this.targetUniqueID = reader.getNextInt();
        this.content = reader.hasNext() ? reader.getNextContentPacket() : null;
    }

    public PacketMobAbilityLevelEventHit(MobAbilityLevelEvent event, Mob target, Packet content) {
        this.eventUniqueID = event.getUniqueID();
        this.targetUniqueID = target.getUniqueID();
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.eventUniqueID);
        writer.putNextInt(this.targetUniqueID);
        if (content != null) {
            writer.putNextContentPacket(content);
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        LevelEvent event = level.entityManager.events.get(this.eventUniqueID, true);
        Mob target = GameUtils.getLevelMob(this.targetUniqueID, level);
        if (target != null && event instanceof MobAbilityLevelEvent) {
            this.serverHit(client, (MobAbilityLevelEvent)event, target);
        } else {
            level.entityManager.submittedHits.submitMobAbilityLevelEventHit(client, this.eventUniqueID, this.targetUniqueID, this::serverHit, (foundClient, attackerUniqueID, foundEvent, targetUniqueID, foundTarget) -> {
                if (foundEvent == null) {
                    foundClient.sendPacket(new PacketLevelEventOver(attackerUniqueID));
                }
                if (foundTarget == null) {
                    foundClient.sendPacket(new PacketRemoveMob(targetUniqueID));
                }
            });
        }
    }

    private void serverHit(ServerClient client, MobAbilityLevelEvent event, Mob target) {
        if (target == client.playerMob || event.handlingClient == client && !target.isPlayer) {
            event.serverHit(target, this.content, true);
        }
    }
}

