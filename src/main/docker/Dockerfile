# api calls 403 error with jenkins 2.176.2+/2.186+ because of security changes: https://jenkins.io/security/advisory/2019-07-17/#SECURITY-626
#ARG jenkins_tag=2.190.1-alpine
# api calls succeed
ARG jenkins_tag=2.176.1-alpine

FROM jenkins/jenkins:$jenkins_tag

COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
# the grep command allows to ignore all comments in the plugins.txt file
RUN xargs /usr/local/bin/install-plugins.sh `grep -v '^#' /usr/share/jenkins/ref/plugins.txt`

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false