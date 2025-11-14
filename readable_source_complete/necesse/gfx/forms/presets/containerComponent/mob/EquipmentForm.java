/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerSettlerArmorSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSettlerWeaponSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentModifiersForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public abstract class EquipmentForm
extends ContainerFormList<Container> {
    private final Form equipmentForm = this.addComponent(new Form("settlerequipment", 400, 200));
    private Form modifiersForm;
    private final FormLabel totalArmorLabel;
    private final FormLabel bonusHealthLabel;
    public FormLocalTextButton filterEquipmentButton;
    public Runnable filterEquipmentButtonPressed;

    public EquipmentForm(Client client, Container container, String header, int cosmeticHeadSlotIndex, int cosmeticChestSlotIndex, int cosmeticFeetSlotIndex, int armorHeadSlotIndex, int armorChestSlotIndex, int armorFeetSlotIndex, int weaponSlotIndex, Consumer<Boolean> onSetSelfManageEquipment, FormEventListener<FormInputEvent<FormButton>> backButtonPressed) {
        super(client, container);
        FormFlow equipmentFlow = new FormFlow(5);
        header = GameUtils.maxString(header, new FontOptions(20), this.equipmentForm.getWidth() - 10 - 32);
        this.equipmentForm.addComponent(equipmentFlow.nextY(new FormLabel(header, new FontOptions(20), -1, 4, 0), 5));
        int equipmentSlotX = this.equipmentForm.getWidth() / 2 - 40;
        int headSlotY = equipmentFlow.next(40);
        int chestSlotY = equipmentFlow.next(40);
        int feetSlotY = equipmentFlow.next(40);
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, cosmeticHeadSlotIndex, equipmentSlotX, headSlotY, ArmorItem.ArmorType.HEAD, true, this));
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, cosmeticChestSlotIndex, equipmentSlotX, chestSlotY, ArmorItem.ArmorType.CHEST, true, this));
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, cosmeticFeetSlotIndex, equipmentSlotX, feetSlotY, ArmorItem.ArmorType.FEET, true, this));
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, armorHeadSlotIndex, equipmentSlotX + 40, headSlotY, ArmorItem.ArmorType.HEAD, false, this));
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, armorChestSlotIndex, equipmentSlotX + 40, chestSlotY, ArmorItem.ArmorType.CHEST, false, this));
        this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(client, container, armorFeetSlotIndex, equipmentSlotX + 40, feetSlotY, ArmorItem.ArmorType.FEET, false, this));
        this.equipmentForm.addComponent(new FormContainerSettlerWeaponSlot(client, container, weaponSlotIndex, equipmentSlotX - 80, chestSlotY, this));
        equipmentFlow.next(5);
        this.totalArmorLabel = this.equipmentForm.addComponent(new FormLabel("", new FontOptions(16), -1, 5, equipmentFlow.next(20), this.equipmentForm.getWidth() - 10));
        this.bonusHealthLabel = this.equipmentForm.addComponent(new FormLabel("", new FontOptions(16), -1, 5, equipmentFlow.next(20), this.equipmentForm.getWidth() - 10));
        this.equipmentForm.addComponent(new FormLocalLabel("ui", "healthfromarmortip", new FontOptions(12), -1, 5, equipmentFlow.next(20), this.equipmentForm.getWidth() - 10));
        equipmentFlow.next(5);
        FormLocalLabel label = this.equipmentForm.addComponent(equipmentFlow.nextY(new FormLocalLabel("ui", "settlerselfmanagequipment", new FontOptions(16), -1, 33, 0, this.equipmentForm.getWidth() - 5 - 24 - 4 - 5), 8));
        FormContentIconToggleButton toggleStorage = this.equipmentForm.addComponent(new FormContentIconToggleButton(5, label.getY() + label.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, this.getInterfaceStyle().button_escaped_20, new GameMessage[0]){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setToggled((Boolean)EquipmentForm.this.getMob().selfManageEquipment.get());
                super.draw(tickManager, perspective, renderBox);
            }
        });
        toggleStorage.onToggled(e -> {
            this.getMob().selfManageEquipment.set(((FormButtonToggle)e.from).isToggled());
            onSetSelfManageEquipment.accept(((FormButtonToggle)e.from).isToggled());
        });
        this.filterEquipmentButton = this.equipmentForm.addComponent(equipmentFlow.nextY(new FormLocalTextButton("ui", "settlerfilterequipment", 20, 0, this.equipmentForm.getWidth() - 40, FormInputSize.SIZE_20, ButtonColor.BASE){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                this.setActive(EquipmentForm.this.filterEquipmentButtonPressed != null && (Boolean)EquipmentForm.this.getMob().selfManageEquipment.get() != false);
                super.draw(tickManager, perspective, renderBox);
            }
        }, 5));
        this.filterEquipmentButton.onClicked(e -> {
            if (this.filterEquipmentButtonPressed != null) {
                this.filterEquipmentButtonPressed.run();
            }
        });
        FormContentIconToggleButton modifiersToggle = this.equipmentForm.addComponent(new FormContentIconToggleButton(this.equipmentForm.getWidth() - 36, 4, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().quickbar_stats_icon, new LocalMessage("ui", "settlershowmodifiers")));
        modifiersToggle.setToggled(false);
        modifiersToggle.onToggled(e -> this.modifiersForm.setHidden(!modifiersToggle.isToggled()));
        this.modifiersForm = this.addComponent(new EquipmentModifiersForm("settlermodifiers", 300, 200){

            @Override
            public Mob getMob() {
                return EquipmentForm.this.getMob();
            }
        });
        this.modifiersForm.setHidden(true);
        this.modifiersForm.setPosition(new FormRelativePosition((FormPositionContainer)this.equipmentForm, () -> this.equipmentForm.getWidth() + this.getInterfaceStyle().formSpacing, () -> 0));
        FormLocalTextButton but = this.equipmentForm.addComponent(equipmentFlow.nextY(new FormLocalTextButton("ui", "backbutton", this.equipmentForm.getWidth() - 154, 0, 150, FormInputSize.SIZE_20, ButtonColor.BASE), 4));
        but.onClicked(backButtonPressed);
        this.equipmentForm.setHeight(equipmentFlow.next());
    }

    public abstract HumanMob getMob();

    public abstract EquipmentBuffManager getEquipmentManager();

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.totalArmorLabel.setText(Localization.translate("ui", "totalarmor", "armor", (Object)Math.round(this.getMob().getArmor())), this.equipmentForm.getWidth() - 10);
        this.bonusHealthLabel.setText(Localization.translate("ui", "healthfromarmor", "health", (Object)this.getMob().getBonusHealth()), this.equipmentForm.getWidth() - 10);
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.equipmentForm);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }
}

