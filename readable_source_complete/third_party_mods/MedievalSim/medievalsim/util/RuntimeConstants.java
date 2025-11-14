/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.util;

public final class RuntimeConstants {
    private RuntimeConstants() {
    }

    public static final class BuildMode {
        private BuildMode() {
        }
    }

    public static final class Zones {
        private static long pvpReentryCooldownMs = 6000L;
        private static float pvpSpawnImmunitySeconds = 5.0f;
        private static float defaultDamageMultiplier = 0.05f;
        private static int maxBarrierTiles = 10000;
        private static int barrierAddBatchSize = 500;
        private static int barrierMaxTilesPerTick = 1000;
        private static int defaultCombatLockSeconds = 10;

        public static long getPvpReentryCooldownMs() {
            return pvpReentryCooldownMs;
        }

        public static float getPvpSpawnImmunitySeconds() {
            return pvpSpawnImmunitySeconds;
        }

        public static float getDefaultDamageMultiplier() {
            return defaultDamageMultiplier;
        }

        public static int getMaxBarrierTiles() {
            return maxBarrierTiles;
        }

        public static int getBarrierAddBatchSize() {
            return barrierAddBatchSize;
        }

        public static int getBarrierMaxTilesPerTick() {
            return barrierMaxTilesPerTick;
        }

        public static int getDefaultCombatLockSeconds() {
            return defaultCombatLockSeconds;
        }

        public static void setPvpReentryCooldownMs(long value) {
            pvpReentryCooldownMs = Math.max(0L, value);
        }

        public static void setPvpSpawnImmunitySeconds(float value) {
            pvpSpawnImmunitySeconds = Math.max(0.0f, value);
        }

        public static void setDefaultDamageMultiplier(float value) {
            defaultDamageMultiplier = Math.max(0.0f, Math.min(1.0f, value));
        }

        public static void setMaxBarrierTiles(int value) {
            maxBarrierTiles = Math.max(0, value);
        }

        public static void setBarrierAddBatchSize(int value) {
            barrierAddBatchSize = Math.max(1, value);
        }

        public static void setBarrierMaxTilesPerTick(int value) {
            barrierMaxTilesPerTick = Math.max(1, value);
        }

        public static void setDefaultCombatLockSeconds(int value) {
            defaultCombatLockSeconds = Math.max(0, value);
        }

        private Zones() {
        }
    }
}

