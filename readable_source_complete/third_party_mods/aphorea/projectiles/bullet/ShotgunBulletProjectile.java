/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.bulletProjectile.BulletProjectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.bullet;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ShotgunBulletProjectile
extends BulletProjectile {
    public float armorPenPercent;
    public int spriteX;
    public static Color[] trailColors = new Color[]{AphColors.iron, AphColors.withAlpha(AphColors.red, 128)};

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.armorPenPercent);
        writer.putNextByte((byte)this.spriteX);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.armorPenPercent = reader.getNextFloat();
        this.spriteX = reader.getNextByte();
    }

    public ShotgunBulletProjectile() {
    }

    public ShotgunBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float armorPenPercent, int knockback, Mob owner, int spriteX) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        this.armorPenPercent = armorPenPercent;
        this.spriteX = spriteX;
    }

    public ShotgunBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float armorPenPercent, int knockback, Mob owner) {
        this(x, y, targetX, targetY, speed, distance, damage, armorPenPercent, knockback, owner, 0);
    }

    public void init() {
        super.init();
        this.setWidth(4.0f);
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        this.piercing = this.spriteX == 1 ? 2 : 0;
    }

    public Trail getTrail() {
        Trail trail = new Trail((Projectile)this, this.getLevel(), trailColors[this.spriteX], 22.0f, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return AphColors.iron;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.spriteX, 0, 8, 14).light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 2).pos(drawX, drawY - (int)this.getHeight());
        list.add((LevelSortedDrawable)new EntityDrawable((Entity)this, (TextureDrawOptions)options){
            final /* synthetic */ TextureDrawOptions val$options;
            {
                this.val$options = textureDrawOptions;
                super(arg0);
            }

            public void draw(TickManager tickManager) {
                this.val$options.draw();
            }
        });
    }

    public void applyDamage(Mob mob, float x, float y) {
        mob.isServerHit(this.getDamage().setArmorPen(mob.getArmor() * this.armorPenPercent), mob.x - x * -this.dx * 50.0f, mob.y - y * -this.dy * 50.0f, (float)this.knockback, (Attacker)this);
    }
}

