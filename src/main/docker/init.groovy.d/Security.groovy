import hudson.markup.RawHtmlMarkupFormatter
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.Jenkins

def instance = Jenkins.instanceOrNull

// =====================================================================================================================
// Allow html markup with syntax highlighting
// =====================================================================================================================
instance.setMarkupFormatter(new RawHtmlMarkupFormatter(false))


// =====================================================================================================================
// Enable CSRF protection (see: https://wiki.jenkins.io/display/JENKINS/CSRF+Protection)
// =====================================================================================================================
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))


// =====================================================================================================================
// Create the admin user
// =====================================================================================================================

def jenkinsRealm = new HudsonPrivateSecurityRealm(false)
jenkinsRealm.createAccount('admin', 'admin')
instance.setSecurityRealm(jenkinsRealm)

// =====================================================================================================================
// Save everything
// =====================================================================================================================
instance.save()
