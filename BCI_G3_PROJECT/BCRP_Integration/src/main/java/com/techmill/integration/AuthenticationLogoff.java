package com.techmill.integration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techmill.integration.auth.ResponseAuth;
import com.techmill.integration.auth.RootAuthentication;

public class AuthenticationLogoff {
	static Log mylog = new Log("LOGOFF");

	public ResponseAuth processRequest(String codigo, String password) throws IOException {
		InputStream input = AuthenticationLogoff.class.getClassLoader()
				.getResourceAsStream("config/bcrpConfig.properties"); // Read
		RootAuthentication rootAuthentication = new RootAuthentication(codigo, password);

		Properties prop = new Properties();
		if (input == null) {
			String error = "Unable to find Config.properties";
			mylog.addError(error);
			return null;
		}
		prop.load(input);
		String Base_URL = prop.getProperty("AuthLogoff_URL");
		String AuthKey = prop.getProperty("AuthKey");
		String dataIn = "";
		String dataOut = "";
		String final_URL = Base_URL;

		dataOut = final_URL;

		HttpPost httpPost = new HttpPost(final_URL);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, AuthKey);
		httpPost.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5");
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
			String objectString = gson.toJson(rootAuthentication);
			StringEntity element = new StringEntity(objectString);
			httpPost.setEntity(element);
			mylog.addInfo("OUT: " + dataOut + " :: " + objectString);
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			JsonElement jElement = JsonParser.parseString(json);
			ResponseAuth root = gson.fromJson(jElement, ResponseAuth.class);
			dataIn = gson.toJson(root);
			mylog.addInfo("IN: " + dataIn);
			if (!root.isOk()) {
				mylog.addError(root.getDescription());
			}
			System.out.println(root);
			return root;
		} catch (Exception e) {
			mylog.addError(e.getMessage());
		} finally {
			httpClient.close();
		}
		return null;
	}

}
