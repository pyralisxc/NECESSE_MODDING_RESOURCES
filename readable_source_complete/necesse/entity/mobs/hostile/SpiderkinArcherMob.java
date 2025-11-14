/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.SpiderkinMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderkinArcherMob
extends SpiderkinMob {
    public static LootTable lootTable = new LootTable(new ChanceLootItemList(0.75f, new LootItem("spideritearrow", 10)));
    public static GameDamage damage = new GameDamage(90.0f);
    protected int shotsRemaining;

    public SpiderkinArcherMob() {
        super(450, 40, 30);
        this.texture = MobRegistry.Textures.spiderkinArcher;
        this.attackAnimTime = 250;
        this.attackCooldown = 1000;
        this.swimMaskMove = 20;
        this.swimMaskOffset = -4;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SpiderkinArcherMob>(this, new ConfusedPlayerChaserWandererAI<SpiderkinArcherMob>(null, 384, 384, 40000, false, false){

            @Override
            public boolean attackTarget(SpiderkinArcherMob mob, Mob target) {
                if (SpiderkinArcherMob.this.canAttack()) {
                    if (SpiderkinArcherMob.this.shotsRemaining <= 0) {
                        SpiderkinArcherMob.this.shotsRemaining = 3;
                    }
                    --SpiderkinArcherMob.this.shotsRemaining;
                    mob.attack(target.getX(), target.getY(), false);
                    Projectile projectile = ProjectileRegistry.getProjectile("spideritearrow", mob.getLevel(), mob.x, mob.y, target.x, target.y, 120.0f, 480, damage, (Mob)mob);
                    projectile.moveDist(10.0);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    if (SpiderkinArcherMob.this.shotsRemaining == 0) {
                        this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean canAttack() {
        if (this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue() || this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return false;
        }
        return super.canAttack() || this.shotsRemaining > 0 && this.getTimeSinceLastAttack() >= 150L;
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (!event.wasPrevented) {
            this.startAttackCooldown();
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderkinArcherMob.getTileCoordinate(x), SpiderkinArcherMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        float animProgress = this.getAttackAnimProgress();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(SpiderkinArcherMob.getTileCoordinate(x), SpiderkinArcherMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.texture).sprite(sprite).mask(swimMask).dir(dir).light(light);
        humanDrawOptions.hatTexture((player, dir1, spriteX, spriteY, spriteRes, drawX1, drawY1, width, height, mirrorX, mirrorY, light1, alpha, mask) -> MobRegistry.Textures.spiderkinArcher_light.initDraw().sprite(spriteX, spriteY, spriteRes).light(light1.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX1, drawY1), ArmorItem.HairDrawMode.NO_HAIR);
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("goldenarachnidwebbow"), null, animProgress, this.attackDir.x, this.attackDir.y);
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
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.bow, (SoundEffect)SoundEffect.effect(this));
        }
    }
}

