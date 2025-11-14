/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.PlayerInventoryManager;

public class CopyInventoryServerCommand
extends ModularChatCommand {
    public CopyInventoryServerCommand() {
        super("copyplayer", "Copy a players inventory, position and health over to another", PermissionLevel.ADMIN, true, new CmdParameter("from", new ServerClientParameterHandler(true, false)), new CmdParameter("to", new ServerClientParameterHandler(true, false)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient from = (ServerClient)args[0];
        ServerClient to = (ServerClient)args[1];
        PlayerInventoryManager fromInv = from.playerMob.getInv();
        PlayerInventoryManager toInv = to.playerMob.getInv();
        this.copyInventory(fromInv.drag, toInv.drag);
        this.copyInventory(fromInv.main, toInv.main);
        int sets = Math.min(fromInv.equipment.getTotalSets(), toInv.equipment.getTotalSets());
        for (int i = 0; i < sets; ++i) {
            this.copyInventory(fromInv.equipment.armor.get(i), toInv.equipment.armor.get(i));
            this.copyInventory(fromInv.equipment.cosmetic.get(i), toInv.equipment.cosmetic.get(i));
            this.copyInventory(fromInv.equipment.trinkets.get(i), toInv.equipment.trinkets.get(i));
            this.copyInventory(fromInv.equipment.equipment.get(i), toInv.equipment.equipment.get(i));
        }
        this.copyInventory(fromInv.trash, toInv.trash);
        this.copyInventory(fromInv.cloud, toInv.cloud);
        to.playerMob.setMaxHealth(from.playerMob.getMaxHealth());
        to.playerMob.setHealth(from.playerMob.getHealth());
        to.playerMob.setPos(from.playerMob.x, from.playerMob.y, true);
        to.changeLevel(from.getLevelIdentifier());
        to.sendPacket(new PacketPlayerGeneral(to));
    }

    private void copyInventory(Inventory from, Inventory to) {
        to.override(from);
        to.markFullDirty();
    }
}

