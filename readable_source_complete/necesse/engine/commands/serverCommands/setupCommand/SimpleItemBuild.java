/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.util.Objects;
import java.util.function.Function;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;

public class SimpleItemBuild
extends CharacterBuild {
    public final Function<PlayerInventoryManager, PlayerInventorySlot> slotGetter;
    public final Function<ServerClient, InventoryItem> itemSupplier;

    public SimpleItemBuild(Function<PlayerInventoryManager, PlayerInventorySlot> slotGetter, Function<ServerClient, InventoryItem> itemSupplier) {
        this.slotGetter = slotGetter;
        Objects.requireNonNull(itemSupplier);
        this.itemSupplier = itemSupplier;
    }

    public SimpleItemBuild(int slot, Function<ServerClient, InventoryItem> itemSupplier) {
        this((PlayerInventoryManager m) -> new PlayerInventorySlot(m.main, slot), itemSupplier);
    }

    public SimpleItemBuild(Function<ServerClient, InventoryItem> itemSupplier) {
        this(null, itemSupplier);
    }

    public SimpleItemBuild(Function<PlayerInventoryManager, PlayerInventorySlot> slotGetter, String itemStringID) {
        this(slotGetter, (ServerClient c) -> new InventoryItem(itemStringID));
    }

    public SimpleItemBuild(int slot, String itemStringID, int amount) {
        this(slot, (ServerClient c) -> new InventoryItem(itemStringID, amount));
    }

    public SimpleItemBuild(String itemStringID, int amount) {
        this(c -> new InventoryItem(itemStringID, amount));
    }

    @Override
    public void apply(ServerClient client) {
        InventoryItem item = this.itemSupplier.apply(client);
        if (item != null) {
            PlayerInventoryManager inv = client.playerMob.getInv();
            if (this.slotGetter == null) {
                client.playerMob.getInv().main.addItem(client.getLevel(), client.playerMob, item, "addbuild", null);
            } else {
                PlayerInventorySlot slot = this.slotGetter.apply(inv);
                if (slot.slot < 0) {
                    slot.getInv(inv).addItem(client.getLevel(), client.playerMob, item, "addbuild", null);
                } else {
                    client.playerMob.getInv().setItem(slot, item);
                }
            }
        }
    }
}

