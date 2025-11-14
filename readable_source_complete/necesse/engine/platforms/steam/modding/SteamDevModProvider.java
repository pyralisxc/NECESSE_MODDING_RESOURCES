/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamAPICall
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamPublishedFileID
 *  com.codedisaster.steamworks.SteamRemoteStorage$WorkshopFileType
 *  com.codedisaster.steamworks.SteamResult
 *  com.codedisaster.steamworks.SteamUGC
 *  com.codedisaster.steamworks.SteamUGC$ItemUpdateInfo
 *  com.codedisaster.steamworks.SteamUGC$ItemUpdateStatus
 *  com.codedisaster.steamworks.SteamUGC$MatchingUGCType
 *  com.codedisaster.steamworks.SteamUGC$UserUGCList
 *  com.codedisaster.steamworks.SteamUGC$UserUGCListSortOrder
 *  com.codedisaster.steamworks.SteamUGCCallback
 *  com.codedisaster.steamworks.SteamUGCDetails
 *  com.codedisaster.steamworks.SteamUGCQuery
 *  com.codedisaster.steamworks.SteamUGCUpdateHandle
 */
package necesse.engine.platforms.steam.modding;

import com.codedisaster.steamworks.SteamAPICall;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamRemoteStorage;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;
import com.codedisaster.steamworks.SteamUGCUpdateHandle;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.DevModProvider;
import necesse.engine.modLoader.LoadedDevMod;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class SteamDevModProvider
extends DevModProvider {
    private SteamUGC steamUGC;
    private NoticeForm updateProgressForm;
    private SteamUGCQuery publishedQuery;
    private int publishedQueryPage;
    private final ArrayList<SteamUGCDetails> publishedItemsDetails = new ArrayList();
    private SteamUGCUpdateHandle updateHandle;
    private Consumer<SteamUGCUpdateHandle> createItemUpdateHandleConsumer;
    private long nextUpdateCheck;
    private FormSwitcher currentParentFormSwitcher;
    private FormComponent currentParentMain;
    private LoadedDevMod currentMod;

    @Override
    public void initialize() {
        this.steamUGC = new SteamUGC(new SteamUGCCallback(){

            public void onUGCQueryCompleted(SteamUGCQuery steamUGCQuery, int returnedResults, int totalResults, boolean cached, SteamResult steamResult) {
                if (steamResult == SteamResult.OK) {
                    if (SteamDevModProvider.this.publishedQuery != null && SteamDevModProvider.this.publishedQuery.equals((Object)steamUGCQuery)) {
                        for (int i = 0; i < returnedResults; ++i) {
                            SteamUGCDetails details = new SteamUGCDetails();
                            if (!SteamDevModProvider.this.steamUGC.getQueryUGCResult(steamUGCQuery, i, details)) continue;
                            SteamDevModProvider.this.publishedItemsDetails.add(details);
                        }
                        int totalReturnedResults = (SteamDevModProvider.this.publishedQueryPage - 1) * 50 + totalResults;
                        if (totalResults > totalReturnedResults) {
                            SteamDevModProvider.this.publishedQueryPage++;
                            System.out.println("Requesting public query page " + SteamDevModProvider.this.publishedQueryPage);
                            SteamDevModProvider.this.publishedQuery = SteamDevModProvider.this.steamUGC.createQueryUserUGCRequest(SteamData.getSteamID().getAccountID(), SteamUGC.UserUGCList.Published, SteamUGC.MatchingUGCType.ItemsReadyToUse, SteamUGC.UserUGCListSortOrder.LastUpdatedDesc, 1169040, 1169040, SteamDevModProvider.this.publishedQueryPage);
                            SteamDevModProvider.this.steamUGC.setReturnLongDescription(SteamDevModProvider.this.publishedQuery, true);
                            SteamDevModProvider.this.steamUGC.sendQueryUGCRequest(SteamDevModProvider.this.publishedQuery);
                        } else {
                            System.out.println("Done requesting published query");
                            SteamDevModProvider.this.startUploadSelection();
                        }
                    }
                } else {
                    NoticeForm loadingError = SteamDevModProvider.this.currentParentFormSwitcher.addComponent(new NoticeForm("requesterror"), (noticeForm, isActive) -> {
                        if (!isActive.booleanValue()) {
                            SteamDevModProvider.this.currentParentFormSwitcher.removeComponent(noticeForm);
                        }
                    });
                    loadingError.setupNotice(new StaticMessage("Error loading: " + steamResult.name()));
                    loadingError.onContinue(() -> SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(SteamDevModProvider.this.currentParentMain));
                    loadingError.setButtonCooldown(2000);
                    SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(loadingError);
                }
                SteamDevModProvider.this.steamUGC.releaseQueryUserUGCRequest(steamUGCQuery);
            }

            public void onRequestUGCDetails(SteamUGCDetails steamUGCDetails, SteamResult steamResult) {
                System.out.println("onRequestUGCDetails steamUGCDetails = " + steamUGCDetails + ", steamResult = " + steamResult);
            }

            public void onCreateItem(SteamPublishedFileID steamPublishedFileID, boolean needToAcceptEULA, SteamResult steamResult) {
                if (needToAcceptEULA || steamResult != SteamResult.OK) {
                    NoticeForm notice = SteamDevModProvider.this.currentParentFormSwitcher.addComponent(new NoticeForm("modcreatefailed"));
                    if (needToAcceptEULA) {
                        notice.setupNotice(new LocalMessage("ui", "moduploadnotaccepted"));
                    } else {
                        notice.setupNotice(new LocalMessage("ui", "moduploadcreatefailed", "message", steamResult.toString()));
                    }
                    notice.onContinue(() -> {
                        SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(SteamDevModProvider.this.currentParentMain);
                        SteamDevModProvider.this.currentParentFormSwitcher.removeComponent(notice);
                    });
                    SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(notice);
                    if (SteamDevModProvider.this.updateProgressForm != null) {
                        SteamDevModProvider.this.updateProgressForm.applyContinue();
                        SteamDevModProvider.this.updateProgressForm = null;
                        SteamDevModProvider.this.updateHandle = null;
                    }
                } else {
                    System.out.println("Created item with file handle: " + SteamNativeHandle.getNativeHandle((SteamNativeHandle)steamPublishedFileID));
                    SteamDevModProvider.this.setUpdateProgress(new LocalMessage("ui", "moduploadupdating"));
                    SteamDevModProvider.this.updateHandle = SteamDevModProvider.this.steamUGC.startItemUpdate(1169040, steamPublishedFileID);
                    System.out.println("Started item update with update handle: " + SteamDevModProvider.this.updateHandle);
                    SteamDevModProvider.this.createItemUpdateHandleConsumer.accept(SteamDevModProvider.this.updateHandle);
                    SteamAPICall steamAPICall = SteamDevModProvider.this.steamUGC.submitItemUpdate(SteamDevModProvider.this.updateHandle, "Mod version " + ((SteamDevModProvider)SteamDevModProvider.this).currentMod.version + " for game version " + ((SteamDevModProvider)SteamDevModProvider.this).currentMod.gameVersion);
                    System.out.println("Updating item with call handle: " + steamAPICall);
                }
            }

            public void onSubmitItemUpdate(SteamPublishedFileID steamPublishedFileID, boolean needToAcceptEULA, SteamResult steamResult) {
                if (needToAcceptEULA || steamResult != SteamResult.OK) {
                    NoticeForm notice = SteamDevModProvider.this.currentParentFormSwitcher.addComponent(new NoticeForm("modcreatefailed"));
                    if (needToAcceptEULA) {
                        notice.setupNotice(new LocalMessage("ui", "moduploadnotaccepted"));
                    } else {
                        notice.setupNotice(new LocalMessage("ui", "moduploadcreatefailed", "message", steamResult.toString()));
                    }
                    notice.onContinue(() -> {
                        SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(SteamDevModProvider.this.currentParentMain);
                        SteamDevModProvider.this.currentParentFormSwitcher.removeComponent(notice);
                    });
                    SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(notice);
                } else {
                    NoticeForm notice = SteamDevModProvider.this.currentParentFormSwitcher.addComponent(new NoticeForm("modupdatesuccess"));
                    notice.setupNotice(new LocalMessage("ui", "moduploadsuccess"));
                    notice.onContinue(() -> {
                        SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(SteamDevModProvider.this.currentParentMain);
                        SteamDevModProvider.this.currentParentFormSwitcher.removeComponent(notice);
                        SteamData.activateGameOverlayToWebPage("steam://url/CommunityFilePage/" + SteamNativeHandle.getNativeHandle((SteamNativeHandle)steamPublishedFileID));
                    });
                    SteamDevModProvider.this.currentParentFormSwitcher.makeCurrent(notice);
                }
                if (SteamDevModProvider.this.updateProgressForm != null) {
                    SteamDevModProvider.this.updateProgressForm.applyContinue();
                    SteamDevModProvider.this.updateProgressForm = null;
                    SteamDevModProvider.this.updateHandle = null;
                }
            }
        });
    }

    @Override
    public void dispose() {
        if (this.steamUGC != null) {
            this.steamUGC.dispose();
        }
    }

    @Override
    public void provideModInfoContent(FormContentBox infoContentBox, LoadedMod mod, boolean[] dependsMet, boolean[] optionalDependsMet, FormSwitcher parentForm, ContinueComponentManager continueComponentManager) {
        int i;
        this.currentMod = (LoadedDevMod)mod;
        this.currentParentFormSwitcher = parentForm;
        this.currentParentMain = parentForm.getCurrent();
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
        if (mod.hasLoaded() && mod.preview != null) {
            FormLocalTextButton uploadButton = infoContentBox.addComponent(flow.nextY(new FormLocalTextButton("ui", "modupload", 5, 10, Math.min(350, infoContentBox.getMinContentWidth() - 10), FormInputSize.SIZE_24, ButtonColor.BASE), 5));
            if (mod.hasExampleModPackageClasses()) {
                uploadButton.setActive(false);
                uploadButton.setLocalTooltip("ui", "moduploadexamplepackage");
            }
            uploadButton.onClicked(e -> {
                NoticeForm loadingNotice = parentForm.addComponent(new NoticeForm("loadpublished"), (noticeForm, isActive) -> {
                    if (!isActive.booleanValue()) {
                        parentForm.removeComponent(noticeForm);
                    }
                });
                loadingNotice.setupNotice(new LocalMessage("ui", "loadingdotdot"), (GameMessage)new LocalMessage("ui", "cancelbutton"));
                loadingNotice.onContinue(() -> {
                    this.publishedQuery = null;
                    parentForm.makeCurrent(this.currentParentMain);
                });
                loadingNotice.setButtonCooldown(5000);
                parentForm.makeCurrent(loadingNotice);
                System.out.println("Requesting public query first page");
                this.publishedQueryPage = 1;
                this.publishedItemsDetails.clear();
                this.publishedQuery = this.steamUGC.createQueryUserUGCRequest(SteamData.getSteamID().getAccountID(), SteamUGC.UserUGCList.Published, SteamUGC.MatchingUGCType.ItemsReadyToUse, SteamUGC.UserUGCListSortOrder.LastUpdatedDesc, 1169040, 1169040, this.publishedQueryPage);
                this.steamUGC.setReturnLongDescription(this.publishedQuery, true);
                this.steamUGC.sendQueryUGCRequest(this.publishedQuery);
            });
        }
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

    protected void onUpdateProgress(SteamUGC.ItemUpdateStatus status, SteamUGC.ItemUpdateInfo info) {
        if (status != SteamUGC.ItemUpdateStatus.Invalid) {
            double percent = (double)info.getBytesProcessed() * 100.0 / (double)info.getBytesTotal();
            GameMessageBuilder message = new GameMessageBuilder().append(status.name()).append("... " + (int)percent + "%");
            this.updateProgressForm.setupNotice(message);
        }
    }

    protected void setUpdateProgress(GameMessage message) {
        if (this.updateProgressForm != null) {
            this.updateProgressForm.applyContinue();
        }
        this.updateProgressForm = new NoticeForm("updateprogress", 400, 400){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                if (SteamDevModProvider.this.updateHandle != null && System.currentTimeMillis() >= SteamDevModProvider.this.nextUpdateCheck) {
                    SteamUGC.ItemUpdateInfo info = new SteamUGC.ItemUpdateInfo();
                    SteamUGC.ItemUpdateStatus status = SteamDevModProvider.this.steamUGC.getItemUpdateProgress(SteamDevModProvider.this.updateHandle, info);
                    SteamDevModProvider.this.onUpdateProgress(status, info);
                    SteamDevModProvider.this.nextUpdateCheck = System.currentTimeMillis() + 250L;
                }
                super.draw(tickManager, perspective, renderBox);
            }
        };
        this.updateProgressForm.setButtonCooldown(-2);
        this.updateProgressForm.setupNotice(message);
        MainMenu mainMenu = (MainMenu)GlobalData.getCurrentState();
        mainMenu.addContinueForm("modupdateprogress", this.updateProgressForm);
    }

    protected void startUploadSelection() {
        Form uploadForm = this.currentParentFormSwitcher.addComponent(new Form("selectuploadtype", 500, 400));
        FormFlow flow = new FormFlow(5);
        uploadForm.addComponent(flow.nextY(new FormLocalLabel("ui", "moduploadselect", new FontOptions(16), 0, uploadForm.getWidth() / 2, 10, uploadForm.getWidth() - 20), 10));
        FormDropdownSelectionButton<Integer> selectionButton = uploadForm.addComponent(new FormDropdownSelectionButton(10, flow.next(30), FormInputSize.SIZE_24, ButtonColor.BASE, uploadForm.getWidth() - 20));
        selectionButton.setSelected(-1, new LocalMessage("ui", "moduploadcreatenew"));
        selectionButton.options.add(-1, new LocalMessage("ui", "moduploadcreatenew"));
        for (int i = 0; i < this.publishedItemsDetails.size(); ++i) {
            SteamUGCDetails details = this.publishedItemsDetails.get(i);
            selectionButton.options.add(i, new StaticMessage(details.getTitle() + " (" + SteamNativeHandle.getNativeHandle((SteamNativeHandle)details.getPublishedFileID()) + ")"));
        }
        flow.next(5);
        FormLocalCheckBox updateDesc = uploadForm.addComponent(flow.nextY(new FormLocalCheckBox((GameMessage)new LocalMessage("ui", "moduploadupdatedesc"), 5, 10, true), 5));
        updateDesc.handleClicksIfNoEventHandlers = true;
        int descContentHeight = 200;
        FormContentBox descContent = uploadForm.addComponent(new FormContentBox(4, flow.next(descContentHeight) + 4, uploadForm.getWidth() - 8, descContentHeight - 8, GameBackground.textBox));
        FormTextBox descTextBox = descContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, descContent.getMinContentWidth(), 100, 5000));
        descTextBox.allowTyping = true;
        descTextBox.setEmptyTextSpace(new Rectangle(descContent.getX(), descContent.getY(), descContent.getWidth(), descContent.getHeight()));
        descTextBox.setText(this.currentMod.description);
        descTextBox.onChange(e -> {
            Rectangle box = descContent.getContentBoxToFitComponents();
            descContent.setContentBox(box);
            descContent.scrollToFit(descTextBox.getCaretBoundingBox());
        });
        descTextBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                descContent.scrollToFit(descTextBox.getCaretBoundingBox());
            }
        });
        flow.next(5);
        uploadForm.addComponent(flow.nextY(new FormLocalLabel("ui", "moduploadselecttags", new FontOptions(16), -1, 5, 10, uploadForm.getWidth() - 20), 10));
        String[] tags = new String[]{"Translation", "Texture pack", "Interface", "New features", "New content", "Tweaks", "Miscellaneous"};
        ArrayList<String> decidedTags = new ArrayList<String>();
        if (this.currentMod.clientside) {
            decidedTags.add("Client mod");
        }
        HashMap<String, FormCheckBox> tagCheckboxes = new HashMap<String, FormCheckBox>();
        for (String tag : tags) {
            FormCheckBox checkBox = uploadForm.addComponent(flow.nextY(new FormCheckBox(tag, 5, 10, uploadForm.getWidth() - 10), 5));
            checkBox.handleClicksIfNoEventHandlers = true;
            tagCheckboxes.put(tag, checkBox);
        }
        selectionButton.onSelected(e -> {
            tagCheckboxes.values().forEach(cb -> {
                cb.checked = false;
            });
            int selectedIndex = (Integer)e.value;
            if (selectedIndex != -1) {
                SteamUGCDetails details = this.publishedItemsDetails.get(selectedIndex);
                descTextBox.setText(details.getDescription());
                Object[] detailTags = details.getTags().split(",");
                System.out.println("Selected mod with tags " + Arrays.toString(detailTags));
                for (Object tag : detailTags) {
                    FormCheckBox checkbox = (FormCheckBox)tagCheckboxes.get(tag);
                    if (checkbox == null) continue;
                    checkbox.checked = true;
                }
            } else {
                descTextBox.setText(this.currentMod.description);
            }
        });
        int buttonsY = flow.next(40);
        uploadForm.addComponent(new FormLocalTextButton("ui", "continuebutton", 4, buttonsY, uploadForm.getWidth() / 2 - 6)).onClicked(e -> {
            int selectedIndex = (Integer)selectionButton.getSelected();
            if (selectedIndex == -1) {
                this.startUploadConfirm(() -> {
                    if (this.currentMod instanceof LoadedDevMod && this.currentMod.validateDevFolder()) {
                        LoadedDevMod currentInfoDevMod = this.currentMod;
                        File previewFile = currentInfoDevMod.preview.saveTextureImage(currentInfoDevMod.devModFolder.getAbsolutePath() + "/preview.png");
                        this.createItemUpdateHandleConsumer = handle -> {
                            this.steamUGC.setItemTitle(handle, currentInfoDevMod.name);
                            if (updateDesc.checked) {
                                this.steamUGC.setItemDescription(handle, descTextBox.getText());
                            }
                            System.out.println("Setting mod content to " + currentInfoDevMod.devModFolder.getAbsolutePath());
                            this.steamUGC.setItemContent(handle, currentInfoDevMod.devModFolder.getAbsolutePath());
                            System.out.println("Setting mod preview to " + previewFile.getAbsolutePath());
                            this.steamUGC.setItemPreview(handle, previewFile.getAbsolutePath());
                            Object[] tagsArray = (String[])Stream.concat(tagCheckboxes.entrySet().stream().filter(entry -> ((FormCheckBox)entry.getValue()).checked).map(Map.Entry::getKey), decidedTags.stream()).toArray(String[]::new);
                            System.out.println("Setting mod tags to " + Arrays.toString(tagsArray));
                            this.steamUGC.setItemTags(handle, (String[])tagsArray);
                        };
                        this.setUpdateProgress(new LocalMessage("ui", "moduploadcreating"));
                        this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
                        SteamAPICall item = this.steamUGC.createItem(1169040, SteamRemoteStorage.WorkshopFileType.Community);
                        System.out.println("Creating community file with call handle " + SteamNativeHandle.getNativeHandle((SteamNativeHandle)item));
                    } else {
                        NoticeForm notice = this.currentParentFormSwitcher.addComponent(new NoticeForm("invalidmodfolder"));
                        notice.setupNotice(new LocalMessage("ui", "moduploadfolderinvalid"));
                        notice.onContinue(() -> {
                            this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
                            this.currentParentFormSwitcher.removeComponent(notice);
                        });
                        this.currentParentFormSwitcher.makeCurrent(notice);
                    }
                });
            } else {
                this.startUploadConfirm(() -> {
                    if (this.currentMod instanceof LoadedDevMod && this.currentMod.validateDevFolder()) {
                        LoadedDevMod currentInfoDevMod = this.currentMod;
                        SteamUGCDetails details = this.publishedItemsDetails.get(selectedIndex);
                        this.setUpdateProgress(new LocalMessage("ui", "moduploadupdating"));
                        this.updateHandle = this.steamUGC.startItemUpdate(1169040, details.getPublishedFileID());
                        System.out.println("Started item update with update handle: " + this.updateHandle);
                        if (updateDesc.checked) {
                            this.steamUGC.setItemDescription(this.updateHandle, descTextBox.getText());
                        }
                        System.out.println("Setting mod content to " + currentInfoDevMod.devModFolder.getAbsolutePath());
                        this.steamUGC.setItemContent(this.updateHandle, currentInfoDevMod.devModFolder.getAbsolutePath());
                        File previewFile = currentInfoDevMod.preview.saveTextureImage(currentInfoDevMod.devModFolder.getAbsolutePath() + "/preview.png");
                        System.out.println("Setting mod preview to " + previewFile.getAbsolutePath());
                        this.steamUGC.setItemPreview(this.updateHandle, previewFile.getAbsolutePath());
                        Object[] tagsArray = (String[])Stream.concat(tagCheckboxes.entrySet().stream().filter(entry -> ((FormCheckBox)entry.getValue()).checked).map(Map.Entry::getKey), decidedTags.stream()).toArray(String[]::new);
                        System.out.println("Setting mod tags to " + Arrays.toString(tagsArray));
                        this.steamUGC.setItemTags(this.updateHandle, (String[])tagsArray);
                        SteamAPICall steamAPICall = this.steamUGC.submitItemUpdate(this.updateHandle, "Mod version " + currentInfoDevMod.version + " for game version " + this.currentMod.gameVersion);
                        System.out.println("Updating item with call handle: " + steamAPICall);
                    } else {
                        NoticeForm notice = this.currentParentFormSwitcher.addComponent(new NoticeForm("invalidmodfolder"));
                        notice.setupNotice(new LocalMessage("ui", "moduploadfolderinvalid"));
                        notice.onContinue(() -> {
                            this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
                            this.currentParentFormSwitcher.removeComponent(notice);
                        });
                        this.currentParentFormSwitcher.makeCurrent(notice);
                    }
                });
            }
            this.currentParentFormSwitcher.removeComponent(uploadForm);
        });
        uploadForm.addComponent(new FormLocalTextButton("ui", "backbutton", uploadForm.getWidth() / 2 + 2, buttonsY, uploadForm.getWidth() / 2 - 6)).onClicked(e -> {
            this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
            this.currentParentFormSwitcher.removeComponent(uploadForm);
        });
        this.currentParentFormSwitcher.makeCurrent(uploadForm);
        uploadForm.setHeight(flow.next());
        uploadForm.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
    }

    protected void startUploadConfirm(Runnable whenConfirmed) {
        ConfirmationForm confirmAgreement = this.currentParentFormSwitcher.addComponent(new ConfirmationForm("confirmagreement", 400, 600));
        confirmAgreement.setupConfirmation(content -> {
            FormFlow confirmFlow = new FormFlow(10);
            String url = "https://steamcommunity.com/sharedfiles/workshoplegalagreement";
            GameMessageBuilder msg = new GameMessageBuilder().append("ui", "moduploadtermsagree").append("\n\n" + url);
            content.addComponent(confirmFlow.nextY(new FormLocalLabel(msg, new FontOptions(20), 0, content.getWidth() / 2, 10, content.getWidth() - 20), 10));
            content.addComponent(confirmFlow.nextY(new FormLocalTextButton(new LocalMessage("ui", "moduploadtermsopen"), new LocalMessage("misc", "openurl", "url", url), 20, 10, content.getWidth() - 40, FormInputSize.SIZE_24, ButtonColor.BASE), 10)).onClicked(openEvent -> GameUtils.openURL(url));
        }, (GameMessage)new LocalMessage("ui", "continuebutton"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> {
            ConfirmationForm confirmMine = this.currentParentFormSwitcher.addComponent(new ConfirmationForm("confirmmine", 400, 600));
            confirmMine.setupConfirmation(new LocalMessage("ui", "moduploadmine"), () -> {
                this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
                this.currentParentFormSwitcher.removeComponent(confirmMine);
                whenConfirmed.run();
            }, () -> {
                this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
                this.currentParentFormSwitcher.removeComponent(confirmMine);
            });
            confirmMine.startConfirmCooldown(5000, true);
            this.currentParentFormSwitcher.makeCurrent(confirmMine);
            this.currentParentFormSwitcher.removeComponent(confirmAgreement);
        }, () -> {
            this.currentParentFormSwitcher.makeCurrent(this.currentParentMain);
            this.currentParentFormSwitcher.removeComponent(confirmAgreement);
        });
        confirmAgreement.startConfirmCooldown(5000, true);
        this.currentParentFormSwitcher.makeCurrent(confirmAgreement);
    }
}

