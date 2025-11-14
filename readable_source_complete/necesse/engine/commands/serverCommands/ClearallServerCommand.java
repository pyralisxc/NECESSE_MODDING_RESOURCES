/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.Entity;
import necesse.level.maps.Level;

public class ClearallServerCommand
extends ModularChatCommand {
    public ClearallServerCommand() {
        super("clearall", "Clears all entities", PermissionLevel.ADMIN, true, new CmdParameter("global", new BoolParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int mobAmount = 0;
        int itemAmount = 0;
        int projectileAmount = 0;
        int levels = 0;
        boolean global = (Boolean)args[0];
        if (global || serverClient == null) {
            for (Level level : server.world.levelManager.getLoadedLevels()) {
                ++levels;
                mobAmount += this.removeEntities(level.entityManager.mobs);
                itemAmount += this.removeEntities(level.entityManager.pickups);
                projectileAmount += this.removeEntities(level.entityManager.projectiles);
            }
        } else {
            levels = 1;
            mobAmount += this.removeEntities(server.world.getLevel((ServerClient)serverClient).entityManager.mobs);
            itemAmount += this.removeEntities(server.world.getLevel((ServerClient)serverClient).entityManager.pickups);
            projectileAmount += this.removeEntities(server.world.getLevel((ServerClient)serverClient).entityManager.projectiles);
        }
        logs.add("Cleared " + mobAmount + " mobs, " + itemAmount + " items and " + projectileAmount + " projectiles on " + levels + " levels.");
    }

    private int removeEntities(Iterable<? extends Entity> entities) {
        int amount = 0;
        for (Entity entity : entities) {
            entity.remove();
            ++amount;
        }
        return amount;
    }
}

