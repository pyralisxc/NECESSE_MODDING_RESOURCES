/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.RegionLevelDataComponent;
import necesse.level.maps.levelData.SingleRegionBasedLevelData;
import necesse.level.maps.regionSystem.Region;

public class OneWorldPirateVillageData
extends SingleRegionBasedLevelData<PirateVillageRegionData>
implements RegionLevelDataComponent {
    public static OneWorldPirateVillageData getPirateVillageData(Level level, boolean createNewIfNull) {
        LevelData data = level.getLevelData("oneworldpiratevillagedata");
        if (data instanceof OneWorldPirateVillageData) {
            return (OneWorldPirateVillageData)data;
        }
        if (createNewIfNull) {
            OneWorldPirateVillageData newData = new OneWorldPirateVillageData();
            level.addLevelData("oneworldpiratevillagedata", newData);
            return newData;
        }
        return null;
    }

    @Override
    public void addRegionSaveData(SaveData save, PirateVillageRegionData data) {
        if (!data.pirateTileRectangles.isEmpty()) {
            for (Rectangle tileRectangle : data.pirateTileRectangles) {
                SaveData rectangleSave = new SaveData("PIRATE_TILE_RECTANGLE");
                rectangleSave.addPoint("tile", new Point(tileRectangle.x, tileRectangle.y));
                rectangleSave.addPoint("size", new Point(tileRectangle.width, tileRectangle.height));
                save.addSaveData(rectangleSave);
            }
        }
    }

    @Override
    public PirateVillageRegionData loadRegionData(Region region, LoadData save) {
        PirateVillageRegionData data = new PirateVillageRegionData(region.regionX, region.regionY);
        for (LoadData rectangleSave : save.getLoadDataByName("PIRATE_TILE_RECTANGLE")) {
            Point size;
            Point tile = rectangleSave.getPoint("tile", null, false);
            if (tile == null || (size = rectangleSave.getPoint("size", null, false)) == null) continue;
            data.pirateTileRectangles.add(new Rectangle(tile.x, tile.y, size.x, size.y));
        }
        if (data.pirateTileRectangles.isEmpty()) {
            return null;
        }
        return data;
    }

    public boolean isPirateTile(int tileX, int tileY) {
        int regionY;
        int regionX = GameMath.getRegionCoordByTile(tileX);
        PirateVillageRegionData data = (PirateVillageRegionData)this.getDataInRegion(regionX, regionY = GameMath.getRegionCoordByTile(tileY));
        return data != null && data.pirateTileRectangles.stream().anyMatch(r -> r.contains(tileX, tileY));
    }

    public void addPirateTileRectangle(Rectangle tileRectangle) {
        int regionStartX = GameMath.getRegionCoordByTile(tileRectangle.x);
        int regionStartY = GameMath.getRegionCoordByTile(tileRectangle.y);
        int regionEndX = GameMath.getRegionCoordByTile(tileRectangle.x + tileRectangle.width - 1);
        int regionEndY = GameMath.getRegionCoordByTile(tileRectangle.y + tileRectangle.height - 1);
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                Region region = this.getLevel().regionManager.getRegion(regionX, regionY, true);
                if (region == null) continue;
                region.isPirateVillageRegion = true;
                int regionTileX = GameMath.getTileCoordByRegion(regionX);
                int regionTileY = GameMath.getTileCoordByRegion(regionY);
                Rectangle regionTileRectangle = new Rectangle(regionTileX, regionTileY, 16, 16);
                Rectangle intersection = tileRectangle.intersection(regionTileRectangle);
                PirateVillageRegionData data = this.computeDataInRegion(regionX, regionY, (p, value) -> {
                    if (value == null) {
                        value = new PirateVillageRegionData(p.x, p.y);
                    }
                    return value;
                });
                data.pirateTileRectangles.add(intersection);
            }
        }
    }

    public static class PirateVillageRegionData
    extends SingleRegionBasedLevelData.RegionData {
        protected LinkedList<Rectangle> pirateTileRectangles = new LinkedList();

        public PirateVillageRegionData(int regionX, int regionY) {
            super(regionX, regionY);
        }
    }
}

