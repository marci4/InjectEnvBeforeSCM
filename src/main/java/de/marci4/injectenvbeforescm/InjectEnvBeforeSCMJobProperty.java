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

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

public class InjectEnvBeforeSCMJobProperty<T extends Job<?, ?>> extends JobProperty<T> {

    private final String linuxPathPrefix;
    private final String windowsPathPrefix;


    @DataBoundConstructor
    public InjectEnvBeforeSCMJobProperty(String linuxPathPrefix, String windowsPathPrefix) {
        this.linuxPathPrefix = linuxPathPrefix;
        this.windowsPathPrefix = windowsPathPrefix;
    }

    @Override
    public JopPropertyDescriptorImpl getDescriptor() {
        return (JopPropertyDescriptorImpl) super.getDescriptor();
    }

    public String getLinuxPathPrefix() {
        return linuxPathPrefix;
    }

    public String getWindowsPathPrefix() {
        return windowsPathPrefix;
    }


    public boolean hasWindowsPathPrefix() {
        return windowsPathPrefix != null && !windowsPathPrefix.isEmpty();
    }
    public boolean hasLinuxPathPrefix() {
        return linuxPathPrefix != null && !linuxPathPrefix.isEmpty();
    }
    public boolean hasBothPrefixes() {
        return hasWindowsPathPrefix() && hasLinuxPathPrefix();
    }

    @Extension
    public static class JopPropertyDescriptorImpl extends JobPropertyDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Inject environment variables";
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public InjectEnvBeforeSCMJobProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            if (formData.isNullObject()|| formData.isEmpty()) {
                return null;
            }
            return (InjectEnvBeforeSCMJobProperty) super.newInstance(req, formData.getJSONObject("active"));
        }
    }
}
