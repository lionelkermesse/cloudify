/***************
 * Cloud configuration file for the Dynamic-Bring-Your-Own-Node (dynamic-byon) cloud.
 * See org.cloudifysource.dsl.cloud.Cloud for more details.
 *
 * @author yael
 *
 */
 
cloud {
	// Mandatory. The name of the cloud, as it will appear in the Cloudify UI.
	name = "dynamic-byon"
	
	/********
	 * General configuration information about the cloud driver implementation.
	 */
	configuration {
		// The cloud-driver implementation class.
		className "org.cloudifysource.esc.driver.provisioning.byon.DynamicByonProvisioningDriver"
		// Mandatory. The template name for the management machines.
		managementMachineTemplate "SMALL_LINUX"
		// Optional. Indicates whether internal cluster communications should use the machine private IP. Defaults to true.
		connectToPrivateIp true
		//Indicates whether communications with the management servers should use the machine private IP.
		bootstrapManagementOnPublicIp false
	}

	/*************
	 * Provider specific information.
	 */
	provider {
		// Mandatory. The name of the provider.
		provider "dynamic-byon"
		
	
		// Optional. The HTTP/S URL where cloudify can be downloaded from by newly started machines. Defaults to downloading the
		// cloudify version matching that of the client from the cloudify CDN.
		// Change this if your compute nodes do not have access to an internet connection, or if you prefer to use a
		// different HTTP server instead.
		// IMPORTANT: the default linux bootstrap script appends '.tar.gz' to the url whereas the default windows script appends '.zip'.
		// Therefore, if setting a custom URL, make sure to leave out the suffix.
		// cloudifyUrl "http://repository.cloudifysource.org/org/cloudifysource/2.3.0-RELEASE/gigaspaces-cloudify-2.3.0-ga-b3500.zip"

		
		// Mandatory. The prefix for new machines started for servies.
		machineNamePrefix "cloudify_agent_"
		// Optional. Defaults to true. Specifies whether cloudify should try to deploy services on the management machine.
		// Do not change this unless you know EXACTLY what you are doing.
		
		managementOnlyFiles ([])
		
		// Optional. Logging level for the intenal cloud provider logger. Defaults to INFO.
		sshLoggingLevel "INFO"
		// Mandatory. Name of the new machine/s started as cloudify management machines. 
		managementGroup "cloudify_manager"
		// Mandatory. Number of management machines to start on bootstrap-cloud. In production, should be 2. Can be 1 for dev.
		numberOfManagementMachines 1
		
		reservedMemoryCapacityPerMachineInMB 1024
	}
	
	/*************
	 * Cloud authentication information
	 */
	user {		
		
	}
	
	/***********
	 * Cloud machine templates available with this cloud. 
	 */
	templates ([
				// Mandatory. Template Name.
				SMALL_LINUX : template{
				// Mandatory. Amount of RAM available to machine.
				machineMemoryMB 1600
				// Mandatory. Files from the local directory will be copied to this directory on the remote machine.
				remoteDirectory "/tmp/gs-files"
				// Optional. template-generic credentials. Can be overridden by specific credentials on each node, in the nodesList section.
				username username
				password password
				
				// Mandatory. All files from this LOCAL directory will be copied to the remote machine directory.
				localDirectory "upload"
				
				// Mandatory for DYNAMIC-BYON.
				custom ([
					// Mandatory for DYNAMIC-BYON. startManagementMachines closure returns a list of started machines used as management machines.
					"startManagementMachines" : {
													return Arrays.asList(managementMachines.split(","))
												},
					// Mandatory for DYNAMIC-BYON. startMachine closure returns the ip of a started machine.
					"startMachine" : 			{
													return startMachineIP
												},
					// Mandatory for DYNAMIC-BYON. stopMachine closure stops the machine with the given ip.
					"stopMachine" : 			{ ip ->
													System.setProperty("stopMachine", ip)
												},
					// Mandatory for DYNAMIC-BYON. stopManagementMachines closure stops all management machines.
					"stopManagementMachines" :	{
													System.setProperty("stopManagementMachines", managementMachines)
												}
				])
					
				
				// enable sudo.
				privileged true
				
				}
	])
	
	/*****************
	 * Optional. Custom properties used to extend existing drivers or create new ones. 
	 */
	 // Optional. Sets whether to delete the remoteDirectory created by the cloud driver, when shutting down.
	custom ([
		"cleanGsFilesOnShutdown": "true",
		"itemsToClean": ([
			"/tmp/gs-files/gigaspaces.tar.gz"
		])
	])

}