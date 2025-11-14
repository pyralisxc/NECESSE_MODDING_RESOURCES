/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.level.maps.Level;

public class SignContainer
extends Container {
    public SignObjectEntity objectEntity;
    public StringCustomAction updateTextAction;

    public SignContainer(NetworkClient client, int uniqueSeed, final SignObjectEntity objectEntity) {
        super(client, uniqueSeed);
        this.objectEntity = objectEntity;
        this.updateTextAction = this.registerAction(new StringCustomAction(){

            @Override
            protected void run(String value) {
                objectEntity.setText(value);
                objectEntity.markDirty();
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        Level level = client.getLevel();
        return !this.objectEntity.removed() && level.getObject(this.objectEntity.tileX, this.objectEntity.tileY).isInInteractRange(level, this.objectEntity.tileX, this.objectEntity.tileY, client.playerMob);
    }
}

