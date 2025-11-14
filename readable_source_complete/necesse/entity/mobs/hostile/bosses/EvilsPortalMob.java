/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsPortalMob
extends BossMob {
    public static LootTable lootTable = new LootTable();
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(50, 65, 75, 85, 100);
    private long lifeTime;

    public EvilsPortalMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.isSummoned = true;
        this.collision = new Rectangle(-10, -12, 20, 20);
        this.hitBox = new Rectangle(-15, -17, 30, 30);
        this.selectBox = new Rectangle(-18, -58, 36, 58);
        this.setKnockbackModifier(0.0f);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("lifeTime", this.lifeTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lifeTime = save.getInt("lifeTime", 0);
    }

    @Override
    public void init() {
        super.init();
        this.lifeTime = 0L;
        this.ai = new BehaviourTreeAI<EvilsPortalMob>(this, new DevilGateAINode());
        if (this.getLevel() != null) {
            this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), new Color(50, 50, 50)), Particle.GType.CRITICAL);
        }
    }

    @Override
    public void tickMovement(float delta) {
        this.dx = 0.0f;
        this.dy = 0.0f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0f, 0.7f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        ++this.lifeTime;
        if (this.lifeTime > 600L) {
            this.setHealth(0);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), new Color(50, 50, 50)), Particle.GType.CRITICAL);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(EvilsPortalMob.getTileCoordinate(x), EvilsPortalMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 62;
        int offset = (int)(this.getWorldEntity().getTime() % 1600L) / 200;
        if (offset > 4) {
            offset = 4 - offset % 4;
        }
        final TextureDrawOptionsEnd options = MobRegistry.Textures.evilsProtector2.initDraw().sprite(2, 0, 64).light(light).pos(drawX, drawY + offset);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public class DevilGateAINode<T extends Mob>
    extends AINode<T> {
        private final ArrayList<Mob> spawnedMobs = new ArrayList();

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            blackboard.onRemoved(e -> this.spawnedMobs.forEach(Mob::remove));
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (EvilsPortalMob.this.lifeTime % 120L == 0L) {
                Mob portalMob = MobRegistry.getMob("portalminion", EvilsPortalMob.this.getLevel());
                EvilsPortalMob.this.getLevel().entityManager.addMob(portalMob, EvilsPortalMob.this.getX() + (int)(GameRandom.globalRandom.nextGaussian() * 3.0), EvilsPortalMob.this.getY() + (int)(GameRandom.globalRandom.nextGaussian() * 3.0));
                this.spawnedMobs.add(portalMob);
            }
            return AINodeResult.SUCCESS;
        }
    }
}

