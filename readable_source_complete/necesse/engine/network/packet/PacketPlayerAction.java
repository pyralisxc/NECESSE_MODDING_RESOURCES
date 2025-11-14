/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerAction
extends Packet {
    public final PlayerAction action;
    public final int attackSeed;

    public PacketPlayerAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.action = PlayerAction.getPlayerAction(reader.getNextByteUnsigned());
        this.attackSeed = reader.getNextShortUnsigned();
    }

    public PacketPlayerAction(PlayerAction action, int attackSeed) {
        this.action = action;
        this.attackSeed = attackSeed;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(action.getID());
        writer.putNextShortUnsigned(attackSeed);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        client.playerMob.runPlayerAction(this.action, this.attackSeed);
        client.refreshAFKTimer();
    }

    public static enum PlayerAction {
        USE_HEALTH_POTION,
        USE_MANA_POTION,
        EAT_FOOD,
        USE_BUFF_POTION;


        public int getID() {
            return this.ordinal();
        }

        public static PlayerAction getPlayerAction(int id) {
            PlayerAction[] actions = PlayerAction.values();
            if (id < 0 || id >= actions.length) {
                return null;
            }
            return actions[id];
        }
    }
}

