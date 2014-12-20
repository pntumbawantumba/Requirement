package requirementserver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.List;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class RequirementDataXML {
	
	private org.w3c.dom.Document document;
	private String projectName;
	private int projectId;
	private String description;
	private String documentPath;
	
	//Constructeur par defaut de la classe
	public RequirementDataXML()
	{
		
		documentPath = new File("RequirementDocument/").getAbsolutePath()+"/";
		
	}
	
	// Setters des variables privés
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setDocument(org.w3c.dom.Document document)
	{
		this.document = document; 
	}
	
	 
	// Getters des variables privés
	public String getDescription()
	{
		return this.description;
	}
	
	public String getProjectName()
	{
		return this.projectName;
	}
	
	
	public org.w3c.dom.Document getDocument()
	{
		return this.document;
	}
	
	public int getProjectId()
	{
		File directory = new File(documentPath);
		try
		{
			projectId = directory.listFiles().length + 1 ;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			projectId = 1;
		}
		
		System.out.println(" projectId = "+projectId);
		return this.projectId;
	}
	
	public ArrayList<String> getAllProjectList()
	{
		ArrayList<String> projectList = new ArrayList<String>();
		
		File directory = new File(documentPath);
	
		String projectXmlFile = "";
		for(File xmlFile : directory.listFiles())
		{
			
			projectXmlFile =  xmlFile.getName();
			projectXmlFile = projectXmlFile.split("-")[0].toString();
			
			char tab[] = projectXmlFile.toCharArray();
			for(int i=0; i<tab.length; i++)
			{
				if(tab[i] == '_')
				{
					tab[i] =  ' ';
				}
			}
			
			projectXmlFile = String.valueOf(tab);
			
			projectList.add(projectXmlFile);
		}
		
		return projectList;
	} 
	
	
	// Convertit le nom du projet en nom de fichier
	public String getFileName(String projectName)
	{
		
		char tab[] = projectName.toCharArray();
		for(int i=0; i<tab.length; i++)
		{
			if(tab[i] == ' ')
			{
				tab[i] =  '_';
			}
		}
		
		return  documentPath+String.valueOf(tab)+"-"+getProjectId()+".xml";
		
	}
	
	// Initialisation du document XML pour une projet des exigences
	public void initRequirementDocument()
	{
		// Création d'un nouveau DOM 
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder constructeur = null;
		try 
		{
			constructeur = fabrique.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) 
		{
			
			e.printStackTrace();
		} 
		
		document =  constructeur.newDocument(); 
		  
		// Propriétés du DOM 
		document.setXmlVersion("1.0"); 
		document.setXmlStandalone(true); 
		  
		// Création de l'arborescence du DOM 
		org.w3c.dom.Element root =  document.createElement("requirementsProject"); 
		
		//Creation du noeud Nom Projet
		org.w3c.dom.Element ProjectName =  document.createElement("ProjectName");
		ProjectName.setTextContent(getProjectName()); 
		root.appendChild(ProjectName);
		
		//Creation du noeud Id Projet
		org.w3c.dom.Element id =  document.createElement("id");
		id.setTextContent(""+getProjectId()+""); 
		root.appendChild(id);
		
		//Creation du noeud Description du Projet
		org.w3c.dom.Element Description =  document.createElement("Description");
		Description.setTextContent(getDescription()); 
		root.appendChild(Description);
		document.appendChild(root);
	}
	
	//Verifier si un projet de meme nom existe
	public boolean isProjectExist(String projectName)
	{
	
		String projectXmlFile = getProjectXmlFile(projectName);
		org.w3c.dom.Document dom = null;
		boolean stat = false;
		
		if(!projectXmlFile.equals("") )
		{
			dom = loadDocumentXML(projectXmlFile);
		}
		
		if(dom != null && dom.getElementsByTagName("ProjectName").item(0).getTextContent().equals(projectName))
		{
			stat = true;
		}
		return stat;
	}
	
	
	//Recupere le fichier xml correspondant au Nom du Projet
	public String getProjectXmlFile(String projectName)
	{
		
		File directory = new File(documentPath);
		char tab[] = projectName.toCharArray();
		
		for(int i=0; i<tab.length; i++)
		{
			if(tab[i] == ' ')
			{
				tab[i] =  '_';
			}
		}
		
		String projectXmlFile = "";
		for(File xmlFile : directory.listFiles())
		{
			if(xmlFile.getName().startsWith(String.valueOf(tab)+"-"))
			{
				projectXmlFile =  xmlFile.getName();
			}
			
		}
		
		return projectXmlFile;
	}
	
	//Ajout d'un noeud regroupant un ensemble des exigences
	public void addSpecification(String name)
	{
		//Creation du noeud Groupe de specification
		org.w3c.dom.Element specification =  document.createElement("specification");
		specification.setAttribute("name",name); 
		//document.getElementById("requirementsProject").appendChild(specification);
		document.getElementsByTagName("requirementsProject").item(0).appendChild(specification);
		
	}
	
	//Ajout d'un noeud des exigences à une specification bien choisie
	public void addRequirement(String refineId, String deriveId, String satisfyId, String intitule, String decription, String nameSpeficication)
	{
		//Creation du noeud Requirement fils de Groupe de specification
		System.out.println("JE suis ici au debut");
		String requirementId = "";
		NodeList requirements = document.getElementsByTagName("requirement"); 
		int nbrRequirement = requirements.getLength();
		if( nbrRequirement == 0)
		{
			requirementId = "S1";
		}
		else
		{
			requirementId = "S"+(nbrRequirement+1);
		}
		
		//Creation du noeud requirement
		org.w3c.dom.Element requirement =  document.createElement("requirement"); 
		requirement.setAttribute("id",requirementId); 
		requirement.setAttribute("refineId",refineId);
		requirement.setAttribute("deriveId",deriveId);
		requirement.setAttribute("satisfyId",satisfyId);
				  
		//Creation du noeud Intitule Requirement fils de Requirement
		org.w3c.dom.Element intituleRequirement =  document.createElement("intitule"); 
		intituleRequirement.setTextContent(intitule); 
		requirement.appendChild(intituleRequirement);
		
		//Creation du noeud Description Requirement fils de Requirement
		org.w3c.dom.Element describeRequirement =  document.createElement("description"); 
		describeRequirement.setTextContent(decription); 
		requirement.appendChild(describeRequirement); 
		
		//Ajout du requirement à specification 
		NodeList specifications = document.getElementsByTagName("specification"); 
		for( int i=0; i < specifications.getLength(); i++)
		{
			final org.w3c.dom.Element specification = (org.w3c.dom.Element) specifications.item(i);
			if(specification.getAttribute("name").equals(nameSpeficication))
			{
				specification.appendChild(requirement);
			}
		}
		System.out.println("JE suis ici et al fin");
		
	}
	
	//Retourne l'Id d'un intitule
	String getRequirementId(String textIntitule)
	{
		String id = "";
		//Creation du noeud Requirement fils de Groupe de specification
		NodeList requirements = document.getElementsByTagName("requirement"); 
		for( int i=0; i < requirements.getLength(); i++)
		{
			final org.w3c.dom.Element requirement = (org.w3c.dom.Element) requirements.item(i);
			if(requirement.getChildNodes().item(0).getTextContent().equals(textIntitule))
			{
				id = requirement.getAttribute("id").toString();
				System.out.println("requirement.getAttribute('id'.toString() :"+requirement.getAttribute("id").toString());
			}
		}
		return id;
	}
	
	//Modification de l'intitule d'un requirement
	public void updateRequirementName(String idRequirement, String textIntitule)
	{
		//Creation du noeud Requirement fils de Groupe de specification
		NodeList requirements = document.getElementsByTagName("requirement"); 
		for( int i=0; i < requirements.getLength(); i++)
		{
			final org.w3c.dom.Element requirement = (org.w3c.dom.Element) requirements.item(i);
			if(requirement.getAttribute("id").equals(idRequirement))
			{
				requirement.getElementsByTagName("intitule").item(0).setTextContent(textIntitule);
			}
		}
		
	}
	
	//Modification des la description d'un requirement
	public void updateRequirementDescription(String idRequirement, String textDescription)
	{
		//Creation du noeud Requirement fils de Groupe de specification
		NodeList requirements = document.getElementsByTagName("requirement"); 
		for( int i=0; i < requirements.getLength(); i++)
		{
			final org.w3c.dom.Element requirement = (org.w3c.dom.Element) requirements.item(i);
			if(requirement.getAttribute("id").equals(idRequirement))
			{
				requirement.getElementsByTagName("description").item(0).setTextContent(textDescription);
			}
		}
		
	}
	
	//Sauvegarde du document au format XML
	public  boolean saveDocumentXML(org.w3c.dom.Document document) { 
		
		
		try { 
			
			// Création du fichier de sortie 
			String pathFile = getFileName(getProjectName());
			if(pathFile.equals(""))
			{
				System.out.println("pathFile");
				return false;
			}
			System.out.println(pathFile);
			File out = new File(pathFile); 
			StreamResult resultat = new StreamResult(out); 
			
			// Création de la source DOM 
			Source source = new DOMSource(document); 
	  
			// Configuration du transformer 
			Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1"); 
	  
			// Transformation 
			transformer.transform(source, resultat); 
	        return true;
	         
		}
		catch(Exception e)
		{ 
			System.out.println(e.getMessage());
			return false;
		} 
	}
	
	//Chargement du fichier XML dans un  document
	public  org.w3c.dom.Document loadDocumentXML(String fileName)
	{
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder constructeur = null;
		System.out.println(" documentPath+fileName " +documentPath+fileName);
		try 
		{
			constructeur = fabrique.newDocumentBuilder(); 
			try 
			{
				return constructeur.parse(documentPath+fileName);
			} 
			catch (SAXException e) 
			{
				
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				
				e.printStackTrace();
				return null;
			}
			
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
			return null;
		} 
		
	}
	
	ArrayList<RequirementInfo> getRequirements(String projetName)
	{
		ArrayList<RequirementInfo> requirements = new ArrayList<RequirementInfo>();
		
		String file_name = getProjectXmlFile(projetName);
		org.w3c.dom.Document projet = loadDocumentXML(file_name);
		NodeList reqNodes = projet.getElementsByTagName("requirement");
		for(int i=0; i < reqNodes.getLength(); i++)
		{
			final org.w3c.dom.Element reqNode = (org.w3c.dom.Element) reqNodes.item(i);
			RequirementInfo reqInfo = new RequirementInfo();
			reqInfo.setId(reqNode.getAttribute("id"));
			String rerivedId = reqNode.getAttribute("deriveId");
			NodeList nodeIntitule = reqNode.getElementsByTagName("intitule");
			reqInfo.setIntitule(nodeIntitule.item(0).getTextContent());
			NodeList nodeDescription = reqNode.getElementsByTagName("description");
			reqInfo.setDescription(nodeDescription.item(0).getTextContent());	
			NodeList reqNodesDerived = projet.getElementsByTagName("requirement");
			if(!rerivedId.equals(""))
			{
				for(int j=0; j < reqNodesDerived.getLength(); j++)
				{
					final org.w3c.dom.Element reqNodeDerived = (org.w3c.dom.Element) reqNodes.item(j);
					if(reqNodeDerived.getAttribute("id").toString().equals(rerivedId.toString()))
					{
						reqInfo.setDerivedId(reqNodeDerived.getElementsByTagName("intitule").item(0).getTextContent());
					}
				}
			}
			requirements.add(reqInfo);
			
		}
		
		return requirements;
	}
}
