/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI
 *  necesse.entity.mobs.ai.behaviourTree.util.AIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.hostile.FlyingHostileMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.bosses.minions;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MiniUnstableGelSlime
extends FlyingHostileMob {
    int escape;
    boolean initialTP = false;
    int countTP;
    public static GameDamage attack = new GameDamage(20.0f);
    public static int attack_knockback = 50;
    public static GameTexture texture;
    public static LootTable lootTable;

    public void setInitialTP(Boolean initialTP) {
        this.initialTP = initialTP;
    }

    public MiniUnstableGelSlime() {
        super(60);
        this.setSpeed(40.0f);
        this.setFriction(2.0f);
        this.staySmoothSnapped = true;
        this.escape = 0;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -21, 28, 28);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI(null, 32768, attack, attack_knockback, 40000), (AIMover)new FlyingAIMover());
        this.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, (Mob)this, 500, (Attacker)this), true);
        if (GameRandom.globalRandom.getChance(0.5f) && this.initialTP) {
            this.executeTeleport();
        } else if (this.isClient()) {
            this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(this.getLevel(), this.x, this.y, AphColors.unstableGel), Particle.GType.CRITICAL);
        }
        this.countTP = 0;
    }

    public GameAreaStream<Mob> streamPossibleTargets(Point base, TargetFinderDistance<MiniUnstableGelSlime> distance) {
        return distance.streamPlayersInRange(base, (Mob)this).filter(m -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map(m -> m);
    }

    public Mob getRandomTarget(Point base, TargetFinderDistance<MiniUnstableGelSlime> distance) {
        ArrayList list = new ArrayList();
        this.streamPossibleTargets(base, distance).forEach(list::add);
        return (Mob)GameRandom.globalRandom.getOneOf(list);
    }

    public void executeTeleport() {
        Mob tpTarget;
        if (!this.removed() && (tpTarget = this.getRandomTarget(new Point((int)this.x, (int)this.y), (TargetFinderDistance<MiniUnstableGelSlime>)new TargetFinderDistance(32768))) != null) {
            float distance = 200.0f;
            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f);
            float xExtra = (float)(Math.cos(angle) * (double)distance);
            float yExtra = (float)(Math.sin(angle) * (double)distance);
            if (this.isClient()) {
                this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(this.getLevel(), this.x, this.y, AphColors.unstableGel), Particle.GType.CRITICAL);
            }
            this.setPos(tpTarget.x + xExtra, tpTarget.y + yExtra, true);
        }
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture == null ? GameTexture.fromFile((String)"mobs/miniunstablegelslime") : texture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).alpha(this.buffManager.hasBuff(BuffRegistry.INVULNERABLE_ACTIVE) ? 0.6f : 1.0f).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
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

    public int getRockSpeed() {
        return 20;
    }

    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 500, (Attacker)this), true);
    }

    public void serverTick() {
        super.serverTick();
        if (this.getLevel().getWorldEntity().isNight()) {
            this.remove();
        } else if (GameUtils.streamServerClients((Level)this.getLevel()).anyMatch(c -> !c.isDead() && !c.playerMob.removed() && c.playerMob.getDistance((Mob)this) < 1280.0f)) {
            this.escape = 0;
        } else if (this.escape >= 100) {
            this.remove();
        } else {
            ++this.escape;
        }
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{ChanceLootItem.between((float)0.3f, (String)"unstablegel", (int)1, (int)1)});
    }
}

