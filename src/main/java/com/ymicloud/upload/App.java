package com.ymicloud.upload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ymicloud.upload.support.ProgressFileBody;
import com.ymicloud.upload.support.ProgressListener;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			ProgressListener listener = new ProgressListener() {
				
				@Override
				public void update(long total, long progress) {
					System.out.println("total:" + total + ", progress:" + progress);
					
				}
			};
			String url = "http://127.0.0.1:3000/upload";
			HttpPost post = new HttpPost(url);
			post.setHeader("Accept", "application/json;charset=UTF-8");

			File file = new File("d:/mind/RabbitMQ.png");
			
			FileUploadParam param = new FileUploadParam();
			param.setFileType("ent-file");
			param.setFolderId(13L);

			HttpEntity postEntity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.setCharset(Charset.forName("UTF-8"))
//					.addBinaryBody("file", file)
					.addPart("file", new ProgressFileBody(file, listener))
					.addTextBody("param", Json.toJson(param), ContentType.APPLICATION_JSON)
					.build();
			if (postEntity != null) {
				post.setEntity(postEntity);
			}
			post.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build());
			CloseableHttpResponse response = httpclient.execute(post);
			try {
				String r = EntityUtils.toString(response.getEntity(), "UTF-8");
				System.out.println(r);
			} finally {
				response.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (Exception e) {
			}
		}
    }
}
