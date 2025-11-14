/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.util.GameRandom
 *  necesse.engine.world.GameClock
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.HumanTexture
 *  necesse.entity.mobs.MaskShaderOptions
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobSpawnLocation
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.pickup.ItemPickupEntity
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.inventory.lootTable.lootItem.OneOfTicketLootItems
 *  necesse.level.maps.Level
 *  necesse.level.maps.TilePosition
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.GameClock;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class SpinelCaveling
extends HostileMob {
    public static GameDamage collision_damage = new GameDamage(40.0f);
    public static int collision_knockback = 50;
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{7, new ChanceLootItem(0.25f, "spinel"), 1, new ChanceLootItem(0.25f, "lifespinel")})});
    public static HumanTexture texture;
    public InventoryItem item;

    public SpinelCaveling() {
        super(160);
        this.setArmor(10);
        this.setSpeed(60.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -40, 32, 50);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 4;
        this.swimSinkOffset = 0;
        this.item = new InventoryItem("spinel", 0);
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI(null, 384, collision_damage, collision_knockback, 40000));
        ArrayList items = lootTable.getNewList(new GameRandom((long)this.getUniqueID()), 1.0f, new Object[0]);
        if (!items.isEmpty()) {
            this.item = (InventoryItem)items.get(0);
        }
        this.dropsLoot = false;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), SpinelCaveling.texture.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (this.item.getAmount() > 0) {
            this.getLevel().entityManager.pickups.add((Entity)new ItemPickupEntity(this.getLevel(), this.item, this.x, this.y, 0.0f, 0.0f));
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48;
        int dir = this.getDir();
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount((Mob)this);
        boolean hasSpelunker = perspective != null && (Boolean)perspective.buffManager.getModifier(BuffModifiers.SPELUNKER) != false;
        Point sprite = this.getAnimSprite(x, y, dir);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd rightArmOptions = SpinelCaveling.texture.rightArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY += this.getBobbing(x, y));
        final TextureDrawOptionsEnd bodyOptions = SpinelCaveling.texture.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY);
        Color drawColor = this.item.item.getDrawColor(this.item, perspective);
        int itemBobbing = sprite.x != 1 && sprite.x != 3 ? 0 : 2;
        GameLight itemLight = hasSpelunker ? light.minLevelCopy(100.0f) : light;
        final TextureDrawOptionsEnd itemOptions = this.item.item.getItemSprite(this.item, perspective).initDraw().colorLight(drawColor, itemLight).mirror(sprite.y < 2, false).size(32).posMiddle(drawX + 32, drawY + 16 + itemBobbing + swimMask.drawYOffset);
        final TextureDrawOptionsEnd leftArmOptions = SpinelCaveling.texture.leftArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY);
        list.add(new MobDrawable(){

            public void draw(TickManager tickManager) {
                boolean hasObject = SpinelCaveling.this.item.getAmount() > 0;
                swimMask.use();
                if (hasObject) {
                    rightArmOptions.draw();
                }
                bodyOptions.draw();
                swimMask.stop();
                if (hasObject) {
                    itemOptions.draw();
                }
                swimMask.use();
                if (hasObject) {
                    leftArmOptions.draw();
                }
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.caveling_shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(arg_0 -> SpinelCaveling.lambda$addDrawables$0((TextureDrawOptions)shadow, arg_0));
    }

    public int getRockSpeed() {
        return 10;
    }

    public boolean isLavaImmune() {
        return true;
    }

    public boolean isSlimeImmune() {
        return true;
    }

    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        return 0;
    }

    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotSolidTile().checkNotOnSurfaceInsideOnFloor().checkNotLevelCollides().checkTile((x, y) -> this.getLevel().getLightLevel(x.intValue(), y.intValue()).getLevel() <= 50.0f);
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadow, TickManager tm) {
        shadow.draw();
    }
}

