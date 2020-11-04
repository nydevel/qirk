package org.wrkr.clb.chat.services;

import java.util.List;

import org.wrkr.clb.chat.services.dto.MessageDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;

public interface ChatService {

    public List<MessageDTO> getLastMessages(ChatTokenData tokenData, Long timestamp);

    public MessageDTO saveMessage(ChatTokenData tokenData, String message) throws Exception;
}
