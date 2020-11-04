package org.wrkr.clb.model;

public interface VersionedIdEntity {

    public Long getId();

    public void setId(Long id);

    public Long getRecordVersion();

    public void setRecordVersion(Long recordVersion);
}
