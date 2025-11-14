/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SlimeGreatbowEvent
extends GroundEffectEvent {
    protected final GroundPillarList<SlimePillar> pillars = new GroundPillarList();
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns;
    protected GameDamage damage;
    protected float resilienceGain;
    protected int knockback;
    protected int eventDuration = 6000;
    protected Rectangle hitbox;

    public SlimeGreatbowEvent() {
    }

    public SlimeGreatbowEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage, float resilienceGain, int knockback) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.resilienceGain = resilienceGain;
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        this.hitbox = new Rectangle(this.x - 50, this.y - 50, 100, 100);
        RayLinkedList<Object> raysHitList = new RayLinkedList();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MAX_VALUE;
        int maxY = Integer.MAX_VALUE;
        int[] arrayOfSlimes = new int[]{4, 5, 5, 6, 5, 5, 4};
        Point2D.Float[] hitboxBounds = new Point2D.Float[]{new Point2D.Float(this.hitbox.x + this.hitbox.width, (float)this.hitbox.getCenterY()), new Point2D.Float((float)this.hitbox.getCenterX(), this.hitbox.y + this.hitbox.height), new Point2D.Float(this.hitbox.x, (float)this.hitbox.getCenterY()), new Point2D.Float((float)this.hitbox.getCenterX(), this.hitbox.y)};
        block6: for (int i = 0; i < hitboxBounds.length; ++i) {
            Point2D.Float normalizedVector = GameMath.normalize(hitboxBounds[i].x - (float)this.hitbox.getCenterX(), hitboxBounds[i].y - (float)this.hitbox.getCenterY());
            raysHitList = GameUtils.castRay(this.level, (double)((float)this.hitbox.getCenterX()), (double)((float)this.hitbox.getCenterY()), (double)normalizedVector.x, (double)normalizedVector.y, hitboxBounds[i].distance((float)this.hitbox.getCenterX(), (float)this.hitbox.getCenterY()), 0, new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock));
            if (raysHitList.isEmpty()) continue;
            Ray first = (Ray)raysHitList.getFirst();
            if (first.targetHit == null) continue;
            switch (i) {
                case 0: {
                    maxX = (int)first.x2;
                    continue block6;
                }
                case 1: {
                    maxY = (int)first.y2;
                    continue block6;
                }
                case 2: {
                    minX = (int)first.x2;
                    continue block6;
                }
                case 3: {
                    minY = (int)first.y2;
                }
            }
        }
        if (minX == Integer.MAX_VALUE) {
            minX = this.hitbox.x;
        }
        if (maxX == Integer.MAX_VALUE) {
            maxX = this.hitbox.x + this.hitbox.width;
        }
        if (minY == Integer.MAX_VALUE) {
            minY = this.hitbox.y;
        }
        if (maxY == Integer.MAX_VALUE) {
            maxY = this.hitbox.y + this.hitbox.height;
        }
        this.hitbox = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        if (this.isClient()) {
            GameRandom random = new GameRandom(this.getUniqueID());
            this.level.entityManager.addPillarHandler(new GroundPillarHandler<SlimePillar>(this.pillars){

                @Override
                protected boolean canRemove() {
                    return SlimeGreatbowEvent.this.isOver();
                }

                @Override
                public double getCurrentDistanceMoved() {
                    return 0.0;
                }
            });
            int yOffset = 15;
            int xOffset = 8;
            float distancePerRow = ((float)this.hitbox.height - (float)yOffset) / (float)(arrayOfSlimes.length - 1);
            for (int i = 0; i < arrayOfSlimes.length; ++i) {
                int particlesInThisRow = arrayOfSlimes[i];
                float rowY = (float)(this.hitbox.y + yOffset) + distancePerRow * (float)i;
                float distancePerColumn = ((float)this.hitbox.width - (float)xOffset) / (float)(particlesInThisRow - 1);
                for (int j = 0; j < particlesInThisRow; ++j) {
                    float rowX = (float)(this.hitbox.x + xOffset) + distancePerColumn * (float)j;
                    int rndX = random.getIntBetween(-xOffset / 3, xOffset / 3);
                    int rndY = random.getIntBetween(-yOffset / 2, yOffset / 2);
                    Point2D.Float targetPoints = new Point2D.Float(rowX + (float)rndX, rowY + (float)rndY);
                    this.pillars.add(new SlimePillar((int)targetPoints.x, (int)targetPoints.y, 0.0, this.getLocalTime(), this.eventDuration));
                }
            }
        }
        if (!this.isClient()) {
            GameUtils.streamTargets(this.owner, this.hitbox).filter(m -> m.canBeHit(this) && m.getHitBox().intersects(this.hitbox)).forEach(m -> {
                m.isServerHit(this.damage, m.x - (float)this.x, m.y - (float)this.y, this.knockback, this);
                if (m.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
                    this.owner.addResilience(this.resilienceGain);
                    this.resilienceGain = 0.0f;
                }
            });
        }
    }

    @Override
    public Shape getHitBox() {
        return this.hitbox;
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_GREATBOW_SLOW_TARGET_DEBUFF, target, 1.0f, (Attacker)this.owner), true);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_GREATBOW_SLOW_TARGET_DEBUFF, target, 1.0f, (Attacker)this.owner), true);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 20 * (this.eventDuration / 1000)) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 20 * (this.eventDuration / 1000)) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    public static class SlimePillar
    extends GroundPillar {
        public GameTextureSection texture = null;
        public boolean mirror = GameRandom.globalRandom.nextBoolean();

        public SlimePillar(int x, int y, double spawnDistance, long spawnTime, int duration) {
            super(x, y, spawnDistance, spawnTime);
            GameTexture pillarSprites = GameResources.slimeGreatbowSlime;
            if (pillarSprites != null) {
                int res = pillarSprites.getHeight();
                int sprite = GameRandom.globalRandom.nextInt(pillarSprites.getWidth() / res);
                this.texture = new GameTextureSection(GameResources.slimeGreatbowSlime).sprite(sprite, 0, res);
            }
            this.behaviour = new GroundPillar.TimedBehaviour(duration, 0, 250);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY + 5);
        }
    }
}

