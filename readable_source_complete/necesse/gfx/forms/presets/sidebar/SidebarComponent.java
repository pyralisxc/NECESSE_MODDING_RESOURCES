/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;

public interface SidebarComponent {
    public void onSidebarUpdate(int var1, int var2);

    public boolean isValid(Client var1);

    public void onAdded(Client var1);

    public void onRemoved(Client var1);
}

