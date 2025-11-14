/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.levelEvent.TicTacToeLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class TicTacToeGameObject
extends GameObject {
    protected GameTexture texture;
    protected int spriteX;
    protected int spriteY;
    protected int multiTileX;
    protected int multiTileY;
    protected int multiTileWidth;
    protected int multiTileHeight;
    protected int[] multiTileIds;
    protected boolean multiTileMaster;

    protected TicTacToeGameObject(int spriteX, int spriteY) {
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.toolType = ToolType.ALL;
        this.rarity = Item.Rarity.EPIC;
        this.isLightTransparent = true;
        this.hoverHitboxSortY = -16;
        this.replaceRotations = false;
        this.mapColor = new Color(94, 70, 70);
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        byte rotation = level.getObjectRotation(layerID, x, y);
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        this.getMultiTile(rotation).streamObjects(x, y).filter(o -> ((GameObject)o.value).getID() == level.getObjectID(layerID, o.tileX, o.tileY)).forEach(o -> level.entityManager.doObjectDamageOverride(layerID, o.tileX, o.tileY, ((GameObject)o.value).objectHealth));
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/tictactoe");
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "tictactoeboard");
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(this.multiTileX, this.multiTileY, this.multiTileWidth, this.multiTileHeight, this.multiTileMaster, this.multiTileIds);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        TextureDrawOptionsEnd drawOptions = this.texture.initDraw().sprite(this.spriteX, this.spriteY, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> drawOptions.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(this.spriteX, this.spriteY, 32).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public void drawFailedPreview(Level level, int tileX, int tileY, int rotation, float alpha, String error, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Color color = error == null ? Color.WHITE : Color.RED;
        this.texture.initDraw().sprite(this.spriteX, this.spriteY, 32).color(color).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "interacttip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        if (this.multiTileMaster && level.objectLayer.isPlayerPlaced(x, y)) {
            return level.entityManager.events.regionList.getInRegionTileByTile(x, y).stream().filter(event -> event instanceof TicTacToeLevelEvent).map(event -> (TicTacToeLevelEvent)event).noneMatch(event -> event.tileX == x && event.tileY == y);
        }
        return false;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            if (level.entityManager.events.regionList.getInRegionTileByTile(x, y).stream().filter(event -> event instanceof TicTacToeLevelEvent).map(event -> (TicTacToeLevelEvent)event).anyMatch(event -> event.tileX == x && event.tileY == y)) {
                return;
            }
            level.entityManager.events.add(new TicTacToeLevelEvent(x, y, null, null, (winner, winnerTiles, xPlayer, oPlayer) -> {
                if (winner == TicTacToeLevelEvent.TileState.X) {
                    if (oPlayer != null) {
                        GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, (float)oPlayer.getMaxHealth() / 6.0f);
                        TicTacToeLevelEvent.spawnPunishProjectiles(winnerTiles, oPlayer, damage, true);
                    }
                } else if (winner == TicTacToeLevelEvent.TileState.O && xPlayer != null) {
                    GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, (float)xPlayer.getMaxHealth() / 6.0f);
                    TicTacToeLevelEvent.spawnPunishProjectiles(winnerTiles, xPlayer, damage, false);
                }
            }));
        }
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "tictactoeboardtip"), 400);
        return tooltips;
    }

    public static void registerTicTacToeObjects() {
        int totalWidth = 11;
        int totalHeight = 11;
        int[] multiTileIds = new int[totalWidth * totalHeight];
        Arrays.fill(multiTileIds, Integer.MIN_VALUE);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 0, 0, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 4, 0, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 8, 0, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 0, 4, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 4, 4, true);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 8, 4, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 0, 8, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 4, 8, false);
        TicTacToeGameObject.registerSection(totalWidth, totalHeight, multiTileIds, 8, 8, false);
    }

    protected static void registerSection(int totalWidth, int totalHeight, int[] multiTileIds, int startX, int startY, boolean isCenterMaster) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int objectID;
                int index = startX + x + (startY + y) * totalWidth;
                TicTacToeGameObject object = new TicTacToeGameObject(x, y);
                object.multiTileX = startX + x;
                object.multiTileY = startY + y;
                object.multiTileWidth = totalWidth;
                object.multiTileHeight = totalHeight;
                object.multiTileIds = multiTileIds;
                object.multiTileMaster = isCenterMaster && x == 1 && y == 1;
                multiTileIds[index] = objectID = ObjectRegistry.registerObject("tictactoeboard" + (object.multiTileMaster ? "" : Integer.valueOf(index)), object, 100.0f, object.multiTileMaster);
            }
        }
    }
}

