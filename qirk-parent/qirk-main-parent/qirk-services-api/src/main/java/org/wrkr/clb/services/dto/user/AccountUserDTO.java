package org.wrkr.clb.services.dto.user;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountUserDTO extends PublicUserDTO {

    @JsonProperty("email")
    public String emailAddress;

    public Boolean manager;

    public static AccountUserDTO fromEntity(User user) {
        AccountUserDTO dto = new AccountUserDTO();

        if (user != null) {
            dto.id = user.getId();
            dto.username = user.getUsername();
            dto.fullName = user.getFullName();
            dto.emailAddress = user.getEmailAddress();
            dto.manager = user.isManager();
        }

        return dto;
    }

    public static List<AccountUserDTO> fromEntitiesWithEmail(List<User> userList) {
        List<AccountUserDTO> dtoList = new ArrayList<AccountUserDTO>(userList.size());
        for (User user : userList) {
            dtoList.add(fromEntity(user));
        }
        return dtoList;
    }
}
