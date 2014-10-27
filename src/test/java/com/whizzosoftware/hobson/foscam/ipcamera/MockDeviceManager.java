package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.hobson.api.device.HobsonDevice;
import com.whizzosoftware.hobson.api.device.manager.DeviceManager;
import com.whizzosoftware.hobson.api.plugin.HobsonPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MockDeviceManager implements DeviceManager {
    public List<HobsonDevice> publishedDevices = new ArrayList<>();

    @Override
    public void publishDevice(HobsonPlugin plugin, HobsonDevice device) {
        publishedDevices.add(device);
    }

    @Override
    public Collection<HobsonDevice> getAllDevices() {
        return null;
    }

    @Override
    public Collection<HobsonDevice> getAllPluginDevices(String pluginId) {
        return null;
    }

    @Override
    public HobsonDevice getDevice(String pluginId, String deviceId) {
        return null;
    }

    @Override
    public boolean hasDevice(String pluginId, String deviceId) {
        return false;
    }

    @Override
    public void setDeviceName(String pluginId, String deviceId, String name) {

    }

    @Override
    public void unpublishDevice(HobsonPlugin plugin, String deviceId) {

    }

    @Override
    public void unpublishAllDevices(HobsonPlugin plugin) {

    }
}
