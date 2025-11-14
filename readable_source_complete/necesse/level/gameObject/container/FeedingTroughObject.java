/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FeedingTroughObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.FeedingTrough2Object;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class FeedingTroughObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected FeedingTroughObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(150, 119, 70);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/feedingtrough");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 22);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 5, y * 32 + 6, 22, 26);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 22);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean hasFeed = false;
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FeedingTroughObjectEntity) {
            hasFeed = ((FeedingTroughObjectEntity)objectEntity).hasFeed();
        }
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(texture);
        if (rotation == 0) {
            options.addSprite(2, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(3, 2, 32).light(light).pos(drawX, drawY);
            }
        } else if (rotation == 1) {
            options.addSprite(0, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(0, 0, 32).light(light).pos(drawX, drawY - 32);
                options.addSprite(0, 1, 32).light(light).pos(drawX, drawY);
            }
        } else if (rotation == 2) {
            options.addSprite(2, 1, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(3, 0, 32).light(light).pos(drawX, drawY - 32);
                options.addSprite(3, 1, 32).light(light).pos(drawX, drawY);
            }
        } else {
            options.addSprite(1, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(1, 0, 32).light(light).pos(drawX, drawY - 32);
                options.addSprite(1, 1, 32).light(light).pos(drawX, drawY);
            }
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
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
        if (rotation == 0) {
            texture.initDraw().sprite(2, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(2, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX + 32, drawY);
        } else if (rotation == 2) {
            texture.initDraw().sprite(2, 1, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(2, 2, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX - 32, drawY);
        }
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FeedingTroughObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "feedingtroughtip"));
        return tooltips;
    }

    public static int[] registerFeedingTrough() {
        int o2ID;
        FeedingTroughObject o1 = new FeedingTroughObject();
        int o1ID = ObjectRegistry.registerObject("feedingtrough", o1, 20.0f, true);
        FeedingTrough2Object o2 = new FeedingTrough2Object();
        o1.counterID = o2ID = ObjectRegistry.registerObject("feedingtrough2", o2, 0.0f, false);
        o2.counterID = o1ID;
        return new int[]{o1ID, o2ID};
    }
}

