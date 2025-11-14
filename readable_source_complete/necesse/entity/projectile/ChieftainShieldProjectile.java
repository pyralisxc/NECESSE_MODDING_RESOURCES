/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class ChieftainShieldProjectile
extends BoomerangProjectile {
    protected int soundTimer;
    private boolean isLodged;
    private float initSpeed;
    float lodgeAngle;

    public ChieftainShieldProjectile() {
    }

    public ChieftainShieldProjectile(float x, float y, float targetX, float targetY, GameDamage damage, float projectileSpeed, int distance, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.initSpeed = projectileSpeed;
        this.speed = projectileSpeed;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.initSpeed);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.initSpeed = reader.getNextFloat();
    }

    @Override
    public void setupPositionPacket(PacketWriter writer) {
        super.setupPositionPacket(writer);
        writer.putNextBoolean(this.returningToOwner);
        writer.putNextBoolean(this.isLodged);
    }

    @Override
    public void applyPositionPacket(PacketReader reader) {
        super.applyPositionPacket(reader);
        boolean lastReturningToOwner = this.returningToOwner;
        boolean returningToOwner = reader.getNextBoolean();
        if (returningToOwner && !lastReturningToOwner) {
            this.returnToOwner();
        }
        this.isLodged = reader.getNextBoolean();
        if (!this.isLodged) {
            this.speed = this.initSpeed;
        }
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(20.0f, true);
        this.height = 15.0f;
        this.piercing = Integer.MAX_VALUE;
        this.isSolid = true;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.isLodged) {
            --this.soundTimer;
            if (this.soundTimer <= 0) {
                this.soundTimer = 5;
                SoundManager.playSound(GameResources.swing2, (SoundEffect)SoundEffect.effect(this).volume(0.5f));
            }
        }
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob == null) {
            if (this.isClient()) {
                SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(this).volume(0.5f));
            }
            this.isLodged = true;
            this.speed = 0.0f;
        }
    }

    public void recall() {
        if (!this.isLodged) {
            this.returnToOwner();
        }
        this.speed = this.initSpeed;
        this.isLodged = false;
        this.sendServerUpdatePacket();
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        float angle = !this.isLodged ? this.getAngle() : this.lodgeAngle;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(angle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, angle, this.shadowTexture.getHeight() / 2);
    }

    @Override
    protected SoundSettings getMoveSound() {
        return null;
    }
}

