/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ApiaryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.ApiaryFramePlaceableItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class ApiaryObject
extends GameObject {
    public ObjectDamagedTextureArray texture;

    public ApiaryObject() {
        super(new Rectangle(2, 8, 28, 22));
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
        this.mapColor = new Color(209, 170, 57);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.rarity = Item.Rarity.COMMON;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
    }

    @Override
    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, tileX, tileY);
        if (apiary != null) {
            ArrayList<LevelJob> jobs = new ArrayList<LevelJob>(2);
            if (apiary.getHoneyAmount() > 0) {
                jobs.add(new HarvestApiaryLevelJob(tileX, tileY));
            }
            if (apiary.canAddFrame()) {
                jobs.add(new FillApiaryFrameLevelJob(tileX, tileY));
            }
            return jobs;
        }
        return super.getLevelJobs(level, tileX, tileY);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/apiary");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        InventoryItem selectedItem;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % 4;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteHeight = texture.getHeight() / 4;
        int spriteX = 0;
        if (perspective != null && (selectedItem = perspective.getSelectedItem()) != null && selectedItem.item instanceof ApiaryFramePlaceableItem) {
            spriteX = 1;
            AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, tileX, tileY);
            if (apiary != null) {
                spriteX = Math.min(1 + apiary.getFrameAmount(), texture.getWidth() / 32);
            }
        }
        final TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(spriteX, rotation, 32, spriteHeight).light(light).pos(drawX, drawY - spriteHeight + 32);
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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteHeight = texture.getHeight() / 4;
        texture.initDraw().sprite(0, rotation % 4, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - spriteHeight + 32);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, tileX, tileY);
        if (apiary != null && GameRandom.globalRandom.nextInt(10) == 0) {
            if (apiary.getHoneyAmount() > 0) {
                int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
                level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), tileY * 32 + 32, Particle.GType.IMPORTANT_COSMETIC).color(new Color(200, 200, 0)).heightMoves(startHeight, startHeight + 20).lifeTime(1000);
            } else if (apiary.hasQueen() || apiary.getBeeAmount() > 0) {
                int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
                level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), tileY * 32 + 32, Particle.GType.IMPORTANT_COSMETIC).color(new Color(200, 200, 200)).heightMoves(startHeight, startHeight + 20).lifeTime(1000);
            }
        }
    }

    public AbstractBeeHiveObjectEntity getApiaryObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof AbstractBeeHiveObjectEntity) {
            return (AbstractBeeHiveObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, x, y);
        if (apiary != null) {
            return apiary.getInteractTip(perspective);
        }
        return null;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, x, y);
        if (apiary != null) {
            apiary.interact(player);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ApiaryObjectEntity(level, x, y);
    }

    @Override
    public boolean onDamaged(Level level, int layerID, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        AbstractBeeHiveObjectEntity apiary;
        boolean out = super.onDamaged(level, layerID, x, y, damage, attacker, client, showEffect, mouseX, mouseY);
        if (damage > 0 && !level.isClient() && (apiary = this.getApiaryObjectEntity(level, x, y)).hasQueen()) {
            apiary.removeQueen(client == null ? null : client.playerMob);
            return false;
        }
        return out;
    }
}

