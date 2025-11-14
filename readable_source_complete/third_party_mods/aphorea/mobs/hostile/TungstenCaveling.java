/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.world.GameClock
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
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.TilePosition
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.world.GameClock;
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
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class TungstenCaveling
extends HostileMob {
    public static GameDamage collision_damage = new GameDamage(50.0f, 20.0f);
    public static int collision_knockback = 50;
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"tungstenore", (int)1, (int)3)});
    public static HumanTexture texture;
    public InventoryItem item;

    public TungstenCaveling() {
        super(600);
        this.setArmor(20);
        this.setSpeed(40.0f);
        this.setFriction(4.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -40, 32, 50);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 4;
        this.swimSinkOffset = 0;
        this.item = new InventoryItem("tungstenore", 1);
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI(null, 384, collision_damage, collision_knockback, 40000));
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), TungstenCaveling.texture.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
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
        final TextureDrawOptionsEnd rightArmOptions = TungstenCaveling.texture.rightArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY += this.getBobbing(x, y));
        final TextureDrawOptionsEnd bodyOptions = TungstenCaveling.texture.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY);
        Color drawColor = this.item.item.getDrawColor(this.item, perspective);
        int itemBobbing = sprite.x != 1 && sprite.x != 3 ? 0 : 2;
        GameLight itemLight = hasSpelunker ? light.minLevelCopy(100.0f) : light;
        final TextureDrawOptionsEnd itemOptions = this.item.item.getItemSprite(this.item, perspective).initDraw().colorLight(drawColor, itemLight).mirror(sprite.y < 2, false).size(32).posMiddle(drawX + 32, drawY + 16 + itemBobbing + swimMask.drawYOffset);
        final TextureDrawOptionsEnd leftArmOptions = TungstenCaveling.texture.leftArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, (long)this.getID(), (GameClock)this).pos(drawX, drawY);
        list.add(new MobDrawable(){

            public void draw(TickManager tickManager) {
                swimMask.use();
                rightArmOptions.draw();
                bodyOptions.draw();
                swimMask.stop();
                itemOptions.draw();
                swimMask.use();
                leftArmOptions.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.caveling_shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(arg_0 -> TungstenCaveling.lambda$addDrawables$0((TextureDrawOptions)shadow, arg_0));
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

