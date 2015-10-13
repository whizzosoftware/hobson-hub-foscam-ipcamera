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
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.api.variable.HobsonVariable;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import com.whizzosoftware.hobson.api.variable.VariableProxyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

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
    public void onStartup(PropertyContainer config) {
        super.onStartup(config);

        readConfig(config);

        // publish variables
        publishVariable(VariableConstants.IMAGE_STATUS_URL, getImageUrl(), HobsonVariable.Mask.READ_ONLY, VariableProxyType.MEDIA_URL);
        publishVariable(VariableConstants.VIDEO_STATUS_URL, getVideoUrl(), HobsonVariable.Mask.READ_ONLY, VariableProxyType.MEDIA_URL);

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
    public Long getLastCheckIn() {
        // the camera is not proactively checked unless an image is requested; so we always claim its active
        // TODO: change this to be based on a proactive ping
        return System.currentTimeMillis();
    }

    @Override
    public String getPreferredVariableName() {
        return VariableConstants.IMAGE_STATUS_URL;
    }

    @Override
    public void onDeviceConfigurationUpdate(PropertyContainer config) {
        super.onDeviceConfigurationUpdate(config);

        if (readConfig(config)) {
            // only fire updated URL variable notifications if variables have been published
            if (varsPublished) {
                fireVariableUpdateNotification(VariableConstants.IMAGE_STATUS_URL, getImageUrl());
                fireVariableUpdateNotification(VariableConstants.VIDEO_STATUS_URL, getVideoUrl());
            }

            logger.debug("Updated configuration with username {}", username);
        }
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return new TypedProperty[] {
            new TypedProperty.Builder(CONFIG_USERNAME, "Username", "A username that can access the camera", TypedProperty.Type.STRING).build(),
            new TypedProperty.Builder(CONFIG_PASSWORD, "Password", "The password for the user", TypedProperty.Type.SECURE_STRING).build()
        };
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

    protected boolean readConfig(PropertyContainer config) {
        if (config != null) {
            String username = (String)config.getPropertyValue(CONFIG_USERNAME);
            String password = (String)config.getPropertyValue(CONFIG_PASSWORD);

            if ((username == null && this.username != null) || (username != null && !username.equals(this.username)) || (password == null && this.password != null) || (password != null && !password.equals(this.password))) {
                this.username = username;
                this.password = password;
                return true;
            }
        }
        return false;
    }
}
