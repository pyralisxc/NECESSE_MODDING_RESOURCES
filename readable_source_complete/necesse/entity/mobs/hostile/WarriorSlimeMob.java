/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileSlimeMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WarriorSlimeMob
extends HostileSlimeMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage damage = new GameDamage(115.0f);

    public WarriorSlimeMob() {
        super(550);
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.setArmor(30);
        this.jumpStats.setJumpStrength(100.0f);
        this.jumpStats.setJumpCooldown(25);
        this.jumpStats.setJumpAnimationTime(300);
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-15, -32, 30, 40);
        this.swimMaskMove = 10;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -8;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<WarriorSlimeMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, (this.isSummoned ? 50 : 16) * 32, damage, 100, 40000));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.warriorSlime.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(WarriorSlimeMob.getTileCoordinate(x), WarriorSlimeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 50;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.warriorSlime.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(WarriorSlimeMob.getTileCoordinate(x), WarriorSlimeMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        float attackProgress = this.getAttackAnimProgress();
        final DrawOptions arms = this.isAttacking ? ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.warriorSlime.body, 0, 8, 32).setOffsets((dir == 3 ? 36 : 28) + swimMask.drawXOffset, 22 + swimMask.drawYOffset, 8, 15, 12, 4, 12).swingRotation(attackProgress).light(light).pos(drawX, drawY) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
                if (arms != null) {
                    arms.draw();
                }
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.warriorSlime.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        if (this.inLiquid(x, y)) {
            return new Point(6, dir);
        }
        return new Point(this.getJumpAnimationFrame(6), dir);
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.slimeSplash2).volume(0.1f).basePitch(1.1f).pitchVariance(0.1f), this);
        }
    }
}

