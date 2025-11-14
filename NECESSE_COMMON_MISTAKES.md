# Necesse 1.0 Common Mistakes & Solutions

**Version**: 1.0  
**Purpose**: Debugging guide for typical modding issues  
**Status**: Complete

---

## Mod Loading Issues

### ❌ Mod Not Loading at All

**Symptoms**:
- Mod doesn't appear in mod menu
- No console output from mod
- Game loads normally without mod

**Common Causes**:
1. Missing `@ModEntry` annotation
2. Main class doesn't extend `SimpleMod`
3. `mod.info` file missing or malformed
4. JAR file not in mods folder
5. Syntax errors in code

**Solutions**:
```java
// ✅ CORRECT
@ModEntry
public class MyMod extends SimpleMod {
    @Override
    public void init() {
        // Registration code
    }
}

// ❌ WRONG - Missing annotation
public class MyMod extends SimpleMod {
    // Won't be recognized as mod entry
}
```

**Verify**:
- [ ] `@ModEntry` annotation present
- [ ] Class extends `SimpleMod`
- [ ] `mod.info` exists in mod root
- [ ] JAR in correct mods folder
- [ ] No compilation errors

---

### ❌ Mod Crashes on Load

**Symptoms**:
- Game crashes when loading mod
- Exception in console
- Mod partially loads then fails

**Common Causes**:
1. Null pointer exception in `init()`
2. Missing dependency mod
3. Texture file not found
4. Invalid registration parameters
5. Circular dependency

**Solutions**:
```java
// ✅ CORRECT - Null checks
@Override
public void init() {
    Item item = createItem();
    if (item != null) {
        ItemRegistry.registerItem("mymod:item", item, 10.0f, true);
    } else {
        System.err.println("Failed to create item");
    }
}

// ❌ WRONG - No null check
@Override
public void init() {
    Item item = createItem();
    ItemRegistry.registerItem("mymod:item", item, 10.0f, true);
    // Crashes if item is null
}
```

**Debug Steps**:
1. Check console for exception message
2. Add logging to `init()` method
3. Verify all dependencies are installed
4. Check texture paths exist
5. Verify registration parameters

---

## Registration Issues

### ❌ Item Not Appearing in Game

**Symptoms**:
- Item registered but not in inventory
- Item not in creative menu
- Item not craftable

**Common Causes**:
1. `isObtainable` set to false
2. Registration called outside `init()`
3. Texture not found
4. Item ID conflicts with another mod

**Solutions**:
```java
// ✅ CORRECT
@Override
public void init() {
    Item item = new MatItem("My Item", "Description");
    item.setItemTexture("items/myitem");
    ItemRegistry.registerItem("mymod:myitem", item, 10.0f, true);
    //                                                        ^^^^
    //                                                   Must be true
}

// ❌ WRONG
ItemRegistry.registerItem("mymod:myitem", item, 10.0f, false);
// Item won't appear because isObtainable is false
```

---

### ❌ Duplicate ID Error

**Symptoms**:
- Console error: "Duplicate ID"
- Item doesn't register
- Game may crash

**Common Causes**:
1. Registering same item twice
2. ID conflicts with vanilla item
3. ID conflicts with another mod
4. Reusing item instance

**Solutions**:
```java
// ✅ CORRECT - Unique IDs
ItemRegistry.registerItem("mymod:item1", item1, 10.0f, true);
ItemRegistry.registerItem("mymod:item2", item2, 20.0f, true);

// ❌ WRONG - Duplicate ID
ItemRegistry.registerItem("mymod:item", item1, 10.0f, true);
ItemRegistry.registerItem("mymod:item", item2, 20.0f, true);
// Second registration fails
```

**Prevention**:
- Use unique mod prefix
- Use unique item names
- Check vanilla item IDs
- Check other mod IDs

---

## Texture Issues

### ❌ Texture Not Displaying

**Symptoms**:
- Item shows as error texture (pink/purple)
- Buff icon missing
- Object appears blank

**Common Causes**:
1. Texture file doesn't exist
2. Wrong texture path
3. Wrong file format
4. Texture in wrong folder

**Solutions**:
```java
// ✅ CORRECT - Proper path
item.setItemTexture("items/myitem");
// Looks for: resources/items/myitem.png

// ❌ WRONG - Common mistakes
item.setItemTexture("items/myitem.png");     // Don't include .png
item.setItemTexture("/items/myitem");        // Don't use leading /
item.setItemTexture("Items/MyItem");         // Wrong case
item.setItemTexture("items\\myitem");        // Wrong separator
```

**Verify**:
- [ ] File exists at `resources/items/myitem.png`
- [ ] File is PNG format
- [ ] File has transparency (RGBA)
- [ ] Path uses forward slashes
- [ ] Path is lowercase
- [ ] No file extension in code

---

### ❌ Texture Size Wrong

**Symptoms**:
- Texture appears stretched/squashed
- Texture appears tiny/huge
- Texture looks pixelated

**Standard Sizes**:
- Items: 32x32 pixels
- Buffs: 32x32 pixels
- Objects: Variable (specify width/height)
- Tiles: 16x16 or 32x32 pixels

**Solution**:
- Resize texture to correct dimensions
- Update width/height in code if needed

---

## Networking Issues

### ❌ Packet Not Sending

**Symptoms**:
- Packet data not received
- Client-server desync
- Container not updating

**Common Causes**:
1. Packet not registered
2. Write/read methods asymmetric
3. Packet sent on wrong side
4. Data type mismatch

**Solutions**:
```java
// ✅ CORRECT - Symmetric packet
public class MyPacket extends NetworkPacket {
    public String name;
    public int value;
    
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
}

// ❌ WRONG - Asymmetric packet
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

## Performance Issues

### ❌ Game Lag/Stuttering

**Symptoms**:
- FPS drops when mod is active
- Game freezes periodically
- High CPU usage

**Common Causes**:
1. Expensive operation in tick method
2. Infinite loop
3. Memory leak
4. Too many objects created

**Solutions**:
```java
// ✅ CORRECT - Efficient tick
@Override
public void serverTick(ActiveBuff buff) {
    if (buff.ticksElapsed % 20 == 0) {  // Every 20 ticks
        // Expensive operation
    }
}

// ❌ WRONG - Inefficient tick
@Override
public void serverTick(ActiveBuff buff) {
    // Expensive operation every tick
    for (int i = 0; i < 1000000; i++) {
        complexCalculation();
    }
}
```

---

## Dependency Issues

### ❌ Mod Requires Another Mod

**Symptoms**:
- Mod doesn't work without dependency
- Crashes if dependency missing
- Errors about missing classes

**Solution**:
```properties
# mod.info
dependencies=othermod
incompatibilities=badmod
```

**Verify**:
- [ ] Dependency mod ID is correct
- [ ] Dependency mod is installed
- [ ] Dependency mod loads first
- [ ] No circular dependencies

---

## Debugging Tips

### Enable Logging
```java
System.out.println("MyMod: Starting initialization");
System.out.println("MyMod: Registered " + count + " items");
System.err.println("MyMod ERROR: " + errorMessage);
```

### Check Console
- Look for exception stack traces
- Search for your mod name
- Note line numbers of errors

### Verify Installation
```bash
# Check mod folder
ls ~/.local/share/Necesse/mods/  # Linux
dir %APPDATA%\Necesse\mods\      # Windows
```

### Test Incrementally
1. Register one item
2. Test it works
3. Add more items
4. Add other systems
5. Test with other mods

---

**When stuck, check the Aphorea mod source code for working examples!**

