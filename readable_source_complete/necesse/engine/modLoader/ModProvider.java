/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.jar.JarFile;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.modLoader.ModInfoNotFoundException;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.platforms.Platform;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HoverStateTextures;

public abstract class ModProvider {
    public void initialize() {
    }

    public void dispose() {
    }

    public abstract List<ModLoadLocation> locateMods(boolean var1) throws ModLoadException;

    public abstract HoverStateTextures getIcon();

    public abstract GameMessage getGameMessage();

    public int getLoadOrder() {
        return Platform.getModProviders().indexOf(this);
    }

    public LoadedMod loadMod(ModLoadLocation loadLocation) {
        File path = loadLocation.path;
        try {
            if (!path.exists()) {
                GameLog.warn.println("Could not find mod jar located at " + path.getAbsolutePath());
                return null;
            }
            if (!path.getName().endsWith(".jar")) {
                GameLog.warn.println("Invalid mod jar located at " + path.getAbsolutePath());
                return null;
            }
            JarFile jarFile = new JarFile(path);
            try {
                ModInfoFile modInfoFile = new ModInfoFile(jarFile);
                return this.createLoadedMod(jarFile, modInfoFile, loadLocation);
            }
            catch (ModInfoNotFoundException e) {
                ModProvider.logLoadError(path.getName() + " did not contain a " + "mod.info" + " file.");
            }
        }
        catch (MalformedURLException e) {
            ModProvider.logLoadError("Could not load mod " + path.getName() + " url");
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            ModProvider.logLoadError("Could not load mod " + path.getName() + " file");
            System.err.println(e.getMessage());
        }
        catch (LinkageError e) {
            ModProvider.logLoadError("Linkage error for " + path.getName() + " mod");
            e.printStackTrace();
        }
        catch (Exception e) {
            ModProvider.logLoadError("Unknown error loading " + path.getName() + " mod");
            e.printStackTrace();
        }
        return null;
    }

    protected abstract LoadedMod createLoadedMod(JarFile var1, ModInfoFile var2, ModLoadLocation var3);

    public void provideModInfoContent(FormContentBox infoContentBox, LoadedMod mod, boolean[] dependsMet, boolean[] optionalDependsMet, FormSwitcher parentForm, ContinueComponentManager continueComponentManager) {
        int i;
        FormFlow flow = new FormFlow(5);
        if (mod.preview != null) {
            final TextureDrawOptionsStart options = mod.preview.initDraw();
            options.shrinkHeight(128, false);
            if (options.getWidth() > infoContentBox.getWidth() - 20) {
                options.shrinkWidth(infoContentBox.getWidth() - 20, false);
            }
            int width = options.getWidth();
            int height = options.getHeight();
            infoContentBox.addComponent(new FormCustomDraw(infoContentBox.getMinContentWidth() / 2 - width / 2, flow.next(height + 5), width, height){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    options.draw(this.getX(), this.getY());
                }
            });
        }
        infoContentBox.addComponent(flow.nextY(new FormLabel(mod.name, new FontOptions(20), -1, 5, infoContentBox.getWidth() / 2, infoContentBox.getMinContentWidth() - 10), 5));
        this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modinfoid", "id", mod.id), new FontOptions(12));
        this.addInfoContent(infoContentBox, flow, mod.loadLocation.modProvider.getGameMessage().translate(), new FontOptions(12));
        this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modinfoversion", "version", mod.version));
        Color gameVersionColor = mod.gameVersion.equals("1.0.1") ? Settings.UI.activeTextColor : Settings.UI.errorTextColor;
        this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modinfogameversion", "version", GameColor.getCustomColorCode(gameVersionColor) + mod.gameVersion));
        if (mod.clientside) {
            this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modclientside"), new FontOptions(12));
        }
        if (mod.depends.length > 0) {
            this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modinfodep"));
            for (i = 0; i < mod.depends.length; ++i) {
                Color col = dependsMet[i] ? Settings.UI.activeTextColor : Settings.UI.errorTextColor;
                this.addInfoContent(infoContentBox, flow, 20, GameColor.getCustomColorCode(col) + ModLoader.getModName(mod.depends[i]), new FontOptions(12));
            }
        }
        if (mod.optionalDepends.length > 0) {
            this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modinfooptdep"));
            for (i = 0; i < mod.optionalDepends.length; ++i) {
                Color col = optionalDependsMet[i] ? Settings.UI.activeTextColor : Settings.UI.warningTextColor;
                this.addInfoContent(infoContentBox, flow, 20, GameColor.getCustomColorCode(col) + ModLoader.getModName(mod.optionalDepends[i]), new FontOptions(12));
            }
        }
        this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "modauthor", "author", mod.author));
        this.addInfoContent(infoContentBox, flow, Localization.translate("ui", "moddescription", "description", mod.description));
        for (String key : mod.modInfo.keySet()) {
            this.addInfoContent(infoContentBox, flow, key + ": " + mod.modInfo.get(key));
        }
        infoContentBox.setContentBox(new Rectangle(infoContentBox.getWidth(), flow.next()));
        infoContentBox.setScrollY(0);
    }

    private void addInfoContent(FormContentBox infoContentBox, FormFlow flow, int x, String text, FontOptions fontOptions) {
        infoContentBox.addComponent(flow.nextY(new FormFairTypeLabel(text, x, 5).setFontOptions(fontOptions).setMaxWidth(infoContentBox.getMinContentWidth() - 5 - x), 5));
    }

    private void addInfoContent(FormContentBox infoContentBox, FormFlow flow, String text, FontOptions fontOptions) {
        this.addInfoContent(infoContentBox, flow, 5, text, fontOptions);
    }

    private void addInfoContent(FormContentBox infoContentBox, FormFlow flow, String text) {
        this.addInfoContent(infoContentBox, flow, text, new FontOptions(16));
    }

    protected static void logLoadError(String error) {
        GameLoadingScreen.addLog(error);
        System.err.println(error);
    }
}

