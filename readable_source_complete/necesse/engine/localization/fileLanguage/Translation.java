/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.fileLanguage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.fileLanguage.TranslationCategory;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeItemParser;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;

public class Translation {
    public static String MISSING_TRANSLATION_PREPEND = "MISSING_TRANSLATION:";
    public static String SAME_TRANSLATION_PREPEND = "SAME_TRANSLATION:";
    public final String fileName;
    private final Translation compareCoverage;
    private int totalUniqueTranslations;
    private final HashMap<String, LinkedList<LineData>> linesBeforeCategory = new HashMap();
    private final HashMap<String, LinkedList<LineData>> linesBeforeTranslation = new HashMap();
    private final HashMap<String, LinkedList<LineData>> linesAfterCategory = new HashMap();
    private final HashMap<String, LinkedList<LineData>> linesAfterTranslation = new HashMap();
    private final ArrayList<LineData> lines = new ArrayList();
    private HashMap<String, TranslationCategory> categories;

    public Translation(String fileName, Translation compareCoverage) {
        this.fileName = fileName;
        this.compareCoverage = compareCoverage;
        this.loadLanguageFile();
    }

    public Translation(String fileName) {
        this(fileName, null);
    }

    public void loadLanguageFile() {
        this.categories = new HashMap();
        try {
            File file = new File(GlobalData.rootPath() + "locale/" + "/" + this.fileName);
            InputStreamReader isr = new InputStreamReader((InputStream)new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            this.loadLanguageFile(br, null);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadModLanguageFile(LoadedMod mod) {
        try {
            Enumeration<JarEntry> entries = mod.jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if (!path.startsWith("resources/") || !path.endsWith(this.fileName)) continue;
                InputStreamReader isr = new InputStreamReader(mod.jarFile.getInputStream(entry), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                this.loadLanguageFile(br, mod);
                br.close();
                break;
            }
        }
        catch (IOException e) {
            System.err.println("Could not load mod " + mod.id + " language file " + this.fileName);
            e.printStackTrace();
        }
    }

    private void loadLanguageFile(BufferedReader br, LoadedMod mod) throws IOException {
        String line;
        TranslationCategory currentCategory = this.categories.get("null");
        if (currentCategory == null) {
            currentCategory = new TranslationCategory("null");
        }
        LinkedList<LineData> miscLines = new LinkedList<LineData>();
        boolean setCategoryLastLine = false;
        SetTranslationLineData lastTranslation = null;
        while ((line = br.readLine()) != null) {
            int endCat;
            if (line.length() == 0) {
                if (setCategoryLastLine) {
                    if (!miscLines.isEmpty()) {
                        this.linesAfterCategory.put(currentCategory.name, miscLines);
                    }
                    miscLines = new LinkedList();
                } else if (lastTranslation != null) {
                    if (!miscLines.isEmpty()) {
                        this.linesAfterTranslation.put(lastTranslation.category + "." + lastTranslation.key, miscLines);
                    }
                    miscLines = new LinkedList();
                }
                miscLines.add(new LineData(LineType.EMPTY, line));
                this.lines.add(new LineData(LineType.EMPTY, line));
                setCategoryLastLine = false;
                lastTranslation = null;
                continue;
            }
            if (line.startsWith("//")) {
                miscLines.add(new LineData(LineType.COMMENT, line));
                this.lines.add(new LineData(LineType.COMMENT, line));
                continue;
            }
            if (line.startsWith("[") && (endCat = line.indexOf("]")) != -1) {
                String newCategoryName;
                if (currentCategory.getTotalTranslations() > 0) {
                    this.categories.put(currentCategory.name, currentCategory);
                }
                currentCategory = this.categories.containsKey(newCategoryName = line.substring(1, endCat)) ? this.categories.get(newCategoryName) : new TranslationCategory(newCategoryName);
                if (!miscLines.isEmpty()) {
                    this.linesBeforeCategory.put(newCategoryName, miscLines);
                }
                miscLines = new LinkedList();
                this.lines.add(new SetCategoryLineData(line, newCategoryName));
                lastTranslation = null;
                setCategoryLastLine = true;
                continue;
            }
            int equalIndex = line.indexOf(61);
            if (equalIndex == -1) {
                this.lines.add(new LineData(LineType.UNKNOWN, line));
                continue;
            }
            String key = line.substring(0, equalIndex);
            boolean sameTranslation = false;
            boolean missingTranslation = false;
            if (key.startsWith(MISSING_TRANSLATION_PREPEND)) {
                key = key.substring(MISSING_TRANSLATION_PREPEND.length());
                missingTranslation = true;
            } else if (key.startsWith(SAME_TRANSLATION_PREPEND)) {
                sameTranslation = true;
                key = key.substring(SAME_TRANSLATION_PREPEND.length());
            }
            if (currentCategory.name.equals("lang")) {
                sameTranslation = true;
            }
            String translation = line.substring(equalIndex + 1);
            translation = translation.replace("\\n", "\n");
            currentCategory.addTranslation(this.fileName, key, translation, sameTranslation, missingTranslation, mod);
            if (this.compareCoverage == null) {
                ++this.totalUniqueTranslations;
            } else {
                String compareTranslation = this.compareCoverage.translate(currentCategory.name, key);
                if (compareTranslation != null && (sameTranslation || !compareTranslation.equals(translation))) {
                    ++this.totalUniqueTranslations;
                }
            }
            if (!miscLines.isEmpty()) {
                this.linesBeforeTranslation.put(currentCategory.name + "." + key, miscLines);
            }
            miscLines = new LinkedList();
            lastTranslation = new SetTranslationLineData(line, currentCategory.name, key, translation);
            this.lines.add(lastTranslation);
            setCategoryLastLine = false;
        }
        if (currentCategory.getTotalTranslations() > 0) {
            this.categories.put(currentCategory.name, currentCategory);
        }
    }

    public TranslationCategory getCategory(String category) {
        return this.categories.get(category);
    }

    public String translate(String category, String key) {
        TranslationCategory cat = this.getCategory(category);
        if (cat != null) {
            return cat.translate(key);
        }
        return null;
    }

    public boolean isMissing(String category, String key) {
        TranslationCategory cat = this.categories.get(category);
        if (cat != null) {
            return cat.isMissing(key);
        }
        return true;
    }

    public boolean exists(String category, String key) {
        TranslationCategory cat = this.categories.get(category);
        if (cat != null) {
            return cat.exists(key);
        }
        return true;
    }

    public boolean isSameAsEnglish(String category, String key) {
        TranslationCategory cat = this.categories.get(category);
        if (cat != null) {
            return cat.isSameAsEnglish(key);
        }
        return false;
    }

    public Stream<TranslationData> streamTranslations(boolean acceptSameTranslations, boolean acceptMissingTranslations) {
        return this.categories.entrySet().stream().flatMap(c -> {
            Stream<Map.Entry<String, String>> translationStream = ((TranslationCategory)c.getValue()).streamTranslations();
            if (!acceptSameTranslations) {
                translationStream = translationStream.filter(t -> !((TranslationCategory)c.getValue()).isSameAsEnglish((String)t.getKey()));
            }
            if (!acceptMissingTranslations) {
                translationStream = translationStream.filter(t -> !((TranslationCategory)c.getValue()).isMissing((String)t.getKey()));
            }
            return translationStream.map(t -> new TranslationData((String)c.getKey(), (String)t.getKey(), (String)t.getValue()));
        });
    }

    public Stream<TranslationData> streamStrippedTranslations(boolean acceptSameTranslations, boolean acceptMissingTranslations) {
        return this.streamTranslations(acceptSameTranslations, acceptMissingTranslations).map(t -> {
            String stripped = t.translation.replaceAll("<([^>])+>", "").replaceAll("\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))", "").replace("\\n", " ").replaceAll(TypeParsers.INPUT_PATTERN.pattern(), " ").replaceAll(TypeItemParser.ITEM_PATTERN.pattern(), " ").replaceAll(TypeItemParser.ITEMS_PATTERN.pattern(), " ").replaceAll("\\s+", " ").trim();
            return new TranslationData(t.category, t.key, stripped);
        });
    }

    public void forEachTranslation(Consumer<TranslationData> action, boolean acceptSameTranslations, boolean acceptMissingTranslations) {
        this.categories.forEach((cName, c) -> c.forEachTranslations((k, t) -> {
            if (!(!acceptSameTranslations && c.isSameAsEnglish((String)k) || !acceptMissingTranslations && c.isMissing((String)k))) {
                action.accept(new TranslationData((String)cName, (String)k, (String)t));
            }
        }));
    }

    public void debugReload() {
        this.loadLanguageFile();
    }

    public void printTranslations() {
        System.out.println("Translations in file:");
        System.out.println(this.fileName);
        System.out.println("--------------------------------");
        for (String category : this.categories.keySet()) {
            System.out.println("[" + category + "]");
            this.categories.get(category).printTranslations(" ");
        }
    }

    public void addCoverageTooltips(ListGameTooltips tooltips) {
        if (this.compareCoverage != null) {
            tooltips.add(new SpacerGameTooltip(5));
            double percent = (double)this.totalUniqueTranslations / (double)this.compareCoverage.totalUniqueTranslations;
            int percentDisplay = (int)Math.floor(percent * 100.0);
            tooltips.add(Localization.translate("settingsui", "translationcoverage", "percent", percentDisplay + "%"));
        }
    }

    /*
     * WARNING - void declaration
     */
    public void fixAndPrintLanguageFile(Translation other, String filePath, boolean addMissingTranslations) {
        ArrayList<String> fixedLines = new ArrayList<String>();
        for (int i = 0; i < this.lines.size(); ++i) {
            void var10_24;
            LinkedList<LineData> linkedList;
            LinkedList<LineData> linesBefore;
            LineData myLine = this.lines.get(i);
            if (myLine.type == LineType.COMMENT || myLine.type == LineType.EMPTY || myLine.type == LineType.UNKNOWN) continue;
            if (myLine.type == LineType.SET_CATEGORY) {
                SetCategoryLineData categoryLine = (SetCategoryLineData)myLine;
                linesBefore = other.linesBeforeCategory.get(categoryLine.category);
                if (linesBefore == null) {
                    linesBefore = this.linesBeforeCategory.getOrDefault(categoryLine.category, new LinkedList());
                }
                for (LineData lineData : linesBefore) {
                    fixedLines.add(lineData.fullLine);
                }
                fixedLines.add(myLine.fullLine);
                LinkedList linesAfter2 = other.linesAfterCategory.get(categoryLine.category);
                if (linesAfter2 == null) {
                    linesAfter2 = this.linesAfterCategory.getOrDefault(categoryLine.category, new LinkedList());
                }
                for (LineData line : linesAfter2) {
                    fixedLines.add(line.fullLine);
                }
                continue;
            }
            if (myLine.type != LineType.SET_TRANSLATION) continue;
            SetTranslationLineData myTranslationLine = (SetTranslationLineData)myLine;
            linesBefore = other.linesBeforeTranslation.get(myTranslationLine.category + "." + myTranslationLine.key);
            if (linesBefore == null) {
                linesBefore = this.linesAfterTranslation.getOrDefault(myTranslationLine.category + "." + myTranslationLine.key, new LinkedList());
            }
            for (LineData lineData : linesBefore) {
                fixedLines.add(lineData.fullLine);
            }
            String otherTranslation = other.translate(myTranslationLine.category, myTranslationLine.key);
            if (otherTranslation == null || !other.isSameAsEnglish(myTranslationLine.category, myTranslationLine.key) && myTranslationLine.translation.equals(otherTranslation)) {
                if (addMissingTranslations) {
                    fixedLines.add(MISSING_TRANSLATION_PREPEND + myTranslationLine.fullLine);
                }
            } else {
                String string = myTranslationLine.translation;
                boolean partOfLang = myTranslationLine.category.equals("lang");
                if (partOfLang) {
                    fixedLines.add(myTranslationLine.key + "=" + otherTranslation.replace("\n", "\\n"));
                } else {
                    HashSet myReplacementKeys = new HashSet();
                    HashSet otherReplacementKeys = new HashSet();
                    GameUtils.forEachMatcherResult(LocalMessage.replaceRegex, string, result -> myReplacementKeys.add(result.group(1)));
                    GameUtils.forEachMatcherResult(LocalMessage.replaceRegex, otherTranslation, result -> otherReplacementKeys.add(result.group(1)));
                    if (!myReplacementKeys.equals(otherReplacementKeys)) {
                        if (addMissingTranslations) {
                            fixedLines.add(MISSING_TRANSLATION_PREPEND + myTranslationLine.fullLine);
                        }
                    } else {
                        boolean sameTranslation = other.isSameAsEnglish(myTranslationLine.category, myTranslationLine.key);
                        String fullLine = (sameTranslation ? SAME_TRANSLATION_PREPEND : "") + myTranslationLine.key + "=" + otherTranslation.replace("\n", "\\n");
                        fixedLines.add(fullLine);
                    }
                }
            }
            if ((linkedList = other.linesAfterTranslation.get(myTranslationLine.category + "." + myTranslationLine.key)) == null) {
                LinkedList linkedList2 = this.linesAfterTranslation.getOrDefault(myTranslationLine.category + "." + myTranslationLine.key, new LinkedList());
            }
            for (LineData line : var10_24) {
                fixedLines.add(line.fullLine);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath, new String[0]), StandardCharsets.UTF_8, new OpenOption[0]);){
            Iterator it = fixedLines.iterator();
            while (it.hasNext()) {
                writer.write((String)it.next());
                if (!it.hasNext()) continue;
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int countTranslationWords() {
        return this.streamStrippedTranslations(true, false).mapToInt(t -> t.translation.split(" ").length).sum();
    }

    public void exportTranslationCSV(String path) {
        File file = new File(path);
        try {
            GameUtils.mkDirs(file);
            FileWriter os = new FileWriter(file);
            this.forEachTranslation(data -> {
                try {
                    os.append("\"").append(data.category).append(".").append(data.key).append("\"").append(",").append("\"").append(data.translation.replace("\n", "\\n")).append("\"").append("\n");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, true, false);
            os.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importTranslationCSV(String langFilePath, String csvFilePath) {
        HashMap<String, TranslationCategory> csvCategories = new HashMap<String, TranslationCategory>();
        Pattern regex = Pattern.compile("(?:^\"|,\")(\"\"|[\\w\\W]*?)(?=\",|\"$)|(?:^(?!\")|,(?!\"))([^,]*?)(?=$|,)|(\\r\\n|\\n)");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvFilePath, new String[0]), StandardCharsets.UTF_8);){
            String line;
            while ((line = reader.readLine()) != null) {
                String category;
                String readLine;
                while (!line.endsWith("\"") && (readLine = reader.readLine()) != null) {
                    line = line + "\\n" + readLine;
                }
                Matcher matcher = regex.matcher(line);
                if (!matcher.find()) {
                    System.out.println("COULD NOT FIND KEY IN " + line + " FOR " + langFilePath);
                    continue;
                }
                String key = matcher.group(1);
                if (!matcher.find()) {
                    System.out.println("COULD NOT FIND TRANSLATION IN " + line + " FOR " + langFilePath);
                    continue;
                }
                String translation = matcher.group(1);
                translation = translation.replace("\"\"", "\"");
                int split = key.indexOf(".");
                if (split != -1) {
                    category = key.substring(0, split);
                    key = key.substring(split + 1);
                } else {
                    category = "null";
                }
                String finalKey = key;
                String finalTranslation = translation;
                csvCategories.compute(category, (s, last) -> {
                    if (last == null) {
                        last = new TranslationCategory(category);
                    }
                    last.addTranslation(this.fileName, finalKey, finalTranslation, false, false, null);
                    return last;
                });
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> fixedLines = new ArrayList<String>();
        for (int i = 0; i < this.lines.size(); ++i) {
            LineData myLine = this.lines.get(i);
            if (myLine.type == LineType.SET_TRANSLATION) {
                SetTranslationLineData myTranslationLine = (SetTranslationLineData)myLine;
                TranslationCategory csvCategory = (TranslationCategory)csvCategories.get(myTranslationLine.category);
                if (csvCategory != null) {
                    String csvTranslation = csvCategory.translate(myTranslationLine.key);
                    if (csvTranslation != null && (!csvTranslation.equals(myTranslationLine.translation) || this.categories.get(myTranslationLine.category).isMissing(myTranslationLine.key))) {
                        fixedLines.add(myTranslationLine.key + "=" + csvTranslation);
                        continue;
                    }
                    fixedLines.add(myTranslationLine.fullLine);
                    continue;
                }
                fixedLines.add(myTranslationLine.fullLine);
                continue;
            }
            fixedLines.add(myLine.fullLine);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(langFilePath, new String[0]), StandardCharsets.UTF_8, new OpenOption[0]);){
            Iterator it = fixedLines.iterator();
            while (it.hasNext()) {
                writer.write((String)it.next());
                if (!it.hasNext()) continue;
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SetTranslationLineData
    extends LineData {
        public final String category;
        public final String key;
        public final String translation;

        public SetTranslationLineData(String fullLine, String category, String key, String translation) {
            super(LineType.SET_TRANSLATION, fullLine);
            this.category = category;
            this.key = key;
            this.translation = translation;
        }
    }

    private static class LineData {
        public final LineType type;
        public final String fullLine;

        public LineData(LineType type, String fullLine) {
            this.type = type;
            this.fullLine = fullLine;
        }
    }

    private static enum LineType {
        SET_CATEGORY(SetCategoryLineData.class),
        SET_TRANSLATION(SetTranslationLineData.class),
        COMMENT(LineData.class),
        EMPTY(LineData.class),
        UNKNOWN(LineData.class);

        public final Class<? extends LineData> typeClass;

        private LineType(Class<? extends LineData> typeClass) {
            this.typeClass = typeClass;
        }
    }

    private static class SetCategoryLineData
    extends LineData {
        public final String category;

        public SetCategoryLineData(String fullLine, String category) {
            super(LineType.SET_CATEGORY, fullLine);
            this.category = category;
        }
    }

    public static class TranslationData {
        public final String category;
        public final String key;
        public final String translation;

        public TranslationData(String category, String key, String translation) {
            this.category = category;
            this.key = key;
            this.translation = translation;
        }
    }
}

