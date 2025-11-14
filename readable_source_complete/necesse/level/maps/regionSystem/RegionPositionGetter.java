/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public interface RegionPositionGetter {
    public Level getLevel();

    public PointSetAbstract<?> getRegionPositions();

    default public boolean isInRegion(int regionX, int regionY) {
        PointSetAbstract<?> regionPositions = this.getRegionPositions();
        return regionPositions.isEmpty() || regionPositions.contains(regionX, regionY);
    }

    default public boolean checkIfOccupyingRegions(Predicate<Point> checker) {
        PointSetAbstract<?> regionPositions = this.getRegionPositions();
        return regionPositions.isEmpty() || regionPositions.stream().anyMatch(checker);
    }

    default public Set<RegionPosition> getRegionPositionsCombined(RegionPositionGetter other) {
        PointSetAbstract<?> thisPositions = this.getRegionPositions();
        PointSetAbstract<?> otherPositions = other.getRegionPositions();
        HashSet<RegionPosition> finalPositions = new HashSet<RegionPosition>(thisPositions.size() + otherPositions.size());
        for (Point region : this.getRegionPositions()) {
            finalPositions.add(new RegionPosition(this.getLevel(), region.x, region.y));
        }
        for (Point region : other.getRegionPositions()) {
            finalPositions.add(new RegionPosition(other.getLevel(), region.x, region.y));
        }
        return finalPositions;
    }

    default public RegionPositionGetter saveRegionPosition() {
        final Level level = this.getLevel();
        final PointHashSet regionPositions = new PointHashSet(this.getRegionPositions());
        return new RegionPositionGetter(){

            @Override
            public Level getLevel() {
                return level;
            }

            @Override
            public PointSetAbstract<?> getRegionPositions() {
                return regionPositions;
            }
        };
    }
}

