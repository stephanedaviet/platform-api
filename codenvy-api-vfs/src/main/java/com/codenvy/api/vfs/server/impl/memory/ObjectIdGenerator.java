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
package com.codenvy.api.vfs.server.impl.memory;

import com.codenvy.commons.lang.NameGenerator;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class ObjectIdGenerator {
    public static String generateId() {
        return NameGenerator.generate(null, 32);
    }
}
