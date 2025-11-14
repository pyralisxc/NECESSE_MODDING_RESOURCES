/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.ExpeditionMission;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkContainerHandler;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSettingRegistry;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.TypeParsers;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.EnumCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.customAction.PointCustomAction;
import necesse.inventory.container.events.FullShopStockUpdateEvent;
import necesse.inventory.container.events.ShopContainerQuestUpdateEvent;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.events.SingleShopStockUpdateEvent;
import necesse.inventory.container.mob.ContainerExpedition;
import necesse.inventory.container.mob.ContainerQuest;
import necesse.inventory.container.mob.MobContainer;
import necesse.inventory.container.mob.NetworkBuyingShopItem;
import necesse.inventory.container.mob.NetworkSellingShopItem;
import necesse.inventory.container.mob.ShopContainerPartyResponseEvent;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.container.slots.ArmorContainerSlot;
import necesse.inventory.container.slots.PartyInventoryContainerSlot;
import necesse.inventory.container.slots.SettlerWeaponContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ShopContainer
extends MobContainer {
    public HumanShop humanShop;
    public boolean hasSettlerAccess;
    public boolean isSettlerOutsideSettlement;
    public boolean canJoinAdventureParties;
    public boolean isInAdventureParty;
    public boolean isInYourAdventureParty;
    public boolean isInPartyConfig;
    public final BooleanCustomAction setIsInPartyConfig;
    public int PARTY_SLOTS_START = -1;
    public int PARTY_SLOTS_END = -1;
    public final long priceSeed;
    public GameMessage workInvMessage;
    public GameMessage missionFailedMessage;
    public GameMessage introMessage;
    public boolean hungerStrike;
    public ArrayList<ContainerQuest> quests = new ArrayList();
    public int settlerHappiness = 50;
    public ArrayList<HappinessModifier> happinessModifiers;
    public ShopManager serverShopManager;
    public int shopWealth;
    public LinkedHashMap<Integer, NetworkSellingShopItem> sellingItems;
    public LinkedHashMap<Integer, NetworkBuyingShopItem> buyingItems;
    public ArrayList<ContainerWorkSetting> workSettings;
    public GameMessage recruitError;
    public ArrayList<InventoryItem> recruitItems;
    public final boolean startInRecruitment;
    public ArrayList<ExpeditionList> expeditionLists;
    public boolean isInEquipment;
    public final BooleanCustomAction setIsInEquipment;
    public final BooleanCustomAction setSelfManageEquipment;
    public int EQUIPMENT_HEAD_SLOT = -1;
    public int EQUIPMENT_CHEST_SLOT = -1;
    public int EQUIPMENT_FEET_SLOT = -1;
    public int EQUIPMENT_COSM_HEAD_SLOT = -1;
    public int EQUIPMENT_COSM_CHEST_SLOT = -1;
    public int EQUIPMENT_COSM_FEET_SLOT = -1;
    public int EQUIPMENT_WEAPON_SLOT = -1;
    public final EmptyCustomAction continueFailedMissionAction;
    public final EnumCustomAction<WorkItemsAction> workItemsAction;
    public final IntCustomAction questTakeButton;
    public final IntCustomAction questCompleteButton;
    public final IntCustomAction questSkipButton;
    public final EmptyCustomAction acceptRecruitAction;
    public final EmptyCustomAction joinAdventurePartyAction;
    public final EmptyCustomAction leaveAdventurePartyAction;
    public final EmptyCustomAction returnToSettlementAction;
    public final BuyItemAction buyItemAction;
    public final SellItemAction sellItemAction;
    public final PointCustomAction buyExpeditionButton;
    private final ContentCustomAction workSettingAction;

    public ShopContainer(final NetworkClient client, int uniqueSeed, final HumanShop mob, Packet contentPacket, ShopContainerData serverData) {
        super(client, uniqueSeed, mob);
        int i;
        this.humanShop = mob;
        client.playerMob.getInv().party.compressItems();
        PacketReader reader = new PacketReader(contentPacket);
        this.hasSettlerAccess = reader.getNextBoolean();
        this.isSettlerOutsideSettlement = reader.getNextBoolean();
        if (this.hasSettlerAccess) {
            new ShopContainerPartyUpdateEvent(reader).applyUpdate(this);
            AdventureParty party = null;
            if (client.isServer()) {
                party = client.getServerClient().adventureParty;
            } else if (client.isClient() && client.getClientClient().isLocalClient()) {
                party = client.getClientClient().getClient().adventureParty;
            }
            for (i = 0; i < PlayerInventoryManager.MAX_PARTY_INVENTORY_SIZE; ++i) {
                int index = this.addSlot(new PartyInventoryContainerSlot(client.playerMob.getInv().party, i, party));
                if (this.PARTY_SLOTS_START == -1) {
                    this.PARTY_SLOTS_START = index;
                }
                if (this.PARTY_SLOTS_END == -1) {
                    this.PARTY_SLOTS_END = index;
                }
                this.PARTY_SLOTS_START = Math.min(this.PARTY_SLOTS_START, index);
                this.PARTY_SLOTS_END = Math.max(this.PARTY_SLOTS_END, index);
            }
            this.addInventoryQuickTransfer(s -> this.isInPartyConfig, this.PARTY_SLOTS_START, this.PARTY_SLOTS_END);
        }
        this.priceSeed = reader.getNextLong();
        if (reader.getNextBoolean()) {
            this.workInvMessage = GameMessage.fromPacket(reader);
        }
        if (reader.getNextBoolean()) {
            this.missionFailedMessage = GameMessage.fromPacket(reader);
        }
        this.introMessage = GameMessage.fromPacket(reader);
        if (this.hasSettlerAccess) {
            this.hungerStrike = reader.getNextBoolean();
            int size = reader.getNextShortUnsigned();
            this.happinessModifiers = new ArrayList(size);
            this.settlerHappiness = 0;
            for (i = 0; i < size; ++i) {
                HappinessModifier happiness = new HappinessModifier(reader);
                this.happinessModifiers.add(happiness);
                this.settlerHappiness += happiness.happiness;
            }
            this.happinessModifiers.sort(Comparator.comparingInt(m -> -m.happiness));
            this.settlerHappiness = GameMath.limit(this.settlerHappiness, 0, 100);
        }
        this.quests = this.replaceContainerQuestsWithManager(new ShopContainerQuestUpdateEvent((PacketReader)reader).quests);
        this.serverShopManager = serverData == null ? null : serverData.shopManager;
        this.subscribeEvent(ShopWealthUpdateEvent.class, e -> this.serverShopManager != null && e.managerUniqueID == this.serverShopManager.uniqueID, () -> true);
        this.onEvent(ShopWealthUpdateEvent.class, (T e) -> {
            this.shopWealth = e.shopWealth;
        });
        this.shopWealth = reader.getNextInt();
        if (reader.getNextBoolean()) {
            if (client.isServer() && this.serverShopManager == null) {
                throw new IllegalArgumentException("Servers shop registry cannot be null");
            }
            this.subscribeEvent(FullShopStockUpdateEvent.class, e -> e.managerUniqueID == this.serverShopManager.uniqueID, () -> true);
            this.onEvent(FullShopStockUpdateEvent.class, (T e) -> {
                this.shopWealth = e.shopWealth;
                for (Map.Entry<Integer, Integer> entry : e.shopItemIDsStock.entrySet()) {
                    NetworkSellingShopItem item = this.sellingItems.get(entry.getKey());
                    if (item == null) continue;
                    item.currentStock = entry.getValue();
                }
            });
            this.subscribeEvent(SingleShopStockUpdateEvent.class, e -> e.managerUniqueID == this.serverShopManager.uniqueID && this.sellingItems.containsKey(e.shopItemID), () -> true);
            this.onEvent(SingleShopStockUpdateEvent.class, (T e) -> {
                NetworkSellingShopItem item = this.sellingItems.get(e.shopItemID);
                if (item != null) {
                    item.currentStock = e.currentStock;
                }
                if (e.shopWealth >= 0) {
                    this.shopWealth = e.shopWealth;
                }
            });
            int totalItems = reader.getNextShortUnsigned();
            this.sellingItems = new LinkedHashMap(totalItems);
            for (i = 0; i < totalItems; ++i) {
                NetworkSellingShopItem item = new NetworkSellingShopItem(reader);
                this.sellingItems.put(item.shopItemID, item);
            }
        } else {
            this.sellingItems = null;
        }
        if (reader.getNextBoolean()) {
            if (client.isServer() && this.serverShopManager == null) {
                throw new IllegalArgumentException("Servers shop registry cannot be null");
            }
            int totalItems = reader.getNextShortUnsigned();
            this.buyingItems = new LinkedHashMap(totalItems);
            for (i = 0; i < totalItems; ++i) {
                NetworkBuyingShopItem item = new NetworkBuyingShopItem(reader);
                if (item.item == null) continue;
                this.buyingItems.put(item.shopItemID, item);
            }
        } else {
            this.buyingItems = null;
        }
        if (reader.getNextBoolean()) {
            this.recruitError = GameMessage.fromContentPacket(reader.getNextContentPacket());
        } else if (reader.getNextBoolean()) {
            int totalRecruitItems = reader.getNextShortUnsigned();
            this.recruitItems = new ArrayList(totalRecruitItems);
            for (i = 0; i < totalRecruitItems; ++i) {
                this.recruitItems.add(InventoryItem.fromContentPacket(reader));
            }
        }
        this.startInRecruitment = reader.getNextBoolean();
        int expeditionsListLength = reader.getNextShortUnsigned();
        this.expeditionLists = new ArrayList(expeditionsListLength);
        for (i = 0; i < expeditionsListLength; ++i) {
            this.expeditionLists.add(new ExpeditionList(reader));
        }
        this.workSettings = new ArrayList();
        for (HumanWorkSetting<?> setting : mob.workSettings.getSettings()) {
            this.workSettings.add(new ContainerWorkSetting(setting, setting.readContainer(this, reader)));
        }
        this.continueFailedMissionAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isServer()) {
                    ShopContainer.this.humanShop.clearMissionResult();
                }
            }
        });
        this.workItemsAction = this.registerAction(new EnumCustomAction<WorkItemsAction>(WorkItemsAction.class){

            @Override
            protected void run(WorkItemsAction value) {
                ShopContainer.this.handleWorkItemsAction(value);
            }
        });
        this.subscribeEvent(ShopContainerQuestUpdateEvent.class, e -> true, () -> true);
        this.onEvent(ShopContainerQuestUpdateEvent.class, (T e) -> {
            this.quests = this.replaceContainerQuestsWithManager(e.quests);
        });
        this.questTakeButton = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                ContainerQuest cq;
                if (client.isClient()) {
                    client.getClientClient().getClient().tutorial.questAccepted();
                }
                if (client.isServer() && (cq = (ContainerQuest)ShopContainer.this.quests.stream().filter(q -> q.quest != null && q.quest.getUniqueID() == value).findFirst().orElse(null)) != null && cq.quest != null) {
                    ServerClient serverClient = client.getServerClient();
                    cq.quest.makeActiveFor(serverClient.getServer(), serverClient);
                    if (serverClient.achievementsLoaded()) {
                        serverClient.achievements().ADVENTURE_BEGINS.markCompleted(serverClient);
                    }
                }
            }
        });
        this.questCompleteButton = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                ContainerQuest cq;
                if (client.isClient()) {
                    client.getClientClient().getClient().tutorial.questAccepted();
                }
                if (client.isServer() && (cq = (ContainerQuest)ShopContainer.this.quests.stream().filter(q -> q.quest != null && q.quest.getUniqueID() == value).findFirst().orElse(null)) != null && cq.quest != null) {
                    ServerClient serverClient = client.getServerClient();
                    if (cq.quest.canComplete(client)) {
                        if (ShopContainer.this.humanShop.completeQuest(serverClient, value) && serverClient.achievementsLoaded()) {
                            serverClient.achievements().ADVENTURE_BEGINS.markCompleted(serverClient);
                        }
                        new ShopContainerQuestUpdateEvent(ShopContainer.this.humanShop.getQuests(serverClient)).applyAndSendToClient(serverClient);
                    }
                }
            }
        });
        this.questSkipButton = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                ServerClient serverClient;
                ContainerQuest cq;
                if (client.isServer() && (cq = (ContainerQuest)ShopContainer.this.quests.stream().filter(q -> q.quest != null && q.quest.getUniqueID() == value).findFirst().orElse(null)) != null && cq.quest != null && cq.canSkip && cq.skipError == null && !ShopContainer.this.humanShop.skipQuest(serverClient = client.getServerClient(), value)) {
                    new ShopContainerQuestUpdateEvent(ShopContainer.this.humanShop.getQuests(serverClient)).applyAndSendToClient(serverClient);
                }
            }
        });
        this.subscribeEvent(ShopContainerPartyUpdateEvent.class, update -> this.hasSettlerAccess && update.mobUniqueID == mob.getUniqueID(), () -> true);
        this.onEvent(ShopContainerPartyUpdateEvent.class, (T event) -> event.applyUpdate(this));
        this.subscribeEvent(ShopContainerPartyResponseEvent.class, update -> true, () -> true);
        this.joinAdventurePartyAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (ShopContainer.this.hasSettlerAccess && ShopContainer.this.canJoinAdventureParties && client.isServer()) {
                    GameMessage message = ShopContainer.this.humanShop.willJoinAdventureParty(client.getServerClient());
                    new ShopContainerPartyResponseEvent(message).applyAndSendToClient(client.getServerClient());
                    if (message == null) {
                        mob.commandFollow(client.getServerClient(), client.playerMob);
                        mob.adventureParty.set(client.getServerClient());
                    }
                }
            }
        });
        this.leaveAdventurePartyAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (ShopContainer.this.hasSettlerAccess && client.isServer() && mob.adventureParty.getServerClient() == client) {
                    mob.adventureParty.clear(true);
                    if (mob.isSettlerWithinSettlement()) {
                        mob.clearCommandsOrders(client.getServerClient());
                    } else if (mob.getHealthPercent() <= 0.5f) {
                        SettlersWorldData settlersData = SettlersWorldData.getSettlersData(mob.getLevel().getServer());
                        settlersData.returnToSettlement(mob, false);
                    }
                    ShopContainer.this.close();
                }
            }
        });
        this.returnToSettlementAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (ShopContainer.this.hasSettlerAccess && client.isServer() && ShopContainer.this.isSettlerOutsideSettlement) {
                    if (mob.adventureParty.isInAdventureParty()) {
                        mob.adventureParty.clear(false);
                    }
                    SettlersWorldData settlersData = SettlersWorldData.getSettlersData(mob.getLevel().getServer());
                    settlersData.returnToSettlement(mob, false);
                    ShopContainer.this.close();
                }
            }
        });
        this.acceptRecruitAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    int recruitedToSettlementUniqueID = ShopContainer.this.humanShop.getRecruitedToSettlementUniqueID(serverClient);
                    Server server = serverClient.getServer();
                    if (recruitedToSettlementUniqueID != 0) {
                        PacketShopContainerUpdate.recruitSettler(server, serverClient, ShopContainer.this, () -> SettlementsWorldData.getSettlementsData(server).getOrLoadServerData(recruitedToSettlementUniqueID));
                    } else if (ShopContainer.this.canPayForRecruit()) {
                        SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
                        List<CachedSettlementData> settlements = worldData.collectCachedSettlements(data -> data.hasAccess(serverClient) && mob.isValidRecruitment((CachedSettlementData)data, serverClient) && data.getName() != null);
                        if (settlements.size() == 1) {
                            CachedSettlementData settlement = settlements.get(0);
                            PacketShopContainerUpdate.recruitSettler(server, serverClient, ShopContainer.this, () -> worldData.getOrLoadServerData(settlement.uniqueID));
                        } else {
                            serverClient.sendPacket(PacketShopContainerUpdate.settlementsList(server, serverClient, ShopContainer.this.humanShop));
                        }
                    }
                }
            }
        });
        this.buyItemAction = this.registerAction(new BuyItemAction());
        this.sellItemAction = this.registerAction(new SellItemAction());
        this.buyExpeditionButton = this.registerAction(new PointCustomAction(){

            @Override
            protected void run(int listIndex, int expeditionIndex) {
                if (listIndex >= 0 && listIndex < ShopContainer.this.expeditionLists.size()) {
                    ExpeditionList list = ShopContainer.this.expeditionLists.get(listIndex);
                    if (expeditionIndex >= 0 && expeditionIndex < list.expeditions.size()) {
                        ContainerExpedition expedition = list.expeditions.get(expeditionIndex);
                        int price = expedition.price;
                        if (price == 0 || price > 0 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("coin"), true, true, false, false, "buy") >= price) {
                            if (price > 0) {
                                client.playerMob.getInv().removeItems(ItemRegistry.getItem("coin"), price, true, true, false, false, "buy");
                            }
                            if (client.isClient()) {
                                SoundManager.playSound(GameResources.coins, SoundEffect.globalEffect());
                            } else if (client.isServer()) {
                                System.out.println(client.getName() + " bought " + expedition.expedition.getDisplayName().translate() + " expedition for " + price + " coins");
                                client.getServerClient().closeContainer(true);
                                ShopContainer.this.humanShop.startMission(new ExpeditionMission(expedition.expedition, price, expedition.successChance, false));
                            }
                        }
                    }
                }
            }
        });
        this.workSettingAction = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                if (client.isServer()) {
                    mob.workSettingAction.runAndSend(content);
                }
            }
        });
        if (this.hasSettlerAccess) {
            Inventory equipmentInventory = mob.getInventory();
            this.EQUIPMENT_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 0, ArmorItem.ArmorType.HEAD));
            this.EQUIPMENT_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 1, ArmorItem.ArmorType.CHEST));
            this.EQUIPMENT_FEET_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 2, ArmorItem.ArmorType.FEET));
            this.EQUIPMENT_COSM_HEAD_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 3, ArmorItem.ArmorType.HEAD));
            this.EQUIPMENT_COSM_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 4, ArmorItem.ArmorType.CHEST));
            this.EQUIPMENT_COSM_FEET_SLOT = this.addSlot(new ArmorContainerSlot(equipmentInventory, 5, ArmorItem.ArmorType.FEET));
            this.EQUIPMENT_WEAPON_SLOT = this.addSlot(new SettlerWeaponContainerSlot(equipmentInventory, 6, mob));
            this.addInventoryQuickTransfer(s -> this.isInEquipment, this.EQUIPMENT_HEAD_SLOT, this.EQUIPMENT_WEAPON_SLOT);
        }
        this.setIsInPartyConfig = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                ShopContainer.this.isInPartyConfig = value;
            }
        });
        this.setIsInEquipment = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                ShopContainer.this.isInEquipment = value;
            }
        });
        this.setSelfManageEquipment = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                if (ShopContainer.this.hasSettlerAccess) {
                    mob.selfManageEquipment.set(value);
                }
            }
        });
    }

    public void handleWorkItemsAction(WorkItemsAction action) {
        if (action == WorkItemsAction.RECEIVE) {
            for (InventoryItem item : this.humanShop.workInventory) {
                item.setNew(true);
                this.client.playerMob.getInv().addItemsDropRemaining(item, "transfer", this.humanShop, !this.client.isServer(), false);
            }
            this.humanShop.workInventory.clear();
        }
        if (this.client.isServer()) {
            this.humanShop.clearMissionResult();
        }
    }

    public GameMessage getWorkInvMessage() {
        if (this.workInvMessage != null) {
            GameMessageBuilder builder = new GameMessageBuilder();
            builder.append(this.workInvMessage);
            for (InventoryItem item : this.humanShop.workInventory) {
                builder.append("\n ").append(TypeParsers.getItemParseString(item) + "x" + item.getAmount() + " ").append(item.getItemLocalization());
            }
            return builder;
        }
        return this.workInvMessage;
    }

    public GameMessage getRecruitMessage() {
        if (this.recruitItems != null) {
            if (this.recruitItems.isEmpty()) {
                return new LocalMessage("ui", this.humanShop.isDowned() ? "settlerrevivefree" : "settlerrecruitfree");
            }
            GameMessageBuilder builder = new GameMessageBuilder();
            for (InventoryItem item : this.recruitItems) {
                builder.append("\n ").append(TypeParsers.getItemParseString(item) + "x" + item.getAmount() + " ").append(item.getItemLocalization());
            }
            return new LocalMessage("ui", this.humanShop.isDowned() ? "settlerrevivecost" : "settlerrecruitcost", "cost", builder);
        }
        return this.recruitError;
    }

    public boolean canPayForRecruit() {
        if (this.recruitItems == null) {
            return false;
        }
        for (InventoryItem recruitItem : this.recruitItems) {
            int items = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, recruitItem.item, "buy");
            if (items >= recruitItem.getAmount()) continue;
            return false;
        }
        return true;
    }

    public void payForRecruit() {
        for (InventoryItem recruitItem : this.recruitItems) {
            this.client.playerMob.getInv().main.removeItems(this.client.playerMob.getLevel(), this.client.playerMob, recruitItem.item, recruitItem.getAmount(), "recruit");
        }
    }

    public ArrayList<ContainerQuest> replaceContainerQuestsWithManager(ArrayList<ContainerQuest> quests) {
        if (quests == null) {
            return quests;
        }
        for (ContainerQuest cq : quests) {
            cq.quest = this.replaceManagerQuest(cq.quest);
        }
        return quests;
    }

    public Quest replaceManagerQuest(Quest quest) {
        Quest existingQuest;
        if (quest == null) {
            return quest;
        }
        QuestManager questManager = null;
        if (this.client.isServer()) {
            questManager = this.client.getServerClient().getServer().world.getQuests();
        } else if (this.client.isClient()) {
            questManager = this.client.getClientClient().getClient().quests;
        }
        if (questManager != null && (existingQuest = questManager.getQuest(quest.getUniqueID())) != null) {
            quest = existingQuest;
        }
        return quest;
    }

    public static Packet getContainerContent(HumanShop mob, ServerClient client, GameMessage workInvMessage, GameMessage missionFailedMessage, GameMessage introMessage, ArrayList<ContainerQuest> quests, ShopManager shopManager, GameMessage recruitError, List<InventoryItem> recruitItems, boolean startInRecruit, List<ExpeditionList> possibleExpeditions, HumanWorkSettingRegistry workSettings) {
        ArrayList<NetworkBuyingShopItem> buyingList;
        ArrayList<NetworkSellingShopItem> sellingList;
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        ServerSettlementData settlementData = mob.isSettler() ? mob.getSettlerSettlementServerData() : null;
        boolean hasAccess = settlementData != null && settlementData.networkData.doesClientHaveAccess(client);
        writer.putNextBoolean(hasAccess);
        writer.putNextBoolean(mob.isSettler() && !mob.isSettlerWithinSettlement());
        if (hasAccess) {
            new ShopContainerPartyUpdateEvent(mob, client).write(writer);
        }
        writer.putNextLong(mob.getShopSeed());
        if (workInvMessage != null) {
            writer.putNextBoolean(true);
            workInvMessage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
        if (missionFailedMessage != null) {
            writer.putNextBoolean(true);
            missionFailedMessage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
        introMessage.writePacket(writer);
        if (hasAccess) {
            writer.putNextBoolean(mob.hungerStrike);
            List<HappinessModifier> happinessModifiers = mob.getHappinessModifiers();
            writer.putNextShortUnsigned(happinessModifiers.size());
            for (HappinessModifier happinessModifier : happinessModifiers) {
                happinessModifier.writePacket(writer);
            }
        }
        new ShopContainerQuestUpdateEvent(quests).write(writer);
        if (shopManager == null) {
            sellingList = null;
            buyingList = null;
        } else {
            sellingList = shopManager.getSellingItemsList(client, mob, mob.getShopSeed());
            buyingList = shopManager.getBuyingItemsList(client, mob, mob.getShopSeed());
        }
        writer.putNextBoolean(sellingList != null);
        writer.putNextInt(shopManager == null ? -1 : shopManager.shopWealth);
        if (sellingList != null) {
            writer.putNextShortUnsigned(sellingList.size());
            for (NetworkSellingShopItem networkSellingShopItem : sellingList) {
                networkSellingShopItem.writePacket(writer);
            }
        }
        writer.putNextBoolean(buyingList != null);
        if (buyingList != null) {
            writer.putNextShortUnsigned(buyingList.size());
            for (NetworkBuyingShopItem networkBuyingShopItem : buyingList) {
                networkBuyingShopItem.writePacket(writer);
            }
        }
        writer.putNextBoolean(recruitError != null);
        if (recruitError != null) {
            writer.putNextContentPacket(recruitError.getContentPacket());
        } else {
            writer.putNextBoolean(recruitItems != null);
            if (recruitItems != null) {
                writer.putNextShortUnsigned(recruitItems.size());
                for (InventoryItem inventoryItem : recruitItems) {
                    InventoryItem.addPacketContent(inventoryItem, writer);
                }
            }
        }
        writer.putNextBoolean(startInRecruit);
        writer.putNextShortUnsigned(possibleExpeditions.size());
        for (ExpeditionList expeditionList : possibleExpeditions) {
            expeditionList.writePacket(writer);
        }
        for (HumanWorkSetting humanWorkSetting : workSettings.getSettings()) {
            humanWorkSetting.writeContainerPacket(client, writer);
        }
        return content;
    }

    public class ContainerWorkSetting<T> {
        public final HumanWorkSetting<T> setting;
        public final T data;
        public final HumanWorkContainerHandler handler;

        public ContainerWorkSetting(HumanWorkSetting<T> setting, Object data) {
            this.setting = setting;
            this.data = data;
            this.handler = setting.setupHandler(ShopContainer.this, this);
        }

        public void sendAction(Packet content) {
            Packet settingsContent = new Packet();
            PacketWriter writer = new PacketWriter(settingsContent);
            writer.putNextShortUnsigned(this.setting.getID());
            writer.putNextContentPacket(content);
            ShopContainer.this.workSettingAction.runAndSend(settingsContent);
        }

        public void sendServerOnlyAction(Packet content) {
            Packet settingsContent = new Packet();
            PacketWriter writer = new PacketWriter(settingsContent);
            writer.putNextShortUnsigned(this.setting.getID());
            writer.putNextContentPacket(content);
            ShopContainer.this.workSettingAction.runAndSend(settingsContent);
        }
    }

    public static enum WorkItemsAction {
        RECEIVE,
        CONTINUE;

    }

    public class BuyItemAction
    extends ContainerCustomAction {
        public void runAndSend(NetworkSellingShopItem item, int amount) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextShortUnsigned(item.shopItemID);
            writer.putNextInt(amount);
            this.runAndSendAction(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int shopID = reader.getNextShortUnsigned();
            int amount = reader.getNextInt();
            NetworkSellingShopItem item = ShopContainer.this.sellingItems.get(shopID);
            if (item != null) {
                SellingShopItem serverItem;
                SellingShopItem sellingShopItem = serverItem = ShopContainer.this.serverShopManager == null ? null : ShopContainer.this.serverShopManager.sellingShop.getItem(item.shopItemID);
                if (serverItem != null) {
                    item.currentStock = serverItem.currentStock;
                    ShopContainer.this.shopWealth = ShopContainer.this.serverShopManager.shopWealth;
                }
                int timesCompleted = 0;
                for (int i = 0; i < amount; ++i) {
                    if (item.canCompleteTrade(ShopContainer.this, ShopContainer.this.getCraftInventories())) {
                        int price = item.completeTrade(ShopContainer.this, ShopContainer.this.getCraftInventories());
                        if (ShopContainer.this.shopWealth >= 0) {
                            ShopContainer.this.shopWealth += price;
                            if (ShopContainer.this.serverShopManager != null) {
                                ShopContainer.this.serverShopManager.shopWealth += price;
                            }
                        }
                        ++timesCompleted;
                        continue;
                    }
                    if (i != 0 || !ShopContainer.this.client.isServer() || ShopContainer.this.serverShopManager == null) break;
                    new SingleShopStockUpdateEvent(ShopContainer.this.serverShopManager, item).applyAndSendToClient(ShopContainer.this.client.getServerClient());
                    ShopContainer.this.client.getServerClient().playerMob.getInv().markFullDirty();
                    break;
                }
                if (timesCompleted > 0 && serverItem != null) {
                    serverItem.currentStock = Math.max(serverItem.currentStock - timesCompleted, 0);
                    new SingleShopStockUpdateEvent(ShopContainer.this.serverShopManager, item).applyAndSendToAllClients(ShopContainer.this.client.getServerClient().getServer());
                }
            } else if (ShopContainer.this.client.isServer()) {
                new FullShopStockUpdateEvent(ShopContainer.this.serverShopManager, ShopContainer.this.sellingItems.values()).applyAndSendToClient(ShopContainer.this.client.getServerClient());
            }
        }
    }

    public class SellItemAction
    extends ContainerCustomAction {
        public void runAndSend(NetworkBuyingShopItem item, int amount, boolean forcedBeyondShopWealth) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextShortUnsigned(item.shopItemID);
            writer.putNextInt(amount);
            writer.putNextBoolean(forcedBeyondShopWealth);
            this.runAndSendAction(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int itemID = reader.getNextShortUnsigned();
            int amount = reader.getNextInt();
            boolean forceBeyondShopWealth = reader.getNextBoolean();
            NetworkBuyingShopItem item = ShopContainer.this.buyingItems.get(itemID);
            if (item != null) {
                if (ShopContainer.this.serverShopManager != null) {
                    ShopContainer.this.shopWealth = ShopContainer.this.serverShopManager.shopWealth;
                }
                int timesCompleted = 0;
                for (int i = 0; i < amount; ++i) {
                    if (item.canCompleteTrade(ShopContainer.this, ShopContainer.this.getCraftInventories(), forceBeyondShopWealth ? -1 : ShopContainer.this.shopWealth)) {
                        int price = item.completeTrade(ShopContainer.this, ShopContainer.this.getCraftInventories());
                        if (ShopContainer.this.shopWealth >= 0) {
                            ShopContainer.this.shopWealth = Math.max(ShopContainer.this.shopWealth - price, 0);
                            if (ShopContainer.this.serverShopManager != null) {
                                ShopContainer.this.serverShopManager.shopWealth = ShopContainer.this.shopWealth;
                            }
                        }
                        ++timesCompleted;
                        continue;
                    }
                    if (i != 0 || !ShopContainer.this.client.isServer() || ShopContainer.this.serverShopManager == null) break;
                    new ShopWealthUpdateEvent(ShopContainer.this.serverShopManager).applyAndSendToClient(ShopContainer.this.client.getServerClient());
                    ShopContainer.this.client.getServerClient().playerMob.getInv().markFullDirty();
                    break;
                }
                if (timesCompleted > 0 && ShopContainer.this.serverShopManager != null) {
                    new ShopWealthUpdateEvent(ShopContainer.this.serverShopManager).applyAndSendToAllClients(ShopContainer.this.client.getServerClient().getServer());
                }
            } else if (ShopContainer.this.client.isServer()) {
                ShopContainer.this.close();
            }
        }
    }
}

