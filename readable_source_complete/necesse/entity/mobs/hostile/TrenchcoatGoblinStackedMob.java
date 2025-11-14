/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.GameTileRange;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinScatteredMob;
import necesse.entity.projectile.TrenchcoatGoblinSpawnProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrenchcoatGoblinStackedMob
extends HostileMob {
    public static GameDamage damage = new GameDamage(20.0f);
    protected long startSwingAttackTime;
    protected int swingAttackDir;
    protected final IntMobAbility startSwingAttackAbility;
    protected final IntMobAbility fireSwingAttackAbility;
    protected int swingAttackChargeUpTime = 750;
    protected int swingAttackSlashTime = 300;
    public CameraShake swingAttackShake;

    public TrenchcoatGoblinStackedMob() {
        super(200);
        this.setSpeed(20.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 28);
        this.selectBox = new Rectangle(-16, -46, 32, 52);
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 8;
        this.startSwingAttackAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                TrenchcoatGoblinStackedMob.this.startSwingAttackTime = TrenchcoatGoblinStackedMob.this.getTime();
                TrenchcoatGoblinStackedMob.this.attackAnimTime = TrenchcoatGoblinStackedMob.this.swingAttackChargeUpTime + 500;
                TrenchcoatGoblinStackedMob.this.attackCooldown = TrenchcoatGoblinStackedMob.this.swingAttackChargeUpTime + 1000;
                TrenchcoatGoblinStackedMob.this.swingAttackDir = value;
                TrenchcoatGoblinStackedMob.this.setDir(value);
                Point vector = TrenchcoatGoblinStackedMob.this.getDirVector();
                TrenchcoatGoblinStackedMob.this.attack(TrenchcoatGoblinStackedMob.this.getX() + vector.x * 100, TrenchcoatGoblinStackedMob.this.getY() + vector.y * 100, true);
                TrenchcoatGoblinStackedMob.this.stopMoving();
            }
        });
        this.fireSwingAttackAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                TrenchcoatGoblinStackedMob.this.startSwingAttackTime = 0L;
                TrenchcoatGoblinStackedMob.this.attackAnimTime = TrenchcoatGoblinStackedMob.this.swingAttackSlashTime;
                TrenchcoatGoblinStackedMob.this.attackCooldown = TrenchcoatGoblinStackedMob.this.swingAttackSlashTime + 500;
                TrenchcoatGoblinStackedMob.this.setDir(value);
                Point vector = TrenchcoatGoblinStackedMob.this.getDirVector();
                int aimX = vector.x * 100;
                int aimY = vector.y * 100;
                TrenchcoatGoblinStackedMob.this.attack(TrenchcoatGoblinStackedMob.this.getX() + aimX, TrenchcoatGoblinStackedMob.this.getY() + aimY, true);
                if (TrenchcoatGoblinStackedMob.this.isServer()) {
                    InventoryItem attackItem = new InventoryItem("woodgreatsword");
                    attackItem.getGndData().setItem("damage", (GNDItem)new GNDItemGameDamage(damage));
                    TrenchcoatGoblinStackedMob.this.getLevel().entityManager.events.add(new ToolItemMobAbilityEvent(TrenchcoatGoblinStackedMob.this, GameRandom.globalRandom.nextInt(), attackItem, aimX, aimY, TrenchcoatGoblinStackedMob.this.swingAttackSlashTime, TrenchcoatGoblinStackedMob.this.swingAttackSlashTime));
                } else {
                    SoundManager.playSound(GameResources.woodGreatsword3, (SoundEffect)SoundEffect.effect(TrenchcoatGoblinStackedMob.this).volume(0.7f));
                }
                TrenchcoatGoblinStackedMob.this.ai.blackboard.submitEvent("confuseAfterAttack", new AIEvent());
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<TrenchcoatGoblinStackedMob>(this, new ConfusedPlayerChaserWandererAI<TrenchcoatGoblinStackedMob>(null, 384, 60, 40000, false, false){

            @Override
            public boolean attackTarget(TrenchcoatGoblinStackedMob mob, Mob target) {
                if (mob.canAttack()) {
                    mob.showAttack(target.getX(), target.getY(), true);
                    mob.startSwingAttackAbility.runAndSend(mob.getDir());
                    this.wanderAfterAttack = true;
                    return true;
                }
                return false;
            }

            @Override
            protected int getRandomConfuseTime() {
                return GameRandom.globalRandom.getIntBetween(2000, 3000);
            }
        });
    }

    @Override
    public void clientTick() {
        super.clientTick();
    }

    @Override
    public void serverTick() {
        long timeSinceStartAttack;
        super.serverTick();
        if (this.startSwingAttackTime != 0L && (timeSinceStartAttack = this.getTime() - this.startSwingAttackTime) >= (long)this.swingAttackChargeUpTime) {
            this.fireSwingAttackAbility.runAndSend(this.swingAttackDir);
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        ArrayList<Point> validSpawnTiles = new ArrayList<Point>();
        GameTileRange range = new GameTileRange(1, new Point[0]);
        for (Point tile : range.getValidTiles(this.getTileX(), this.getTileY())) {
            if (this.getLevel().isSolidTile(tile.x, tile.y)) continue;
            validSpawnTiles.add(tile);
        }
        this.shootOneGoblin(validSpawnTiles, TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType.Helmet);
        this.shootOneGoblin(validSpawnTiles, TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType.Chestplate);
        this.shootOneGoblin(validSpawnTiles, TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType.Shoes);
    }

    private void shootOneGoblin(ArrayList<Point> validSpawnTiles, TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType goblinType) {
        if (!validSpawnTiles.isEmpty()) {
            Point spawnTile = GameRandom.globalRandom.getOneOf(validSpawnTiles);
            int targetX = spawnTile.x * 32 + 16;
            int targetY = spawnTile.y * 32 + 16;
            int distance = (int)this.getDistance(targetX, targetY);
            this.getLevel().entityManager.projectiles.add(new TrenchcoatGoblinSpawnProjectile(this.getLevel(), goblinType, this, this.x, this.y, targetX, targetY, distance, new GameDamage(0.0f), 50));
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TrenchcoatGoblinStackedMob.getTileCoordinate(x), TrenchcoatGoblinStackedMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 56;
        int dir = this.getDir();
        if (this.startSwingAttackTime != 0L) {
            if (this.swingAttackShake == null || this.swingAttackShake.isOver(this.getLocalTime())) {
                this.swingAttackShake = new CameraShake(this.getLocalTime(), this.swingAttackChargeUpTime, 75, 1.5f, 1.5f, true);
            }
            Point2D.Float shake = this.swingAttackShake.getCurrentShake(this.getWorldEntity().getLocalTime());
            drawX = (int)((float)drawX + shake.x);
            drawY = (int)((float)drawY + shake.y);
        }
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(TrenchcoatGoblinStackedMob.getTileCoordinate(x), TrenchcoatGoblinStackedMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.trenchcoatgoblin_stacked).sprite(sprite).dir(dir).mask(swimMask).light(light).attackOffsets(32, 16, 10, 16, 12, 2, 14);
        if (dir == 1 || dir == 3) {
            humanDrawOptions.attackArmPosOffset(-4, 0);
        }
        float attackProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            InventoryItem showItem = new InventoryItem("woodgreatsword");
            if (this.startSwingAttackTime != 0L) {
                long timeSinceStartAttack = this.getTime() - this.startSwingAttackTime;
                float progress = GameMath.limit((float)timeSinceStartAttack / (float)this.swingAttackChargeUpTime, 0.0f, 1.0f);
                showItem.getGndData().setFloat("chargePercent", progress);
                showItem.getGndData().setBoolean("charging", true);
            }
            humanDrawOptions.itemAttack(showItem, null, attackProgress, this.attackDir.x, this.attackDir.y);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                drawOptions.draw();
                swimMask.stop();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public int getRockSpeed() {
        return 16;
    }

    @Override
    public float getAttackingMovementModifier() {
        return 0.0f;
    }

    @Override
    public LootTable showAdditionalLootTableInJournal() {
        return TrenchcoatGoblinScatteredMob.lootTable;
    }
}

