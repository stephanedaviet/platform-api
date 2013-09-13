/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.api.core.rest;

/**
 * Thrown if cannot access remote API resource or when got a response from remote API that we don't understand.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
@SuppressWarnings("serial")
public class UnknownRemoteException extends RuntimeException {
    public UnknownRemoteException(String message) {
        super(message);
    }

    public UnknownRemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownRemoteException(Throwable cause) {
        super(cause);
    }
}
