/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketAdventurePartyCompressInventory;
import necesse.engine.network.server.AdventureParty;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;

public class PartyConfigForm
extends Form {
    protected Client client;
    protected Container container;
    protected int slotStartIndex;
    protected int slotEndIndex;
    protected Runnable sizeUpdated;
    public FormLocalLabel partySizeLabel;
    public FormDropdownSelectionButton<AdventureParty.BuffPotionPolicy> buffPolicyDropdown;
    public FormContentBox inventoryContent;
    public FormContainerSlot[] slots;
    public FormLocalTextButton compressInventoryButton;
    public FormLocalTextButton backButton;
    protected int lastInventorySize;
    protected int lastPartySize;

    public PartyConfigForm(Client client, Container container, int slotStartIndex, int slotEndIndex, int width, Runnable commandPressed, Runnable backPressed, Runnable sizeUpdated) {
        super("partyConfig", width, 90);
        this.client = client;
        this.container = container;
        this.slotStartIndex = slotStartIndex;
        this.slotEndIndex = slotEndIndex;
        FormFlow flow = new FormFlow(5);
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "adventureparty", new FontOptions(20), -1, 4, 0), 5));
        this.lastPartySize = client.adventureParty.getSize();
        this.partySizeLabel = this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize), new FontOptions(12), -1, 4, 0), 5));
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "adventurepartytip", new FontOptions(12), -1, 4, 0, this.getWidth() - 8), 10));
        if (commandPressed != null) {
            int commandButtonWidth = Math.min(width - 8, 350);
            this.addComponent(flow.nextY(new FormLocalTextButton("ui", "adventurepartycommand", this.getWidth() / 2 - commandButtonWidth / 2, 4, commandButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE), 10)).onClicked(e -> commandPressed.run());
        }
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "buffpotionpolicy", new FontOptions(20), -1, 4, 0), 5));
        int buffPolicyButtonWidth = Math.min(width - 8, 350);
        this.buffPolicyDropdown = this.addComponent(flow.nextY(new FormDropdownSelectionButton(this.getWidth() / 2 - buffPolicyButtonWidth / 2, 4, FormInputSize.SIZE_24, ButtonColor.BASE, buffPolicyButtonWidth), 10));
        for (AdventureParty.BuffPotionPolicy policy : AdventureParty.BuffPotionPolicy.values()) {
            this.buffPolicyDropdown.options.add(policy, policy.displayName);
        }
        this.buffPolicyDropdown.setSelected(client.adventureParty.getBuffPotionPolicy(), client.adventureParty.getBuffPotionPolicy().displayName);
        this.buffPolicyDropdown.onSelected(e -> client.adventureParty.setBuffPotionPolicy((AdventureParty.BuffPotionPolicy)((Object)((Object)e.value)), true));
        if (slotStartIndex != -1) {
            this.addComponent(flow.nextY(new FormLocalLabel("ui", "adventurepartyinventory", new FontOptions(20), -1, 4, 0, this.getWidth() - 8), 2));
            this.addComponent(flow.nextY(new FormLocalLabel("ui", "adventurepartyinventorytip", new FontOptions(12), -1, 4, 0, this.getWidth() - 8), 4));
            this.inventoryContent = this.addComponent(new FormContentBox(0, flow.next(40), this.getWidth(), 40));
        }
        int compressInventoryButtonWidth = Math.min(width - 8, 350);
        this.compressInventoryButton = this.addComponent(new FormLocalTextButton("ui", "adventurepartyinventorycompress", this.getWidth() / 2 - compressInventoryButtonWidth / 2, 4, compressInventoryButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.compressInventoryButton.onClicked(e -> client.network.sendPacket(new PacketAdventurePartyCompressInventory()));
        if (backPressed != null) {
            this.backButton = this.addComponent(new FormLocalTextButton("ui", "backbutton", this.getWidth() / 2 - 75, 4, 150, FormInputSize.SIZE_24, ButtonColor.BASE));
            this.backButton.onClicked(e -> backPressed.run());
        }
        this.updateHeight();
        this.sizeUpdated = sizeUpdated;
    }

    public void updateHeight() {
        FormFlow flow = new FormFlow();
        if (this.inventoryContent != null) {
            flow.next(this.inventoryContent.getY());
            this.slots = new FormContainerSlot[this.slotEndIndex - this.slotStartIndex + 1];
            int maxInventoryHeight = 140;
            int slotSize = 40;
            int maxWidth = this.getWidth() - 8;
            int columns = Math.max(maxWidth / slotSize, 1);
            int inventorySize = Math.min(this.container.client.playerMob.getInv().party.getSize(), this.slots.length);
            int rows = (int)Math.ceil((float)inventorySize / (float)columns);
            this.inventoryContent.clearComponents();
            for (int i = 0; i < inventorySize; ++i) {
                int slotIndex = i + this.slotStartIndex;
                int x = i % columns;
                int y = i / columns;
                FormContainerSlot slot = new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, y * 40);
                slot.setDecal(i % 2 == 0 ? this.getInterfaceStyle().inventoryslot_icon_food : this.getInterfaceStyle().inventoryslot_icon_potion);
                this.slots[i] = this.inventoryContent.addComponent(slot);
                if (i != 3) continue;
                this.buffPolicyDropdown.controllerDownFocus = this.slots[i];
            }
            this.inventoryContent.setContentBox(new Rectangle(0, 0, this.getWidth(), rows * slotSize));
            this.inventoryContent.setHeight(Math.min(maxInventoryHeight, rows * slotSize));
            flow.next(this.inventoryContent.getHeight() + 5);
        }
        if (this.compressInventoryButton != null) {
            this.compressInventoryButton.setY(flow.next(28));
        }
        if (this.backButton != null) {
            this.backButton.setY(flow.next(28));
        }
        this.setHeight(flow.next());
        if (this.sizeUpdated != null) {
            this.sizeUpdated.run();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.buffPolicyDropdown != null && this.buffPolicyDropdown.getSelected() != this.client.adventureParty.getBuffPotionPolicy()) {
            this.buffPolicyDropdown.setSelected(this.client.adventureParty.getBuffPotionPolicy(), this.client.adventureParty.getBuffPotionPolicy().displayName);
        }
        if (this.lastPartySize != this.client.adventureParty.getSize()) {
            this.lastPartySize = this.client.adventureParty.getSize();
            this.partySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize));
        }
        if (this.inventoryContent != null && this.lastInventorySize != this.container.client.playerMob.getInv().party.getSize()) {
            this.updateHeight();
            this.lastInventorySize = this.container.client.playerMob.getInv().party.getSize();
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

