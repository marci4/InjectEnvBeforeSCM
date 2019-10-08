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

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.jenkinsci.lib.envinject.EnvInjectLogger;
import org.jenkinsci.plugins.envinject.EnvInjectJobProperty;
import org.jenkinsci.plugins.envinject.EnvInjectJobPropertyInfo;
import org.jenkinsci.plugins.envinject.service.PropertiesVariablesRetriever;

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
    public static void getEnvVariables(Job job, EnvVars env, TaskListener listener) {
        EnvInjectJobProperty jobProperty = (EnvInjectJobProperty) job.getProperty(EnvInjectJobProperty.class);
        if (jobProperty != null && jobProperty.isOn()) {
            EnvInjectJobPropertyInfo jobPropertyInfo = jobProperty.getInfo();

            // Processes "Properties Content"
            if (jobPropertyInfo != null) {
                String propertyFilePath = jobPropertyInfo.getPropertiesFilePath();
                if (propertyFilePath != null) {
                    propertyFilePath = Utils.updatePathToOs(job, listener, propertyFilePath);
                    EnvInjectLogger logger = new EnvInjectLogger(listener);
                    PropertiesVariablesRetriever propertiesVariablesRetriever = new PropertiesVariablesRetriever(propertyFilePath, null, env, logger);
                    try {
                        Map<String, String> result = propertiesVariablesRetriever.invoke(new File(""), null);
                        if (result != null) {
                            env.putAll(result);
                        }
                    } catch (IOException | InterruptedException e) {
                    }
                }
            }
        }
    }

    private static String updatePathToOs(Job job, TaskListener listener, String propertyFilePath) {
        InjectEnvBeforeSCMJobProperty injectEnvBeforeSCMJobProperty = Utils.getInjectEnvBeforeSCMJobProperty(job);
        String result = propertyFilePath;
        String osName = System.getProperty("os.name").toLowerCase();
        if (injectEnvBeforeSCMJobProperty.hasBothPrefixes()) {
            if (osName.contains("win")) {
                if (propertyFilePath.startsWith(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix())) {
                    listener.getLogger().println("[InjectEnvBeforeSCM] - Detected linux path on a windows slave --> replacing prefix");
                    result = propertyFilePath.replaceFirst(injectEnvBeforeSCMJobProperty.getLinuxPathPrefix(), injectEnvBeforeSCMJobProperty.getWindowsPathPrefix());
                }
            } else {
                // Linux
                if (propertyFilePath.startsWith(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix())) {
                    listener.getLogger().println("[InjectEnvBeforeSCM] - Detected windows path on a linux slave --> replacing prefix");
                    result = propertyFilePath.replaceFirst(injectEnvBeforeSCMJobProperty.getWindowsPathPrefix(), injectEnvBeforeSCMJobProperty.getLinuxPathPrefix());
                }
            }
        }
        return Paths.get(result).toString();
    }
}
