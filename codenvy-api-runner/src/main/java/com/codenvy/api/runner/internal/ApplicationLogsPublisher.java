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
package com.codenvy.api.runner.internal;

import com.codenvy.api.core.notification.EventService;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Publishes application's outputs to the EventService.
 *
 * @author andrew00x
 */
public class ApplicationLogsPublisher extends DelegateApplicationLogger {
    private final AtomicInteger lineCounter;
    private final EventService  eventService;
    private final long          processId;
    private final String        workspace;
    private final String        project;

    public ApplicationLogsPublisher(ApplicationLogger delegate, EventService eventService, long processId, String workspace,
                                    String project) {
        super(delegate);
        this.eventService = eventService;
        this.processId = processId;
        this.workspace = workspace;
        this.project = project;
        lineCounter = new AtomicInteger(1);
    }

    @Override
    public void writeLine(String line) throws IOException {
        if (line != null) {
            eventService.publish(RunnerEvent.messageLoggedEvent(processId, workspace, project,
                                                                new RunnerEvent.LoggedMessage(line, lineCounter.getAndIncrement())));
        }
        super.writeLine(line);
    }
}
