/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.FenceObject;
import necesse.level.gameObject.FenceObjectInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionType;

public class FenceGateObject
extends DoorObject
implements FenceObjectInterface {
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected int collisionWidth;
    protected int collisionHeight;
    protected LinkedList<Integer> connections = new LinkedList();

    protected FenceGateObject(int counterID, boolean isOpen, String textureName, Color mapColor, int collisionWidth, int collisionHeight) {
        super(new Rectangle(32, 32), counterID, isOpen);
        this.setItemCategory("objects", "fencesandgates");
        this.setCraftingCategory("objects", "fencesandgates");
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.collisionWidth = collisionWidth;
        this.collisionHeight = collisionHeight;
        this.isFence = true;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.canPlaceOnShore = true;
        this.regionType = RegionType.FENCE_GATE;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
        this.replaceCategories.add("fencegate");
        this.canReplaceCategories.add("fencegate");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("door");
        this.replaceRotations = false;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public boolean attachesToObject(GameObject me, Level level, int tileX, int tileY, LevelObject other) {
        return other.object.isWall || other.object.isRock || other.object.getID() == me.getID() || this.connections.contains(other.object.getID());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        LevelObject topObject = level.getLevelObject(tileX, tileY - 1);
        LevelObject botObject = level.getLevelObject(tileX, tileY + 1);
        boolean attachesTop = this.attachesToObject(this, level, tileX, tileY, topObject);
        boolean attachesBot = this.attachesToObject(this, level, tileX, tileY, botObject);
        if (attachesTop || attachesBot) {
            final SharedTextureDrawOptions botDraws = new SharedTextureDrawOptions(texture);
            final SharedTextureDrawOptions topDraws = new SharedTextureDrawOptions(texture);
            botDraws.addSprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY + 14);
            if (attachesBot) {
                botDraws.addSprite(3, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY + 14);
            }
            topDraws.addSprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - 14);
            topDraws.addSprite(4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - 14);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 26;
                }

                @Override
                public void draw(TickManager tickManager) {
                    botDraws.draw();
                }
            });
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 6;
                }

                @Override
                public void draw(TickManager tickManager) {
                    topDraws.draw();
                }
            });
        } else {
            final TextureDrawOptionsEnd draw = texture.initDraw().sprite(1, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 14;
                }

                @Override
                public void draw(TickManager tickManager) {
                    draw.draw();
                }
            });
        }
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        LevelObject topObject = level.getLevelObject(tileX, tileY - 1);
        LevelObject botObject = level.getLevelObject(tileX, tileY + 1);
        boolean attachesTop = this.attachesToObject(this, level, tileX, tileY, topObject);
        boolean attachesBot = this.attachesToObject(this, level, tileX, tileY, botObject);
        if (attachesTop || attachesBot) {
            texture.initDraw().sprite(2, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 14);
            texture.initDraw().sprite(4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 14);
            texture.initDraw().sprite(2, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY + 14);
            if (attachesBot) {
                texture.initDraw().sprite(3, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY + 14);
            }
        } else {
            texture.initDraw().sprite(1, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        LevelObject topObject = level.getLevelObject(x, y - 1);
        LevelObject botObject = level.getLevelObject(x, y + 1);
        boolean attachesTop = this.attachesToObject(this, level, x, y, topObject);
        boolean attachesBot = this.attachesToObject(this, level, x, y, botObject);
        if (attachesTop || attachesBot) {
            return new Rectangle(x * 32 + (32 - this.collisionWidth) / 2, y * 32, this.collisionWidth, 32);
        }
        return new Rectangle(x * 32, y * 32 + (32 - this.collisionHeight) / 2, 32, this.collisionHeight);
    }

    @Override
    public void playSwitchSound(Level level, int x, int y) {
        if (level.isClient()) {
            SoundManager.playSound(this.isSwitched ? GameResources.doorclose : GameResources.dooropen, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
        }
    }

    public static int[] registerGatePair(int fenceID, String stringIDPrefix, String textureName, Color mapColor, int collisionWidth, int collisionHeight, float brokerValue) {
        int openID;
        FenceGateObject closed = new FenceGateObject(0, false, textureName, mapColor, collisionWidth, collisionHeight);
        int closedID = ObjectRegistry.registerObject(stringIDPrefix, closed, brokerValue, true);
        FenceGateOpenObject open = new FenceGateOpenObject(closedID, true, textureName, mapColor, collisionWidth, collisionHeight);
        closed.counterID = openID = ObjectRegistry.registerObject(stringIDPrefix + "open", open, 0.0f, false);
        closed.connections.add(openID);
        closed.connections.add(fenceID);
        open.connections.add(closedID);
        open.connections.add(fenceID);
        GameObject fenceObject = ObjectRegistry.getObject(fenceID);
        if (fenceObject instanceof FenceObject) {
            ((FenceObject)fenceObject).connections.add(closedID);
            ((FenceObject)fenceObject).connections.add(openID);
        }
        return new int[]{closedID, openID};
    }

    private static class FenceGateOpenObject
    extends FenceGateObject {
        protected FenceGateOpenObject(int counterID, boolean isOpen, String textureName, Color mapColor, int collisionWidth, int collisionHeight) {
            super(counterID, isOpen, textureName, mapColor, collisionWidth, collisionHeight);
        }

        @Override
        public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
            return ObjectRegistry.getObject(this.counterID).getLootTable(level, layerID, tileX, tileY);
        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
            LevelObject topObject = level.getLevelObject(tileX, tileY - 1);
            LevelObject botObject = level.getLevelObject(tileX, tileY + 1);
            boolean attachesTop = this.attachesToObject(this, level, tileX, tileY, topObject);
            boolean attachesBot = this.attachesToObject(this, level, tileX, tileY, botObject);
            if (attachesTop || attachesBot) {
                final ArrayList<TextureDrawOptionsEnd> botDraws = new ArrayList<TextureDrawOptionsEnd>();
                final ArrayList<TextureDrawOptionsEnd> topDraws = new ArrayList<TextureDrawOptionsEnd>();
                botDraws.add(texture.initDraw().sprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY + 14));
                if (attachesBot) {
                    botDraws.add(texture.initDraw().sprite(3, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY + 14));
                }
                topDraws.add(texture.initDraw().sprite(2, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - 14));
                botDraws.add(texture.initDraw().sprite(5, 0, 32, texture.getHeight()).light(light).pos(drawX - 16, drawY + 14));
                list.add(new LevelSortedDrawable(this, tileX, tileY){

                    @Override
                    public int getSortY() {
                        return 26;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        botDraws.forEach(TextureDrawOptions::draw);
                    }
                });
                list.add(new LevelSortedDrawable(this, tileX, tileY){

                    @Override
                    public int getSortY() {
                        return 6;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        topDraws.forEach(TextureDrawOptions::draw);
                    }
                });
            } else {
                final ArrayList<TextureDrawOptionsEnd> draws = new ArrayList<TextureDrawOptionsEnd>();
                draws.add(texture.initDraw().sprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY));
                list.add(new LevelSortedDrawable(this, tileX, tileY){

                    @Override
                    public int getSortY() {
                        return 14;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        draws.forEach(TextureDrawOptions::draw);
                    }
                });
            }
        }

        @Override
        public Rectangle getCollision(Level level, int x, int y, int rotation) {
            return new Rectangle();
        }

        @Override
        public boolean shouldSnapSmartMining(Level level, int x, int y) {
            return true;
        }

        @Override
        public boolean isSolid(Level level, int x, int y) {
            return false;
        }
    }
}

