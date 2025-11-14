/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.TeleportToTeamContainer;

public class TeleportToTeamContainerForm<T extends TeleportToTeamContainer>
extends ContainerForm<T> {
    protected int maxContentHeight = 300;
    protected FormContentBox contentBox;
    protected FormLocalTextButton closeButton;
    protected ArrayList<TargetButton> targetButtons = new ArrayList();

    public TeleportToTeamContainerForm(Client client, T container) {
        super(client, 350, 300, container);
        FormFlow flow = new FormFlow(8);
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "teleporttoteamheader", new FontOptions(20), 0, this.getWidth() / 2, 0, this.getWidth() - 10), 5));
        if (((TeleportToTeamContainer)container).rangeMeters > 0) {
            LocalMessage range = new LocalMessage("ui", "teleporttoteamrange", "range", ((TeleportToTeamContainer)container).rangeMeters);
            this.addComponent(flow.nextY(new FormLocalLabel(range, new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 10), 5));
        }
        this.contentBox = this.addComponent(new FormContentBox(0, flow.next(), this.getWidth(), this.maxContentHeight));
        this.updateContent();
    }

    public void updateContent() {
        if (this.closeButton != null) {
            this.removeComponent(this.closeButton);
        }
        this.targetButtons = new ArrayList();
        this.contentBox.clearComponents();
        FormFlow flow = new FormFlow(this.contentBox.getY());
        List targets = ((TeleportToTeamContainer)this.container).teamMemberSlots.stream().map(this.client::getClient).filter(Objects::nonNull).collect(Collectors.toList());
        FormFlow contentFlow = new FormFlow(10);
        if (targets.isEmpty()) {
            this.contentBox.addComponent(contentFlow.nextY(new FormLocalLabel("ui", "teleporttoteamnone", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 10), 5));
            contentFlow.next(10);
        } else {
            for (ClientClient target : targets) {
                FormTextButton targetButton = this.contentBox.addComponent(contentFlow.nextY(new FormTextButton(target.getName(), 14, 0, this.getWidth() - 28, FormInputSize.SIZE_24, ButtonColor.BASE), 5));
                targetButton.onClicked(e -> {
                    ((TeleportToTeamContainer)this.container).teleportToSlotAction.runAndSend(target.slot);
                    this.client.closeContainer(true);
                });
                this.targetButtons.add(new TargetButton(target, targetButton));
            }
        }
        int contentHeight = contentFlow.next(4);
        if (contentHeight < this.maxContentHeight) {
            this.contentBox.setHeight(contentHeight);
        }
        this.contentBox.setContentBox(new Rectangle(0, 0, this.contentBox.getWidth(), contentHeight));
        flow.next(Math.min(contentHeight, this.maxContentHeight));
        this.closeButton = this.addComponent(flow.nextY(new FormLocalTextButton("ui", "closebutton", 4, 0, this.getWidth() - 8), 8));
        this.closeButton.onClicked(e -> this.client.closeContainer(true));
        this.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosMiddle(this);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        for (TargetButton targetButton : this.targetButtons) {
            targetButton.update();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    protected class TargetButton {
        public final ClientClient target;
        public final FormTextButton button;

        public TargetButton(ClientClient target, FormTextButton button) {
            this.target = target;
            this.button = button;
        }

        public void update() {
            int meters;
            ClientClient me = TeleportToTeamContainerForm.this.client.getClient();
            if (me == null) {
                return;
            }
            int n = meters = ((TeleportToTeamContainer)TeleportToTeamContainerForm.this.container).rangeMeters <= 0 ? 0 : ((TeleportToTeamContainer)TeleportToTeamContainerForm.this.container).getDistanceInMeters(this.target.playerMob);
            if (((TeleportToTeamContainer)TeleportToTeamContainerForm.this.container).rangeMeters > 0 && meters > ((TeleportToTeamContainer)TeleportToTeamContainerForm.this.container).rangeMeters) {
                this.button.setText(this.target.getName() + " (" + meters + "m)");
                this.button.setActive(false);
            } else {
                this.button.setText(this.target.getName());
                this.button.setActive(true);
            }
        }
    }
}

