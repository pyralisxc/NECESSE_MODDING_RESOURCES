/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.WallObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionType;

public class WallTrapObject
extends GameObject {
    protected String textureName;
    public WallObject wallObject;
    public GameTexture texture;

    public WallTrapObject(WallObject wallObject, String trapTexture) {
        this(wallObject, trapTexture, wallObject.toolTier, wallObject.toolType);
    }

    public WallTrapObject(WallObject wallObject, String trapTexture, float toolTier, ToolType toolType) {
        super(new Rectangle(32, 32));
        this.wallObject = wallObject;
        this.textureName = trapTexture;
        this.toolTier = toolTier;
        this.toolType = toolType;
        this.regionType = RegionType.WALL;
        this.setItemCategory("objects", "traps");
        this.displayMapTooltip = true;
        this.mapColor = wallObject.mapColor;
        this.isWall = true;
        this.showsWire = true;
        this.replaceCategories.add("wall");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("door");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("fencegate");
    }

    @Override
    public void onObjectRegistryClosed() {
        super.onObjectRegistryClosed();
        this.wallObject.connectedWalls.add(this.getID());
    }

    @Override
    public GameMessage getNewLocalization() {
        return this.wallObject.getNewTrapLocalization(new LocalMessage("object", this.textureName));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        final ArrayList<LevelSortedDrawable> wallList = new ArrayList<LevelSortedDrawable>();
        this.wallObject.addDrawables(wallList, tileList, level, tileX, tileY, tickManager, camera, perspective);
        final SharedTextureDrawOptions trapDraws = new SharedTextureDrawOptions(this.texture);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        float alpha = 1.0f;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 48, 64, 48);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5f;
            } else if (alphaRec.contains(camera.getMouseLevelPosX(), camera.getMouseLevelPosY())) {
                alpha = 0.5f;
            }
        }
        int spriteX = rotation % 4;
        if (!level.getObject((int)tileX, (int)(tileY - 1)).isWall) {
            trapDraws.addSprite(spriteX, 0, 32, 64).alpha(alpha).light(light).pos(drawX, drawY - 32);
        } else {
            trapDraws.addSprite(spriteX, 0, 32, 64).light(light).pos(drawX, drawY - 32);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
            }

            @Override
            public void draw(TickManager tickManager) {
                wallList.forEach(d -> d.draw(tickManager));
                trapDraws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        this.wallObject.drawPreview(level, tileX, tileY, rotation, alpha, player, camera);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteX = rotation % 4;
        this.texture.initDraw().sprite(spriteX, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
        this.texture.initDraw().sprite(spriteX, 1, 32).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        return this.wallObject.getHoverHitboxes(level, layerID, tileX, tileY);
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        ObjectEntity ent;
        if (active && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null) {
            ((TrapObjectEntity)ent).triggerTrap(wireID, level.getObjectRotation(tileX, tileY));
        }
    }

    @Override
    public boolean stopsTerrainSplatting() {
        return true;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new TrapObjectEntity(level, x, y, 10000L);
    }

    @Override
    public void loadTextures() {
        GameTexture texture;
        super.loadTextures();
        try {
            texture = GameTexture.fromFileRaw("objects/" + this.textureName + "_short");
        }
        catch (FileNotFoundException e) {
            texture = GameTexture.fromFile("objects/" + this.textureName);
        }
        this.texture = texture;
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture itemTexture = new GameTexture(this.wallObject.generateItemTexture());
        itemTexture.merge(this.texture, 0, 0, 128, 0, 32, 32, MergeFunction.NORMAL);
        itemTexture.makeFinal();
        return itemTexture;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "activatedwiretip"));
        return tooltips;
    }
}

