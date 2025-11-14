/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.DifficultyBasedGetter
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.MaxHealthGetter
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI
 *  necesse.entity.mobs.hostile.FlyingHostileMob
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
 *  necesse.inventory.lootTable.lootItem.RotationLootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.projectiles.mob.PinkWitchProjectile;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.DifficultyBasedGetter;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.hostile.FlyingHostileMob;
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
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PinkWitch
extends FlyingHostileMob {
    public static GameDamage attack = new GameDamage(DamageTypeRegistry.MAGIC, 30.0f);
    public static int attack_knockback = 50;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(400, 500, 600, 700, 800);
    public int threeAttack = 5;
    public static GameTexture texture;
    public static LootTable lootTable;

    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        if (client.getLevel().entityManager.objectEntities.streamAreaTileRange(targetX, targetY, 100).anyMatch(oe -> Objects.equals(oe.type, "witchstatue"))) {
            return false;
        }
        return super.isValidSpawnLocation(server, client, targetX, targetY);
    }

    public PinkWitch() {
        super(600);
        this.difficultyChanges.setMaxHealth((DifficultyBasedGetter)MAX_HEALTH);
        this.setSpeed(60.0f);
        this.setFriction(1.0f);
        this.collision = new Rectangle(-18, -14, 36, 32);
        this.hitBox = new Rectangle(-26, -22, 52, 48);
        this.selectBox = new Rectangle(-26, -42, 52, 68);
    }

    public void init() {
        super.init();
        PlayerChaserWandererAI<PinkWitch> playerChaserAI = new PlayerChaserWandererAI<PinkWitch>(null, 768, 320, 4000, true, true){

            public boolean attackTarget(PinkWitch mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.getLevel().entityManager.projectiles.add((Entity)new PinkWitchProjectile(mob.getLevel(), (Mob)mob, mob.x, mob.y, target.x, target.y, 120.0f, 640, attack, attack_knockback));
                    if (mob.isServer()) {
                        --PinkWitch.this.threeAttack;
                    }
                    if (PinkWitch.this.threeAttack >= 0) {
                        PinkWitch.this.attackCooldown = (int)(500.0f + 1000.0f * mob.getHealthPercent());
                    } else {
                        PinkWitch.this.attackCooldown = (int)(100.0f + 200.0f * mob.getHealthPercent());
                        if (PinkWitch.this.threeAttack == -2 && mob.isServer()) {
                            PinkWitch.this.threeAttack = 5;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)playerChaserAI);
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, GameRandom.globalRandom.nextInt(8), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
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
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public int getRockSpeed() {
        return 20;
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"stardust", (int)2, (int)3), RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{ChanceLootItem.between((String)"healthpotion", (int)1, (int)2), ChanceLootItem.between((String)"manapotion", (int)1, (int)2)}), RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("broom"), new LootItem("broom"), new LootItem("witchmedallion"), new LootItem("pinkwitchhat"), new ChanceLootItem(0.25f, "magicalvial")})});
    }
}

