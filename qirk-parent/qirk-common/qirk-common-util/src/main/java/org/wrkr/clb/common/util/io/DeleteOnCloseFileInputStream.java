package org.wrkr.clb.common.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteOnCloseFileInputStream extends FileInputStream {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private final File file;

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        super.close();
        try {
            file.delete();
            LOG.trace("File successfully deleted");
        } catch (Exception e) {
            LOG.error("Exception caught at input stream", e);
        }
    }
}
