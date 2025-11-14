/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;

public class SoundTestGameTool
extends MouseDebugGameTool {
    public SoundTestGameTool(DebugForm parent) {
        super(parent, "Sound test");
        this.onLeftClick(e -> {
            int mouseX = this.getMouseX();
            int mouseY = this.getMouseY();
            PlayerMob player = parent.client.getPlayer();
            SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(mouseX, mouseY));
            parent.client.chat.addMessage("Sound offset: " + (mouseX - player.getX()) + ", " + (mouseY - player.getY()));
            return true;
        }, "Play sound effect here");
        this.onRightClick(e -> {
            GameToolManager.clearGameTool(this);
            return true;
        }, "Cancel");
    }

    @Override
    public void init() {
    }
}

