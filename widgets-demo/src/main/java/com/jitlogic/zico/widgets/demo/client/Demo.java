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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.jitlogic.zico.widgets.client.StatusBar;
import com.jitlogic.zico.widgets.client.WidgetResources;
import com.jitlogic.zico.widgets.client.ZicoDataGridResources;

public class Demo implements EntryPoint {

    static {
        ZicoDataGridResources.INSTANCE.dataGridStyle().ensureInjected();
        WidgetResources.INSTANCE.formCss().ensureInjected();
    }

    private Shell shell;

    public void onModuleLoad() {
        shell = new Shell(new StatusBar());
        RootLayoutPanel.get().add(shell);
    }
}
