package org.wrkr.clb.chat.services.jms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.wrkr.clb.chat.services.jms.MQDestination;

public class ProjectChatListener extends ChatListener {

    private Map<Long, ConcurrentHashMap<String, MQDestination>> chatIdToSessionIdsToControllers = new ConcurrentHashMap<Long, ConcurrentHashMap<String, MQDestination>>();

    @Override
    protected Map<Long, ConcurrentHashMap<String, MQDestination>> getChatIdToSessionIdsToControllers() {
        return chatIdToSessionIdsToControllers;
    }
}
