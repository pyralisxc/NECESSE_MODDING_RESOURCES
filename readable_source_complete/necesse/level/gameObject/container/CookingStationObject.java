/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.container.CookingStation2Object;
import necesse.level.gameObject.container.FueledCraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class CookingStationObject
extends FueledCraftingStationObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected CookingStationObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(51, 53, 56);
        this.rarity = Item.Rarity.UNCOMMON;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
    }

    @Override
    public int getCraftingCategoryDepth() {
        return 2;
    }

    @Override
    public int getCraftingFormWidth() {
        return 418;
    }

    @Override
    public boolean allowHighlightOption() {
        return false;
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        if (fueledObjectEntity != null && fueledObjectEntity.isFueled()) {
            return 100;
        }
        return 0;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        return super.getPlaceOptions(level, levelX, levelY, playerMob, Math.floorMod(playerDir - 1, 4), offsetMultiTile);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/cookingstation");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
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
            options.add(texture.initDraw().sprite(0, 2, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(0, 8, 32).light(light).pos(drawX, drawY - 32 + 14));
            }
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(0, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 6, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(0, 7, 32).light(light).pos(drawX, drawY - 14));
            }
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(1, 8, 32).light(light).pos(drawX, drawY - 14));
            }
        } else {
            options.add(texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 4, 32).light(light).pos(drawX, drawY));
            if (isActive) {
                options.add(texture.initDraw().sprite(1, 7, 32).light(light).pos(drawX, drawY - 14));
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
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY - 64);
            texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            texture.initDraw().sprite(0, 5, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(0, 6, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 5, 32).alpha(alpha).draw(drawX + 32, drawY - 32);
            texture.initDraw().sprite(1, 6, 32).alpha(alpha).draw(drawX + 32, drawY);
        } else if (rotation == 2) {
            texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 4, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY - 32);
            texture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX - 32, drawY);
        }
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.COOKING_STATION, RecipeTechRegistry.COOKING_POT, RecipeTechRegistry.ROASTING_STATION};
    }

    public static int[] registerCookingStation() {
        int i2;
        CookingStationObject o1 = new CookingStationObject();
        CookingStation2Object o2 = new CookingStation2Object();
        int i1 = ObjectRegistry.registerObject("cookingstation", o1, 140.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("cookingstation2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }
}

