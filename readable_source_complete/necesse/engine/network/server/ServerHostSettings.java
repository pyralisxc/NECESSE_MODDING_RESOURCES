/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import necesse.engine.network.server.Server;

public class ServerHostSettings {
    public String password = null;
    public boolean allowOutsideCharacters;
    public boolean forcedPvP;

    public void apply(Server server, boolean alreadyStarted) {
        if (this.password != null) {
            server.getSettings().password = this.password;
        }
        server.world.settings.allowOutsideCharacters = this.allowOutsideCharacters;
        server.world.settings.forcedPvP = this.forcedPvP;
        if (alreadyStarted) {
            server.world.settings.sendSettingsPacket();
        }
    }
}

