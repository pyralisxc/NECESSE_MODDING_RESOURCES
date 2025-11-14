# Necesse 1.0 Item System - Detailed API Reference

**Version**: 1.0  
**Focus**: Item creation, registration, and usage patterns

---

## Item Registration

### ItemRegistry.registerItem() ✅

**Location**: `necesse.engine.registries.ItemRegistry`

**Signature** (Simplest):
```
public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable)
```

**Parameters**:
- `stringID: String` - Unique identifier for the item (e.g., "mymod:myitem")
- `item: Item` - Item instance to register
- `brokerValue: float` - Broker/merchant value for trading
- `isObtainable: boolean` - Whether item can be obtained in survival mode

**Returns**: Item ID (int)

**Extended Signatures** (Optional parameters):
```
registerItem(String stringID, Item item, float brokerValue, boolean isObtainable,
             boolean countInStats, String... isObtainedByOtherItemStringIDs)

registerItem(String stringID, Item item, float brokerValue, boolean isObtainable,
             boolean countInStats, boolean isObtainableInCreative,
             String... isObtainedByOtherItemStringIDs)
```

**Usage Context**: Called during mod initialization phase

---

## Item Class Structure

### Item Base Class ✅

**Location**: `necesse.inventory.item.Item`

#### Constructor
```
public Item(String name, String description)
```

#### Key Properties

| Property | Type | Purpose |
|----------|------|---------|
| `idData` | IDData | Unique identifier data |
| `type` | Type | Item type classification |
| `rarity` | Rarity | Item rarity level |
| `stackSize` | int | Max stack size (1 = no stacking) |
| `itemTexture` | GameTexture | Item sprite in inventory |
| `holdTexture` | GameTexture | Item sprite when held |
| `attackTexture` | GameTexture | Item sprite during attack |
| `hungerUsage` | float | Hunger consumed when used |
| `spoilDurationSeconds` | int | Time before item spoils |
| `dropDecayTimeMillis` | long | Time before dropped item disappears |

#### Key Methods

**Texture Management** ✅
- `setItemTexture(String path)` - Set inventory texture
- `setHoldTexture(String path)` - Set held texture
- `setAttackTexture(String path)` - Set attack texture

**Stack Management** ✅
- `setStackSize(int size)` - Set maximum stack size
- `getStackSize()` - Get max stack size

**Rarity & Category** ✅
- `setRarity(Rarity rarity)` - Set item rarity
- `getRarity()` - Get item rarity
- `addItemCategory(ItemCategoryManager manager, String... categories)` - Add to categories

**Durability** ✅
- `setMaxDurability(int durability)` - Set max durability
- `getMaxDurability()` - Get max durability

**Spoilage** ✅
- `setSpoilDurationSeconds(int seconds)` - Set spoil time
- `setDropDecayTimeMillis(long millis)` - Set decay time

---

## Item Subclasses

### ToolItem ✅

**Location**: `necesse.inventory.item.toolItem.ToolItem`

**Purpose**: Weapons and tools with attack capabilities

**Key Methods**:
- `setAttackDamage(int damage)` - Set base damage
- `setAttackSpeed(int cooldown)` - Set attack cooldown (ms)
- `setAttackRange(float range)` - Set attack range
- `getAttackDamage(InventoryItem item)` - Get current damage
- `getAttackSpeed(InventoryItem item)` - Get current speed

**ToolType Enum** ✅
- `PICKAXE` - Mining tool
- `AXE` - Chopping tool
- `SHOVEL` - Digging tool
- `SPEAR` - Melee weapon
- `SWORD` - Melee weapon
- `GLAIVE` - Polearm weapon
- `SUMMON` - Summoning weapon

### ArmorItem ✅

**Location**: `necesse.inventory.item.armorItem.ArmorItem`

**Subclasses**:
- `HelmetArmorItem` - Head armor
- `ChestArmorItem` - Chest armor
- `BootsArmorItem` - Leg/foot armor

**Key Methods**:
- `setArmorValue(int armor)` - Set defense value
- `getArmorValue(InventoryItem item)` - Get current armor
- `addModifier(ModifierValue<?> modifier)` - Add stat modifier

### TrinketItem ✅

**Location**: `necesse.inventory.item.trinketItem.TrinketItem`

**Purpose**: Accessory items with stat bonuses

**Key Methods**:
- `addModifier(ModifierValue<?> modifier)` - Add stat modifier
- `getModifiers(InventoryItem item)` - Get all modifiers

### MatItem ✅

**Location**: `necesse.inventory.item.matItem.MatItem`

**Purpose**: Crafting materials and resources

**Subclasses**:
- `BookMatItem` - Book/knowledge items
- `EssenceMatItem` - Essence materials
- `FishItem` - Fish/food items
- `MultiTextureMatItem` - Materials with variant textures

### PlaceableItem ✅

**Location**: `necesse.inventory.item.placeableItem.PlaceableItem`

**Purpose**: Items that can be placed in the world

**Subclasses**:
- `TileItem` - Placeable tiles
- `ObjectItem` - Placeable objects
- `ConsumableItem` - Consumable items (potions, food)
- `FishingRodItem` - Fishing tools
- `MountItem` - Mount items

**Key Methods**:
- `onPlace(Level level, int x, int y, PlayerMob player)` - Called when placed
- `canPlace(Level level, int x, int y)` - Check if can place

---

## Item Rarity System

### Rarity Enum ✅

**Location**: `necesse.inventory.item.Item.Rarity`

| Rarity | Purpose | Color |
|--------|---------|-------|
| `NORMAL` | Common items | Gray |
| `COMMON` | Slightly rarer | White |
| `UNCOMMON` | Uncommon drops | Green |
| `RARE` | Rare drops | Blue |
| `EPIC` | Epic tier | Purple |
| `LEGENDARY` | Legendary tier | Orange |
| `QUEST` | Quest items | Yellow |
| `UNIQUE` | Unique items | Red |

---

## Item Type System

### Type Enum ✅

**Location**: `necesse.inventory.item.Item.Type`

| Type | Purpose | Base Class |
|------|---------|-----------|
| `MAT` | Crafting materials | MatItem |
| `TOOL` | Tools/weapons | ToolItem |
| `ARMOR` | Armor pieces | ArmorItem |
| `TRINKET` | Accessories | TrinketItem |
| `MOUNT` | Mounts | MountItem |
| `ARROW` | Arrow ammunition | ArrowItem |
| `BULLET` | Bullet ammunition | BulletItem |
| `SEED` | Seed objects | SeedObjectItem |
| `BAIT` | Fishing bait | BaitItem |
| `FOOD` | Food consumables | FoodConsumableItem |
| `QUEST` | Quest items | QuestItem |
| `MISC` | Miscellaneous items | Item |

---

## Item Modifiers

### ModifierValue ✅

**Location**: `necesse.engine.modifiers.ModifierValue`

**Purpose**: Apply stat changes to items

**Generic Type**: `ModifierValue<T>`

**Key Methods**:
- `ModifierValue(ModifierType type, T value)` - Create modifier
- `getValue()` - Get modifier value
- `getModifierType()` - Get modifier type

**Common Modifier Types**:
- `BuffModifiers.ATTACK_SPEED` - Attack speed bonus
- `BuffModifiers.ALL_DAMAGE` - Damage bonus
- `BuffModifiers.ARMOR_FLAT` - Armor bonus
- `BuffModifiers.MAX_HEALTH` - Health bonus
- `BuffModifiers.CRIT_CHANCE` - Critical chance

---

## Item Inventory Integration

### InventoryItem ✅

**Location**: `necesse.inventory.InventoryItem`

**Purpose**: Represents an item instance in inventory

**Key Properties**:
- `item: Item` - The item type
- `count: int` - Stack count
- `durability: int` - Current durability

**Key Methods**:
- `getItem()` - Get item type
- `getCount()` - Get stack count
- `setCount(int count)` - Set stack count
- `getDurability()` - Get current durability
- `setDurability(int durability)` - Set durability

---

## Item Categories

### ItemCategory ✅

**Location**: `necesse.inventory.item.ItemCategory`

**Purpose**: Organize items into categories for UI

**Key Methods**:
- `addItemCategory(ItemCategoryManager manager, String... categories)` - Add to categories
- `getItemCategories(ItemCategoryManager manager)` - Get categories

**Common Categories**:
- "tools" - Tools
- "weapons" - Weapons
- "armor" - Armor
- "materials" - Materials
- "consumables" - Consumables
- "trinkets" - Trinkets

---

## Item Texture System

### GameTexture ✅

**Location**: `necesse.gfx.gameTexture.GameTexture`

**Purpose**: Manages item sprite textures

**Key Methods**:
- `getWidth()` - Get texture width
- `getHeight()` - Get texture height
- `draw(DrawOptions options)` - Draw texture

**Texture Paths**:
- Items: `items/itemname`
- Held: `items/itemname_hold`
- Attack: `items/itemname_attack`

---

## Item Durability

### Durability System ✅

**Key Methods**:
- `setMaxDurability(int durability)` - Set max durability
- `getMaxDurability()` - Get max durability
- `getDurability(InventoryItem item)` - Get current durability
- `setDurability(InventoryItem item, int durability)` - Set durability

**Durability Reduction**:
- Happens on successful attack/use
- Durability 0 = item breaks
- Some items have infinite durability

---

## Item Spoilage System

### Spoilage ✅

**Key Methods**:
- `setSpoilDurationSeconds(int seconds)` - Set spoil time
- `getSpoilDurationSeconds()` - Get spoil time
- `setDropDecayTimeMillis(long millis)` - Set decay time

**Behavior**:
- Items spoil over time when in inventory
- Spoiled items may have reduced value
- Decay time controls how long dropped items persist

---

## Item Generation & Loot

### Item Generation ✅

**Location**: `necesse.inventory.item.Item`

**Key Methods**:
- `getDefaultLootTier()` - Get default loot tier
- `setDefaultLootTier(float tier)` - Set loot tier

**Loot Tier System**:
- Controls item rarity in loot drops
- Higher tier = rarer drops
- Used by loot tables

---

## Item Tooltips

### ItemStatTip ✅

**Location**: `necesse.inventory.item.ItemStatTip`

**Purpose**: Display item stats in tooltips

**Key Methods**:
- `addTip(String tip)` - Add tooltip line
- `getTips()` - Get all tooltip lines

**Tooltip Types**:
- `StringItemStatTip` - Text tooltip
- `DoubleItemStatTip` - Numeric tooltip
- `LocalMessageItemStatTip` - Localized text

---

## Item Interaction

### ItemUsed ✅

**Location**: `necesse.inventory.item.ItemUsed`

**Purpose**: Represents item usage event

**Key Properties**:
- `item: InventoryItem` - Item being used
- `player: PlayerMob` - Player using item
- `level: Level` - Current level

---

## Best Practices

1. **Always register items during mod init** - Use ItemRegistry.registerItem()
2. **Set appropriate rarity** - Affects item value and appearance
3. **Use item categories** - Helps with inventory organization
4. **Set stack sizes** - Tools/armor should have stackSize=1
5. **Add textures** - Use setItemTexture(), setHoldTexture(), setAttackTexture()
6. **Configure durability** - For tools/weapons
7. **Add modifiers** - For armor/trinkets to provide stat bonuses

---

**Note**: All APIs marked with ✅ verified in Necesse 1.0 decompiled source.

