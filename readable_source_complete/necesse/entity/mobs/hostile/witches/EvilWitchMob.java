/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.witches;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelStats;
import necesse.level.maps.light.GameLight;

public class EvilWitchMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(RotationLootItem.customLootRotation(1, mob -> {
        LevelStats stats = mob.getLevel().levelStats;
        return stats.mob_kills.getKills("evilwitch");
    }, new LootItem("witchhat"), new LootItem("witchrobe"), new LootItem("witchshoes")));
    protected int lookSeed;
    protected HumanLook look = new HumanLook();

    public EvilWitchMob() {
        super(1000);
        this.attackCooldown = 600;
        this.attackAnimTime = 800;
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.updateLook();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lookSeed);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lookSeed = reader.getNextInt();
        this.updateLook();
    }

    @Override
    public void init() {
        super.init();
        this.updateLook();
    }

    public void updateLook() {
        if (this.lookSeed == 0) {
            this.lookSeed = GameRandom.globalRandom.nextInt();
        }
        GameRandom random = new GameRandom(this.lookSeed);
        this.look.setFacialFeature(0);
        this.look.setSkin(9);
        this.look.setEyeType(7);
        this.look.setEyeColor(12);
        this.look.setHair(random.getOneOf(21, 28, 31, 33));
        this.look.setHairColor(random.getOneOf(2, 7, 9));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            if (this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS).booleanValue()) {
                return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
            }
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("witch", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 50; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            int lifeTime = GameRandom.globalRandom.getIntBetween(2000, 5000);
            float lifePerc = (float)lifeTime / 5000.0f;
            float startHeight = GameRandom.globalRandom.getIntBetween(0, 10);
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(70, 150) * lifePerc;
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(15, 20).movesFriction((float)GameRandom.globalRandom.getIntBetween(2, 36) * dir.x, (float)GameRandom.globalRandom.getIntBetween(2, 36) * dir.y, 1.0f).heightMoves(startHeight, height).color(this.getDeathParticleColor(GameRandom.globalRandom)).lifeTime(lifeTime);
        }
    }

    protected Color getDeathParticleColor(GameRandom random) {
        return new Color(random.getIntBetween(22, 88), random.getIntBetween(22, 33), random.getIntBetween(22, 88));
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(EvilWitchMob.getTileCoordinate(x), EvilWitchMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.look, false).sprite(sprite).dir(dir).mask(swimMask).light(light).helmet(new InventoryItem("witchhat")).chestplate(new InventoryItem("witchrobe")).boots(new InventoryItem("witchshoes"));
        this.setupWitchDrawOptions(humanDrawOptions);
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY += level.getTile(EvilWitchMob.getTileCoordinate(x), EvilWitchMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    protected void setupWitchDrawOptions(HumanDrawOptions humanDrawOptions) {
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (this.countStats) {
            attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.newStats.mob_kills.addKill("evilwitch"));
        }
    }
}

