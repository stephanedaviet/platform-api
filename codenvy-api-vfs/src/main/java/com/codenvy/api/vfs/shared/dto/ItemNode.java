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
package com.codenvy.api.vfs.shared.dto;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
@DTO
public interface ItemNode {
    Item getItem();

    ItemNode withItem(Item item);

    void setItem(Item item);

    /**
     * Get children of item.
     *
     * @return children of item. Always return <code>null</code> for files
     */
    List<ItemNode> getChildren();

    ItemNode withChildren(List<ItemNode> children);

    void setChildren(List<ItemNode> children);
}
