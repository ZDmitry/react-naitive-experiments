package com.rnexperimental;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class ReactNativeFile {

    static short READ   = 0;
    static short WRITE  = 1;
    static short APPEND = 2;

    private FileOutputStream _fout;
    private FileInputStream  _fin;

    private File _file;

    ReactNativeFile(String path, short mode) throws Exception {
        _file = new File(path);

        if (mode == WRITE || mode == APPEND) {
            _fout = new FileOutputStream(_file, mode == APPEND);
        } else {
            _fin = new FileInputStream(_file);
        }
    }

    public long write(String str) throws Exception  {
        return this.write(str.getBytes());
    }

    public long write(ByteBuffer bb) throws Exception  {
        return this.write(bb.array());
    }

    public long write(byte[] bytes) throws Exception  {
        _fout.write(bytes);
        return bytes.length;
    }

    public void flush() throws Exception {
        _fout.flush();
    }

    public long read(int len) throws Exception  {
        byte[] bs = new byte[len];
        return _fin.read(bs);
    }

    public long read(ByteBuffer bb) throws Exception  {
        return this.write(bb.array());
    }

    public long read(byte[] bytes) throws Exception  {
        _fout.write(bytes);
        return bytes.length;
    }

    public void close() {
        try {
            if (_fout != null) _fout.close();
            if (_fin  != null) _fin.close();
        } catch (Exception e) {
            // ...
        }
    }

}
