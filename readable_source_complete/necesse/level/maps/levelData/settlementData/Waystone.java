/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.level.maps.Level;

public class Waystone {
    public LevelIdentifier destination;
    public int tileX;
    public int tileY;
    public String name;

    public Waystone(LevelIdentifier destination, int tileX, int tileY) {
        this.destination = destination;
        this.tileX = tileX;
        this.tileY = tileY;
        this.name = "";
    }

    public Waystone(LoadData save) {
        try {
            this.destination = new LevelIdentifier(save.getUnsafeString("destination", null, false));
        }
        catch (InvalidLevelIdentifierException e) {
            int islandX = save.getInt("islandX");
            int islandY = save.getInt("islandY");
            int dimension = save.getInt("dimension");
            this.destination = new LevelIdentifier(islandX, islandY, dimension);
        }
        this.tileX = save.getInt("tileX");
        this.tileY = save.getInt("tileY");
        this.name = save.hasLoadDataByName("name") ? save.getSafeString("name") : "";
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("destination", this.destination.stringID);
        if (this.destination.isIslandPosition()) {
            save.addInt("islandX", this.destination.getIslandX());
            save.addInt("islandY", this.destination.getIslandY());
            save.addInt("dimension", this.destination.getIslandDimension());
        }
        save.addInt("tileX", this.tileX);
        save.addInt("tileY", this.tileY);
        if (this.name != null && !this.name.isEmpty()) {
            save.addSafeString("name", this.name);
        }
    }

    public Waystone(PacketReader reader) {
        this.destination = new LevelIdentifier(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.name = reader.getNextString();
    }

    public void writePacket(PacketWriter writer) {
        this.destination.writePacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextString(this.name);
    }

    public boolean checkIsValid(Server server, int settlementUniqueID) {
        WaystoneObjectEntity waystoneEntity;
        Level level = server.world.getLevel(this.destination);
        level.regionManager.ensureTileIsLoaded(this.tileX, this.tileY);
        System.out.println("HSGHSDKMLGJ " + this.destination + ", " + this.tileX + "x" + this.tileY + ", " + level.getObject(this.tileX, this.tileY).getStringID());
        if (level.getObject(this.tileX, this.tileY).getStringID().equals("waystone") && (waystoneEntity = level.entityManager.getObjectEntity(this.tileX, this.tileY, WaystoneObjectEntity.class)) != null) {
            return waystoneEntity.settlementUniqueID == settlementUniqueID;
        }
        return false;
    }

    public boolean matches(Level level, int tileX, int tileY) {
        return level.getIdentifier().equals(this.destination) && this.tileX == tileX && this.tileY == tileY;
    }

    public Point findTeleportLocation(Server server, Mob mob) {
        return Waystone.findTeleportLocation(server.world.getLevel(this.destination), this.tileX, this.tileY, mob);
    }

    public static Point findTeleportLocation(Level level, int targetTileX, int targetTileY, Mob mob) {
        ArrayList<Point> positions = new ArrayList<Point>();
        for (int y = targetTileY + 1; y >= targetTileY - 1; --y) {
            for (int x = targetTileX - 1; x <= targetTileX + 1; ++x) {
                Point pos = new Point(x * 32 + 16, y * 32 + 16);
                if (mob.collidesWith(level, pos.x, pos.y)) continue;
                positions.add(pos);
            }
        }
        return positions.stream().min(Comparator.comparingDouble(p -> p.distance(targetTileX * 32 + 16, targetTileY * 32 + 16))).orElse(new Point(targetTileX * 32 + 16, targetTileY * 32 + 16));
    }
}

