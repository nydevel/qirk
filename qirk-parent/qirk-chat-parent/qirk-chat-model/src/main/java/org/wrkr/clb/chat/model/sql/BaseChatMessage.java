/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
