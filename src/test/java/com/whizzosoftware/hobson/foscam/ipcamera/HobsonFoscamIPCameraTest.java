package com.whizzosoftware.hobson.foscam.ipcamera;

import com.whizzosoftware.hobson.api.property.PropertyContainer;
import org.junit.Test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HobsonFoscamIPCameraTest {
    @Test
    public void testURLWithoutCredentials() {
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("foo");
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(plugin, "id", "camera", InetAddress.getLoopbackAddress());
        assertFalse(c.hasCredentials());
        assertEquals("http://127.0.0.1/snapshot.cgi", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11", c.getVideoUrl());
    }

    @Test
    public void testURLWithCredentials() {
        FoscamIPCameraPlugin plugin = new FoscamIPCameraPlugin("foo");
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(plugin, "id", "camera", InetAddress.getLoopbackAddress());
        PropertyContainer config = new PropertyContainer();
        Map<String,Object> values = new HashMap<>();
        values.put(HobsonFoscamIPCamera.CONFIG_USERNAME, "foo");
        values.put(HobsonFoscamIPCamera.CONFIG_PASSWORD, "bar");
        config.setPropertyValues(values);
        c.onDeviceConfigurationUpdate(config);
        assertTrue(c.hasCredentials());
        assertEquals("foo", c.getUsername());
        assertEquals("bar", c.getPassword());
        assertEquals("http://127.0.0.1/snapshot.cgi?user=foo&pwd=bar", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11&user=foo&pwd=bar", c.getVideoUrl());
    }
}
