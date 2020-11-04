package org.wrkr.clb.services.dto.meta;

public class PaginationMetaDTO {

    public long total = 0L;

    public PaginationMetaDTO() {
    }

    public PaginationMetaDTO(long total) {
        this.total = total;
    }
}
