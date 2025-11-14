/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.Objects;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.EnchantableSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class MageContainer
extends ShopContainer {
    public final EmptyCustomAction enchantButton;
    public final ContentCustomAction enchantButtonResponse;
    public final BooleanCustomAction setIsEnchanting;
    private boolean isEnchanting;
    public final int ENCHANT_SLOT;
    public MageHumanMob mageMob;
    public final long enchantCostSeed;
    public final float enchantCostModifier;
    public final PlayerTempInventory enchantInv;

    public MageContainer(final NetworkClient client, int uniqueSeed, MageHumanMob mob, PacketReader contentReader, ShopContainerData serverData) {
        super(client, uniqueSeed, mob, contentReader.getNextContentPacket(), serverData);
        this.enchantCostSeed = this.priceSeed * (long)GameRandom.prime(28);
        this.enchantCostModifier = contentReader.getNextFloat();
        this.enchantInv = client.playerMob.getInv().applyTempInventoryPacket(contentReader.getNextContentPacket(), m -> this.isClosed());
        this.ENCHANT_SLOT = this.addSlot(new EnchantableSlot(this.enchantInv, 0));
        this.addInventoryQuickTransfer(s -> this.isEnchanting, this.ENCHANT_SLOT, this.ENCHANT_SLOT);
        this.mageMob = mob;
        this.isEnchanting = false;
        this.enchantButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isServer()) {
                    if (MageContainer.this.canEnchant()) {
                        int enchantCost = MageContainer.this.getEnchantCost();
                        InventoryItem item = MageContainer.this.getSlot(MageContainer.this.ENCHANT_SLOT).getItem();
                        ((Enchantable)((Object)item.item)).setEnchantment(item, MageContainer.this.getBiasedEnchantmentID(item));
                        if (client.getServerClient().achievementsLoaded()) {
                            client.getServerClient().achievements().ENCHANT_ITEM.markCompleted(client.getServerClient());
                        }
                        client.getServerClient().newStats.money_spent.increment(enchantCost);
                        client.playerMob.getInv().main.removeItems(client.playerMob.getLevel(), client.playerMob, ItemRegistry.getItem("coin"), enchantCost, "buy");
                        client.getServerClient().newStats.items_enchanted.increment(1);
                        Packet itemContent = InventoryItem.getContentPacket(item);
                        MageContainer.this.enchantButtonResponse.runAndSend(itemContent);
                    }
                    MageContainer.this.getSlot(MageContainer.this.ENCHANT_SLOT).markDirty();
                }
            }
        });
        this.enchantButtonResponse = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                if (client.isClient()) {
                    InventoryItem enchantedItem = InventoryItem.fromContentPacket(content);
                    client.playerMob.getLevel().hudManager.addElement(new ItemPickupText(client.playerMob, enchantedItem));
                    SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(client.playerMob));
                }
            }
        });
        this.setIsEnchanting = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                MageContainer.this.isEnchanting = value;
            }
        });
    }

    public int getEnchantCost() {
        if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
            return 0;
        }
        InventoryItem item = this.getSlot(this.ENCHANT_SLOT).getItem();
        if (item.item.isEnchantable(item)) {
            Enchantable enchantItem = (Enchantable)((Object)item.item);
            GameRandom random = new GameRandom(this.enchantCostSeed + (long)item.item.getID() * (long)GameRandom.prime(54) + (long)enchantItem.getEnchantmentID(item) * (long)GameRandom.prime(13));
            return Math.abs((int)((float)enchantItem.getRandomEnchantCost(item, random, this.settlerHappiness) * this.enchantCostModifier));
        }
        return 0;
    }

    private int getBiasedEnchantmentID(InventoryItem item) {
        if (item.item.isEnchantable(item)) {
            float enchantCostMod;
            Enchantable enchantItem = (Enchantable)((Object)item.item);
            ItemEnchantment[] positiveEnchantments = (ItemEnchantment[])enchantItem.getValidEnchantmentIDs(item).stream().filter(id -> id.intValue() != enchantItem.getEnchantmentID(item)).map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new);
            ItemEnchantment[] negativeEnchantments = (ItemEnchantment[])enchantItem.getValidEnchantmentIDs(item).stream().filter(id -> id.intValue() != enchantItem.getEnchantmentID(item)).map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() <= 1.0f).toArray(ItemEnchantment[]::new);
            float happinessMultiplier = 0.05f;
            float lotteryBias = 0.0f;
            int settlerHappiness = GameMath.limit(this.settlerHappiness, 0, 100);
            TicketSystemList lottery = new TicketSystemList();
            if (settlerHappiness > 50) {
                lotteryBias = (float)(settlerHappiness - 50) * happinessMultiplier;
            } else if (settlerHappiness < 50) {
                lotteryBias = (float)(-settlerHappiness + 50) * happinessMultiplier;
            }
            for (ItemEnchantment positiveEnchantment : positiveEnchantments) {
                enchantCostMod = (positiveEnchantment.getEnchantCostMod() - 1.0f) * 100.0f;
                if (settlerHappiness > 50) {
                    lottery.addObject(100 + (int)(lotteryBias * enchantCostMod), positiveEnchantment);
                    continue;
                }
                lottery.addObject(100 - (int)(lotteryBias * enchantCostMod), positiveEnchantment);
            }
            for (ItemEnchantment negativeEnchantment : negativeEnchantments) {
                enchantCostMod = (negativeEnchantment.getEnchantCostMod() - 1.0f) * 100.0f;
                if (settlerHappiness > 50) {
                    lottery.addObject(100 + (int)(lotteryBias * enchantCostMod), negativeEnchantment);
                    continue;
                }
                lottery.addObject(100 - (int)(lotteryBias * enchantCostMod), negativeEnchantment);
            }
            ItemEnchantment randomObject = (ItemEnchantment)lottery.getRandomObject(GameRandom.globalRandom);
            return EnchantmentRegistry.getEnchantmentID(randomObject.getStringID());
        }
        return 0;
    }

    public boolean isItemEnchantable() {
        if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
            return false;
        }
        InventoryItem item = this.getSlot(this.ENCHANT_SLOT).getItem();
        return item.item.isEnchantable(item);
    }

    public boolean canEnchant() {
        int amount;
        boolean out;
        if (!this.isItemEnchantable()) {
            return false;
        }
        boolean bl = out = !this.getSlot(this.ENCHANT_SLOT).isClear();
        if (out && (amount = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy")) < this.getEnchantCost()) {
            out = false;
        }
        return out;
    }

    public static ShopContainerData getMageContainerContent(MageHumanMob mob, ServerClient client) {
        ShopContainerData baseData = mob.getShopContainerData(client);
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(baseData.content);
        float enchantHappinessCostModifier = mob.isSettler() ? GameMath.limit((float)mob.getSettlerHappiness() / 100.0f, 0.0f, 1.0f) : 0.25f;
        float enchantCostModifier = GameMath.lerp(enchantHappinessCostModifier, 1.0f, 0.3f);
        writer.putNextFloat(enchantCostModifier);
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return new ShopContainerData(packet, baseData.shopManager);
    }
}

