/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.util.function.Consumer;
import java.util.function.Function;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.gfx.forms.components.FormComponent;

public class FormClickHandler {
    public Consumer<InputEvent> onClick;
    public Function<InputEvent, Boolean> eventAccept;
    public Function<InputEvent, Boolean> eventIDAccept;
    private boolean isDown;
    private InputEvent downEvent;

    public FormClickHandler(Function<InputEvent, Boolean> eventAccept, Function<InputEvent, Boolean> eventIDAccept, Consumer<InputEvent> onClick) {
        this.eventAccept = eventAccept;
        this.eventIDAccept = eventIDAccept;
        this.onClick = onClick;
        this.isDown = false;
    }

    public FormClickHandler(Function<InputEvent, Boolean> eventAccept, int eventID, Consumer<InputEvent> onClick) {
        this(eventAccept, e -> e.getID() == eventID, onClick);
    }

    public FormClickHandler(FormComponent component, int eventID, Consumer<InputEvent> onClick) {
        this(component::isMouseOver, e -> e.getID() == eventID, onClick);
    }

    public void handleEvent(InputEvent event) {
        if (this.eventIDAccept.apply(event).booleanValue()) {
            boolean validEvent = this.eventAccept.apply(event);
            if (event.state && validEvent) {
                this.isDown = true;
                this.downEvent = event;
                event.use();
            } else {
                if (this.isDown && validEvent) {
                    this.forceClick(event);
                }
                this.isDown = false;
                this.downEvent = null;
            }
        }
    }

    public void forceHandleEvent(InputEvent event) {
        if (event.state) {
            this.isDown = true;
            this.downEvent = event;
            event.use();
        } else {
            if (this.isDown) {
                this.forceClick(event);
            }
            this.isDown = false;
            this.downEvent = null;
        }
    }

    public void forceClick(InputEvent event) {
        this.onClick.accept(event);
        event.use();
    }

    public void reset() {
        this.isDown = false;
        this.downEvent = null;
    }

    public boolean isDown() {
        return this.isDown;
    }

    public InputEvent getDownEvent() {
        return this.downEvent;
    }

    public InputPosition getDownPosition() {
        if (this.downEvent == null) {
            return null;
        }
        return this.downEvent.pos;
    }
}

