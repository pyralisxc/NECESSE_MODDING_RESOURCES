/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.Language;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.fileLanguage.FileLanguage;
import necesse.engine.localization.fileLanguage.Translation;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.LocalReplacement;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.gfx.gameFont.FontManager;

public class Localization {
    private static final Object LOCK = new Object();
    private static final ArrayList<Language> officialLanguages = new ArrayList();
    private static final ArrayList<Language> languages = new ArrayList();
    public static final Translation EnglishTranslation;
    public static final Language NoTranslation;
    public static final Language English;
    public static final Language ChineseSimplified;
    public static final Language ChineseTraditional;
    public static final Language Russian;
    public static final Language BrazilianPortuguese;
    public static final Language Spanish;
    public static final Language German;
    public static final Language Polish;
    public static final Language Czech;
    public static final Language Turkish;
    public static final Language Japanese;
    public static final Language French;
    public static final Language Ukrainian;
    public static final Language Danish;
    public static final Language Swedish;
    public static final Language Norwegian;
    public static final Language Hungarian;
    public static final Language Italian;
    public static final Language Korean;
    public static final Language Thai;
    public static final Language Vietnamese;
    public static final Language Indonesian;
    public static final Language Lithuanian;
    public static final Language Dutch;
    public static final Language Finnish;
    public static final Language Croatian;
    public static final Language Catalan;
    public static final Language Arabic;
    public static final Language defaultLang;
    private static Language currentLang;
    private static final LinkedList<LocalizationChangeListener> nextListeners;
    private static final LinkedList<LocalizationChangeListener> listeners;
    private static final HashMap<Integer, Long> warnTimers;
    private static final int warnCooldown = 10000;

    public static <T extends Language> T registerLanguage(T language) {
        return Localization.registerLanguage(language, false);
    }

    private static <T extends Language> T registerLanguage(T language, boolean isOfficial) {
        if (!language.stringID.matches("[a-zA-Z0-9_\\-]+")) {
            throw new IllegalArgumentException("Tried to register language with invalid stringID: \"" + language.stringID + "\"");
        }
        if (languages.stream().anyMatch(l -> l.stringID.equals(language.stringID))) {
            throw new IllegalArgumentException("Tried to register language with duplicate stringID: \"" + language.stringID + "\"");
        }
        if (isOfficial) {
            officialLanguages.add(language);
        }
        languages.add(language);
        return language;
    }

    public static void reloadLanguageFiles() {
        for (Language language : languages) {
            language.reload(ModLoader.getEnabledMods());
            language.updateUnique();
        }
        Localization.updateListeners();
    }

    public static Language getLanguageStringID(String stringID) {
        return languages.stream().filter(l -> l.stringID.equals(stringID)).findFirst().orElse(null);
    }

    public static boolean isOfficial(Language language) {
        return officialLanguages.stream().filter(l -> l == language).findFirst().orElse(null) != null;
    }

    public static Language getCurrentLang() {
        return currentLang;
    }

    private static void warn(String category, String translationKey) {
        int hash = (category + "." + translationKey).hashCode();
        long timer = 0L;
        if (warnTimers.containsKey(hash)) {
            timer = warnTimers.get(hash);
        }
        if (timer < System.currentTimeMillis()) {
            GameLog.warn.println("Translation of " + category + "." + translationKey + " is not found.");
            warnTimers.put(hash, System.currentTimeMillis() + 10000L);
        }
    }

    public static void setCurrentLang(Language lang) {
        Localization.setCurrentLang(lang, true);
    }

    private static void setCurrentLang(Language lang, boolean updateListeners) {
        if (Localization.getCurrentLang() == lang) {
            return;
        }
        currentLang = lang;
        GameLog.debug.println("Changed current language to " + lang.stringID);
        if (FontManager.bit != null && lang != defaultLang) {
            long time = System.currentTimeMillis();
            FontManager.updateFont(lang);
            GameLog.debug.println("Font update took " + (System.currentTimeMillis() - time) + " ms");
        }
        if (updateListeners) {
            Localization.updateListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void updateListeners() {
        Object object = LOCK;
        synchronized (object) {
            listeners.addAll(nextListeners);
            nextListeners.clear();
            Localization.cleanListeners();
            for (LocalizationChangeListener listener : listeners) {
                listener.onChange(currentLang);
            }
        }
    }

    public static void loadModsLanguage() {
        for (Language language : languages) {
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                language.loadMod(mod);
            }
        }
    }

    public static Language[] getLanguages() {
        return languages.toArray(new Language[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void cleanListeners() {
        Object object = LOCK;
        synchronized (object) {
            listeners.removeIf(LocalizationChangeListener::isDisposed);
            nextListeners.removeIf(LocalizationChangeListener::isDisposed);
        }
    }

    public static int getListenersSize() {
        return listeners.size() + nextListeners.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static LocalizationChangeListener addListener(LocalizationChangeListener listener) {
        Object object = LOCK;
        synchronized (object) {
            nextListeners.add(listener);
        }
        return listener;
    }

    public static String translate(Language language, String category, String translationKey, boolean debug) {
        String message = language.translate(category, translationKey);
        if (message == null && (message = defaultLang.translate(category, translationKey)) == null) {
            if (debug) {
                Localization.warn(category, translationKey);
            }
            return category + "." + translationKey;
        }
        return message;
    }

    public static String translate(Language language, String category, String translationKey) {
        return Localization.translate(language, category, translationKey, true);
    }

    public static String translate(String category, String translationKey, boolean debug) {
        return Localization.translate(Localization.getCurrentLang(), category, translationKey, debug);
    }

    public static String translate(String category, String translationKey) {
        return Localization.translate(category, translationKey, true);
    }

    public static String translate(String category, String key, String replaceKey, String replacement) {
        return new LocalMessage(category, key, replaceKey, replacement).translate();
    }

    public static String translate(String category, String key, String replaceKey, Object replacement) {
        return new LocalMessage(category, key, replaceKey, replacement.toString()).translate();
    }

    public static String translate(String category, String key, String ... replacements) {
        return new LocalMessage(category, key, replacements).translate();
    }

    public static String translate(String category, String key, Object ... replacements) {
        return new LocalMessage(category, key, replacements).translate();
    }

    public static String translate(String category, String key, ArrayList<LocalReplacement> replacements) {
        return new LocalMessage(category, key, replacements).translate();
    }

    static {
        NoTranslation = Localization.registerLanguage(new Language("no_translation", null, "No translation", "No translation", null){

            @Override
            public String translate(String category, String key) {
                return category + "." + key;
            }

            @Override
            public boolean isMissing(String category, String key) {
                return false;
            }

            @Override
            public boolean translationExists(String category, String key) {
                return false;
            }

            @Override
            public boolean isSameAsEnglish(String category, String key) {
                return false;
            }

            @Override
            public int countTranslationWords() {
                return 0;
            }

            @Override
            public void exportTranslationCSV(String path) {
            }

            @Override
            public void importTranslationCSV(String csvFilePath) {
            }

            @Override
            public boolean isDebugOnly() {
                return true;
            }

            @Override
            public String getCharactersUsed() {
                return "";
            }
        }, true);
        EnglishTranslation = new Translation("en.lang");
        English = Localization.registerLanguage(new FileLanguage("en", "english", EnglishTranslation, "English", "English"), true);
        ChineseSimplified = Localization.registerLanguage(new FileLanguage("zh-CN", "schinese", new Translation("zh-CN.lang", EnglishTranslation), "Chinese - Simplified", "\u7b80\u4f53\u4e2d\u6587"), false);
        ChineseTraditional = Localization.registerLanguage(new FileLanguage("zh-TW", "tchinese", new Translation("zh-TW.lang", EnglishTranslation), "Chinese - Traditional", "\u7e41\u9ad4\u4e2d\u6587"), false);
        Russian = Localization.registerLanguage(new FileLanguage("ru", "russian", new Translation("ru.lang", EnglishTranslation), "Russian", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439"), false);
        BrazilianPortuguese = Localization.registerLanguage(new FileLanguage("pt-BR", "brazilian", new Translation("pt-BR.lang", EnglishTranslation), "Portuguese - Brazil", "Portugu\u00eas - Brasil"), false);
        Spanish = Localization.registerLanguage(new FileLanguage("es", "spanish", new Translation("es.lang", EnglishTranslation), "Spanish", "Espa\u00f1ol"), false);
        German = Localization.registerLanguage(new FileLanguage("de", "german", new Translation("de.lang", EnglishTranslation), "German", "Deutsch"), false);
        Polish = Localization.registerLanguage(new FileLanguage("pl", "polish", new Translation("pl.lang", EnglishTranslation), "Polish", "Polski"), false);
        Czech = Localization.registerLanguage(new FileLanguage("cs", "czech", new Translation("cs.lang", EnglishTranslation), "Czech", "\u010ce\u0161tina"), false);
        Turkish = Localization.registerLanguage(new FileLanguage("tr", "turkish", new Translation("tr.lang", EnglishTranslation), "Turkish", "T\u00fcrk\u00e7e"), false);
        Japanese = Localization.registerLanguage(new FileLanguage("ja", "japanese", new Translation("ja.lang", EnglishTranslation), "Japanese", "\u65e5\u672c\u8a9e"), false);
        French = Localization.registerLanguage(new FileLanguage("fr", "french", new Translation("fr.lang", EnglishTranslation), "French", "Fran\u00e7ais"), false);
        Ukrainian = Localization.registerLanguage(new FileLanguage("uk", "ukrainian", new Translation("uk.lang", EnglishTranslation), "Ukrainian", "\u0423\u043a\u0440\u0430\u0457\u043d\u0441\u044c\u043a\u0430"), false);
        Danish = Localization.registerLanguage(new FileLanguage("da", "danish", new Translation("da.lang", EnglishTranslation), "Danish", "Dansk"), false);
        Swedish = Localization.registerLanguage(new FileLanguage("se", "swedish", new Translation("se.lang", EnglishTranslation), "Swedish", "Svenska"), false);
        Norwegian = Localization.registerLanguage(new FileLanguage("no", "norwegian", new Translation("no.lang", EnglishTranslation), "Norwegian", "Norsk"), false);
        Hungarian = Localization.registerLanguage(new FileLanguage("hu", "hungarian", new Translation("hu.lang", EnglishTranslation), "Hungarian", "Magyar"), false);
        Italian = Localization.registerLanguage(new FileLanguage("it", "italian", new Translation("it.lang", EnglishTranslation), "Italian", "Italiano"), false);
        Korean = Localization.registerLanguage(new FileLanguage("kr", "koreana", new Translation("kr.lang", EnglishTranslation), "Korean", "\ud55c\uad6d\uc5b4"), false);
        Thai = Localization.registerLanguage(new FileLanguage("th", "thai", new Translation("th.lang", EnglishTranslation), "Thai", "\u0e44\u0e17\u0e22"), false);
        Vietnamese = Localization.registerLanguage(new FileLanguage("vi", "vietnamese", new Translation("vi.lang", EnglishTranslation), "Vietnamese", "Ti\u1ebfng Vi\u1ec7t"), false);
        Indonesian = Localization.registerLanguage(new FileLanguage("id", "indonesian", new Translation("id.lang", EnglishTranslation), "Indonesian", "Indonesia"), false);
        Lithuanian = Localization.registerLanguage(new FileLanguage("lt", "lithuanian", new Translation("lt.lang", EnglishTranslation), "Lithuanian", "Lietuvi\u0161kai"), false);
        Dutch = Localization.registerLanguage(new FileLanguage("nl", "dutch", new Translation("nl.lang", EnglishTranslation), "Dutch", "Nederlands"), false);
        Finnish = Localization.registerLanguage(new FileLanguage("fi", "finnish", new Translation("fi.lang", EnglishTranslation), "Finnish", "Suomi"), false);
        Croatian = Localization.registerLanguage(new FileLanguage("hr", "croatian", new Translation("hr.lang", EnglishTranslation), "Croatian", "Hrvatski"), false);
        Catalan = Localization.registerLanguage(new FileLanguage("ca", "catalan", new Translation("ca.lang", EnglishTranslation), "Catalan", "Catal\u00e0"), false);
        Arabic = Localization.registerLanguage(new FileLanguage("ar", "arabic", new Translation("ar.lang", EnglishTranslation), "Arabic", "\u0627\u0644\u0639\u0631\u0628\u064a\u0629"), false);
        String[] ignoredFiles = new String[]{".DS_Store", ".gitignore"};
        Pattern namePattern = Pattern.compile("([a-zA-Z0-9-_]+)\\.lang");
        File[] files = new File(GlobalData.rootPath() + "locale/").listFiles();
        if (files != null) {
            for (File file : files) {
                if (Arrays.stream(ignoredFiles).anyMatch(s -> s.equals(file.getName()))) continue;
                Matcher matcher = namePattern.matcher(file.getName());
                if (file.isFile() && matcher.find()) {
                    String stringID = matcher.group(1);
                    if (languages.stream().anyMatch(l -> l.stringID.equals(stringID))) continue;
                    String err = FileLanguage.loadFileLanguage(stringID, file.getName());
                    if (err != null) {
                        GameLog.warn.println("Error loading " + file.getName() + ": " + err);
                        continue;
                    }
                    System.out.println("Registered unsupported language " + stringID);
                    continue;
                }
                GameLog.warn.println("Locale file " + file.getName() + " does not match name requirements");
            }
        }
        currentLang = defaultLang = English;
        nextListeners = new LinkedList();
        listeners = new LinkedList();
        warnTimers = new HashMap();
    }
}

