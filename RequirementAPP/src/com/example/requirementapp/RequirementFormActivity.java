package com.example.requirementapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import com.example.requirementapp.R.id;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RequirementFormActivity extends Activity {
	
	private Spinner spiner1;
	EditText editRequirementName;
	EditText editRequirementDesc;
	String ProjectName;
	Toast toast;
	static final String url = "http://10.0.2.2:8181/RequirementServer";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_requirement_form);
		chargerDataInSpinner();
		addListenerOnSpinnerItemSelection();
		
	}
	
	public void chargerDataInSpinner()
	{
		Bundle objetBundle  = this.getIntent().getExtras(); 
		ArrayList<String> intituleProjetList = new ArrayList<String>();
		intituleProjetList = objetBundle.getStringArrayList("intitule");
		ProjectName = objetBundle.getString("projectName");
		spiner1 = (Spinner)findViewById(R.id.list_requirement_spinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, intituleProjetList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiner1.setAdapter(dataAdapter);
	}
	
	public void addListenerOnSpinnerItemSelection() 
	{
		spiner1 = (Spinner) findViewById(R.id.list_requirement_spinner);
	}
	
	public void ajouterRequirement(View view)
	{
		editRequirementName = (EditText) findViewById(R.id.EditNewRequirementName);
		editRequirementDesc = (EditText) findViewById(R.id.EditNewRequirementDesc);
		//String selectedItem = spiner1.getSelectedItem().toString();
		
		if(editRequirementName.getText().toString().isEmpty() & editRequirementDesc.getText().toString().isEmpty())
		{
			toast = Toast.makeText(RequirementFormActivity.this,"Bien vouloir remplir tous les champs", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.TOP, 15, 200);
		    toast.show();
		}
		else
		{
			grabURL(url);
			
		}
		
	}
	
	public void grabURL(String url)
	{
		  Log.v("ProjectFormActivity", url);
		  new GrabURL().execute(url);
	}
	
	private class GrabURL extends AsyncTask<String, Void, String> {
		  
		  private static final int REGISTRATION_TIMEOUT = 3 * 1000;
		  private static final int WAIT_TIMEOUT = 30 * 1000;
		  private final HttpClient httpclient = new DefaultHttpClient();
		  final HttpParams params = httpclient.getParams();
		  HttpResponse response;
		  private String content =  null;
		  private boolean error = false;
		  private ProgressDialog dialog = new ProgressDialog(RequirementFormActivity.this);
		 
		  protected void onPreExecute() 
		  {
			   dialog.setMessage("Enregistrement du Project est cours...");
			   dialog.show();
		  }
		 
		  protected String doInBackground(String... urls) 
		  {
		 
			   String URL = null;
			    
			   try 
			   {			   
				   URL = urls[0];
				   Log.v("RequirementFormActivity",  URL );
				   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
				   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
				   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
				   
				   HttpPost httpPost = new HttpPost(URL);
				   
				   List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				   pairs.add(new BasicNameValuePair("action", "ajouterExigence"));
				   pairs.add(new BasicNameValuePair("ProjectName", ProjectName));
				   pairs.add(new BasicNameValuePair("RequirementName", editRequirementName.getText().toString()));
				   pairs.add(new BasicNameValuePair("RequirementDesc", editRequirementDesc.getText().toString()));
				   pairs.add(new BasicNameValuePair("Derived", spiner1.getSelectedItem().toString()));
				   httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				   response = httpclient.execute(httpPost);
			 
				   StatusLine statusLine = response.getStatusLine();
				   if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				   {
					   	ByteArrayOutputStream out = new ByteArrayOutputStream();
					   	response.getEntity().writeTo(out);
					   	out.close();
					   	content = out.toString();
					   	Log.v("RequirementFormActivity",  content );
				   } 
				   else
				   {
					   //Fermeture de la connexion.
					   Log.w("RequirementFormActivity => HTTP1:",statusLine.getReasonPhrase());
					   response.getEntity().getContent().close();
					   throw new IOException(statusLine.getReasonPhrase());
				   }
			   } 
			   catch (ClientProtocolException e) 
			   {
				   Log.w("RequirementFormActivity => HTTP2:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   } 
			   catch (IOException e) 
			   {
				   Log.w("RequirementFormActivity => HTTP3:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   }
			   catch (Exception e) 
			   {
				    Log.w("RequirementFormActivity => HTTP4:",e );
				    content = e.getMessage();
				    error = true;
				    cancel(true);
			   }
			 
			   return content;
		  }
		 
		  protected void onCancelled() 
		  {
			  dialog.dismiss();
			  Toast toast = Toast.makeText(RequirementFormActivity.this,"Erreur de connexion au serveur", Toast.LENGTH_LONG);
			  toast.setGravity(Gravity.TOP, 25, 400);
			  toast.show();
		 
		  }
		 
		  protected void onPostExecute(String content) 
		  {
			  Log.w("onPostExecute:",content );
			  dialog.dismiss();
			  Toast toast;
			  if (error) 
			  {
				  toast = Toast.makeText(RequirementFormActivity.this,
				  content, Toast.LENGTH_LONG);
				  toast.setGravity(Gravity.TOP, 25, 400);
				  toast.show();
			  } 
			  else 
			  {
				  Toast toasts = Toast.makeText(RequirementFormActivity.this,
				  content, Toast.LENGTH_LONG);
				  toasts.setGravity(Gravity.TOP, 25, 400);
				  toasts.show();
				  if(content.equals("Exigence ajoutee avec succes"))
				  {
					  Intent intent = new Intent(RequirementFormActivity.this, RequirementActivity.class);
					  startActivity(intent);
				  }
				  
			  }
		  }
		 
		 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requirement_form, menu);
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
