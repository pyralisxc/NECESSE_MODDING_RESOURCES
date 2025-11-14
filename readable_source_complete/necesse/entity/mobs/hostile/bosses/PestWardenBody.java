/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobBody;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.entity.mobs.hostile.bosses.PestWardenMoveLine;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PestWardenBody
extends BossWormMobBody<PestWardenHead, PestWardenBody> {
    public Point sprite = new Point(0, 0);
    public boolean showLegs;
    public int shadowSprite = 0;
    public int index;
    private final TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(30);
    public GameDamage collisionDamage;

    public PestWardenBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-35, -20, 70, 40);
        this.hitBox = new Rectangle(-40, -25, 80, 50);
        this.selectBox = new Rectangle(-50, -80, 100, 100);
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "pestwarden");
    }

    @Override
    public void init() {
        super.init();
        this.collisionDamage = this.getLevel() instanceof IncursionLevel ? PestWardenHead.baseBodyCollisionDamage : PestWardenHead.incursionBodyCollisionDamage;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();
            while (this.particleSpawner.shouldTick()) {
                ComputedValue<GameObject> obj = new ComputedValue<GameObject>(() -> this.getLevel().getObject(this.getTileX(), this.getTileY()));
                if (this.height < 20.0f && (obj.get().isWall || obj.get().isRock)) {
                    this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 10.0f + 5.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).smokeColor().heightMoves(10.0f, GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(200);
                    continue;
                }
                if (!(this.height < 0.0f)) continue;
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 10.0f + 5.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).smokeColor().heightMoves(10.0f, GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(200);
            }
        }
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)));
    }

    @Override
    public float getIncomingDamageModifier() {
        if (this.moveLine != null && this.moveLine.object instanceof PestWardenMoveLine) {
            return super.getIncomingDamageModifier() * (((PestWardenMoveLine)this.moveLine.object).isHardened ? 0.1f : 1.0f);
        }
        return super.getIncomingDamageModifier();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (!this.isVisible()) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.pestWarden, 8 + GameRandom.globalRandom.nextInt(6), 4, 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected void addExtraDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, final int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DrawOptions legs;
        super.addExtraDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y);
        Point2D.Float moveDir = null;
        if (this.moveLine != null) {
            ComputedObjectValue<GameLinkedList.Element, Double> dist = WormMobHead.moveDistance(this.moveLine, -40.0);
            if (dist.object != null) {
                Point2D.Double pos = WormMobHead.linePos(dist);
                moveDir = GameMath.normalize((float)pos.x - (float)x, (float)pos.y - (float)y);
            }
        }
        if (moveDir == null) {
            PestWardenHead head = (PestWardenHead)this.master.get(this.getLevel());
            moveDir = head != null ? GameMath.normalize(head.dx, head.dy) : GameMath.normalize(this.dx, this.dy);
        }
        float threshold = 0.7f;
        int dir = Math.abs(moveDir.x) - Math.abs(moveDir.y) <= threshold ? (moveDir.y < 0.0f ? 0 : 2) : (moveDir.x < 0.0f ? 3 : 1);
        final MobDrawable body = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, this.sprite.x, this.sprite.y, 128), MobRegistry.Textures.pestWarden_mask, light, (int)this.height, drawX, drawY, 112);
        if (this.showLegs) {
            PestWardenHead head = (PestWardenHead)this.master.get(this.getLevel());
            float speed = head != null ? head.getSpeed() : 100.0f;
            int animTime = (int)(400.0f * (100.0f / speed));
            int sprite1 = GameUtils.getAnim(this.getWorldEntity().getLocalTime(), 8, animTime);
            sprite1 = (sprite1 + this.index) % 8;
            int sprite2 = (sprite1 + 1) % 8;
            float split = PestWardenHead.lengthPerBodyPart / 4.0f;
            int legXOffset = (int)(moveDir.x * split);
            int legYOffset = (int)(moveDir.y * split);
            TextureDrawOptionsEnd leg1 = MobRegistry.Textures.pestWarden.initDraw().sprite(sprite1, 3 + dir, 128).light(light).pos(drawX + legXOffset, drawY - 96 - 16 + 8 + legYOffset);
            TextureDrawOptionsEnd leg2 = MobRegistry.Textures.pestWarden.initDraw().sprite(sprite2, 3 + dir, 128).light(light).pos(drawX - legXOffset, drawY - 96 - 16 + 8 - legYOffset);
            legs = () -> {
                leg1.draw();
                leg2.draw();
            };
        } else {
            legs = null;
        }
        if (dir == 0 || dir == 2) {
            list.add(new LevelSortedDrawable(this){

                @Override
                public int getSortY() {
                    return y;
                }

                @Override
                public void draw(TickManager tickManager) {
                    body.draw(tickManager);
                    if (legs != null) {
                        legs.draw();
                    }
                }
            });
        } else {
            list.add(new LevelSortedDrawable(this){

                @Override
                public int getSortY() {
                    return y;
                }

                @Override
                public void draw(TickManager tickManager) {
                    body.draw(tickManager);
                }
            });
            if (legs != null) {
                list.add(new LevelSortedDrawable(this){

                    @Override
                    public int getSortY() {
                        return y + 10;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        legs.draw();
                    }
                });
            }
        }
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.pestWarden_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.shadowSprite, 0, res).light(light).pos(drawX, (drawY += this.getBobbing(x, y)) - 40);
    }
}

