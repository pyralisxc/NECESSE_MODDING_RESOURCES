/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.HumanLook;
import necesse.gfx.PlayerSprite;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.SettlementFlagObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.light.GameLight;

public class SettlementFlagObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    private static PlayerMob player;

    public SettlementFlagObject() {
        super(new Rectangle(6, 6, 20, 20));
        this.mapColor = new Color(171, 174, 190);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/settlementflag");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DrawOptions notificationOptions;
        SettlementNotificationSeverity highestSeverity;
        DrawOptions lookOptions;
        HumanLook humanLook;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd baseOptions = texture.initDraw().sprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(level);
        NetworkSettlementData networkData = settlementsData.getNetworkDataAtTile(level.getIdentifier(), tileX, tileY);
        HumanLook humanLook2 = humanLook = networkData == null ? null : networkData.getLook();
        if (humanLook != null) {
            DrawOptions lookOptionsWithoutShader = SettlementFlagObject.getHumanLookDrawOptions(drawX + 1, drawY + 1, 32, 1.0f, light, humanLook);
            lookOptions = () -> {
                GameResources.rectangleShader.use(drawX + 8, drawY - 30, 16, 28);
                lookOptionsWithoutShader.draw();
                GameResources.rectangleShader.stop();
            };
        } else {
            lookOptions = texture.initDraw().sprite(1, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32 - 4);
        }
        SettlementNotificationSeverity settlementNotificationSeverity = highestSeverity = networkData == null ? null : (SettlementNotificationSeverity)networkData.notifications.getNotifications().stream().map(SettlementNotificationManager.ActiveNotification::getHighestSeverity).max(Comparator.comparingInt(Enum::ordinal)).orElse(null);
        if (highestSeverity != null && highestSeverity != SettlementNotificationSeverity.NOTE) {
            GameTexture iconTexture = highestSeverity.iconTexture.get();
            int bounce = GameUtils.getBounceAnim(level.getLocalTime(), 15, 2000, 3000, 800);
            notificationOptions = iconTexture.initDraw().light(light.minLevelCopy(100.0f)).pos(drawX + 16 - iconTexture.getWidth() / 2, drawY - 32 - iconTexture.getHeight() - bounce);
        } else {
            notificationOptions = () -> {};
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                baseOptions.draw();
                lookOptions.draw();
                notificationOptions.draw();
            }
        });
    }

    public static synchronized DrawOptions getHumanLookDrawOptions(int drawX, int drawY, int size, float alpha, GameLight light, HumanLook look) {
        if (player == null) {
            player = new PlayerMob(0L, null);
        }
        SettlementFlagObject.player.look = look;
        player.getInv().giveLookArmor();
        return PlayerSprite.getIconDrawOptions(drawX, drawY - 32, size, size, player, 0, 2, alpha, light);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(0, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 4, -38, 24, 38));
        return list;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SettlementFlagObjectEntity(level, x, y, this.texture);
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
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "settlementflagtip1"));
        tooltips.add(Localization.translate("itemtooltip", "settlementflagtip2"));
        tooltips.add(Localization.translate("itemtooltip", "settlementflagtip3"));
        return tooltips;
    }

    @Override
    public Item generateNewObjectItem() {
        return new SettlementFlagObjectItem(this);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public int getInteractRange(Level level, int tileX, int tileY) {
        return 32000;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        ServerSettlementData serverData;
        if (level.isServer() && (serverData = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y)) != null) {
            ServerClient client = player.getServerClient();
            PacketOpenContainer openPacket = PacketOpenContainer.Settlement(ContainerRegistry.SETTLEMENT_CONTAINER, serverData);
            ContainerRegistry.openAndSendContainer(client, openPacket);
        }
    }
}

