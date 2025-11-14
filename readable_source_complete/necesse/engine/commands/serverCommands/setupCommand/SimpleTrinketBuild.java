/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.function.Function;
import necesse.engine.commands.serverCommands.setupCommand.SimpleItemBuild;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;

public class SimpleTrinketBuild
extends SimpleItemBuild {
    public SimpleTrinketBuild(int slot, Function<ServerClient, InventoryItem> itemSupplier) {
        super((PlayerInventoryManager m) -> new PlayerInventorySlot(m.equipment.getSelectedTrinketsInventory(slot), slot), itemSupplier);
    }

    public SimpleTrinketBuild(Function<ServerClient, InventoryItem> itemSupplier) {
        this(-1, itemSupplier);
    }

    public SimpleTrinketBuild(int slot, String itemStringID) {
        super((PlayerInventoryManager m) -> new PlayerInventorySlot(m.equipment.getSelectedTrinketsInventory(slot), slot), itemStringID);
    }

    public SimpleTrinketBuild(String itemStringID) {
        this(-1, itemStringID);
    }
}

