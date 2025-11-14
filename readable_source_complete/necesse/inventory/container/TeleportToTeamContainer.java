/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.IntCustomAction;

public abstract class TeleportToTeamContainer
extends Container {
    public final ArrayList<Integer> teamMemberSlots = new ArrayList();
    public final IntCustomAction teleportToSlotAction;
    public final int rangeMeters;

    public TeleportToTeamContainer(final NetworkClient client, int uniqueSeed, Packet contentPacket) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(contentPacket);
        int teamMemberCount = reader.getNextShortUnsigned();
        for (int i = 0; i < teamMemberCount; ++i) {
            this.teamMemberSlots.add(reader.getNextShortUnsigned());
        }
        this.rangeMeters = reader.getNextInt();
        this.teleportToSlotAction = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    ServerClient targetClient = serverClient.getServer().getClient(value);
                    if (targetClient != null && targetClient.isSameTeam(serverClient)) {
                        if (TeleportToTeamContainer.this.rangeMeters <= 0 || TeleportToTeamContainer.this.getDistanceInMeters(targetClient.playerMob) <= TeleportToTeamContainer.this.rangeMeters) {
                            TeleportToTeamContainer.this.performTeleport(serverClient, targetClient);
                        } else {
                            System.out.println(serverClient.getName() + " tried to teleport to out of range target: " + targetClient.getName());
                        }
                    } else {
                        System.out.println(serverClient.getName() + " tried to teleport to invalid target: " + (targetClient == null ? "null" : targetClient.getName()) + " (slot " + value + ")");
                    }
                }
            }
        });
    }

    public Point getFromLevelPosition() {
        return new Point(this.client.playerMob.getX(), this.client.playerMob.getY());
    }

    public int getDistanceInMeters(PlayerMob target) {
        Point from = this.getFromLevelPosition();
        float distance = GameMath.getExactDistance(from.x, from.y, target.getX(), target.getY());
        return (int)GameMath.pixelsToMeters(distance);
    }

    public abstract void performTeleport(ServerClient var1, ServerClient var2);

    public TeleportEvent getTeleportEvent(ServerClient client, ServerClient target, int delay, float sicknessSeconds) {
        return new TeleportEvent(client, delay, target.getLevelIdentifier(), sicknessSeconds, null, newLevel -> {
            Point targetPos = PortalObjectEntity.getTeleportDestinationAroundObject(newLevel, client.playerMob, target.playerMob.getTileX(), target.playerMob.getTileY(), true);
            return new TeleportResult(true, target.getLevelIdentifier(), targetPos.x, targetPos.y);
        });
    }

    public static Packet getContainerContentPacket(ServerClient client, int rangeMeters) {
        Server server = client.getServer();
        List teamMembers = server.streamClients().filter(c -> c != client && c.isSameTeam(client)).collect(Collectors.toList());
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextShortUnsigned(teamMembers.size());
        for (ServerClient teamMember : teamMembers) {
            writer.putNextShortUnsigned(teamMember.slot);
        }
        writer.putNextInt(rangeMeters);
        return packet;
    }
}

