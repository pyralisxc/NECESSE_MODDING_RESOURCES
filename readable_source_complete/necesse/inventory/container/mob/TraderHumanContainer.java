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
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.entity.mobs.friendly.human.humanShop.TraderHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.TradingMission;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.ContainerSlot;

public class TraderHumanContainer
extends ShopContainer {
    public final boolean canDoTradingMission;
    public boolean isInTradingForm;
    public PlayerTempInventory inventory;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;
    private float profit;
    public final EmptyCustomAction quickStackButton;
    public final EmptyCustomAction transferAll;
    public final EmptyCustomAction restockButton;
    public final EmptyCustomAction lootButton;
    public final StartTradingMissionAction startMissionAction;
    public final BooleanCustomAction setIsInTradingForm;

    public TraderHumanContainer(final NetworkClient client, int uniqueSeed, HumanShop mob, PacketReader reader, ShopContainerData serverData) {
        super(client, uniqueSeed, mob, reader.getNextContentPacket(), serverData);
        this.canDoTradingMission = reader.getNextBoolean();
        this.inventory = client.playerMob.getInv().applyTempInventoryPacket(reader.getNextContentPacket(), (player, size, invID) -> new PlayerTempInventory(player, size, invID){

            @Override
            public boolean shouldDispose() {
                return TraderHumanContainer.this.isClosed();
            }

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                TraderHumanContainer.this.updateProfit();
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
        this.addInventoryQuickTransfer(s -> this.isInTradingForm, this.INVENTORY_START, this.INVENTORY_END);
        this.quickStackButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(TraderHumanContainer.this.inventory)));
                TraderHumanContainer.this.quickStackToInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.transferAll = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = TraderHumanContainer.this.CLIENT_INVENTORY_START; i <= TraderHumanContainer.this.CLIENT_INVENTORY_END; ++i) {
                    if (TraderHumanContainer.this.getSlot(i).isItemLocked()) continue;
                    TraderHumanContainer.this.transferToSlots(TraderHumanContainer.this.getSlot(i), TraderHumanContainer.this.INVENTORY_START, TraderHumanContainer.this.INVENTORY_END, "transferall");
                }
            }
        });
        this.restockButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(TraderHumanContainer.this.inventory)));
                TraderHumanContainer.this.restockFromInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.lootButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = TraderHumanContainer.this.INVENTORY_START; i <= TraderHumanContainer.this.INVENTORY_END; ++i) {
                    if (TraderHumanContainer.this.getSlot(i).isItemLocked()) continue;
                    TraderHumanContainer.this.transferToSlots(TraderHumanContainer.this.getSlot(i), Arrays.asList(new SlotIndexRange(TraderHumanContainer.this.CLIENT_HOTBAR_START, TraderHumanContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(TraderHumanContainer.this.CLIENT_INVENTORY_START, TraderHumanContainer.this.CLIENT_INVENTORY_END)), "lootall");
                }
            }
        });
        this.startMissionAction = this.registerAction(new StartTradingMissionAction());
        this.setIsInTradingForm = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                TraderHumanContainer.this.isInTradingForm = value;
            }
        });
    }

    @Override
    public void lootAllControlPressed() {
        if (this.isInTradingForm) {
            this.lootButton.runAndSend();
        }
    }

    @Override
    public void quickStackControlPressed() {
        if (this.isInTradingForm) {
            this.quickStackButton.runAndSend();
        }
    }

    public float calculateProfit() {
        float profit = 0.0f;
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (item == null) continue;
            profit += item.getBrokerValue();
        }
        return profit;
    }

    public void updateProfit() {
        this.profit = this.calculateProfit();
    }

    public int getProfit() {
        return (int)this.profit;
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

    public static ShopContainerData getMerchantContainerContent(TraderHumanMob mob, ServerClient client) {
        ShopContainerData baseData = mob.getShopContainerData(client);
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(baseData.content);
        writer.putNextBoolean(mob.isSettlerWithinSettlement() && !mob.isMovingOut());
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(20));
        return new ShopContainerData(packet, baseData.shopManager);
    }

    public class StartTradingMissionAction
    extends ContainerCustomAction {
        public void runAndSend() {
            this.runAndSendAction(new Packet());
        }

        @Override
        public void executePacket(PacketReader reader) {
            if (!TraderHumanContainer.this.canDoTradingMission) {
                return;
            }
            if (TraderHumanContainer.this.client.isServer()) {
                if (!TraderHumanContainer.this.humanShop.isSettlerWithinSettlement() || TraderHumanContainer.this.humanShop.isMovingOut()) {
                    TraderHumanContainer.this.close();
                    TraderHumanContainer.this.client.getServerClient().playerMob.getInv().markFullDirty();
                    return;
                }
                int profit = (int)TraderHumanContainer.this.calculateProfit();
                ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
                boolean foundAnyItems = false;
                for (int i = 0; i < TraderHumanContainer.this.inventory.getSize(); ++i) {
                    InventoryItem item = TraderHumanContainer.this.inventory.getItem(i);
                    if (item == null) continue;
                    items.add(item.copy());
                    foundAnyItems = true;
                }
                if (TraderHumanContainer.this.humanShop.startMission(new TradingMission(items, profit, false))) {
                    TraderHumanContainer.this.inventory.clearInventory();
                }
                TraderHumanContainer.this.close();
                if (foundAnyItems) {
                    TraderHumanContainer.this.client.getServerClient().playerMob.getInv().markFullDirty();
                }
            } else {
                TraderHumanContainer.this.inventory.clearInventory();
            }
        }
    }
}

