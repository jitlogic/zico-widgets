/**
 * Copyright 2012-2014 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jitlogic.zico.widgets.demo.client.views;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.jitlogic.zico.widgets.client.*;
import com.jitlogic.zico.widgets.demo.client.DemoIcons;
import com.jitlogic.zico.widgets.demo.client.data.UserInfo;
import com.jitlogic.zico.widgets.demo.client.data.UserService;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import javax.inject.Inject;
import java.util.*;

public class UserManagementPanel extends Composite {
    interface UserManagementPanelUiBinder extends UiBinder<DockLayoutPanel, UserManagementPanel> { }
    private static UserManagementPanelUiBinder ourUiBinder = GWT.create(UserManagementPanelUiBinder.class);

    @UiField
    ToolButton btnRefresh;

    @UiField
    ToolButton btnAdd;

    @UiField
    ToolButton btnEdit;

    @UiField
    ToolButton btnRemove;

    @UiField
    ToolButton btnPassword;

    @UiField
    ToolSeparator separator;

    @UiField
    ToolButton btnOk;

    @UiField
    ToolButton btnCancel;

    @UiField(provided = true)
    DataGrid<UserInfo> userGrid;

    private static final String MDS = "UserManagementPanel";

    private Set<Integer> changedIds = new HashSet<Integer>();
    private Set<Integer> removedIds = new HashSet<Integer>();

    private PopupMenu contextMenu;

    private ListDataProvider<UserInfo> userStore;
    private SingleSelectionModel<UserInfo> selectionModel;

    private MessageDisplay md;
    private UserService userService;

    @Inject
    public UserManagementPanel(MessageDisplay md, UserService userService) {

        this.md = md;
        this.userService = userService;

        createUserGrid();
        createContextMenu();

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    private void markChange(UserInfo user) {
        changedIds.add(user.getId());
        if (!separator.isVisible()) {
            editMode(true);
        }
    }

    private void editMode(boolean enabled) {
        separator.setVisible(enabled);
        btnOk.setVisible(enabled);
        btnCancel.setVisible(enabled);
    }

    private final static ProvidesKey<UserInfo> KEY_PROVIDER = new ProvidesKey<UserInfo>() {
        public Object getKey(UserInfo item) {
            return item.getId();
        }
    };

    private void createUserGrid() {
        userGrid = new DataGrid<UserInfo>(1024 * 1024, ZicoDataGridResources.INSTANCE, KEY_PROVIDER);
        selectionModel = new SingleSelectionModel<UserInfo>(KEY_PROVIDER);
        userGrid.setSelectionModel(selectionModel);

        Column<UserInfo,String> colUserId = new Column<UserInfo, String>(new TextCell()) {
            @Override
            public String getValue(UserInfo object) {
                return ""+object.getId();
            }
        };
        userGrid.addColumn(colUserId, new ResizableHeader<UserInfo>("#", userGrid, colUserId));
        userGrid.setColumnWidth(colUserId, 35, Style.Unit.PX);

        final EditTextCell cellUsername = new EditTextCell();
        Column<UserInfo,String> colUsername = new Column<UserInfo, String>(cellUsername) {
            @Override
            public String getValue(UserInfo user) {
                return user.getUsername();
            }
        };
        colUsername.setFieldUpdater(new FieldUpdater<UserInfo, String>() {
            public void update(int index, UserInfo user, String value) {
                markChange(user);
                user.setUsername(value);
                cellUsername.clearViewData(user.getId());
            }
        });
        userGrid.addColumn(colUsername, new ResizableHeader<UserInfo>("Username", userGrid, colUsername));
        userGrid.setColumnWidth(colUsername, 128, Style.Unit.PX);

        Map<String,Integer> roles = ZicoWidgets.map("VIEWER", 1, "ADMIN", 2);

        final SelectCell<String,Integer> cellRoles = new SelectCell<String, Integer>(roles);
        Column<UserInfo,Integer> colUserRole = new Column<UserInfo, Integer>(cellRoles) {
            @Override
            public Integer getValue(UserInfo user) {
                return user.getRole();
            }
        };
        colUserRole.setFieldUpdater(new FieldUpdater<UserInfo, Integer>() {
            public void update(int index, UserInfo user, Integer value) {
                markChange(user);
                user.setRole(value);
                cellRoles.clearViewData(user.getId());
            }
        });
        userGrid.addColumn(colUserRole, new ResizableHeader<UserInfo>("Role", userGrid, colUserRole));
        userGrid.setColumnWidth(colUserRole, 128, Style.Unit.PX);


        final EditTextCell cellFullname = new EditTextCell();
        Column<UserInfo,String> colFullname = new Column<UserInfo, String>(cellFullname) {
            @Override
            public String getValue(UserInfo user) {
                return user.getFullname();
            }
        };
        colFullname.setFieldUpdater(new FieldUpdater<UserInfo, String>() {
            public void update(int index, UserInfo user, String value) {
                markChange(user);
                user.setFullname(value);
                cellFullname.clearViewData(user.getId());
            }
        });
        userGrid.addColumn(colFullname, new ResizableHeader<UserInfo>("Full Name", userGrid, colFullname));
        userGrid.setColumnWidth(colFullname, 256, Style.Unit.PX);


        final EditTextCell cellComment = new EditTextCell();
        Column<UserInfo,String> colComment = new Column<UserInfo, String>(cellComment) {
            @Override
            public String getValue(UserInfo user) {
                return user.getComment();
            }
        };
        colComment.setFieldUpdater(new FieldUpdater<UserInfo, String>() {
            public void update(int index, UserInfo user, String value) {
                markChange(user);
                user.setComment(value);
                cellComment.clearViewData(user.getId());
            }
        });
        userGrid.addColumn(colComment, "Comment");
        userGrid.setColumnWidth(colComment, 100, Style.Unit.PC);

        userStore = new ListDataProvider<UserInfo>(KEY_PROVIDER);
        userStore.addDataDisplay(userGrid);

        userGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<UserInfo>() {
            public void onCellPreview(CellPreviewEvent<UserInfo> event) {
                NativeEvent nev = event.getNativeEvent();
                String eventType = nev.getType();
                if ((BrowserEvents.KEYDOWN.equals(eventType) && nev.getKeyCode() == KeyCodes.KEY_ENTER)
                        || BrowserEvents.DBLCLICK.equals(nev.getType())) {
                    selectionModel.setSelected(event.getValue(), true);
                    editUser(null);
                }
                if (BrowserEvents.CONTEXTMENU.equals(eventType)) {
                    selectionModel.setSelected(event.getValue(), true);
                    if (event.getValue() != null) {
                        contextMenu.setPopupPosition(
                                event.getNativeEvent().getClientX(),
                                event.getNativeEvent().getClientY());
                        contextMenu.show();
                    }
                }

            }
        });

        userGrid.addDomHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
                event.preventDefault();
            }
        }, DoubleClickEvent.getType());
        userGrid.addDomHandler(new ContextMenuHandler() {
            public void onContextMenu(ContextMenuEvent event) {
                event.preventDefault();
            }
        }, ContextMenuEvent.getType());

    }

    private void createContextMenu() {
        contextMenu = new PopupMenu();

        MenuItem mnuRefresh = new MenuItem("Refresh", DemoIcons.INSTANCE.refreshIcon(),
                new Scheduler.ScheduledCommand() {
                    public void execute() {
                        refreshUsers(null);
                    }
                });
        contextMenu.addItem(mnuRefresh);

        contextMenu.addSeparator();

        MenuItem mnuAddUser = new MenuItem("Add user", DemoIcons.INSTANCE.addIcon(),
                new Scheduler.ScheduledCommand() {
                    public void execute() {
                        addUser(null);
                    }
                });
        contextMenu.addItem(mnuAddUser);

        MenuItem mnuRemoveUser = new MenuItem("Remove user", DemoIcons.INSTANCE.removeIcon(),
                new Scheduler.ScheduledCommand() {
                    public void execute() {
                        removeUser(null);
                    }
                });
        contextMenu.addItem(mnuRemoveUser);

        MenuItem mnuEditUser = new MenuItem("Edit user", DemoIcons.INSTANCE.editIcon(),
                new Scheduler.ScheduledCommand() {
                    public void execute() {
                        editUser(null);
                    }
                });
        contextMenu.addItem(mnuEditUser);

        contextMenu.addSeparator();

        MenuItem mnuChangePassword = new MenuItem("Change password", DemoIcons.INSTANCE.keyIcon(),
                new Scheduler.ScheduledCommand() {
                    public void execute() {
                        changePassword(null);
                    }
                });
        contextMenu.addItem(mnuChangePassword);
    }


    @UiHandler("btnAdd")
    void addUser(ClickEvent e) {
        UserInfo user = new UserInfo();
        user.setUsername("newuser");
        user.setFullname("New User");
        user.setRole(UserInfo.VIEWER);
        userStore.getList().add(0, user);
    }


    @UiHandler("btnRefresh")
    void refreshUsers(ClickEvent e) {
        userStore.getList().clear();
        userService.list(new MethodCallback<List<UserInfo>>() {
            public void onFailure(Method method, Throwable e) {
                md.error(MDS, "Error loading user data", e);
            }

            public void onSuccess(Method method, List<UserInfo> users) {
                userStore.getList().addAll(users);
                userStore.flush();
                userGrid.redraw();
                md.clear(MDS);
            }
        });

    }


    @UiHandler("btnRemove")
    void removeUser(ClickEvent e) {
        // TODO
    }


    @UiHandler("btnEdit")
    void editUser(ClickEvent e) {
        // TODO
    }


    @UiHandler("btnPassword")
    void changePassword(ClickEvent e) {
        // TODO
    }


    @UiHandler("btnCancel")
    void cancelChanges(ClickEvent e) {
        editMode(false);
        refreshUsers(e);
    }


    @UiHandler("btnOk")
    void saveChanges(ClickEvent e) {
        editMode(false);

        refreshUsers(e);
    }
}