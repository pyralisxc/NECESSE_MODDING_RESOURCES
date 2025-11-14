/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormBuffList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DebugBuffsForm
extends Form {
    public FormBuffList buffList;
    public FormTextInput buffFilter;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private int duration = 10;

    public DebugBuffsForm(String name, final DebugForm parent) {
        super(name, 240, 400);
        this.addComponent(new FormLabel("Buffs", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.buffList = this.addComponent(new FormBuffList(0, 36, this.getWidth(), this.getHeight() - 140){

            @Override
            public void onClicked(final Buff buff) {
                MouseDebugGameTool tool = new MouseDebugGameTool(parent, null){

                    @Override
                    public void init() {
                        this.onLeftClick(e -> {
                            int mouseX = this.getMouseX();
                            int mouseY = this.getMouseY();
                            for (Mob mob : this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getMouseTileX(), this.getMouseTileY(), 1)) {
                                if (!mob.getSelectBox().contains(mouseX, mouseY)) continue;
                                while (mob.isRiding() && mob.getMount() != null) {
                                    mob = mob.getMount();
                                }
                                mob.addBuff(new ActiveBuff(buff, mob, (float)DebugBuffsForm.this.duration, null), true);
                                return true;
                            }
                            GameUtils.streamClientClients(this.parent.client.getLevel()).filter(c -> c.playerMob.getSelectBox().contains(mouseX, mouseY)).findFirst().ifPresent(c -> {
                                Mob mob = c.playerMob;
                                while (mob.isRiding() && mob.getMount() != null) {
                                    mob = mob.getMount();
                                }
                                mob.addBuff(new ActiveBuff(buff, mob, (float)DebugBuffsForm.this.duration, null), true);
                            });
                            return true;
                        }, "Give buff");
                        this.onRightClick(e -> {
                            int mouseX = this.getMouseX();
                            int mouseY = this.getMouseY();
                            for (Mob mob : this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getMouseTileX(), this.getMouseTileY(), 1)) {
                                if (!mob.getSelectBox().contains(mouseX, mouseY)) continue;
                                if (mob.isRiding() && mob.getMount() != null) {
                                    mob = mob.getMount();
                                }
                                mob.buffManager.removeBuff(buff.getID(), true);
                                return true;
                            }
                            GameUtils.streamClientClients(this.parent.client.getLevel()).filter(c -> c.playerMob.getSelectBox().contains(mouseX, mouseY)).findFirst().ifPresent(c -> {
                                Mob mob = c.playerMob;
                                while (mob.isRiding() && mob.getMount() != null) {
                                    mob = mob.getMount();
                                }
                                mob.buffManager.removeBuff(buff.getID(), true);
                            });
                            return true;
                        }, "Remove buff");
                        this.onScroll(e -> {
                            DebugBuffsForm.this.wheelBuffer.add((InputEvent)e);
                            DebugBuffsForm.this.wheelBuffer.useScrollY(isPositive -> {
                                int delta = DebugBuffsForm.this.duration >= 600 ? 600 : (DebugBuffsForm.this.duration >= 60 ? 60 : (DebugBuffsForm.this.duration >= 10 ? 10 : 1));
                                if (isPositive) {
                                    DebugBuffsForm.this.duration = Math.min(3600, DebugBuffsForm.this.duration + delta);
                                } else {
                                    DebugBuffsForm.this.duration = Math.max(1, DebugBuffsForm.this.duration - delta);
                                }
                            });
                            return true;
                        }, "Change duration");
                    }

                    @Override
                    public GameTooltips getTooltips() {
                        ListGameTooltips tooltips = new ListGameTooltips(super.getTooltips());
                        tooltips.add("Duration: " + GameUtils.formatSeconds(DebugBuffsForm.this.duration));
                        return tooltips;
                    }
                };
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
            }
        });
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
        this.buffFilter = this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
        this.buffFilter.placeHolder = new StaticMessage("Search filter");
        this.buffFilter.rightClickToClear = true;
        this.buffFilter.onChange(e -> this.buffList.setFilter(((FormTextInput)e.from).getText()));
        this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.mainMenu));
    }
}

