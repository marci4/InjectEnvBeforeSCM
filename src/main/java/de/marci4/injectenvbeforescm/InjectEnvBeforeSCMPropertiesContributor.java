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

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.model.*;

import java.io.IOException;


@Extension
public class InjectEnvBeforeSCMPropertiesContributor extends EnvironmentContributor {

    @Override
    public void buildEnvironmentFor(@NonNull Job job,@NonNull EnvVars env,@NonNull TaskListener listener) {
        // Determine the project of a matrix configuration
        if (job instanceof MatrixConfiguration) {
            job = ((MatrixConfiguration)job).getParent();
        }
        if (Utils.isEnvInjectPluginInstalled() && Utils.isInjectEnvActive(job)) {
            listener.getLogger().println("[InjectEnvBeforeSCM] - Injection active");
            try {
                Utils.getEnvVariables(job, env, listener);
            } catch (IOException | InterruptedException e) {
                // We cannot really handle any exception here, so simple drop them
            }
        }
    }
}
