/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public interface GameState {
    public boolean isClient();

    public Client getClient();

    public boolean isServer();

    public Server getServer();

    default public String getHostString() {
        boolean server = this.isServer();
        boolean client = this.isClient();
        if (server && client) {
            return "BOTH";
        }
        if (server) {
            return "SERVER";
        }
        if (client) {
            return "CLIENT";
        }
        return "NONE";
    }
}

