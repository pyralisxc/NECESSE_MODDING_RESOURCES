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
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.logicGate.WireSelectCustomAction;
import necesse.level.gameLogicGate.entities.BufferLogicGateEntity;

public class BufferLogicGateContainer
extends Container {
    public BufferLogicGateEntity entity;
    public final WireSelectCustomAction setInputs;
    public final WireSelectCustomAction setOutputs;
    public final IntCustomAction setDelay;

    public BufferLogicGateContainer(final NetworkClient client, int uniqueSeed, final BufferLogicGateEntity entity) {
        super(client, uniqueSeed);
        this.entity = entity;
        this.setInputs = this.registerAction(new WireSelectCustomAction(){

            @Override
            protected void run(boolean[] wires) {
                entity.wireInputs = wires;
                if (client.isServer()) {
                    entity.updateOutputs(false);
                    entity.sendUpdatePacket();
                }
            }
        });
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
        this.setDelay = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                entity.delayTicks = GameMath.limit(value, 1, 200);
                if (client.isServer()) {
                    entity.updateOutputs(false);
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

