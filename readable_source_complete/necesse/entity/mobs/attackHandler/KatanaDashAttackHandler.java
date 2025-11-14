/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.KatanaDashLevelEvent;
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
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.level.maps.hudManager.HudDrawElement;

public class KatanaDashAttackHandler
extends MousePositionAttackHandler {
    protected SoundPlayer katanaChargeSoundPlayer;
    public int chargeTime;
    public boolean fullyCharged;
    public KatanaToolItem katanaItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected int endAttackBuffer;
    protected HudDrawElement hudDrawElement;

    public KatanaDashAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, KatanaToolItem katanaItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.katanaItem = katanaItem;
        this.chargeTime = chargeTime;
        this.particleColors = particleColors;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
        if (!attackerMob.isClient()) {
            return;
        }
        this.katanaChargeSoundPlayer = SoundManager.playSound(GameResources.katanaChargeBegin, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float)this.getTimeSinceStart() / (float)this.chargeTime;
    }

    protected Color startColor() {
        return new Color(0, 0, 0, 0);
    }

    protected Color endColor() {
        return new Color(220, 255, 255, 100);
    }

    protected Color edgeColor() {
        return new Color(0, 0, 0, 100);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.attackerMob.isClient() && this.hudDrawElement == null) {
            this.hudDrawElement = this.attackerMob.getLevel().hudManager.addElement(new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    if (KatanaDashAttackHandler.this.attackerMob.getAttackHandler() != KatanaDashAttackHandler.this) {
                        this.remove();
                        return;
                    }
                    float distance = KatanaDashAttackHandler.this.getChargeDistance(KatanaDashAttackHandler.this.getChargePercent());
                    if (distance > 0.0f) {
                        Point2D.Float dir = GameMath.normalize((float)KatanaDashAttackHandler.this.lastX - KatanaDashAttackHandler.this.attackerMob.x, (float)KatanaDashAttackHandler.this.lastY - KatanaDashAttackHandler.this.attackerMob.y);
                        final DrawOptions drawOptions = HUD.getArrowHitboxIndicator(KatanaDashAttackHandler.this.attackerMob.x, KatanaDashAttackHandler.this.attackerMob.y, dir.x, dir.y, (int)distance, 50, KatanaDashAttackHandler.this.startColor(), KatanaDashAttackHandler.this.endColor(), KatanaDashAttackHandler.this.edgeColor(), camera);
                        list.add(new SortedDrawable(){

                            @Override
                            public int getPriority() {
                                return 1000;
                            }

                            @Override
                            public void draw(TickManager tickManager) {
                                drawOptions.draw();
                            }
                        });
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
        showItem.getGndData().setBoolean("chargeUp", true);
        GNDItemMap attackMap = new GNDItemMap();
        this.attackerMob.showItemAttack(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
        if (this.attackerMob.isServer()) {
            if (this.attackerMob.isPlayer) {
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                this.attackerMob.getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(player, showItem, this.lastX, this.lastY, 0, this.seed, attackMap), this.attackerMob, client);
            } else {
                this.attackerMob.showItemAttackMobAbility.runAndSend(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
            }
        }
        if (chargePercent >= 1.0f && !this.fullyCharged) {
            this.fullyCharged = true;
            if (this.attackerMob.isClient()) {
                int particles = 35;
                float anglePerParticle = 360.0f / (float)particles;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.particleColors).heightMoves(0.0f, 30.0f).lifeTime(500);
                }
                SoundManager.playSound(new SoundSettings(GameResources.katanaDashReady).volume(0.8f), this.attackerMob);
                if (this.attackerMob.isClient() && this.katanaChargeSoundPlayer != null) {
                    this.katanaChargeSoundPlayer.stop();
                }
            }
        }
    }

    @Override
    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (this.attackerMob.isClient() && this.katanaChargeSoundPlayer != null) {
            this.katanaChargeSoundPlayer.fadeOutAndStop(0.4f);
        }
        if (!this.endedByInteract && chargePercent >= 0.5f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setBoolean("sliceDash", true);
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            Point2D.Float dir = GameMath.normalize((float)this.lastX - this.attackerMob.x, (float)this.lastY - this.attackerMob.y);
            chargePercent = Math.min(chargePercent, 1.0f);
            KatanaDashLevelEvent event = new KatanaDashLevelEvent(this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int)(200.0f * chargePercent), this.katanaItem.getAttackDamage(this.item).modDamage(2.0f), this.katanaItem.maxDashStacks.getValue(this.katanaItem.getUpgradeTier(this.item)));
            this.attackerMob.addAndSendAttackerLevelEvent(event);
            this.attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN, (Mob)this.attackerMob, 3.0f, null), this.attackerMob.isServer());
            if (this.attackerMob.isClient()) {
                SoundManager.playSound(new SoundSettings(GameResources.swing1), this.attackerMob);
                SoundManager.playSound(new SoundSettings(GameResources.katanaDash), this.attackerMob);
            }
        }
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }

    public float getChargeDistance(float chargePercent) {
        if ((chargePercent = Math.min(chargePercent, 1.0f)) > 0.5f) {
            return (chargePercent - 0.5f) * 2.0f * (float)this.katanaItem.dashRange.getValue(this.katanaItem.getUpgradeTier(this.item)).intValue();
        }
        return 0.0f;
    }
}

