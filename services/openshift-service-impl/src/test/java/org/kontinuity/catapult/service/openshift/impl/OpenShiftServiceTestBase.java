package org.kontinuity.catapult.service.openshift.impl;


import org.junit.Rule;
import org.junit.Test;
import org.kontinuity.catapult.service.openshift.api.DuplicateProjectException;
import org.kontinuity.catapult.service.openshift.api.OpenShiftProject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 * @author <a href="mailto:rmartine@redhat.com">Ricardo Martinelli de Oliveira</a>
 * @author <a href="mailto:xcoulon@redhat.com">Xavier Coulon</a>
 */
public abstract class OpenShiftServiceTestBase implements OpenShiftServiceContainer {

    private static final Logger log = Logger.getLogger(OpenShiftServiceTestBase.class.getName());

    private static final String PREFIX_NAME_PROJECT = "test-project-";

    @Rule
    public DeleteOpenShiftProjectRule deleteOpenShiftProjectRule = new DeleteOpenShiftProjectRule(this);
    
    @Test
    public void createProjectOnly() {
    	// given
        final String projectName = getUniqueProjectName();
        // when (just) creating the project
        final OpenShiftProject project = triggerCreateProject(projectName);
        // then
        final String actualName = project.getName();
        assertEquals("returned project did not have expected name", projectName, actualName);
    }

    @Test
    public void createProjectAndApplyTemplate() throws URISyntaxException {
    	// given
    	final String projectName = getUniqueProjectName();
    	// when creating the project and then applying the template
    	final OpenShiftProject project = triggerCreateProject(projectName);
    	log.log(Level.INFO, "Created project: \'" + projectName + "\'");

       //TODO Issue #135 This reliance on tnozicka has to be cleared up, introduced temporarily for testing as part of #134
    	final URI projectGitHubRepoUri = new URI("https://github.com/tnozicka/jboss-eap-quickstarts.git");
      final URI pipelineTemplateUri = new URI("https://raw.githubusercontent.com/tnozicka/jboss-eap-quickstarts/sync-WIP/helloworld/.openshift-ci_cd/pipeline-template.yaml");
      final String gitRef = "sync-WIP";

    	getOpenShiftService().configureProject(project,
              projectGitHubRepoUri,
              gitRef,
              pipelineTemplateUri);
    	// then
    	final String actualName = project.getName();
    	assertEquals("returned project did not have expected name", projectName, actualName);
		assertThat(project.getResources()).isNotNull().hasSize(1);
      assertTrue(project.getResources().get(0).getKind().equals("BuildConfig"));
    }
    
    @Test(expected = DuplicateProjectException.class)
    public void duplicateProjectNameShouldFail() {
    	// given
        final OpenShiftProject project = triggerCreateProject(getUniqueProjectName());
        // when
        final String name = project.getName();
        getOpenShiftService().createProject(name); 
        // then using same name should fail with DPE here
    }

    private String getUniqueProjectName() {
        return PREFIX_NAME_PROJECT + System.currentTimeMillis();
    }

    private OpenShiftProject triggerCreateProject(final String projectName) {
        final OpenShiftProject project = getOpenShiftService().createProject(projectName);
        log.log(Level.INFO, "Created project: \'" + projectName + "\'");
        deleteOpenShiftProjectRule.add(project);
        return project;
    }
}
