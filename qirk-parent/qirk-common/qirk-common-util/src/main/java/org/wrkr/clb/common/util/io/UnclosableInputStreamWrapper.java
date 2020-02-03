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
package org.wrkr.clb.common.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnclosableInputStreamWrapper extends InputStream {

    private final InputStream innerInputStream;

    public UnclosableInputStreamWrapper(InputStream innerInputStream) {
        this.innerInputStream = innerInputStream;
    }

    @Override
    public int read() throws IOException {
        return innerInputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return innerInputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return innerInputStream.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return innerInputStream.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return innerInputStream.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return innerInputStream.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return innerInputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return innerInputStream.available();
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public synchronized void mark(int readlimit) {
        innerInputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        innerInputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return innerInputStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return innerInputStream.transferTo(out);
    }

    public void closeInner() throws IOException {
        innerInputStream.close();
    }
}
