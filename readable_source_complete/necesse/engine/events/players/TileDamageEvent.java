/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.level.maps.Level;

public class TileDamageEvent
extends PreventableGameEvent {
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final int damage;
    public final float toolTier;
    public final Attacker attacker;
    public final ServerClient client;

    public TileDamageEvent(Level level, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.damage = damage;
        this.toolTier = toolTier;
        this.attacker = attacker;
        this.client = client;
    }
}

