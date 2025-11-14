/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
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

public class SpideriteArmorStandObject
extends GameObject {
    private GameTexture texture;

    public SpideriteArmorStandObject(Color mapColor) {
        super(new Rectangle(32, 32));
        this.mapColor = mapColor;
        this.objectHealth = 10;
        this.attackThrough = true;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/spideritearmorstand");
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
    public void spawnDestroyedParticles(Level level, int tileX, int tileY) {
        super.spawnDestroyedParticles(level, tileX, tileY);
        GameRandom random = GameRandom.globalRandom;
        for (int i = 0; i < 4; ++i) {
            FleshParticle particle = new FleshParticle(level, this.texture, i, 2, 32, tileX * 32 + 16, (float)(tileY * 32 + 16), 20.0f, (float)random.getIntBetween(-100, 100), (float)random.getIntBetween(-100, 100));
            level.entityManager.addParticle(particle, Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % 4;
        int yOffset = rotation % 2 == 0 ? -2 : 0;
        final TextureDrawOptionsEnd drawOptions = this.texture.initDraw().sprite(rotation % 4, 0, 32, 64).light(light).pos(drawX, drawY - this.texture.getHeight() + 64 + yOffset);
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
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int yOffset = rotation % 2 == 0 ? -2 : 0;
        this.texture.initDraw().sprite(rotation % 4, 0, 32, 64).light(light).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 64 + yOffset);
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
        SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).pitch(0.5f));
    }
}

