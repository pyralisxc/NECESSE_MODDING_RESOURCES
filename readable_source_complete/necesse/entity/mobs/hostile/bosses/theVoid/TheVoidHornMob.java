/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class TheVoidHornMob
extends FlyingBossMob {
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    public int removeTicker;
    public final LevelMob<TheVoidMob> master = new LevelMob();
    private int damageTaken;
    private int destructionThreshold = 50000;
    public boolean isBroken;
    public boolean isLeftHorn;
    public BooleanMobAbility setBrokenAbility;

    public TheVoidHornMob() {
        super(10);
        this.isSummoned = true;
        this.dropsLoot = false;
        this.collision = new Rectangle(-40, -130, 80, 140);
        this.hitBox = new Rectangle(-50, -140, 100, 160);
        this.selectBox = new Rectangle(-70, -160, 140, 200);
        this.setKnockbackModifier(0.0f);
        this.setRegen(0.0f);
        this.setSpeed(0.0f);
        this.setArmor(100);
        this.setBrokenAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                TheVoidHornMob.this.isBroken = value;
                TheVoidMob master = TheVoidHornMob.this.master.get(TheVoidHornMob.this.getLevel());
                if (TheVoidHornMob.this.isClient() && TheVoidHornMob.this.isBroken) {
                    TheVoidHornMob.this.playDeathSound();
                    TheVoidHornMob.this.spawnDeathParticles(0.0f, 0.0f);
                    TheVoidHornMob.this.playHurtSound();
                    if (master != null) {
                        master.playHurtSound(true);
                    }
                }
                if (master != null) {
                    master.regenHornsAtPercentHealth = master.getHealthPercent() - 0.25f;
                }
                TheVoidHornMob.this.damageTaken = 0;
            }
        });
    }

    public Point getHornOffset() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getHornOffset(this.isLeftHorn);
        }
        return new Point(0, 0);
    }

    @Override
    public boolean shouldSendSpawnPacket() {
        return false;
    }

    @Override
    public Mob getSpawnPacketMaster() {
        return this.master.get(this.getLevel());
    }

    @Override
    public void init() {
        super.init();
        this.setMovement(new MobMovementRelative(this.master.get(this.getLevel()), 0.0f, 0.0f));
        this.countStats = false;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickMaster();
        if (this.isBroken) {
            for (int i = 0; i < 2; ++i) {
                int xOffset = GameRandom.globalRandom.getIntBetween(-15, 15);
                this.getLevel().entityManager.addParticle(this.x + (float)xOffset, this.y + (float)GameRandom.globalRandom.getIntBetween(-15, 15), this.particleTypeSwitcher.next()).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).sizeFades(24, 48).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-30, -60) + Math.abs(xOffset), 0.8f).lifeTime(1000);
            }
        } else {
            int xOffset = GameRandom.globalRandom.getIntBetween(-30, 30);
            this.getLevel().entityManager.addParticle(this.x + (float)xOffset + (float)(this.isLeftHorn ? -20 : 20), this.y + (float)(this.isLeftHorn ? -xOffset : xOffset), Particle.GType.COSMETIC).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).sizeFades(24, 48).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-60, -90) + Math.abs(xOffset), 0.8f).lifeTime(1000);
        }
    }

    @Override
    public void serverTick() {
        TheVoidMob master;
        super.serverTick();
        if (!this.isBroken && (master = this.master.get(this.getLevel())) != null) {
            this.destructionThreshold = master.getMaxHealth() / 10;
        }
        this.movementUpdateTime = this.getTime();
        this.healthUpdateTime = this.getTime();
        if (this.damageTaken > this.destructionThreshold) {
            this.setBrokenAbility.runAndSend(true);
        }
        this.tickMaster();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int height = this.getFlyingHeight() + 10;
        for (int i = 0; i < 6; ++i) {
            float xOffset = this.isLeftHorn ? -200.0f : 200.0f;
            float yOffset = GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f);
            FleshParticle particle = new FleshParticle(this.getLevel(), MobRegistry.Textures.theVoidDebris, i, 0, 96, this.x + (xOffset += GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f)), this.y + yOffset, knockbackX, knockbackY);
            particle.lifeTime = 10000L;
            particle.dx = GameRandom.globalRandom.getFloatBetween(-80.0f, 80.0f);
            particle.dy = GameRandom.globalRandom.getFloatBetween(-80.0f, 80.0f);
            particle.friction = 0.2f;
            particle.height = GameRandom.globalRandom.getFloatBetween(height, height + 20);
            this.getLevel().entityManager.addParticle(particle, Particle.GType.CRITICAL);
        }
    }

    @Override
    public int getHealth() {
        TheVoidMob head;
        if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
            return head.getHealth();
        }
        return super.getHealth();
    }

    @Override
    public int getMaxHealth() {
        TheVoidMob head;
        if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
            return head.getMaxHealth();
        }
        return super.getMaxHealth();
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        TheVoidMob master;
        int lastHealth = this.getHealth();
        if (this.master != null && (master = this.master.get(this.getLevel())) != null) {
            master.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        int currentHealth = this.getHealth();
        if (currentHealth < lastHealth && !this.isBroken) {
            this.damageTaken += lastHealth - currentHealth;
        }
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public float getIncomingDamageModifier() {
        return 5.0f;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        return !this.isBroken;
    }

    @Override
    public int getFlyingHeight() {
        TheVoidMob master = this.master.get(this.getLevel());
        return master == null ? 0 : master.getFlyingHeight();
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void requestServerUpdate() {
    }

    public void tickMaster() {
        if (this.removed()) {
            return;
        }
        this.master.computeIfPresent(this.getLevel(), m -> {
            this.setMaxHealth(m.getMaxHealth());
            this.setHealthHidden(m.getHealth(), 0.0f, 0.0f, null);
            this.setArmor(m.getArmorFlat() * 2);
        });
        ++this.removeTicker;
        if (this.removeTicker > 20) {
            this.remove();
        }
    }

    @Override
    public Rectangle getHitBox(int x, int y) {
        return super.getHitBox(x + (this.isLeftHorn ? -160 : 160), y);
    }

    @Override
    public Rectangle getCollision(int x, int y) {
        return super.getCollision(x + (this.isLeftHorn ? -160 : 160), y);
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        return super.getSelectBox(x + (this.isLeftHorn ? -160 : 160), y);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float overlayAlpha;
        float headAlpha;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        int halfSpriteWidth = 224;
        int drawX = camera.getDrawX(x) - halfSpriteWidth;
        int drawY = camera.getDrawY(y) - 176 + 25;
        drawY -= this.getFlyingHeight();
        TheVoidMob master = this.master.get(this.getLevel());
        float masterDX = master != null ? master.dx : 0.0f;
        float rotate = GameMath.limit(masterDX / 10.0f, -10.0f, 10.0f);
        float alpha = master != null ? master.getFadeAlpha() : 1.0f;
        final float whiteness = master != null ? master.getFadeWhiteness() : 0.0f;
        alpha = Math.max(alpha, whiteness);
        if (alpha >= 0.5f) {
            headAlpha = 1.0f;
            overlayAlpha = GameMath.lerp(GameMath.clamp(alpha, 0.5f, 1.0f), 1.0f, 0.0f);
        } else {
            headAlpha = 0.0f;
            overlayAlpha = GameMath.lerp(GameMath.clamp(alpha, 0.0f, 0.5f), 0.0f, 1.0f);
        }
        int spriteX = GameUtils.getAnim(this.getTime(), 6, 600) * 2;
        if (!this.isLeftHorn) {
            ++spriteX;
            drawX += halfSpriteWidth;
        }
        final TextureDrawOptionsEnd hornOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteX, this.isBroken ? 6 : 5, halfSpriteWidth, 352).rotate(rotate, this.isLeftHorn ? halfSpriteWidth : 0, 176).alpha(headAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd hornOverlayOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteX, this.isBroken ? 6 : 5, halfSpriteWidth, 352).rotate(rotate, this.isLeftHorn ? halfSpriteWidth : 0, 176).color(0.0f, 0.0f, 0.0f, overlayAlpha).alpha(overlayAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd orbOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteX, 7, halfSpriteWidth, 352).rotate(rotate, this.isLeftHorn ? halfSpriteWidth : 0, 176).alpha(headAlpha).pos(drawX, drawY);
        final TextureDrawOptionsEnd orbOverlayOptions = MobRegistry.Textures.theVoidHead.initDraw().sprite(spriteX, 7, halfSpriteWidth, 352).rotate(rotate, this.isLeftHorn ? halfSpriteWidth : 0, 176).alpha(overlayAlpha).pos(drawX, drawY);
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                try {
                    GameResources.whiteShader.use();
                    GameResources.whiteShader.pass1f("white", whiteness);
                    hornOptions.draw();
                    hornOverlayOptions.draw();
                }
                finally {
                    GameResources.whiteShader.stop();
                }
            }
        });
        topList.add(100, new Drawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (!TheVoidHornMob.this.isBroken) {
                    try {
                        GameResources.whiteShader.use();
                        GameResources.whiteShader.pass1f("white", whiteness);
                        orbOptions.draw();
                        orbOverlayOptions.draw();
                    }
                    finally {
                        GameResources.whiteShader.stop();
                    }
                }
            }
        });
    }

    @Override
    public Mob getAttackOwner() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return master;
        }
        return super.getAttackOwner();
    }

    @Override
    public GameMessage getAttackerName() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getAttackerName();
        }
        return super.getAttackerName();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return ((Mob)master).getDeathMessages();
        }
        return super.getDeathMessages();
    }
}

