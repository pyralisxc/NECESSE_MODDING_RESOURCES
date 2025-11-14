/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.client.Client
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.Renderer
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.placeableItem.objectItem.ObjectItem
 *  necesse.inventory.item.placeableItem.tileItem.TileItem
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameTile.GameTile
 *  necesse.level.maps.Level
 *  necesse.level.maps.hudManager.HudDrawElement
 *  necesse.level.maps.multiTile.MultiTile
 */
package medievalsim.buildmode;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import medievalsim.buildmode.BlockCountCalculator;
import medievalsim.buildmode.BuildModeManager;
import medievalsim.buildmode.ShapeCalculator;
import medievalsim.patches.ObjectItemOnAttackPatch;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.multiTile.MultiTile;

public class BuildModePreviewElement
extends HudDrawElement {
    private final Client client;
    private Point lastMouseTile = null;
    private int lastShape = -1;
    private boolean lastHollow = false;
    private int lastPlayerDir = -1;
    private int lastLineLength = -1;
    private int lastSquareSize = -1;
    private int lastCircleRadius = -1;
    private int lastSpacing = -1;
    private int lastObjectRotation = -1;
    private int lastObjectWidth = -1;
    private int lastObjectHeight = -1;
    private String lastItemStringID = null;
    private List<Point> cachedPositions = null;

    public BuildModePreviewElement(Client client) {
        this.client = client;
    }

    public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
        List<Point> previewPositions;
        boolean cacheValid;
        BuildModeManager manager = BuildModeManager.getInstance();
        manager.checkLevelChange();
        if (!manager.buildModeEnabled) {
            return;
        }
        final PlayerMob player = this.client.getPlayer();
        if (player == null) {
            return;
        }
        PlayerInventorySlot slot = player.getSelectedItemSlot();
        final InventoryItem selectedItem = slot.getInv(player.getInv()).getItem(slot.slot);
        if (selectedItem == null || !selectedItem.item.isPlaceable()) {
            return;
        }
        final Item item = selectedItem.item;
        int mouseLevelX = camera.getMouseLevelPosX();
        int mouseLevelY = camera.getMouseLevelPosY();
        int mouseTileX = GameMath.getTileCoordinate((int)mouseLevelX);
        int mouseTileY = GameMath.getTileCoordinate((int)mouseLevelY);
        final Level level = this.getLevel();
        int playerDir = player.isAttacking ? player.beforeAttackDir : player.getDir();
        final int objectRotation = ObjectItemOnAttackPatch.calculateFinalRotation(playerDir, manager.direction);
        GameObject previewObject = null;
        int objectWidth = 1;
        int objectHeight = 1;
        if (item instanceof ObjectItem && (previewObject = ((ObjectItem)item).getObject()) != null) {
            MultiTile multiTile = previewObject.getMultiTile(objectRotation);
            objectWidth = multiTile.width;
            objectHeight = multiTile.height;
        }
        String currentItemStringID = item.getStringID();
        boolean bl = cacheValid = this.cachedPositions != null && this.lastMouseTile != null && this.lastMouseTile.x == mouseTileX && this.lastMouseTile.y == mouseTileY && this.lastShape == manager.selectedShape && this.lastHollow == manager.isHollow && this.lastPlayerDir == playerDir && this.lastLineLength == manager.lineLength && this.lastSquareSize == manager.squareSize && this.lastCircleRadius == manager.circleRadius && this.lastSpacing == manager.spacing && this.lastObjectRotation == objectRotation && this.lastObjectWidth == objectWidth && this.lastObjectHeight == objectHeight && currentItemStringID.equals(this.lastItemStringID);
        if (cacheValid) {
            previewPositions = this.cachedPositions;
        } else {
            this.cachedPositions = previewPositions = ShapeCalculator.calculatePositions(mouseTileX, mouseTileY, manager.selectedShape, manager.isHollow, playerDir, manager.lineLength, manager.squareSize, manager.circleRadius, manager.spacing, objectWidth, objectHeight);
            this.lastMouseTile = new Point(mouseTileX, mouseTileY);
            this.lastShape = manager.selectedShape;
            this.lastHollow = manager.isHollow;
            this.lastPlayerDir = playerDir;
            this.lastLineLength = manager.lineLength;
            this.lastSquareSize = manager.squareSize;
            this.lastCircleRadius = manager.circleRadius;
            this.lastSpacing = manager.spacing;
            this.lastObjectRotation = objectRotation;
            this.lastObjectWidth = objectWidth;
            this.lastObjectHeight = objectHeight;
            this.lastItemStringID = currentItemStringID;
        }
        for (final Point pos : previewPositions) {
            list.add(new SortedDrawable(){

                public int getPriority() {
                    return -100000;
                }

                public void draw(TickManager tickManager) {
                    try {
                        ObjectItem objectItem;
                        GameObject object;
                        if (item instanceof TileItem) {
                            TileItem tileItem = (TileItem)item;
                            GameTile tile = tileItem.getTile();
                            if (tile != null) {
                                String canPlace = tileItem.canPlace(level, pos.x * 32, pos.y * 32, player, null, selectedItem, null);
                                if (canPlace == null) {
                                    tile.drawPreview(level, pos.x, pos.y, 0.5f, player, camera);
                                } else {
                                    tile.drawPreview(level, pos.x, pos.y, 0.5f, player, camera);
                                }
                            }
                        } else if (item instanceof ObjectItem && (object = (objectItem = (ObjectItem)item).getObject()) != null) {
                            String canPlace = object.canPlace(level, 0, pos.x, pos.y, objectRotation, true, false);
                            if (canPlace == null) {
                                object.drawMultiTilePreview(level, pos.x, pos.y, objectRotation, 0.5f, player, camera);
                            } else {
                                object.getMultiTile(objectRotation).streamObjects(pos.x, pos.y).forEach(e -> {
                                    String error = ((GameObject)e.value).canPlace(level, 0, e.tileX, e.tileY, objectRotation, true, false);
                                    ((GameObject)e.value).drawFailedPreview(level, e.tileX, e.tileY, objectRotation, 0.5f, error, player, camera);
                                });
                            }
                        }
                    }
                    catch (Exception e2) {
                        System.err.println("MedievalSim: ERROR - Preview rendering failed: " + e2.getMessage());
                        e2.printStackTrace();
                    }
                }
            });
        }
        if (manager.selectedShape != 0) {
            int blockCount = BlockCountCalculator.calculateBlockCount(manager.selectedShape, manager.isHollow, manager.lineLength, manager.squareSize, manager.circleRadius, manager.spacing);
            String shapeName = manager.getShapeName(manager.selectedShape, manager.isHollow);
            final String tooltipText = Localization.translate((String)"ui", (String)"buildmodeblockcost", (String[])new String[]{"shape", shapeName, "count", String.valueOf(blockCount)});
            list.add(new SortedDrawable(){

                public int getPriority() {
                    return Integer.MAX_VALUE;
                }

                public void draw(TickManager tickManager) {
                    int mouseX = camera.getMouseLevelPosX();
                    int mouseY = camera.getMouseLevelPosY();
                    int screenX = camera.getDrawX(mouseX);
                    int screenY = camera.getDrawY(mouseY);
                    int tooltipX = screenX + 20;
                    int tooltipY = screenY + -10;
                    FontOptions fontOptions = new FontOptions(16).color(Color.WHITE);
                    int textWidth = FontManager.bit.getWidthCeil(tooltipText, fontOptions);
                    int textHeight = FontManager.bit.getHeightCeil(tooltipText, fontOptions);
                    Renderer.initQuadDraw((int)(textWidth + 8), (int)(textHeight + 8)).color(new Color(0, 0, 0, 180)).draw(tooltipX - 4, tooltipY - 4);
                    FontManager.bit.drawString((float)tooltipX, (float)tooltipY, tooltipText, fontOptions);
                }
            });
        }
    }
}

