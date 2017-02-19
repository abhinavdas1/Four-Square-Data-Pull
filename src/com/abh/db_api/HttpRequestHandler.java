package com.abh.db_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

public class HttpRequestHandler {

	private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();
	
	public static String execute(String endPoint, Map<String, String> parameters, String method){
		HttpUriRequest http = initRequestInstance(endPoint, parameters, method);
		
		try {	
			
			CloseableHttpClient httpClient = getHttpClient();
			CloseableHttpResponse httpResponse = httpClient.execute(http);

			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
				String response = readResponse(httpClient, httpResponse);
				System.out.println(response);

				return response;
			} else {
				String response = readResponse(httpClient, httpResponse);
				System.out.println(response);
				return null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String readResponse(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) {
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();
			httpClient.close();
			return response.toString();
		}catch(IOException e){
			return null;
		}		
	}
	
	private static HttpUriRequest initRequestInstance(String endPoint, Map<String, String> parameters, String method){
		HttpUriRequest http = null;
		URIBuilder ub = new URIBuilder();
		for (String name : parameters.keySet()){
			if (parameters.get(name) != null){
				ub.setParameter(name, parameters.get(name));
			}			
		}
		
		try {
			URI uri = ub.setScheme("https")
					.setHost("api.foursquare.com")
					.setPath(endPoint)
					.build();
			
			switch(method){
				case "POST": 
					http = new HttpPost(uri); 
					((HttpPost)http).setConfig(requestConfig);
					break;
				case "GET":
					http = new HttpGet(uri); 
					((HttpGet)http).setConfig(requestConfig);
					break;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
		
		return http;
	}
	
	private static CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(new HttpRequestRetryHandler() {

					@Override
					public boolean retryRequest(IOException ex,
							int executionCount, HttpContext ctx) {
						if (executionCount >= 5) {
							return false;
						}
						if (ex instanceof InterruptedIOException) {
							return false;
						}
						if (ex instanceof UnknownHostException) {
							return false;
						}
						if (ex instanceof ConnectTimeoutException) {
							return false;
						}
						if (ex instanceof SSLException) {
							return false;
						}
						HttpClientContext clientContext = HttpClientContext
								.adapt(ctx);
						HttpRequest request = clientContext.getRequest();
						boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
						if (idempotent) {
							return true;
						}
						return false;
					}

				}).build();

		return httpClient;
	}
}
