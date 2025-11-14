/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent
 *  necesse.entity.mobs.Mob
 *  necesse.gfx.GameResources
 */
package aphorea.levelevents.runes;

import aphorea.projectiles.rune.RuneOfCryoQueenProjectile;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class AphRuneOfCryoQueenEvent
extends MobAbilityLevelEvent {
    public float effectNumber;
    private int x;
    private int y;
    private int timer;
    private int index;
    private float startAngle;
    private boolean clockwise;

    public AphRuneOfCryoQueenEvent() {
    }

    public AphRuneOfCryoQueenEvent(Mob owner, int x, int y, float startAngle, boolean clockwise, float effectNumber) {
        super(owner, new GameRandom());
        this.effectNumber = effectNumber;
        this.x = x;
        this.y = y;
        this.startAngle = startAngle;
        this.clockwise = clockwise;
        this.timer = 0;
        this.index = 0;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.effectNumber);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
        writer.putNextFloat(this.startAngle);
        writer.putNextBoolean(this.clockwise);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.effectNumber = reader.getNextFloat();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.startAngle = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
        this.timer = 0;
        this.index = 0;
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.timer = 0;
        this.index = 0;
        if (this.isClient()) {
            float pitch = ((Float)GameRandom.globalRandom.getOneOf((Object[])new Float[]{Float.valueOf(1.0f), Float.valueOf(1.05f)})).floatValue();
            SoundManager.playSound((GameSound)GameResources.jingle, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).pitch(pitch));
        }
    }

    public void clientTick() {
        super.clientTick();
        this.tick();
    }

    public void serverTick() {
        super.serverTick();
        this.tick();
    }

    public void tick() {
        this.timer += 50;
        while (this.timer >= 30) {
            if (this.index >= 18) {
                this.over();
            }
            this.timer -= 30;
            float angle = this.startAngle + (float)(this.index * 20);
            float speed = this.getProjectileSpeed();
            this.owner.getLevel().entityManager.projectiles.add((Entity)new RuneOfCryoQueenProjectile(this.x, this.y, 20.0f, angle, this.clockwise, speed, (int)this.effectNumber, 100, this.owner));
            ++this.index;
        }
    }

    protected float getProjectileSpeed() {
        return 150.0f;
    }
}

