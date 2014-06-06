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

package com.jitlogic.zico.widgets.client;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.*;

public class SelectCell<T,V> extends AbstractInputCell<V, V> {

    interface Template extends SafeHtmlTemplates {
        @Template("<option value=\"{0}\">{1}</option>")
        SafeHtml option(String val, String desc);

        @Template("<option value=\"{0}\" selected=\"selected\">{1}</option>")
        SafeHtml selectedOption(String val, String desc);
    }

    public static final Template DEFAULT_TEMPLATE = GWT.create(Template.class);

    private Template template;

    private List<T> options = new ArrayList<T>();
    private List<V> values = new ArrayList<V>();

    public SelectCell() { this(new HashMap<T, V>(), DEFAULT_TEMPLATE); }

    public SelectCell(Map<T,V> opts) {
        this(opts, DEFAULT_TEMPLATE);
    }


    public SelectCell(Map<T,V> opts, Template template) {
        super(BrowserEvents.CHANGE);
        this.template = template;
        setOptions(opts);
    }

    public void setOptions(Map<T, V> opts) {
        for (Map.Entry<T,V> e : opts.entrySet()) {
            options.add(e.getKey());
            values.add(e.getValue());
        }
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, V value,
                               NativeEvent event, ValueUpdater<V> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        String type = event.getType();
        if (BrowserEvents.CHANGE.equals(type)) {
            Object key = context.getKey();
            SelectElement select = parent.getFirstChild().cast();
            int idx = select.getSelectedIndex();
            if (idx != -1) {
                V v = values.get(idx);
                if (v != null) {
                    setViewData(key, v);
                    finishEditing(parent, v, key, valueUpdater);
                    if (valueUpdater != null) {
                        valueUpdater.update(v);
                    }
                }
            }
        }
    }

    @Override
    public void render(Context context, V value, SafeHtmlBuilder sb) {
        Object key = context.getKey();
        V viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        V selected = viewData == null ? value : viewData;

        sb.appendHtmlConstant("<select tabindex=\"-1\">");

        for (int i = 0; i < options.size(); i++) {
            V val = values.get(i);
            T opt = options.get(i);
            if (selected != null && selected.equals(val)) {
                sb.append(template.selectedOption(""+val, ""+opt));
            } else {
                sb.append(template.option(""+val, ""+opt));
            }
        }

        sb.appendHtmlConstant("</select>");
    }
}
