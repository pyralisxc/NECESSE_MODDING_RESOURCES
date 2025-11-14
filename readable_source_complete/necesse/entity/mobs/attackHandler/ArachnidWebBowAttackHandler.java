/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireArachnidWebBow;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ArachnidWebBowToolItem;

public class ArachnidWebBowAttackHandler
extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final ArachnidWebBowToolItem toolItem;
    private final int attackSeed;
    private int shotsRemaining = 3;
    private int shots;
    private long timeBuffer;
    private final GameRandom random = new GameRandom();
    private final int timeBetweenReloads = 500;
    private final int timeBetweenBurstShots = 125;

    public ArachnidWebBowAttackHandler(ItemAttackerMob itemAttacker, ItemAttackSlot slot, InventoryItem item, ArachnidWebBowToolItem toolItem, int seed, int startTargetX, int startTargetY) {
        super(itemAttacker, slot, 50, 1000.0f, startTargetX, startTargetY);
        this.attackSeed = seed;
        this.timeBuffer = 500L;
        this.item = item;
        this.toolItem = toolItem;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null && !ChaserAINode.hasLineOfSightToTarget(this.attackerMob, this.lastItemAttackerTarget, 5.0f)) {
            this.attackerMob.endAttackHandler(true);
            return;
        }
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            float speedModifier;
            int seed = Item.getRandomAttackSeed(this.random.seeded(GameRandom.prime(this.attackSeed * this.shots)));
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, seed, 0);
            this.timeBuffer += (long)this.updateInterval;
            while (!((float)this.timeBuffer < 500.0f * (speedModifier = this.getSpeedModifier()))) {
                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                --this.shotsRemaining;
                this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                if (this.attackerMob.isClient()) {
                    ArachnidWebBowAttackHandler.playFireSound(this.attackerMob);
                } else if (this.attackerMob.isServer()) {
                    this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireArachnidWebBow(this.attackerMob));
                }
                if (this.shotsRemaining <= 0) {
                    this.shotsRemaining = 3;
                    this.timeBuffer = 0L;
                    break;
                }
                this.timeBuffer = (int)(375.0f * speedModifier);
            }
        }
    }

    public static void playFireSound(Mob target) {
        SoundManager.playSound(GameResources.bow, (SoundEffect)SoundEffect.effect(target));
    }

    private float getSpeedModifier() {
        return 1.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        if (this.attackerMob.isPlayer) {
            ((PlayerMob)this.attackerMob).startItemCooldown(this.toolItem, (int)(500.0f * this.getSpeedModifier()));
        }
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

