/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.HumanTexture
 *  necesse.entity.mobs.MaskShaderOptions
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI
 *  necesse.entity.mobs.hostile.GoblinMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.human.HumanDrawOptions
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class DaggerGoblin
extends GoblinMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{GoblinMob.lootTable});
    public static HumanTexture humanTexture;
    public final String daggerType;
    public GameDamage gameDamage;

    public DaggerGoblin(String daggerType) {
        String string = this.daggerType = daggerType != null ? daggerType : "copperdagger";
        if (Objects.equals(daggerType, "golddagger")) {
            this.gameDamage = new GameDamage(30.0f);
            this.setMaxHealth(80);
            this.setHealthHidden(80);
        } else if (Objects.equals(daggerType, "irondagger")) {
            this.gameDamage = new GameDamage(25.0f);
            this.setMaxHealth(70);
            this.setHealthHidden(70);
        } else {
            this.gameDamage = new GameDamage(20.0f);
            this.setMaxHealth(60);
            this.setHealthHidden(60);
        }
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new CollisionPlayerChaserWandererAI<GoblinMob>(() -> !this.getLevel().isCave && !this.getServer().world.worldEntity.isNight(), 384, this.gameDamage, 25, 40000){

            public boolean attackTarget(GoblinMob mob, Mob target) {
                if (target != null) {
                    DaggerGoblin.this.attack((int)target.x, (int)target.y, true);
                }
                return super.attackTarget((Mob)mob, target);
            }
        });
    }

    public LootTable getLootTable() {
        return new LootTable(new LootItemInterface[]{lootTable, new LootItem(this.daggerType)});
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 6 - 26;
        int drawY = camera.getDrawY(y) - 28 - 26;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount((Mob)this);
        float attackProgress = this.getAttackAnimProgress() / 2.0f;
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, humanTexture).sprite(sprite).dir(dir).mask(swimMask).light(light);
        if (this.isAttacking) {
            this.setupHumanAttackOptions(humanDrawOptions, new InventoryItem(this.daggerType), attackProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public void setupHumanAttackOptions(HumanDrawOptions humanDrawOptions, InventoryItem dagger, float attackProgress) {
        humanDrawOptions.itemAttack(dagger, null, attackProgress, this.attackDir.x, this.attackDir.y);
    }

    public static class GoldDaggerGoblin
    extends DaggerGoblin {
        public GoldDaggerGoblin() {
            super("golddagger");
        }

        protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
            super.onDeath(attacker, attackers);
        }
    }

    public static class IronDaggerGoblin
    extends DaggerGoblin {
        public IronDaggerGoblin() {
            super("irondagger");
        }
    }

    public static class CopperDaggerGoblin
    extends DaggerGoblin {
        public CopperDaggerGoblin() {
            super("copperdagger");
        }
    }
}

