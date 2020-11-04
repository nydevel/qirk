package org.wrkr.clb.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wrkr.clb.common.util.strings.CharSet;

@Controller
@RequestMapping(path = "heartbeat")
public class HeartbeatController {

    private static final String HEARTBEAT_STRING = "OK";

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=" + CharSet.UTF8)
    public @ResponseBody String heartbeat(@SuppressWarnings("unused") HttpServletRequest request) {
        return HEARTBEAT_STRING;
    }
}
