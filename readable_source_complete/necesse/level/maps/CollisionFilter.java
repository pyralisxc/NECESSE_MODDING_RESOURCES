/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.TilePosition;
import necesse.level.maps.regionSystem.RegionType;

public class CollisionFilter {
    private Predicate<TilePosition> filter = null;
    private final LinkedList<BiConsumer<TilePosition, LinkedList<Rectangle>>> customAdders = new LinkedList();

    public CollisionFilter copy() {
        CollisionFilter copy = new CollisionFilter();
        copy.filter = this.filter;
        copy.customAdders.addAll(this.customAdders);
        return copy;
    }

    public boolean hasAdders() {
        return !this.customAdders.isEmpty();
    }

    public CollisionFilter customAdder(BiConsumer<TilePosition, LinkedList<Rectangle>> adder) {
        this.customAdders.add(adder);
        return this;
    }

    public CollisionFilter mobCollision() {
        return this.customAdder((tp, rectangles) -> rectangles.addAll(tp.object().getCollisions(tp.object().rotation)));
    }

    public CollisionFilter addEmptyTiles() {
        return this.customAdder((tp, rectangles) -> {
            if (tp.tileID() == TileRegistry.emptyID) {
                rectangles.add(new Rectangle(GameMath.getLevelCoordinate(tp.tileX), GameMath.getLevelCoordinate(tp.tileY), 32, 32));
            }
        });
    }

    public CollisionFilter projectileCollision() {
        return this.customAdder((tp, rectangles) -> rectangles.addAll(tp.object().getProjectileCollisions(tp.object().rotation)));
    }

    public CollisionFilter attackThroughCollision() {
        return this.attackThroughCollision(tp -> tp.object().object.attackThrough);
    }

    public CollisionFilter attackThroughCollision(Predicate<TilePosition> filter) {
        return this.customAdder((tp, rectangles) -> {
            if (filter.test((TilePosition)tp)) {
                rectangles.addAll(tp.object().getAttackThroughCollisions());
            }
        });
    }

    public CollisionFilter summonedMobCollision() {
        return this.addFilter(tp -> tp.object().object.getRegionType() != RegionType.SUMMON_IGNORED);
    }

    public CollisionFilter allLandTiles() {
        return this.customAdder((tp, rectangles) -> {
            if (!tp.tile().tile.isLiquid) {
                rectangles.add(new Rectangle(tp.tileX * 32, tp.tileY * 32, 32, 32));
            }
        });
    }

    public CollisionFilter allLandExShoreTiles() {
        return this.customAdder((tp, rectangles) -> {
            if (!(tp.tile().tile.isLiquid || tp.level.liquidManager.getHeight(tp.tileX, tp.tileY) < 2 && tp.tile().tile.terrainSplatting)) {
                rectangles.add(new Rectangle(tp.tileX * 32, tp.tileY * 32, 32, 32));
            }
        });
    }

    public CollisionFilter allLiquidTiles() {
        return this.customAdder((tp, rectangles) -> {
            if (tp.tile().tile.isLiquid) {
                rectangles.add(new Rectangle(tp.tileX * 32, tp.tileY * 32, 32, 32));
            }
        });
    }

    public CollisionFilter overrideFilter(Predicate<TilePosition> filter) {
        this.filter = filter;
        return this;
    }

    public CollisionFilter addFilter(Predicate<TilePosition> filter) {
        this.filter = this.filter == null ? filter : this.filter.and(filter);
        return this;
    }

    public boolean testFilter(TilePosition tp) {
        if (this.filter == null) {
            return true;
        }
        return this.filter.test(tp);
    }

    public boolean check(Shape shape, TilePosition tp) {
        CollisionsGenerator generator = new CollisionsGenerator(tp);
        Rectangle next = generator.getNext();
        while (next != null) {
            if (shape.intersects(next)) {
                return true;
            }
            next = generator.getNext();
        }
        return false;
    }

    public void addCollisions(ArrayList<LevelObjectHit> hits, Shape shape, TilePosition tp) {
        CollisionsGenerator generator = new CollisionsGenerator(tp);
        Rectangle next = generator.getNext();
        while (next != null) {
            if (shape.intersects(next)) {
                hits.add(new LevelObjectHit(next, tp.level, tp.tileX, tp.tileY));
            }
            next = generator.getNext();
        }
    }

    private class CollisionsGenerator {
        private final LinkedList<Runnable> adders = new LinkedList();
        private final LinkedList<Rectangle> rectangles = new LinkedList();

        public CollisionsGenerator(TilePosition tp) {
            if (!CollisionFilter.this.testFilter(tp)) {
                return;
            }
            for (BiConsumer adder : CollisionFilter.this.customAdders) {
                this.adders.add(() -> adder.accept(tp, this.rectangles));
            }
        }

        public Rectangle getNext() {
            while (this.rectangles.isEmpty() && !this.adders.isEmpty()) {
                this.adders.removeFirst().run();
            }
            while (!this.rectangles.isEmpty()) {
                Rectangle first = this.rectangles.removeFirst();
                if (first == null) continue;
                return first;
            }
            return null;
        }
    }
}

