/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

public interface AchievementProviderInterface {
    public AchievementState getAchievementState(String var1);

    public static class AchievementState {
        public final boolean completed;
        public final Long completedTime;

        public AchievementState(boolean completed, Long completedTime) {
            this.completed = completed;
            this.completedTime = completedTime;
        }
    }
}

