/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.HumanLook;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.events.StylistSettlersUpdateContainerEvent;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.WigArmorItem;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class StylistContainer
extends ShopContainer {
    private static final int HAIR_COST = 200;
    private static final int FACIAL_FEATURE_COST = 200;
    private static final int HAIR_COLOR_COST = 100;
    private static final int SHIRT_COLOR_COST = 200;
    private static final int SHOES_COLOR_COST = 100;
    public final ContentCustomAction playerStyleButton;
    public final ContentCustomAction settlerStyleButton;
    public final EmptyCustomAction styleButtonResponse;
    public StylistHumanMob stylistMob;
    public final long styleCostSeed;
    public ArrayList<HumanMob> availableSettlers;

    public StylistContainer(final NetworkClient client, int uniqueSeed, final StylistHumanMob mob, PacketReader contentReader, ShopContainerData serverData) {
        super(client, uniqueSeed, mob, contentReader.getNextContentPacket(), serverData);
        this.stylistMob = mob;
        this.styleCostSeed = this.priceSeed * (long)GameRandom.prime(42);
        this.availableSettlers = new StylistSettlersUpdateContainerEvent(contentReader).getHumanMobs(mob.getLevel());
        this.subscribeEvent(StylistSettlersUpdateContainerEvent.class, e -> true, () -> true);
        this.onEvent(StylistSettlersUpdateContainerEvent.class, (T e) -> {
            this.availableSettlers = e.getHumanMobs(mob.getLevel());
        });
        this.playerStyleButton = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                HumanLook newLook;
                ArrayList<InventoryItem> cost;
                if (client.isServer() && StylistContainer.this.canStyle(cost = StylistContainer.this.getTotalStyleCost(client.playerMob.look, newLook = new HumanLook(new PacketReader(content))))) {
                    ServerClient serverClient = client.getServerClient();
                    for (InventoryItem item : cost) {
                        client.playerMob.getInv().main.removeItems(client.playerMob.getLevel(), client.playerMob, item.item, item.getAmount(), "buy");
                        if (!item.item.getStringID().equals("coin")) continue;
                        serverClient.newStats.money_spent.increment(item.getAmount());
                    }
                    client.playerMob.look = new HumanLook(newLook);
                    serverClient.getServer().network.sendToAllClients(new PacketPlayerAppearance(serverClient));
                    StylistContainer.this.styleButtonResponse.runAndSend();
                    if (serverClient.achievementsLoaded()) {
                        serverClient.achievements().FEELING_STYLISH.markCompleted(serverClient);
                    }
                }
            }
        });
        this.settlerStyleButton = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                if (client.isServer()) {
                    PacketReader reader = new PacketReader(content);
                    int mobUniqueID = reader.getNextInt();
                    HumanLook newLook = new HumanLook(reader);
                    ServerClient serverClient = client.getServerClient();
                    ServerSettlementData settlementData = mob.getSettlerSettlementServerData();
                    if (settlementData == null) {
                        new StylistSettlersUpdateContainerEvent(StylistContainer.this.stylistMob, serverClient).applyAndSendToClient(serverClient);
                        return;
                    }
                    if (!settlementData.networkData.doesClientHaveAccess(serverClient)) {
                        new StylistSettlersUpdateContainerEvent(StylistContainer.this.stylistMob, serverClient).applyAndSendToClient(serverClient);
                        return;
                    }
                    LevelSettler settler = settlementData.getSettler(mobUniqueID);
                    if (settler == null) {
                        new StylistSettlersUpdateContainerEvent(StylistContainer.this.stylistMob, serverClient).applyAndSendToClient(serverClient);
                        return;
                    }
                    SettlerMob mob2 = settler.getMob();
                    if (!(mob2 instanceof HumanMob)) {
                        new StylistSettlersUpdateContainerEvent(StylistContainer.this.stylistMob, serverClient).applyAndSendToClient(serverClient);
                        return;
                    }
                    HumanMob humanMob = (HumanMob)mob2;
                    ArrayList<InventoryItem> cost = StylistContainer.this.getTotalStyleCost(humanMob.look, newLook);
                    if (StylistContainer.this.canStyle(cost)) {
                        for (InventoryItem item : cost) {
                            client.playerMob.getInv().main.removeItems(client.playerMob.getLevel(), client.playerMob, item.item, item.getAmount(), "buy");
                            if (!item.item.getStringID().equals("coin")) continue;
                            serverClient.newStats.money_spent.increment(item.getAmount());
                        }
                        humanMob.customLook = true;
                        humanMob.look = new HumanLook(newLook);
                        serverClient.getServer().network.sendToClientsWithEntity(new PacketSpawnMob(humanMob), humanMob);
                        StylistContainer.this.styleButtonResponse.runAndSend();
                    }
                    new StylistSettlersUpdateContainerEvent(StylistContainer.this.stylistMob, serverClient).applyAndSendToClient(serverClient);
                }
            }
        });
        this.styleButtonResponse = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isClient()) {
                    SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(client.playerMob));
                }
            }
        });
    }

    private void updatePlayerInventory(PlayerInventory inventory, HumanLook oldLook) {
        for (int i = 0; i < inventory.getSize(); ++i) {
            this.updatePlayerSlot(new InventorySlot(inventory, i), oldLook, inventory.player.look);
        }
    }

    private void updatePlayerSlot(InventorySlot slot, HumanLook oldLook, HumanLook newLook) {
        if (slot.isSlotClear()) {
            return;
        }
        InventoryItem item = slot.getItem();
        if (item.item.getStringID().equals("wig")) {
            if (WigArmorItem.getHair(item.getGndData()) == oldLook.getHair() && WigArmorItem.getHairCol(item.getGndData()) == oldLook.getHairColor()) {
                WigArmorItem.addWigData(item, newLook);
                slot.markDirty();
            }
        } else if (item.item.getStringID().equals("shirt")) {
            if (ShirtArmorItem.getColor(item.getGndData()).equals(oldLook.getShirtColor())) {
                ShirtArmorItem.addColorData(item, newLook.getShirtColor());
                slot.markDirty();
            }
        } else if (item.item.getStringID().equals("shoes") && ShoesArmorItem.getColor(item.getGndData()).equals(oldLook.getShoesColor())) {
            ShoesArmorItem.addColorData(item, newLook.getShoesColor());
            slot.markDirty();
        }
    }

    public ArrayList<InventoryItem> getTotalStyleCost(HumanLook previousLook, HumanLook newLook) {
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        boolean changed = this.addToList(items, this.getSkinColorCost(previousLook.getSkin(), newLook.getSkin()));
        changed = this.addToList(items, this.getEyeTypeCost(previousLook.getEyeType(), newLook.getEyeType())) || changed;
        changed = this.addToList(items, this.getEyeColorCost(previousLook.getEyeColor(), newLook.getEyeColor())) || changed;
        changed = this.addToList(items, this.getHairStyleCost(previousLook.getHair(), newLook.getHair())) || changed;
        changed = this.addToList(items, this.getFacialFeatureCost(previousLook.getFacialFeature(), newLook.getFacialFeature())) || changed;
        changed = this.addToList(items, this.getHairColorCost(previousLook.getHairColor(), newLook.getHairColor())) || changed;
        changed = this.addToList(items, this.getShirtColorCost(previousLook.getShirtColor(), newLook.getShirtColor())) || changed;
        boolean bl = changed = this.addToList(items, this.getShoesColorCost(previousLook.getShoesColor(), newLook.getShoesColor())) || changed;
        if (!changed) {
            return null;
        }
        return items;
    }

    public boolean addToList(ArrayList<InventoryItem> items, ArrayList<InventoryItem> append) {
        if (append == null) {
            return false;
        }
        for (InventoryItem item : append) {
            item.combineOrAddToList(this.client.playerMob.getLevel(), this.client.playerMob, items, "add");
        }
        return true;
    }

    public ArrayList<InventoryItem> getSkinColorCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("voidshard", 10)));
    }

    public ArrayList<InventoryItem> getEyeTypeCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("voidshard", 5)));
    }

    public ArrayList<InventoryItem> getEyeColorCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("voidshard", 3)));
    }

    public ArrayList<InventoryItem> getHairStyleCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(24) + (long)newID * (long)GameRandom.prime(82), 200))));
    }

    public ArrayList<InventoryItem> getFacialFeatureCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(29) + (long)newID * (long)GameRandom.prime(32), 200))));
    }

    public ArrayList<InventoryItem> getHairColorCost(int oldID, int newID) {
        if (oldID == newID) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(67) + (long)newID * (long)GameRandom.prime(817), 100))));
    }

    public ArrayList<InventoryItem> getShirtColorCost(Color oldColor, Color newColor) {
        if (newColor != null && oldColor.getRGB() == newColor.getRGB()) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(466), 200))));
    }

    public ArrayList<InventoryItem> getShoesColorCost(Color oldColor, Color newColor) {
        if (newColor != null && oldColor.getRGB() == newColor.getRGB()) {
            return null;
        }
        return new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(576), 100))));
    }

    private int getRandomPrice(long seed, int middlePrice) {
        return HumanShop.getRandomHappinessMiddlePrice(new GameRandom(seed), this.settlerHappiness, middlePrice, 2, 4);
    }

    public boolean canStyle(ArrayList<InventoryItem> cost) {
        if (cost == null) {
            return false;
        }
        for (InventoryItem item : cost) {
            if (this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, item.item, "buy") >= item.getAmount()) continue;
            return false;
        }
        return true;
    }

    public static ShopContainerData getStylistContainerContent(StylistHumanMob mob, ServerClient client) {
        ShopContainerData baseData = mob.getShopContainerData(client);
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(baseData.content);
        new StylistSettlersUpdateContainerEvent(mob, client).write(writer);
        return new ShopContainerData(packet, baseData.shopManager);
    }
}

