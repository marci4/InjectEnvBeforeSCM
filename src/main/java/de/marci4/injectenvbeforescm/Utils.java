/*
 * Copyright (c) 2019 marci4
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.marci4.injectenvbeforescm;

import com.mig82.folders.properties.FolderProperties;
import com.mig82.folders.properties.PropertiesLoader;
import com.mig82.folders.wrappers.ParentFolderBuildWrapper;
import hudson.EnvVars;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.jenkinsci.lib.envinject.EnvInjectLogger;
import org.jenkinsci.plugins.envinject.EnvInjectJobProperty;
import org.jenkinsci.plugins.envinject.EnvInjectJobPropertyInfo;
import org.jenkinsci.plugins.envinject.service.PropertiesVariablesRetriever;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Utils {

    public static boolean isEnvInjectPluginInstalled() {
        final Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            return false;
        }
        return jenkins.getPlugin("envinject") != null;
    }

    public static boolean isInjectEnvActive(Job job) {
        return Utils.getInjectEnvBeforeSCMJobProperty(job) != null;
    }

    public static InjectEnvBeforeSCMJobProperty getInjectEnvBeforeSCMJobProperty(Job job) {
        return (InjectEnvBeforeSCMJobProperty) job.getProperty(InjectEnvBeforeSCMJobProperty.class);
    }

    public static EnvInjectJobProperty getEnvInjectJobProperty(Job job) {
        return (EnvInjectJobProperty) job.getProperty(EnvInjectJobProperty.class);
    }

    public static boolean isInjectFolderPropertiesActive(Job job) {
        if (job instanceof FreeStyleProject) {
            FreeStyleProject freeStyleProject = (FreeStyleProject)job;
            try {
                return (ParentFolderBuildWrapper) freeStyleProject.getBuildWrappersList().get(ParentFolderBuildWrapper.class) != null;
            } catch (Exception e) {
                // Ignore
            }
        }
        return false;
    }


    public static boolean getEnvVariables(Job job, EnvVars env, TaskListener listener) throws IOException, InterruptedException {
        EnvInjectJobProperty jobProperty = getEnvInjectJobProperty(job);
        boolean propertiesLoaded = false;
        if (jobProperty != null && jobProperty.isOn()) {
            EnvInjectJobPropertyInfo jobPropertyInfo = jobProperty.getInfo();
            // Processes "Properties Content"
            if (jobPropertyInfo != null) {
                String propertyFilePath = jobPropertyInfo.getPropertiesFilePath();
                if (propertyFilePath != null && !propertyFilePath.isEmpty()) {
                    propertyFilePath = Utils.updatePropertyFilePathToOperatingSystem(job, listener, propertyFilePath);
                    EnvInjectLogger logger = new EnvInjectLogger(listener);
                    PropertiesVariablesRetriever propertiesVariablesRetriever = new PropertiesVariablesRetriever(propertyFilePath, null, env, logger);
                    Map<String, String> result = propertiesVariablesRetriever.invoke(new File(""), null);
                    if (result != null) {
                        env.putAll(result);
                    }
                    propertiesLoaded =  true;
                }
            }
        }
        if (Utils.isInjectEnvActive(job) && Utils.isInjectFolderPropertiesActive(job)) {
            listener.getLogger().println("[InjectEnvBeforeSCM] - Injecting folder properties");
            EnvVars result = PropertiesLoader.loadFolderProperties(job);
            env.putAll(result);
            propertiesLoaded = true;
        }
        return propertiesLoaded;
    }

    public static String updatePropertyFilePathToOperatingSystem(Job job, TaskListener listener, @Nonnull String propertyFilePath) {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = Utils.getInjectEnvBeforeSCMJobProperty(job);
        String result = propertyFilePath;
        String osName = System.getProperty("os.name").toLowerCase();
        if (injectEnvBeforeSCMJobProperty.hasBothPrefixes()) {
            if (osName.contains("win")) {
                if (propertyFilePath.startsWith(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix())) {
                    listener.getLogger().println("[InjectEnvBeforeSCM] - Detected linux path on a windows slave --> replacing prefix");
                    result = injectEnvBeforeSCMJobProperty.getWindowsPathPrefix() + propertyFilePath.substring(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix().length());
                }
            } else {
                // Linux
                if (propertyFilePath.startsWith(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix())) {
                    listener.getLogger().println("[InjectEnvBeforeSCM] - Detected windows path on a linux slave --> replacing prefix");
                    result = injectEnvBeforeSCMJobProperty.getLinuxPathPrefix() + propertyFilePath.substring(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix().length());
                }
            }
        }
        return Paths.get(result).toString();
    }
}
