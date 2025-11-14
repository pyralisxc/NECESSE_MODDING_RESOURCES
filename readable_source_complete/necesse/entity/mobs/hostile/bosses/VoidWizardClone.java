/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.VoidWizardCloneProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardClone
extends BossMob {
    protected Mob original;
    protected long despawnTime;

    public VoidWizardClone() {
        super(100);
        this.isSummoned = true;
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -15, 36, 30);
        this.selectBox = new Rectangle(-18, -41, 36, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -8;
        this.swimSinkOffset = 0;
        this.shouldSave = false;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.original == null) {
            writer.putNextInt(-1);
        } else {
            writer.putNextInt(this.original.getUniqueID());
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int uID = reader.getNextInt();
        this.original = uID == -1 ? null : GameUtils.getLevelMob(uID, this.getLevel());
    }

    public void setOriginal(VoidWizard original) {
        this.original = original;
        this.setMaxHealth(original.getMaxHealthFlat());
        this.setHealth(original.getHealth());
    }

    @Override
    public void init() {
        super.init();
        this.despawnTime = this.getWorldEntity().getTime() + 2000L;
    }

    public void moveToPos(int tileX, int tileY) {
        this.ai = new BehaviourTreeAI<VoidWizardClone>(this, new CloneAINode(tileX, tileY));
        this.despawnTime = -1L;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.despawnTime > 0L && this.despawnTime <= this.getWorldEntity().getTime()) {
            this.remove();
        }
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (this.isServer() && !event.wasPrevented && this.original != null) {
            if (this.original instanceof VoidWizard && ((VoidWizard)this.original).canAddProjectile()) {
                VoidWizardCloneProjectile p = new VoidWizardCloneProjectile(this.getLevel(), this.x, this.y, this.original, VoidWizard.cloneProjectile);
                this.getLevel().entityManager.projectiles.add(p);
                ((VoidWizard)this.original).addProjectile(p);
            }
            this.remove();
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), 80, VoidWizard.getWizardColor(this.original)), Particle.GType.CRITICAL);
    }

    @Override
    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), 80, VoidWizard.getWizardColor(this.original)), Particle.GType.CRITICAL);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(VoidWizardClone.getTileCoordinate(x), VoidWizardClone.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final DrawOptions drawOptions = new HumanDrawOptions(level, MobRegistry.Textures.voidWizard).sprite(sprite).mask(swimMask).dir(dir).light(light).pos(drawX, drawY += level.getTile(VoidWizardClone.getTileCoordinate(x), VoidWizardClone.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.voidWizard_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 5;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    @Override
    public boolean isHealthBarVisible() {
        if (this.original != null) {
            return this.original.isHealthBarVisible();
        }
        return super.isHealthBarVisible();
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-8, -22, 16, 25);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 26;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
        new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.voidWizard).sprite(sprite).dir(dir).size(32, 32).draw(drawX, drawY);
    }

    public static class CloneAINode<T extends Mob>
    extends MoveTaskAINode<T> {
        public final int tileX;
        public final int tileY;
        public boolean hasStartedMoving;

        public CloneAINode(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.moveToTileTask(this.tileX, this.tileY, null, path -> {
                this.hasStartedMoving = true;
                path.moveIfWithin(-1, -1, null);
                return AINodeResult.SUCCESS;
            });
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
            if (this.hasStartedMoving && !blackboard.mover.isMoving()) {
                ((Mob)mob).remove();
            }
            return AINodeResult.SUCCESS;
        }
    }
}

