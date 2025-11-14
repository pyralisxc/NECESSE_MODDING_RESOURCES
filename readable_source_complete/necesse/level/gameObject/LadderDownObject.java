/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.LadderDownObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.LadderUpObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;
import necesse.level.maps.light.GameLight;

public class LadderDownObject
extends GameObject {
    public GameTexture texture;
    public int ladderUpObjectID = -1;
    public final String textureName;
    public final LevelIdentifier destinationLevelIdentifier;
    public String localizationKey;

    protected LadderDownObject(String textureName, String localizationKey, LevelIdentifier destinationLevelIdentifier, Color mapColor, Item.Rarity rarity) {
        this.textureName = textureName;
        this.localizationKey = localizationKey;
        this.destinationLevelIdentifier = destinationLevelIdentifier;
        this.mapColor = mapColor;
        this.rarity = rarity;
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public GameMessage getNewLocalization() {
        if (this.localizationKey != null) {
            return new LocalMessage("object", this.localizationKey);
        }
        return super.getNewLocalization();
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        ArrayList<ObjectPlaceOption> placeOptions = super.getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
        if (this.ladderUpObjectID != -1) {
            placeOptions.addAll(ObjectRegistry.getObject(this.ladderUpObjectID).getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile));
        }
        return placeOptions;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName + "down");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - this.texture.getWidth() / 2 + 16;
        int tileDrawY = camera.getTileDrawY(tileY);
        int drawY = tileDrawY - (this.texture.getHeight() - 32) + 32;
        TextureDrawOptionsEnd tileOptions = this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, tileDrawY);
        final TextureDrawOptionsEnd objOptions = this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).light(light).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
            }

            @Override
            public void draw(TickManager tickManager) {
                objOptions.draw();
            }
        });
        tileList.add(tm -> tileOptions.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX) - this.texture.getWidth() / 2 + 16;
        int tileDrawY = camera.getTileDrawY(tileY);
        int drawY = tileDrawY - (this.texture.getHeight() - 32) + 32;
        this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, tileDrawY);
        this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return "notsurface";
        }
        return null;
    }

    @Override
    public Item generateNewObjectItem() {
        return new ObjectItem(this, false);
    }

    @Override
    public void attemptPlace(Level level, int x, int y, PlayerMob player, String error) {
        if (level.isClient() && error.equals("notsurface")) {
            player.getLevel().hudManager.addElement(new ChatBubbleText(player, Localization.translate("misc", "laddernotsurface")));
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        ObjectEntity objectEntity;
        if (level.isServer() && player.isServerClient() && (objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof PortalObjectEntity) {
            ServerClient client = player.getServerClient();
            ((PortalObjectEntity)objectEntity).use(level.getServer(), client);
            if (client.achievementsLoaded()) {
                client.achievements().SPELUNKER.markCompleted(client);
            }
        }
        super.interact(level, x, y, player);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new LadderDownObjectEntity(this.textureName + "down", level, x, y, this.destinationLevelIdentifier, this.getID(), this.ladderUpObjectID);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        PortalObjectEntity portal;
        ObjectEntity objectEntity;
        if (level.isServer() && (objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof PortalObjectEntity && level.getServer().world.levelExists((portal = (PortalObjectEntity)objectEntity).getDestinationIdentifier())) {
            Level nextLevel = level.getServer().world.getLevel(portal.getDestinationIdentifier());
            nextLevel.regionManager.ensureTileIsLoaded(portal.destinationTileX, portal.destinationTileY);
            if (nextLevel.getObjectID(portal.destinationTileX, portal.destinationTileY) == this.ladderUpObjectID) {
                nextLevel.setObject(portal.destinationTileX, portal.destinationTileY, 0);
                level.getServer().network.sendToClientsWithTile(new PacketChangeObject(nextLevel, 0, portal.destinationTileX, portal.destinationTileY, 0), nextLevel, portal.destinationTileX, portal.destinationTileY);
            }
        }
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips out = super.getItemTooltips(item, perspective);
        out.add(Localization.translate("itemtooltip", this.getStringID() + "tip"));
        return out;
    }

    public static int[] registerLadderPair(String stringID, String textureName, String localizationKey, LevelIdentifier destinationLevelIdentifier, Color debrisColor, Item.Rarity rarity, int itemBrokerValue) {
        int upObjectID;
        LadderDownObject downObject = new LadderDownObject(textureName, localizationKey, destinationLevelIdentifier, debrisColor, rarity);
        LadderUpObject upObject = new LadderUpObject(textureName, localizationKey, destinationLevelIdentifier, debrisColor);
        int downObjectID = ObjectRegistry.registerObject(stringID + "down", downObject, itemBrokerValue, true);
        downObject.ladderUpObjectID = upObjectID = ObjectRegistry.registerObject(stringID + "up", upObject, 0.0f, false);
        upObject.ladderDownObjectID = downObjectID;
        return new int[]{downObjectID, upObjectID};
    }
}

