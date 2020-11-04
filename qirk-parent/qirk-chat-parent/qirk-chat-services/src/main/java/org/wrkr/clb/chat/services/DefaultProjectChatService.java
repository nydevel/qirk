package org.wrkr.clb.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.services.sql.SQLChatService;
import org.wrkr.clb.chat.services.sql.SQLProjectChatService;

@Component
public class DefaultProjectChatService extends DefaultChatService implements ProjectChatService {

    @Deprecated
    @Autowired(required = false)
    private SQLProjectChatService mariaDBService;

    @Deprecated
    @Override
    protected SQLChatService getSQLService() {
        return mariaDBService;
    }
}
