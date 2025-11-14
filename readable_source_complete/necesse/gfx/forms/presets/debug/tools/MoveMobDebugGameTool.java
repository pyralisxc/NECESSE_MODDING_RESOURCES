/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.network.packet.PacketMobDebugMove;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;

public class MoveMobDebugGameTool
extends MouseDebugGameTool {
    private Mob selectedMob;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public MoveMobDebugGameTool(DebugForm parent) {
        super(parent, "Move mob");
        this.updateActions();
    }

    public void updateActions() {
        if (this.selectedMob == null || this.selectedMob.removed()) {
            this.onLeftClick(this::selectMob, "Select mob");
            this.onRightClick(null, null);
            this.onScroll(null, null);
        } else {
            this.onLeftClick(this::teleportMob, "Move " + this.selectedMob.getDisplayName());
            this.onRightClick(this::clearMob, "Clear mob");
            this.onScroll(this::changeDir, "Change mob dir");
        }
    }

    private boolean selectMob(InputEvent event) {
        int mouseX = this.getMouseX();
        int mouseY = this.getMouseY();
        for (Mob mob : this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getMouseTileX(), this.getMouseTileY(), 1)) {
            if (!mob.getSelectBox().contains(mouseX, mouseY)) continue;
            this.selectedMob = mob;
            this.updateActions();
            break;
        }
        return true;
    }

    private boolean clearMob(InputEvent event) {
        this.selectedMob = null;
        this.updateActions();
        return true;
    }

    private boolean changeDir(InputEvent event) {
        this.wheelBuffer.add(event);
        this.wheelBuffer.useScrollY(isPositive -> {
            if (this.selectedMob != null && !this.selectedMob.removed()) {
                int dir = this.selectedMob.getDir() + (isPositive ? 1 : -1);
                if (dir > 3) {
                    dir = 0;
                } else if (dir < 0) {
                    dir = 3;
                }
                this.parent.client.network.sendPacket(new PacketMobDebugMove(this.selectedMob, this.selectedMob.getX(), this.selectedMob.getY(), dir));
            } else {
                this.selectedMob = null;
                this.updateActions();
            }
        });
        return true;
    }

    private boolean teleportMob(InputEvent event) {
        if (this.selectedMob != null && !this.selectedMob.removed()) {
            this.parent.client.network.sendPacket(new PacketMobDebugMove(this.selectedMob, this.getMouseX(), this.getMouseY(), this.selectedMob.getDir()));
        } else {
            this.selectedMob = null;
            this.updateActions();
        }
        return true;
    }

    @Override
    public void init() {
        this.selectedMob = null;
    }
}

