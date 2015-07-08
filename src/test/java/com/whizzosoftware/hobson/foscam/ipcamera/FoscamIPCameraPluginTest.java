/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.foscam.camera.model.FoscamCamera;
import com.whizzosoftware.hobson.api.device.MockDeviceManager;

import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.variable.MockVariableManager;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

public class FoscamIPCameraPluginTest {
    @Test
    public void testDiscovery() throws Exception {
        MockVariableManager vm = new MockVariableManager();
        MockDeviceManager dm = new MockDeviceManager();
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("id", null);
        plugin.setVariableManager(vm);
        plugin.setDeviceManager(dm);
        plugin.onStartup(new PropertyContainer());
        assertEquals(0, dm.publishedDevices.size());
        plugin.onCameraDiscovered(new FoscamCamera("cid", "camera", InetAddress.getLocalHost()));
        assertEquals(1, dm.publishedDevices.size());
    }
}
