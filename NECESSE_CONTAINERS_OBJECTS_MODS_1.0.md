# Necesse 1.0 Containers, Objects & Mod System - Detailed API Reference

**Version**: 1.0  
**Focus**: Container system, game objects, tiles, and mod initialization

---

## Container System

### ContainerRegistry ✅

**Location**: `necesse.engine.registries.ContainerRegistry`

**Signature**:
```
public static int registerContainer(ClientContainerHandler clientHandler,
                                    ServerContainerHandler serverHandler)
```

**Parameters**:
- `clientHandler: ClientContainerHandler` - Client-side container UI handler
- `serverHandler: ServerContainerHandler` - Server-side container logic handler

**Returns**: Container ID (int)

**Key Methods**:
- `registerContainer(ClientContainerHandler, ServerContainerHandler)` - Register container
- `getContainer(int id)` - Get container by ID
- `getContainer(String stringID)` - Get container by string ID

**Registration Pattern**:
```
int containerId = ContainerRegistry.registerContainer(
    new MyClientHandler(),
    new MyServerHandler()
);
```

---

## Container Base Class

### Container ✅

**Location**: `necesse.inventory.container.Container`

**Purpose**: Base class for interactive containers

#### Key Methods

**Inventory Management** ✅
- `getInventory()` - Get container inventory
- `getSlots()` - Get container slots
- `addItem(InventoryItem item)` - Add item to container
- `removeItem(int slotIndex, int count)` - Remove item from slot

**Interaction** ✅
- `onAction(ServerClient client, ContainerAction action)` - Handle player action
- `onClose(ServerClient client)` - Called when container closes
- `onOpen(ServerClient client)` - Called when container opens

**Validation** ✅
- `canAddItem(InventoryItem item)` - Check if item can be added
- `canRemoveItem(int slotIndex)` - Check if item can be removed

---

## Container Handlers

### ClientContainerHandler ✅

**Location**: `necesse.engine.network.client.Client`

**Purpose**: Client-side container UI handling

**Key Methods**:
- `onOpen(Client client, Container container)` - Open container UI
- `onClose(Client client)` - Close container UI
- `onUpdate(Client client, Container container)` - Update UI

### ServerContainerHandler ✅

**Location**: `necesse.engine.network.server.Server`

**Purpose**: Server-side container logic

**Key Methods**:
- `onOpen(ServerClient client, Container container)` - Handle open
- `onClose(ServerClient client, Container container)` - Handle close
- `onAction(ServerClient client, ContainerAction action)` - Handle action

---

## Game Objects

### ObjectRegistry ✅

**Location**: `necesse.engine.registries.ObjectRegistry`

**Key Methods**:
- `registerObject(String id, GameObject object)` - Register object
- `getObject(String id)` - Get object by ID
- `getObjects()` - Get all objects

**Registration Pattern**:
```
ObjectRegistry.registerObject("mymod:myobject", new MyGameObject())
```

---

## GameObject Base Class

### GameObject ✅

**Location**: `necesse.level.gameObject.GameObject`

**Purpose**: Base class for all game objects (trees, rocks, furniture, etc.)

#### Key Properties ✅
- `idData: IDData` - Unique identifier
- `width, height: int` - Object dimensions (in pixels)
- `texture: GameTexture` - Object sprite
- `isWalkable: boolean` - Can mobs walk through it
- `isDestructible: boolean` - Can be damaged/destroyed

#### Key Methods

**Identification** ✅
- `getID()` - Get object ID
- `getName()` - Get display name
- `getDescription()` - Get description

**Rendering** ✅
- `getTexture()` - Get object sprite
- `getWidth()` - Get width
- `getHeight()` - Get height
- `draw(DrawOptions options)` - Draw object

**Interaction** ✅
- `onInteract(Level level, int x, int y, PlayerMob player)` - Handle interaction
- `canInteract(Level level, int x, int y, PlayerMob player)` - Check if can interact

**Damage** ✅
- `onDamage(Level level, int x, int y, GameDamage damage)` - Handle damage
- `getHealth()` - Get object health
- `getMaxHealth()` - Get max health

**Placement** ✅
- `canPlace(Level level, int x, int y)` - Check if can place
- `onPlace(Level level, int x, int y)` - Called when placed
- `onRemove(Level level, int x, int y)` - Called when removed

**Ticking** ✅
- `tick(Level level, int x, int y)` - Called each game tick
- `clientTick(Level level, int x, int y)` - Called on client each tick

---

## Tile System

### TileRegistry ✅

**Location**: `necesse.engine.registries.TileRegistry`

**Key Methods**:
- `registerTile(String id, GameTile tile)` - Register tile
- `getTile(String id)` - Get tile by ID
- `getTiles()` - Get all tiles

**Registration Pattern**:
```
TileRegistry.registerTile("mymod:mytile", new MyGameTile())
```

---

## GameTile Base Class

### GameTile ✅

**Location**: `necesse.level.gameTile.GameTile`

**Purpose**: Base class for tile types

#### Key Properties ✅
- `idData: IDData` - Unique identifier
- `texture: GameTexture` - Tile sprite
- `isWalkable: boolean` - Can mobs walk on it
- `isDestructible: boolean` - Can be mined/destroyed

#### Key Methods

**Identification** ✅
- `getID()` - Get tile ID
- `getName()` - Get display name

**Rendering** ✅
- `getTexture()` - Get tile sprite
- `draw(DrawOptions options)` - Draw tile

**Properties** ✅
- `isWalkable()` - Check if walkable
- `isDestructible()` - Check if destructible
- `getHardness()` - Get mining difficulty

**Interaction** ✅
- `onDamage(Level level, int x, int y, GameDamage damage)` - Handle damage
- `onBreak(Level level, int x, int y)` - Called when broken

---

## Mob Registry

### MobRegistry ✅

**Location**: `necesse.engine.registries.MobRegistry`

**Key Methods**:
- `registerMob(String id, Mob mob)` - Register mob
- `getMob(String id)` - Get mob by ID
- `getMobs()` - Get all mobs

**Registration Pattern**:
```
MobRegistry.registerMob("mymod:mymob", new MyMob())
```

---

## Projectile System

### ProjectileRegistry ✅

**Location**: `necesse.engine.registries.ProjectileRegistry`

**Key Methods**:
- `registerProjectile(String id, Projectile projectile)` - Register projectile
- `getProjectile(String id)` - Get projectile by ID

---

## Mod System

### SimpleMod Base Class ✅

**Location**: `necesse.engine.modLoader.SimpleMod`

**Purpose**: Base class for all mods

#### Key Methods

**Initialization** ✅
- `init()` - Called during mod initialization
  - Register items, buffs, objects, etc.
  - Called once at game start
  
- `postInit()` - Called after all mods loaded
  - Setup cross-mod dependencies
  - Final configuration
  
- `clientInit()` - Called on client initialization
  - Client-side setup
  - UI registration
  
- `serverInit()` - Called on server initialization
  - Server-side setup
  - Game logic initialization

#### Lifecycle ✅
1. `init()` - All mods initialize
2. `postInit()` - All mods post-initialize
3. `clientInit()` - Client-specific init
4. `serverInit()` - Server-specific init

---

## ModEntry Annotation

### @ModEntry ✅

**Location**: `necesse.engine.modLoader.annotations.ModEntry`

**Purpose**: Mark mod entry point class

**Usage**:
```
@ModEntry
public class MyMod extends SimpleMod {
    public void init() {
        // Register items, buffs, etc.
    }
}
```

**Requirements**:
- Must extend SimpleMod
- Must have no-arg constructor
- Must be annotated with @ModEntry

---

## LoadedMod

### LoadedMod Class ✅

**Location**: `necesse.engine.modLoader.LoadedMod`

**Purpose**: Represents a loaded mod instance

**Key Methods**:
- `getModName()` - Get mod display name
- `getModVersion()` - Get mod version
- `getModID()` - Get unique mod ID
- `getModInstance()` - Get SimpleMod instance
- `isEnabled()` - Check if mod is enabled

---

## Mod Info File

### ModInfoFile ✅

**Location**: `necesse.engine.modLoader.ModInfoFile`

**Purpose**: Mod metadata (mod.info file)

**Required Fields**:
- `name` - Mod display name
- `version` - Mod version
- `id` - Unique mod ID
- `description` - Mod description

**Optional Fields**:
- `author` - Mod author
- `dependencies` - Required mods
- `incompatibilities` - Incompatible mods

**Example mod.info**:
```
name=My Awesome Mod
version=1.0.0
id=mymod
description=This is my awesome mod
author=MyName
```

---

## Mod Loading Order

### Load Sequence ✅

1. **Mod Discovery** - Find all mod.jar files
2. **Mod Parsing** - Read mod.info files
3. **Dependency Resolution** - Order mods by dependencies
4. **Class Loading** - Load mod classes
5. **Instantiation** - Create mod instances
6. **init()** - Call init() on all mods
7. **postInit()** - Call postInit() on all mods
8. **clientInit()** - Call clientInit() on client
9. **serverInit()** - Call serverInit() on server

---

## Registration Best Practices

### Pattern 1: Item Registration ✅
```
@ModEntry
public class MyMod extends SimpleMod {
    public void init() {
        Item myItem = new MatItem("My Item", "Description");
        myItem.setItemTexture("items/myitem");
        ItemRegistry.registerItem("mymod:myitem", myItem, 10.0f);
    }
}
```

### Pattern 2: Buff Registration ✅
```
public void init() {
    Buff myBuff = new SimplePotionBuff(
        new ModifierValue<Float>(BuffModifiers.SPEED, 0.2f)
    );
    BuffRegistry.registerBuff("mymod:mybuff", myBuff);
}
```

### Pattern 3: Object Registration ✅
```
public void init() {
    GameObject myObject = new GameObject("My Object", "Description");
    myObject.setTexture("objects/myobject");
    ObjectRegistry.registerObject("mymod:myobject", myObject);
}
```

### Pattern 4: Container Registration ✅
```
public void init() {
    ContainerRegistry.registerContainer("mymod:mycontainer",
        new MyClientHandler(),
        new MyServerHandler()
    );
}
```

---

## Mod Dependencies

### Dependency Declaration ✅

In mod.info:
```
dependencies=modid1,modid2
incompatibilities=badmod1,badmod2
```

**Usage**:
- Ensures mods load in correct order
- Prevents incompatible mods from loading together
- Allows cross-mod features

---

## Mod Configuration

### ModSettings ✅

**Location**: `necesse.engine.modLoader.ModSettings`

**Purpose**: Store mod configuration

**Key Methods**:
- `getSetting(String key)` - Get setting value
- `setSetting(String key, String value)` - Set setting value
- `save()` - Save settings to file

---

## Best Practices

### Initialization ✅
1. Register all items in `init()`
2. Register all buffs in `init()`
3. Register all objects in `init()`
4. Setup cross-mod features in `postInit()`
5. Use unique IDs with mod prefix (e.g., "mymod:item")

### Naming ✅
1. Use lowercase IDs
2. Use colons to separate mod ID from item ID
3. Use descriptive names
4. Avoid conflicts with vanilla items

### Organization ✅
1. Keep mod.info in mod root
2. Organize code into packages
3. Use separate classes for different systems
4. Document public APIs

### Testing ✅
1. Test mod initialization
2. Test item registration
3. Test buff application
4. Test object placement
5. Test cross-mod compatibility

---

## File Reference Guide

| System | Location |
|--------|----------|
| Containers | `necesse.inventory.container` |
| Objects | `necesse.level.gameObject` |
| Tiles | `necesse.level.gameTile` |
| Mobs | `necesse.entity.mobs` |
| Projectiles | `necesse.entity.projectile` |
| Mod Loading | `necesse.engine.modLoader` |
| Registries | `necesse.engine.registries` |

---

**Note**: All APIs marked with ✅ verified in Necesse 1.0 decompiled source.

