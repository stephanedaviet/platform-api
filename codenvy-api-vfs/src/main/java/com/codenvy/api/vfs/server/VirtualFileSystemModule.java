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
package com.codenvy.api.vfs.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/** @author andrew00x */
public class VirtualFileSystemModule extends AbstractModule {
    @Override
    protected void configure() {
        // Initialize empty set of VirtualFileSystemProvider.
        Multibinder.newSetBinder(binder(), VirtualFileSystemProvider.class);
        bind(VirtualFileSystemRegistryPlugin.class);
        // Avoid writing ContentStream with common JSON writer.
        // ContentStream should be serialized with dedicated MessageBodyWriter
        Multibinder.newSetBinder(binder(), Class.class, Names.named("codenvy.json.ignored_classes"))
                   .addBinding().toInstance(ContentStream.class);
        bind(ContentStreamWriter.class);
        bind(RequestValidator.class).toProvider(Providers.<RequestValidator>of(null));
        bind(VirtualFileSystemFactory.class);
    }
}
