/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;

public class SimpleToolSetBuild
extends CharacterBuild {
    public String pickaxe;
    public String axe;
    public String shovel;

    public SimpleToolSetBuild(String pickaxe, String axe, String shovel) {
        super(-1000);
        this.pickaxe = pickaxe;
        this.axe = axe;
        this.shovel = shovel;
    }

    @Override
    public void apply(ServerClient client) {
        PlayerInventoryManager inventory = client.playerMob.getInv();
        if (this.pickaxe != null) {
            inventory.main.setItem(0, new InventoryItem(this.pickaxe));
        }
        if (this.axe != null) {
            inventory.main.setItem(9, new InventoryItem(this.axe));
        }
        if (this.shovel != null) {
            inventory.main.setItem(8, new InventoryItem(this.shovel));
        }
    }
}

