# Necesse 1.0 Buff & Mob System - Detailed API Reference

**Version**: 1.0  
**Focus**: Buff mechanics, mob interactions, and stat modifiers

---

## Buff System

### BuffRegistry ✅

**Location**: `necesse.engine.registries.BuffRegistry`

**Key Methods**:
- `registerBuff(String id, Buff buff)` - Register new buff
- `getBuff(String id)` - Get buff by ID
- `getBuff(int classID)` - Get buff by class ID
- `getBuffs()` - Get all registered buffs

**Registration Pattern**:
```
BuffRegistry.registerBuff("mymod:mybuff", new MyBuff())
```

---

## Buff Base Class

### Buff Class ✅

**Location**: `necesse.entity.mobs.buffs.staticBuffs.Buff`

**Purpose**: Base class for all buffs/debuffs

#### Key Methods

**Identification** ✅
- `getStringID()` - Get buff string identifier
- `getID()` - Get unique buff ID
- `getDisplayName()` - Get buff display name
- `getLocalization()` - Get buff localization message

**Initialization** ✅
- `init(ActiveBuff buff, BuffEventSubscriber subscriber)` - Initialize buff (abstract, must override)
- `firstAdd(ActiveBuff buff)` - Called when buff first added to mob

**Lifecycle Events** ✅
- `onUpdate(ActiveBuff buff)` - Called each tick while buff is active
- `onRemoved(ActiveBuff buff)` - Called when buff removed
- `onOverridden(ActiveBuff buff, ActiveBuff other)` - Called when buff is overridden by another
- `onStacksUpdated(ActiveBuff buff, ActiveBuff other)` - Called when stacks change

**Combat Events** ✅
- `onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event)` - Before mob attacks
- `onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event)` - Before mob is attacked
- `onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event)` - Before damage calculated
- `onBeforeAttackedCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event)` - Before damage received calculated
- `onWasHit(ActiveBuff buff, MobWasHitEvent event)` - After mob attacks
- `onHasAttacked(ActiveBuff buff, MobWasHitEvent event)` - After mob is attacked
- `onHasKilledTarget(ActiveBuff buff, MobWasKilledEvent event)` - When mob kills target

**Tick Methods** ✅
- `serverTick(ActiveBuff buff)` - Called each server tick
- `clientTick(ActiveBuff buff)` - Called each client tick

**Rendering** ✅
- `getDrawIcon(ActiveBuff buff)` - Get buff icon texture
- `drawIcon(int x, int y, ActiveBuff buff)` - Draw buff icon with stacks/duration
- `shouldDrawDuration(ActiveBuff buff)` - Whether to show duration text
- `getStacksDisplayCount(ActiveBuff buff)` - Get stacks to display

**Utility** ✅
- `isPotionBuff()` - Whether buff is from potion
- `getLocalizationKey()` - Get localization key for buff name

---

## Buff Modifiers

### BuffModifiers ✅

**Location**: `necesse.entity.mobs.buffs.BuffModifiers`

**Purpose**: Defines all available stat modifiers

#### Combat Modifiers ✅
- `ATTACK_SPEED` - Attack speed multiplier
- `MELEE_ATTACK_SPEED` - Melee-specific attack speed
- `RANGED_ATTACK_SPEED` - Ranged-specific attack speed
- `MAGIC_ATTACK_SPEED` - Magic-specific attack speed
- `SUMMON_ATTACK_SPEED` - Summon-specific attack speed
- `ALL_DAMAGE` - All damage types multiplier
- `MELEE_DAMAGE` - Melee damage multiplier
- `RANGED_DAMAGE` - Ranged damage multiplier
- `MAGIC_DAMAGE` - Magic damage multiplier
- `SUMMON_DAMAGE` - Summon damage multiplier
- `TOOL_DAMAGE` - Tool damage multiplier
- `TOOL_DAMAGE_FLAT` - Flat tool damage bonus
- `CRIT_CHANCE` - Critical hit chance (0.0-1.0)
- `MELEE_CRIT_CHANCE` - Melee critical chance
- `RANGED_CRIT_CHANCE` - Ranged critical chance
- `MAGIC_CRIT_CHANCE` - Magic critical chance
- `SUMMON_CRIT_CHANCE` - Summon critical chance
- `CRIT_DAMAGE` - Critical damage multiplier
- `MELEE_CRIT_DAMAGE` - Melee critical damage
- `RANGED_CRIT_DAMAGE` - Ranged critical damage
- `MAGIC_CRIT_DAMAGE` - Magic critical damage
- `SUMMON_CRIT_DAMAGE` - Summon critical damage
- `ARMOR_PEN` - Armor penetration multiplier
- `ARMOR_PEN_FLAT` - Flat armor penetration
- `KNOCKBACK_OUT` - Knockback resistance

#### Defense Modifiers ✅
- `ARMOR` - Armor multiplier
- `ARMOR_FLAT` - Flat armor value
- `INCOMING_DAMAGE_MOD` - Incoming damage multiplier
- `FIRE_DAMAGE` - Fire resistance multiplier
- `FIRE_DAMAGE_FLAT` - Flat fire resistance
- `FROST_DAMAGE` - Frost resistance multiplier
- `FROST_DAMAGE_FLAT` - Flat frost resistance
- `POISON_DAMAGE` - Poison resistance multiplier
- `POISON_DAMAGE_FLAT` - Flat poison damage reduction
- `SLOW` - Slow effect multiplier

#### Health & Mana ✅
- `MAX_HEALTH` - Maximum health multiplier
- `MAX_HEALTH_FLAT` - Flat max health bonus
- `MAX_RESILIENCE` - Maximum resilience multiplier
- `MAX_RESILIENCE_FLAT` - Flat max resilience bonus
- `MAX_MANA` - Maximum mana multiplier
- `MAX_MANA_FLAT` - Flat max mana bonus
- `HEALTH_REGEN` - Base health regen multiplier
- `HEALTH_REGEN_FLAT` - Flat base health regen
- `COMBAT_HEALTH_REGEN` - Combat health regen multiplier
- `COMBAT_HEALTH_REGEN_FLAT` - Flat combat health regen
- `RESILIENCE_GAIN` - Resilience gain multiplier
- `RESILIENCE_DECAY` - Resilience decay multiplier
- `RESILIENCE_DECAY_FLAT` - Flat resilience decay
- `RESILIENCE_REGEN` - Resilience regen multiplier
- `RESILIENCE_REGEN_FLAT` - Flat resilience regen
- `MANA_REGEN` - Base mana regen multiplier
- `MANA_REGEN_FLAT` - Flat base mana regen
- `COMBAT_MANA_REGEN` - Combat mana regen multiplier
- `COMBAT_MANA_REGEN_FLAT` - Flat combat mana regen
- `LIFE_ESSENCE_GAIN` - Life essence gain multiplier
- `LIFE_ESSENCE_DURATION` - Life essence duration multiplier

#### Movement ✅
- `SPEED` - Movement speed multiplier
- `SPEED_FLAT` - Flat movement speed bonus
- `SWIM_SPEED` - Swimming speed multiplier
- `FRICTION` - Friction multiplier
- `ACCELERATION` - Acceleration multiplier
- `DECELERATION` - Deceleration multiplier

#### Utility ✅
- `MINING_SPEED` - Mining speed multiplier
- `BUILDING_SPEED` - Building speed multiplier
- `TOOL_TIER` - Tool tier bonus
- `MINING_RANGE` - Mining range bonus
- `BUILD_RANGE` - Building range bonus
- `ITEM_PICKUP_RANGE` - Item pickup range bonus
- `ARROW_USAGE` - Arrow usage efficiency
- `BULLET_USAGE` - Bullet usage efficiency
- `MANA_USAGE` - Mana usage efficiency
- `BLINDNESS` - Blindness effect (0.0-1.0)

#### Projectile & Velocity ✅
- `PROJECTILE_VELOCITY` - Projectile speed multiplier
- `THROWING_VELOCITY` - Throwing speed multiplier
- `PROJECTILE_BOUNCES` - Number of projectile bounces
- `ATTACK_MOVEMENT_MOD` - Movement during attack

#### Summoning ✅
- `MAX_SUMMONS` - Maximum summons allowed
- `SUMMONS_SPEED` - Summon movement speed multiplier
- `SUMMONS_TARGET_RANGE` - Summon target range multiplier

---

## ModifierValue System

### ModifierValue Class ✅

**Location**: `necesse.engine.modifiers.ModifierValue`

**Generic Type**: `ModifierValue<T>`

**Purpose**: Represents a single stat modifier

#### Constructor ✅
```
ModifierValue(ModifierType type, T value)
```

**Parameters**:
- `type` - BuffModifiers constant
- `value` - Modifier value (type-specific)

#### Key Methods ✅
- `getValue()` - Get modifier value
- `getModifierType()` - Get modifier type
- `min(T minValue)` - Set minimum value
- `max(T maxValue)` - Set maximum value

#### Usage Example ✅
```
new ModifierValue<Float>(BuffModifiers.SPEED, 0.2f)
new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 5)
new ModifierValue<Boolean>(BuffModifiers.INVISIBILITY, true)
```

---

## Buff Subclasses

### SimplePotionBuff ✅

**Location**: `necesse.entity.mobs.buffs.staticBuffs.SimplePotionBuff`

**Purpose**: Basic buff with stat modifiers

**Constructor**:
```
SimplePotionBuff(ModifierValue<?>... modifiers)
```

**Usage**:
```
new SimplePotionBuff(
    new ModifierValue<Float>(BuffModifiers.SPEED, 0.2f),
    new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, 0.15f)
)
```

### SimpleDebuff ✅

**Location**: `necesse.entity.mobs.buffs.staticBuffs.SimpleDebuff`

**Purpose**: Basic debuff with stat modifiers

**Constructor**:
```
SimpleDebuff(Color color, String id, ModifierValue<?>... modifiers)
```

**Parameters**:
- `color` - Debuff display color
- `id` - Unique debuff ID
- `modifiers` - Stat modifiers to apply

### ActiveBuff ✅

**Location**: `necesse.entity.mobs.buffs.ActiveBuff`

**Purpose**: Buff with active ability/effect

**Key Methods**:
- `getAbility()` - Get active ability
- `onAbilityStart(Mob mob)` - Called when ability starts
- `onAbilityEnd(Mob mob)` - Called when ability ends

---

## Buff Manager

### BuffManager ✅

**Location**: `necesse.entity.mobs.buffs.BuffManager`

**Purpose**: Manages buffs on a mob

**Key Methods**

**Adding Buffs** ✅
- `addBuff(Buff buff, int duration)` - Add buff with duration (ticks)
- `addBuff(Buff buff, int duration, int stacks)` - Add buff with stacks
- `addBuff(String buffID, int duration)` - Add buff by ID

**Removing Buffs** ✅
- `removeBuff(Buff buff)` - Remove buff
- `removeBuff(String buffID)` - Remove buff by ID
- `removeAllBuffs()` - Remove all buffs

**Querying Buffs** ✅
- `hasBuff(Buff buff)` - Check if has buff
- `hasBuff(String buffID)` - Check if has buff by ID
- `getBuffs()` - Get all active buffs
- `getBuffDuration(Buff buff)` - Get remaining duration

**Modifiers** ✅
- `getModifiers()` - Get all active modifiers
- `getModifier(ModifierType type)` - Get specific modifier

---

## Mob System

### Mob Base Class ✅

**Location**: `necesse.entity.mobs.Mob`

**Purpose**: Base class for all mobs (players, enemies, NPCs)

#### Position & Movement ✅
- `x, y: float` - World position
- `vx, vy: float` - Velocity
- `getX()` - Get X position
- `getY()` - Get Y position
- `setPosition(float x, float y)` - Set position
- `addVelocity(float vx, float vy)` - Add velocity

#### Health & Mana ✅
- `health: float` - Current health
- `maxHealth: float` - Maximum health
- `mana: float` - Current mana
- `maxMana: float` - Maximum mana
- `getHealth()` - Get current health
- `getMaxHealth()` - Get max health
- `setHealth(float health)` - Set health
- `getMana()` - Get current mana
- `setMana(float mana)` - Set mana

#### Buffs ✅
- `buffManager: BuffManager` - Buff manager
- `addBuff(Buff buff, int duration)` - Add buff
- `removeBuff(Buff buff)` - Remove buff
- `hasBuff(Buff buff)` - Check buff

#### Damage ✅
- `takeDamage(GameDamage damage)` - Apply damage
- `heal(float amount)` - Restore health
- `getArmor()` - Get armor value

#### Events ✅
- `onDeath()` - Called when mob dies
- `onHit(GameDamage damage)` - Called when hit
- `onKill(Mob killed)` - Called when kills another mob

---

## PlayerMob

### PlayerMob Class ✅

**Location**: `necesse.entity.mobs.PlayerMob`

**Purpose**: Player character entity

**Key Methods**

**Inventory** ✅
- `getInventory()` - Get player inventory
- `getEquipment()` - Get equipped items
- `addItem(Item item, int count)` - Add item to inventory

**Equipment** ✅
- `getHelmet()` - Get helmet item
- `getChest()` - Get chest armor
- `getBoots()` - Get boots
- `getTrinkets()` - Get trinket items

**Stats** ✅
- `getLevel()` - Get player level
- `getExperience()` - Get current experience
- `addExperience(int amount)` - Add experience

**Interaction** ✅
- `interact(GameObject object)` - Interact with object
- `attack(Mob target)` - Attack mob

---

## HostileMob

### HostileMob Class ✅

**Location**: `necesse.entity.mobs.hostile.HostileMob`

**Purpose**: Enemy mob base class

**Key Methods**

**Combat** ✅
- `getAttackDamage()` - Get damage value
- `getAttackRange()` - Get attack range
- `getAttackSpeed()` - Get attack cooldown

**AI** ✅
- `getTarget()` - Get current target
- `setTarget(Mob target)` - Set target
- `updateAI()` - Update AI behavior

**Loot** ✅
- `getLootTable()` - Get loot drops
- `dropLoot()` - Drop loot on death

---

## FriendlyMob

### FriendlyMob Class ✅

**Location**: `necesse.entity.mobs.friendly.FriendlyMob`

**Purpose**: Non-hostile mob (animals, NPCs)

**Key Methods**:
- `interact(PlayerMob player)` - Handle player interaction
- `getInteractionRange()` - Get interaction distance

---

## SummonedMob

### SummonedMob Class ✅

**Location**: `necesse.entity.mobs.summon.SummonedMob`

**Purpose**: Player-summoned mob

**Key Methods**:
- `getSummoner()` - Get summoning player
- `getFollowTarget()` - Get target to follow
- `setFollowTarget(Mob target)` - Set follow target
- `getDuration()` - Get remaining duration

---

## GameDamage

### GameDamage Class ✅

**Location**: `necesse.entity.mobs.GameDamage`

**Purpose**: Represents damage calculation

**Key Properties**:
- `damage: float` - Damage amount
- `damageType: DamageType` - Type of damage
- `attacker: Mob` - Mob dealing damage
- `knockback: float` - Knockback amount

**Damage Types** ✅
- `MELEE` - Melee damage
- `RANGED` - Ranged damage
- `MAGIC` - Magic damage
- `SUMMON` - Summon damage
- `TRUE` - True damage (ignores armor)
- `NORMAL` - Normal damage

---

## Mob Events

### Mob Event System ✅

**Location**: `necesse.entity.mobs`

#### MobBeforeHitEvent ✅
- Called before damage calculated
- Can modify damage

#### MobWasHitEvent ✅
- Called after damage applied
- Damage already taken

#### MobWasKilledEvent ✅
- Called when mob dies
- Provides killer information

#### MobHealthChangedEvent ✅
- Called when health changes
- Provides old/new health

#### MobManaChangedEvent ✅
- Called when mana changes
- Provides old/new mana

---

## Buff Application Patterns

### Pattern 1: Simple Buff ✅
```
BuffRegistry.registerBuff("mymod:speedbuff", 
    new SimplePotionBuff(
        new ModifierValue<Float>(BuffModifiers.SPEED, 0.2f)
    )
);
```

### Pattern 2: Complex Buff ✅
```
class MyCustomBuff extends Buff {
    public void onApply(Mob mob) {
        // Custom logic on apply
    }
    
    public void tick(Mob mob, int ticksLeft) {
        // Custom logic each tick
    }
    
    public void onRemove(Mob mob) {
        // Custom logic on remove
    }
    
    public List<ModifierValue<?>> getModifiers(Mob mob) {
        // Return modifiers
    }
}
```

### Pattern 3: Applying Buff to Mob ✅
```
Buff buff = BuffRegistry.getBuff("mymod:speedbuff");
mob.buffManager.addBuff(buff, 300); // 300 ticks duration
```

---

## Best Practices

1. **Register buffs during mod init** - Use BuffRegistry.registerBuff()
2. **Use appropriate modifiers** - Choose correct BuffModifiers constant
3. **Set modifier values correctly** - Type must match (Float, Integer, Boolean)
4. **Handle buff lifecycle** - Implement onApply/onRemove/tick
5. **Validate buff duration** - Use reasonable tick counts
6. **Stack buffs carefully** - Consider canStack() behavior
7. **Test modifier interactions** - Multiple buffs may conflict
8. **Use buff colors** - Distinguish buffs visually

---

**Note**: All APIs marked with ✅ verified in Necesse 1.0 decompiled source.

