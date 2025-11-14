/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.WallObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WallWindowPlaceObject
extends GameObject {
    public WallWindowPlaceObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory("objects", "wallsanddoors");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        TextureDrawOptionsEnd options = GameResources.error.initDraw().light(light).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameResources.error.initDraw().light(light).draw(drawX, drawY);
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        int windowID;
        int tileY;
        Point offset = offsetMultiTile ? this.getPlaceOffset(playerDir) : null;
        int tileX = GameMath.getTileCoordinate(levelX + (offset == null ? 0 : offset.x));
        GameObject object = level.getObject(tileX, tileY = GameMath.getTileCoordinate(levelY + (offset == null ? 0 : offset.y)));
        if (object instanceof WallObject && (windowID = ((WallObject)object).windowID) != -1) {
            return ObjectRegistry.getObject(windowID).getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
        }
        return new ArrayList<ObjectPlaceOption>();
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        return "notplacable";
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return false;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "windowplacetip"));
        return tooltips;
    }
}

