/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.whizzosoftware.foscam.camera.discovery.CameraDiscoveryListener;
import com.whizzosoftware.hobson.api.device.DeviceContext;
import com.whizzosoftware.hobson.api.device.HobsonDevice;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.whizzosoftware.foscam.camera.discovery.FoscamCameraDiscovery;
import com.whizzosoftware.hobson.api.plugin.AbstractHobsonPlugin;
import com.whizzosoftware.hobson.api.plugin.PluginStatus;


/**
 * A Hobson plugin that supports Foscam IP cameras.
 *
 * @author Dan Noguerol
 */
public class FoscamIPCameraPlugin extends AbstractHobsonPlugin implements CameraDiscoveryListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FoscamCameraDiscovery discovery;
    private List<String> publishedCameraIds = new ArrayList<>();

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
    public void onStartup(PropertyContainer configuration) {
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
    public void onPluginConfigurationUpdate(PropertyContainer config) {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        logger.debug("Stopping camera discovery");
        discovery.stop();
        discovery = null;
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return null;
    }

    /*
     * CameraDiscoveryListener methods
     */

    @Override
    public void onCameraDiscovered(String cameraId, String cameraName, InetAddress address) {
        logger.trace("Discovered camera: " + cameraId);
        if (publishedCameraIds.contains(cameraId)) {
            HobsonDevice device = getDevice(DeviceContext.create(getContext(), cameraId));
            device.getRuntime().setDeviceAvailability(true, System.currentTimeMillis());
        } else {
            publishDevice(new HobsonFoscamIPCamera(this, cameraId, cameraName, address));
            logger.debug("Added camera {}", cameraId);
            publishedCameraIds.add(cameraId);
        }
    }
}
