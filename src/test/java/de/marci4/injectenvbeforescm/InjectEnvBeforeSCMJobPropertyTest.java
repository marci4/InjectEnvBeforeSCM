package de.marci4.injectenvbeforescm;

import org.junit.Test;

import static org.junit.Assert.*;

public class InjectEnvBeforeSCMJobPropertyTest {

    @Test
    public void shouldNotContainAnyPath() {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty(null, null);
        assertFalse(injectEnvBeforeSCMJobProperty.hasLinuxPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasWindowsPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasBothPrefixes());
        assertNull(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
        assertNull(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
    }

    @Test
    public void shouldNotContainAnyPathDueToEmptyPaths() {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("", "");
        assertFalse(injectEnvBeforeSCMJobProperty.hasLinuxPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasWindowsPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasBothPrefixes());
        assertEquals("", injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
        assertEquals("", injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
    }

    @Test
    public void shouldContainLinuxPath() {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("/tmp", null);
        assertTrue(injectEnvBeforeSCMJobProperty.hasLinuxPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasWindowsPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasBothPrefixes());
        assertEquals("/tmp", injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
        assertNull(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
    }

    @Test
    public void shouldContainWindowsPath() {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty(null, "C:\\temp");
        assertFalse(injectEnvBeforeSCMJobProperty.hasLinuxPathPrefix());
        assertTrue(injectEnvBeforeSCMJobProperty.hasWindowsPathPrefix());
        assertFalse(injectEnvBeforeSCMJobProperty.hasBothPrefixes());
        assertNull(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
        assertEquals("C:\\temp", injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
    }
    @Test
    public void shouldContainLinuxAndWindowsPath() {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("/tmp", "C:\\temp");
        assertTrue(injectEnvBeforeSCMJobProperty.hasLinuxPathPrefix());
        assertTrue(injectEnvBeforeSCMJobProperty.hasWindowsPathPrefix());
        assertTrue(injectEnvBeforeSCMJobProperty.hasBothPrefixes());
        assertEquals("/tmp", injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
        assertEquals("C:\\temp", injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
    }
}
