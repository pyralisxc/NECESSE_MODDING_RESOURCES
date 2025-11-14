/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketShowAttack
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.ui.HUD
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.hudManager.HudDrawElement
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package aphorea.items.tools.weapons.melee.greatsword.logic;

import aphorea.items.tools.weapons.melee.greatsword.AphGreatswordSecondarySpinToolItem;
import aphorea.items.tools.weapons.melee.greatsword.logic.GreatswordDashLevelEvent;
import aphorea.registry.AphBuffs;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class GreatswordSecondarySpinAttackHandler<T extends AphGreatswordSecondarySpinToolItem>
extends MousePositionAttackHandler {
    public int chargeTime;
    public boolean fullyCharged;
    public T toolItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected int endAttackBuffer;
    protected HudDrawElement hudDrawElement;

    public GreatswordSecondarySpinAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, T toolItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.toolItem = toolItem;
        this.chargeTime = chargeTime;
        this.particleColors = particleColors;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float)this.getTimeSinceStart() / (float)this.chargeTime;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.attackerMob.isClient() && this.hudDrawElement == null) {
            this.hudDrawElement = this.attackerMob.getLevel().hudManager.addElement(new HudDrawElement(){

                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    if (GreatswordSecondarySpinAttackHandler.this.attackerMob.getAttackHandler() != GreatswordSecondarySpinAttackHandler.this) {
                        this.remove();
                    } else {
                        float distance = GreatswordSecondarySpinAttackHandler.this.getChargeDistance(GreatswordSecondarySpinAttackHandler.this.getChargePercent());
                        if (distance > 0.0f) {
                            Point2D.Float dir = GameMath.normalize((float)((float)GreatswordSecondarySpinAttackHandler.this.lastX - GreatswordSecondarySpinAttackHandler.this.attackerMob.x), (float)((float)GreatswordSecondarySpinAttackHandler.this.lastY - GreatswordSecondarySpinAttackHandler.this.attackerMob.y));
                            final DrawOptions drawOptions = HUD.getArrowHitboxIndicator((float)GreatswordSecondarySpinAttackHandler.this.attackerMob.x, (float)GreatswordSecondarySpinAttackHandler.this.attackerMob.y, (float)dir.x, (float)dir.y, (int)((int)distance), (int)50, (Color)new Color(0, 0, 0, 0), (Color)new Color(220, 255, 255, 100), (Color)new Color(0, 0, 0, 100), (GameCamera)camera);
                            list.add(new SortedDrawable(){

                                public int getPriority() {
                                    return 1000;
                                }

                                public void draw(TickManager tickManager) {
                                    drawOptions.draw();
                                }
                            });
                        }
                    }
                }
            });
        }
        float chargePercent = this.getChargePercent();
        if (!this.attackerMob.isPlayer && chargePercent >= 1.0f) {
            this.endAttackBuffer += this.updateInterval;
            if (this.endAttackBuffer >= 350) {
                this.endAttackBuffer = 0;
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setBoolean("charging", true);
        GNDItemMap attackMap = new GNDItemMap();
        this.attackerMob.showItemAttack(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
        if (this.attackerMob.isServer()) {
            if (this.attackerMob.isPlayer) {
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                this.attackerMob.getServer().network.sendToClientsWithEntityExcept((Packet)new PacketShowAttack(player, showItem, this.lastX, this.lastY, 0, this.seed, attackMap), (RegionPositionGetter)this.attackerMob, client);
            } else {
                this.attackerMob.showItemAttackMobAbility.runAndSend(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
            }
        }
        if (chargePercent >= 1.0f && !this.fullyCharged) {
            this.fullyCharged = true;
            if (this.attackerMob.isClient()) {
                int particles = 35;
                float anglePerParticle = 360.0f / (float)particles;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                    this.attackerMob.getLevel().entityManager.addParticle((Entity)this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.particleColors).heightMoves(0.0f, 30.0f).lifeTime(500);
                }
                SoundManager.playSound((GameSound)GameResources.magicbolt4, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob).volume(0.1f).pitch(2.5f));
            }
        }
    }

    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 1.0f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            Point2D.Float dir = GameMath.normalize((float)((float)this.lastX - this.attackerMob.x), (float)((float)this.lastY - this.attackerMob.y));
            chargePercent = Math.min(chargePercent, 1.0f);
            GreatswordDashLevelEvent event = new GreatswordDashLevelEvent(this.attackerMob, attackItem, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int)(200.0f * chargePercent), this.toolItem.getAttackDamage(this.item));
            this.attackerMob.addAndSendAttackerLevelEvent((LevelEvent)event);
            this.attackerMob.buffManager.addBuff(new ActiveBuff(AphBuffs.SPIN_ATTACK_COOLDOWN, (Mob)this.attackerMob, 3.0f, null), this.attackerMob.isServer());
        }
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }

    public float getChargeDistance(float chargePercent) {
        chargePercent = Math.min(chargePercent, 1.0f);
        return chargePercent * 200.0f;
    }
}

