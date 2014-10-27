/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.hobson.api.device.AbstractHobsonDevice;
import com.whizzosoftware.hobson.api.device.DeviceType;
import com.whizzosoftware.hobson.api.variable.HobsonVariable;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import com.whizzosoftware.hobson.bootstrap.api.config.ConfigurationMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Dictionary;

/**
 * A class representing a Foscam IP camera.
 *
 * @author Dan Noguerol
 */
public class HobsonFoscamIPCamera extends AbstractHobsonDevice {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String CONFIG_USERNAME = "username";
    public static final String CONFIG_PASSWORD = "password";

    private String username;
    private String password;
    private InetAddress address;
    private boolean varsPublished = false;

    /**
     * Constructor.
     *
     * @param plugin the HobsonPlugin that created this device
     * @param id the camera device ID
     * @param name the camera name
     * @param address the address of the camera
     */
    public HobsonFoscamIPCamera(FoscamIPCameraPlugin plugin, String id, String name, InetAddress address) {
        super(plugin, id);

        setDefaultName(name);
        this.address = address;
    }

    @Override
    public void onStartup() {
        // publish configuration metadata
        addConfigurationMetaData(new ConfigurationMetaData(CONFIG_USERNAME, "Username", "A username that can access the camera", ConfigurationMetaData.Type.STRING));
        addConfigurationMetaData(new ConfigurationMetaData(CONFIG_PASSWORD, "Password", "The password for the user", ConfigurationMetaData.Type.PASSWORD));

        // publish variables
        publishVariable(VariableConstants.IMAGE_STATUS_URL, getImageUrl(), HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.VIDEO_STATUS_URL, getVideoUrl(), HobsonVariable.Mask.READ_ONLY);

        varsPublished = true;
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public DeviceType getType() {
        return DeviceType.CAMERA;
    }

    @Override
    public String getPreferredVariableName() {
        return VariableConstants.VIDEO_STATUS_URL;
    }

    @Override
    public void onDeviceConfigurationUpdate(Dictionary config) {
        super.onDeviceConfigurationUpdate(config);

        if (config != null) {
            this.username = (String) config.get(CONFIG_USERNAME);
            this.password = (String) config.get(CONFIG_PASSWORD);

            // only fire updated URL variable notifications if variables have been published
            if (varsPublished) {
                fireVariableUpdateNotification(VariableConstants.IMAGE_STATUS_URL, getImageUrl());
                fireVariableUpdateNotification(VariableConstants.VIDEO_STATUS_URL, getVideoUrl());
            }

            logger.debug("Updated configuration with username {}", username);
        }
    }

    @Override
    public void onSetVariable(String name, Object value) {
        // there are no writeable variables
    }

    public boolean hasCredentials() {
        return (username != null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getImageUrl() {
        if (hasCredentials()) {
            return "http://" + address.getHostAddress() + "/snapshot.cgi?user=" + getUsername() + "&pwd=" + getPassword();
        } else {
            return "http://" + address.getHostAddress() + "/snapshot.cgi";
        }
    }

    public String getVideoUrl() {
        if (hasCredentials()) {
            return "http://" + address.getHostAddress() + "/videostream.cgi?resolution=8&rate=11&user=" + getUsername() + "&pwd=" + getPassword();
        } else {
            return "http://" + address.getHostAddress() + "/videostream.cgi?resolution=8&rate=11";
        }
    }
}
