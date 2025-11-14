/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrozenDwarfMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("icejavelin", 10, 15));
    public static GameDamage damage = new GameDamage(17.0f);
    public boolean hasHair;

    public FrozenDwarfMob() {
        super(120);
        this.attackCooldown = 1250;
        this.attackAnimTime = 250;
        this.setSpeed(35.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -35, 28, 42);
        this.swimMaskMove = 14;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FrozenDwarfMob>(this, new ConfusedPlayerChaserWandererAI<FrozenDwarfMob>(null, 384, 256, 40000, false, false){

            @Override
            public boolean attackTarget(FrozenDwarfMob mob, Mob target) {
                boolean success = this.shootSimpleProjectile(mob, target, "hostileicejavelin", damage, 80, 480, 40);
                if (success) {
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                }
                return success;
            }
        });
        this.hasHair = new GameRandom(this.getUniqueID()).nextSeeded(51).nextBoolean();
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (!event.wasPrevented) {
            this.startAttackCooldown();
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.frozenDwarf.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FrozenDwarfMob.getTileCoordinate(x), FrozenDwarfMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FrozenDwarfMob.getTileCoordinate(x), FrozenDwarfMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        float animProgress = this.getAttackAnimProgress();
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.frozenDwarf).hairTexture(this.hasHair ? MobRegistry.Textures.frozenDwarfHair : null).sprite(sprite).dir(dir).mask(swimMask).light(light).attackOffsets(dir == 3 ? 36 : 28, 23, 10, 15, 12, 4, 12);
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.frozenDwarf.body, 0, 9, 32).itemRotatePoint(4, 4).itemEnd().armSprite(MobRegistry.Textures.frozenDwarf.body, 0, 8, 32).swingRotation(animProgress).light(light);
            humanDrawOptions.attackAnim(attackOptions, animProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this).volume(0.5f));
        }
    }
}

