/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.temple;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.temple.TempleLevel;

public class TempleArenaLevel
extends TempleLevel {
    public static final int ARENA_SIZE = 40;
    public static final int LAVA_EDGE_SIZE = 7;
    public static final int EDGE_SIZE = 40;
    private static final int TOTAL_SIZE = 134;

    public TempleArenaLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public TempleArenaLevel(LevelIdentifier identifier, WorldEntity worldEntity) {
        super(identifier, 134, 134, worldEntity);
        this.baseBiome = BiomeRegistry.TEMPLE;
        this.isCave = true;
        this.isProtected = true;
        this.generateLevel();
    }

    public void generateLevel() {
        GameRandom random = new GameRandom(this.getSeed());
        int wall = ObjectRegistry.getObjectID("deepsandstonewall");
        int sandBrick = TileRegistry.getTileID("sandbrick");
        int woodFloor = TileRegistry.getTileID("woodfloor");
        int lavaTile = TileRegistry.getTileID("lavatile");
        int fireChalice = ObjectRegistry.getObjectID("templefirechalice");
        TicketSystemList tiles = new TicketSystemList();
        tiles.addObject(100, (Object)sandBrick);
        tiles.addObject(50, (Object)woodFloor);
        for (int x = 0; x < this.tileWidth; ++x) {
            for (int y = 0; y < this.tileHeight; ++y) {
                this.setTile(x, y, (Integer)tiles.getRandomObject(random));
            }
        }
        int centerX = this.tileWidth / 2;
        int centerY = this.tileHeight / 2;
        for (int x = 0; x < this.tileWidth; ++x) {
            for (int y = 0; y < this.tileHeight; ++y) {
                double dist = new Point2D.Float(centerX, centerY).distance(x, y);
                if (dist <= 20.5) {
                    this.setObject(x, y, 0);
                    continue;
                }
                if (dist <= 27.5) {
                    this.setObject(x, y, 0);
                    this.setTile(x, y, lavaTile);
                    continue;
                }
                this.setObject(x, y, wall);
            }
        }
        this.placeObjectAngle(centerX, centerY, 14.0f, -90.0f, fireChalice, 0, 0.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, 45.0f, fireChalice, 0, 0.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, -45.0f, fireChalice, 0, 0.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, 0.0f, fireChalice, 0, 0.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, 180.0f, fireChalice, 0, -1.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, 135.0f, fireChalice, 0, 0.0f, 0.0f);
        this.placeObjectAngle(centerX, centerY, 14.0f, -135.0f, fireChalice, 0, 0.0f, 0.0f);
        Point exitPosition = TempleArenaLevel.getExitPosition();
        GameObject exitObject = ObjectRegistry.getObject("templeexit");
        exitObject.placeObject(this, exitPosition.x, exitPosition.y, 0, false);
        Mob mob = MobRegistry.getMob("fallenwizard", (Level)this);
        Point2D.Float bossPos = TempleArenaLevel.getBossPosition();
        this.entityManager.addMob(mob, bossPos.x, bossPos.y);
    }

    private void placeObjectAngle(int centerX, int centerY, float radius, float angle, int objectID, int rotation, float xOffset, float yOffset) {
        GameObject object = ObjectRegistry.getObject(objectID);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        object.placeObject(this, (int)((float)centerX + dir.x * radius + xOffset), (int)((float)centerY + dir.y * radius + yOffset), rotation, false);
    }

    public static Point getExitPosition() {
        return new Point(66, 82);
    }

    public static Point2D.Float getBossPosition() {
        return new Point2D.Float(2160.0f, 2160.0f);
    }
}

