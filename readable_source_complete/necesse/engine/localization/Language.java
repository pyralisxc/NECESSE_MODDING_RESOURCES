/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization;

import java.util.List;
import java.util.Objects;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;

public abstract class Language {
    public int updateUnique;
    public final String stringID;
    public final String steamAPICode;
    public final String englishDisplayName;
    public final String localDisplayName;
    public final String credits;

    public void updateUnique() {
        this.updateUnique = GameRandom.globalRandom.nextInt();
    }

    public Language(String stringID, String steamAPICode, String englishDisplayName, String localDisplayName, String credits) {
        Objects.requireNonNull(stringID);
        Objects.requireNonNull(englishDisplayName);
        Objects.requireNonNull(localDisplayName);
        this.stringID = stringID;
        this.steamAPICode = steamAPICode;
        this.englishDisplayName = englishDisplayName;
        this.localDisplayName = localDisplayName;
        this.credits = credits;
        this.updateUnique();
    }

    public void reload(List<LoadedMod> mods) {
    }

    public abstract String translate(String var1, String var2);

    public abstract boolean isMissing(String var1, String var2);

    public abstract boolean translationExists(String var1, String var2);

    public abstract boolean isSameAsEnglish(String var1, String var2);

    public void loadMod(LoadedMod mod) {
    }

    public void setCurrent() {
        Localization.setCurrentLang(this);
    }

    public boolean isDebugOnly() {
        return false;
    }

    public abstract String getCharactersUsed();

    public abstract int countTranslationWords();

    public abstract void exportTranslationCSV(String var1);

    public abstract void importTranslationCSV(String var1);

    public void addTooltips(ListGameTooltips tooltips) {
        if (!this.englishDisplayName.equals(this.localDisplayName)) {
            tooltips.add(this.englishDisplayName);
        }
        if (this.credits != null && !this.credits.isEmpty()) {
            tooltips.add(new SpacerGameTooltip(5));
            tooltips.add(this.credits, 500);
        }
    }
}

