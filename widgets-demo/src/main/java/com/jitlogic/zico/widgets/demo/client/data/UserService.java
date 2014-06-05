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

package com.jitlogic.zico.widgets.demo.client.data;

import com.google.gwt.user.client.Timer;
import com.google.inject.Singleton;
import org.fusesource.restygwt.client.MethodCallback;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class UserService {

    private List<UserInfo> users;

    @Inject
    public UserService() {
        users = new ArrayList<UserInfo>();
        users.addAll(Arrays.asList(
            mku(1, "test", "Test User", UserInfo.VIEWER, "Pastuch"),
            mku(2, "other", "Other user", UserInfo.ADMIN, "Administrajtor")
        ));
    }


    public void list(final MethodCallback<List<UserInfo>> cb) {
        final List<UserInfo> lst = new ArrayList<UserInfo>();
        for (UserInfo u : users) {
            lst.add(copy(u));
        }

        new Timer() {
            @Override
            public void run() {
                cb.onSuccess(null, lst);
            }
        }.schedule(10);
    }


    private UserInfo mku(int id, String username, String fullname, int role, String comment) {
        UserInfo u = new UserInfo();
        u.setId(id);
        u.setUsername(username);
        u.setFullname(fullname);
        u.setRole(role);
        u.setComment(comment);
        return u;
    }


    private UserInfo copy(UserInfo orig) {
        UserInfo u = new UserInfo();
        u.setId(orig.getId());
        u.setUsername(orig.getUsername());
        u.setFullname(orig.getFullname());
        u.setRole(orig.getRole());
        u.setComment(orig.getComment());
        return u;
    }
}
