/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.PlayerStyleForm;
import necesse.gfx.forms.presets.containerComponent.mob.SettlerStyleForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.mob.StylistContainer;

public class StylistContainerForm<T extends StylistContainer>
extends ShopContainerForm<T> {
    public PlayerStyleForm playerStyleForm;
    public SettlerStyleForm settlerStyleForm;

    public StylistContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);
        this.playerStyleForm = this.addComponent(new PlayerStyleForm((StylistContainer)container){

            @Override
            public void onBackPressed() {
                StylistContainerForm.this.makeCurrent(StylistContainerForm.this.dialogueForm);
            }
        });
        this.settlerStyleForm = this.addComponent(new SettlerStyleForm((StylistContainer)container){

            @Override
            public void onBackPressed() {
                StylistContainerForm.this.makeCurrent(StylistContainerForm.this.dialogueForm);
            }
        });
    }

    public StylistContainerForm(Client client, T container) {
        this(client, container, 408, defaultHeight, defaultHeight);
    }

    @Override
    protected void addShopDialogueOptions() {
        super.addShopDialogueOptions();
        if (((StylistContainer)this.container).humanShop instanceof StylistHumanMob && (((StylistContainer)this.container).sellingItems != null && !((StylistContainer)this.container).sellingItems.isEmpty() || ((StylistContainer)this.container).buyingItems != null && !((StylistContainer)this.container).buyingItems.isEmpty())) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "stylistwantchange"), () -> this.makeCurrent(this.playerStyleForm));
            if (((StylistContainer)this.container).availableSettlers != null) {
                this.dialogueForm.addDialogueOption(new LocalMessage("ui", "stylistchangesettler"), () -> this.makeCurrent(this.settlerStyleForm));
            }
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.playerStyleForm);
        ContainerComponent.setPosFocus(this.settlerStyleForm);
    }
}

