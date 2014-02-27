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
