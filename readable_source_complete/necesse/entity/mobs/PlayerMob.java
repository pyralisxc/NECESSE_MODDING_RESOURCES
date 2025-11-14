/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import necesse.engine.AreaFinder;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.journal.listeners.FoodConsumedJournalChallengeListener;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketActiveMountAbility;
import necesse.engine.network.packet.PacketActiveMountAbilityStopped;
import necesse.engine.network.packet.PacketActiveSetBuffAbility;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityStopped;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketCreativePlayerSettings;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketObjectInteract;
import necesse.engine.network.packet.PacketPlayerAction;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerAttack;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.packet.PacketPlayerDropItem;
import necesse.engine.network.packet.PacketPlayerHunger;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerItemInteract;
import necesse.engine.network.packet.PacketPlayerItemMobInteract;
import necesse.engine.network.packet.PacketPlayerMobInteract;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketPlayerUseMount;
import necesse.engine.network.packet.PacketRequestActiveMountAbility;
import necesse.engine.network.packet.PacketRequestActiveSetBuffAbility;
import necesse.engine.network.packet.PacketRequestActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketShowItemLevelInteract;
import necesse.engine.network.packet.PacketShowItemMobInteract;
import necesse.engine.network.packet.PacketSpawnCreativeItem;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.packet.PacketSwapInventorySlots;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldSettings;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.explosionEvent.TNTExplosionEvent;
import necesse.entity.mobs.ActiveMountAbility;
import necesse.entity.mobs.ActiveMountAbilityContainer;
import necesse.entity.mobs.ActiveTrinketBuff;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HealthUpgradeManager;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.MountAbility;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.OpenedDoor;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerInventoryItemAttackSlot;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.ActiveBuffAbilityContainer;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.PlayerSprite;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.forms.components.FormExpressionWheel;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ControllerInteractTarget;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.TorchItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.hudManager.floatText.DamageText;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class PlayerMob
extends ItemAttackerMob
implements FishingMob,
HungerMob,
AmmoUserMob,
ObjectUserMob {
    public static int distanceToRunAtFullHunger = 57600;
    public static int secondsToPassAtFullHunger = 10800;
    public static float attacksHungerModifier = 1.0f;
    public static int spawnInvincibilityFrameMilliseconds = 2000;
    public HumanLook look;
    public boolean constantAttack;
    public boolean constantInteract;
    public PlayerInventorySlot attackSlot;
    public final HashMap<Integer, Long> toolHits = new HashMap();
    private boolean firstPress;
    private PlayerInventoryManager inv;
    private boolean inventoryExtended;
    private boolean creativeMenuExtended = true;
    private int selectedSlot;
    private boolean inventoryActionUpdate;
    private boolean maxManaReached;
    private long timeOfMaxMana;
    public HealthUpgradeManager healthUpgradeManager = new HealthUpgradeManager();
    public boolean hasInvincibility;
    protected boolean hasGodModeInCreative = true;
    public Point currentAttackLastPlacePosition;
    protected int lastMaxFoodBuffsModifier;
    public EquipmentBuffManager equipmentBuffManager = new EquipmentBuffManager(this){

        @Override
        public InventoryItem getArmorItem(int slot) {
            return PlayerMob.this.getInv().equipment.getSelectedArmorSlot(slot).getItem();
        }

        @Override
        public InventoryItem getCosmeticItem(int slot) {
            return PlayerMob.this.getInv().equipment.getSelectedCosmeticSlot(slot).getItem();
        }

        @Override
        public ArrayList<InventoryItem> getTrinketItems() {
            PlayerInventoryManager inv = PlayerMob.this.getInv();
            ArrayList<InventoryItem> trinketItems = new ArrayList<InventoryItem>(inv.equipment.getTrinketSlotsSize() + 1);
            trinketItems.add(inv.equipment.getSelectedEquipmentSlot(1).getItem());
            for (int i = 0; i < inv.equipment.getTrinketSlotsSize(); ++i) {
                trinketItems.add(inv.equipment.getSelectedTrinketsSlot(i).getItem());
            }
            return trinketItems;
        }
    };
    private ActiveBuffAbilityContainer activeSetBuffAbility;
    private ActiveBuffAbilityContainer activeTrinketBuffAbility;
    private ActiveMountAbilityContainer activeMountAbility;
    private final NetworkClient networkClient;
    public String playerName;
    public float hungerLevel = 1.3f;
    protected double lastDistanceRan;
    public boolean autoOpenDoors = true;
    public boolean hotbarLocked = false;
    private boolean smartMiningTogglePressed = false;
    private float lastControllerAimX;
    private float lastControllerAimY;
    private int refreshRemainingSpawnTime;
    private long spawnInvincibilityEndTime;
    protected ObjectUserActive objectUser;
    protected FormExpressionWheel.Expression activeExpression;
    protected long activeExpressionEndTime;
    private static final int angleSnap = 5;
    public static Attacker STARVING_ATTACKER = new Attacker(){

        @Override
        public GameMessage getAttackerName() {
            return new LocalMessage("deaths", "starvationname");
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.getDeathMessages("starvation", 2);
        }

        @Override
        public Mob getFirstAttackOwner() {
            return null;
        }
    };

    public PlayerMob(long tempNameIdentifier, NetworkClient networkClient) {
        super(100);
        this.playerName = "player" + tempNameIdentifier;
        this.networkClient = networkClient;
        if (networkClient != null) {
            this.setTeam(networkClient.getTeamID());
        }
        this.look = new HumanLook();
        this.resetInv();
        this.setDir(2);
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.hitCooldown = 500;
        this.attackTime = 0L;
        this.hitTime = 0L;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("inventoryExtended", this.inventoryExtended);
        save.addBoolean("creativeMenuExtended", this.creativeMenuExtended);
        save.addBoolean("autoOpenDoors", this.autoOpenDoors);
        save.addBoolean("hotbarLocked", this.hotbarLocked);
        save.addFloat("hungerLevel", this.hungerLevel);
        if (this.hasInvincibility) {
            save.addBoolean("hasInvincibility", this.hasInvincibility);
        }
        save.addBoolean("hasGodModeInCreative", this.hasGodModeInCreative);
        this.healthUpgradeManager.addSaveData(save, "HEALTH_UPGRADES");
        SaveData lookData = new SaveData("LOOK");
        this.look.addSaveData(lookData);
        save.addSaveData(lookData);
        SaveData invData = new SaveData("INVENTORY");
        this.getInv().addSaveData(invData);
        save.addSaveData(invData);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventoryExtended = save.getBoolean("inventoryExtended", this.inventoryExtended, false);
        this.creativeMenuExtended = save.getBoolean("creativeMenuExtended", this.creativeMenuExtended, false);
        this.autoOpenDoors = save.getBoolean("autoOpenDoors", this.autoOpenDoors, false);
        this.hotbarLocked = save.getBoolean("hotbarLocked", this.hotbarLocked, false);
        this.hungerLevel = save.getFloat("hungerLevel", 1.0f, 0.0f, Float.MAX_VALUE, false);
        this.hasInvincibility = save.getBoolean("hasInvincibility", this.hasInvincibility, false);
        this.hasGodModeInCreative = save.getBoolean("hasGodModeInCreative", this.hasGodModeInCreative, false);
        this.healthUpgradeManager.readLoadData(save, "HEALTH_UPGRADES", this);
        LoadData lookData = save.getFirstLoadDataByName("LOOK");
        if (lookData != null) {
            this.look.applyLoadData(lookData);
        } else {
            GameLog.warn.println("Could not load player look data");
        }
        LoadData inventory = save.getFirstLoadDataByName("INVENTORY");
        if (inventory != null) {
            this.getInv().applyLoadData(inventory);
        } else {
            GameLog.warn.println("Could not load player inventory data");
        }
        this.getInv().clean();
        this.openedDoors.clear();
    }

    public void addLoadedCharacterSaveData(SaveData save) {
        save.addInt("maxHealth", this.getMaxHealthFlat());
        save.addInt("health", this.getHealth());
        save.addInt("maxResilience", this.getMaxResilienceFlat());
        save.addFloat("resilience", this.getResilience());
        save.addInt("maxMana", this.getMaxManaFlat());
        save.addFloat("mana", this.getMana());
        save.addBoolean("inventoryExtended", this.inventoryExtended);
        save.addBoolean("creativeMenuExtended", this.creativeMenuExtended);
        save.addBoolean("autoOpenDoors", this.autoOpenDoors);
        save.addBoolean("hotbarLocked", this.hotbarLocked);
        save.addFloat("hungerLevel", this.hungerLevel);
        save.addBoolean("hasGodModeInCreative", this.hasGodModeInCreative);
        this.healthUpgradeManager.addSaveData(save, "HEALTH_UPGRADES");
        SaveData buffs = new SaveData("BUFFS");
        this.buffManager.addSaveData(buffs);
        save.addSaveData(buffs);
        SaveData lookData = new SaveData("LOOK");
        this.look.addSaveData(lookData);
        save.addSaveData(lookData);
        SaveData invData = new SaveData("INVENTORY");
        this.getInv().addSaveData(invData);
        save.addSaveData(invData);
    }

    public void applyLoadedCharacterLoadData(LoadData save) {
        LoadData lookData;
        this.setMaxHealth(save.getInt("maxHealth", this.getMaxHealthFlat()));
        this.loadedHealth = save.getInt("health", this.getMaxHealthFlat(), false);
        this.setHealthHidden(this.loadedHealth);
        this.setMaxResilience(save.getInt("maxResilience", this.getMaxResilienceFlat()));
        this.loadedResilience = save.getFloat("resilience", 0.0f, false);
        this.setResilienceHidden(this.loadedResilience);
        this.setMaxMana(save.getInt("maxMana", this.getMaxManaFlat()));
        this.loadedMana = save.getFloat("mana", this.getMaxManaFlat(), false);
        this.setManaHidden(this.loadedMana);
        this.inventoryExtended = save.getBoolean("inventoryExtended", this.inventoryExtended, false);
        this.creativeMenuExtended = save.getBoolean("creativeMenuExtended", this.creativeMenuExtended, false);
        this.autoOpenDoors = save.getBoolean("autoOpenDoors", this.autoOpenDoors, false);
        this.hotbarLocked = save.getBoolean("hotbarLocked", this.hotbarLocked, false);
        this.hungerLevel = save.getFloat("hungerLevel", 1.0f, 0.0f, Float.MAX_VALUE, false);
        this.hasGodModeInCreative = save.getBoolean("hasGodModeInCreative", this.hasGodModeInCreative, false);
        this.healthUpgradeManager.readLoadData(save, "HEALTH_UPGRADES", this);
        LoadData buffs = save.getFirstLoadDataByName("BUFFS");
        if (buffs != null) {
            this.buffManager.applyLoadData(buffs);
        }
        if ((lookData = save.getFirstLoadDataByName("LOOK")) != null) {
            this.look.applyLoadData(lookData);
        } else {
            GameLog.warn.println("Could not load player look data");
        }
        LoadData inventory = save.getFirstLoadDataByName("INVENTORY");
        if (inventory != null) {
            this.getInv().applyLoadData(inventory);
        } else {
            GameLog.warn.println("Could not load player inventory data");
        }
        this.getInv().clean();
        this.equipmentBuffManager.updateAll();
    }

    public void setupLoadedCharacterPacket(PacketWriter writer) {
        writer.putNextInt(this.getMaxHealthFlat());
        writer.putNextInt(Math.max(this.getHealth(), this.loadedHealth));
        writer.putNextInt(this.getMaxResilienceFlat());
        writer.putNextFloat(Math.max(this.getResilience(), this.loadedResilience));
        writer.putNextInt(this.getMaxManaFlat());
        writer.putNextFloat(Math.max(this.getMana(), this.loadedMana));
        this.buffManager.setupContentPacket(writer);
        writer.putNextByteUnsigned(this.selectedSlot);
        writer.putNextBoolean(this.inventoryExtended);
        writer.putNextBoolean(this.autoOpenDoors);
        writer.putNextBoolean(this.hotbarLocked);
        this.look.setupContentPacket(writer, true);
        this.getInv().setupContentPacket(writer);
        writer.putNextFloat(this.hungerLevel);
        writer.putNextBoolean(this.hasGodModeInCreative);
        this.healthUpgradeManager.writePacket(writer);
    }

    public void applyLoadedCharacterPacket(PacketReader reader) {
        this.setMaxHealth(reader.getNextInt());
        this.loadedHealth = reader.getNextInt();
        this.setHealthHidden(this.loadedHealth);
        this.setMaxResilience(reader.getNextInt());
        this.loadedResilience = reader.getNextFloat();
        this.setResilienceHidden(this.loadedResilience);
        this.setMaxMana(reader.getNextInt());
        this.loadedMana = reader.getNextFloat();
        this.setManaHidden(this.loadedMana);
        this.buffManager.applyContentPacket(reader);
        this.selectedSlot = reader.getNextByteUnsigned();
        this.inventoryExtended = reader.getNextBoolean();
        this.autoOpenDoors = reader.getNextBoolean();
        this.hotbarLocked = reader.getNextBoolean();
        this.look.applyContentPacket(reader);
        this.getInv().applyContentPacket(reader);
        this.hungerLevel = reader.getNextFloat();
        this.hasGodModeInCreative = reader.getNextBoolean();
        this.healthUpgradeManager.readPacket(reader);
        this.equipmentBuffManager.updateAll();
        this.buffManager.forceUpdateBuffs();
        this.handleLoadedValues();
    }

    public double allowServerMovement(Server server, ServerClient client, float x, float y) {
        return this.allowServerMovement(server, client, x, y, this.dx, this.dy);
    }

    public double allowServerMovement(Server server, ServerClient client, float x, float y, float dx, float dy) {
        if (this.getWorldSettings().creativeMode) {
            return -10000.0;
        }
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel() && server.world.settings.cheatsAllowedOrHidden()) {
            return -100000.0;
        }
        if (!Settings.strictServerAuthority) {
            return -10000.0;
        }
        Point2D.Float lastPos = new Point2D.Float(this.x, this.y);
        Point2D.Float lastDelta = new Point2D.Float(this.dx, this.dy);
        double combinedTolerance = lastPos.distance(x, y) + lastDelta.distance(dx, dy);
        return combinedTolerance - (double)Server.clientMoveTolerance;
    }

    public void setupPlayerMovementPacket(PacketWriter writer) {
        this.writeObjectUserPacket(writer);
        writer.putNextMaxValue(this.getDir(), 3);
        writer.putNextByteUnsigned(this.selectedSlot);
        this.getInv().equipment.writeSelectedSet(writer);
        writer.putNextFloat(this.moveX);
        writer.putNextFloat(this.moveY);
        writer.putNextBoolean(this.autoOpenDoors);
        writer.putNextBoolean(this.hotbarLocked);
        writer.putNextBoolean(this.activeSetBuffAbility != null);
        if (this.activeSetBuffAbility != null) {
            writer.putNextInt(this.activeSetBuffAbility.uniqueID);
        }
        writer.putNextBoolean(this.activeTrinketBuffAbility != null);
        if (this.activeTrinketBuffAbility != null) {
            writer.putNextInt(this.activeTrinketBuffAbility.uniqueID);
        }
        writer.putNextBoolean(this.activeMountAbility != null);
        if (this.activeMountAbility != null) {
            writer.putNextInt(this.activeMountAbility.uniqueID);
        }
    }

    public void applyPlayerMovementPacket(PacketPlayerMovement packet, PacketReader reader) {
        ClientClient clientClient;
        Mob mount;
        boolean usageChanges = this.readObjectUserPacket(reader);
        if (packet != null && !usageChanges) {
            this.setPos(packet.x, packet.y, packet.isDirect);
            this.dx = packet.dx;
            this.dy = packet.dy;
        }
        this.refreshClientUpdateTime();
        int nextDir = reader.getNextMaxValue(3);
        if (this.isAttacking) {
            this.beforeAttackDir = nextDir;
        } else {
            this.setDir(nextDir);
        }
        this.selectedSlot = reader.getNextByteUnsigned();
        this.getInv().equipment.readSelectedSet(reader);
        this.moveX = reader.getNextFloat();
        this.moveY = reader.getNextFloat();
        if (this.isRiding() && (mount = this.getMount()) != null) {
            if (!this.isAttacking) {
                mount.setDir(nextDir);
            }
            mount.setPos(this.x, this.y, true);
            mount.moveX = this.moveX;
            mount.moveY = this.moveY;
        }
        this.autoOpenDoors = reader.getNextBoolean();
        this.hotbarLocked = reader.getNextBoolean();
        if (reader.getNextBoolean()) {
            int abilityUniqueID = reader.getNextInt();
            if ((this.activeSetBuffAbility == null || this.activeSetBuffAbility.uniqueID != abilityUniqueID) && this.isClientClient()) {
                clientClient = this.getClientClient();
                clientClient.getClient().network.sendPacket(new PacketRequestActiveSetBuffAbility(clientClient.slot));
            }
        } else {
            if (this.activeSetBuffAbility != null) {
                this.activeSetBuffAbility.onStopped(this);
            }
            this.activeSetBuffAbility = null;
        }
        if (reader.getNextBoolean()) {
            int abilityUniqueID = reader.getNextInt();
            if ((this.activeTrinketBuffAbility == null || this.activeTrinketBuffAbility.uniqueID != abilityUniqueID) && this.isClientClient()) {
                clientClient = this.getClientClient();
                clientClient.getClient().network.sendPacket(new PacketRequestActiveTrinketBuffAbility(clientClient.slot));
            }
        } else {
            if (this.activeTrinketBuffAbility != null) {
                this.activeTrinketBuffAbility.onStopped(this);
            }
            this.activeTrinketBuffAbility = null;
        }
        if (reader.getNextBoolean()) {
            int abilityUniqueID = reader.getNextInt();
            if ((this.activeMountAbility == null || this.activeMountAbility.uniqueID != abilityUniqueID) && this.isClientClient()) {
                clientClient = this.getClientClient();
                clientClient.getClient().network.sendPacket(new PacketRequestActiveMountAbility(clientClient.slot));
            }
        } else {
            if (this.activeMountAbility != null) {
                this.activeMountAbility.onStopped(this);
            }
            this.activeMountAbility = null;
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.inventoryExtended = reader.getNextBoolean();
        this.creativeMenuExtended = reader.getNextBoolean();
        this.selectedSlot = reader.getNextByteUnsigned();
        this.look.applyContentPacket(reader);
        this.getInv().applyContentPacket(reader);
        this.hungerLevel = reader.getNextFloat();
        this.hasGodModeInCreative = reader.getNextBoolean();
        this.healthUpgradeManager.readPacket(reader);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.inventoryExtended);
        writer.putNextBoolean(this.creativeMenuExtended);
        writer.putNextByteUnsigned(this.selectedSlot);
        this.look.setupContentPacket(writer, true);
        this.getInv().setupContentPacket(writer);
        writer.putNextFloat(this.hungerLevel);
        writer.putNextBoolean(this.hasGodModeInCreative);
        this.healthUpgradeManager.writePacket(writer);
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        this.setupPlayerMovementPacket(writer);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.applyPlayerMovementPacket(null, reader);
    }

    @Override
    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        super.setupHealthPacket(writer, isFull);
        if (this.isServerClient()) {
            writer.putNextBoolean(this.getServerClient().isDead());
        } else if (this.isClientClient()) {
            writer.putNextBoolean(this.getClientClient().isDead());
        } else {
            writer.putNextBoolean(this.removed());
        }
    }

    @Override
    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        super.applyHealthPacket(reader, isFull);
        boolean isDead = reader.getNextBoolean();
        if (this.isClientClient()) {
            ClientClient client = this.getClientClient();
            if (isDead != client.isDead()) {
                if (isDead) {
                    client.die(5000);
                } else {
                    this.restore();
                }
            }
        } else if (this.isServerClient()) {
            ServerClient client = this.getServerClient();
            if (isDead != client.isDead()) {
                if (isDead) {
                    client.die(5000);
                } else {
                    this.restore();
                }
            }
        } else if (isDead != this.removed()) {
            if (isDead) {
                this.remove();
            } else {
                this.restore();
            }
        }
    }

    public void applyInventoryPacket(PacketPlayerInventory packet) {
        this.refreshClientUpdateTime();
        if (this.getInv() == null) {
            return;
        }
        this.getInv().applyContentPacket(new PacketReader(packet.inventoryContent));
    }

    public void applyAppearancePacket(PacketPlayerAppearance packet) {
        this.refreshClientUpdateTime();
        this.look = new HumanLook(packet.look);
        this.inv.giveLookArmor(false);
        this.playerName = packet.name;
    }

    public void setupCreativeSettingsPacket(PacketWriter writer) {
        writer.putNextBoolean(this.hasGodModeInCreative);
    }

    public void applyCreativeSettingsPacket(PacketReader packetReader) {
        this.refreshClientUpdateTime();
        this.hasGodModeInCreative = packetReader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onLevelChanged() {
        super.onLevelChanged();
        if (this.objectUser != null) {
            this.objectUser.stopUsing(false, 0.0f, 0.0f);
        }
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        CollisionFilter collisionFilter = this.hasGodMode() ? new CollisionFilter() : super.getLevelCollisionFilter();
        return collisionFilter.addEmptyTiles();
    }

    @Override
    public void tickMovement(float delta) {
        if (this.refreshRemainingSpawnTime > 0) {
            this.spawnInvincibilityEndTime = this.getWorldEntity().getLocalTime() + (long)this.refreshRemainingSpawnTime;
            this.refreshRemainingSpawnTime = 0;
        }
        if (this.attackHandler != null && this.isClient() && !(this.attackHandler.isFromInteract() ? Control.MOUSE2 : Control.MOUSE1).isDown()) {
            this.attackHandler.clientEndAttack();
        }
        if (this.isAttacking || this.onCooldown) {
            this.getAttackAnimProgress();
        }
        super.tickMovement(delta);
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        Level level = this.getLevel();
        if (level == null) {
            return null;
        }
        if (this.autoOpenDoors) {
            return level.regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return level.regionManager.CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS;
    }

    @Override
    public void serverTick() {
        int maxFoodBuffs;
        InventoryItem selectedItem;
        if (this.hasInvincibility && !this.buffManager.hasBuff(BuffRegistry.DEBUG_INVINCIBILITY)) {
            this.buffManager.addBuff(new ActiveBuff(BuffRegistry.DEBUG_INVINCIBILITY, (Mob)this, 1.0f, null), false);
        }
        this.refreshCreativeModeBuff();
        if (this.objectUser != null) {
            if (this.isAccelerating() && this.objectUser.accelerationCancelsUse()) {
                Point2D.Float dir = GameMath.normalize(this.moveX, this.moveY);
                this.objectUser.stopUsing(true, dir.x, dir.y);
            } else {
                this.objectUser.keepUsing();
                this.objectUser.tick();
            }
        }
        this.getInv().tick();
        this.tickHunger();
        this.buffManager.serverTick();
        this.serverFollowersManager.serverTick();
        this.tickRegen();
        this.tickLevel();
        if (this.activeSetBuffAbility != null && !this.activeSetBuffAbility.tick(this)) {
            this.activeSetBuffAbility.onStopped(this);
            this.activeSetBuffAbility = null;
        }
        if (this.activeTrinketBuffAbility != null && !this.activeTrinketBuffAbility.tick(this)) {
            this.activeTrinketBuffAbility.onStopped(this);
            this.activeTrinketBuffAbility = null;
        }
        if (this.activeMountAbility != null && !this.activeMountAbility.tick(this)) {
            this.activeMountAbility.onStopped(this);
            this.activeMountAbility = null;
        }
        if ((selectedItem = this.getSelectedItem()) != null) {
            selectedItem.item.tickHolding(selectedItem, this);
        }
        if (this.healthUpdateTime + (long)this.healthUpdateCooldown < this.getTime()) {
            this.sendHealthPacket(false);
        }
        if (this.resilienceUpdateTime + (long)this.resilienceUpdateCooldown < this.getTime()) {
            this.sendResiliencePacket(false);
        }
        if (this.manaUpdateTime + (long)this.manaUpdateCooldown < this.getTime()) {
            this.sendManaPacket(false);
        }
        if (this.isManaExhausted) {
            this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, (Mob)this, 1000, null), false);
            if (this.isManaExhausted) {
                this.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, (Mob)this, 1000, null), false);
            }
        }
        if ((maxFoodBuffs = this.buffManager.getModifier(BuffModifiers.MAX_FOOD_BUFFS).intValue()) < this.lastMaxFoodBuffsModifier) {
            FoodConsumableItem.clearFoodBuffsAboveCount(this, maxFoodBuffs);
        }
        this.lastMaxFoodBuffsModifier = maxFoodBuffs;
    }

    @Override
    public void clientTick() {
        InventoryItem selectedItem;
        Client client;
        if (this.objectUser != null) {
            this.objectUser.tick();
        }
        this.getInv().tick();
        this.tickHunger();
        this.equipmentBuffManager.clientTickEffects();
        super.clientTick();
        this.refreshCreativeModeBuff();
        if (this.activeSetBuffAbility != null && !this.activeSetBuffAbility.tick(this)) {
            if (this.activeSetBuffAbility.isRunningClient) {
                client = this.getLevel().getClient();
                client.network.sendPacket(new PacketActiveSetBuffAbilityStopped(client.getSlot(), this.activeSetBuffAbility.uniqueID));
            }
            this.activeSetBuffAbility.onStopped(this);
            this.activeSetBuffAbility = null;
        }
        if (this.activeTrinketBuffAbility != null && !this.activeTrinketBuffAbility.tick(this)) {
            if (this.activeTrinketBuffAbility.isRunningClient) {
                client = this.getLevel().getClient();
                client.network.sendPacket(new PacketActiveTrinketBuffAbilityStopped(client.getSlot(), this.activeTrinketBuffAbility.uniqueID));
            }
            this.activeTrinketBuffAbility.onStopped(this);
            this.activeTrinketBuffAbility = null;
        }
        if (this.activeMountAbility != null && !this.activeMountAbility.tick(this)) {
            if (this.activeMountAbility.isRunningClient) {
                client = this.getLevel().getClient();
                client.network.sendPacket(new PacketActiveMountAbilityStopped(client.getSlot(), this.activeMountAbility.uniqueID));
            }
            this.activeMountAbility.onStopped(this);
            this.activeMountAbility = null;
        }
        if ((selectedItem = this.getSelectedItem()) != null) {
            selectedItem.item.tickHolding(selectedItem, this);
        }
    }

    public void tickSync() {
        this.getInv().tickSync();
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        ServerClient client;
        super.doWasHitLogic(event);
        if (this.isServerClient() && (client = this.getServerClient()).achievementsLoaded() && !client.achievements().SAFETY_LAST.isCompleted() && event.damage > 0 && event.attacker instanceof TNTExplosionEvent) {
            client.achievements().SAFETY_LAST.markCompleted(client);
        }
    }

    public int getRemainingSpawnInvincibilityTime() {
        if (this.refreshRemainingSpawnTime > 0) {
            return this.refreshRemainingSpawnTime;
        }
        WorldEntity worldEntity = this.getWorldEntity();
        if (worldEntity != null) {
            return (int)Math.max(this.spawnInvincibilityEndTime - worldEntity.getLocalTime(), 0L);
        }
        return 0;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        int remainingSpawnInvincibilityTime = this.getRemainingSpawnInvincibilityTime();
        if (remainingSpawnInvincibilityTime > 0) {
            return false;
        }
        return super.canBeHit(attacker);
    }

    public void refreshSpawnTime(int remainingInvincibilityTime) {
        this.refreshRemainingSpawnTime = remainingInvincibilityTime;
    }

    public void refreshSpawnTime() {
        this.refreshSpawnTime(spawnInvincibilityFrameMilliseconds);
    }

    public float getInvincibilityFrameAlpha() {
        int remainingTime = this.getRemainingSpawnInvincibilityTime();
        if (remainingTime > 0) {
            if (remainingTime > spawnInvincibilityFrameMilliseconds) {
                float perc = GameUtils.getAnimFloat(remainingTime - spawnInvincibilityFrameMilliseconds, 200);
                return (float)(Math.sin((double)perc * Math.PI * 2.0) + 1.0) / 2.0f;
            }
            float perc = Math.abs((float)remainingTime / (float)spawnInvincibilityFrameMilliseconds - 1.0f);
            return (float)Math.abs((Math.cos(Math.PI * 6 / (double)(perc + 0.5f)) + 1.0) / 2.0);
        }
        return 1.0f;
    }

    @Override
    public NetworkClient getPvPOwner() {
        return this.networkClient;
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        if (!super.canBeTargeted(attacker, attackerClient)) {
            return false;
        }
        if (attackerClient != null) {
            NetworkClient networkClient = this.getNetworkClient();
            if (attackerClient == networkClient) {
                return false;
            }
            if (networkClient != null) {
                return networkClient.pvpEnabled() && attackerClient.pvpEnabled();
            }
        }
        return true;
    }

    @Override
    protected boolean switchDoor(LevelObject lo) {
        if (!this.isClient() || this.getLevel().getClient().getPlayer() != this) {
            return false;
        }
        OpenedDoor last = this.openedDoors.get(lo.tileX, lo.tileY);
        if (last != null) {
            last.mobX = this.getX();
            last.mobY = this.getY();
            return true;
        }
        PathDoorOption pathDoorOption = this.getPathDoorOption();
        if (pathDoorOption != null && pathDoorOption.canOpen(lo.tileX, lo.tileY)) {
            AtomicBoolean out = new AtomicBoolean();
            GameEvents.triggerEvent(new ObjectInteractEvent(this.getLevel(), lo.tileX, lo.tileY, this), e -> {
                if (PlayerMob.staticSwitchDoor(lo, this)) {
                    this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), lo.tileX, lo.tileY));
                    this.openedDoors.add(lo.tileX, lo.tileY, this.getX(), this.getY(), lo.object.isSwitched);
                    out.set(true);
                }
            });
            return out.get();
        }
        return false;
    }

    @Override
    protected void switchDoor(OpenedDoor od) {
        GameEvents.triggerEvent(new ObjectInteractEvent(this.getLevel(), od.tileX, od.tileY, this), e -> {
            LevelObject lo = new LevelObject(this.getLevel(), od.tileX, od.tileY);
            if (PlayerMob.staticSwitchDoor(lo, this)) {
                this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), lo.tileX, lo.tileY));
            }
        });
    }

    public void tickControls(MainGame mainGame, boolean isGameTick, GameCamera camera) {
        int i;
        Packet content;
        ObjectValue<ActiveBuff, SetBonusBuff> setBonusBuff;
        boolean paralyzed = this.buffManager.getModifier(BuffModifiers.PARALYZED);
        if (Control.INVENTORY.isPressed()) {
            if (mainGame.showMap()) {
                mainGame.setShowMap(false);
            } else if (mainGame.formManager.hasFocusForm() && mainGame.formManager.getFocusComp().inventoryHotkeyClosesContainer()) {
                mainGame.getClient().closeContainer(true);
            } else {
                this.setInventoryExtended(!this.isInventoryExtended());
            }
            mainGame.updateMenuLayerActive();
        }
        if (Control.USE_MOUNT.isPressed() && this.objectUser == null) {
            InventoryItem item = this.inv.equipment.getSelectedEquipmentSlot(0).getItem();
            if (item != null && item.item.isMountItem()) {
                MountItem mountItem = (MountItem)item.item;
                String canUseMountError = mountItem.canUseMount(item, this, this.getLevel());
                if (canUseMountError == null) {
                    this.getLevel().getClient().network.sendPacket(new PacketPlayerUseMount(this.getPlayerSlot(), this, mountItem));
                } else if (!canUseMountError.isEmpty()) {
                    UniqueFloatText text = new UniqueFloatText(this.getX(), this.getY() - 20, canUseMountError, new FontOptions(16).outline().color(new Color(200, 100, 100)), "mountfail"){

                        @Override
                        public int getAnchorX() {
                            return PlayerMob.this.getX();
                        }

                        @Override
                        public int getAnchorY() {
                            return PlayerMob.this.getY() - 20;
                        }
                    };
                    this.getLevel().hudManager.addElement(text);
                }
            } else {
                Mob mount = this.getMount();
                if (mount != null) {
                    this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(this.getLevel().getClient().getSlot(), mount.getUniqueID()));
                }
            }
        }
        if (!paralyzed && Control.SET_ABILITY.isPressed() && (setBonusBuff = this.equipmentBuffManager.getSetBonusBuff()) != null) {
            Object buffAbility;
            if (setBonusBuff.value instanceof BuffAbility) {
                buffAbility = (BuffAbility)setBonusBuff.value;
                content = buffAbility.getAbilityContent(this, (ActiveBuff)setBonusBuff.object, camera);
                if (buffAbility.canRunAbility(this, (ActiveBuff)setBonusBuff.object, content)) {
                    buffAbility.runAndSendAbility(this.getLevel().getClient(), this, (ActiveBuff)setBonusBuff.object, content);
                }
                return;
            }
            if (setBonusBuff.value instanceof ActiveBuffAbility) {
                buffAbility = (ActiveBuffAbility)setBonusBuff.value;
                ActiveBuff buff = (ActiveBuff)setBonusBuff.object;
                Packet content2 = buffAbility.getStartAbilityContent(this, buff, camera);
                if (buffAbility.canRunAbility(this, buff, content2)) {
                    if (this.activeSetBuffAbility != null) {
                        this.activeSetBuffAbility.onStopped(this);
                        this.activeSetBuffAbility = null;
                    }
                    buffAbility.onActiveAbilityStarted(this, buff, content2);
                    int uniqueID = GameRandom.globalRandom.nextInt();
                    this.activeSetBuffAbility = new ActiveBuffAbilityContainer(uniqueID, true, (ActiveBuffAbility)buffAbility, buff);
                    Client client = this.getLevel().getClient();
                    client.network.sendPacket(new PacketActiveSetBuffAbility(client.getSlot(), buff.buff, uniqueID, content2));
                }
                return;
            }
        }
        if (!paralyzed && Control.TRINKET_ABILITY.isPressed()) {
            Object mountAbility;
            Mob mount = this.getMount();
            if (mount instanceof MountAbility) {
                mountAbility = (MountAbility)((Object)mount);
                content = mountAbility.getMountAbilityContent(this, camera);
                if (mountAbility.canRunMountAbility(this, content)) {
                    mountAbility.runAndSendMountAbility(this.getLevel().getClient(), this, content);
                }
                return;
            }
            if (mount instanceof ActiveMountAbility) {
                mountAbility = (ActiveMountAbility)((Object)mount);
                content = mountAbility.getStartMountAbilityContent(this, camera);
                if (mountAbility.canRunMountAbility(this, content)) {
                    if (this.activeMountAbility != null) {
                        this.activeMountAbility.onStopped(this);
                        this.activeMountAbility = null;
                    }
                    if (this.activeTrinketBuffAbility != null) {
                        this.activeTrinketBuffAbility.onStopped(this);
                        this.activeTrinketBuffAbility = null;
                    }
                    mountAbility.onActiveMountAbilityStarted(this, content);
                    int uniqueID = GameRandom.globalRandom.nextInt();
                    this.activeMountAbility = new ActiveMountAbilityContainer(uniqueID, true, (ActiveMountAbility)mountAbility, mount);
                    Client client = this.getLevel().getClient();
                    client.network.sendPacket(new PacketActiveMountAbility(client.getSlot(), mount, uniqueID, content));
                }
                return;
            }
            for (ActiveTrinketBuff trinketBuffs : this.equipmentBuffManager.getTrinketBuffs()) {
                for (int i2 = 0; i2 < trinketBuffs.buffs.length; ++i2) {
                    ActiveBuff buff;
                    Object buffAbility;
                    if (trinketBuffs.buffs[i2] instanceof BuffAbility) {
                        buffAbility = (BuffAbility)((Object)trinketBuffs.buffs[i2]);
                        buff = trinketBuffs.activeBuffs[i2];
                        if (buff == null) continue;
                        Packet content3 = buffAbility.getAbilityContent(this, buff, camera);
                        if (buffAbility.canRunAbility(this, buff, content3)) {
                            buffAbility.runAndSendAbility(this.getLevel().getClient(), this, buff, content3);
                        }
                        return;
                    }
                    if (!(trinketBuffs.buffs[i2] instanceof ActiveBuffAbility)) continue;
                    buffAbility = (ActiveBuffAbility)((Object)trinketBuffs.buffs[i2]);
                    buff = trinketBuffs.activeBuffs[i2];
                    if (buff == null) continue;
                    Packet content4 = buffAbility.getStartAbilityContent(this, buff, camera);
                    if (buffAbility.canRunAbility(this, buff, content4)) {
                        if (this.activeMountAbility != null) {
                            this.activeMountAbility.onStopped(this);
                            this.activeMountAbility = null;
                        }
                        if (this.activeTrinketBuffAbility != null) {
                            this.activeTrinketBuffAbility.onStopped(this);
                            this.activeTrinketBuffAbility = null;
                        }
                        buffAbility.onActiveAbilityStarted(this, buff, content4);
                        int uniqueID = GameRandom.globalRandom.nextInt();
                        this.activeTrinketBuffAbility = new ActiveBuffAbilityContainer(uniqueID, true, (ActiveBuffAbility)buffAbility, buff);
                        Client client = this.getLevel().getClient();
                        client.network.sendPacket(new PacketActiveTrinketBuffAbility(client.getSlot(), buff.buff, uniqueID, content4));
                    }
                    return;
                }
            }
            return;
        }
        if (!paralyzed && Control.HEALTH_POT.isPressed()) {
            int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
            this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_HEALTH_POTION, seed));
            this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_HEALTH_POTION, seed);
            return;
        }
        if (!paralyzed && Control.MANA_POT.isPressed()) {
            int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
            this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_MANA_POTION, seed));
            this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_MANA_POTION, seed);
            return;
        }
        if (!paralyzed && Control.EAT_FOOD.isPressed()) {
            int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
            this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.EAT_FOOD, seed));
            this.runPlayerAction(PacketPlayerAction.PlayerAction.EAT_FOOD, seed);
            return;
        }
        if (!paralyzed && Control.BUFF_POTS.isPressed()) {
            int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
            this.getLevel().getClient().network.sendPacket(new PacketPlayerAction(PacketPlayerAction.PlayerAction.USE_BUFF_POTION, seed));
            this.runPlayerAction(PacketPlayerAction.PlayerAction.USE_BUFF_POTION, seed);
            return;
        }
        if (!paralyzed && Control.PLACE_TORCH.isPressed()) {
            for (int i3 = 0; i3 < this.getInv().main.getSize(); ++i3) {
                int centerTileY;
                int centerTileX;
                Point levelPos;
                final InventoryItem item = this.getInv().main.getItem(i3);
                if (item == null || !(item.item instanceof TorchItem)) continue;
                if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                    Point2D.Float aimDir = this.getControllerAimDir();
                    levelPos = item.item.getControllerAttackLevelPos(this.getLevel(), aimDir.x, aimDir.y, this, item);
                } else {
                    levelPos = new Point(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
                }
                final TorchItem torchItem = (TorchItem)((Object)item.item);
                final Level level = this.getLevel();
                if (torchItem.canPlaceTorch(level, levelPos.x, levelPos.y, item, this)) {
                    this.tryAttack(new PlayerInventorySlot(this.getInv().main.getInventoryID(), i3), levelPos.x, levelPos.y);
                    return;
                }
                int placeRange = torchItem.getTorchPlaceRange(level, item, this);
                if (placeRange <= 0) continue;
                double distance = levelPos.distance(this.x, this.y);
                Point2D.Float placeDir = GameMath.normalize((float)levelPos.x - this.x, (float)levelPos.y - this.y);
                final float placeAngle = GameMath.getAngle(placeDir);
                final Point2D.Float angleTestPos = new Point2D.Float(this.x - placeDir.x * 64.0f, this.y - placeDir.y * 64.0f);
                if (distance >= (double)placeRange) {
                    centerTileX = GameMath.getTileCoordinate(this.x + placeDir.x * (float)placeRange);
                    centerTileY = GameMath.getTileCoordinate(this.y + placeDir.y * (float)placeRange);
                } else {
                    centerTileX = GameMath.getTileCoordinate(levelPos.x);
                    centerTileY = GameMath.getTileCoordinate(levelPos.y);
                }
                int tileRange = (int)Math.ceil((float)placeRange / 32.0f) + 1;
                AreaFinder areaFinder = new AreaFinder(centerTileX, centerTileY, tileRange, true){

                    @Override
                    public boolean checkPoint(int x, int y) {
                        int levelX = x * 32 + 16;
                        int levelY = y * 32 + 16;
                        Point2D.Float dir = GameMath.normalize((float)levelX - angleTestPos.x, (float)levelY - angleTestPos.y);
                        float angle = GameMath.getAngle(dir);
                        float angleDifference = GameMath.getAngleDifference(placeAngle, angle);
                        if (Math.abs(angleDifference) > 45.0f) {
                            return false;
                        }
                        return torchItem.canPlaceTorch(level, levelX, levelY, item, PlayerMob.this);
                    }
                };
                areaFinder.runFinder();
                if (!areaFinder.hasFound()) continue;
                Point placeTile = areaFinder.getFirstFind();
                this.tryAttack(new PlayerInventorySlot(this.getInv().main.getInventoryID(), i3), placeTile.x * 32 + 16, placeTile.y * 32 + 16);
                return;
            }
        }
        if (Control.ERASER.isPressed() && this.getWorldSettings().creativeMode) {
            boolean toHotbar = !this.inventoryExtended || Input.lastInputIsController;
            Item eraser = ItemRegistry.getItem("eraser");
            if (toHotbar) {
                this.swapToItem(eraser, true);
            } else {
                PacketSpawnCreativeItem.runAndSendAction(this.getClient(), new InventoryItem(eraser), PacketSpawnCreativeItem.Destination.DragSlot, false, true);
            }
            return;
        }
        if (Control.PIPETTE.isPressed()) {
            this.pipetteAt(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
            return;
        }
        if (Control.SMART_MINING.isPressed() && !mainGame.formManager.isMouseOver(Control.SMART_MINING.getEvent())) {
            this.smartMiningTogglePressed = true;
        } else if (Control.SMART_MINING.isReleased()) {
            if (this.smartMiningTogglePressed && !mainGame.formManager.isMouseOver(Control.SMART_MINING.getEvent())) {
                Settings.smartMining = !Settings.smartMining;
                Settings.saveClientSettings();
                Color color = Settings.smartMining ? new Color(100, 200, 100) : new Color(200, 100, 100);
                UniqueFloatText text = new UniqueFloatText(this.getX(), this.getY() - 20, Localization.translate("misc", Settings.smartMining ? "smartminingon" : "smartminingoff"), new FontOptions(16).outline().color(color), "smartmining"){

                    @Override
                    public int getAnchorX() {
                        return PlayerMob.this.getX();
                    }

                    @Override
                    public int getAnchorY() {
                        return PlayerMob.this.getY() - 20;
                    }
                };
                text.riseTime = 500;
                text.fadeTime = 500;
                text.expandTime = 50;
                this.getLevel().hudManager.addElement(text);
                SoundManager.playSound(GameResources.tick, SoundEffect.ui());
            }
            this.smartMiningTogglePressed = false;
        }
        if (!(!ControllerInput.ATTACK.isJustPressed() || ControllerInput.isCursorVisible() && mainGame.getFormManager().isMouseOver())) {
            if (ControllerInput.isCursorVisible()) {
                this.tryAttack(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
            } else {
                this.tryControllerAttack();
            }
        }
        if (ControllerInput.INTERACT.isJustPressed()) {
            if (ControllerInput.isCursorVisible()) {
                this.runClientInteract(camera.getMouseLevelPosX(), camera.getMouseLevelPosY(), false);
            } else if (this.attackHandler != null && !this.attackHandler.isFromInteract()) {
                this.attackHandler.onControllerInteracted(ControllerInput.getAimX(), ControllerInput.getAimY());
                this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractController(ControllerInput.getAimX(), ControllerInput.getAimY()));
            } else {
                ControllerInteractTarget target = this.getControllerInteractTarget(false, this.getCurrentAttackHeight(), camera);
                if (target != null) {
                    target.runInteract();
                }
            }
        }
        if (Control.NEXT_HOTBAR.isPressed()) {
            this.setSelectedSlot((this.selectedSlot + 1) % 10);
        }
        if (Control.PREV_HOTBAR.isPressed()) {
            this.setSelectedSlot((this.selectedSlot + 9) % 10);
        }
        for (i = 0; i < Control.HOTBAR_SLOTS.length; ++i) {
            if (!Control.HOTBAR_SLOTS[i].isPressed()) continue;
            this.setSelectedSlot(i);
        }
        for (i = 0; i < Control.ITEM_SETS.length; ++i) {
            if (!Control.ITEM_SETS[i].isPressed()) continue;
            this.getInv().equipment.setSelectedSet(i);
            Client client = this.getClient();
            if (client == null) continue;
            client.sendMovementPacket(false);
        }
        if (isGameTick) {
            Mob mount;
            boolean reset;
            double distance;
            boolean grounded = this.buffManager.getModifier(BuffModifiers.GROUNDED);
            boolean canMove = !paralyzed && !grounded;
            float lastMoveX = this.moveX;
            float lastMoveY = this.moveY;
            int dir = this.getDir();
            this.moveX = 0.0f;
            this.moveY = 0.0f;
            if (canMove && (ControllerInput.MOVE.hasChanged() || ControllerInput.MOVE.getX() != 0.0f || ControllerInput.MOVE.getY() != 0.0f)) {
                double distance2 = new Point2D.Float().distance(ControllerInput.MOVE.getX(), ControllerInput.MOVE.getY());
                if (distance2 >= 0.7) {
                    this.moveX = ControllerInput.MOVE.getX();
                    this.moveY = ControllerInput.MOVE.getY();
                    this.lastControllerAimX = this.moveX;
                    this.lastControllerAimY = this.moveY;
                    this.snapMovementAngle();
                } else if (distance2 >= (double)0.4f && !this.isAttacking) {
                    this.setFacingDir(ControllerInput.MOVE.getX(), ControllerInput.MOVE.getY());
                    this.lastControllerAimX = ControllerInput.MOVE.getX();
                    this.lastControllerAimY = ControllerInput.MOVE.getY();
                }
            }
            if ((ControllerInput.AIM.hasChanged() || ControllerInput.AIM.getX() != 0.0f || ControllerInput.AIM.getY() != 0.0f) && (distance = new Point2D.Float().distance(ControllerInput.AIM.getX(), ControllerInput.AIM.getY())) >= (double)0.4f) {
                this.lastControllerAimX = ControllerInput.AIM.getX();
                this.lastControllerAimY = ControllerInput.AIM.getY();
            }
            if (canMove && Control.MOVE_TO_MOUSE.isDown()) {
                int yDelta;
                int xDelta = camera.getMouseLevelPosX() - this.getX();
                if (Math.abs(xDelta) > 4) {
                    this.moveX = xDelta;
                }
                if (Math.abs(yDelta = camera.getMouseLevelPosY() - this.getY()) > 4) {
                    this.moveY = yDelta;
                }
                if (this.moveX != 0.0f || this.moveY != 0.0f) {
                    this.snapMovementAngle();
                }
            }
            boolean bl = reset = this.dx == 0.0f && this.dy == 0.0f;
            if (canMove && Control.MOVE_LEFT.isDown()) {
                reset = false;
                if (dir == 3) {
                    this.firstPress = false;
                }
                if (!this.firstPress) {
                    this.moveX = -1.0f;
                } else {
                    if (!this.isAttacking) {
                        this.setDir(3);
                    }
                    this.firstPress = false;
                }
            }
            if (canMove && Control.MOVE_RIGHT.isDown()) {
                reset = false;
                if (dir == 1) {
                    this.firstPress = false;
                }
                if (!this.firstPress) {
                    this.moveX = 1.0f;
                } else {
                    if (!this.isAttacking) {
                        this.setDir(1);
                    }
                    this.firstPress = false;
                }
            }
            if (canMove && Control.MOVE_UP.isDown()) {
                reset = false;
                if (dir == 0) {
                    this.firstPress = false;
                }
                if (!this.firstPress) {
                    this.moveY = -1.0f;
                } else {
                    if (!this.isAttacking) {
                        this.setDir(0);
                    }
                    this.firstPress = false;
                }
            }
            if (canMove && Control.MOVE_DOWN.isDown()) {
                reset = false;
                if (dir == 2) {
                    this.firstPress = false;
                }
                if (!this.firstPress) {
                    this.moveY = 1.0f;
                } else {
                    if (!this.isAttacking) {
                        this.setDir(2);
                    }
                    this.firstPress = false;
                }
            }
            if (reset) {
                this.firstPress = true;
            }
            if (this.isRiding() && (mount = this.getMount()) != null) {
                if (!this.isAttacking) {
                    mount.setDir(dir);
                }
                mount.setPos(this.x, this.y, true);
                mount.moveX = this.moveX;
                mount.moveY = this.moveY;
            }
            if (lastMoveX != this.moveX || lastMoveY != this.moveY || dir != this.getDir()) {
                this.getLevel().getClient().sendMovementPacket(false);
            }
        }
    }

    public boolean pipetteAt(int x, int y) {
        Optional<Mob> mobToFind;
        boolean isCreative = this.getWorldSettings().creativeMode;
        PlaceableItem itemToLookFor = null;
        if (isCreative && (mobToFind = this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(x, y, 1).filter(mob -> !mob.isPlayer && mob.getSelectBox().contains(x, y)).findFirst()).isPresent()) {
            itemToLookFor = mobToFind.get().getSpawnItem();
        }
        if (itemToLookFor == null) {
            Point levelPos = new Point(x, y);
            Level level = this.getLevel();
            GameTile tile = level.getTile(GameMath.getTileCoordinate(levelPos.x), GameMath.getTileCoordinate(levelPos.y));
            TileItem tileItem = tile.getTileItem();
            GameObject object = level.getObject(GameMath.getTileCoordinate(levelPos.x), GameMath.getTileCoordinate(levelPos.y));
            ObjectItem objectItem = object.getObjectItem();
            PlaceableItem placeableItem = itemToLookFor = object.getID() == 0 ? tileItem : objectItem;
        }
        if (itemToLookFor != null) {
            if (this.getDraggingItem() != null && isCreative && ItemRegistry.isValidCreativeItem(itemToLookFor)) {
                int index = this.getClient().getInventoryContainer().getClientDraggingSlot().getContainerIndex();
                ContainerActionResult result = this.getClient().getContainer().applyContainerAction(index, ContainerAction.QUICK_TRASH);
                this.getClient().network.sendPacket(new PacketContainerAction(index, ContainerAction.QUICK_TRASH, result.value));
            }
            if (this.isCreativeMenuExtended() && this.inventoryExtended) {
                PacketSpawnCreativeItem.runAndSendAction(this.getClient(), new InventoryItem(itemToLookFor), PacketSpawnCreativeItem.Destination.DragSlot, false, false);
                return true;
            }
            this.swapToItem(itemToLookFor, true);
        }
        return false;
    }

    public boolean swapToItem(Item item, boolean spawnItemInCreative) {
        InventoryItem invItem;
        int i;
        boolean shouldSpawnItemInCreative;
        boolean hasEmptySlot = false;
        boolean bl = shouldSpawnItemInCreative = spawnItemInCreative && this.getWorldSettings().creativeMode && ItemRegistry.isValidCreativeItem(item);
        if (shouldSpawnItemInCreative) {
            for (i = 0; i < this.getInv().main.getSize(); ++i) {
                invItem = this.getInv().main.getItem(i);
                if (invItem == null) {
                    hasEmptySlot = true;
                    continue;
                }
                if (invItem.item.getID() != item.getID()) continue;
                shouldSpawnItemInCreative = false;
                break;
            }
        }
        if (shouldSpawnItemInCreative) {
            if (!hasEmptySlot) {
                Container inventoryContainer = this.getClient().getInventoryContainer();
                int index = inventoryContainer.CLIENT_HOTBAR_START + this.getSelectedSlot();
                ContainerActionResult result = this.getClient().getContainer().applyContainerAction(index, ContainerAction.QUICK_TRASH);
                this.getClient().network.sendPacket(new PacketContainerAction(index, ContainerAction.QUICK_TRASH, result.value));
            }
            PacketSpawnCreativeItem.runAndSendAction(this.getClient(), new InventoryItem(item), PacketSpawnCreativeItem.Destination.Hotbar, false, false);
            return true;
        }
        for (i = 0; i < this.getInv().main.getSize(); ++i) {
            invItem = this.getInv().main.getItem(i);
            if (invItem == null || invItem.item.getID() != item.getID()) continue;
            if (i < 10) {
                this.setSelectedSlot(i);
                return true;
            }
            int selectedSlot = this.getSelectedSlot();
            if (this.getInv().main.isSlotClear(selectedSlot)) {
                this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, i, selectedSlot));
                this.getInv().main.swapItems(i, selectedSlot);
                return true;
            }
            int nonLockedSlot = -1;
            for (int j = 0; j < 10; ++j) {
                if (this.getInv().main.isSlotClear(j)) {
                    this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, i, j));
                    this.getInv().main.swapItems(i, j);
                    this.setSelectedSlot(j);
                    return true;
                }
                if (nonLockedSlot != -1 || this.getInv().main.isItemLocked(j)) continue;
                nonLockedSlot = j;
            }
            if (nonLockedSlot == -1) break;
            this.getLevel().getClient().network.sendPacket(new PacketSwapInventorySlots(this, i, nonLockedSlot));
            this.getInv().main.swapItems(i, nonLockedSlot);
            this.setSelectedSlot(nonLockedSlot);
            return true;
        }
        return false;
    }

    public void runActiveSetBuffAbility(int buffID, int uniqueID, Packet content) {
        ActiveBuff buff = this.buffManager.getBuff(buffID);
        if (buff != null && buff.buff instanceof ActiveBuffAbility) {
            if (this.activeSetBuffAbility != null) {
                this.activeSetBuffAbility.onStopped(this);
                this.activeSetBuffAbility = null;
            }
            ActiveBuffAbility buffAbility = (ActiveBuffAbility)((Object)buff.buff);
            buffAbility.onActiveAbilityStarted(this, buff, content);
            this.activeSetBuffAbility = new ActiveBuffAbilityContainer(uniqueID, false, buffAbility, buff);
        }
    }

    public void runActiveTrinketBuffAbility(int buffID, int uniqueID, Packet content) {
        ActiveBuff buff = this.buffManager.getBuff(buffID);
        if (buff != null && buff.buff instanceof ActiveBuffAbility) {
            if (this.activeMountAbility != null) {
                this.activeMountAbility.onStopped(this);
                this.activeMountAbility = null;
            }
            if (this.activeTrinketBuffAbility != null) {
                this.activeTrinketBuffAbility.onStopped(this);
                this.activeTrinketBuffAbility = null;
            }
            ActiveBuffAbility buffAbility = (ActiveBuffAbility)((Object)buff.buff);
            buffAbility.onActiveAbilityStarted(this, buff, content);
            this.activeTrinketBuffAbility = new ActiveBuffAbilityContainer(uniqueID, false, buffAbility, buff);
        }
    }

    public void runActiveMountAbility(int mountUniqueID, int uniqueID, Packet content) {
        Mob mount = this.getMount();
        if (mount != null && mount.getUniqueID() == mountUniqueID && mount instanceof ActiveMountAbility) {
            if (this.activeMountAbility != null) {
                this.activeMountAbility.onStopped(this);
                this.activeMountAbility = null;
            }
            if (this.activeTrinketBuffAbility != null) {
                this.activeTrinketBuffAbility.onStopped(this);
                this.activeTrinketBuffAbility = null;
            }
            ActiveMountAbility mountAbility = (ActiveMountAbility)((Object)mount);
            mountAbility.onActiveMountAbilityStarted(this, content);
            this.activeMountAbility = new ActiveMountAbilityContainer(uniqueID, false, mountAbility, mount);
        }
    }

    public boolean runActiveSetBuffAbilityUpdate(int uniqueID, Packet content) {
        if (this.activeSetBuffAbility != null && this.activeSetBuffAbility.uniqueID == uniqueID) {
            this.activeSetBuffAbility.update(this, content);
            return true;
        }
        return false;
    }

    public boolean runActiveTrinketBuffAbilityUpdate(int uniqueID, Packet content) {
        if (this.activeTrinketBuffAbility != null && this.activeTrinketBuffAbility.uniqueID == uniqueID) {
            this.activeTrinketBuffAbility.update(this, content);
            return true;
        }
        return false;
    }

    public boolean runActiveMountAbilityUpdate(int uniqueID, Packet content) {
        if (this.activeMountAbility != null && this.activeMountAbility.uniqueID == uniqueID) {
            this.activeMountAbility.update(this, content);
            return true;
        }
        return false;
    }

    public void sendActiveSetBuffAbilityState(Server server, ServerClient target) {
        ServerClient me = this.getServerClient();
        if (this.activeSetBuffAbility != null) {
            Packet content = this.activeSetBuffAbility.buffAbility.getRunningAbilityContent(this, this.activeSetBuffAbility.activeBuff);
            target.sendPacket(new PacketActiveSetBuffAbility(me.slot, this.activeSetBuffAbility.activeBuff.buff, this.activeSetBuffAbility.uniqueID, content));
        } else {
            target.sendPacket(new PacketActiveSetBuffAbilityStopped(me.slot, 0));
        }
    }

    public void sendActiveTrinketBuffAbilityState(Server server, ServerClient target) {
        ServerClient me = this.getServerClient();
        if (this.activeTrinketBuffAbility != null) {
            Packet content = this.activeTrinketBuffAbility.buffAbility.getRunningAbilityContent(this, this.activeTrinketBuffAbility.activeBuff);
            target.sendPacket(new PacketActiveTrinketBuffAbility(me.slot, this.activeTrinketBuffAbility.activeBuff.buff, this.activeTrinketBuffAbility.uniqueID, content));
        } else {
            target.sendPacket(new PacketActiveTrinketBuffAbilityStopped(me.slot, 0));
        }
    }

    public void sendActiveMountAbilityState(Server server, ServerClient target) {
        ServerClient me = this.getServerClient();
        if (this.activeMountAbility != null) {
            Packet content = this.activeMountAbility.mountAbility.getRunningMountAbilityContent(this);
            target.sendPacket(new PacketActiveMountAbility(me.slot, this.activeMountAbility.mount, this.activeMountAbility.uniqueID, content));
        } else {
            target.sendPacket(new PacketActiveMountAbilityStopped(me.slot, 0));
        }
    }

    public void onActiveSetBuffAbilityStopped(int uniqueID) {
        if (this.activeSetBuffAbility != null && (uniqueID == 0 || this.activeSetBuffAbility.uniqueID == uniqueID)) {
            this.activeSetBuffAbility.onStopped(this);
            this.activeSetBuffAbility = null;
        }
    }

    public void onActiveTrinketBuffAbilityStopped(int uniqueID) {
        if (this.activeTrinketBuffAbility != null && (uniqueID == 0 || this.activeTrinketBuffAbility.uniqueID == uniqueID)) {
            this.activeTrinketBuffAbility.onStopped(this);
            this.activeTrinketBuffAbility = null;
        }
    }

    public void onActiveMountAbilityStopped(int uniqueID) {
        if (this.activeMountAbility != null && (uniqueID == 0 || this.activeMountAbility.uniqueID == uniqueID)) {
            this.activeMountAbility.onStopped(this);
            this.activeMountAbility = null;
        }
    }

    private void snapMovementAngle() {
        int angle = (int)Math.toDegrees(Math.atan2(this.moveY, this.moveX));
        angle = angle / 5 * 5;
        Point2D.Float dir = GameMath.getAngleDir(angle);
        this.moveX = GameMath.toDecimals(dir.x, 2);
        this.moveY = GameMath.toDecimals(dir.y, 2);
    }

    @Override
    public void tickCurrentMovement(float delta) {
    }

    @Override
    public boolean isVisible() {
        NetworkClient client = this.getNetworkClient();
        if (client != null && !client.hasSpawned()) {
            return false;
        }
        return super.isVisible();
    }

    @Override
    public boolean canBePushed(Mob other) {
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && objectUser.preventsUserPushed()) {
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
        return super.getSelectBox(x, y);
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
    public DrawOptions getUserDrawOptions(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, Consumer<HumanDrawOptions> humanDrawOptionsModifier) {
        GameLight light = level.getLightLevel(PlayerMob.getTileCoordinate(x), PlayerMob.getTileCoordinate(y));
        return PlayerSprite.getDrawOptions(this, x, y, light, camera, humanDrawOptionsModifier);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(PlayerMob.getTileCoordinate(x), PlayerMob.getTileCoordinate(y));
        DrawOptionsList debug = new DrawOptionsList();
        if (Debug.isActive() && this.getLevel() != null) {
            int debugX = this.getX() - camera.getX() - 20;
            int debugY = this.getY() - camera.getY();
            FontOptions fontOptions = new FontOptions(16);
            debug.add(new StringDrawOptions(fontOptions, x + ", " + this.dx).pos(debugX, debugY));
            debug.add(new StringDrawOptions(fontOptions, y + ", " + this.dy).pos(debugX, debugY + 15));
            debug.add(new StringDrawOptions(fontOptions, this.getFriction() + ", " + this.getSpeed() + ", " + this.getCurrentSpeed() + ", " + this.getDistanceRan()).pos(debugX, debugY + 30));
            debug.add(new StringDrawOptions(fontOptions, "Team: " + this.getTeam()).pos(debugX, debugY + 45));
            debug.add(new StringDrawOptions(fontOptions, "Mount: " + this.mount).pos(debugX, debugY + 60));
            debug.add(new StringDrawOptions(fontOptions, "Boomerangs: " + this.getBoomerangsUsage() + " - " + GameUtils.join(this.boomerangs.stream().map(p -> p.getStringID() + " (" + p.getUniqueID() + ")").toArray(), ", ")).pos(debugX, debugY + 75));
            debug.add(new StringDrawOptions(fontOptions, "Dir: " + this.getDir() + ", " + this.beforeAttackDir).pos(debugX, debugY + 90));
        }
        topList.add(tm -> debug.draw());
        if (this.objectUser == null || this.objectUser.drawsUser()) {
            final DrawOptions options = PlayerSprite.getDrawOptions(this, x, y, light, camera, null);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    public FormExpressionWheel.Expression getActiveExpression() {
        return this.getLocalTime() < this.activeExpressionEndTime ? this.activeExpression : null;
    }

    public void modifyExpressionDrawOptions(HumanDrawOptions options) {
        long time;
        if (this.activeExpression != null && (time = this.activeExpressionEndTime - this.getLocalTime()) >= 0L) {
            float progress = Math.min(1.0f, 1.0f - (float)time / (float)this.activeExpression.animationTimeMillis);
            this.activeExpression.drawOptionsModifier.accept(Float.valueOf(progress), options);
        }
    }

    public void startExpression(FormExpressionWheel.Expression expression) {
        this.activeExpression = expression;
        this.activeExpressionEndTime = this.getLocalTime() + (long)expression.animationTimeMillis;
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-10, -10, 20, 24);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        if (this.getLevel() == null) {
            return;
        }
        PlayerSprite.getIconAnimationDrawOptions(x - 15, y - 26, 32, 32, this).draw();
        String name = this.getDisplayName();
        FontOptions options = new FontOptions(12).color(200, 200, 200).outline();
        int nameWidth = FontManager.bit.getWidthCeil(name, options);
        FontManager.bit.drawString(x - nameWidth / 2, y - 34, name, options);
    }

    public void submitInputEvent(MainGame mainGame, InputEvent event, GameCamera camera) {
        int mouseLevelX = event.pos.sceneX + camera.getX();
        int mouseLevelY = event.pos.sceneY + camera.getY();
        if (event.isMouseMoveEvent() || !event.state || mainGame.formManager.isMouseOver() || this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return;
        }
        if (event.getID() == Control.MOUSE1.getKey()) {
            this.tryAttack(mouseLevelX, mouseLevelY);
        } else if (event.getID() == Control.MOUSE2.getKey() && !this.runClientInteract(mouseLevelX, mouseLevelY, false) && this.getDraggingItem() != null) {
            ContainerAction action = Control.INV_QUICK_MOVE.isDown() ? ContainerAction.TAKE_ONE : ContainerAction.RIGHT_CLICK;
            ContainerActionResult result = this.getLevel().getClient().getContainer().applyContainerAction(-1, action);
            if (result.error != null) {
                InputPosition mousePos = WindowManager.getWindow().mousePos();
                Renderer.hudManager.addElement(new UniqueScreenFloatText(mousePos.hudX, mousePos.hudY, result.error, new FontOptions(16).outline(), "dropError"));
            } else {
                this.getLevel().getClient().network.sendPacket(new PacketContainerAction(-1, action, result.value));
            }
        }
    }

    public Point2D.Float getControllerAimDir() {
        float dx = ControllerInput.getAimX();
        float dy = ControllerInput.getAimY();
        if (dx == 0.0f && dy == 0.0f) {
            dx = ControllerInput.MOVE.getX();
            dy = ControllerInput.MOVE.getY();
        }
        if (dx == 0.0f && dy == 0.0f) {
            dx = this.lastControllerAimX;
            dy = this.lastControllerAimY;
        }
        if (dx == 0.0f && dy == 0.0f) {
            int dir = this.getDir();
            if (this.isAttacking && this.beforeAttackDir != -1) {
                dir = this.beforeAttackDir;
            }
            if (dir == 0) {
                dy = -1.0f;
            } else if (dir == 1) {
                dx = 1.0f;
            } else if (dir == 2) {
                dy = 1.0f;
            } else {
                dx = dir == 3 ? -1.0f : 1.0f;
            }
        }
        return new Point2D.Float(dx, dy);
    }

    public Point2D.Float getControllerAimDirNormalized() {
        Point2D.Float dir = this.getControllerAimDir();
        return GameMath.normalize(dir.x, dir.y);
    }

    public void tryAttack(GameCamera camera) {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            this.tryControllerAttack();
        } else {
            this.tryAttack(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
        }
    }

    public void runClientInteract(boolean onlyItemInteract, GameCamera camera) {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            if (this.attackHandler != null && !this.attackHandler.isFromInteract()) {
                this.constantInteract = this.attackHandler.getConstantInteract();
                this.attackHandler.onControllerInteracted(ControllerInput.getAimX(), ControllerInput.getAimY());
                this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractController(ControllerInput.getAimX(), ControllerInput.getAimY()));
            } else {
                ControllerInteractTarget target = this.getControllerInteractTarget(false, this.getCurrentAttackHeight(), camera);
                if (target != null) {
                    target.runInteract();
                }
            }
        } else {
            this.runClientInteract(camera.getMouseLevelPosX(), camera.getMouseLevelPosY(), onlyItemInteract);
        }
    }

    public void tryAttack(int levelX, int levelY) {
        this.tryAttack(this.getSelectedItemSlot(), levelX, levelY);
    }

    public boolean tryAttack(PlayerInventorySlot slot, int levelX, int levelY) {
        return this.runClientAttack(slot, levelX, levelY);
    }

    public void tryControllerAttack() {
        this.tryControllerAttack(this.getSelectedItemSlot());
    }

    public boolean tryControllerAttack(PlayerInventorySlot slot) {
        return this.runClientControllerAttack(slot);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runPlayerAction(PacketPlayerAction.PlayerAction action, int seed) {
        block2 : switch (action) {
            case USE_HEALTH_POTION: {
                for (SlotPriority slotPriority : this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usehealthpotion")) {
                    if (this.getInv().main.isSlotClear(slotPriority.slot)) continue;
                    ItemUsed itemUsed = this.getInv().main.getItemSlot(slotPriority.slot).useHealthPotion(this.getLevel(), this, seed, this.getInv().main.getItem(slotPriority.slot));
                    this.getInv().main.setItem(slotPriority.slot, itemUsed.item);
                    if (!itemUsed.used) continue;
                    break block2;
                }
                break;
            }
            case USE_MANA_POTION: {
                for (SlotPriority slotPriority : this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usemanapotion")) {
                    if (this.getInv().main.isSlotClear(slotPriority.slot)) continue;
                    ItemUsed itemUsed = this.getInv().main.getItemSlot(slotPriority.slot).useManaPotion(this.getLevel(), this, seed, this.getInv().main.getItem(slotPriority.slot));
                    this.getInv().main.setItem(slotPriority.slot, itemUsed.item);
                    if (!itemUsed.used) continue;
                    break block2;
                }
                break;
            }
            case EAT_FOOD: {
                for (SlotPriority slotPriority : this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "eatfood")) {
                    if (this.getInv().main.isSlotClear(slotPriority.slot)) continue;
                    ItemUsed itemUsed = this.getInv().main.getItemSlot(slotPriority.slot).eatFood(this.getLevel(), this, seed, this.getInv().main.getItem(slotPriority.slot));
                    this.getInv().main.setItem(slotPriority.slot, itemUsed.item);
                    if (!itemUsed.used) continue;
                    break block2;
                }
                break;
            }
            case USE_BUFF_POTION: {
                for (SlotPriority slotPriority : this.getInv().main.getPriorityList(this.getLevel(), this, 0, this.getInv().main.getSize() - 1, "usebuffpotion")) {
                    if (this.getInv().main.isSlotClear(slotPriority.slot)) continue;
                    this.getInv().main.setItem(slotPriority.slot, this.getInv().main.getItemSlot((int)slotPriority.slot).useBuffPotion((Level)this.getLevel(), (PlayerMob)this, (int)seed, (InventoryItem)this.getInv().main.getItem((int)slotPriority.slot)).item);
                }
                if (!this.isServerClient()) break;
                ServerClient serverClient = this.getServerClient();
                if (serverClient.adventureParty.getBuffPotionPolicy() != AdventureParty.BuffPotionPolicy.ON_HOTKEY) break;
                Object object = serverClient.adventureParty.MOBS_LOCK;
                synchronized (object) {
                    for (HumanMob mob : serverClient.adventureParty.getMobs()) {
                        InventoryItem usedItem;
                        for (int i = 0; i < 100 && (usedItem = mob.adventureParty.tryConsumeItem("usebuffpotion")) != null; ++i) {
                            if (i != 99) continue;
                            GameLog.warn.println(serverClient.getName() + " party consumed 100 items on buff hotkey???");
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public GameMessage getLocalization() {
        return new StaticMessage(this.playerName);
    }

    public InventoryItem getSelectedHotbarItem() {
        if (!this.getInv().main.isSlotClear(this.getSelectedSlot())) {
            return this.getInv().main.getItem(this.getSelectedSlot());
        }
        return null;
    }

    public InventoryItem getSelectedItem() {
        if (this.getDraggingItem() != null) {
            return this.getDraggingItem();
        }
        return this.getSelectedHotbarItem();
    }

    public PlayerInventorySlot getSelectedItemSlot() {
        if (this.getDraggingItem() != null) {
            return new PlayerInventorySlot(this.getInv().drag, 0);
        }
        return new PlayerInventorySlot(this.getInv().main, this.getSelectedSlot());
    }

    public InventoryItem getDraggingItem() {
        return this.getInv().drag.getItem(0);
    }

    public void setDraggingItem(InventoryItem item) {
        this.getInv().drag.setItem(0, item);
    }

    @Override
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        return new PlayerInventoryItemAttackSlot(this, this.getSelectedItemSlot());
    }

    @Override
    public boolean itemAttackerHoldsItem(InventoryItem item) {
        return false;
    }

    public void dropItem(InventoryItem item) {
        if (this.isClient()) {
            System.err.println("Players cannot drop items client side");
            return;
        }
        float dx = 0.0f;
        float dy = 0.0f;
        int dir = this.getDir();
        if (dir == 0) {
            dx = 0.0f;
            dy = -1.0f;
        } else if (dir == 1) {
            dx = 1.0f;
            dy = 0.0f;
        } else if (dir == 2) {
            dx = 0.0f;
            dy = 1.0f;
        } else if (dir == 3) {
            dx = -1.0f;
            dy = 0.0f;
        }
        ItemPickupEntity pickupEntity = item.getPickupEntity(this.getLevel(), this.x, this.y, dx * 175.0f, dy * 175.0f);
        if (this.isServerClient()) {
            pickupEntity.authPickupCooldown.put(this.getServerClient().authentication, 2000);
        }
        pickupEntity.height = 20.0f;
        pickupEntity.dh = 40.0f;
        pickupEntity.droppedByPlayer = true;
        this.getLevel().entityManager.pickups.add(pickupEntity);
    }

    public void dropDraggingItem(int amount) {
        if (this.isCreativeMenuExtended() && this.inventoryExtended) {
            ServerClient client = this.getServerClient();
            ContainerSlot draggingSlot = client.getContainer().getClientDraggingSlot();
            int draggingSlotIndex = draggingSlot.getContainerIndex();
            client.getContainer().applyContainerAction(draggingSlotIndex, ContainerAction.QUICK_TRASH);
            client.getContainer().getClientTrashSlot().markDirty();
            draggingSlot.markDirty();
        } else {
            this.inv.dropItem(this.inv.drag, 0, amount);
        }
    }

    public void clientDropItem(PlayerInventory inv, int slot) {
        this.clientDropItem(inv, slot, inv.getAmount(slot));
    }

    public void clientDropItem(PlayerInventory inv, int slot, int amount) {
        if (this.isClient() && !inv.isSlotClear(slot)) {
            this.getLevel().getClient().network.sendPacket(new PacketPlayerDropItem(inv.getInventoryID(), slot, amount));
        }
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
        this.getLevel().hudManager.addElement(new DamageText((Mob)this, damage, new FontOptions(size).outline().color(isCrit ? new Color(255, 85, 0) : Color.RED), isCrit ? GameRandom.globalRandom.getIntBetween(60, 70) : GameRandom.globalRandom.getIntBetween(40, 50)));
    }

    public int getPlayerSlot() {
        return this.getUniqueID();
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.hurt, (SoundEffect)SoundEffect.effect(this).pitch(pitch));
    }

    @Override
    public float getAttackAnimProgress() {
        float progress = (float)(this.getWorldEntity().getTime() - this.attackTime) / (float)this.attackAnimTime;
        if (progress >= 1.0f) {
            this.forceEndAttack();
        }
        return Math.min(1.0f, progress);
    }

    public boolean runClientInteract(int levelX, int levelY, boolean onlyItemInteract) {
        LevelObject interactObject;
        if (!GlobalData.getCurrentState().isRunning() || this.getLevel() == null) {
            return false;
        }
        if (this.attackHandler != null && !this.attackHandler.isFromInteract()) {
            this.constantInteract = this.attackHandler.getConstantInteract();
            this.attackHandler.onMouseInteracted(levelX, levelY);
            this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractMouse(levelX, levelY));
            return false;
        }
        if (this.onCooldown) {
            return false;
        }
        PlayerInventorySlot slot = this.getSelectedItemSlot();
        if (this.attackHandler != null && this.attackHandler.isFromInteract() && !this.attackHandler.canRunAttack(this.getLevel(), levelX, levelY, this, this.attackingItem, new PlayerInventoryItemAttackSlot(this, slot))) {
            return false;
        }
        int mouseTileX = PlayerMob.getTileCoordinate(levelX);
        int mouseTileY = PlayerMob.getTileCoordinate(levelY);
        InventoryItem item = this.getInv().getItem(slot);
        ItemInteractAction interactItem = item != null && item.item instanceof ItemInteractAction ? (ItemInteractAction)((Object)item.item) : null;
        for (Mob mob : this.getLevel().entityManager.mobs.getInRegionByTileRange(mouseTileX, mouseTileY, 8)) {
            if (!mob.isVisible() || !mob.getSelectBox().contains(levelX, levelY)) continue;
            if (interactItem != null && !this.isItemOnCooldown(item.item) && interactItem.canMobInteract(this.getLevel(), mob, this, item)) {
                this.runClientItemMobInteract(interactItem, item, levelX, levelY, mob, slot);
                return true;
            }
            if (onlyItemInteract || !mob.inInteractRange(this) || !mob.canInteract(this)) continue;
            this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(this.getLevel().getClient().getSlot(), mob.getUniqueID()));
            this.constantInteract = false;
            return true;
        }
        if (interactItem != null && interactItem.overridesObjectInteract(this.getLevel(), this, item) && !this.isItemOnCooldown(item.item) && interactItem.canLevelInteract(this.getLevel(), levelX, levelY, this, item)) {
            this.runClientItemLevelInteract(interactItem, item, levelX, levelY, slot);
            return true;
        }
        if (!onlyItemInteract && (interactObject = GameUtils.getInteractObjectHit(this.getLevel(), levelX, levelY, 0, lo -> lo.isInInteractRange(this) && lo.canInteract(this), null)) != null) {
            ObjectInteractEvent event = new ObjectInteractEvent(interactObject.level, interactObject.tileX, interactObject.tileY, this);
            GameEvents.triggerEvent(event);
            if (!event.isPrevented()) {
                interactObject.interact(this);
                this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(this.getLevel(), this.getLevel().getClient().getSlot(), interactObject.tileX, interactObject.tileY));
                this.constantInteract = false;
                return true;
            }
        }
        if (interactItem != null && !this.isItemOnCooldown(item.item) && interactItem.canLevelInteract(this.getLevel(), levelX, levelY, this, item)) {
            this.runClientItemLevelInteract(interactItem, item, levelX, levelY, slot);
            return true;
        }
        return false;
    }

    public ControllerInteractTarget getControllerInteractTarget(boolean onlyItemInteract, int attackHeight, final GameCamera camera) {
        ItemControllerInteract controllerInteract;
        float distance;
        ItemControllerInteract controllerInteract2;
        if (!GlobalData.getCurrentState().isRunning()) {
            return null;
        }
        if (this.onCooldown || this.attackHandler != null) {
            return null;
        }
        if (this.getLevel() == null) {
            return null;
        }
        float dx = ControllerInput.getAimX();
        float dy = ControllerInput.getAimY();
        if (dx == 0.0f && dy == 0.0f) {
            int dir = this.getDir();
            if (dir == 0) {
                dy = -1.0f;
            } else if (dir == 1) {
                dx = 1.0f;
            } else if (dir == 2) {
                dy = 1.0f;
            } else {
                dx = dir == 3 ? -1.0f : 1.0f;
            }
        }
        float angle = GameMath.getAngle(new Point2D.Float(dx, dy));
        int dir = (int)(GameMath.fixAngle(angle + 90.0f + 22.5f) * 8.0f / 360.0f);
        LinkedList<Rectangle> mobInteractBoxes = new LinkedList<Rectangle>();
        LinkedList<Rectangle> tileInteractBoxes = new LinkedList<Rectangle>();
        int ibw = 6;
        int ibh = 6;
        switch (dir) {
            case 0: {
                mobInteractBoxes.add(new Rectangle(this.getX() - ibw * 32 / 2, this.getY() - ibh * 32, ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() - ibw / 2, this.getTileY() - ibh, ibw, ibh));
                break;
            }
            case 1: {
                mobInteractBoxes.add(new Rectangle(this.getX(), this.getY() - ibw * 32, ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() + 1, this.getTileY() - ibh, ibw, ibh));
                break;
            }
            case 2: {
                mobInteractBoxes.add(new Rectangle(this.getX(), this.getY() - ibw * 32 / 2, ibh * 32, ibw * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() + 1, this.getTileY() - ibw / 2, ibh, ibw));
                break;
            }
            case 3: {
                mobInteractBoxes.add(new Rectangle(this.getX(), this.getY(), ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() + 1, this.getTileY() + 1, ibw, ibh));
                break;
            }
            case 4: {
                mobInteractBoxes.add(new Rectangle(this.getX() - ibw * 32 / 2, this.getY(), ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() + 1 - ibw / 2, this.getTileY() + 1, ibw, ibh));
                break;
            }
            case 5: {
                mobInteractBoxes.add(new Rectangle(this.getX() - ibw * 32, this.getY(), ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() - ibw, this.getTileY() + 1, ibw, ibh));
                break;
            }
            case 6: {
                mobInteractBoxes.add(new Rectangle(this.getX() - ibw * 32, this.getY() - ibw * 32 / 2, ibh * 32, ibw * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() - ibw, this.getTileY() - ibw / 2, ibh, ibw));
                break;
            }
            case 7: {
                mobInteractBoxes.add(new Rectangle(this.getX() - ibw * 32, this.getY() - ibw * 32, ibw * 32, ibh * 32));
                tileInteractBoxes.add(new Rectangle(this.getTileX() - ibw, this.getTileY() - ibh, ibw, ibh));
            }
        }
        final PlayerInventorySlot slot = this.getSelectedItemSlot();
        final InventoryItem item = this.getInv().getItem(slot);
        final ItemInteractAction interactItem = item != null && item.item instanceof ItemInteractAction ? (ItemInteractAction)((Object)item.item) : null;
        ArrayList<Mob> mobs = this.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getTileX(), this.getTileY(), 1);
        Mob mount = this.getMount();
        ObjectValue mobInteract = mobs.stream().filter(m -> m.isVisible() && m != mount).filter(m -> {
            Rectangle selectionBox = m.getSelectBox();
            return mobInteractBoxes.stream().anyMatch(r -> r.intersects(selectionBox));
        }).map(m -> {
            if (interactItem != null && !this.isItemOnCooldown(item.item) && interactItem.canMobInteract(this.getLevel(), (Mob)m, this, item)) {
                return new ObjectValue<Mob, 6>((Mob)m, new ControllerInteractTarget(){
                    final /* synthetic */ Mob val$m;
                    final /* synthetic */ ItemInteractAction val$interactItem;
                    final /* synthetic */ InventoryItem val$item;
                    final /* synthetic */ PlayerInventorySlot val$slot;
                    final /* synthetic */ GameCamera val$camera;
                    {
                        this.val$m = mob;
                        this.val$interactItem = itemInteractAction;
                        this.val$item = inventoryItem;
                        this.val$slot = playerInventorySlot;
                        this.val$camera = gameCamera;
                    }

                    @Override
                    public void runInteract() {
                        Rectangle selectionBox = this.val$m.getSelectBox();
                        int centerX = selectionBox.x + selectionBox.width / 2;
                        int centerY = selectionBox.y + selectionBox.height / 2;
                        PlayerMob.this.runClientItemMobInteract(this.val$interactItem, this.val$item, centerX, centerY, this.val$m, this.val$slot);
                    }

                    @Override
                    public DrawOptions getDrawOptions() {
                        Rectangle selectBox = this.val$m.getSelectBox();
                        return HUD.levelBoundOptions(this.val$camera, Settings.UI.controllerFocusBoundsColor, true, selectBox);
                    }

                    @Override
                    public void onCurrentlyFocused() {
                        Rectangle selectBox = this.val$m.getSelectBox();
                        GameTooltipManager.setTooltipsInteractFocus(InputPosition.fromScenePos(WindowManager.getWindow().getInput(), this.val$camera.getDrawX(selectBox.x), this.val$camera.getDrawY(selectBox.y)));
                        this.val$item.item.onMouseHoverMob(this.val$item, this.val$camera, PlayerMob.this, this.val$m, false);
                    }
                });
            }
            if (!onlyItemInteract && m.inInteractRange(this) && m.canInteract(this)) {
                return new ObjectValue<Mob, 7>((Mob)m, new ControllerInteractTarget(){
                    final /* synthetic */ Mob val$m;
                    final /* synthetic */ GameCamera val$camera;
                    {
                        this.val$m = mob;
                        this.val$camera = gameCamera;
                    }

                    @Override
                    public void runInteract() {
                        PlayerMob.this.getLevel().getClient().network.sendPacket(new PacketPlayerMobInteract(PlayerMob.this.getLevel().getClient().getSlot(), this.val$m.getUniqueID()));
                        PlayerMob.this.constantInteract = false;
                    }

                    @Override
                    public DrawOptions getDrawOptions() {
                        Rectangle selectBox = this.val$m.getSelectBox();
                        return HUD.levelBoundOptions(this.val$camera, Settings.UI.controllerFocusBoundsColor, true, selectBox);
                    }

                    @Override
                    public void onCurrentlyFocused() {
                        Rectangle selectBox = this.val$m.getSelectBox();
                        GameTooltipManager.setTooltipsInteractFocus(InputPosition.fromScenePos(WindowManager.getWindow().getInput(), this.val$camera.getDrawX(selectBox.x), this.val$camera.getDrawY(selectBox.y)));
                        this.val$m.onMouseHover(this.val$camera, PlayerMob.this, false);
                    }
                });
            }
            return null;
        }).filter(Objects::nonNull).min(Comparator.comparingDouble(ov -> ((Mob)ov.object).getDistance(this))).orElse(null);
        ObjectValue<Point, 8> itemInteract = null;
        if (interactItem != null && !this.isItemOnCooldown(item.item) && (controllerInteract2 = interactItem.getControllerInteract(this.getLevel(), this, item, true, dir, mobInteractBoxes, tileInteractBoxes)) != null) {
            itemInteract = new ObjectValue<Point, 8>(new Point(controllerInteract2.levelX, controllerInteract2.levelY), new ControllerInteractTarget(){

                @Override
                public void runInteract() {
                    if (interactItem.canLevelInteract(PlayerMob.this.getLevel(), controllerInteract2.levelX, controllerInteract2.levelY, PlayerMob.this, item)) {
                        PlayerMob.this.runClientItemLevelInteract(interactItem, item, controllerInteract2.levelX, controllerInteract2.levelY, slot);
                    }
                }

                @Override
                public DrawOptions getDrawOptions() {
                    return controllerInteract2.getDrawOptions(camera);
                }

                @Override
                public void onCurrentlyFocused() {
                    controllerInteract2.onCurrentlyFocused(camera);
                }
            });
        }
        ObjectValue objectInteract = null;
        if (!onlyItemInteract) {
            objectInteract = tileInteractBoxes.stream().flatMap(r -> {
                LinkedList<LevelObject> objects = new LinkedList<LevelObject>();
                for (int i = 0; i < r.width; ++i) {
                    for (int j = 0; j < r.height; ++j) {
                        objects.add(this.getLevel().getLevelObject(r.x + i, r.y + j));
                    }
                }
                return objects.stream();
            }).filter(lo -> lo.isInInteractRange(this) && lo.canInteract(this)).min(Comparator.comparingDouble(lo -> this.getDistance(lo.tileX * 32 + 16, lo.tileY * 32 + 16))).map(lo -> new ObjectValue<LevelObject, 9>((LevelObject)lo, new ControllerInteractTarget(){
                final /* synthetic */ LevelObject val$lo;
                final /* synthetic */ GameCamera val$camera;
                {
                    this.val$lo = levelObject;
                    this.val$camera = gameCamera;
                }

                @Override
                public void runInteract() {
                    ObjectInteractEvent event = new ObjectInteractEvent(this.val$lo.level, this.val$lo.tileX, this.val$lo.tileY, PlayerMob.this);
                    GameEvents.triggerEvent(event);
                    if (!event.isPrevented()) {
                        this.val$lo.interact(PlayerMob.this);
                        PlayerMob.this.getLevel().getClient().network.sendPacket(new PacketObjectInteract(PlayerMob.this.getLevel(), PlayerMob.this.getLevel().getClient().getSlot(), this.val$lo.tileX, this.val$lo.tileY));
                        PlayerMob.this.constantInteract = false;
                    }
                }

                @Override
                public DrawOptions getDrawOptions() {
                    Rectangle selectBox = this.val$lo.getMultiTile().getTileRectangle(this.val$lo.tileX, this.val$lo.tileY);
                    return HUD.tileBoundOptions(this.val$camera, Settings.UI.controllerFocusBoundsColor, true, selectBox);
                }

                @Override
                public void onCurrentlyFocused() {
                    Rectangle selectBox = this.val$lo.getMultiTile().getTileRectangle(this.val$lo.tileX, this.val$lo.tileY);
                    GameTooltipManager.setTooltipsInteractFocus(InputPosition.fromScenePos(WindowManager.getWindow().getInput(), this.val$camera.getDrawX(selectBox.x * 32), this.val$camera.getDrawY(selectBox.y * 32)));
                    String controlMsg = this.val$lo.getInteractTip(PlayerMob.this, false);
                    if (controlMsg != null) {
                        GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, controlMsg, this.val$lo.isInInteractRange(PlayerMob.this) ? 1.0f : 0.7f), TooltipLocation.INTERACT_FOCUS);
                    }
                    this.val$lo.onMouseHover(this.val$camera, PlayerMob.this, false);
                }
            })).orElse(null);
        }
        ControllerInteractTarget best = null;
        double bestDistance = Double.MAX_VALUE;
        if (mobInteract != null && (double)(distance = this.getDistance((Mob)mobInteract.object)) < bestDistance) {
            best = (ControllerInteractTarget)mobInteract.value;
            bestDistance = distance;
        }
        if (itemInteract != null && (double)(distance = this.getDistance(((Point)itemInteract.object).x, ((Point)itemInteract.object).y)) < bestDistance) {
            best = (ControllerInteractTarget)itemInteract.value;
            bestDistance = distance;
        }
        if (objectInteract != null && (double)(distance = this.getDistance(((LevelObject)objectInteract.object).tileX * 32 + 16, ((LevelObject)objectInteract.object).tileY * 32 + 16)) < bestDistance) {
            best = (ControllerInteractTarget)objectInteract.value;
            bestDistance = distance;
        }
        if (best != null) {
            return best;
        }
        if (interactItem != null && !this.isItemOnCooldown(item.item) && (controllerInteract = interactItem.getControllerInteract(this.getLevel(), this, item, false, dir, mobInteractBoxes, tileInteractBoxes)) != null) {
            return new ControllerInteractTarget(){

                @Override
                public void runInteract() {
                    PlayerMob.this.runClientItemLevelInteract(interactItem, item, controllerInteract.levelX, controllerInteract.levelY, slot);
                }

                @Override
                public DrawOptions getDrawOptions() {
                    return controllerInteract.getDrawOptions(camera);
                }

                @Override
                public void onCurrentlyFocused() {
                    controllerInteract.onCurrentlyFocused(camera);
                }
            };
        }
        return null;
    }

    @Override
    public GNDItemMap runItemAttack(InventoryItem item, int targetX, int targetY, int seed, int animAttack, ItemAttackSlot slot, GNDItemMap attackMap) {
        attackMap = super.runItemAttack(item, targetX, targetY, seed, animAttack, slot, attackMap);
        this.constantAttack = item.item.getConstantUse(item);
        return attackMap;
    }

    @Override
    protected GNDItemMap runItemMobInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, Mob targetMob, int seed, ItemAttackSlot slot, GNDItemMap interactMap) {
        interactMap = super.runItemMobInteract(interactItem, item, targetX, targetY, targetMob, seed, slot, interactMap);
        this.constantInteract = interactItem.getConstantInteract(item);
        return interactMap;
    }

    @Override
    protected GNDItemMap runItemLevelInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, int seed, ItemAttackSlot slot, GNDItemMap interactMap) {
        interactMap = super.runItemLevelInteract(interactItem, item, targetX, targetY, seed, slot, interactMap);
        this.constantInteract = interactItem.getConstantInteract(item);
        return interactMap;
    }

    protected void runClientItemAttack(InventoryItem item, int targetX, int targetY, int animAttack, PlayerInventorySlot slot, int seed, GNDItemMap attackMap) {
        this.attackSlot = slot;
        this.runItemAttack(item, targetX, targetY, seed, animAttack, new PlayerInventoryItemAttackSlot(this, slot), attackMap);
        PacketPlayerAttack p = new PacketPlayerAttack(this, this.attackSlot, item.item, targetX, targetY, animAttack, seed, attackMap);
        this.getClient().network.sendPacket(p);
    }

    protected void runClientItemAttack(InventoryItem item, int targetX, int targetY, int animAttack, PlayerInventorySlot slot, int seed) {
        this.attackSlot = slot;
        GNDItemMap attackMap = this.runItemAttack(item, targetX, targetY, seed, animAttack, new PlayerInventoryItemAttackSlot(this, slot), null);
        PacketPlayerAttack p = new PacketPlayerAttack(this, this.attackSlot, item.item, targetX, targetY, animAttack, seed, attackMap);
        this.getClient().network.sendPacket(p);
    }

    protected void runClientItemMobInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, Mob targetMob, PlayerInventorySlot slot) {
        this.attackSlot = slot;
        int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
        GNDItemMap interactMap = this.runItemMobInteract(interactItem, item, targetX, targetY, targetMob, seed, new PlayerInventoryItemAttackSlot(this, slot), null);
        PacketPlayerItemMobInteract p = new PacketPlayerItemMobInteract(this, this.attackSlot, item.item, targetX, targetY, targetMob, 0, seed, interactMap);
        this.getClient().network.sendPacket(p);
    }

    protected void runClientItemLevelInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, PlayerInventorySlot slot) {
        this.attackSlot = slot;
        int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
        GNDItemMap interactMap = this.runItemLevelInteract(interactItem, item, targetX, targetY, seed, new PlayerInventoryItemAttackSlot(this, slot), null);
        PacketPlayerItemInteract p = new PacketPlayerItemInteract(this, this.attackSlot, item.item, targetX, targetY, 0, seed, interactMap);
        this.getClient().network.sendPacket(p);
    }

    public void runServerItemMobInteract(PacketPlayerItemMobInteract packet, Mob mob) {
        this.setPos(packet.playerX, packet.playerY, false);
        InventoryItem item = this.getInv().getItem(packet.getSlot());
        if (item != null && item.item.getID() == packet.itemID && item.item instanceof ItemInteractAction) {
            ItemInteractAction interactItem = (ItemInteractAction)((Object)item.item);
            if (this.attackHandler != null) {
                return;
            }
            boolean canAttack = interactItem.canMobInteract(this.getLevel(), mob, this, item);
            if (!canAttack && Settings.strictServerAuthority) {
                return;
            }
            this.attackSlot = packet.getSlot();
            this.runItemMobInteract(interactItem, item, mob, packet.seed, new PlayerInventoryItemAttackSlot(this, this.attackSlot), packet.mapContent);
            this.getServer().network.sendToClientsWithAnyRegionExcept(new PacketShowItemMobInteract(this, item, packet.attackX, packet.attackY, mob, packet.seed, packet.mapContent), this.getRegionPositionsCombined(mob), this.getServerClient());
        } else {
            this.getInv().markFullDirty();
        }
    }

    public void runServerItemLevelInteract(PacketPlayerItemInteract packet) {
        this.setPos(packet.playerX, packet.playerY, false);
        InventoryItem item = this.getInv().getItem(packet.getSlot());
        if (item != null && item.item.getID() == packet.itemID && item.item instanceof ItemInteractAction) {
            ItemInteractAction interactItem = (ItemInteractAction)((Object)item.item);
            if (this.attackHandler != null) {
                return;
            }
            boolean canAttack = interactItem.canLevelInteract(this.getLevel(), packet.attackX, packet.attackY, this, item);
            if (!canAttack && Settings.strictServerAuthority) {
                return;
            }
            this.attackSlot = packet.getSlot();
            this.runItemLevelInteract(interactItem, item, packet.attackX, packet.attackY, packet.seed, new PlayerInventoryItemAttackSlot(this, this.attackSlot), packet.mapContent);
            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowItemLevelInteract(this, item, packet.attackX, packet.attackY, packet.seed, packet.mapContent), this, this.getServerClient());
        } else {
            this.getInv().markFullDirty();
        }
    }

    public boolean runClientControllerAttack(PlayerInventorySlot slot) {
        if (!GlobalData.getCurrentState().isRunning()) {
            return false;
        }
        if (this.attackHandler != null && this.attackHandler.isFromInteract()) {
            this.constantInteract = this.attackHandler.getConstantInteract();
            this.attackHandler.onControllerInteracted(ControllerInput.getAimX(), ControllerInput.getAimY());
            this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractController(ControllerInput.getAimX(), ControllerInput.getAimY()));
            return false;
        }
        if (this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue() || this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue()) {
            return false;
        }
        if (this.objectUser != null && !this.objectUser.canInteract(slot, this)) {
            return false;
        }
        InventoryItem attackItem = this.isAttacking ? this.attackingItem : this.getInv().getItem(slot);
        if (attackItem != null) {
            Point2D.Float aimDir = this.getControllerAimDir();
            this.lastControllerAimX = aimDir.x;
            this.lastControllerAimY = aimDir.y;
            Point levelPos = attackItem.item.getControllerAttackLevelPos(this.getLevel(), aimDir.x, aimDir.y, this, attackItem);
            return this.runClientAttack(slot, levelPos.x, levelPos.y);
        }
        return false;
    }

    public boolean runClientAttack(PlayerInventorySlot slot, int levelX, int levelY) {
        if (!GlobalData.getCurrentState().isRunning()) {
            return false;
        }
        if (this.attackHandler != null && this.attackHandler.isFromInteract()) {
            this.constantInteract = this.attackHandler.getConstantInteract();
            this.attackHandler.onMouseInteracted(levelX, levelY);
            this.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientInteractMouse(levelX, levelY));
            return false;
        }
        if (this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue() || this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue()) {
            return false;
        }
        if (this.objectUser != null && !this.objectUser.canAttack(slot, this)) {
            return false;
        }
        if (this.isAttacking) {
            if (this.canAnimAttackAgain(this.attackSlot.getItem(this.inv)) && this.getNextAnimAttackCooldown() <= 0L) {
                float hungerUsage;
                ToolDamageItem.SmartMineTarget hitTile = null;
                if (this.attackingItem != null && this.attackingItem.item instanceof ToolDamageItem && (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) && (hitTile = ((ToolDamageItem)this.attackingItem.item).getFirstSmartHitTile(this.getLevel(), this, this.attackingItem, levelX, levelY)) != null) {
                    levelX = hitTile.x * 32 + 16;
                    levelY = hitTile.y * 32 + 16;
                }
                GNDItemMap attackMap = new GNDItemMap();
                int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
                this.attackingItem.item.setupAttackMapContent(attackMap, this.getLevel(), levelX, levelY, this, seed, this.attackingItem);
                if (hitTile != null && hitTile.tileDamageOption != null) {
                    ((ToolDamageItem)this.attackingItem.item).setupAttackMapContentHitTile(attackMap, this.getLevel(), hitTile.tileDamageOption);
                }
                this.runClientItemAttack(this.attackingItem, levelX, levelY, this.animAttack, slot, seed, attackMap);
                if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger() && (hungerUsage = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier) > 0.0f) {
                    this.useHunger(hungerUsage, false);
                }
                return true;
            }
            return false;
        }
        if (this.onCooldown) {
            return false;
        }
        if (this.attackHandler != null && !this.attackHandler.isFromInteract() && !this.attackHandler.canRunAttack(this.getLevel(), levelX, levelY, this, this.attackingItem, new PlayerInventoryItemAttackSlot(this, slot))) {
            return false;
        }
        this.attackSlot = slot;
        this.attackingItem = !this.getInv().isSlotClear(this.attackSlot) ? this.getInv().getItem(this.attackSlot) : null;
        this.attackDir = GameMath.normalize((float)levelX - this.x, (float)levelY - this.y);
        if (this.attackingItem != null) {
            this.constantAttack = this.attackingItem.item.getConstantUse(this.attackingItem);
            if (!this.isItemOnCooldown(this.attackingItem.item)) {
                String canAttack = this.attackingItem.item.canAttack(this.getLevel(), levelX, levelY, this, this.attackingItem);
                if (canAttack == null) {
                    float hungerUsage;
                    ToolDamageItem.SmartMineTarget hitTile = null;
                    if (this.attackingItem != null && this.attackingItem.item instanceof ToolDamageItem && (Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) && (hitTile = ((ToolDamageItem)this.attackingItem.item).getFirstSmartHitTile(this.getLevel(), this, this.attackingItem, levelX, levelY)) != null) {
                        levelX = hitTile.x * 32 + 16;
                        levelY = hitTile.y * 32 + 16;
                    }
                    GNDItemMap attackMap = new GNDItemMap();
                    int seed = Item.getRandomAttackSeed(GameRandom.globalRandom);
                    this.attackingItem.item.setupAttackMapContent(attackMap, this.getLevel(), levelX, levelY, this, seed, this.attackingItem);
                    if (hitTile != null && hitTile.tileDamageOption != null) {
                        ((ToolDamageItem)this.attackingItem.item).setupAttackMapContentHitTile(attackMap, this.getLevel(), hitTile.tileDamageOption);
                    }
                    this.runClientItemAttack(this.attackingItem, levelX, levelY, 0, slot, seed, attackMap);
                    if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger() && (hungerUsage = this.attackingItem.item.getHungerUsage(this.attackingItem, this) * attacksHungerModifier) > 0.0f) {
                        this.useHunger(hungerUsage, false);
                    }
                    return true;
                }
                if (!canAttack.isEmpty()) {
                    UniqueFloatText text = new UniqueFloatText(this.getX(), this.getY() - 20, canAttack, new FontOptions(16).outline().color(new Color(200, 100, 100)), "mountfail"){

                        @Override
                        public int getAnchorX() {
                            return PlayerMob.this.getX();
                        }

                        @Override
                        public int getAnchorY() {
                            return PlayerMob.this.getY() - 20;
                        }
                    };
                    this.getLevel().hudManager.addElement(text);
                }
            }
        }
        return false;
    }

    public void runServerAttack(PacketPlayerAttack packet) {
        InventoryItem item = this.getInv().getItem(packet.getSlot());
        if (item != null && item.item.getID() == packet.itemID) {
            float hungerUsage;
            if (packet.animAttack >= 1 && packet.animAttack == this.animAttack && this.canAnimAttackAgain(this.attackSlot.getItem(this.inv)) && this.getNextAnimAttackCooldown() <= (long)(Settings.strictServerAuthority ? 50 : 1000)) {
                float hungerUsage2;
                item = this.getInv().getItem(this.attackSlot);
                this.runItemAttack(item, packet.attackX, packet.attackY, packet.seed, this.animAttack, new PlayerInventoryItemAttackSlot(this, this.attackSlot), packet.mapContent);
                if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger() && (hungerUsage2 = item.item.getHungerUsage(item, this) * attacksHungerModifier) > 0.0f) {
                    this.useHunger(hungerUsage2, false);
                    this.sendHungerPacket();
                }
                return;
            }
            if (this.isAttacking) {
                if (this.getNextAttackCooldown() <= (long)(Settings.strictServerAuthority ? 40 : 1000)) {
                    this.forceEndAttack();
                } else {
                    return;
                }
            }
            if (this.attackHandler != null && !this.attackHandler.canRunAttack(this.getLevel(), packet.attackX, packet.attackY, this, item, new PlayerInventoryItemAttackSlot(this, packet.getSlot()))) {
                return;
            }
            String canAttack = item.item.canAttack(this.getLevel(), packet.attackX, packet.attackY, this, item);
            if (canAttack != null) {
                item.item.onServerCanAttackFailed(this.getLevel(), packet.attackX, packet.attackY, this, item, canAttack, !Settings.strictServerAuthority);
                if (Settings.strictServerAuthority) {
                    return;
                }
            }
            this.attackSlot = packet.getSlot();
            this.runItemAttack(item, packet.attackX, packet.attackY, packet.seed, 0, new PlayerInventoryItemAttackSlot(this, this.attackSlot), packet.mapContent);
            if (this.getWorldSettings() != null && this.getWorldSettings().playerHunger() && (hungerUsage = item.item.getHungerUsage(item, this) * attacksHungerModifier) > 0.0f) {
                this.useHunger(hungerUsage, false);
                this.sendHungerPacket();
            }
            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this, item, packet.attackX, packet.attackY, 0, packet.seed, packet.mapContent), this, this.getServerClient());
        } else {
            this.getInv().markFullDirty();
        }
    }

    @Override
    public void showAttackAndSendAttacker(InventoryItem item, int targetX, int targetY, int animAttack, int seed, GNDItemMap attackMap) {
        this.showItemAttack(item, targetX, targetY, animAttack, seed, attackMap);
        if (this.isServer() && this.isServerClient()) {
            this.getServer().network.sendToAllClientsExcept(new PacketShowAttack(this, item, targetX, targetY, animAttack, seed, attackMap), this.getServerClient());
        }
    }

    @Override
    public void doAndSendStopAttackAttacker(boolean endAttackHandler) {
        this.stopAttack(false);
        if (this.isServer() && this.isServerClient()) {
            this.getServer().network.sendToAllClientsExcept(new PacketPlayerStopAttack(this.getPlayerSlot()), this.getServerClient());
        }
    }

    @Override
    public void addAndSendAttackerProjectile(Projectile projectile, Runnable runAfterAdded) {
        this.getLevel().entityManager.projectiles.addHidden(projectile);
        if (runAfterAdded != null) {
            runAfterAdded.run();
        }
        if (this.isServer() && this.isServerClient()) {
            this.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), this, this.getServerClient());
        }
    }

    @Override
    public void addAndSendAttackerLevelEvent(LevelEvent event) {
        this.getLevel().entityManager.events.addHidden(event);
        if (this.isServer() && this.isServerClient()) {
            this.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(event), this, this.getServerClient());
        }
    }

    @Override
    public void sendAttackerPacket(RegionPositionGetter entity, Packet packet) {
        if (this.isServer() && this.isServerClient()) {
            this.getServer().network.sendToClientsWithEntityExcept(packet, entity, this.getServerClient());
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        int respawnTime = attackers.stream().filter(a -> !a.removed()).map(Attacker::getRespawnTime).max(Comparator.comparingInt(Integer::intValue)).orElse(5000);
        this.getServerClient().die(respawnTime);
        GameMessage deathMessage = DeathMessageTable.getDeathMessage(attacker, this.getLocalization());
        this.getLevel().getServer().network.sendToAllClients(new PacketChatMessage(new GameMessageBuilder().append("\u00a76").append(deathMessage)));
        System.out.println(deathMessage.translate());
    }

    @Override
    public GameMessage getAttackerName() {
        return new StaticMessage(this.getDisplayName());
    }

    @Override
    public int getUniqueID() {
        return this.getRealUniqueID();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameSkin gameSkin = this.look.getGameSkin(false);
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), gameSkin.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void setSelectedSlot(int slot) {
        if (slot < 0 || slot > 9) {
            throw new IllegalArgumentException("Slot must be in range [0 - 9]");
        }
        if (this.selectedSlot != slot) {
            this.inventoryActionUpdate = true;
        }
        this.selectedSlot = slot;
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void setInventoryExtended(boolean extended) {
        InventoryItem draggingItem;
        if (this.inventoryExtended != extended) {
            this.inventoryActionUpdate = true;
        }
        if (this.getLevel() != null && this.isServer() && this.inventoryExtended && !extended && (draggingItem = this.getDraggingItem()) != null) {
            if (!draggingItem.isLocked()) {
                this.dropDraggingItem(draggingItem.getAmount());
            } else {
                this.getInv().addItem(draggingItem, true, "addback", null);
                if (draggingItem.getAmount() <= 0) {
                    this.setDraggingItem(null);
                } else {
                    this.dropDraggingItem(draggingItem.getAmount());
                }
            }
        }
        this.inventoryExtended = extended;
    }

    public boolean isInventoryExtended() {
        return this.inventoryExtended;
    }

    public void setCreativeMenuExtended(boolean extended) {
        if (!this.getWorldSettings().creativeMode) {
            return;
        }
        if (this.creativeMenuExtended != extended) {
            this.inventoryActionUpdate = true;
        }
        this.creativeMenuExtended = extended;
    }

    public boolean isCreativeMenuExtended() {
        if (!this.getWorldSettings().creativeMode) {
            return false;
        }
        return this.creativeMenuExtended;
    }

    public boolean shouldUpdateInventoryAction() {
        return this.inventoryActionUpdate;
    }

    public void resetUpdateInventoryAction() {
        this.inventoryActionUpdate = false;
    }

    public void resetInv() {
        if (this.inv == null) {
            this.inv = new PlayerInventoryManager(this);
        } else {
            this.inv.clearInventories();
        }
    }

    public NetworkClient getNetworkClient() {
        return this.networkClient;
    }

    public PlayerInventoryManager getInv() {
        return this.inv;
    }

    @Override
    public int getDir() {
        int forcedDir;
        ObjectUserActive objectUser = this.objectUser;
        if (objectUser != null && (forcedDir = objectUser.getForcedUserDir()) >= 0 && forcedDir <= 3) {
            return forcedDir;
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
    public float getHungerLevel() {
        return this.hungerLevel;
    }

    @Override
    public void useHunger(float amount, boolean forceUse) {
        if (this.hasGodMode()) {
            this.hungerLevel = Math.max(0.2f, this.hungerLevel);
            return;
        }
        if (!forceUse && FoodConsumableItem.doesMobHaveAnyFoodBuff(this)) {
            return;
        }
        this.hungerLevel = Math.max(0.0f, this.hungerLevel - amount);
    }

    @Override
    public void addHunger(float amount) {
        this.hungerLevel = Math.min(this.hungerLevel + amount, 1.0f + amount);
    }

    @Override
    public boolean useFoodItem(FoodConsumableItem item, boolean giveBuff) {
        if (!FoodConsumableItem.canEatFood(this, item)) {
            return false;
        }
        HungerMob.super.useFoodItem(item, giveBuff);
        if (this.isServerClient()) {
            ServerClient client = this.getServerClient();
            client.newStats.food_consumed.add(item);
            JournalChallengeRegistry.handleListeners(client, FoodConsumedJournalChallengeListener.class, challenge -> challenge.onFoodConsumed(client, item));
        }
        this.sendHungerPacket();
        return true;
    }

    public void tickHunger() {
        WorldSettings worldSettings = this.getLevel().getWorldSettings();
        if (worldSettings.playerHunger()) {
            float reduce = 0.0f;
            reduce += 50.0f / (1000.0f * (float)secondsToPassAtFullHunger);
            double distanceRan = (double)((int)this.getDistanceRan()) - this.lastDistanceRan;
            if (distanceRan > 0.0) {
                float speedMod = Math.min(1.0f, 35.0f / this.getSpeed());
                reduce = (float)((double)reduce + distanceRan / (double)distanceToRunAtFullHunger * (double)speedMod);
            }
            this.useHunger(reduce, false);
        }
        this.lastDistanceRan = this.getDistanceRan();
        if (worldSettings.playerHunger()) {
            if (this.hungerLevel <= 0.0f) {
                if (this.isServer() && worldSettings.survivalMode && this.canTakeDamage() && !this.isOnGenericCooldown("starvationdamage")) {
                    int maxHealth = this.getMaxHealth();
                    float damage = Math.max((float)Math.pow(maxHealth, 0.5) + (float)maxHealth / 20.0f, 20.0f);
                    this.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage /= 2.0f), 0.0f, 0.0f, 0.0f, STARVING_ATTACKER);
                    this.startGenericCooldown("starvationdamage", 5000L);
                }
                if (!this.buffManager.hasBuff(BuffRegistry.STARVING_BUFF)) {
                    this.buffManager.addBuff(new ActiveBuff(BuffRegistry.STARVING_BUFF, (Mob)this, 0, null), false);
                }
                this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
            } else if (this.hungerLevel <= 0.1f) {
                if (!this.buffManager.hasBuff(BuffRegistry.HUNGRY_BUFF)) {
                    this.buffManager.addBuff(new ActiveBuff(BuffRegistry.HUNGRY_BUFF, (Mob)this, 0, null), false);
                }
                this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
            } else {
                this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
                this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
            }
        } else {
            this.buffManager.removeBuff(BuffRegistry.HUNGRY_BUFF, false);
            this.buffManager.removeBuff(BuffRegistry.STARVING_BUFF, false);
        }
    }

    public ServerClient getServerClient() {
        return (ServerClient)this.networkClient;
    }

    public boolean isServerClient() {
        return this.networkClient instanceof ServerClient;
    }

    public ClientClient getClientClient() {
        return (ClientClient)this.networkClient;
    }

    public boolean isClientClient() {
        return this.networkClient instanceof ClientClient;
    }

    public void sendHungerPacket() {
        if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPlayerHunger(this.getUniqueID(), this.hungerLevel), this);
        }
    }

    @Override
    public int getAvailableArrows(String purpose) {
        return this.getInv().main.getAmount(this.getLevel(), this, Item.Type.ARROW, purpose);
    }

    @Override
    public int getAvailableBullets(String purpose) {
        return this.getInv().main.getAmount(this.getLevel(), this, Item.Type.BULLET, purpose);
    }

    @Override
    public Item getFirstAvailableArrow(String purpose) {
        return this.getInv().main.getFirstItem(this.getLevel(), this, Item.Type.ARROW, purpose);
    }

    @Override
    public Item getFirstAvailableBullet(String purpose) {
        return this.getInv().main.getFirstItem(this.getLevel(), this, Item.Type.BULLET, purpose);
    }

    @Override
    public int getAvailableAmmo(Item[] items, String purpose) {
        int count = 0;
        for (Item item : items) {
            count += this.getInv().main.getAmount(this.getLevel(), this, item, purpose);
        }
        return count;
    }

    @Override
    public Item getFirstAvailableAmmo(Item[] items, String purpose) {
        return this.getInv().main.getFirstItem(this.getLevel(), this, items, purpose);
    }

    @Override
    public AmmoConsumed removeAmmo(Item item, int amount, String purpose) {
        return new AmmoConsumed(this.getInv().main.removeItems(this.getLevel(), this, item, amount, purpose), 1.0f);
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        if (slotType != null) {
            switch (slotType) {
                case HELMET: {
                    InventoryItem helmetItem = this.getInv().equipment.getSelectedArmorSlot(0).getItem();
                    return helmetItem != null && helmetItem.item.getID() == item.getID();
                }
                case CHEST: {
                    InventoryItem chestItem = this.getInv().equipment.getSelectedArmorSlot(1).getItem();
                    return chestItem != null && chestItem.item.getID() == item.getID();
                }
                case FEET: {
                    InventoryItem feetItem = this.getInv().equipment.getSelectedArmorSlot(2).getItem();
                    return feetItem != null && feetItem.item.getID() == item.getID();
                }
                case TRINKETS: {
                    InventoryItem trinketAbilityItem = this.getInv().equipment.getSelectedEquipmentSlot(0).getItem();
                    if (trinketAbilityItem != null && trinketAbilityItem.item.getID() == item.getID()) {
                        return true;
                    }
                    return this.getInv().equipment.getSelectedTrinketsInventory().streamSlots().anyMatch(slot -> {
                        InventoryItem currentItem = slot.getItem();
                        return currentItem != null && currentItem.item.getID() == item.getID();
                    });
                }
                case MOUNT: {
                    InventoryItem mountItem = this.getInv().equipment.getSelectedEquipmentSlot(0).getItem();
                    return mountItem != null && mountItem.item.getID() == item.getID();
                }
            }
        }
        return this.getInv().streamInventorySlots(false, false, false, false).anyMatch(slot -> {
            InventoryItem currentItem = slot.getItem();
            return currentItem != null && currentItem.item.getID() == item.getID();
        });
    }

    @Override
    public float getRegenFlat() {
        return (float)this.getMaxHealth() / 200.0f;
    }

    @Override
    public boolean usesMana() {
        return true;
    }

    public boolean isManaBarVisible() {
        if (this.usesMana()) {
            int maxMana;
            float mana = this.getMana();
            if (mana < (float)(maxMana = this.getMaxMana())) {
                this.maxManaReached = false;
                return true;
            }
            if (this.timeOfMaxMana + 2000L < this.getWorldEntity().getTime() && this.maxManaReached) {
                return false;
            }
            if (!this.maxManaReached) {
                this.timeOfMaxMana = this.getWorldEntity().getTime();
                this.maxManaReached = true;
            }
            return true;
        }
        return false;
    }

    public boolean hasGodMode() {
        WorldSettings worldSettings = this.getWorldSettings();
        if (worldSettings == null) {
            return false;
        }
        return worldSettings.creativeMode && this.hasGodModeInCreative;
    }

    public void setGodModeInCreative(boolean value) {
        this.hasGodModeInCreative = value;
        this.getClient().network.sendPacket(new PacketCreativePlayerSettings(this));
        this.refreshCreativeModeBuff();
    }

    public void refreshCreativeModeBuff() {
        if (this.hasGodMode()) {
            this.buffManager.addBuff(new ActiveBuff(BuffRegistry.GODMODE_BUFF, (Mob)this, 1.0f, null), false);
        } else {
            this.buffManager.removeBuff(BuffRegistry.GODMODE_BUFF, false);
        }
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        if (this.activeSetBuffAbility != null) {
            this.activeSetBuffAbility.onStopped(this);
            this.activeSetBuffAbility = null;
        }
        if (this.activeTrinketBuffAbility != null) {
            this.activeTrinketBuffAbility.onStopped(this);
            this.activeTrinketBuffAbility = null;
        }
        if (this.activeMountAbility != null) {
            this.activeMountAbility.onStopped(this);
            this.activeMountAbility = null;
        }
    }

    @Override
    public int getMaxHealthUpgrade() {
        if (this.healthUpgradeManager == null) {
            return 0;
        }
        return this.healthUpgradeManager.getCurrentMaxHealthIncrease();
    }

    @Override
    public void giveBaitBack(BaitItem bait) {
        ItemPickupEntity pickup = this.inv.addItemsDropRemaining(new InventoryItem(bait), "addback", this, false, false);
        if (pickup != null) {
            pickup.pickupCooldown = 0;
        }
    }

    @Override
    public void stopFishing() {
        this.stopAttack();
    }

    @Override
    public void showFishingWaitAnimation(FishingRodItem fishingRod, int targetX, int targetY) {
        this.showItemAttack(FishingPoleHolding.setGNDData(new InventoryItem("fishinghold"), fishingRod), targetX, targetY, 0, 0, new GNDItemMap());
    }

    @Override
    public boolean isFishingSwingDone() {
        this.getAttackAnimProgress();
        return !this.isAttacking;
    }

    @Override
    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        ServerClient client = this.getServerClient();
        return client == null ? spot.getBiome().getFishingLootTable(spot) : client.getFishingLoot(spot);
    }

    @Override
    public void restore() {
        super.restore();
        this.equipmentBuffManager.updateArmorBuffs();
        this.equipmentBuffManager.updateCosmeticSetBonus();
        this.equipmentBuffManager.updateTrinketBuffs();
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        this.inv.closeTempInventories();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.getInv().dispose();
        if (this.activeSetBuffAbility != null) {
            this.activeSetBuffAbility.onStopped(this);
            this.activeSetBuffAbility = null;
        }
        if (this.activeTrinketBuffAbility != null) {
            this.activeTrinketBuffAbility.onStopped(this);
            this.activeTrinketBuffAbility = null;
        }
        if (this.activeMountAbility != null) {
            this.activeMountAbility.onStopped(this);
            this.activeMountAbility = null;
        }
    }
}

