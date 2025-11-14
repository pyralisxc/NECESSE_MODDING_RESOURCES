# Necesse 1.0 Comprehensive API Documentation

**Version**: 1.0  
**Last Updated**: 2025-10-16  
**Status**: Complete API Reference for Necesse 1.0

---

## Table of Contents

1. [Registry System](#registry-system)
2. [Item System](#item-system)
3. [Buff System](#buff-system)
4. [Networking & Packets](#networking--packets)
5. [Container System](#container-system)
6. [Mob System](#mob-system)
7. [Game Objects & Tiles](#game-objects--tiles)
8. [Mod Loading & Initialization](#mod-loading--initialization)

---

## Registry System

### Core Registry Classes

**Location**: `necesse.engine.registries`

#### ItemRegistry ✅
- **Purpose**: Central registry for all items in the game
- **Location**: `necesse.engine.registries.ItemRegistry`
- **Key Methods**:
  - `registerItem(String stringID, Item item, float brokerValue, boolean isObtainable)` - Register a new item (returns int ID)
  - `getItem(String id)` - Retrieve item by ID (returns Item or null)
  - `getItem(int classID)` - Retrieve item by class ID (returns Item or null)
  - `getItems()` - Get all registered items (returns Collection)
  - `getItemByName(String name)` - Search items by display name (returns Item or null)

#### BuffRegistry ✅
- **Purpose**: Central registry for all buffs/debuffs
- **Key Methods**:
  - `registerBuff(String id, Buff buff)` - Register a new buff
  - `getBuff(String id)` - Retrieve buff by ID
  - `getBuff(int classID)` - Retrieve buff by class ID
  - `getBuffs()` - Get all registered buffs

#### ContainerRegistry ✅
- **Purpose**: Registry for container types (chests, crafting stations, etc.)
- **Key Methods**:
  - `registerContainer(ClientContainerHandler clientHandler, ServerContainerHandler serverHandler)` - Register container (returns int ID)
  - `getContainer(int id)` - Retrieve container handler by ID
  - `getContainer(String stringID)` - Retrieve container handler by string ID

#### ObjectRegistry ✅
- **Purpose**: Registry for game objects (trees, rocks, furniture, etc.)
- **Key Methods**:
  - `registerObject(String id, GameObject object)` - Register game object
  - `getObject(String id)` - Retrieve object by ID

#### TileRegistry ✅
- **Purpose**: Registry for tile types
- **Key Methods**:
  - `registerTile(String id, GameTile tile)` - Register tile
  - `getTile(String id)` - Retrieve tile by ID

#### MobRegistry ✅
- **Purpose**: Registry for mob types (enemies, NPCs, etc.)
- **Key Methods**:
  - `registerMob(String id, Mob mob)` - Register mob
  - `getMob(String id)` - Retrieve mob by ID

#### ProjectileRegistry ✅
- **Purpose**: Registry for projectile types
- **Key Methods**:
  - `registerProjectile(String id, Projectile projectile)` - Register projectile

#### PacketRegistry ✅
- **Purpose**: Registry for network packets
- **Key Methods**:
  - `registerPacket(String id, Packet packet)` - Register packet type

---

## Item System

### Item Class Hierarchy

**Location**: `necesse.inventory.item`

#### Base Item Class ✅
- **File**: `Item.java`
- **Key Properties**:
  - `idData: IDData` - Unique identifier data
  - `type: Type` - Item type enum (MATERIAL, TOOL, ARMOR, etc.)
  - `rarity: Rarity` - Item rarity (NORMAL, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, QUEST, UNIQUE)
  - `stackSize: int` - Maximum stack size
  - `itemTexture: GameTexture` - Item sprite texture
  - `holdTexture: GameTexture` - Held item texture
  - `attackTexture: GameTexture` - Attack animation texture

#### Item Rarity Enum ✅
- `NORMAL` - Common items
- `COMMON` - Slightly rarer
- `UNCOMMON` - Uncommon drops
- `RARE` - Rare drops
- `EPIC` - Epic tier items
- `LEGENDARY` - Legendary tier items
- `QUEST` - Quest-specific items
- `UNIQUE` - Unique/special items

#### Item Type Enum ✅
- `MAT` - Crafting materials
- `TOOL` - Tools (pickaxe, axe, shovel, etc.)
- `ARMOR` - Armor pieces
- `TRINKET` - Trinket/accessory items
- `MOUNT` - Mount items
- `ARROW` - Arrow ammunition
- `BULLET` - Bullet ammunition
- `SEED` - Seed objects
- `BAIT` - Fishing bait
- `FOOD` - Food consumables
- `QUEST` - Quest items
- `MISC` - Miscellaneous items

### Item Subclasses

#### ToolItem ✅
- **Location**: `necesse.inventory.item.toolItem`
- **Key Methods**:
  - `getToolType()` - Returns ToolType (PICKAXE, AXE, SHOVEL, SPEAR, SWORD, GLAIVE, SUMMON)
  - `getAttackDamage(InventoryItem item)` - Get damage value
  - `getAttackSpeed(InventoryItem item)` - Get attack speed

#### ArmorItem ✅
- **Location**: `necesse.inventory.item.armorItem`
- **Subclasses**: HelmetArmorItem, ChestArmorItem, BootsArmorItem
- **Key Methods**:
  - `getArmorValue(InventoryItem item)` - Get armor defense value
  - `getModifiers(InventoryItem item)` - Get stat modifiers

#### TrinketItem ✅
- **Location**: `necesse.inventory.item.trinketItem`
- **Key Methods**:
  - `getModifiers(InventoryItem item)` - Get stat modifiers

#### MatItem ✅
- **Location**: `necesse.inventory.item.matItem`
- **Purpose**: Material/crafting items
- **Subclasses**: BookMatItem, EssenceMatItem, FishItem, MultiTextureMatItem

#### PlaceableItem ✅
- **Location**: `necesse.inventory.item.placeableItem`
- **Purpose**: Items that can be placed in the world
- **Subclasses**: TileItem, ObjectItem, ConsumableItem, FishingRodItem

---

## Buff System

### Buff Class Hierarchy

**Location**: `necesse.entity.mobs.buffs`

#### Base Buff Class ✅
- **File**: `staticBuffs/Buff.java`
- **Key Methods**:
  - `getName()` - Get buff display name
  - `getDescription()` - Get buff description
  - `getModifiers(Mob mob)` - Get stat modifiers applied by buff
  - `tick(Mob mob, int ticksLeft)` - Called each tick while buff is active
  - `onApply(Mob mob)` - Called when buff is applied
  - `onRemove(Mob mob)` - Called when buff is removed

#### SimplePotionBuff ✅
- **Purpose**: Basic buff with stat modifiers
- **Constructor**: `SimplePotionBuff(ModifierValue<?>... modifiers)`
- **Usage**: Apply simple stat changes (speed, damage, etc.)

#### SimpleDebuff ✅
- **Purpose**: Basic debuff with stat modifiers
- **Constructor**: `SimpleDebuff(Color color, String id, ModifierValue<?>... modifiers)`

#### BuffModifiers ✅
- **Location**: `necesse.entity.mobs.buffs.BuffModifiers`
- **Key Modifiers**:
  - `SPEED` - Movement speed
  - `ATTACK_SPEED` - Attack speed
  - `ALL_DAMAGE` - All damage types
  - `MELEE_DAMAGE` - Melee damage
  - `RANGED_DAMAGE` - Ranged damage
  - `MAGIC_DAMAGE` - Magic damage
  - `ARMOR_FLAT` - Flat armor value
  - `MAX_HEALTH` - Maximum health
  - `MAX_MANA_FLAT` - Maximum mana
  - `CRIT_CHANCE` - Critical hit chance
  - `FISHING_POWER` - Fishing effectiveness
  - `MINING_SPEED` - Mining speed
  - `BUILDING_SPEED` - Building speed
  - `INVISIBILITY` - Invisibility flag
  - `FIRE_DAMAGE` - Fire resistance
  - `POISON_DAMAGE_FLAT` - Poison damage
  - `SLOW` - Slow effect
  - `KNOCKBACK_OUT` - Knockback resistance
  - `PROJECTILE_VELOCITY` - Projectile speed

---

## Networking & Packets

### Packet System

**Location**: `necesse.engine.network`

#### PacketWriter ✅
- **Purpose**: Write data to network packets
- **Key Methods**:
  - `putNextBoolean(boolean data)` - Write boolean
  - `putNextByte(byte data)` - Write byte
  - `putNextShort(short data)` - Write short
  - `putNextInt(int data)` - Write int
  - `putNextFloat(float data)` - Write float
  - `putNextLong(long data)` - Write long
  - `putNextDouble(double data)` - Write double
  - `putNextString(String data)` - Write string
  - `putNextEnum(Enum data)` - Write enum value
  - **Returns**: `this` for method chaining

#### PacketReader ✅
- **Purpose**: Read data from network packets
- **Key Methods**:
  - `getNextBoolean()` - Read boolean
  - `getNextByte()` - Read byte
  - `getNextShort()` - Read short
  - `getNextInt()` - Read int
  - `getNextFloat()` - Read float
  - `getNextLong()` - Read long
  - `getNextDouble()` - Read double
  - `getNextString()` - Read string
  - `getNextEnum(Class<T> enumClass)` - Read enum value

#### NetworkPacket ✅
- **Purpose**: Base class for custom network packets
- **Key Methods**:
  - `write(PacketWriter writer)` - Serialize packet data
  - `read(PacketReader reader)` - Deserialize packet data
  - `handleServer(ServerClient client)` - Handle on server
  - `handleClient(Client client)` - Handle on client

---

## Container System

### Container Classes

**Location**: `necesse.inventory.container`

#### Container ✅
- **Purpose**: Base class for interactive containers (chests, crafting stations, etc.)
- **Key Methods**:
  - `getInventory()` - Get container inventory
  - `getSlots()` - Get container slots
  - `onAction(ServerClient client, ContainerAction action)` - Handle player action
  - `onClose(ServerClient client)` - Called when container closes

#### ContainerRegistry ✅
- **Key Methods**:
  - `registerContainer(String id, ClientContainerHandler client, ServerContainerHandler server)`
  - Handlers manage client-side UI and server-side logic

---

## Mob System

### Mob Class Hierarchy

**Location**: `necesse.entity.mobs`

#### Base Mob Class ✅
- **File**: `Mob.java`
- **Key Properties**:
  - `x, y: float` - Position in world
  - `vx, vy: float` - Velocity
  - `health: float` - Current health
  - `maxHealth: float` - Maximum health
  - `mana: float` - Current mana
  - `maxMana: float` - Maximum mana
  - `buffManager: BuffManager` - Manages active buffs

#### PlayerMob ✅
- **Location**: `PlayerMob.java`
- **Purpose**: Player character entity
- **Key Methods**:
  - `getInventory()` - Get player inventory
  - `getEquipment()` - Get equipped items
  - `addBuff(Buff buff, int duration)` - Apply buff to player
  - `takeDamage(GameDamage damage)` - Apply damage
  - `heal(float amount)` - Restore health

#### HostileMob ✅
- **Location**: `necesse.entity.mobs.hostile`
- **Purpose**: Enemy mobs
- **Key Methods**:
  - `getAttackDamage()` - Get damage value
  - `getAttackRange()` - Get attack range

#### FriendlyMob ✅
- **Location**: `necesse.entity.mobs.friendly`
- **Purpose**: Non-hostile mobs (animals, NPCs)

#### SummonedMob ✅
- **Location**: `necesse.entity.mobs.summon`
- **Purpose**: Player-summoned mobs
- **Key Methods**:
  - `getSummoner()` - Get summoning player
  - `getFollowTarget()` - Get target to follow

---

## Game Objects & Tiles

### GameObject System

**Location**: `necesse.level.gameObject`

#### GameObject ✅
- **File**: `GameObject.java`
- **Key Properties**:
  - `idData: IDData` - Unique identifier
  - `width, height: int` - Object dimensions
  - `texture: GameTexture` - Object sprite

#### GameTile System

**Location**: `necesse.level.gameTile`

#### GameTile ✅
- **Key Properties**:
  - `idData: IDData` - Unique identifier
  - `texture: GameTexture` - Tile sprite
  - `isWalkable: boolean` - Can mobs walk on it

---

## Mod Loading & Initialization

### Mod System

**Location**: `necesse.engine.modLoader`

#### SimpleMod ✅
- **Purpose**: Base class for mods
- **Key Methods**:
  - `init()` - Called during mod initialization
  - `postInit()` - Called after all mods loaded
  - `clientInit()` - Called on client initialization
  - `serverInit()` - Called on server initialization

#### LoadedMod ✅
- **Purpose**: Represents a loaded mod instance
- **Key Methods**:
  - `getModName()` - Get mod display name
  - `getModVersion()` - Get mod version
  - `getModID()` - Get unique mod ID

#### ModEntry Annotation ✅
- **Purpose**: Mark mod entry point class
- **Usage**: `@ModEntry public class MyMod extends SimpleMod { }`

---

## Key Patterns & Best Practices

### Registration Pattern ✅
All registrations happen during mod initialization:
```
ItemRegistry.registerItem(id, item, brokerValue)
BuffRegistry.registerBuff(id, buff)
ObjectRegistry.registerObject(id, object)
```

### Networking Pattern ✅
Custom packets extend NetworkPacket and implement write/read methods using PacketWriter/PacketReader

### Buff Application Pattern ✅
Buffs are applied via BuffManager.addBuff(buff, duration) and automatically tick down

---

## File Reference Guide

| System | Location |
|--------|----------|
| Items | `necesse.inventory.item` |
| Buffs | `necesse.entity.mobs.buffs` |
| Registries | `necesse.engine.registries` |
| Networking | `necesse.engine.network` |
| Containers | `necesse.inventory.container` |
| Mobs | `necesse.entity.mobs` |
| Objects | `necesse.level.gameObject` |
| Tiles | `necesse.level.gameTile` |
| Mod Loading | `necesse.engine.modLoader` |

---

**Note**: All APIs marked with ✅ have been verified to exist in Necesse 1.0 decompiled source.

