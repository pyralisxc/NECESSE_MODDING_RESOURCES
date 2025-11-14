/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.zones;

public final class ZoneConstants {
    private static long pvpReentryCooldownMs = 6000L;
    private static float pvpSpawnImmunitySeconds = 5.0f;
    private static float defaultDamageMultiplier = 0.05f;
    private static int maxBarrierTiles = 10000;
    private static int barrierAddBatchSize = 500;
    private static int barrierMaxTilesPerTick = 1000;
    private static int defaultCombatLockSeconds = 3;

    private ZoneConstants() {
    }

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

    public static void setPvpReentryCooldownMs(long v) {
        pvpReentryCooldownMs = Math.max(0L, v);
    }

    public static void setPvpSpawnImmunitySeconds(float v) {
        pvpSpawnImmunitySeconds = Math.max(0.0f, v);
    }

    public static void setDefaultDamageMultiplier(float v) {
        defaultDamageMultiplier = Math.max(0.0f, v);
    }

    public static void setMaxBarrierTiles(int v) {
        maxBarrierTiles = Math.max(0, v);
    }

    public static void setBarrierAddBatchSize(int v) {
        barrierAddBatchSize = Math.max(1, v);
    }

    public static void setDefaultCombatLockSeconds(int v) {
        defaultCombatLockSeconds = Math.max(0, v);
    }

    public static void setBarrierMaxTilesPerTick(int v) {
        barrierMaxTilesPerTick = Math.max(1, v);
    }
}

