/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.state.State;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextureButton;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class HelpForms {
    private static final HashMap<String, HelpFormConstructor> constructors = new HashMap();

    public static boolean openHelpForm(String key, Object ... data) {
        HelpFormConstructor constructor = constructors.get(key);
        if (constructor == null) {
            System.err.println("Could not find help form with key: " + key);
            return true;
        }
        State currentState = GlobalData.getCurrentState();
        ContinueComponent form = constructor.getForm(data);
        if (form != null) {
            FormManager formManager = currentState.getFormManager();
            if (formManager instanceof ContinueComponentManager) {
                ((ContinueComponentManager)((Object)formManager)).addContinueForm("help", form);
                return true;
            }
            return false;
        }
        return false;
    }

    protected static FormFairTypeLabel getInfoLabel(String localeCategory, String localeKey, int fontSize, int contentWidth) {
        FormFairTypeLabel label = new FormFairTypeLabel(new LocalMessage(localeCategory, localeKey), new FontOptions(fontSize), FairType.TextAlign.CENTER, contentWidth / 2, 0);
        label.setMaxWidth(contentWidth - 20);
        label.setParsers(TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(fontSize), TypeParsers.MobIcon(fontSize));
        return label;
    }

    protected static FormTextureButton getHelpTexture(Supplier<GameTexture> textureSupplier, int contentWidth) {
        FormTextureButton helpTexture = new FormTextureButton(contentWidth / 2, 0, textureSupplier, contentWidth - 20, -1);
        helpTexture.background = GameBackground.textBox;
        helpTexture.xAlign = FairType.TextAlign.CENTER;
        return helpTexture;
    }

    static {
        GameWindow window = WindowManager.getWindow();
        constructors.put("settlerfullinv", data -> {
            NoticeForm form = new NoticeForm("fullinvhelp", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationfullinv", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationfullinvassign", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationassignchesthelp", 16, content.getWidth()), 10));
                flow.next(10);
                content.addComponent(flow.nextY(HelpForms.getHelpTexture(() -> content.getInterfaceStyle().help_assign_chest, content.getWidth())));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
        constructors.put("settlerhungry", data -> {
            NoticeForm form = new NoticeForm("hungryhelp", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationhungry", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationhungryassign", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationassignchesthelp", 16, content.getWidth()), 10));
                flow.next(10);
                content.addComponent(flow.nextY(HelpForms.getHelpTexture(() -> content.getInterfaceStyle().help_assign_chest, content.getWidth())));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
        constructors.put("settlementlowfood", data -> {
            NoticeForm form = new NoticeForm("lowfoodhelp", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationlowfood", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationlowfoodhelp", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationassignchesthelp", 16, content.getWidth()), 10));
                flow.next(10);
                content.addComponent(flow.nextY(HelpForms.getHelpTexture(() -> content.getInterfaceStyle().help_assign_chest, content.getWidth())));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
        constructors.put("settlernobed", data -> {
            NoticeForm form = new NoticeForm("nobedhelp", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationnobed", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationnobedassign", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationbedhelp", 16, content.getWidth()), 10));
                flow.next(10);
                content.addComponent(flow.nextY(HelpForms.getHelpTexture(() -> content.getInterfaceStyle().help_assign_bed, content.getWidth())));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
        constructors.put("settlerunhappy", data -> {
            NoticeForm form = new NoticeForm("settlerunhappy", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationunhappy", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationunhappynote", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationunhappyhelp", 16, content.getWidth()), 10));
                flow.next(10);
                content.addComponent(flow.nextY(HelpForms.getHelpTexture(() -> content.getInterfaceStyle().help_settler_happiness, content.getWidth())));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
        constructors.put("settlementnoflag", data -> {
            NoticeForm form = new NoticeForm("settlementnoflag", 500, 500){

                @Override
                public void onWindowResized(GameWindow window1) {
                }
            };
            form.setupNotice(content -> {
                FormFlow flow = new FormFlow(10);
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationnoflag", 20, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationnoflagnote", 16, content.getWidth()), 10));
                content.addComponent(flow.nextY(HelpForms.getInfoLabel("ui", "notificationnoflaghelp", 16, content.getWidth()), 10));
                flow.next(10);
            }, (GameMessage)new LocalMessage("ui", "closebutton"));
            form.escapeOrBackToContinue = true;
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            form.setDraggingBox(new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE), true);
            return form;
        });
    }

    private static interface HelpFormConstructor {
        public ContinueComponent getForm(Object ... var1);
    }
}

