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
import org.apache.http.entity.mime.content.StringBody;
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
			String url = "http://192.168.1.120/lua/upload";
			HttpPost post = new HttpPost(url);
//			post.setHeader("Accept", "application/json;charset=UTF-8");
			post.setHeader("ct", "g1p70c1xuybdf334fe4f891e4dc1a787ee8ac159d1244be05ee192c297d1b803bf64906299f10143882794599000000000000000000");

			File file = new File("d:/test.jpg");
			
			FileUploadParam param = new FileUploadParam();
			param.setFileCategory("group");
			param.setGroupId(1101L);
			param.setParentId(1106L);
			param.setFileSize(file.length());

			HttpEntity postEntity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.setCharset(Charset.forName("UTF-8"))
//					.addBinaryBody("file", file)
					.addPart("file", new ProgressFileBody(file, listener))
//					.addPart("param", new StringBody(Json.toJson(param), ContentType.TEXT_PLAIN))
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
