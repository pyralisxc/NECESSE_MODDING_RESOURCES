/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HealServerCommand
extends ModularChatCommand {
    public HealServerCommand() {
        super("healmobs", "Heals mobs around you", PermissionLevel.ADMIN, true, new CmdParameter("health", new IntParameterHandler(-1)), new CmdParameter("range", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("filter", new MultiParameterHandler(new PresetStringParameterHandler("hostile", "passive", "boss", "settler"), new StringParameterHandler()), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String filterMsg;
        int amount = (Integer)args[0];
        int range = (Integer)args[1];
        Object[] filtersSubArgs = (Object[])args[2];
        String presetFilter = (String)filtersSubArgs[0];
        String stringIDFilter = (String)filtersSubArgs[1];
        if (serverClient == null) {
            logs.add("Command cannot be run from server");
            return;
        }
        Predicate<Mob> filter = m -> true;
        if (presetFilter != null) {
            switch (presetFilter) {
                case "hostile": {
                    filter = m -> m.isHostile;
                    break;
                }
                case "passive": {
                    filter = m -> !m.isHostile;
                    break;
                }
                case "boss": {
                    filter = Mob::isBoss;
                    break;
                }
                case "settler": {
                    filter = m -> m instanceof HumanMob && ((HumanMob)m).isSettler();
                }
            }
            filterMsg = presetFilter;
        } else if (stringIDFilter == null || stringIDFilter.isEmpty()) {
            filterMsg = null;
        } else {
            filter = m -> m.getStringID().contains(stringIDFilter);
            filterMsg = stringIDFilter;
        }
        AtomicInteger counter = new AtomicInteger(0);
        if (range < 0) {
            serverClient.getLevel().entityManager.mobs.stream().filter(filter).forEach(m -> {
                counter.addAndGet(1);
                m.setHealth(m.getHealth() + amount);
            });
            logs.add("Healed " + counter.get() + (filterMsg == null ? "" : " " + filterMsg) + " mobs for " + amount + " health");
        } else {
            Point base = new Point(serverClient.playerMob.getX(), serverClient.playerMob.getY());
            serverClient.getLevel().entityManager.mobs.streamAreaTileRange(base.x, base.y, range).filter(m -> m.getDistance(base.x, base.y) <= (float)(range * 32)).filter(filter).forEach(m -> {
                counter.addAndGet(1);
                m.setHealth(m.getHealth() + amount);
            });
            logs.add("Healed " + counter.get() + (filterMsg == null ? "" : " " + filterMsg) + " mobs " + range + " tiles around you for " + amount + " health");
        }
    }
}

