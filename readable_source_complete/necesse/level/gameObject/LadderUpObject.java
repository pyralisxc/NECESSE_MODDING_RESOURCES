/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
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
import necesse.entity.objectEntity.LadderUpObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

class LadderUpObject
extends GameObject {
    public GameTexture texture;
    public int ladderDownObjectID = -1;
    public final String textureName;
    public final LevelIdentifier targetLevelIdentifier;
    public String localizationKey;

    protected LadderUpObject(String textureName, String localizationKey, LevelIdentifier targetLevelIdentifier, Color mapColor) {
        this.textureName = textureName;
        this.localizationKey = localizationKey;
        this.targetLevelIdentifier = targetLevelIdentifier;
        this.mapColor = mapColor;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.lightLevel = 75;
        this.hoverHitbox = new Rectangle(0, -20, 32, 52);
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
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName + "up");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return ObjectRegistry.getObject(this.ladderDownObjectID).getLootTable(level, layerID, tileX, tileY);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - this.texture.getWidth() / 2 + 16;
        int drawY = camera.getTileDrawY(tileY) - (this.texture.getHeight() - 32) + 32;
        final TextureDrawOptionsEnd options = this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).light(light).pos(drawX, drawY);
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
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - this.texture.getWidth() / 2 + 16;
        int drawY = camera.getTileDrawY(tileY) - (this.texture.getHeight() - 32) + 32;
        this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).light(light).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        if (!level.getIdentifier().equals(this.targetLevelIdentifier)) {
            return "invalidlevel";
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
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
            ((PortalObjectEntity)objectEntity).use(level.getServer(), player.getServerClient());
        }
        super.interact(level, x, y, player);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        PortalObjectEntity portal;
        ObjectEntity objectEntity;
        if (level.isServer() && (objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof PortalObjectEntity && level.getServer().world.levelExists((portal = (PortalObjectEntity)objectEntity).getDestinationIdentifier())) {
            Level nextLevel = level.getServer().world.getLevel(portal.getDestinationIdentifier());
            nextLevel.regionManager.ensureTileIsLoaded(portal.destinationTileX, portal.destinationTileY);
            if (nextLevel.getObjectID(portal.destinationTileX, portal.destinationTileY) == this.ladderDownObjectID) {
                nextLevel.setObject(portal.destinationTileX, portal.destinationTileY, 0);
                level.getServer().network.sendToClientsWithTile(new PacketChangeObject(nextLevel, 0, portal.destinationTileX, portal.destinationTileY, 0), nextLevel, portal.destinationTileX, portal.destinationTileY);
            }
        }
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new LadderUpObjectEntity(this.textureName + "up", level, x, y, LevelIdentifier.SURFACE_IDENTIFIER, this.ladderDownObjectID, this.texture == null ? null : new GameSprite(this.texture, 0, 0, 32));
    }
}

