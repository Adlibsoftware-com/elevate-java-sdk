import java.net.URL;
import java.util.List;

import com.adlib.elevate.authorize.AES;
import com.adlib.elevate.client.ArrayOflong;
import com.adlib.elevate.client.Payload;
import com.adlib.elevate.integration.Common;
import com.adlib.elevate.integration.JobManagementServiceClient;
import com.adlib.elevate.integration.ProcessedJobResponse;
import com.adlib.elevate.integration.Settings;

public class JavaDocSample {

	/**
	 * Used for javadoc Overview.html
	 */
	public static void sample(String[] args) {
		try {
			Settings settings = new Settings();
			// Replace with Repository that has been setup in Adlib Vision Console
			settings.setRepositoryName("CustomIntegration");
			// replace [computer] with Adlib Service Server fully qualified name
			settings.setJobManagementServiceWsdlUrl(new URL("https://[computer]:55583/Adlib/Services/JobManagement.svc?wsdl"));
			settings.setTokenServiceUrl(new URL("https://[computer]:8088/connect/token"));
	
			// should be AD/LDAP username that has access within Adlib (e.g. Vision console credentials)
			settings.setTokenServiceUsername("adlibadmin");
			// secret key should be unique and kept safe (keystore, etc.)
			settings.setSecretKey("s3cr3tk3y!");
			// example where plain text password is passed in (normally this would already be encrypted or retrieved from keystore)
			String encryptedPassword = AES.encrypt(args[0]);
			settings.setTokenServiceEncryptedPassword(encryptedPassword); 
	
			JobManagementServiceClient client = new JobManagementServiceClient(settings, true);
	
			ArrayOflong jobIds = new ArrayOflong();
	
			// TODO: fill in payload information
			Payload inputPayload = new Payload();
	
			// submit job
			long jobId = client.getJobManagement().submitJob(settings.getRepositoryName(), inputPayload);
			jobIds.getLong().add(jobId);
	
			// wait for it to complete
			List<ProcessedJobResponse> processedJobs = client.waitForJobsToProcess(jobIds, settings.getDefaultTimeout(),
									settings.getDefaultPollingInterval());
	
			// examine output payload from processedJobs and copy files etc.
			// ...
			
			// complete jobs
			client.completeJobs(processedJobs);
			
			
			processedJobs.clear();
			
			// sample synchronous job
			processedJobs.add(
					client.submitSynchronousJob(
							settings.getRepositoryName()
							, inputPayload
							, settings.getDefaultTimeout()
							, settings.getDefaultPollingInterval()
							, false));
			
			// examine output payload from processedJobs and copy files etc.
			// ...
			
			// complete jobs
			client.completeJobs(processedJobs);
			
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println(Common.getFriendlyError(e));
			System.exit(1);
		}
	}
}
