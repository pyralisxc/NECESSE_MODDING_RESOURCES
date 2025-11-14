/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamPublishedFileID
 *  com.codedisaster.steamworks.SteamUGC
 *  com.codedisaster.steamworks.SteamUGC$ItemInstallInfo
 *  com.codedisaster.steamworks.SteamUGCCallback
 */
package necesse.engine.platforms.steam.modding;

import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.platforms.steam.modding.LoadedSteamMod;
import necesse.engine.platforms.steam.modding.SteamModLoadLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HoverStateTextures;

public class SteamModProvider
extends ModProvider {
    private final GameMessage gameMessage = new LocalMessage("ui", "modfromworkshop");

    @Override
    public List<ModLoadLocation> locateMods(boolean isServer) throws ModLoadException {
        ArrayList<ModLoadLocation> modLoadLocations = new ArrayList<ModLoadLocation>();
        if (!isServer) {
            SteamUGC steamUGC = new SteamUGC(new SteamUGCCallback(){});
            int subscribedItems = steamUGC.getNumSubscribedItems();
            if (subscribedItems > 0) {
                System.out.println("Found " + subscribedItems + " subscribed items from Steam Workshop");
                SteamPublishedFileID[] fileIDs = new SteamPublishedFileID[subscribedItems];
                steamUGC.getSubscribedItems(fileIDs);
                for (SteamPublishedFileID fileID : fileIDs) {
                    SteamUGC.ItemInstallInfo info = new SteamUGC.ItemInstallInfo();
                    if (steamUGC.getItemInstallInfo(fileID, info)) {
                        File modPath = new File(info.getFolder());
                        if (!modPath.isDirectory()) continue;
                        File[] files = modPath.listFiles();
                        if (files == null) {
                            GameLog.warn.println("Could not list files in mod directory at " + modPath.getAbsolutePath());
                            continue;
                        }
                        File loaded = null;
                        for (File file : files) {
                            if (!file.getName().endsWith(".jar")) continue;
                            if (loaded != null) {
                                GameLog.warn.println("Already loaded one mod " + loaded.getName() + " at " + modPath.getAbsolutePath());
                                continue;
                            }
                            loaded = file;
                            modLoadLocations.add(new SteamModLoadLocation(this, file, fileID));
                        }
                        continue;
                    }
                    System.out.println("Subscribed mod " + SteamNativeHandle.getNativeHandle((SteamNativeHandle)fileID) + " is not installed yet");
                }
            }
            steamUGC.dispose();
        }
        return modLoadLocations;
    }

    @Override
    public HoverStateTextures getIcon() {
        return Settings.UI.steam_icon;
    }

    @Override
    public GameMessage getGameMessage() {
        return this.gameMessage;
    }

    @Override
    protected LoadedMod createLoadedMod(JarFile jarFile, ModInfoFile modInfoFile, ModLoadLocation loadLocation) {
        return new LoadedSteamMod(jarFile, modInfoFile, (SteamModLoadLocation)loadLocation);
    }

    @Override
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
        infoContentBox.addComponent(flow.nextY(new FormLocalTextButton(new LocalMessage("ui", "modopenworkshop"), 5, 10, Math.min(350, infoContentBox.getMinContentWidth() - 10), FormInputSize.SIZE_24, ButtonColor.BASE), 5)).onClicked(e -> SteamData.activateGameOverlayToWebPage("steam://url/CommunityFilePage/" + SteamNativeHandle.getNativeHandle((SteamNativeHandle)((LoadedSteamMod)mod).workshopFileID)));
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
}

