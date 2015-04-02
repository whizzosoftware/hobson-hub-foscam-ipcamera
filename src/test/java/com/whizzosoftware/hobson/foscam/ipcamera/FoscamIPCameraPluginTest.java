/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.foscam.camera.model.FoscamCamera;
import com.whizzosoftware.hobson.api.config.Configuration;
import com.whizzosoftware.hobson.api.device.MockDeviceManager;

import org.junit.Test;
import static org.junit.Assert.*;

public class FoscamIPCameraPluginTest {
    @Test
    public void testDiscovery() {
        MockDeviceManager dm = new MockDeviceManager();
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("id", null);
        plugin.setDeviceManager(dm);
        plugin.onStartup(new Configuration());
        assertEquals(0, dm.publishedDevices.size());
        plugin.onCameraDiscovered(new FoscamCamera("cid", "camera", null));
        assertEquals(1, dm.publishedDevices.size());
    }
}
