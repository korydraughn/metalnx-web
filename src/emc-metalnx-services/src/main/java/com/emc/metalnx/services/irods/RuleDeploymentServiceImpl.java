/*
 * Copyright (c) 2015-2017, Dell EMC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.emc.metalnx.services.irods;

import com.emc.metalnx.core.domain.exceptions.DataGridException;
import com.emc.metalnx.services.interfaces.*;
import org.apache.commons.io.FilenameUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.Stream2StreamAO;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@Transactional
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.INTERFACES)
public class RuleDeploymentServiceImpl implements RuleDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(RuleDeploymentServiceImpl.class);

    private static final int BUFFER_SIZE = 4 * 1024 * 1024;
    private static final String RULE_CACHE_DIR_NAME = ".rulecache";

    @Autowired
    private IRODSServices irodsServices;

    @Autowired
    private FileOperationService fos;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RuleService ruleService;

    @Override
    public void deployRule(MultipartFile file) throws DataGridException {
        logger.info("Deploying rule");

        if (file == null) {
            logger.error("File could not be sent to the data grid. Rule file is null.");
            throw new DataGridException("Rule file is null.");
        }

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            logger.error("Could not get input stream from rule file: ", e.getMessage());
            throw new DataGridException("Could not get input stream from ruleFile.");
        }

        // Getting DataObjectAO in order to create the new rule file
        IRODSFileFactory irodsFileFactory = irodsServices.getIRODSFileFactory();
        Stream2StreamAO stream2StreamA0 = irodsServices.getStream2StreamAO();
        IRODSFile targetFile = null;

        try {
            String ruleCacheDirPath = String.format("/%s/%s", configService.getIrodsZone(), RULE_CACHE_DIR_NAME);
            String ruleName = file.getOriginalFilename().isEmpty() ? file.getName() : file.getOriginalFilename();
            targetFile = irodsFileFactory.instanceIRODSFile(ruleCacheDirPath, ruleName);

            stream2StreamA0.transferStreamToFileUsingIOStreams(inputStream, (File) targetFile, 0, BUFFER_SIZE);

            String resourceName = irodsServices.getDefaultStorageResource();
            Resource resc = irodsServices.getResourceAO().findByName(resourceName);
            String vaultPath = resc.getVaultPath();
            String host = resc.getLocation();

            String ruleVaultPath = String.format("%s/%s/%s", vaultPath, RULE_CACHE_DIR_NAME, ruleName);

            String ruleNameWithoutExtension = FilenameUtils.removeExtension(ruleName);
            ruleService.execDeploymentRule(host, ruleNameWithoutExtension, ruleVaultPath);
        } catch (JargonException e) {
            if (targetFile != null) fos.deleteDataObject(targetFile.getPath(), true);
            logger.error("Upload stream failed from Metalnx to the data grid. {}", e.getMessage());
            throw new DataGridException("Upload failed. Resource(s) might be full.");
        } finally {
            try {
                inputStream.close(); // Closing streams opened
            } catch (IOException e) {
                logger.error("Could close stream: ", e.getMessage());
            }
        }
    }
}