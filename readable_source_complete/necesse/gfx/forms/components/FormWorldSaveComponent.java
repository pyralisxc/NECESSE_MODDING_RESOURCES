/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.WorldSave;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;

public abstract class FormWorldSaveComponent
extends Form {
    public final WorldSave worldSave;

    public FormWorldSaveComponent(int width, final WorldSave worldSave) {
        super("world" + worldSave.filePath.getName(), width, 74);
        String header;
        this.worldSave = worldSave;
        boolean isBackup = WorldSave.isLatestBackup(worldSave.filePath.getName());
        boolean isSaveArchive = worldSave.filePath.getName().endsWith(".zip") || worldSave.filePath.getName().endsWith(".rar");
        this.drawBase = false;
        FormInputSize buttonSize = FormInputSize.SIZE_24;
        FormFlow buttonFlow = new FormFlow(this.getWidth() - 5);
        if (!isBackup) {
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.GREEN, this.getInterfaceStyle().button_collapsed_24, new LocalMessage("ui", "loadsave")), 2)).onClicked(e -> this.onSelectPressed());
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.BASE, this.getInterfaceStyle().container_rename, new LocalMessage("ui", "renamebutton")), 2)).onClicked(e -> this.onRenamePressed());
        }
        FormContentIconButton backupButton = this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.BASE, isBackup ? this.getInterfaceStyle().add_existing_button : this.getInterfaceStyle().copy_button, new LocalMessage("ui", isBackup ? "restoresave" : "backupsave")), 2));
        backupButton.onClicked(e -> this.onBackupPressed(isBackup));
        if (!isBackup) {
            this.addComponent(buttonFlow.prevX(new FormContentIconButton(0, 5, buttonSize, ButtonColor.RED, this.getInterfaceStyle().button_trash_24, new LocalMessage("ui", "deletebutton")), 2)).onClicked(e -> this.onDeletePressed());
        }
        int textX = 5;
        FormFlow textFlow = new FormFlow(5);
        int textMaxWidth = buttonFlow.next() - textX - 5;
        FontOptions headerOptions = new FontOptions(20);
        if (!Settings.zipSaves && isSaveArchive) {
            headerOptions.color(this.getInterfaceStyle().warningTextColor);
            this.addComponent(new FormMouseHover(textX, textFlow.next(), textMaxWidth, this.getHeight() - textFlow.next() - 5, false){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    super.draw(tickManager, perspective, renderBox);
                    if (this.isHovering()) {
                        GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("misc", "saveiscompressed"), GameColor.ITEM_LEGENDARY, 400), TooltipLocation.FORM_FOCUS);
                    }
                }
            }, Integer.MAX_VALUE);
        }
        final boolean nameLimited = !(header = GameUtils.maxString(worldSave.displayName, headerOptions, textMaxWidth)).equals(worldSave.displayName);
        this.addComponent(textFlow.nextY(new FormLabel(header, headerOptions, -1, textX, 0){
            boolean isHovering;
            {
                super(text, fontOptions, align, x, y);
                this.isHovering = false;
            }

            @Override
            public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
                super.handleInputEvent(event, tickManager, perspective);
                this.isHovering = !event.isUsed() && this.isMouseOver(event);
            }

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                if (this.isHovering && nameLimited) {
                    GameTooltipManager.addTooltip(new StringTooltips(worldSave.displayName), TooltipLocation.FORM_FOCUS);
                }
            }
        }, 5));
        FontOptions subtitleOptions = new FontOptions(12);
        LocalMessage subtitle1 = new LocalMessage("ui", worldSave.worldSettings().creativeMode ? "newworldcreativemode" : "newworldsurvivalmode");
        this.addComponent(textFlow.nextY(new FormLocalLabel(subtitle1, subtitleOptions, -1, textX, 0, textMaxWidth), 2));
        LocalMessage subtitle2 = new LocalMessage("ui", "savetip", "day", worldSave.getWorldDay(), "time", worldSave.getWorldTimeReadable());
        this.addComponent(textFlow.nextY(new FormLocalLabel(subtitle2, subtitleOptions, -1, textX, 0, textMaxWidth), 2));
        this.addComponent(textFlow.nextY(new FormLabel(worldSave.getDate(), subtitleOptions, -1, textX, 0, textMaxWidth), 2));
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormWorldSaveComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
    }

    public abstract void onSelectPressed();

    public abstract void onRenamePressed();

    public abstract void onBackupPressed(boolean var1);

    public abstract void onDeletePressed();
}

