/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.events.loot.MobLootTableDropsEvent;
import necesse.engine.events.loot.MobPrivateLootTableDropsEvent;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.listeners.MobKilledJournalChallengeListener;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDeath;
import necesse.engine.network.packet.PacketHitMob;
import necesse.engine.network.packet.PacketMobFollowUpdate;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketMobMana;
import necesse.engine.network.packet.PacketMobMovement;
import necesse.engine.network.packet.PacketMobPathBreakDownHit;
import necesse.engine.network.packet.PacketMobResilience;
import necesse.engine.network.packet.PacketMobUseLife;
import necesse.engine.network.packet.PacketMobUseMana;
import necesse.engine.network.packet.PacketPlayerCollisionHit;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.manager.MobHealthChangeListenerEntityComponent;
import necesse.entity.manager.MobLootTableDropsListenerEntityComponent;
import necesse.entity.manager.MobManaChangeListenerEntityComponent;
import necesse.entity.manager.MobPrivateLootTableDropsListenerEntityComponent;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobDifficultyChanges;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthChangedEvent;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.MobManaChangedEvent;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.MobWasKilledEvent;
import necesse.entity.mobs.OpenedDoor;
import necesse.entity.mobs.OpenedDoors;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ability.MobAbilityRegistry;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.EmptyAINode;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.networkField.MobNetworkFieldRegistry;
import necesse.entity.mobs.networkField.NetworkField;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.ProgressBarDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.MobSpawnItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.hudManager.floatText.DamageText;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.PathBreakDownLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.regionSystem.Region;

public class Mob
extends Entity
implements Attacker {
    public static MobSpawnArea MOB_SPAWN_AREA = new MobSpawnArea(700, 1400);
    public ModifierValue<Integer> spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0);
    public static MobSpawnArea CRITTER_SPAWN_AREA = new MobSpawnArea(800, 1400);
    public static Attacker TOO_BUFFED_ATTACKER = new Attacker(){

        @Override
        public GameMessage getAttackerName() {
            return new LocalMessage("deaths", "unknownatt");
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return DeathMessageTable.oneOf("toobuffed");
        }

        @Override
        public Mob getFirstAttackOwner() {
            return null;
        }
    };
    public final IDData idData = new IDData();
    public final MobDifficultyChanges difficultyChanges = new MobDifficultyChanges(this);
    public BehaviourTreeAI<?> ai = new BehaviourTreeAI<Mob>(this, new EmptyAINode());
    public boolean countStats = false;
    private int dir;
    private int armor;
    private int health;
    private int maxHealth;
    protected int loadedHealth;
    private float mana;
    protected float loadedMana;
    private int maxMana;
    private float resilience;
    protected float loadedResilience;
    private int maxResilience;
    protected float resilienceDecay;
    protected float resilienceDecayDelay;
    public boolean isManaExhausted;
    public boolean isUsingItemsForArmorAndHealth;
    public boolean isUsingItemsForDamage;
    public float blinkCooldown = 0.0f;
    public float timeUntilNextBlink = 0.0f;
    public float blinkDuration = 100.0f;
    protected boolean shouldPlayAmbience = true;
    public long ambientSoundCooldown;
    public long ambientSoundLastTriggerTime;
    public int ambientSoundCooldownMin = 12000;
    public int ambientSoundCooldownMax = 28000;
    private float friction;
    private float speed;
    private float swimSpeed;
    private float regen;
    private float combatRegen;
    private float manaRegen;
    private float combatManaRegen;
    public float accelerationMod;
    public float decelerationMod;
    private float knockbackModifier;
    private double regenBuffer;
    public float dx;
    public float dy;
    public float colDx;
    public float colDy;
    protected float nX;
    protected float nY;
    protected boolean isSmoothSnapped;
    public boolean staySmoothSnapped;
    protected int hitCooldown;
    protected int attackCooldown;
    public long hitTime;
    public long attackTime;
    public long lastCombatTime;
    public long combatStartTime = -1L;
    public long lastManaSpentTime;
    public long removedTime;
    public boolean isAttacking;
    public boolean onCooldown;
    public boolean isHostile;
    public boolean isCritter;
    protected int cornerSkip = 12;
    protected boolean isStatic;
    protected Rectangle collision;
    protected Rectangle hitBox;
    protected Rectangle selectBox;
    public boolean prioritizeVerticalDir = true;
    protected int swimMaskMove;
    protected int swimMaskOffset;
    protected int swimSinkOffset;
    protected MobMovement currentMovement;
    protected boolean hasArrivedAtTarget = true;
    protected boolean stopMoveWhenArrive = false;
    public int moveAccuracy = 5;
    public float moveX;
    public float moveY;
    protected float movePosTolerance = 0.0f;
    protected boolean sendNextMovementPacket;
    protected boolean nextMovementPacketDirect;
    protected PointHashSet nextMovementPacketRegionPositions = new PointHashSet();
    protected boolean sendNextHealthPacket;
    protected boolean nextHealthPacketFull;
    protected boolean sendNextResiliencePacket;
    protected boolean nextResiliencePacketFull;
    protected boolean sendNextManaPacket;
    protected boolean nextManaPacketFull;
    protected int movementUpdateCooldown = 5000;
    protected int healthUpdateCooldown = 5000;
    protected int resilienceUpdateCooldown = 5000;
    protected int manaUpdateCooldown = 5000;
    protected long movementUpdateTime;
    protected long healthUpdateTime;
    protected long resilienceUpdateTime;
    protected long manaUpdateTime;
    protected long resilienceGainedTime;
    public int mount = -1;
    public int rider = -1;
    public boolean mountSetMounterPos;
    protected boolean overrideMountedWaterWalking;
    public String secondType;
    private int team = -1;
    public BuffManager buffManager;
    private final HashMap<String, Long> genericCooldowns;
    private final HashSet<Attacker> attackers;
    protected boolean isDamagedByPlayers;
    public LinkedList<Runnable> runOnNextServerTick = new LinkedList();
    public LinkedList<Runnable> runOnNextClientTick = new LinkedList();
    public ArrayList<ItemPickupEntity> itemsDropped = new ArrayList();
    public boolean canDespawn;
    protected Point spawnTilePosition = null;
    public boolean shouldSave = true;
    public boolean isSummoned = false;
    public boolean dropsLoot = true;
    public boolean isSecondaryIncursionBoss;
    public final boolean isItemAttacker;
    public final boolean isPlayer;
    public final boolean isHuman;
    protected boolean hasDied;
    protected int followingUniqueID = -1;
    private Mob foundFollowingMob;
    protected float currentSpeed;
    protected double distanceRan;
    protected double distanceRidden;
    protected int moveSent;
    protected int healthSent;
    protected int resilienceSent;
    protected int manaSent;
    protected OpenedDoors openedDoors = new OpenedDoors(this);
    protected long lastPathBreakTime;
    public int pathBreakCooldown = 1000;
    public MobHitCooldowns collisionHitCooldowns = new MobHitCooldowns();
    private final MobNetworkFieldRegistry networkFields = new MobNetworkFieldRegistry(this);
    private final MobAbilityRegistry abilities = new MobAbilityRegistry(this);

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Mob(int health) {
        if (this instanceof PlayerMob) {
            this.isPlayer = true;
            this.idData.setData(-1, "player");
        } else {
            this.isPlayer = false;
            MobRegistry.instance.applyIDData(this.getClass(), this.idData);
        }
        this.isItemAttacker = this instanceof ItemAttackerMob;
        this.isHuman = this instanceof HumanMob;
        this.buffManager = new BuffManager(this);
        this.attackers = new HashSet();
        this.isDamagedByPlayers = false;
        this.genericCooldowns = new HashMap();
        this.secondType = this.getStringID();
        this.setMaxHealth(health);
        this.setHealthHidden(health);
        this.setMaxResilience(0);
        this.setMaxMana(100);
        this.setManaHidden(100.0f);
        this.collision = new Rectangle();
        this.hitBox = new Rectangle();
        this.selectBox = new Rectangle();
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
        this.hitCooldown = 0;
        this.attackCooldown = 1000;
        this.setRegen(1.0f);
        this.setResilienceDecay(1.0f);
        this.resilienceDecayDelay = 4000.0f;
        this.setManaRegen(0.0f);
        this.setCombatRegen(0.0f);
        this.setCombatManaRegen(0.4f);
        this.accelerationMod = 0.5f;
        this.decelerationMod = 1.0f;
        this.setKnockbackModifier(1.0f);
        this.setPos(0.0f, 0.0f, true);
        this.dir = GameRandom.globalRandom.nextInt(4);
        this.setFriction(0.3f);
        this.setSwimSpeed(0.5f);
    }

    public void onConstructed(Level level) {
        if (level != null) {
            this.setLevel(level);
        }
        this.setHealthHidden(this.getMaxHealth());
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.getUniqueID());
        save.addInt("dir", this.getDir());
        save.addFloat("x", this.x);
        save.addFloat("y", this.y);
        save.addFloat("dx", this.dx);
        save.addFloat("dy", this.dy);
        save.addInt("maxhealth", this.maxHealth);
        save.addInt("health", this.getHealth());
        save.addInt("maxresilience", this.maxResilience);
        save.addFloat("resilience", this.resilience);
        save.addInt("maxmana", this.maxMana);
        save.addFloat("mana", this.getMana());
        save.addBoolean("canDespawn", this.canDespawn);
        if (this.spawnTilePosition != null) {
            save.addPoint("spawnTilePosition", this.spawnTilePosition);
        }
        if (this.isSecondaryIncursionBoss) {
            save.addBoolean("isSecondaryIncursionBoss", this.isSecondaryIncursionBoss);
        }
        save.addLong("lastCombatTime", this.lastCombatTime);
        if (this.usesMana()) {
            save.addLong("lastManaSpentTime", this.lastManaSpentTime);
            save.addBoolean("isManaExhausted", this.isManaExhausted);
        }
        if (!this.openedDoors.isEmpty()) {
            SaveData openedDoorsData = new SaveData("openedDoors");
            for (OpenedDoor door : this.openedDoors) {
                openedDoorsData.addPoint("", new Point(door.tileX, door.tileY));
            }
            save.addSaveData(openedDoorsData);
        }
        SaveData buffs = new SaveData("BUFFS");
        this.buffManager.addSaveData(buffs);
        save.addSaveData(buffs);
    }

    public void applyLoadData(LoadData save) {
        LoadData buffs;
        this.setUniqueID(save.getInt("uniqueID", this.getRealUniqueID()));
        this.setDir(save.getInt("dir"));
        this.x = save.getFloat("x");
        this.y = save.getFloat("y");
        this.nX = this.x;
        this.nY = this.y;
        this.isSmoothSnapped = true;
        this.dx = save.getFloat("dx");
        this.dy = save.getFloat("dy");
        this.setMaxHealth(save.getInt("maxhealth", this.getMaxHealthFlat()));
        this.loadedHealth = save.getInt("health", this.getMaxHealthFlat());
        this.setHealthHidden(this.loadedHealth);
        this.setMaxResilience(save.getInt("maxresilience", this.getMaxResilienceFlat(), false));
        this.loadedResilience = save.getFloat("resilience", 0.0f, false);
        this.setResilienceHidden(this.loadedResilience);
        if (this.usesMana()) {
            this.setMaxMana(save.getInt("maxmana", this.getMaxManaFlat()));
            this.loadedMana = save.getFloat("mana", this.getMaxManaFlat());
            this.setManaHidden(this.loadedMana);
        }
        this.canDespawn = Boolean.parseBoolean(save.getFirstDataByName("canDespawn"));
        this.spawnTilePosition = save.getPoint("spawnTilePosition", null, false);
        this.isSecondaryIncursionBoss = save.getBoolean("isSecondaryIncursionBoss", this.isSecondaryIncursionBoss, false);
        this.lastCombatTime = save.getLong("lastCombatTime", this.lastCombatTime, false);
        this.lastManaSpentTime = save.getLong("lastManaSpentTime", this.lastManaSpentTime, false);
        this.isManaExhausted = save.getBoolean("isManaExhausted", this.isManaExhausted, false);
        LoadData openedDoorsData = save.getFirstLoadDataByName("openedDoors");
        if (openedDoorsData != null) {
            for (LoadData openedDoorData : openedDoorsData.getLoadData()) {
                if (!openedDoorData.isData()) continue;
                try {
                    Point point = LoadData.getPoint(openedDoorData);
                    this.openedDoors.add(point.x, point.y, this.getX(), this.getY(), !this.getLevel().getObject((int)point.x, (int)point.y).isSwitched);
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load mob opened door: " + openedDoorData.getData());
                }
            }
        }
        if ((buffs = save.getFirstLoadDataByName("BUFFS")) != null) {
            this.buffManager.applyLoadData(buffs);
        }
    }

    public boolean shouldSendSpawnPacket() {
        return true;
    }

    public Mob getSpawnPacketMaster() {
        return null;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.getUniqueID());
        this.setupHealthPacket(writer, true);
        this.sendNextHealthPacket = false;
        this.nextHealthPacketFull = false;
        this.setupResiliencePacket(writer, true);
        this.sendNextResiliencePacket = false;
        this.nextResiliencePacketFull = false;
        this.setupMovementPacket(writer);
        this.sendNextMovementPacket = false;
        this.nextMovementPacketDirect = false;
        if (this.usesMana()) {
            writer.putNextBoolean(true);
            this.setupManaPacket(writer, true);
        } else {
            writer.putNextBoolean(false);
        }
        this.sendNextManaPacket = false;
        this.nextManaPacketFull = false;
        if (this.followingUniqueID != -1) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.followingUniqueID);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextBoolean(this.mountSetMounterPos);
        writer.putNextLong(this.lastCombatTime);
        if (this.usesMana()) {
            writer.putNextBoolean(true);
            writer.putNextLong(this.lastManaSpentTime);
            writer.putNextBoolean(this.isManaExhausted);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextBoolean(this.isSecondaryIncursionBoss);
        this.networkFields.writeSpawnPacket(writer);
        this.buffManager.setupContentPacket(writer);
    }

    public void applySpawnPacket(PacketReader reader) {
        this.refreshClientUpdateTime();
        this.setUniqueID(reader.getNextInt());
        this.applyHealthPacket(reader, true);
        if (this.getHealth() != this.loadedHealth) {
            this.setHealthHidden(this.loadedHealth);
        }
        this.applyResiliencePacket(reader, true);
        if (this.getResilience() != this.loadedResilience) {
            this.setResilienceHidden(this.loadedResilience);
        }
        this.applyMovementPacket(reader, true);
        if (reader.getNextBoolean()) {
            this.applyManaPacket(reader, true);
            if (this.getMana() != this.loadedMana) {
                this.setManaHidden(this.loadedMana);
            }
        }
        this.followingUniqueID = reader.getNextBoolean() ? reader.getNextInt() : -1;
        this.mountSetMounterPos = reader.getNextBoolean();
        this.lastCombatTime = reader.getNextLong();
        if (reader.getNextBoolean()) {
            this.lastManaSpentTime = reader.getNextLong();
            this.isManaExhausted = reader.getNextBoolean();
        }
        this.isSecondaryIncursionBoss = reader.getNextBoolean();
        this.networkFields.readSpawnPacket(reader);
        this.buffManager.applyContentPacket(reader);
    }

    public void setupMovementPacket(PacketWriter writer) {
        writer.putNextMaxValue(this.getDir(), 3);
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextFloat(this.dx);
        writer.putNextFloat(this.dy);
        writer.putNextBoolean(this.rider != -1);
        if (this.rider != -1) {
            writer.putNextInt(this.rider);
        }
        writer.putNextBoolean(this.mount != -1);
        if (this.mount != -1) {
            writer.putNextInt(this.mount);
        }
        writer.putNextBoolean(this.stopMoveWhenArrive);
        if (this.currentMovement != null) {
            writer.putNextShort((short)this.currentMovement.getID());
            this.currentMovement.setupPacket(this, writer);
        } else {
            writer.putNextShort((short)-1);
        }
    }

    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        boolean hasMount;
        int mount;
        int rider;
        this.refreshClientUpdateTime();
        this.dir = reader.getNextMaxValue(3);
        float x = reader.getNextFloat();
        float y = reader.getNextFloat();
        float dx = reader.getNextFloat();
        float dy = reader.getNextFloat();
        boolean hasRider = reader.getNextBoolean();
        int n = rider = hasRider ? reader.getNextInt() : -1;
        if (this.rider != rider) {
            Mob lastRider = this.getRider();
            if (lastRider != null) {
                lastRider.mount = -1;
            }
            this.rider = rider;
            this.updateRider();
            this.buffManager.updateBuffs();
        }
        int n2 = mount = (hasMount = reader.getNextBoolean()) ? reader.getNextInt() : -1;
        if (this.mount != mount) {
            Mob lastMount = this.getMount();
            if (lastMount != null) {
                lastMount.rider = -1;
            }
            this.mount = mount;
            this.updateMount();
            this.buffManager.updateBuffs();
        }
        this.updatePosFromServer(x, y, dx, dy, isDirect);
        this.stopMoveWhenArrive = reader.getNextBoolean();
        short movementID = reader.getNextShort();
        if (movementID != -1) {
            this.currentMovement = MobMovement.registry.getNewInstance(movementID);
            this.currentMovement.applyPacket(this, reader);
        } else {
            this.currentMovement = null;
        }
    }

    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        if (isFull) {
            writer.putNextInt(this.maxHealth);
        }
        writer.putNextInt(this.getHealth());
    }

    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        if (isFull) {
            this.setMaxHealth(reader.getNextInt());
        }
        this.loadedHealth = reader.getNextInt();
        int delta = Math.abs(this.loadedHealth - this.getHealth());
        if (this.loadedHealth == 0 || delta > 1) {
            this.setHealthHidden(this.loadedHealth, 0.0f, 0.0f, null, true);
        }
    }

    public void setupResiliencePacket(PacketWriter writer, boolean isFull) {
        if (isFull) {
            writer.putNextInt(this.maxResilience);
        }
        writer.putNextFloat(this.getResilience());
        writer.putNextLong(this.resilienceGainedTime);
    }

    public void applyResiliencePacket(PacketReader reader, boolean isFull) {
        if (isFull) {
            this.setMaxResilience(reader.getNextInt());
        }
        this.loadedResilience = reader.getNextFloat();
        float delta = Math.abs(this.loadedResilience - this.getResilience());
        if (this.loadedResilience == 0.0f || this.loadedResilience >= (float)this.getMaxResilience() || delta >= 1.0f) {
            this.setResilienceHidden(this.loadedResilience);
        }
        this.resilienceGainedTime = reader.getNextLong();
    }

    public void setupManaPacket(PacketWriter writer, boolean isFull) {
        if (isFull) {
            writer.putNextInt(this.maxMana);
        }
        writer.putNextFloat(this.getMana());
        writer.putNextLong(this.lastManaSpentTime);
        writer.putNextBoolean(this.isManaExhausted);
    }

    public void applyManaPacket(PacketReader reader, boolean isFull) {
        if (isFull) {
            this.setMaxMana(reader.getNextInt());
        }
        this.loadedMana = reader.getNextFloat();
        float delta = Math.abs(this.loadedMana - this.getMana());
        if (this.loadedMana == 0.0f || delta >= 1.0f) {
            this.setManaHidden(this.loadedMana, true);
        }
        this.lastManaSpentTime = reader.getNextLong();
        this.isManaExhausted = reader.getNextBoolean();
    }

    protected void moveX(float mod) {
        this.x += (this.dx + this.colDx) * mod / 250.0f;
    }

    protected void moveY(float mod) {
        this.y += (this.dy + this.colDy) * mod / 250.0f;
    }

    @Override
    public void init() {
        super.init();
        WorldEntity worldEntity = this.getWorldEntity();
        if (worldEntity != null) {
            this.healthUpdateTime = worldEntity.getTime();
            this.movementUpdateTime = worldEntity.getTime();
            if (this.usesMana()) {
                this.manaUpdateTime = worldEntity.getTime();
            }
        }
        this.buffManager.forceUpdateBuffs();
        this.difficultyChanges.init();
        this.networkFields.closeRegistry();
        this.abilities.closeRegistry();
        this.countStats = true;
        this.updateMount();
        this.updateRider();
        this.refreshClientUpdateTime();
        this.ambientSoundCooldown = GameRandom.globalRandom.getIntBetween(this.ambientSoundCooldownMin, this.ambientSoundCooldownMax);
        SoundSettings ambientSound = this.getAmbientSound();
        if (ambientSound == null) {
            this.shouldPlayAmbience = false;
        }
        this.ambientSoundLastTriggerTime = this.getTime();
    }

    @Override
    public void onLoadingComplete() {
        super.onLoadingComplete();
        this.handleLoadedValues();
    }

    protected void handleLoadedValues() {
        this.buffManager.forceUpdateBuffs();
        if (this.getHealth() < this.loadedHealth) {
            this.setHealthHidden(this.loadedHealth);
            this.loadedHealth = 0;
        }
        if (this.getResilience() < this.loadedResilience) {
            this.setResilienceHidden(this.loadedResilience);
            this.loadedResilience = 0.0f;
        }
        if (this.getMana() < this.loadedMana) {
            this.setManaHidden(this.loadedMana);
            this.loadedMana = 0.0f;
        }
    }

    @Override
    public void onLevelChanged() {
        super.onLevelChanged();
        this.spawnTilePosition = null;
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        this.limitWithinRegionBounds(region);
        this.ai.onUnloading();
    }

    @Override
    public void onRegionChanged(int lastRegionX, int lastRegionY, int newRegionX, int newRegionY) {
        super.onRegionChanged(lastRegionX, lastRegionY, newRegionX, newRegionY);
        if (this.isServer() && this.shouldSendSpawnPacket()) {
            this.sendPacketToNewClientsWithRegion(lastRegionX, lastRegionY, newRegionX, newRegionY, () -> new PacketSpawnMob(this));
        }
    }

    @Override
    public void clientTick() {
        while (!this.runOnNextClientTick.isEmpty()) {
            this.runOnNextClientTick.removeFirst().run();
        }
        this.difficultyChanges.tick();
        if (!this.isPlayer && this.getTimeSinceClientUpdate() >= (long)(this.movementUpdateCooldown + 10000)) {
            this.refreshClientUpdateTime();
            if (this.getRider() == null) {
                this.requestServerUpdate();
            }
        }
        this.buffManager.clientTick();
        this.tickRegen();
        this.tickLevel();
        if (this.buffManager.getModifier(BuffModifiers.EMITS_LIGHT).booleanValue()) {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
        }
        if (this.usesMana() && this.isManaExhausted) {
            this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, this, 1000, null), false);
        }
        if (this.shouldPlayAmbience && this.getTime() - this.ambientSoundCooldown >= this.ambientSoundLastTriggerTime) {
            this.ambientSoundCooldown = GameRandom.globalRandom.getIntBetween(this.ambientSoundCooldownMin, this.ambientSoundCooldownMax);
            this.ambientSoundLastTriggerTime = this.getTime();
            this.playAmbientSound();
        }
    }

    public void requestServerUpdate() {
        if (this.isClient()) {
            GameLog.debug.println("Client requesting update for mob " + this);
            this.getClient().network.sendPacket(new PacketRequestMobData(this.getUniqueID()));
        }
    }

    @Override
    public void serverTick() {
        Mob followingMob;
        while (!this.runOnNextServerTick.isEmpty()) {
            this.runOnNextServerTick.removeFirst().run();
        }
        this.difficultyChanges.tick();
        if (this.isServer()) {
            this.networkFields.tickSync();
        }
        this.buffManager.serverTick();
        this.tickOpenedDoors();
        this.tickRegen();
        this.tickLevel();
        if (Float.isNaN(this.x) || Float.isNaN(this.y)) {
            System.out.println("Mob " + this.getRealUniqueID() + " bugged out and was removed.");
            this.remove();
        }
        if ((followingMob = this.getFollowingMob()) != null && !this.isSamePlace(followingMob)) {
            this.onFollowingAnotherLevel(followingMob);
        }
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "ai", () -> {
            if (!this.isMounted()) {
                this.ai.tick();
            }
        });
        if (this.movementUpdateTime + (long)this.movementUpdateCooldown < this.getTime()) {
            this.sendMovementPacket(false);
        }
        if (this.canTakeDamage() && this.healthUpdateTime + (long)this.healthUpdateCooldown < this.getTime()) {
            this.sendHealthPacket(false);
        }
        if (this.usesMana()) {
            if (this.manaUpdateTime + (long)this.manaUpdateCooldown < this.getTime()) {
                this.sendManaPacket(false);
            }
            if (this.isManaExhausted) {
                this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, this, 1000, null), false);
            }
        }
    }

    public void tickCurrentMovement(float delta) {
        this.moveX = 0.0f;
        this.moveY = 0.0f;
        if (this.isMounted()) {
            Mob mounted = this.getRider();
            if (mounted != null) {
                this.setDir(mounted.getDir());
                this.moveX = mounted.moveX;
                this.moveY = mounted.moveY;
            }
        } else if (this.currentMovement != null) {
            this.hasArrivedAtTarget = this.currentMovement.tick(this);
            if (this.stopMoveWhenArrive && this.hasArrivedAtTarget) {
                this.stopMoving();
            }
        } else {
            this.hasArrivedAtTarget = true;
        }
    }

    public void tickMovement(float delta) {
        Mob mount;
        if (this.removed()) {
            return;
        }
        Point2D.Float oldPos = new Point2D.Float(this.x, this.y);
        if (this.isRiding() && (mount = this.getMount()) != null) {
            if (!this.isPlayer && !this.getWorldSettings().disableMobAI) {
                this.tickCurrentMovement(delta);
            }
            this.setPos(mount.x, mount.y, true);
            if (!this.isAttacking) {
                this.setDir(mount.getDir());
            }
            this.dx = 0.0f;
            this.dy = 0.0f;
            this.buffManager.tickMovement(delta);
            this.tickSendSyncPackets();
            return;
        }
        if (!this.getWorldSettings().disableMobAI || this.isPlayer || this.isMounted()) {
            this.tickCurrentMovement(delta);
            this.calcAcceleration(this.getSpeed(), this.getFriction(), this.moveX, this.moveY, delta);
        } else {
            this.dx = 0.0f;
            this.dy = 0.0f;
            this.moveX = 0.0f;
            this.moveY = 0.0f;
        }
        Mob rider = this.getRider();
        this.colDx = 0.0f;
        this.colDy = 0.0f;
        this.checkCollision();
        this.tickCollisionMovement(delta, rider);
        this.colDx = 0.0f;
        this.colDy = 0.0f;
        Point2D.Float newPos = new Point2D.Float(this.x, this.y);
        float distance = (float)oldPos.distance(newPos);
        this.currentSpeed = distance / (delta / 250.0f);
        this.distanceRan += (double)distance;
        if (rider != null) {
            rider.currentSpeed = this.currentSpeed;
            rider.distanceRidden += (double)distance;
        }
        this.calcNetworkSmooth(delta);
        this.buffManager.tickMovement(delta);
        this.tickSendSyncPackets();
    }

    protected boolean shouldSendMovementPacketWithRider(Mob rider) {
        return false;
    }

    public void tickSendSyncPackets() {
        if (this.isServer()) {
            if (this.sendNextMovementPacket) {
                Mob rider = this.getRider();
                if (rider == null || this.shouldSendMovementPacketWithRider(rider)) {
                    ++this.moveSent;
                    PacketMobMovement packet = new PacketMobMovement(this, this.nextMovementPacketDirect);
                    this.nextMovementPacketDirect = false;
                    if (!this.nextMovementPacketRegionPositions.isEmpty()) {
                        PointSetAbstract<?> myRegionPositions = this.getRegionPositions();
                        if (myRegionPositions.isEmpty()) {
                            this.getServer().network.sendToClientsAtEntireLevel((Packet)packet, this.getLevel());
                        } else {
                            this.nextMovementPacketRegionPositions.addAll(myRegionPositions);
                            this.getServer().network.sendToClientsWithAnyRegion((Packet)packet, this.getLevel(), this.nextMovementPacketRegionPositions);
                        }
                    } else {
                        this.getServer().network.sendToClientsWithEntity(packet, this);
                    }
                }
                this.movementUpdateTime = this.getTime();
                this.sendNextMovementPacket = false;
                this.nextMovementPacketRegionPositions.clear();
            }
            if (this.sendNextHealthPacket) {
                if (this.canTakeDamage()) {
                    ++this.healthSent;
                    this.getServer().network.sendToClientsWithEntity(new PacketMobHealth(this, this.nextHealthPacketFull), this);
                    this.nextHealthPacketFull = false;
                }
                this.healthUpdateTime = this.getTime();
                this.sendNextHealthPacket = false;
            }
            if (this.sendNextResiliencePacket) {
                if (this.getMaxResilience() > 0) {
                    ++this.resilienceSent;
                    this.getServer().network.sendToClientsWithEntity(new PacketMobResilience(this, this.nextResiliencePacketFull), this);
                    this.nextResiliencePacketFull = false;
                }
                this.resilienceUpdateTime = this.getTime();
                this.sendNextResiliencePacket = false;
            }
            if (this.sendNextManaPacket) {
                if (this.usesMana()) {
                    ++this.manaSent;
                    this.getServer().network.sendToClientsWithEntity(new PacketMobMana(this, this.nextManaPacketFull), this);
                    this.nextManaPacketFull = false;
                }
                this.manaUpdateTime = this.getTime();
                this.sendNextManaPacket = false;
            }
        } else {
            this.nextMovementPacketRegionPositions.clear();
        }
    }

    protected void tickCollisionMovement(float delta, Mob rider) {
        Mob doorOpener = rider == null ? this : rider;
        CollisionFilter collisionFilter = this.getLevelCollisionFilter();
        ArrayList<LevelObjectHit> currentCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
        LinkedList finishedMovementActions = new LinkedList();
        Consumer<Runnable> onFinishedMovement = finishedMovementActions::add;
        if (currentCollisions.isEmpty()) {
            ArrayList<LevelObjectHit> cols;
            Rectangle col;
            int i;
            boolean stop;
            boolean collided;
            ArrayList<LevelObjectHit> newCols;
            Rectangle newCol;
            if (this.dx != 0.0f || this.colDx != 0.0f) {
                this.moveX(delta);
                newCol = this.getCollision();
                newCols = this.getLevel().getCollisions(newCol, collisionFilter);
                collided = true;
                stop = true;
                if (!this.checkAndHandleCollisions(newCols, newCol, doorOpener, true, true, onFinishedMovement)) {
                    collided = false;
                }
                if (collided && this.cornerSkip > 0) {
                    for (i = 2; i <= this.cornerSkip; i += 2) {
                        if (this.dy >= 0.0f) {
                            col = this.getCollision(this.getX(), this.getY() + i);
                            cols = this.getLevel().getCollisions(col, collisionFilter);
                            if (cols.isEmpty()) {
                                this.y += Math.abs(this.dx) / 4.0f / 250.0f * delta;
                                stop = false;
                                break;
                            }
                            if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, false, onFinishedMovement)) {
                                this.y += Math.abs(this.dx) / 4.0f / 250.0f * delta;
                                stop = false;
                                break;
                            }
                        }
                        if (!(this.dy <= 0.0f)) continue;
                        col = this.getCollision(this.getX(), this.getY() - i);
                        cols = this.getLevel().getCollisions(col, collisionFilter);
                        if (cols.isEmpty()) {
                            this.y -= Math.abs(this.dx) / 4.0f / 250.0f * delta;
                            stop = false;
                            break;
                        }
                        if (this.checkAndHandleCollisions(cols, col, doorOpener, false, false, onFinishedMovement)) continue;
                        this.y -= Math.abs(this.dx) / 4.0f / 250.0f * delta;
                        stop = false;
                        break;
                    }
                }
                if (collided) {
                    this.moveX(-delta);
                    if (stop) {
                        if (this.buffManager.getModifier(BuffModifiers.BOUNCY).booleanValue()) {
                            this.dx = -this.dx;
                            if (Math.abs(this.dx) >= 10.0f) {
                                this.sendMovementPacket(false);
                            }
                        } else {
                            this.dx = 0.0f;
                        }
                    }
                }
            }
            if (this.dy != 0.0f || this.colDy != 0.0f) {
                this.moveY(delta);
                newCol = this.getCollision();
                newCols = this.getLevel().getCollisions(newCol, collisionFilter);
                collided = true;
                stop = true;
                if (!this.checkAndHandleCollisions(newCols, newCol, doorOpener, true, false, onFinishedMovement)) {
                    collided = false;
                }
                if (collided && this.cornerSkip > 0) {
                    for (i = 2; i <= this.cornerSkip; i += 2) {
                        if (this.dx >= 0.0f) {
                            col = this.getCollision(this.getX() + i, this.getY());
                            cols = this.getLevel().getCollisions(col, collisionFilter);
                            if (cols.isEmpty()) {
                                this.x += Math.abs(this.dy) / 4.0f / 250.0f * delta;
                                stop = false;
                                break;
                            }
                            if (!this.checkAndHandleCollisions(cols, col, doorOpener, false, true, onFinishedMovement)) {
                                this.x += Math.abs(this.dy) / 4.0f / 250.0f * delta;
                                stop = false;
                                break;
                            }
                        }
                        if (!(this.dx <= 0.0f)) continue;
                        col = this.getCollision(this.getX() - i, this.getY());
                        cols = this.getLevel().getCollisions(col, collisionFilter);
                        if (cols.isEmpty()) {
                            this.x -= Math.abs(this.dy) / 4.0f / 250.0f * delta;
                            stop = false;
                            break;
                        }
                        if (this.checkAndHandleCollisions(cols, col, doorOpener, false, true, onFinishedMovement)) continue;
                        this.x -= Math.abs(this.dy) / 4.0f / 250.0f * delta;
                        stop = false;
                        break;
                    }
                }
                if (collided) {
                    this.moveY(-delta);
                    if (stop) {
                        if (this.buffManager.getModifier(BuffModifiers.BOUNCY).booleanValue()) {
                            this.dy = -this.dy;
                            if (Math.abs(this.dy) >= 10.0f) {
                                this.sendMovementPacket(false);
                            }
                        } else {
                            this.dy = 0.0f;
                        }
                    }
                }
            }
        } else {
            ArrayList<LevelObjectHit> newCollisions;
            if (collisionFilter != null) {
                Set currentTiles = currentCollisions.stream().filter(h -> !h.invalidPos()).map(h -> new Point(h.tileX, h.tileY)).collect(Collectors.toSet());
                collisionFilter = collisionFilter.copy().addFilter(tp -> !currentTiles.contains(new Point(tp.tileX, tp.tileY)));
            }
            if (this.dx != 0.0f | this.colDx != 0.0f) {
                this.moveX(delta * 0.5f);
                newCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
                if (!newCollisions.isEmpty()) {
                    this.moveX(-delta * 0.5f);
                    this.dx = this.buffManager.getModifier(BuffModifiers.BOUNCY) != false ? -this.dx : 0.0f;
                }
            }
            if (this.dy != 0.0f | this.colDy != 0.0f) {
                this.moveY(delta * 0.5f);
                newCollisions = this.getLevel().getCollisions(this.getCollision(), collisionFilter);
                if (!newCollisions.isEmpty()) {
                    this.moveY(-delta * 0.5f);
                    this.dy = this.buffManager.getModifier(BuffModifiers.BOUNCY) != false ? -this.dy : 0.0f;
                }
            }
        }
        finishedMovementActions.forEach(Runnable::run);
    }

    public boolean isBlinking() {
        long currentTime = this.getTime();
        if (this.blinkCooldown == 0.0f) {
            this.blinkCooldown = GameRandom.globalRandom.getChance(0.33f) ? 200.0f : (float)GameRandom.globalRandom.getIntBetween(3000, 9000);
            this.timeUntilNextBlink = (float)currentTime + this.blinkCooldown;
        }
        if ((float)currentTime >= this.timeUntilNextBlink) {
            if ((float)currentTime >= this.timeUntilNextBlink + this.blinkDuration) {
                this.blinkCooldown = GameRandom.globalRandom.getChance(0.33f) ? 200.0f : (float)GameRandom.globalRandom.getIntBetween(3000, 9000);
                this.timeUntilNextBlink = (float)currentTime + this.blinkCooldown;
            }
            return true;
        }
        return false;
    }

    protected boolean isTileOnPath(int tileX, int tileY) {
        return this.ai.blackboard.mover.hasDestinationInPath(tileX, tileY);
    }

    protected boolean canBreakDownLastResort(int tileX, int tileY) {
        Mob targetMob = this.ai.blackboard.mover.getTargetMob();
        return targetMob != null && targetMob.getTileX() == tileX && targetMob.getTileY() == tileY && targetMob.canBeTargetedFromAdjacentTiles();
    }

    protected boolean checkAndHandleCollisions(ArrayList<LevelObjectHit> collisions, Rectangle myCollision, Mob doorOpener, boolean doAction, boolean horizontal, Consumer<Runnable> onFinishedMovement) {
        if (collisions.isEmpty()) {
            return false;
        }
        LinkedList<LevelObject> actionDoors = null;
        boolean breakDown = false;
        CollisionFilter collisionFilter = this.getLevelCollisionFilter();
        for (LevelObjectHit hit : collisions) {
            if (hit.tileX == -1 && hit.tileY == -1) {
                return true;
            }
            LevelObject lo2 = hit.getLevelObject();
            PathDoorOption doorOption = doorOpener.getPathDoorOption();
            if (doorOption != null) {
                if (lo2.object.isDoor && doorOption.canPassDoor((DoorObject)lo2.object, lo2.tileX, lo2.tileY)) {
                    if (breakDown || this.openedDoors.hasMobServerOpened(lo2.tileX, lo2.tileY)) continue;
                    if (!lo2.object.pathCollidesIfOpen(lo2.level, lo2.tileX, lo2.tileY, collisionFilter, myCollision)) {
                        if (actionDoors == null) {
                            actionDoors = new LinkedList();
                        }
                        actionDoors.add(lo2);
                        continue;
                    }
                    return true;
                }
                if ((doorOption.canBreakDown(lo2.tileX, lo2.tileY) || this.canBreakDownLastResort(lo2.tileX, lo2.tileY)) && this.isTileOnPath(lo2.tileX, lo2.tileY)) {
                    if (!lo2.object.pathCollidesIfBreakDown(lo2.level, lo2.tileX, lo2.tileY, collisionFilter, myCollision)) {
                        if (!breakDown) {
                            actionDoors = new LinkedList<LevelObject>();
                        }
                        actionDoors.add(lo2);
                        breakDown = true;
                        continue;
                    }
                    return true;
                }
                return true;
            }
            return true;
        }
        if (breakDown) {
            if (doAction) {
                actionDoors.forEach(lo -> doorOpener.pathBreakDown((LevelObject)lo, horizontal, onFinishedMovement));
            }
            return true;
        }
        if (doAction && actionDoors != null) {
            boolean opened = false;
            for (LevelObject door : actionDoors) {
                opened = doorOpener.switchDoor(door) || opened;
            }
            return !opened;
        }
        return false;
    }

    public void setMovement(MobMovement movement) {
        boolean changed = !Objects.equals(this.currentMovement, movement);
        this.currentMovement = movement;
        if (changed || movement == null) {
            boolean bl = this.hasArrivedAtTarget = movement == null;
        }
        if (changed) {
            this.sendMovementPacket(false);
        }
    }

    public void stopMoving() {
        this.setMovement(null);
    }

    public MobMovement getCurrentMovement() {
        return this.currentMovement;
    }

    public boolean hasCurrentMovement() {
        return this.currentMovement != null;
    }

    public boolean hasArrivedAtTarget() {
        return this.hasArrivedAtTarget;
    }

    public void stopMovementOnArrive(boolean val) {
        this.stopMoveWhenArrive = val;
    }

    public void tickOpenedDoors() {
        if (!this.openedDoors.isEmpty()) {
            PathDoorOption pathDoorOption = this.getPathDoorOption();
            if (pathDoorOption == null) {
                this.openedDoors.clear();
                return;
            }
            Iterator<OpenedDoor> iterator = this.openedDoors.iterator();
            while (iterator.hasNext()) {
                OpenedDoor od = iterator.next();
                if (od.isValid(this.getLevel()) && pathDoorOption.canClose(od.tileX, od.tileY)) {
                    Mob collider;
                    Mob mount = this.getMount();
                    Mob mob = collider = mount == null ? this : mount;
                    if (od.switchedDoorCollides(this.getLevel(), collider.getCollision(), collider.getLevelCollisionFilter()) || od.entityCollidesWithSwitchedDoor(this.getLevel()) || od.clientCollidesWithSwitchedDoor(this.getLevel())) continue;
                    this.switchDoor(od);
                    iterator.remove();
                    continue;
                }
                iterator.remove();
            }
        }
    }

    protected static boolean staticSwitchDoor(LevelObject lo, Attacker attacker) {
        lo.object.onPathOpened(lo.level, lo.tileX, lo.tileY, attacker);
        return true;
    }

    protected boolean pathBreakDown(LevelObject lo, boolean horizontal, Consumer<Runnable> onFinishedMovement) {
        if (!this.isClient() && this.lastPathBreakTime < this.getTime() - (long)this.pathBreakCooldown) {
            this.lastPathBreakTime = this.getTime();
            boolean dir = horizontal ? this.dx > 0.0f : this.dy > 0.0f;
            List<Rectangle> collisions = lo.getCollisions(lo.rotation);
            int hitX = lo.tileX * 32 + 16;
            int hitY = lo.tileY * 32 + 16;
            RectangularShape bounds = null;
            for (Rectangle collision : collisions) {
                if (bounds == null) {
                    bounds = collision;
                    continue;
                }
                bounds = ((Rectangle)bounds).union(collision);
            }
            if (bounds != null) {
                if (horizontal) {
                    hitY = (int)bounds.getCenterY();
                    hitX = dir ? ((Rectangle)bounds).x : ((Rectangle)bounds).x + ((Rectangle)bounds).width;
                } else {
                    hitX = (int)bounds.getCenterX();
                    hitY = dir ? ((Rectangle)bounds).y : ((Rectangle)bounds).y + ((Rectangle)bounds).height;
                }
            }
            int damage = this.getPathBreakDownDamage(lo);
            PathBreakDownLevelData data = PathBreakDownLevelData.getPathBreakDownData(this.getLevel());
            int totalDamage = data.getFinalDamage(lo.tileX, lo.tileY, damage);
            boolean destroyed = lo.object.onPathBreakDown(lo.level, lo.tileX, lo.tileY, totalDamage, this, hitX, hitY);
            if (destroyed) {
                data.clear(lo.tileX, lo.tileY);
            }
            if (this.ai != null) {
                this.ai.resetStuck();
            }
            onFinishedMovement.accept(() -> this.onPathBreakDownHit(lo, dir, horizontal));
            if (this.isServer()) {
                this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobPathBreakDownHit(this, lo, dir, horizontal), this);
            }
            return true;
        }
        return false;
    }

    public int getPathBreakDownDamage(LevelObject lo) {
        return lo.object.getPathBreakDamage(lo.level, lo.tileX, lo.tileY);
    }

    public void onPathBreakDownHit(LevelObject lo, boolean dir, boolean horizontal) {
        Mob target;
        Mob mount = this.getMount();
        Mob mob = target = mount == null ? this : mount;
        if (horizontal) {
            target.dx = dir ? -50.0f : 50.0f;
        } else {
            target.dy = dir ? -50.0f : 50.0f;
        }
    }

    protected boolean switchDoor(LevelObject lo) {
        if (!this.isClient() && !this.isPlayer) {
            OpenedDoor last = this.openedDoors.get(lo.tileX, lo.tileY);
            if (last != null) {
                last.mobX = this.getX();
                last.mobY = this.getY();
                return true;
            }
            PathDoorOption pathDoorOption = this.getPathDoorOption();
            if (pathDoorOption != null && pathDoorOption.canOpen(lo.tileX, lo.tileY) && Mob.staticSwitchDoor(lo, this)) {
                this.openedDoors.add(lo.tileX, lo.tileY, this.getX(), this.getY(), lo.object.isSwitched);
                return true;
            }
        }
        return false;
    }

    protected void switchDoor(OpenedDoor od) {
        od.switchDoor(this.getLevel());
    }

    public boolean inLiquid() {
        return this.inLiquid(this.getX(), this.getY());
    }

    public boolean inLiquid(int x, int y) {
        float fullInLiquidAtPercent = this.getFullInLiquidAtPercent(x, y);
        if (fullInLiquidAtPercent <= 0.0f) {
            return this.getLevel() != null && this.getLevel().inLiquid(x, y);
        }
        return this.inLiquidFloat(x, y) >= fullInLiquidAtPercent;
    }

    public float inLiquidFloat() {
        return this.inLiquidFloat(this.getX(), this.getY());
    }

    public float inLiquidFloat(int x, int y) {
        if (this.isRiding() || this.isWaterWalking()) {
            return 0.0f;
        }
        if (this.getLevel() != null) {
            return this.getLevel().liquidManager.getAdvancedMobSinkPercent(this, x, y);
        }
        return 0.0f;
    }

    public float getFullInLiquidAtPercent(int x, int y) {
        return 0.8f;
    }

    public MaskShaderOptions getSwimMaskShaderOptions(float depthPercent) {
        GameTexture swimMask;
        if (depthPercent > 0.0f && (swimMask = this.getSwimMask()) != null) {
            int maskMove = this.getSwimMaskMove();
            int maskOffset = this.getSwimMaskOffset();
            int sinkOffset = this.getSwimSinkOffset();
            int offset = maskOffset + (int)(depthPercent * (float)maskMove);
            int drawYOffset = -maskOffset + offset + (int)(depthPercent * (float)sinkOffset);
            return new MaskShaderOptions(swimMask, 0, drawYOffset, 0, offset);
        }
        return new MaskShaderOptions(0, 0);
    }

    public GameTexture getSwimMask() {
        return MobRegistry.Textures.swimmask;
    }

    public int getSwimMaskMove() {
        return this.swimMaskMove;
    }

    public int getSwimMaskOffset() {
        return this.swimMaskOffset;
    }

    public int getSwimSinkOffset() {
        return this.swimSinkOffset;
    }

    public void tickLevel() {
        this.getLevel().getLevelTile(this.getTileX(), this.getTileY()).tick(this);
        this.getLevel().getLevelObject(this.getTileX(), this.getTileY()).tick(this);
    }

    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        float accModX = this.getDecelerationModifier();
        float accModY = this.getDecelerationModifier();
        if (moveX != 0.0f || moveY != 0.0f) {
            if (moveX != 0.0f) {
                accModX = this.getAccelerationModifier();
            }
            if (moveY != 0.0f) {
                accModY = this.getAccelerationModifier();
            }
            if (!this.isAttacking) {
                this.setFacingDir(moveX, moveY);
            }
        }
        Point2D.Float normalize = GameMath.normalize(moveX, moveY);
        if (friction != 0.0f) {
            this.dx += (speed * friction * normalize.x - friction * this.dx) * delta / 250.0f * accModX;
            this.dy += (speed * friction * normalize.y - friction * this.dy) * delta / 250.0f * accModY;
        } else if (normalize.x != 0.0f || normalize.y != 0.0f) {
            this.dx += (speed * normalize.x - this.dx) * delta / 250.0f * accModX;
            this.dy += (speed * normalize.y - this.dy) * delta / 250.0f * accModY;
        }
        if (moveX == 0.0f && Math.abs(this.dx) < speed * friction / 50.0f) {
            this.dx = 0.0f;
        }
        if (moveY == 0.0f && Math.abs(this.dy) < speed * friction / 50.0f) {
            this.dy = 0.0f;
        }
    }

    public int stoppingDistance(float friction, float currentSpeed) {
        if (currentSpeed == 0.0f) {
            return 0;
        }
        return (int)(currentSpeed / friction);
    }

    public void setFacingDir(float deltaX, float deltaY) {
        float threshold = 0.2f;
        if (this.prioritizeVerticalDir) {
            if (Math.abs(deltaX) - Math.abs(deltaY) <= threshold) {
                this.setDir(deltaY < 0.0f ? 0 : 2);
            } else {
                this.setDir(deltaX < 0.0f ? 3 : 1);
            }
        } else if (Math.abs(deltaY) - Math.abs(deltaX) <= threshold) {
            this.setDir(deltaX < 0.0f ? 3 : 1);
        } else {
            this.setDir(deltaY < 0.0f ? 0 : 2);
        }
    }

    public boolean isSmoothSnapped() {
        return this.isSmoothSnapped;
    }

    protected void calcNetworkSmooth(float delta) {
        if (this.isServer()) {
            this.nX = this.x;
            this.nY = this.y;
            this.isSmoothSnapped = true;
            return;
        }
        if (this.staySmoothSnapped) {
            this.isSmoothSnapped = true;
        }
        if (this.isSmoothSnapped) {
            this.nX = this.x;
            this.nY = this.y;
        } else {
            float deltaX = this.x - this.nX;
            float deltaY = this.y - this.nY;
            Point2D.Float tempPoint = new Point2D.Float(deltaX, deltaY);
            float dist = (float)tempPoint.distance(0.0, 0.0);
            if (dist <= 1.0f) {
                dist = 1.0f;
            }
            float normX = tempPoint.x / dist;
            float normY = tempPoint.y / dist;
            Point2D.Float dir = new Point2D.Float(normX, normY);
            float currentSpeed = Math.max(Math.max(this.getSpeed(), 5.0f), this.getCurrentSpeed());
            float speed = (currentSpeed + dist * 4.0f) * (delta / 250.0f);
            if (speed > dist) {
                this.isSmoothSnapped = true;
                this.nX = this.x;
                this.nY = this.y;
            } else {
                this.nX += dir.x * speed;
                this.nY += dir.y * speed;
            }
        }
    }

    @Override
    public int getDrawX() {
        return this.isSmoothSnapped || this.staySmoothSnapped ? this.getX() : (int)this.nX;
    }

    @Override
    public int getDrawY() {
        return this.isSmoothSnapped || this.staySmoothSnapped ? this.getY() : (int)this.nY;
    }

    @Override
    public final void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Point drawPos = this.getDrawPos();
        this.addDrawablesLoop(list, tileList, topList, level, drawPos.x, drawPos.y, tickManager, camera, perspective, false);
        this.addExtraDrawables(list, tileList, topList, level, drawPos.x, drawPos.y, tickManager, camera, perspective);
        this.addStatusBarDrawable(overlayList, level, drawPos.x, drawPos.y, tickManager, camera, perspective);
        if (GlobalData.debugActive()) {
            topList.add(tm -> {
                Renderer.drawLineRGBA(camera.getDrawX(this.x), camera.getDrawY(this.y), camera.getDrawX(drawPos.x), camera.getDrawY(drawPos.y), this.isSmoothSnapped ? 0.2f : 0.8f, this.isSmoothSnapped ? 0.8f : 0.2f, 0.2f, 1.0f);
                if (Settings.serverPerspective) {
                    FontManager.bit.drawString(camera.getDrawX(this.x), camera.getDrawY(this.y), this.moveSent + ", " + this.healthSent + ", " + this.resilienceSent + ", " + this.manaSent, new FontOptions(12).outline());
                    if (this.ai.blackboard.mover.hasPath()) {
                        this.ai.blackboard.mover.getPath().drawPath(this, camera);
                    }
                }
            });
        }
    }

    private void addDrawablesLoop(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, boolean fromMount) {
        Mob rider;
        final LinkedList<DrawOptions> backDrawOptions = new LinkedList<DrawOptions>();
        final LinkedList<DrawOptions> frontDrawOptions = new LinkedList<DrawOptions>();
        final LinkedList<MobDrawable> drawables = new LinkedList<MobDrawable>();
        final LinkedList<LevelSortedDrawable> riderDrawables = new LinkedList<LevelSortedDrawable>();
        Mob mount = this.getMount();
        if (mount == null || fromMount) {
            this.addDrawables(drawables, tileList, topList, level, x, y, tickManager, camera, perspective);
        }
        if (this.isMounted() && this.shouldDrawRider() && (rider = this.getRider()) != null) {
            rider.addDrawablesLoop(riderDrawables, tileList, topList, level, x, y, tickManager, camera, perspective, true);
        }
        this.buffManager.addExtraDrawOptions(backDrawOptions, frontDrawOptions, x, y, tickManager, camera, perspective);
        final int sortY = this.getDrawSortY(level, x, y, tickManager, camera, perspective, fromMount);
        list.add(new LevelSortedDrawable(this){

            @Override
            public int getSortY() {
                return sortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                backDrawOptions.forEach(DrawOptions::draw);
                drawables.forEach(d -> d.drawBehindRider(tickManager));
                riderDrawables.forEach(d -> d.draw(tickManager));
                drawables.forEach(d -> d.draw(tickManager));
                frontDrawOptions.forEach(DrawOptions::draw);
            }
        });
    }

    protected int getDrawSortY(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, boolean fromMount) {
        return y;
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    protected void addExtraDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public GameTexture getMobIcon() {
        return MobRegistry.getMobIcon(this.getStringID());
    }

    public boolean isHealthBarVisible() {
        return Settings.showMobHealthBars && this.isVisible() && this.getHealthUnlimited() < this.getMaxHealth() && (!this.isBoss() || !Settings.showBossHealthBars);
    }

    public Rectangle getHealthBarBounds(int x, int y) {
        Rectangle selectBox = this.getSelectBox(x, y);
        int width = GameMath.limit(selectBox.width, 32, 64);
        x = selectBox.x + selectBox.width / 2 - width / 2;
        y = selectBox.y - Settings.UI.healthbar_small_background.getHeight() - 2;
        return new Rectangle(x, y, width, Settings.UI.healthbar_small_background.getHeight());
    }

    public void addStatusBarDrawable(OrderableDrawables list, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (perspective == this) {
            return;
        }
        if (this.isHealthBarVisible()) {
            Rectangle bounds = this.getHealthBarBounds(x, y);
            if (bounds == null) {
                return;
            }
            int health = this.getHealthUnlimited();
            int maxHealth = this.getMaxHealth();
            float perc = GameMath.limit((float)health / (float)maxHealth, 0.0f, 1.0f);
            int drawX = camera.getDrawX(bounds.x);
            int drawY = camera.getDrawY(bounds.y);
            GameLight light = level.getLightLevel(Mob.getTileCoordinate(bounds.x + bounds.width / 2), Mob.getTileCoordinate(bounds.y + 4));
            float alphaMod = GameMath.lerp(light.getFloatLevel(), 0.2f, 1.0f);
            Color statusColor = GameUtils.getStatusColorRedPref(perc, 0.75f, 0.7f, 2.2f);
            Color finalFillColor = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), (int)(240.0f * alphaMod));
            Color finalBackgroundColor = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), (int)(190.0f * alphaMod));
            DrawOptions options = new ProgressBarDrawOptions(Settings.UI.healthbar_small_fill, bounds.width).color(finalBackgroundColor).addBar(Settings.UI.healthbar_small_background, perc).color(finalFillColor).minWidth(4).end().pos(drawX, drawY);
            list.add(tm -> options.draw());
        }
    }

    protected void addShadowDrawables(OrderableDrawables list, Level level, int x, int y, GameLight light, GameCamera camera) {
        if (this.buffManager.getModifier(BuffModifiers.INVISIBILITY).booleanValue() || this.isRiding()) {
            return;
        }
        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera);
        if (shadowOptions != null) {
            list.add(tm -> shadowOptions.draw());
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return this.isBoss() || super.isVisibleOnMap(client, map);
    }

    public CollisionFilter getLevelCollisionFilter() {
        return new CollisionFilter().mobCollision();
    }

    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            if (this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS).booleanValue()) {
                return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
            }
            return this.getLevel().regionManager.BASIC_DOOR_OPTIONS;
        }
        return null;
    }

    public boolean estimateCanMoveTo(int tileX, int tileY, boolean acceptAdjacentTile) {
        CollisionFilter collisionFilter = this.getLevelCollisionFilter();
        if (collisionFilter == null || !collisionFilter.hasAdders()) {
            return true;
        }
        PathDoorOption doorOption = this.getPathDoorOption();
        if (doorOption != null) {
            return doorOption.canMoveToTile(this.getTileX(), this.getTileY(), tileX, tileY, acceptAdjacentTile);
        }
        return RegionPathfinding.canMoveToTile(this.getLevel(), this.getTileX(), this.getTileY(), tileX, tileY, doorOption, acceptAdjacentTile);
    }

    public boolean canBeTargetedFromAdjacentTiles() {
        return false;
    }

    public CollisionFilter modifyChasingCollisionFilter(CollisionFilter collisionFilter, Mob target) {
        int tileY;
        int tileX;
        if (collisionFilter == null) {
            return null;
        }
        if (this.canBeTargetedFromAdjacentTiles()) {
            tileX = this.getTileX();
            tileY = this.getTileY();
            collisionFilter = collisionFilter.addFilter(tp -> tp.tileX != tileX || tp.tileY != tileY);
        }
        if (target != null && target.canBeTargetedFromAdjacentTiles()) {
            tileX = target.getTileX();
            tileY = target.getTileY();
            collisionFilter = collisionFilter.addFilter(tp -> tp.tileX != tileX || tp.tileY != tileY);
        }
        return collisionFilter;
    }

    public final boolean collidesWith(Level level, int x, int y) {
        return level.collides((Shape)this.getCollision(x, y), this.getLevelCollisionFilter());
    }

    public final boolean collidesWith(Level level) {
        return this.collidesWith(level, this.getX(), this.getY());
    }

    public boolean collidesWith(Mob other) {
        return other != this && this.getCollision().intersects(other.getCollision());
    }

    public Rectangle getCollision(int x, int y) {
        return new Rectangle(x + this.collision.x, y + this.collision.y, this.collision.width, this.collision.height);
    }

    public Rectangle getCollision() {
        return this.getCollision(this.getX(), this.getY());
    }

    public Rectangle getHitBox(int x, int y) {
        return new Rectangle(x + this.hitBox.x, y + this.hitBox.y, this.hitBox.width, this.hitBox.height);
    }

    public Rectangle getHitBox() {
        return this.getHitBox(this.getX(), this.getY());
    }

    public Rectangle getSelectBox(int x, int y) {
        Mob mount;
        if (this.isRiding() && (mount = this.getMount()) != null && !mount.shouldDrawRider()) {
            Rectangle mountSelectBox = mount.getSelectBox(x, y);
            return new Rectangle(mountSelectBox.x, mountSelectBox.y, mountSelectBox.width, 0);
        }
        MaskShaderOptions mask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        int offset = mask == null ? 0 : mask.drawYOffset;
        return new Rectangle(x + this.selectBox.x, y + this.selectBox.y + offset, this.selectBox.width, this.selectBox.height - offset);
    }

    public Rectangle getSelectBox() {
        return this.getSelectBox(this.getX(), this.getY());
    }

    public Rectangle getMountUnionSelectBox(int x, int y) {
        Rectangle currentSelectBox = this.getSelectBox(x, y);
        for (Mob currentMob = this.getMount(); currentMob != null; currentMob = currentMob.getMount()) {
            currentSelectBox = currentSelectBox.union(currentMob.getSelectBox(x, y));
        }
        return currentSelectBox;
    }

    public void updatePosFromServer(float x, float y, boolean isDirect) {
        this.updatePosFromServer(x, y, this.dx, this.dy, isDirect);
    }

    public void updatePosFromServer(float x, float y, float dx, float dy, boolean isDirect) {
        if (isDirect || this.movePosTolerance <= 0.0f || this.getDistance(x, y) >= this.movePosTolerance) {
            this.setPos(x, y, isDirect);
            this.dx = dx;
            this.dy = dy;
        }
    }

    public void setPos(float x, float y, boolean isDirect) {
        Mob mount;
        if (isDirect && this.isServer() && this.isInitialized() && this.shouldSendSpawnPacket()) {
            int lastRegionX = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.x));
            int lastRegionY = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.y));
            int nextRegionX = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(x));
            int nextRegionY = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(y));
            if (lastRegionX != nextRegionX || lastRegionY != nextRegionY) {
                this.nextMovementPacketRegionPositions.add(lastRegionX, lastRegionY);
                this.sendMovementPacket(isDirect);
            }
        }
        if (this.isRiding() && (mount = this.getMount()) != null) {
            mount.setPos(x, y, isDirect);
        }
        this.x = x;
        this.y = y;
        if (isDirect) {
            this.nX = x;
            this.nY = y;
            this.isSmoothSnapped = true;
        } else {
            this.isSmoothSnapped = false;
        }
    }

    protected void checkCollision() {
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "checkCollision", () -> {
            Rectangle collision = this.getCollision();
            int range = (int)GameMath.max(GameMath.diagonalMoveDistance(0, 0, collision.width, collision.height), 100.0);
            this.getLevel().entityManager.streamAreaMobsAndPlayers(this.x, this.y, range).filter(m -> m != this && this.collidesWith((Mob)m)).forEach(this::collidedWith);
        });
    }

    public boolean collidesWithAnyMob(Level level, int x, int y) {
        Rectangle collision = this.getCollision(x, y);
        int range = (int)GameMath.max(GameMath.diagonalMoveDistance(0, 0, collision.width, collision.height), 100.0);
        return level.entityManager.streamAreaMobsAndPlayers(x, y, range).anyMatch(m -> m != this && m.canPushMob(this) && this.canBePushed((Mob)m) && this.collidesWith((Mob)m));
    }

    protected void collidedWith(Mob other) {
        if (this.mount == other.getRealUniqueID() || this.rider == other.getRealUniqueID()) {
            return;
        }
        if (other.canPushMob(this) && this.canBePushed(other)) {
            float dist;
            Point2D.Float normVec = GameMath.normalize(this.x - other.x, this.y - other.y);
            if (normVec.x == 0.0f && normVec.y == 0.0f) {
                normVec = new Point2D.Float(0.0f, -1.0f);
            }
            if ((dist = this.getDistance(other)) < 1.0f) {
                dist = 1.0f;
            }
            this.colDx += normVec.x * 50.0f / dist;
            this.colDy += normVec.y * 50.0f / dist;
        }
        if (this.getWorldSettings().disableMobAI) {
            return;
        }
        if (other.isPlayer) {
            GameDamage collisionDamage;
            if (this.isServer()) {
                if (Settings.strictServerAuthority && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other)) {
                    this.handleCollisionHit(other, false, null);
                }
            } else if (this.isClient() && !this.getClient().hasStrictServerAuthority() && ((PlayerMob)other).getNetworkClient() == this.getClient().getClient() && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other) && (collisionDamage = this.getCollisionDamage(other, false, null)) != null) {
                this.collisionHitCooldowns.startCooldown(other);
                other.startHitCooldown();
                this.getClient().network.sendPacket(new PacketPlayerCollisionHit(this));
            }
        } else if (this.isServer() && this.collisionHitCooldowns.canHit(other) && this.canCollisionHit(other)) {
            this.handleCollisionHit(other, false, null);
        }
    }

    public final void handleCollisionHit(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        GameDamage collisionDamage = this.getCollisionDamage(target, fromPacket, packetSubmitter);
        if (collisionDamage != null) {
            this.handleCollisionHit(target, collisionDamage, this.getCollisionKnockback(target));
            this.collisionHitCooldowns.startCooldown(target);
        }
    }

    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        target.isServerHit(damage, target.x - this.x, target.y - this.y, knockback, this);
    }

    public boolean canPushMob(Mob other) {
        return true;
    }

    public boolean canBePushed(Mob other) {
        return this.buffManager.getModifier(BuffModifiers.GROUNDED) == false;
    }

    public boolean canCollisionHit(Mob target) {
        return !this.removed() && target.canBeTargeted(this, this.getPvPOwner()) && target.canBeHit(this);
    }

    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return null;
    }

    public int getCollisionKnockback(Mob target) {
        return 100;
    }

    public float getDistance(Mob other) {
        return (float)new Point2D.Float(this.x, this.y).distance(other.x, other.y);
    }

    public float getDistance(float x, float y) {
        return (float)new Point2D.Float(this.x, this.y).distance(x, y);
    }

    public float getDiagonalMoveDistance(Mob other) {
        return this.getDiagonalMoveDistance(other.x, other.y);
    }

    public float getDiagonalMoveDistance(float toX, float toY) {
        return (float)GameMath.diagonalMoveDistance(this.x - toX, this.y - toY);
    }

    public NetworkClient getPvPOwner() {
        if (this.rider != -1) {
            if (this.isServer()) {
                if (this.rider >= 0 && this.rider < this.getServer().getSlots()) {
                    return this.getServer().getClient(this.rider);
                }
            } else if (this.isClient() && this.rider >= 0 && this.rider < this.getClient().getSlots()) {
                return this.getClient().getClient(this.rider);
            }
        }
        return this.getFollowingClient();
    }

    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        NetworkClient pvpOwner;
        Mob followingMob;
        if (this.buffManager.getModifier(BuffModifiers.UNTARGETABLE).booleanValue()) {
            return false;
        }
        if (!this.canTakeDamage()) {
            return false;
        }
        if (this.getUniqueID() == attacker.getUniqueID()) {
            return false;
        }
        if (this.getUniqueID() == attacker.mount) {
            return false;
        }
        if (this.isSameTeam(attacker)) {
            return false;
        }
        if (!this.isSamePlace(attacker)) {
            return false;
        }
        if (attacker.isInAttackOwnerChain(this)) {
            return false;
        }
        if (!attacker.canTarget(this)) {
            return false;
        }
        if (this.isFollowing() && (followingMob = this.getFollowingMob()) != null) {
            if (followingMob.isSameTeam(attacker)) {
                return false;
            }
            if (this.isInAttackOwnerChain(attacker)) {
                return false;
            }
        }
        if ((followingMob = attacker.getFollowingMob()) != null) {
            NetworkClient followingClient;
            if (!this.canBeTargeted(followingMob, attackerClient)) {
                return false;
            }
            if (followingMob.isPlayer && (followingClient = ((PlayerMob)followingMob).getNetworkClient()) != null && attacker == followingClient.playerMob) {
                return false;
            }
        }
        return (pvpOwner = this.getPvPOwner()) == null || attackerClient == null || pvpOwner == attackerClient || pvpOwner.pvpEnabled() && attackerClient.pvpEnabled();
    }

    public boolean canTarget(Mob target) {
        return true;
    }

    public int getFlyingHeight() {
        Mob mount;
        if (this.isRiding() && (mount = this.getMount()) != null) {
            return mount.getFlyingHeight();
        }
        return 0;
    }

    public final boolean isFlying() {
        return this.getFlyingHeight() > 0;
    }

    public boolean canHitThroughCollision() {
        return false;
    }

    public boolean canBeHit(Attacker attacker) {
        if (!this.canTakeDamage()) {
            return false;
        }
        Mob mounted = this.getRider();
        if (mounted != null && mounted.canTakeDamage()) {
            return mounted.canBeHit(attacker);
        }
        return this.getTime() >= this.hitTime + (long)this.hitCooldown;
    }

    public int getHitCooldownUniqueID() {
        return this.getUniqueID();
    }

    public void startHitCooldown() {
        this.hitTime = this.getTime();
    }

    public long getTimeSinceLastHit() {
        return this.getTime() - this.hitTime;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
    }

    public void playAmbientSound() {
        SoundManager.playSound(this.getAmbientSound(), this);
    }

    public void playHitSound() {
        SoundManager.playSound(this.getHitSound(), this);
    }

    public void playHurtSound() {
        SoundManager.playSound(this.getHurtSound(), this);
    }

    public void playResilienceSound() {
        SoundManager.playSound(this.getResilienceSound(), this);
    }

    public void playHitDeathSound() {
        SoundManager.playSound(this.getHitDeathSound(), this);
    }

    public void playDeathSound() {
        SoundManager.playSound(this.getDeathSound(), this);
    }

    protected SoundSettings getAmbientSound() {
        return null;
    }

    protected SoundSettings getHitSound() {
        return new SoundSettings(GameResources.npchurt);
    }

    protected SoundSettings getHurtSound() {
        return null;
    }

    protected SoundSettings getResilienceSound() {
        return new SoundSettings(GameResources.cling);
    }

    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.npcdeath);
    }

    protected SoundSettings getDeathSound() {
        return null;
    }

    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
    }

    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        Mob attackOwner;
        if (!this.canTakeDamage()) {
            return null;
        }
        int damageResisted = 0;
        int damage = event.damage;
        this.startHitCooldown();
        this.lastCombatTime = this.hitTime;
        Mob mob = attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        if (attacker != null) {
            attacker.getAttackOwners().forEach(m -> {
                m.lastCombatTime = this.hitTime;
            });
        }
        if (!event.wasPrevented) {
            damage = event.damage - (int)this.getResilience();
            if (damage < 0) {
                this.setResilienceHidden(-damage);
                damageResisted = event.damage;
            } else {
                this.setHealthHidden(this.getHealth() - damage, event.knockbackX, event.knockbackY, attacker);
                if (this.getResilience() > 0.0f) {
                    damageResisted = (int)this.getResilience();
                    this.setResilienceHidden(0.0f);
                }
            }
            float finalKnockback = event.knockbackAmount;
            if (attackOwner != null && attackOwner.isPlayer) {
                finalKnockback *= this.getWorldSettings().difficulty.knockbackGivenModifier;
            }
            this.knockback(event.knockbackX, event.knockbackY, finalKnockback);
        }
        this.doWasHitLogic(event);
        if (attackOwner != null) {
            attackOwner.doHasAttackedLogic(event);
        }
        if (this.getLevel() != null && !this.isServer()) {
            if (Settings.showDamageText && event.showDamageTip) {
                if (damageResisted > 0) {
                    this.spawnResistedDamageText(damageResisted, event.isCrit ? 20 : 16, event.isCrit);
                }
                if (damage > 0) {
                    this.spawnDamageText(damage, event.isCrit ? 20 : 16, event.isCrit);
                }
            }
            if (this.getHealth() > 0 && event.playHitSound) {
                if (damageResisted > 0) {
                    this.playResilienceSound();
                }
                if (damage > 0) {
                    this.playHitSound();
                    this.playHurtSound();
                }
            }
        }
        return event;
    }

    public void spawnDamageText(int damage, int size, boolean isCrit) {
        Rectangle box = this.getSelectBox();
        int rndX = GameRandom.globalRandom.getIntOffset(box.x + box.width / 2, box.width / 4);
        int rndY = GameRandom.globalRandom.getIntOffset(box.y + box.height, box.height / 4);
        this.getLevel().hudManager.addElement(new DamageText(rndX, rndY, damage, new FontOptions(size).outline().color(isCrit ? new Color(225, 120, 0) : Color.YELLOW), isCrit ? GameRandom.globalRandom.getIntBetween(50, 65) : GameRandom.globalRandom.getIntBetween(25, 45)));
    }

    public void spawnResistedDamageText(int damage, int size, boolean isCrit) {
        Rectangle box = this.getSelectBox();
        int rndX = GameRandom.globalRandom.getIntOffset(box.x + box.width / 2, box.width / 4);
        int rndY = GameRandom.globalRandom.getIntOffset(box.y + box.height, box.height / 4);
        this.getLevel().hudManager.addElement(new DamageText(rndX, rndY, damage, new FontOptions(size).outline().color(new Color(125, 125, 125)), isCrit ? GameRandom.globalRandom.getIntBetween(50, 65) : GameRandom.globalRandom.getIntBetween(25, 45)));
    }

    public void knockback(float x, float y, float knockback) {
        Mob mount;
        if (this.isRiding() && (mount = this.getMount()) != null) {
            mount.knockback(x, y, knockback);
            return;
        }
        if ((knockback *= this.getKnockbackModifier()) != 0.0f) {
            Point2D.Float normVec = GameMath.normalize(x, y);
            this.dx += normVec.x * knockback;
            this.dy += normVec.y * knockback;
        }
    }

    public MobWasHitEvent isServerHit(GameDamage damage, float x, float y, float knockback, Attacker attacker) {
        PlayerMob firstPlayer;
        Mob mounted;
        if (this.removed()) {
            return null;
        }
        if (!this.canTakeDamage()) {
            return null;
        }
        if (this.isMounted() && (mounted = this.getRider()) != null && mounted.canTakeDamage()) {
            mounted.isServerHit(damage, x, y, knockback, attacker);
            return null;
        }
        Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        int beforeHealth = this.getHealth();
        MobBeforeHitEvent beforeHitEvent = new MobBeforeHitEvent(this, attacker, damage, x, y, knockback);
        this.doBeforeHitLogic(beforeHitEvent);
        if (attackOwner != null) {
            attackOwner.doBeforeAttackedLogic(beforeHitEvent);
        }
        MobBeforeHitCalculatedEvent beforeHitCalculatedEvent = new MobBeforeHitCalculatedEvent(beforeHitEvent);
        this.doBeforeHitCalculatedLogic(beforeHitCalculatedEvent);
        if (attackOwner != null) {
            attackOwner.doBeforeAttackedCalculatedLogic(beforeHitCalculatedEvent);
        }
        MobWasHitEvent hitEvent = new MobWasHitEvent(beforeHitCalculatedEvent);
        this.sendHitPacket(hitEvent, attacker);
        this.isHit(hitEvent, attacker);
        PlayerMob playerMob = firstPlayer = attacker == null ? null : attacker.getFirstPlayerOwner();
        if (firstPlayer != null && this.countStats && firstPlayer.isServerClient()) {
            ServerClient client = firstPlayer.getServerClient();
            int healthDamage = beforeHealth - this.getHealth();
            if (healthDamage > 0 && this.countDamageDealt()) {
                client.newStats.type_damage_dealt.addDamage(damage.type, healthDamage);
            }
            if (healthDamage >= this.getMaxHealth()) {
                JournalChallenge challenge;
                if (AchievementManager.ONE_TAPPED_MOBS.contains(this.getStringID()) && client.achievementsLoaded()) {
                    client.achievements().ONE_TAPPED.markCompleted(client);
                }
                if (JournalChallengeRegistry.ONESHOT_SKELETON_VALID_STRINGIDS.contains(this.getStringID()) && !(challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.ONESHOT_SKELETON_ID)).isCompleted(client) && challenge.isJournalEntryDiscovered(client)) {
                    Level level = this.getLevel();
                    if (level.isCave && JournalChallengeUtils.isDesertOrTempleBiome(level.getBiome(this.getTileX(), this.getTileY()))) {
                        challenge.markCompleted(client);
                        client.forceCombineNewStats();
                    }
                }
            }
        }
        return hitEvent;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return null;
    }

    @Override
    public GameMessage getAttackerName() {
        return this.getLocalization();
    }

    public GameMessage getLocalization() {
        return MobRegistry.getLocalization(this.getID());
    }

    public final String getDisplayName() {
        return this.getLocalization().translate();
    }

    @Override
    public Mob getFirstAttackOwner() {
        return this;
    }

    @Override
    public int getAttackerUniqueID() {
        return this.getUniqueID();
    }

    protected void sendHitPacket(MobWasHitEvent event, Attacker attacker) {
        if (!this.isServer()) {
            return;
        }
        this.getServer().network.sendToClientsWithEntity(new PacketHitMob(this, event, attacker), this);
    }

    protected void doBeforeHitLogic(MobBeforeHitEvent event) {
        if (!this.isMounted() && !this.isClient()) {
            this.ai.beforeHit(event);
        }
        this.buffManager.getArrayBuffs().forEach(b -> b.onBeforeHit(event));
    }

    protected void doBeforeAttackedLogic(MobBeforeHitEvent event) {
        this.buffManager.getArrayBuffs().forEach(b -> b.onBeforeAttacked(event));
    }

    protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent event) {
        if (!this.isMounted() && !this.isClient()) {
            this.ai.beforeHitCalculated(event);
        }
        this.buffManager.getArrayBuffs().forEach(b -> b.onBeforeHitCalculated(event));
    }

    protected void doBeforeAttackedCalculatedLogic(MobBeforeHitCalculatedEvent event) {
        this.buffManager.getArrayBuffs().forEach(b -> b.onBeforeAttackedCalculated(event));
    }

    protected void doWasHitLogic(MobWasHitEvent event) {
        if (!this.isMounted() && !this.isClient()) {
            this.ai.wasHit(event);
        }
        this.buffManager.getArrayBuffs().forEach(b -> b.onWasHit(event));
    }

    protected void doHasAttackedLogic(MobWasHitEvent event) {
        this.buffManager.getArrayBuffs().forEach(b -> b.onHasAttacked(event));
    }

    protected void doHasKilledTarget(MobWasKilledEvent event) {
        this.buffManager.getArrayBuffs().forEach(b -> b.onHasKilledTarget(event));
    }

    public void startAttackCooldown() {
        this.attackTime = this.getTime();
    }

    public long getNextAttackCooldown() {
        return this.attackTime + (long)this.attackCooldown - this.getTime();
    }

    public boolean canAttack() {
        boolean bl = this.onCooldown = this.getNextAttackCooldown() > 0L;
        if (this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue() || this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return false;
        }
        return !this.onCooldown;
    }

    public LootTable getLootTable() {
        return new LootTable();
    }

    public LootTable getPrivateLootTable() {
        return new LootTable();
    }

    public LootTable showAdditionalLootTableInJournal() {
        return new LootTable();
    }

    public boolean dropsLoot() {
        return this.dropsLoot;
    }

    public double getDistanceRan() {
        return this.distanceRan;
    }

    public double getDistanceRidden() {
        return this.distanceRidden;
    }

    public float getCurrentSpeed() {
        return this.currentSpeed;
    }

    protected void tickRegen() {
        int maxResilience;
        if (this.removed()) {
            return;
        }
        this.buffManager.tickDamageOverTime();
        if (!this.isInCombat()) {
            this.regenBuffer += (double)(this.getRegen() / 20.0f);
        }
        this.regenBuffer += (double)(this.getCombatRegen() / 20.0f);
        if (this.regenBuffer >= 1.0 || this.regenBuffer <= -1.0) {
            int delta = (int)this.regenBuffer;
            this.regenBuffer -= (double)delta;
            if (delta < 0 && this.getHealth() > 0 || delta > 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + delta, null);
            }
        }
        if ((maxResilience = this.getMaxResilience()) > 0) {
            float resilienceDecay;
            float resilienceRegen = this.getResilienceRegen();
            if (resilienceRegen > 0.0f) {
                this.addResilienceHidden(resilienceRegen / 20.0f);
            } else if (this.getResilience() > 0.0f && (float)this.resilienceGainedTime + this.resilienceDecayDelay < (float)this.getTime() && (resilienceDecay = this.getResilienceDecay()) > 0.0f) {
                this.addResilienceHidden(-resilienceDecay / 20.0f);
            }
        }
        if (this.usesMana()) {
            float manaRegenBuffer = 0.0f;
            if (!this.isInCombat()) {
                manaRegenBuffer += this.getManaRegen() / 20.0f;
            }
            if (this.getTime() > this.lastManaSpentTime + 5000L) {
                this.isManaExhausted = false;
                if (this.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
                    this.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
                }
            }
            this.setManaHidden(this.getMana() + (manaRegenBuffer += this.getCombatManaRegen() / 20.0f));
        }
    }

    public boolean isInCombat() {
        return this.getTime() < this.lastCombatTime + 5000L;
    }

    public void forceInCombat() {
        this.lastCombatTime = this.getTime();
    }

    public long getTimeSinceStartCombat() {
        if (this.combatStartTime < 0L) {
            return this.combatStartTime;
        }
        if (this.getHealth() == this.getMaxHealth()) {
            return -1L;
        }
        return this.getTime() - this.combatStartTime;
    }

    public void sendMovementPacket(boolean isDirect) {
        this.sendNextMovementPacket = true;
        this.nextMovementPacketDirect = this.nextMovementPacketDirect || isDirect;
    }

    public void sendHealthPacket(boolean isFull) {
        if (!this.canTakeDamage()) {
            return;
        }
        this.sendNextHealthPacket = true;
        this.nextHealthPacketFull = this.nextHealthPacketFull || isFull;
    }

    public void sendResiliencePacket(boolean isFull) {
        if (this.getMaxResilience() <= 0) {
            return;
        }
        this.sendNextResiliencePacket = true;
        this.nextResiliencePacketFull = this.nextResiliencePacketFull || isFull;
    }

    public void sendManaPacket(boolean isFull) {
        if (!this.usesMana()) {
            return;
        }
        this.sendNextManaPacket = true;
        this.nextManaPacketFull = this.nextManaPacketFull || isFull;
    }

    public boolean isAccelerating() {
        return this.moveX != 0.0f || this.moveY != 0.0f;
    }

    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotInLiquid().checkNotSolidTile().checkNotOnSurfaceInsideOnFloor().checkNotLevelCollides();
    }

    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return false;
    }

    public void onSpawned(int posX, int posY) {
        this.setPos(posX, posY, true);
        this.setHealth(this.getMaxHealth());
        this.spawnTilePosition = new Point(GameMath.getTileCoordinate(posX), GameMath.getTileCoordinate(posY));
    }

    public void setSpawnTilePosition(int tileX, int tileY) {
        this.spawnTilePosition = new Point(tileX, tileY);
    }

    public boolean canDespawn() {
        if (!this.canDespawn) {
            return false;
        }
        return GameUtils.streamNetworkClients(this.getLevel()).filter(c -> c.playerMob != null).noneMatch(c -> this.getDistance(c.playerMob) < (float)Mob.MOB_SPAWN_AREA.maxSpawnDistance);
    }

    @Override
    public boolean shouldRemoveWhenInUnloadedRegion() {
        if (this.isClient()) {
            return true;
        }
        return this.canDespawn;
    }

    public void attack(int x, int y, boolean showAllDirections) {
        this.attackTime = this.getTime();
        if (showAllDirections) {
            this.setFacingDir(x - this.getX(), y - this.getY());
        } else if (x > this.getX()) {
            this.setDir(1);
        } else {
            this.setDir(3);
        }
    }

    public void forceStopAttack() {
        this.attackTime = 0L;
        this.isAttacking = false;
    }

    public long getLastAttackTime() {
        return this.attackTime;
    }

    public long getTimeSinceLastAttack() {
        return this.getTime() - this.attackTime;
    }

    public void showAttack(int x, int y, boolean showAllDirections) {
    }

    public int getStartAttackHeight() {
        return 14;
    }

    public int getCurrentAttackHeight() {
        int height = this.getStartAttackHeight();
        Mob mount = this.getMount();
        if (mount != null) {
            height -= mount.getBobbing(mount.getX(), mount.getY());
            if (mount.getLevel() != null) {
                height -= mount.getLevel().getTile(mount.getTileX(), mount.getTileY()).getMobSinkingAmount(mount);
            }
        } else {
            height -= this.getBobbing(this.getX(), this.getY());
            if (this.getLevel() != null) {
                height -= this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount(this);
                MaskShaderOptions swimShader = this.getSwimMaskShaderOptions(this.inLiquidFloat(this.getX(), this.getY()));
                if (swimShader != null) {
                    height -= swimShader.drawYOffset;
                }
            }
        }
        return height;
    }

    public int getCurrentAttackDrawYOffset() {
        Mob mount = this.getMount();
        if (mount != null) {
            return -this.getCurrentAttackHeight() + mount.getRiderDrawYOffset();
        }
        return -this.getCurrentAttackHeight();
    }

    public int getCurrentAttackDrawXOffset() {
        Mob mount = this.getMount();
        if (mount != null) {
            return mount.getRiderDrawXOffset();
        }
        return this.getRiderDrawXOffset();
    }

    public final void runNetworkFieldUpdate(PacketReader reader) {
        this.networkFields.readUpdatePacket(reader);
    }

    public <T extends NetworkField<?>> T registerNetworkField(T field) {
        return this.networkFields.registerField(field);
    }

    public final void runAbility(int id, PacketReader reader) {
        this.abilities.runAbility(id, reader);
    }

    public <T extends MobAbility> T registerAbility(T ability) {
        return this.abilities.registerAbility(ability);
    }

    public void interact(PlayerMob player) {
    }

    public Point getInteractPos() {
        return this.getDrawPos();
    }

    public boolean inInteractRange(Mob mob) {
        Point interactPos = this.getInteractPos();
        return mob.getDistance(interactPos.x, interactPos.y) <= 75.0f;
    }

    public boolean canInteract(Mob mob) {
        return false;
    }

    public Mob getMount() {
        if (!this.isRiding()) {
            return null;
        }
        Mob out = GameUtils.getLevelMob(this.mount, this.getLevel());
        if (out != null) {
            if (out.rider != this.getRealUniqueID()) {
                out.rider = this.getRealUniqueID();
            }
            if (out.removed()) {
                this.mount = -1;
                this.buffManager.updateBuffs();
                return null;
            }
        }
        return out;
    }

    public Mob getRider() {
        if (!this.isMounted()) {
            return null;
        }
        Mob out = GameUtils.getLevelMob(this.rider, this.getLevel());
        if (out != null) {
            if (out.mount != this.getRealUniqueID()) {
                out.mount = this.getRealUniqueID();
                out.buffManager.updateBuffs();
            }
            if (out.removed()) {
                this.rider = -1;
                return null;
            }
        }
        return out;
    }

    public boolean isMounted() {
        return this.rider != -1;
    }

    public boolean isRiding() {
        return this.mount != -1;
    }

    protected void doMountedLogic() {
    }

    public boolean mount(Mob mob, boolean setMounterPos) {
        return this.mount(mob, setMounterPos, this.x, this.y, false);
    }

    public boolean mount(Mob mob, boolean setMounterPos, float mounterX, float mounterY, boolean forced) {
        Mob lastMount;
        if (this.isRiding() && (lastMount = this.getMount()) != mob) {
            GameMessage dismountError;
            if (lastMount != null && !forced && (dismountError = lastMount.getMountDismountError(this, null)) != null) {
                return false;
            }
            this.dismount();
        }
        if (mob != null) {
            this.mountSetMounterPos = setMounterPos;
            if (setMounterPos) {
                if (mob.collidesWith(mob.getLevel(), this.getX(), this.getY())) {
                    GameLog.debug.println("Looks like mount is colliding, swapping back to original mount position");
                    mob.setPos(mounterX, mounterY, true);
                } else {
                    mob.setPos(this.getX(), this.getY(), true);
                }
            } else {
                this.setPos(mob.x, mob.y, true);
            }
            mob.rider = this.getUniqueID();
            this.mount = mob.getUniqueID();
            mob.doMountedLogic();
            this.buffManager.updateBuffs();
            return true;
        }
        return false;
    }

    public void updateMount() {
        Mob mount = this.getMount();
        if (mount != null) {
            this.mount(mount, this.mountSetMounterPos);
        } else if (this.isRiding() && this.getLevel() != null) {
            if (this.isClient()) {
                this.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.mount));
            } else {
                this.dismount();
                this.sendMovementPacket(false);
            }
        }
    }

    public void updateRider() {
        Mob rider = this.getRider();
        if (rider != null) {
            rider.mount(this, rider.mountSetMounterPos);
        } else if (this.isMounted() && this.getLevel() != null) {
            if (this.isClient()) {
                this.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.rider));
            } else {
                this.dismounted();
                this.sendMovementPacket(false);
            }
        }
    }

    public void dismount() {
        Mob mount = this.getMount();
        this.mount = -1;
        this.buffManager.updateBuffs();
        if (mount != null) {
            this.dx = mount.dx;
            this.dy = mount.dy;
            mount.rider = -1;
            if (!this.collidesWith(this.getLevel(), this.getX() + 5, this.getY())) {
                this.setPos(this.getX() + 5, this.getY(), true);
            } else if (!this.collidesWith(this.getLevel(), this.getX(), this.getY() + 5)) {
                this.setPos(this.getX(), this.getY() + 5, true);
            } else if (!this.collidesWith(this.getLevel(), this.getX() - 5, this.getY())) {
                this.setPos(this.getX() - 5, this.getY(), true);
            } else if (!this.collidesWith(this.getLevel(), this.getX(), this.getY() - 5)) {
                this.setPos(this.getX(), this.getY() - 5, true);
            }
        }
    }

    public final GameMessage getRiderDismountError(InventoryItem item) {
        Mob mount = this.getMount();
        if (mount != null) {
            return mount.getMountDismountError(this, item);
        }
        return null;
    }

    public GameMessage getMountDismountError(Mob rider, InventoryItem item) {
        return null;
    }

    public void dismounted() {
        Mob rider = this.getRider();
        if (rider != null) {
            rider.dx = this.dx;
            rider.dy = this.dy;
            rider.mount = -1;
            rider.buffManager.updateBuffs();
        }
        this.rider = -1;
    }

    public int getTeam() {
        Mob mounted = this.getRider();
        if (mounted != null) {
            return mounted.getTeam();
        }
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null) {
            return followingMob.getTeam();
        }
        return this.team;
    }

    public boolean isFollowing() {
        return this.followingUniqueID != -1;
    }

    public int getFollowingUniqueID() {
        return this.followingUniqueID;
    }

    public void setFollowing(Mob mob, boolean sendUpdatePacket) {
        if (mob == null) {
            this.followingUniqueID = -1;
            this.foundFollowingMob = null;
        } else {
            this.followingUniqueID = mob.getUniqueID();
            this.foundFollowingMob = mob;
        }
        if (sendUpdatePacket) {
            this.getServer().network.sendToClientsWithEntity(new PacketMobFollowUpdate(this.getUniqueID(), this.followingUniqueID), this);
        }
    }

    public Mob getFollowingMob() {
        if (this.followingUniqueID == -1) {
            if (this.foundFollowingMob != null) {
                this.foundFollowingMob = null;
            }
            return null;
        }
        this.calculateFoundFollowingMob();
        return this.foundFollowingMob;
    }

    protected void calculateFoundFollowingMob() {
        if (this.foundFollowingMob == null || this.foundFollowingMob.getUniqueID() != this.followingUniqueID) {
            this.foundFollowingMob = GameUtils.getLevelMob(this.followingUniqueID, this.getLevel(), false, true);
        }
    }

    public NetworkClient getFollowingClient() {
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null && followingMob.isPlayer) {
            return ((PlayerMob)followingMob).getNetworkClient();
        }
        return null;
    }

    public ItemAttackerMob getFollowingItemAttacker() {
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null && followingMob.isItemAttacker) {
            return (ItemAttackerMob)followingMob;
        }
        return null;
    }

    public void onFollowingAnotherLevel(Mob followingMob) {
        this.getLevel().entityManager.changeMobLevel(this, followingMob.getLevel(), followingMob.getX(), followingMob.getY(), true);
    }

    public void applyFollowUpdatePacket(PacketMobFollowUpdate packet) {
        this.refreshClientUpdateTime();
        this.followingUniqueID = packet.followingUniqueID;
        this.calculateFoundFollowingMob();
    }

    public boolean isSameTeam(Mob other) {
        if (this.getTeam() == -1 || other.getTeam() == -1) {
            return false;
        }
        return this.getTeam() == other.getTeam();
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getBobbing() {
        return this.getBobbing(this.getX(), this.getY());
    }

    public int getBobbing(int x, int y) {
        Mob mount = this.getMount();
        if (mount != null) {
            return mount.getBobbing(x, y);
        }
        if (!this.inLiquid(x, y)) {
            return 0;
        }
        if (this.dx == 0.0f && this.dy == 0.0f) {
            return this.getLevel().getLevelTile(Mob.getTileCoordinate(x), Mob.getTileCoordinate(y)).getLiquidBobbing();
        }
        int rock = this.getWaterRockSpeed();
        int halfRock = rock / 2;
        if (halfRock == 0) {
            return 0;
        }
        int dir = this.getDir();
        if (dir == 0) {
            return y % rock / halfRock;
        }
        if (dir == 1) {
            return x % rock / halfRock;
        }
        if (dir == 2) {
            return y % rock / halfRock;
        }
        if (dir == 3) {
            return x % rock / halfRock;
        }
        return 0;
    }

    protected int getRockSpeed() {
        return 17;
    }

    protected int getWaterRockSpeed() {
        return (int)(this.getSpeed() * 2.0f);
    }

    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if (this.removed()) {
            return;
        }
        this.buffManager.addBuff(buff, sendUpdatePacket);
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.empty();
    }

    public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
        return Stream.empty();
    }

    public Point getPathMoveOffset() {
        return new Point(16, 16);
    }

    public final void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setSwimSpeed(float swimSpeed) {
        this.swimSpeed = swimSpeed;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void addAttackers(Collection<Attacker> attackers) {
        this.attackers.addAll(attackers);
    }

    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        int damage;
        int maxHealth;
        if (!this.canTakeDamage() && health < this.getHealth()) {
            return;
        }
        int beforeHealth = this.getHealth();
        if (beforeHealth == (maxHealth = this.getMaxHealth())) {
            this.combatStartTime = this.getTime();
        }
        this.health = Math.min(health, maxHealth);
        if (this.health == maxHealth) {
            this.attackers.clear();
            this.isDamagedByPlayers = false;
        }
        MobHealthChangedEvent healthChangedEvent = new MobHealthChangedEvent(beforeHealth, this.health, fromNetworkUpdate);
        if (this.getLevel() != null && beforeHealth != this.health) {
            this.getLevel().streamAll(MobHealthChangeListenerEntityComponent.class).forEach(listener -> listener.onLevelMobHealthChanged(this, beforeHealth, this.health, knockbackX, knockbackY, attacker));
        }
        this.buffManager.submitMobEvent(healthChangedEvent);
        if (this.isServer() && (damage = beforeHealth - this.getHealth()) > 0) {
            PlayerMob pl;
            if (this.countStats && this.isPlayer && (pl = (PlayerMob)this).getServerClient() != null) {
                pl.getServerClient().newStats.damage_taken.increment(damage);
            }
            if (attacker != null) {
                if (!this.isDamagedByPlayers && attacker.getFirstPlayerOwner() != null) {
                    this.isDamagedByPlayers = true;
                }
                attacker.addAttackersToSet(this.attackers);
                for (Mob mob : attacker.getAttackOwnerChain()) {
                    if (!this.countStats || !this.countDamageDealt() || !mob.isPlayer) continue;
                    ((PlayerMob)mob).getServerClient().newStats.damage_dealt.increment(damage);
                }
            }
        }
        if (!this.isClient() && this.getHealth() <= 0) {
            this.remove(knockbackX, knockbackY, attacker);
        }
    }

    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker) {
        this.setHealthHidden(health, knockbackX, knockbackY, attacker, false);
    }

    public final void setHealthHidden(int health) {
        this.setHealthHidden(health, 0.0f, 0.0f, null);
    }

    public void setHealth(int health, float knockbackX, float knockbackY, Attacker attacker) {
        if (!this.canTakeDamage()) {
            return;
        }
        this.setHealthHidden(health, knockbackX, knockbackY, attacker);
        if (this.getLevel() != null && this.isServer()) {
            this.sendHealthPacket(false);
        }
    }

    public final void setHealth(int health) {
        this.setHealth(health, 0.0f, 0.0f, null);
    }

    public final void setHealth(int health, Attacker attacker) {
        this.setHealth(health, 0.0f, 0.0f, attacker);
    }

    public void setResilienceHidden(float resilience) {
        if (resilience >= this.getResilience()) {
            this.resilienceGainedTime = this.getTime();
        }
        this.resilience = GameMath.limit(resilience, 0.0f, (float)this.getMaxResilience());
    }

    public void addResilienceHidden(float resilience) {
        if (resilience > 0.0f) {
            this.setResilienceHidden(this.getResilience() + resilience * this.buffManager.getModifier(BuffModifiers.RESILIENCE_GAIN).floatValue());
        } else {
            this.setResilienceHidden(this.getResilience() + resilience);
        }
    }

    public void addResilience(float resilience) {
        this.addResilienceHidden(resilience);
        if (this.getLevel() != null && this.isServer()) {
            this.sendResiliencePacket(false);
        }
    }

    public boolean canGiveResilience(Attacker attacker) {
        return true;
    }

    public boolean canGiveLifeSteal(Attacker attacker) {
        return this.canGiveResilience(attacker);
    }

    public int getStopManaExhaustLimit() {
        return Math.max(this.getMaxMana() / 20, 1);
    }

    public void setManaHidden(float mana, boolean fromNetworkUpdate) {
        float beforeMana = this.getMana();
        int maxMana = this.getMaxMana();
        this.mana = GameMath.limit(mana, 0.0f, (float)maxMana);
        MobManaChangedEvent manaChangedEvent = new MobManaChangedEvent(beforeMana, this.mana, fromNetworkUpdate);
        if (this.getLevel() != null && beforeMana != this.mana) {
            this.getLevel().streamAll(MobManaChangeListenerEntityComponent.class).forEach(listener -> listener.onLevelMobManaChanged(this, beforeMana, this.mana));
        }
        this.buffManager.submitMobEvent(manaChangedEvent);
        int stopExhaustionLimit = this.getStopManaExhaustLimit();
        if (this.mana >= (float)stopExhaustionLimit) {
            this.isManaExhausted = false;
            if (this.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
                this.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
            }
        }
    }

    public void setManaHidden(float mana) {
        this.setManaHidden(mana, false);
    }

    public void setMana(float mana) {
        this.setManaHidden(mana);
        if (this.getLevel() != null && this.isServer()) {
            this.sendManaPacket(false);
        }
    }

    public void useMana(float usedMana, ServerClient except) {
        if (!this.usesMana()) {
            return;
        }
        float modifiedUsedMana = usedMana * this.buffManager.getModifier(BuffModifiers.MANA_USAGE).floatValue();
        if (modifiedUsedMana > 0.0f) {
            this.lastManaSpentTime = this.getTime();
            float finalMana = this.getMana() - modifiedUsedMana;
            this.setManaHidden(finalMana);
            if (finalMana <= 0.0f) {
                this.isManaExhausted = true;
                this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, this, 1000, null), false);
            }
            if (this.getLevel() != null && this.isServer()) {
                if (except != null) {
                    this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketMobUseMana(this), this, except);
                } else {
                    this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobUseMana(this), this);
                }
            }
        }
    }

    public void useLife(int usedLife, ServerClient except, final GameMessage attackerName) {
        this.useLife(usedLife, except, new Attacker(){

            @Override
            public GameMessage getAttackerName() {
                return attackerName;
            }

            @Override
            public DeathMessageTable getDeathMessages() {
                return this.getDeathMessages("life", 3);
            }

            @Override
            public Mob getFirstAttackOwner() {
                return Mob.this;
            }
        });
    }

    public void useLife(int usedLife, ServerClient except, Attacker attacker) {
        if (usedLife <= 0) {
            return;
        }
        this.useLife(this.getHealth() - usedLife, usedLife, except, attacker);
    }

    public void useLife(int currentLife, int usedLife, ServerClient except, Attacker attacker) {
        this.setHealth(currentLife, attacker);
        if (this.getLevel() != null) {
            if (this.isServer()) {
                if (except != null) {
                    this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketMobUseLife(this, usedLife), this, except);
                } else {
                    this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobUseLife(this, usedLife), this);
                }
            } else {
                this.spawnDamageText(usedLife, 12, false);
            }
        }
    }

    @Override
    public final void remove() {
        this.remove(0.0f, 0.0f, null);
    }

    public final void remove(float knockbackX, float knockbackY, Attacker attacker) {
        this.remove(knockbackX, knockbackY, attacker, this.getHealth() <= 0);
    }

    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        if (!this.removed()) {
            super.remove();
            this.hasDied = this.hasDied || isDeath;
            this.buffManager.clearBuffs();
            this.ai.isRemoved();
            if (this.getLevel() != null) {
                if (this.isServer()) {
                    ItemAttackerMob followingAttacker;
                    if (this.isFollowing() && (followingAttacker = this.getFollowingItemAttacker()) != null) {
                        followingAttacker.serverFollowersManager.removeFollower(this, false);
                    }
                    if (isDeath) {
                        this.onDeath(attacker, this.attackers);
                    }
                    this.getLevel().getServer().network.sendToClientsWithEntity(new PacketDeath(this, knockbackX, knockbackY, isDeath), this);
                } else if (isDeath) {
                    this.spawnDeathParticles(knockbackX, knockbackY);
                    this.playHitDeathSound();
                    this.playDeathSound();
                } else {
                    this.spawnRemoveParticles(knockbackX, knockbackY);
                }
            }
            this.dismount();
            this.dismounted();
        }
    }

    @Override
    public void onRemovedFromManager() {
        super.onRemovedFromManager();
        this.removedTime = this.getTime();
    }

    public boolean shouldAddToDeletedLevelReturnedMobs() {
        return false;
    }

    public Iterable<Attacker> getAttackers() {
        return this.attackers;
    }

    public Stream<Attacker> streamAttackers() {
        return this.attackers.stream();
    }

    public boolean isAttacker(Attacker target) {
        return this.attackers.contains(target);
    }

    public boolean isClientPlayerNearby() {
        return this.isClientPlayerNearby(100);
    }

    public boolean isClientPlayerNearby(int tileDistance) {
        if (!this.isClient()) {
            if (this.isServer()) {
                GameLog.err.println("isClientNearby() called on server, should only be used on clients");
            }
            return false;
        }
        PlayerMob player = this.getClient().getPlayer();
        return player != null && player.getLevel() == this.getLevel() && player.getDistance(this) <= (float)(tileDistance * 32);
    }

    public Point getLootDropsPosition(ServerClient privateClient) {
        return new Point(this.getX(), this.getY());
    }

    public Biome getWanderBaseBiome(Level level) {
        return this.spawnTilePosition != null ? level.getBiome(this.spawnTilePosition.x, this.spawnTilePosition.y) : null;
    }

    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.isLiquidTile() || pos.isShore()) {
            int height = pos.level.liquidManager.getHeight(pos.tileX, pos.tileY);
            return -1000 + height;
        }
        if (baseBiome != null && pos.getBiomeID() != baseBiome.getID()) {
            return -500;
        }
        return 0;
    }

    public Point getWanderBaseTile() {
        return this.spawnTilePosition;
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        ServerClient client;
        Mob attackOwner;
        Mob mob = attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        if (attackOwner != null) {
            attackOwner.doHasKilledTarget(new MobWasKilledEvent(this, attacker));
        }
        this.getLevel().onMobDied(this, attacker, attackers);
        this.itemsDropped.clear();
        boolean dropsLoot = this.dropsLoot();
        if (dropsLoot) {
            ArrayList<InventoryItem> drops = this.getLootTable().getNewList(GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this);
            this.getLevel().getExtraMobDrops(this).addItems(drops, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this);
            LootTablePresets.globalMobDrops.addItems(drops, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this);
            GameSeasons.addMobDrops(this, drops, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue());
            Point publicLootPosition = this.getLootDropsPosition(null);
            publicLootPosition.x = this.getLevel().limitLevelXToBounds(publicLootPosition.x, 0, 32);
            publicLootPosition.y = this.getLevel().limitLevelYToBounds(publicLootPosition.y, 0, 32);
            this.getLevel().streamAll(MobLootTableDropsListenerEntityComponent.class).sorted(Comparator.comparingInt(e -> -e.getLevelMobDropsLootPriority())).forEach(e -> e.onLevelMobDropsLoot(this, publicLootPosition, drops));
            MobLootTableDropsEvent dropEvent = new MobLootTableDropsEvent(this, publicLootPosition, drops);
            GameEvents.triggerEvent(dropEvent);
            if (dropEvent.dropPos != null && dropEvent.drops != null) {
                for (InventoryItem item : dropEvent.drops) {
                    ItemPickupEntity entity = item.getPickupEntity(this.getLevel(), dropEvent.dropPos.x, dropEvent.dropPos.y);
                    if (this.isBoss()) {
                        entity.showsLightBeam = true;
                    }
                    this.getLevel().entityManager.pickups.add(entity);
                    this.itemsDropped.add(entity);
                }
            }
        }
        attackers.stream().map(Attacker::getAttackOwner).filter(m -> m != null && m.isPlayer).distinct().forEach(m -> {
            ServerClient client = ((PlayerMob)m).getServerClient();
            if (this.countStats && this.countKillStat()) {
                client.newStats.mob_kills.addKill(this);
                client.forceCombineNewStats();
            }
            JournalChallengeRegistry.handleListeners(client, MobKilledJournalChallengeListener.class, challenge -> challenge.onMobKilled(client, this));
            if (this.isBoss() && client.achievementsLoaded() && this.getTimeSinceStartCombat() < 30000L) {
                client.achievements().TOO_EASY.markCompleted(client);
            }
            if (dropsLoot) {
                ArrayList<InventoryItem> privateDrops = this.getPrivateLootTable().getNewList(GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this, client);
                client.addQuestDrops(privateDrops, this, GameRandom.globalRandom);
                this.getLevel().getExtraPrivateMobDrops(this, client).addItems(privateDrops, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), this, client);
                Point privateLootPosition = this.getLootDropsPosition(client);
                privateLootPosition.x = this.getLevel().limitLevelXToBounds(privateLootPosition.x, 0, 32);
                privateLootPosition.y = this.getLevel().limitLevelYToBounds(privateLootPosition.y, 0, 32);
                this.getLevel().streamAll(MobPrivateLootTableDropsListenerEntityComponent.class).sorted(Comparator.comparingInt(e -> -e.getLevelMobDropsPrivateLootPriority())).forEach(e -> e.onLevelMobPrivateDropsLoot(this, client, privateLootPosition, privateDrops));
                MobPrivateLootTableDropsEvent privateDropEvent = new MobPrivateLootTableDropsEvent(this, client, privateLootPosition, privateDrops);
                GameEvents.triggerEvent(privateDropEvent);
                if (privateDropEvent.dropPos != null && privateDropEvent.drops != null) {
                    for (InventoryItem item : privateDropEvent.drops) {
                        ItemPickupEntity pickupEntity = item.copy(item.getAmount()).getPickupEntity(this.getLevel(), privateDropEvent.dropPos.x, privateDropEvent.dropPos.y);
                        pickupEntity.setReservedAuth(client.authentication);
                        if (this.isBoss()) {
                            pickupEntity.showsLightBeam = true;
                        }
                        this.getLevel().entityManager.pickups.add(pickupEntity);
                    }
                }
            }
            if (this.isBoss()) {
                for (SettlementQuestTier questTier : SettlementQuestTier.questTiers) {
                    questTier.onBossKilled(this, client);
                }
            }
        });
        if (attackOwner != null && this.countStats && attackOwner.isPlayer && (client = ((PlayerMob)attackOwner).getServerClient()) != null && client.achievementsLoaded() && this.isHostile && attacker instanceof BombExplosionEvent) {
            client.achievements().DEMOLITION_EXPERT.markCompleted(client);
        }
    }

    public void onFocussedBySummons(PlayerMob playerMob) {
    }

    public void onSummonReceivedNewFocus(Mob newFocus) {
    }

    @Override
    public void restore() {
        super.restore();
        this.attackers.clear();
        this.isDamagedByPlayers = false;
        this.hasDied = false;
    }

    public boolean hasDied() {
        return this.hasDied;
    }

    public final boolean countKillStat() {
        return MobRegistry.countMobKillStat(this.getID());
    }

    public boolean countDamageDealt() {
        return true;
    }

    public void setDir(int dir) {
        if (this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return;
        }
        this.dir = dir;
    }

    public int getDir() {
        return this.dir;
    }

    public Point getDirVector() {
        switch (this.getDir()) {
            case 0: {
                return new Point(0, -1);
            }
            case 1: {
                return new Point(1, 0);
            }
            case 2: {
                return new Point(0, 1);
            }
        }
        return new Point(-1, 0);
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setMaxResilience(int maxResilience) {
        this.maxResilience = maxResilience;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public void setRegen(float regen) {
        this.regen = regen;
    }

    public void setCombatRegen(float combatRegen) {
        this.combatRegen = combatRegen;
    }

    public void setResilienceDecay(float decay) {
        this.resilienceDecay = decay;
    }

    public void setManaRegen(float manaRegen) {
        this.manaRegen = manaRegen;
    }

    public void setCombatManaRegen(float combatManaRegen) {
        this.combatManaRegen = combatManaRegen;
    }

    public void setKnockbackModifier(float knockbackResistance) {
        this.knockbackModifier = knockbackResistance;
    }

    public float getCritChance() {
        return this.buffManager.getModifier(BuffModifiers.CRIT_CHANCE).floatValue();
    }

    public float getCritDamageModifier() {
        return this.buffManager.getModifier(BuffModifiers.CRIT_DAMAGE).floatValue();
    }

    public float getSpeed() {
        return (this.speed + this.buffManager.getModifier(BuffModifiers.SPEED_FLAT).floatValue()) * this.getSpeedModifier();
    }

    public float getSwimSpeedModifier() {
        float swimMod = 1.0f;
        if (!this.isFlying()) {
            float inLiquid = GameMath.limit(this.inLiquidFloat(), 0.0f, 1.0f);
            swimMod = GameMath.lerp(inLiquid, 1.0f, this.getSwimSpeed());
        }
        return swimMod;
    }

    public float getSpeedModifier() {
        if (this.buffManager.getModifier(BuffModifiers.GROUNDED).booleanValue() || this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return 0.0f;
        }
        float swimMod = this.getSwimSpeedModifier();
        GameTile tile = this.getLevel().getTile(this.getTileX(), this.getTileY());
        GameObject object = this.getLevel().getObject(this.getTileX(), this.getTileY());
        float buffMod = this.buffManager.getAndApplyModifiers(BuffModifiers.SPEED, tile.getSpeedModifier(this), object.getSpeedModifier(this)).floatValue();
        float slowMod = 1.0f - this.buffManager.getAndApplyModifiers(BuffModifiers.SLOW, tile.getSlowModifier(this), object.getSlowModifier(this)).floatValue();
        float attackMod = 1.0f;
        Mob mounted = this.getRider();
        if (mounted != null && mounted.isAttacking) {
            attackMod = mounted.getAttackingMovementModifier();
        } else if (this.isAttacking) {
            attackMod = this.getAttackingMovementModifier();
        }
        return buffMod * attackMod * slowMod * swimMod;
    }

    public float getSwimSpeed() {
        return this.swimSpeed * this.buffManager.getModifier(BuffModifiers.SWIM_SPEED).floatValue();
    }

    public float getFriction() {
        float frictionMod = this.buffManager.getAndApplyModifiers(BuffModifiers.FRICTION, this.getLevel().getTile(this.getTileX(), this.getTileY()).getFrictionModifier(this), this.getLevel().getObject(this.getTileX(), this.getTileY()).getFrictionModifier(this)).floatValue();
        return this.friction * frictionMod;
    }

    public final int getMaxHealthFlat() {
        return this.maxHealth;
    }

    public int getMaxHealth() {
        return Math.max(Math.round((float)(this.getMaxHealthFlat() + this.buffManager.getModifier(BuffModifiers.MAX_HEALTH_FLAT) + this.getMaxHealthUpgrade()) * this.getMaxHealthModifier()), 1);
    }

    public int getMaxHealthUpgrade() {
        return 0;
    }

    public float getMaxHealthModifier() {
        return this.buffManager.getModifier(BuffModifiers.MAX_HEALTH).floatValue();
    }

    public int getHealthUnlimited() {
        return this.health;
    }

    public int getHealth() {
        int maxHealth = this.getMaxHealth();
        if (this.health > maxHealth) {
            this.health = maxHealth;
        } else if (this.health < 0) {
            this.health = 0;
        }
        return this.health;
    }

    public float getHealthPercent() {
        return (float)this.getHealth() / (float)this.getMaxHealth();
    }

    public int getMaxResilienceFlat() {
        return this.maxResilience;
    }

    public float getResilience() {
        if (this.resilience > (float)this.getMaxResilience()) {
            this.resilience = this.getMaxResilience();
        } else if (this.resilience < 0.0f) {
            this.resilience = 0.0f;
        }
        return this.resilience;
    }

    public int getMaxResilience() {
        return (int)((float)this.getMaxResilienceFlat() + (float)this.buffManager.getModifier(BuffModifiers.MAX_RESILIENCE_FLAT).intValue() * this.buffManager.getModifier(BuffModifiers.MAX_RESILIENCE).floatValue());
    }

    public int getMaxManaFlat() {
        return this.maxMana;
    }

    public int getMaxMana() {
        return Math.max((int)((float)(this.getMaxManaFlat() + this.buffManager.getModifier(BuffModifiers.MAX_MANA_FLAT)) * this.buffManager.getModifier(BuffModifiers.MAX_MANA).floatValue()), 1);
    }

    public float getMissingMana() {
        return (float)this.getMaxMana() - this.getMana();
    }

    public float getMana() {
        int maxMana = this.getMaxMana();
        if (this.mana > (float)maxMana) {
            this.mana = maxMana;
        } else if (this.mana < 0.0f) {
            this.mana = 0.0f;
        }
        return this.mana;
    }

    public int getArmorFlat() {
        return this.armor;
    }

    public float getArmor() {
        return (float)(this.getArmorFlat() + this.buffManager.getModifier(BuffModifiers.ARMOR_FLAT)) * this.getArmorModifier();
    }

    public float getArmorModifier() {
        return this.buffManager.getModifier(BuffModifiers.ARMOR).floatValue();
    }

    public float getArmorAfterPen(float armorPen) {
        return Math.max(0.0f, this.getArmor() - armorPen);
    }

    public float getRegenFlat() {
        return this.regen;
    }

    public float getRegen() {
        return (this.getRegenFlat() + this.buffManager.getModifier(BuffModifiers.HEALTH_REGEN_FLAT).floatValue()) * this.buffManager.getModifier(BuffModifiers.HEALTH_REGEN).floatValue();
    }

    public float getCombatRegenFlat() {
        return this.combatRegen;
    }

    public float getCombatRegen() {
        return (this.getCombatRegenFlat() + this.buffManager.getModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT).floatValue()) * this.buffManager.getModifier(BuffModifiers.COMBAT_HEALTH_REGEN).floatValue();
    }

    public float getResilienceDecay() {
        return (this.resilienceDecay + this.buffManager.getModifier(BuffModifiers.RESILIENCE_DECAY_FLAT).floatValue()) * this.buffManager.getModifier(BuffModifiers.RESILIENCE_DECAY).floatValue();
    }

    public float getResilienceRegen() {
        return this.buffManager.getModifier(BuffModifiers.RESILIENCE_REGEN_FLAT).floatValue() * this.buffManager.getModifier(BuffModifiers.RESILIENCE_REGEN).floatValue();
    }

    public float getManaRegenFlat() {
        return this.manaRegen;
    }

    public float getManaRegen() {
        return (this.getManaRegenFlat() + this.buffManager.getModifier(BuffModifiers.MANA_REGEN_FLAT).floatValue()) * this.buffManager.getModifier(BuffModifiers.MANA_REGEN).floatValue();
    }

    public float getCombatManaRegenFlat() {
        return this.combatManaRegen;
    }

    public float getCombatManaRegen() {
        return (this.getCombatManaRegenFlat() + this.buffManager.getModifier(BuffModifiers.COMBAT_MANA_REGEN_FLAT).floatValue()) * this.buffManager.getModifier(BuffModifiers.COMBAT_MANA_REGEN).floatValue();
    }

    public float getPoisonTotal() {
        return this.buffManager.getModifier(BuffModifiers.POISON_DAMAGE_FLAT).floatValue() * this.buffManager.getModifier(BuffModifiers.POISON_DAMAGE).floatValue();
    }

    public float getFireDamageTotal() {
        return this.buffManager.getModifier(BuffModifiers.FIRE_DAMAGE_FLAT).floatValue() * this.buffManager.getModifier(BuffModifiers.FIRE_DAMAGE).floatValue();
    }

    public float getFrostDamageTotal() {
        return this.buffManager.getModifier(BuffModifiers.FROST_DAMAGE_FLAT).floatValue() * this.buffManager.getModifier(BuffModifiers.FROST_DAMAGE).floatValue();
    }

    public float getAccelerationModifier() {
        return this.accelerationMod * this.buffManager.getModifier(BuffModifiers.ACCELERATION).floatValue();
    }

    public float getDecelerationModifier() {
        return this.decelerationMod * this.buffManager.getModifier(BuffModifiers.DECELERATION).floatValue();
    }

    public float getKnockbackModifier() {
        return Math.max(0.0f, this.knockbackModifier * this.buffManager.getModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD).floatValue());
    }

    public float getAttackingMovementModifier() {
        return 0.5f + 0.5f * Math.abs(this.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD).floatValue() - 1.0f);
    }

    public float getIncomingDamageModifier() {
        return this.buffManager.getModifier(BuffModifiers.INCOMING_DAMAGE_MOD).floatValue();
    }

    public float getOutgoingDamageModifier() {
        return this.buffManager.getModifier(BuffModifiers.ALL_DAMAGE).floatValue();
    }

    public float getFinalOutgoingDamageModifier() {
        return 1.0f;
    }

    public boolean isWaterWalking() {
        Mob m;
        if (!this.overrideMountedWaterWalking && (m = this.getRider()) != null) {
            return m.isWaterWalking();
        }
        return this.buffManager.getModifier(BuffModifiers.WATER_WALKING);
    }

    public boolean isVisible() {
        return true;
    }

    public final Point getAnimSprite() {
        return this.getAnimSprite(this.getX(), this.getY(), this.getDir());
    }

    public Point getAnimSprite(int x, int y, int dir) {
        Point p = new Point(0, dir);
        p.x = this.inLiquid(x, y) ? 5 : (Math.abs(this.dx) <= 0.01f && Math.abs(this.dy) <= 0.01f ? 0 : (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 4 + 1);
        return p;
    }

    public final Point getSpriteOffset(Point sprite) {
        return this.getSpriteOffset(sprite.x, sprite.y);
    }

    public Point getSpriteOffset(int spriteX, int spriteY) {
        Point p = new Point(0, 0);
        if (spriteY == 1 || spriteY == 3) {
            p.y = 2;
        }
        p.x += this.getRiderDrawXOffset();
        p.y += this.getRiderDrawYOffset();
        return p;
    }

    public int getRiderDir(int startDir) {
        return startDir;
    }

    public MobSpawnItem getSpawnItem() {
        return MobRegistry.getMobSpawnItemID(this.getID());
    }

    public boolean canLevelInteract() {
        return !this.isStatic;
    }

    public boolean canTakeDamage() {
        return !this.isStatic;
    }

    public boolean usesMana() {
        return false;
    }

    public boolean shouldDrawRider() {
        return true;
    }

    public boolean forceFollowRiderLevelChange(Mob rider) {
        return false;
    }

    public int getRiderDrawXOffset() {
        return 0;
    }

    public int getRiderDrawYOffset() {
        return -32;
    }

    public MaskShaderOptions getRiderMaskOptions(int x, int y) {
        GameTexture riderMask = this.getRiderMask();
        Point spriteOffset = this.getSpriteOffset(this.getAnimSprite(x, y, this.getDir()));
        if (riderMask != null) {
            int maskXOffset = this.getRiderMaskXOffset();
            int maskYOffset = this.getRiderMaskYOffset();
            return new MaskShaderOptions(riderMask, spriteOffset.x, spriteOffset.y, maskXOffset, maskYOffset);
        }
        return new MaskShaderOptions(spriteOffset.x, spriteOffset.y);
    }

    public GameTexture getRiderMask() {
        return null;
    }

    public int getRiderMaskXOffset() {
        return 0;
    }

    public int getRiderMaskYOffset() {
        return 0;
    }

    public int getRiderArmSpriteX() {
        return 1;
    }

    public int getRiderSpriteX() {
        return 6;
    }

    @Override
    public boolean shouldSave() {
        return this.shouldSave;
    }

    public boolean isBoss() {
        return MobRegistry.isBossMob(this.getID());
    }

    public boolean isSecondaryIncursionBoss() {
        return this.isSecondaryIncursionBoss;
    }

    public boolean isLavaImmune() {
        return this.isBoss();
    }

    public boolean isSlimeImmune() {
        return this.isLavaImmune();
    }

    public boolean isOnGenericCooldown(String key) {
        return this.getGenericCooldownLeft(key) > 0L;
    }

    public void startGenericCooldown(String key, long cooldownMillis) {
        this.genericCooldowns.put(key, this.getTime() + cooldownMillis);
    }

    public long getGenericCooldownLeft(String key) {
        return this.genericCooldowns.getOrDefault(key, 0L) - this.getTime();
    }

    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "interacttip");
    }

    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug && !this.isVisible()) {
            return false;
        }
        ListGameTooltips tips = new ListGameTooltips();
        this.addHoverTooltips(tips, debug);
        if (debug) {
            if (!WindowManager.getWindow().isKeyDown(340)) {
                this.addDebugTooltips(tips);
            } else {
                List modTips = this.buffManager.getModifierTooltips().stream().map(mf -> mf.toTooltip(true)).collect(Collectors.toList());
                if (modTips.isEmpty()) {
                    tips.add(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW));
                } else {
                    tips.addAll(modTips);
                }
            }
        }
        if (this.canInteract(perspective)) {
            String controlMsg;
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            if (Settings.showControlTips && (controlMsg = this.getInteractTip(perspective, debug)) != null) {
                tips.add(new InputTooltip(Control.MOUSE2, controlMsg, this.inInteractRange(perspective) ? 1.0f : 0.7f));
            }
        }
        GameTooltipManager.addTooltip(tips, TooltipLocation.INTERACT_FOCUS);
        return true;
    }

    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        if (!this.canTakeDamage() || this.getMaxHealth() <= 1) {
            tooltips.add(this.getDisplayName());
        } else {
            tooltips.add(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
        }
    }

    protected void addDebugTooltips(ListGameTooltips tooltips) {
        tooltips.add("Name: " + this.getDisplayName());
        tooltips.add("UniqueID: " + this.getRealUniqueID());
        tooltips.add("Health: " + this.getHealth() + "/" + this.getMaxHealth());
        tooltips.add("Mana: " + this.getMana() + "/" + this.getMaxMana());
        tooltips.add("Resilience: " + this.getResilience() + "/" + this.getMaxResilience());
        tooltips.add("Armor: " + this.getArmor() + " (" + this.getArmorFlat() + ")");
        tooltips.add("Pos/dir: " + this.getX() + ", " + this.getY() + " / " + this.getDir());
        tooltips.add("Speed: " + this.getCurrentSpeed());
        tooltips.add("Movement: " + this.currentMovement + ", " + this.hasArrivedAtTarget);
        tooltips.add("Team: " + this.getTeam());
        if (this.spawnTilePosition != null) {
            int deltaX = this.getTileX() - this.spawnTilePosition.x;
            int deltaY = this.getTileY() - this.spawnTilePosition.y;
            tooltips.add("Spawn tile delta: " + deltaX + "x" + deltaY + " (" + this.spawnTilePosition.x + "x" + this.spawnTilePosition.y + ")");
        }
        tooltips.add("Can despawn: " + this.canDespawn);
        for (ActiveBuff ab : this.buffManager.getBuffs().values()) {
            ab.addDebugTooltips(tooltips);
        }
    }

    public String toString() {
        return super.toString() + "{" + this.getUniqueID() + ", " + this.getHostString() + ", " + this.getDisplayName() + "}";
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset, Mob oldMob) {
        this.setPos(this.x + (float)positionOffset.x, this.y + (float)positionOffset.y, true);
    }
}

