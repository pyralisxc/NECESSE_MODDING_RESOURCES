/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.logicGate;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.logicGate.WireSelectCustomAction;
import necesse.level.gameLogicGate.entities.CounterLogicGateEntity;

public class CounterLogicGateContainer
extends Container {
    public CounterLogicGateEntity entity;
    public final WireSelectCustomAction setIncInputs;
    public final WireSelectCustomAction setDecInputs;
    public final WireSelectCustomAction setResetInputs;
    public final WireSelectCustomAction setOutputs;
    public final IntCustomAction setMaxValue;

    public CounterLogicGateContainer(final NetworkClient client, int uniqueSeed, final CounterLogicGateEntity entity) {
        super(client, uniqueSeed);
        this.entity = entity;
        this.setIncInputs = this.registerAction(new WireSelectCustomAction(){

            @Override
            protected void run(boolean[] wires) {
                entity.incInputs = wires;
                if (client.isServer()) {
                    entity.updateOutputs(false);
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setDecInputs = this.registerAction(new WireSelectCustomAction(){

            @Override
            protected void run(boolean[] wires) {
                entity.decInputs = wires;
                if (client.isServer()) {
                    entity.updateOutputs(false);
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setResetInputs = this.registerAction(new WireSelectCustomAction(){

            @Override
            protected void run(boolean[] wires) {
                entity.resetInputs = wires;
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
        this.setMaxValue = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                entity.setMaxValue(value);
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

