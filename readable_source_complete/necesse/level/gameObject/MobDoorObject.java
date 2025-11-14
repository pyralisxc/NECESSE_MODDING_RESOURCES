/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.Level;

public class MobDoorObject
extends SwitchObject {
    private final ArrayList<Mob> originalMobs;
    private ArrayList<Mob> remainingMobs;
    private int[] spawnZone;
    private boolean arenaHasStarted = false;

    public MobDoorObject(Rectangle collision, int counterID, ArrayList<Mob> mobs) {
        super(collision, counterID, false);
        this.originalMobs = mobs;
        this.remainingMobs = mobs;
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        if (active) {
            if (!this.isSwitched && !this.arenaHasStarted && wireID == 0 && this.spawnZone != null) {
                this.setRemainingMobs(this.originalMobs);
                for (int i = 0; i < this.originalMobs.size(); ++i) {
                }
                this.arenaHasStarted = true;
            } else if (this.isSwitched && wireID != 0) {
                this.isSwitched = false;
            }
            this.checkSwitchConditions();
        }
    }

    public void checkSwitchConditions() {
        if (this.remainingMobs.isEmpty()) {
            this.isSwitched = true;
            this.spawnZone = null;
        } else {
            this.arenaHasStarted = false;
        }
    }

    public void setRemainingMobs(ArrayList<Mob> mobs) {
        this.remainingMobs = mobs;
    }

    public void setSpawnZone(int startX, int startY, int endX, int endY) {
        this.spawnZone = new int[]{startX, startY, endX, endY};
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }
}

