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

package com.jitlogic.zico.widgets.demo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.jitlogic.zico.widgets.client.StatusBar;
import com.jitlogic.zico.widgets.demo.client.views.UserManagementPanel;

import javax.inject.Inject;

@Singleton
public class Shell extends Composite {
    interface ShellUiBinder extends UiBinder<Widget, Shell> { }
    private static ShellUiBinder ourUiBinder = GWT.create(ShellUiBinder.class);

    @UiField
    Hyperlink lnkHelp;

    @UiField
    Label lblUserInfo;

    @UiField(provided = true)
    StatusBar statusBar;

    @UiField(provided = true)
    UserManagementPanel userManagementPanel;

    private static final String MCS = "SHELL";

    @Inject
    public Shell(StatusBar statusBar, UserManagementPanel userManagementPanel) {
        this.statusBar = statusBar;
        this.userManagementPanel = userManagementPanel;
        initWidget(ourUiBinder.createAndBindUi(this));

        //statusBar.info(MCS, "Getting user information...");
    }
}

