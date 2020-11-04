package org.wrkr.clb.chat.repo.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage_;
import org.wrkr.clb.chat.model.sql.BaseChatMessage_;
import org.wrkr.clb.chat.model.sql.IssueMessage;
import org.wrkr.clb.chat.model.sql.IssueMessage_;


@Repository
public class SQLIssueMessageRepo extends BaseAttachedChatMessageRepo {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SQLIssueMessageRepo.class);

    private static final String SQL_SELECT_MESSAGES_STATEMENT = String.format(
            "SELECT %s, %s, %s FROM %s WHERE %s = ? AND %s < ? ORDER BY %s DESC LIMIT ?;",
            BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp, BaseChatMessage_.message, // select columns
            IssueMessage_.TABLE_NAME,
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp, // filter columns
            BaseChatMessage_.timestamp); // order by

    private static final String SQL_INSERT_STATEMENT = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?);",
            IssueMessage_.TABLE_NAME,
            BaseChatMessage_.uuid, BaseAttachedChatMessage_.chatId, BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp, BaseChatMessage_.message);

    private static final String SQL_DELETE_STATEMENT = String.format(
            "DELETE FROM %s WHERE %s = ? AND %s = ?;",
            IssueMessage_.TABLE_NAME,
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp);

    private static final String SQL_SELECT_CHATS_STATEMENT = String.format(
            "SELECT %s.%s, %s, %s, %s FROM "
                    + "(SELECT %s, MAX(%s) as last_timestamp FROM %s WHERE %s in (?) GROUP BY %s) AS last_message "
                    + "JOIN %s ON last_message.%s = %s.%s AND last_message.last_timestamp = %s.%s ORDER BY last_timestamp DESC;",
            IssueMessage_.TABLE_NAME, BaseAttachedChatMessage_.chatId, BaseAttachedChatMessage_.senderId, BaseChatMessage_.timestamp,
            BaseChatMessage_.message, // select columns
            BaseAttachedChatMessage_.chatId, BaseChatMessage_.timestamp, // subquery select columns
            IssueMessage_.TABLE_NAME, // subquery table name
            BaseAttachedChatMessage_.chatId, // subquery where clause
            BaseAttachedChatMessage_.chatId, // subquery group by clause
            IssueMessage_.TABLE_NAME, // join table name
            BaseAttachedChatMessage_.chatId, IssueMessage_.TABLE_NAME, BaseAttachedChatMessage_.chatId, // join clause
            IssueMessage_.TABLE_NAME, BaseChatMessage_.timestamp); // join clause

    @Override
    protected BaseAttachedChatMessage createMessageInstance() {
        return new IssueMessage();
    }

    @Override
    protected String getSqlSelectMessagesStatement() {
        return SQL_SELECT_MESSAGES_STATEMENT;
    }

    @Override
    protected String getSqlInsertStatement() {
        return SQL_INSERT_STATEMENT;
    }

    @Override
    protected String getSqlDeleteStatement() {
        return SQL_DELETE_STATEMENT;
    }

    @Override
    protected String getSqlSelectChatsStatement() {
        return SQL_SELECT_CHATS_STATEMENT;
    }

}
