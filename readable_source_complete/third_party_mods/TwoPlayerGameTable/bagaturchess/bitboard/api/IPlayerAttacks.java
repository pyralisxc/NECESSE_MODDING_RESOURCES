/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IPlayerAttacks {
    public static final boolean DEBUG_ATTACKS = false;
    public static final boolean SUPPORT_HIDDEN_ATTACKS = false;

    public long allAttacks();

    public long attacksByType(int var1);

    public int attacksByTypeUnintersectedSize(int var1);

    public long[] attacksByTypeUnintersected(int var1);

    public long attacksByFieldID(int var1, int var2);

    public int countAttacks(int var1, long var2);

    public void checkConsistency();
}

