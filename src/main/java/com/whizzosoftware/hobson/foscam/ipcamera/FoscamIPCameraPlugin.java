/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import java.io.IOException;
import java.util.Dictionary;

import com.whizzosoftware.hobson.api.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.whizzosoftware.foscam.camera.discovery.FoscamCameraDiscovery;
import com.whizzosoftware.foscam.camera.discovery.FoscamCameraDiscoveryListener;
import com.whizzosoftware.foscam.camera.model.FoscamCamera;
import com.whizzosoftware.hobson.api.plugin.AbstractHobsonPlugin;
import com.whizzosoftware.hobson.api.plugin.PluginStatus;


/**
 * A Hobson plugin that supports Foscam IP cameras.
 *
 * @author Dan Noguerol
 */
public class FoscamIPCameraPlugin extends AbstractHobsonPlugin implements FoscamCameraDiscoveryListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FoscamCameraDiscovery discovery;

    public FoscamIPCameraPlugin(String pluginId) {
        super(pluginId);
    }

    public FoscamIPCameraPlugin(String pluginId, FoscamCameraDiscovery discovery) {
        this(pluginId);
        this.discovery = discovery;
    }

    @Override
    public String getName() {
        return "Foscam Camera Plugin";
    }

    @Override
    public void onStartup(Configuration configuration) {
        try {
            // create Foscam camera discovery object
            if (discovery == null) {
                discovery = new FoscamCameraDiscovery(this);
                discovery.start();
                logger.debug("Camera discovery started");
            }

            // flag plugin as running
            setStatus(PluginStatus.running());
        } catch (IOException e) {
            logger.error("Error enabling camera discovery", e);
            setStatus(PluginStatus.failed(e.getLocalizedMessage()));
        }

    }

    @Override
    public void onPluginConfigurationUpdate(Configuration config) {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        logger.debug("Stopping camera discovery");
        discovery.stop();
        discovery = null;
    }

    /*
     * FoscamCameraDiscoveryListener methods
     */

    @Override
    public void onCameraDiscovered(FoscamCamera camera) {
        logger.trace("Discovered camera: " + camera.getId());
        if (!hasDevice(camera.getId())) {
            publishDevice(new HobsonFoscamIPCamera(this, camera.getId(), camera.getName(), camera.getAddress()));
            logger.debug("Added camera {}", camera.getId());
        } else {
            logger.trace("Already aware of this camera; skipping");
        }
    }
}
