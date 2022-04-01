package de.marci4.injectenvbeforescm;

import hudson.EnvVars;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.envinject.EnvInjectJobProperty;
import org.jenkinsci.plugins.envinject.EnvInjectJobPropertyInfo;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.junit.Assert.*;

public class UtilsTest {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldContainInjectEnvProperty() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty(null, null);
        job.addProperty(injectEnvBeforeSCMJobProperty);

        InjectEnvBeforeSCMJobProperty injectEnvProperty = Utils.getInjectEnvBeforeSCMJobProperty(job);
        assertNotNull(injectEnvProperty);
        assertTrue(Utils.isInjectEnvActive(job));
    }
    @Test
    public void shouldNotContainInjectEnvProperty() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();

        InjectEnvBeforeSCMJobProperty injectEnvProperty = Utils.getInjectEnvBeforeSCMJobProperty(job);
        assertNull(injectEnvProperty);
        assertFalse(Utils.isInjectEnvActive(job));
    }

    @Test
    public void shouldContainEnvInjectJobProperty() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        job.addProperty(envInjectJobProperty);

        EnvInjectJobProperty envInjectJobPropertyOfJob = Utils.getEnvInjectJobProperty(job);
        assertNotNull(envInjectJobPropertyOfJob);
    }

    @Test
    public void shouldNotEnvInjectJobProperty() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();

        EnvInjectJobProperty envInjectJobProperty = Utils.getEnvInjectJobProperty(job);
        assertNull(envInjectJobProperty);
    }
    @Test
    public void shouldNotUpdatePropertiesFilePathSinceNoPrefixesAreProvided0() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty(null, null);
        job.addProperty(injectEnvBeforeSCMJobProperty);

        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "C:\\temp.properties";
        assertEquals(Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath), propertiesFilePath);
    }
    @Test
    public void shouldNotUpdatePropertiesFilePathSinceNoPrefixesAreProvided1() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("", "");
        job.addProperty(injectEnvBeforeSCMJobProperty);

        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "C:\\temp.properties";
        String osName = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        assertEquals(propertiesFilePath, updatedPropertiesPath);
    }
    @Test
    public void shouldNotUpdatePropertiesFilePathSinceNoPrefixesAreProvided2() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("", "C:\\temp");
        job.addProperty(injectEnvBeforeSCMJobProperty);

        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "C:\\temp.properties";
        String osName = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        assertEquals(propertiesFilePath, updatedPropertiesPath);
    }

    @Test
    public void shouldUpdatePropertiesFilePathFromLinuxToWindows0() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("/tmp/helga", "C:\\Users");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        String osName = System.getProperty("os.name");
        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "/tmp/helga/temp.properties";
        System.setProperty("os.name", "Windows10");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        if (osName.toLowerCase().contains("win")) {
            assertEquals("C:\\Users\\temp.properties", updatedPropertiesPath);
        } else {
            assertEquals("C:\\Users/temp.properties", updatedPropertiesPath);
        }
    }
    @Test
    public void shouldUpdatePropertiesFilePathFromLinuxToWindows1() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("/tmp/helga/", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        String osName = System.getProperty("os.name");
        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "/tmp/helga/temp.properties";
        System.setProperty("os.name", "Windows10");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        assertEquals("C:\\Users\\temp.properties", updatedPropertiesPath);
    }

    @Test
    public void shouldUpdatePropertiesFilePathFromWindowsToLinux0() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga", "C:\\Users");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        String osName = System.getProperty("os.name");
        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "C:\\Users\\temp.properties";
        System.setProperty("os.name", "Linux");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        assertEquals("\\tmp\\helga\\temp.properties", updatedPropertiesPath);
    }

    @Test
    public void shouldUpdatePropertiesFilePathFromWindowsToLinux1() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        String osName = System.getProperty("os.name");
        TaskListener listener = jenkins.createTaskListener();
        String propertiesFilePath = "C:\\Users\\temp.properties";
        System.setProperty("os.name", "Linux");
        String updatedPropertiesPath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertiesFilePath);
        System.setProperty("os.name", osName);
        assertEquals("\\tmp\\helga\\temp.properties", updatedPropertiesPath);
    }

    @Test
    public void shouldNotRunDueToMissingEnvInject() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        assertFalse(Utils.getEnvVariables(job, vars, listener));
    }

    @Test
    public void shouldNotRunDueToDisabledEnvInject() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(false);
        job.addProperty(envInjectJobProperty);
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        assertFalse(Utils.getEnvVariables(job, vars, listener));
    }
    @Test
    public void shouldNotRunDueToMissingEnvInjectJobPropertyInfo() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(true);

        job.addProperty(envInjectJobProperty);
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        assertFalse(Utils.getEnvVariables(job, vars, listener));
    }

    @Test
    public void shouldNotRunDueToNullEnvInjectPropertiesFile() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(true);
        EnvInjectJobPropertyInfo envInjectJobPropertyInfo = new EnvInjectJobPropertyInfo(null, "", "", "", "", true);
        envInjectJobProperty.setInfo(envInjectJobPropertyInfo);
        job.addProperty(envInjectJobProperty);
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        assertFalse(Utils.getEnvVariables(job, vars, listener));
    }
    @Test
    public void shouldNotRunDueToEmptyEnvInjectPropertiesFile() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(true);
        EnvInjectJobPropertyInfo envInjectJobPropertyInfo = new EnvInjectJobPropertyInfo("", "", "", "", "", true);
        envInjectJobProperty.setInfo(envInjectJobPropertyInfo);
        job.addProperty(envInjectJobProperty);
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        assertFalse(Utils.getEnvVariables(job, vars, listener));
    }

    @Test
    public void shouldTryToInsertEnviromentVariables() throws Exception {
        FreeStyleProject job = jenkins.createFreeStyleProject();
        TaskListener listener = jenkins.createTaskListener();
        EnvVars vars = new EnvVars();
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(true);
        EnvInjectJobPropertyInfo envInjectJobPropertyInfo = new EnvInjectJobPropertyInfo("C:\\temp", "", "", "", "", true);
        envInjectJobProperty.setInfo(envInjectJobPropertyInfo);
        job.addProperty(envInjectJobProperty);
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = new InjectEnvBeforeSCMJobProperty("\\tmp\\helga\\", "C:\\Users\\");
        job.addProperty(injectEnvBeforeSCMJobProperty);
        Utils.getEnvVariables(job, vars, listener);
    }

}
