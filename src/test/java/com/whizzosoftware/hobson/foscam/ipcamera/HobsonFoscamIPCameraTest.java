package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.hobson.api.config.Configuration;
import com.whizzosoftware.hobson.api.config.ConfigurationProperty;
import com.whizzosoftware.hobson.api.config.ConfigurationPropertyMetaData;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class HobsonFoscamIPCameraTest {
    @Test
    public void testURLWithoutCredentials() {
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("pluginId");
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(plugin, "id", "camera", InetAddress.getLoopbackAddress());
        assertFalse(c.hasCredentials());
        assertEquals("http://127.0.0.1/snapshot.cgi", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11", c.getVideoUrl());
    }

    @Test
    public void testURLWithCredentials() {
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("pluginId");
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(plugin, "id", "camera", InetAddress.getLoopbackAddress());
        Configuration config = new Configuration();
        config.addProperty(new ConfigurationProperty(new ConfigurationPropertyMetaData(HobsonFoscamIPCamera.CONFIG_USERNAME), "foo"));
        config.addProperty(new ConfigurationProperty(new ConfigurationPropertyMetaData(HobsonFoscamIPCamera.CONFIG_PASSWORD), "bar"));
        c.onDeviceConfigurationUpdate(config);
        assertTrue(c.hasCredentials());
        assertEquals("foo", c.getUsername());
        assertEquals("bar", c.getPassword());
        assertEquals("http://127.0.0.1/snapshot.cgi?user=foo&pwd=bar", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11&user=foo&pwd=bar", c.getVideoUrl());
    }
}
