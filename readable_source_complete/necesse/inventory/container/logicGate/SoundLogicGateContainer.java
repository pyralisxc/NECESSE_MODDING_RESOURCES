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
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;

public class SoundLogicGateContainer
extends Container {
    public SoundLogicGateEntity entity;
    public final IntCustomAction setSound;
    public final IntCustomAction setSemitone;

    public SoundLogicGateContainer(final NetworkClient client, int uniqueSeed, final SoundLogicGateEntity entity) {
        super(client, uniqueSeed);
        this.entity = entity;
        this.setSound = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                entity.sound = value;
                if (client.isServer()) {
                    entity.sendUpdatePacket();
                }
            }
        });
        this.setSemitone = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                entity.semitone = value;
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

