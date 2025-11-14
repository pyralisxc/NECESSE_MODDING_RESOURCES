/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.message;

import java.util.HashMap;
import java.util.function.Function;
import necesse.engine.localization.Language;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class LocalReplacement {
    private static GameMessage NULL_MESSAGE = new StaticMessage("Null");
    private String key;
    private Function<LocalMessage, GameMessage> replacement;
    private GameMessage lastReplacement;

    public LocalReplacement(Packet packet) {
        PacketReader reader = new PacketReader(packet);
        this.key = reader.getNextString();
        GameMessage replacement = GameMessage.fromContentPacket(reader.getNextContentPacket());
        this.replacement = l -> replacement;
    }

    public Packet getContentPacket(LocalMessage lm) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        String key = this.getKey();
        GameMessage replacement = this.getReplacement(lm);
        writer.putNextString(key);
        writer.putNextContentPacket(replacement.getContentPacket());
        return p;
    }

    public LocalReplacement(LoadData save) {
        this.key = save.getUnsafeString("key");
        GameMessage replacement = GameMessage.loadSave(save.getFirstLoadDataByName("replace"));
        this.replacement = lm -> replacement;
    }

    public SaveData getSaveData(String saveName, LocalMessage lm) {
        SaveData save = new SaveData(saveName);
        save.addUnsafeString("key", this.key);
        save.addSaveData(this.getReplacement(lm).getSaveData("replace"));
        return save;
    }

    public LocalReplacement(String key, Function<LocalMessage, GameMessage> replacement) {
        this.key = key;
        this.replacement = replacement;
    }

    public LocalReplacement(String key, GameMessage replacement) {
        this(key, (LocalMessage l) -> replacement);
    }

    public LocalReplacement(String key, String replacement) {
        this.key = key;
        StaticMessage message = new StaticMessage(replacement);
        this.replacement = l -> message;
    }

    public String getKey() {
        return this.key;
    }

    public GameMessage getReplacement(LocalMessage lm) {
        GameMessage replacement = this.replacement.apply(lm);
        return replacement == null ? NULL_MESSAGE : replacement;
    }

    public String replace(LocalMessage lm, String msg) {
        this.lastReplacement = this.getReplacement(lm);
        return msg.replace("<" + this.getKey() + ">", this.lastReplacement.translate());
    }

    public String replaceDebug(Language language, LocalMessage lm, String msg) {
        this.lastReplacement = this.getReplacement(lm);
        return msg.replace("<" + this.getKey() + ">", this.lastReplacement.translateDebug(language));
    }

    public String setSteamRichPresence(HashMap<String, String> map, LocalMessage lm, String key, int index) throws DuplicateRichPresenceKeyException {
        String out = this.getReplacement(lm).setSteamRichPresence(map, null, index);
        if (key != null) {
            if (map.containsKey(key)) {
                throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + key + ". Before: " + map.get(key) + ", now: " + out);
            }
            map.put(key, out);
        }
        return out;
    }

    public boolean hasUpdated(LocalMessage lm) {
        GameMessage replacement = this.getReplacement(lm);
        return this.lastReplacement == null || replacement.hasUpdated();
    }

    public boolean isSame(LocalMessage thisLM, LocalMessage otherLM, LocalReplacement other) {
        return other.key.equals(this.key) && other.getReplacement(otherLM).isSame(this.getReplacement(thisLM));
    }
}

