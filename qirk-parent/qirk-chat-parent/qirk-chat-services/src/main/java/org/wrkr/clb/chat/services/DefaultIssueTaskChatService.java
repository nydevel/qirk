package org.wrkr.clb.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.services.sql.SQLChatService;
import org.wrkr.clb.chat.services.sql.SQLIssueChatService;

@Component
public class DefaultIssueTaskChatService extends DefaultChatService implements IssueChatService {

    @Deprecated
    @Autowired(required = false)
    private SQLIssueChatService mariaDBService;

    @Deprecated
    @Override
    protected SQLChatService getSQLService() {
        return mariaDBService;
    }
}
