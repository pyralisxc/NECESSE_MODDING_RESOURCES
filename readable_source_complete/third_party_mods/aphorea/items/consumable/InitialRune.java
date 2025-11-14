/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketOpenContainer
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ContainerRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.container.Container
 *  necesse.inventory.container.ContainerActionResult
 *  necesse.inventory.container.slots.ContainerSlot
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.consumable;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import aphorea.items.vanillaitemtypes.AphConsumableItem;
import aphorea.registry.AphContainers;
import aphorea.registry.AphItems;
import java.awt.geom.Line2D;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class InitialRune
extends AphConsumableItem {
    public InitialRune() {
        super(1, false);
        this.rarity = Item.Rarity.UNIQUE;
        this.itemCooldownTime.setBaseValue(1000);
        this.worldDrawSize = 32;
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            PacketOpenContainer p = new PacketOpenContainer(AphContainers.INITIAL_RUNE_CONTAINER);
            ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
        }
        return item;
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(player);
        return !playerData.runeSelected ? null : Localization.translate((String)"message", (String)"alreadyselectedinitialrune");
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(container.getClient().playerMob);
                if (!playerData.runeSelected) {
                    if (container.getClient().isServer()) {
                        ServerClient client = container.getClient().getServerClient();
                        PacketOpenContainer p = new PacketOpenContainer(AphContainers.INITIAL_RUNE_CONTAINER);
                        ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
                    }
                    return new ContainerActionResult(-1002911334);
                }
                return new ContainerActionResult(208675834, Localization.translate((String)"message", (String)"alreadyselectedinitialrune"));
            }
            return new ContainerActionResult(208675834, Localization.translate((String)"itemtooltip", (String)"rclickinvopenerror"));
        };
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(perspective);
        if (playerData.runeSelected) {
            tooltips.add(Localization.translate((String)"message", (String)"alreadyselectedinitialrune"));
        } else {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"consumetip"));
        }
        return tooltips;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"initialrune");
    }

    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (perspective != null) {
            int sprite = (int)(perspective.getWorldTime() / 200L % (long)AphItems.initialRunes.size());
            return AphItems.initialRunes.get(sprite).getItemSprite(item, perspective);
        }
        return AphItems.initialRunes.get(0).getItemSprite(item, null);
    }
}

