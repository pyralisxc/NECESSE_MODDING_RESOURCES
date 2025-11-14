/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.ArcanicPylonLightningLevelEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EmptyAINode;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ArcanicPylonMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("electrifiedmana", 1, 2), ChanceLootItem.between(0.05f, "ironbar", 1, 2), ChanceLootItem.between(0.05f, "wire", 1, 2));
    private long nextFireTime;
    private final long chargeUpTime = 1000L;
    private final long cooldownTime = 500L;
    private final EmptyMobAbility resetShootCooldown;

    public ArcanicPylonMob() {
        super(350);
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
        this.setArmor(35);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.resetShootCooldown = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                ArcanicPylonMob.this.nextFireTime = ArcanicPylonMob.this.getTime();
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<ArcanicPylonMob>(this, new EmptyAINode());
        if (this.isServer()) {
            this.resetShootCooldown.runAndSend();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        PlayerMob target = this.getTargetInRange();
        if (this.readyToFire() && target != null) {
            this.getLevel().entityManager.addLevelEvent(new ArcanicPylonLightningLevelEvent(this, 300, 125, target.getPositionPoint()));
            this.resetShootCooldown.runAndSend();
        } else if (this.readyToFire() || target == null) {
            this.resetShootCooldown.runAndSend();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.chargingUp() && this.getTargetInRange() != null) {
            for (int i = 0; i < 4; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                float startX = (float)this.getX() + dir.x * range;
                float startY = this.getY() + -20;
                float endHeight = 29.0f;
                float startHeight = endHeight + dir.y * range;
                int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                float speed = dir.x * range * 250.0f / (float)lifeTime;
                Color color1 = new Color(13, 118, 150);
                Color color2 = new Color(3, 167, 255);
                Color color3 = new Color(71, 221, 255);
                Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
                this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
            }
        }
    }

    public boolean chargingUp() {
        return this.getTime() >= this.nextFireTime + 500L;
    }

    public boolean readyToFire() {
        return this.getTime() >= this.nextFireTime + 1000L + 500L;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("arcanicpylon", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.arcanicPylon, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        GameTexture texture = MobRegistry.Textures.arcanicPylon;
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 0;
    }

    public PlayerMob getTargetInRange() {
        int checkForPlayersRange = 384;
        ArrayList<PlayerMob> targetsFoundInLineOfSight = new ArrayList<PlayerMob>();
        List playerMobs = this.getLevel().entityManager.players.streamInRegionsInRange(this.getX(), this.getY(), checkForPlayersRange).filter(p -> p.getDistance(this) <= (float)checkForPlayersRange).collect(Collectors.toList());
        if (!playerMobs.isEmpty()) {
            for (PlayerMob playerMob : playerMobs) {
                if (!ChaserAINode.hasLineOfSightToTarget(this, playerMob)) continue;
                targetsFoundInLineOfSight.add(playerMob);
            }
        } else {
            return null;
        }
        if (targetsFoundInLineOfSight.isEmpty()) {
            return null;
        }
        return (PlayerMob)targetsFoundInLineOfSight.get(GameRandom.globalRandom.getIntBetween(0, targetsFoundInLineOfSight.size() - 1));
    }
}

