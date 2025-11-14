/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.Arrays;
import java.util.HashSet;

public class GameVersion {
    private static final HashSet<Character> ints = new HashSet<Character>(Arrays.asList(Character.valueOf('0'), Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'), Character.valueOf('5'), Character.valueOf('6'), Character.valueOf('7'), Character.valueOf('8'), Character.valueOf('9')));
    public final String versionString;
    private final int[] versions;

    public GameVersion(String versionString) {
        this.versionString = versionString;
        String[] split = versionString.split("\\.");
        this.versions = new int[split.length];
        for (int i = 0; i < split.length; ++i) {
            try {
                int j;
                for (j = 0; j < split[i].length() && ints.contains(Character.valueOf(split[i].charAt(j))); ++j) {
                }
                this.versions[i] = Integer.parseInt(split[i].substring(0, j));
                continue;
            }
            catch (NumberFormatException e) {
                this.versions[i] = Integer.MIN_VALUE;
            }
        }
    }

    public boolean isLaterThan(GameVersion other) {
        int max = Math.max(this.versions.length, other.versions.length);
        for (int i = 0; i < max; ++i) {
            if (i < this.versions.length) {
                if (i >= other.versions.length) {
                    return true;
                }
                if (this.versions[i] < other.versions[i]) {
                    return false;
                }
                if (this.versions[i] <= other.versions[i]) continue;
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isLaterThan(String versionString) {
        return this.isLaterThan(new GameVersion(versionString));
    }

    public boolean isEarlierThan(GameVersion other) {
        int max = Math.max(this.versions.length, other.versions.length);
        for (int i = 0; i < max; ++i) {
            if (i < this.versions.length) {
                if (i >= other.versions.length) {
                    return false;
                }
                if (this.versions[i] < other.versions[i]) {
                    return true;
                }
                if (this.versions[i] <= other.versions[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isEarlierThan(String versionString) {
        return this.isEarlierThan(new GameVersion(versionString));
    }

    public String toString() {
        return this.versionString;
    }
}

