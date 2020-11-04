package org.wrkr.clb.services.user;

import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;

public interface RegistrationRetryWrapperService {

    public EmailSentDTO register(EmailAddressDTO emailDTO) throws Exception;

    public User activate(ActivationDTO activationDTO) throws Exception;
}
