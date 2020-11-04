package org.wrkr.clb.chat.repo.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage;
import org.wrkr.clb.chat.model.sql.BaseAttachedChatMessage_;
import org.wrkr.clb.chat.model.sql.BaseChatMessage;
import org.wrkr.clb.chat.model.sql.BaseChatMessage_;

public abstract class BaseAttachedChatMessageRepo extends BaseChatMessageRepo {

    public List<BaseChatMessage> listTopSinceTimestampByChatId(long chatId, long timestamp, int limit) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectMessagesStatement(),
                chatId, timestamp, limit);

        List<BaseChatMessage> messageList = new ArrayList<BaseChatMessage>(limit);
        for (Map<String, Object> row : rows) {
            BaseAttachedChatMessage message = createMessageInstance();
            message.setChatId(chatId);
            message.setSenderId((Long) row.get(BaseAttachedChatMessage_.senderId));
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }

        return messageList;
    }

    @Override
    public void save(BaseChatMessage message) {
        BaseAttachedChatMessage attachedChatMessage = (BaseAttachedChatMessage) message;
        getJdbcTemplate().update(getSqlInsertStatement(), attachedChatMessage.getUuid(),
                attachedChatMessage.getChatId(), attachedChatMessage.getSenderId(), attachedChatMessage.getTimestamp(),
                attachedChatMessage.getMessage());
    }

    public void deleteByChatIdAndTimestamp(long chatId, long timestamp) {
        getJdbcTemplate().update(getSqlDeleteStatement(), chatId, timestamp);
    }

    // TODO
    @Deprecated
    @SuppressWarnings("unused")
    public List<BaseAttachedChatMessage> getChatList(List<Long> chatIds) {
        List<BaseAttachedChatMessage> messageList = new ArrayList<BaseAttachedChatMessage>();
        // @formatter:off
        /*List<Map<String, Object>> rows = getJdbcTemplate().queryForList(getSqlSelectChatsStatement(),
                StringUtils.join(chatIds, ", "));
        for (Map<String, Object> row : rows) {
            BaseAttachedChatMessage message = createMessageInstance();
            message.setChatId((Long) row.get(BaseAttachedChatMessage_.chatId));
            message.setSenderId((Long) row.get(BaseAttachedChatMessage_.senderId));
            message.setTimestamp((Long) row.get(BaseChatMessage_.timestamp));
            message.setMessage((String) row.get(BaseChatMessage_.message));
            messageList.add(message);
        }*/
        // @formatter:on
        return messageList;
    }

    protected abstract BaseAttachedChatMessage createMessageInstance();

}
