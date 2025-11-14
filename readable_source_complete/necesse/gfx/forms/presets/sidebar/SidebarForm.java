/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.presets.sidebar.SidebarComponent;
import necesse.inventory.InventoryItem;

public abstract class SidebarForm
extends Form
implements SidebarComponent {
    protected InventoryItem playerSelectedItem;

    public SidebarForm(String name, int width, int height) {
        super(name, width, height);
    }

    public SidebarForm(String name, int width, int height, InventoryItem playerSelectedItem) {
        this(name, width, height);
        this.playerSelectedItem = playerSelectedItem;
    }

    @Override
    public void onSidebarUpdate(int x, int y) {
        this.setPosition(x, y);
    }

    @Override
    public boolean isValid(Client client) {
        PlayerMob player = client.getPlayer();
        if (player != null) {
            InventoryItem selectedItem = player.getSelectedItem();
            return this.playerSelectedItem != null && selectedItem == this.playerSelectedItem;
        }
        return true;
    }

    @Override
    public void onAdded(Client client) {
    }

    @Override
    public void onRemoved(Client client) {
    }
}

