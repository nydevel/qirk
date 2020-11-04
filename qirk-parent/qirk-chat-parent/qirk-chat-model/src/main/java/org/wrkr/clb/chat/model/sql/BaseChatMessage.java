package org.wrkr.clb.chat.model.sql;

public abstract class BaseChatMessage {

	private String uuid;
	private long senderId;
	private long timestamp;
	private String message;

	private Boolean deleted;
	private Long updatedAt;
	private Long inResponseTo;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getInResponseTo() {
		return inResponseTo;
	}

	public void setInResponseTo(Long inResponseTo) {
		this.inResponseTo = inResponseTo;
	}

	public abstract String getChatType();
}
