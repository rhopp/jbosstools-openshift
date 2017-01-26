package org.jboss.tools.openshift.reddeer.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.eclipse.osgi.util.NLS;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.tools.openshift.core.connection.Connection;
import org.jboss.tools.openshift.reddeer.condition.OpenShiftProjectExists;
import org.jboss.tools.openshift.reddeer.requirement.OpenShiftResourceUtils;

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.project.IProjectRequest;

public class ProjectNativeUtils {

	private static Logger LOGGER = new Logger(ProjectNativeUtils.class);

	public static IProject getOrCreateProject(String name, String displayName, String description, Connection connection) {
		IProject project = OpenShiftResourceUtils.getProject(name, connection);
		if (project == null) {
			LOGGER.debug(NLS.bind("Project {0} doesnt exist yet in {1}, creating it.", name, connection.getHost()));
			project = createProject(name, displayName, description, connection);
			// new WaitUntil(
			// new ProjectExists(name, connection),
			// TimePeriod.LONG);
			/**
			 * WORKAROUND: explorer wont get notified of the the new project and
			 * therefore wont display it unless a manual refresh is done on the
			 * connection. https://issues.jboss.org/browse/JBIDE-23513 remove
			 * this wait once WatchManager is watching projects and notifies the
			 * ui.
			 * 
			 * @see WatchManager#KINDS
			 */
		}
		return project;
	}

	public static IProject createProject(String name, String displayName, String description, Connection connection) {
		assertTrue(StringUtils.isNotBlank(name));
		assertNotNull(displayName);
		assertNotNull(description);
		assertNotNull(connection);

		IProjectRequest request = connection.getResourceFactory().stub(ResourceKind.PROJECT_REQUEST, name);
		request.setDisplayName(StringUtils.isEmpty(displayName) ? name : displayName);
		request.setDescription(StringUtils.isEmpty(description) ? name : description);

		IProject createdProject = (IProject) connection.createResource(request);
		new WaitUntil(new OpenShiftProjectExists(name, connection));
		return createdProject;
	}

}
