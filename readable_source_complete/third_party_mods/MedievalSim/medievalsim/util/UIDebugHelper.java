/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.util;

import medievalsim.util.ResponsiveButtonHelper;

public class UIDebugHelper {
    public static void runTextFitTests() {
        int[] containerWidths;
        System.out.println("=== UI TEXT FIT ANALYSIS ===");
        String[] testStrings = new String[]{"Build Tools", "Zone Tools", "Command Center", "Protected Zones", "PvP Zones", "Create Protected Zone", "Create PvP Zone", "Back to Menu", "Back", "Configure", "Delete Zone", "Expand Zone"};
        for (int containerWidth : containerWidths = new int[]{268, 300, 360, 400, 500, 600}) {
            System.out.println("\n--- Container Width: " + containerWidth + "px ---");
            for (String text : testStrings) {
                int optimalWidth = ResponsiveButtonHelper.calculateOptimalWidth(text, 160, containerWidth);
                boolean fits = ResponsiveButtonHelper.doesTextFit(text, containerWidth);
                String status = fits ? "\u2713 FITS" : "\u2717 TRUNCATED";
                System.out.println(String.format("  %-20s : %3dpx optimal | %s", text, optimalWidth, status));
            }
        }
        System.out.println("\n=== RECOMMENDATIONS ===");
        System.out.println("\u2022 Main menu (300px container): Consider increasing to 360px+ for longer text");
        System.out.println("\u2022 Zone forms (400px container): Should handle most text well");
        System.out.println("\u2022 Zone lists (600px container): Excellent for all text lengths");
        System.out.println("\u2022 Minimum button width increased to 160px for better readability");
    }

    public static void testSpecificText(String text, int containerWidth) {
        System.out.println("=== TESTING SPECIFIC TEXT ===");
        System.out.println("Text: '" + text + "'");
        System.out.println("Container: " + containerWidth + "px");
        boolean fits = ResponsiveButtonHelper.doesTextFit(text, containerWidth);
        int optimalWidth = ResponsiveButtonHelper.calculateOptimalWidth(text, 160, containerWidth);
        System.out.println("Optimal width: " + optimalWidth + "px");
        System.out.println("Result: " + (fits ? "\u2713 FITS PERFECTLY" : "\u2717 WILL BE TRUNCATED"));
        if (!fits) {
            int recommendedContainerWidth = optimalWidth + 32;
            System.out.println("Recommended container width: " + recommendedContainerWidth + "px");
        }
    }
}

