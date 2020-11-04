package org.wrkr.clb.services.user;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ProfileDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.dto.user.PasswordChangeDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;

@Validated
public interface ProfileService {

    public EmailSentDTO resetPassword(@Valid EmailAddressDTO emailDTO) throws Exception;

    public void changePassword(User sessionUser, @Valid PasswordChangeDTO passwordDTO) throws Exception;

    public User getAccount(@Valid LoginDTO loginDTO);

    public ProfileDTO getProfile(User sessionUser) throws Exception;

    public ProfileDTO updateProfile(HttpSession session, User sessionUser, @Valid PriofileUpdateDTO profileDTO)
            throws Exception;

    public TokenAndIvDTO getNotificationToken(User sessionUser) throws Exception;
}
