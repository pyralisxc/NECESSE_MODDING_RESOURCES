/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class AncientMummyStatueObject
extends StaticMultiObject {
    protected final GameRandom drawRandom;
    protected boolean showAnimation;

    protected AncientMummyStatueObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 1;
        this.rarity = Item.Rarity.UNCOMMON;
        this.mapColor = new Color(143, 143, 143);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory("objects", "landscaping", "misc");
        this.drawRandom = new GameRandom();
        this.lightLevel = 150;
        this.lightHue = 10.0f;
        this.lightSat = 0.75f;
        this.showAnimation = true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/statues/" + this.texturePath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteWidth = 64;
        int frame = 0;
        if (this.showAnimation) {
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                long tileSeed = AncientMummyStatueObject.getTileSeed(tileX - this.multiX, tileY - this.multiY);
                frame = GameUtils.getAnim((long)this.drawRandom.seeded(tileSeed).nextInt(800) + level.getWorldEntity().getWorldTime(), 8, 800) + 1;
            }
        }
        GameSprite sprite = new GameSprite(texture, frame, 0, spriteWidth, 128, spriteWidth, 128);
        final DrawOptions options = this.getMultiTextureDrawOptions(sprite, level, tileX, tileY, camera);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteWidth = 64;
        int frame = 0;
        if (this.showAnimation) {
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                long tileSeed = AncientMummyStatueObject.getTileSeed(tileX - this.multiX, tileY - this.multiY);
                frame = GameUtils.getAnim((long)this.drawRandom.seeded(tileSeed).nextInt(800) + level.getWorldEntity().getWorldTime(), 8, 800) + 1;
            }
        }
        GameSprite sprite = new GameSprite(texture, frame, 0, spriteWidth, 128, spriteWidth, 128);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerAncientMummyStatue(String texturePath, boolean isObtainable) {
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(0, 0, 64, 64);
        ids[0] = ObjectRegistry.registerObject(texturePath, new AncientMummyStatueObject(texturePath, 0, 0, 2, 2, ids, collision), 0.0f, isObtainable);
        ids[1] = ObjectRegistry.registerObject(texturePath + "1", new AncientMummyStatueObject(texturePath, 1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject(texturePath + "2", new AncientMummyStatueObject(texturePath, 0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject(texturePath + "3", new AncientMummyStatueObject(texturePath, 1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

