/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.seasons.SeasonalHat;
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
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EnchantedZombieMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(randomMapDrop);
    protected SeasonalHat hat;

    public EnchantedZombieMob() {
        super(160);
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
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
        this.ai = new BehaviourTreeAI<EnchantedZombieMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 480, new GameDamage(25.0f), 100, 40000));
        this.hat = GameSeasons.getHat(new GameRandom(this.getUniqueID()));
    }

    @Override
    public LootTable getLootTable() {
        if (this.hat != null) {
            return this.hat.getLootTable(lootTable);
        }
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("zombie", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.enchantedZombie.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(EnchantedZombieMob.getTileCoordinate(x), EnchantedZombieMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(EnchantedZombieMob.getTileCoordinate(x), EnchantedZombieMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.enchantedZombie).sprite(sprite).dir(dir).mask(swimMask).light(light);
        if (this.hat != null) {
            humanDrawOptions.hatTexture(this.hat.getDrawOptions(), ArmorItem.HairDrawMode.NO_HAIR);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
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
        return new SoundSettings(GameResources.zombieGroans[GameRandom.globalRandom.getIntBetween(10, 14)]).volume(0.35f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.zombieGroans[6], GameResources.zombieGroans[7], GameResources.zombieGroans[13], GameResources.zombieGroans[18]).volume(0.3f);
    }
}

