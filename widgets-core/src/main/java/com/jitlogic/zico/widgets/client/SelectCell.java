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

    public static interface SelectAdapter<T,V> {

        String description(T o);

        V value(T o);
    }


    public static class MapAdapter<T,V> implements SelectAdapter<T,V> {

        private Map<T,V> options;

        public MapAdapter(Map<T,V> options) {
            this.options = options;
        }


        @Override
        public String description(T o) {
            return o.toString();
        }

        @Override
        public V value(T obj) {
            return options.get(obj);
        }
    }


    private Template template;

    private List<T> options = new ArrayList<T>();
    private List<V> values = new ArrayList<V>();

    private SelectAdapter<T,V> adapter;

    public SelectCell(Map<T,V> options) {
        this(new MapAdapter<T, V>(options));

        List<T> opts = new ArrayList<T>();

        for (Map.Entry<T,V> e : options.entrySet()) {
            opts.add(e.getKey());
        }

        setOptions(opts);
    }


    public SelectCell(SelectAdapter<T,V> adapter) {
        this(adapter, DEFAULT_TEMPLATE);
    }


    public SelectCell(SelectAdapter<T,V> adapter, Template template) {
        super(BrowserEvents.CHANGE);
        this.adapter = adapter;
        this.template = template;
    }


    public void setOptions(List<T> options) {
        this.options.clear();
        this.options.addAll(options);

        this.values.clear();

        for (T option : options) {
            this.values.add(adapter.value(option));
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
            String val = ""+values.get(i);
            String desc = adapter.description(options.get(i));
            if (selected != null && selected.equals(val)) {
                sb.append(template.selectedOption(val, desc));
            } else {
                sb.append(template.option(val, desc));
            }
        }

        sb.appendHtmlConstant("</select>");
    }
}
