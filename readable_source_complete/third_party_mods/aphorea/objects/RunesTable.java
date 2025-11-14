/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.recipe.Tech
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.ObjectDamagedTextureArray
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.objects;

import aphorea.objects.AphCraftingStationObject;
import aphorea.registry.AphTech;
import aphorea.utils.AphColors;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RunesTable
extends AphCraftingStationObject {
    public ObjectDamagedTextureArray texture;

    public RunesTable() {
        super(new Rectangle(32, 32));
        this.mapColor = AphColors.wood;
        this.isLightTransparent = true;
    }

    public int getCraftingCategoryDepth() {
        return 2;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, (String)"objects/runestable");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 8, y * 32 + 8, 16, 20) : new Rectangle(x * 32 + 5, y * 32 + 14, 22, 16);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture((GameObject)this, level, tileX, tileY);
        TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - (texture.getHeight() - 32));
        list.add(new LevelSortedDrawable((GameObject)this, tileX, tileY, (TextureDrawOptions)options){
            final /* synthetic */ TextureDrawOptions val$options;
            {
                this.val$options = textureDrawOptions;
                super(arg0, arg1, arg2);
            }

            public int getSortY() {
                return 20;
            }

            public void draw(TickManager tickManager) {
                this.val$options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - (texture.getHeight() - 32));
    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{AphTech.RUNES};
    }
}

