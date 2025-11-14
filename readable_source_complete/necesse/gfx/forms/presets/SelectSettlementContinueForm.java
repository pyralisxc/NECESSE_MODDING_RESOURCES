/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;

public abstract class SelectSettlementContinueForm
extends NoticeForm {
    public SelectSettlementContinueForm(String name, int width, int maxHeight, GameMessage header, Option ... options) {
        super(name, width, maxHeight);
        this.setupNotice((FormContentBox content) -> {
            FormFlow flow = new FormFlow();
            if (header != null) {
                flow.next(5);
                content.addComponent(flow.nextY(new FormLocalLabel(header, new FontOptions(20), 0, width / 2, 0, width - 10)));
                flow.next(5);
            }
            if (options.length == 0) {
                flow.next(10);
                content.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementselecthelp"), new FontOptions(16), 0, width / 2, 0, width - 10)));
                flow.next(10);
            } else {
                for (Option option : options) {
                    FormLocalTextButton button = content.addComponent(new FormLocalTextButton(option.name, 0, flow.next(40), width));
                    button.onClicked(e -> option.onSelected(this));
                    button.setActive(option.available);
                }
            }
        }, (GameMessage)new LocalMessage("ui", "cancelbutton"));
        this.onContinue(this::onCancel);
    }

    public abstract void onCancel();

    public static abstract class Option {
        public final boolean available;
        public final int settlementUniqueID;
        public final GameMessage name;

        public Option(boolean available, int settlementUniqueID, GameMessage name) {
            this.available = available;
            this.settlementUniqueID = settlementUniqueID;
            this.name = name;
        }

        public abstract void onSelected(SelectSettlementContinueForm var1);
    }
}

