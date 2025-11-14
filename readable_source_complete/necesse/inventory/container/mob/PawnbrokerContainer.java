/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.PawnBrokerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.ContainerSlot;

public class PawnbrokerContainer
extends ShopContainer {
    public boolean isPawning;
    public PlayerTempInventory inventory;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;
    private float profit;
    public final EmptyCustomAction quickStackButton;
    public final EmptyCustomAction transferAll;
    public final EmptyCustomAction restockButton;
    public final EmptyCustomAction lootButton;
    public final PawnItemsAction sellButton;
    public final BooleanCustomAction setIsPawning;

    public PawnbrokerContainer(final NetworkClient client, int uniqueSeed, HumanShop mob, PacketReader reader, ShopContainerData serverData) {
        super(client, uniqueSeed, mob, reader.getNextContentPacket(), serverData);
        this.inventory = client.playerMob.getInv().applyTempInventoryPacket(reader.getNextContentPacket(), (player, size, invID) -> new PlayerTempInventory(player, size, invID){

            @Override
            public boolean shouldDispose() {
                return PawnbrokerContainer.this.isClosed();
            }

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                PawnbrokerContainer.this.updateProfit();
            }
        });
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            int index = this.addSlot(new ContainerSlot(this.inventory, i){

                @Override
                public boolean canLockItem() {
                    return false;
                }

                @Override
                public String getItemInvalidError(InventoryItem item) {
                    if (item == null || !item.item.getStringID().equals("coin")) {
                        return null;
                    }
                    return "";
                }
            });
            if (this.INVENTORY_START == -1) {
                this.INVENTORY_START = index;
            }
            if (this.INVENTORY_END == -1) {
                this.INVENTORY_END = index;
            }
            this.INVENTORY_START = Math.min(this.INVENTORY_START, index);
            this.INVENTORY_END = Math.max(this.INVENTORY_END, index);
        }
        this.addInventoryQuickTransfer(s -> this.isPawning, this.INVENTORY_START, this.INVENTORY_END);
        this.quickStackButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(PawnbrokerContainer.this.inventory)));
                PawnbrokerContainer.this.quickStackToInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.transferAll = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = PawnbrokerContainer.this.CLIENT_INVENTORY_START; i <= PawnbrokerContainer.this.CLIENT_INVENTORY_END; ++i) {
                    if (PawnbrokerContainer.this.getSlot(i).isItemLocked()) continue;
                    PawnbrokerContainer.this.transferToSlots(PawnbrokerContainer.this.getSlot(i), PawnbrokerContainer.this.INVENTORY_START, PawnbrokerContainer.this.INVENTORY_END, "transferall");
                }
            }
        });
        this.restockButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(PawnbrokerContainer.this.inventory)));
                PawnbrokerContainer.this.restockFromInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.lootButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = PawnbrokerContainer.this.INVENTORY_START; i <= PawnbrokerContainer.this.INVENTORY_END; ++i) {
                    if (PawnbrokerContainer.this.getSlot(i).isItemLocked()) continue;
                    PawnbrokerContainer.this.transferToSlots(PawnbrokerContainer.this.getSlot(i), Arrays.asList(new SlotIndexRange(PawnbrokerContainer.this.CLIENT_HOTBAR_START, PawnbrokerContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(PawnbrokerContainer.this.CLIENT_INVENTORY_START, PawnbrokerContainer.this.CLIENT_INVENTORY_END)), "lootall");
                }
            }
        });
        this.sellButton = this.registerAction(new PawnItemsAction());
        this.setIsPawning = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                PawnbrokerContainer.this.isPawning = value;
            }
        });
    }

    @Override
    public void lootAllControlPressed() {
        if (this.isPawning) {
            this.lootButton.runAndSend();
        }
    }

    @Override
    public void quickStackControlPressed() {
        if (this.isPawning) {
            this.quickStackButton.runAndSend();
        }
    }

    public void updateProfit() {
        this.profit = 0.0f;
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (item == null) continue;
            this.profit += item.getBrokerValue();
        }
    }

    public int getProfit() {
        return (int)this.profit;
    }

    public void sellItems() {
        this.updateProfit();
        int profit = this.getProfit();
        if (this.client.isServer()) {
            if (profit > this.shopWealth) {
                profit = this.shopWealth;
            }
            if (profit > 0) {
                int totalItems = 0;
                for (int i = 0; i < this.inventory.getSize(); ++i) {
                    InventoryItem item = this.inventory.getItem(i);
                    if (item == null) continue;
                    totalItems += item.getAmount();
                }
                InventoryItem coins = new InventoryItem("coin", profit);
                this.client.playerMob.getInv().addItemsDropRemaining(coins, "sell", this.client.playerMob, !this.client.isServer(), false, true);
                this.client.getServerClient().newStats.items_sold.increment(totalItems);
                this.client.getServerClient().newStats.money_earned.increment(profit);
                if (this.serverShopManager != null) {
                    this.shopWealth = this.serverShopManager.shopWealth;
                }
                this.shopWealth = Math.max(this.shopWealth - profit, 0);
                if (this.serverShopManager != null) {
                    this.serverShopManager.shopWealth = this.shopWealth;
                    new ShopWealthUpdateEvent(this.serverShopManager).applyAndSendToAllClients(this.client.getServerClient().getServer());
                }
            }
        } else if (this.client.isClient()) {
            if (profit > this.shopWealth) {
                profit = this.shopWealth;
            }
            if (profit > 0) {
                InventoryItem coins = new InventoryItem("coin", profit);
                this.client.playerMob.getInv().addItemsDropRemaining(coins, "sell", this.client.playerMob, !this.client.isServer(), false, true);
                SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(this.client.playerMob));
                this.shopWealth = Math.max(this.shopWealth - profit, 0);
            }
        }
        this.inventory.clearInventory();
        this.updateProfit();
    }

    @Override
    public void onClose() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (item == null) continue;
            this.client.playerMob.getInv().addItemsDropRemaining(item, "itempickup", this.client.playerMob, !this.client.isServer(), false);
        }
        this.inventory.clearInventory();
        super.onClose();
    }

    public static ShopContainerData getBrokerContainerContent(PawnBrokerHumanMob mob, ServerClient client) {
        ShopContainerData baseData = mob.getShopContainerData(client);
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(baseData.content);
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(20));
        return new ShopContainerData(packet, baseData.shopManager);
    }

    public class PawnItemsAction
    extends ContainerCustomAction {
        public void runAndSend(int expectedWealth) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextInt(expectedWealth);
            this.runAndSendAction(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int expectedWealth = reader.getNextInt();
            if (PawnbrokerContainer.this.serverShopManager != null) {
                PawnbrokerContainer.this.shopWealth = PawnbrokerContainer.this.serverShopManager.shopWealth;
            }
            if (PawnbrokerContainer.this.shopWealth != expectedWealth) {
                if (PawnbrokerContainer.this.client.isServer()) {
                    new ShopWealthUpdateEvent(PawnbrokerContainer.this.serverShopManager).applyAndSendToClient(PawnbrokerContainer.this.client.getServerClient());
                    PawnbrokerContainer.this.client.getServerClient().playerMob.getInv().markFullDirty();
                }
            } else {
                PawnbrokerContainer.this.sellItems();
            }
        }
    }
}

