/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.path.FinalPath;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class TilePathFindingDrawOptions
implements DrawOptions {
    private Point mouseTile;
    private GameCamera camera;
    private Pathfinding.Node mouseNode;
    private TilePathfinding finder;
    private DrawOptionsList drawOptions = new DrawOptionsList();
    private SharedTextureDrawOptions quadDrawOptions = new SharedTextureDrawOptions(Renderer.getQuadTexture());

    public TilePathFindingDrawOptions(PathResult<Point, TilePathfinding> result, GameCamera camera) {
        float floatCost;
        this.finder = (TilePathfinding)result.finder;
        this.camera = camera;
        this.drawOptions.add(this.quadDrawOptions::draw);
        double maxCost = Stream.concat(result.openNodes.stream(), result.closedNodes.stream()).map(t -> t.pathCost).max(Double::compareTo).orElse(1.0);
        int index = 0;
        for (Pathfinding.Node node : result.closedNodes) {
            floatCost = (float)(node.pathCost / maxCost);
            this.addNodeDrawOptions(node, result, index, floatCost, 0.0f, 0.0f, 0.5f, camera, this.finder.moveOffsetX, this.finder.moveOffsetY);
            ++index;
        }
        index = 0;
        for (Pathfinding.Node node : result.openNodes) {
            floatCost = (float)(node.pathCost / maxCost);
            this.addNodeDrawOptions(node, result, index, floatCost, 1.0f, 0.0f, 0.5f, camera, this.finder.moveOffsetX, this.finder.moveOffsetY);
            ++index;
        }
        for (Point point : result.invalidChecked) {
            int tileDrawX = camera.getTileDrawX(point.x);
            int tileDrawY = camera.getTileDrawY(point.y);
            this.quadDrawOptions.addFull().size(32, 32).color(0.0f, 1.0f, 1.0f, 0.25f).pos(tileDrawX, tileDrawY);
        }
        this.drawOptions.add(TilePathfinding.getPathLineDrawOptions(result.path, camera));
        int tileDrawX = camera.getTileDrawX(((Point)result.target).x);
        int n = camera.getTileDrawY(((Point)result.target).y);
        this.quadDrawOptions.addFull().size(32, 32).color(0.0f, 0.0f, 0.5f).pos(tileDrawX, n);
        Pathfinding.Node last = result.getLastPathNode();
        if (last != null) {
            this.addNodeDrawOptions(last, result, 0, 0.0f, 0.0f, 1.0f, 0.5f, camera, this.finder.moveOffsetX, this.finder.moveOffsetY);
        }
        this.drawOptions.add(() -> GameTooltipManager.addTooltip(new StringTooltips("Path iterations: " + result.iterations).add("Node priority: " + (Object)((Object)((TilePathfinding)result.finder).options.nodePriority)).add("Time taken: " + GameUtils.getTimeStringNano(result.nsTaken)).add("Found target: " + result.foundTarget), TooltipLocation.PLAYER));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TilePathFindingDrawOptions(Pathfinding.Process<TilePathfinding> process, GameCamera camera) {
        this.finder = (TilePathfinding)process.finder;
        this.camera = camera;
        Object object = process.nodeLock;
        synchronized (object) {
            float floatCost;
            double maxCost = Stream.concat(process.streamOpenNodes(), process.streamClosedNodes()).map(t -> t.pathCost).max(Double::compareTo).orElse(1.0);
            int index = 0;
            for (Pathfinding.Node node : process.getClosedNodes()) {
                floatCost = (float)(node.pathCost / maxCost);
                this.addNodeDrawOptions(node, process, index, floatCost, 0.0f, 0.0f, 0.5f, camera, this.finder.moveOffsetX, this.finder.moveOffsetY);
                ++index;
            }
            index = 0;
            for (Pathfinding.Node node : process.getOpenNodes()) {
                floatCost = (float)(node.pathCost / maxCost);
                this.addNodeDrawOptions(node, process, index, floatCost, 1.0f, 0.0f, 0.5f, camera, this.finder.moveOffsetX, this.finder.moveOffsetY);
                ++index;
            }
            for (Point point : process.getInvalidChecked()) {
                int tileDrawX = camera.getTileDrawX(point.x);
                int tileDrawY = camera.getTileDrawY(point.y);
                this.quadDrawOptions.addFull().size(32, 32).color(0.0f, 1.0f, 1.0f, 0.25f).pos(tileDrawX, tileDrawY);
            }
            int tileDrawX = camera.getTileDrawX(((Point)process.target).x);
            int n = camera.getTileDrawY(((Point)process.target).y);
            this.quadDrawOptions.addFull().size(32, 32).color(0.0f, 0.0f, 0.5f).pos(tileDrawX, n);
            this.addTileDrawOptions((Point)process.target, 0, 0.0f, 0.0f, 1.0f, 0.5f, camera);
            this.drawOptions.add(() -> GameTooltipManager.addTooltip(new StringTooltips("Path iterations: " + process.getIterations()).add("Node priority: " + (Object)((Object)this.finder.options.nodePriority)).add("Time taken: " + GameUtils.getTimeStringNano(process.getTime())), TooltipLocation.PLAYER));
        }
    }

    @Override
    public void draw() {
        this.mouseTile = new Point(this.camera.getMouseLevelTilePosX(), this.camera.getMouseLevelTilePosY());
        this.quadDrawOptions.draw();
        this.drawOptions.draw();
        if (this.mouseNode != null) {
            LinkedList<Pathfinding.Node> path = this.finder.constructPath(this.mouseNode);
            FinalPath finalPath = new FinalPath(TilePathfinding.reducePathPoints(this.finder, path));
            finalPath.drawPath(null, this.camera);
            GameTooltipManager.addTooltip(new StringTooltips("Path length: " + finalPath.getFullLength()).add("Time for speed 35: " + Entity.getTravelTimeMillis(35.0f, finalPath.getCurrentLength())), TooltipLocation.PLAYER);
        }
    }

    public boolean isMouseOverTile(int tileX, int tileY) {
        return this.mouseTile != null && this.mouseTile.x == tileX && this.mouseTile.y == tileY;
    }

    private void addNodeDrawOptions(Pathfinding.Node node, Pathfinding.Process<TilePathfinding> process, int index, float r, float g, float b, float alpha, GameCamera camera, int moveOffsetX, int moveOffsetY) {
        this.addNodeDrawOptions(node, (TilePathfinding)process.finder, index, r, g, b, alpha, camera, moveOffsetX, moveOffsetY);
    }

    private void addNodeDrawOptions(Pathfinding.Node node, PathResult<Point, TilePathfinding> result, int index, float r, float g, float b, float alpha, GameCamera camera, int moveOffsetX, int moveOffsetY) {
        this.addNodeDrawOptions(node, (TilePathfinding)result.finder, index, r, g, b, alpha, camera, moveOffsetX, moveOffsetY);
    }

    private void addNodeDrawOptions(Pathfinding.Node node, TilePathfinding finder, int index, float r, float g, float b, float alpha, GameCamera camera, int moveOffsetX, int moveOffsetY) {
        this.addNodeDrawOptions(node, finder.options, index, r, g, b, alpha, camera, moveOffsetX, moveOffsetY);
    }

    private void addNodeDrawOptions(Pathfinding.Node node, PathOptions pathOptions, int index, float r, float g, float b, float alpha, GameCamera camera, int moveOffsetX, int moveOffsetY) {
        this.addNodeDrawOptions(node, pathOptions.nodePriority, index, r, g, b, alpha, camera, moveOffsetX, moveOffsetY);
    }

    private void addNodeDrawOptions(Pathfinding.Node node, TilePathfinding.NodePriority nodePriority, int index, float r, float g, float b, float alpha, GameCamera camera, int moveOffsetX, int moveOffsetY) {
        int tileDrawX = camera.getTileDrawX(((Point)node.item).x);
        int tileDrawY = camera.getTileDrawY(((Point)node.item).y);
        if (tileDrawX > -32 && tileDrawY > -32 && tileDrawX < camera.getWidth() && tileDrawY < camera.getHeight()) {
            this.quadDrawOptions.addFull().size(32, 32).color(r, g, b, alpha).pos(tileDrawX, tileDrawY);
            if (node.cameFrom != null) {
                int fromTileDrawX = camera.getTileDrawX(((Point)node.cameFrom.item).x);
                int fromTileDrawY = camera.getTileDrawY(((Point)node.cameFrom.item).y);
                this.drawOptions.add(() -> Renderer.drawLineRGBA(tileDrawX + moveOffsetX, tileDrawY + moveOffsetY, fromTileDrawX + moveOffsetX, fromTileDrawY + moveOffsetY, 1.0f, 1.0f, 1.0f, 0.5f));
            }
            FontOptions fontOptions = new FontOptions(12).alphaf(0.8f);
            this.drawOptions.add(() -> {
                FontManager.bit.drawString(tileDrawX, tileDrawY, "" + index, fontOptions);
                if (this.isMouseOverTile(((Point)node.item).x, ((Point)node.item).y)) {
                    this.mouseNode = node;
                    GameTooltipManager.addTooltip(new StringTooltips("Path cost: " + GameMath.toDecimals(node.pathCost, 1)).add("Node cost: " + GameMath.toDecimals(node.nodeCost, 1)).add("Heuristic cost: " + GameMath.toDecimals(node.heuristicCost, 1)).add("Total cost: " + nodePriority.cost.getCost(node)).add("Path count: " + node.pathCount), TooltipLocation.PLAYER);
                }
            });
        }
    }

    private void addTileDrawOptions(Point tile, int index, float r, float g, float b, float alpha, GameCamera camera) {
        int tileDrawX = camera.getTileDrawX(tile.x);
        int tileDrawY = camera.getTileDrawY(tile.y);
        if (tileDrawX > -32 && tileDrawY > -32 && tileDrawX < camera.getWidth() && tileDrawY < camera.getHeight()) {
            this.quadDrawOptions.addFull().size(32, 32).color(r, g, b, alpha).pos(tileDrawX, tileDrawY);
            FontOptions fontOptions = new FontOptions(12).alphaf(0.8f);
            this.drawOptions.add(() -> FontManager.bit.drawString(tileDrawX, tileDrawY, "" + index, fontOptions));
        }
    }
}

