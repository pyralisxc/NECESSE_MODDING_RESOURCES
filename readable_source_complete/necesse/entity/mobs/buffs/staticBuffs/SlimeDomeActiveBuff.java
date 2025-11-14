/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class SlimeDomeActiveBuff
extends Buff
implements HumanDrawBuff {
    public GameTexture slimeDomeTexture;

    public SlimeDomeActiveBuff() {
        this.isVisible = true;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.INCOMING_DAMAGE_MOD, Float.valueOf(0.0f));
        buff.setModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(1.0f));
        buff.setMaxModifier(BuffModifiers.FRICTION, Float.valueOf(0.25f), 1000);
        buff.setModifier(BuffModifiers.BOUNCY, true);
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.5f));
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.slimeDomeTexture = GameTexture.fromFile("particles/slimedome");
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (!event.wasPrevented) {
            buff.getGndData().setLong("timeHit", buff.owner.getTime());
        }
    }

    public float getBouncyAnimation(long timeRemaining, int totalTime) {
        if (timeRemaining > (long)totalTime) {
            float perc = GameUtils.getAnimFloat(timeRemaining - (long)totalTime, 200);
            return (float)(Math.sin((double)perc * Math.PI * 2.0) + 1.0) / 2.0f;
        }
        float perc = Math.abs((float)timeRemaining / (float)totalTime - 1.0f);
        return (float)Math.abs((Math.cos(Math.PI * 6 / (double)(perc + 0.5f)) + 1.0) / 2.0);
    }

    @Override
    public void addHumanDraw(ActiveBuff buff, HumanDrawOptions drawOptions) {
        if (buff.owner.buffManager.hasBuff(BuffRegistry.SLIME_DOME_ACTIVE)) {
            float heightSize;
            float widthSize;
            long timeHit = buff.getGndData().getLong("timeHit");
            long timeSinceHit = buff.owner.getTime() - timeHit;
            if (timeHit != 0L && timeSinceHit <= 2000L) {
                float bouncePercent = this.getBouncyAnimation(2000L - timeSinceHit, 2000);
                float bounceWidth = GameMath.lerp(bouncePercent, 0.85f, 1.15f);
                float bounceHeight = GameMath.lerp(bouncePercent, 1.15f, 0.85f);
                float animPercent = (float)timeSinceHit / 2000.0f;
                widthSize = GameMath.lerp(animPercent, bounceWidth, 1.0f);
                heightSize = GameMath.lerp(animPercent, bounceHeight, 1.0f);
            } else {
                widthSize = 1.0f;
                heightSize = 1.0f;
            }
            drawOptions.addTopDraw(new HumanDrawOptions.HumanDrawOptionsGetter(){

                @Override
                public DrawOptions getDrawOptions(PlayerMob player, int dir, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
                    int wiggleWidth = (int)((float)width * widthSize);
                    int wiggleHeight = (int)((float)height * heightSize);
                    int widthDiff = width - wiggleWidth;
                    int heightDiff = height - wiggleHeight;
                    return SlimeDomeActiveBuff.this.slimeDomeTexture.initDraw().size(wiggleWidth, wiggleHeight).light(light).addMaskShader(mask).pos(drawX + widthDiff / 2 + 2, drawY + heightDiff / 2 + 2);
                }
            });
        }
    }
}

