package org.wrkr.clb.repo.sort;

public class SortingOption {
    
    public enum Order {
        ASC("ASC"),
        DESC("DESC");
        
        @SuppressWarnings("unused")
        private final String order;

        Order(final String order) {
            this.order = order;
        }
    }

    public enum ForTask {
        ASSIGNEE("ASSIGNEE"),
        CREATED_AT("CREATED_AT"),
        NUMBER("NUMBER"),
        PRIORITY("PRIORITY"),
        REPORTER("REPORTER"),
        SUMMARY("SUMMARY"),
        UPDATED_AT("UPDATED_AT");
        
        @SuppressWarnings("unused")
        private final String field;

        ForTask(final String field) {
            this.field = field;
        }
    }
}
