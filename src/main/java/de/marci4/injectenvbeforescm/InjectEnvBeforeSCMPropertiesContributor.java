package de.marci4.injectenvbeforescm;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;

import java.io.IOException;


@Extension
public class InjectEnvBeforeSCMPropertiesContributor extends EnvironmentContributor {

    @Override
    public void buildEnvironmentFor(@NonNull Job job,@NonNull EnvVars env,@NonNull TaskListener listener) throws IOException, InterruptedException {
        if (Utils.isEnvInjectPluginInstalled() && Utils.isInjectEnvActive(job)) {
            listener.getLogger().println("[InjectEnvBeforeSCM] - Injection active");
            Utils.getEnvVariables(job, env, listener);
        } else {
            listener.getLogger().println("[InjectEnvBeforeSCM] - Injection inactive");
        }
    }
}
