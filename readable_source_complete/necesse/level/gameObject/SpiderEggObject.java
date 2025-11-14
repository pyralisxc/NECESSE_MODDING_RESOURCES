/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SpiderEggObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SpiderEggBrokenParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEggObject
extends GameObject {
    private GameTexture texture;
    private GameTexture brokenTexture;

    public SpiderEggObject() {
        super(new Rectangle(32, 32));
        this.objectHealth = 10;
        this.attackThrough = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/spideregg");
        this.brokenTexture = GameTexture.fromFile("objects/spideregg_broken");
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        if (!level.objectLayer.isPlayerPlaced(x, y)) {
            super.attackThrough(level, x, y, damage, attacker);
        }
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture drawnTexture = this.texture;
        ObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX, tileY);
        if (objectEntity instanceof SpiderEggObjectEntity && ((SpiderEggObjectEntity)objectEntity).isBroken) {
            drawnTexture = this.brokenTexture;
        }
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 24;
        final TextureDrawOptionsEnd options = drawnTexture.initDraw().sprite(0, 0, 64).light(light).pos(drawX, drawY);
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
        GameTexture drawnTexture = this.texture;
        ObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX, tileY);
        if (objectEntity instanceof SpiderEggObjectEntity && ((SpiderEggObjectEntity)objectEntity).isBroken) {
            drawnTexture = this.brokenTexture;
        }
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 24;
        drawnTexture.initDraw().sprite(0, 0, 64).light(light).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SpiderEggObjectEntity(level, x, y);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        if (damage.damage > 0.0f) {
            this.playDamageSound(level, x, y, true);
        }
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(new SoundSettings(GameResources.slimeSplash2).volume(0.3f), SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        ObjectEntity objectEntity;
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        if (client != null && (objectEntity = this.getCurrentObjectEntity(level, x, y)) instanceof SpiderEggObjectEntity) {
            SpiderEggObjectEntity spiderEggObjectEntity = (SpiderEggObjectEntity)objectEntity;
            if (!spiderEggObjectEntity.isBroken) {
                spiderEggObjectEntity.breakEgg();
            }
        }
        if (level.isClient()) {
            level.entityManager.addParticle(new SpiderEggBrokenParticle(level, x * 32 + 16, y * 32 + 16, 5000L, this.brokenTexture), Particle.GType.CRITICAL);
            if (!level.objectLayer.isPlayerPlaced(x, y)) {
                for (int i = 0; i < 40; ++i) {
                    level.entityManager.addParticle(x * 32 + 16, y * 32 + 16, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1000).sizeFades(30, 50).movesFriction(GameRandom.globalRandom.getIntBetween(-10, 10) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3), GameRandom.globalRandom.getIntBetween(5, 15) * (GameRandom.globalRandom.nextBoolean() ? -1 : -3), 0.5f).color(new Color(166, 204, 52)).height(10.0f);
                }
            }
        }
    }
}

