/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.api.workspace.shared.dto;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * @author Eugene Voevodin
 */
@DTO
public interface WorkspaceReference {

    String getId();

    void setId(String id);

    WorkspaceReference withId(String id);

    String getName();

    void setName(String name);

    WorkspaceReference withName(String name);

    boolean isTemporary();

    void setTemporary(boolean isTemporary);

    WorkspaceReference withTemporary(boolean isTemporary);

    List<Link> getLinks();

    void setLinks(List<Link> links);

    WorkspaceReference withLinks(List<Link> links);
}
