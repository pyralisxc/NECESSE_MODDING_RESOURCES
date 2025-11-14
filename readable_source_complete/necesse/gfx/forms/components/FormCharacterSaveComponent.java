/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.CharacterSave;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.PlayerSprite;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;

public abstract class FormCharacterSaveComponent
extends Form {
    public final File file;
    public final CharacterSave character;

    public FormCharacterSaveComponent(int width, File file, CharacterSave character, boolean worldHasCheats, boolean worldIsCreative, boolean isOwner) {
        super("character" + character.characterUniqueID, width, 74);
        this.file = file;
        this.character = character;
        this.drawBase = false;
        this.addComponent(new FormCustomDraw(5, 5, 64, 64){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                PlayerSprite.drawInForms((drawX, drawY) -> {
                    GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
                    PlayerSprite.getIconDrawOptions(drawX, drawY, 64, 64, FormCharacterSaveComponent.this.character.player, 0, 2).draw();
                    GameTexture.overrideBlendQuality = null;
                }, 5, 0, 64, 64);
            }
        });
        LocalMessage canUseError = null;
        ArrayList<GameMessage> useWarnings = new ArrayList<GameMessage>();
        if (worldHasCheats && !character.cheatsEnabled) {
            useWarnings.add(new LocalMessage("ui", "characterhascheatsdisabled"));
        } else if (!worldHasCheats && character.cheatsEnabled) {
            useWarnings.add(new LocalMessage("ui", "characterhascheatsenabled"));
            if (!isOwner) {
                canUseError = new LocalMessage("ui", "charactercheatserror");
            }
        }
        if (worldIsCreative && !character.creativeEnabled) {
            useWarnings.add(new LocalMessage("ui", "characterissurvival"));
        } else if (!worldIsCreative && character.creativeEnabled) {
            useWarnings.add(new LocalMessage("ui", "characteriscreative"));
            if (!isOwner) {
                canUseError = new LocalMessage("ui", "charactercreativeerror");
            }
        }
        FormInputSize buttonSize = FormInputSize.SIZE_24;
        FormFlow buttonFlow = new FormFlow(this.getWidth() - 5);
        FormContentIconButton selectButton = this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.GREEN, this.getInterfaceStyle().button_collapsed_24, new LocalMessage("ui", "selectbutton")), 2));
        selectButton.onClicked(e -> this.onSelectPressed());
        if (canUseError != null) {
            selectButton.setActive(false);
            selectButton.setTooltips(canUseError);
        }
        if (file != null) {
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.BASE, this.getInterfaceStyle().container_rename, new LocalMessage("ui", "renamebutton")), 2)).onClicked(e -> this.onRenamePressed());
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.RED, this.getInterfaceStyle().button_trash_24, new LocalMessage("ui", "deletebutton")), 2)).onClicked(e -> this.onDeletePressed());
        } else {
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.BASE, this.getInterfaceStyle().container_loot_all, new LocalMessage("ui", "downloadcharacter")), 2)).onClicked(e -> {
                this.onDownloadPressed();
                ((FormButton)e.from).startCooldown(5000);
            });
        }
        int textX = 74;
        FormFlow textFlow = new FormFlow(5);
        int textMaxWidth = buttonFlow.next() - textX - 5;
        FontOptions headerOptions = new FontOptions(20);
        String header = GameUtils.maxString(character.player.playerName, headerOptions, textMaxWidth);
        this.addComponent(textFlow.nextY(new FormLabel(header, headerOptions, -1, textX, 0), 5));
        FontOptions subtitleOptions = new FontOptions(12);
        int subtitleMaxWidth = this.getWidth() - textX - 5;
        LocalMessage playTime = new LocalMessage("ui", "characterplaytime", "time", GameUtils.formatSeconds(character.timePlayed));
        this.addComponent(textFlow.nextY(new FormFairTypeLabel(playTime, subtitleOptions, FairType.TextAlign.LEFT, textX, 0).setMax(subtitleMaxWidth, 1, true), 2));
        if (file == null) {
            this.addComponent(textFlow.nextY(new FormFairTypeLabel(new LocalMessage("ui", "characterfromworld"), subtitleOptions, FairType.TextAlign.LEFT, textX, 0).setMax(subtitleMaxWidth, 1, true), 2));
        } else {
            GameMessage lastPlayed = character.lastUsed;
            if (lastPlayed == null) {
                lastPlayed = new LocalMessage("ui", "characternotplayed");
            }
            this.addComponent(textFlow.nextY(new FormFairTypeLabel(lastPlayed, subtitleOptions, FairType.TextAlign.LEFT, textX, 0).setMax(subtitleMaxWidth, 1, true), 2));
            useWarnings.forEach(warning -> this.addComponent(textFlow.nextY(new FormFairTypeLabel((GameMessage)warning, subtitleOptions.copy().color(this.getInterfaceStyle().errorTextColor), FairType.TextAlign.LEFT, textX, 0).setMax(subtitleMaxWidth, 1, true), 2)));
        }
        textFlow.next(8);
        this.setHeight(Math.max(this.getHeight(), textFlow.next()));
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormCharacterSaveComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
    }

    public abstract void onSelectPressed();

    public abstract void onRenamePressed();

    public abstract void onDeletePressed();

    public abstract void onDownloadPressed();
}

