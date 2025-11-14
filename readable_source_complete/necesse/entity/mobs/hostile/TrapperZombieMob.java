/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrapperZombieMob
extends HostileMob {
    public static LootTable extraLootTable = new LootTable(new ChanceLootItem(0.05f, "trapperhat"));

    public TrapperZombieMob() {
        super(120);
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<TrapperZombieMob>(this, new ConfusedCollisionPlayerChaserWandererAI(() -> !this.getLevel().isCave && !this.getWorldEntity().isNight() && this.canDespawn, 384, new GameDamage(18.0f), 100, 40000));
        this.ambientSoundCooldown = GameRandom.globalRandom.getIntBetween(12000, 28000);
    }

    @Override
    public LootTable getLootTable() {
        return new LootTable(ZombieMob.lootTable, extraLootTable);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("zombie", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.trapperZombie.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TrapperZombieMob.getTileCoordinate(x), TrapperZombieMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final DrawOptions drawOptions = new HumanDrawOptions(level, MobRegistry.Textures.trapperZombie).sprite(sprite).dir(dir).mask(swimMask).light(light).pos(drawX, drawY += level.getTile(TrapperZombieMob.getTileCoordinate(x), TrapperZombieMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.zombieGroans[GameRandom.globalRandom.getIntBetween(20, 24)]).volume(0.35f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.zombieGroans[6], GameResources.zombieGroans[7], GameResources.zombieGroans[13], GameResources.zombieGroans[18]).volume(0.3f);
    }
}

