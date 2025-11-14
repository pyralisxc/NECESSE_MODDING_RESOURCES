/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ShippingChestObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.container.ShippingChest2Object;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.jobs.ShippingChestLevelJob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class ShippingChestObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray openTexture;
    protected int counterID;

    protected ShippingChestObject() {
        super(new Rectangle(32, 32));
        this.displayMapTooltip = true;
        this.mapColor = new Color(132, 91, 25);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.rarity = Item.Rarity.UNCOMMON;
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/shippingchest");
        this.openTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/shippingchest_open");
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        return super.getPlaceOptions(level, levelX, levelY, playerMob, Math.floorMod(playerDir - 1, 4), offsetMultiTile);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ShippingChestObjectEntity objectEntity;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        ObjectDamagedTextureArray usedTexture = this.texture;
        if (this.openTexture != null && (objectEntity = this.getCurrentObjectEntity(level, tileX, tileY, ShippingChestObjectEntity.class)) != null && objectEntity.isInUse()) {
            usedTexture = this.openTexture;
        }
        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = rotation == 0 ? texture.initDraw().sprite(2, texture.getHeight() / 32 - 1, 32).light(light).pos(drawX, drawY) : (rotation == 1 ? texture.initDraw().sprite(3, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32) : (rotation == 2 ? texture.initDraw().sprite(5, 0, 32, texture.getHeight() - 32).light(light).pos(drawX, drawY - 32) : texture.initDraw().sprite(1, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32)));
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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        TextureDrawOptionsEnd options = rotation == 0 ? texture.initDraw().sprite(2, texture.getHeight() / 32 - 1, 32).alpha(alpha).pos(drawX, drawY) : (rotation == 1 ? texture.initDraw().sprite(3, 0, 32, texture.getHeight()).alpha(alpha).pos(drawX, drawY - texture.getHeight() + 32) : (rotation == 2 ? texture.initDraw().sprite(5, 0, 32, texture.getHeight() - 32).alpha(alpha).pos(drawX, drawY - 32) : texture.initDraw().sprite(1, 0, 32, texture.getHeight()).alpha(alpha).pos(drawX, drawY - texture.getHeight() + 32)));
        options.draw();
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(new LocalMessage("ui", "shippingchesttip"), 400);
        return tooltips;
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
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.SHIPPING_CHEST_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ShippingChestObjectEntity(level, x, y);
    }

    @Override
    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        return Collections.singletonList(new ShippingChestLevelJob(tileX, tileY));
    }

    public static int[] registerShippingChest() {
        int i2;
        ShippingChestObject o1 = new ShippingChestObject();
        ShippingChest2Object o2 = new ShippingChest2Object();
        int i1 = ObjectRegistry.registerObject("shippingchest", o1, 200.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("shippingchest2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}

