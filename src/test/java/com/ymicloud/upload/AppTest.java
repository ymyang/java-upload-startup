package com.ymicloud.upload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AppTest {

	public static void main(String[] args) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			File file = new File("d:/test.jpg");
			String url = "http://192.168.1.120/lua/upload?fc=personal";
			url += "&fn=" + file.getName();
			url += "&fs=" + file.length();
			HttpPost post = new HttpPost(url);
//			post.setHeader("Accept", "application/json;charset=UTF-8");
			post.setHeader("ct", "5qf918zi5ghpa1124a7bdad74dbfd59bd70e8e31a3f1a818f4a952c88ceb05ac7125fb8c3f160145328072279000000000000000000");
			
			HttpEntity postEntity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.setCharset(Charset.forName("UTF-8"))
//					.addBinaryBody("file", file)
					.addPart("file", new FileBody(file))
//					.addPart("param", new StringBody(Json.toJson(param), ContentType.TEXT_PLAIN))
//					.addTextBody("param", Json.toJson(param), ContentType.APPLICATION_JSON)
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
