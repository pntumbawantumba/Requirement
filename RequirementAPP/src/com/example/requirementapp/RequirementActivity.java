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
import org.json.JSONObject;
import com.google.gson.Gson;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RequirementActivity extends Activity {
	
	private String projectName = null;
	static final String url = "http://10.0.2.2:8181/RequirementServer";
	ArrayList<RequirementInfo> requirementList;
	ArrayList<String> intituleProjetList;
	CustomiseAdapter requirementAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_requirement);
		Bundle objetBundle  = this.getIntent().getExtras(); 
		projectName = objetBundle.getString("ProjectName");
		TextView textHello = (TextView)findViewById(R.id.hello_world);
		textHello.setText(projectName);
		grabURL(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requirement, menu);
		return true;
	}
	
	public void addNewRequirement(View view)
	{
		Intent intent = new Intent(RequirementActivity.this, RequirementFormActivity.class);
		intituleProjetList = new ArrayList<String>();
		intituleProjetList.add("pas de relation");
		for(RequirementInfo requirementInfo : requirementList)
		{
			intituleProjetList.add(requirementInfo.getIntitule());
		}
		Bundle objetbunble = new Bundle();
		objetbunble.putStringArrayList("intitule", intituleProjetList);
		objetbunble.putString("projectName", projectName);
		intent.putExtras(objetbunble);
		startActivity(intent);		
	}
	
	public void grabURL(String url)
	 {
		  Log.v("activity_requirement", url);
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
		  private ProgressDialog dialog = new ProgressDialog(RequirementActivity.this);
		 
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
				   Log.v("RequirementActivity",  URL );
				   HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
				   HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
				   ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
				   
				   HttpPost httpPost = new HttpPost(URL);
				   
				   List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				   pairs.add(new BasicNameValuePair("action", "loadRequirements"));
				   pairs.add(new BasicNameValuePair("projectName", projectName));
				   httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				   response = httpclient.execute(httpPost);
			 
				   StatusLine statusLine = response.getStatusLine();
				   if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				   {
					   	ByteArrayOutputStream out = new ByteArrayOutputStream();
					   	response.getEntity().writeTo(out);
					   	out.close();
					   	content = out.toString();
					   	Log.v("RequirementActivity",  content );
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
			  Toast toast = Toast.makeText(RequirementActivity.this,"Erreur de connexion au serveur", Toast.LENGTH_LONG);
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
				  toast = Toast.makeText(RequirementActivity.this,content, Toast.LENGTH_LONG);
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
		JSONObject responseObj = null; 
		Log.w("displayListView:",response );
		Gson gson = new Gson();
        try 
        {
			responseObj = new JSONObject(response);
			JSONArray requirementListObj = responseObj.getJSONArray("requirementList");
			requirementList = new ArrayList<RequirementInfo>();
			for(int i=0; i < requirementListObj.length(); i++)
			{
				String requirementInfo = requirementListObj.getJSONObject(i).toString();
				RequirementInfo reqInfo = gson.fromJson(requirementInfo, RequirementInfo.class);
				requirementList.add(reqInfo);
			}
			
			requirementAdapter = new CustomiseAdapter(this, R.layout.requirement_info, requirementList);
			ListView requirementListView = (ListView)findViewById(R.id.list_requirement);
			requirementListView.setAdapter(requirementAdapter);
			requirementListView.setTextFilterEnabled(true);
		} 
        catch (JSONException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
