ARG jenkins_tag=2.401.3-lts-jdk17
FROM jenkins/jenkins:$jenkins_tag

COPY --chown=jenkins:jenkins init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Plugins
COPY --chown=jenkins:jenkins plugins.yml /usr/share/jenkins/ref/plugins.yml
# the grep command allows to ignore all comments in the plugins.txt file
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.yml

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
