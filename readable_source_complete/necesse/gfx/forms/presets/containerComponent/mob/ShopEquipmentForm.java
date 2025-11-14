/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.inventory.container.mob.ShopContainer;

public class ShopEquipmentForm
extends EquipmentForm {
    private ShopContainer shopContainer;

    public ShopEquipmentForm(Client client, ShopContainer container, FormEventListener<FormInputEvent<FormButton>> backButtonPressed) {
        super(client, container, MobRegistry.getLocalization(container.humanShop.getID()).translate(), container.EQUIPMENT_COSM_HEAD_SLOT, container.EQUIPMENT_COSM_CHEST_SLOT, container.EQUIPMENT_COSM_FEET_SLOT, container.EQUIPMENT_HEAD_SLOT, container.EQUIPMENT_CHEST_SLOT, container.EQUIPMENT_FEET_SLOT, container.EQUIPMENT_WEAPON_SLOT, container.setSelfManageEquipment::runAndSend, backButtonPressed);
        this.shopContainer = container;
    }

    @Override
    public HumanMob getMob() {
        return this.shopContainer.humanShop;
    }

    @Override
    public EquipmentBuffManager getEquipmentManager() {
        return this.shopContainer.humanShop.equipmentBuffManager;
    }
}

