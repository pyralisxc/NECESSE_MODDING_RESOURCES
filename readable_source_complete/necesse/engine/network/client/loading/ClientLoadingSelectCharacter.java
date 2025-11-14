/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import java.io.File;
import necesse.engine.GameLaunch;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingPhase;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketDownloadCharacter;
import necesse.engine.network.packet.PacketDownloadCharacterResponse;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.save.CharacterSave;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.presets.CharacterSelectForm;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NewCharacterForm;

public class ClientLoadingSelectCharacter
extends ClientLoadingPhase {
    private FormSwitcher switcher;
    private ConfirmationForm lookErrorForm;
    private NewCharacterForm createCharacterForm;
    private CharacterSelectForm characterSelectForm;
    private boolean needAppearance;
    private boolean allowCharacterSelect;
    private int serverCharacterUniqueID;
    private PlayerMob serverCharacterPlayer;
    private long serverCharacterTimePlayed;

    public ClientLoadingSelectCharacter(ClientLoading loading) {
        super(loading, false);
    }

    public void submitPlayerAppearancePacket(PacketPlayerAppearance packet) {
        if (packet.slot == this.client.getSlot()) {
            this.markDone();
        }
    }

    public void submitConnectAccepted(PacketConnectApproved packet) {
        this.needAppearance = packet.needAppearance;
        this.allowCharacterSelect = packet.characterSelect;
        this.serverCharacterUniqueID = packet.serverCharacterUniqueID;
        if (packet.serverCharacterAppearance != null) {
            this.serverCharacterPlayer = new PlayerMob(this.serverCharacterUniqueID, null);
            this.serverCharacterPlayer.look = packet.serverCharacterAppearance;
            this.serverCharacterPlayer.getInv().applyLookContentPacket(new PacketReader(packet.serverCharacterLookContent));
            this.serverCharacterPlayer.playerName = packet.serverCharacterName;
        }
        this.serverCharacterTimePlayed = packet.serverCharacterTimePlayed;
        if (!this.needAppearance && !this.allowCharacterSelect) {
            this.markDone();
        }
    }

    public void submitDownloadedCharacter(PacketDownloadCharacterResponse packet) {
        if (this.switcher != null && !this.switcher.isDisposed()) {
            CharacterSave character = new CharacterSave(packet.characterUniqueID, packet.networkData);
            if (this.client.playingOnDisplayName != null) {
                character.lastUsed = new LocalMessage("ui", "characterlastworld", "world", this.client.playingOnDisplayName);
            }
            File file = CharacterSave.saveCharacter(character, null, false);
            this.characterSelectForm.onCharacterLoaded(file, character, true);
        }
    }

    public void submitError(HumanLook previousLook, GameMessage error) {
        if (this.switcher != null && !this.switcher.isDisposed()) {
            if (previousLook != null) {
                this.createCharacterForm.setLook(previousLook);
            }
            if (error != null) {
                this.client.characterFilePath = null;
                this.lookErrorForm.setupConfirmation(error, (GameMessage)new LocalMessage("ui", "continuebutton"), (GameMessage)new LocalMessage("ui", "connectcancel"), () -> this.switcher.makeCurrent(this.allowCharacterSelect ? this.characterSelectForm : this.createCharacterForm), this::cancelConnection);
                this.switcher.makeCurrent(this.lookErrorForm);
                GameWindow window = WindowManager.getWindow();
                this.lookErrorForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            }
        }
    }

    @Override
    public FormResizeWrapper start() {
        if (!this.needAppearance && !this.allowCharacterSelect) {
            this.markDone();
            return null;
        }
        this.switcher = new FormSwitcher();
        this.characterSelectForm = this.switcher.addComponent(new CharacterSelectForm(new LocalMessage("ui", "connectcancel"), this.client.worldSettings.allowCheats, this.client.worldSettings.creativeMode, this.client.getPermissionLevel().getLevel() >= PermissionLevel.OWNER.getLevel()){

            @Override
            public void onSelected(File filePath, CharacterSave character) {
                if (ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                    return;
                }
                ClientLoadingSelectCharacter.this.client.characterFilePath = filePath;
                if (!character.creativeEnabled && ClientLoadingSelectCharacter.this.client.worldSettings.creativeMode && filePath != null) {
                    CharacterSave.backupCharacterForCreativeIfNecessary(character, ClientLoadingSelectCharacter.this.client);
                }
                ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketSelectedCharacter(character.characterUniqueID, filePath == null ? null : character));
            }

            @Override
            public void onBackPressed() {
                ClientLoadingSelectCharacter.this.cancelConnection();
            }

            @Override
            public void onDownloadPressed(int characterUniqueID) {
                if (ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                    return;
                }
                ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketDownloadCharacter(characterUniqueID));
            }
        });
        this.createCharacterForm = this.switcher.addComponent(new NewCharacterForm("newCharacter"){

            @Override
            public void onCreatePressed(PlayerMob player) {
                if (ClientLoadingSelectCharacter.this.client.hasDisconnected()) {
                    return;
                }
                ClientLoadingSelectCharacter.this.client.network.sendPacket(new PacketPlayerAppearance(ClientLoadingSelectCharacter.this.client.getSlot(), CharacterSave.getNewUniqueCharacterID(uniqueID -> !ClientLoadingSelectCharacter.this.characterSelectForm.isCharacterUniqueIDOccupied((int)uniqueID)), player));
            }

            @Override
            public void onCancelPressed() {
                ClientLoadingSelectCharacter.this.cancelConnection();
            }
        });
        if (this.serverCharacterPlayer != null) {
            this.createCharacterForm.setLook(this.serverCharacterPlayer.look);
        }
        this.lookErrorForm = this.switcher.addComponent(new ConfirmationForm("lookError", 400, 120));
        if (this.allowCharacterSelect) {
            if (this.serverCharacterPlayer != null) {
                this.characterSelectForm.addExtraCharacter(this.serverCharacterUniqueID, this.serverCharacterPlayer, this.serverCharacterTimePlayed);
            }
            this.characterSelectForm.loadCharacters();
            this.switcher.makeCurrent(this.characterSelectForm);
        } else {
            this.switcher.makeCurrent(this.createCharacterForm);
        }
        return new FormResizeWrapper(this.switcher, () -> {
            GameWindow window = WindowManager.getWindow();
            this.characterSelectForm.onWindowResized(window);
            this.createCharacterForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            this.lookErrorForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        });
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new StaticMessage("CREATING_CHARACTER");
    }

    @Override
    public void tick() {
    }

    @Override
    public void end() {
        GameLaunch.useCharacter = null;
    }
}

