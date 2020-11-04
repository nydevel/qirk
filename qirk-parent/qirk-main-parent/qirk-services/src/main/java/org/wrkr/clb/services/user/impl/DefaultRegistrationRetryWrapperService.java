package org.wrkr.clb.services.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jdbc.transaction.Executor;
import org.wrkr.clb.common.jdbc.transaction.RetryOnCannotAcquireLock;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.user.RegistrationRetryWrapperService;
import org.wrkr.clb.services.user.RegistrationService;

@Service
public class DefaultRegistrationRetryWrapperService implements RegistrationRetryWrapperService {

    @Autowired
    private RegistrationService registrationService;

    @Override
    public EmailSentDTO register(EmailAddressDTO emailDTO) throws Exception {
        return RetryOnCannotAcquireLock.<EmailSentDTO>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public EmailSentDTO exec(int retryNumber) throws Exception {
                return registrationService.register(emailDTO);
            }
        });
    }

    @Override
    public User activate(ActivationDTO activationDTO) throws Exception {
        return RetryOnCannotAcquireLock.<User>exec(new Executor() {
            @SuppressWarnings({ "unchecked", "unused" })
            @Override
            public User exec(int retryNumber) throws Exception {
                return registrationService.activate(activationDTO);
            }
        });
    }
}
