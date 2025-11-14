/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.sharedOnPC.forms;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.NetworkPacketList;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.packet.PacketServerStatusRequest;
import necesse.engine.platforms.Platform;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormScoreboardList;
import necesse.gfx.forms.components.lists.FormSelectedElement;
import necesse.gfx.forms.components.lists.FormSelectedList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormIndexEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormServerList
extends FormSelectedList<ServerElement> {
    private final FormEventsHandler<FormIndexEvent<FormServerList>> doubleSelect;
    private DatagramSocket socket;
    private Thread listenThread;
    private Thread lanThread;
    private final NetworkPacketList incompletePackets = new NetworkPacketList(5000);
    private ServerElement landSearchElement;

    public static String fileDir() {
        return GlobalData.cfgPath() + "serverlist.cfg";
    }

    public FormServerList(int x, int y, int width, int height) {
        super(x, y, width, height, 35);
        this.doubleSelect = new FormEventsHandler();
    }

    @Override
    public void reset() {
        super.reset();
        this.elements = Collections.synchronizedList(new ArrayList());
    }

    public FormServerList onDoubleSelect(FormEventListener<FormIndexEvent<FormServerList>> listener) {
        this.doubleSelect.addListener(listener);
        return this;
    }

    public void addServer(String name, String address, int port, boolean refresh) throws IllegalArgumentException {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }
        ServerElement server = new ServerElement(name, address, port);
        this.elements.add(server);
        this.sort();
        if (refresh) {
            server.refresh(this);
        }
        this.saveData();
    }

    public void addServer(String name, String address, int port) throws IllegalArgumentException {
        this.addServer(name, address, port, true);
    }

    public boolean canDeleteSelected() {
        ServerElement el = (ServerElement)this.getSelectedElement();
        return el != null && el.canDelete();
    }

    public void deleteSelected() {
        ServerElement el = (ServerElement)this.getSelectedElement();
        if (el == null || !el.canDelete()) {
            return;
        }
        this.elements.remove(el);
        this.saveData();
        this.clearSelected();
    }

    public boolean hasSelected() {
        return this.getSelectedElement() != null;
    }

    public String getSelectedName() {
        ServerElement el = (ServerElement)this.getSelectedElement();
        return el == null ? "N/A" : el.getName();
    }

    public void refresh() {
        this.elements.forEach(el -> el.refresh(this));
    }

    public void saveData() {
        SaveData save = new SaveData("SERVERS");
        this.elements.stream().filter(ServerElement::shouldSave).forEach(server -> save.addSaveData(server.getSaveData()));
        save.saveScript(new File(FormServerList.fileDir()));
    }

    public boolean readData() {
        File file = new File(FormServerList.fileDir());
        if (!file.exists()) {
            return false;
        }
        this.elements = Collections.synchronizedList(new ArrayList());
        this.clearSelected();
        try {
            LoadData save = new LoadData(file);
            save.getLoadDataByName("SERVER").forEach(serverSave -> {
                ServerElement server = new ServerElement((LoadData)serverSave);
                if (server.port != 0) {
                    this.elements.add(server);
                }
            });
        }
        catch (Exception e) {
            System.err.println("Error loading server list, some might be missing");
            e.printStackTrace();
        }
        this.sort();
        return true;
    }

    public void connect(MainMenu menu) {
        ServerElement el = (ServerElement)this.getSelectedElement();
        if (el != null) {
            el.lastConnectedTime = new Date().getTime();
            this.sort();
            this.saveData();
            Client client = Platform.getNetworkManager().startJoinServerClient(el.getName(), el.address, el.port);
            menu.startConnection(client, MainMenu.ConnectFrom.MultiplayerJoinServer);
        }
    }

    public void setupSocket() {
        if (this.socket != null) {
            return;
        }
        try {
            this.socket = new DatagramSocket();
            final FormServerList parent = this;
            if (this.listenThread != null) {
                this.listenThread.interrupt();
            }
            this.listenThread = new Thread("Server list"){

                @Override
                public void run() {
                    while (!FormServerList.this.socket.isClosed()) {
                        try {
                            byte[] data = new byte[1024];
                            DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                            try {
                                FormServerList.this.socket.receive(datagramPacket);
                            }
                            catch (SocketException se) {
                                continue;
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }
                            if (datagramPacket.getAddress() == null) continue;
                            boolean found = false;
                            try {
                                Packet typePacket;
                                NetworkPacket packet = FormServerList.this.incompletePackets.submitPacket(new NetworkPacket(FormServerList.this.socket, datagramPacket), null);
                                if (packet == null || !((typePacket = packet.getTypePacket()) instanceof PacketServerStatus)) continue;
                                PacketServerStatus statusPacket = (PacketServerStatus)typePacket;
                                for (ServerElement e : FormServerList.this.elements.toArray(new ServerElement[0])) {
                                    if (!e.applyPacket(statusPacket)) continue;
                                    found = true;
                                }
                                if (found || !(packet.networkInfo instanceof DatagramNetworkInfo) || !((DatagramNetworkInfo)packet.networkInfo).address.isSiteLocalAddress()) continue;
                                FormServerList.this.elements.add(new ServerElement(packet, (DatagramNetworkInfo)packet.networkInfo, statusPacket, parent));
                                FormServerList.this.sort();
                            }
                            catch (UnknownPacketException e) {
                                GameLog.warn.println("Server list received unknown packet");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            this.listenThread.start();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void loadServerList() {
        if (!this.readData()) {
            this.saveData();
        }
        this.setupSocket();
        this.refresh();
        this.startLanSearch();
    }

    public void startLanSearch() {
        if (this.landSearchElement != null) {
            this.elements.remove(this.landSearchElement);
        }
        this.landSearchElement = new ServerElement(new LocalMessage("ui", "searchinglan"));
        Runnable r = () -> {
            block6: {
                MulticastSocket lanSocket = null;
                try {
                    lanSocket = new MulticastSocket();
                    lanSocket.setBroadcast(true);
                    LinkedList<InetAddress> broadcastAddresses = new LinkedList<InetAddress>();
                    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = networkInterfaces.nextElement();
                        if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress address = interfaceAddress.getBroadcast();
                            if (address == null) continue;
                            broadcastAddresses.add(address);
                        }
                    }
                    for (int i = 0; i < Settings.lanPorts.length; ++i) {
                        int port = Settings.lanPorts[i];
                        if (this.socket == null || this.socket.isClosed()) break;
                        for (InetAddress broadcastAddress : broadcastAddresses) {
                            NetworkPacket p = new NetworkPacket(new PacketServerStatusRequest(this.socket.getLocalPort(), 0), new DatagramNetworkInfo(this.socket, broadcastAddress, port));
                            p.sendPacket();
                        }
                    }
                    lanSocket.close();
                    Thread.sleep(5000L);
                    this.landSearchElement.name = new LocalMessage("ui", "searchlandone");
                }
                catch (IOException | InterruptedException e) {
                    if (lanSocket == null || lanSocket.isClosed()) break block6;
                    lanSocket.close();
                }
            }
        };
        if (this.lanThread != null && this.lanThread.isAlive()) {
            this.lanThread.interrupt();
        }
        this.lanThread = new Thread(null, r, "LAN Search");
        this.lanThread.start();
        this.elements.add(this.landSearchElement);
        this.sort();
    }

    public void sort() {
        Collections.sort(this.elements);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.lanThread != null) {
            this.lanThread.interrupt();
        }
        if (this.listenThread != null) {
            this.listenThread.interrupt();
        }
        if (this.socket != null) {
            this.socket.close();
        }
    }

    public class ServerElement
    extends FormSelectedElement<FormServerList>
    implements Comparable<ServerElement> {
        private final int state;
        private GameMessage name;
        private String address;
        private InetAddress netAddress;
        private boolean addressError;
        private int port;
        private boolean isLanServer;
        private boolean isText;
        private boolean gotInfo;
        private long uniqueID;
        private int slots;
        private int online;
        private boolean hasPassword;
        private int modsHash;
        private String version;
        private WorldSettings worldSettings;
        private int latency;
        private boolean expectsPacket;
        private long refreshTime;
        private long gotInfoRefresh;
        private long lastConnectedTime;
        private long lastClick;

        public ServerElement(GameMessage text) {
            this.name = text;
            this.state = 0;
            this.isText = true;
        }

        public ServerElement(String name, String address, int port) {
            this.name = new StaticMessage(name);
            this.address = address;
            this.state = GameRandom.globalRandom.nextInt();
            try {
                this.netAddress = InetAddress.getByName(address);
            }
            catch (UnknownHostException e) {
                this.addressError = true;
            }
            this.port = port;
            this.lastConnectedTime = new Date().getTime();
        }

        public ServerElement(LoadData save) {
            this.state = GameRandom.globalRandom.nextInt();
            this.applyLoadData(save);
        }

        public ServerElement(NetworkPacket packet, DatagramNetworkInfo networkInfo, PacketServerStatus statusPacket, FormServerList parent) {
            this("LAN Server", networkInfo.address.getHostAddress(), networkInfo.port);
            this.applyStatus(statusPacket);
            this.isLanServer = true;
            this.refresh(parent, false);
        }

        public String getName() {
            return this.name.translate();
        }

        public SaveData getSaveData() {
            SaveData save = new SaveData("SERVER");
            save.addSafeString("name", this.getName());
            save.addSafeString("address", this.address);
            save.addInt("port", this.port);
            save.addLong("lastConnected", this.lastConnectedTime);
            return save;
        }

        public void applyLoadData(LoadData save) {
            this.name = new StaticMessage(save.getSafeString("name", "necesse/server"));
            this.address = save.getSafeString("address", "address");
            this.port = save.getInt("port", 14159);
            this.lastConnectedTime = save.getLong("lastConnected", this.lastConnectedTime);
            try {
                this.netAddress = InetAddress.getByName(this.address);
            }
            catch (UnknownHostException e) {
                this.addressError = true;
            }
        }

        public boolean shouldSave() {
            return !this.isText && !this.isLanServer;
        }

        public boolean canDelete() {
            return !this.isText && !this.isLanServer;
        }

        public int getSlots() {
            return this.slots;
        }

        public int getOnline() {
            return this.online;
        }

        public boolean hasPassword() {
            return this.hasPassword;
        }

        public int getModsHash() {
            return this.modsHash;
        }

        public String getVersion() {
            return this.version;
        }

        public boolean gotInfo() {
            long waitTime = System.currentTimeMillis() - this.gotInfoRefresh;
            return this.gotInfo && waitTime > 500L;
        }

        public boolean applyPacket(PacketServerStatus statusPacket) {
            if (this.isText) {
                return false;
            }
            if (this.state == statusPacket.state) {
                return this.applyStatus(statusPacket);
            }
            if (this.gotInfo && statusPacket.uniqueID == this.uniqueID) {
                return this.applyStatus(statusPacket);
            }
            return false;
        }

        private boolean applyStatus(PacketServerStatus statusPacket) {
            if (this.expectsPacket) {
                this.latency = (int)(System.currentTimeMillis() - this.refreshTime);
            }
            this.gotInfo = true;
            this.uniqueID = statusPacket.uniqueID;
            this.online = statusPacket.playersOnline;
            this.slots = statusPacket.slots;
            this.hasPassword = statusPacket.passwordProtected;
            this.worldSettings = statusPacket.worldSettings;
            this.modsHash = statusPacket.modsHash;
            this.version = statusPacket.version;
            this.expectsPacket = false;
            return true;
        }

        public void refresh(FormServerList parent) {
            this.refresh(parent, true);
        }

        public void refresh(FormServerList parent, boolean resetGotInfo) {
            if (this.isText) {
                return;
            }
            if (parent.socket != null) {
                try {
                    NetworkPacket p = new NetworkPacket(new PacketServerStatusRequest(this.isLanServer ? parent.socket.getLocalPort() : 0, this.state), new DatagramNetworkInfo(parent.socket, this.netAddress, this.port));
                    if (this.netAddress != null) {
                        p.sendPacket();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.refreshTime = System.currentTimeMillis();
            this.expectsPacket = true;
            if (resetGotInfo) {
                this.gotInfo = false;
                this.gotInfoRefresh = this.refreshTime;
            }
        }

        @Override
        protected void draw(FormServerList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            if (this.isText) {
                FontOptions options = new FontOptions(16).color(FormServerList.this.getInterfaceStyle().activeTextColor);
                String text = GameUtils.maxString(this.getName(), options, parent.width - 10);
                int mid = parent.width / 2 - FontManager.bit.getWidthCeil(text, options) / 2;
                FontManager.bit.drawString(mid, 10.0f, text, options);
                return;
            }
            Color col = this.isSelected() ? FormServerList.this.getInterfaceStyle().highlightTextColor : FormServerList.this.getInterfaceStyle().activeTextColor;
            FontOptions nameOptions = new FontOptions(16).color(col);
            String name = GameUtils.maxString(this.getName(), nameOptions, parent.width - 60);
            FontManager.bit.drawString(10.0f, 2.0f, name, nameOptions);
            Color noteCol = this.isSelected() ? col : FormServerList.this.getInterfaceStyle().activeTextColor;
            FontOptions noteOptions = new FontOptions(12).color(noteCol);
            String address = GameUtils.maxString(this.getAddressString(), noteOptions, parent.width - 60);
            FontManager.bit.drawString(10.0f, 20.0f, address, noteOptions);
            boolean gotInfo = this.gotInfo();
            if (gotInfo) {
                String latencyTip = this.latency < 0 ? "N/A" : String.valueOf(this.latency);
                FormScoreboardList.drawLatencyBars(this.latency, 5, 16, parent.width - 32 - 5, 2);
                if (this.isMouseOver(parent) && FormScoreboardList.isMouseOverLatencyBar(parent.width - 32 - 5, 2, this.getMoveEvent())) {
                    GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("ui", "latencytip", "latency", latencyTip)), TooltipLocation.FORM_FOCUS);
                }
                int iconX = parent.width - 22;
                for (SERVER_ICONS icon : SERVER_ICONS.values()) {
                    IconOptions options = icon.getOptions(this);
                    if (options == null) continue;
                    options.sprite.initDraw().size(16, 16).draw(iconX, 20);
                    if (options.tooltip != null && this.getMoveEvent() != null && this.mouseOverIcon(iconX, 20, this.getMoveEvent())) {
                        GameTooltipManager.addTooltip(options.tooltip, TooltipLocation.FORM_FOCUS);
                    }
                    iconX -= 18;
                }
            }
            String status = this.getStatusString();
            FontManager.bit.drawString(parent.width - 10 - FontManager.bit.getWidthCeil(status, nameOptions) - (gotInfo ? 32 : 0), 2.0f, status, nameOptions);
        }

        private boolean mouseOverIcon(int drawX, int drawY, InputEvent event) {
            if (event == null) {
                return false;
            }
            return new Rectangle(drawX, drawY, 16, 16).contains(event.pos.hudX, event.pos.hudY);
        }

        public String getStatusString() {
            if (this.gotInfo()) {
                return this.online + "/" + this.slots;
            }
            long waitTime = System.currentTimeMillis() - this.refreshTime;
            if (waitTime > 10000L) {
                return "???";
            }
            int spot = (int)(waitTime / 500L % 3L);
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < 3; ++i) {
                out.append(spot == i ? "?" : ".");
            }
            return out.toString();
        }

        public String getAddressString() {
            if (this.addressError) {
                return Localization.translate("ui", "unknownaddress");
            }
            return this.address + ":" + this.port;
        }

        @Override
        public int compareTo(ServerElement other) {
            if (this.isLanServer) {
                return 1;
            }
            if (other.isLanServer) {
                return -1;
            }
            if (this.isText) {
                return 1;
            }
            if (other.isText) {
                return -1;
            }
            return Long.compare(other.lastConnectedTime, this.lastConnectedTime);
        }

        @Override
        protected void onClick(FormServerList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            if (this.isText) {
                return;
            }
            ServerElement lastSelected = (ServerElement)parent.getSelectedElement();
            super.onClick(parent, elementIndex, event, perspective);
            if (lastSelected == this && System.currentTimeMillis() - this.lastClick < 500L) {
                parent.doubleSelect.onEvent(new FormIndexEvent<FormServerList>(parent, elementIndex));
            }
            if (this.isSelected()) {
                this.lastClick = System.currentTimeMillis();
                if (lastSelected != this) {
                    FormServerList.this.playTickSound();
                }
            }
        }

        @Override
        protected void onControllerEvent(FormServerList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            if (this.isText) {
                return;
            }
            ServerElement lastSelected = (ServerElement)parent.getSelectedElement();
            super.onControllerEvent(parent, elementIndex, event, tickManager, perspective);
            if (lastSelected == this && System.currentTimeMillis() - this.lastClick < 500L) {
                parent.doubleSelect.onEvent(new FormIndexEvent<FormServerList>(parent, elementIndex));
            }
            if (this.isSelected()) {
                this.lastClick = System.currentTimeMillis();
                if (lastSelected != this) {
                    FormServerList.this.playTickSound();
                }
            }
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    @FunctionalInterface
    private static interface IconGetter {
        public IconOptions getOptions(ServerElement var1);
    }

    private static class IconOptions {
        public final GameSprite sprite;
        public final GameTooltips tooltip;

        public IconOptions(GameSprite sprite, GameTooltips tooltip) {
            Objects.requireNonNull(sprite);
            this.sprite = sprite;
            this.tooltip = tooltip;
        }
    }

    private static enum SERVER_ICONS {
        VERSION_CHECK(s -> {
            boolean sameVersion = s.getVersion().equals("1.0.1");
            GameSprite sprite = new GameSprite(sameVersion ? Settings.UI.status_version_good : Settings.UI.status_version_bad);
            StringTooltips tooltip = new StringTooltips(Localization.translate("ui", sameVersion ? "correctversion" : "wrongversion"));
            if (!sameVersion) {
                tooltip.add(Localization.translate("ui", "serverversion", "version", s.getVersion()));
            }
            return new IconOptions(sprite, tooltip);
        }),
        MODS_CHECK(s -> {
            boolean sameMods;
            boolean hasMods = s.getModsHash() != 0;
            boolean bl = sameMods = s.getModsHash() == ModLoader.getModsHash();
            if (hasMods || !sameMods) {
                GameSprite sprite = new GameSprite(sameMods ? Settings.UI.status_mods_good : Settings.UI.status_mods_bad);
                StringTooltips tooltip = new StringTooltips(Localization.translate("ui", sameMods ? "modmatch" : "modmismatch"));
                return new IconOptions(sprite, tooltip);
            }
            return null;
        }),
        PASSWORD_CHECK(s -> {
            if (!s.hasPassword()) {
                return null;
            }
            GameSprite sprite = new GameSprite(Settings.UI.status_password);
            StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "passwordprotected"));
            return new IconOptions(sprite, tooltips);
        }),
        ACHIEVEMENTS_CHECK(s -> {
            if (s.worldSettings == null) {
                return null;
            }
            GameSprite sprite = new GameSprite(s.worldSettings.achievementsEnabled() ? Settings.UI.status_achievements_good : Settings.UI.status_achievements_bad);
            StringTooltips tooltips = new StringTooltips(Localization.translate("ui", s.worldSettings.achievementsEnabled() ? "achenabled" : "achdisabled"));
            return new IconOptions(sprite, tooltips);
        }),
        WORLD_SETTINGS(s -> {
            if (s.worldSettings == null) {
                return null;
            }
            GameSprite sprite = new GameSprite(Settings.UI.status_settings);
            GameTooltips tooltips = s.worldSettings.getTooltips(new LocalMessage("ui", "serversettings"));
            return new IconOptions(sprite, tooltips);
        });

        private final IconGetter iconGetter;

        private SERVER_ICONS(IconGetter iconGetter) {
            Objects.requireNonNull(iconGetter);
            this.iconGetter = iconGetter;
        }

        public IconOptions getOptions(ServerElement server) {
            return this.iconGetter.getOptions(server);
        }
    }
}

