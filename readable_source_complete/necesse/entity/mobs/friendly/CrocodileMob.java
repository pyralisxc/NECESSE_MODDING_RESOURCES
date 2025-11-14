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
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
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
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class CrocodileMob
extends FriendlyMob {
    public static LootTable lootTable = new LootTable(new ChanceLootItemList(0.2f, new OneOfLootItems(new LootItem("safarihat"), new LootItem("safarishirt"), new LootItem("safarishoes"))));
    private final HashSet<Mob> targets = new HashSet();
    public final BooleanMobAbility setHostileAbility;

    public CrocodileMob() {
        super(250);
        this.setArmor(12);
        this.updateStats();
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.2f);
        this.prioritizeVerticalDir = true;
        this.swimSinkOffset = -4;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-20, -16, 40, 32);
        this.selectBox = new Rectangle(-20, -50, 40, 55);
        this.setHostileAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                CrocodileMob.this.isHostile = value;
                CrocodileMob.this.updateStats();
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
        this.setSpeed(this.isHostile ? 45.0f : 15.0f);
        this.setCombatRegen(this.isHostile ? 0.0f : 10.0f);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CrocodileMob>(this, new CollisionChaserWandererAI<CrocodileMob>(null, 480, new GameDamage(35.0f), 100, 40000){

            @Override
            public GameAreaStream<Mob> streamPossibleTargets(CrocodileMob mob, Point base, TargetFinderDistance<CrocodileMob> distance) {
                return distance.streamMobsAndPlayersInRange(base, mob).filter(m -> CrocodileMob.this.targets.contains(m));
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
        return this.getDeathMessages("crocodile", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crocodile, i, 16, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CrocodileMob.getTileCoordinate(x), CrocodileMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 128 + 36;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.crocodile.initDraw().sprite(sprite.x, sprite.y, 128).light(light).pos(drawX, drawY += level.getTile(CrocodileMob.getTileCoordinate(x), CrocodileMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public int getRockSpeed() {
        return this.isHostile ? 12 : 10;
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.waterID) {
            return 1000;
        }
        int height = pos.level.liquidManager.getHeight(pos.tileX, pos.tileY);
        if (height >= 0 && height <= 3) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
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
}

