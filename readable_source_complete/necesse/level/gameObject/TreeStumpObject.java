/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TreeStumpObject
extends ChairObject {
    public String logStringID;
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected ObjectDamagedTextureArray textureRoots;
    protected final GameRandom drawRandom;

    public TreeStumpObject(String textureName, String logStringID, Color mapColor) {
        super(textureName, ToolType.AXE, mapColor, new String[0]);
        this.textureName = textureName;
        this.logStringID = logStringID;
        this.mapColor = mapColor;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.displayMapTooltip = true;
        this.isTree = true;
        this.drawDamage = false;
        this.drawRandom = new GameRandom();
        this.replaceCategories.add("tree");
        this.replaceRotations = false;
        this.setItemCategory("objects", "landscaping", "plants");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        this.textureRoots = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName + "roots");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        LootTable lootTable = new LootTable();
        if (this.logStringID != null) {
            lootTable.items.add(LootItem.between(this.logStringID, 4, 5).splitItems(5));
        }
        return lootTable;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "treeSetup", () -> {
            boolean mirror;
            List<ObjectUserMob> users = this.getObjectUsers(level, tileX, tileY);
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int spriteRes = 64;
            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            GameTexture rootsTexture = this.textureRoots.getDamagedTexture(this, level, tileX, tileY);
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                this.drawRandom.setSeed(TreeStumpObject.getTileSeed(tileX, tileY));
                mirror = this.drawRandom.nextBoolean();
            }
            final DrawOptionsList optionsList = new DrawOptionsList();
            TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 0, spriteRes).light(light).mirror(mirror, false).pos(drawX - 16, drawY - 32);
            final TextureDrawOptionsEnd rootsOptions = rootsTexture.initDraw().sprite(0, 0, spriteRes).light(light).mirror(mirror, false).pos(drawX - 16, drawY - 32);
            optionsList.add(options);
            for (ObjectUserMob user : users) {
                Point offset = this.getMobPosSitOffset(level, tileX, tileY);
                optionsList.add(user.getUserDrawOptions(level, tileX * 32 + offset.x, tileY * 32 + offset.y, tickManager, camera, perspective, humanOptions -> {
                    if (humanOptions != null) {
                        this.modifyHumanDrawOptions(level, tileX, tileY, (HumanDrawOptions)humanOptions);
                    }
                }));
            }
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return -32;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "treeDraw", rootsOptions::draw);
                }
            });
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 12;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "treeDraw", optionsList::draw);
                }
            });
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteRes = 64;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameTexture rootsTexture = this.textureRoots.getDamagedTexture(0.0f);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(TreeStumpObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        rootsTexture.initDraw().sprite(0, 0, spriteRes).alpha(alpha).light(light).mirror(mirror, false).draw(drawX - 16, drawY - 32);
        texture.initDraw().sprite(0, 0, spriteRes).alpha(alpha).light(light).mirror(mirror, false).draw(drawX - 16, drawY - 32);
    }

    @Override
    public void modifyHumanDrawOptions(Level level, int tileX, int tileY, HumanDrawOptions options) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        options.dir(rotation).sprite(6, (int)rotation);
    }

    @Override
    public boolean onDamaged(Level level, int layerID, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        boolean out = super.onDamaged(level, layerID, x, y, damage, attacker, client, showEffect, mouseX, mouseY);
        if (showEffect) {
            level.makeGrassWeave(x, y, 250, true);
        }
        return out;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        GameObject[] adj;
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        for (GameObject obj : adj = level.getAdjacentObjects(x, y)) {
            if (!obj.isTree) continue;
            return "treenear";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (super.isValid(level, layerID, x, y)) {
            GameObject[] adj;
            for (GameObject obj : adj = level.getAdjacentObjects(x, y)) {
                if (!obj.isTree) continue;
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, -2);
            }
            case 1: {
                return new Point(22, 2);
            }
            case 2: {
                return new Point(16, 4);
            }
            case 3: {
                return new Point(10, 2);
            }
        }
        return new Point(16, 0);
    }
}

