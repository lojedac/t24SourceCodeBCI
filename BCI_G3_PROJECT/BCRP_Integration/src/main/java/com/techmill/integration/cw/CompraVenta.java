package com.techmill.integration.cw;

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
import com.techmill.integration.Log;
import com.techmill.integration.cw.compraVenta.DatosCompraVentaME;
import com.techmill.integration.cw.compraVenta.ResponseCompraVenta;
import com.techmill.integration.cw.compraVenta.RootCompraVenta;

public class CompraVenta {
	static Log log = new Log("CompraVenta.class");

	public ResponseCompraVenta instruirCompraVentaME(String sid, DatosCompraVentaME datosCompraVentaME, String firma)
			throws IOException {

		InputStream input = CompraVenta.class.getClassLoader().getResourceAsStream("config/bcrpConfig.properties"); // Read
		RootCompraVenta rootCompraVenta = new RootCompraVenta(sid, datosCompraVentaME, firma);

		Properties prop = new Properties();
		if (input == null) {
			String error = "Unable to find Config.properties";
			log.addError(error);
			return null;
		}
		prop.load(input);
		String Base_URL = prop.getProperty("InstruirCompraVenta_URL");
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
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
			String objectString = gson.toJson(rootCompraVenta);
			StringEntity element = new StringEntity(objectString);
			httpPost.setEntity(element);
			log.addInfo("OUT: " + dataOut + " :: " + objectString);
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			JsonElement jElement = JsonParser.parseString(json);
			ResponseCompraVenta root = gson.fromJson(jElement, ResponseCompraVenta.class);
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

	public ResponseCompraVenta confirmarCompraVenta(String sid, DatosCompraVentaME datosCompraVentaME, String firma)
			throws IOException {
		InputStream input = CompraVenta.class.getClassLoader().getResourceAsStream("config/bcrpConfig.properties"); // Read
		RootCompraVenta rootCompraVenta = new RootCompraVenta(sid, datosCompraVentaME, firma);

		Properties prop = new Properties();
		if (input == null) {
			String error = "Unable to find Config.properties";
			log.addError(error);
			return null;
		}
		prop.load(input);
		String Base_URL = prop.getProperty("ConfirmarCompraVenta_URL");
		String AuthKey = prop.getProperty("AuthKey");
		String dataIn = "";
		String dataOut = "";
		String final_URL = Base_URL;

		dataOut = final_URL;

		HttpPost httpPost = new HttpPost(final_URL);
		httpPost.setHeader("Authorization", AuthKey);
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
		httpPost.setHeader("content-type", "application/json;charset=UTF-8");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
			String objectString = gson.toJson(rootCompraVenta);
			StringEntity element = new StringEntity(objectString);
			httpPost.setEntity(element);
			log.addInfo("Trama out: " + dataOut + " :: " + objectString);
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			JsonElement jElement = JsonParser.parseString(json);
			ResponseCompraVenta root = gson.fromJson(jElement, ResponseCompraVenta.class);
			dataIn = gson.toJson(root);
			log.addInfo("Trama in: " + dataIn);
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
