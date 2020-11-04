package org.wrkr.clb.services.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

@Validated
public interface UserService {

    public PublicUserDTO get(@NotNull(message = "id must not be null") Long id) throws Exception;

    public List<PublicUserDTO> listByIds(@NotNull(message = "ids must not be null") List<Long> ids) throws Exception;

    public List<PublicUserDTO> searchForProject(User currentUser,
            @NotNull(message = "searchValue must not be null") String searchValue,
            @Valid IdOrUiIdDTO excludeProjectDTO) throws Exception;

    public List<PublicUserDTO> search(
            @NotNull(message = "prefix must not be null") String prefix) throws Exception;

    @Deprecated
    public TokenAndIvDTO getDialogChatToken(User currentUser, long userId) throws Exception;
}
