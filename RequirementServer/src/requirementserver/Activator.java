package requirementserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	 private HttpServiceTracker serviceTracker;

	
	public void start(BundleContext bundleContext) throws Exception {
		serviceTracker = new HttpServiceTracker(bundleContext);
        serviceTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		serviceTracker.close();
        serviceTracker = null;
	}

}
