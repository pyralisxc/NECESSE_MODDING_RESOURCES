/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class RavenNestWithEggObject
extends StaticMultiObject {
    protected final GameRandom drawRandom;
    protected ObjectDamagedTextureArray eggTexture;

    public RavenNestWithEggObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "emptyravennest");
        this.displayMapTooltip = true;
        this.drawDamage = false;
        this.drawRandom = new GameRandom();
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.eggTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/ravennestegg");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture nestTexture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture eggTexture = this.eggTexture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite nestSprite = new GameSprite(nestTexture, 0, 0, 64);
        GameSprite eggSprite = new GameSprite(eggTexture, 0, 0, 64);
        final DrawOptions nestOptions = this.getMultiTextureDrawOptions(nestSprite, level, tileX, tileY, camera);
        final DrawOptions eggOptions = this.getMultiTextureDrawOptions(eggSprite, level, tileX, tileY, camera);
        tileList.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                nestOptions.draw();
            }
        });
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 18;
            }

            @Override
            public void draw(TickManager tickManager) {
                eggOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameSprite sprite = new GameSprite(texture, 0, 0, 64);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerRavenNestWithEgg() {
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(16, 16, 32, 32);
        ids[0] = ObjectRegistry.registerObject("ravennestwithegg", new RavenNestWithEggObject(0, 0, 2, 2, ids, collision), -1.0f, true);
        ids[1] = ObjectRegistry.registerObject("ravennestwithegg2", new RavenNestWithEggObject(1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("ravennestwithegg3", new RavenNestWithEggObject(0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("ravennestwithegg4", new RavenNestWithEggObject(1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

