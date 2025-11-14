/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketHumanWorkUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldSettings;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.ActivityDescription;
import necesse.entity.mobs.ActivityDescriptionMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobInventory;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestGiver;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CustomMobAbility;
import necesse.entity.mobs.ability.EnumListMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.ClientInteractMob;
import necesse.entity.mobs.friendly.InteractingClients;
import necesse.entity.mobs.friendly.RopeClearerMob;
import necesse.entity.mobs.friendly.human.AdventurePartyHumanHandler;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.HumanMobItemAttackSlot;
import necesse.entity.mobs.friendly.human.MoveToTile;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.SettlerMission;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanDietFilterSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanEquipmentFilterSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkPrioritiesSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSettingRegistry;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import necesse.entity.mobs.networkField.IntNetworkField;
import necesse.entity.mobs.networkField.LongNetworkField;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.forms.components.FormExpressionWheel;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemHolding;
import necesse.inventory.item.SwingSpriteAttackItem;
import necesse.inventory.item.WorkSpriteAttackItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootInventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.MoodFloatText;
import necesse.level.maps.hudManager.floatText.thoughtBubble.AgreeThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.AngerThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.DisagreeThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ExcitedThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ItemThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.LoveThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.MobThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.OtherSettlerThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.HuntMobLevelJob;
import necesse.level.maps.levelData.jobs.ManageEquipmentLevelJob;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.PlantCropLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShippingChestLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.StartExpeditionLevelJob;
import necesse.level.maps.levelData.jobs.StorePickupItemLevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.levelData.settlementData.SettlementWealthCounter;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;
import necesse.level.maps.levelData.settlementData.settler.DietThought;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.settler.PopulationThought;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.SubRegion;

public abstract class HumanMob
extends ItemAttackerMob
implements SettlerMob,
CommandMob,
EntityJobWorker,
FishingMob,
HungerMob,
ObjectUserMob,
ActivityDescriptionMob,
MobInventory,
ClientInteractMob,
AmmoUserMob,
RopeClearerMob {
    public static int visitorStayTimeSeconds = 480;
    public static int timeToTravelOut = 240;
    public static int humanPathIterations;
    public static int defaultJobPathIterations;
    public static int secondsToPassAtFullHunger;
    public static float adventurePartyHungerUsageMod;
    public HumanGender gender;
    public boolean customLook;
    public HumanLook look;
    public Point home;
    public int maxSecondsFullHealthRegen = 300;
    public OneWorldNPCVillageData.NPCVillagerData villagerData;
    public int settlerSeed;
    protected String settlerName;
    protected Point moveInPoint;
    protected Point moveOutPoint;
    private int settlerCheck;
    protected BooleanNetworkField clientHasCommandOrders = this.registerNetworkField(new BooleanNetworkField(false));
    public int resetCommandsBuffer;
    public Mob commandFollowMob;
    public Point commandGuardPoint;
    public boolean commandMoveToGuardPoint;
    public boolean commandMoveToFollowPoint;
    public Mob commandAttackMob;
    public boolean canJoinAdventureParties = true;
    public final AdventurePartyHumanHandler adventureParty = new AdventurePartyHumanHandler(this);
    protected long nextShowMoodTime;
    public final InteractingClients interactingClients = new InteractingClients(this);
    public boolean canInteractWithOtherSettlers = false;
    public final ShowSimpleThoughtMobAbility showAgreeThoughtAbility;
    public final ShowSimpleThoughtMobAbility showDisagreeThoughtAbility;
    public final ShowSimpleThoughtMobAbility showExcitedThoughtAbility;
    public final ShowSimpleThoughtMobAbility showAngerThoughtAbility;
    public final ShowSimpleThoughtMobAbility showLoveThoughtAbility;
    public final ShowOtherSettlerThoughtMobAbility showOtherSettlerThoughtAbility;
    public final ShowItemThoughtMobAbility showItemThoughtAbility;
    public final ShowMobThoughtMobAbility showMobThoughtAbility;
    protected FormExpressionWheel.Expression activeExpression;
    protected long activeExpressionEndTime;
    public final StartExpressionMobAbility startExpressionAbility;
    public final String settlerStringID;
    protected int settlementUniqueID;
    protected Point earlyAccessSettlementIsland;
    protected ServerSettlementData cachedServerSettlementData;
    protected NetworkSettlementData cachedNetworkSettlementData;
    public LevelSettler levelSettler;
    protected int settlerQuestTier = -1;
    protected int settlerHappiness = 0;
    protected ItemCategoriesFilter loadedDietFilter = null;
    public FoodConsumableItem lastFoodEaten;
    public LinkedList<Integer> recentFoodItemIDsEaten = new LinkedList();
    public float hungerLevel = 0.2f;
    protected long clearRopedTime;
    protected DownedState downedState = null;
    protected Point earlyAccessRecruitReservedIsland;
    protected int recruitReservedSettlementUniqueID;
    protected long recruitReservedTimeout;
    protected InventoryItem customHoldItem;
    protected long customHoldItemTimeout;
    private long lastWorkAnimationRequest = -1L;
    public final CustomMobAbility itemAttackAbility;
    public final CustomMobAbility itemSwingSpriteAbility;
    public final CustomMobAbility itemWorkSpriteAbility;
    public final CustomMobAbility itemHoldSpriteAbility;
    public final BooleanMobAbility playConsumeSound;
    public final EnumListMobAbility<MoodFloatText.Moods> showMoods;
    public final HumanWorkSettingRegistry workSettings;
    public final CustomMobAbility workSettingAction;
    protected long searchEquipmentCooldown;
    public BooleanNetworkField selfManageEquipment = this.registerNetworkField(new BooleanNetworkField(true));
    public Inventory equipmentInventory = new Inventory(7){

        @Override
        public void updateSlot(int slot) {
            ServerSettlementData data;
            super.updateSlot(slot);
            if (slot < 3) {
                HumanMob.this.equipmentBuffManager.updateArmorBuffs();
                HumanMob.this.updateStats(true);
            } else if (slot < 6) {
                HumanMob.this.equipmentBuffManager.updateCosmeticSetBonus();
            }
            Level level = HumanMob.this.getLevel();
            if (level != null && level.isLoadingComplete() && HumanMob.this.isServer() && HumanMob.this.isSettler() && (data = HumanMob.this.getSettlerSettlementServerData()) != null) {
                InventoryRange range = slot < 3 ? new InventoryRange(this, 0, 2) : new InventoryRange(this, 3, 5);
                data.onSettlerEquipmentChanged(HumanMob.this.getUniqueID(), range, slot, slot >= 3);
            }
        }
    };
    public EquipmentBuffManager equipmentBuffManager = new EquipmentBuffManager(this){

        @Override
        public InventoryItem getArmorItem(int slot) {
            return HumanMob.this.equipmentInventory.getItem(slot);
        }

        @Override
        public InventoryItem getCosmeticItem(int slot) {
            return HumanMob.this.equipmentInventory.getItem(3 + slot);
        }

        @Override
        public ArrayList<InventoryItem> getTrinketItems() {
            return new ArrayList<InventoryItem>();
        }
    };
    public ArrayList<InventoryItem> workInventory = new ArrayList();
    public ActivityDescription currentActivity;
    public int hungryStrikeBuffer;
    public float leaveBuffer;
    public float leaveBufferWarnings;
    public BooleanNetworkField isOnStrike = this.registerNetworkField(new BooleanNetworkField(false){

        @Override
        public void onChanged(Boolean value) {
            super.onChanged(value);
            if (!value.booleanValue()) {
                HumanMob.this.endStrikeBuffer = 0;
            }
        }
    });
    public boolean hungerStrike;
    public int endStrikeBuffer;
    public int workBreakBuffer;
    public boolean onWorkBreak;
    public static int maxWorkBreakBufferAtFullHappiness;
    public static int maxWorkBreakBufferAtNoHappiness;
    public static int resetWorkBreakWhenBufferAt;
    public static float workBreakBufferUsageModAtFullHappiness;
    public static float workBreakBufferUsageModAtNoHappiness;
    public static float workBreakBufferRegenModAtFullHappiness;
    public static float workBreakBufferRegenModAtNoHappiness;
    public JobTypeHandler jobTypeHandler = new JobTypeHandler();
    protected long nextMissionWorldTime;
    protected boolean cancelJob;
    protected boolean allowFullInventoryMood;
    private boolean sendWorkUpdatePacket;
    public boolean workDirty;
    protected int nonSettlerHealth;
    protected int settlerHealth;
    protected Point earlyAccessVisitorIsland;
    protected int visitorSettlementUniqueID;
    protected ServerSettlementData cachedVisitorServerSettlementData;
    protected NetworkSettlementData cachedVisitorNetworkSettlementData;
    protected long travelOutTime;
    protected SettlerMission currentMission;
    public boolean completedMission;
    public boolean missionFailed;
    public GameMessage missionFailedMessage;
    public ObjectUserActive objectUser;
    protected Point loadedObjectUserTile;
    public BooleanNetworkField hideOnLowHealth = this.registerNetworkField(new BooleanNetworkField(true));
    public IntNetworkField team = this.registerNetworkField(new IntNetworkField(-10){

        @Override
        public void onChanged(Integer value) {
            super.onChanged(value);
            HumanMob.this.setTeam(value);
        }
    });
    public LongNetworkField owner = this.registerNetworkField(new LongNetworkField(-1L){

        @Override
        public void onChanged(Long value) {
            super.onChanged(value);
        }
    });
    public boolean isHiding;
    public static String[] elderNames;
    public static String[] maleNames;
    public static String[] femaleNames;
    public static String[] neutralNames;
    public static String[] lastNames;

    public HumanMob(int nonSettlerHealth, int settlerHealth, String settlerStringID) {
        super(nonSettlerHealth);
        this.nonSettlerHealth = nonSettlerHealth;
        this.settlerHealth = settlerHealth;
        this.settlerStringID = settlerStringID;
        this.isHostile = false;
        this.look = new HumanLook();
        this.attackAnimTime = this.attackCooldown;
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.setArmor(15);
        this.isUsingItemsForArmorAndHealth = true;
        this.setTeam(-10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.showAgreeThoughtAbility = this.registerAbility(new ShowSimpleThoughtMobAbility(animTime -> new AgreeThoughtBubble(this, (int)animTime)));
        this.showDisagreeThoughtAbility = this.registerAbility(new ShowSimpleThoughtMobAbility(animTime -> new DisagreeThoughtBubble(this, (int)animTime)));
        this.showExcitedThoughtAbility = this.registerAbility(new ShowSimpleThoughtMobAbility(animTime -> new ExcitedThoughtBubble(this, (int)animTime)));
        this.showAngerThoughtAbility = this.registerAbility(new ShowSimpleThoughtMobAbility(animTime -> new AngerThoughtBubble(this, (int)animTime)));
        this.showLoveThoughtAbility = this.registerAbility(new ShowSimpleThoughtMobAbility(animTime -> new LoveThoughtBubble(this, (int)animTime)));
        this.showOtherSettlerThoughtAbility = this.registerAbility(new ShowOtherSettlerThoughtMobAbility());
        this.showItemThoughtAbility = this.registerAbility(new ShowItemThoughtMobAbility());
        this.showMobThoughtAbility = this.registerAbility(new ShowMobThoughtMobAbility());
        this.startExpressionAbility = this.registerAbility(new StartExpressionMobAbility());
        this.workSettings = new HumanWorkSettingRegistry();
        this.workSettingAction = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                int id = reader.getNextShortUnsigned();
                HumanWorkSetting<?> setting = HumanMob.this.workSettings.getSetting(id);
                if (setting != null) {
                    setting.runAction(reader.getNextContentPacket());
                }
            }
        });
        this.itemAttackAbility = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                InventoryItem invItem;
                PacketReader reader = new PacketReader(content);
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                if (reader.getNextBoolean()) {
                    invItem = InventoryItem.fromContentPacket(reader);
                    if (invItem == null) {
                        return;
                    }
                } else {
                    Item item = ItemRegistry.getItem(reader.getNextShortUnsigned());
                    if (item == null) {
                        return;
                    }
                    invItem = new InventoryItem(item);
                }
                int animTime = reader.getNextInt();
                int cooldown = reader.getNextInt();
                HumanMob.this.attackingItem = invItem;
                HumanMob.this.showItemAttack(invItem, x, y, 0, HumanMob.this.attackSeed, null);
                HumanMob.this.attackAnimTime = animTime;
                HumanMob.this.attackCooldown = Math.max(animTime, cooldown);
                HumanMob.this.lastWorkAnimationRequest = -1L;
            }
        });
        this.itemSwingSpriteAbility = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                int itemID = reader.getNextBoolean() ? reader.getNextShortUnsigned() : -1;
                int animTime = reader.getNextInt();
                boolean inverted = reader.getNextBoolean();
                InventoryItem invItem = SwingSpriteAttackItem.setup(new InventoryItem("swingspriteattack"), itemID, inverted);
                HumanMob.this.showItemAttack(invItem, x, y, 0, HumanMob.this.attackSeed, null);
                HumanMob.this.attackAnimTime = animTime;
                HumanMob.this.lastWorkAnimationRequest = -1L;
            }
        });
        this.itemWorkSpriteAbility = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                int itemID = reader.getNextBoolean() ? reader.getNextShortUnsigned() : -1;
                int animTime = reader.getNextInt();
                long lastAttackTime = HumanMob.this.attackTime;
                InventoryItem invItem = WorkSpriteAttackItem.setup(new InventoryItem("workspriteattack"), itemID, false);
                HumanMob.this.showItemAttack(invItem, x, y, 0, HumanMob.this.attackSeed, null);
                HumanMob.this.attackAnimTime = animTime;
                if (!HumanMob.this.isAttacking) {
                    HumanMob.this.attackTime = lastAttackTime;
                }
                HumanMob.this.lastWorkAnimationRequest = HumanMob.this.getWorldEntity().getLocalTime();
            }
        });
        this.itemHoldSpriteAbility = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                if (reader.getNextBoolean()) {
                    InventoryItem holdItem = new InventoryItem("itemhold");
                    int itemID = reader.getNextShortUnsigned();
                    ItemHolding.setGNDData(holdItem, new InventoryItem(ItemRegistry.getItem(itemID)));
                    HumanMob.this.customHoldItem = holdItem;
                    HumanMob.this.customHoldItemTimeout = HumanMob.this.getLocalTime() + (long)reader.getNextInt();
                } else {
                    HumanMob.this.customHoldItem = null;
                }
            }
        });
        this.playConsumeSound = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                if (HumanMob.this.isClient()) {
                    SoundManager.playSound(value ? GameResources.drink : GameResources.eat, (SoundEffect)SoundEffect.effect(HumanMob.this));
                }
            }
        });
        this.showMoods = this.registerAbility(new EnumListMobAbility<MoodFloatText.Moods>(MoodFloatText.Moods.class){

            @Override
            protected void run(List<MoodFloatText.Moods> list) {
                if (HumanMob.this.isServer()) {
                    return;
                }
                HumanMob.this.getLevel().hudManager.addElement(new MoodFloatText((Mob)HumanMob.this, 5000, list));
            }
        });
        this.currentActivity = new ActivityDescription(this);
        this.jobTypeHandler.setJobHandler(ConsumeFoodLevelJob.class, 0, 0, 0, 0, (handler, worker) -> this.hungerLevel <= 0.25f, foundJob -> ConsumeFoodLevelJob.getJobSequence(this, foundJob, this, new HashSet<Integer>(this.recentFoodItemIDsEaten)));
        this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).extraJobStreamer = ConsumeFoodLevelJob.getJobStreamer(() -> this.levelSettler == null ? null : this.levelSettler.dietFilter);
        this.jobTypeHandler.setJobHandler(ManageEquipmentLevelJob.class, 0, 0, 0, 0, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (Boolean)this.selfManageEquipment.get() != false && this.searchEquipmentCooldown <= this.getTime(), foundJob -> {
            this.searchEquipmentCooldown = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(60, 300) * 1000L;
            return ManageEquipmentLevelJob.getJobSequence(this, foundJob, this);
        });
        this.jobTypeHandler.getJobHandler(ManageEquipmentLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(ManageEquipmentLevelJob.class).extraJobStreamer = ManageEquipmentLevelJob.getJobStreamer(() -> this.levelSettler == null ? null : this.levelSettler.equipmentFilter, () -> this.levelSettler == null || this.levelSettler.preferArmorSets);
        this.jobTypeHandler.setJobHandler(StartExpeditionLevelJob.class, 0, 0, 20000, 30000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerWithinSettlement() && this.nextMissionWorldTime <= this.getWorldTime() && this.hungerLevel > 0.1f && !this.isInventoryFull(true), foundJob -> StartExpeditionLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(StartExpeditionLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(StartExpeditionLevelJob.class).extraJobStreamer = StartExpeditionLevelJob.getJobStreamer(this);
        this.jobTypeHandler.setJobHandler(ShippingChestLevelJob.class, 0, 0, 20000, 30000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerWithinSettlement() && this.nextMissionWorldTime <= this.getWorldTime() && this.hungerLevel > 0.1f && !this.isInventoryFull(true), foundJob -> ShippingChestLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(HaulFromLevelJob.class, 0, 0, 0, 5000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerWithinSettlement() && !this.isInventoryFull(true), foundJob -> HaulFromLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(UseWorkstationLevelJob.class, 0, 0, 0, 10000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerWithinSettlement() && !this.isInventoryFull(true), foundJob -> UseWorkstationLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(HasStorageLevelJob.class, 0, 0, 0, 0, (handler, worker) -> !this.hasCompletedMission() && this.isSettlerWithinSettlement(), foundJob -> HasStorageLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(StorePickupItemLevelJob.class, 0, 0, 0, 5000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && this.isSettlerWithinSettlement() && !this.isInventoryFull(true), foundJob -> StorePickupItemLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(StorePickupItemLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(StorePickupItemLevelJob.class).extraJobStreamer = StorePickupItemLevelJob.getJobStreamer();
        this.jobTypeHandler.setJobHandler(ForestryLevelJob.class, 0, 0, 0, 4000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> ForestryLevelJob.getJobSequence(this, this.isSettler(), foundJob));
        this.jobTypeHandler.setJobHandler(PlantSaplingLevelJob.class, 0, 0, 0, 1000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()), foundJob -> PlantSaplingLevelJob.getJobSequence(this, this.isSettler(), foundJob));
        this.jobTypeHandler.setJobHandler(HarvestFruitLevelJob.class, 0, 0, 0, 4000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> HarvestFruitLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(HarvestCropLevelJob.class, 0, 0, 0, 4000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> HarvestCropLevelJob.getJobSequence(this, this.isSettler(), foundJob));
        this.jobTypeHandler.setJobHandler(PlantCropLevelJob.class, 0, 0, 0, 1000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()), foundJob -> PlantCropLevelJob.getJobSequence(this, this.isSettler(), foundJob));
        this.jobTypeHandler.setJobHandler(FertilizeLevelJob.class, 0, 0, 0, 1000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> FertilizeLevelJob.getJobSequence(this, this.isSettler(), foundJob), FertilizeLevelJob::getPreSequenceCompute);
        this.jobTypeHandler.setJobHandler(FishingPositionLevelJob.class, 0, 0, 10000, 50000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> FishingPositionLevelJob.getJobSequence(this, this, foundJob));
        this.jobTypeHandler.setJobHandler(HuntMobLevelJob.class, 0, 0, 0, 20000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> HuntMobLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(HuntMobLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(HuntMobLevelJob.class).extraJobStreamer = HuntMobLevelJob.getJobStreamer();
        this.jobTypeHandler.setJobHandler(SlaughterHusbandryMobLevelJob.class, 0, 0, 0, 16000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> SlaughterHusbandryMobLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(SlaughterHusbandryMobLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(SlaughterHusbandryMobLevelJob.class).extraJobStreamer = SlaughterHusbandryMobLevelJob.getJobStreamer();
        this.jobTypeHandler.setJobHandler(MilkHusbandryMobLevelJob.class, 0, 0, 0, 4000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> MilkHusbandryMobLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(MilkHusbandryMobLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(MilkHusbandryMobLevelJob.class).extraJobStreamer = MilkHusbandryMobLevelJob.getJobStreamer();
        this.jobTypeHandler.setJobHandler(ShearHusbandryMobLevelJob.class, 0, 0, 0, 4000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> ShearHusbandryMobLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.getJobHandler(ShearHusbandryMobLevelJob.class).searchInLevelJobData = false;
        this.jobTypeHandler.getJobHandler(ShearHusbandryMobLevelJob.class).extraJobStreamer = ShearHusbandryMobLevelJob.getJobStreamer();
        this.jobTypeHandler.setJobHandler(HarvestApiaryLevelJob.class, 0, 0, 0, 8000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && (!this.isSettler() || this.isSettlerWithinSettlement()) && !this.isInventoryFull(true), foundJob -> HarvestApiaryLevelJob.getJobSequence(this, foundJob));
        this.jobTypeHandler.setJobHandler(FillApiaryFrameLevelJob.class, 0, 0, 0, 2000, (handler, worker) -> !this.isOnWorkBreak() && !this.isOnStrike() && !this.hasCompletedMission() && !this.isSettler() || this.isSettlerWithinSettlement(), foundJob -> FillApiaryFrameLevelJob.getJobSequence(this, this.isSettler(), foundJob), FillApiaryFrameLevelJob::getPreSequenceCompute);
        this.workSettings.registerSetting(new HumanWorkPrioritiesSetting(this));
        this.workSettings.registerSetting(new HumanDietFilterSetting(this));
        this.workSettings.registerSetting(new HumanEquipmentFilterSetting(this));
    }

    @Override
    public void addSaveData(SaveData save) {
        String jobStringID;
        super.addSaveData(save);
        if (this.home != null) {
            save.addPoint("home", this.home);
        }
        save.addBoolean("hideInsideOnLowHealth", (Boolean)this.hideOnLowHealth.get());
        if (this.commandGuardPoint != null) {
            save.addBoolean("commandMoveToGuardPoint", this.commandMoveToGuardPoint);
            save.addPoint("commandGuardPoint", this.commandGuardPoint);
        }
        if (this.commandFollowMob != null) {
            save.addInt("commandFollowMob", this.commandFollowMob.getUniqueID());
        }
        if (this.commandAttackMob != null) {
            save.addInt("commandAttackMob", this.commandAttackMob.getUniqueID());
        }
        if (this.resetCommandsBuffer > 0) {
            save.addInt("resetCommandsBuffer", this.resetCommandsBuffer);
        }
        this.adventureParty.addSaveData("adventurePartyAuth", save);
        save.addInt("settlerSeed", this.settlerSeed);
        if (this.customLook) {
            SaveData customLookData = new SaveData("customLook");
            this.look.addSaveData(customLookData);
            save.addSaveData(customLookData);
        }
        if (this.settlementUniqueID != 0) {
            save.addInt("settlementUniqueID", this.settlementUniqueID);
        }
        save.addInt("visitorSettlementUniqueID", this.visitorSettlementUniqueID);
        if (this.visitorSettlementUniqueID != 0 || this.earlyAccessVisitorIsland != null) {
            save.addLong("travelOutTime", this.travelOutTime);
        }
        save.addInt("settlerQuestTier", this.settlerQuestTier);
        save.addFloat("hungerLevel", this.hungerLevel);
        if (this.lastFoodEaten != null) {
            save.addUnsafeString("lastFoodEaten", this.lastFoodEaten.getStringID());
        }
        if (!this.recentFoodItemIDsEaten.isEmpty()) {
            String[] recentFoodItemStringIDs = (String[])this.recentFoodItemIDsEaten.stream().map(ItemRegistry::getItemStringID).filter(Objects::nonNull).toArray(String[]::new);
            save.addStringArray("recentFoodItemsEaten", recentFoodItemStringIDs);
        }
        if (this.downedState != null) {
            save.addEnum("downedState", this.downedState);
        }
        if (this.recruitReservedSettlementUniqueID != 0 || this.earlyAccessRecruitReservedIsland != null) {
            save.addInt("recruitReservedSettlementUniqueID", this.recruitReservedSettlementUniqueID);
            save.addLong("recruitReservedTimeout", this.recruitReservedTimeout);
        }
        if (this.moveInPoint != null) {
            save.addPoint("moveInPoint", this.moveInPoint);
        }
        if (this.moveOutPoint != null) {
            save.addPoint("moveOutPoint", this.moveOutPoint);
        }
        if (this.objectUser != null) {
            save.addPoint("objectUser", new Point(this.objectUser.tileX, this.objectUser.tileY));
        }
        save.addSafeString("settlerName", this.settlerName);
        save.addInt("hungryStrikeBuffer", this.hungryStrikeBuffer);
        save.addFloat("leaveBuffer", this.leaveBuffer);
        save.addBoolean("isOnStrike", (Boolean)this.isOnStrike.get());
        if (((Boolean)this.isOnStrike.get()).booleanValue()) {
            save.addBoolean("hungerStrike", this.hungerStrike);
        }
        if (this.endStrikeBuffer > 0) {
            save.addInt("endStrikeBuffer", this.endStrikeBuffer);
        }
        save.addInt("workBreakBuffer", this.workBreakBuffer);
        save.addBoolean("onWorkBreak", this.onWorkBreak);
        if (this.nextMissionWorldTime != 0L) {
            save.addLong("nextMissionWorldTime", this.nextMissionWorldTime);
        }
        if (this.jobTypeHandler.lastPerformedJobID != -1 && (jobStringID = LevelJobRegistry.getJobStringID(this.jobTypeHandler.lastPerformedJobID)) != null) {
            save.addSafeString("lastPerformedJobID", jobStringID);
        }
        if (this.jobTypeHandler.prioritizeNextJobID != -1 && (jobStringID = LevelJobRegistry.getJobStringID(this.jobTypeHandler.prioritizeNextJobID)) != null) {
            save.addSafeString("prioritizeNextJobID", jobStringID);
        }
        SaveData workPriorities = new SaveData("workPriorities");
        for (JobTypeHandler.TypePriority typePriority : this.jobTypeHandler.getTypePriorities()) {
            if (typePriority.priority == 0 && !typePriority.disabledByPlayer) continue;
            SaveData workPriority = new SaveData(typePriority.type.getStringID());
            typePriority.addSaveData(workPriority);
            workPriorities.addSaveData(workPriority);
        }
        if (!workPriorities.isEmpty()) {
            save.addSaveData(workPriorities);
        }
        if (!this.workInventory.isEmpty()) {
            SaveData workInventoryData = new SaveData("workInventory");
            for (InventoryItem item : this.workInventory) {
                SaveData itemSave = new SaveData("item");
                item.addSaveData(itemSave);
                workInventoryData.addSaveData(itemSave);
            }
            save.addSaveData(workInventoryData);
        }
        if (this.currentMission != null) {
            SaveData missionData = new SaveData("MISSION");
            missionData.addUnsafeString("stringID", this.currentMission.getStringID());
            this.currentMission.addSaveData(this, missionData);
            save.addSaveData(missionData);
        }
        save.addBoolean("completedMission", this.completedMission);
        save.addBoolean("missionFailed", this.missionFailed);
        if (this.missionFailed && this.missionFailedMessage != null) {
            save.addSaveData(this.missionFailedMessage.getSaveData("missionFailedMessage"));
        }
        save.addBoolean("selfManageEquipment", (Boolean)this.selfManageEquipment.get());
        save.addSaveData(InventorySave.getSave(this.equipmentInventory, "equipment"));
        if (this.villagerData != null) {
            save.addSaveData(this.villagerData.getSaveData("VILLAGER_DATA"));
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        LoadData villagerDataSave;
        LoadData loadData;
        LoadData workPriorities;
        String prioritizeNextJobStringID;
        String recruitReservedString;
        String newStringID;
        String[] item;
        int commandAttackMob;
        super.applyLoadData(save);
        this.home = save.getPoint("home", this.home, false);
        this.hideOnLowHealth.set(save.getBoolean("hideInsideOnLowHealth", (Boolean)this.hideOnLowHealth.get(), false));
        this.commandMoveToGuardPoint = save.getBoolean("commandMoveToGuardPoint", this.commandMoveToGuardPoint, false);
        this.commandGuardPoint = save.getPoint("commandGuardPoint", this.commandGuardPoint, false);
        int commandFollowMob = save.getInt("commandFollowMob", -1, false);
        if (commandFollowMob != -1) {
            this.runOnNextServerTick.add(() -> {
                this.commandFollowMob = GameUtils.getLevelMob(commandFollowMob, this.getLevel());
            });
        }
        if ((commandAttackMob = save.getInt("commandAttackMob", -1, false)) != -1) {
            this.runOnNextServerTick.add(() -> {
                this.commandAttackMob = GameUtils.getLevelMob(commandAttackMob, this.getLevel());
            });
        }
        this.resetCommandsBuffer = save.getInt("resetCommandsBuffer", this.resetCommandsBuffer, false);
        this.adventureParty.applyLoadData("adventurePartyAuth", save);
        this.setSettlerSeed(save.getInt("settlerSeed", GameRandom.globalRandom.nextInt(), false), false);
        LoadData customLookData = save.getFirstLoadDataByName("customLook");
        if (customLookData != null) {
            this.customLook = true;
            this.look = new HumanLook();
            this.look.applyLoadData(customLookData);
        }
        this.settlementUniqueID = save.getInt("settlementUniqueID", this.settlementUniqueID, false);
        if (save.getBoolean("isSettler", false, false)) {
            OneWorldMigration migration;
            int settlerIslandX = save.getInt("settlerIslandX", Integer.MIN_VALUE, false);
            int settlerIslandY = save.getInt("settlerIslandY", Integer.MIN_VALUE, false);
            if (settlerIslandX != Integer.MIN_VALUE && settlerIslandY != Integer.MIN_VALUE && this.isServer() && (migration = this.getServer().world.oneWorldMigration) != null) {
                this.earlyAccessSettlementIsland = new Point(settlerIslandX, settlerIslandY);
            }
        }
        this.visitorSettlementUniqueID = save.getInt("visitorSettlementUniqueID", this.visitorSettlementUniqueID, false);
        boolean isOldVisitor = save.getBoolean("isVisitor", false, false);
        if (save.hasLoadDataByName("isTravelingHuman")) {
            isOldVisitor = save.getBoolean("isTravelingHuman", isOldVisitor, false);
        }
        if (isOldVisitor) {
            LevelIdentifier oldIdentifier = this.getLevel().getIdentifier();
            if (oldIdentifier.isIslandPosition()) {
                this.earlyAccessVisitorIsland = new Point(oldIdentifier.getIslandX(), oldIdentifier.getIslandY());
            } else {
                this.remove();
            }
        }
        if (this.visitorSettlementUniqueID != 0 || this.earlyAccessVisitorIsland != null) {
            this.travelOutTime = save.getLong("travelOutTime", 0L);
            if (this.travelOutTime <= 0L) {
                this.remove();
            }
        }
        this.settlerQuestTier = save.getInt("settlerQuestTier", this.settlerQuestTier, 0, Integer.MAX_VALUE, false);
        LoadData dietFilterSave = save.getFirstLoadDataByName("dietFilter");
        if (dietFilterSave != null) {
            this.loadedDietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
            this.loadedDietFilter.applyLoadData(dietFilterSave);
        }
        this.hungerLevel = save.getFloat("hungerLevel", this.hungerLevel, false);
        String lastFoodEatenStringID = save.getUnsafeString("lastFoodEaten", null, false);
        if (lastFoodEatenStringID != null && (item = ItemRegistry.getItem(newStringID = VersionMigration.tryFixStringID(lastFoodEatenStringID, VersionMigration.oldItemStringIDs))) != null && item.isFoodItem()) {
            this.lastFoodEaten = (FoodConsumableItem)item;
        }
        this.recentFoodItemIDsEaten.clear();
        String[] recentFoodItemStringIDs = save.getStringArray("recentFoodItemsEaten", null, false);
        if (recentFoodItemStringIDs != null) {
            for (String stringID : recentFoodItemStringIDs) {
                String string = VersionMigration.tryFixStringID(stringID, VersionMigration.oldItemStringIDs);
                Item item2 = ItemRegistry.getItem(string);
                if (item2 == null || !item2.isFoodItem()) continue;
                this.recentFoodItemIDsEaten.add(item2.getID());
            }
        }
        this.downedState = save.getBoolean("isTrapped", false, false) ? DownedState.TRAPPED : (DownedState)save.getEnum(DownedState.class, "downedState", null, false);
        this.recruitReservedSettlementUniqueID = save.getInt("recruitReservedSettlementUniqueID", 0, false);
        this.recruitReservedTimeout = save.getLong("recruitReservedTimeout", this.recruitReservedTimeout, false);
        if (save.hasLoadDataByName("recruitReserved") && (recruitReservedString = save.getUnsafeString("recruitReserved", null, false)) != null) {
            try {
                LevelIdentifier recruitReserved = new LevelIdentifier(recruitReservedString);
                if (recruitReserved.isIslandPosition()) {
                    this.earlyAccessRecruitReservedIsland = new Point(recruitReserved.getIslandX(), recruitReserved.getIslandY());
                }
            }
            catch (InvalidLevelIdentifierException recruitReserved) {
                // empty catch block
            }
        }
        this.moveInPoint = save.getPoint("moveInPoint", this.moveInPoint, false);
        this.moveOutPoint = save.getPoint("moveOutPoint", this.moveOutPoint, false);
        this.loadedObjectUserTile = save.getPoint("objectUser", null, false);
        this.settlerName = save.getSafeString("settlerName", this.settlerName, false);
        this.hungryStrikeBuffer = save.getInt("hungryStrikeBuffer", this.hungryStrikeBuffer, false);
        this.leaveBuffer = save.getFloat("leaveBuffer", this.leaveBuffer, false);
        this.isOnStrike.set(save.getBoolean("isOnStrike", (Boolean)this.isOnStrike.get(), false));
        this.hungerStrike = save.getBoolean("hungerStrike", this.hungerStrike, false);
        this.endStrikeBuffer = save.getInt("endStrikeBuffer", this.endStrikeBuffer, false);
        this.workBreakBuffer = save.getInt("workBreakBuffer", this.workBreakBuffer, false);
        this.onWorkBreak = save.getBoolean("onWorkBreak", this.onWorkBreak, false);
        this.nextMissionWorldTime = save.getLong("nextMissionWorldTime", this.nextMissionWorldTime, false);
        String lastPerformedJobStringID = save.getSafeString("lastPerformedJobID", null, false);
        if (lastPerformedJobStringID != null) {
            this.jobTypeHandler.lastPerformedJobID = LevelJobRegistry.getJobID(lastPerformedJobStringID);
        }
        if ((prioritizeNextJobStringID = save.getSafeString("prioritizeNextJobID", null, false)) != null) {
            this.jobTypeHandler.prioritizeNextJobID = LevelJobRegistry.getJobID(prioritizeNextJobStringID);
        }
        if ((workPriorities = save.getFirstLoadDataByName("workPriorities")) != null) {
            for (LoadData loadData2 : workPriorities.getLoadData()) {
                JobTypeHandler.TypePriority priority;
                if (!loadData2.isArray() || (priority = this.jobTypeHandler.getPriority(loadData2.getName())) == null) continue;
                priority.loadSaveData(loadData2);
            }
        }
        this.workInventory.clear();
        LoadData workInventorySave = save.getFirstLoadDataByName("workInventory");
        if (workInventorySave != null) {
            for (LoadData itemSave : workInventorySave.getLoadData()) {
                InventoryItem item3 = InventoryItem.fromLoadData(itemSave);
                if (item3 == null) continue;
                this.workInventory.add(item3);
            }
        }
        if ((loadData = save.getFirstLoadDataByName("MISSION")) != null) {
            String missionStringID = loadData.getUnsafeString("stringID", null, false);
            if (missionStringID != null) {
                try {
                    this.currentMission = (SettlerMission)SettlerMission.registry.getNewInstance(missionStringID);
                    this.currentMission.applySaveData(this, loadData);
                }
                catch (Exception e) {
                    System.err.println("Failed to load explorer mission");
                    e.printStackTrace();
                    this.currentMission = null;
                }
            } else {
                GameLog.warn.println("Could not find explorer mission stringID");
            }
        }
        if (this.isDowned() && this.currentMission != null) {
            this.currentMission = null;
        }
        this.completedMission = save.getBoolean("completedMission", this.completedMission, false);
        this.missionFailed = save.getBoolean("missionFailed", this.missionFailed, false);
        LoadData missionFailedMessageSave = save.getFirstLoadDataByName("missionFailedMessage");
        if (missionFailedMessageSave != null) {
            this.missionFailedMessage = GameMessage.loadSave(missionFailedMessageSave);
        }
        this.selfManageEquipment.set(save.getBoolean("selfManageEquipment", (Boolean)this.selfManageEquipment.get(), false));
        LoadData equipmentSave = save.getFirstLoadDataByName("equipment");
        if (equipmentSave != null) {
            this.equipmentInventory.override(InventorySave.loadSave(equipmentSave));
        }
        if ((villagerDataSave = save.getFirstLoadDataByName("VILLAGER_DATA")) != null) {
            try {
                this.villagerData = new OneWorldNPCVillageData.NPCVillagerData(villagerDataSave);
            }
            catch (LoadDataException e) {
                System.err.println("Error loading villager data for " + this + ": " + e.getMessage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.updateStats(false);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        if (reader.getNextBoolean()) {
            this.settlementUniqueID = reader.getNextInt();
        }
        this.visitorSettlementUniqueID = reader.getNextInt();
        this.settlerQuestTier = reader.getNextShort();
        this.setSettlerSeed(reader.getNextInt(), false);
        this.customLook = reader.getNextBoolean();
        if (this.customLook) {
            this.look.applyContentPacket(reader);
        }
        this.settlerName = reader.getNextBoolean() ? reader.getNextString() : null;
        this.applyWorkPacket(reader);
        this.currentActivity.readSpawnPacket(reader);
        this.equipmentInventory.override(Inventory.getInventory(reader));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.settlementUniqueID != 0) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.settlementUniqueID);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(this.visitorSettlementUniqueID);
        writer.putNextShort((short)this.settlerQuestTier);
        writer.putNextInt(this.settlerSeed);
        writer.putNextBoolean(this.customLook);
        if (this.customLook) {
            this.look.setupContentPacket(writer, false);
        }
        writer.putNextBoolean(this.settlerName != null);
        if (this.settlerName != null) {
            writer.putNextString(this.settlerName);
        }
        this.setupWorkPacket(writer);
        this.currentActivity.writeSpawnPacket(writer);
        this.equipmentInventory.writeContent(writer);
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        this.writeObjectUserPacket(writer);
        writer.putNextBoolean(this.currentMission != null);
        if (this.currentMission != null) {
            writer.putNextShortUnsigned(this.currentMission.getID());
            this.currentMission.setupMovementPacket(this, writer);
        }
        writer.putNextBoolean(this.completedMission);
        writer.putNextBoolean(this.missionFailed);
        writer.putNextBoolean(this.downedState != null);
        if (this.downedState != null) {
            writer.putNextEnum(this.downedState);
        }
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.readObjectUserPacket(reader);
        if (reader.getNextBoolean()) {
            this.currentMission = (SettlerMission)SettlerMission.registry.getNewInstance(reader.getNextShortUnsigned());
            this.currentMission.applyMovementPacket(this, reader);
        } else {
            this.currentMission = null;
        }
        this.completedMission = reader.getNextBoolean();
        this.missionFailed = reader.getNextBoolean();
        DownedState prevDownedState = this.downedState;
        this.downedState = reader.getNextBoolean() ? reader.getNextEnum(DownedState.class) : null;
        if (prevDownedState != this.downedState) {
            this.updateStats(false);
            this.updateTeam();
        }
    }

    public void setupWorkPacket(PacketWriter writer) {
        if (this.home != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.home.x);
            writer.putNextInt(this.home.y);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(this.settlerHappiness);
        writer.putNextFloat(this.hungerLevel);
        writer.putNextBoolean(this.lastFoodEaten != null);
        if (this.lastFoodEaten != null) {
            writer.putNextShortUnsigned(this.lastFoodEaten.getID());
        }
        writer.putNextBoolean(this.onWorkBreak);
        writer.putNextShortUnsigned(this.workInventory.size());
        for (InventoryItem item : this.workInventory) {
            InventoryItem.addPacketContent(item, writer);
        }
    }

    public void applyWorkPacket(PacketReader reader) {
        int foodID;
        Item item;
        this.workDirty = true;
        if (reader.getNextBoolean()) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            this.home = new Point(x, y);
        } else {
            this.home = null;
        }
        this.settlerHappiness = reader.getNextInt();
        this.hungerLevel = reader.getNextFloat();
        this.lastFoodEaten = reader.getNextBoolean() ? ((item = ItemRegistry.getItem(foodID = reader.getNextShortUnsigned())) != null && item.isFoodItem() ? (FoodConsumableItem)item : null) : null;
        this.onWorkBreak = reader.getNextBoolean();
        int itemsCount = reader.getNextShortUnsigned();
        this.workInventory = new ArrayList(itemsCount);
        for (int i = 0; i < itemsCount; ++i) {
            this.workInventory.add(InventoryItem.fromContentPacket(reader));
        }
    }

    public void sendWorkUpdatePacket() {
        this.sendWorkUpdatePacket = true;
    }

    @Override
    public void init() {
        super.init();
        this.workSettings.closeRegistry();
        this.interactingClients.clear();
        if (this.settlerSeed == 0) {
            this.setSettlerSeed(GameRandom.globalRandom.nextInt(), false);
        }
        this.settlerCheck = GameRandom.globalRandom.nextInt(600);
        this.equipmentBuffManager.updateAll();
        this.updateStats(true);
        this.updateTeam();
        this.ai = new BehaviourTreeAI<HumanMob>(this, new HumanAI(320, true, false, 25000), new AIMover(humanPathIterations));
        this.jobTypeHandler.startGlobalCooldown(this.getTime(), GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
        this.jobTypeHandler.startCooldowns(this.getTime());
        this.searchEquipmentCooldown = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(60, 300) * 1000L;
        if (this.isServer() && this.isSettler() && !this.isSettlerOnCurrentLevel()) {
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(this.getServer());
            if (!settlersData.exists(this.getUniqueID())) {
                this.remove();
                System.out.println("REMOVED SETTLER OUTSIDE SETTLEMENT BECAUSE IT IS NOT IN SETTLERS WORLD DATA? " + this);
            } else {
                this.commandGuard(null, this.getX(), this.getY());
            }
        }
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        if (this.objectUser != null) {
            this.objectUser.stopUsing();
        }
        if (this.isServer() && !this.isSettlerWithinSettlementLoadedRegions()) {
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(this.getServer());
            settlersData.refreshWorldSettler(this, false);
        }
    }

    @Override
    protected void handleLoadedValues() {
        super.handleLoadedValues();
        if (this.loadedObjectUserTile != null) {
            GameObject object = this.getLevel().getObject(this.loadedObjectUserTile.x, this.loadedObjectUserTile.y);
            if (object instanceof ObjectUsersObject) {
                ((ObjectUsersObject)((Object)object)).updateUserToExitPos(this.getLevel(), this.loadedObjectUserTile.x, this.loadedObjectUserTile.y, this, 0.0f, 0.0f);
            }
            this.loadedObjectUserTile = null;
        }
    }

    @Override
    public void onLevelChanged() {
        super.onLevelChanged();
        if (this.objectUser != null) {
            this.objectUser.stopUsing(false, 0.0f, 0.0f);
        }
        if (this.isServer() && this.levelSettler != null && !this.isSettlerWithinSettlement(this.levelSettler.data.networkData)) {
            this.setHome(null);
        }
    }

    @Override
    public InteractingClients getInteractingClients() {
        return this.interactingClients;
    }

    @Override
    public GameMessage getLocalization() {
        if (this.settlerName == null) {
            return super.getLocalization();
        }
        return new LocalMessage("mob", this.getStringID() + "name", "name", this.settlerName);
    }

    @Override
    public LootTable getLootTable() {
        LootTable missionLootTable;
        LootTable lootTable = new LootTable();
        for (InventoryItem item : this.workInventory) {
            lootTable.items.add(new LootInventoryItem(item));
        }
        for (int slot = 0; slot < this.equipmentInventory.getSize(); ++slot) {
            InventoryItem item;
            item = this.equipmentInventory.getItem(slot);
            if (item == null) continue;
            lootTable.items.add(new LootInventoryItem(item));
        }
        if (this.currentMission != null && (missionLootTable = this.currentMission.getLootTable(this)) != null) {
            lootTable.items.add(missionLootTable);
        }
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameSkin gameSkin = this.look.getGameSkin(!this.customLook);
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), gameSkin.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent event) {
        super.doBeforeHitCalculatedLogic(event);
        if (!event.isPrevented() && event.getExpectedHealth() <= 0 && this.adventureParty.isInAdventureParty()) {
            this.adventureParty.onSecondWindAttempt(event);
        }
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        WorldSettings worldSettings;
        int maxHealth;
        if (this.isDowned() && health > (maxHealth = Math.max(1, (int)((float)this.getMaxHealth() * 0.1f)))) {
            health = maxHealth;
        }
        if (!(health > 0 || !this.isSettler() && !this.isDowned() || (worldSettings = this.getWorldSettings()) != null && worldSettings.difficulty.canSettlersDie)) {
            health = 1;
            if (this.isServer() && !this.isDowned()) {
                ServerSettlementData serverSettlementData = this.getSettlerSettlementServerData();
                if (serverSettlementData != null) {
                    serverSettlementData.removeSettler(this.getUniqueID(), null);
                    this.moveOutPoint = null;
                    this.settlementUniqueID = 0;
                    this.levelSettler = null;
                    this.cancelJob();
                    this.updateStats(false);
                    LocalMessage displayName = new LocalMessage("deaths", "settlerfrom", "victim", this.getLocalization(), "settlement", serverSettlementData.networkData.getSettlementName());
                    LocalMessage downedMessage = new LocalMessage("deaths", "settlerdowned", "settler", displayName);
                    GameMessageBuilder coloredMessage = new GameMessageBuilder().append("\u00a79").append(downedMessage);
                    serverSettlementData.networkData.streamTeamMembers().forEach(c -> c.sendChatMessage(coloredMessage));
                }
                int timeout = 600000;
                this.setDowned(this.getSettlementUniqueID(), timeout);
                for (InventoryItem inventoryItem : this.workInventory) {
                    this.getLevel().entityManager.pickups.add(inventoryItem.getPickupEntity(this.getLevel(), this.x, this.y));
                }
                this.workInventory.clear();
                this.sendWorkUpdatePacket();
            }
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        ServerSettlementData data;
        super.onDeath(attacker, attackers);
        if (this.isSettler() && this.isServer() && (data = this.getSettlerSettlementServerData()) != null) {
            data.onSettlerDeath(this.getUniqueID());
            if (this.levelSettler != null) {
                this.levelSettler.onSettlerDeath();
            }
            LocalMessage displayName = new LocalMessage("deaths", "settlerfrom", "victim", this.getLocalization(), "settlement", data.networkData.getSettlementName());
            GameMessageBuilder deathMessage = new GameMessageBuilder().append("\u00a79").append(DeathMessageTable.getDeathMessage(attacker, displayName));
            data.networkData.streamTeamMembers().forEach(c -> c.sendChatMessage(deathMessage));
        }
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        if (!this.removed()) {
            if (this.getLevel() != null && this.isServer() && this.adventureParty.isInAdventureParty()) {
                ServerClient serverClient = this.adventureParty.getServerClient();
                serverClient.adventureParty.serverRemove(this, true, isDeath);
            }
            if (isDeath && this.villagerData != null) {
                OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(this.getLevel(), true);
                villageData.addNPCVillager(this.villagerData);
                this.villagerData = null;
            }
        }
        super.remove(knockbackX, knockbackY, attacker, isDeath);
    }

    @Override
    public void addAttackersToSet(HashSet<Attacker> attackers) {
        PlayerMob player;
        super.addAttackersToSet(attackers);
        if (this.adventureParty != null && (player = this.adventureParty.getPlayerMob()) != null) {
            attackers.add(player);
        }
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public void clientTick() {
        this.equipmentBuffManager.clientTickEffects();
        super.clientTick();
        if (this.objectUser != null) {
            this.objectUser.tick();
        }
        this.tickHunger();
        InventoryItem.tickList(this, this, this, null, this.getWorldSettings(), 1.0f, this.workInventory);
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.equipmentInventory.tickItems(this));
        if (this.currentMission != null) {
            this.currentMission.clientTick(this);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.serverFollowersManager.setSummonFocus(this.commandAttackMob);
        if (this.leaveBuffer > 0.0f && this.hungerLevel > 0.0f && !this.isOnStrike()) {
            float bufferDecrease = 1.3888889E-5f;
            this.leaveBuffer -= bufferDecrease;
        }
        if (((Boolean)this.isOnStrike.get()).booleanValue()) {
            if (this.hungerStrike) {
                if (this.hungerLevel > 0.0f) {
                    this.isOnStrike.set(false);
                }
            } else if ((float)this.getSettlerHappiness() >= 0.5f) {
                this.isOnStrike.set(false);
            }
        }
        if (this.endStrikeBuffer > 0) {
            NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
            if ((settlement == null || !settlement.isRaidActiveOrApproaching()) && this.hasCommandOrders()) {
                this.clearCommandsOrders(null);
            }
            this.endStrikeBuffer -= 50;
        }
        if (((Boolean)this.isOnStrike.get()).booleanValue() && this.endStrikeBuffer <= 0) {
            this.isOnStrike.set(false);
        }
        if (!(this.hasActiveJob() || this.isOnStrike() || this.hasCommandOrders())) {
            this.regenWorkBreakBuffer(50);
        }
        if (this.objectUser != null) {
            this.objectUser.tick();
        }
        this.tickHunger();
        InventoryItem.tickList(this, this, this, null, this.getWorldSettings(), 1.0f, this.workInventory);
        if (this.resetCommandsBuffer > 0) {
            this.resetCommandsBuffer -= 50;
            if (this.resetCommandsBuffer <= 0) {
                this.clearCommandsOrders(null);
            }
        }
        this.adventureParty.serverTick();
        if (this.isSettler()) {
            if (this.nextShowMoodTime <= this.getTime()) {
                NetworkSettlementData settlement;
                ArrayList<MoodFloatText.Moods> moodList;
                this.nextShowMoodTime = this.getTime() + 5000L;
                if (this.getCurrentMission() == null && !(moodList = this.getMoods()).isEmpty()) {
                    this.showMoods.runAndSend(moodList);
                }
                if ((settlement = this.getSettlerSettlementNetworkData()) != null) {
                    if (this.levelSettler != null && this.levelSettler.getBed() == null) {
                        if (this.canSubmitNoBedNotification()) {
                            settlement.notifications.submitNotification("nobed", this, SettlementNotificationSeverity.NOTE);
                        }
                    } else {
                        settlement.notifications.removeNotification("nobed", this);
                    }
                    if (this.getSettlerHappiness() < 50) {
                        SettlementNotificationSeverity severity = SettlementNotificationSeverity.NOTE;
                        if (this.getSettlerHappiness() < 25) {
                            severity = SettlementNotificationSeverity.WARNING;
                        }
                        settlement.notifications.submitNotification("unhappy", this, severity);
                    } else {
                        settlement.notifications.removeNotification("unhappy", this);
                    }
                }
            }
            if (this.commandFollowMob != null && !this.commandFollowMob.isSamePlace(this)) {
                this.commandFollowMob = null;
            }
            ++this.settlerCheck;
            if (this.settlerCheck > 600) {
                this.runSettlerCheck();
                this.settlerCheck = 0;
            }
        }
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
        this.interactingClients.serverTick();
        this.clientHasCommandOrders.set(this.hasCommandOrders());
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.equipmentInventory.tickItems(this));
        this.serverTickInventorySync(this.getServer(), this);
        if (this.sendWorkUpdatePacket && this.isServer()) {
            this.sendWorkUpdatePacket = false;
            this.getServer().network.sendToClientsWithEntity(new PacketHumanWorkUpdate(this), this);
        }
        if (this.isVisitor()) {
            this.travelOutTime -= 50L;
            if (this.moveOutPoint == null) {
                if (this.travelOutTime <= 0L) {
                    this.endVisitor();
                }
            } else if (this.travelOutTime <= (long)(-timeToTravelOut) * 1000L) {
                this.remove();
            }
        }
        if (this.currentMission != null) {
            if (!this.currentMission.isOver()) {
                this.currentMission.serverTick(this);
            }
            if (this.currentMission.isOver()) {
                this.currentMission = null;
            }
        }
        if (this.isSettlerOnCurrentLevel()) {
            NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
            SettlerMission currentMission = this.getCurrentMission();
            if (currentMission != null) {
                this.setActivity("idle", 1000, currentMission.getActivityMessage(this));
            } else if (this.isOnStrike() && (settlement == null || !settlement.isRaidActiveOrApproaching())) {
                this.setActivity("idle", 1000, new LocalMessage("activities", "onstrike"));
            } else if (this.isHiding) {
                this.setActivity("idle", 1000, new LocalMessage("activities", "hiding"));
            } else if (this.getWorldEntity().isNight() && (settlement == null || !settlement.isRaidActive())) {
                this.setActivity("idle", 1000, new LocalMessage("activities", "resting"));
            } else if (this.isOnWorkBreak()) {
                this.setActivity("idle", 1000, new LocalMessage("activities", "onbreak"));
            } else if (this.getWorkInventory().isFull()) {
                this.setActivity("idle", 1000, new LocalMessage("activities", "inventoryfull"));
            }
        }
        this.currentActivity.serverTick();
    }

    public final void setHome(int x, int y) {
        this.setHome(new Point(x, y));
    }

    @Override
    public void setHome(Point home) {
        if (!Objects.equals(this.home, home)) {
            this.home = home;
            this.sendWorkUpdatePacket();
        }
    }

    public int getHomeX() {
        if (this.home == null) {
            return -1;
        }
        return this.home.x;
    }

    public int getHomeY() {
        if (this.home == null) {
            return -1;
        }
        return this.home.y;
    }

    @Override
    public int getDir() {
        int forcedDir;
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && (forcedDir = objectUser.getForcedUserDir()) >= 0 && forcedDir <= 3) {
            return forcedDir;
        }
        if (this.isDowned()) {
            return 1;
        }
        return super.getDir();
    }

    @Override
    public void setUsingObject(ObjectUserActive object) {
        if (this.objectUser != null) {
            this.objectUser.stopUsing();
        }
        this.objectUser = object;
        object.init(this);
    }

    @Override
    public ObjectUserActive getUsingObject() {
        return this.objectUser;
    }

    @Override
    public void clearUsingObject() {
        if (this.objectUser != null) {
            this.objectUser.stopUsing();
        }
        this.objectUser = null;
    }

    @Override
    public Rectangle getCollision() {
        Rectangle newCollision;
        Rectangle collision = super.getCollision();
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && (newCollision = objectUser.getUserCollisionBox(collision)) != null) {
            return newCollision;
        }
        return collision;
    }

    @Override
    public Rectangle getHitBox() {
        Rectangle newHitBox;
        Rectangle hitbox = super.getHitBox();
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && (newHitBox = objectUser.getUserHitBox(hitbox)) != null) {
            return newHitBox;
        }
        return hitbox;
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null) {
            return objectUser.getUserSelectBox();
        }
        if (this.isDowned()) {
            return new Rectangle(x - 20, y - 16, 48, 28);
        }
        return super.getSelectBox(x, y);
    }

    @Override
    public float getHungerLevel() {
        return this.hungerLevel;
    }

    public void tickHunger() {
        boolean inAdventureParty;
        if (!this.isSettler()) {
            return;
        }
        if (this.getCurrentMission() != null) {
            return;
        }
        boolean bl = inAdventureParty = this.adventureParty.isInAdventureParty() && !this.isSettlerWithinSettlement();
        if (this.getWorldEntity().isNight() && !inAdventureParty) {
            return;
        }
        double seconds = secondsToPassAtFullHunger;
        if (inAdventureParty) {
            seconds /= (double)adventurePartyHungerUsageMod;
        }
        float usedHunger = (float)(50.0 / (1000.0 * seconds));
        this.useHunger(usedHunger, false);
        if (this.hungerLevel <= 0.0f && !this.isOnStrike() && this.isServer()) {
            this.hungryStrikeBuffer += 50;
            if (this.hungryStrikeBuffer >= 300000) {
                this.hungryStrikeBuffer = 0;
                this.addLeaveBuffer(0.2f);
                this.attemptStartStrike(true, false);
            }
        }
        if (this.hungerLevel < 0.1f) {
            this.lastFoodEaten = null;
        } else {
            this.hungryStrikeBuffer = 0;
        }
    }

    @Override
    public void useHunger(float amount, boolean forceUse) {
        this.hungerLevel = Math.max(0.0f, this.hungerLevel - amount);
    }

    @Override
    public void addHunger(float amount) {
        this.hungerLevel = Math.min(this.hungerLevel + amount, 1.0f + amount);
        this.workDirty = true;
    }

    @Override
    public boolean useFoodItem(FoodConsumableItem item, boolean giveBuff) {
        boolean out = HungerMob.super.useFoodItem(item, giveBuff);
        this.playConsumeSound.runAndSend(item.drinkSound);
        this.lastFoodEaten = item;
        this.recentFoodItemIDsEaten.addFirst(item.getID());
        while (this.recentFoodItemIDsEaten.size() > Settler.dietThoughts.last().variety) {
            this.recentFoodItemIDsEaten.removeLast();
        }
        this.updateHappiness();
        this.sendWorkUpdatePacket();
        return out;
    }

    public void addLeaveBuffer(float percent) {
        ServerSettlementData serverData;
        if (!this.isServer()) {
            return;
        }
        WorldSettings worldSettings = this.getWorldSettings();
        if (worldSettings != null && !worldSettings.survivalMode) {
            return;
        }
        this.leaveBuffer += percent;
        if (this.isSettler() && (serverData = this.getSettlerSettlementServerData()) != null) {
            LocalMessage teamMessage = null;
            if (this.leaveBuffer >= 0.8f && this.leaveBufferWarnings < this.leaveBuffer) {
                this.leaveBufferWarnings = this.leaveBuffer;
                teamMessage = new LocalMessage("misc", "settlerhungrywarning2", "settler", this.getLocalization(), "settlement", serverData.networkData.getSettlementName());
            } else if (this.leaveBuffer >= 0.6f && this.leaveBufferWarnings < this.leaveBuffer) {
                this.leaveBufferWarnings = this.leaveBuffer;
                teamMessage = new LocalMessage("misc", "settlerhungrywarning2", "settler", this.getLocalization(), "settlement", serverData.networkData.getSettlementName());
            } else if (this.leaveBuffer >= 0.4f && this.leaveBufferWarnings < this.leaveBuffer) {
                this.leaveBufferWarnings = this.leaveBuffer;
                teamMessage = new LocalMessage("misc", "settlerhungrywarning1", "settler", this.getLocalization(), "settlement", serverData.networkData.getSettlementName());
            } else {
                this.leaveBufferWarnings = 0.0f;
            }
            if (this.leaveBuffer >= 1.0f && serverData.removeSettler(this.getUniqueID(), null)) {
                teamMessage = new LocalMessage("misc", "settlerhungryleft", "settler", this.getLocalization(), "settlement", serverData.networkData.getSettlementName());
            }
            if (teamMessage != null) {
                LocalMessage finalWarning = teamMessage;
                serverData.networkData.streamTeamMembers().forEach(c -> c.sendChatMessage(finalWarning));
            }
        }
    }

    @Override
    public void setSettlerSeed(int seed, boolean overrideName) {
        this.settlerSeed = seed;
        GameRandom random = new GameRandom(seed + 6);
        this.gender = random.getOneOfWeighted(HumanGender.class, new Object[]{40, HumanGender.MALE, 40, HumanGender.FEMALE, 20, HumanGender.NEUTRAL});
        if (!this.customLook) {
            this.look = new HumanLook();
            this.randomizeLook(this.look, this.gender, random);
        }
        if (this.settlerName == null || overrideName) {
            this.settlerName = this.getRandomName(new GameRandom(seed));
        }
    }

    public void randomizeLook(HumanLook look, HumanGender gender, GameRandom random) {
        look.randomizeLook(random, this.shouldOnlyUseHumanLikeLook(), gender, true, true, true, true);
    }

    @Override
    public int getSettlerSeed() {
        return this.settlerSeed;
    }

    @Override
    public void setSettlerName(String name) {
        this.settlerName = name;
    }

    @Override
    public String getSettlerName() {
        return this.settlerName;
    }

    @Override
    public Point getDrawPos() {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null) {
            return objectUser.getUserAppearancePos();
        }
        return super.getDrawPos();
    }

    @Override
    public int getDrawX() {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null) {
            return objectUser.getUserAppearancePos().x;
        }
        return super.getDrawX();
    }

    @Override
    public int getDrawY() {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null) {
            return objectUser.getUserAppearancePos().y;
        }
        return super.getDrawY();
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (!this.isAttacking) {
            this.turnTo(player);
        }
    }

    @Override
    public boolean canInteract(Mob mob) {
        return true;
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "talktip");
    }

    protected void turnTo(Mob mob) {
        int deltaX = mob.getX() - this.getX();
        int deltaY = mob.getY() - this.getY();
        this.setFacingDir(deltaX, deltaY);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    public boolean isBusy() {
        return this.currentMission != null;
    }

    @Override
    public boolean canBePushed(Mob other) {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && objectUser.preventsUserPushed()) {
            return false;
        }
        if (this.currentMission != null && !this.currentMission.isMobVisible(this)) {
            return false;
        }
        return super.canBePushed(other);
    }

    @Override
    public boolean canPushMob(Mob other) {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && objectUser.preventsUserPushed()) {
            return false;
        }
        if (this.currentMission != null && !this.currentMission.isMobVisible(this)) {
            return false;
        }
        return super.canPushMob(other);
    }

    @Override
    public boolean canLevelInteract() {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && objectUser.preventsUserLevelInteract()) {
            return false;
        }
        return super.canLevelInteract();
    }

    @Override
    public boolean canBeTargetedFromAdjacentTiles() {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null) {
            return objectUser.userCanBeTargetedFromAdjacentTiles();
        }
        return super.canBeTargetedFromAdjacentTiles();
    }

    @Override
    public boolean isVisible() {
        if (this.currentMission != null) {
            return this.currentMission.isMobVisible(this);
        }
        return super.isVisible();
    }

    public MoveToTile getMoveToPoint() {
        if (this.currentMission != null) {
            return this.currentMission.getMoveOutPoint(this);
        }
        if (this.moveOutPoint != null) {
            if (this.isDowned() || this.isTrapped()) {
                this.moveOutPoint = null;
                return null;
            }
            if (this.isAtEdgeOfSettlement()) {
                this.remove();
            }
            return new MoveToTile(this.moveOutPoint, true){

                @Override
                public boolean moveIfPathFailed(float tileDistance) {
                    return tileDistance >= 20.0f;
                }

                @Override
                public boolean isAtLocation(float tileDistance, boolean foundPath) {
                    if (foundPath) {
                        return tileDistance < 2.0f;
                    }
                    return tileDistance < 20.0f;
                }

                @Override
                public void onArrivedAtLocation() {
                    HumanMob.this.remove();
                }
            };
        }
        if (this.moveInPoint != null) {
            return new MoveToTile(this.moveInPoint, true){

                @Override
                public boolean moveIfPathFailed(float tileDistance) {
                    return tileDistance >= 30.0f;
                }

                @Override
                public boolean isAtLocation(float tileDistance, boolean foundPath) {
                    if (foundPath) {
                        return tileDistance < 5.0f;
                    }
                    return tileDistance < 30.0f;
                }

                @Override
                public void onArrivedAtLocation() {
                    HumanMob.this.stopMovingIn();
                }
            };
        }
        return null;
    }

    @Override
    public void assignBed(LevelSettler settler, SettlementBed bed, boolean walkNow) {
        this.moveOutPoint = null;
        this.levelSettler = settler;
        if (bed != null) {
            this.moveIn(bed.tileX, bed.tileY, walkNow);
        } else {
            this.moveIn(settler.data.networkData.getTileX(), settler.data.networkData.getTileY(), walkNow);
        }
    }

    @Override
    public boolean hasBedAssigned() {
        return this.levelSettler != null && this.levelSettler.getBed() != null;
    }

    public void moveIn(int tileX, int tileY, boolean walkNow) {
        this.setHome(tileX, tileY);
        if (walkNow) {
            this.moveInPoint = this.getNewMoveInPoint(tileX, tileY);
        }
    }

    @Override
    public boolean isMovingIn() {
        return this.moveInPoint != null;
    }

    public void stopMovingIn() {
        this.moveInPoint = null;
    }

    @Override
    public void moveOut() {
        if (this.moveOutPoint != null) {
            return;
        }
        if (!this.isDowned()) {
            this.moveOutPoint = this.getNewEdgeOfSettlementTile();
        }
        this.settlementUniqueID = 0;
        this.levelSettler = null;
        this.cancelJob();
    }

    @Override
    public boolean isMovingOut() {
        return this.moveOutPoint != null;
    }

    protected Point getNewMoveInPoint(int homeX, int homeY) {
        if (this.getLevel().isOutside(homeX, homeY)) {
            return new Point(homeX, homeY);
        }
        ArrayList<Point> destinations = new ArrayList<Point>();
        ConnectedSubRegionsResult room = this.getLevel().regionManager.getRoomConnectedByTile(homeX, homeY, true, 2000);
        if (room == null) {
            return new Point(homeX, homeY);
        }
        for (SubRegion sr : room.connectedRegions) {
            for (Point tile : sr.getLevelTiles()) {
                if (this.getLevel().getObject(tile.x, tile.y).isSolid(this.getLevel(), tile.x, tile.y)) continue;
                destinations.add(tile);
            }
        }
        if (destinations.isEmpty()) {
            return new Point(homeX, homeY);
        }
        return (Point)GameRandom.globalRandom.getOneOf(destinations);
    }

    public boolean isAtEdgeOfSettlement() {
        NetworkSettlementData settlement = this.getSettlementNetworkData();
        if (settlement == null) {
            return true;
        }
        Level level = this.getLevel();
        if (!settlement.level.isSamePlace(level)) {
            return true;
        }
        int tileX = this.getTileX();
        int tileY = this.getTileY();
        int padding = 3;
        if (level.tileWidth > 0 && (tileX < padding || tileX >= level.tileWidth - padding)) {
            return true;
        }
        if (level.tileHeight > 0 && (tileY < padding || tileY >= level.tileHeight - padding)) {
            return true;
        }
        Rectangle tileRectangle = settlement.getLoadedTileRectangle();
        tileRectangle = new Rectangle(tileRectangle.x + padding, tileRectangle.y + padding, tileRectangle.width - padding * 2, tileRectangle.height - padding * 2);
        return !tileRectangle.contains(tileX, tileY);
    }

    public Point getNewEdgeOfSettlementTile() {
        NetworkSettlementData settlement = this.getSettlementNetworkData();
        if (settlement == null) {
            return new Point(this.getTileX(), this.getTileY());
        }
        int padding = 3;
        Rectangle[] spawnTileRectangles = ServerSettlementData.getOutsideRectangles(settlement.getLoadedTileRectangle(), -padding, padding);
        Point tile = null;
        for (int i = 0; i < 50; ++i) {
            Rectangle spawnRectangle = GameRandom.globalRandom.getOneOf(spawnTileRectangles);
            tile = new Point(this.getLevel().limitTileXToBounds(spawnRectangle.x + GameRandom.globalRandom.nextInt(spawnRectangle.width), 0, 2), this.getLevel().limitTileYToBounds(spawnRectangle.y + GameRandom.globalRandom.nextInt(spawnRectangle.height), 0, 2));
            if (!this.estimateCanMoveTo(tile.x, tile.y, true)) continue;
            return tile;
        }
        return tile;
    }

    public boolean startMission(SettlerMission mission) {
        if (mission.canStart(this)) {
            this.cancelJob();
            if (this.adventureParty.isInAdventureParty()) {
                this.adventureParty.clear(true);
            }
            this.clearCommandsOrders(null);
            this.currentMission = mission;
            this.currentMission.start(this);
            this.sendMovementPacket(false);
            this.nextMissionWorldTime = this.getWorldTime() + 600000L;
            return true;
        }
        return false;
    }

    public SettlerMission getCurrentMission() {
        return this.currentMission;
    }

    public boolean canDoExpedition(SettlerExpedition expedition) {
        return false;
    }

    public List<ExpeditionList> getPossibleExpeditions() {
        return Collections.emptyList();
    }

    public boolean hasCompletedMission() {
        return this.completedMission;
    }

    public void clearMissionResult() {
        this.completedMission = false;
        this.missionFailed = false;
        this.missionFailedMessage = null;
        this.sendMovementPacket(false);
    }

    public void updateTeam() {
        if (this.getLevel() == null || this.isClient()) {
            return;
        }
        if (this.downedState != null) {
            this.team.set(-1);
            this.owner.set(-1L);
            return;
        }
        int newTeam = -10;
        long newOwnerAuth = -1L;
        if (this.isSettler()) {
            int teamID;
            ServerSettlementData settlementData = this.getSettlerSettlementServerData();
            if (settlementData != null) {
                teamID = settlementData.networkData.getTeamID();
                newOwnerAuth = settlementData.networkData.getOwnerAuth();
            } else {
                teamID = -1;
            }
            if (teamID != -1) {
                newTeam = teamID;
            }
        } else if (this.isVisitor()) {
            newTeam = -1;
        }
        this.team.set(newTeam);
        this.owner.set(newOwnerAuth);
    }

    @Override
    public void tickSettler(ServerSettlementData data, LevelSettler settler) {
        this.levelSettler = settler;
        this.updateTeam();
        this.updateSettlerStats(data, settler);
        this.updateHappiness();
    }

    @Override
    public void makeSettler(ServerSettlementData data, LevelSettler settler) {
        if (this.downedState != null) {
            this.clearDownedState();
        }
        if (!this.isSettler()) {
            this.hungerLevel = 0.2f;
            this.selfManageEquipment.set(data.newSettlerSelfManageEquipment);
            this.markDirty();
        }
        if (this.loadedDietFilter != null) {
            settler.dietFilter.loadFromCopy(this.loadedDietFilter);
            this.loadedDietFilter = null;
        }
        this.canDespawn = false;
        this.visitorSettlementUniqueID = 0;
        this.settlementUniqueID = data.uniqueID;
        this.cachedServerSettlementData = data;
        this.cachedNetworkSettlementData = data.networkData;
        this.cachedVisitorServerSettlementData = null;
        this.cachedVisitorNetworkSettlementData = null;
        this.levelSettler = settler;
        this.setSettlerSeed(settler.settlerSeed, false);
        this.assignBed(settler, settler.getBed(), false);
        this.updateSettlerStats(data, settler);
        this.updateTeam();
    }

    public void startVisitor(ServerSettlementData data) {
        this.levelSettler = null;
        this.settlementUniqueID = 0;
        this.visitorSettlementUniqueID = data.uniqueID;
        this.cachedServerSettlementData = null;
        this.cachedNetworkSettlementData = null;
        this.cachedVisitorServerSettlementData = data;
        this.cachedVisitorNetworkSettlementData = data.networkData;
        this.settlerQuestTier = data.getQuestTiersCompleted();
        this.updateStats(true);
        Point travelPos = data.getFlagTile();
        if (travelPos != null) {
            this.moveIn(travelPos.x, travelPos.y, true);
        } else {
            GameLog.warn.println("Could not find move-in position for visitor " + this);
        }
        this.home = new Point(this.moveInPoint);
        this.travelOutTime = (long)visitorStayTimeSeconds * 1000L;
        this.updateTeam();
    }

    @Override
    public ServerSettlementData getSettlerSettlementServerData() {
        int settlementUniqueID = this.getSettlementUniqueID();
        if (settlementUniqueID == 0) {
            this.cachedServerSettlementData = null;
            return null;
        }
        if (this.cachedServerSettlementData != null && this.cachedServerSettlementData.uniqueID == settlementUniqueID && !this.cachedServerSettlementData.networkData.isUnloadedOrDisbanded()) {
            return this.cachedServerSettlementData;
        }
        this.cachedServerSettlementData = SettlerMob.super.getSettlerSettlementServerData();
        return this.cachedServerSettlementData;
    }

    @Override
    public NetworkSettlementData getSettlerSettlementNetworkData() {
        int settlementUniqueID = this.getSettlementUniqueID();
        if (settlementUniqueID == 0) {
            this.cachedNetworkSettlementData = null;
            return null;
        }
        if (this.cachedNetworkSettlementData != null && this.cachedNetworkSettlementData.uniqueID == settlementUniqueID && !this.cachedNetworkSettlementData.isUnloadedOrDisbanded()) {
            return this.cachedNetworkSettlementData;
        }
        if (this.cachedServerSettlementData != null) {
            if (this.cachedServerSettlementData.uniqueID == settlementUniqueID && !this.cachedServerSettlementData.networkData.isUnloadedOrDisbanded()) {
                this.cachedNetworkSettlementData = this.cachedServerSettlementData.networkData;
                return this.cachedServerSettlementData.networkData;
            }
            this.cachedServerSettlementData = null;
        }
        this.cachedNetworkSettlementData = SettlerMob.super.getSettlerSettlementNetworkData();
        return this.cachedNetworkSettlementData;
    }

    public void endVisitor() {
        this.moveOut();
        ServerSettlementData serverData = this.getVisitorSettlementServerData();
        if (serverData != null) {
            serverData.onVisitorLeave(this);
        }
    }

    public ServerSettlementData getVisitorSettlementServerData() {
        if (this.visitorSettlementUniqueID == 0) {
            this.cachedVisitorServerSettlementData = null;
            return null;
        }
        if (this.cachedVisitorServerSettlementData != null && this.cachedVisitorServerSettlementData.uniqueID == this.visitorSettlementUniqueID && !this.cachedVisitorServerSettlementData.networkData.isUnloadedOrDisbanded()) {
            return this.cachedVisitorServerSettlementData;
        }
        if (!this.isServer()) {
            return null;
        }
        this.cachedVisitorServerSettlementData = SettlementsWorldData.getSettlementsData(this.getMob()).getOrLoadServerData(this.visitorSettlementUniqueID);
        return this.cachedVisitorServerSettlementData;
    }

    public NetworkSettlementData getVisitorSettlementNetworkData() {
        if (this.visitorSettlementUniqueID == 0) {
            this.cachedVisitorNetworkSettlementData = null;
            return null;
        }
        if (this.cachedVisitorNetworkSettlementData != null && this.cachedVisitorNetworkSettlementData.uniqueID == this.visitorSettlementUniqueID && !this.cachedVisitorNetworkSettlementData.isUnloadedOrDisbanded()) {
            return this.cachedVisitorNetworkSettlementData;
        }
        if (this.cachedVisitorServerSettlementData != null) {
            if (this.cachedVisitorServerSettlementData.uniqueID == this.visitorSettlementUniqueID && !this.cachedVisitorServerSettlementData.networkData.isUnloadedOrDisbanded()) {
                this.cachedVisitorNetworkSettlementData = this.cachedVisitorServerSettlementData.networkData;
                return this.cachedServerSettlementData.networkData;
            }
            this.cachedVisitorServerSettlementData = null;
        }
        this.cachedVisitorNetworkSettlementData = SettlementsWorldData.getSettlementsData(this.getMob()).getNetworkData(this.visitorSettlementUniqueID);
        return this.cachedVisitorNetworkSettlementData;
    }

    public ServerSettlementData getSettlementServerData() {
        if (this.isVisitor()) {
            return this.getVisitorSettlementServerData();
        }
        if (this.isSettler()) {
            return this.getSettlerSettlementServerData();
        }
        return SettlementsWorldData.getSettlementsData(this).getOrLoadServerDataAtTile(this.getLevel().getIdentifier(), this.getTileX(), this.getTileY());
    }

    public NetworkSettlementData getSettlementNetworkData() {
        if (this.isVisitor()) {
            return this.getVisitorSettlementNetworkData();
        }
        if (this.isSettler()) {
            return this.getSettlerSettlementNetworkData();
        }
        return SettlementsWorldData.getSettlementsData(this).getNetworkDataAtTile(this.getLevel().getIdentifier(), this.getTileX(), this.getTileY());
    }

    public void updateSettlerStats(ServerSettlementData data, LevelSettler settler) {
        int oldSettlerQuestTier = this.settlerQuestTier;
        this.settlerQuestTier = data.getQuestTiersCompleted();
        if (oldSettlerQuestTier != this.settlerQuestTier) {
            this.updateStats(true);
            this.markDirty();
        }
    }

    public List<HappinessModifier> getHappinessModifiers() {
        ArrayList<HappinessModifier> modifiers = new ArrayList<HappinessModifier>();
        if (this.lastFoodEaten != null && this.lastFoodEaten.quality != null) {
            modifiers.add(this.lastFoodEaten.quality.getModifier());
        } else {
            modifiers.add(FoodQuality.noFoodModifier);
        }
        int differentFoodsEaten = (int)this.recentFoodItemIDsEaten.stream().distinct().count();
        DietThought dietThought = Settler.getDietThought(differentFoodsEaten);
        if (dietThought != null) {
            modifiers.add(dietThought.getModifier());
        }
        if (this.levelSettler != null) {
            SettlementBed bed = this.levelSettler.getBed();
            if (bed != null) {
                modifiers.addAll(bed.getHappinessModifiers());
            } else {
                modifiers.add(HappinessModifier.noBedModifier);
            }
            PopulationThought populationThough = Settler.getPopulationThough(this.levelSettler.data.countTotalSettlers());
            if (populationThough != null) {
                modifiers.add(populationThough.getModifier());
            }
        }
        return modifiers;
    }

    public void updateHappiness() {
        int oldHappiness = this.settlerHappiness;
        this.settlerHappiness = 0;
        List<HappinessModifier> modifiers = this.getHappinessModifiers();
        for (HappinessModifier happinessModifier : modifiers) {
            this.settlerHappiness += happinessModifier.happiness;
        }
        if (oldHappiness != this.settlerHappiness) {
            NetworkSettlementData networkData;
            this.sendWorkUpdatePacket();
            if (this.settlerHappiness >= 100 && (networkData = this.getSettlerSettlementNetworkData()) != null) {
                networkData.streamTeamMembers().forEach(c -> {
                    if (c.achievementsLoaded()) {
                        c.achievements().CLOUD_NINE.markCompleted((ServerClient)c);
                    }
                });
            }
        }
    }

    @Override
    public boolean doesEatFood() {
        return !this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).disabledBySettler;
    }

    @Override
    public int getSettlerHappiness() {
        return this.settlerHappiness;
    }

    public ArrayList<MoodFloatText.Moods> getMoods() {
        ArrayList<MoodFloatText.Moods> moodList = new ArrayList<MoodFloatText.Moods>();
        if (!this.hasActiveJob()) {
            if (this.hungerLevel <= 0.15f) {
                moodList.add(MoodFloatText.Moods.HUNGRY);
            }
            if (!this.isOnStrike() && !this.getWorldEntity().isNight() && this.allowFullInventoryMood && this.getWorkInventory().isFull()) {
                moodList.add(MoodFloatText.Moods.INVENTORY_FULL);
            }
        }
        if (this.levelSettler != null && this.canSubmitNoBedNotification()) {
            SettlementBed bed = this.levelSettler.getBed();
            if (bed != null) {
                SettlementRoom room = this.levelSettler.getBed().getRoom();
                if (room == null) {
                    moodList.add(MoodFloatText.Moods.BED_OUTSIDE);
                }
            } else {
                moodList.add(MoodFloatText.Moods.NO_BED);
            }
        }
        return moodList;
    }

    @Override
    public GameMessage getCurrentActivity() {
        GameMessage currentActivity = this.currentActivity.getCurrentActivity();
        if (currentActivity != null) {
            return currentActivity;
        }
        return new LocalMessage("activities", "idle");
    }

    public static int getBonusHealth(int armor) {
        float healthPerArmorValue = 25.0f;
        return (int)((float)armor * healthPerArmorValue);
    }

    public int getBonusHealth() {
        ActiveBuff equipmentBuff = this.buffManager.getBuff(BuffRegistry.EQUIPMENT_BUFF);
        int armor = equipmentBuff == null ? 0 : equipmentBuff.getModifier(BuffModifiers.ARMOR_FLAT);
        return HumanMob.getBonusHealth(armor);
    }

    public void updateStats(boolean updateHealth) {
        int lastHealth = this.getHealth();
        float healthPercent = (float)this.getHealth() / (float)this.getMaxHealth();
        if (this.isSettler() || this.isVisitor()) {
            this.setMaxHealth(this.settlerHealth + this.getBonusHealth());
            this.setArmor(0);
        } else {
            this.setMaxHealth(this.nonSettlerHealth);
            this.setArmor(50);
        }
        this.buffManager.forceUpdateBuffs();
        if (updateHealth) {
            this.setHealth(Math.max(lastHealth, (int)((float)this.getMaxHealth() * healthPercent)));
        }
    }

    public boolean isFriendlyClient(NetworkClient client) {
        NetworkSettlementData settlement;
        if (client == null || client.playerMob == null) {
            return false;
        }
        if (this.isVisitor() && (settlement = this.getVisitorSettlementNetworkData()) != null && settlement.isClientPartOf(client)) {
            return true;
        }
        long ownerAuth = (Long)this.owner.get();
        if (ownerAuth != -1L) {
            if (ownerAuth == client.authentication) {
                return true;
            }
            if (client.pvpEnabled()) {
                NetworkClient ownerClient = null;
                if (this.isClient()) {
                    ownerClient = this.getClient().getClientByAuth(ownerAuth);
                } else if (this.isServer()) {
                    ownerClient = this.getServer().getClientByAuth(ownerAuth);
                }
                if (ownerClient != null && !((NetworkClient)ownerClient).pvpEnabled()) {
                    return true;
                }
                if (ownerClient == null && !this.getWorldSettings().forcedPvP) {
                    return true;
                }
            } else if (!this.getWorldSettings().forcedPvP) {
                return true;
            }
        }
        return this.isSameTeam(client.playerMob);
    }

    public boolean isFriendlyHuman(HumanMob other) {
        long targetOwnerAuth;
        if (this.isVisitor() || other.isVisitor()) {
            return true;
        }
        if (this.isVisitor(other.settlementUniqueID)) {
            return true;
        }
        if (other.isVisitor(this.settlementUniqueID)) {
            return true;
        }
        int myTeam = this.getTeam();
        int otherTeam = other.getTeam();
        if (myTeam == -1 && otherTeam == -1) {
            return true;
        }
        if (myTeam >= 0 && myTeam == otherTeam) {
            return true;
        }
        long ownerAuth = (Long)this.owner.get();
        if (ownerAuth == (targetOwnerAuth = ((Long)other.owner.get()).longValue())) {
            return true;
        }
        if (!this.getWorldSettings().forcedPvP && ownerAuth != -1L && targetOwnerAuth != -1L) {
            NetworkClient ownerClient = null;
            NetworkClient targetOwnerClient = null;
            if (this.isClient()) {
                ownerClient = this.getClient().getClientByAuth(ownerAuth);
                targetOwnerClient = this.getClient().getClientByAuth(targetOwnerAuth);
            } else if (this.isServer()) {
                ownerClient = this.getServer().getClientByAuth(ownerAuth);
                targetOwnerClient = this.getServer().getClientByAuth(targetOwnerAuth);
            }
            if (ownerClient == null || targetOwnerClient == null) {
                return true;
            }
            return !ownerClient.pvpEnabled() || !((NetworkClient)targetOwnerClient).pvpEnabled();
        }
        return false;
    }

    public GameMessage willJoinAdventureParty(ServerClient client) {
        if (this.isOnStrike()) {
            return new LocalMessage("ui", "settlerjoinpartynothappy");
        }
        return null;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        Stream<ModifierValue<Object>> out = super.getDefaultModifiers();
        if (this.downedState != null) {
            return Stream.concat(out, Stream.of(new ModifierValue<Boolean>(BuffModifiers.PARALYZED, true), new ModifierValue<Float>(BuffModifiers.INCOMING_DAMAGE_MOD, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(-0.5f))));
        }
        if (!this.isSettler()) {
            out = Stream.concat(out, Stream.of(new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(2.0f)), new ModifierValue<Integer>(BuffModifiers.ARMOR_PEN_FLAT, 1000)));
        }
        if (this.getLevel() != null) {
            NetworkSettlementData settlement;
            if (this.isSettler() && this.adventureParty.isInAdventureParty()) {
                out = Stream.concat(out, Stream.of(new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE).max(Float.valueOf(0.0f))));
            }
            if ((settlement = this.getSettlerSettlementNetworkData()) != null && settlement.isRaidActive() && settlement.isTileWithinBounds(this.getTileX(), this.getTileY())) {
                out = Stream.concat(out, Stream.of(new ModifierValue<Float>(BuffModifiers.SPEED_FLAT, Float.valueOf(10.0f))));
            }
            if (this.getHealthPercent() <= 0.4f) {
                out = Stream.concat(out, Stream.of(new ModifierValue<Float>(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.5f))));
            }
        }
        return out;
    }

    @Override
    public float getRegenFlat() {
        if (this.adventureParty.isInAdventureParty() && !this.isSettlerWithinSettlement()) {
            return super.getRegenFlat();
        }
        int maxHealth = this.getMaxHealth();
        return Math.max((float)maxHealth / (float)this.maxSecondsFullHealthRegen, super.getRegenFlat());
    }

    @Override
    public boolean canTarget(Mob target) {
        if (target.isPlayer && this.isFriendlyClient(((PlayerMob)target).getNetworkClient())) {
            return false;
        }
        if (target instanceof HumanMob) {
            return !this.isFriendlyHuman((HumanMob)target);
        }
        return super.canTarget(target);
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        if (this.isDowned()) {
            return false;
        }
        if (attackerClient != null && this.isFriendlyClient(attackerClient)) {
            return false;
        }
        return super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public boolean isSameTeam(Mob other) {
        if (other instanceof HumanMob) {
            return this.isFriendlyHuman((HumanMob)other);
        }
        return super.isSameTeam(other);
    }

    @Override
    public boolean isSettler() {
        return this.settlementUniqueID != 0;
    }

    @Override
    public int getSettlementUniqueID() {
        return this.settlementUniqueID;
    }

    @Override
    public String getSettlerStringID() {
        return this.settlerStringID;
    }

    @Override
    public void addToWealthCounter(SettlementWealthCounter counter) {
        for (int i = 0; i < this.equipmentInventory.getSize(); ++i) {
            counter.addSettlerItem(this.equipmentInventory.getItem(i));
        }
    }

    public boolean isVisitor() {
        return this.visitorSettlementUniqueID != 0;
    }

    public boolean isVisitor(int settlementUniqueID) {
        return this.visitorSettlementUniqueID != 0 && this.visitorSettlementUniqueID == settlementUniqueID;
    }

    public int getVisitorToSettlementUniqueID() {
        return this.visitorSettlementUniqueID;
    }

    @Override
    public Inventory getInventory() {
        return this.equipmentInventory;
    }

    public InventoryItem getArmorItem(int slot) {
        return this.equipmentInventory.getItem(slot);
    }

    public InventoryItem getCosmeticArmorItem(int slot) {
        return this.equipmentInventory.getItem(3 + slot);
    }

    public InventoryItem getDisplayArmor(int slot, InventoryItem defaultItem) {
        InventoryItem cosmeticItem = this.getCosmeticArmorItem(slot);
        if (cosmeticItem != null && cosmeticItem.item.isArmorItem()) {
            return cosmeticItem;
        }
        InventoryItem armorItem = this.getArmorItem(slot);
        if (armorItem != null && armorItem.item.isArmorItem()) {
            return armorItem;
        }
        return defaultItem;
    }

    public InventoryItem getInventoryWeapon() {
        return this.equipmentInventory.getItem(6);
    }

    @Override
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        return new HumanMobItemAttackSlot(this);
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        InventoryItem weapon;
        if (slotType != null) {
            switch (slotType) {
                case HELMET: {
                    InventoryItem helmetItem = this.getArmorItem(0);
                    return helmetItem != null && helmetItem.item.getID() == item.getID();
                }
                case CHEST: {
                    InventoryItem chestItem = this.getArmorItem(1);
                    return chestItem != null && chestItem.item.getID() == item.getID();
                }
                case FEET: {
                    InventoryItem feetItem = this.getArmorItem(2);
                    return feetItem != null && feetItem.item.getID() == item.getID();
                }
            }
        }
        return (weapon = this.getInventoryWeapon()) != null && weapon.item.getID() == item.getID();
    }

    @Override
    public int getAvailableArrows(String purpose) {
        return 1000;
    }

    @Override
    public int getAvailableBullets(String purpose) {
        return 1000;
    }

    @Override
    public int getAvailableAmmo(Item[] items, String purpose) {
        return 1000;
    }

    @Override
    public Item getFirstAvailableArrow(String purpose) {
        return ItemRegistry.getItem("stonearrow");
    }

    @Override
    public Item getFirstAvailableBullet(String purpose) {
        return ItemRegistry.getItem("simplebullet");
    }

    @Override
    public Item getFirstAvailableAmmo(Item[] items, String purpose) {
        if (items.length == 0) {
            return null;
        }
        return items[0];
    }

    @Override
    public AmmoConsumed removeAmmo(Item item, int amount, String purpose) {
        return new AmmoConsumed(amount, 0.0f);
    }

    @Override
    public Point getJobSearchTile() {
        if (this.home != null) {
            return new Point(this.home.x, this.home.y);
        }
        return new Point(this.getTileX(), this.getTileY());
    }

    @Override
    public Rectangle getJobSearchBounds() {
        NetworkSettlementData settlementNetworkData;
        if (this.isSettler() && (settlementNetworkData = this.getSettlementNetworkData()) != null) {
            return settlementNetworkData.getLevelRectangle();
        }
        return EntityJobWorker.super.getJobSearchBounds();
    }

    @Override
    public JobSequence findJob() {
        if (this.getWorldEntity().isNight()) {
            return null;
        }
        if (this.objectUser != null) {
            return null;
        }
        NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
        if (settlement != null) {
            if (settlement.isRaidActive()) {
                return null;
            }
            if (settlement.isDisbanding()) {
                return null;
            }
            if (!settlement.hasOwner()) {
                return null;
            }
        }
        if (this.isHiding) {
            return null;
        }
        if (this.isVisitor()) {
            return null;
        }
        if (this.hasCommandOrders()) {
            return null;
        }
        if (this.adventureParty.isInAdventureParty()) {
            return null;
        }
        JobSequence foundJob = EntityJobWorker.super.findJob();
        if (foundJob == null && this.allowFullInventoryMood && this.getWorkInventory().isFull()) {
            this.submitFullInventoryNotification();
        } else {
            this.removeFullInventoryNotification();
        }
        return foundJob;
    }

    @Override
    public ZoneTester getJobRestrictZone() {
        if (this.levelSettler != null) {
            return this.levelSettler.isTileInSettlementBoundsAndRestrictZoneTester();
        }
        Level level = this.getLevel();
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(this);
        ZoneTester noSettlementTester = (tileX, tileY) -> settlementsData.getNetworkDataAtTile(level.getIdentifier(), tileX, tileY) == null;
        ZoneTester tester = EntityJobWorker.super.getJobRestrictZone();
        return tester.and(noSettlementTester);
    }

    @Override
    public JobTypeHandler getJobTypeHandler() {
        return this.jobTypeHandler;
    }

    @Override
    public boolean isFullInventoryNotificationStillValid() {
        return this.allowFullInventoryMood && this.getWorkInventory().isFull();
    }

    public boolean isInventoryFull(boolean changeAllowMood) {
        boolean out = this.getWorkInventory().isFull();
        if (changeAllowMood) {
            this.allowFullInventoryMood = out;
        }
        return out;
    }

    @Override
    public WorkInventory getWorkInventory() {
        return new WorkInventory(){

            @Override
            public ListIterator<InventoryItem> listIterator() {
                return HumanMob.this.workInventory.listIterator();
            }

            @Override
            public Iterable<InventoryItem> items() {
                return HumanMob.this.workInventory;
            }

            @Override
            public Stream<InventoryItem> stream() {
                return HumanMob.this.workInventory.stream();
            }

            @Override
            public void markDirty() {
                HumanMob.this.sendWorkUpdatePacket();
            }

            @Override
            public void add(InventoryItem item) {
                if (!HumanMob.this.isSettler()) {
                    return;
                }
                item.combineOrAddToList(HumanMob.this.getLevel(), null, HumanMob.this.workInventory, "add");
                this.markDirty();
            }

            @Override
            public int getCanAddAmount(InventoryItem item) {
                if (this.getTotalItemStacks() > 4) {
                    return 0;
                }
                float currentBrokerValue = HumanMob.this.workInventory.stream().reduce(Float.valueOf(0.0f), (value, i) -> Float.valueOf(value.floatValue() + i.getBrokerValue()), Float::sum).floatValue();
                float remainingValue = 300.0f - currentBrokerValue;
                if (remainingValue < 0.0f) {
                    return 0;
                }
                float singleItemBrokerValue = item.item.getBrokerValue(item);
                return Math.min((int)(remainingValue / singleItemBrokerValue), item.getAmount());
            }

            @Override
            public boolean isFull() {
                if (this.getTotalItemStacks() > 4) {
                    return true;
                }
                float brokerValue = HumanMob.this.workInventory.stream().reduce(Float.valueOf(0.0f), (value, i) -> Float.valueOf(value.floatValue() + i.getBrokerValue()), Float::sum).floatValue();
                return brokerValue > 300.0f;
            }

            @Override
            public int getTotalItemStacks() {
                return HumanMob.this.workInventory.size();
            }

            @Override
            public boolean isEmpty() {
                return HumanMob.this.workInventory.isEmpty();
            }
        };
    }

    public long getShopSeed() {
        return (long)this.getWorldEntity().getDay() * 191L * (long)this.settlerSeed;
    }

    @Override
    public boolean hasActiveJob() {
        return this.ai.blackboard.getObject(ActiveJob.class, "currentJob") != null;
    }

    @Override
    public boolean isInWorkAnimation() {
        return this.isAttacking;
    }

    @Override
    public boolean isJobCancelled() {
        return this.cancelJob;
    }

    @Override
    public void resetJobCancelled() {
        this.cancelJob = false;
    }

    public void cancelJob() {
        this.cancelJob = true;
    }

    @Override
    public int getWorkBreakBuffer() {
        return this.workBreakBuffer;
    }

    @Override
    public boolean isOnWorkBreak() {
        return this.onWorkBreak;
    }

    @Override
    public void useWorkBreakBuffer(int milliseconds) {
        float happinessPercent = this.isSettler() ? GameMath.limit((float)this.getSettlerHappiness() / 100.0f, 0.0f, 1.0f) : 0.5f;
        float usageMod = GameMath.lerp(happinessPercent, workBreakBufferUsageModAtNoHappiness, workBreakBufferUsageModAtFullHappiness);
        this.workBreakBuffer -= (int)((float)milliseconds * usageMod);
        if (this.workBreakBuffer <= 0 && !this.onWorkBreak) {
            this.onWorkBreak = true;
            this.attemptStartStrike(false, true);
            this.sendWorkUpdatePacket();
        }
    }

    @Override
    public void regenWorkBreakBuffer(int milliseconds) {
        int maxWorkBreakBuffer;
        float happinessPercent = this.isSettler() ? GameMath.limit((float)this.getSettlerHappiness() / 100.0f, 0.0f, 1.0f) : 0.5f;
        float regenMod = GameMath.lerp(happinessPercent, workBreakBufferRegenModAtNoHappiness, workBreakBufferRegenModAtFullHappiness);
        this.workBreakBuffer += (int)((float)milliseconds * regenMod);
        if (this.onWorkBreak && this.workBreakBuffer >= resetWorkBreakWhenBufferAt) {
            this.sendWorkUpdatePacket();
            this.onWorkBreak = false;
        }
        if (this.workBreakBuffer >= (maxWorkBreakBuffer = GameMath.lerp(happinessPercent, maxWorkBreakBufferAtNoHappiness, maxWorkBreakBufferAtFullHappiness))) {
            this.workBreakBuffer = maxWorkBreakBuffer;
            if (this.onWorkBreak) {
                this.sendWorkUpdatePacket();
            }
            this.onWorkBreak = false;
        }
    }

    @Override
    public boolean isOnStrike() {
        return (Boolean)this.isOnStrike.get();
    }

    @Override
    public void attemptStartStrike(boolean isHungerStrike, boolean activateOthers) {
        if (this.isSettlerWithinSettlement() && !this.isOnStrike()) {
            float happinessPercent = GameMath.limit((float)this.getSettlerHappiness() / 100.0f, 0.0f, 1.0f);
            if (isHungerStrike || (double)happinessPercent < 0.5) {
                float strikeChance = (float)(-Math.pow(happinessPercent * 2.0f, 0.75)) + 1.0f;
                if (isHungerStrike || GameRandom.globalRandom.getChance(strikeChance)) {
                    ServerSettlementData data;
                    this.isOnStrike.set(true);
                    this.hungerStrike = isHungerStrike;
                    this.endStrikeBuffer = 300000;
                    this.cancelJob();
                    if (activateOthers && (data = this.getSettlerSettlementServerData()) != null) {
                        for (LevelSettler settler : data.getSettlers()) {
                            SettlerMob mob = settler.getMob();
                            if (!(mob instanceof EntityJobWorker)) continue;
                            ((EntityJobWorker)((Object)mob)).attemptStartStrike(false, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setActivity(String type, int priority, GameMessage description) {
        this.currentActivity.setActivity(type, priority, description);
    }

    @Override
    public void clearActivity(String type) {
        this.currentActivity.clearActivity(type);
    }

    @Override
    public void showPickupAnimation(int x, int y, Item item, int attackAnimTime) {
        this.attackSprite(x, y, item, attackAnimTime, true);
    }

    @Override
    public void showPlaceAnimation(int x, int y, Item item, int attackAnimTime) {
        this.attackSprite(x, y, item, attackAnimTime, false);
    }

    @Override
    public void showWorkAnimation(int x, int y, Item item, int attackAnimTime) {
        long timeSinceLastWorkRequest = this.getWorldEntity().getLocalTime() - this.lastWorkAnimationRequest;
        if (timeSinceLastWorkRequest >= (long)(attackAnimTime / 2)) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextBoolean(item != null);
            if (item != null) {
                writer.putNextShortUnsigned(item.getID());
            }
            writer.putNextInt(attackAnimTime);
            this.itemWorkSpriteAbility.runAndSend(content);
            this.lastWorkAnimationRequest = this.getWorldEntity().getLocalTime();
        }
    }

    @Override
    public void showAttackAnimation(int x, int y, Item item, int attackAnimTime) {
        this.attackItem(x, y, item, attackAnimTime, attackAnimTime);
    }

    @Override
    public void showHoldAnimation(Item item, int timeout) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(item != null);
        if (item != null) {
            writer.putNextShortUnsigned(item.getID());
            writer.putNextInt(timeout);
        }
        this.itemHoldSpriteAbility.runAndSend(content);
    }

    @Override
    public void clearHoldAnimation() {
        this.showHoldAnimation(null, 0);
    }

    @Override
    public void giveBaitBack(BaitItem bait) {
    }

    @Override
    public void stopFishing() {
        this.isAttacking = false;
    }

    @Override
    public void showFishingWaitAnimation(FishingRodItem fishingRod, int targetX, int targetY) {
        this.showAttack(targetX, targetY, false);
        InventoryItem invItem = FishingPoleHolding.setGNDData(new InventoryItem("fishinghold"), fishingRod);
        this.showItemAttack(invItem, targetX, targetY, 0, 0, null);
        this.attackAnimTime = 5000;
    }

    @Override
    public boolean isFishingSwingDone() {
        this.getAttackAnimProgress();
        return !this.isAttacking;
    }

    @Override
    public void giveCaughtItem(FishingEvent event, InventoryItem item) {
        this.getWorkInventory().add(item);
    }

    @Override
    public boolean shouldClearAfterTime(long time) {
        return time < this.clearRopedTime;
    }

    public void clearRopedMobs() {
        this.clearRopedTime = this.getTime() + 1000L;
    }

    public int getRecruitedToSettlementUniqueID(ServerClient client) {
        if (this.recruitReservedSettlementUniqueID != 0 && this.getTime() < this.recruitReservedTimeout) {
            CachedSettlementData cachedData = SettlementsWorldData.getSettlementsData(this).getCachedData(this.recruitReservedSettlementUniqueID);
            if (cachedData != null) {
                return this.recruitReservedSettlementUniqueID;
            }
            this.recruitReservedSettlementUniqueID = 0;
        }
        if (this.downedState == DownedState.TRAPPED) {
            return 0;
        }
        if (this.isVisitor()) {
            return this.visitorSettlementUniqueID;
        }
        return 0;
    }

    public boolean isValidRecruitment(CachedSettlementData settlement, ServerClient client) {
        return true;
    }

    public boolean shouldTeleportToLevelOnRecruited(ServerClient client, ServerSettlementData data, LevelSettler settler) {
        return !this.isDowned();
    }

    public void onRecruited(ServerClient client, ServerSettlementData data, LevelSettler settler) {
        if (client != null && client.achievementsLoaded()) {
            client.achievements().HEADHUNTER.markCompleted(client);
        }
        if (this.villagerData != null) {
            OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(this.getLevel(), true);
            villageData.addNPCVillager(this.villagerData);
            this.villagerData = null;
        }
        this.clearRopedMobs();
        this.updateStats(true);
    }

    @Override
    public boolean hasCommandOrders() {
        if (this.getLevel() != null && this.isClient()) {
            return (Boolean)this.clientHasCommandOrders.get();
        }
        return this.commandGuardPoint != null || this.commandFollowMob != null || this.commandAttackMob != null;
    }

    @Override
    public boolean canBeCommanded(Client client) {
        NetworkSettlementData settlement;
        if (this.isBusy()) {
            return false;
        }
        if (client.adventureParty.contains(this)) {
            return true;
        }
        if (this.isOnStrike() && (settlement = this.getSettlerSettlementNetworkData()) != null && !settlement.isRaidActiveOrApproaching() && settlement.isTileWithinBounds(this.getTileX(), this.getTileY())) {
            return false;
        }
        int myTeam = client.getTeam();
        if (myTeam == -1) {
            return (Long)this.owner.get() == GameAuth.getAuthentication();
        }
        return (Integer)this.team.get() == myTeam;
    }

    @Override
    public boolean canBeCommanded(ServerClient client) {
        NetworkSettlementData settlement;
        if (this.isBusy()) {
            return false;
        }
        if (client.adventureParty.contains(this)) {
            return true;
        }
        if (this.isOnStrike() && (settlement = this.getSettlerSettlementNetworkData()) != null && !settlement.isRaidActiveOrApproaching() && settlement.isTileWithinBounds(this.getTileX(), this.getTileY())) {
            return false;
        }
        settlement = this.getSettlerSettlementNetworkData();
        if (settlement == null) {
            return false;
        }
        return settlement.doesClientHaveAccess(client);
    }

    @Override
    public void clearCommandsOrders(ServerClient commander) {
        boolean hadCommands = this.hasCommandOrders();
        this.commandFollowMob = null;
        this.commandGuardPoint = null;
        this.commandMoveToGuardPoint = false;
        this.commandMoveToFollowPoint = false;
        this.commandAttackMob = null;
        this.sendNextMovementPacket = true;
        this.stopMovingIn();
        this.adventureParty.clear(true);
        this.ai.blackboard.submitEvent("resetTarget", new AIEvent());
        if (hadCommands) {
            this.ai.blackboard.submitEvent("wanderNow", new AIEvent());
        }
    }

    @Override
    public void commandFollow(ServerClient commander, Mob mob) {
        this.cancelJob();
        this.commandFollowMob = mob;
        this.commandGuardPoint = null;
        this.commandMoveToGuardPoint = false;
        this.commandMoveToFollowPoint = true;
        this.commandAttackMob = null;
        this.sendNextMovementPacket = true;
        this.stopMovingIn();
        this.ai.blackboard.submitEvent("resetTarget", new AIEvent());
        this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
        if (this.canJoinAdventureParties && commander != null && mob == commander.playerMob && this.willJoinAdventureParty(commander) == null && this.adventureParty.getServerClient() != commander) {
            this.adventureParty.set(commander);
        }
        this.resetCommandsBuffer = 0;
    }

    @Override
    public void commandGuard(ServerClient commander, int x, int y) {
        this.cancelJob();
        this.commandFollowMob = null;
        this.commandGuardPoint = new Point(x, y);
        this.commandMoveToGuardPoint = true;
        this.commandMoveToFollowPoint = false;
        this.commandAttackMob = null;
        this.sendNextMovementPacket = true;
        this.stopMovingIn();
        if (commander != null && !this.isSamePlace(commander.playerMob)) {
            this.getLevel().entityManager.changeMobLevel(this, commander.playerMob.getLevel(), commander.playerMob.getX(), commander.playerMob.getY(), true);
            this.ai.blackboard.mover.stopMoving(this);
        }
        if (this.isSettlerWithinSettlement()) {
            this.adventureParty.clear(true);
        } else if (this.canJoinAdventureParties && commander != null && this.willJoinAdventureParty(commander) == null && this.adventureParty.getServerClient() != commander) {
            this.adventureParty.set(commander);
        }
        this.ai.blackboard.submitEvent("resetTarget", new AIEvent());
        this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
        this.resetCommandsBuffer = 600000;
    }

    @Override
    public void commandAttack(ServerClient commander, Mob mob) {
        if (mob == null || mob.canBeTargeted(this, null)) {
            this.commandMoveToGuardPoint = false;
            this.commandMoveToFollowPoint = false;
            this.stopMovingIn();
            this.commandAttackMob = mob;
            this.ai.blackboard.submitEvent("resetTarget", new AIEvent());
            this.ai.blackboard.submitEvent("newCommandSet", new AIEvent());
            this.resetCommandsBuffer = 600000;
        }
    }

    @Override
    public void setHideOnLowHealth(boolean shouldHideInside) {
        this.hideOnLowHealth.set(shouldHideInside);
    }

    @Override
    public boolean getHideOnLowHealth() {
        return (Boolean)this.hideOnLowHealth.get();
    }

    @Override
    public Mob getMob() {
        return this;
    }

    public Predicate<Mob> filterHumanTargets() {
        if (this.downedState != null) {
            return m -> false;
        }
        return m -> m.isHostile;
    }

    @Override
    public boolean canAttack() {
        return this.downedState == null && super.canAttack();
    }

    public void attackSprite(int x, int y, Item item, int animTime, boolean inverted) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextBoolean(item != null);
        if (item != null) {
            writer.putNextShortUnsigned(item.getID());
        }
        writer.putNextInt(animTime);
        writer.putNextBoolean(inverted);
        this.itemSwingSpriteAbility.runAndSend(content);
    }

    public void attackSprite(int x, int y, String itemStringID, int animTime, boolean inverted) {
        this.attackSprite(x, y, itemStringID == null ? null : ItemRegistry.getItem(itemStringID), animTime, inverted);
    }

    public void attackItem(int x, int y, Item item, int attackTime, int cooldown) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextBoolean(false);
        writer.putNextShortUnsigned(item.getID());
        writer.putNextInt(attackTime);
        writer.putNextInt(cooldown);
        this.itemAttackAbility.runAndSend(content);
    }

    public void attackItem(int x, int y, InventoryItem item, int attackTime, int cooldown) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextBoolean(true);
        item.addPacketContent(writer);
        writer.putNextInt(attackTime);
        writer.putNextInt(cooldown);
        this.itemAttackAbility.runAndSend(content);
    }

    public void attackItem(int x, int y, InventoryItem item) {
        this.attackItem(x, y, item, item.item.getAttackAnimTime(item, this), Math.max(item.item.getAttackCooldownTime(item, this), item.item.getItemCooldownTime(item, this)));
    }

    public void attackItem(int x, int y, String itemStringID, int animTime, int cooldown) {
        this.attackItem(x, y, ItemRegistry.getItem(itemStringID), animTime, cooldown);
    }

    @Override
    public float getAttackAnimProgress() {
        float out = super.getAttackAnimProgress();
        if (!this.isAttacking) {
            long timeSinceLastWorkRequest = this.getLocalTime() - this.lastWorkAnimationRequest;
            if (this.lastWorkAnimationRequest > 0L && timeSinceLastWorkRequest < (long)this.attackAnimTime) {
                this.attackTime = this.getTime();
                this.isAttacking = true;
            }
        }
        return out;
    }

    public boolean shouldOnlyUseHumanLikeLook() {
        return true;
    }

    @Override
    public DrawOptions getUserDrawOptions(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, Consumer<HumanDrawOptions> humanDrawOptionsModifier) {
        GameLight light = level.getLightLevel(HumanMob.getTileCoordinate(x), HumanMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean humanLikeLook = this.shouldOnlyUseHumanLikeLook();
        if (this.customLook) {
            humanLikeLook = false;
        }
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, humanLikeLook);
        this.setDefaultArmor(humanOptions);
        this.setDisplayArmor(humanOptions);
        this.modifyExpressionDrawOptions(humanOptions);
        humanOptions.invis(this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).blinking(this.isBlinking()).sprite(sprite).dir(dir).light(light);
        humanDrawOptionsModifier.accept(humanOptions);
        humanOptions = this.setCustomItemAttackOptions(humanOptions);
        DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
        return () -> {
            drawOptions.draw();
            markerOptions.draw();
        };
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.objectUser != null && !this.objectUser.drawsUser()) {
            return;
        }
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(HumanMob.getTileCoordinate(x), HumanMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(HumanMob.getTileCoordinate(x), HumanMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        boolean humanLikeLook = this.shouldOnlyUseHumanLikeLook();
        if (this.customLook) {
            humanLikeLook = false;
        }
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, humanLikeLook).invis(this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).blinking(this.isBlinking()).sprite(sprite).mask(swimMask).dir(dir).light(light);
        this.setDefaultArmor(humanOptions);
        this.setDisplayArmor(humanOptions);
        this.modifyExpressionDrawOptions(humanOptions);
        boolean inLiquid = this.inLiquid(x, y);
        if (this.isDowned()) {
            humanOptions.blinking(true);
            humanOptions.dir(1);
            humanOptions.rotate(90.0f, 32, 32).drawOffset(6, inLiquid ? 7 : 11);
            humanOptions.mask(new MaskShaderOptions(0, 0));
        } else if (inLiquid) {
            humanOptions.armSprite(2);
            humanOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
        }
        humanOptions = this.setCustomItemAttackOptions(humanOptions);
        this.buffManager.addHumanDraws(humanOptions);
        final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
        final DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (boat != null) {
                    boat.draw();
                }
                drawOptions.draw();
                markerOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    protected HumanDrawOptions setCustomItemAttackOptions(HumanDrawOptions options) {
        options = this.setupAttackDraw(options);
        if (!this.isAttacking) {
            NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
            if (this.isOnStrike() && (settlement == null || !settlement.isRaidActiveOrApproaching())) {
                options.holdItem(new InventoryItem("strikebanner"));
            } else if (this.customHoldItem != null && this.getLocalTime() <= this.customHoldItemTimeout) {
                options.holdItem(this.customHoldItem);
            }
        }
        return options;
    }

    @Override
    public boolean itemAttackerHoldsItem(InventoryItem item) {
        return this.hasCommandOrders() || this.adventureParty.isInAdventureParty();
    }

    public DrawOptions getMarkerDrawOptions(int x, int y, GameLight light, GameCamera camera, int xOffset, int yOffset, PlayerMob perspective) {
        QuestMarkerOptions markerOptions = this.getMarkerOptions(perspective);
        if (markerOptions != null) {
            return markerOptions.getDrawOptions(x, y, light, camera, xOffset, yOffset);
        }
        return () -> {};
    }

    public QuestMarkerOptions getMarkerOptions(PlayerMob perspective) {
        if (this.isVisitor()) {
            return new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor);
        }
        if (this.completedMission) {
            return new QuestMarkerOptions('?', this.missionFailed ? QuestMarkerOptions.grayColor : QuestMarkerOptions.orangeColor);
        }
        return null;
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        Point sprite = super.getAnimSprite(x, y, dir);
        if (this.downedState != null) {
            sprite.x = 6;
        }
        return sprite;
    }

    @Override
    public void setFacingDir(float deltaX, float deltaY) {
        if (this.downedState != null) {
            return;
        }
        super.setFacingDir(deltaX, deltaY);
    }

    protected void setDownedState(DownedState downedState) {
        if (this.downedState == downedState) {
            return;
        }
        this.downedState = downedState;
        this.updateTeam();
        this.sendMovementPacket(false);
    }

    public boolean isDowned() {
        return this.downedState == DownedState.DOWNED;
    }

    public boolean isTrapped() {
        return this.downedState == DownedState.TRAPPED;
    }

    public void setTrapped() {
        this.setDownedState(DownedState.TRAPPED);
    }

    public void setDowned(int settlementUniqueID, int reservedTime) {
        this.setDownedState(DownedState.DOWNED);
        this.recruitReservedSettlementUniqueID = settlementUniqueID;
        this.recruitReservedTimeout = this.getTime() + (long)reservedTime;
        if (this.currentMission != null) {
            LootTable lootTable = this.currentMission.getLootTable(this);
            if (lootTable != null) {
                ArrayList<InventoryItem> drops = lootTable.getNewList(GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this);
                Point dropPos = this.getLootDropsPosition(null);
                dropPos.x = this.getLevel().limitLevelXToBounds(dropPos.x);
                dropPos.y = this.getLevel().limitLevelYToBounds(dropPos.y);
                for (InventoryItem item : drops) {
                    ItemPickupEntity entity = item.getPickupEntity(this.getLevel(), dropPos.x, dropPos.y);
                    this.getLevel().entityManager.pickups.add(entity);
                }
            }
            this.currentMission = null;
        }
        this.setMovement(null);
    }

    public void clearDownedState() {
        this.setDownedState(null);
    }

    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(null);
        drawOptions.chestplate(ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor()));
        drawOptions.boots(ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor()));
    }

    public void setDisplayArmor(HumanDrawOptions drawOptions) {
        InventoryItem boots;
        InventoryItem chestplate;
        InventoryItem helmet = this.getDisplayArmor(0, null);
        if (helmet != null && Settings.showSettlerHeadArmor) {
            drawOptions.helmet(helmet);
        }
        if ((chestplate = this.getDisplayArmor(1, null)) != null) {
            drawOptions.chestplate(chestplate);
        }
        if ((boots = this.getDisplayArmor(2, null)) != null) {
            drawOptions.boots(boots);
        }
        if (this.downedState == DownedState.TRAPPED) {
            drawOptions.addTopDraw((player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> MobRegistry.Textures.human_enchained_iron.initDraw().sprite(spriteX, spriteY, spriteRes).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
        }
    }

    public void modifyExpressionDrawOptions(HumanDrawOptions options) {
        long time;
        if (this.activeExpression != null && (time = this.activeExpressionEndTime - this.getLocalTime()) >= 0L) {
            float progress = Math.min(1.0f, 1.0f - (float)time / (float)this.activeExpression.animationTimeMillis);
            this.activeExpression.drawOptionsModifier.accept(Float.valueOf(progress), options);
        }
    }

    @Override
    public boolean shouldDrawOnMap() {
        if (!this.isVisible()) {
            return false;
        }
        return this.isSettler() || this.isVisitor() || this.isDowned();
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        HumanDrawOptions humanOptions = new HumanDrawOptions(this.getLevel(), this.look, !this.customLook);
        this.setDefaultArmor(humanOptions);
        this.setDisplayArmor(humanOptions);
        this.modifyExpressionDrawOptions(humanOptions);
        if (this.isDowned()) {
            humanOptions.blinking(true);
            humanOptions.rotate(90.0f, 16, 14);
        }
        Settler.getHumanFaceDrawOptions(humanOptions, 16, x - 8, y - 8).draw();
        QuestMarkerOptions marker = this.getMarkerOptions(client.getPlayer());
        if (marker != null && Settings.showQuestMarkers) {
            QuestGiver.getMarkerDrawOptions(marker.icons, 20, marker.color, x, y - 5, 1.0f, -1, 0).draw();
        }
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-8, -8, 16, 16);
    }

    @Override
    public GameTooltips getMapTooltips() {
        GameMessage currentActivity;
        StringTooltips tooltips = new StringTooltips(this.getDisplayName());
        if (this.isSettler() && (currentActivity = this.getCurrentActivity()) != null) {
            if (this.hasCommandOrders()) {
                tooltips.add(currentActivity.translate(), GameColor.RED, 300);
            } else {
                tooltips.add(currentActivity.translate(), GameColor.LIGHT_GRAY, 300);
            }
        }
        if (this.isDowned()) {
            tooltips.add(Localization.translate("activities", "downed"), GameColor.YELLOW);
        }
        return tooltips;
    }

    @Override
    public boolean shouldSave() {
        return this.isDowned() || !this.isMovingOut() && super.shouldSave();
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        GameMessage currentActivity;
        super.addHoverTooltips(tooltips, debug);
        if (this.isSettler() && (currentActivity = this.getCurrentActivity()) != null) {
            if (this.hasCommandOrders()) {
                tooltips.add(new StringTooltips(currentActivity.translate(), GameColor.RED, 300));
            } else {
                tooltips.add(new StringTooltips(currentActivity.translate(), GameColor.LIGHT_GRAY, 300));
            }
        }
        if (this.isDowned()) {
            tooltips.add(new StringTooltips(Localization.translate("activities", "downed"), GameColor.YELLOW));
        }
    }

    @Override
    protected void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("Home: " + (this.home == null ? null : this.home.x + ", " + this.home.y));
        if (this.isVisitor()) {
            tooltips.add("Leave: " + GameUtils.formatSeconds(this.travelOutTime / 1000L));
        }
        if (this.currentMission != null) {
            tooltips.add("currentMission: " + this.currentMission.getStringID());
            this.currentMission.addDebugTooltips(tooltips);
        }
        tooltips.add("completedMission: " + this.completedMission);
        tooltips.add("missionFailed: " + this.missionFailed);
        if (this.isSettler()) {
            tooltips.add("tier: " + this.settlerQuestTier);
            tooltips.add("happiness: " + this.getSettlerHappiness() + "%");
            tooltips.add("hunger: " + (int)(this.getHungerLevel() * 100.0f) + "%");
            if (this.lastFoodEaten != null) {
                tooltips.add("lastFoodEaten: " + this.lastFoodEaten.getStringID() + " (" + this.lastFoodEaten.quality.displayName.translate() + ")");
            }
        }
        if (!this.isClient()) {
            tooltips.add("workBuffer: " + this.workBreakBuffer);
        }
        tooltips.add("onWorkBreak: " + this.onWorkBreak);
        if (!this.isClient()) {
            int differentFoodsEaten = (int)this.recentFoodItemIDsEaten.stream().distinct().count();
            tooltips.add("recentFood: (" + differentFoodsEaten + ") " + Arrays.toString(this.recentFoodItemIDsEaten.stream().map(ItemRegistry::getItemStringID).toArray()));
        }
    }

    public GameMessage getRandomAngryMessage() {
        ArrayList<GameMessage> messages = this.getAngryMessages();
        return GameRandom.globalRandom.getOneOf(messages);
    }

    protected ArrayList<GameMessage> getAngryMessages() {
        ArrayList<GameMessage> messages = this.getLocalMessages("humanangry", 6);
        if (this.defendsHimselfAgainstPlayers()) {
            messages.addAll(this.getLocalMessages("humanangryactive", 3));
        }
        return messages;
    }

    protected boolean defendsHimselfAgainstPlayers() {
        return this.downedState == null;
    }

    public GameMessage getRandomAttackMessage() {
        ArrayList<GameMessage> messages = this.getAttackMessages();
        return GameRandom.globalRandom.getOneOf(messages);
    }

    protected ArrayList<GameMessage> getAttackMessages() {
        if (!this.defendsHimselfAgainstPlayers()) {
            return this.getAngryMessages();
        }
        return this.getLocalMessages("humanattack", 6);
    }

    public GameMessage getRandomMessage(GameRandom random, ServerClient client) {
        ArrayList<GameMessage> messages = this.getMessages(client);
        return random.getOneOf(messages);
    }

    protected GameMessage getTrappedMessage(ServerClient client) {
        return new LocalMessage("ui", "enchainedhelpme");
    }

    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("humantalk", 5);
    }

    protected final ArrayList<GameMessage> getLocalMessages(String keyPrefix, int totalKeys) {
        return this.getLocalMessages("mobmsg", keyPrefix, totalKeys);
    }

    protected final ArrayList<GameMessage> getLocalMessages(String category, String keyPrefix, int totalKeys) {
        ArrayList<GameMessage> out = new ArrayList<GameMessage>();
        for (int i = 0; i < totalKeys; ++i) {
            out.add(new LocalMessage(category, keyPrefix + (i + 1)));
        }
        return out;
    }

    protected HumanMob getRandomHuman(String type) {
        return this.getRandomHuman(type, 1600);
    }

    protected HumanMob getRandomHuman(String type, int range) {
        Predicate<HumanMob> filter;
        if (this.isSettler()) {
            if (!this.isSettlerWithinSettlement()) {
                return null;
            }
            filter = m -> m.isSettler();
        } else {
            filter = m -> !m.isSettler();
        }
        HumanMob[] mobs = (HumanMob[])this.getLevel().entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(this.getX(), this.getY(), range), 0).filter(m -> m.getStringID().contains(type) && m instanceof HumanMob).map(m -> (HumanMob)m).filter(filter).toArray(HumanMob[]::new);
        return GameRandom.globalRandom.getOneOf(mobs);
    }

    protected String getRandomName(GameRandom random) {
        switch (this.gender) {
            case MALE: {
                return HumanMob.getRandomName(random, maleNames);
            }
            case FEMALE: {
                return HumanMob.getRandomName(random, femaleNames);
            }
            case NEUTRAL: {
                return HumanMob.getRandomName(random, neutralNames);
            }
        }
        return "NULL";
    }

    public static String getRandomName(GameRandom random, HumanGender gender) {
        switch (gender) {
            case MALE: {
                return maleNames[random.nextInt(maleNames.length)];
            }
            case FEMALE: {
                return femaleNames[random.nextInt(femaleNames.length)];
            }
        }
        return neutralNames[random.nextInt(neutralNames.length)];
    }

    public static String getRandomName(GameRandom random, String[] names) {
        return names[random.nextInt(names.length)];
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset, Mob oldMob) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, tileOffset, positionOffset, oldMob);
        if (oldMob instanceof HumanMob) {
            HumanMob oldHuman = (HumanMob)oldMob;
            if (oldHuman.earlyAccessSettlementIsland != null) {
                this.settlementUniqueID = migrationData.getOldSettlementAtLevelUniqueID(new LevelIdentifier(oldHuman.earlyAccessSettlementIsland.x, oldHuman.earlyAccessSettlementIsland.y, 0));
            }
            if (oldHuman.earlyAccessVisitorIsland != null) {
                this.visitorSettlementUniqueID = migrationData.getOldSettlementAtLevelUniqueID(new LevelIdentifier(oldHuman.earlyAccessVisitorIsland.x, oldHuman.earlyAccessVisitorIsland.y, 0));
                this.travelOutTime = oldHuman.travelOutTime;
            }
            if (oldHuman.earlyAccessRecruitReservedIsland != null) {
                this.recruitReservedSettlementUniqueID = migrationData.getOldSettlementAtLevelUniqueID(new LevelIdentifier(oldHuman.earlyAccessRecruitReservedIsland.x, oldHuman.earlyAccessRecruitReservedIsland.y, 0));
                this.recruitReservedTimeout = oldHuman.recruitReservedTimeout;
            }
        }
        if (this.home != null) {
            this.home.x += tileOffset.x;
            this.home.y += tileOffset.y;
        }
        if (this.isSettler() && !this.isSettlerWithinSettlementLoadedRegions()) {
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(this.getServer());
            settlersData.refreshWorldSettler(this, false);
        }
    }

    static {
        defaultJobPathIterations = humanPathIterations = 25000;
        secondsToPassAtFullHunger = 1200;
        adventurePartyHungerUsageMod = 2.0f;
        maxWorkBreakBufferAtFullHappiness = 180000;
        maxWorkBreakBufferAtNoHappiness = 60000;
        resetWorkBreakWhenBufferAt = 60000;
        workBreakBufferUsageModAtFullHappiness = 0.5f;
        workBreakBufferUsageModAtNoHappiness = 3.0f;
        workBreakBufferRegenModAtFullHappiness = 1.0f;
        workBreakBufferRegenModAtNoHappiness = 0.2f;
        elderNames = new String[]{"Diligo", "Haydex", "Kamikaze", "Subject", "Vireyar", "Rainfury", "Tsukuss", "Fair", "Stormes", "Jonas", "Rune", "Bo", "Jonathan", "Oliver", "Christian"};
        maleNames = new String[]{"Aaron", "Adam", "Alastair", "Albert", "Alexander", "Alfred", "Alton", "Andrew", "Archie", "Arthur", "Basil", "Ben", "Benny", "Billy", "Blake", "Brandon", "Brayden", "Brent", "Bruce", "Cain", "Calvin", "Carl", "Chad", "Charles", "Chris", "Cody", "Dale", "Darren", "Dave", "David", "Dean", "Dennis", "Devon", "Dexter", "Dustin", "Elvis", "Eric", "Francis", "Garrett", "Gavin", "Glen", "Harrison", "Henry", "Issac", "Jake", "James", "Jared", "Jason", "Jayden", "Jeff", "Jordan", "Josh", "Justin", "Kaine", "Karl", "Ken", "Kent", "King", "Kyle", "Larry", "Lloyd", "Marc", "Marco", "Mason", "Matthew", "Michael", "Mick", "Nick", "Oliver", "Olly", "Patrick", "Paul", "Peter", "Phil", "Philip", "Ricardo", "Ricky", "Rob", "Roger", "Samuel", "Scott", "Sean", "Seth", "Shane", "Shawn", "Sheldon", "Stan", "Steve", "Tim", "Tobias", "Tom", "Tony", "Troy", "Warren", "Will", "Zach"};
        femaleNames = new String[]{"Abigail", "Abitha", "Alexis", "Alicia", "Amity", "Angel", "Anne", "Aphra", "Aurinda", "Azuba", "Betsy", "Bonny", "Camille", "Candace", "Carmen", "Carolyn", "Christine", "Cess", "Charity", "Charlotte", "Christine", "Claire", "Clara", "Connie", "Cornelia", "Cristi", "Denise", "Diana", "Diane", "Edith", "Eleanor", "Electra", "Elizabeth", "Emeline", "Emma", "Esther", "Faith", "Fidelity", "Frances", "Francine", "Georgine", "Gloria", "Grace", "Harriet", "Hazel", "Helen", "Henrietta", "Hepzibah", "Hester", "Isabella", "Jane", "Jess", "Joy", "Joyce", "Judith", "Kathrine", "Kaz", "Kristine", "Laura", "Leona", "Lisha", "Lucy", "Lydia", "Maggie", "Martha", "Martina", "Mary", "Megan", "Melissa", "Mellie", "Mercy", "Natalie", "Ness", "Nina", "Penny", "Petra", "Phila", "Phoebe", "Rebekah", "Rosanna", "Sara", "Shirley", "Sophia", "Sue", "Susanna", "Tabitha", "Tamara", "Tess", "Tiara", "Violet", "Virginia", "Winnie"};
        neutralNames = new String[]{"Alex", "Ash", "Bobby", "Casey", "Darcy", "Dylan", "Frankie", "George", "Glenn", "Jesse", "Leigh", "Lindsay", "Robin", "Ryan", "Sam"};
        lastNames = new String[]{"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson", "White", "Lopez", "Lee", "Gonzalez", "Harris", "Clark", "Lewis", "Robinson", "Walker", "Perez", "Hall", "Young", "Allen", "Sanchez", "Wright", "King", "Scott", "Green", "Baker", "Adams", "Nelson", "Hill", "Ramirez", "Campbell", "Mitchell", "Roberts", "Carter", "Phillips", "Evans", "Turner", "Torres", "Parker", "Collins", "Edwards", "Stewart", "Flores", "Morris", "Nguyen", "Murphy", "Rivera", "Cook", "Rogers", "Morgan", "Peterson", "Cooper", "Reed", "Bailey", "Bell", "Gomez", "Kelly", "Howard", "Ward", "Cox", "Diaz", "Richardson", "Wood", "Watson", "Brooks", "Bennett", "Gray", "James", "Reyes", "Cruz", "Hughes", "Price", "Myers", "Long", "Foster", "Sanders", "Ross", "Morales", "Powell", "Sullivan", "Russell", "Ortiz", "Jenkins", "Gutierrez", "Perry", "Butler", "Barnes", "Fisher"};
    }

    public static enum DownedState {
        DOWNED,
        TRAPPED;

    }

    public class ShowSimpleThoughtMobAbility
    extends MobAbility {
        public Function<Integer, ThoughtBubble> thoughtBubbleConstructor;

        public ShowSimpleThoughtMobAbility(Function<Integer, ThoughtBubble> thoughtBubbleConstructor) {
            this.thoughtBubbleConstructor = thoughtBubbleConstructor;
        }

        public void runAndSend(int animTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(animTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int animTime = reader.getNextInt();
            ThoughtBubble thoughtBubble = this.thoughtBubbleConstructor.apply(animTime);
            HumanMob.this.getLevel().hudManager.addElement(thoughtBubble);
        }
    }

    public class ShowOtherSettlerThoughtMobAbility
    extends MobAbility {
        public void runAndSend(Mob target, int animTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(target.getUniqueID());
            writer.putNextInt(animTime);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int uniqueID = reader.getNextInt();
            Mob target = GameUtils.getLevelMob(uniqueID, this.getMob().getLevel());
            int animTime = reader.getNextInt();
            if (target instanceof HumanMob) {
                HumanMob.this.getLevel().hudManager.addElement(new OtherSettlerThoughtBubble(HumanMob.this, animTime, (HumanMob)target));
            }
        }
    }

    public class ShowItemThoughtMobAbility
    extends MobAbility {
        public void runAndSend(Item item, int animTime) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextInt(item.getID());
            writer.putNextInt(animTime);
            this.runAndSendAbility(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int itemID = reader.getNextInt();
            int animTime = reader.getNextInt();
            HumanMob.this.getLevel().hudManager.addElement(new ItemThoughtBubble(HumanMob.this, animTime, ItemRegistry.getItem(itemID)));
        }
    }

    public class ShowMobThoughtMobAbility
    extends MobAbility {
        public void runAndSend(String mobStringID, int animTime) {
            this.runAndSend(MobRegistry.getMobID(mobStringID), animTime);
        }

        public void runAndSend(int mobID, int animTime) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextInt(mobID);
            writer.putNextInt(animTime);
            this.runAndSendAbility(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int mobID = reader.getNextInt();
            int animTime = reader.getNextInt();
            HumanMob.this.getLevel().hudManager.addElement(new MobThoughtBubble(HumanMob.this, animTime, mobID));
        }
    }

    public class StartExpressionMobAbility
    extends MobAbility {
        public void runAndSend(FormExpressionWheel.Expression expression, int animTime) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextEnum(expression);
            writer.putNextInt(animTime);
            this.runAndSendAbility(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            HumanMob.this.activeExpression = reader.getNextEnum(FormExpressionWheel.Expression.class);
            HumanMob.this.activeExpressionEndTime = HumanMob.this.getLocalTime() + (long)reader.getNextInt();
        }
    }
}

