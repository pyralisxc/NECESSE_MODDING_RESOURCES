/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import necesse.engine.localization.Language;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.GameColor;

public class GameMessageBuilder
extends GameMessage {
    private final ArrayList<GameMessage> messages = new ArrayList();

    @Override
    public boolean isEmpty() {
        return this.messages.isEmpty() || this.messages.stream().allMatch(GameMessage::isEmpty);
    }

    @Override
    public boolean hasUpdated() {
        return this.messages.stream().anyMatch(GameMessage::hasUpdated);
    }

    @Override
    public String translate() {
        StringBuilder builder = new StringBuilder();
        for (GameMessage message : this.messages) {
            builder.append(message.translate());
        }
        return builder.toString();
    }

    @Override
    public String setSteamRichPresence(HashMap<String, String> map, String key, int index) throws DuplicateRichPresenceKeyException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.messages.size(); ++i) {
            GameMessage message = this.messages.get(i);
            builder.append(message.setSteamRichPresence(map, null, i));
        }
        String out = builder.toString();
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
        for (GameMessage message : this.messages) {
            if (!message.isMissingKey(language)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isSameAsEnglish(Language language) {
        for (GameMessage message : this.messages) {
            if (!message.isSameAsEnglish(language)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String translateDebug(Language language) {
        StringBuilder builder = new StringBuilder();
        for (GameMessage message : this.messages) {
            builder.append(message.translateDebug(language));
        }
        return builder.toString();
    }

    @Override
    public boolean isSame(GameMessage message) {
        if (message.getID() != this.getID()) {
            return false;
        }
        GameMessageBuilder other = (GameMessageBuilder)message;
        if (other.messages.size() != this.messages.size()) {
            return false;
        }
        for (int i = 0; i < this.messages.size(); ++i) {
            if (other.messages.get(i).isSame(this.messages.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void addPacketContent(PacketWriter writer) {
        writer.putNextShortUnsigned(this.messages.size());
        for (GameMessage message : this.messages) {
            writer.putNextContentPacket(message.getContentPacket());
        }
    }

    @Override
    protected void applyPacketContent(PacketReader reader) {
        this.messages.clear();
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            this.messages.add(GameMessageBuilder.fromContentPacket(reader.getNextContentPacket()));
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        for (GameMessage message : this.messages) {
            save.addSaveData(message.getSaveData(""));
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.messages.clear();
        for (LoadData component : save.getLoadData()) {
            this.messages.add(GameMessageBuilder.loadSave(component));
        }
    }

    public GameMessageBuilder append(GameMessage message) {
        this.messages.add(message);
        return this;
    }

    public GameMessageBuilder append(int index, GameMessage message) {
        this.messages.add(index, message);
        return this;
    }

    public GameMessageBuilder prepend(GameMessage message) {
        this.messages.add(0, message);
        return this;
    }

    public GameMessageBuilder append(String staticMessage) {
        return this.append(new StaticMessage(staticMessage));
    }

    public GameMessageBuilder append(String category, String key) {
        return this.append(new LocalMessage(category, key));
    }

    public GameMessageBuilder append(int index, String staticMessage) {
        return this.append(index, new StaticMessage(staticMessage));
    }

    public GameMessageBuilder prepend(String staticMessage) {
        return this.prepend(new StaticMessage(staticMessage));
    }

    public void clear() {
        this.messages.clear();
    }

    public int size() {
        return this.messages.size();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + Arrays.toString(this.messages.toArray()) + "]";
    }

    public static GameMessageBuilder buildHighlight(GameColor defaultColor, GameColor highlightColor, GameMessage message) {
        return new GameMessageBuilder().append(highlightColor.getColorCode()).append(message).append(defaultColor.getColorCode());
    }

    public static GameMessageBuilder buildHighlight(GameColor defaultColor, GameColor highlightColor, String category, String key) {
        return GameMessageBuilder.buildHighlight(defaultColor, highlightColor, new LocalMessage(category, key));
    }

    public static GameMessageBuilder colorCoded(GameColor color, GameMessage message) {
        return new GameMessageBuilder().append(color.getColorCode()).append(message);
    }

    public static GameMessageBuilder colorCoded(GameColor color, String category, String key) {
        return GameMessageBuilder.colorCoded(color, new LocalMessage(category, key));
    }
}

