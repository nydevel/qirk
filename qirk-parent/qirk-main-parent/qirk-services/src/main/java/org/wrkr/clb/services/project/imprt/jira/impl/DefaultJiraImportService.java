/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.services.project.imprt.jira.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wrkr.clb.common.util.io.UnclosableInputStreamWrapper;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;
import org.wrkr.clb.model.project.imprt.jira.JiraUpload;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.imprt.jira.JiraUploadRepo;
import org.wrkr.clb.repo.project.task.TaskPriorityRepo;
import org.wrkr.clb.repo.project.task.TaskStatusRepo;
import org.wrkr.clb.repo.project.task.TaskTypeRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.QirkDataDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraIdAndNameDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectMatchDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraUploadDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraUserDTO;
import org.wrkr.clb.services.project.imprt.jira.ImportedJiraProjectService;
import org.wrkr.clb.services.project.imprt.jira.JiraImportService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.dom.DOMUtils;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;

@Validated
@Service
public class DefaultJiraImportService implements JiraImportService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJiraImportService.class);

    private static final String YANDEXCLOUD_FILE_PATH_PREFIX = "import/jira/raw/";

    private static final String ENTITIES_FILENAME = "entities.xml";

    private static final char DEFAULT_UPLOAD_FOLDER_DELIMETER = '/';
    private static final List<Character> UPLOAD_FOLDER_DELIMETERS = Collections.unmodifiableList(Arrays.asList(
            DEFAULT_UPLOAD_FOLDER_DELIMETER, '\\'));

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String ENTITIES_FILE_NOT_FOUND = "ENTITIES_FILE_NOT_FOUND";
        public static final String INCORRECT_ARCHIVE_STRUCTURE = "INCORRECT_ARCHIVE_STRUCTURE";
    }

    @Autowired
    private YandexCloudApiService yandexCloudService;

    @Autowired
    private ImportedJiraProjectService importedProjectService;

    @Autowired
    private JiraUploadRepo jiraUploadRepo;

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private JDBCProjectRepo projectRepo;

    @Autowired
    private TaskTypeRepo typeRepo;

    @Autowired
    private TaskPriorityRepo priorityRepo;

    @Autowired
    private TaskStatusRepo statusRepo;

    @Autowired
    private ProjectSecurityService securityService;

    private String generateUploadFolderPath(long timestamp) {
        return YANDEXCLOUD_FILE_PATH_PREFIX + timestamp + "/";
    }

    public String generateFilePath(long timestamp, String filename) {
        return generateUploadFolderPath(timestamp) + filename;
    }

    @Override
    public JiraUploadDTO uploadJiraImportFile(User currentUser, FileItem file) throws Exception {
        // security
        securityService.authzCanImportProjects(currentUser);
        // security

        long now = System.currentTimeMillis();

        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
                UnclosableInputStreamWrapper zipInputStreamWrapper = new UnclosableInputStreamWrapper(zipInputStream)) {

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    yandexCloudService.upload(generateFilePath(now, zipEntry.getName()), zipInputStreamWrapper);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        JiraUpload upload = new JiraUpload();
        upload.setUploadTimestamp(now);
        upload.setArchiveFilename(file.getName());
        jiraUploadRepo.save(upload);

        return new JiraUploadDTO(now, upload.getArchiveFilename(), new ArrayList<ImportedJiraProject>());
    }

    @Override
    public List<JiraUploadDTO> listUploads(User currentUser) throws ApplicationException {
        // security
        securityService.authzCanImportProjects(currentUser);
        // security

        List<String> folderPaths = yandexCloudService.listFolders(YANDEXCLOUD_FILE_PATH_PREFIX);

        Map<Long, List<ImportedJiraProject>> timestampToImportedProject = importedProjectService
                .mapTimestampToImportedProject();
        Map<Long, String> timestampToArchiveName = jiraUploadRepo.mapTimestampToArchiveFilename();

        List<JiraUploadDTO> dtoList = new ArrayList<JiraUploadDTO>(folderPaths.size());
        List<ImportedJiraProject> emptyImportedProjectList = new ArrayList<ImportedJiraProject>();
        for (String folderPath : folderPaths) {
            String timestampString = folderPath.substring(YANDEXCLOUD_FILE_PATH_PREFIX.length(), folderPath.length() - 1);
            Long timestamp;
            try {
                timestamp = Long.valueOf(timestampString);
            } catch (NumberFormatException e) {
                LOG.error("Could not convert folder name to timestamp", e);
                continue;
            }
            dtoList.add(new JiraUploadDTO(timestamp, timestampToArchiveName.get(timestamp),
                    timestampToImportedProject.getOrDefault(timestamp, emptyImportedProjectList)));
        }
        return dtoList;
    }

    private Document getProjectDOM(long timestamp) throws Exception {
        InputStream entitiesFile = yandexCloudService.getFileOrThrowApplicationException(
                generateFilePath(timestamp, ENTITIES_FILENAME),
                JsonStatusCode.ENTITIES_FILE_NOT_FOUND,
                "There was no upload at specified timestamp or the archive structure is incorrect.");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(entitiesFile);
        entitiesFile.close();
        return document;
    }

    @Override
    public List<JiraProjectDTO> listProjects(User currentUser, long timestamp) throws Exception {
        // security
        securityService.authzCanImportProjects(currentUser);
        // security

        Document document = getProjectDOM(timestamp);

        NodeList nodes = DOMUtils.getNodes(document, "/entity-engine-xml/Project");
        return JiraProjectDTO.fromDOMNodeList(nodes);
    }

    private Set<String> addJiraIdsToSet(Document document, String filterXPathExpression, Set<String> ids)
            throws XPathExpressionException {
        NodeList idNodes = DOMUtils.getNodes(document, filterXPathExpression);
        for (int i = 0; i < idNodes.getLength(); i++) {
            ids.add(idNodes.item(i).getNodeValue());
        }
        return ids;
    }

    private List<JiraIdAndNameDTO> getJiraEntities(NodeList entityNodes, Set<String> ids) {
        List<JiraIdAndNameDTO> dtoList = new ArrayList<JiraIdAndNameDTO>(entityNodes.getLength());
        for (int i = 0; i < entityNodes.getLength(); i++) {
            Element entityElement = (Element) entityNodes.item(i);
            if (ids.contains(entityElement.getAttribute("id"))) {
                dtoList.add(JiraIdAndNameDTO.fromDOMElement(entityElement));
            }
        }
        return dtoList;
    }

    @Override
    public JiraProjectMatchDTO listProjectsData(User currentUser, long timestamp, Set<String> projectIds) throws Exception {
        // security
        securityService.authzCanImportProjects(currentUser);
        // security

        Document document = getProjectDOM(timestamp);

        NodeList projectNodes = DOMUtils.getNodes(document, "/entity-engine-xml/Project");
        NodeList userNodes = DOMUtils.getNodes(document, "/entity-engine-xml/User");
        NodeList typeNodes = DOMUtils.getNodes(document, "/entity-engine-xml/IssueType");
        NodeList priorityNodes = DOMUtils.getNodes(document, "/entity-engine-xml/Priority");
        NodeList statusNodes = DOMUtils.getNodes(document, "/entity-engine-xml/Status");

        List<JiraProjectDTO> projects = new ArrayList<JiraProjectDTO>(projectNodes.getLength());
        Set<String> usernames = new HashSet<String>();
        Set<String> typeIds = new HashSet<String>();
        Set<String> priorityIds = new HashSet<String>();
        Set<String> statusIds = new HashSet<String>();

        for (int i = 0; i < projectNodes.getLength(); i++) {
            Element projectElement = (Element) projectNodes.item(i);
            String projectId = projectElement.getAttribute("id");

            if (projectIds.contains(projectElement.getAttribute(JiraProjectDTO.ID))) {
                projects.add(JiraProjectDTO.fromDOMElement(projectElement));

                usernames = addJiraIdsToSet(document,
                        "/entity-engine-xml/Issue[@project=" + projectId + "]/@reporter|" +
                                "/entity-engine-xml/Issue[@project=" + projectId + "]/@assignee",
                        usernames);
                typeIds = addJiraIdsToSet(document, "/entity-engine-xml/Issue[@project=" + projectId + "]/@type", typeIds);
                priorityIds = addJiraIdsToSet(
                        document, "/entity-engine-xml/Issue[@project=" + projectId + "]/@priority", priorityIds);
                statusIds = addJiraIdsToSet(document, "/entity-engine-xml/Issue[@project=" + projectId + "]/@status", statusIds);
            }
        }

        List<JiraUserDTO> users = new ArrayList<JiraUserDTO>(userNodes.getLength());
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element userElement = (Element) userNodes.item(i);
            if (usernames.contains(userElement.getAttribute(JiraUserDTO.USERNAME))) {
                users.add(JiraUserDTO.fromDOMElement(userElement));
            }
        }

        List<JiraIdAndNameDTO> types = getJiraEntities(typeNodes, typeIds);
        List<JiraIdAndNameDTO> priorities = getJiraEntities(priorityNodes, priorityIds);
        List<JiraIdAndNameDTO> statuses = getJiraEntities(statusNodes, statusIds);

        List<Project> projectList = projectRepo.listImportedFromJira();
        List<User> userList = userRepo.list();
        return new JiraProjectMatchDTO(projects, users, types, priorities, statuses,
                QirkDataDTO.fromEntities(projectList, userList));
    }

    private <A extends Object> Map<String, A> mapJiraIdToTaskAttributeMap(
            Map<String, String> jiraIdToQirkNameCode, Map<String, A> nameCodeToTaskAttribute) throws ApplicationException {
        Map<String, A> jiraIdToTaskAttribute = new HashMap<String, A>(jiraIdToQirkNameCode.size());
        for (String jiraId : jiraIdToQirkNameCode.keySet()) {
            A taskAttribute = nameCodeToTaskAttribute.get(jiraIdToQirkNameCode.get(jiraId));
            if (taskAttribute == null) {
                throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
            }
            jiraIdToTaskAttribute.put(jiraId, taskAttribute);
        }
        return jiraIdToTaskAttribute;
    }

    private char getUploadFolderDelimeter(String uploadFolderPath) throws ApplicationException {
        boolean noInnerFolders = true;

        for (char delimeter : UPLOAD_FOLDER_DELIMETERS) {
            List<String> folders = yandexCloudService.listFolders(uploadFolderPath, delimeter);
            if (folders.contains(uploadFolderPath + "data" + delimeter)) {
                return delimeter;
            }

            if (!folders.isEmpty()) {
                noInnerFolders = false;
            }
        }

        if (noInnerFolders) {
            return DEFAULT_UPLOAD_FOLDER_DELIMETER;
        }
        throw new BadRequestException(JsonStatusCode.INCORRECT_ARCHIVE_STRUCTURE, "Archive structure is incorrect.");
    }

    private ImportStatusDTO getFailedStatusDTO(ImportStatusDTO.Status status, String jiraProjectId, Exception e) {
        String errorCode = JsonStatusCode.INTERNAL_SERVER_ERROR;
        if (e instanceof ApplicationException) {
            errorCode = ((ApplicationException) e).getJsonStatusCode();
        }
        return new ImportStatusDTO(jiraProjectId, status, errorCode);
    }

    private ImportStatusDTO importProject(User importingUser,
            Document entitiesDoc, Element projectElement, JiraProjectImportDTO importDTO,
            String uploadFolderPath, char uploadFolderDelimeter) throws Exception {
        String jiraProjectId = projectElement.getAttribute(JiraProjectDTO.ID);
        Long projectId = importDTO.jiraProjectIdToQirkProjectId.get(jiraProjectId);

        ImportedJiraProject importedProject = new ImportedJiraProject();

        importedProject.setUploadTimestamp(importDTO.timestamp);
        importedProject.setProjectId(projectId);
        importedProject.setJiraProjectId(Long.valueOf(jiraProjectId));
        importedProject.setJiraProjectKey(ExtStringUtils.substring(projectElement.getAttribute(JiraProjectDTO.KEY), 10));
        importedProject.setJiraProjectName(ExtStringUtils.substring(projectElement.getAttribute(JiraProjectDTO.NAME), 80));

        if (projectId == null) {
            try {
                importedProjectService.importNewProject(importingUser, entitiesDoc, importedProject, importDTO,
                        uploadFolderPath, uploadFolderDelimeter);
                return new ImportStatusDTO(jiraProjectId, ImportStatusDTO.Status.CREATED);
            } catch (Exception e) {
                LOG.error("Could not import project from jira", e);
                return getFailedStatusDTO(ImportStatusDTO.Status.CREATE_FAILED, jiraProjectId, e);
            }

        } else {
            try {
                return importedProjectService.importProjectUpdate(importingUser, entitiesDoc, importedProject, importDTO,
                        uploadFolderPath, uploadFolderDelimeter);
            } catch (Exception e) {
                LOG.error("Could not update project from jira", e);
                return getFailedStatusDTO(ImportStatusDTO.Status.UPDATE_FAILED, jiraProjectId, e);
            }
        }
    }

    @Override
    public List<ImportStatusDTO> importProjects(User currentUser, JiraProjectImportDTO importDTO) throws Exception {
        // security
        securityService.authzCanImportProjects(currentUser);
        // security

        Document document = getProjectDOM(importDTO.timestamp);

        NodeList projectNodes = DOMUtils.getNodes(document, "/entity-engine-xml/Project");

        importDTO.jiraTypeIdToQirkType = mapJiraIdToTaskAttributeMap(
                importDTO.jiraTypeIdToQirkTypeNameCode, typeRepo.mapNameCodeToType());
        importDTO.jiraPriorityIdToQirkPriority = mapJiraIdToTaskAttributeMap(
                importDTO.jiraPriorityIdToQirkPriorityNameCode, priorityRepo.mapNameCodeToPriority());
        importDTO.jiraStatusIdToQirkStatus = mapJiraIdToTaskAttributeMap(
                importDTO.jiraStatusIdToQirkStatusNameCode, statusRepo.mapNameCodeToStatus());

        importDTO.defaultType = typeRepo.getByNameCode(TaskType.DEFAULT);
        importDTO.defaultPriority = priorityRepo.getByNameCode(TaskPriority.DEFAULT);
        importDTO.defaultStatus = statusRepo.getByNameCode(TaskStatus.DEFAULT);

        List<User> users = userRepo.listEmailsByIds(importDTO.jiraUserNameToQirkOrgUserId.values());
        Map<Long, User> idToUser = new HashMap<Long, User>();
        for (User user : users) {
            idToUser.put(user.getId(), user);
        }
        importDTO.jiraUserNameToQirkUser = new HashMap<String, User>();
        for (String jiraUserName : importDTO.jiraUserNameToQirkUser.keySet()) {
            User user = idToUser.get(importDTO.jiraUserNameToQirkOrgUserId.get(jiraUserName));
            if (user != null) {
                importDTO.jiraUserNameToQirkUser.put(jiraUserName.toLowerCase(), user);
            }
        }

        NodeList userNodes = DOMUtils.getNodes(document, "/entity-engine-xml/User");
        importDTO.jiraUserNameToJiraUserId = new HashMap<String, String>();
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element userElement = (Element) userNodes.item(i);
            importDTO.jiraUserNameToJiraUserId.put(
                    userElement.getAttribute(JiraUserDTO.USERNAME), userElement.getAttribute(JiraUserDTO.ID));
        }

        String uploadFolderPath = generateUploadFolderPath(importDTO.timestamp);
        char uploadFolderDelimeter = getUploadFolderDelimeter(uploadFolderPath);

        List<ImportStatusDTO> result = new ArrayList<ImportStatusDTO>();
        for (int i = 0; i < projectNodes.getLength(); i++) {
            Element projectElement = (Element) projectNodes.item(i);
            String jiraProjectId = projectElement.getAttribute(JiraProjectDTO.ID);
            if (importDTO.projectIds.contains(jiraProjectId)) {
                result.add(
                        importProject(currentUser, document, projectElement,
                                importDTO, uploadFolderPath, uploadFolderDelimeter));
            }
        }
        return result;
    }
}
