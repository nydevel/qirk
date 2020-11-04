package org.wrkr.clb.services.dto.project;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @NotNull(message = "record_version in ProjectDTO must not be null", groups = OnUpdate.class)
    public Long recordVersion;

    @NotBlank(message = "name in ProjectDTO must not be blank")
    public String name;

    @JsonProperty(value = "ui_id")
    @NotNull(message = "ui_id in ProjectDTO must not be null")
    @Pattern(regexp = RegExpPattern.SLUG + "{0,23}", message = "ui_id in ProjectDTO must be slug")
    public String uiId;

    @NotNull(message = "key in ProjectDTO must not be null")
    public String key = "";

    // @JsonProperty(value = "private") TODO turn on
    @JsonIgnore
    @NotNull(message = "private in ProjectDTO must not be null")
    public Boolean isPrivate = true;

    @NotNull(message = "description in ProjectDTO must not be null")
    @Size(max = 10000, message = "description in ProjectDTO must not be no more than 10000 characters")
    public String description;

    @JsonProperty(value = "tags")
    @NotNull(message = "tags in ProjectDTO must not be null")
    public Set<String> tagNames = new HashSet<String>();

    @JsonProperty(value = "languages")
    @NotNull(message = "languages in ProjectDTO must not be null")
    public Set<Long> languageIds = new HashSet<Long>();
}
