/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.EnchantableSpecificSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.miscItem.EnchantingScrollItem;

public class EnchantingScrollContainer
extends Container {
    public final EmptyCustomAction enchantButton;
    public final int ENCHANT_SLOT;
    public final PlayerTempInventory ingredientInv;
    public final ItemEnchantment enchantment;
    public final EnchantingScrollItem.EnchantScrollType scrollType;
    private final ContainerSlot scrollSlot;

    public EnchantingScrollContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        int scrollSlotIndex = reader.getNextInt();
        Packet tempInvContent = reader.getNextContentPacket();
        this.ingredientInv = client.playerMob.getInv().applyTempInventoryPacket(tempInvContent, m -> this.isClosed());
        ContainerSlot slot = this.getSlot(scrollSlotIndex);
        if (slot != null && !slot.isClear() && slot.getItem().item instanceof EnchantingScrollItem) {
            InventoryItem item = slot.getItem();
            this.lockSlot(scrollSlotIndex);
            this.scrollSlot = slot;
            EnchantingScrollItem scrollItem = (EnchantingScrollItem)item.item;
            this.enchantment = scrollItem.getEnchantment(item);
            this.scrollType = scrollItem.getType(this.enchantment);
        } else {
            this.scrollSlot = null;
            this.enchantment = null;
            this.scrollType = null;
        }
        this.ENCHANT_SLOT = this.addSlot(new EnchantableSpecificSlot(this.ingredientInv, 0, this.enchantment));
        this.addInventoryQuickTransfer(this.ENCHANT_SLOT, this.ENCHANT_SLOT);
        this.enchantButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    if (EnchantingScrollContainer.this.canEnchant() && EnchantingScrollContainer.this.isValid(serverClient) && EnchantingScrollContainer.this.scrollSlot != null) {
                        InventoryItem enchantItem = EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.ENCHANT_SLOT).getItem();
                        InventoryItem scrollItem = EnchantingScrollContainer.this.scrollSlot.getItem();
                        ItemEnchantment enchantment = ((EnchantingScrollItem)scrollItem.item).getEnchantment(scrollItem);
                        ((Enchantable)((Object)enchantItem.item)).setEnchantment(enchantItem, enchantment.getID());
                        if (serverClient.achievementsLoaded()) {
                            serverClient.achievements().ENCHANT_ITEM.markCompleted(serverClient);
                        }
                        serverClient.newStats.items_enchanted.increment(1);
                        InventoryItem draggingItem = EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.CLIENT_DRAGGING_SLOT).getItem();
                        if (draggingItem != null) {
                            serverClient.playerMob.getInv().addItemsDropRemaining(draggingItem, "addback", client.playerMob, false, true);
                        }
                        EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.ENCHANT_SLOT).setItem(null);
                        EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.CLIENT_DRAGGING_SLOT).setItem(enchantItem);
                        EnchantingScrollContainer.this.scrollSlot.setAmount(EnchantingScrollContainer.this.scrollSlot.getItemAmount() - 1);
                        EnchantingScrollContainer.this.close();
                    } else {
                        EnchantingScrollContainer.this.close();
                    }
                }
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (this.scrollSlot == null) {
            return false;
        }
        if (this.scrollSlot.isClear()) {
            return false;
        }
        InventoryItem item = this.scrollSlot.getItem();
        return item.item instanceof EnchantingScrollItem && ((EnchantingScrollItem)item.item).getEnchantment(item) == this.enchantment;
    }

    public boolean canEnchant() {
        if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
            return false;
        }
        InventoryItem item = this.getSlot(this.ENCHANT_SLOT).getItem();
        return item.item.isEnchantable(item);
    }

    public static Packet getContainerContent(ServerClient client, int slotIndex) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextInt(slotIndex);
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return p;
    }
}

