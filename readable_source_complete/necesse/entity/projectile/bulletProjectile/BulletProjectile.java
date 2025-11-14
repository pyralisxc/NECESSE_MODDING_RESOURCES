/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RicochetableProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class BulletProjectile
extends Projectile
implements RicochetableProjectile {
    public BulletProjectile() {
        this.height = 18.0f;
    }

    public BulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    @Override
    public void init() {
        super.init();
        this.givesLight = true;
        this.trailOffset = 0.0f;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.height);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.height = reader.getNextFloat();
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(150, 50, 0), 22.0f, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    protected Color getWallHitColor() {
        return new Color(150, 50, 0);
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, this.lightSaturation);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.gunhit, (SoundEffect)SoundEffect.effect(x, y));
    }
}

