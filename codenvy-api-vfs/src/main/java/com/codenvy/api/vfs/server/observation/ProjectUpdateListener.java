/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.vfs.server.observation;

import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.dto.server.DtoFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listens updates of any project items and save time of last update.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class ProjectUpdateListener implements EventListener {
    private final String projectId;

    public ProjectUpdateListener(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
        final List<Property> properties = new ArrayList<>(1);
        final Property updateTimeProperty = DtoFactory.getInstance().createDto(Property.class);
        updateTimeProperty.setName("vfs:lastUpdateTime");
        updateTimeProperty.setValue(Collections.singletonList(Long.toString(System.currentTimeMillis())));
        properties.add(updateTimeProperty);
        event.getVirtualFileSystem().updateItem(projectId, properties, null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectUpdateListener)) {
            return false;
        }
        ProjectUpdateListener other = (ProjectUpdateListener)o;
        return projectId.equals(other.projectId);
    }

    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 31 * hash + projectId.hashCode();
        return hash;
    }
}
