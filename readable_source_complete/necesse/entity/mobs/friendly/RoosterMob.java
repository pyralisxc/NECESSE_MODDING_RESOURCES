/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.GameTileRange;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ItemCostList;
import necesse.entity.Entity;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.HusbandryImpregnateWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.ChickenMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.misc.MobProcessObjectHandler;
import necesse.entity.mobs.misc.StartMobProcessObjectMobAbility;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.HumanGender;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.EggNestObjectInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ProcessObjectHandler;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RoosterMob
extends HusbandryMob {
    public static GameTileRange FERTILIZE_EGG_TILE_RANGE = new GameTileRange(15, new Point[0]);
    public static LootTable lootTable = new LootTable(LootItem.between("rawchickenleg", 2, 3));
    protected MobProcessObjectHandler fertilizeEggHandler = new MobProcessObjectHandler(this){

        @Override
        public void tickInProgress() {
            super.tickInProgress();
            if (!RoosterMob.this.isServer() && GameRandom.globalRandom.nextInt(20) == 0) {
                RoosterMob.this.spawnFertilizeEggParticles();
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            if (!RoosterMob.this.isClient() && this.target != null && this.target.canProcess()) {
                this.target.process();
                RoosterMob.this.refreshBirthingCooldown();
                RoosterMob.this.ai.blackboard.submitEvent("wanderNow", new AIEvent());
            }
            if (!RoosterMob.this.isServer()) {
                for (int i = 0; i < 5; ++i) {
                    RoosterMob.this.spawnFertilizeEggParticles();
                }
            }
        }
    };
    public StartMobProcessObjectMobAbility fertilizeEggAbility;

    public RoosterMob() {
        super(50);
        this.setSpeed(12.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-12, -9, 24, 18);
        this.hitBox = new Rectangle(-16, -12, 32, 24);
        this.selectBox = new Rectangle(-18, -30, 36, 36);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
        this.timeToGrowUp = 600000;
        this.fertilizeEggAbility = this.registerAbility(new StartMobProcessObjectMobAbility(this.fertilizeEggHandler));
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<RoosterMob>(this, new RoosterAI(30000), new AIMover());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.fertilizeEggHandler.tick();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.fertilizeEggHandler.tick();
    }

    public void spawnFertilizeEggParticles() {
        if (this.fertilizeEggHandler.isTargetOnTile) {
            ChickenMob.spawnEggParticles(this.getLevel(), this.fertilizeEggHandler.tileX, this.fertilizeEggHandler.tileY);
        } else {
            ChickenMob.spawnEggParticles(this.getLevel(), this.x + (float)GameRandom.globalRandom.getIntBetween(-10, 10), this.y + (float)GameRandom.globalRandom.getIntBetween(-5, 5));
        }
    }

    @Override
    public LootTable getLootTable() {
        if (!this.isGrown()) {
            return new LootTable();
        }
        return lootTable;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.roosterAmbient);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.roosterHurt);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.roosterDeath).volume(0.3f);
    }

    @Override
    public void setDefaultBuyPrice(GameRandom random) {
        this.buyPrice = new ItemCostList();
        this.buyPrice.addItem("coin", random.getIntBetween(500, 600));
    }

    @Override
    public GameMessage getLocalization() {
        if (this.isGrown()) {
            return super.getLocalization();
        }
        return new LocalMessage("mob", "chick");
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameTexture texture = this.getTexture();
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), texture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(RoosterMob.getTileCoordinate(x), RoosterMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        TextureDrawOptionsEnd shadow = this.getShadowTexture().initDraw().sprite(0, dir, 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
        tileList.add(tm -> shadow.draw());
        float eggProgress = this.fertilizeEggHandler.getProgressPercent();
        final MaskShaderOptions swimMask = eggProgress > 0.0f ? this.getSwimMaskShaderOptions(Math.min(eggProgress * 2.0f, 0.7f)) : this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = this.getTexture().initDraw().sprite(sprite.x, sprite.y, 64).light(light).addMaskShader(swimMask).pos(drawX, drawY += level.getTile(RoosterMob.getTileCoordinate(x), RoosterMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
    }

    private GameTexture getTexture() {
        if (this.isGrown()) {
            return MobRegistry.Textures.rooster;
        }
        return MobRegistry.Textures.chick;
    }

    private GameTexture getShadowTexture() {
        if (this.isGrown()) {
            return MobRegistry.Textures.rooster_shadow;
        }
        return MobRegistry.Textures.chick_shadow;
    }

    @Override
    protected int getRockSpeed() {
        if (this.isGrown()) {
            return 10;
        }
        return 7;
    }

    @Override
    public HumanGender getGender() {
        return HumanGender.MALE;
    }

    @Override
    public boolean canImpregnateMob(HusbandryMob other) {
        return other.getStringID().equals("chicken");
    }

    public boolean isFertilizingEgg() {
        return this.fertilizeEggHandler.isInProgress();
    }

    public static class RoosterAI<T extends RoosterMob>
    extends HusbandryImpregnateWandererAI<T> {
        public final ChickenFertilizeEggAINode<T> fertilizeEggNode = new ChickenFertilizeEggAINode();

        public RoosterAI(int wanderFrequency) {
            super(wanderFrequency);
            this.addChildFirst(this.fertilizeEggNode);
        }
    }

    public static class ChickenFertilizeEggAINode<T extends RoosterMob>
    extends MoveTaskAINode<T> {
        public long nextCheckTime;
        public ProcessObjectHandler target;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.target != null && this.target.reservable != null) {
                this.target.reservable.reserve((Entity)mob);
            }
            return super.tick(mob, blackboard);
        }

        @Override
        public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                return AINodeResult.SUCCESS;
            }
            if (this.target == null && ((HusbandryMob)mob).canImpregnate() && !((HusbandryMob)mob).isOnBirthingCooldown() && this.nextCheckTime <= mob.getTime()) {
                this.nextCheckTime = mob.getTime() + (long)GameRandom.globalRandom.getIntBetween(20, 30) * 1000L;
                Point baseTile = new Point(((Entity)mob).getTileX(), ((Entity)mob).getTileY());
                GameTileRange range = FERTILIZE_EGG_TILE_RANGE;
                ArrayList<ProcessObjectHandler> validTiles = new ArrayList<ProcessObjectHandler>();
                Point pathOffset = ((Mob)mob).getPathMoveOffset();
                for (Point tile : range.getValidTiles(baseTile.x, baseTile.y)) {
                    ProcessObjectHandler handler;
                    GameObject object = ((Entity)mob).getLevel().getObject(tile.x, tile.y);
                    if (((Entity)mob).getLevel().isSolidTile(tile.x, tile.y) || !(object instanceof EggNestObjectInterface) || ((Mob)mob).collidesWith(((Entity)mob).getLevel(), tile.x * 32 + pathOffset.x, tile.y * 32 + pathOffset.y) || !((Mob)mob).estimateCanMoveTo(tile.x, tile.y, false) || (handler = ((EggNestObjectInterface)((Object)object)).getFertilizeEggHandler(((Entity)mob).getLevel(), tile.x, tile.y)) == null || !handler.canProcess() || handler.reservable != null && !handler.reservable.isAvailable((Entity)mob)) continue;
                    validTiles.add(handler);
                }
                if (!validTiles.isEmpty()) {
                    this.target = (ProcessObjectHandler)GameRandom.globalRandom.getOneOf(validTiles);
                    if (this.target.reservable != null) {
                        this.target.reservable.reserve((Entity)mob);
                    }
                    return this.moveToTileTask(this.target.tileX, this.target.tileY, null, path -> {
                        if (path.moveIfWithin(-1, 0, null)) {
                            return AINodeResult.SUCCESS;
                        }
                        return AINodeResult.FAILURE;
                    });
                }
            }
            if (this.target != null && this.target.isValid()) {
                if (this.target.canProcess() && !((RoosterMob)mob).isFertilizingEgg() && ((HusbandryMob)mob).canImpregnate() && GameMath.diagonalMoveDistance(this.target.tileX, this.target.tileY, ((Entity)mob).getTileX(), ((Entity)mob).getTileY()) <= 1.0) {
                    ((RoosterMob)mob).fertilizeEggAbility.runAndSend(this.target);
                }
                if (((RoosterMob)mob).isFertilizingEgg()) {
                    return AINodeResult.SUCCESS;
                }
                this.target = null;
                return AINodeResult.SUCCESS;
            }
            this.target = null;
            return AINodeResult.FAILURE;
        }
    }
}

