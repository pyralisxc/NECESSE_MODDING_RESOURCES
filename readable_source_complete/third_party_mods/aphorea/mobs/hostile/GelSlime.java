/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobSpawnLocation
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserAI
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.ActiveBuff
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
 *  necesse.level.maps.Level
 *  necesse.level.maps.levelBuffManager.LevelModifiers
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphData;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
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
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class GelSlime
extends HostileMob {
    public static GameDamage attack = new GameDamage(25.0f);
    public static int attack_knockback = 50;
    public static GameTexture texture;
    public static LootTable lootTable;
    public boolean turnPhosphor;

    public GelSlime() {
        super(60);
        this.setSpeed(30.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -21, 28, 28);
    }

    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        if (client == null) {
            return false;
        }
        return !AphData.gelSlimesNulled(client.getLevel().getWorldEntity()) && !client.getLevel().getWorldEntity().isNight() && new MobSpawnLocation((Mob)this, targetX, targetY).checkMobSpawnLocation().checkMaxHostilesAround(4, 8, client).validAndApply();
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, new GelSlimeAI(() -> this.getServer().world.worldEntity.isNight(), 128, attack, attack_knockback, 40000));
        this.turnPhosphor = GameRandom.globalRandom.getChance(0.03f);
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("turnPhosphor", this.turnPhosphor);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.turnPhosphor = save.getBoolean("turnPhosphor", false, false);
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

    public int getRockSpeed() {
        return 20;
    }

    public void collidedWith(Mob other) {
        super.collidedWith(other);
        if (this.isServer()) {
            other.addBuff(new ActiveBuff(AphBuffs.STICKY, other, 1000, (Attacker)this), true);
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.turnPhosphor && this.getLevel().getWorldEntity().isNight()) {
            Mob phosphor = MobRegistry.getMob((String)"wildphosphorslime", (Level)this.getLevel());
            this.getLevel().entityManager.addMob(phosphor, this.x, this.y);
            this.remove();
        }
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        if (attacker != null && attacker.getAttackOwner() != null && Arrays.stream(attackers.toArray()).noneMatch(a -> ((Attacker)a).getAttackOwner() != null && ((Attacker)a).getAttackOwner().isPlayer)) {
            this.dropsLoot = false;
        }
        super.onDeath(attacker, attackers);
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{ChanceLootItem.between((float)0.8f, (String)"gelball", (int)1, (int)2), ChanceLootItem.between((float)0.05f, (String)"unstablecore", (int)1, (int)1), ChanceLootItem.between((float)0.02f, (String)"gelring", (int)1, (int)1)});
    }

    public static class GelSlimeAI<T extends Mob>
    extends SelectorAINode<T> {
        public final EscapeAINode<T> escapeAINode;
        public final CollisionOnlyPlayerChaserAI<T> collisionPlayerChaserAI;
        public final WandererAINode<T> wandererAINode;

        public GelSlimeAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
            this.escapeAINode = new EscapeAINode<T>(){

                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    if (((Mob)mob).isHostile && !((Mob)mob).isSummoned && ((Boolean)mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)).booleanValue()) {
                        return true;
                    }
                    return shouldEscape != null && (Boolean)shouldEscape.get() != false;
                }
            };
            this.addChild((AINode)this.escapeAINode);
            this.collisionPlayerChaserAI = new CollisionOnlyPlayerChaserAI(searchDistance, damage, knockback);
            this.addChild((AINode)this.collisionPlayerChaserAI);
            this.wandererAINode = new WandererAINode(wanderFrequency);
            this.addChild((AINode)this.wandererAINode);
        }
    }

    public static class CollisionOnlyPlayerChaserAI<T extends Mob>
    extends CollisionChaserAI<T> {
        public CollisionOnlyPlayerChaserAI(int searchDistance, GameDamage damage, int knockback) {
            super(searchDistance, damage, knockback);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
            return TargetFinderAINode.streamPlayers(mob, (Point)base, distance).map(playerMob -> playerMob);
        }
    }
}

