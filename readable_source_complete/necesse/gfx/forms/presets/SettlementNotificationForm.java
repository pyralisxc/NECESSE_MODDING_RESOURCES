/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormSettlementNotificationComponent;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;

public class SettlementNotificationForm
extends Form {
    public final Client client;
    private HashMap<Integer, SettlementNotificationSeverity> notificationComponents = new HashMap();
    protected boolean forceUpdateComponents = false;
    protected boolean playNewNotificationSounds = false;
    protected SettlementNotificationSeverity minSeverity = SettlementNotificationSeverity.NOTE;

    public SettlementNotificationForm(Client client, String name) {
        super(name, 200, 200);
        this.client = client;
        this.shouldLimitDrawArea = false;
        this.drawBase = false;
        this.onWindowResized(WindowManager.getWindow());
    }

    protected void updateComponents(NetworkSettlementData settlement) {
        this.clearComponents();
        HashMap<Integer, SettlementNotificationSeverity> lastNotifications = this.notificationComponents;
        this.notificationComponents = new HashMap();
        SettlementNotificationManager notifications = settlement.notifications;
        List sorted = notifications.getNotifications().stream().sorted(Comparator.comparingInt(d -> d.getHighestSeverity().ordinal())).collect(Collectors.toList());
        FormFlow flow = new FormFlow();
        for (SettlementNotificationManager.ActiveNotification data : sorted) {
            SettlementNotificationSeverity severity = data.getHighestSeverity();
            boolean visible = false;
            if (severity.ordinal() >= this.minSeverity.ordinal()) {
                visible = true;
                FormSettlementNotificationComponent component = this.addComponent(flow.nextY(new FormSettlementNotificationComponent(0, 0, data, severity), 4));
                component.onClicked(e -> data.notification.onClicked(this.client, data));
            }
            this.notificationComponents.put(data.notification.getID(), severity);
            SettlementNotificationSeverity lastNotification = lastNotifications.get(data.notification.getID());
            if (!this.playNewNotificationSounds || lastNotification == severity) continue;
            severity.playNewNotificationSound.accept(visible);
        }
        this.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
        this.playNewNotificationSounds = true;
    }

    protected void checkUpdateComponents(NetworkSettlementData settlement) {
        if (this.forceUpdateComponents) {
            this.forceUpdateComponents = false;
            this.updateComponents(settlement);
            return;
        }
        SettlementNotificationManager notifications = settlement.notifications;
        HashSet<Integer> existingNotifications = new HashSet<Integer>(this.notificationComponents.keySet());
        for (SettlementNotificationManager.ActiveNotification n : notifications.getNotifications()) {
            if (!existingNotifications.remove(n.notification.getID())) {
                this.updateComponents(settlement);
                return;
            }
            if (this.notificationComponents.get(n.notification.getID()) == n.getHighestSeverity()) continue;
            this.updateComponents(settlement);
            return;
        }
        if (!existingNotifications.isEmpty()) {
            this.updateComponents(settlement);
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        NetworkSettlementData settlement = null;
        if (perspective != null && perspective.getLevel() != null) {
            settlement = SettlementsWorldData.getSettlementsData(this.client).getNetworkDataAtTile(perspective.getLevel().getIdentifier(), perspective.getTileX(), perspective.getTileY());
        }
        if (settlement != null) {
            this.checkUpdateComponents(settlement);
        } else {
            this.clearComponents();
            this.notificationComponents.clear();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public void showSeverityAbove(SettlementNotificationSeverity severity) {
        if (this.minSeverity != severity) {
            this.forceUpdateComponents = true;
        }
        this.minSeverity = severity;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosition(10, window.getHudHeight() - 280 - 80 - 5 - this.getHeight());
    }

    @Override
    public boolean shouldSkipRenderBoxCheck() {
        return true;
    }
}

