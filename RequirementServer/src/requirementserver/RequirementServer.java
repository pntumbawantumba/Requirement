
package requirementserver;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class RequirementServer extends HttpServlet{

	RequirementDataXML data = new 	RequirementDataXML();
	
	
	public void doGet(HttpServletRequest query, HttpServletResponse response) throws 
	ServletException, IOException
	{
		response.getOutputStream().println("It Works");
	}
	
	public void doPost(HttpServletRequest query, HttpServletResponse response)
	{
		try 
		{
            if(query.getParameter("action").equals("loadProjects") )
            {
            		
            	ArrayList<String> projectList = null;
        		projectList = data.getAllProjectList();
        		String json = new Gson().toJson(projectList);
        		
            	response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
        	    response.setCharacterEncoding("UTF-8");
        	    
        	    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                writer.write(json);
                writer.flush();
                writer.close();
            }
            
            if(query.getParameter("action").equals("creerProject"))
            {
            	String projectName = query.getParameter("ProjectName");
            	String projectDesc = query.getParameter("ProjectDesc");
            	String ReqTitle = query.getParameter("StartRequirementName");
            	String ReqDesc = query.getParameter("StartRequirementDesc");
            	String SubReqTitle = query.getParameter("StartSubRequirementName");
            	String SubReqDesc = query.getParameter("StartSubRequirementDesc");
            	if(data.isProjectExist(projectName) == false)
            	{
            		data.setProjectName(projectName);
                	data.setDescription(projectDesc);
                	data.initRequirementDocument();
                	data.addSpecification("Globale");
                	data.addRequirement("", "", "", ReqTitle, ReqDesc, "Globale");
                	String idParentReq = data.getRequirementId(ReqTitle).toString();
                	if( idParentReq != "")
                	{
                		data.addRequirement("", idParentReq, "", SubReqTitle, SubReqDesc, "Globale");
                	}
            	}
            	String respDb = "";
            	if(data.saveDocumentXML(data.getDocument()))
            	{
            		respDb = "Project cree avec succes";
            	}
            	else
            	{
            		respDb = "Ce project existe deja";
            	}
            	
        		response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/text");
        	    response.setCharacterEncoding("UTF-8");
        	    
        	    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                writer.write(respDb);
                writer.flush();
                writer.close();
            	
            }
            
            if(query.getParameter("action").equals("loadRequirements"))
            {
            	ArrayList<RequirementInfo> requirementList = data.getRequirements(query.getParameter("projectName"));
            	Gson gson = new Gson();
            	JsonArray arrayObj = new JsonArray();
            	for(int i=0; i < requirementList.size(); i++)
            	{
            		RequirementInfo reqInfo = requirementList.get(i);
            		JsonElement reqJsonObj =  gson.toJsonTree(reqInfo);
            		arrayObj.add(reqJsonObj);
            	}
            	
            	JsonObject finalObject = new JsonObject();
            	finalObject.addProperty("succes", true);
            	finalObject.add("requirementList", arrayObj);
            	
            	response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
        	    response.setCharacterEncoding("UTF-8");
        	    
        	    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                writer.write(finalObject.toString());
                writer.flush();
                writer.close();
            }
            
            if(query.getParameter("action").equals("ajouterExigence"))
            {
            	
            	String projectName = query.getParameter("ProjectName");
            	String reqTitle = query.getParameter("RequirementName");
            	String reqDesc = query.getParameter("RequirementDesc");
            	String derivedRequirement = query.getParameter("Derived");
            	data.setProjectName(projectName);
            	if(data.isProjectExist(projectName) == true)
            	{
            		String xmlFile = data.getProjectXmlFile(projectName);
            		data.setDocument(data.loadDocumentXML(xmlFile));
                	String derivedId = ""; 
                	if(!derivedRequirement.equals("pas de relation"))
                	{
                		derivedId = data.getRequirementId(derivedRequirement);
                	}
                	data.addRequirement("", derivedId, "", reqTitle, reqDesc, "Globale");	
            	}
            	String respDb = "";
            	if(data.saveDocumentXML(data.getDocument()))
            	{
            		respDb = "Exigence ajoutee avec succes";
            		System.out.println(respDb);
            	}
            	else
            	{
            		respDb = "Impossible d'ajouter cette exigence";
            		System.out.println(respDb);
            	}
            	
        		response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/text");
        	    response.setCharacterEncoding("UTF-8");
        	    
        	    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                writer.write(respDb);
                writer.flush();
                writer.close();
            }
 
        }
		catch (IOException e) 
		{
 
            try
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(e.getMessage());
                response.getWriter().close();
            } 
            catch (IOException ioe) 
            {}
        }  
	}
}
