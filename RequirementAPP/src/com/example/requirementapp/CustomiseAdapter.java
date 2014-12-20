package com.example.requirementapp;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomiseAdapter extends ArrayAdapter<RequirementInfo>
{
	
		  private ArrayList<RequirementInfo> requirementList;
		 
		  public CustomiseAdapter(Context context, int textViewResourceId ,ArrayList<RequirementInfo> requirementList) {
			   
			  super(context, textViewResourceId, requirementList);
			  this.requirementList = new ArrayList<RequirementInfo>();
			  this.requirementList.addAll(requirementList);
		  }
		 
		  private class ViewHolder 
		  { 
			   TextView intitule;
			   TextView description;
			   TextView derivedId;
			  // TextView id;
		  }
		 
		  public View getView(int position, View convertView, ViewGroup parent) 
		  {
			   Log.v("Requirement_Activity", String.valueOf(position));
			   ViewHolder holder = null;
			   
			   if (convertView == null) 
			   {
				    LayoutInflater vi = LayoutInflater.from(parent.getContext());
				    convertView = vi.inflate(R.layout.requirement_info, null);
				    holder = new ViewHolder();
				    holder.derivedId = (TextView)convertView.findViewById(R.id.deriv_name_requirement);
				    holder.intitule = (TextView)convertView.findViewById(R.id.name_requirement);
				    holder.description = (TextView)convertView.findViewById(R.id.descs_requirement);
				    convertView.setTag(holder);
				 
			   } 
			   else
			   {
				   holder = (ViewHolder)convertView.getTag();
			   }
			   
			   RequirementInfo reqInfo = requirementList.get(position);
			   holder.intitule.setText(reqInfo.getIntitule());
			   holder.description.setText(reqInfo.getDescription());
			   String txt = reqInfo.getDerivedId();
			   if(txt == null)
			   {
				   txt = "";
			   }
			   else
			   {
				   txt = "Derive de : "+reqInfo.getDerivedId();
			   }
			   holder.derivedId.setText(txt);
			   return convertView;
				 
			 }
}
	