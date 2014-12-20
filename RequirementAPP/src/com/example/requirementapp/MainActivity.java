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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private ListView listProject;
	private final int CODE_ACTIVITE = 1;
	static final String url = "http://10.0.2.2:8181/RequirementServer";
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_main); 
	  grabURL(url);
	  
	 }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
	}
	 
	public void loadProjectForm(View view)
	{
		Intent intent = new Intent(MainActivity.this, ProjectFormActivity.class);
		startActivity(intent);
	} 
	
	 public void grabURL(String url)
	 {
		  Log.v("MainActivity", url);
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
		  private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		 
		  protected void onPreExecute() 
		  {
			   dialog.setMessage("Chargement des projects, Bien vouloir patienter...");
			   dialog.show();
		  }
		 
		  protected String doInBackground(String... urls) 
		  {
		 
			   String URL = null;
			    
			   try 
			   {			   
				   URL = urls[0];
				   Log.v("MainActivity",  URL );
				   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
				   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
				   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
				   
				   HttpPost httpPost = new HttpPost(URL);
				   
				   List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				   pairs.add(new BasicNameValuePair("action", "loadProjects"));
				   httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				   response = httpclient.execute(httpPost);
			 
				   StatusLine statusLine = response.getStatusLine();
				   if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				   {
					   	ByteArrayOutputStream out = new ByteArrayOutputStream();
					   	response.getEntity().writeTo(out);
					   	out.close();
					   	content = out.toString();
					   	Log.v("MainActivity",  content );
				   } 
				   else
				   {
					   //Fermeture de la connexion.
					   Log.w("HTTP1:",statusLine.getReasonPhrase());
					   response.getEntity().getContent().close();
					   throw new IOException(statusLine.getReasonPhrase());
				   }
			   } 
			   catch (ClientProtocolException e) 
			   {
				   Log.w("HTTP2:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   } 
			   catch (IOException e) 
			   {
				   Log.w("HTTP3:",e );
				   content = e.getMessage();
				   error = true;
				   cancel(true);
			   }
			   catch (Exception e) 
			   {
				    Log.w("HTTP4:",e );
				    content = e.getMessage();
				    error = true;
				    cancel(true);
			   }
			 
			   return content;
		  }
		 
		  protected void onCancelled() 
		  {
			  dialog.dismiss();
			  Toast toast = Toast.makeText(MainActivity.this,"Erreur de connexion au serveur", Toast.LENGTH_LONG);
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
				  toast = Toast.makeText(MainActivity.this,
				  content, Toast.LENGTH_LONG);
				  toast.setGravity(Gravity.TOP, 25, 400);
				  toast.show();
			  } 
			  else 
			  {
				  displayListView(content);
			  }
		  }
		 
		 }
	 
	 private void displayListView(String response)
	 {
		String  projectListItems = "";
		Log.w("displayListView:",response );
		JSONArray jsonArray;
		try 
		{
			jsonArray = new JSONArray(response);
			for (int i = 0; i < jsonArray.length(); i++) 
			{
				Log.w("jsonArray.getString(i):",jsonArray.getString(i) );
				
				if(i > 0 || i < jsonArray.length() -1)
				{
					projectListItems += "@"+jsonArray.getString(i);
				}
				
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w(" projectListItems ==:",projectListItems );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, projectListItems.split("@"));
		listProject = (ListView) findViewById(R.id.list_project);
		listProject.setAdapter(adapter);
		listProject.setClickable(true);
		listProject.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
			{	
				String projectName = (listProject.getItemAtPosition(position).toString());
				Intent intent = new Intent(MainActivity.this, RequirementActivity.class);
				Bundle objetbunble = new Bundle();
	            objetbunble .putString("ProjectName",projectName.toString());
	            intent.putExtras(objetbunble);
				startActivity(intent);
			}  
		});
	}
	
}
