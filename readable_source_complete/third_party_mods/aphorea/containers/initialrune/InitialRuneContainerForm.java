/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.input.InputEvent
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.containerSlot.FormContainerSlot
 *  necesse.gfx.forms.components.localComponents.FormLocalLabel
 *  necesse.gfx.forms.presets.containerComponent.ContainerForm
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.inventory.InventoryItem
 */
package aphorea.containers.initialrune;

import aphorea.containers.initialrune.FormInitialRunesList;
import aphorea.containers.initialrune.InitialRuneContainer;
import aphorea.registry.AphItems;
import java.util.Objects;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;

public class InitialRuneContainerForm<T extends InitialRuneContainer>
extends ContainerForm<T> {
    public FormContainerSlot[] slots;
    public FormInitialRunesList runesList;

    public InitialRuneContainerForm(Client client, T container) {
        super(client, 408, 180, container);
        this.addComponent((FormComponent)new FormLocalLabel((GameMessage)new StaticMessage(Localization.translate((String)"item", (String)"initialrune")), new FontOptions(20), -1, 10, 10));
        this.runesList = new FormInitialRunesList(6, 40, this.getWidth() - 6, this.getHeight() - 46 - 40, client, (InitialRuneContainer)((Object)container)){
            final /* synthetic */ InitialRuneContainer val$container;
            {
                this.val$container = initialRuneContainer;
                super(x, y, width, height, client);
            }

            @Override
            public void onRuneClicked(InventoryItem rune, InputEvent event) {
                this.playTickSound();
                int n = -1;
                for (int i = 0; i < AphItems.initialRunes.size(); ++i) {
                    if (!Objects.equals(AphItems.initialRunes.get(i).getStringID(), rune.item.getStringID())) continue;
                    n = i;
                    break;
                }
                if (n != -1) {
                    this.val$container.executeRuneAction.runAndSend(n, 0);
                }
            }
        };
        this.addComponent((FormComponent)this.runesList);
        this.addComponent((FormComponent)new FormLocalLabel((GameMessage)new StaticMessage(Localization.translate((String)"message", (String)"initialrunetip")), new FontOptions(14), -1, 10, this.getHeight() - 46 + 8));
        this.loadRunes();
    }

    public void loadRunes() {
        this.runesList.setRunes(((InitialRuneContainer)this.container).getInitialRunes());
    }
}

