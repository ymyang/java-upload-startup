package com.ymicloud.upload.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.Args;

public class ProgressFileBody extends FileBody{
	private ProgressListener listener;
	
	public ProgressFileBody(File file, ProgressListener listener) {
		super(file);
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new FileInputStream(getFile());
        
        long total = getContentLength();
    	long progress = 0;
    	
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
           	
                out.write(tmp, 0, l);
                
                progress += l;
                listener.update(total, progress);
                if (listener.isCanceled()) {
					break;
				}
            }
            out.flush();
        } finally {
            in.close();
        }
    }
}
