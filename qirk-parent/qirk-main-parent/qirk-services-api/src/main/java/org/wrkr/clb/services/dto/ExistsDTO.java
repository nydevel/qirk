package org.wrkr.clb.services.dto;

public class ExistsDTO extends BaseEntityDTO {

    public boolean exists;

    public ExistsDTO(boolean exists) {
        this.exists = exists;
    }
}
