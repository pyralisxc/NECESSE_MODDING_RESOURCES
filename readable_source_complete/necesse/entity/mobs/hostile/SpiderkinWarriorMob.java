/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.SpiderkinMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderkinWarriorMob
extends SpiderkinMob {
    public static GameDamage damage = new GameDamage(115.0f);

    public SpiderkinWarriorMob() {
        super(600, 35, 40);
        this.attackAnimTime = 300;
        this.attackCooldown = 700;
        this.texture = MobRegistry.Textures.spiderkinWarrior;
        this.swimMaskMove = 20;
        this.swimMaskOffset = -4;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SpiderkinWarriorMob>(this, new ConfusedPlayerChaserWandererAI<SpiderkinWarriorMob>(null, 384, 64, 40000, false, false){

            @Override
            public boolean attackTarget(SpiderkinWarriorMob mob, Mob target) {
                if (SpiderkinWarriorMob.this.canAttack()) {
                    mob.attack(target.getX(), target.getY(), true);
                    InventoryItem attackItem = new InventoryItem("goldencausticexecutioner");
                    attackItem.getGndData().setItem("damage", (GNDItem)new GNDItemGameDamage(damage));
                    SpiderkinWarriorMob.this.getLevel().entityManager.events.add(new ToolItemMobAbilityEvent(SpiderkinWarriorMob.this, GameRandom.globalRandom.nextInt(), attackItem, mob.getX(), mob.getY(), SpiderkinWarriorMob.this.attackAnimTime, SpiderkinWarriorMob.this.attackAnimTime));
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderkinWarriorMob.getTileCoordinate(x), SpiderkinWarriorMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        float animProgress = this.getAttackAnimProgress();
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(SpiderkinWarriorMob.getTileCoordinate(x), SpiderkinWarriorMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.texture).sprite(sprite).mask(swimMask).dir(dir).light(light);
        humanDrawOptions.hatTexture(new HumanDrawOptions.HumanDrawOptionsGetter(){

            @Override
            public DrawOptions getDrawOptions(PlayerMob player, int dir, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
                return MobRegistry.Textures.spiderkinWarrior_light.initDraw().sprite(spriteX, spriteY, spriteRes).light(light.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
            }
        }, ArmorItem.HairDrawMode.NO_HAIR);
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("goldencausticexecutioner"), null, animProgress, this.attackDir.x, this.attackDir.y);
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
            SoundManager.playSound(GameResources.swing2, (SoundEffect)SoundEffect.effect(this));
        }
    }
}

