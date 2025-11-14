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
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
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

public class RavenEffigyObject
extends StaticMultiObject {
    protected RavenEffigyObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 1;
        this.rarity = Item.Rarity.UNCOMMON;
        this.mapColor = new Color(143, 143, 143);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        GameRandom globalRandom = GameRandom.globalRandom;
        if (globalRandom.getChance(0.2f)) {
            int startHeight = 32 + globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + globalRandom.getIntBetween(4, 24), tileY * 32 + 8 + globalRandom.getIntBetween(0, 8), Particle.GType.COSMETIC).sprite(GameResources.magicSparkParticles.sprite(2, 0, 22)).heightMoves(startHeight, startHeight + 42).sizeFades(14, 14).givesLight(255.0f, 30.0f).givesLight(150).movesConstant(globalRandom.getIntBetween(-3, 3), 0.0f).color(new Color(207, 178, 250)).lifeTime(3000);
        }
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/statues/" + this.texturePath);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite sprite = new GameSprite(texture, 0, 0, 224, 112, 224, 112);
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

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameSprite sprite = new GameSprite(texture, 0, 0, 224, 112, 224, 112);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerRavenEffigy(String texturePath, boolean isObtainable) {
        int[] ids = new int[2];
        Rectangle collision = new Rectangle(0, 0, 64, 32);
        ids[0] = ObjectRegistry.registerObject(texturePath, new RavenEffigyObject(texturePath, 0, 0, 2, 1, ids, collision), 0.0f, isObtainable);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new RavenEffigyObject(texturePath, 1, 0, 2, 1, ids, collision), 0.0f, false);
        return ids;
    }
}

