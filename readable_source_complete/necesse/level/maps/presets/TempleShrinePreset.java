/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;

public class TempleShrinePreset
extends Preset {
    public TempleShrinePreset(GameRandom random) {
        super("PRESET = {\n\twidth = 5,\n\theight = 6,\n\ttileIDs = [35, sandbrick, 10, woodfloor],\n\ttiles = [35, 35, 10, 35, 35, 10, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 10, 35, 35, 35, 10, 35, 35, 35, 10, 35, 35, 35, 35],\n\tobjectIDs = [0, air, 64, sandstonedoor, 113, deepsandstonewall, 1, coin, 759, vase, 407, sandstonepressureplate, 232, paintingcooljonas, 345, palmcandelabra],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 113, 113, 113, 345, 232, 345, 113, 113, 759, 0, 1, 113, 113, 1, 407, 759, 113, 113, 113, 64, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 95, 87, 85, 85, 85, 93, 85, 85, 85, 85, 93, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 2,\n\t\t\ttileY = 2,\n\t\t\tstringID = tflipflopgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 1000,\n\t\t\t\twireInputs = 0100,\n\t\t\t\twireOutputs1 = 1000,\n\t\t\t\twireOutputs2 = 0000,\n\t\t\t\tflipped = false\n\t\t\t}\n\t\t}\n\t},\n\tobjectEntities = {\n\t\tentity = {\n\t\t\ttileX = 1,\n\t\t\ttileY = 4,\n\t\t\tdata = {\n\t\t\t\tcoinAmount = 71\n\t\t\t}\n\t\t},\n\t\tentity = {\n\t\t\ttileX = 2,\n\t\t\ttileY = 4,\n\t\t\tdata = {\n\t\t\t}\n\t\t},\n\t\tentity = {\n\t\t\ttileX = 3,\n\t\t\ttileY = 3,\n\t\t\tdata = {\n\t\t\t\tcoinAmount = 72\n\t\t\t}\n\t\t}\n\t}\n}");
        String rarePaintingID = random.getOneOf(PaintingSelectionTable.getRandomEpicPaintingIDBasedOnWeight(random));
        this.replaceObject("paintingcooljonas", rarePaintingID);
        this.setObject(2, 2, ObjectRegistry.getObjectID(rarePaintingID), 2);
        this.addCanApplyRectPredicate(0, this.height - 1, this.width, 1, 0, new Preset.ApplyAreaPredicateFunction(){

            @Override
            public boolean canApplyToLevel(Level level, int levelStartX, int levelStartY, int levelEndX, int levelEndY, int dir) {
                for (int tileX = levelStartX; tileX <= levelEndX; ++tileX) {
                    if (level.getObject((int)tileX, (int)levelStartY).isWall && !level.isSolidTile(tileX, levelStartY + 1)) continue;
                    return false;
                }
                return true;
            }
        });
    }
}

