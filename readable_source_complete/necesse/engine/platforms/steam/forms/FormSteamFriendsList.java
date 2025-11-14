/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamException
 *  com.codedisaster.steamworks.SteamFriends
 *  com.codedisaster.steamworks.SteamFriends$FriendFlags
 *  com.codedisaster.steamworks.SteamFriends$FriendGameInfo
 *  com.codedisaster.steamworks.SteamFriends$PersonaChange
 *  com.codedisaster.steamworks.SteamFriends$PersonaState
 *  com.codedisaster.steamworks.SteamFriendsCallback
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamNativeHandle
 */
package necesse.engine.platforms.steam.forms;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.HashMap;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormSteamFriendsList
extends FormGeneralList<FriendElement> {
    protected SteamFriends steamFriends;
    protected HashMap<Integer, GameTexture> avatars = new HashMap();

    public FormSteamFriendsList(int x, int y, int width, int height) {
        super(x, y, width, height, 36);
        this.steamFriends = new SteamFriends(new SteamFriendsCallback(){

            public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change) {
                FriendElement friend = FormSteamFriendsList.this.getFriend(steamID);
                if (friend != null) {
                    switch (change) {
                        case Name: {
                            friend.onNameChanged();
                            break;
                        }
                        case Status: {
                            friend.onStatusChanged();
                            break;
                        }
                        case GamePlayed: {
                            friend.onGameChanged();
                        }
                    }
                }
            }

            public void onGameOverlayActivated(boolean active, boolean userInitiated, int appID) {
                System.out.println("Activated overlay: " + active);
            }
        });
        this.reset();
    }

    @Override
    public void reset() {
        super.reset();
        if (this.steamFriends != null) {
            for (int i = 0; i < this.steamFriends.getFriendCount(SteamFriends.FriendFlags.Immediate); ++i) {
                SteamID id = this.steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.Immediate);
                FriendElement friend = new FriendElement(id);
                if (!this.shouldShow(friend)) continue;
                this.elements.add(friend);
            }
            this.sort();
        }
    }

    public boolean shouldShow(FriendElement friend) {
        return true;
    }

    protected void sort() {
        this.elements.sort((f1, f2) -> {
            boolean f2Active;
            boolean f2Offline;
            if (f1.inGame != f2.inGame) {
                if (f1.inGame) {
                    return -1;
                }
                return 1;
            }
            boolean f1Offline = f1.status == SteamFriends.PersonaState.Offline;
            boolean bl = f2Offline = f2.status == SteamFriends.PersonaState.Offline;
            if (f1Offline != f2Offline) {
                if (f1Offline) {
                    return 1;
                }
                return -1;
            }
            boolean f1Active = f1.status == SteamFriends.PersonaState.Online;
            boolean bl2 = f2Active = f2.status == SteamFriends.PersonaState.Online;
            if (f1Active != f2Active) {
                if (f1Active) {
                    return -1;
                }
                return 1;
            }
            return f1.name.compareTo(f2.name);
        });
    }

    protected FriendElement getFriend(SteamID id) {
        return this.elements.stream().filter(e -> SteamNativeHandle.getNativeHandle((SteamNativeHandle)e.steamID) == SteamNativeHandle.getNativeHandle((SteamNativeHandle)id)).findFirst().orElse(null);
    }

    protected abstract void onFriendClicked(FriendElement var1);

    @Override
    public void dispose() {
        super.dispose();
        this.steamFriends.dispose();
        this.avatars.values().forEach(GameTexture::delete);
    }

    protected class FriendElement
    extends FormListElement<FormSteamFriendsList> {
        public final SteamID steamID;
        protected String name;
        protected SteamFriends.PersonaState status;
        protected boolean inGame;
        public final SteamFriends.FriendGameInfo gameInfo;
        private int avatar = -1;

        public FriendElement(SteamID steamID) {
            this.steamID = steamID;
            this.gameInfo = new SteamFriends.FriendGameInfo();
            this.onNameChanged();
            this.onStatusChanged();
            this.onGameChanged();
        }

        public void onNameChanged() {
            this.name = FormSteamFriendsList.this.steamFriends.getFriendPersonaName(this.steamID);
        }

        public void onStatusChanged() {
            this.status = FormSteamFriendsList.this.steamFriends.getFriendPersonaState(this.steamID);
            FormSteamFriendsList.this.sort();
        }

        public void onGameChanged() {
            this.inGame = FormSteamFriendsList.this.steamFriends.getFriendGamePlayed(this.steamID, this.gameInfo);
            FormSteamFriendsList.this.sort();
        }

        public void generateAvatarTexture() {
            if (FormSteamFriendsList.this.avatars.containsKey(this.avatar)) {
                FormSteamFriendsList.this.avatars.get(this.avatar).delete();
            }
            this.avatar = FormSteamFriendsList.this.steamFriends.getSmallFriendAvatar(this.steamID);
            if (this.avatar != 0 && this.avatar != 1) {
                int width = SteamData.getUtils().getImageWidth(this.avatar);
                int height = SteamData.getUtils().getImageHeight(this.avatar);
                try {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
                    SteamData.getUtils().getImageRGBA(this.avatar, buffer);
                    GameTexture texture = new GameTexture("friendAvatar" + this.steamID.toString(), width, height, buffer);
                    texture.makeFinal();
                    FormSteamFriendsList.this.avatars.put(this.avatar, texture);
                }
                catch (SteamException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void draw(FormSteamFriendsList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            String statusText;
            Color statusColor;
            GameTexture avatarTexture;
            if (this.avatar == -1) {
                this.generateAvatarTexture();
            }
            if ((avatarTexture = FormSteamFriendsList.this.avatars.get(this.avatar)) != null) {
                avatarTexture.initDraw().size(32, 32).draw(4, 0);
            } else {
                FormSteamFriendsList.this.getInterfaceStyle().icon_unknown.initDraw().color(FormSteamFriendsList.this.getInterfaceStyle().activeButtonTextColor).draw(16 - FormSteamFriendsList.this.getInterfaceStyle().icon_unknown.getWidth() / 2, 16 - FormSteamFriendsList.this.getInterfaceStyle().icon_unknown.getHeight() / 2);
            }
            FontOptions nameOptions = new FontOptions(20).color(FormSteamFriendsList.this.getInterfaceStyle().highlightTextColor);
            String nameMax = GameUtils.maxString(this.name, nameOptions, FormSteamFriendsList.this.width - 44);
            FontManager.bit.drawString(40.0f, 0.0f, nameMax, nameOptions);
            if (this.inGame) {
                statusColor = FormSteamFriendsList.this.getInterfaceStyle().successTextColor;
                statusText = this.gameInfo.getGameID() == 1169040L ? Localization.translate("ui", "playinggame", "game", "Necesse") : Localization.translate("ui", "playingother");
                if (this.status != SteamFriends.PersonaState.Online) {
                    statusText = statusText + " (" + SteamData.getFriendStatusMessage(this.status).translate() + ")";
                }
            } else {
                statusText = SteamData.getFriendStatusMessage(this.status).translate();
                statusColor = this.status == SteamFriends.PersonaState.Offline ? FormSteamFriendsList.this.getInterfaceStyle().inactiveTextColor : FormSteamFriendsList.this.getInterfaceStyle().activeTextColor;
            }
            FontOptions statusOptions = new FontOptions(16).color(statusColor);
            String statusMax = GameUtils.maxString(statusText, statusOptions, FormSteamFriendsList.this.width - 35);
            FontManager.bit.drawString(40.0f, 20.0f, statusText, statusOptions);
            if (this.isMouseOver(parent)) {
                StringTooltips tooltips = new StringTooltips();
                if (!nameMax.equals(this.name)) {
                    tooltips.add(this.name);
                }
                if (!statusText.equals(statusMax)) {
                    tooltips.add(statusText);
                }
                if (tooltips.getSize() != 0) {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
            }
        }

        @Override
        protected void onClick(FormSteamFriendsList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            parent.onFriendClicked(this);
        }

        @Override
        protected void onControllerEvent(FormSteamFriendsList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            parent.onFriendClicked(this);
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

