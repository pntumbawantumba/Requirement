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
import org.json.JSONArray;
import org.json.JSONException;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.Toast;

public class ProjectFormActivity extends Activity {

	EditText editProjectName;
	EditText editProjectDesc;
	EditText editStartRequirementName;
	EditText editStartRequirementDesc;
	EditText editStartSubRequirementName;
	EditText editStartSubRequirementDesc;
	static final String url = "http://10.0.2.2:8181/RequirementServer";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_form, menu);
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
	
	@SuppressLint("NewApi")
	public void creerProject(View view)
	{
		editProjectName = (EditText) findViewById(R.id.EditProjectName);
		editProjectDesc = (EditText) findViewById(R.id.EditProjectDesc);
		editStartRequirementName = (EditText) findViewById(R.id.EditStartRequirementName);
		editStartRequirementDesc = (EditText) findViewById(R.id.EditStartRequirementDesc);
		editStartSubRequirementName = (EditText) findViewById(R.id.EditStartSubRequirementName);
		editStartSubRequirementDesc = (EditText) findViewById(R.id.EditStartSubRequirementDesc);
		if(editProjectName.getText().toString().isEmpty() && editProjectDesc.getText().toString().isEmpty() 
			&& editStartRequirementName.getText().toString().isEmpty() && editStartRequirementDesc.getText().toString().isEmpty()
			&& editStartSubRequirementName.getText().toString().isEmpty() && editStartSubRequirementDesc.getText().toString().isEmpty())
		{
			Toast toast = Toast.makeText(ProjectFormActivity.this,"Bien vouloir remplir tous les champs", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.TOP, 25, 200);
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
		  private ProgressDialog dialog = new ProgressDialog(ProjectFormActivity.this);
		 
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
				   Log.v("ProjectFormActivity",  URL );
				   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
				   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
				   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
				   
				   HttpPost httpPost = new HttpPost(URL);
				   
				   List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				   pairs.add(new BasicNameValuePair("action", "creerProject"));
				   pairs.add(new BasicNameValuePair("ProjectName", editProjectName.getText().toString()));
				   pairs.add(new BasicNameValuePair("ProjectDesc", editProjectDesc.getText().toString()));
				   pairs.add(new BasicNameValuePair("StartRequirementName", editStartRequirementName.getText().toString()));
				   pairs.add(new BasicNameValuePair("StartRequirementDesc", editStartRequirementDesc.getText().toString()));
				   pairs.add(new BasicNameValuePair("StartSubRequirementName", editStartSubRequirementName.getText().toString()));
				   pairs.add(new BasicNameValuePair("StartSubRequirementDesc", editStartSubRequirementDesc.getText().toString()));
				   httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				   response = httpclient.execute(httpPost);
			 
				   StatusLine statusLine = response.getStatusLine();
				   if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				   {
					   	ByteArrayOutputStream out = new ByteArrayOutputStream();
					   	response.getEntity().writeTo(out);
					   	out.close();
					   	content = out.toString();
					   	Log.v("ProjectFormActivity",  content );
				   } 
				   else
				   {
					   //Fermeture de la connexion.
					   Log.w("ProjectFormActivity => HTTP1:",statusLine.getReasonPhrase());
					   response.getEntity().getContent().close();
					   throw new IOException(statusLine.getReasonPhrase());
				   }
			   } 
			   catch (ClientProtocolException e) 
			   {
				   Log.w("ProjectFormActivity => HTTP2:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   } 
			   catch (IOException e) 
			   {
				   Log.w("ProjectFormActivity => HTTP3:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   }
			   catch (Exception e) 
			   {
				    Log.w("ProjectFormActivity => HTTP4:",e );
				    content = e.getMessage();
				    error = true;
				    cancel(true);
			   }
			 
			   return content;
		  }
		 
		  protected void onCancelled() 
		  {
			  dialog.dismiss();
			  Toast toast = Toast.makeText(ProjectFormActivity.this,"Erreur de connexion au serveur", Toast.LENGTH_LONG);
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
				  toast = Toast.makeText(ProjectFormActivity.this,
				  content, Toast.LENGTH_LONG);
				  toast.setGravity(Gravity.TOP, 25, 400);
				  toast.show();
			  } 
			  else 
			  {
				  toast = Toast.makeText(ProjectFormActivity.this,
				  content, Toast.LENGTH_LONG);
				  toast.setGravity(Gravity.TOP, 25, 400);
				  toast.show();
				  if(content.equals("Project cree avec succes"))
				  {
					  Intent intent = new Intent(ProjectFormActivity.this, MainActivity.class);
					  startActivity(intent);
				  }
				  
			  }
		  }
		 
		 }
	
}
