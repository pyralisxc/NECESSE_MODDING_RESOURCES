/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.WallSet;

public class FurnitureHousePreset
extends Preset {
    public FurnitureHousePreset(int width, int height, int floorID, WallSet wallSet, GameRandom random, TicketSystemList<Preset> furniture, float placeChance) {
        super(width, height);
        this.fillTile(0, 0, width, height, floorID);
        this.fillObject(0, 0, width, height, 0);
        this.boxObject(0, 0, width, height, wallSet.wall);
        int doorX = width / 2;
        this.setObject(doorX, 0, wallSet.doorClosed, 0);
        if (width % 2 == 0) {
            this.setObject(doorX - 1, 0, wallSet.doorClosed, 0);
        }
        this.addCustomApplyAreaEach(1, 1, width - 2, height - 2, 2, (level, levelX, levelY, dir, blackboard) -> {
            if (random.getChance(placeChance)) {
                GenerationTools.generateFurniture(level, random, levelX, levelY, furniture, pos -> pos.objectID() == 0);
            }
            return null;
        });
    }
}

