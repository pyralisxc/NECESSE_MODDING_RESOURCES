/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.WallObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WallWindowObject
extends WallObject {
    public WallObject wallObject;

    public WallWindowObject(WallObject wallObject) {
        super(wallObject.textureName, wallObject.outlineTextureName, wallObject.mapColor, wallObject.toolTier, wallObject.toolType);
        this.wallObject = wallObject;
        this.connectedWalls = wallObject.connectedWalls;
        this.canReplaceCategories.clear();
        this.isLightTransparent = true;
    }

    @Override
    public void onObjectRegistryClosed() {
        super.onObjectRegistryClosed();
        this.wallObject.connectedWalls.add(this.getID());
        this.wallObject.windowID = this.getID();
    }

    @Override
    public GameMessage getNewLocalization() {
        return new GameMessageBuilder().append("object", "wallwindow").append(" (").append(this.wallObject.getLocalization()).append(")");
    }

    @Override
    public boolean allowsAmbientLightPassThrough(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable(new LootItem("wallwindow").preventLootMultiplier());
    }

    @Override
    public void loadTextures() {
        this.wallTexture = this.wallObject.wallTexture;
        this.outlineTexture = this.wallObject.outlineTexture;
    }

    public int getWindowDir(Level level, int tileX, int tileY) {
        return this.getWindowDir(this.isConnectedWall(level.getObject(tileX, tileY - 1)), this.isConnectedWall(level.getObject(tileX + 1, tileY)), this.isConnectedWall(level.getObject(tileX, tileY + 1)), this.isConnectedWall(level.getObject(tileX - 1, tileY)));
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        level.objectLayer.setObject(layerID, x, y, this.wallObject.getID());
    }

    public int getWindowDir(boolean connectedWallUp, boolean connectedWallRight, boolean connectedWallBot, boolean connectedWallLeft) {
        if (connectedWallUp && connectedWallBot) {
            if (connectedWallLeft || connectedWallRight) {
                return -1;
            }
            return 1;
        }
        if (connectedWallLeft && connectedWallRight) {
            if (connectedWallUp || connectedWallBot) {
                return -1;
            }
            return 0;
        }
        return -1;
    }

    @Override
    public void addWallDrawOptions(SharedTextureDrawOptions options, GameTextureSection wallTexture, int drawX, int drawY, GameLight[] lights, float alpha, boolean[] sameWall, boolean allIsSameWall, boolean forceDrawTop, boolean forceRemoveBot) {
        int windowDir = this.getWindowDir(sameWall[1], sameWall[4], sameWall[6], sameWall[3]);
        if (windowDir == 1) {
            this.applyLights(options.add(wallTexture.sprite(4, 0, 16)), lights, 1.0f, 0, -1).pos(drawX, drawY - 16);
            this.applyLights(options.add(wallTexture.sprite(5, 0, 16)), lights, 1.0f, 1, -1).pos(drawX + 16, drawY - 16);
            this.applyLights(options.add(wallTexture.sprite(4, 1, 16)), lights, 1.0f, 0, 0).pos(drawX, drawY);
            this.applyLights(options.add(wallTexture.sprite(5, 1, 16)), lights, 1.0f, 1, 0).pos(drawX + 16, drawY);
        } else {
            this.applyLights(options.add(wallTexture.sprite(4, 2, 16)), lights, alpha, 0, -1).pos(drawX, drawY - 64);
            this.applyLights(options.add(wallTexture.sprite(5, 2, 16)), lights, alpha, 1, -1).pos(drawX + 16, drawY - 64);
            this.applyLights(options.add(wallTexture.sprite(4, 3, 16)), lights, alpha, 0, -1).pos(drawX, drawY - 48);
            this.applyLights(options.add(wallTexture.sprite(5, 3, 16)), lights, alpha, 1, -1).pos(drawX + 16, drawY - 48);
            this.applyLights(options.add(wallTexture.sprite(4, 4, 16)), lights, alpha, 0, -1).pos(drawX, drawY - 32);
            this.applyLights(options.add(wallTexture.sprite(5, 4, 16)), lights, alpha, 1, -1).pos(drawX + 16, drawY - 32);
            this.applyLights(options.add(wallTexture.sprite(4, 5, 16)), lights, alpha, 0, -1).pos(drawX, drawY - 16);
            this.applyLights(options.add(wallTexture.sprite(5, 5, 16)), lights, alpha, 1, -1).pos(drawX + 16, drawY - 16);
            this.applyLights(options.add(wallTexture.sprite(4, 6, 16)), lights, 1.0f, 0, 0).pos(drawX, drawY);
            this.applyLights(options.add(wallTexture.sprite(5, 6, 16)), lights, 1.0f, 1, 0).pos(drawX + 16, drawY);
            this.applyLights(options.add(wallTexture.sprite(4, 7, 16)), lights, 1.0f, 0, 1).pos(drawX, drawY + 16);
            this.applyLights(options.add(wallTexture.sprite(5, 7, 16)), lights, 1.0f, 1, 1).pos(drawX + 16, drawY + 16);
        }
    }

    @Override
    public boolean drawsFullTile() {
        return false;
    }

    @Override
    public boolean isWallDrawingTop() {
        return true;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        int windowDir;
        boolean valid = super.isValid(level, layerID, x, y);
        if (valid && (windowDir = this.getWindowDir(level, x, y)) == -1) {
            return false;
        }
        return valid;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        GameObject currentObject = level.getObject(x, y);
        if (currentObject == this.wallObject) {
            int windowDir = this.getWindowDir(level, x, y);
            if (windowDir == -1) {
                return "nosupport";
            }
            return null;
        }
        return "nowall";
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "windowplacetip"));
        return tooltips;
    }
}

