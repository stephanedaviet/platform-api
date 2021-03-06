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
package com.codenvy.api.account.shared.dto;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * Describes new account membership
 *
 * @author Eugene Voevodin
 */
@DTO
public interface NewMembership {

    String getUserId();

    void setUserId(String id);

    NewMembership withUserId(String id);

    List<String> getRoles();

    void setRoles(List<String> roles);

    NewMembership withRoles(List<String> roles);
}
