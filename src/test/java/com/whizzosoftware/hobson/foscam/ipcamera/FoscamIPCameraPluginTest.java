/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.hobson.api.device.DeviceContext;
import com.whizzosoftware.hobson.api.device.MockDeviceManager;

import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.variable.MockVariableManager;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import org.junit.Test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

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
        plugin.onCameraDiscovered("cid", "camera", InetAddress.getLocalHost());
        assertEquals(1, dm.publishedDevices.size());
    }

    @Test
    public void testConfigurationStartup() throws Exception {
        MockVariableManager vm = new MockVariableManager();
        MockDeviceManager dm = new MockDeviceManager();
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("id", null);
        plugin.setVariableManager(vm);
        plugin.setDeviceManager(dm);

        HobsonFoscamIPCamera camera = new HobsonFoscamIPCamera(plugin, "cid", "camera", InetAddress.getLocalHost());
        Map<String,Object> values = new HashMap<>();
        values.put(HobsonFoscamIPCamera.CONFIG_USERNAME, "foo");
        values.put(HobsonFoscamIPCamera.CONFIG_PASSWORD, "bar");
        PropertyContainer pc = new PropertyContainer(null, values);

        camera.onStartup(pc);

        assertEquals(2, vm.getPublishedDeviceVariables().size());
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.IMAGE_STATUS_URL).getValue().toString().endsWith("/snapshot.cgi?user=foo&pwd=bar"));
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.VIDEO_STATUS_URL).getValue().toString().endsWith("/videostream.cgi?resolution=8&rate=11&user=foo&pwd=bar"));
    }

    @Test
    public void testConfigurationChangeWithEmptyStartup() throws Exception {
        MockVariableManager vm = new MockVariableManager();
        MockDeviceManager dm = new MockDeviceManager();
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("id", null);
        plugin.setVariableManager(vm);
        plugin.setDeviceManager(dm);

        HobsonFoscamIPCamera camera = new HobsonFoscamIPCamera(plugin, "cid", "camera", InetAddress.getLocalHost());
        PropertyContainer pc = new PropertyContainer();

        camera.onStartup(pc);

        assertEquals(2, vm.getPublishedDeviceVariables().size());
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.IMAGE_STATUS_URL).getValue().toString().endsWith("/snapshot.cgi"));
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.VIDEO_STATUS_URL).getValue().toString().endsWith("/videostream.cgi?resolution=8&rate=11"));

        Map<String,Object> values = new HashMap<>();
        values.put(HobsonFoscamIPCamera.CONFIG_USERNAME, "foo1");
        values.put(HobsonFoscamIPCamera.CONFIG_PASSWORD, "bar1");
        pc = new PropertyContainer(null, values);

        // simulate user setting username/password configuration fields
        assertEquals(0, vm.getVariableUpdates().size());
        camera.onDeviceConfigurationUpdate(pc);
        assertEquals(2, vm.getVariableUpdates().size());

        assertTrue(vm.getVariableUpdates().get(0).getValue().toString().endsWith("/snapshot.cgi?user=foo1&pwd=bar1"));
        assertTrue(vm.getVariableUpdates().get(1).getValue().toString().endsWith("/videostream.cgi?resolution=8&rate=11&user=foo1&pwd=bar1"));
    }

    @Test
    public void testConfigurationStartupWithEmptyUpdate() throws Exception {
        MockVariableManager vm = new MockVariableManager();
        MockDeviceManager dm = new MockDeviceManager();
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("id", null);
        plugin.setVariableManager(vm);
        plugin.setDeviceManager(dm);

        HobsonFoscamIPCamera camera = new HobsonFoscamIPCamera(plugin, "cid", "camera", InetAddress.getLocalHost());
        Map<String,Object> values = new HashMap<>();
        values.put(HobsonFoscamIPCamera.CONFIG_USERNAME, "foo");
        values.put(HobsonFoscamIPCamera.CONFIG_PASSWORD, "bar");
        PropertyContainer pc = new PropertyContainer(null, values);

        camera.onStartup(pc);

        assertEquals(2, vm.getPublishedDeviceVariables().size());
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.IMAGE_STATUS_URL).getValue().toString().endsWith("/snapshot.cgi?user=foo&pwd=bar"));
        assertTrue(vm.getPublishedDeviceVariable(DeviceContext.createLocal("id", "cid"), VariableConstants.VIDEO_STATUS_URL).getValue().toString().endsWith("/videostream.cgi?resolution=8&rate=11&user=foo&pwd=bar"));

        // simulate user clearing username/password configuration fields
        pc = new PropertyContainer();

        assertEquals(0, vm.getVariableUpdates().size());
        camera.onDeviceConfigurationUpdate(pc);
        assertEquals(2, vm.getVariableUpdates().size());

        assertTrue(vm.getVariableUpdates().get(0).getValue().toString().endsWith("/snapshot.cgi"));
        assertTrue(vm.getVariableUpdates().get(1).getValue().toString().endsWith("/videostream.cgi?resolution=8&rate=11"));
    }
}
