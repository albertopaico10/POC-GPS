package com.example.poc_sendcoordinatesv3;

import java.text.DateFormat;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.poc_sendcoordinatesv3.services.CoordinateService;
import com.example.poc_sendcoordinatesv3.services.impl.CoordinateServiceImpl;
import com.example.poc_sendcoordinatesv3.util.GPSTracker;

public class MainActivity extends ActionBarActivity {
	CoordinateService coordinateService=new CoordinateServiceImpl(); 
	final static DateFormat fmt = DateFormat.getTimeInstance(DateFormat.LONG);
	String valueLong="",valueLat="";
	Handler handler = new Handler();
	int numberRequest=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	private Runnable runnableCode = new Runnable() {
	    @Override
	    public void run() {
	      // Do something here on the main thread
	    	System.out.println("ESTE ES UN CRON");
	    	getCurrentLocation();
	    	numberRequest++;
	    	System.out.println("Empezo de Request N° : "+numberRequest);
	    	Toast.makeText(MainActivity.this, "Empezo de Request N° : "+numberRequest, Toast.LENGTH_SHORT).show();
	    	new RegisterService().execute();
	    	System.out.println("Se termino el  Request N°: "+numberRequest);
	    	Toast.makeText(MainActivity.this, "Se termino el  Request N°: "+numberRequest, Toast.LENGTH_SHORT).show();
	    	handler.postDelayed(runnableCode, 4000);
	    }
	};
	
	public void startProcess(View v) {
		System.out.println("Arrancamos");
		handler.post(runnableCode);
	}
	
	private void getCurrentLocation(){
		GPSTracker gps = new GPSTracker(MainActivity.this);
		if (gps.canGetLocation()) {
			double dLatitude = gps.getLatitude();
	        double dLongitude = gps.getLongitude();
	        System.out.println("Valores : "+valueLat+"***"+valueLong);
	        valueLat=String.valueOf(dLatitude);
	        valueLong=String.valueOf(dLongitude);
		}else{
			handler.removeCallbacks(runnableCode);
			gps.showSettingsAlert();
		} 
	}

	public void stopProcess(View v) {
		System.out.println("Terminamos");
		handler.removeCallbacks(runnableCode);
	}
	
	private class RegisterService extends AsyncTask<Void, Void, Void> {
		private String Content="";
		@Override
		protected void onPreExecute() {
			
		}
		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				
				Content=coordinateService.callService(valueLat,valueLong,"car1");
			} catch (Exception e) {
//				resultServices=false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			JSONObject jObject = null;
			try {
				jObject = new JSONObject(Content);
				String codeResponse = jObject.getString("codeResponse");
				System.out.println("RESPUESTA : "+codeResponse);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
//			if(!resultServices){
//				methodError(getString(R.string.connectionRefused));
//			}else{
//				JSONObject jObject = null;
//				try {
//					jObject = new JSONObject(Content);
//					String codeResponse = jObject.getString("codeResponse");
//					String email = jObject.getString("additional");
//					int idUser = jObject.getInt("idUser");
//					System.out.println("codeResponse : "+codeResponse+"************++");
//					if(CommonConstants.CodeResponse.RESPONSE_SUCCESS_VALIDATION.equals(codeResponse)){
//						try {
//							loginService.sucessLogin(String.valueOf(idUser), email,dbBalletPaper);	
//							Intent i = new Intent(LoginActivity.this, PrincipalMainActivity.class);
//							startActivity(i);
//						} catch (Exception e) {
//							methodError(getString(R.string.errorInGeneral));
//						}
//					}else if(CommonConstants.CodeResponse.RESPONSE_FAIL_VALIDATION.equals(codeResponse)){
//						methodError(getString(R.string.validationFail));
//					}else if(CommonConstants.CodeResponse.RESPONSE_EMAIL_NOT_EXIST.equals(codeResponse)){
//						methodError(getString(R.string.emailNotExit));
//					}
//				} catch (Exception e) {
//					methodError(getString(R.string.errorUser)+e+getString(R.string.sorryMessages));
//				}
//			}
//			linearLayoutForm.setVisibility(View.VISIBLE);
//			linearLayoutProgress.setVisibility(View.GONE);	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
