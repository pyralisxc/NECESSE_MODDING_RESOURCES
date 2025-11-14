/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.levelEvent.explosionEvent.AscendedBombExplosionEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.level.maps.presets.AscendedCagePreset;
import necesse.level.maps.presets.PresetUtils;

public class AscendedCageStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private Point targetPoint;
    private int currentChargeTime;
    private int chargeTime;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        this.targetPoint = new Point(target.getTileX() * 32 + 16, target.getTileY() * 32 + 16);
        AscendedCagePreset preset = new AscendedCagePreset();
        PresetUtils.placeAndSendPresetToClients(((Entity)mob).getServer(), preset, ((Entity)mob).getLevel(), target.getTileX() - 4, target.getTileY() - 4);
        ((AscendedWizardMob)mob).clearProblematicEntities();
        target.buffManager.removeBuff(BuffRegistry.Debuffs.ASCENDED_DARKNESS, true);
        this.currentChargeTime = 0;
        this.chargeTime = GameMath.lerp(((Mob)mob).getHealthPercent(), 6000, 10000);
        ((AscendedWizardMob)mob).startCageAbility.runAndSend(this.targetPoint.x, this.targetPoint.y, this.chargeTime);
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.currentChargeTime > this.chargeTime) {
            AscendedBombExplosionEvent e = new AscendedBombExplosionEvent(this.targetPoint.x, this.targetPoint.y, 200, AscendedWizardMob.bombDamage, true, 10.0f, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.events.add(e);
            return AINodeResult.SUCCESS;
        }
        this.currentChargeTime += 50;
        return AINodeResult.RUNNING;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

