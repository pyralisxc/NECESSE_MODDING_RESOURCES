/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.fairType.TypeParsers;
import necesse.level.maps.Level;

public class LevelsServerCommand
extends ModularChatCommand {
    public LevelsServerCommand() {
        super("levels", "Lists currently loaded levels", PermissionLevel.MODERATOR, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        logs.add("Loaded levels: " + server.world.levelManager.getLoadedLevelsNum());
        int num = 0;
        for (Level level : server.world.levelManager.getLoadedLevels()) {
            ++num;
            int mobs = level.entityManager.mobs.count();
            int pickups = level.entityManager.pickups.count();
            int projectiles = level.entityManager.projectiles.count();
            int objectEntities = level.entityManager.objectEntities.count();
            long players = server.streamClients().filter(c -> c.isSamePlace(level)).count();
            String levelIdentifierString = serverClient == null ? level.getIdentifier().toString() : TypeParsers.getTeleportParseString(level.getIdentifier());
            logs.add("Level " + num + ": " + levelIdentifierString + ". Cave: " + level.isCave + ", Mobs: " + mobs + ", Pickups: " + pickups + ", Projectiles: " + projectiles + ", ObjectEnts: " + objectEntities + ", Players: " + players);
        }
    }
}

