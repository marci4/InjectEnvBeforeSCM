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

    @DataBoundConstructor
    public InjectEnvBeforeSCMJobProperty() { }

    @Override
    public JopPropertyDescriptorImpl getDescriptor() {
        return (JopPropertyDescriptorImpl) super.getDescriptor();
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
            return (InjectEnvBeforeSCMJobProperty) super.newInstance(req, formData);
        }
    }
}
