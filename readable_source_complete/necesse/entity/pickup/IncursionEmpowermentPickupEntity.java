/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.pickup;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public class IncursionEmpowermentPickupEntity
extends PickupEntity {
    private int tickCounter;

    public IncursionEmpowermentPickupEntity() {
    }

    public IncursionEmpowermentPickupEntity(Level level, float x, float y, float dx, float dy) {
        super(level, x, y, dx, dy);
        this.bouncy = 0.75f;
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            GameRandom random = GameRandom.globalRandom;
            for (int i = 0; i < 10; ++i) {
                this.getLevel().entityManager.addParticle(this.x + random.floatGaussian() * 16.0f, this.y + random.floatGaussian() * 12.0f, Particle.GType.IMPORTANT_COSMETIC).sizeFades(22, 11).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(22, 155, 215)).movesFrictionAngle(random.getIntBetween(0, 360), 50.0f, 0.5f).lifeTime(5000).givesLight(75.0f, 0.5f);
            }
        }
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 200) {
            this.remove();
        } else {
            super.clientTick();
            if (this.getLevel() != null) {
                this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), this.y - 20.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle(GameRandom.globalRandom.getIntBetween(0, 360), 3.0f).height(-20.0f).givesLight(247.0f, 1.0f);
            }
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 200) {
            this.remove();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void onPickup(ServerClient client) {
        this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityPickup(this, new Packet()), this);
        if (!this.isServer()) {
            SoundManager.playSound(GameResources.shatter2, (SoundEffect)SoundEffect.effect(this).volume(3.0f).pitch(2.0f));
        }
        Buff randomEmpowermentBuff = GameRandom.globalRandom.getOneOf(BuffRegistry.INCURSION_EMPOWERMENT_REGEN, BuffRegistry.INCURSION_EMPOWERMENT_ATTACK_SPEED, BuffRegistry.INCURSION_EMPOWERMENT_CRIT_CHANCE);
        int buffDuration = 8000;
        if (randomEmpowermentBuff == BuffRegistry.INCURSION_EMPOWERMENT_REGEN) {
            buffDuration = 4000;
        }
        if (this.getLevel().isIncursionLevel && ((IncursionLevel)this.getLevel()).incursionData != null && ((IncursionLevel)this.getLevel()).incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.IMPROVED_EMPOWERMENT_BUFFS.getID())) {
            buffDuration *= 2;
        }
        client.playerMob.buffManager.addBuff(new ActiveBuff(randomEmpowermentBuff, (Mob)client.playerMob, buffDuration, null), true);
        this.remove();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        int timePerFrame = 100;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 2;
        final TextureDrawOptionsEnd pickupOptions = GameResources.empowermentBuffPickup.initDraw().sprite(spriteIndex, 0, 28, 40).size(28, 40).pos(drawX - 14, drawY - 20);
        topList.add(new LevelSortedDrawable(this){

            @Override
            public int getSortY() {
                return 0;
            }

            @Override
            public void draw(TickManager tickManager) {
                pickupOptions.draw();
            }
        });
    }
}

