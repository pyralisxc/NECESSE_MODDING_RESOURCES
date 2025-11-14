/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.level.maps.levelData.settlementData.Waystone;

public class HomestoneContainerForm<T extends HomestoneContainer>
extends ContainerForm<T> {
    private FormContentBox content;
    private ArrayList<WaystoneComponents> buttons;
    private FormLocalLabel emptyLabel;

    public HomestoneContainerForm(Client client, T container) {
        super(client, 408, 200, container);
        this.addComponent(new FormLocalLabel("object", "homestone", new FontOptions(20), -1, 4, 4, 390));
        this.emptyLabel = this.addComponent(new FormLocalLabel(new StaticMessage(""), new FontOptions(16), -1, 4, 24, 390));
        this.addComponent(new FormContentIconButton(this.getWidth() - 24, 4, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().button_help_20, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                return new StringTooltips().add(Localization.translate("ui", "waystonehelp"), 400);
            }
        });
        this.content = this.addComponent(new FormContentBox(0, 45, this.getWidth(), this.getHeight() - 45));
        this.content.alwaysShowVerticalScrollBar = true;
        this.updateWaystones();
        ((Container)((Object)container)).onEvent(HomestoneUpdateEvent.class, e -> this.updateWaystones());
    }

    public void updateWaystones() {
        GameWindow window = WindowManager.getWindow();
        int heightPerWaystone = 28;
        int maxWaystoneHeight = ((HomestoneContainer)this.container).maxWaystones * heightPerWaystone + 2;
        int contentHeight = Math.max(Math.min(window.getHudHeight() - 400, maxWaystoneHeight), 200);
        this.content.setHeight(contentHeight);
        this.setHeight(this.content.getY() + contentHeight);
        int free = ((HomestoneContainer)this.container).maxWaystones - ((HomestoneContainer)this.container).waystones.size();
        this.emptyLabel.setLocalization(new LocalMessage("ui", "emptywaystones", "count", free));
        if (this.buttons != null) {
            for (WaystoneComponents waystoneComponents : this.buttons) {
                waystoneComponents.dispose();
            }
        }
        this.buttons = new ArrayList();
        for (int i = 0; i < ((HomestoneContainer)this.container).maxWaystones; ++i) {
            if (i < ((HomestoneContainer)this.container).waystones.size()) {
                Waystone waystone = ((HomestoneContainer)this.container).waystones.get(i);
                this.buttons.add(new WaystoneComponents(this, i, waystone));
                continue;
            }
            FormTextButton formTextButton = this.content.addComponent(new FormLocalTextButton("ui", "waystoneempty", 4, i * heightPerWaystone + 2, this.getWidth() - 16, FormInputSize.SIZE_24, ButtonColor.BASE));
            formTextButton.setActive(false);
        }
        Rectangle contentRectangle = this.content.getContentBoxToFitComponents().union(new Rectangle(0, 0, this.getWidth(), this.content.getHeight()));
        this.content.setContentBox(contentRectangle);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateWaystones();
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }

    private static class WaystoneComponents {
        public final HomestoneContainerForm<?> form;
        public final int index;
        public final Waystone waystone;
        public FormTextButton button;
        public FormTextInput renameInput;
        public FormContentIconButton renameButton;
        public FormIconButton moveUpButton;
        public FormIconButton moveDownButton;
        public int buttonX;

        public WaystoneComponents(HomestoneContainerForm<?> form, int index, Waystone waystone) {
            this.form = form;
            this.index = index;
            this.waystone = waystone;
            this.buttonX = form.getWidth() - 8 - 28;
            int y = index * 28 + 2;
            this.moveUpButton = ((HomestoneContainerForm)form).content.addComponent(new FormIconButton(4, y - 1, form.getInterfaceStyle().button_moveup, 16, 13, new LocalMessage("ui", "moveupbutton")));
            this.moveUpButton.onClicked(e -> {
                ((HomestoneContainer)((HomestoneContainerForm)form).container).moveWaystoneUp.runAndSend(index, WindowManager.getWindow().isKeyDown(340) || WindowManager.getWindow().isKeyDown(344));
                form.updateWaystones();
            });
            this.moveUpButton.setActive(index > 0);
            this.moveDownButton = ((HomestoneContainerForm)form).content.addComponent(new FormIconButton(4, y + 12, form.getInterfaceStyle().button_movedown, 16, 13, new LocalMessage("ui", "movedownbutton")));
            this.moveDownButton.onClicked(e -> {
                ((HomestoneContainer)((HomestoneContainerForm)form).container).moveWaystoneDown.runAndSend(index, WindowManager.getWindow().isKeyDown(340) || WindowManager.getWindow().isKeyDown(344));
                form.updateWaystones();
            });
            this.moveDownButton.setActive(index < ((HomestoneContainer)((HomestoneContainerForm)form).container).waystones.size() - 1);
            this.renameButton = ((HomestoneContainerForm)form).content.addComponent(new FormContentIconButton(this.buttonX, y, FormInputSize.SIZE_24, ButtonColor.BASE, form.getInterfaceStyle().container_rename, new LocalMessage("ui", "renamebutton")));
            this.setupButton();
            this.renameInput = null;
            this.renameButton.onClicked(e -> {
                if (this.renameInput == null) {
                    ((HomestoneContainerForm)form).content.removeComponent(this.button);
                    this.renameButton.setIcon(form.getInterfaceStyle().container_rename_save);
                    this.renameButton.setTooltips(new LocalMessage("ui", "savebutton"));
                    this.renameInput = ((HomestoneContainerForm)form).content.addComponent(new FormTextInput(24, y, FormInputSize.SIZE_24, this.buttonX - 28, 25));
                    this.renameInput.setText(this.button.getText());
                    this.renameInput.onSubmit(e2 -> {
                        form.playTickSound();
                        this.submitRename(((FormTextInput)e2.from).getText());
                    });
                    this.renameInput.setTyping(true);
                    this.button = null;
                } else {
                    this.submitRename(this.renameInput.getText());
                }
            });
        }

        public void submitRename(String name) {
            this.waystone.name = name;
            if (this.renameInput != null) {
                ((HomestoneContainerForm)this.form).content.removeComponent(this.renameInput);
            }
            if (this.button != null) {
                ((HomestoneContainerForm)this.form).content.removeComponent(this.button);
            }
            this.renameInput = null;
            this.setupButton();
            this.renameButton.setIcon(this.form.getInterfaceStyle().container_rename);
            this.renameButton.setTooltips(new LocalMessage("ui", "renamebutton"));
            ((HomestoneContainer)((HomestoneContainerForm)this.form).container).renameWaystone.runAndSend(this.index, name);
        }

        private void setupButton() {
            this.button = ((HomestoneContainerForm)this.form).content.addComponent(new FormTextButton("", 24, this.index * 28 + 2, this.buttonX - 28, FormInputSize.SIZE_24, ButtonColor.BASE));
            this.updateName();
            this.button.onClicked(e -> ((HomestoneContainer)((HomestoneContainerForm)this.form).container).useWaystone.runAndSend(this.index));
        }

        public void updateName() {
            if (this.button != null) {
                if (this.waystone.name.isEmpty()) {
                    this.button.setText(Localization.translate("ui", "waystonenum", "num", (Object)(this.index + 1)));
                } else {
                    this.button.setText(this.waystone.name);
                }
            }
        }

        public void dispose() {
            if (this.button != null) {
                ((HomestoneContainerForm)this.form).content.removeComponent(this.button);
            }
            if (this.renameInput != null) {
                ((HomestoneContainerForm)this.form).content.removeComponent(this.renameInput);
            }
            if (this.renameButton != null) {
                ((HomestoneContainerForm)this.form).content.removeComponent(this.renameButton);
            }
        }
    }
}

