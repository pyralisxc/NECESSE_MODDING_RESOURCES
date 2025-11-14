/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;

public class AlchemistContainer
extends ShopContainer {
    private static final float BASE_COST = 10.0f;
    private static final float SHAPE_COST = 5.0f;
    private static final float COLOR_COST = 5.0f;
    private static final float CRACKLE_COST = 5.0f;
    public final ContentCustomAction buyFireworkButton;
    public AlchemistHumanMob alchemistMob;
    public long costSeed;

    public AlchemistContainer(final NetworkClient client, int uniqueSeed, AlchemistHumanMob mob, Packet contentPacket, ShopContainerData serverData) {
        super(client, uniqueSeed, mob, contentPacket, serverData);
        this.alchemistMob = mob;
        this.costSeed = mob.getShopSeed();
        this.buyFireworkButton = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                GNDItemMap gndData = new GNDItemMap(reader);
                int amount = reader.getNextShortUnsigned();
                if (AlchemistContainer.this.buyFirework(gndData, amount) > 0 && client.isClient()) {
                    SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(client.playerMob));
                }
            }
        });
    }

    public int buyFirework(GNDItemMap gndData, int amount) {
        int bought = 0;
        for (int i = 0; i < amount && this.canBuyFirework(gndData); ++i) {
            InventoryItem item = new InventoryItem("fireworkrocket");
            item.setGndData(gndData);
            ContainerSlot slot = this.getClientDraggingSlot();
            if (!slot.isClear() && (!slot.getItem().canCombine(this.client.playerMob.getLevel(), this.client.playerMob, item, "buy") || slot.getItemAmount() + item.getAmount() > slot.getItemStackLimit(slot.getItem()))) break;
            bought += item.getAmount();
            int cost = this.getFireworksCost(gndData);
            this.client.playerMob.getInv().main.removeItems(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), cost, "buy");
            if (this.client.isServer()) {
                this.client.getServerClient().newStats.money_spent.increment(cost);
            }
            if (slot.isClear()) {
                slot.setItem(item.copy());
                continue;
            }
            slot.getItem().combine(this.client.playerMob.getLevel(), this.client.playerMob, slot.getInventory(), slot.getInventorySlot(), item.copy(), "buy", null);
        }
        return bought;
    }

    private float getRandomPrice(long seed, float middlePrice) {
        return this.getRandomPrice(seed, middlePrice, middlePrice / 5.0f);
    }

    private float getRandomPrice(long seed, float middlePrice, float offset) {
        return new GameRandom(seed).getFloatOffset(middlePrice, offset);
    }

    public int getFireworksCost(GNDItemMap gndData) {
        FireworkPlaceableItem.FireworkCrackle crackle;
        FireworkPlaceableItem.FireworkColor color;
        float totalCost = this.getRandomPrice(this.costSeed, 10.0f);
        FireworkPlaceableItem.FireworksShape shape = FireworkPlaceableItem.getShape(gndData);
        if (shape != null) {
            totalCost += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(4) * (long)(shape.ordinal() + 1), 5.0f);
        }
        if ((color = FireworkPlaceableItem.getColor(gndData)) != null) {
            totalCost += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(8) * (long)(color.ordinal() + 1), 5.0f);
        }
        if ((crackle = FireworkPlaceableItem.getCrackle(gndData)) != null) {
            totalCost += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(12) * (long)(crackle.ordinal() + 1), 5.0f);
        }
        return (int)totalCost;
    }

    public boolean canBuyFirework(GNDItemMap gndData) {
        int cost = this.getFireworksCost(gndData);
        if (cost <= 0) {
            return true;
        }
        int amount = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy");
        return amount >= cost;
    }
}

