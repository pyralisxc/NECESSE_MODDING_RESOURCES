/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionShooterPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CaveSpiderSpitProjectile;
import necesse.entity.projectile.CaveSpiderWebProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class GiantCaveSpiderMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("cavespidergland", 1, 2, MultiTextureMatItem.getGNDData(0)));
    public Variant variant = Variant.NORMAL;
    protected GameDamage meleeDamage;
    protected GameDamage spitDamage;

    public GiantCaveSpiderMob(Variant variant, int health, GameDamage meleeDamage, GameDamage spitDamage) {
        super(health);
        this.variant = variant;
        this.meleeDamage = meleeDamage;
        this.spitDamage = spitDamage;
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.2f);
        this.setArmor(5);
        this.attackAnimTime = 500;
        this.collision = new Rectangle(-20, -20, 40, 40);
        this.hitBox = new Rectangle(-30, -25, 60, 50);
        this.selectBox = new Rectangle(-40, -45, 80, 60);
        this.swimMaskMove = 20;
        this.swimMaskOffset = -24;
        this.swimSinkOffset = 0;
    }

    public GiantCaveSpiderMob() {
        this(Variant.NORMAL, 200, new GameDamage(20.0f), new GameDamage(12.0f));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.variant.ordinal());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.variant = Variant.values()[reader.getNextShortUnsigned()];
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<GiantCaveSpiderMob>(this, new CollisionShooterPlayerChaserWandererAI<GiantCaveSpiderMob>(null, 256, this.meleeDamage, 100, CooldownAttackTargetAINode.CooldownTimer.HAS_TARGET, 3500, 480, 40000){

            @Override
            public boolean shootAtTarget(GiantCaveSpiderMob mob, Mob target) {
                if (mob.canAttack() && !mob.inLiquid()) {
                    int targetDistance = (int)mob.getDistance(target);
                    mob.attack(target.getX(), target.getY(), false);
                    Point point = ProjectileToolItem.controlledRangePosition(GameRandom.globalRandom, mob.getX(), mob.getY(), target.getX(), target.getY(), Math.max(320, targetDistance + 32), 32, 16);
                    int pointDistance = (int)mob.getDistance(point.x, point.y);
                    if (GameRandom.globalRandom.nextBoolean()) {
                        mob.getLevel().entityManager.projectiles.add(new CaveSpiderWebProjectile(mob.x, mob.y, point.x, point.y, mob.spitDamage, mob, pointDistance));
                    } else {
                        mob.getLevel().entityManager.projectiles.add(new CaveSpiderSpitProjectile(mob.variant, mob.x, mob.y, point.x, point.y, mob.spitDamage, mob, pointDistance));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public Point getPathMoveOffset() {
        return new Point(32, 32);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("cavespider", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 10; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), this.variant.texture.get().body, 14 + i / 5, i % 5, 48, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GiantCaveSpiderMob.getTileCoordinate(x), GiantCaveSpiderMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 48;
        int drawY = camera.getDrawY(y) - 60;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(GiantCaveSpiderMob.getTileCoordinate(x), GiantCaveSpiderMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.isAttacking) {
            sprite.x = 6;
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = this.variant.texture.get().body.initDraw().sprite(sprite.x, sprite.y, 96).addMaskShader(swimMask).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                body.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = this.variant.texture.get().shadow.initDraw().sprite(0, sprite.y, 96).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 15;
    }

    @Override
    public float getAttackingMovementModifier() {
        return 0.0f;
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.spiderNestID) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
    }

    @Override
    public void attack(int x, int y, boolean showAllDirections) {
        super.attack(x, y, showAllDirections);
        this.setFacingDir(this.attackDir.x, this.attackDir.y);
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        this.setFacingDir(this.attackDir.x, this.attackDir.y);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.spit, (SoundEffect)SoundEffect.effect(this));
        }
    }

    public static enum Variant {
        NORMAL(() -> MobRegistry.Textures.giantCaveSpider, new Color(160, 200, 65)),
        BLACK(() -> MobRegistry.Textures.giantSnowCaveSpider, new Color(182, 60, 53)),
        SWAMP(() -> MobRegistry.Textures.giantSwampCaveSpider, new Color(82, 126, 60));

        public Supplier<MobTexture> texture;
        public Color particleColor;

        private Variant(Supplier<MobTexture> texture, Color particleColor) {
            this.texture = texture;
            this.particleColor = particleColor;
        }
    }
}

