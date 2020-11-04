package org.wrkr.clb.chat.model.sql;

import org.wrkr.clb.common.util.chat.ChatType;

public class TaskMessage extends BaseAttachedChatMessage {

    @Override
    public String getChatType() {
        return ChatType.TASK;
    }
}
