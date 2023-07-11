package com.techmill.integration.cw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techmill.integration.Log;
import com.techmill.integration.cw.operacionesCvme.RootOperacionesCvme;

public class OperacionesCvme {
	static Log log = new Log("CWOperacionesCvme");

	public RootOperacionesCvme processRequest(String sid, String fechaLiquidacion) throws IOException {
		String data = "?&sid=" + sid + "&fechaLiquidacion=" + fechaLiquidacion;
		InputStream input = OperacionesCvme.class.getClassLoader().getResourceAsStream("config/bcrpConfig.properties"); // Read

		Properties prop = new Properties();
		if (input == null) {
			String error = "Unable to find Config.properties";
			log.addError(error);
			return null;
		}
		prop.load(input);
		String Base_URL = prop.getProperty("OperacionesCvme_URL");
		String AuthKey = prop.getProperty("AuthKey");
		String dataIn = "";
		String dataOut = "";
		String final_URL = Base_URL + data;
		System.out.println("URL: " + final_URL);

		dataOut = final_URL;

		HttpGet httpget = new HttpGet(final_URL);
		httpget.setHeader(HttpHeaders.AUTHORIZATION, AuthKey);
		httpget.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5");
		httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			log.addInfo("OUT: " + dataOut);
			HttpResponse response = httpClient.execute(httpget);
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			System.out.println(json);
			JsonElement element = JsonParser.parseString(json);
			RootOperacionesCvme root = gson.fromJson(element, RootOperacionesCvme.class);
			dataIn = gson.toJson(root);
			log.addInfo("IN: " + dataIn);
			if (!root.isOk()) {
				log.addError(root.getDescription());
			}
			System.out.println(root);
			return root;
		} catch (Exception e) {
			log.addError(e.getMessage());
		} finally {
			httpClient.close();
		}
		return null;
	}
}
