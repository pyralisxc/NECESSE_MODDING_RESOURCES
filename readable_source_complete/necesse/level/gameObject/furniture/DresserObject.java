/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DresserObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.DecorationHolderInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.TorchHolderInterface;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DresserObject
extends FurnitureObject
implements TorchHolderInterface,
DecorationHolderInterface {
    protected String textureName;
    public ObjectDamagedTextureArray texture;

    public DresserObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.furnitureType = "dresser";
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    public DresserObject(String textureName, Color mapColor, String ... category) {
        this(textureName, ToolType.ALL, mapColor, category);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public DecorDrawOffset getTorchDrawOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 0) {
            return new DecorDrawOffset(0, -22, 20, true);
        }
        if (rotation == 1) {
            return new DecorDrawOffset(-6, -26, 20, true);
        }
        if (rotation == 2) {
            return new DecorDrawOffset(0, -32, 20, true);
        }
        return new DecorDrawOffset(6, -26, 20, true);
    }

    @Override
    public DecorDrawOffset getDecorationDrawOffset(Level level, int tileX, int tileY, GameObject decoration) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 0) {
            return new DecorDrawOffset(0, -24, 20, true);
        }
        if (rotation == 1) {
            return new DecorDrawOffset(-5, -28, 20, true);
        }
        if (rotation == 2) {
            return new DecorDrawOffset(0, -34, 20, true);
        }
        return new DecorDrawOffset(5, -28, 20, true);
    }

    @Override
    public boolean canPlaceDecoration(Level level, int tileX, int tileY) {
        return !this.isTilePlaceOccupied(level, ObjectLayerRegistry.FENCE_AND_TABLE_DECOR, tileX, tileY, true);
    }

    @Override
    public Dimension getMaxDecorationSize(Level level, int tileX, int tileY) {
        return new Dimension(20, 18);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
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
        texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 12, 28, 18);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 2, 22, 28);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 2, y * 32 + 2, 28, 18);
        }
        return new Rectangle(x * 32 + 10, y * 32 + 2, 22, 28);
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(layerID, tileX, tileY);
        LinkedList<ObjectHoverHitbox> list = new LinkedList<ObjectHoverHitbox>();
        if (rotation == 0) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -12, 32, 44, 24));
        } else if (rotation == 1) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32, 16));
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 22, 16, 16));
        } else if (rotation == 2) {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48, 8));
        } else {
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32, 16));
            list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 10, -16, 22, 16, 16));
        }
        return list;
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
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.DRESSER_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new DresserObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "dressertip"));
        return tooltips;
    }

    @Override
    public void doExplosionDamage(Level level, int layerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, tileX, tileY);
        if (!hasSettlement) {
            super.doExplosionDamage(level, layerID, tileX, tileY, damage, toolTier, attacker, client);
        }
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return new SoundSettings(GameResources.drawerOpen).volume(0.4f);
    }
}

