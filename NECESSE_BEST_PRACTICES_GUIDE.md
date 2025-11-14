# Necesse 1.0 Best Practices Guide

**Version**: 1.0  
**Purpose**: Quality standards and recommended patterns for Necesse modding  
**Status**: Complete

---

## Code Organization

### Package Structure
```
com.yourname.yourmod
├── YourMod.java                    (Main mod class)
├── items/
│   ├── YourItems.java              (Item registration)
│   ├── custom/
│   │   └── CustomItem.java         (Custom item classes)
├── buffs/
│   ├── YourBuffs.java              (Buff registration)
│   ├── custom/
│   │   └── CustomBuff.java         (Custom buff classes)
├── objects/
│   ├── YourObjects.java            (Object registration)
│   ├── custom/
│   │   └── CustomObject.java       (Custom object classes)
├── containers/
│   ├── YourContainers.java         (Container registration)
│   ├── handlers/
│   │   ├── CustomClientHandler.java
│   │   └── CustomServerHandler.java
├── packets/
│   ├── YourPackets.java            (Packet registration)
│   ├── custom/
│   │   └── CustomPacket.java       (Custom packet classes)
└── util/
    ├── Constants.java              (Mod constants)
    └── Helpers.java                (Utility methods)
```

### Separation of Concerns
- **Registration classes**: Handle only registration logic
- **Implementation classes**: Contain actual functionality
- **Utility classes**: Provide helper methods
- **Constants**: Define all magic strings and numbers

---

## Naming Conventions

### IDs (Critical for Uniqueness)
```java
// ✅ CORRECT
"mymod:myitem"
"mymod:mybuff"
"mymod:myobject"

// ❌ WRONG
"MyMod:MyItem"           // Mixed case
"mymod_myitem"           // Underscore instead of colon
"myitem"                 // Missing mod prefix
"my-mod:my-item"         // Hyphens in ID
```

### Class Names
```java
// ✅ CORRECT
class MyCustomItem extends Item { }
class MyCustomBuff extends Buff { }
class MyCustomObject extends GameObject { }

// ❌ WRONG
class my_custom_item { }     // Snake case
class MYCUSTOMITEM { }       // All caps
class Item_Custom { }        // Inconsistent
```

### Constants
```java
// ✅ CORRECT
public static final String MOD_ID = "mymod";
public static final String ITEM_ID = MOD_ID + ":myitem";
public static final float BROKER_VALUE = 10.0f;

// ❌ WRONG
public static final String modId = "mymod";      // Not final
String itemId = "mymod:myitem";                  // Not constant
final float brokerValue = 10.0f;                 // Not public
```

---

## Registration Best Practices

### Centralized Registration
```java
// ✅ CORRECT - All registration in one place
@ModEntry
public class MyMod extends SimpleMod {
    @Override
    public void init() {
        YourItems.registerAll();
        YourBuffs.registerAll();
        YourObjects.registerAll();
    }
}

// ❌ WRONG - Scattered registration
@ModEntry
public class MyMod extends SimpleMod {
    @Override
    public void init() {
        ItemRegistry.registerItem(...);
        BuffRegistry.registerBuff(...);
        // ... more scattered code
    }
}
```

### Registration Order
1. Items (dependencies for other systems)
2. Buffs (used by items/objects)
3. Objects (may use items/buffs)
4. Containers (may use items)
5. Packets (used by containers/objects)
6. Mobs (may use items/buffs)

---

## Error Handling

### Null Checks
```java
// ✅ CORRECT
if (item != null) {
    ItemRegistry.registerItem(id, item, value, true);
} else {
    System.err.println("Failed to create item: " + id);
}

// ❌ WRONG
ItemRegistry.registerItem(id, item, value, true);  // No null check
```

### Logging
```java
// ✅ CORRECT
System.out.println("MyMod: Registering items...");
System.out.println("MyMod: Registered " + count + " items");
System.err.println("MyMod ERROR: Failed to register item");

// ❌ WRONG
System.out.println("Registering...");              // No mod prefix
System.out.println("Done");                        // Vague message
```

---

## Performance Optimization

### Tick Methods
```java
// ✅ CORRECT - Efficient tick
@Override
public void serverTick(ActiveBuff buff) {
    if (buff.ticksElapsed % 20 == 0) {  // Every 20 ticks
        // Do expensive operation
    }
}

// ❌ WRONG - Inefficient tick
@Override
public void serverTick(ActiveBuff buff) {
    // Expensive operation every tick
    calculateComplexMath();
}
```

### Resource Management
```java
// ✅ CORRECT - Clean up resources
@Override
public void onRemoved(ActiveBuff buff) {
    // Clean up any resources
    buff.data.clear();
}

// ❌ WRONG - Memory leak
@Override
public void onRemoved(ActiveBuff buff) {
    // Resources not cleaned up
}
```

---

## Texture Management

### Texture Paths
```java
// ✅ CORRECT
item.setItemTexture("items/myitem");
buff.setIcon("buffs/mybuff");
object.setTexture("objects/myobject");

// ❌ WRONG
item.setItemTexture("items/myitem.png");         // Include extension
item.setItemTexture("/items/myitem");            // Leading slash
item.setItemTexture("Items/MyItem");             // Wrong case
```

### Texture Organization
- Items: `resources/items/`
- Buffs: `resources/buffs/`
- Objects: `resources/objects/`
- Tiles: `resources/tiles/`
- UI: `resources/ui/`

---

## Networking Best Practices

### Packet Implementation
```java
// ✅ CORRECT - Symmetric read/write
@Override
public void write(PacketWriter writer) {
    writer.putNextString(name);
    writer.putNextInt(value);
}

@Override
public void read(PacketReader reader) {
    name = reader.getNextString();
    value = reader.getNextInt();
}

// ❌ WRONG - Asymmetric
@Override
public void write(PacketWriter writer) {
    writer.putNextString(name);
    writer.putNextInt(value);
}

@Override
public void read(PacketReader reader) {
    name = reader.getNextString();
    // Missing value read!
}
```

---

## Documentation

### Code Comments
```java
// ✅ CORRECT - Meaningful comments
/**
 * Applies damage reduction based on armor value.
 * Reduction is capped at 90% to prevent invulnerability.
 */
public void applyArmorReduction(GameDamage damage) {
    // ...
}

// ❌ WRONG - Useless comments
// Increment counter
counter++;

// Apply armor
applyArmor();
```

### README.md
Include:
- Mod description
- Features list
- Installation instructions
- Configuration options
- Known issues
- Credits

---

## Testing

### Test Checklist
- [ ] Mod loads without errors
- [ ] All items register successfully
- [ ] All buffs apply correctly
- [ ] Objects place and interact properly
- [ ] Packets send/receive correctly
- [ ] No console errors or warnings
- [ ] Works with other mods
- [ ] Performance is acceptable

---

## Common Patterns

### Modifier Application
```java
// ✅ CORRECT
ModifierValue<Float> modifier = 
    new ModifierValue<>(BuffModifiers.SPEED, 1.2f);
buff.addModifier(modifier);
```

### Texture Loading
```java
// ✅ CORRECT
item.setItemTexture("items/myitem");
// Necesse automatically loads from resources/
```

---

**Follow these practices to create high-quality, maintainable mods!**

