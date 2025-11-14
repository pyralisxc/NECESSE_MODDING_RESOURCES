/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.Preset;

public class DungeonEntrancePreset
extends Preset {
    public DungeonEntrancePreset(GameRandom random) {
        this(20, random);
    }

    private DungeonEntrancePreset(int size, GameRandom random) {
        super(size, size);
        int mid = size / 2;
        int maxDistance = size / 2 * 32;
        int dungeonFloor = TileRegistry.getTileID("dungeonfloor");
        int dungeonWall = ObjectRegistry.getObjectID("dungeonwall");
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                float chance;
                float distance = (float)new Point(mid * 32 + 16, mid * 32 + 16).distance(x * 32 + 16, y * 32 + 16);
                float distancePerc = distance / (float)maxDistance;
                if (distancePerc < 0.4f) {
                    this.setTile(x, y, dungeonFloor);
                    this.setObject(x, y, 0);
                } else if (distancePerc <= 1.0f && random.getChance(chance = Math.abs((distancePerc - 0.5f) * 2.0f - 1.0f) * 2.0f)) {
                    this.setTile(x, y, dungeonFloor);
                    this.setObject(x, y, 0);
                }
                if (!(distance < (float)maxDistance) || !(distance >= (float)(maxDistance - 40)) || !random.getChance(0.4f)) continue;
                this.setObject(x, y, dungeonWall);
            }
        }
        int chaliceOffset = 3;
        this.setFireChalice(mid - chaliceOffset - 1, mid - chaliceOffset - 1);
        this.setFireChalice(mid - chaliceOffset - 1, mid + chaliceOffset);
        this.setFireChalice(mid + chaliceOffset, mid - chaliceOffset - 1);
        this.setFireChalice(mid + chaliceOffset, mid + chaliceOffset);
        this.setObject(mid, mid, ObjectRegistry.getObjectID("dungeonentrance"));
        int mobOffset = 5;
        this.addMob("voidapprentice", mid - mobOffset, mid - mobOffset, false);
        this.addMob("voidapprentice", mid + mobOffset, mid - mobOffset, false);
        this.addMob("voidapprentice", mid - mobOffset, mid + mobOffset, false);
        this.addMob("voidapprentice", mid + mobOffset, mid + mobOffset, false);
    }

    private void setFireChalice(int x, int y) {
        this.setObject(x, y, ObjectRegistry.getObjectID("firechalice"));
        this.setObject(x + 1, y, ObjectRegistry.getObjectID("firechalice2"));
        this.setObject(x, y + 1, ObjectRegistry.getObjectID("firechalice3"));
        this.setObject(x + 1, y + 1, ObjectRegistry.getObjectID("firechalice4"));
    }
}

