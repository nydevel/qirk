package org.wrkr.clb.chat.model.sql;

import org.wrkr.clb.common.util.chat.ChatType;

public class DialogMessage extends BaseChatMessage {

    private long user1Id;
    private long user2Id;

    public long getUser1Id() {
        return user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }

    public void setUser1Id(long id) {
        this.user1Id = id;
    }

    public void setUser2Id(long id) {
        this.user2Id = id;
    }

    @Override
    public String getChatType() {
        return ChatType.DIALOG;
    }
}
