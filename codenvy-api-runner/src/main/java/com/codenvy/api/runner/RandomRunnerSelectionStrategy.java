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
package com.codenvy.api.runner;

import javax.inject.Singleton;
import java.util.List;
import java.util.Random;

/**
 * Select random runner to launch application.
 *
 * @author Sergii Kabashniuk
 */
@Singleton
public class RandomRunnerSelectionStrategy implements RunnerSelectionStrategy {
    private final Random random = new Random();

    @Override
    public RemoteRunner select(List<RemoteRunner> remoteRunners) {
        if (remoteRunners.size() == 1) {
            return remoteRunners.get(0);
        }
        return remoteRunners.get(random.nextInt(remoteRunners.size()));
    }
}
