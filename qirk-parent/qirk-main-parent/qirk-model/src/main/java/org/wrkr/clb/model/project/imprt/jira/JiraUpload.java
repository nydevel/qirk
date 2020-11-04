package org.wrkr.clb.model.project.imprt.jira;

public class JiraUpload {

    private Long uploadTimestamp;
    private String archiveFilename;

    public Long getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public String getArchiveFilename() {
        return archiveFilename;
    }

    public void setArchiveFilename(String archiveFilename) {
        this.archiveFilename = archiveFilename;
    }
}
