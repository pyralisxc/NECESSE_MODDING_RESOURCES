/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalToggle;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTeamInvite;
import necesse.gfx.forms.components.FormTeamJoinRequest;
import necesse.gfx.forms.components.FormTeamMember;
import necesse.gfx.forms.components.FormTeamPlayerInvite;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPAllTeamsUpdateEvent;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;

public class PvPTeamsContainerForm
extends ContainerFormSwitcher<PvPTeamsContainer> {
    public static boolean pauseGameOnClose = false;
    private boolean isOwner;
    public Form main;
    public Form invites;
    public Form changeName;
    public Form joinTeam;
    public ConfirmationForm confirmForm;
    public FormLocalLabel ownerLabel;
    public FormContentBox membersContent;
    public FormContentBox invitesContent;
    public FormContentBox joinTeamContent;
    public FormTextInput changeNameInput;
    public FormContentIconButton isPublicButton;

    public PvPTeamsContainerForm(Client client, PvPTeamsContainer container) {
        super(client, container);
        this.setupMainForm();
        FormFlow joinTeamFlow = new FormFlow(5);
        this.joinTeam = this.addComponent(new Form("jointeam", 300, 400));
        this.joinTeam.addComponent(joinTeamFlow.nextY(new FormLocalLabel("ui", "teamjoinateam", new FontOptions(20), 0, this.joinTeam.getWidth() / 2, 0, this.joinTeam.getWidth() - 20), 10));
        this.joinTeamContent = this.joinTeam.addComponent(new FormContentBox(0, joinTeamFlow.next(350), this.joinTeam.getWidth(), 350));
        this.joinTeamContent.alwaysShowVerticalScrollBar = true;
        this.joinTeam.addComponent(new FormLocalTextButton("ui", "backbutton", 4, joinTeamFlow.next(40), this.joinTeam.getWidth() - 8, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE)).onClicked(e -> this.makeCurrent(this.main));
        this.joinTeam.setHeight(joinTeamFlow.next());
        this.confirmForm = this.addComponent(new ConfirmationForm("teamconfirm"));
        this.changeName = this.addComponent(new Form(300, 110));
        this.changeName.addComponent(new FormLocalLabel("ui", "teamchangename", new FontOptions(20), 0, this.changeName.getWidth() / 2, 5));
        this.changeNameInput = this.changeName.addComponent(new FormTextInput(4, 30, FormInputSize.SIZE_32_TO_40, this.changeName.getWidth() - 8, 20));
        this.changeName.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6)).onClicked(e -> {
            container.changeTeamNameAction.runAndSend(this.changeNameInput.getText());
            this.makeCurrent(this.main);
        });
        this.changeName.addComponent(new FormLocalTextButton("ui", "backbutton", this.changeName.getWidth() / 2 + 2, this.changeName.getHeight() - 40, this.changeName.getWidth() / 2 - 6)).onClicked(e -> this.makeCurrent(this.main));
        container.onEvent(PvPCurrentTeamUpdateEvent.class, e -> this.onFullUpdate());
        container.onEvent(PvPOwnerUpdateEvent.class, e -> this.onOwnerUpdate());
        container.onEvent(PvPPublicUpdateEvent.class, e -> this.onPublicUpdate());
        container.onEvent(PvPMemberUpdateEvent.class, e -> this.updateMembersContent());
        container.onEvent(PvPJoinRequestUpdateEvent.class, e -> this.updateMembersContent());
        container.onEvent(PvPAllTeamsUpdateEvent.class, e -> this.updateJoinTeamsContent((PvPAllTeamsUpdateEvent)e));
        this.makeCurrent(this.main);
    }

    public void setupMainForm() {
        if (this.main != null) {
            this.removeComponent(this.main);
        }
        this.main = this.addComponent(new Form(300, 400));
        this.ownerLabel = null;
        this.membersContent = null;
        this.isOwner = false;
        FormFlow flow = new FormFlow(10);
        this.main.addComponent(new FormLocalLabel("ui", "pvplabel", new FontOptions(16), 0, this.main.getWidth() / 2, flow.next(20)));
        FormHorizontalToggle pvpToggle = this.main.addComponent(new FormHorizontalToggle(this.main.getWidth() / 2 - 16, flow.next(30)));
        pvpToggle.setToggled(this.client.pvpEnabled());
        pvpToggle.onToggled(e -> this.client.setPvP(((FormButtonToggle)e.from).isToggled()));
        pvpToggle.setCooldown(5000);
        long pvpCooldown = System.currentTimeMillis() - this.client.pvpChangeTime;
        if (pvpCooldown < 5000L) {
            pvpToggle.startCooldown((int)(5000L - pvpCooldown));
        }
        pvpToggle.setActive(!this.client.worldSettings.forcedPvP);
        PvPTeamsContainer.TeamData team = ((PvPTeamsContainer)this.container).data.currentTeam;
        if (team == null) {
            this.main.addComponent(flow.nextY(new FormLocalLabel("ui", "teamnocurrent", new FontOptions(16), 0, this.main.getWidth() / 2, 0, this.main.getWidth() - 10), 10));
            this.main.addComponent(new FormLocalTextButton("ui", "teamcreate", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> {
                ((PvPTeamsContainer)this.container).createTeamButton.runAndSend();
                ((FormButton)e.from).startCooldown(5000);
            });
            this.main.addComponent(new FormLocalTextButton("ui", "teamjoinateam", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> {
                this.joinTeamContent.clearComponents();
                this.joinTeamContent.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 10, this.joinTeamContent.getWidth() - 20));
                this.joinTeamContent.setContentBox(new Rectangle(this.joinTeamContent.getWidth(), this.joinTeamContent.getHeight()));
                this.makeCurrent(this.joinTeam);
                ((PvPTeamsContainer)this.container).askForExistingTeams.runAndSend();
            });
            flow.next(5);
            FormLocalLabel invitesLabel = this.main.addComponent(new FormLocalLabel("ui", "teaminvites", new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(invitesLabel.getHeight() + 10);
            this.invitesContent = this.main.addComponent(new FormContentBox(0, flow.next(120), this.main.getWidth(), 120));
            this.updateInvitesContent();
        } else {
            FormLocalLabel teamLabel = this.main.addComponent(new FormLocalLabel(new StaticMessage(team.name), new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(teamLabel.getHeight() + 5);
            long owner = team.owner;
            PvPTeamsContainer.MemberData ownerMember = ((PvPTeamsContainer)this.container).data.members.stream().filter(m -> m.auth == owner).findFirst().orElse(null);
            String ownerName = ownerMember == null ? "Unknown" : ownerMember.name;
            this.ownerLabel = this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "teamowner", "owner", ownerName), new FontOptions(12), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(this.ownerLabel.getHeight() + 5);
            boolean bl = this.isOwner = owner == GameAuth.getAuthentication();
            if (this.isOwner) {
                flow.next(5);
                FormLocalLabel publicLabel = this.main.addComponent(flow.nextY(new FormLocalLabel("ui", "teamcanselfjoin", new FontOptions(16), -1, 28, 0, this.main.getWidth() - 32), 10));
                this.isPublicButton = this.main.addComponent(new FormContentIconButton(4, publicLabel.getY() + publicLabel.getHeight() / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, new GameMessage[0]));
                this.isPublicButton.onClicked(e -> ((PvPTeamsContainer)this.container).setPublicAction.runAndSend(!((PvPTeamsContainer)this.container).data.currentTeam.isPublic));
                this.onPublicUpdate();
                this.main.addComponent(new FormLocalTextButton("ui", "teamchangename", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> {
                    this.changeNameInput.setText(team.name);
                    this.makeCurrent(this.changeName);
                });
            }
            this.main.addComponent(new FormLocalTextButton("ui", "teaminvite", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> this.openInvites());
            flow.next(5);
            FormLocalLabel membersLabel = this.main.addComponent(new FormLocalLabel("ui", "teammembers", new FontOptions(20), 0, this.main.getWidth() / 2, flow.next(), this.main.getWidth() - 10));
            flow.next(membersLabel.getHeight() + 10);
            this.membersContent = this.main.addComponent(new FormContentBox(0, flow.next(120), this.main.getWidth(), 120));
            this.updateMembersContent();
            this.main.addComponent(new FormLocalTextButton("ui", "teamleave", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> {
                GameMessageBuilder builder = new GameMessageBuilder().append("ui", "teamleaveconf");
                if (((PvPTeamsContainer)this.container).data.currentTeam != null && !((PvPTeamsContainer)this.container).data.currentTeam.isPublic) {
                    builder.append("\n\n").append("ui", "teamleaveneedinvite");
                }
                this.confirmForm.setupConfirmation(builder, () -> {
                    ((PvPTeamsContainer)this.container).leaveTeamButton.runAndSend();
                    this.setupMainForm();
                    this.makeCurrent(this.main);
                }, () -> this.makeCurrent(this.main));
                this.makeCurrent(this.confirmForm);
            });
        }
        this.main.addComponent(new FormLocalTextButton("ui", "closebutton", 4, flow.next(40), this.main.getWidth() - 8)).onClicked(e -> this.client.closeContainer(true));
        this.main.setHeight(flow.next());
        GameWindow window = WindowManager.getWindow();
        this.main.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void updateInvitesContent() {
        if (this.invitesContent != null) {
            this.invitesContent.clearComponents();
            FormFlow invitesFlow = new FormFlow();
            if (((PvPTeamsContainer)this.container).data.invites.isEmpty()) {
                invitesFlow.next(10);
                this.invitesContent.addComponent(invitesFlow.nextY(new FormLocalLabel("ui", "teamnoinvites", new FontOptions(16), 0, this.invitesContent.getWidth() / 2, 10, this.invitesContent.getWidth() - 20), 10));
            } else {
                int colorCounter = 0;
                for (PvPTeamsContainer.InviteData invite : ((PvPTeamsContainer)this.container).data.invites) {
                    Color color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                    this.invitesContent.addComponent(new FormTeamInvite(4, invitesFlow.next(20), this.main.getWidth() - 8, 20, invite, color){

                        @Override
                        public void onAccept(PvPTeamsContainer.InviteData invite) {
                            PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamInviteReply(invite.teamID, true));
                        }

                        @Override
                        public void onDecline(PvPTeamsContainer.InviteData invite) {
                            ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).data.invites.removeIf(i -> i.teamID == invite.teamID);
                            PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamInviteReply(invite.teamID, false));
                            PvPTeamsContainerForm.this.updateInvitesContent();
                        }
                    });
                    ++colorCounter;
                }
            }
            this.invitesContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), invitesFlow.next()));
        }
    }

    public void updateJoinTeamsContent(PvPAllTeamsUpdateEvent event) {
        this.joinTeamContent.clearComponents();
        FormFlow flow = new FormFlow();
        if (event.teams.isEmpty()) {
            flow.next(10);
            this.joinTeamContent.addComponent(flow.nextY(new FormLocalLabel("ui", "teamnoteams", new FontOptions(16), 0, this.joinTeamContent.getWidth() / 2, 0, this.joinTeamContent.getWidth() - 20), 10));
        } else {
            for (PvPTeamsContainer.TeamData team : event.teams) {
                this.joinTeamContent.addComponent(flow.nextY(new FormLabel(team.name, new FontOptions(20), -1, 5, 0), 2));
                this.joinTeamContent.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "teammembercount", "count", team.memberCount), new FontOptions(16), -1, 5, 0), 2));
                if (team.isPublic) {
                    this.joinTeamContent.addComponent(flow.nextY(new FormLocalLabel("ui", "teampublic", new FontOptions(16), -1, 5, 0), 2));
                    this.joinTeamContent.addComponent(flow.nextY(new FormLocalTextButton("ui", "teamjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked(e -> {
                        ((PvPTeamsContainer)this.container).requestToJoinTeam.runAndSend(team.teamID);
                        ((FormButton)e.from).startCooldown(5000);
                    });
                } else {
                    this.joinTeamContent.addComponent(flow.nextY(new FormLocalLabel("ui", "teamprivate", new FontOptions(16), -1, 5, 0), 2));
                    this.joinTeamContent.addComponent(flow.nextY(new FormLocalTextButton("ui", "teamrequestjoin", 4, 0, this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked(e -> {
                        ((PvPTeamsContainer)this.container).requestToJoinTeam.runAndSend(team.teamID);
                        ((FormButton)e.from).startCooldown(5000);
                    });
                }
                flow.next(8);
                this.joinTeamContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 4, flow.next(), this.joinTeamContent.getWidth() - 8 - this.joinTeamContent.getScrollBarWidth(), true));
                flow.next(8);
            }
        }
        this.joinTeamContent.setContentBox(new Rectangle(0, 0, this.joinTeamContent.getWidth(), flow.next()));
    }

    public void updateMembersContent() {
        if (this.membersContent != null) {
            Color color;
            this.membersContent.clearComponents();
            FormFlow membersFlow = new FormFlow();
            int colorCounter = 0;
            for (PvPTeamsContainer.MemberData member : ((PvPTeamsContainer)this.container).data.members) {
                color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                this.membersContent.addComponent(new FormTeamMember(4, membersFlow.next(20), this.main.getWidth() - 8, 20, member, this.isOwner, color){

                    @Override
                    public void onKickMember(PvPTeamsContainer.MemberData member) {
                        ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).kickMemberAction.runAndSend(member.auth);
                    }

                    @Override
                    public void onPassOwnership(PvPTeamsContainer.MemberData member) {
                        ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).passOwnershipAction.runAndSend(member.auth);
                    }
                });
                ++colorCounter;
            }
            if (!((PvPTeamsContainer)this.container).data.joinRequests.isEmpty()) {
                membersFlow.next(10);
                this.membersContent.addComponent(new FormLocalLabel("ui", "teamjoinrequests", new FontOptions(16), -1, 5, membersFlow.next(20) + 2));
                for (PvPTeamsContainer.MemberData request : ((PvPTeamsContainer)this.container).data.joinRequests) {
                    color = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
                    this.membersContent.addComponent(new FormTeamJoinRequest(4, membersFlow.next(20), this.main.getWidth() - 8, 20, request, color){

                        @Override
                        public void onAccept(PvPTeamsContainer.MemberData request) {
                            PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamRequestReply(request.auth, true));
                        }

                        @Override
                        public void onDecline(PvPTeamsContainer.MemberData request) {
                            PvPTeamsContainerForm.this.client.network.sendPacket(new PacketPlayerTeamRequestReply(request.auth, false));
                            ((PvPTeamsContainer)PvPTeamsContainerForm.this.container).data.joinRequests.removeIf(i -> i.auth == request.auth);
                            PvPTeamsContainerForm.this.updateMembersContent();
                        }
                    });
                    ++colorCounter;
                }
            }
            this.membersContent.setContentBox(new Rectangle(0, 0, this.main.getWidth(), membersFlow.next()));
        }
    }

    public void openInvites() {
        if (this.invites != null) {
            this.removeComponent(this.invites);
        }
        this.invites = this.addComponent(new Form(300, 300));
        this.invites.addComponent(new FormLocalLabel("ui", "teaminvite", new FontOptions(20), 0, this.invites.getWidth() / 2, 5));
        FormContentBox content = this.invites.addComponent(new FormContentBox(0, 30, this.invites.getWidth(), this.invites.getHeight() - 110));
        List inviteComps = this.client.streamClients().filter(c -> c != null && c.slot != this.client.getSlot()).map(c -> new FormTeamPlayerInvite(0, 0, (ClientClient)c, this.invites.getWidth(), null)).collect(Collectors.toList());
        FormFlow invitesFlow = new FormFlow();
        int colorCounter = 0;
        for (FormTeamPlayerInvite inviteComp : inviteComps) {
            inviteComp.backgroundColor = colorCounter % 2 == 0 ? new Color(220, 220, 220) : new Color(180, 180, 180);
            inviteComp.setPosition(0, invitesFlow.next(20));
            content.addComponent(inviteComp);
            ++colorCounter;
        }
        content.setContentBox(new Rectangle(0, 0, content.getWidth(), invitesFlow.next()));
        this.invites.addComponent(new FormLocalTextButton("ui", "teaminviteselected", 4, this.invites.getHeight() - 80, this.invites.getWidth() - 8)).onClicked(e -> {
            ClientClient[] clients = (ClientClient[])inviteComps.stream().filter(c -> c.selected && this.client.getClient(c.client.slot) == c.client).map(c -> c.client).toArray(ClientClient[]::new);
            if (clients.length != 0) {
                ((PvPTeamsContainer)this.container).inviteMembersAction.runAndSend(clients);
            }
            this.makeCurrent(this.main);
            this.removeComponent(this.invites);
            this.invites = null;
        });
        this.invites.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.invites.getHeight() - 40, this.invites.getWidth() - 8)).onClicked(e -> {
            this.makeCurrent(this.main);
            this.removeComponent(this.invites);
            this.invites = null;
        });
        GameWindow window = WindowManager.getWindow();
        this.invites.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.makeCurrent(this.invites);
    }

    public void onFullUpdate() {
        boolean mainCurrent = this.isCurrent(this.main);
        this.setupMainForm();
        if (mainCurrent) {
            this.makeCurrent(this.main);
        } else if (((PvPTeamsContainer)this.container).data.currentTeam != null) {
            if (this.isCurrent(this.joinTeam)) {
                this.makeCurrent(this.main);
            }
            if (((PvPTeamsContainer)this.container).data.currentTeam.owner != GameAuth.getAuthentication() && this.isCurrent(this.changeName)) {
                this.makeCurrent(this.main);
            }
        } else if (this.invites != null && this.isCurrent(this.invites) || this.isCurrent(this.changeName)) {
            this.makeCurrent(this.main);
        }
    }

    public void onOwnerUpdate() {
        if (this.isOwner || ((PvPTeamsContainer)this.container).data.currentTeam.owner == GameAuth.getAuthentication()) {
            this.onFullUpdate();
        } else if (this.ownerLabel != null) {
            long owner = ((PvPTeamsContainer)this.container).data.currentTeam.owner;
            PvPTeamsContainer.MemberData ownerMember = ((PvPTeamsContainer)this.container).data.members.stream().filter(m -> m.auth == owner).findFirst().orElse(null);
            String ownerName = ownerMember == null ? "Unknown" : ownerMember.name;
            this.ownerLabel.setLocalization(new LocalMessage("ui", "teamowner", "owner", ownerName));
        }
    }

    public void onPublicUpdate() {
        if (this.isPublicButton != null && ((PvPTeamsContainer)this.container).data.currentTeam != null) {
            this.isPublicButton.setIcon(((PvPTeamsContainer)this.container).data.currentTeam.isPublic ? this.getInterfaceStyle().button_checked_20 : this.getInterfaceStyle().button_escaped_20);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.main.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        if (this.invites != null) {
            this.invites.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        }
        this.changeName.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.joinTeam.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }

    @Override
    public void onContainerClosed() {
        super.onContainerClosed();
        if (pauseGameOnClose) {
            State currentState = GlobalData.getCurrentState();
            if (currentState instanceof MainGame) {
                currentState.setRunning(false);
            }
            pauseGameOnClose = false;
        }
    }
}

