/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.message;

import java.util.HashMap;
import necesse.engine.localization.Language;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class StaticMessage
extends GameMessage {
    private String message;
    private boolean updated;

    public StaticMessage() {
        this.message = "null";
    }

    public StaticMessage(String message) {
        this.setMessage(message);
    }

    @Override
    protected void addPacketContent(PacketWriter writer) {
        writer.putNextString(this.message);
    }

    @Override
    protected void applyPacketContent(PacketReader reader) {
        String message = reader.getNextString();
        if (this.message == null || !this.message.equals(message)) {
            this.updated = true;
        }
        this.message = message;
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addSafeString("message", this.message);
    }

    @Override
    public void applyLoadData(LoadData save) {
        String message = save.getSafeString("message", null);
        if (this.message == null || !this.message.equals(message)) {
            this.updated = true;
        }
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        if (this.message == null || !this.message.equals(message)) {
            this.updated = true;
        }
        this.message = message == null ? "Null" : message;
    }

    @Override
    public boolean isEmpty() {
        return this.message == null || this.message.isEmpty();
    }

    @Override
    public boolean hasUpdated() {
        return this.updated;
    }

    @Override
    public String translate() {
        this.updated = false;
        return this.message;
    }

    @Override
    public String setSteamRichPresence(HashMap<String, String> map, String key, int index) throws DuplicateRichPresenceKeyException {
        if (key != null) {
            if (map.containsKey(key)) {
                throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + key + ". Before: " + map.get(key) + ", now: " + this.message);
            }
            map.put(key, this.message);
        }
        return this.message;
    }

    @Override
    public boolean isMissingKey(Language language) {
        return false;
    }

    @Override
    public boolean isSameAsEnglish(Language language) {
        return false;
    }

    @Override
    public String translateDebug(Language language) {
        return this.message;
    }

    @Override
    public boolean isSame(GameMessage message) {
        return message.getID() == this.getID() && ((StaticMessage)message).message.equals(this.message);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + this.message + "]";
    }
}

