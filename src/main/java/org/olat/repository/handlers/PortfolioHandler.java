/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.repository.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.layout.MainLayoutController;
import org.olat.core.gui.control.generic.wizard.StepsMainRunController;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.Roles;
import org.olat.core.logging.AssertException;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.Util;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.coordinate.LockResult;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.fileresource.FileResourceManager;
import org.olat.fileresource.types.ResourceEvaluation;
import org.olat.portfolio.EPSecurityCallback;
import org.olat.portfolio.EPSecurityCallbackFactory;
import org.olat.portfolio.EPTemplateMapResource;
import org.olat.portfolio.manager.EPFrontendManager;
import org.olat.portfolio.manager.EPStructureManager;
import org.olat.portfolio.manager.EPXStreamHandler;
import org.olat.portfolio.model.structel.EPAbstractMap;
import org.olat.portfolio.model.structel.EPStructuredMapTemplate;
import org.olat.portfolio.model.structel.PortfolioStructure;
import org.olat.portfolio.model.structel.PortfolioStructureMap;
import org.olat.portfolio.ui.EPTemplateRuntimeController;
import org.olat.portfolio.ui.structel.EPCreateMapController;
import org.olat.portfolio.ui.structel.EPMapViewController;
import org.olat.repository.ErrorList;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryManager;
import org.olat.repository.RepositoryService;
import org.olat.repository.controllers.WizardCloseResourceController;
import org.olat.repository.ui.RepositoryEntryRuntimeController.RuntimeControllerCreator;
import org.olat.resource.OLATResource;
import org.olat.resource.references.ReferenceManager;

import de.bps.onyx.plugin.StreamMediaResource;

/**
 * 
 * Description:<br>
 * Handler wihich allow the portfolio map in repository to be opened and launched.
 * 
 * <P>
 * Initial Date:  12 oct. 2010 <br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
// Loads of parameters are unused
public class PortfolioHandler implements RepositoryHandler {
	private static final OLog log = Tracing.createLoggerFor(PortfolioHandler.class);
	
	@Override
	public boolean isCreate() {
		return true;
	}
	
	@Override
	public String getCreateLabelI18nKey() {
		return "new.portfolio";
	}
	
	@Override
	public RepositoryEntry createResource(Identity initialAuthor, String displayname, String description, Locale locale) {
		EPFrontendManager ePFMgr = CoreSpringFactory.getImpl(EPFrontendManager.class);
		EPStructureManager eSTMgr = CoreSpringFactory.getImpl(EPStructureManager.class);
		RepositoryService repositoryService = CoreSpringFactory.getImpl(RepositoryService.class);
		
		OLATResource resource = eSTMgr.createPortfolioMapTemplateResource();
		RepositoryEntry re = repositoryService.create(initialAuthor, null, "", displayname, description, resource, RepositoryEntry.ACC_OWNERS);

		PortfolioStructureMap mapTemp = eSTMgr.createAndPersistPortfolioMapTemplateFromEntry(initialAuthor, re);
		// add a page, as each map should have at least one per default!
		
		Translator pt = Util.createPackageTranslator(EPCreateMapController.class, locale);
		String pageTitle = pt.translate("new.page.title");
		String pageDescription = pt.translate("new.page.desc");
		ePFMgr.createAndPersistPortfolioPage(mapTemp, pageTitle, pageDescription);
		
		DBFactory.getInstance().commit();
		return re;
	}
	
	@Override
	public boolean isPostCreateWizardAvailable() {
		return false;
	}

	@Override
	public ResourceEvaluation acceptImport(File file, String filename) {
		return new ResourceEvaluation(false);
	}
	
	@Override
	public RepositoryEntry importResource(Identity initialAuthor, String initialAuthorAlt, String displayname, String description,
			boolean withReferences, Locale locale, File file, String filename) {
		EPFrontendManager ePFMgr = CoreSpringFactory.getImpl(EPFrontendManager.class);
		EPStructureManager eSTMgr = CoreSpringFactory.getImpl(EPStructureManager.class);
		
		PortfolioStructure structure = EPXStreamHandler.getAsObject(file, false);
		OLATResource resource = eSTMgr.createPortfolioMapTemplateResource();
		RepositoryEntry re = CoreSpringFactory.getImpl(RepositoryService.class)
				.create(initialAuthor, null, "", displayname, description, resource, RepositoryEntry.ACC_OWNERS);
		
		ePFMgr.importPortfolioMapTemplate(structure, resource);
		return re;
	}
	
	@Override
	public RepositoryEntry copy(RepositoryEntry source, RepositoryEntry target) {
		OLATResource sourceResource = source.getOlatResource();
		OLATResource targetResource = source.getOlatResource();
		
		EPFrontendManager ePFMgr = CoreSpringFactory.getImpl(EPFrontendManager.class);
		PortfolioStructure structure = ePFMgr.loadPortfolioStructure(sourceResource);
		PortfolioStructure newStructure = EPXStreamHandler.copy(structure);
		ePFMgr.importPortfolioMapTemplate(newStructure, targetResource);
		return target;
	}

	@Override
	public boolean supportsDownload() {
		return false;
	}

	@Override
	public EditionSupport supportsEdit(OLATResourceable resource) {
		return EditionSupport.embedded;
	}

	@Override
	public boolean supportsLaunch() {
		return true;
	}

	@Override
	public String archive(Identity archiveOnBehalfOf, String archivFilePath, RepositoryEntry repoEntry) {
		// Apperantly, this method is used for backing up any user related content
		// (comments etc.) on deletion. Up to now, this doesn't exist in blogs.
		return null;
	}
	
	@Override
	public VFSContainer getMediaContainer(RepositoryEntry repoEntry) {
		return FileResourceManager.getInstance()
				.getFileResourceMedia(repoEntry.getOlatResource());
	}

	@Override
	public boolean readyToDelete(OLATResourceable res, Identity identity, Roles roles, Locale locale, ErrorList errors) {
		EPFrontendManager ePFMgr = (EPFrontendManager) CoreSpringFactory.getBean("epFrontendManager");
		PortfolioStructure map = ePFMgr.loadPortfolioStructure(res);
		if(map != null) {
			//owner group has its constraints shared beetwen the repository entry and the template
			((EPAbstractMap)map).setGroups(null);
		}
		if(map instanceof EPStructuredMapTemplate) {
			EPStructuredMapTemplate exercise = (EPStructuredMapTemplate)map;
			if (ePFMgr.isTemplateInUse(exercise, null, null, null)) return false;
		}
		ReferenceManager refM = ReferenceManager.getInstance();
		String referencesSummary = refM.getReferencesToSummary(res, locale);
		if (referencesSummary != null) {
			Translator translator = Util.createPackageTranslator(RepositoryManager.class, locale);
			errors.setError(translator.translate("details.delete.error.references", new String[] { referencesSummary }));
			return false;
		}		
		return true;
	}

	@Override
	public boolean cleanupOnDelete(OLATResourceable res) {
		EPFrontendManager ePFMgr = (EPFrontendManager) CoreSpringFactory.getBean("epFrontendManager");
		ePFMgr.deletePortfolioMapTemplate(res);
		return true;
	}

	/**
	 * Transform the map in a XML file and zip it (Repository export want a zip)
	 * @see org.olat.repository.handlers.RepositoryHandler#getAsMediaResource(org.olat.core.id.OLATResourceable)
	 */
	@Override
	public MediaResource getAsMediaResource(OLATResourceable res, boolean backwardsCompatible) {
		MediaResource mr = null;

		EPFrontendManager ePFMgr = (EPFrontendManager)CoreSpringFactory.getBean("epFrontendManager");
		PortfolioStructure structure = ePFMgr.loadPortfolioStructure(res);
		try {
			InputStream inOut = EPXStreamHandler.toStream(structure);
			mr = new StreamMediaResource(inOut, null, 0l, 0l);
		} catch (IOException e) {
			log.error("Cannot export this map: " + structure, e);
		}
		return mr;
	}

	@Override
	public Controller createDetailsForm(UserRequest ureq, WindowControl wControl, OLATResourceable res) {
		return null;
	}

	@Override
	public Controller createEditorController(RepositoryEntry re, UserRequest ureq, WindowControl control, TooledStackedPanel panel) {
		return createLaunchController(re, ureq, control);
	}

	@Override
	public MainLayoutController createLaunchController(RepositoryEntry re, UserRequest ureq, WindowControl wControl) {
		return new EPTemplateRuntimeController(ureq, wControl, re, 
			new RuntimeControllerCreator() {
				@Override
				public Controller create(UserRequest uureq, WindowControl wwControl, TooledStackedPanel toolbarPanel, RepositoryEntry entry) {
					EPFrontendManager ePFMgr = CoreSpringFactory.getImpl(EPFrontendManager.class);
					PortfolioStructureMap map = (PortfolioStructureMap)ePFMgr.loadPortfolioStructure(entry.getOlatResource());
					EPSecurityCallback secCallback = EPSecurityCallbackFactory.getSecurityCallback(uureq, map, ePFMgr);
					return new EPMapViewController(uureq, wwControl, map, false, false, secCallback);
				}
			});
	}

	@Override
	public String getSupportedType() {
		return EPTemplateMapResource.TYPE_NAME;
	}

	@Override
	public LockResult acquireLock(OLATResourceable ores, Identity identity) {
		return CoordinatorManager.getInstance().getCoordinator().getLocker().acquireLock(ores, identity, "subkey");
	}

	@Override
	public void releaseLock(LockResult lockResult) {
		if(lockResult!=null) {
			CoordinatorManager.getInstance().getCoordinator().getLocker().releaseLock(lockResult);
		}
	}

	@Override
	public boolean isLocked(OLATResourceable ores) {
		return CoordinatorManager.getInstance().getCoordinator().getLocker().isLocked(ores, "subkey");
	}

	@Override
	public StepsMainRunController createWizardController(OLATResourceable res, UserRequest ureq, WindowControl wControl) {
		throw new AssertException("Trying to get wizard where no creation wizard is provided for this type.");
	}

	@Override
	public WizardCloseResourceController createCloseResourceController(UserRequest ureq, WindowControl control, RepositoryEntry repositoryEntry) {
		// No specific close wizard is implemented.
		throw new AssertException("not implemented");
	}
}
