# InjectEnvBeforeSCM

Small jenkins plugin to inject the enviroment variables provided by the EnvInject properties file path before an SCM pull.

This plugin requires a EnvInject properties file path which is not part of the workspace (and therefore is a path available on the master).