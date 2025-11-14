/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.SettlerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class FormSettlerHelpList
extends FormGeneralGridList<ListSettler> {
    public FormSettlerHelpList(int x, int y, int width, int height) {
        super(x, y, width, height, 36, 36);
        this.reset();
    }

    @Override
    public void reset() {
        super.reset();
        for (Settler settler : SettlerRegistry.getSettlers()) {
            if (!settler.isPartOfCompleteHost) continue;
            this.elements.add(new ListSettler(settler));
        }
    }

    protected static class ListSettler
    extends FormListGridElement<FormSettlerHelpList> {
        public final Settler settler;

        public ListSettler(Settler settler) {
            this.settler = settler;
        }

        @Override
        protected void draw(FormSettlerHelpList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            if (this.isMouseOver(parent)) {
                parent.getInterfaceStyle().inventoryslot_small.highlighted.initDraw().color(parent.getInterfaceStyle().highlightElementColor).draw(2, 2);
                ListGameTooltips tooltips = new ListGameTooltips(this.settler.getGenericMobName());
                GameMessage tip = this.settler.getAcquireTip();
                if (tip != null) {
                    tooltips.add(new StringTooltips(tip.translate(), 300));
                }
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            } else {
                parent.getInterfaceStyle().inventoryslot_small.active.initDraw().color(parent.getInterfaceStyle().activeElementColor).draw(2, 2);
            }
            this.settler.getSettlerFaceDrawOptions(2, 2, 32, null).draw();
        }

        @Override
        protected void onClick(FormSettlerHelpList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
        }

        @Override
        protected void onControllerEvent(FormSettlerHelpList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        }
    }
}

