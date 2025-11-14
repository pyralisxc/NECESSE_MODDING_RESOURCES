/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.StaticItemAttackSlot;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class UseItemBuild
extends CharacterBuild {
    public String itemStringID;

    public UseItemBuild(String itemStringID) {
        this.itemStringID = itemStringID;
    }

    @Override
    public void apply(ServerClient client) {
        InventoryItem attackItem = new InventoryItem(this.itemStringID);
        PlayerMob player = client.playerMob;
        GNDItemMap map = new GNDItemMap();
        int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
        attackItem.item.setupAttackMapContent(map, player.getLevel(), player.getX(), player.getY(), player, seed, attackItem);
        attackItem.item.onAttack(player.getLevel(), player.getX(), player.getY(), player, player.getCurrentAttackHeight(), attackItem, new StaticItemAttackSlot(attackItem), 0, seed, map);
    }
}

