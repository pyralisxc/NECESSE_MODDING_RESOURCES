/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.util.HashMap;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;

public class CreativeModeServerCommand
extends ModularChatCommand {
    private long lastServerWorldUniqueID;
    private final HashMap<Long, Long> hasTriedToSetCreativeMode = new HashMap();

    public CreativeModeServerCommand() {
        super("creativemode", "Enables creative mode (not reversible). Will disabled achievements.", PermissionLevel.OWNER, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (this.lastServerWorldUniqueID != server.world.getUniqueID()) {
            this.hasTriedToSetCreativeMode.clear();
        }
        this.lastServerWorldUniqueID = server.world.getUniqueID();
        if (server.world.settings.creativeMode) {
            logs.add(GameColor.RED.getColorCode() + "Creative mode already enabled.");
        } else {
            long auth = serverClient == null ? -1L : serverClient.authentication;
            long lastSetTime = this.hasTriedToSetCreativeMode.getOrDefault(auth, -1L);
            if (lastSetTime != -1L && System.currentTimeMillis() - lastSetTime < 300000L) {
                server.world.settings.enableCreativeMode(true);
                server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeenabled")));
                this.hasTriedToSetCreativeMode.clear();
            } else {
                logs.add(GameColor.CYAN.getColorCode() + "Warning, this will permanently disable achievements for this world and all characters currently logged on. Run command again to confirm.");
                this.hasTriedToSetCreativeMode.put(auth, System.currentTimeMillis());
            }
        }
    }
}

