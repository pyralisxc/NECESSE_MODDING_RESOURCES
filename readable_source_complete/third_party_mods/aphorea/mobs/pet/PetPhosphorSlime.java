/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.Packet
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.pet;

import aphorea.mobs.friendly.WildPhosphorSlime;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PetPhosphorSlime
extends PetFollowingMob {
    public static GameTexture texture;
    public static GameTexture texture_scared;
    int time;
    int sprite;
    int lightTime;
    static int lightCycle;
    int dayCount = 0;

    public PetPhosphorSlime() {
        super(1);
        this.setSpeed(30.0f);
        this.setFriction(0.5f);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28 - this.getFlyingHeight(), 32, 34 + this.getFlyingHeight());
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new PlayerFollowerAINode(480, 32));
    }

    public void clientTick() {
        super.clientTick();
        ++this.time;
        if (this.time >= 3) {
            this.time = 0;
            ++this.sprite;
        }
        if (this.lightTime >= lightCycle) {
            this.lightTime = 0;
        }
        float lightVariation = (float)Math.sin(Math.toRadians((float)this.lightTime * 360.0f / (float)lightCycle));
        int lightColorVariation = 64 - (int)(64.0f * lightVariation);
        int lightLevelVariation = (int)(10.0f * lightVariation);
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, new Color(255 - lightColorVariation, 208, lightColorVariation), 1.0f, 120 + lightLevelVariation);
        ++this.lightTime;
    }

    public void serverTick() {
        super.serverTick();
        if (this.isScared(this.getLevel())) {
            if (!this.buffManager.hasBuff("movespeedburst")) {
                this.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, (Mob)this, 3000, (Attacker)this), true);
            }
            if (PetPhosphorSlime.dayInSurface(this.getLevel())) {
                ++this.dayCount;
                if (this.dayCount > 400) {
                    this.getServer().network.sendToClientsAtEntireLevel((Packet)new WildPhosphorSlime.PhosphorSlimeParticlesPacket(this.x, this.y), this.getLevel());
                    this.remove();
                }
            }
        }
    }

    public void playDeathSound() {
    }

    public int getFlyingHeight() {
        return 20;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51 - this.getFlyingHeight();
        Point sprite = new Point(this.sprite % 5, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = (this.isScared(level) ? texture_scared : texture).initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
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

    public boolean isScared(Level level) {
        return PetPhosphorSlime.dayInSurface(level) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).anyMatch(m -> m.isHostile && m.getDistance((Mob)this) <= 500.0f) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).noneMatch(m -> m.getDistance((Mob)this) <= 500.0f);
    }

    public static boolean dayInSurface(Level level) {
        return level.getIslandDimension() == 0 && !level.getWorldEntity().isNight();
    }

    static {
        lightCycle = 80;
    }
}

