/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;

public class SimpleArmorSetBuild
extends CharacterBuild {
    public String head;
    public String chest;
    public String feet;

    public SimpleArmorSetBuild(String head, String chest, String feet) {
        this.head = head;
        this.chest = chest;
        this.feet = feet;
    }

    @Override
    public void apply(ServerClient client) {
        PlayerInventoryManager inventory = client.playerMob.getInv();
        if (this.head != null) {
            inventory.equipment.getSelectedArmorSlot(0).setItem(new InventoryItem(this.head));
        }
        if (this.chest != null) {
            inventory.equipment.getSelectedArmorSlot(1).setItem(new InventoryItem(this.chest));
        }
        if (this.feet != null) {
            inventory.equipment.getSelectedArmorSlot(2).setItem(new InventoryItem(this.feet));
        }
    }
}

