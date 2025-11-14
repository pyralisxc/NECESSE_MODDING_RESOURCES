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

public class PacketPlayerAttackHandler
extends Packet {
    public final int subType;
    public final Packet content;

    public PacketPlayerAttackHandler(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.subType = reader.getNextByteUnsigned();
        this.content = reader.getNextContentPacket();
    }

    private PacketPlayerAttackHandler(int subType, Packet content) {
        this.subType = subType;
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(subType);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        if (client.playerMob.getAttackHandler() != null) {
            switch (this.subType) {
                case 0: {
                    client.playerMob.endAttackHandler(true);
                    break;
                }
                case 2: {
                    PacketReader reader = new PacketReader(this.content);
                    if (reader.getNextBoolean()) {
                        int levelX = reader.getNextInt();
                        int levelY = reader.getNextInt();
                        client.playerMob.getAttackHandler().onMouseInteracted(levelX, levelY);
                        break;
                    }
                    float aimX = reader.getNextFloat();
                    float aimY = reader.getNextFloat();
                    client.playerMob.getAttackHandler().onControllerInteracted(aimX, aimY);
                    break;
                }
                case 3: {
                    client.playerMob.getAttackHandler().onUpdatePacket(new PacketReader(this.content));
                    break;
                }
                default: {
                    System.out.println(client.getName() + " sent invalid attack handler update");
                    break;
                }
            }
        } else {
            client.sendPacket(new PacketPlayerAttackHandler(1, new Packet()));
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer();
        if (player.getAttackHandler() != null) {
            switch (this.subType) {
                case 1: {
                    player.endAttackHandler(false);
                    break;
                }
                case 3: {
                    player.getAttackHandler().onUpdatePacket(new PacketReader(this.content));
                    break;
                }
                default: {
                    System.out.println("Got invalid attack handler update from server");
                }
            }
        }
    }

    public static PacketPlayerAttackHandler clientEnd() {
        return new PacketPlayerAttackHandler(0, new Packet());
    }

    public static PacketPlayerAttackHandler serverEnd() {
        return new PacketPlayerAttackHandler(1, new Packet());
    }

    public static PacketPlayerAttackHandler clientInteractMouse(int levelX, int levelY) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(true);
        writer.putNextInt(levelX);
        writer.putNextInt(levelY);
        return new PacketPlayerAttackHandler(2, content);
    }

    public static PacketPlayerAttackHandler clientInteractController(float aimX, float aimY) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(false);
        writer.putNextFloat(aimX);
        writer.putNextFloat(aimY);
        return new PacketPlayerAttackHandler(2, content);
    }

    public static PacketPlayerAttackHandler update(Packet content) {
        return new PacketPlayerAttackHandler(3, content);
    }
}

