package requirementserver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker extends ServiceTracker
{

	public HttpServiceTracker(BundleContext bundleContext)
	{
		super(bundleContext, HttpService.class.getName(),null );
		
	}
	
	public Object addingService(ServiceReference reference)
	{
		HttpService httpService = (HttpService) super.addingService(reference);
		if( httpService ==  null)
		{
			return null;
		}
		
		try
		{
			System.out.println("Registering RequirementService at /RequirementServer");
			httpService.registerServlet("/RequirementServer", new RequirementServer(), null, null);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return httpService;
	}
	
	public void removedService(ServiceReference reference, Object service)
	{
		HttpService httpService = (HttpService) service;
		System.out.println("Unregistering RequirementService at /RequirementServer");
		httpService.unregister("/RequirementServer");
		super.removedService(reference, service);
		
	}
}
