/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.logicGate;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.logicGate.WireSelectCustomAction;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;

public class SensorLogicGateContainer
extends Container {
    public SensorLogicGateEntity entity;
    public final WireSelectCustomAction setOutputs;
    public final BooleanCustomAction setPlayers;
    public final BooleanCustomAction setPassiveMobs;
    public final BooleanCustomAction setHostileMobs;
    public final IntCustomAction setRange;

    public SensorLogicGateContainer(final NetworkClient client, int uniqueSeed, final SensorLogicGateEntity entity) {
        super(client, uniqueSeed);
        this.entity = entity;
        this.setOutputs = this.registerAction(new WireSelectCustomAction(){

            @Override
            protected void run(boolean[] wires) {
                entity.wireOutputs = wires;
                if (client.isServer()) {
                    entity.updateOutputs(false);
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setPlayers = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                entity.players = value;
                if (client.isServer()) {
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setPassiveMobs = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                entity.passiveMobs = value;
                if (client.isServer()) {
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setHostileMobs = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                entity.hostileMobs = value;
                if (client.isServer()) {
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setRange = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                entity.range = GameMath.limit(value, 1, SensorLogicGateEntity.MAX_RANGE);
                if (client.isServer()) {
                    entity.sendUpdatePacket();
                }
            }
        });
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (this.entity.isRemoved()) return false;
        Point point = new Point(this.entity.tileX * 32 + 16, this.entity.tileY * 32 + 16);
        if (!(point.distance(client.playerMob.getX(), client.playerMob.getY()) <= (double)PlaceableItemInterface.getPlaceRange(client.playerMob))) return false;
        return true;
    }
}

