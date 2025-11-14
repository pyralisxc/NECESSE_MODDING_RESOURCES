/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamAPI
 *  com.codedisaster.steamworks.SteamApps
 *  com.codedisaster.steamworks.SteamFriends
 *  com.codedisaster.steamworks.SteamFriends$FriendFlags
 *  com.codedisaster.steamworks.SteamFriends$FriendRelationship
 *  com.codedisaster.steamworks.SteamFriends$OverlayToWebPageMode
 *  com.codedisaster.steamworks.SteamFriends$PersonaState
 *  com.codedisaster.steamworks.SteamFriendsCallback
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamMatchmaking
 *  com.codedisaster.steamworks.SteamMatchmaking$ChatRoomEnterResponse
 *  com.codedisaster.steamworks.SteamMatchmakingCallback
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamUser
 *  com.codedisaster.steamworks.SteamUserCallback
 *  com.codedisaster.steamworks.SteamUtils
 *  com.codedisaster.steamworks.SteamUtils$FloatingGamepadTextInputMode
 *  com.codedisaster.steamworks.SteamUtils$GamepadTextInputMode
 *  com.codedisaster.steamworks.SteamUtils$GamepadTextLineMode
 *  com.codedisaster.steamworks.SteamUtilsCallback
 */
package necesse.engine.platforms.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUserCallback;
import com.codedisaster.steamworks.SteamUtils;
import com.codedisaster.steamworks.SteamUtilsCallback;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.steam.SteamFriend;
import necesse.engine.platforms.steam.network.SteamNetworkManager;
import necesse.engine.state.MainMenu;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.presets.ConfirmationContinueForm;

public class SteamData {
    private static boolean initialized = false;
    private static boolean created = false;
    private static SteamApps apps;
    private static SteamUser user;
    private static SteamFriends friends;
    private static SteamMatchmaking matchmaking;
    private static SteamUtils utils;
    private static boolean overlayActive;
    private static SteamID connectLobby;
    private static boolean joinedLobby;
    private static boolean inLobby;
    private static long connectLobbyTimeout;
    private static long lastCallbackError;

    public static void init() {
        if (initialized) {
            return;
        }
        if (SteamAPI.isSteamRunning()) {
            apps = new SteamApps();
            user = new SteamUser(new SteamUserCallback(){});
            friends = new SteamFriends(new SteamFriendsCallback(){

                public void onGameOverlayActivated(boolean active, boolean userInitiated, int appID) {
                    overlayActive = active;
                }

                public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
                    GameLog.debug.println("Got join lobby request from " + friends.getFriendPersonaName(steamIDFriend) + ": " + steamIDLobby);
                    connectLobby = steamIDLobby;
                    joinedLobby = false;
                    inLobby = false;
                }

                public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect) {
                    GameLog.debug.println("Got join request from " + friends.getFriendPersonaName(steamIDFriend) + ": " + connect);
                }
            });
            matchmaking = new SteamMatchmaking(new SteamMatchmakingCallback(){

                public void onLobbyInvite(SteamID steamIDUser, SteamID steamIDLobby, long gameID) {
                    if (gameID == 1169040L && GlobalData.getCurrentState() instanceof MainMenu) {
                        MainMenu mainMenu = (MainMenu)GlobalData.getCurrentState();
                        ConfirmationContinueForm form = new ConfirmationContinueForm("inviterecevied");
                        String name = SteamData.tryToGetSteamName(steamIDUser).orElse(steamIDUser.toString());
                        form.setupConfirmation(new LocalMessage("ui", "gotinvite", "name", name), (GameMessage)new LocalMessage("ui", "acceptbutton"), (GameMessage)new LocalMessage("ui", "declinebutton"), () -> {
                            System.out.println("Accepted invite from " + name);
                            SteamData.connectLobby(steamIDLobby);
                        }, () -> System.out.println("Declined invite from " + name));
                        mainMenu.addContinueForm("gameinvite", form);
                        WindowManager.getWindow().requestAttention();
                    }
                }

                public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response) {
                    GameLog.debug.println("Entered lobby " + steamIDLobby + ", " + chatPermissions + ", " + blocked + ", " + response);
                    joinedLobby = false;
                    if (steamIDLobby.equals((Object)connectLobby)) {
                        inLobby = true;
                    }
                }
            });
            utils = new SteamUtils(new SteamUtilsCallback(){

                public void onGamepadTextInputDismissed(boolean submitted) {
                    if (submitted) {
                        int length = utils.getEnteredGamepadTextLength();
                        System.out.println("LENGTH: " + length);
                        System.out.println("ENTERED: " + utils.getEnteredGamepadTextInput(length));
                    } else {
                        System.out.println("CANCELLED");
                    }
                }
            });
            created = true;
        }
        initialized = true;
    }

    private static boolean initAndIsCreated() {
        if (!initialized) {
            SteamData.init();
        }
        return SteamData.isCreated();
    }

    public static boolean isCreated() {
        return created;
    }

    public static SteamApps getApps() {
        return apps;
    }

    public static SteamUtils getUtils() {
        return utils;
    }

    public static SteamID getSteamID() {
        if (!SteamData.initAndIsCreated()) {
            return null;
        }
        return user.getSteamID();
    }

    public static String getSteamName() {
        if (!SteamData.initAndIsCreated()) {
            return null;
        }
        return friends.getPersonaName();
    }

    public static boolean isOverlayActive() {
        return overlayActive;
    }

    public static SteamFriend[] getFriends(SteamFriends.FriendFlags ... flags) {
        if (!SteamData.initAndIsCreated()) {
            return new SteamFriend[0];
        }
        List<SteamFriends.FriendFlags> flagsList = Arrays.asList(flags);
        int count = friends.getFriendCount(flagsList);
        SteamFriend[] out = new SteamFriend[count];
        for (int i = 0; i < count; ++i) {
            SteamID steamID = friends.getFriendByIndex(i, flagsList);
            out[i] = new SteamFriend(steamID){

                @Override
                public String getName() {
                    return friends.getFriendPersonaName(this.steamID);
                }

                @Override
                public SteamFriends.PersonaState getState() {
                    return friends.getFriendPersonaState(this.steamID);
                }

                @Override
                public SteamFriends.FriendRelationship getRelationship() {
                    return friends.getFriendRelationship(this.steamID);
                }
            };
        }
        return out;
    }

    public static String getFriendName(SteamID steamIdOfFriend) {
        return friends.getFriendPersonaName(steamIdOfFriend);
    }

    public static Optional<String> tryToGetSteamName(SteamID steamID) {
        SteamFriend[] friends;
        for (SteamFriend friend : friends = SteamData.getFriends(SteamFriends.FriendFlags.values())) {
            if (!friend.steamID.equals((Object)steamID)) continue;
            return Optional.of(friend.getName());
        }
        return Optional.empty();
    }

    public static void connectLobby(SteamID steamID) {
        if (steamID.isValid()) {
            connectLobby = steamID;
            joinedLobby = false;
            inLobby = false;
        } else {
            GameLog.warn.println(steamID + " was not a valid Steam lobby id");
        }
    }

    public static boolean isOnCallbackErrorCooldown() {
        long timeSinceLast = System.currentTimeMillis() - lastCallbackError;
        return lastCallbackError != 0L && timeSinceLast < 60000L;
    }

    public static void resetCallbackErrorCooldown() {
        lastCallbackError = System.currentTimeMillis();
    }

    public static boolean setRichPresence(String key, String value) {
        return friends.setRichPresence(key, value);
    }

    public static void clearRichPresence() {
        friends.clearRichPresence();
    }

    public static GameMessage getFriendStatusMessage(SteamFriends.PersonaState state) {
        switch (state) {
            case Online: {
                return new LocalMessage("ui", "statusonline");
            }
            case Offline: {
                return new LocalMessage("ui", "statusoffline");
            }
            case Away: {
                return new LocalMessage("ui", "statusaway");
            }
            case Busy: {
                return new LocalMessage("ui", "statusbusy");
            }
            case Snooze: {
                return new LocalMessage("ui", "statusSnooze");
            }
            case LookingToPlay: {
                return new LocalMessage("ui", "statuslookingtoplay");
            }
            case LookingToTrade: {
                return new LocalMessage("ui", "statuslookingtotrade");
            }
        }
        return new StaticMessage(state.toString());
    }

    public static ConnectInfo tickLobbyConnectRequested() {
        if (connectLobby != null) {
            if (!joinedLobby && !inLobby) {
                matchmaking.joinLobby(connectLobby);
                connectLobbyTimeout = System.currentTimeMillis() + 5000L;
                joinedLobby = true;
                return null;
            }
            if (inLobby) {
                String steamIDStr = matchmaking.getLobbyData(connectLobby, "serverHostSteamID");
                if (steamIDStr != null && !steamIDStr.isEmpty()) {
                    try {
                        long handle = Long.parseLong(steamIDStr);
                        if (handle == 0L) {
                            throw new NumberFormatException();
                        }
                        matchmaking.leaveLobby(connectLobby);
                        connectLobby = null;
                        joinedLobby = false;
                        inLobby = false;
                        return new SteamConnectInfo(SteamID.createFromNativeHandle((long)handle));
                    }
                    catch (NumberFormatException e) {
                        GameLog.warn.println("Lobby remote SteamID invalid: " + steamIDStr);
                    }
                } else {
                    String address;
                    String port = matchmaking.getLobbyData(connectLobby, "serverPort");
                    if (port != null && !port.isEmpty() && (address = matchmaking.getLobbyData(connectLobby, "serverAddress")) != null && !address.isEmpty()) {
                        try {
                            int portInt = Integer.parseInt(port);
                            if (portInt < 0) {
                                throw new NumberFormatException();
                            }
                            matchmaking.leaveLobby(connectLobby);
                            connectLobby = null;
                            joinedLobby = false;
                            inLobby = false;
                            return new AddressConnectInfo(address, portInt);
                        }
                        catch (NumberFormatException e) {
                            GameLog.warn.println("Lobby port invalid: " + port);
                        }
                    }
                }
            }
            if ((joinedLobby || inLobby) && connectLobbyTimeout < System.currentTimeMillis()) {
                GameLog.warn.println("Timed out getting connect info from lobby " + connectLobby);
                matchmaking.leaveLobby(connectLobby);
                connectLobby = null;
                joinedLobby = false;
                inLobby = false;
                return null;
            }
        }
        return null;
    }

    public static void activateGameOverlayToWebPage(String url) {
        friends.activateGameOverlayToWebPage(url, SteamFriends.OverlayToWebPageMode.Default);
    }

    public static boolean showGamepadTextInput(SteamUtils.GamepadTextInputMode inputMode, SteamUtils.GamepadTextLineMode lineMode, String description, int maxCharacters, String existingText) {
        return utils.showGamepadTextInput(inputMode, lineMode, description, maxCharacters, existingText);
    }

    public static boolean showFloatingGamepadTextInput(SteamUtils.FloatingGamepadTextInputMode keyboardMode, int textFieldXPosition, int textFieldYPosition, int textFieldWidth, int textFieldHeight) {
        return utils.showFloatingGamepadTextInput(keyboardMode, textFieldXPosition, textFieldYPosition, textFieldWidth, textFieldHeight);
    }

    public static void dispose() {
        if (SteamData.isCreated()) {
            apps.dispose();
            user.dispose();
            friends.dispose();
            matchmaking.dispose();
            utils.dispose();
        }
    }

    static {
        overlayActive = false;
        connectLobby = null;
    }

    public static class SteamConnectInfo
    extends ConnectInfo {
        public final SteamID remoteID;
        public final String friendName;

        public SteamConnectInfo(SteamID remoteID) {
            this.friendName = SteamData.getFriendName(remoteID);
            this.remoteID = remoteID;
        }

        @Override
        public void startConnectionClient(MainMenu mainMenu) {
            mainMenu.startConnection(((SteamNetworkManager)Platform.getNetworkManager()).startJoinFriendClient(this.friendName, this.remoteID), null);
        }

        public String toString() {
            return "STEAM:" + SteamID.getNativeHandle((SteamNativeHandle)this.remoteID);
        }
    }

    public static class AddressConnectInfo
    extends ConnectInfo {
        public final String address;
        public final int port;

        protected AddressConnectInfo(String address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public void startConnectionClient(MainMenu mainMenu) {
            Client client = Platform.getNetworkManager().startJoinServerClient(this.address, this.address, this.port);
            mainMenu.startConnection(client, null);
        }

        public String toString() {
            return this.address + ":" + this.port;
        }
    }

    public static abstract class ConnectInfo {
        public abstract void startConnectionClient(MainMenu var1);
    }
}

