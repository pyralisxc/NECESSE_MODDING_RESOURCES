/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.DisposableExecutorService;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FutureAITask;
import necesse.entity.mobs.ai.path.FinalPath;
import necesse.entity.mobs.ai.path.FinalPathPoint;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.ai.path.TilePathfinding;

public class MoveToTileAITask
extends FutureAITask<AIPathResult> {
    private MoveToTileAITask(Callable<AIPathResult> task, Function<AIPathResult, AINodeResult> handler) {
        super((DisposableExecutorService)null, task, handler);
    }

    private MoveToTileAITask(TickManager tickManagerChild, AIMover mover, AINode<?> node, int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, int maxPathIterations, PathOptions pathOptions, Function<AIPathResult, AINodeResult> handler) {
        super(((Entity)node.mob()).getLevel(), () -> {
            PathResult<Point, TilePathfinding> path = TilePathfinding.findPath(tickManagerChild, node.mob(), tileX, tileY, pathOptions, isAtTarget, maxPathIterations);
            return new AIPathResult(mover, node, path);
        }, handler);
    }

    public static MoveToTileAITask pathToTile(AIMover mover, AINode<?> node, int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, int maxPathIterations, PathOptions pathOptions, Function<AIPathResult, AINodeResult> handler) {
        TickManager tickManager = ((Entity)node.mob()).getLevel().tickManager();
        return new MoveToTileAITask(tickManager == null ? null : tickManager.getChild(), mover, node, tileX, tileY, isAtTarget, maxPathIterations, pathOptions, handler);
    }

    public static MoveToTileAITask directMoveToTile(AIMover mover, AINode<?> node, int tileX, int tileY, Function<AIPathResult, AINodeResult> handler) {
        return new MoveToTileAITask(() -> new DirectAIPathResult(mover, node, tileX, tileY), handler);
    }

    private static class DirectAIPathResult
    extends AIPathResult {
        private final int tileX;
        private final int tileY;

        public DirectAIPathResult(AIMover mover, AINode<?> node, int tileX, int tileY) {
            super(mover, node, null);
            this.tileX = tileX;
            this.tileY = tileY;
        }

        @Override
        public boolean isMobWithinStart(int tiles) {
            return true;
        }

        @Override
        public boolean isResultWithin(int tiles) {
            return true;
        }

        @Override
        public FinalPath getFinalPath() {
            if (this.finalPath == null) {
                Object mob = this.node.mob();
                List<FinalPathPoint> pathPoints = Arrays.asList(new FinalPathPoint(((Entity)mob).getTileX(), ((Entity)mob).getTileY(), () -> true), new FinalPathPoint(this.tileX, this.tileY, () -> true));
                this.finalPath = new FinalPath(new ArrayList<FinalPathPoint>(pathPoints));
            }
            return this.finalPath;
        }

        @Override
        public float getFullPathLength() {
            return ((Mob)this.node.mob()).getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16);
        }

        @Override
        public float getCurrentPathLength() {
            return ((Mob)this.node.mob()).getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16);
        }

        @Override
        public boolean moveIfWithin(int startTileRange, int resultTileRange, Runnable pathInvalidated) {
            this.move(pathInvalidated);
            return true;
        }

        @Override
        public void move(Runnable pathInvalidated) {
            this.mover.directMoveTo(this.node, this.tileX * 32 + 16, this.tileY * 32 + 16);
        }
    }

    public static class AIPathResult {
        protected final AIMover mover;
        protected final AINode<?> node;
        public final PathResult<Point, TilePathfinding> result;
        protected FinalPath finalPath;

        private AIPathResult(AIMover mover, AINode<?> node, PathResult<Point, TilePathfinding> result) {
            this.mover = mover;
            this.node = node;
            this.result = result;
        }

        public boolean isMobWithinStart(int tiles) {
            if (this.result.start == null) {
                return false;
            }
            if (tiles < 0) {
                return true;
            }
            return GameMath.squareDistance(((Point)this.result.start).x, ((Point)this.result.start).y, GameMath.getTileCoordinate(((Entity)this.node.mob()).getX() + ((TilePathfinding)this.result.finder).moveOffsetX), GameMath.getTileCoordinate(((Entity)this.node.mob()).getY() + ((TilePathfinding)this.result.finder).moveOffsetY)) <= (float)tiles;
        }

        public boolean isResultWithin(int tiles) {
            if (this.result.target == null) {
                return false;
            }
            if (tiles < 0) {
                return true;
            }
            if (TilePathfinding.isResultWithin(this.result, ((Point)this.result.target).x, ((Point)this.result.target).y, tiles)) {
                return true;
            }
            Point last = this.result.getLastPathResult();
            return last != null && ((TilePathfinding)this.result.finder).doorOption.canBreakDown(last.x, last.y);
        }

        public FinalPath getFinalPath() {
            if (this.finalPath == null) {
                ArrayList<FinalPathPoint> finalPathPoints = TilePathfinding.reducePathPoints((TilePathfinding)this.result.finder, this.result.path);
                this.finalPath = new FinalPath(finalPathPoints);
            }
            return this.finalPath;
        }

        public float getFullPathLength() {
            return this.getFinalPath().getFullLength();
        }

        public float getCurrentPathLength() {
            return this.getFinalPath().getCurrentLength();
        }

        public float estimateMillisToFullPathWithSpeed(float mobSpeed) {
            return Entity.getTravelTimeMillis(mobSpeed, this.getFullPathLength());
        }

        public int getNextPathTimeBasedOnPathTime(float mobSpeed, float timeDivisor, int minimumMillis, float randomPercentOffset) {
            float timeToPath = this.estimateMillisToFullPathWithSpeed(mobSpeed);
            int finalTime = (int)(timeToPath / timeDivisor);
            int offset = (int)((float)finalTime * randomPercentOffset);
            return Math.max(minimumMillis, GameRandom.globalRandom.getIntOffset(finalTime, offset));
        }

        public boolean moveIfWithin(int startTileRange, int resultTileRange, Runnable pathInvalidated) {
            if (this.isMobWithinStart(startTileRange) && this.isResultWithin(resultTileRange)) {
                this.move(pathInvalidated);
                return true;
            }
            return false;
        }

        public void move(Runnable pathInvalidated) {
            this.mover.setPath(this.node, this.getFinalPath(), pathInvalidated);
        }
    }
}

