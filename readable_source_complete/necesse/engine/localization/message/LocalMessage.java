/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalReplacement;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class LocalMessage
extends GameMessage {
    public static Pattern replaceRegex = Pattern.compile("<(\\w+)>");
    public String category;
    public String key;
    private ArrayList<LocalReplacement> replacements;
    private String lastTranslation;
    private int lastLanguageUnique;
    private boolean updated;

    public LocalMessage() {
        this.category = "null";
        this.key = "null";
        this.replacements = new ArrayList();
    }

    public LocalMessage(String category, String key) {
        this.category = category;
        this.key = key;
        this.replacements = new ArrayList();
        this.updated = true;
    }

    public LocalMessage(String category, String key, ArrayList<LocalReplacement> replacements) {
        this(category, key);
        this.replacements = replacements;
    }

    public LocalMessage(String category, String key, String replaceKey, String replacement) {
        this(category, key);
        this.addReplacement(replaceKey, replacement);
    }

    public LocalMessage(String category, String key, String replaceKey, GameMessage replacement) {
        this(category, key);
        this.addReplacement(replaceKey, replacement);
    }

    public LocalMessage(String category, String key, String replaceKey, Function<LocalMessage, GameMessage> replacement) {
        this(category, key);
        this.addReplacement(replaceKey, replacement);
    }

    public LocalMessage(String category, String key, String ... replacements) {
        this(category, key);
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must have an even amount of parameters");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            String replaceKey = replacements[i];
            String replacement = replacements[i + 1];
            this.addReplacement(replaceKey, replacement);
        }
    }

    public LocalMessage(String category, String key, Object ... replacements) {
        this(category, key);
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must have an even amount of parameters");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            String replaceKey = replacements[i].toString();
            if (replacements[i + 1] instanceof GameMessage) {
                this.addReplacement(replaceKey, (GameMessage)replacements[i + 1]);
                continue;
            }
            this.addReplacement(replaceKey, replacements[i + 1].toString());
        }
    }

    @Override
    protected void addPacketContent(PacketWriter writer) {
        writer.putNextString(this.category);
        writer.putNextString(this.key);
        writer.putNextShortUnsigned(this.replacements.size());
        for (LocalReplacement lr : this.replacements) {
            writer.putNextContentPacket(lr.getContentPacket(this));
        }
    }

    @Override
    protected void applyPacketContent(PacketReader reader) {
        this.category = reader.getNextString();
        this.key = reader.getNextString();
        int size = reader.getNextShortUnsigned();
        this.replacements = new ArrayList();
        for (int i = 0; i < size; ++i) {
            this.replacements.add(new LocalReplacement(reader.getNextContentPacket()));
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addUnsafeString("category", this.category);
        save.addUnsafeString("key", this.key);
        if (!this.replacements.isEmpty()) {
            SaveData replaces = new SaveData("REPLACES");
            for (LocalReplacement replacement : this.replacements) {
                replaces.addSaveData(replacement.getSaveData("", this));
            }
            save.addSaveData(replaces);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.category = save.getUnsafeString("category");
        this.key = save.getUnsafeString("key");
        this.replacements = new ArrayList();
        LoadData replaces = save.getFirstLoadDataByName("REPLACES");
        if (replaces != null) {
            for (LoadData replaceData : replaces.getLoadData()) {
                this.replacements.add(new LocalReplacement(replaceData));
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean hasUpdated() {
        if (this.updated) {
            return true;
        }
        if (this.lastLanguageUnique != Localization.getCurrentLang().updateUnique) {
            return true;
        }
        for (LocalReplacement lr : this.replacements) {
            if (!lr.hasUpdated(this)) continue;
            return true;
        }
        return false;
    }

    protected void update() {
        String msg = Localization.translate(this.category, this.key);
        for (LocalReplacement lr : this.replacements) {
            msg = lr.replace(this, msg);
        }
        this.lastTranslation = msg;
        this.lastLanguageUnique = Localization.getCurrentLang().updateUnique;
        this.updated = false;
    }

    @Override
    public String translate() {
        if (this.hasUpdated()) {
            this.update();
        }
        return this.lastTranslation;
    }

    @Override
    public String setSteamRichPresence(HashMap<String, String> map, String key, int index) throws DuplicateRichPresenceKeyException {
        String out = "#" + this.category + "_" + this.key;
        for (int i = 0; i < this.replacements.size(); ++i) {
            LocalReplacement lr = this.replacements.get(i);
            lr.setSteamRichPresence(map, this, lr.getKey(), i);
        }
        if (key != null) {
            if (map.containsKey(key)) {
                throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + key + ". Before: " + map.get(key) + ", now: " + out);
            }
            map.put(key, out);
        }
        return out;
    }

    @Override
    public boolean isMissingKey(Language language) {
        if (language.isMissing(this.category, this.key)) {
            return true;
        }
        for (LocalReplacement replacement : this.replacements) {
            GameMessage message = replacement.getReplacement(this);
            if (!message.isMissingKey(language)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isSameAsEnglish(Language language) {
        if (language.isSameAsEnglish(this.key, this.category)) {
            return true;
        }
        for (LocalReplacement replacement : this.replacements) {
            GameMessage message = replacement.getReplacement(this);
            if (!message.isSameAsEnglish(language)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String translateDebug(Language language) {
        String msg = Localization.translate(language, this.category, this.key);
        for (LocalReplacement lr : this.replacements) {
            msg = lr.replaceDebug(language, this, msg);
        }
        return msg;
    }

    @Override
    public boolean isSame(GameMessage message) {
        if (message.getID() != this.getID()) {
            return false;
        }
        LocalMessage o = (LocalMessage)message;
        if (!o.category.equals(this.category) || !o.key.equals(this.key) || o.replacements.size() != this.replacements.size()) {
            return false;
        }
        for (int i = 0; i < this.replacements.size(); ++i) {
            if (this.replacements.get(i).isSame(this, o, o.replacements.get(i))) continue;
            return false;
        }
        return true;
    }

    public LocalMessage setReplacements(ArrayList<LocalReplacement> replacements) {
        this.replacements = replacements;
        return this;
    }

    public ArrayList<LocalReplacement> getReplacements() {
        return this.replacements;
    }

    public LocalMessage addReplacement(String key, String replacement) {
        this.replacements.add(new LocalReplacement(key, replacement));
        this.updated = true;
        return this;
    }

    public LocalMessage addReplacement(String key, GameMessage replacement) {
        this.replacements.add(new LocalReplacement(key, replacement));
        this.updated = true;
        return this;
    }

    public LocalMessage addReplacement(String key, Function<LocalMessage, GameMessage> replacement) {
        this.replacements.add(new LocalReplacement(key, replacement));
        this.updated = true;
        return this;
    }

    public String toString() {
        StringBuilder replacements = new StringBuilder();
        for (LocalReplacement lr : this.replacements) {
            replacements.append(", ").append(lr.getKey()).append(": ").append(lr.getReplacement(this).toString());
        }
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + this.category + "." + this.key + replacements + "]";
    }
}

