package org.wrkr.clb.services.user.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.util.exception.ApplicationException;


@Service
public class TransactionalActivationTokenService extends DefaultPasswordActivationTokenService {

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public PasswordActivationToken create(User user) {
        return super.create(user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public User getUserAndDeleteToken(String token) throws ApplicationException {
        return super.getUserAndDeleteToken(token);
    }
}
