/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.ui;

public class PlayerDropdownEntry
implements Comparable<PlayerDropdownEntry> {
    public final String characterName;
    public final long steamAuth;
    public final boolean isOnline;
    public final long lastLogin;

    public PlayerDropdownEntry(String characterName, long steamAuth, boolean isOnline, long lastLogin) {
        this.characterName = characterName;
        this.steamAuth = steamAuth;
        this.isOnline = isOnline;
        this.lastLogin = lastLogin;
    }

    @Override
    public int compareTo(PlayerDropdownEntry other) {
        if (this.isOnline != other.isOnline) {
            return this.isOnline ? -1 : 1;
        }
        return this.characterName.compareToIgnoreCase(other.characterName);
    }

    public boolean matchesFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return true;
        }
        String lowerFilter = filter.toLowerCase();
        return this.characterName.toLowerCase().contains(lowerFilter) || String.valueOf(this.steamAuth).contains(lowerFilter);
    }
}

