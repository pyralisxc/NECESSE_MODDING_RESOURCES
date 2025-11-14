/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedCrystalDragonGolemsStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedCrystalDragonShardsStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedMoonlightDancerStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedMotherSlimeStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedNightSwarmStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedSpiderEmpressStage;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.ai.AscendedSunlightChampionStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.RelayAttackStageAINode;

public class AscendedRandomBossAttackStage<T extends AscendedWizardMob>
extends RelayAttackStageAINode<T> {
    public static ArrayList<Supplier<AINode<? extends AscendedWizardMob>>> possibleStages = new ArrayList();
    public ArrayList<Integer> currentRotationIndexes;

    public AscendedRandomBossAttackStage(ArrayList<Integer> rotationTracker) {
        this.currentRotationIndexes = rotationTracker != null ? rotationTracker : new ArrayList();
    }

    @Override
    public AINode<T> getNextNode() {
        if (this.currentRotationIndexes.isEmpty()) {
            for (int i = 0; i < possibleStages.size(); ++i) {
                this.currentRotationIndexes.add(i);
            }
            Collections.shuffle(this.currentRotationIndexes, GameRandom.globalRandom);
        }
        int index = this.currentRotationIndexes.remove(0);
        return possibleStages.get(index).get();
    }

    static {
        possibleStages.add(AscendedMoonlightDancerStage::new);
        possibleStages.add(AscendedMotherSlimeStage::new);
        possibleStages.add(AscendedSpiderEmpressStage::new);
        possibleStages.add(AscendedSunlightChampionStage::new);
        possibleStages.add(AscendedCrystalDragonGolemsStage::new);
        possibleStages.add(AscendedCrystalDragonShardsStage::new);
        possibleStages.add(AscendedNightSwarmStage::new);
    }
}

