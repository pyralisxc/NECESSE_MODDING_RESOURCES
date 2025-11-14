/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.placeableItem.objectItem.FallenAltarObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class FallenAltarObject
extends StaticMultiObject {
    public ObjectDamagedTextureArray emptyAltarTexture;
    public GameTexture portalTexture;

    protected FallenAltarObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "fallenaltar");
        this.stackSize = 1;
        this.rarity = Item.Rarity.LEGENDARY;
        this.mapColor = new Color(0, 130, 121);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("incursions");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.emptyAltarTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.texturePath + "empty");
        this.portalTexture = GameTexture.fromFile("objects/" + this.texturePath + "portal");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        FallenAltarObjectEntity altarEntity = this.getCurrentObjectEntity(level, tileX, tileY, FallenAltarObjectEntity.class);
        if (altarEntity != null) {
            return new LootTable(new LootItem(this.getStringID(), FallenAltarObjectItem.altarGNDData(altarEntity.altarData)).preventLootMultiplier());
        }
        return super.getLootTable(level, layerID, tileX, tileY);
    }

    @Override
    public Item generateNewObjectItem() {
        return new FallenAltarObjectItem(this);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "interacttip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        ObjectEntity objectEntity;
        LevelObject master;
        if (level.isServer() && player.isServerClient() && (master = (LevelObject)this.getMultiTile(level, 0, x, y).getMasterLevelObject(level, 0, x, y).orElse(null)) != null && (objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY)) instanceof FallenAltarObjectEntity) {
            FallenAltarObjectEntity altarEntity = (FallenAltarObjectEntity)objectEntity;
            PacketOpenContainer p = PacketOpenContainer.ObjectEntity(ContainerRegistry.FALLEN_ALTAR_CONTAINER, objectEntity, FallenAltarContainer.getContainerContent(level.getServer(), altarEntity));
            ContainerRegistry.openAndSendContainer(player.getServerClient(), p);
        }
        super.interact(level, x, y, player);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity objectEntity;
        ObjectDamagedTextureArray texture = this.texture;
        MultiTile multiTile = this.getMultiTile(level, 0, tileX, tileY);
        LevelObject masterObject = multiTile.getMasterLevelObject(level, 0, tileX, tileY).orElse(null);
        if (masterObject != null && (objectEntity = level.entityManager.getObjectEntity(masterObject.tileX, masterObject.tileY)) instanceof FallenAltarObjectEntity) {
            InventoryItem item;
            GameLight light;
            int drawY;
            int drawX;
            FallenAltarObjectEntity altarEntity = (FallenAltarObjectEntity)objectEntity;
            OpenIncursion openIncursion = altarEntity.getOpenIncursion();
            if (openIncursion != null) {
                texture = this.emptyAltarTexture;
                if (this.isMultiTileMaster()) {
                    Point centerPos = multiTile.getCenterLevelPos(tileX, tileY);
                    drawX = camera.getDrawX(centerPos.x) - 1;
                    drawY = camera.getDrawY(centerPos.y) - 16;
                    light = level.getLightLevel(GameMath.getTileCoordinate(centerPos.x), GameMath.getTileCoordinate(centerPos.y)).minLevelCopy(75.0f);
                    Color color0 = new Color(140, 82, 50);
                    Color color1 = new Color(150, 54, 13);
                    Color color2 = new Color(255, 91, 3);
                    Color color3 = new Color(255, 121, 3);
                    Color color4 = new Color(244, 184, 152);
                    Color color5 = new Color(253, 243, 236);
                    ArrayList<Color> customGatewayColors = openIncursion.incursionData.getIncursionBiome().getFallenAltarGatewayColorsForBiome();
                    if (customGatewayColors != null) {
                        color0 = customGatewayColors.get(0);
                        color1 = customGatewayColors.get(1);
                        color2 = customGatewayColors.get(2);
                        color3 = customGatewayColors.get(3);
                        color4 = customGatewayColors.get(4);
                        color5 = customGatewayColors.get(5);
                    }
                    int maxSize = Math.max(this.portalTexture.getWidth(), this.portalTexture.getHeight()) + 10;
                    float animTimeMod = 2.0f;
                    SharedTextureDrawOptions portalOptions = new SharedTextureDrawOptions(this.portalTexture);
                    this.addSphere(level, drawX, drawY, 0L, (int)(1000.0f * animTimeMod), maxSize, maxSize, color0, 1.0f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(1000.0f * animTimeMod), maxSize - (int)((float)maxSize / 5.0f), maxSize, color1, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(900.0f * animTimeMod), maxSize - (int)((float)maxSize / 5.0f), maxSize, color1, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(800.0f * animTimeMod), maxSize - (int)((float)maxSize / 3.5f), maxSize - (int)((float)maxSize / 5.0f), color2, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(760.0f * animTimeMod), maxSize - (int)((float)maxSize / 3.5f), maxSize - (int)((float)maxSize / 5.0f), color2, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(700.0f * animTimeMod), maxSize - (int)((float)maxSize / 3.5f), maxSize - (int)((float)maxSize / 5.0f), color2, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(740.0f * animTimeMod), maxSize - (int)((float)maxSize / 2.5f), maxSize - (int)((float)maxSize / 4.0f), color3, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(680.0f * animTimeMod), maxSize - (int)((float)maxSize / 2.5f), maxSize - (int)((float)maxSize / 4.0f), color3, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(650.0f * animTimeMod), maxSize - (int)((float)maxSize / 2.5f), maxSize - (int)((float)maxSize / 4.0f), color3, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(630.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.2f), maxSize - (int)((float)maxSize / 2.8f), color4, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(580.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.2f), maxSize - (int)((float)maxSize / 2.8f), color4, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(550.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.2f), maxSize - (int)((float)maxSize / 2.8f), color4, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(540.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.05f), maxSize - (int)((float)maxSize / 1.5f), color5, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(520.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.05f), maxSize - (int)((float)maxSize / 1.5f), color5, 0.5f, light, portalOptions);
                    this.addSphere(level, drawX, drawY, 0L, (int)(490.0f * animTimeMod), maxSize - (int)((float)maxSize / 1.05f), maxSize - (int)((float)maxSize / 1.5f), color5, 0.5f, light, portalOptions);
                    tileList.add(10000, tm -> portalOptions.draw());
                }
            }
            if (this.isMultiTileMaster() && ((item = altarEntity.inventory.getItem(0)) != null || openIncursion != null)) {
                if (openIncursion != null) {
                    item = new InventoryItem("gatewaytablet");
                    GatewayTabletItem.setIncursionData(item, openIncursion.incursionData);
                }
                drawX = camera.getTileDrawX(tileX);
                drawY = camera.getTileDrawY(tileY);
                light = level.getLightLevel(tileX, tileY);
                final DrawOptions drawOptions = item.getWorldDrawOptions(perspective, drawX + 16 + 32, drawY + 32 + 16, light, 0.3f, 32);
                list.add(new LevelSortedDrawable(this, tileX, tileY){

                    @Override
                    public int getSortY() {
                        return 64;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        }
        final DrawOptions options = this.getMultiTextureDrawOptions(texture.getDamagedTexture(this, level, tileX, tileY), level, tileX, tileY, camera);
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

    protected void addSphere(Level level, int drawX, int drawY, long timeOffset, int animTime, int minSize, int maxSize, Color color, float alpha, GameLight light, SharedTextureDrawOptions options) {
        float animFloat = GameUtils.getAnimFloat(level.getWorldEntity().getLocalTime() + timeOffset, animTime);
        int sizeDelta = maxSize - minSize;
        int size = minSize + (int)((float)sizeDelta * animFloat);
        options.addFull().color(color).alpha(alpha).light(light).size(size).posMiddle(drawX, drawY, true);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new FallenAltarObjectEntity(level, x, y);
        }
        return super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerFallenAltar() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(12, 4, 72, 56);
        ids[0] = ObjectRegistry.registerObject("fallenaltar", new FallenAltarObject(0, 0, 3, 2, ids, collision), 0.0f, true);
        ids[1] = ObjectRegistry.registerObject("fallenaltar2", new FallenAltarObject(1, 0, 3, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("fallenaltar3", new FallenAltarObject(2, 0, 3, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("fallenaltar4", new FallenAltarObject(0, 1, 3, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject("fallenaltar5", new FallenAltarObject(1, 1, 3, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject("fallenaltar6", new FallenAltarObject(2, 1, 3, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

