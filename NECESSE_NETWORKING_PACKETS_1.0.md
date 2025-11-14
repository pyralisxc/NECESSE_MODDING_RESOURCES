# Necesse 1.0 Networking & Packet System - Detailed API Reference

**Version**: 1.0  
**Focus**: Client-server communication and packet handling

---

## Packet System Overview

### Core Packet Classes

**Location**: `necesse.engine.network`

#### Packet ✅

**File**: `Packet.java`

**Purpose**: Low-level byte buffer for packet data

**Key Methods**:
- `putBoolean(int index, int bit, boolean data)` - Write boolean at bit position
- `putByte(int index, byte data)` - Write byte
- `putShort(int index, short data)` - Write short
- `putInt(int index, int data)` - Write int
- `putFloat(int index, float data)` - Write float
- `putLong(int index, long data)` - Write long
- `putDouble(int index, double data)` - Write double
- `putString(int index, String data)` - Write string
- `getBoolean(int index, int bit)` - Read boolean at bit position
- `getByte(int index)` - Read byte
- `getShort(int index)` - Read short
- `getInt(int index)` - Read int
- `getFloat(int index)` - Read float
- `getLong(int index)` - Read long
- `getDouble(int index)` - Read double
- `getString(int index, int length)` - Read string

---

## PacketWriter - Writing Data

### PacketWriter Class ✅

**Location**: `necesse.engine.network.PacketWriter`

**Purpose**: Convenience class for writing packet data sequentially

**Constructors**:
```
PacketWriter(Packet packet)
PacketWriter(Packet packet, int startIndex)
PacketWriter(PacketIterator copy)
```

### Writing Methods

All methods return `this` for method chaining.

#### Boolean & Bit Operations ✅
- `putNextBoolean(boolean data)` - Write single boolean
- `putNextBitValue(int value, int bits)` - Write value using N bits
- `putNextMaxValue(int value, int maxValue)` - Write value with max constraint

#### Byte Operations ✅
- `putNextByte(byte data)` - Write signed byte
- `putNextByteUnsigned(int data)` - Write unsigned byte (0-255)

#### Short Operations ✅
- `putNextShort(short data)` - Write signed short
- `putNextShortUnsigned(int data)` - Write unsigned short (0-65535)

#### Integer Operations ✅
- `putNextInt(int data)` - Write signed int
- `putNextIntUnsigned(long data)` - Write unsigned int

#### Floating Point ✅
- `putNextFloat(float data)` - Write float (32-bit)
- `putNextDouble(double data)` - Write double (64-bit)

#### Long Operations ✅
- `putNextLong(long data)` - Write long (64-bit)

#### String Operations ✅
- `putNextString(String data)` - Write string with short length prefix (max 65535 chars)
- `putNextStringLong(String data)` - Write string with int length prefix (for longer strings)

#### Array Operations ✅
- `putNextBytes(byte[] bytes)` - Write byte array
- `putNextBytesUnsigned(int[] values)` - Write unsigned byte array
- `putNextShorts(short[] values)` - Write short array
- `putNextShortsUnsigned(int[] values)` - Write unsigned short array
- `putNextInts(int[] values)` - Write int array
- `putNextLongs(long[] values)` - Write long array
- `putNextBooleans(boolean[] values)` - Write boolean array

#### Packet Operations ✅
- `putNextContentPacket(Packet data)` - Write nested packet with size prefix

#### Enum Operations ✅
- `putNextEnum(Enum data)` - Write enum as ordinal value

#### Collection Operations ✅
- `putNextCollection(Collection<?> collection, Consumer<T> elementWriter)` - Write collection with size prefix

---

## PacketReader - Reading Data

### PacketReader Class ✅

**Location**: `necesse.engine.network.PacketReader`

**Purpose**: Convenience class for reading packet data sequentially

**Constructors**:
```
PacketReader(Packet packet)
PacketReader(Packet packet, int startIndex)
PacketReader(PacketIterator copy)
```

### Reading Methods

#### Boolean & Bit Operations ✅
- `getNextBoolean()` - Read single boolean
- `getNextBitValue(int bits)` - Read value using N bits
- `getNextMaxValue(int maxValue)` - Read value with max constraint

#### Byte Operations ✅
- `getNextByte()` - Read signed byte
- `getNextByteUnsigned()` - Read unsigned byte (0-255)

#### Short Operations ✅
- `getNextShort()` - Read signed short
- `getNextShortUnsigned()` - Read unsigned short (0-65535)

#### Integer Operations ✅
- `getNextInt()` - Read signed int
- `getNextIntUnsigned()` - Read unsigned int

#### Floating Point ✅
- `getNextFloat()` - Read float (32-bit)
- `getNextDouble()` - Read double (64-bit)

#### Long Operations ✅
- `getNextLong()` - Read long (64-bit)

#### String Operations ✅
- `getNextString()` - Read string with short length prefix (max 65535 chars)
- `getNextStringLong()` - Read string with int length prefix (for longer strings)

#### Array Operations ✅
- `getNextBytes(int length)` - Read byte array
- `getNextBytesUnsigned(int length)` - Read unsigned byte array
- `getNextShorts(int length)` - Read short array
- `getNextShortsUnsigned(int length)` - Read unsigned short array
- `getNextInts(int length)` - Read int array
- `getNextLongs(int length)` - Read long array
- `getNextBooleans(int length)` - Read boolean array

#### Packet Operations ✅
- `getNextContentPacket()` - Read nested packet with size prefix
- `getRemainingBytes()` - Get all remaining bytes
- `getRemainingBytesPacket()` - Get remaining bytes as packet
- `getRemainingSize()` - Get remaining byte count

#### Enum Operations ✅
- `getNextEnum(Class<T> enumClass)` - Read enum from ordinal value

#### Collection Operations ✅
- `getNextCollection(IntFunction<L> collectionConstructor, Supplier<T> elementConstructor)` - Read collection with size prefix
- `assignNextCollection(L collection, Supplier<T> elementConstructor)` - Read collection into existing collection

---

## NetworkPacket - Custom Packets

### NetworkPacket Base Class ✅

**Location**: `necesse.engine.network.NetworkPacket`

**Purpose**: Base class for custom network packets

**Key Methods**:

#### Serialization ✅
- `write(PacketWriter writer)` - Serialize packet data to writer
- `read(PacketReader reader)` - Deserialize packet data from reader

#### Server Handling ✅
- `handleServer(ServerClient client)` - Handle packet on server side
  - `ServerClient` provides access to player, level, server

#### Client Handling ✅
- `handleClient(Client client)` - Handle packet on client side
  - `Client` provides access to player, level, client

### Packet Registration

#### PacketRegistry ✅

**Location**: `necesse.engine.registries.PacketRegistry`

**Key Methods**:
- `registerPacket(String id, NetworkPacket packet)` - Register custom packet
- `getPacket(String id)` - Retrieve packet by ID

**Registration Pattern**:
```
PacketRegistry.registerPacket("mymod:mypacket", new MyCustomPacket())
```

---

## Game Network Data (GND)

### GND System ✅

**Location**: `necesse.engine.network.gameNetworkData`

**Purpose**: Serializable game data for network transmission

#### GNDItem ✅
- Base class for network-serializable data
- Subclasses for different data types:
  - `GNDItemString` - String data
  - `GNDItemInt` - Integer data
  - `GNDItemFloat` - Float data
  - `GNDItemBoolean` - Boolean data
  - `GNDItemGameItem` - Item data
  - `GNDItemInventory` - Inventory data
  - `GNDItemMap` - Map/dictionary data
  - `GNDItemArray` - Array data

#### GNDRegistry ✅

**Location**: `necesse.engine.registries.GNDRegistry`

**Purpose**: Registry for GND data types

**Key Methods**:
- `registerGND(String id, GNDItem gnd)` - Register GND type
- `getGND(String id)` - Retrieve GND type

---

## Network Manager

### NetworkManager ✅

**Location**: `necesse.engine.network.NetworkManager`

**Purpose**: Central network communication hub

**Key Methods**:
- `sendPacket(NetworkPacket packet)` - Send packet to server/client
- `sendPacketToClient(ServerClient client, NetworkPacket packet)` - Send to specific client
- `sendPacketToAllClients(NetworkPacket packet)` - Broadcast to all clients
- `sendPacketToClientsInLevel(Level level, NetworkPacket packet)` - Send to clients in level

---

## Client-Server Architecture

### ServerClient ✅

**Location**: `necesse.engine.network.server.ServerClient`

**Purpose**: Represents a connected client on server

**Key Properties**:
- `player: PlayerMob` - Player entity
- `level: Level` - Current level
- `server: Server` - Server instance

**Key Methods**:
- `sendPacket(NetworkPacket packet)` - Send packet to this client
- `getPlayer()` - Get player mob
- `getLevel()` - Get current level

### Client ✅

**Location**: `necesse.engine.network.client.Client`

**Purpose**: Client-side network interface

**Key Properties**:
- `player: PlayerMob` - Local player
- `level: Level` - Current level

**Key Methods**:
- `sendPacket(NetworkPacket packet)` - Send packet to server
- `getPlayer()` - Get local player
- `getLevel()` - Get current level

---

## Packet Data Types

### Supported Data Types ✅

| Type | Writer Method | Reader Method | Size |
|------|---------------|---------------|------|
| Boolean | putNextBoolean() | getNextBoolean() | 1 bit |
| Byte | putNextByte() | getNextByte() | 1 byte |
| Unsigned Byte | putNextByteUnsigned() | getNextByteUnsigned() | 1 byte |
| Short | putNextShort() | getNextShort() | 2 bytes |
| Unsigned Short | putNextShortUnsigned() | getNextShortUnsigned() | 2 bytes |
| Int | putNextInt() | getNextInt() | 4 bytes |
| Unsigned Int | putNextIntUnsigned() | getNextIntUnsigned() | 4 bytes |
| Float | putNextFloat() | getNextFloat() | 4 bytes |
| Long | putNextLong() | getNextLong() | 8 bytes |
| Double | putNextDouble() | getNextDouble() | 8 bytes |
| String | putNextString() | getNextString() | Variable |
| Enum | putNextEnum() | getNextEnum() | Variable |

---

## Packet Optimization

### Bit-Level Operations ✅

For space-efficient packets, use bit operations:
- `putNextBitValue(value, bits)` - Write value using exact number of bits
- `getNextBitValue(bits)` - Read value using exact number of bits
- `putNextMaxValue(value, maxValue)` - Write value with max constraint
- `getNextMaxValue(maxValue)` - Read value with max constraint

**Example**: Boolean flag uses 1 bit instead of 1 byte

### Packet Size Considerations

- Minimize data sent per packet
- Use appropriate data types (byte vs int)
- Use bit operations for flags
- Batch related data into single packet

---

## Common Packet Patterns

### Pattern 1: Simple Data Packet ✅

```
class MyDataPacket extends NetworkPacket {
    public int value;
    
    public void write(PacketWriter writer) {
        writer.putNextInt(value);
    }
    
    public void read(PacketReader reader) {
        value = reader.getNextInt();
    }
    
    public void handleServer(ServerClient client) {
        // Handle on server
    }
    
    public void handleClient(Client client) {
        // Handle on client
    }
}
```

### Pattern 2: Complex Data Packet ✅

```
class ComplexPacket extends NetworkPacket {
    public String name;
    public int count;
    public float value;
    
    public void write(PacketWriter writer) {
        writer.putNextString(name);
        writer.putNextInt(count);
        writer.putNextFloat(value);
    }
    
    public void read(PacketReader reader) {
        name = reader.getNextString();
        count = reader.getNextInt();
        value = reader.getNextFloat();
    }
}
```

---

## Network Events

### Packet Handling Flow ✅

1. **Client sends packet**: `NetworkManager.sendPacket(packet)`
2. **Server receives**: `packet.handleServer(serverClient)`
3. **Server sends response**: `NetworkManager.sendPacketToClient(client, response)`
4. **Client receives**: `packet.handleClient(client)`

---

## Best Practices

1. **Always implement both write() and read()** - Must be symmetric
2. **Use appropriate data types** - Minimize packet size
3. **Validate data on receive** - Check for invalid values
4. **Handle network errors** - Packets may be lost
5. **Batch related data** - Send together in one packet
6. **Use bit operations** - For flags and small values
7. **Register packets early** - During mod initialization
8. **Keep packets small** - Reduces network overhead

---

**Note**: All APIs marked with ✅ verified in Necesse 1.0 decompiled source.

