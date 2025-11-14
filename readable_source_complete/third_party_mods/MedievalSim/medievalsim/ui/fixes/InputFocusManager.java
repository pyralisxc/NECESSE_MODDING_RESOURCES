/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.input.InputEvent
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextInput
 *  necesse.gfx.forms.components.FormTypingComponent
 */
package medievalsim.ui.fixes;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.FormTypingComponent;

public class InputFocusManager {
    public static void enhanceTextInput(FormTextInput textInput) {
        if (textInput == null) {
            return;
        }
        textInput.onSubmit(e -> textInput.clearSelection());
    }

    public static boolean isAnyTextInputFocused() {
        return FormTypingComponent.isCurrentlyTyping();
    }

    public static class EnhancedTextInput
    extends FormTextInput {
        private boolean hasFocus = false;

        public EnhancedTextInput(int x, int y, FormInputSize size, int width, int maxLength) {
            super(x, y, size, width, maxLength);
            this.setupEnhancedBehavior();
        }

        public EnhancedTextInput(int x, int y, FormInputSize size, int width, int maxWidth, int maxLength) {
            super(x, y, size, width, maxWidth, maxLength);
            this.setupEnhancedBehavior();
        }

        private void setupEnhancedBehavior() {
            this.onSubmit(e -> {
                this.clearSelection();
                this.hasFocus = false;
            });
        }

        public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob playerMob) {
            if (this.isTyping()) {
                this.hasFocus = true;
                super.handleInputEvent(event, tickManager, playerMob);
            } else {
                this.hasFocus = false;
                super.handleInputEvent(event, tickManager, playerMob);
            }
        }

        public boolean hasInputFocus() {
            return this.hasFocus && this.isTyping();
        }

        public void clearFocus() {
            this.clearSelection();
            this.hasFocus = false;
        }

        public void setPlaceholder(String text) {
            this.placeHolder = new StaticMessage(text);
        }

        public void setPlaceholder(GameMessage message) {
            this.placeHolder = message;
        }
    }
}

