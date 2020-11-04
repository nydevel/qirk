package org.wrkr.clb.chat.model.sql;

public abstract class BaseAttachedChatMessage extends BaseChatMessage {
	/**
	 * sender may not be an user, so, system or third party senders will have
	 * negative value, like -1 or so.
	 */
	public static final long SENDER_TYPE_EXAMPLE = -1L;
	private long chatId;

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

}
