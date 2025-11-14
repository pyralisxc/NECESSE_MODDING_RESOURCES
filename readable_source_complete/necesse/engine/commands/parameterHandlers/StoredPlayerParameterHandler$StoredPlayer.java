/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import necesse.engine.network.server.Server;
import necesse.engine.world.WorldFile;

public static class StoredPlayerParameterHandler.StoredPlayer {
    public long authentication;
    public String name;
    public WorldFile file;
    public WorldFile mapFile;

    public StoredPlayerParameterHandler.StoredPlayer(Server server, String name, long authentication) {
        this.name = name;
        this.authentication = authentication;
        this.file = server.world.fileSystem.getPlayerFile(authentication);
        this.mapFile = server.world.fileSystem.getMapPlayerFile(authentication);
    }
}
