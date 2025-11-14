/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
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
import necesse.level.gameObject.container.FueledCraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ForgeObject
extends FueledCraftingStationObject {
    public ObjectDamagedTextureArray texture;

    public ForgeObject() {
        super(new Rectangle(32, 32));
        this.isLightTransparent = true;
        this.roomProperties.add("metalwork");
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        if (fueledObjectEntity != null && fueledObjectEntity.isFueled()) {
            return 100;
        }
        return 0;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        ObjectEntity objectEntity;
        super.tickEffect(level, layerID, tileX, tileY);
        if (GameRandom.globalRandom.nextInt(10) == 0 && (objectEntity = level.entityManager.getObjectEntity(tileX, tileY)) instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)objectEntity).isFueled()) {
            int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), tileY * 32 + 32, Particle.GType.COSMETIC).smokeColor().heightMoves(startHeight, startHeight + 20).lifeTime(1000);
        }
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/forge");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation % 2 == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd flame;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        boolean isFueled = false;
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FueledInventoryObjectEntity) {
            isFueled = ((FueledInventoryObjectEntity)objectEntity).isFueled();
        }
        int spriteHeight = texture.getHeight() - 32;
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).light(light).pos(drawX, drawY - (spriteHeight - 32));
        if (isFueled && rotation == 2) {
            int spriteX = (int)(level.getWorldEntity().getWorldTime() % 1200L / 300L);
            flame = texture.initDraw().sprite(spriteX, spriteHeight / 32, 32).light(light).pos(drawX, drawY);
        } else {
            flame = null;
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
                if (flame != null) {
                    flame.draw();
                }
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteHeight = texture.getHeight() - 32;
        texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - (spriteHeight - 32));
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FORGE};
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "forgetip"));
        return tooltips;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new AnyLogFueledInventoryObjectEntity(level, "forge", x, y, false);
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }
}

