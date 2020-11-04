package org.wrkr.clb.model.project.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "task_priority")
public class TaskPriority extends BaseIdEntity {

    public static final Priority DEFAULT = Priority.MAJOR;
    private static final Set<Priority> DEPRECATED = Collections.unmodifiableSet(new HashSet<Priority>(Arrays.asList(
            Priority.TRIVIAL, Priority.BLOCKING)));

    public static enum Priority {
        TRIVIAL("TRIVIAL", "Trivial"),
        MINOR("MINOR", "Minor"),
        MAJOR("MAJOR", "Major'"),
        CRITICAL("CRITICAL", "Critical"),
        BLOCKING("BLOCKING", "Blocking");

        private final String nameCode;
        private final String humanReadable;

        Priority(final String nameCode, final String humanReadable) {
            this.nameCode = nameCode;
            this.humanReadable = humanReadable;
        }

        public boolean isDeprecated() {
            return DEPRECATED.contains(this);
        }

        public String getValue() {
            return nameCode;
        }

        public String toHumanReadable() {
            return humanReadable;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "name_code", nullable = false, unique = true)
    @JsonProperty(value = "name_code")
    private Priority nameCode;

    @Column(name = "importance", nullable = false, unique = true)
    @JsonIgnore
    private Integer importance;

    public Priority getNameCode() {
        return nameCode;
    }

    public void setNameCode(Priority nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    @JsonIgnore
    public boolean isDeprecated() {
        return nameCode.isDeprecated();
    }
}
