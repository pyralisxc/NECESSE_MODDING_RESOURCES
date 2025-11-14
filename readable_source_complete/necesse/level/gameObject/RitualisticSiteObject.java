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
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class RitualisticSiteObject
extends FurnitureObject {
    protected String ritualTexturePath;
    protected String bullSkullTexturePath;
    public GameTexture ritualTexture;
    public GameTexture bullSkullTexture;
    protected int counterID;

    private RitualisticSiteObject(String ritualTexturePath, String bullSkullTexturePath, ToolType toolType, Color mapColor) {
        super(new Rectangle(8, 10, 24, 2));
        this.ritualTexturePath = ritualTexturePath;
        this.bullSkullTexturePath = bullSkullTexturePath;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 2, true, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.ritualTexture = GameTexture.fromFile("particles/" + this.ritualTexturePath);
        this.bullSkullTexture = GameTexture.fromFile("objects/" + this.bullSkullTexturePath);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        final TextureDrawOptionsEnd ritualTextureDrawOptions = this.ritualTexture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        final TextureDrawOptionsEnd bullSkullTextureDrawOptions = this.bullSkullTexture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 5;
            }

            @Override
            public void draw(TickManager tickManager) {
                ritualTextureDrawOptions.draw();
                bullSkullTextureDrawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.ritualTexture.initDraw().sprite(0, 0, 64).alpha(alpha).draw(drawX, drawY);
        this.bullSkullTexture.initDraw().sprite(0, 0, 64).alpha(alpha).draw(drawX, drawY);
    }

    public static int[] registerRitualisticSite(String stringID, String ritualTexturePath, String bullSkullTexturePath, ToolType toolType, Color mapColor, float brokerValue) {
        int id1;
        RitualisticSiteObject obj1 = new RitualisticSiteObject(ritualTexturePath, bullSkullTexturePath, toolType, mapColor);
        obj1.counterID = id1 = ObjectRegistry.registerObject(stringID, obj1, brokerValue, true);
        return new int[]{id1};
    }

    public static int[] registerRitualisticSite(String stringID, String ritualTexturePath, String bullSkullTexturePath, Color mapColor, float brokerValue) {
        return RitualisticSiteObject.registerRitualisticSite(stringID, ritualTexturePath, bullSkullTexturePath, ToolType.ALL, mapColor, brokerValue);
    }
}

