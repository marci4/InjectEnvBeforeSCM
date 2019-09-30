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
        return (InjectEnvBeforeSCMJobProperty) job.getProperty(InjectEnvBeforeSCMJobProperty.class) != null;
    }
    public static void getEnvVariables(Job job, EnvVars env, TaskListener listener) {
        EnvInjectJobProperty jobProperty = (EnvInjectJobProperty) job.getProperty(EnvInjectJobProperty.class);
        if (jobProperty != null && jobProperty.isOn()) {
            EnvInjectJobPropertyInfo jobPropertyInfo = jobProperty.getInfo();

            // Processes "Properties Content"
            if (jobPropertyInfo != null) {
                String propertyFilePath = jobPropertyInfo.getPropertiesFilePath();
                if (propertyFilePath != null) {
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
}
