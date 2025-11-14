/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SoundLogicGateEntity
extends LogicGateEntity {
    public int sound = -1;
    public int semitone = 0;
    private boolean active = false;

    public SoundLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public SoundLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("sound", this.sound);
        save.addInt("semitone", this.semitone);
        save.addBoolean("active", this.active);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.sound = save.getInt("sound", -1);
        this.semitone = save.getInt("semitone", -1);
        this.active = save.getBoolean("active", false);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextByteUnsigned(this.sound);
        writer.putNextByte((byte)this.semitone);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        this.sound = reader.getNextByteUnsigned();
        this.semitone = reader.getNextByte();
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        if (active && !this.active) {
            this.playSound();
            this.active = true;
        } else {
            boolean temp = false;
            for (int i = 0; i < 4; ++i) {
                if (!this.isWireActive(i)) continue;
                temp = true;
            }
            if (!temp) {
                this.active = false;
            }
        }
    }

    public void playSound() {
        if (this.isClient()) {
            SoundLogicGateEntity.playSound(this.sound, this.semitone, this.tileX * 32 + 16, this.tileY * 32 + 16);
        }
    }

    public static float getPitch(int semitone) {
        return (float)Math.pow(2.0, (float)semitone / 12.0f);
    }

    public static void playSound(int sound, int semitone, int x, int y) {
        GameSound[] sounds = SoundLogicGateEntity.getSounds();
        if (sound >= 0 && sound < sounds.length) {
            SoundManager.playSound(sounds[sound], (SoundEffect)SoundEffect.effect(x, y).pitch(SoundLogicGateEntity.getPitch(semitone)));
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        String[] sounds = SoundLogicGateEntity.getSoundNames();
        String soundName = this.sound >= 0 && this.sound < sounds.length ? sounds[this.sound] : Localization.translate("logictooltips", "soundnone");
        tooltips.add(Localization.translate("logictooltips", "soundname", "name", soundName));
        tooltips.add(Localization.translate("logictooltips", "soundsemitone", "value", (Object)this.semitone));
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.SOUND_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }

    public static GameSound[] getSounds() {
        return new GameSound[]{GameResources.bassNote, GameResources.hatNote, GameResources.kickNote, GameResources.pianoNote, GameResources.snareNote};
    }

    public static String[] getSoundNames() {
        return new String[]{Localization.translate("logictooltips", "soundbass"), Localization.translate("logictooltips", "soundhat"), Localization.translate("logictooltips", "soundkick"), Localization.translate("logictooltips", "soundpiano"), Localization.translate("logictooltips", "soundsnare")};
    }
}

