/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ChieftainsPedestalObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChieftainsPedestalObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected boolean hasRotation;
    protected int yOffset = -3;

    public ChieftainsPedestalObject() {
        super(new Rectangle(2, 5, 28, 22));
        this.toolType = ToolType.UNBREAKABLE;
        this.mapColor = new Color(186, 136, 46);
        this.isLightTransparent = true;
        this.lightLevel = 255;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.collision = new Rectangle(0, -8, 32, 32);
        this.lightHue = 166.0f;
        this.lightSat = 0.7f;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/chieftainspedestal");
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        ChieftainsPedestalObjectEntity chieftainsPedestal = this.getCurrentObjectEntity(level, tileX, tileY, ChieftainsPedestalObjectEntity.class);
        if (chieftainsPedestal != null) {
            float heightMultiplier = chieftainsPedestal.getAnimationHeightProgress();
            if (heightMultiplier > 0.0f && heightMultiplier < 1.0f) {
                if (GameRandom.globalRandom.getChance(heightMultiplier)) {
                    int startHeight = -8 + GameRandom.globalRandom.nextInt(16);
                    level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(0, 32), tileY * 32 + 32, Particle.GType.COSMETIC).sizeFades(8, 12).smokeColor().heightMoves(startHeight, startHeight + 32).lifeTime(1000);
                }
            } else if (GameRandom.globalRandom.getChance(0.25f)) {
                level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(6, 28), tileY * 32 + GameRandom.globalRandom.getIntBetween(10, 16) + 28, GameRandom.globalRandom.getChance(0.75f) ? Particle.GType.CRITICAL : Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f), GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f)).color(new Color(77, 209, 178)).sizeFades(10, 14).heightMoves(48.0f, 70.0f).lifeTime(2000);
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd drawOptions;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) + 32;
        ChieftainsPedestalObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY, ChieftainsPedestalObjectEntity.class);
        float heightProgress = 1.0f;
        if (objectEntity != null) {
            heightProgress = objectEntity.getAnimationHeightProgress();
        }
        int endY = (int)(heightProgress * (float)texture.getHeight());
        if (this.hasRotation) {
            byte rotation = level.getObjectRotation(tileX, tileY);
            int spriteRes = texture.getWidth() / 4;
            int spriteX = rotation % 4;
            int xOffset = (spriteRes - 32) / 2;
            drawOptions = texture.initDraw().section(spriteX * spriteRes, spriteX * spriteRes + spriteRes, 0, endY).light(light).pos(drawX - xOffset, drawY + this.yOffset - endY);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            drawOptions = texture.initDraw().section(0, texture.getWidth(), 0, endY).light(light).pos(drawX - xOffset, drawY + this.yOffset - endY);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        if (this.hasRotation) {
            int spriteWidth = texture.getWidth() / 4;
            int xOffset = (spriteWidth - 32) / 2;
            texture.initDraw().sprite(rotation % 4, 0, spriteWidth, texture.getHeight()).alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            texture.initDraw().alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "activatetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        Item item;
        super.interact(level, x, y, player);
        ChieftainsPedestalObjectEntity currentObjectEntity = this.getCurrentObjectEntity(level, x, y, ChieftainsPedestalObjectEntity.class);
        if (currentObjectEntity != null && currentObjectEntity.canStartChieftainEvent() && !player.isItemOnCooldown(item = ItemRegistry.getItem("boneoffering"))) {
            if (player.getInv().removeItems(item, 1, false, false, false, false, "use") > 0) {
                player.startItemCooldown(item, 2000);
                if (level.isServer()) {
                    currentObjectEntity.startChieftainEvent();
                }
            } else if (level.isServer() && player.isServerClient()) {
                player.getServerClient().sendChatMessage(new LocalMessage("misc", "bossmissingitem"));
            }
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ChieftainsPedestalObjectEntity(level, x, y);
    }
}

