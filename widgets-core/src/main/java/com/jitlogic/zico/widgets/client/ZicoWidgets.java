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

import java.util.Map;
import java.util.TreeMap;

public class ZicoWidgets {

    public static void init() {
        WidgetResources.INSTANCE.toolBarCss().ensureInjected();
        WidgetResources.INSTANCE.formCss().ensureInjected();
    }

    public static <K,V> Map<K,V> map(Object...objs) {
        Map<K,V> map = new TreeMap<K, V>();
        for (int i = 1; i < objs.length; i += 2) {
            map.put((K)(objs[i-1]), (V)(objs[i]));
        }
        return map;
    }

}
