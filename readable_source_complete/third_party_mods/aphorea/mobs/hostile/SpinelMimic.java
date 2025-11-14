/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
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
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.RotationLootItem
 *  necesse.inventory.lootTable.presets.CaveChestLootTable
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.registry.AphLootTables;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CaveChestLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpinelMimic
extends HostileMob {
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameDamage damage;
    public int jump = 0;
    public static float jumpHeight;
    public static int jumpDuration;
    public static LootTable lootTable;
    int adjustY = 22;

    public SpinelMimic() {
        super(400);
        this.setArmor(20);
        this.setSpeed(60.0f);
        this.setFriction(5.0f);
        this.collision = new Rectangle(-10, 14 - this.adjustY, 20, 10);
        this.hitBox = new Rectangle(-14, 10 - this.adjustY, 28, 14);
        this.selectBox = new Rectangle(-16, -6 - this.adjustY, 32, 32);
        this.setKnockbackModifier(0.0f);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI(null, 192, damage, 0, 800000));
        this.jump = 0;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 7; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, i, 5, 16, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void clientTick() {
        super.clientTick();
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32 - this.adjustY;
        drawY -= (int)(Math.sin((double)((float)this.jump / (float)jumpDuration) * Math.PI) * (double)jumpHeight);
        drawY += this.getBobbing(x, y);
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 32, 64).light(light).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    protected void addShadowDrawables(OrderableDrawables list, Level level, int x, int y, GameLight light, GameCamera camera) {
        TextureDrawOptions shadowOptions;
        if (!((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).booleanValue() && !this.isRiding() && (shadowOptions = this.getShadowDrawOptions(level, x, y, light, camera)) != null) {
            list.add(tm -> shadowOptions.draw());
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32 - this.adjustY;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        return shadowTexture.initDraw().sprite(sprite.x, sprite.y, 32, 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(dir, this.jump > 0 ? 1 : 0);
    }

    static {
        damage = new GameDamage(60.0f, 20.0f);
        jumpHeight = 30.0f;
        jumpDuration = 12;
        lootTable = new LootTable(new LootItemInterface[]{new LootItem("spinelchest"), AphLootTables.infectedCaveVariousTreasures, RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{CaveChestLootTable.potions, CaveChestLootTable.bars, CaveChestLootTable.extraItems})});
    }
}

