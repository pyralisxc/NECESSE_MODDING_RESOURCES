/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.objectEntity.AscendedPylonObjectEntity;
import necesse.level.maps.presets.Preset;

public class AscendedPylonAreaPreset
extends Preset {
    public AscendedPylonAreaPreset(int size, GameRandom random, ArrayList<Integer> attackIndexes) {
        super(size, size);
        int mid = size / 2;
        int maxDistance = size / 2 * 32;
        TicketSystemList tileList = new TicketSystemList();
        tileList.addObject(100, (Object)-1);
        tileList.addObject(100, (Object)TileRegistry.ascendedCorruptionID);
        tileList.addObject(100, (Object)TileRegistry.ascendedGrowthID);
        tileList.addObject(25, (Object)TileRegistry.ascendedVoidID);
        tileList.addObject(75, (Object)TileRegistry.getTileID("deadwoodfloor"));
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                float chance;
                float distance = (float)new Point(mid * 32 + 16, mid * 32 + 16).distance(x * 32 + 16, y * 32 + 16);
                float distancePerc = distance / (float)maxDistance;
                if (distancePerc < 0.5f) {
                    this.setTile(x, y, (Integer)tileList.getRandomObject(random));
                    this.setObject(x, y, 0);
                    continue;
                }
                if (!(distancePerc <= 1.0f) || !random.getChance(chance = Math.abs((distancePerc - 0.5f) * 2.0f - 1.0f) * 2.0f)) continue;
                this.setTile(x, y, (Integer)tileList.getRandomObject(random));
                this.setObject(x, y, 0);
            }
        }
        int totalColumns = random.getIntBetween(8, 10);
        float columnAngle = random.nextInt(360);
        float anglePerColumn = 360.0f / (float)totalColumns;
        int columnID = ObjectRegistry.getObjectID("cryptcolumn");
        for (int i = 0; i < totalColumns; ++i) {
            Point2D.Float dir2 = GameMath.getAngleDir(columnAngle += random.getFloatOffset(anglePerColumn, anglePerColumn / 10.0f));
            float distance = (float)size / 3.0f;
            int tileX = (int)((float)mid + dir2.x * distance);
            int tileY = (int)((float)mid + dir2.y * distance);
            this.setObject(tileX, tileY, columnID);
        }
        this.setObject(mid, mid, ObjectRegistry.getObjectID("ascendedpylon"));
        if (attackIndexes != null && !attackIndexes.isEmpty()) {
            this.addCustomApply(mid, mid, 0, (level, levelX, levelY, dir, blackboard) -> {
                AscendedPylonObjectEntity objectEntity = level.entityManager.getObjectEntity(levelX, levelY, AscendedPylonObjectEntity.class);
                if (objectEntity != null) {
                    objectEntity.possibleAttackIndexes = new ArrayList(attackIndexes);
                }
                return null;
            }, true);
        }
    }
}

