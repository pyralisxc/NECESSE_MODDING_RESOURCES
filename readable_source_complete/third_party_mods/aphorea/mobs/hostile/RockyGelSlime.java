/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.projectiles.mob.RockyGelSlimeLootProjectile;
import aphorea.projectiles.mob.RockyGelSlimeProjectile;
import aphorea.registry.AphBiomes;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RockyGelSlime
extends HostileMob {
    public static GameDamage collision_damage = new GameDamage(30.0f);
    public static int collision_knockback = 50;
    public static GameDamage rock_damage = new GameDamage(15.0f);
    public static GameDamage rock_damage_if = new GameDamage(30.0f);
    public static int rock_knockback = 25;
    public static GameTexture texture;
    public static LootTable lootTable;

    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return super.isValidSpawnLocation(server, client, targetX, targetY);
    }

    public RockyGelSlime() {
        super(220);
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-15, -6, 30, 14);
        this.hitBox = new Rectangle(-26, -16, 52, 28);
        this.selectBox = new Rectangle(-26, -27, 52, 39);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI(null, 384, collision_damage, collision_knockback, 40000));
        if (this.getLevel().baseBiome == AphBiomes.INFECTED_FIELDS) {
            this.setSpeed(this.getSpeed() * 1.4f);
        }
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
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

    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        MobWasHitEvent eventResult = super.isHit(event, attacker);
        if (this.isServer()) {
            if (eventResult != null && attacker != null && attacker.getAttackOwner() != null && !eventResult.wasPrevented && eventResult.damage < this.getHealth()) {
                Mob attackOwner = attacker.getAttackOwner();
                this.throwRock(attackOwner.getX(), attackOwner.getY(), false);
            }
            this.throwRock(GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2), false);
        }
        return eventResult;
    }

    public int getRockSpeed() {
        return 20;
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (this.isServer()) {
            float initialAngle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
            int projectiles = 10;
            for (int i = 0; i < projectiles; ++i) {
                float angle = initialAngle + (float)(i * 2) * (float)Math.PI / (float)projectiles;
                boolean dropRockyGel = i == 0 || GameRandom.globalRandom.getChance(0.25f);
                this.throwRock(angle + GameRandom.globalRandom.getFloatBetween(-0.08726647f, 0.08726647f), dropRockyGel);
            }
        }
    }

    public void throwRock(float angle, boolean dropRockyGel) {
        int targetX = this.getX() + (int)(Math.cos(angle) * 100.0);
        int targetY = this.getY() + (int)(Math.sin(angle) * 100.0);
        this.throwRock(targetX, targetY, dropRockyGel);
    }

    public void throwRock(int targetX, int targetY, boolean dropRockyGel) {
        float speed = GameRandom.globalRandom.getFloatBetween(40.0f, 50.0f);
        GameDamage damage = this.getLevel().baseBiome == AphBiomes.INFECTED_FIELDS ? rock_damage_if : rock_damage;
        RockyGelSlimeProjectile projectile = dropRockyGel ? new RockyGelSlimeLootProjectile((Mob)this, this.x, this.y, targetX, targetY, speed, 640, damage, rock_knockback) : new RockyGelSlimeProjectile((Mob)this, this.x, this.y, targetX, targetY, speed, 640, damage, rock_knockback);
        this.getLevel().entityManager.projectiles.add((Entity)projectile);
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{new LootItem("rockygel", 0), ChanceLootItem.between((float)0.05f, (String)"unstablecore", (int)1, (int)1)});
    }
}

