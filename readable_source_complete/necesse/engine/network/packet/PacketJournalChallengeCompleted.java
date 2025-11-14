/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenJournal;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.presets.containerComponent.journal.JournalContainerForm;

public class PacketJournalChallengeCompleted
extends Packet {
    public final int journalEntryID;
    public final int challengeID;
    public boolean rewardAvailable;

    public PacketJournalChallengeCompleted(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.journalEntryID = reader.getNextInt();
        this.challengeID = reader.getNextInt();
        this.rewardAvailable = reader.getNextBoolean();
    }

    public PacketJournalChallengeCompleted(int journalEntryID, int challengeID, boolean rewardAvailable) {
        this.journalEntryID = journalEntryID;
        this.challengeID = challengeID;
        this.rewardAvailable = rewardAvailable;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(journalEntryID);
        writer.putNextInt(challengeID);
        writer.putNextBoolean(rewardAvailable);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        JournalChallenge challenge = this.challengeID == -1 ? null : JournalChallengeRegistry.getChallenge(this.challengeID);
        JournalEntry journalEntry = JournalRegistry.getJournalEntry(this.journalEntryID);
        if (journalEntry == null) {
            System.err.println("Received challenge completed update about invalid journal entry ID: " + this.journalEntryID);
            return;
        }
        if (challenge == null) {
            if (this.challengeID != -1) {
                System.err.println("Received challenge completed update about invalid challenge ID: " + this.challengeID);
            }
        } else {
            GameMessage name = challenge.getName();
            if (name != null) {
                GameColor defaultColor = GameColor.CYAN;
                GameColor highlightColor = GameColor.GREEN;
                LocalMessage completeMessage = new LocalMessage("journal", "challengecompleted").addReplacement("challenge", GameMessageBuilder.buildHighlight(defaultColor, highlightColor, challenge.getName())).addReplacement("journal", GameMessageBuilder.buildHighlight(defaultColor, highlightColor, journalEntry.getLocalization()));
                this.submitToChat(client, GameMessageBuilder.colorCoded(defaultColor, completeMessage));
            }
        }
        if (this.rewardAvailable) {
            SoundManager.playSound(GameResources.alljournalchallengescompleted, SoundEffect.globalEffect().volume(0.3f));
            GameColor defaultColor = GameColor.PURPLE;
            GameColor highlightColor = GameColor.GREEN;
            LocalMessage completeMessage = new LocalMessage("journal", "challengerewardavail").addReplacement("journal", GameMessageBuilder.buildHighlight(defaultColor, highlightColor, journalEntry.getLocalization()));
            this.submitToChat(client, GameMessageBuilder.colorCoded(defaultColor, completeMessage));
        } else {
            SoundManager.playSound(GameResources.journalchallengecompleted, SoundEffect.globalEffect().volume(0.2f));
        }
        client.hasNewJournalEntry = true;
        JournalContainerForm.lastOpenBiomeEntry = journalEntry.getStringID();
    }

    private void submitToChat(Client client, GameMessage message) {
        FairType chatMsg = new FairType();
        chatMsg.append(ChatMessage.fontOptions, message.translate());
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
            FairGlyph[] out = new FairGlyph[stringGlyphs.length];
            System.arraycopy(stringGlyphs, 0, out, 0, stringGlyphs.length);
            return out;
        }));
        chatMsg.applyParsers(ChatMessage.getParsers(ChatMessage.fontOptions));
        client.chat.addMessage(new ChatMessage(chatMsg));
    }
}

