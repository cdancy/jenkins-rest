ARG jenkins_tag=2.190.3-alpine

FROM jenkins/jenkins:$jenkins_tag

COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
# the grep command allows to ignore all comments in the plugins.txt file
RUN xargs /usr/local/bin/install-plugins.sh `grep -v '^#' /usr/share/jenkins/ref/plugins.txt`

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
