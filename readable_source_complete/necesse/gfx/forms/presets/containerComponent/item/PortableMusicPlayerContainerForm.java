/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.sound.GameMusic;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.MusicPlayerManager;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.item.PortableMusicPlayerContainer;

public class PortableMusicPlayerContainerForm
extends ItemInventoryContainerForm<PortableMusicPlayerContainer> {
    protected GameMusic lastPlaying;
    protected FormContentIconButton pauseButton;
    protected FormLocalSlider playingSlider;

    public PortableMusicPlayerContainerForm(Client client, PortableMusicPlayerContainer container) {
        super(client, container);
    }

    @Override
    protected void addSlots(FormFlow flow) {
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "insertplaylist", new FontOptions(16), -1, 5, 0), 4));
        super.addSlots(flow);
        for (FormContainerSlot slot : this.slots) {
            slot.setDecal(this.getInterfaceStyle().inventoryslot_icon_vinyl);
        }
        flow.next(10);
        this.playingSlider = this.addComponent(flow.nextY(new FormLocalSlider(new StaticMessage(""), 5, 0, 0, 0, 60, this.getWidth() - 10){

            @Override
            public String getValueText() {
                int currentMinutes;
                int currentSeconds = this.getValue();
                String currentSecondsString = (currentSeconds -= (currentMinutes = currentSeconds / 60) * 60) < 10 ? "0" + currentSeconds : "" + currentSeconds;
                int maxSeconds = this.getMaxValue();
                int maxMinutes = maxSeconds / 60;
                String maxSecondsString = (maxSeconds -= maxMinutes * 60) < 10 ? "0" + maxSeconds : "" + maxSeconds;
                return currentMinutes + ":" + currentSecondsString + " / " + maxMinutes + ":" + maxSecondsString;
            }
        }, 5));
        this.playingSlider.allowScroll = false;
        this.playingSlider.onGrab(e -> {
            if (!e.grabbed) {
                MusicOptionsOffset currentMusic;
                int desiredOffset = ((FormSlider)e.from).getValue() * 1000;
                MusicPlayerManager manager = ((PortableMusicPlayerContainer)this.container).getMusicManager();
                MusicOptionsOffset musicOptionsOffset = currentMusic = manager == null ? null : manager.getCurrentMusic();
                if (currentMusic != null) {
                    long deltaOffset = currentMusic.offset - (long)desiredOffset;
                    ((PortableMusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(deltaOffset);
                }
            }
        });
        int buttonY = flow.next(32);
        this.pauseButton = this.addComponent(new FormContentIconButton(this.getWidth() / 2 - 16, buttonY, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().world_icon, new GameMessage[0]));
        this.pauseButton.onClicked(e -> {
            MusicPlayerManager manager = ((PortableMusicPlayerContainer)this.container).getMusicManager();
            ((PortableMusicPlayerContainer)this.container).setPaused.runAndSend(manager != null && !manager.isPaused());
        });
        this.addComponent(new FormContentIconButton(this.getWidth() / 2 - 16 - 4 - 32, buttonY, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().prev_song, new GameMessage[0])).onClicked(e -> {
            MusicOptionsOffset currentMusic;
            MusicPlayerManager manager = ((PortableMusicPlayerContainer)this.container).getMusicManager();
            MusicOptionsOffset musicOptionsOffset = currentMusic = manager == null ? null : manager.getCurrentMusic();
            if (currentMusic != null) {
                MusicOptions previousMusic;
                long offset = currentMusic.offset;
                if (currentMusic.offset < 3000L && (previousMusic = manager.getPreviousMusic()) != null) {
                    offset += previousMusic.getMusicListMilliseconds();
                }
                ((PortableMusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(offset);
            }
        });
        this.addComponent(new FormContentIconButton(this.getWidth() / 2 + 16 + 4, buttonY, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().next_song, new GameMessage[0])).onClicked(e -> {
            MusicOptionsOffset currentMusic;
            MusicPlayerManager manager = ((PortableMusicPlayerContainer)this.container).getMusicManager();
            MusicOptionsOffset musicOptionsOffset = currentMusic = manager == null ? null : manager.getCurrentMusic();
            if (currentMusic != null) {
                long currentLength = currentMusic.options.getMusicListMilliseconds();
                long offset = currentMusic.offset - currentLength;
                ((PortableMusicPlayerContainer)this.container).forwardMilliseconds.runAndSend(offset);
            }
        });
    }

    public void updatePlayingComponents() {
        GameMusic currentMusic;
        MusicPlayerManager manager = ((PortableMusicPlayerContainer)this.container).getMusicManager();
        MusicOptionsOffset currentPlaying = manager == null ? null : manager.getCurrentMusic();
        GameMusic gameMusic = currentMusic = currentPlaying == null ? null : currentPlaying.options.music;
        if (this.lastPlaying != currentMusic) {
            if (currentMusic != null) {
                this.playingSlider.setLocalization(new LocalMessage("ui", "musicplaying", "name", currentMusic.trackName.translate()));
            } else {
                this.playingSlider.setLocalization(new StaticMessage(""));
            }
            this.lastPlaying = currentMusic;
        }
        if (currentPlaying != null) {
            int totalSeconds = (int)((double)currentPlaying.options.music.sound.getLengthInMillis() / 1000.0);
            this.playingSlider.setRange(0, totalSeconds);
            if (!this.playingSlider.isGrabbed()) {
                int progressSeconds = (int)((double)currentPlaying.offset / 1000.0);
                this.playingSlider.setValue(progressSeconds);
            }
        } else {
            this.playingSlider.setRange(0, 0);
            this.playingSlider.setValue(0);
        }
        this.pauseButton.setIcon(manager != null && manager.isPaused() ? this.getInterfaceStyle().play_song : this.getInterfaceStyle().pause_song);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updatePlayingComponents();
        super.draw(tickManager, perspective, renderBox);
    }
}

