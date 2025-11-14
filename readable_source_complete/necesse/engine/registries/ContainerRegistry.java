/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.entity.objectEntity.GlyphTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SalvageStationObjectEntity;
import necesse.entity.objectEntity.ShippingChestObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.entity.objectEntity.UpgradeStationObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.PartyConfigContainerForm;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.containerComponent.QuestsContainerForm;
import necesse.gfx.forms.presets.containerComponent.SettlementNameContainerForm;
import necesse.gfx.forms.presets.containerComponent.SleepContainerForm;
import necesse.gfx.forms.presets.containerComponent.TeleportToTeamContainerForm;
import necesse.gfx.forms.presets.containerComponent.TravelContainerComponent;
import necesse.gfx.forms.presets.containerComponent.creative.CreativeTeleportToPlayerContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.CloudItemContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.CraftingGuideContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.EnchantingScrollContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.PortableMusicPlayerContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.RecipeBookContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.RenameItemContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.WrappingPaperContainerForm;
import necesse.gfx.forms.presets.containerComponent.journal.JournalContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.BufferLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CountdownLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CountdownRelayLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.CounterLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.DelayLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SRLatchLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SensorLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SimpleLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.SoundLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.TFlipFlopLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.logicGate.TimerLogicGateContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.AlchemistContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.BuyAnimalContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.ElderContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.MageContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.PawnbrokerContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.StylistContainerForm;
import necesse.gfx.forms.presets.containerComponent.mob.TraderHumanContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ArmorStandContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.CraftingStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.DresserContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledCraftingStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledIncineratorInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledOEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledProcessingInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.FueledRefrigeratorInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.GlyphContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.HomestoneContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.MissionBoardContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.MusicPlayerContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.SalvageStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.ShippingChestContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.SignContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.UpgradeStationContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.inventory.container.AdventureJournalContainer;
import necesse.inventory.container.AdventurePartyConfigContainer;
import necesse.inventory.container.BedContainer;
import necesse.inventory.container.Container;
import necesse.inventory.container.SettlementNameContainer;
import necesse.inventory.container.creative.CreativeTeleportToPlayerContainer;
import necesse.inventory.container.item.CloudItemContainer;
import necesse.inventory.container.item.CraftingGuideContainer;
import necesse.inventory.container.item.EnchantingScrollContainer;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.item.PortableMusicPlayerContainer;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.container.item.RenameItemContainer;
import necesse.inventory.container.item.TeleportationScrollContainer;
import necesse.inventory.container.item.WrappingPaperContainer;
import necesse.inventory.container.logicGate.BufferLogicGateContainer;
import necesse.inventory.container.logicGate.CountdownLogicGateContainer;
import necesse.inventory.container.logicGate.CountdownRelayLogicGateContainer;
import necesse.inventory.container.logicGate.CounterLogicGateContainer;
import necesse.inventory.container.logicGate.DelayLogicGateContainer;
import necesse.inventory.container.logicGate.SRLatchLogicGateContainer;
import necesse.inventory.container.logicGate.SensorLogicGateContainer;
import necesse.inventory.container.logicGate.SimpleLogicGateContainer;
import necesse.inventory.container.logicGate.SoundLogicGateContainer;
import necesse.inventory.container.logicGate.TFlipFlopLogicGateContainer;
import necesse.inventory.container.logicGate.TimerLogicGateContainer;
import necesse.inventory.container.mob.AlchemistContainer;
import necesse.inventory.container.mob.BuyAnimalContainer;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.container.mob.MageContainer;
import necesse.inventory.container.mob.PawnbrokerContainer;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.mob.StylistContainer;
import necesse.inventory.container.mob.TraderHumanContainer;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.object.FueledCraftingStationContainer;
import necesse.inventory.container.object.FueledIncineratorInventoryContainer;
import necesse.inventory.container.object.FueledOEInventoryContainer;
import necesse.inventory.container.object.FueledProcessingOEInventoryContainer;
import necesse.inventory.container.object.FueledRefrigeratorInventoryContainer;
import necesse.inventory.container.object.GlyphTrapContainer;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.container.object.MusicPlayerContainer;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.object.SalvageStationContainer;
import necesse.inventory.container.object.ShippingChestInventoryContainer;
import necesse.inventory.container.object.SignContainer;
import necesse.inventory.container.object.TeleportationStoneContainer;
import necesse.inventory.container.object.UpgradeStationContainer;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.container.object.missionBoard.MissionBoardContainer;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelScrollContainer;
import necesse.inventory.container.travel.TravelStoneContainer;
import necesse.level.gameLogicGate.entities.BufferLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownRelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.CounterLogicGateEntity;
import necesse.level.gameLogicGate.entities.DelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameLogicGate.entities.SRLatchLogicGateEntity;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;
import necesse.level.gameLogicGate.entities.SimpleLogicGateEntity;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;
import necesse.level.gameLogicGate.entities.TFlipFlopLogicGateEntity;
import necesse.level.gameLogicGate.entities.TimerLogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class ContainerRegistry
extends GameRegistry<ContainerRegistryElement> {
    public static int OE_INVENTORY_CONTAINER;
    public static int ARMOR_STAND_CONTAINER;
    public static int DRESSER_CONTAINER;
    public static int PROCESSING_INVENTORY_CONTAINER;
    public static int MUSIC_PLAYER_CONTAINER;
    public static int CRAFTING_STATION_CONTAINER;
    public static int FUELED_CRAFTING_STATION_CONTAINER;
    public static int FUELED_OE_INVENTORY_CONTAINER;
    public static int FUELED_PROCESSING_STATION_CONTAINER;
    public static int FUELED_REFRIGERATOR_INVENTORY_CONTAINER;
    public static int INCINERATOR_INVENTORY_CONTAINER;
    public static int SIMPLE_LOGIC_GATE_CONTAINER;
    public static int BUFFER_LOGIC_GATE_CONTAINER;
    public static int DELAY_LOGIC_GATE_CONTAINER;
    public static int SRLATCH_LOGIC_GATE_CONTAINER;
    public static int COUNTER_LOGIC_GATE_CONTAINER;
    public static int SENSOR_LOGIC_GATE_CONTAINER;
    public static int SOUND_LOGIC_GATE_CONTAINER;
    public static int TFLIPFLOP_LOGIC_GATE_CONTAINER;
    public static int TIMER_LOGIC_GATE_CONTAINER;
    public static int COUNTDOWN_LOGIC_GATE_CONTAINER;
    public static int COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER;
    public static int ELDER_CONTAINER;
    public static int SHOP_CONTAINER;
    public static int MAGE_CONTAINER;
    public static int STYLIST_CONTAINER;
    public static int PAWNBROKER_CONTAINER;
    public static int ALCHEMIST_CONTAINER;
    public static int BUY_ANIMAL_CONTAINER;
    public static int SIGN_CONTAINER;
    public static int SETTLEMENT_NAME_CONTAINER;
    public static int SETTLEMENT_CONTAINER;
    public static int TRAVEL_CONTAINER;
    public static int TRAVEL_SCROLL_CONTAINER;
    public static int TRAVEL_STONE_CONTAINER;
    public static int TELEPORTATION_SCROLL_CONTAINER;
    public static int TELEPORTATION_STONE_CONTAINER;
    public static int CRAFTING_GUIDE_CONTAINER;
    public static int RECIPE_BOOK_CONTAINER;
    public static int ENCHANTING_SCROLL_CONTAINER;
    public static int PARTY_CONFIG_CONTAINER;
    public static int PVP_TEAMS_CONTAINER;
    public static int QUESTS_CONTAINER;
    public static int JOURNAL_CONTAINER;
    public static int GLYPH_TRAP_CONTAINER;
    public static int TRAVELING_MERCHANT_CONTAINER;
    public static int MISSION_BOARD_CONTAINER;
    public static int SHIPPING_CHEST_CONTAINER;
    public static int WRAPPING_PAPER_CONTAINER;
    public static int BED_CONTAINER;
    public static int CLOUD_INVENTORY_CONTAINER;
    public static int HOMESTONE_CONTAINER;
    public static int ITEM_INVENTORY_CONTAINER;
    public static int ITEM_MUSIC_PLAYER_CONTAINER;
    public static int RENAME_ITEM_CONTAINER;
    public static int FALLEN_ALTAR_CONTAINER;
    public static int UPGRADE_STATION_CONTAINER;
    public static int SALVAGE_STATION_CONTAINER;
    public static int CREATIVE_TELEPORT_TO_PLAYER_CONTAINER;
    public static final ContainerRegistry instance;

    private ContainerRegistry() {
        super("Container", 65535, false);
    }

    @Override
    public void registerCore() {
        OE_INVENTORY_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new OEInventoryContainerForm<OEInventoryContainer>(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new OEInventoryContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        ARMOR_STAND_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new ArmorStandContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new OEInventoryContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        DRESSER_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new DresserContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new OEInventoryContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        PROCESSING_INVENTORY_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new ProcessingInventoryContainerForm(client, new OEInventoryContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new OEInventoryContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        MUSIC_PLAYER_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new MusicPlayerContainerForm(client, new MusicPlayerContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new MusicPlayerContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        CRAFTING_STATION_CONTAINER = ContainerRegistry.registerSettlementDependantLOContainer((client, uniqueSeed, settlement, lo, content) -> new CraftingStationContainerForm<CraftingStationContainer>(client, new CraftingStationContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (LevelObject)lo, new PacketReader(content))), (client, uniqueSeed, settlement, lo, content, serverObject) -> new CraftingStationContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (LevelObject)lo, new PacketReader(content)));
        FUELED_CRAFTING_STATION_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new FueledCraftingStationContainerForm<FueledCraftingStationContainer>(client, new FueledCraftingStationContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (FueledInventoryObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new FueledCraftingStationContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, (FueledInventoryObjectEntity)oe, new PacketReader(content)));
        FUELED_OE_INVENTORY_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new FueledOEInventoryContainerForm<FueledOEInventoryContainer>(client, new FueledOEInventoryContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new FueledOEInventoryContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (OEInventory)((Object)oe), new PacketReader(content)));
        FUELED_PROCESSING_STATION_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new FueledProcessingInventoryContainerForm(client, new FueledProcessingOEInventoryContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (FueledProcessingInventoryObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new FueledProcessingOEInventoryContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, (FueledProcessingInventoryObjectEntity)oe, new PacketReader(content)));
        FUELED_REFRIGERATOR_INVENTORY_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new FueledRefrigeratorInventoryContainerForm<FueledRefrigeratorInventoryContainer>(client, new FueledRefrigeratorInventoryContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (FueledRefrigeratorObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new FueledRefrigeratorInventoryContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, (FueledRefrigeratorObjectEntity)oe, new PacketReader(content)));
        INCINERATOR_INVENTORY_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new FueledIncineratorInventoryContainerForm<FueledIncineratorInventoryContainer>(client, new FueledIncineratorInventoryContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (FueledIncineratorObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new FueledIncineratorInventoryContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, (FueledIncineratorObjectEntity)oe, new PacketReader(content)));
        GLYPH_TRAP_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new GlyphContainerForm<GlyphTrapContainer>(client, new GlyphTrapContainer(client.getClient(), uniqueSeed, (GlyphTrapObjectEntity)oe)), (client, uniqueSeed, oe, content, serverObject) -> new GlyphTrapContainer(client, uniqueSeed, (GlyphTrapObjectEntity)oe));
        SIMPLE_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new SimpleLogicGateContainerForm<SimpleLogicGateContainer>(client, new SimpleLogicGateContainer(client.getClient(), uniqueSeed, (SimpleLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new SimpleLogicGateContainer(client, uniqueSeed, (SimpleLogicGateEntity)lge));
        BUFFER_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new BufferLogicGateContainerForm<BufferLogicGateContainer>(client, new BufferLogicGateContainer(client.getClient(), uniqueSeed, (BufferLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new BufferLogicGateContainer(client, uniqueSeed, (BufferLogicGateEntity)lge));
        DELAY_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new DelayLogicGateContainerForm<DelayLogicGateContainer>(client, new DelayLogicGateContainer(client.getClient(), uniqueSeed, (DelayLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new DelayLogicGateContainer(client, uniqueSeed, (DelayLogicGateEntity)lge));
        SRLATCH_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new SRLatchLogicGateContainerForm<SRLatchLogicGateContainer>(client, new SRLatchLogicGateContainer(client.getClient(), uniqueSeed, (SRLatchLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new SRLatchLogicGateContainer(client, uniqueSeed, (SRLatchLogicGateEntity)lge));
        COUNTER_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new CounterLogicGateContainerForm<CounterLogicGateContainer>(client, new CounterLogicGateContainer(client.getClient(), uniqueSeed, (CounterLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new CounterLogicGateContainer(client, uniqueSeed, (CounterLogicGateEntity)lge));
        SENSOR_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new SensorLogicGateContainerForm<SensorLogicGateContainer>(client, new SensorLogicGateContainer(client.getClient(), uniqueSeed, (SensorLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new SensorLogicGateContainer(client, uniqueSeed, (SensorLogicGateEntity)lge));
        SOUND_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new SoundLogicGateContainerForm<SoundLogicGateContainer>(client, new SoundLogicGateContainer(client.getClient(), uniqueSeed, (SoundLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new SoundLogicGateContainer(client, uniqueSeed, (SoundLogicGateEntity)lge));
        TFLIPFLOP_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new TFlipFlopLogicGateContainerForm<TFlipFlopLogicGateContainer>(client, new TFlipFlopLogicGateContainer(client.getClient(), uniqueSeed, (TFlipFlopLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new TFlipFlopLogicGateContainer(client, uniqueSeed, (TFlipFlopLogicGateEntity)lge));
        TIMER_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new TimerLogicGateContainerForm<TimerLogicGateContainer>(client, new TimerLogicGateContainer(client.getClient(), uniqueSeed, (TimerLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new TimerLogicGateContainer(client, uniqueSeed, (TimerLogicGateEntity)lge));
        COUNTDOWN_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new CountdownLogicGateContainerForm<CountdownLogicGateContainer>(client, new CountdownLogicGateContainer(client.getClient(), uniqueSeed, (CountdownLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new CountdownLogicGateContainer(client, uniqueSeed, (CountdownLogicGateEntity)lge));
        COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER = ContainerRegistry.registerLogicGateContainer((client, uniqueSeed, lge, content) -> new CountdownRelayLogicGateContainerForm<CountdownRelayLogicGateContainer>(client, new CountdownRelayLogicGateContainer(client.getClient(), uniqueSeed, (CountdownRelayLogicGateEntity)lge)), (client, uniqueSeed, lge, content, serverObject) -> new CountdownRelayLogicGateContainer(client, uniqueSeed, (CountdownRelayLogicGateEntity)lge));
        ELDER_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new ElderContainerForm<ElderContainer>(client, new ElderContainer((NetworkClient)client.getClient(), uniqueSeed, (ElderHumanMob)mob, new PacketReader(content), null)), (client, uniqueSeed, mob, content, serverObject) -> new ElderContainer((NetworkClient)client, uniqueSeed, (ElderHumanMob)mob, new PacketReader(content), (ShopContainerData)serverObject));
        SHOP_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new ShopContainerForm<ShopContainer>(client, new ShopContainer(client.getClient(), uniqueSeed, (HumanShop)mob, content, null)), (client, uniqueSeed, mob, content, serverObject) -> new ShopContainer(client, uniqueSeed, (HumanShop)mob, content, (ShopContainerData)serverObject));
        MAGE_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new MageContainerForm<MageContainer>(client, new MageContainer((NetworkClient)client.getClient(), uniqueSeed, (MageHumanMob)mob, new PacketReader(content), null)), (client, uniqueSeed, mob, content, serverObject) -> new MageContainer((NetworkClient)client, uniqueSeed, (MageHumanMob)mob, new PacketReader(content), (ShopContainerData)serverObject));
        STYLIST_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new StylistContainerForm<StylistContainer>(client, new StylistContainer((NetworkClient)client.getClient(), uniqueSeed, (StylistHumanMob)mob, new PacketReader(content), null)), (client, uniqueSeed, mob, content, serverObject) -> new StylistContainer((NetworkClient)client, uniqueSeed, (StylistHumanMob)mob, new PacketReader(content), (ShopContainerData)serverObject));
        PAWNBROKER_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new PawnbrokerContainerForm<PawnbrokerContainer>(client, new PawnbrokerContainer((NetworkClient)client.getClient(), uniqueSeed, (HumanShop)mob, new PacketReader(content), null)), (client, uniqueSeed, mob, content, serverObject) -> new PawnbrokerContainer((NetworkClient)client, uniqueSeed, (HumanShop)mob, new PacketReader(content), (ShopContainerData)serverObject));
        TRAVELING_MERCHANT_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new TraderHumanContainerForm<TraderHumanContainer>(client, new TraderHumanContainer((NetworkClient)client.getClient(), uniqueSeed, (HumanShop)mob, new PacketReader(content), null)), (client, uniqueSeed, mob, content, serverObject) -> new TraderHumanContainer((NetworkClient)client, uniqueSeed, (HumanShop)mob, new PacketReader(content), (ShopContainerData)serverObject));
        ALCHEMIST_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new AlchemistContainerForm<AlchemistContainer>(client, new AlchemistContainer((NetworkClient)client.getClient(), uniqueSeed, (AlchemistHumanMob)mob, content, null)), (client, uniqueSeed, mob, content, serverObject) -> new AlchemistContainer((NetworkClient)client, uniqueSeed, (AlchemistHumanMob)mob, content, (ShopContainerData)serverObject));
        BUY_ANIMAL_CONTAINER = ContainerRegistry.registerMobContainer((client, uniqueSeed, mob, content) -> new BuyAnimalContainerForm<BuyAnimalContainer>(client, new BuyAnimalContainer(client.getClient(), uniqueSeed, (Mob)mob, content)), (client, uniqueSeed, mob, content, serverObject) -> new BuyAnimalContainer(client, uniqueSeed, (Mob)mob, content));
        SIGN_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new SignContainerForm<SignContainer>(client, new SignContainer(client.getClient(), uniqueSeed, (SignObjectEntity)oe)), (client, uniqueSeed, oe, content, serverObject) -> new SignContainer(client, uniqueSeed, (SignObjectEntity)oe));
        SETTLEMENT_NAME_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new SettlementNameContainerForm(client, new SettlementNameContainer(client.getClient(), uniqueSeed, new PacketReader(packet))), (client, uniqueSeed, packet, serverObject) -> new SettlementNameContainer(client, uniqueSeed, new PacketReader(packet)));
        SETTLEMENT_CONTAINER = ContainerRegistry.registerSettlementDependantContainer((client, uniqueSeed, settlement, content) -> new SettlementContainerForm<SettlementContainer>(client, new SettlementContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, content)), (client, uniqueSeed, settlement, content, serverObject) -> new SettlementContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, content));
        TRAVEL_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new TravelContainerComponent<TravelContainer>(client, new TravelContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new TravelContainer(client, uniqueSeed, content));
        TRAVEL_SCROLL_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new TravelContainerComponent<TravelScrollContainer>(client, new TravelScrollContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new TravelScrollContainer(client, uniqueSeed, content));
        TRAVEL_STONE_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new TravelContainerComponent<TravelStoneContainer>(client, new TravelStoneContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new TravelStoneContainer(client, uniqueSeed, content));
        TELEPORTATION_SCROLL_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new TeleportToTeamContainerForm<TeleportationScrollContainer>(client, new TeleportationScrollContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new TeleportationScrollContainer(client, uniqueSeed, content));
        TELEPORTATION_STONE_CONTAINER = ContainerRegistry.registerLOContainer((client, uniqueSeed, levelObject, content) -> new TeleportToTeamContainerForm<TeleportationStoneContainer>(client, new TeleportationStoneContainer(client.getClient(), uniqueSeed, (LevelObject)levelObject, content)), (client, uniqueSeed, levelObject, content, serverObject) -> new TeleportationStoneContainer(client, uniqueSeed, (LevelObject)levelObject, content));
        CRAFTING_GUIDE_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new CraftingGuideContainerForm<CraftingGuideContainer>(client, new CraftingGuideContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new CraftingGuideContainer(client, uniqueSeed, content));
        RECIPE_BOOK_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new RecipeBookContainerForm<RecipeBookContainer>(client, new RecipeBookContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new RecipeBookContainer(client, uniqueSeed, content));
        ENCHANTING_SCROLL_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new EnchantingScrollContainerForm<EnchantingScrollContainer>(client, new EnchantingScrollContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new EnchantingScrollContainer(client, uniqueSeed, content));
        PARTY_CONFIG_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new PartyConfigContainerForm(client, new AdventurePartyConfigContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new AdventurePartyConfigContainer(client, uniqueSeed, content));
        PVP_TEAMS_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new PvPTeamsContainerForm(client, new PvPTeamsContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new PvPTeamsContainer(client, uniqueSeed, content));
        QUESTS_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new QuestsContainerForm(client, new Container(client.getClient(), uniqueSeed)), (client, uniqueSeed, content, serverObject) -> new Container(client, uniqueSeed));
        JOURNAL_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new JournalContainerForm(client, new AdventureJournalContainer(client.getClient(), uniqueSeed)), (client, uniqueSeed, content, serverObject) -> new AdventureJournalContainer(client, uniqueSeed));
        MISSION_BOARD_CONTAINER = ContainerRegistry.registerSettlementDependantContainer((client, uniqueSeed, settlement, content) -> new MissionBoardContainerForm<MissionBoardContainer>(client, new MissionBoardContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, content)), (client, uniqueSeed, settlement, content, serverObject) -> new MissionBoardContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, content));
        SHIPPING_CHEST_CONTAINER = ContainerRegistry.registerSettlementDependantOEContainer((client, uniqueSeed, settlement, oe, content) -> new ShippingChestContainerForm<ShippingChestInventoryContainer>(client, new ShippingChestInventoryContainer((NetworkClient)client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (ShippingChestObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, settlement, oe, content, serverObject) -> new ShippingChestInventoryContainer((NetworkClient)client, uniqueSeed, (SettlementDataEvent)settlement, (ShippingChestObjectEntity)oe, new PacketReader(content)));
        WRAPPING_PAPER_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new WrappingPaperContainerForm<WrappingPaperContainer>(client, new WrappingPaperContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new WrappingPaperContainer(client, uniqueSeed, content));
        BED_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, objectEntity, content) -> new SleepContainerForm(client, new BedContainer(client.getClient(), uniqueSeed, (ObjectEntity)objectEntity, content)), (client, uniqueSeed, objectEntity, content, serverObject) -> new BedContainer(client, uniqueSeed, (ObjectEntity)objectEntity, content));
        CLOUD_INVENTORY_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new CloudItemContainerForm<CloudItemContainer>(client, new CloudItemContainer(client.getClient(), uniqueSeed, content)), (client, uniqueSeed, content, serverObject) -> new CloudItemContainer(client, uniqueSeed, content));
        HOMESTONE_CONTAINER = ContainerRegistry.registerSettlementDependantLOContainer((client, uniqueSeed, settlement, levelObject, content) -> new HomestoneContainerForm<HomestoneContainer>(client, new HomestoneContainer(client.getClient(), uniqueSeed, (SettlementDataEvent)settlement, (LevelObject)levelObject, content)), (client, uniqueSeed, settlement, levelObject, content, serverObject) -> new HomestoneContainer(client, uniqueSeed, (SettlementDataEvent)settlement, (LevelObject)levelObject, content));
        ITEM_INVENTORY_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new ItemInventoryContainerForm<ItemInventoryContainer>(client, new ItemInventoryContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new ItemInventoryContainer(client, uniqueSeed, packet));
        ITEM_MUSIC_PLAYER_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new PortableMusicPlayerContainerForm(client, new PortableMusicPlayerContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new PortableMusicPlayerContainer(client, uniqueSeed, packet));
        RENAME_ITEM_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new RenameItemContainerForm<RenameItemContainer>(client, new RenameItemContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new RenameItemContainer(client, uniqueSeed, packet));
        FALLEN_ALTAR_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new FallenAltarContainerForm(client, new FallenAltarContainer(client.getClient(), uniqueSeed, (FallenAltarObjectEntity)oe, content)), (client, uniqueSeed, oe, content, serverObject) -> new FallenAltarContainer(client, uniqueSeed, (FallenAltarObjectEntity)oe, content));
        UPGRADE_STATION_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new UpgradeStationContainerForm<UpgradeStationContainer>(client, new UpgradeStationContainer(client.getClient(), uniqueSeed, (UpgradeStationObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) -> new UpgradeStationContainer(client, uniqueSeed, (UpgradeStationObjectEntity)oe, new PacketReader(content)));
        SALVAGE_STATION_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new SalvageStationContainerForm<SalvageStationContainer>(client, new SalvageStationContainer(client.getClient(), uniqueSeed, (SalvageStationObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) -> new SalvageStationContainer(client, uniqueSeed, (SalvageStationObjectEntity)oe, new PacketReader(content)));
        CREATIVE_TELEPORT_TO_PLAYER_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, content) -> new CreativeTeleportToPlayerContainerForm<CreativeTeleportToPlayerContainer>(client, new CreativeTeleportToPlayerContainer(client.getClient(), uniqueSeed)), (client, uniqueSeed, packet, serverObject) -> new CreativeTeleportToPlayerContainer(client, uniqueSeed));
    }

    @Override
    protected void onRegister(ContainerRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerSettlementDependantOEContainer(ClientBiExtraContainerHandler<SettlementDataEvent, ObjectEntity> clientHandler, ServerBiExtraContainerHandler<SettlementDataEvent, ObjectEntity> serverHandler) {
        return ContainerRegistry.registerSettlementDependantLOContainer((client, uniqueSeed, settlement, lo, content) -> clientHandler.handle(client, uniqueSeed, (SettlementDataEvent)settlement, lo.getObjectEntity(), content), (client, uniqueSeed, settlement, lo, content, serverObject) -> serverHandler.handle(client, uniqueSeed, (SettlementDataEvent)settlement, lo.getObjectEntity(), content, serverObject));
    }

    public static int registerSettlementDependantLOContainer(ClientBiExtraContainerHandler<SettlementDataEvent, LevelObject> clientHandler, ServerBiExtraContainerHandler<SettlementDataEvent, LevelObject> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            LevelObject lo = level.getLevelObject(reader.getNextInt(), reader.getNextInt());
            return clientHandler.handle(client, uniqueSeed, basicsEvent, lo, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            LevelObject lo = level.getLevelObject(reader.getNextInt(), reader.getNextInt());
            return serverHandler.handle(client, uniqueSeed, basicsEvent, lo, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerSettlementDependantMobContainer(ClientBiExtraContainerHandler<SettlementDataEvent, Mob> clientHandler, ServerBiExtraContainerHandler<SettlementDataEvent, Mob> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            Mob mob = level.entityManager.mobs.get(reader.getNextInt(), false);
            return clientHandler.handle(client, uniqueSeed, basicsEvent, mob, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            Mob mob = level.entityManager.mobs.get(reader.getNextInt(), false);
            return serverHandler.handle(client, uniqueSeed, basicsEvent, mob, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerSettlementDependantContainer(ClientExtraContainerHandler<SettlementDataEvent> clientHandler, ServerExtraContainerHandler<SettlementDataEvent> serverHandler) {
        return ContainerRegistry.registerContainer((client, uniqueSeed, content) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            return clientHandler.handle(client, uniqueSeed, basicsEvent, reader.getNextContentPacket());
        }, (client, uniqueSeed, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            SettlementDataEvent basicsEvent = reader.getNextBoolean() ? new SettlementDataEvent(reader) : null;
            return serverHandler.handle(client, uniqueSeed, basicsEvent, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerLogicGateContainer(ClientExtraContainerHandler<LogicGateEntity> clientHandler, ServerExtraContainerHandler<LogicGateEntity> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            LogicGateEntity lge = level.logicLayer.getEntity(reader.getNextInt(), reader.getNextInt());
            return clientHandler.handle(client, uniqueSeed, lge, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            LogicGateEntity lge = level.logicLayer.getEntity(reader.getNextInt(), reader.getNextInt());
            return serverHandler.handle(client, uniqueSeed, lge, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerOEContainer(ClientExtraContainerHandler<ObjectEntity> clientHandler, ServerExtraContainerHandler<ObjectEntity> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            ObjectEntity oe = level.entityManager.getObjectEntity(reader.getNextInt(), reader.getNextInt());
            return clientHandler.handle(client, uniqueSeed, oe, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            ObjectEntity oe = level.entityManager.getObjectEntity(reader.getNextInt(), reader.getNextInt());
            return serverHandler.handle(client, uniqueSeed, oe, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerLOContainer(ClientExtraContainerHandler<LevelObject> clientHandler, ServerExtraContainerHandler<LevelObject> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            LevelObject lo = level.getLevelObject(reader.getNextInt(), reader.getNextInt());
            return clientHandler.handle(client, uniqueSeed, lo, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            LevelObject lo = level.getLevelObject(reader.getNextInt(), reader.getNextInt());
            return serverHandler.handle(client, uniqueSeed, lo, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerMobContainer(ClientExtraContainerHandler<Mob> clientHandler, ServerExtraContainerHandler<Mob> serverHandler) {
        return ContainerRegistry.registerLevelContainer((client, uniqueSeed, level, content) -> {
            PacketReader reader = new PacketReader(content);
            Mob mob = level.entityManager.mobs.get(reader.getNextInt(), false);
            return clientHandler.handle(client, uniqueSeed, mob, reader.getNextContentPacket());
        }, (client, uniqueSeed, level, content, serverObject) -> {
            PacketReader reader = new PacketReader(content);
            Mob mob = level.entityManager.mobs.get(reader.getNextInt(), false);
            return serverHandler.handle(client, uniqueSeed, mob, reader.getNextContentPacket(), serverObject);
        });
    }

    public static int registerLevelContainer(ClientExtraContainerHandler<Level> clientHandler, ServerExtraContainerHandler<Level> serverHandler) {
        return ContainerRegistry.registerContainer((client, uniqueSeed, content) -> {
            Level level = client.getLevel();
            return clientHandler.handle(client, uniqueSeed, level, content);
        }, (client, uniqueSeed, content, serverObject) -> {
            Level level = client.getServer().world.getLevel(client);
            return serverHandler.handle(client, uniqueSeed, level, content, serverObject);
        });
    }

    public static int registerContainer(ClientContainerHandler clientHandler, ServerContainerHandler serverHandler) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register containers");
        }
        return instance.register("container", new ContainerRegistryElement(clientHandler, serverHandler));
    }

    public static void openContainer(int containerID, Client client, int uniqueSeed, Packet content) {
        try {
            ContainerRegistryElement e = (ContainerRegistryElement)instance.getElement(containerID);
            if (e != null) {
                client.openContainerForm(e.clientHandler.handle(client, uniqueSeed, content));
            }
        }
        catch (Exception e) {
            System.err.println("Error trying to open " + containerID + " client side:");
            e.printStackTrace();
        }
    }

    public static void openContainer(int containerID, ServerClient client, int uniqueSeed, Packet content, Object serverObject) {
        try {
            ContainerRegistryElement e = (ContainerRegistryElement)instance.getElement(containerID);
            if (e != null) {
                client.openContainer(e.serverHandler.handle(client, uniqueSeed, content, serverObject));
            }
        }
        catch (Exception e) {
            System.err.println("Error trying to open " + containerID + " server side:");
            e.printStackTrace();
        }
    }

    public static void openAndSendContainer(ServerClient client, PacketOpenContainer packet) {
        client.getServer().network.sendPacket((Packet)packet, client);
        ContainerRegistry.openContainer(packet.containerID, client, packet.uniqueSeed, packet.content, packet.serverObject);
    }

    static {
        instance = new ContainerRegistry();
    }

    @FunctionalInterface
    public static interface ClientBiExtraContainerHandler<T, C> {
        public ContainerComponent<? extends Container> handle(Client var1, int var2, T var3, C var4, Packet var5);
    }

    @FunctionalInterface
    public static interface ServerBiExtraContainerHandler<T, C> {
        public Container handle(ServerClient var1, int var2, T var3, C var4, Packet var5, Object var6);
    }

    @FunctionalInterface
    public static interface ClientExtraContainerHandler<T> {
        public ContainerComponent<? extends Container> handle(Client var1, int var2, T var3, Packet var4);
    }

    @FunctionalInterface
    public static interface ServerExtraContainerHandler<T> {
        public Container handle(ServerClient var1, int var2, T var3, Packet var4, Object var5);
    }

    @FunctionalInterface
    public static interface ClientContainerHandler {
        public ContainerComponent<? extends Container> handle(Client var1, int var2, Packet var3);
    }

    @FunctionalInterface
    public static interface ServerContainerHandler {
        public Container handle(ServerClient var1, int var2, Packet var3, Object var4);
    }

    protected static class ContainerRegistryElement
    implements IDDataContainer {
        public final IDData data = new IDData();
        public final ClientContainerHandler clientHandler;
        public final ServerContainerHandler serverHandler;

        public ContainerRegistryElement(ClientContainerHandler clientHandler, ServerContainerHandler serverHandler) {
            this.clientHandler = clientHandler;
            this.serverHandler = serverHandler;
        }

        @Override
        public IDData getIDData() {
            return this.data;
        }
    }
}

