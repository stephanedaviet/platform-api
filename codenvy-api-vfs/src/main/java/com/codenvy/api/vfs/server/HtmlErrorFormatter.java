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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Factory of WebApplicationException that contains error message in HTML format.
 *
 * @author andrew00x
 */
public final class HtmlErrorFormatter {
    public static void sendErrorAsHTML(Exception e) {
        // GWT framework (used on client side) requires result in HTML format if use HTML forms.
        throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage()), MediaType.TEXT_HTML).build());
    }

    private static String formatAsHtml(String message) {
        return String.format("<pre>message: %s</pre>", message);
    }

    private HtmlErrorFormatter() {
    }
}
