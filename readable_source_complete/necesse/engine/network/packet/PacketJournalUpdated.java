/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenJournal;
import necesse.engine.registries.JournalRegistry;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairColorChangeGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.presets.containerComponent.journal.JournalContainerForm;

public class PacketJournalUpdated
extends Packet {
    public final int journalEntryID;

    public PacketJournalUpdated(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.journalEntryID = reader.getNextInt();
    }

    public PacketJournalUpdated(int journalEntryID) {
        this.journalEntryID = journalEntryID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(journalEntryID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        FairType chatMsg = new FairType();
        chatMsg.append(ChatMessage.fontOptions, Localization.translate("journal", "newentry"));
        chatMsg.applyParsers(TypeParsers.replaceParserRegex("(\\[\\[.+\\]\\])", result -> {
            String replace = result.matcher.group(1);
            FairCharacterGlyph[] stringGlyphs = FairCharacterGlyph.fromString(ChatMessage.fontOptions, replace, e -> {
                if (e.getID() == -100) {
                    if (!e.state) {
                        client.network.sendPacket(new PacketOpenJournal());
                        client.hasNewJournalEntry = false;
                    }
                    return true;
                }
                return false;
            }, null);
            FairGlyph[] out = new FairGlyph[stringGlyphs.length + 1];
            out[0] = new FairColorChangeGlyph(GameColor.CYAN);
            System.arraycopy(stringGlyphs, 0, out, 1, stringGlyphs.length);
            return out;
        }));
        chatMsg.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions));
        client.chat.addMessage(new ChatMessage(chatMsg));
        client.hasNewJournalEntry = true;
        JournalEntry journalEntry = JournalRegistry.getJournalEntry(this.journalEntryID);
        if (journalEntry != null) {
            journalEntry.toggleIsHidden = false;
            JournalContainerForm.lastOpenBiomeEntry = journalEntry.getStringID();
        }
    }
}

