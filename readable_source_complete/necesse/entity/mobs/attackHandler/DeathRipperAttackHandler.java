/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.DeathRipperProjectileToolItem;

public class DeathRipperAttackHandler
extends MouseAngleAttackHandler {
    protected SoundPlayer chargeSoundPlayer;
    protected int lastChargeSoundCooldown;
    private final InventoryItem item;
    private final DeathRipperProjectileToolItem toolItem;
    private long lastTime;
    private long timeBuffer;
    private final int attackSeed;
    private int shots;
    private final GameRandom random = new GameRandom();

    public DeathRipperAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, DeathRipperProjectileToolItem toolItem, int seed, int startTargetX, int startTargetY) {
        super(attackerMob, slot, 50, 1000.0f, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.attackSeed = seed;
        this.lastTime = attackerMob.getLocalTime();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null && !ChaserAINode.hasLineOfSightToTarget(this.attackerMob, this.lastItemAttackerTarget, 5.0f)) {
            this.attackerMob.endAttackHandler(true);
            return;
        }
        if (this.attackerMob.isClient()) {
            int cooldown = this.getShootCooldownFlat();
            if (this.chargeSoundPlayer == null || this.lastChargeSoundCooldown != cooldown) {
                SoundPlayer lastPlayer = this.chargeSoundPlayer;
                float progress = GameMath.getPercentageBetweenTwoNumbers(cooldown, 150.0f, 350.0f);
                float pitch = GameMath.lerp(progress, 1.3f, 1.0f);
                float volume = GameMath.lerp(progress, 0.2f, 0.3f);
                this.chargeSoundPlayer = SoundManager.playSound(GameResources.deathRipperCharge, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(volume).pitch(pitch), player -> {
                    if (lastPlayer == null) {
                        player.fadeIn(1.0f);
                    } else {
                        player.copyFadeInProgress(lastPlayer);
                        player.setPosition(lastPlayer.getPositionSeconds());
                    }
                });
                this.lastChargeSoundCooldown = cooldown;
            }
            if (this.chargeSoundPlayer != null && !this.chargeSoundPlayer.isDone()) {
                this.chargeSoundPlayer.refreshLooping(0.5f);
            }
        }
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        long currentTime = this.attackerMob.getLevel().getLocalTime();
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            float multiplier;
            int cooldown;
            int actualCooldown;
            this.timeBuffer += currentTime - this.lastTime;
            int seed = Item.getRandomAttackSeed(this.random.seeded(GameRandom.prime(this.attackSeed * this.shots)));
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, 0, seed);
            while (this.timeBuffer >= (long)(actualCooldown = (int)((float)(cooldown = this.getShootCooldownFlat()) * (multiplier = 1.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob))))) {
                this.timeBuffer -= (long)actualCooldown;
                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                if (this.attackerMob.isClient()) {
                    DeathRipperAttackHandler.playFireSound(this.attackerMob);
                    continue;
                }
                if (!this.attackerMob.isServer()) continue;
                this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireDeathRipper(this.attackerMob));
            }
        } else {
            this.onEndAttack(true);
        }
        this.lastTime = currentTime;
    }

    public static void playFireSound(Mob target) {
        SoundManager.playSound(GameResources.handgun, (SoundEffect)SoundEffect.effect(target));
    }

    private int getShootCooldownFlat() {
        if (this.shots > 9) {
            return 150;
        }
        if (this.shots > 6) {
            return 200;
        }
        if (this.shots > 3) {
            return 275;
        }
        return 350;
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
        this.shots = 0;
        if (this.chargeSoundPlayer != null) {
            this.chargeSoundPlayer.fadeOutAndStop(0.5f);
        }
    }
}

