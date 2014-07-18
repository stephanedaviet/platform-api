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

import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * @author andrew00x
 */
@DTO
public interface NewWorkspace {
    String getName();

    void setName(String name);

    NewWorkspace withName(String name);

    String getAccountId();

    void setAccountId(String accountId);

    NewWorkspace withAccountId(String accountId);

    List<Attribute> getAttributes();

    void setAttributes(List<Attribute> attributes);

    NewWorkspace withAttributes(List<Attribute> attributes);
}