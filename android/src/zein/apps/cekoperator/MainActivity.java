package zein.apps.cekoperator;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity {
	EditText txt_nomer_HP;
	ProgressDialog pDialog;
	TextView tv_saran_kritik;
	
	private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		tv_saran_kritik = (TextView)findViewById(R.id.txt_saran_dan_kritik);
//		tv_saran_kritik.setText("Informasikan ke tim kami jika terjadi salah identifikasi. (zein.apps@gmail.com)");
		txt_nomer_HP = (EditText)findViewById(R.id.txt_no_hp);
		
		Button btn_kontak = (Button)findViewById(R.id.btn_kontak);

		btn_kontak.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	OpenKontak();
           }
        });
		
		Button btn_cek = (Button)findViewById(R.id.btn_cek);

		btn_cek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(cek_status_internet(MainActivity.this)){
            		String no_hp = txt_nomer_HP.getText().toString();
                	new Request(no_hp).execute();
            	}else {
					pesan("Koneksi internet tidak terdeteksi", true);
				}
           }
        });
		
		Button btn_share = (Button)findViewById(R.id.btn_share);

		btn_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	sharetoMedsos("Shared", "https://play.google.com/store/apps/details?id=zein.apps.cekoperator");
           }
        });
		
		 AdRequest adRequest1 = new AdRequest.Builder()
	       .build();
		 
		 AdView adView = (AdView) this.findViewById(R.id.adView);
	     adView.loadAd(adRequest1);
	     
	     
	     AdRequest adRequest2 = new AdRequest.Builder()
	       .build();
	     
	     AdView adView2 = (AdView) this.findViewById(R.id.adView2);
	     adView2.loadAd(adRequest2);
		 
	     
	     AdRequest adRequest3 = new AdRequest.Builder()
	       .build();
	     
		interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-1799215828167471/1049654548");
        
	      interstitial.loadAd(adRequest3);
	      
	     interstitial.setAdListener(new AdListener() {
		     public void onAdLoaded() {
//		    	 displayInterstitial();
		     }
	     });
	     
//		AdRequest adRequest2 = new AdRequest.Builder().build();
	     
		
	}
	
  public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
	  Log.d("Kakak", " "+interstitial.isLoaded());
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
		}
	
	private boolean isValidHp(String no_hp){
		boolean valid = false;
		if(no_hp.substring(0,2).equals("08") || no_hp.substring(0,4).equals("+628") || no_hp.substring(0,3).equals("628")){
			valid = true;
		}
		return valid;
	}
	
	private String convertNoHPtoPrefix(String no_hp){
		String prefix="";
		no_hp = no_hp.replace("+", "");
		if(no_hp.substring(0,3).equals("628")){
			no_hp = no_hp.replaceFirst("628", "08");
		}
		
		prefix = no_hp.substring(0,4);
		
		return prefix;
	}

	private String getOperator(String prefix){
		String operator = "Maaf!\nOperator tidak dikenali";
		try {
    		String json_str = readFromFile("data.json");
    		JSONObject json = new JSONObject(json_str);
    		JSONObject jdata = new JSONObject(json.getString("data"));
    		operator = jdata.getString(prefix);
		} catch (Exception e) {
			Log.e("Eror Jason", e.getMessage());
		}
		return operator;
	}
	
	
	private class Request extends AsyncTask<String, Integer, String> {
		String no_hp;
		public Request(String no_hp){
			this.no_hp = no_hp;
		}
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Harap Tunggu! Pengecekan sedang berlangsung...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected String  doInBackground(String... arg0) {
            
            try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}       
        	
            return null;
        }
 
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
            if(this.no_hp.length() >= 6){
        		if(isValidHp(this.no_hp)){
        			pesan(getOperator(convertNoHPtoPrefix(this.no_hp)), true);
        			
        		}else {
        			pesan("invalid number", false);
				}
        	}else{
        		pesan("kurang dari 6 digit", false);
        	}
            
            if (pDialog.isShowing())
                pDialog.dismiss();

            
            
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
	
	public String readFromFile(String namafile) {

	    String ret = "";

	    try {
	        InputStream inputStream = getAssets().open(namafile);

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	void pesan(String txt, boolean dialog){
		if(dialog){
//			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//	    	alertDialog.setTitle("PEMBERITAHUAN");
//	    	alertDialog.setMessage(txt);
//	    	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//	    	    new DialogInterface.OnClickListener() {
//	    	        public void onClick(DialogInterface dialog, int which) {
//	    	            dialog.dismiss();
//	    	        }
//	    	    });
//	    	alertDialog.show();
			
			final Dialog dialog_box = new Dialog(MainActivity.this);
			dialog_box.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog_box.setContentView(R.layout.custom_dialog);
//			dialog_box.setTitle("Hasil");

			// set the custom dialog components - text, image and button
			TextView text = (TextView) dialog_box.findViewById(R.id.txt_hasil);
			text.setText(txt);
			

			Button dialogButton = (Button) dialog_box.findViewById(R.id.btn_ok);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog_box.dismiss();
					displayInterstitial();
				}
			});

			dialog_box.show();
			
		}else{
			Toast.makeText(getApplicationContext(), txt,
	    			Toast.LENGTH_LONG).show();
		}
    	
    	
    }
	public boolean cek_status_internet(Context cek) {
    	ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.isConnected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	int PICK_CONTACT = 1;
	private void OpenKontak(){
		try
		{
		     Intent intent = new Intent(Intent.ACTION_PICK);
		     intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		     startActivityForResult(intent, PICK_CONTACT);
		}
		catch (Exception e) {
		    Intent intent = getIntent();
		    finish();
		    startActivity(intent);
		}
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
      try{
      if (requestCode == PICK_CONTACT){
		  Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
		  cursor.moveToNext();
		  String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		  String  name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
		  String phone=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		  String phoneNumber="test";

          if ( phone.equalsIgnoreCase("1"))
              phone = "true";
          else
              phone = "false" ;

          if (Boolean.parseBoolean(phone)) 
          {
           Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
           while (phones.moveToNext()) 
           {
             phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
           }
           phones.close();
           txt_nomer_HP.setText(phoneNumber);
          }else{
        	  Toast.makeText(this, "Bukan Nomer", Toast.LENGTH_LONG).show();
          }
          
         
          

      }
        }
      catch (Exception e) {
        // TODO: handle exception
        }
    }
	
	private void sharetoMedsos(String title, String link) {
	    // Standard message to send
	    String msg = title + " " + link;

	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("text/plain");

	    List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(share, 0);
	    if (!resInfo.isEmpty()) {
	        List<Intent> targetedShareIntents = new ArrayList<Intent>();
	        Intent targetedShareIntent = null;

	        for (ResolveInfo resolveInfo : resInfo) {
	            String packageName = resolveInfo.activityInfo.packageName;
	            targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
	            targetedShareIntent.setType("text/plain");

	            // Find twitter: com.twitter.android...
	            if ("com.twitter.android".equals(packageName)) {
	                targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
	            } else if ("com.google.android.gm".equals(packageName)) {
	                targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
	                targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Uri.encode(title + "\r\n" + link));
	            } else if ("com.android.email".equals(packageName)) {
	                targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
	                targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Uri.encode(title + "\n" + link));
	            } else {
	                // Rest of Apps
	                targetedShareIntent.putExtra( android.content.Intent.EXTRA_TEXT, msg);
	            }

	            targetedShareIntent.setPackage(packageName);
	            targetedShareIntents.add(targetedShareIntent);
	        }

	        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), getResources().getString(R.string.share));
	        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
	        startActivityForResult(chooserIntent, 0);
	    }
	}
	
}
