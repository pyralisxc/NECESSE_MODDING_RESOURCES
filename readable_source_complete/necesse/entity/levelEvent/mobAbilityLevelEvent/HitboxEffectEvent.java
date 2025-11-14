/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class HitboxEffectEvent
extends MobAbilityLevelEvent {
    protected HudDrawElement hudDrawElement;

    public HitboxEffectEvent() {
    }

    public HitboxEffectEvent(Mob owner, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient() && !this.isOver()) {
            this.hudDrawElement = this.level.hudManager.addElement(new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return 0;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            Shape hitBox;
                            if (GlobalData.debugActive() && (hitBox = HitboxEffectEvent.this.getHitBox()) != null) {
                                Renderer.drawShape(hitBox, camera, false, 1.0f, 0.0f, 0.0f, 1.0f);
                            }
                        }
                    });
                }
            });
        }
    }

    public abstract Shape getHitBox();

    public abstract void clientHit(Mob var1);

    public abstract void serverHit(Mob var1, boolean var2);

    public abstract void hitObject(LevelObjectHit var1);

    public boolean canHit(Mob mob) {
        return mob.canBeHit(this);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleHits(hitBox, this::canHit, null);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleHits(hitBox, this::canHit, null);
        }
    }

    @Override
    public final void hit(LevelObjectHit hit) {
        super.hit(hit);
        this.hitObject(hit);
    }

    @Override
    public final void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.clientHit(target);
    }

    @Override
    public final void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        this.serverHit(target, clientSubmitted);
    }

    @Override
    public void onDispose() {
        super.onDispose();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }
}

