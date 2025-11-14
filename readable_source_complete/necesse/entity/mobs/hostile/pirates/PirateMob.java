/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.pirates;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PirateAITree;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class PirateMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(new ChanceLootItemList(0.05f, new OneOfLootItems(new LootItem("cutlass"), new LootItemList(new LootItem("flintlock"), LootItem.between("simplebullet", 40, 80)))), LootItem.between("coin", 10, 50));
    private boolean rangedAttack;
    private long shootTime;
    private Mob shootTarget;
    private final int normalAnimTime;
    public int meleeDamage = 50;
    public int shootDamage = 45;
    public Point baseTile;
    public final TargetedMobAbility startShootingAbility;
    public final CoordinateMobAbility shootAbility;

    public PirateMob(int health) {
        super(health);
        this.attackCooldown = 500;
        this.attackAnimTime = this.normalAnimTime = 400;
        this.setSpeed(35.0f);
        this.setSwimSpeed(1.0f);
        this.setFriction(3.0f);
        this.setArmor(20);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.canDespawn = false;
        this.baseTile = null;
        this.startShootingAbility = this.registerAbility(new TargetedMobAbility(){

            @Override
            protected void run(Mob target) {
                int chargeTime = 1500;
                PirateMob.this.attackAnimTime = chargeTime + 500;
                PirateMob.this.attackCooldown = chargeTime;
                PirateMob.this.shootTime = PirateMob.this.getWorldEntity().getTime() + (long)chargeTime;
                PirateMob.this.rangedAttack = true;
                PirateMob.this.shootTarget = target;
                PirateMob.this.startAttackCooldown();
                if (target != null) {
                    PirateMob.this.showAttack(target.getX(), target.getY(), false);
                } else {
                    PirateMob.this.showAttack(PirateMob.this.getX() + 100, PirateMob.this.getY(), false);
                }
            }
        });
        this.shootAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                PirateMob.this.rangedAttack = true;
                PirateMob.this.attackAnimTime = PirateMob.this.normalAnimTime;
                PirateMob.this.shootAbilityProjectile(x, y);
            }
        });
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.baseTile != null) {
            save.addPoint("baseTile", this.baseTile);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        if (save.hasLoadDataByName("baseTile")) {
            this.baseTile = save.getPoint("baseTile", null);
        }
    }

    @Override
    public void init() {
        super.init();
        this.setupAI();
    }

    public void setupAI() {
        if (this.baseTile == null || this.baseTile.x == 0 && this.baseTile.y == 0) {
            this.baseTile = new Point(this.getTileX(), this.getTileY());
        }
        this.ai = new BehaviourTreeAI<PirateMob>(this, new PirateAITree(544, 5000, 40, 640, 60000));
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickShooting();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    private void tickShooting() {
        if (this.shootTime != 0L && this.getWorldEntity().getTime() > this.shootTime) {
            if (this.isServer() && this.shootTarget != null && this.isSamePlace(this.shootTarget)) {
                CollisionFilter collisionFilter = this.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), this.shootTarget);
                if (!this.getLevel().collides(new Line2D.Float(this.x, this.y, this.shootTarget.x, this.shootTarget.y), collisionFilter)) {
                    this.shootAbility.runAndSend(this.shootTarget.getX(), this.shootTarget.getY());
                }
            }
            this.shootTime = 0L;
        }
    }

    @Override
    public LootTable getLootTable() {
        if (this.isSummoned) {
            return new LootTable();
        }
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), this.getPirateTexture().body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void superAddDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PirateMob.getTileCoordinate(x), PirateMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(PirateMob.getTileCoordinate(x), PirateMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.getPirateTexture()).sprite(sprite).dir(dir).mask(swimMask).light(light);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            humanDrawOptions.armSprite(2);
            humanDrawOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
        }
        float attackProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            this.addAttackDraw(humanDrawOptions, attackProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (boat != null) {
                    boat.draw();
                }
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    protected abstract HumanTexture getPirateTexture();

    protected GameTexture getBoatTexture() {
        return MobRegistry.Textures.woodBoat;
    }

    protected void addAttackDraw(HumanDrawOptions drawOptions, float attackProgress) {
        if (this.rangedAttack) {
            this.addRangedAttackDraw(drawOptions, attackProgress);
        } else {
            this.addMeleeAttackDraw(drawOptions, attackProgress);
        }
    }

    protected void addRangedAttackDraw(HumanDrawOptions drawOptions, float attackProgress) {
        drawOptions.itemAttack(new InventoryItem("flintlock"), null, attackProgress, this.attackDir.x, this.attackDir.y);
    }

    protected void addMeleeAttackDraw(HumanDrawOptions drawOptions, float attackProgress) {
        drawOptions.itemAttack(new InventoryItem("cutlass"), null, attackProgress, this.attackDir.x, this.attackDir.y);
    }

    @Override
    public float getAttackAnimProgress() {
        float out = super.getAttackAnimProgress();
        if (!this.isAttacking) {
            this.attackAnimTime = this.normalAnimTime;
            this.attackCooldown = 500;
            this.rangedAttack = false;
        }
        return out;
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    public void shootAbilityProjectile(int x, int y) {
        if (this.isServer()) {
            Projectile p = ProjectileRegistry.getProjectile("handgunbullet", this.getLevel(), this.x, this.y, (float)x, (float)y, 500.0f, 800, new GameDamage(this.shootDamage), 50, (Mob)this);
            p.resetUniqueID(new GameRandom(x + y));
            this.getLevel().entityManager.projectiles.add(p);
        }
        this.showAttack(x, y, false);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.handgun, (SoundEffect)SoundEffect.effect(this));
        }
    }

    public void setSummoned() {
        this.isSummoned = true;
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
    }

    @Override
    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotSolidTile().checkNotLevelCollides();
    }

    @Override
    public boolean shouldSave() {
        return !this.isSummoned;
    }

    @Override
    public int getRespawnTime() {
        if (!this.isSummoned) {
            return super.getRespawnTime();
        }
        return BossMob.getBossRespawnTime(this);
    }
}

