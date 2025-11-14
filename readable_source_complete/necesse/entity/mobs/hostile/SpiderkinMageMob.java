/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WebWeaverWebEvent;
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

public class SpiderkinMageMob
extends SpiderkinMob {
    public static GameDamage damage = new GameDamage(100.0f);
    private final int attackCooldown = 3000;

    public SpiderkinMageMob() {
        super(400, 40, 30);
        this.attackAnimTime = 1500;
        this.texture = MobRegistry.Textures.spiderkinMage;
        this.swimMaskMove = 20;
        this.swimMaskOffset = -4;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SpiderkinMageMob>(this, new ConfusedPlayerChaserWandererAI<SpiderkinMageMob>(null, 384, 256, 40000, false, false){

            @Override
            public boolean attackTarget(SpiderkinMageMob mob, Mob target) {
                if (SpiderkinMageMob.this.canAttack() && !SpiderkinMageMob.this.isOnGenericCooldown("attackCooldown")) {
                    mob.attack(target.getX(), target.getY(), false);
                    WebWeaverWebEvent event = new WebWeaverWebEvent(mob, (int)target.x, (int)target.y, GameRandom.globalRandom, damage, 0.0f, 1000L);
                    mob.getLevel().entityManager.events.add(event);
                    SpiderkinMageMob.this.startGenericCooldown("attackCooldown", 3000L);
                    this.wanderAfterAttack = true;
                }
                return true;
            }

            @Override
            protected int getRandomConfuseTime() {
                return GameRandom.globalRandom.getIntBetween(1000, 3000);
            }
        });
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderkinMageMob.getTileCoordinate(x), SpiderkinMageMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        float animProgress = this.getAttackAnimProgress();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(SpiderkinMageMob.getTileCoordinate(x), SpiderkinMageMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.texture).sprite(sprite).mask(swimMask).dir(dir).light(light);
        humanDrawOptions.hatTexture(new HumanDrawOptions.HumanDrawOptionsGetter(){

            @Override
            public DrawOptions getDrawOptions(PlayerMob player, int dir, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
                return MobRegistry.Textures.spiderkinMage_light.initDraw().sprite(spriteX, spriteY, spriteRes).light(light.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
            }
        }, ArmorItem.HairDrawMode.NO_HAIR);
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("goldenwebweaver"), null, animProgress, this.attackDir.x, this.attackDir.y);
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
            SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 1.6f)));
        }
    }
}

