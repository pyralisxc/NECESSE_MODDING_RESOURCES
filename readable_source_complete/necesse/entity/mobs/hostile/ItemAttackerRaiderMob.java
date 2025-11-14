/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.RaiderMobPhase;
import necesse.entity.mobs.StaticItemAttackSlot;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.SetMoveToTileAIEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.MoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RaiderItemFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerPlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToTile;
import necesse.entity.mobs.hostile.HostileItemAttackerMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.SwingSpriteAttackItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ItemAttackerRaiderMob
extends HostileItemAttackerMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface(){

        @Override
        public void addPossibleLoot(LootList list, Object ... extra) {
            ItemAttackerRaiderMob self = LootTable.expectExtra(ItemAttackerRaiderMob.class, extra, 0);
            if (self != null && self.carryingLoot != null) {
                list.add(self.carryingLoot.item);
            }
        }

        @Override
        public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
            ItemAttackerRaiderMob self = LootTable.expectExtra(ItemAttackerRaiderMob.class, extra, 0);
            if (self != null && self.carryingLoot != null) {
                list.add(self.carryingLoot);
            }
        }
    }, new LootItemInterface(){

        @Override
        public void addPossibleLoot(LootList list, Object ... extra) {
            ItemAttackerRaiderMob self = LootTable.expectExtra(ItemAttackerRaiderMob.class, extra, 0);
            if (self != null) {
                if (self.droppedWeapon != null) {
                    if (ItemRegistry.isObtainable(self.droppedWeapon.item.getID()) && self.weaponDropTickets > 0) {
                        list.add(self.droppedWeapon.item);
                    }
                } else if (self.weapon != null && ItemRegistry.isObtainable(self.weapon.item.getID()) && self.weaponDropTickets > 0) {
                    list.add(self.weapon.item);
                }
                if (self.canEquipArmor) {
                    if (self.helmet != null && ItemRegistry.isObtainable(self.helmet.item.getID()) && self.helmetDropTickets > 0) {
                        list.add(self.helmet.item);
                    }
                    if (self.chest != null && ItemRegistry.isObtainable(self.chest.item.getID()) && self.chestDropTickets > 0) {
                        list.add(self.chest.item);
                    }
                    if (self.boots != null && ItemRegistry.isObtainable(self.boots.item.getID()) && self.bootsDropTickets > 0) {
                        list.add(self.boots.item);
                    }
                }
            }
        }

        @Override
        public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
            ItemAttackerRaiderMob self = LootTable.expectExtra(ItemAttackerRaiderMob.class, extra, 0);
            if (random.getChance(0.25f)) {
                InventoryItem item;
                TicketSystemList items = new TicketSystemList();
                if (self != null) {
                    if (self.droppedWeapon != null) {
                        if (ItemRegistry.isObtainable(self.droppedWeapon.item.getID()) && self.weaponDropTickets > 0) {
                            items.addObject(self.weaponDropTickets, self.droppedWeapon.copy());
                        }
                    } else if (self.weapon != null && ItemRegistry.isObtainable(self.weapon.item.getID()) && self.weaponDropTickets > 0) {
                        items.addObject(self.weaponDropTickets, self.weapon.copy());
                    }
                    if (self.canEquipArmor) {
                        if (self.helmet != null && ItemRegistry.isObtainable(self.helmet.item.getID()) && self.helmetDropTickets > 0) {
                            items.addObject(self.helmetDropTickets, self.helmet.copy());
                        }
                        if (self.chest != null && ItemRegistry.isObtainable(self.chest.item.getID()) && self.chestDropTickets > 0) {
                            items.addObject(self.chestDropTickets, self.chest.copy());
                        }
                        if (self.boots != null && ItemRegistry.isObtainable(self.boots.item.getID()) && self.bootsDropTickets > 0) {
                            items.addObject(self.bootsDropTickets, self.boots.copy());
                        }
                    }
                }
                if ((item = (InventoryItem)items.getRandomObject(random)) != null) {
                    list.add(item);
                }
            }
        }
    }, new ChanceLootItem(0.01f, "stormingthehamletpart1vinyl"), new ChanceLootItem(0.01f, "stormingthehamletpart2vinyl"));
    public final boolean canEquipArmor;
    public int lookSeed = GameRandom.globalRandom.nextInt();
    public Point preparingTile;
    public Point attackTile;
    public int raidingStartTimer;
    public int raidingGroup;
    public int raidEventUniqueID = 0;
    private SettlementRaidLevelEvent raidEvent;
    public RaiderMobPhase phase = RaiderMobPhase.PREPARING;
    public float difficultyModifier = 1.0f;
    public InventoryItem weapon;
    public InventoryItem droppedWeapon;
    public float skillPercent = 0.8f;
    public int weaponDropTickets = 150;
    public InventoryItem helmet;
    public InventoryItem chest;
    public InventoryItem boots;
    public boolean isArmorCosmetic;
    public int helmetDropTickets = 100;
    public int chestDropTickets = 100;
    public int bootsDropTickets = 100;
    public double weaponValue;
    public double armorValue;
    public InventoryItem carryingLoot;
    public int carryingLootTimer;
    public boolean combatTriggered = false;
    protected int noTargetFoundCounter;
    protected final CarryingLootMobAbility setCarryingLoot;
    public EquipmentBuffManager equipmentBuffManager = new EquipmentBuffManager(this){

        @Override
        public InventoryItem getArmorItem(int slot) {
            if (ItemAttackerRaiderMob.this.isArmorCosmetic) {
                return null;
            }
            switch (slot) {
                case 0: {
                    return ItemAttackerRaiderMob.this.helmet;
                }
                case 1: {
                    return ItemAttackerRaiderMob.this.chest;
                }
                case 2: {
                    return ItemAttackerRaiderMob.this.boots;
                }
            }
            return null;
        }

        @Override
        public InventoryItem getCosmeticItem(int slot) {
            switch (slot) {
                case 0: {
                    return ItemAttackerRaiderMob.this.helmet;
                }
                case 1: {
                    return ItemAttackerRaiderMob.this.chest;
                }
                case 2: {
                    return ItemAttackerRaiderMob.this.boots;
                }
            }
            return null;
        }

        @Override
        public ArrayList<InventoryItem> getTrinketItems() {
            return new ArrayList<InventoryItem>();
        }
    };

    public ItemAttackerRaiderMob(boolean canEquipArmor) {
        super(100);
        this.canEquipArmor = canEquipArmor;
        this.shouldSave = false;
        this.isUsingItemsForArmorAndHealth = true;
        this.setCarryingLoot = this.registerAbility(new CarryingLootMobAbility());
    }

    @Override
    public void addSaveData(SaveData save) {
        int armor;
        SaveData weaponSave;
        super.addSaveData(save);
        save.addInt("lookSeed", this.lookSeed);
        if (this.weapon != null) {
            weaponSave = new SaveData("weapon");
            this.weapon.addSaveData(weaponSave);
            save.addSaveData(weaponSave);
        }
        if (this.droppedWeapon != null) {
            weaponSave = new SaveData("droppedWeapon");
            this.droppedWeapon.addSaveData(weaponSave);
            save.addSaveData(weaponSave);
        }
        if (this.canEquipArmor) {
            if (this.helmet != null) {
                SaveData helmetSave = new SaveData("helmet");
                this.helmet.addSaveData(helmetSave);
                save.addSaveData(helmetSave);
            }
            if (this.chest != null) {
                SaveData chestSave = new SaveData("chest");
                this.chest.addSaveData(chestSave);
                save.addSaveData(chestSave);
            }
            if (this.boots != null) {
                SaveData bootsSave = new SaveData("boots");
                this.boots.addSaveData(bootsSave);
                save.addSaveData(bootsSave);
            }
            save.addBoolean("isArmorCosmetic", this.isArmorCosmetic);
        }
        if ((armor = this.getArmorFlat()) != 0) {
            save.addInt("armor", armor);
        }
        save.addDouble("weaponValue", this.weaponValue);
        save.addDouble("armorValue", this.armorValue);
        if (this.carryingLoot != null) {
            SaveData lootSave = new SaveData("carryingLoot");
            this.carryingLoot.addSaveData(lootSave);
            save.addSaveData(lootSave);
            save.addInt("carryingLootTimer", this.carryingLootTimer);
        }
        save.addEnum("phase", this.phase);
        if (this.preparingTile != null) {
            save.addPoint("preparingTile", this.preparingTile);
        }
        if (this.attackTile != null) {
            save.addPoint("attackTile", this.attackTile);
        }
        save.addInt("raidingStartTimer", this.raidingStartTimer);
        save.addInt("raidingGroup", this.raidingGroup);
        save.addFloat("difficultyModifier", this.difficultyModifier);
        save.addInt("raidEventUniqueID", this.raidEventUniqueID);
    }

    @Override
    public void applyLoadData(LoadData save) {
        int armor;
        LoadData droppedWeaponSave;
        super.applyLoadData(save);
        this.lookSeed = save.getInt("lookSeed", this.lookSeed);
        LoadData weaponSave = save.getFirstLoadDataByName("weapon");
        if (weaponSave != null && !weaponSave.isData()) {
            this.weapon = InventoryItem.fromLoadData(weaponSave);
        }
        if ((droppedWeaponSave = save.getFirstLoadDataByName("droppedWeapon")) != null && !droppedWeaponSave.isData()) {
            this.droppedWeapon = InventoryItem.fromLoadData(droppedWeaponSave);
        }
        if (this.canEquipArmor) {
            LoadData bootsSave;
            LoadData chestSave;
            LoadData helmetSave = save.getFirstLoadDataByName("helmet");
            if (helmetSave != null) {
                this.helmet = InventoryItem.fromLoadData(helmetSave);
            }
            if ((chestSave = save.getFirstLoadDataByName("chest")) != null) {
                this.chest = InventoryItem.fromLoadData(chestSave);
            }
            if ((bootsSave = save.getFirstLoadDataByName("boots")) != null) {
                this.boots = InventoryItem.fromLoadData(bootsSave);
            }
            this.isArmorCosmetic = save.getBoolean("isArmorCosmetic", this.isArmorCosmetic, false);
        }
        if ((armor = save.getInt("armor", 0, false)) != 0) {
            this.setArmor(armor);
        }
        this.weaponValue = save.getDouble("weaponValue", this.weaponValue, false);
        this.armorValue = save.getDouble("armorValue", this.armorValue, false);
        LoadData lootSave = save.getFirstLoadDataByName("carryingLoot");
        if (lootSave != null && !lootSave.isData()) {
            this.carryingLoot = InventoryItem.fromLoadData(lootSave);
            this.carryingLootTimer = save.getInt("carryingLootTimer", 0, false);
        }
        this.phase = save.getEnum(RaiderMobPhase.class, "phase", RaiderMobPhase.ESCAPING, false);
        this.preparingTile = save.getPoint("preparingTile", this.preparingTile, false);
        this.attackTile = save.getPoint("attackTile", this.attackTile, false);
        this.raidingStartTimer = save.getInt("raidingStartTimer", this.raidingStartTimer, false);
        if (this.raidingStartTimer == 0) {
            this.raidingStartTimer = save.getInt("raidingStartTicks", 0, false) * 50;
        }
        this.raidingGroup = save.getInt("raidingGroup", this.raidingGroup);
        this.difficultyModifier = save.getFloat("difficultyModifier", this.difficultyModifier);
        this.raidEventUniqueID = save.getInt("raidEventUniqueID", this.raidEventUniqueID);
        this.updateAIAndLook();
        this.updateArmor();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lookSeed = reader.getNextInt();
        this.weapon = InventoryItem.fromContentPacket(reader);
        this.droppedWeapon = InventoryItem.fromContentPacket(reader);
        if (this.canEquipArmor) {
            this.helmet = InventoryItem.fromContentPacket(reader);
            this.chest = InventoryItem.fromContentPacket(reader);
            this.boots = InventoryItem.fromContentPacket(reader);
            this.isArmorCosmetic = reader.getNextBoolean();
        }
        this.setArmor(reader.getNextInt());
        this.carryingLoot = InventoryItem.fromContentPacket(reader);
        if (this.carryingLoot != null) {
            this.carryingLootTimer = reader.getNextInt();
        }
        this.updateAIAndLook();
        this.updateArmor();
        this.phase = reader.getNextEnum(RaiderMobPhase.class);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lookSeed);
        InventoryItem.addPacketContent(this.weapon, writer);
        InventoryItem.addPacketContent(this.droppedWeapon, writer);
        if (this.canEquipArmor) {
            InventoryItem.addPacketContent(this.helmet, writer);
            InventoryItem.addPacketContent(this.chest, writer);
            InventoryItem.addPacketContent(this.boots, writer);
            writer.putNextBoolean(this.isArmorCosmetic);
        }
        writer.putNextInt(this.getArmorFlat());
        InventoryItem.addPacketContent(this.carryingLoot, writer);
        if (this.carryingLoot != null) {
            writer.putNextInt(this.carryingLootTimer);
        }
        writer.putNextEnum(this.phase);
    }

    @Override
    public void init() {
        NetworkSettlementData settlement;
        super.init();
        if (this.attackTile == null && this.isServer() && (settlement = this.getNetworkSettlementData()) != null) {
            this.setAttackTile(new Point(settlement.getTileX(), settlement.getTileY()));
        }
        this.updateAIAndLook();
        this.updateArmor();
    }

    public void updateAIAndLook() {
        this.ai = new BehaviourTreeAI<ItemAttackerRaiderMob>(this, new ItemAttackerRaiderAI(), new AIMover(HumanMob.humanPathIterations));
    }

    public void updateArmor() {
        ActiveBuff equipmentBuff;
        if (this.canEquipArmor) {
            this.equipmentBuffManager.updateArmorBuffs();
            this.equipmentBuffManager.updateCosmeticSetBonus();
            this.equipmentBuffManager.updateTrinketBuffs();
        }
        float healthPerArmorValue = 20.0f;
        int lastHealth = this.getHealth();
        float healthPercent = (float)this.getHealth() / (float)this.getMaxHealth();
        int armor = this.canEquipArmor && !this.isArmorCosmetic ? ((equipmentBuff = this.buffManager.getBuff(BuffRegistry.EQUIPMENT_BUFF)) == null ? 0 : equipmentBuff.getModifier(BuffModifiers.ARMOR_FLAT)) : this.getArmorFlat();
        int maxHealth = (int)Math.max(50.0f, (float)armor * healthPerArmorValue);
        this.setMaxHealth(maxHealth);
        this.setHealthHidden(Math.max(lastHealth, (int)((float)this.getMaxHealth() * healthPercent)));
    }

    @Override
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        if (this.weapon == null) {
            return null;
        }
        return new StaticItemAttackSlot(this.weapon);
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        if (slotType != null) {
            switch (slotType) {
                case HELMET: {
                    if (!this.canEquipArmor || this.isArmorCosmetic) {
                        return true;
                    }
                    return this.helmet != null && this.helmet.item.getID() == item.getID();
                }
                case CHEST: {
                    if (!this.canEquipArmor || this.isArmorCosmetic) {
                        return true;
                    }
                    return this.chest != null && this.chest.item.getID() == item.getID();
                }
                case FEET: {
                    if (!this.canEquipArmor || this.isArmorCosmetic) {
                        return true;
                    }
                    return this.boots != null && this.boots.item.getID() == item.getID();
                }
            }
        }
        return this.weapon != null && this.weapon.item.getID() == item.getID();
    }

    @Override
    public float getWeaponSkillPercent(InventoryItem item) {
        return this.skillPercent;
    }

    public double getMaxWeaponOrArmorValue() {
        return Math.max(this.weaponValue, this.armorValue);
    }

    @Override
    public void serverTick() {
        SettlementRaidLevelEvent raidEvent;
        super.serverTick();
        if (this.carryingLoot != null) {
            this.carryingLootTimer += 50;
        }
        if (this.raidEventUniqueID != 0 && !this.combatTriggered && this.isInCombat() && (raidEvent = this.getRaidEvent()) != null) {
            this.combatTriggered = true;
            raidEvent.triggerCombatEvent();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.carryingLoot != null) {
            this.carryingLootTimer += 50;
        }
        if (this.canEquipArmor) {
            this.equipmentBuffManager.clientTickEffects();
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return super.canBePushed(other) && this.carryingLootTimer <= 45000;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
        }
        return null;
    }

    @Override
    public boolean shouldSave() {
        return this.phase != RaiderMobPhase.ESCAPING || this.carryingLoot != null;
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return new MobSpawnLocation(this, targetX, targetY).checkNotLevelCollides().validAndApply();
    }

    @Override
    public boolean canDespawn() {
        if (this.shouldSave()) {
            return false;
        }
        return super.canDespawn();
    }

    public void makeRaider(SettlementRaidLevelEvent event, Point preparingTile, Point attackTile, int raidingStartTimer, int raidingGroup, float difficultyModifier) {
        this.phase = RaiderMobPhase.PREPARING;
        this.raidingStartTimer = raidingStartTimer;
        this.raidingGroup = raidingGroup;
        this.preparingTile = preparingTile;
        this.setAttackTile(attackTile);
        this.difficultyModifier = difficultyModifier;
        this.raidEventUniqueID = event == null ? 0 : event.getUniqueID();
        this.raidEvent = event;
    }

    public void setAttackTile(Point tile) {
        this.attackTile = tile;
        this.ai.blackboard.submitEvent("setMoveToTile", new SetMoveToTileAIEvent(this.getMoveToTile(), true));
    }

    protected MoveToTile getMoveToTile() {
        if (this.attackTile == null) {
            return null;
        }
        return new MoveToTile(this.attackTile.x, this.attackTile.y, true){

            @Override
            public boolean moveIfPathFailed(float tileDistance) {
                return true;
            }

            @Override
            public boolean isAtLocation(float tileDistance, boolean foundPath) {
                return tileDistance <= 5.0f;
            }

            @Override
            public void onArrivedAtLocation() {
                ItemAttackerRaiderMob.this.setAttackTile(null);
            }
        };
    }

    public SettlementRaidLevelEvent getRaidEvent() {
        LevelEvent event;
        if (this.raidEvent != null) {
            return this.raidEvent;
        }
        if (this.raidEventUniqueID != 0 && (event = this.getLevel().entityManager.events.get(this.raidEventUniqueID, false)) instanceof SettlementRaidLevelEvent) {
            this.raidEvent = (SettlementRaidLevelEvent)event;
        }
        return this.raidEvent;
    }

    public ServerSettlementData getServerSettlementData() {
        SettlementRaidLevelEvent raidEvent = this.getRaidEvent();
        if (raidEvent != null) {
            return raidEvent.getServerSettlementData();
        }
        return null;
    }

    public NetworkSettlementData getNetworkSettlementData() {
        SettlementRaidLevelEvent raidEvent = this.getRaidEvent();
        if (raidEvent != null) {
            return raidEvent.getNetworkSettlementData();
        }
        return null;
    }

    public int getRaidingStartTimer() {
        return this.raidingStartTimer;
    }

    public void updateRaidingStartTimer(int raidingStartTimer) {
        this.raidingStartTimer = raidingStartTimer;
    }

    public void setRaiderPhase(RaiderMobPhase phase) {
        this.phase = phase;
    }

    public void setWeapon(InventoryItem weapon) {
        this.weapon = weapon;
    }

    public void setDroppedWeapon(InventoryItem droppedWeapon) {
        this.droppedWeapon = droppedWeapon;
    }

    public void setArmorItems(InventoryItem helmet, InventoryItem chest, InventoryItem boots) {
        if (this.canEquipArmor) {
            this.helmet = helmet;
            this.chest = chest;
            this.boots = boots;
        } else {
            int equipmentArmor = 0;
            if (helmet != null && helmet.item.isArmorItem()) {
                equipmentArmor += ((ArmorItem)helmet.item).getTotalArmorValue(helmet, this);
            }
            if (chest != null && chest.item.isArmorItem()) {
                equipmentArmor += ((ArmorItem)chest.item).getTotalArmorValue(chest, this);
            }
            if (boots != null && boots.item.isArmorItem()) {
                equipmentArmor += ((ArmorItem)boots.item).getTotalArmorValue(boots, this);
            }
            this.setArmor(equipmentArmor);
        }
    }

    public void setArmorBasedOnEquipment(InventoryItem helmet, InventoryItem chest, InventoryItem boots) {
        int equipmentArmor = 0;
        if (helmet != null && helmet.item.isArmorItem()) {
            equipmentArmor += ((ArmorItem)helmet.item).getTotalArmorValue(helmet, this);
        }
        if (chest != null && chest.item.isArmorItem()) {
            equipmentArmor += ((ArmorItem)chest.item).getTotalArmorValue(chest, this);
        }
        if (boots != null && boots.item.isArmorItem()) {
            equipmentArmor += ((ArmorItem)boots.item).getTotalArmorValue(boots, this);
        }
        this.setArmor(equipmentArmor);
    }

    public boolean wouldLikeToStartLooting() {
        return this.noTargetFoundCounter >= 100;
    }

    @Override
    public float getOutgoingDamageModifier() {
        WorldSettings worldSettings = this.getWorldSettings();
        float modifier = worldSettings == null ? 1.0f : worldSettings.difficulty.raiderDamageModifier;
        return super.getOutgoingDamageModifier() * modifier;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        ModifierValue<Float> slow = new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f));
        ModifierValue<Float> knockback = new ModifierValue<Float>(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.0f));
        if (this.carryingLoot != null) {
            slow = slow.min(Float.valueOf(0.25f), Integer.MAX_VALUE);
            if (this.carryingLootTimer > 30000) {
                knockback = knockback.min(Float.valueOf(1.0f), Integer.MAX_VALUE);
            }
        }
        return Stream.concat(super.getDefaultModifiers(), Stream.of(slow, knockback, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(-0.5f))));
    }

    @Override
    public GameTooltips getMapTooltips() {
        StringTooltips tooltips = new StringTooltips(this.getDisplayName());
        if (this.carryingLoot != null) {
            tooltips.add(Localization.translate("misc", "raiderescaping", "item", this.carryingLoot.getAmount() + "x " + this.carryingLoot.getItemDisplayName()));
        }
        return tooltips;
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        super.addHoverTooltips(tooltips, debug);
        if (this.carryingLoot != null) {
            tooltips.add(Localization.translate("misc", "raiderescaping", "item", this.carryingLoot.getAmount() + "x " + this.carryingLoot.getItemDisplayName()));
        }
    }

    @Override
    protected void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        if (this.weapon != null) {
            tooltips.add("Weapon: " + this.weapon);
        }
        if (this.helmet != null) {
            tooltips.add("Helmet: " + this.helmet);
        }
        if (this.chest != null) {
            tooltips.add("Chest: " + this.chest);
        }
        if (this.boots != null) {
            tooltips.add("Boots: " + this.boots);
        }
        if (this.weaponValue != 0.0) {
            tooltips.add("Weapon value: " + this.weaponValue);
        }
        if (this.armorValue != 0.0) {
            tooltips.add("Armor value: " + this.armorValue);
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (attacker != null && attacker.isTrapAttacker()) {
            GameUtils.streamServerClients(this.getLevel()).filter(ServerClient::achievementsLoaded).forEach(c -> c.achievements().HOME_ALONE.markCompleted((ServerClient)c));
        }
    }

    public class CarryingLootMobAbility
    extends MobAbility {
        public void runAndSend(InventoryItem invItem) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            InventoryItem.addPacketContent(invItem, writer);
            if (invItem == null) {
                ItemAttackerRaiderMob.this.carryingLootTimer = 0;
            }
            writer.putNextInt(ItemAttackerRaiderMob.this.carryingLootTimer);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            ItemAttackerRaiderMob.this.carryingLoot = InventoryItem.fromContentPacket(reader);
            ItemAttackerRaiderMob.this.carryingLootTimer = reader.getNextInt();
        }
    }

    public static class ItemAttackerRaiderAI<T extends ItemAttackerRaiderMob>
    extends SelectorAINode<T> {
        public final EscapeAINode<T> escapeAINode;
        public final ItemAttackerPlayerChaserAI<T> playerChaserAI;
        public final WandererAINode<T> wandererAINode;

        public ItemAttackerRaiderAI() {
            this.addChild(new ConditionAINode<ItemAttackerRaiderMob>(new RaiderItemFinderAINode<T>(){

                @Override
                public float getMaxPickupValue(T mob) {
                    return (float)((Mob)mob).getMaxHealth() / 5.0f;
                }

                @Override
                public void onPickedUpItem(T mob, int tileX, int tileY, InventoryItem inventoryItem) {
                    InventoryItem showItem = SwingSpriteAttackItem.setup(new InventoryItem("swingspriteattack"), inventoryItem.item, true);
                    ((ItemAttackerMob)mob).showAttackAndSendAttacker(showItem, tileX * 32 + 16, tileY * 32 + 16, 0, Item.getRandomAttackSeed(GameRandom.globalRandom));
                    ((ItemAttackerRaiderMob)mob).setCarryingLoot.runAndSend(inventoryItem);
                    ((ItemAttackerRaiderMob)mob).phase = RaiderMobPhase.ESCAPING;
                }

                @Override
                public void onFoundItem(T mob, int tileX, int tileY, int slot, InventoryItem inventoryItem) {
                    super.onFoundItem(mob, tileX, tileY, slot, inventoryItem);
                    SettlementRaidLevelEvent raidEvent = ((ItemAttackerRaiderMob)mob).getRaidEvent();
                    if (raidEvent != null) {
                        raidEvent.reservedLoot.add(new Point(tileX, tileY), slot);
                    }
                }

                @Override
                public boolean isValidItem(T mob, int tileX, int tileY, int slot, InventoryItem inventoryItem) {
                    if (!super.isValidItem(mob, tileX, tileY, slot, inventoryItem)) {
                        return false;
                    }
                    SettlementRaidLevelEvent raidEvent = ((ItemAttackerRaiderMob)mob).getRaidEvent();
                    if (raidEvent != null) {
                        return !raidEvent.reservedLoot.contains(new Point(tileX, tileY), slot);
                    }
                    return true;
                }
            }, mob -> mob.phase == RaiderMobPhase.LOOTING, AINodeResult.FAILURE));
            this.escapeAINode = new EscapeAINode<T>(){

                @Override
                public void onEscaped(T mob) {
                    NetworkSettlementData settlement;
                    super.onEscaped(mob);
                    if (((ItemAttackerRaiderMob)mob).carryingLoot != null && (settlement = ((ItemAttackerRaiderMob)mob).getNetworkSettlementData()) != null) {
                        GameMessage settlementName = settlement.getSettlementName();
                        GameMessageBuilder itemString = new GameMessageBuilder().append(((ItemAttackerRaiderMob)mob).carryingLoot.getAmount() + "x " + TypeParsers.getItemParseString(((ItemAttackerRaiderMob)mob).carryingLoot) + " ").append(((ItemAttackerRaiderMob)mob).carryingLoot.getItemLocalization());
                        LocalMessage message = new LocalMessage("misc", "raiderescapedwith").addReplacement("raider", ((Mob)mob).getLocalization()).addReplacement("settlement", settlementName).addReplacement("item", itemString);
                        settlement.streamTeamMembers().forEach(c -> c.sendChatMessage(message));
                    }
                }

                @Override
                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    if (((ItemAttackerRaiderMob)mob).isHostile && !((ItemAttackerRaiderMob)mob).isSummoned && ((Entity)mob).getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING).booleanValue()) {
                        return true;
                    }
                    if (((ItemAttackerRaiderMob)mob).carryingLoot != null) {
                        return true;
                    }
                    return ((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.LOOTING || ((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.ESCAPING || ((ItemAttackerRaiderMob)mob).phase == null;
                }
            };
            this.addChild(this.escapeAINode);
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.PREPARING) {
                        if (((ItemAttackerRaiderMob)mob).raidingStartTimer > 0) {
                            ((ItemAttackerRaiderMob)mob).raidingStartTimer -= 50;
                        }
                        if (((ItemAttackerRaiderMob)mob).raidingStartTimer <= 0) {
                            ((ItemAttackerRaiderMob)mob).phase = RaiderMobPhase.RAIDING;
                            SettlementRaidLevelEvent raidEvent = ((ItemAttackerRaiderMob)mob).getRaidEvent();
                            if (raidEvent != null) {
                                raidEvent.startRaid(true);
                            }
                        }
                        for (AIWasHitEvent lastHit : blackboard.getLastHits()) {
                            ((Entity)mob).getLevel().entityManager.mobs.getInRegionByTileRange(((Entity)mob).getTileX(), ((Entity)mob).getTileY(), 25).stream().filter(m -> m instanceof ItemAttackerRaiderMob).forEach(m -> m.ai.blackboard.submitEvent("startRaid" + mob.raidingGroup, new AIEvent()));
                            ((ItemAttackerRaiderMob)mob).phase = RaiderMobPhase.RAIDING;
                            SettlementRaidLevelEvent raidEvent = ((ItemAttackerRaiderMob)mob).getRaidEvent();
                            if (raidEvent == null) continue;
                            raidEvent.startRaid(true);
                        }
                        for (AIEvent event : blackboard.getLastCustomEvents("startRaid" + ((ItemAttackerRaiderMob)mob).raidingGroup)) {
                            ((ItemAttackerRaiderMob)mob).phase = RaiderMobPhase.RAIDING;
                        }
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.playerChaserAI = new ItemAttackerPlayerChaserAI<T>(0, new InventoryItem("woodsword")){

                @Override
                public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    super.onRootSet(root, mob, blackboard);
                    blackboard.onEvent("itemAttackerUpdated", e -> {
                        ChaserAINode chaser = this.itemAttackerChaserAINode.getChaserAIIfExists();
                        if (chaser != null) {
                            chaser.moveIfFailedPath = (target, path) -> mob.getDistance((Mob)target) > 320.0f;
                        }
                    });
                }
            };
            this.addChild(this.playerChaserAI);
            this.playerChaserAI.targetFinderAINode.distance = new TargetFinderDistance<T>(0){

                @Override
                protected int getSearchDistanceFlat(T mob, Mob target) {
                    if (((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.LOOTING) {
                        return 384;
                    }
                    return ((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.RAIDING ? 768 : 512;
                }
            };
            this.playerChaserAI.targetFinderAINode.loseTargetMinCooldown = 1000;
            this.playerChaserAI.targetFinderAINode.loseTargetMaxCooldown = 2000;
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    Mob currentTarget;
                    if (((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.RAIDING && ((ItemAttackerRaiderMob)mob).attackTile == null && (currentTarget = blackboard.getObject(Mob.class, "currentTarget")) == null) {
                        ++((ItemAttackerRaiderMob)mob).noTargetFoundCounter;
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new ConditionAINode<ItemAttackerRaiderMob>(new MoveToAINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    super.onRootSet(root, mob, blackboard);
                    this.moveToTile = ((ItemAttackerRaiderMob)mob).getMoveToTile();
                }

                @Override
                public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
                    return super.tickNode(mob, blackboard);
                }
            }, mob -> mob.phase == RaiderMobPhase.RAIDING, AINodeResult.FAILURE));
            this.wandererAINode = new WandererAINode<T>(40000){

                @Override
                public WandererBaseOptions<T> getBaseOptions() {
                    return new WandererBaseOptions<T>(){

                        @Override
                        public Point getBaseTile(T mob) {
                            if (((ItemAttackerRaiderMob)mob).phase == RaiderMobPhase.PREPARING) {
                                return ((ItemAttackerRaiderMob)mob).preparingTile;
                            }
                            return null;
                        }
                    };
                }
            };
            this.addChild(this.wandererAINode);
            this.wandererAINode.searchRadius = 5;
        }
    }
}

