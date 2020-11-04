package org.wrkr.clb.chat.repo.sql;

import java.sql.SQLException;

import org.wrkr.clb.chat.model.sql.BaseChatMessage;

public abstract class BaseChatMessageRepo extends BaseChatRepo {

    protected abstract String getSqlSelectMessagesStatement();

    protected abstract String getSqlInsertStatement();

    protected abstract String getSqlDeleteStatement();

    protected abstract String getSqlSelectChatsStatement();

    public abstract void save(BaseChatMessage message) throws SQLException;

}
