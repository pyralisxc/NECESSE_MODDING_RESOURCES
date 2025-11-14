/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.fileLanguage;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.fileLanguage.Translation;
import necesse.engine.localization.fileLanguage.TranslationCategory;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class FileLanguage
extends Language {
    public static final String langsPath = "locale/";
    private final Translation translation;

    public FileLanguage(String stringID, String steamAPICode, Translation translation, String englishDisplayName, String localDisplayName) {
        super(stringID, steamAPICode, englishDisplayName, localDisplayName, translation.translate("lang", "credits"));
        this.translation = translation;
    }

    public String getFileName() {
        return this.translation.fileName;
    }

    public String getFilePath() {
        return GlobalData.rootPath() + langsPath + "/" + this.translation.fileName;
    }

    @Override
    public void reload(List<LoadedMod> mods) {
        this.translation.loadLanguageFile();
        for (LoadedMod mod : mods) {
            this.translation.loadModLanguageFile(mod);
        }
    }

    @Override
    public void loadMod(LoadedMod mod) {
        this.translation.loadModLanguageFile(mod);
    }

    public TranslationCategory getCategory(String category) {
        return this.translation.getCategory(category);
    }

    @Override
    public String translate(String category, String key) {
        return this.translation.translate(category, key);
    }

    @Override
    public boolean isMissing(String category, String key) {
        return this.translation.isMissing(category, key);
    }

    @Override
    public boolean translationExists(String category, String key) {
        return this.translation.exists(category, key);
    }

    @Override
    public boolean isSameAsEnglish(String category, String key) {
        return this.translation.isSameAsEnglish(category, key);
    }

    @Override
    public int countTranslationWords() {
        return this.translation.countTranslationWords();
    }

    @Override
    public void exportTranslationCSV(String path) {
        this.translation.exportTranslationCSV(GlobalData.rootPath() + path + "/" + GameUtils.removeFileExtension(this.translation.fileName) + ".csv");
    }

    @Override
    public void importTranslationCSV(String csvFilePath) {
        this.translation.importTranslationCSV(this.getFilePath(), csvFilePath);
    }

    @Override
    public String getCharactersUsed() {
        LinkedHashSet chars = new LinkedHashSet();
        this.translation.streamTranslations(true, true).forEach(t -> t.translation.chars().forEach(i -> {
            if (chars.contains(i)) {
                return;
            }
            chars.add(i);
        }));
        StringBuilder builder = new StringBuilder(chars.size());
        Iterator iterator = chars.iterator();
        while (iterator.hasNext()) {
            int ch = (Integer)iterator.next();
            builder.append((char)ch);
        }
        return builder.toString();
    }

    @Override
    public void addTooltips(ListGameTooltips tooltips) {
        super.addTooltips(tooltips);
        this.translation.addCoverageTooltips(tooltips);
    }

    public void fixAndPrintLanguageFile(FileLanguage other, String filePath, boolean addMissingTranslations) {
        this.translation.fixAndPrintLanguageFile(other.translation, filePath, addMissingTranslations);
    }

    public static String loadFileLanguage(String stringID, String fileName) {
        try {
            if (Localization.getLanguageStringID(stringID) != null) {
                return stringID + " language already registered";
            }
            File file = new File(GlobalData.rootPath() + langsPath + "/" + fileName);
            if (file.exists()) {
                Translation translation = new Translation(fileName, Localization.EnglishTranslation);
                String localName = translation.translate("lang", "localname");
                String engName = translation.translate("lang", "engname");
                if (localName == null) {
                    return "Missing lang.localname translation";
                }
                if (engName == null) {
                    return "Missing lang.engname translation";
                }
                Localization.registerLanguage(new FileLanguage(stringID, null, translation, engName, localName));
                return null;
            }
            return fileName + " does not exist";
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

