/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.localization.Language;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameFont.FontOptions;

public abstract class GameMessage
implements IDDataContainer {
    public final IDData idData = new IDData();
    public static final EmptyConstructorGameRegistry<GameMessage> registry = new EmptyConstructorGameRegistry<GameMessage>("GameMessage", Short.MAX_VALUE){

        @Override
        public void registerCore() {
            this.registerClass("static", StaticMessage.class);
            this.registerClass("local", LocalMessage.class);
            this.registerClass("builder", GameMessageBuilder.class);
        }

        @Override
        protected void onRegistryClose() {
        }
    };

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public GameMessage() {
        registry.applyIDData(this.getClass(), this.idData);
    }

    public abstract boolean isEmpty();

    public abstract boolean hasUpdated();

    public abstract String translate();

    public abstract String setSteamRichPresence(HashMap<String, String> var1, String var2, int var3) throws DuplicateRichPresenceKeyException;

    public abstract boolean isMissingKey(Language var1);

    public abstract boolean isSameAsEnglish(Language var1);

    public abstract String translateDebug(Language var1);

    public abstract boolean isSame(GameMessage var1);

    public static boolean isSame(GameMessage msg1, GameMessage msg2) {
        return msg1 == msg2 || msg1 != null && msg2 != null && msg1.isSame(msg2);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GameMessage) {
            return this.isSame((GameMessage)obj);
        }
        return super.equals(obj);
    }

    protected abstract void addPacketContent(PacketWriter var1);

    protected abstract void applyPacketContent(PacketReader var1);

    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);

    public final void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.getID());
        this.addPacketContent(writer);
    }

    public static GameMessage fromPacket(PacketReader reader) {
        int id = reader.getNextShortUnsigned();
        GameMessage newMessage = GameMessage.getNewMessage(id);
        if (newMessage != null) {
            newMessage.applyPacketContent(reader);
        }
        return newMessage;
    }

    public final Packet getContentPacket() {
        Packet p = new Packet();
        this.writePacket(new PacketWriter(p));
        return p;
    }

    public static GameMessage fromContentPacket(Packet p) {
        return GameMessage.fromPacket(new PacketReader(p));
    }

    public final SaveData getSaveData(String saveName) {
        SaveData save = new SaveData(saveName);
        save.addUnsafeString("stringID", this.getStringID());
        this.addSaveData(save);
        return save;
    }

    public static GameMessage loadSave(LoadData save) {
        String stringID = save.getUnsafeString("stringID");
        GameMessage newMessage = GameMessage.getNewMessage(stringID);
        if (newMessage != null) {
            newMessage.applyLoadData(save);
        }
        return newMessage;
    }

    public static GameMessage loadSave(LoadData save, String dataName, boolean printWarning) {
        LoadData messageSave = save.getFirstLoadDataByName(dataName);
        if (messageSave != null) {
            GameMessage message = GameMessage.loadSave(messageSave);
            if (message == null && printWarning) {
                GameLog.warn.println("Could not load " + dataName + " GameMessage because from " + (save.getName().isEmpty() ? "null" : save.getName()) + " because of error.");
            }
            return message;
        }
        if (printWarning) {
            GameLog.warn.println("Could not load " + dataName + " GameMessage because from " + (save.getName().isEmpty() ? "null" : save.getName()) + ".");
        }
        return null;
    }

    public ArrayList<GameMessage> breakMessage(FontOptions fontOptions, int maxWidth) {
        ArrayList<String> breaks = GameUtils.breakString(this.translate(), fontOptions, maxWidth);
        return breaks.stream().map(StaticMessage::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public static int registerClass(String stringID, Class<? extends GameMessage> messageClass) {
        return registry.registerClass(stringID, messageClass);
    }

    public static GameMessage getNewMessage(int id) {
        return registry.getNewInstance(id);
    }

    public static GameMessage getNewMessage(String stringID) {
        return registry.getNewInstance(stringID);
    }

    static {
        registry.registerCore();
    }
}

