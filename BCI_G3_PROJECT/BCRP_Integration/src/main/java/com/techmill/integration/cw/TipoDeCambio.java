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
import com.techmill.integration.cw.tipoDeCambio.RootTipodeCambio;

public class TipoDeCambio {
	static Log log = new Log("CWTipoDeCambio");

	public RootTipodeCambio processRequest(String sid, String codMoneda, String fecha) throws IOException {
		String data = "/" + codMoneda + "/cambio?fecha=" + fecha + "&sid=" + sid;
		InputStream input = TipoDeCambio.class.getClassLoader().getResourceAsStream("config/bcrpConfig.properties"); // Read

		Properties prop = new Properties();
		if (input == null) {
			String error = "Unable to find Config.properties";
			log.addError(error);
			return null;
		}
		prop.load(input);
		String Base_URL = prop.getProperty("TipoDeCambio_URL");
		String AuthKey = prop.getProperty("AuthKey");
		String dataIn = "";
		String dataOut = "";
		String final_URL = Base_URL + data;

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
			JsonElement element = JsonParser.parseString(json);
			RootTipodeCambio root = gson.fromJson(element, RootTipodeCambio.class);
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
