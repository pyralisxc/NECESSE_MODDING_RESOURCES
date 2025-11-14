/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events;

import necesse.engine.events.GameEvent;
import necesse.engine.network.server.Server;

public class ServerStopEvent
extends GameEvent {
    public final Server server;

    public ServerStopEvent(Server server) {
        this.server = server;
    }
}

