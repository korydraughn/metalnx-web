package com.emc.metalnx.controller;

import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.extensions.dataprofiler.DataProfile;
import org.irods.jargon.extensions.dataprofiler.DataProfilerFactory;
import org.irods.jargon.extensions.dataprofiler.DataProfilerSettings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;

import com.emc.metalnx.core.domain.entity.IconObject;
import com.emc.metalnx.core.domain.exceptions.DataGridException;
import com.emc.metalnx.modelattribute.breadcrumb.DataGridBreadcrumb;
import com.emc.metalnx.services.interfaces.CollectionService;
import com.emc.metalnx.services.interfaces.ConfigService;
import com.emc.metalnx.services.interfaces.IRODSServices;
import com.emc.metalnx.services.interfaces.IconService;
import com.emc.metalnx.services.interfaces.PermissionsService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes({ "sourcePaths" })
@RequestMapping(value = "/collectionInfo")
public class CollectionInfoController {

	@Autowired
	CollectionService collectionService;

	@Autowired
	CollectionController collectionController;

	@Autowired
	PermissionsService permissionsService;

	@Autowired
	DataProfilerFactory dataProfilerFactory;

	@Autowired
	IRODSServices irodsServices;

	@Autowired
	IconService iconService;

	@Autowired
	DataProfilerSettings dataProfilerSettings;

	@Autowired
	ConfigService configService;

	private static final Logger logger = LogManager.getLogger(CollectionInfoController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String index(final Model model, HttpServletRequest request, @RequestParam("path") final String path)
			throws DataGridException, JargonException {

		logger.info("index()");
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("null or empty path");
		}

		logger.info("path:{}", path);

		IconObject icon = null;
		String mimeType = "";
		String template = "";
		
     	        String permissionType = collectionService.getPermissionsForPath(path);
		model.addAttribute("permissionType", permissionType);
		boolean access = collectionService.canUserAccessThisPath(path);
		model.addAttribute("access", access);
		logger.info("Has Access :: {}", access);
		@SuppressWarnings("rawtypes")
		DataProfile dataProfile = null;
		try {
			if (access) {

				dataProfile = collectionService.getCollectionDataProfile(path);
				if (!dataProfile.isFile())
					template = "collections/collectionInfo";
				if (dataProfile.isFile())
					template = "collections/fileInfo";

			} else {

				template = "httpErrors/noAccess";
				logger.info("returning to :{}", template);
				return template;

			}

			if (dataProfile != null && dataProfile.isFile()) {
				mimeType = dataProfile.getDataType().getMimeType();
			}

			collectionController.setCurrentPath(path);

			icon = collectionService.getIcon(mimeType);
			model.addAttribute("icon", icon);
			model.addAttribute("dataProfile", dataProfile);
			model.addAttribute("breadcrumb", new DataGridBreadcrumb(dataProfile.getAbsolutePath()));

			logger.info("returning to :{}", template);
		} catch (FileNotFoundException e) {
			logger.error("#########################");
			logger.error("collection does not exist.");
			e.printStackTrace();
			logger.error("#########################");

			template = "httpErrors/noFileOrCollection";
			return template;
		}

		return template;

	}

	@RequestMapping(value = "/collectionFileInfo/", method = RequestMethod.POST)
	public String getCollectionFileInfo(final Model model, @RequestParam("path") final String path)
			throws DataGridException {
		logger.info("CollectionInfoController getCollectionFileInfo() starts :: " + path);

		IconObject icon = null;
		String mimeType = "";

		@SuppressWarnings("rawtypes")
		String myPath = URLDecoder.decode(path);

		try {
			@SuppressWarnings("rawtypes")
			DataProfile dataProfile = collectionService.getCollectionDataProfile(myPath);
			if (dataProfile != null && dataProfile.isFile()) {
				mimeType = dataProfile.getDataType().getMimeType();
			}
			icon = collectionService.getIcon(mimeType);
			model.addAttribute("icon", icon);
			model.addAttribute("dataProfile", dataProfile);
			logger.info("getCollectionFileInfo() ends !!");

			return "collections/details :: detailsView";

		} catch (FileNotFoundException e) {
			logger.error("#########################");
			logger.error("collection does not exist.");
			e.printStackTrace();
			logger.error("#########################");
			return "httpErrors/noFileOrCollection";
		}

	}

	public DataProfilerFactory getDataProfilerFactory() {
		return dataProfilerFactory;
	}

	public void setDataProfilerFactory(DataProfilerFactory dataProfilerFactory) {
		this.dataProfilerFactory = dataProfilerFactory;
	}

	public IRODSServices getIrodsServices() {
		return irodsServices;
	}

	public void setIrodsServices(IRODSServices irodsServices) {
		this.irodsServices = irodsServices;
	}

	public DataProfilerSettings getDataProfilerSettings() {
		return dataProfilerSettings;
	}

	public void setDataProfilerSettings(DataProfilerSettings dataProfilerSettings) {
		this.dataProfilerSettings = dataProfilerSettings;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

}
