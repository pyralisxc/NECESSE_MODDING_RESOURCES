/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

public static class AchievementProviderInterface.AchievementState {
    public final boolean completed;
    public final Long completedTime;

    public AchievementProviderInterface.AchievementState(boolean completed, Long completedTime) {
        this.completed = completed;
        this.completedTime = completedTime;
    }
}
