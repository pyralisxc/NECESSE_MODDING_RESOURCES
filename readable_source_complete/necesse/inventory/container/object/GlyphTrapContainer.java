/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.GlyphTrapObjectEntity;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.level.maps.Level;

public class GlyphTrapContainer
extends Container {
    public GlyphTrapObjectEntity glyph;
    public final BooleanCustomAction setPlayers;
    public final BooleanCustomAction setPassiveMobs;
    public final BooleanCustomAction setHostileMobs;

    public GlyphTrapContainer(NetworkClient client, int uniqueSeed, final GlyphTrapObjectEntity glyph) {
        super(client, uniqueSeed);
        this.glyph = glyph;
        this.setPlayers = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                glyph.players = value;
                glyph.markDirty();
            }
        });
        this.setPassiveMobs = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                glyph.passiveMobs = value;
                glyph.markDirty();
            }
        });
        this.setHostileMobs = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                glyph.hostileMobs = value;
                glyph.markDirty();
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        Level level = client.getLevel();
        return !this.glyph.removed() && level.getObject(this.glyph.tileX, this.glyph.tileY).isInInteractRange(level, this.glyph.tileX, this.glyph.tileY, client.playerMob);
    }
}

