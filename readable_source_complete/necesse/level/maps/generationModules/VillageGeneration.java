/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.generationModules.VillageModularGeneration;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageFarm3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse4Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse5Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse6Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse7Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse8Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillageHouse9Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePathPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;
import necesse.level.maps.presets.set.VillageSet;

public class VillageGeneration {
    private final Level level;
    private final int maxSize;
    public GameRandom random;
    private final VillageSet defaultSet = VillageSet.defaultSet;
    private final TicketSystemList<VillageSet> villageSets;
    private final ArrayList<AddedPreset> basicPresets = new ArrayList();
    private final ArrayList<AddedPreset> edgePresets = new ArrayList();

    public VillageGeneration(Level level, int maxSize, TicketSystemList<VillageSet> villageSets, GameRandom random) {
        this.level = level;
        this.maxSize = maxSize;
        this.villageSets = villageSets;
        this.random = random;
    }

    private static TicketSystemList<VillageSet> toTicketSystem(VillageSet villageSet) {
        TicketSystemList<VillageSet> ticketSystem = new TicketSystemList<VillageSet>();
        ticketSystem.addObject(100, (Object)villageSet);
        return ticketSystem;
    }

    public VillageGeneration addStandardPresets() {
        this.addBasicPreset(new VillageHouse1Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse2Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse5Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse6Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse7Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse8Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse9Preset(this.random), 8, 1);
        this.addBasicPreset(new VillageHouse1Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse2Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse5Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse6Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse7Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse8Preset(this.random), 3, 2);
        this.addBasicPreset(new VillageHouse9Preset(this.random), 3, 2);
        this.addEdgePreset(new VillageFarm1Preset(this.random), 8, 2);
        this.addEdgePreset(new VillageFarm2Preset(this.random), 25, 1);
        this.addEdgePreset(new VillageFarm3Preset(this.random), 25, 1);
        this.addEdgePreset(new VillageFarm2Preset(this.random), 5, 1);
        this.addEdgePreset(new VillageFarm3Preset(this.random), 5, 1);
        this.addEdgePreset(new VillageHouse3Preset(this.random), 50, 1);
        this.addEdgePreset(new VillageHouse4Preset(this.random), 50, 1);
        return this;
    }

    public VillageGeneration addBasicPreset(VillagePreset preset, int tickets, int maxUsage) {
        this.basicPresets.add(new AddedPreset(preset, tickets, maxUsage));
        return this;
    }

    public VillageGeneration addEdgePreset(VillagePreset preset, int tickets, int maxUsage) {
        this.edgePresets.add(new AddedPreset(preset, tickets, maxUsage));
        return this;
    }

    public void generate(OneWorldNPCVillageData villageData, int centerTileX, int centerTileY) {
        int cellsSize = this.maxSize / 3;
        VillageModularGeneration mg = new VillageModularGeneration(this.level, cellsSize, cellsSize, 3, 3, 1){

            @Override
            public Point getStartCell() {
                return new Point(this.cellsWidth / 2, this.cellsHeight / 2);
            }
        };
        mg.random = this.random;
        int xOffset = centerTileX - mg.cellRes * mg.cellsWidth / 2;
        int yOffset = centerTileY - mg.cellRes * mg.cellsHeight / 2;
        Rectangle generationArea = new Rectangle((mg.getCellRealX(0) + xOffset) * 32, (mg.getCellRealY(0) + yOffset) * 32, mg.cellsWidth * mg.cellRes * 32, mg.cellsHeight * mg.cellRes * 32);
        this.level.entityManager.mobs.streamInRegionsShape(generationArea, 0).filter(m -> generationArea.contains(m.getX(), m.getY())).forEach(Mob::remove);
        VillageSet villageSet = this.villageSets.getRandomObject(this.random);
        VillagePreset path = this.defaultSet.replaceWith(villageSet, new VillagePathPreset());
        mg.setStartPreset(path);
        mg.initGeneration(xOffset, yOffset);
        Point startCell = mg.getStartCell();
        int reach = Math.max(this.random.getIntBetween(cellsSize / 6, cellsSize / 3), 2);
        for (int i = 1; i <= reach; ++i) {
            mg.applyPreset(path, new Point(startCell.x - i, startCell.y), false, false, xOffset, yOffset, new Point(startCell.x - (i - 1), startCell.y));
            mg.applyPreset(path, new Point(startCell.x + i, startCell.y), false, false, xOffset, yOffset, new Point(startCell.x + (i - 1), startCell.y));
            mg.applyPreset(path, new Point(startCell.x, startCell.y - i), false, false, xOffset, yOffset, new Point(startCell.x, startCell.y - (i - 1)));
            mg.applyPreset(path, new Point(startCell.x, startCell.y + i), false, false, xOffset, yOffset, new Point(startCell.x, startCell.y + (i - 1)));
        }
        VillagePathPreset path1 = this.defaultSet.replaceWith(villageSet, new VillagePathPreset(true, true, true, true));
        mg.addPreset(path1, 6);
        VillagePathPreset path2 = this.defaultSet.replaceWith(villageSet, new VillagePathPreset(true, false, true, false));
        mg.addPreset(path2, 3);
        VillagePathPreset path3 = this.defaultSet.replaceWith(villageSet, new VillagePathPreset(false, true, false, true));
        mg.addPreset(path3, 3);
        mg.tickGeneration(xOffset, yOffset, 4);
        for (AddedPreset basicPreset : this.basicPresets) {
            mg.addPreset(basicPreset.preset, basicPreset.tickets, basicPreset.maxUsage);
        }
        mg.tickGeneration(xOffset, yOffset, this.maxSize / 2);
        for (AddedPreset edgePreset : this.edgePresets) {
            mg.addPreset(edgePreset.preset, edgePreset.tickets, edgePreset.maxUsage);
        }
        mg.tickGeneration(xOffset, yOffset, this.maxSize * 5);
        ArrayList<VillagePathLocation> closedCells = new ArrayList<VillagePathLocation>();
        ArrayList<VillagePathLocation> openCells = new ArrayList<VillagePathLocation>();
        VillagePathLocation startLocation = new VillagePathLocation(null, mg.getPlacedPreset(startCell));
        startLocation.markGoesSomewhere();
        openCells.add(startLocation);
        while (!openCells.isEmpty()) {
            VillagePathLocation current = (VillagePathLocation)openCells.remove(0);
            if (current.preset.isPresetOpenTop()) {
                this.tickPathRemove(mg, current, current.preset.cell.x, current.preset.cell.y - 1, closedCells, openCells);
            }
            if (current.preset.isPresetOpenBot()) {
                this.tickPathRemove(mg, current, current.preset.cell.x, current.preset.cell.y + 1, closedCells, openCells);
            }
            if (current.preset.isPresetOpenRight()) {
                this.tickPathRemove(mg, current, current.preset.cell.x + 1, current.preset.cell.y, closedCells, openCells);
            }
            if (current.preset.isPresetOpenLeft()) {
                this.tickPathRemove(mg, current, current.preset.cell.x - 1, current.preset.cell.y, closedCells, openCells);
            }
            closedCells.add(current);
        }
        for (VillagePathLocation closedCell : closedCells) {
            int realX = mg.getCellRealX(closedCell.preset.cell.x) + xOffset;
            int realY = mg.getCellRealY(closedCell.preset.cell.y) + yOffset;
            if (closedCell.goesSomewhere()) continue;
            closedCell.preset.replacedLevel.applyToLevel(this.level, realX, realY);
            VillagePathLocation parent = closedCell.parent;
            while (parent != null && parent.goesSomewhere()) {
                mg.openCell(closedCell.parent.preset.cell);
                parent = parent.parent;
            }
            mg.removePlacedPreset(closedCell.preset);
        }
        mg.removeAllPresets(path1);
        mg.removeAllPresets(path2);
        mg.removeAllPresets(path3);
        mg.tickGeneration(xOffset, yOffset, this.maxSize * 5);
        mg.endGeneration();
        if (villageData != null) {
            for (PlacedPreset placedPreset : mg.getPlacedPresets()) {
                int startTileX = mg.getCellRealX(placedPreset.cell.x) + xOffset - (placedPreset.xMirror ? placedPreset.preset.getMirroredX(0) - (mg.cellRes - 1) : 0);
                int startTileY = mg.getCellRealY(placedPreset.cell.y) + yOffset - (placedPreset.yMirror ? placedPreset.preset.getMirroredY(0) - (mg.cellRes - 1) : 0);
                if (placedPreset.x != 0 || placedPreset.y != 0) continue;
                for (int x = 0; x < placedPreset.preset.width; ++x) {
                    int realTileX = startTileX + x;
                    int presetX = placedPreset.xMirror ? placedPreset.preset.getMirroredX(x) : x;
                    block8: for (int y = 0; y < placedPreset.preset.height; ++y) {
                        int realTileY = startTileY + y;
                        int presetY = placedPreset.yMirror ? placedPreset.preset.getMirroredY(y) : y;
                        int tileID = placedPreset.preset.getTile(presetX, presetY);
                        if (tileID != -1 && tileID != 0) {
                            for (Point offset : Level.adjacentGettersWithCenter) {
                                villageData.addVillageTile(realTileX + offset.x, realTileY + offset.y);
                            }
                            continue;
                        }
                        for (int layerID = 0; layerID < ObjectLayerRegistry.getTotalLayers(); ++layerID) {
                            int objectID = placedPreset.preset.getObject(layerID, presetX, presetY);
                            if (objectID == -1 || objectID == 0) continue;
                            for (Point offset : Level.adjacentGettersWithCenter) {
                                villageData.addVillageTile(realTileX + offset.x, realTileY + offset.y);
                            }
                            continue block8;
                        }
                    }
                }
            }
        }
    }

    private void tickPathRemove(ModularGeneration mg, VillagePathLocation current, int nextX, int nextY, List<VillagePathLocation> closedCells, List<VillagePathLocation> openCells) {
        PlacedPreset nextPreset = mg.getPlacedPreset(new Point(nextX, nextY));
        if (nextPreset == null) {
            return;
        }
        if (nextPreset.preset instanceof VillagePathPreset) {
            if (closedCells.stream().noneMatch(c -> c.preset.cell.equals(new Point(nextX, nextY)))) {
                openCells.add(new VillagePathLocation(current, nextPreset));
            }
        } else {
            current.markGoesSomewhere();
        }
    }

    private class AddedPreset {
        public final VillagePreset preset;
        public final int tickets;
        public final int maxUsage;

        public AddedPreset(VillagePreset preset, int tickets, int maxUsage) {
            preset.replaceObject("storagebox", "sprucechest");
            VillageSet villageSet = (VillageSet)VillageGeneration.this.villageSets.getRandomObject(VillageGeneration.this.random);
            this.preset = VillageGeneration.this.defaultSet.replaceWith(villageSet, preset);
            this.tickets = tickets;
            this.maxUsage = maxUsage;
        }
    }

    private static class VillagePathLocation {
        public final VillagePathLocation parent;
        public final PlacedPreset preset;
        private boolean goesSomewhere = false;

        public VillagePathLocation(VillagePathLocation parent, PlacedPreset preset) {
            this.parent = parent;
            this.preset = preset;
        }

        public void markGoesSomewhere() {
            this.goesSomewhere = true;
            if (this.parent != null) {
                this.parent.markGoesSomewhere();
            }
        }

        public boolean goesSomewhere() {
            return this.goesSomewhere;
        }
    }
}

