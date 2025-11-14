/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.LevelTileLiquidDrawOptions
 *  necesse.gfx.drawables.LevelTileTerrainDrawOptions
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTexture.GameTextureSection
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.placeableItem.tileItem.TileItem
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameTile.EdgedTiledTexture
 *  necesse.level.gameTile.GameTile
 *  necesse.level.maps.Level
 */
package aphorea.tiles;

import aphorea.items.vanillaitemtypes.AphTileItem;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.EdgedTiledTexture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class GelTile
extends EdgedTiledTexture {
    protected GameTextureSection texture;

    public GelTile(String textureName, Color mapColor) {
        super(true, textureName);
        this.mapColor = mapColor;
        this.canBeMined = true;
        this.tilesHeight = 2;
    }

    protected void loadTextures() {
        super.loadTextures();
        this.texture = tileTextures.addTexture(GameTexture.fromFile((String)("tiles/" + this.textureName)));
    }

    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        super.addDrawables(underLiquidList, liquidList, overLiquidList, objectTileList, sortedList, level, tileX, tileY, camera, tickManager);
        if (level.isShore(tileX, tileY + 1) && !level.getTile((int)tileX, (int)(tileY + 1)).isFloor) {
            this.addBridgeDrawables(overLiquidList, sortedList, level, tileX, tileY + 1, camera, tickManager);
        }
    }

    public void addBridgeDrawables(LevelTileTerrainDrawOptions sharedList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        if (tileX != 0 && level.isLiquidTile(tileX - 1, tileY) && level.getTileID(tileX - 1, tileY - 1) == this.getID()) {
            sharedList.add(this.texture.sprite(6, 2, 16, 32)).pos(drawX, drawY);
        } else {
            sharedList.add(this.texture.sprite(4, 2, 16, 32)).pos(drawX, drawY);
        }
        if (tileX != level.tileWidth - 1 && level.isLiquidTile(tileX + 1, tileY) && level.getTileID(tileX + 1, tileY - 1) == this.getID()) {
            sharedList.add(this.texture.sprite(5, 2, 16, 32)).pos(drawX + 16, drawY);
        } else {
            sharedList.add(this.texture.sprite(7, 2, 16, 32)).pos(drawX + 16, drawY);
        }
    }

    protected boolean isMergeTile(Level level, int tileX, int tileY) {
        if (super.isMergeTile(level, tileX, tileY)) {
            return true;
        }
        GameObject object = level.getObject(tileX, tileY);
        return object.isWall && object.isDoor;
    }

    public ModifierValue<Float> getSpeedModifier(Mob mob) {
        if (mob.isPlayer && mob.buffManager.hasBuff("unstableperiapt")) {
            return super.getSpeedModifier(mob);
        }
        return mob.isFlying() ? super.getSpeedModifier(mob) : new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(-0.5f));
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"geltile"));
        return tooltips;
    }

    public TileItem generateNewTileItem() {
        return new AphTileItem((GameTile)this);
    }
}

