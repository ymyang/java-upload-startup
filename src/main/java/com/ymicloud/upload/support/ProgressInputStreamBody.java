package com.ymicloud.upload.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.InputStreamBody;

class ProgressInputStreamBody extends InputStreamBody {

	private ProgressListener listener;
	private long contentLength = -1;

	public ProgressInputStreamBody(byte[] data, String filename, ProgressListener listener) {
		super(new ByteArrayInputStream(data), filename);
		this.listener = listener;
		this.contentLength = data.length;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
        final InputStream in = getInputStream();
        try {
        	long total = contentLength;
        	long progress = 0;

            final byte[] tmp = new byte[16 * 1024];
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
