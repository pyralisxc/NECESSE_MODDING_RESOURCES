/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketObjectSwitched;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class SwitchObject
extends GameObject {
    public int counterID;

    public SwitchObject(Rectangle collision, int counterID, boolean isSwitched) {
        super(collision);
        this.isSwitch = true;
        this.isSwitched = isSwitched;
        this.counterID = counterID;
    }

    @Override
    public GameMessage getNewLocalization() {
        if (this.counterID == -1 || !this.isSwitched) {
            return super.getNewLocalization();
        }
        return ObjectRegistry.getObject(this.counterID).getNewLocalization();
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (this.counterID == -1) {
            return;
        }
        if (level.isServer()) {
            this.onSwitched(level, x, y);
        }
    }

    public void onSwitched(Level level, int x, int y) {
        level.setObject(x, y, this.counterID);
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithTile(new PacketObjectSwitched(level, x, y, this.getID()), level, x, y);
        }
        if (level.isClient()) {
            this.playSwitchSound(level, x, y);
        }
    }

    public void playSwitchSound(Level level, int x, int y) {
    }

    @Override
    public boolean shouldShowInItemList() {
        return super.shouldShowInItemList() && !this.isSwitched;
    }
}

