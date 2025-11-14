/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.bosses.minions;

import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.utils.AphColors;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HearthCrystalMob
extends HostileMob {
    protected int centerX;
    protected int centerY;
    protected float angleOffset;
    protected float radius;
    protected float constantTime;
    protected boolean clockwise;

    public HearthCrystalMob() {
        super(200);
        this.setArmor(10);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-8, -8, 16, 16);
        this.hitBox = new Rectangle(-16, -32, 32, 48);
        this.selectBox = new Rectangle(-18, -18, 34, 34);
        this.shouldSave = false;
    }

    public void setCircularMovement(int centerX, int centerY, float angleOffset, float radius, float constantTime, boolean clockwise) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angleOffset = angleOffset;
        this.radius = radius;
        this.constantTime = constantTime;
        this.clockwise = clockwise;
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.centerX = reader.getNextInt();
        this.centerY = reader.getNextInt();
        this.angleOffset = reader.getNextFloat();
        this.radius = reader.getNextFloat();
        this.constantTime = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.centerX);
        writer.putNextInt(this.centerY);
        writer.putNextFloat(this.angleOffset);
        writer.putNextFloat(this.radius);
        writer.putNextFloat(this.constantTime);
        writer.putNextBoolean(this.clockwise);
    }

    public void clientTick() {
        super.clientTick();
        if (this.notBabylonTowerClose()) {
            this.remove();
        } else {
            long time = this.getTime();
            this.setPos(this.getXPosition(time), this.getYPosition(time), false);
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, AphColors.spinel, 1.0f, 50);
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.notBabylonTowerClose()) {
            this.remove();
        } else {
            long time = this.getTime();
            this.setPos(this.getXPosition(time), this.getYPosition(time), false);
        }
    }

    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    public void playDeathSound() {
        SoundManager.playSound((GameSound)GameResources.crystalHit1, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).volume(1.5f));
    }

    public boolean canBePushed(Mob other) {
        return false;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32;
        TextureDrawOptionsEnd drawOptions = ItemRegistry.getItem((String)"lifespinel").getItemSprite(null, null).initDraw().light(light).pos(drawX, drawY);
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    public boolean canTakeDamage() {
        return true;
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 8;
        int drawY = y - 8;
        BabylonTowerMob.icon.initDraw().sprite(0, 0, 32).size(16, 16).draw(drawX, drawY);
    }

    public boolean canPushMob(Mob other) {
        return false;
    }

    public boolean notBabylonTowerClose() {
        return this.getLevel().entityManager.mobs.stream().noneMatch(m -> Objects.equals(m.getStringID(), "babylontower") && m.getDistance((Mob)this) < (float)BabylonTowerMob.BOSS_AREA_RADIUS);
    }

    public void init() {
        super.init();
        SoundManager.playSound((GameSound)GameResources.crystalHit1, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).volume(1.5f));
        long time = this.getTime();
        this.setPos(this.getXPosition(time), this.getYPosition(time), true);
    }

    public float getAngularSpeed() {
        return 1.0E-6f * (float)(Math.PI * 2 * (double)this.radius / (double)this.constantTime) * (float)(this.clockwise ? -1 : 1);
    }

    public float getCurrentAngle(long time) {
        return this.angleOffset + this.getAngularSpeed() * (float)time;
    }

    public float getXPosition(long time) {
        return (float)this.centerX + this.radius * (float)Math.cos(this.getCurrentAngle(time));
    }

    public float getYPosition(long time) {
        return (float)this.centerY + this.radius * (float)Math.sin(this.getCurrentAngle(time));
    }
}

