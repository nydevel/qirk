package org.wrkr.clb.services.dto;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.services.dto.meta.PaginationMetaDTO;

public class PaginatedListDTO<D extends Object> {

    public List<D> data;
    public PaginationMetaDTO meta;

    public PaginatedListDTO() {
        this.data = new ArrayList<D>();
        this.meta = new PaginationMetaDTO();
    }

    public PaginatedListDTO(List<D> data, long total) {
        this.data = data;
        this.meta = new PaginationMetaDTO(total);
    }
}
