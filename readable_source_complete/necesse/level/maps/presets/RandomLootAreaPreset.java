/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.level.maps.presets.Preset;

public class RandomLootAreaPreset
extends Preset {
    public RandomLootAreaPreset(GameRandom random, int size, String columnStringID, TicketSystemList<String> mobStringIDs) {
        super(size, size);
        int mid = size / 2;
        int maxDistance = size / 2 * 32;
        int woodFloor = TileRegistry.getTileID("woodfloor");
        int[] breakObjects = new int[]{ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase"), ObjectRegistry.getObjectID("coinstack")};
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                float chance;
                float distance = (float)new Point(mid * 32 + 16, mid * 32 + 16).distance(x * 32 + 16, y * 32 + 16);
                float distancePerc = distance / (float)maxDistance;
                if (distancePerc < 0.5f) {
                    this.setTile(x, y, woodFloor);
                    this.setObject(x, y, 0);
                } else if (distancePerc <= 1.0f && random.getChance(chance = Math.abs((distancePerc - 0.5f) * 2.0f - 1.0f) * 2.0f)) {
                    if (random.getChance(0.75f)) {
                        this.setTile(x, y, woodFloor);
                    }
                    this.setObject(x, y, 0);
                }
                if (!(distancePerc <= 1.0f) || this.getObject(x, y) == -1 || !random.getChance(0.12f)) continue;
                this.setObject(x, y, breakObjects[random.nextInt(breakObjects.length)]);
            }
        }
        int totalColumns = random.getIntBetween(3, 4);
        float columnAngle = random.nextInt(360);
        float anglePerColumn = 360.0f / (float)totalColumns;
        int columnID = ObjectRegistry.getObjectID(columnStringID);
        for (int i = 0; i < totalColumns; ++i) {
            Point2D.Float dir = GameMath.getAngleDir(columnAngle += random.getFloatOffset(anglePerColumn, anglePerColumn / 10.0f));
            float distance = (float)size / 4.0f;
            int tileX = (int)((float)mid + dir.x * distance);
            int tileY = (int)((float)mid + dir.y * distance);
            this.setObject(tileX, tileY, columnID);
        }
        int totalMobs = size / 3;
        for (int i = 0; i < totalMobs; ++i) {
            int tileY;
            if (mobStringIDs.isEmpty()) continue;
            float angle = random.nextFloat() * 360.0f;
            float dx = (float)Math.cos(Math.toRadians(angle));
            float dy = (float)Math.sin(Math.toRadians(angle));
            int distance = size / 4 * 32;
            int tileX = GameMath.getTileCoordinate((float)(mid * 32 + 16) + dx * (float)distance);
            if (this.getObject(tileX, tileY = GameMath.getTileCoordinate((float)(mid * 32 + 16) + dy * (float)distance)) != 0) continue;
            this.addMob(mobStringIDs.getRandomObject(random), tileX, tileY, false);
        }
    }

    public RandomLootAreaPreset(GameRandom random, int size, String columnStringID, String ... mobStringIDs) {
        this(random, size, columnStringID, RandomLootAreaPreset.toTicketSystem(mobStringIDs));
    }

    private static TicketSystemList<String> toTicketSystem(String ... mobStringIDs) {
        TicketSystemList<String> out = new TicketSystemList<String>();
        for (String mobStringID : mobStringIDs) {
            out.addObject(100, (Object)mobStringID);
        }
        return out;
    }
}

