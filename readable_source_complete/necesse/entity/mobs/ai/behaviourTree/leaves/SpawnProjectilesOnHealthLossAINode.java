/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class SpawnProjectilesOnHealthLossAINode<T extends Mob>
extends AINode<T> {
    public int totalProjectiles;
    public int maxPerSecond;
    public boolean isPaused;
    public int lastHealth;
    public float nextProjectile;

    public SpawnProjectilesOnHealthLossAINode(int totalProjectiles, int maxPerSecond) {
        this.totalProjectiles = totalProjectiles;
        this.maxPerSecond = maxPerSecond;
    }

    public SpawnProjectilesOnHealthLossAINode(int totalProjectiles) {
        this(totalProjectiles, 5);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    public float getTotalProjectiles(T mob) {
        return this.totalProjectiles;
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (!this.isPaused) {
            int nextHealth = ((Mob)mob).getHealth();
            int lostHealth = this.lastHealth - nextHealth;
            this.lastHealth = nextHealth;
            if (lostHealth > 0) {
                float modifier = (float)((Mob)mob).getMaxHealth() / (float)((Mob)mob).getMaxHealthFlat();
                float lifePerProjectile = (float)((Mob)mob).getMaxHealthFlat() / this.getTotalProjectiles(mob) * modifier;
                float increase = (float)lostHealth / lifePerProjectile;
                this.nextProjectile += Math.min((float)this.maxPerSecond / 20.0f, increase);
            }
            if (this.nextProjectile > 1.0f) {
                this.shootProjectile(mob);
                this.nextProjectile -= 1.0f;
            }
        }
        return AINodeResult.SUCCESS;
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        if (!this.isPaused) {
            return;
        }
        this.lastHealth = ((Mob)this.mob()).getHealth();
    }

    public abstract void shootProjectile(T var1);
}

