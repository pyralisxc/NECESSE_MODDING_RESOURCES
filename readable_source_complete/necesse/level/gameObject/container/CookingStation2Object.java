/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.FueledCraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

class CookingStation2Object
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected CookingStation2Object() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(51, 53, 56);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.lightLevel = 100;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        return this.getMultiTile(level.getObjectRotation(tileX, tileY)).getMasterLevelObject(level, layerID, tileX, tileY).map(lo -> {
            FueledInventoryObjectEntity fueledObjectEntity = ((FueledCraftingStationObject)lo.object).getFueledObjectEntity(lo.level, lo.tileX, lo.tileY);
            if (fueledObjectEntity != null && fueledObjectEntity.isFueled()) {
                return 100;
            }
            return 0;
        }).orElseGet(() -> super.getLightLevel(level, layerID, tileX, tileY));
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/cookingstation");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean isActive = this.getMultiTile(rotation).getMasterLevelObject(level, 0, tileX, tileY).map(lo -> {
            FueledInventoryObjectEntity fueledObjectEntity = ((FueledCraftingStationObject)lo.object).getFueledObjectEntity(lo.level, lo.tileX, lo.tileY);
            return fueledObjectEntity != null && fueledObjectEntity.isFueled();
        }).orElse(false);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(0, 8, 32).light(light).pos(drawX, drawY - 14));
            }
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(1, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 6, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(1, 7, 32).light(light).pos(drawX, drawY - 14));
            }
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(1, 8, 32).light(light).pos(drawX, drawY - 32 + 14));
            }
        } else {
            options.add(texture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 4, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(0, 7, 32).light(light).pos(drawX, drawY - 14));
            }
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).map(e -> e.getInteractTip(perspective, debug)).orElseGet(() -> super.getInteractTip(level, x, y, perspective, debug));
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).map(e -> e.canInteract(player)).orElseGet(() -> super.canInteract(level, x, y, player));
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
    }

    @Override
    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }
}

