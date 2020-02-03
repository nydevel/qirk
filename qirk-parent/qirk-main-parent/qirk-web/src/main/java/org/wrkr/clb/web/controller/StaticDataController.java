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
package org.wrkr.clb.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.wrkr.clb.common.util.strings.CharSet;

//@Controller
//@RequestMapping(path = "static-data", produces = MediaType.TEXT_HTML_VALUE + ";charset=" + CharSet.UTF8)
public class StaticDataController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(StaticDataController.class);

    private static final int CHUNKED_DOWNLOAD_CHUNK_SIZE = 2048;
    private static final String CONTENT_TYPE = MediaType.TEXT_HTML_VALUE + ";charset=" + CharSet.UTF8;

    @ExceptionHandler(FileNotFoundException.class)
    private void handleFileNotFoundException(HttpServletResponse response, FileNotFoundException e) throws IOException {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void getResourceFile(HttpServletResponse response, String resourceName) throws IOException {
        URL fileURL = getClass().getClassLoader().getResource(resourceName);
        if (fileURL == null) {
            LOG.error("Resource " + resourceName + " is not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        File file = new File(fileURL.getFile());
        try (InputStream fileStream = new FileInputStream(file);
                OutputStream responseStream = response.getOutputStream()) {

            response.addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
            response.addHeader(HttpHeaders.CONTENT_LENGTH, Long.valueOf(file.length()).toString());

            while (fileStream.available() > 0) {
                byte[] bytes = fileStream.readNBytes(CHUNKED_DOWNLOAD_CHUNK_SIZE);
                responseStream.write(bytes);
            }
        }
    }

    @GetMapping(value = "changelog")
    public void getChangelog(HttpServletResponse response) throws IOException {
        getResourceFile(response, "static-data/changelog.html");
    }

    @GetMapping(value = "license")
    public void getLicense(HttpServletResponse response) throws IOException {
        getResourceFile(response, "static-data/license.html");
    }
}
