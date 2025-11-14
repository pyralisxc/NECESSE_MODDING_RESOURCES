/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class FormTestList
extends FormGeneralList<TestElement> {
    private int itemCounter = 0;

    public FormTestList(int x, int y, int width, int height) {
        super(x, y, width, height, 25);
        for (int i = 0; i < 10; ++i) {
            this.addElement();
        }
    }

    public void deleteElement() {
        this.elements.remove(this.elements.size() - 1);
    }

    public void addElement() {
        this.elements.add(new TestElement(this.itemCounter++));
    }

    public class TestElement
    extends FormListElement<FormTestList> {
        private int index;

        public TestElement(int index) {
            this.index = index;
        }

        @Override
        protected void draw(FormTestList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            float shade = this.isMouseOver(parent) ? 1.0f : 0.8f;
            FontOptions options = new FontOptions(16).colorf(shade, shade, shade);
            FontManager.bit.drawString(4.0f, 2.0f, "Item #" + this.index, options);
        }

        @Override
        protected void onClick(FormTestList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            System.out.println("Clicked " + elementIndex + ", " + event.pos.hudX + ", " + event.pos.hudY + " with buttton " + event.getID());
        }

        @Override
        protected void onControllerEvent(FormTestList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            System.out.println("Clicked " + elementIndex + ", with controller " + event);
        }
    }
}

