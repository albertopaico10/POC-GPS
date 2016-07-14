package com.example.poc_sendcoordinatesv3.services.impl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.example.poc_sendcoordinatesv3.services.CoordinateService;
import com.example.poc_sendcoordinatesv3.util.CommonConstants;

public class CoordinateServiceImpl implements CoordinateService{

	public String callService(String longitude,String latitude,String idValueCar)throws Exception{
//		System.out.println("Email 222: "+loginBean.getEmail()+"***"+loginBean.getPassword());
		System.out.println("Common : "+CommonConstants.URLService.REGISTER_COORDINATE);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 2000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 2000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpPost post = new HttpPost(CommonConstants.URLService.REGISTER_COORDINATE);
		post.setHeader("content-type", "application/json; charset=UTF-8");
		JSONObject dato = new JSONObject();
		dato.put(CommonConstants.CoordinateRequest.REQ_COORDINATE_IDVALUE, idValueCar);
		dato.put(CommonConstants.CoordinateRequest.REQ_COORDINATE_LATITUDE, latitude);
		dato.put(CommonConstants.CoordinateRequest.REQ_COORDINATE_LONGITUDE, longitude);
		dato.put(CommonConstants.CoordinateRequest.REQ_COORDINATE_TITLE, "Carro #1");
		System.out.println("Request: "+dato.toString());
		StringEntity entity = new StringEntity(dato.toString());
		post.setEntity(entity);

		
		HttpResponse resp = httpClient.execute(post);
		String respStr = EntityUtils.toString(resp.getEntity());
		System.out.println("La respues que viene : " + respStr);
		return respStr;
	}
	
}
