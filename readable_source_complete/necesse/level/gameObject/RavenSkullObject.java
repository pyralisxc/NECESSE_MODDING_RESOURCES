/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class RavenSkullObject
extends StaticMultiObject {
    protected RavenSkullObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "ravenskull");
        this.mapColor = new Color(115, 80, 138);
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite sprite = new GameSprite(texture, 0, 0, 64);
        final DrawOptions options = this.getMultiTextureDrawOptions(sprite, level, tileX, tileY, camera);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 8;
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
        GameSprite sprite = new GameSprite(texture, 0, 0, 64);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerRavenSkull() {
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(20, 10, 24, 24);
        ids[0] = ObjectRegistry.registerObject("ravenskull", (GameObject)new RavenSkullObject(0, 0, 2, 2, ids, collision), 0.0f, true, true, true, new String[0]);
        ids[1] = ObjectRegistry.registerObject("ravenskull2", new RavenSkullObject(1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("ravenskull3", new RavenSkullObject(0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("ravenskull4", new RavenSkullObject(1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

