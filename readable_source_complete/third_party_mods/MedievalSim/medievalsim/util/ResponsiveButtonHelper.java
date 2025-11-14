/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormCheckBox
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormContentIconButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 *  necesse.gfx.ui.ButtonTexture
 */
package medievalsim.util;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;

public final class ResponsiveButtonHelper {
    private ResponsiveButtonHelper() {
    }

    public static int calculateOptimalWidth(String text, int minWidth, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return minWidth;
        }
        try {
            FontOptions defaultFontOptions = new FontOptions(16);
            int textWidth = FontManager.bit.getWidthCeil(text, defaultFontOptions);
            int paddedWidth = textWidth + 32;
            System.out.println("MedievalSim: INFO - Button width calculation for '" + text + "':");
            System.out.println("  Text width: " + textWidth + "px, Padded: " + paddedWidth + "px");
            System.out.println("  Min: " + minWidth + "px, Max: " + maxWidth + "px");
            int finalWidth = Math.max(minWidth, Math.min(paddedWidth, maxWidth));
            System.out.println("  Final width: " + finalWidth + "px");
            if (finalWidth == maxWidth && paddedWidth > maxWidth) {
                System.err.println("MedievalSim: WARNING - Button text '" + text + "' may be truncated! Needs " + paddedWidth + "px but limited to " + maxWidth + "px");
            }
            return finalWidth;
        }
        catch (Exception e) {
            System.err.println("MedievalSim: WARNING - Failed to measure text width for: " + text + ". Using standard width.");
            return 240;
        }
    }

    public static FormTextButton createButton(String text, int x, int y, int maxAvailableWidth) {
        int optimalWidth = ResponsiveButtonHelper.calculateOptimalWidth(text, 160, maxAvailableWidth - 32);
        return new FormTextButton(text, x, y, optimalWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
    }

    public static FormTextButton createLocalizedButton(String category, String key, int x, int y, int maxAvailableWidth) {
        String text = Localization.translate((String)category, (String)key);
        return ResponsiveButtonHelper.createButton(text, x, y, maxAvailableWidth);
    }

    public static FormTextButton[] createEqualButtonLayout(Form form, String[] labels, int y, int containerWidth) {
        if (labels == null || labels.length == 0) {
            return new FormTextButton[0];
        }
        int totalMargin = 32;
        int buttonCount = labels.length;
        int totalSpacing = 8 * (buttonCount - 1);
        int availableWidth = containerWidth - totalMargin - totalSpacing;
        int buttonWidth = availableWidth / buttonCount;
        if (buttonWidth < 160) {
            System.err.println("MedievalSim: WARNING - Calculated button width (" + buttonWidth + "px) is below minimum. Using minimum width.");
            buttonWidth = 160;
        }
        FormTextButton[] buttons = new FormTextButton[buttonCount];
        int currentX = 16;
        for (int i = 0; i < buttonCount; ++i) {
            buttons[i] = new FormTextButton(labels[i], currentX, y, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
            form.addComponent((FormComponent)buttons[i]);
            currentX += buttonWidth + 8;
        }
        return buttons;
    }

    public static FormTextButton[] createEqualLocalizedButtons(Form form, String category, String[] keys, int y, int containerWidth) {
        String[] labels = new String[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            labels[i] = Localization.translate((String)category, (String)keys[i]);
        }
        return ResponsiveButtonHelper.createEqualButtonLayout(form, labels, y, containerWidth);
    }

    public static FormTextButton[] createDialogButtons(Form form, String[] labels, int y, int containerWidth) {
        int startX;
        if (labels == null || labels.length == 0) {
            return new FormTextButton[0];
        }
        int maxTextWidth = 0;
        FontOptions defaultFontOptions = new FontOptions(16);
        for (String label : labels) {
            if (label == null || label.isEmpty()) continue;
            try {
                int textWidth = FontManager.bit.getWidthCeil(label, defaultFontOptions);
                maxTextWidth = Math.max(maxTextWidth, textWidth);
            }
            catch (Exception textWidth) {
                // empty catch block
            }
        }
        int buttonWidth = Math.max(160, maxTextWidth + 16);
        FormTextButton[] buttons = new FormTextButton[labels.length];
        int totalButtonWidth = buttonWidth * labels.length + 8 * (labels.length - 1);
        int currentX = startX = containerWidth - 16 - totalButtonWidth;
        for (int i = 0; i < labels.length; ++i) {
            buttons[i] = new FormTextButton(labels[i], currentX, y, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
            form.addComponent((FormComponent)buttons[i]);
            currentX += buttonWidth + 8;
        }
        return buttons;
    }

    public static void validateButtonLayout(FormTextButton[] buttons, int expectedCount) {
        if (buttons.length != expectedCount) {
            System.err.println("MedievalSim: WARNING - Button layout mismatch. Expected: " + expectedCount + ", Got: " + buttons.length);
        }
    }

    public static void debugButtonSizes(FormTextButton[] buttons, String layoutName) {
        System.out.println("MedievalSim: INFO - Button sizes for " + layoutName + ":");
        for (int i = 0; i < buttons.length; ++i) {
            System.out.println("  Button " + i + ": " + buttons[i].getWidth() + "px wide");
        }
    }

    public static boolean doesTextFit(String text, int availableWidth) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        try {
            boolean fits;
            FontOptions fontOptions = new FontOptions(16);
            int textWidth = FontManager.bit.getWidthCeil(text, fontOptions);
            int requiredWidth = textWidth + 32;
            boolean bl = fits = requiredWidth <= availableWidth;
            if (!fits) {
                System.err.println("MedievalSim: WARNING - Text '" + text + "' WILL NOT FIT! Needs " + requiredWidth + "px but only " + availableWidth + "px available");
            }
            return fits;
        }
        catch (Exception e) {
            return true;
        }
    }

    public static FormTextButton createAutoExpandingButton(String text, int x, int y, int preferredWidth) {
        int optimalWidth = ResponsiveButtonHelper.calculateOptimalWidth(text, 160, Integer.MAX_VALUE);
        int actualWidth = Math.max(preferredWidth, optimalWidth);
        if (actualWidth > preferredWidth) {
            System.out.println("MedievalSim: INFO - Auto-expanding button '" + text + "' from " + preferredWidth + "px to " + actualWidth + "px to prevent truncation");
        }
        return new FormTextButton(text, x, y, actualWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
    }

    public static FormTextButton[] createTabLayout(Form form, String[] tabLabels, int y, int containerWidth, int maxTabs) {
        if (tabLabels == null || tabLabels.length == 0) {
            return new FormTextButton[0];
        }
        int tabCount = Math.min(tabLabels.length, maxTabs);
        int tabSpacing = 8;
        int totalSpacing = tabSpacing * (tabCount - 1);
        int availableWidth = containerWidth - 32 - totalSpacing;
        int tabWidth = Math.max(150, availableWidth / tabCount);
        FormTextButton[] tabs = new FormTextButton[tabCount];
        int currentX = 16;
        for (int i = 0; i < tabCount; ++i) {
            tabs[i] = new FormTextButton(tabLabels[i], currentX, y, tabWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
            form.addComponent((FormComponent)tabs[i]);
            currentX += tabWidth + tabSpacing;
        }
        return tabs;
    }

    public static FormContentIconButton[] createIconButtonRow(FormContentBox parentBox, IconButtonConfig[] buttonConfigs, int startX, int y) {
        if (buttonConfigs == null || buttonConfigs.length == 0) {
            return new FormContentIconButton[0];
        }
        FormContentIconButton[] buttons = new FormContentIconButton[buttonConfigs.length];
        int currentX = startX;
        int buttonSpacing = 24;
        for (int i = 0; i < buttonConfigs.length; ++i) {
            IconButtonConfig config = buttonConfigs[i];
            buttons[i] = new FormContentIconButton(currentX, y, config.size, config.color, config.texture, config.tooltip);
            parentBox.addComponent((FormComponent)buttons[i]);
            currentX += 24 + buttonSpacing;
        }
        return buttons;
    }

    public static FormCheckBox[] createCheckboxGroup(Form form, String[] checkboxLabels, int y, int containerWidth, boolean[] checkStates) {
        if (checkboxLabels == null || checkboxLabels.length == 0) {
            return new FormCheckBox[0];
        }
        int checkboxCount = checkboxLabels.length;
        int availableWidth = containerWidth - 32;
        int checkboxWidth = availableWidth / checkboxCount;
        FormCheckBox[] checkboxes = new FormCheckBox[checkboxCount];
        int currentX = 16;
        for (int i = 0; i < checkboxCount; ++i) {
            boolean isChecked = checkStates != null && i < checkStates.length ? checkStates[i] : false;
            checkboxes[i] = new FormCheckBox(checkboxLabels[i], currentX, y, checkboxWidth, isChecked);
            form.addComponent((FormComponent)checkboxes[i]);
            currentX += checkboxWidth;
        }
        return checkboxes;
    }

    public static FormTextButton createBackButton(Form form, String text, int y, int containerWidth) {
        int buttonWidth = ResponsiveButtonHelper.calculateOptimalWidth(text, 160, 240);
        int buttonX = containerWidth - 16 - buttonWidth;
        FormTextButton backButton = new FormTextButton(text, buttonX, y, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE);
        form.addComponent((FormComponent)backButton);
        return backButton;
    }

    public static class IconButtonConfig {
        public final FormInputSize size;
        public final ButtonColor color;
        public final ButtonTexture texture;
        public final GameMessage[] tooltip;

        public IconButtonConfig(FormInputSize size, ButtonColor color, ButtonTexture texture, GameMessage[] tooltip) {
            this.size = size;
            this.color = color;
            this.texture = texture;
            this.tooltip = tooltip;
        }
    }
}

