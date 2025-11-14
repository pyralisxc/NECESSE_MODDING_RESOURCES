/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CampfireAddonObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RoastingStationObject
extends CampfireAddonObject {
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray roasterTexture;

    public RoastingStationObject() {
        this.mapColor = new Color(233, 134, 39);
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
    }

    @Override
    public int getCraftingCategoryDepth() {
        return 2;
    }

    @Override
    public int getCraftingFormWidth() {
        return 418;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/campfire");
        this.roasterTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/campfire_roaster");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd flame;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 0, 32, 32).light(light).pos(drawX, drawY - (texture.getHeight() - 32));
        GameTexture roasterTexture = this.roasterTexture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd roasterOptions = roasterTexture.initDraw().light(light).pos(drawX, drawY - (roasterTexture.getHeight() - 32));
        boolean isFueled = false;
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof CampfireObjectEntity) {
            CampfireObjectEntity coe = (CampfireObjectEntity)objectEntity;
            isFueled = coe.isFueled();
        }
        if (isFueled) {
            int spriteX = (int)(level.getWorldEntity().getWorldTime() % 6000L / 2000L);
            flame = texture.initDraw().sprite(spriteX + 1, 0, 32).light(light).pos(drawX, drawY);
        } else {
            flame = null;
        }
        tileList.add(tm -> {
            options.draw();
            if (flame != null) {
                flame.draw();
            }
        });
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                roasterOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(0, 0, 32, 32).alpha(alpha).draw(drawX, drawY - (texture.getHeight() - 32));
        GameTexture roasterTexture = this.roasterTexture.getDamagedTexture(0.0f);
        roasterTexture.initDraw().alpha(alpha).draw(drawX, drawY - (roasterTexture.getHeight() - 32));
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.ROASTING_STATION};
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "roastingtip"));
        return tooltips;
    }
}

