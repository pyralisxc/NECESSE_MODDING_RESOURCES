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
import necesse.level.maps.presets.Preset;

public class AncientVultureArenaPreset
extends Preset {
    public AncientVultureArenaPreset(int size, GameRandom random) {
        super(size, size);
        int mid = size / 2;
        int maxDistance = size / 2 * 32;
        int woodfloor = TileRegistry.getTileID("woodfloor");
        int sandstone = TileRegistry.getTileID("sandstonetile");
        int sandbrick = TileRegistry.getTileID("sandbrick");
        int[] breakObjects = new int[]{ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase")};
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                float chance;
                float distance = (float)new Point(mid * 32 + 16, mid * 32 + 16).distance(x * 32 + 16, y * 32 + 16);
                float distancePerc = distance / (float)maxDistance;
                if (distancePerc < 0.5f) {
                    this.setTile(x, y, woodfloor);
                    if (random.getChance(0.8f)) {
                        this.setTile(x, y, sandbrick);
                    }
                    this.setObject(x, y, 0);
                } else if (distancePerc <= 1.0f && random.getChance(chance = Math.abs((distancePerc - 0.5f) * 2.0f - 1.0f) * 2.0f)) {
                    if (random.getChance(0.75f)) {
                        this.setTile(x, y, random.getChance(0.75f) ? sandbrick : woodfloor);
                    } else {
                        this.setTile(x, y, sandstone);
                    }
                    this.setObject(x, y, 0);
                }
                if (!(distancePerc <= 1.0f) || this.getObject(x, y) == -1 || !random.getChance(0.1f)) continue;
                this.setObject(x, y, breakObjects[random.nextInt(breakObjects.length)]);
            }
        }
        int totalColumns = random.getIntBetween(8, 10);
        float columnAngle = random.nextInt(360);
        float anglePerColumn = 360.0f / (float)totalColumns;
        int columnID = ObjectRegistry.getObjectID("sandstonecolumn");
        for (int i = 0; i < totalColumns; ++i) {
            Point2D.Float dir = GameMath.getAngleDir(columnAngle += random.getFloatOffset(anglePerColumn, anglePerColumn / 10.0f));
            float distance = (float)size / 3.0f;
            int tileX = (int)((float)mid + dir.x * distance);
            int tileY = (int)((float)mid + dir.y * distance);
            this.setObject(tileX, tileY, columnID);
        }
        this.setObject(mid, mid, ObjectRegistry.getObjectID("ancienttotem"));
    }
}

