/*
 * Copyright 2014 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rackspace.papi.components.logging;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.filter.logic.AbstractConfiguredFilterHandlerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpLoggingSlf4jHandlerFactory extends AbstractConfiguredFilterHandlerFactory<HttpLoggingSlf4jHandler> {
    @Override
    protected Map<Class, UpdateListener<?>> getListeners() {
        return new HashMap<Class, UpdateListener<?>>();
    }

    @Override
    protected HttpLoggingSlf4jHandler buildHandler() {
        if (!this.isInitialized()) {
            return null;
        }
        return new HttpLoggingSlf4jHandler(new HttpSlf4jLoggerWrapper());
    }
}
