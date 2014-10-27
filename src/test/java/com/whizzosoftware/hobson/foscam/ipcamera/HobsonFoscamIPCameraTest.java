package com.whizzosoftware.hobson.foscam.ipcamera;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class HobsonFoscamIPCameraTest {
    @Test
    public void testURLWithoutCredentials() {
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(null, "id", "camera", InetAddress.getLoopbackAddress());
        assertFalse(c.hasCredentials());
        assertEquals("http://127.0.0.1/snapshot.cgi", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11", c.getVideoUrl());
    }

    @Test
    public void testURLWithCredentials() {
        HobsonFoscamIPCamera c = new HobsonFoscamIPCamera(null, "id", "camera", InetAddress.getLoopbackAddress());
        Hashtable config = new Hashtable();
        config.put(HobsonFoscamIPCamera.CONFIG_USERNAME, "foo");
        config.put(HobsonFoscamIPCamera.CONFIG_PASSWORD, "bar");
        c.onDeviceConfigurationUpdate(config);
        assertTrue(c.hasCredentials());
        assertEquals("foo", c.getUsername());
        assertEquals("bar", c.getPassword());
        assertEquals("http://127.0.0.1/snapshot.cgi?user=foo&pwd=bar", c.getImageUrl());
        assertEquals("http://127.0.0.1/videostream.cgi?resolution=8&rate=11&user=foo&pwd=bar", c.getVideoUrl());
    }
}
