package requirementserver;

public class RequirementInfo {
	
	private String intitule = null;
	private String description = null;
	private String derivedId = null;
	private String id = null;
	
	public RequirementInfo(){}
	
	/**
	 * Getters 
	 * @return
	 */
	
	public String getId()
	{
		return this.id;
	}
	
	public String getIntitule()
	{
		return this.intitule;
	}

	public String getDescription()
	{
		return this.description;
	}
	
	public String getDerivedId()
	{
		return this.derivedId;
	}
	
	/**
	 * Setters 
	 * @return
	 */
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setDerivedId(String derivedId)
	{
		this.derivedId = derivedId;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setIntitule(String intitule)
	{
		this.intitule = intitule;
	}

}
