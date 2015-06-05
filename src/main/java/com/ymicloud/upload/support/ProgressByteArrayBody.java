package com.ymicloud.upload.support;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.ByteArrayBody;

public class ProgressByteArrayBody extends ByteArrayBody{
	private ProgressListener listener;
	private byte[] mydata;

	public ProgressByteArrayBody(byte[] data, String filename, ProgressListener listener) {
		super(data, filename);
		this.mydata = data;
		this.listener = listener;		
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
        	long total = getContentLength();
        	long progress = 0;

        	int step = 2 * 1024 * 1024;

            // 剩余的字节数
            long remain = total;
            // 每次写入的字节数
            int writeNum = 0;

            while(progress < total){
            	
            	if(remain < step){
            		writeNum = (int) remain;
            	}else{
            		writeNum = step;
            	}

                out.write(mydata, (int)progress, writeNum);

                progress += writeNum;
                
                remain = total - progress;
                
                listener.update(total, progress);
                
                if (listener.isCanceled()) {
					break;
				}
            }
            
            out.flush();

	}
	
}
