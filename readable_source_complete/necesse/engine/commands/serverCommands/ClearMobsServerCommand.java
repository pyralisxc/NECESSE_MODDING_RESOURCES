/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class ClearMobsServerCommand
extends ModularChatCommand {
    public ClearMobsServerCommand() {
        super("clearmobs", "Clears all mobs or a specific type on your level or on all loaded levels", PermissionLevel.ADMIN, true, new CmdParameter("global", new BoolParameterHandler(), true, new CmdParameter("type", new StringParameterHandler(), true, new CmdParameter[0])));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int amount = 0;
        int levels = 0;
        boolean global = (Boolean)args[0];
        String type = (String)args[1];
        if (type != null) {
            if (global || serverClient == null) {
                for (Level level : server.world.levelManager.getLoadedLevels()) {
                    ++levels;
                    amount += this.removeMobType(level, type);
                }
            } else {
                ++levels;
                amount += this.removeMobType(server.world.getLevel(serverClient), type);
            }
        } else if (global || serverClient == null) {
            for (Level level : server.world.levelManager.getLoadedLevels()) {
                ++levels;
                amount += this.removeHostileMobs(level);
            }
        } else {
            ++levels;
            amount += this.removeHostileMobs(server.world.getLevel(serverClient));
        }
        logs.add("Cleared " + amount + " mobs on " + levels + " levels.");
    }

    private int removeMobType(Level level, String type) {
        int amount = 0;
        for (Mob mob : level.entityManager.mobs) {
            if (!mob.getStringID().contains(type.toLowerCase())) continue;
            mob.remove();
            ++amount;
        }
        return amount;
    }

    private int removeHostileMobs(Level level) {
        int amount = 0;
        for (Mob mob : level.entityManager.mobs) {
            if (!mob.isHostile) continue;
            mob.remove();
            ++amount;
        }
        return amount;
    }
}

