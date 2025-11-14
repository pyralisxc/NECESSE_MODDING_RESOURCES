/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireShardCannon;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
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
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShardCannonProjectileToolItem;

public class ShardCannonAttackHandler
extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final ShardCannonProjectileToolItem toolItem;
    private long lastTime;
    private long timeBuffer;
    private final int attackSeed;
    private int shots;
    private final GameRandom random = new GameRandom();

    public ShardCannonAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, ShardCannonProjectileToolItem toolItem, int seed, int startTargetX, int startTargetY) {
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
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        long currentTime = this.attackerMob.getLevel().getLocalTime();
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            int cooldown;
            this.timeBuffer += currentTime - this.lastTime;
            int seed = Item.getRandomAttackSeed(this.random.seeded(GameRandom.prime(this.attackSeed * this.shots)));
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, 0, seed);
            while (this.timeBuffer >= (long)(cooldown = this.getShootCooldown())) {
                this.timeBuffer -= (long)cooldown;
                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                if (this.attackerMob.isClient()) {
                    ShardCannonAttackHandler.playFireSound(this.attackerMob);
                    continue;
                }
                if (!this.attackerMob.isServer()) continue;
                this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireShardCannon(this.attackerMob));
            }
        }
        this.lastTime = currentTime;
    }

    public static void playFireSound(Mob target) {
        SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(target).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 1.75f)));
        SoundManager.playSound(GameRandom.globalRandom.getOneOf(GameResources.crystalHit2, GameResources.crystalHit3), (SoundEffect)SoundEffect.effect(target).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }

    private int getShootCooldown() {
        float multiplier = 1.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
        return (int)(multiplier * 150.0f);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

