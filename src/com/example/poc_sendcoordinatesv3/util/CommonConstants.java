package com.example.poc_sendcoordinatesv3.util;

public class CommonConstants {

	public class URLService {
		public static final String URL_SERVICE = "http://162.248.54.10:8090/";
//		public static final String URL_SERVICE = "http://192.168.1.35:8080/";
//		public static final String URL_SERVICE = "http://10.65.52.168:8080/";
		
		public static final String SERVICE_NAME = "POCGMSave";
		public static final String REGISTER_COORDINATE = URL_SERVICE+SERVICE_NAME+"/rest/coordinate/register";
		
	}
	
	public class CoordinateRequest{
		public static final String REQ_COORDINATE_IDVALUE = "idValueCar";
		public static final String REQ_COORDINATE_LATITUDE = "latitude";
		public static final String REQ_COORDINATE_LONGITUDE = "longitude";
		public static final String REQ_COORDINATE_TITLE = "titleCar";
	}
	
	
	
	
}
