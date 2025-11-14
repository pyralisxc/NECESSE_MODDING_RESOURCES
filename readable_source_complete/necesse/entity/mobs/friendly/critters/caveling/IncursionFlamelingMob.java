/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.NetworkClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.FlamelingsModifierSmokePuffLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.EmptyAINode;
import necesse.entity.mobs.friendly.critters.caveling.CavelingMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class IncursionFlamelingMob
extends CavelingMob {
    public int frameCounter = 2;
    public float frameSpeed;
    float itemYHover = 0.0f;
    boolean itemHoveringDown = false;
    float startTime;
    int fireBallCounter;
    float shootInterval = 222.0f;

    public IncursionFlamelingMob() {
        super(200, 40);
        this.setTeam(-2);
        this.isHostile = true;
    }

    @Override
    public void init() {
        super.init();
        this.texture = MobRegistry.Textures.flameling;
        this.popParticleColor = new Color(227, 122, 30);
        this.ai = new BehaviourTreeAI<IncursionFlamelingMob>(this, new EmptyAINode());
        this.isRock = false;
        if (this.item == null) {
            this.item = new InventoryItem("flamelingorb", 1);
        }
        this.startTime = this.getLevel().getTime();
        if (this.isClient()) {
            FlamelingsModifierSmokePuffLevelEvent explosion = new FlamelingsModifierSmokePuffLevelEvent(this.getX(), this.getY(), 20, new GameDamage(0.0f), false, 0.0f, this);
            this.getLevel().entityManager.events.addHidden(explosion);
        }
    }

    @Override
    public LootTable getLootTable() {
        return new LootTable();
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return this.buffManager.hasBuff(BuffRegistry.PERK_FLAMELINGS_CAN_DIE) && super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        return this.buffManager.hasBuff(BuffRegistry.PERK_FLAMELINGS_CAN_DIE);
    }

    @Override
    public boolean canTakeDamage() {
        return super.canTakeDamage() && this.buffManager.hasBuff(BuffRegistry.PERK_FLAMELINGS_CAN_DIE);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.frameSpeed += delta;
        while (this.frameSpeed > 200.0f) {
            this.frameCounter = this.frameCounter == 4 ? 1 : ++this.frameCounter;
            this.frameSpeed = 0.0f;
            if (!this.isClient()) continue;
            this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y - 4.0f, new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL).next()).sprite(GameResources.particles.sprite(0, 0, 8)).sizeFades(15, 10).rotates().movesConstant(0.0f, 15.0f).flameColor(30.0f).fadesAlphaTime(100, 500).height(50.0f).givesLight(75.0f, 0.5f).lifeTime(800);
        }
        if (this.itemYHover < 5.0f & !this.itemHoveringDown) {
            this.itemYHover += delta / 100.0f;
            if (this.itemYHover >= 5.0f) {
                this.itemHoveringDown = true;
            }
        } else if (this.itemYHover > -5.0f & this.itemHoveringDown) {
            this.itemYHover -= delta / 100.0f;
            if (this.itemYHover <= -5.0f) {
                this.itemHoveringDown = false;
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.readyToShoot()) {
            this.spawnFireBall(this.fireBallCounter * 20);
            ++this.fireBallCounter;
            if (this.fireBallCounter > 18) {
                this.remove();
            }
        }
        if (this.getLevel().buffManager.getModifier(LevelModifiers.MODIFIERS_AFFECT_ENEMIES).booleanValue()) {
            this.setTeam(-1);
        } else {
            this.setTeam(-2);
        }
    }

    @Override
    public void clientTick() {
        super.serverTick();
        if (this.readyToShoot() && this.fireBallCounter <= 18) {
            this.spawnFireBall(0.0f);
            ++this.fireBallCounter;
        }
        if (this.getLevel().buffManager.getModifier(LevelModifiers.MODIFIERS_AFFECT_ENEMIES).booleanValue()) {
            this.setTeam(-1);
        } else {
            this.setTeam(-2);
        }
    }

    public boolean readyToShoot() {
        return (float)this.getLevel().getTime() >= this.startTime + this.shootInterval * (float)this.fireBallCounter;
    }

    public void spawnFireBall(float angle) {
        if (this.isServer()) {
            GameDamage dmg = new GameDamage(100.0f);
            EvilsProtectorAttack1Projectile projectile = new EvilsProtectorAttack1Projectile(this.x, this.y - 30.0f + this.itemYHover, angle, 70.0f, 800, dmg, this);
            projectile.setLevel(this.getLevel());
            projectile.moveDist(60.0);
            this.getLevel().entityManager.projectiles.add(projectile);
        } else {
            SoundManager.playSound(GameResources.fireShot, (SoundEffect)SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.3f)));
        }
    }

    @Override
    public float getOutgoingDamageModifier() {
        float modifier = super.getOutgoingDamageModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE).floatValue();
        }
        return modifier;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(IncursionFlamelingMob.getTileCoordinate(x), IncursionFlamelingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48;
        HumanTexture texture = this.texture != null ? this.texture : MobRegistry.Textures.stoneCaveling;
        drawY += level.getTile(IncursionFlamelingMob.getTileCoordinate(x), IncursionFlamelingMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        Point sprite = new Point(0, 2);
        sprite.x = this.frameCounter;
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd rightArmOptions = texture.rightArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).pos(drawX, drawY += this.getBobbing(x, y));
        final TextureDrawOptionsEnd bodyOptions = texture.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).pos(drawX, drawY);
        final TextureDrawOptionsEnd itemOptions = this.item != null ? this.item.item.getItemSprite(this.item, perspective).initDraw().light(light.minLevelCopy(150.0f)).mirror(sprite.y < 2, false).size(32).posMiddle(drawX + 32 - 2, drawY + (int)this.itemYHover) : null;
        final TextureDrawOptionsEnd leftArmOptions = texture.leftArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                rightArmOptions.draw();
                bodyOptions.draw();
                swimMask.stop();
                if (itemOptions != null) {
                    itemOptions.draw();
                }
                swimMask.use();
                leftArmOptions.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.caveling_shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
        super.spawnRemoveParticles(knockbackX, knockbackY);
        FlamelingsModifierSmokePuffLevelEvent explosion = new FlamelingsModifierSmokePuffLevelEvent(this.getX(), this.getY(), 20, new GameDamage(0.0f), false, 0.0f, this);
        this.getLevel().entityManager.events.addHidden(explosion);
    }
}

