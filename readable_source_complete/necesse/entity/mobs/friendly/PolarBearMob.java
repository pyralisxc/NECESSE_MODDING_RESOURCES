/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PolarBearMob
extends FriendlyMob {
    public static LootTable lootTable = new LootTable(LootItem.between("icefish", 2, 5), new LootItem("polarclaw"));
    private final HashSet<Mob> targets = new HashSet();
    public final BooleanMobAbility setHostileAbility;

    public PolarBearMob() {
        super(1000);
        this.setArmor(40);
        this.updateStats();
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.2f);
        this.prioritizeVerticalDir = false;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-20, -16, 40, 32);
        this.selectBox = new Rectangle(-20, -50, 40, 55);
        this.swimMaskMove = 32;
        this.swimMaskOffset = -48;
        this.swimSinkOffset = -8;
        this.setHostileAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                PolarBearMob.this.isHostile = value;
                PolarBearMob.this.updateStats();
            }
        });
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isHostile = reader.getNextBoolean();
        this.updateStats();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isHostile);
    }

    public void updateStats() {
        this.setSpeed(this.isHostile ? 40.0f : 10.0f);
        this.setCombatRegen(this.isHostile ? 0.0f : 10.0f);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<PolarBearMob>(this, new CollisionChaserWandererAI<PolarBearMob>(null, 480, new GameDamage(50.0f), 100, 40000){

            @Override
            public GameAreaStream<Mob> streamPossibleTargets(PolarBearMob mob, Point base, TargetFinderDistance<PolarBearMob> distance) {
                return distance.streamMobsAndPlayersInRange(base, mob).filter(m -> PolarBearMob.this.targets.contains(m));
            }
        });
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.targets.removeIf(m -> m.removed() || !m.isSamePlace(this) || m.getDistance(this) > 384.0f);
        this.setHostile(!this.targets.isEmpty());
    }

    @Override
    public MobWasHitEvent isServerHit(GameDamage damage, float x, float y, float knockback, Attacker attacker) {
        Mob attackOwner;
        MobWasHitEvent out = super.isServerHit(damage, x, y, knockback, attacker);
        if (out != null && !out.wasPrevented && (attackOwner = attacker.getAttackOwner()) != null) {
            this.targets.add(attackOwner);
        }
        return out;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("polar", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.polarBear, i, 16, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PolarBearMob.getTileCoordinate(x), PolarBearMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 128 + 36;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.polarBear.initDraw().sprite(sprite.x, sprite.y, 128).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(PolarBearMob.getTileCoordinate(x), PolarBearMob.getTileCoordinate(y)).getMobSinkingAmount(this));
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
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.polarBear_shadow;
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 128 + 36;
        return shadowTexture.initDraw().sprite(0, this.getDir(), 128).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    public void setHostile(boolean hostile) {
        if (this.getLevel() == null || this.getLevel().getServer() == null) {
            return;
        }
        if (this.isHostile == hostile) {
            return;
        }
        this.setHostileAbility.runAndSend(hostile);
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.polarBearAmbient);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.polarBearHurt);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.polarBearDeath).volume(1.1f);
    }
}

