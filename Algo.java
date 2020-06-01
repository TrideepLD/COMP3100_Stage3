import java.util.ArrayList;

public class Algo {

	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;

	Algo(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	// STATES: 0=inactive, 1=booting, 2=idle, 3=active, 4=unavailable

	/*
	 * Best-fit algorithm implemented by Bradley Kenny. This algorithm iterates
	 * through the ArrayList and looks for the server that will be the 'best' for
	 * our given job. This is determined by calculating the fitness value and
	 * ensuring the server has enough resources to be able to handle the job.
	 */
	public Server bestFit(Job job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best = null;
		Boolean found = false;

		for (Server serv : servers) {
			if ((serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory)) {
				int fitnessValue = serv.coreCount - job.cpuCores;
				if ((fitnessValue < bestFit) || (fitnessValue == bestFit && serv.availableTime < minAvail)) {
					bestFit = fitnessValue;
					minAvail = serv.availableTime;
					if (serv.state == 0 || serv.state == 1 || serv.state == 2 || serv.state == 3) {
						found = true;
						best = serv;
					}
				}
			}
		}
		if (found) {
			return best;
		} else {
			// We only want to get here if there is nothing calculated above.
			int bestFitAlt = Integer.MAX_VALUE;
			Server servAlt = null;
			for (Server serv : xmlServers) {
				int fitnessValueAlt = serv.coreCount - job.cpuCores;
				if (fitnessValueAlt >= 0 && fitnessValueAlt < bestFitAlt && serv.disk >= job.disk
						&& serv.memory >= job.memory) {
					bestFitAlt = fitnessValueAlt;
					servAlt = serv;
				}
			}
			servAlt.id = 0; // If this isn't zero, server thinks it doesn't exist.
			return servAlt;
		}
	}

	/*
	 * First-Fit algorithm implemented by John Kim. Iterate through sorted servers,
	 * compare each jobs' requirements to the servers' capacity and if it can run
	 * the job, assign it to that server. otherwise, look for the next active server
	 * that can run the job and assign it, regardless of how ill-fitting the job
	 * size to the server size.
	 */
	public Server firstFit(Job job) {
		Server[] sortedServers = sortByID(xmlServers);

		// Iterate through the sorted servers and check for the server's available
		// resources and if the server has sufficient amount of resources, assign
		// the job to the server by returning the server which is then passed to
		// the ds-server.
		for (Server serv : sortedServers) {
			for (Server serv2 : servers) {
				if ((serv.type).equals(serv2.type)) {
					if (serv2.coreCount >= job.cpuCores && serv2.disk >= job.disk && serv2.memory >= job.memory
							&& (serv.state == 0 || serv.state == 1 || serv.state == 2 || serv.state == 3)) {
						return serv2;
					}
				}
			}
		}
		// For when there aren't any good fit to for job-server
		// iterate through the whole arrayList of servers and find the next active
		// server that can run the job.
		Server temp = null;
		for (Server serv : xmlServers) {
			
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory) {
				temp = serv;
				temp.id = 0; // If this isn't zero, server thinks it doesn't exist.
				return temp;
			}
		}
		return null;
	}

	public Server firstShortJob(Job job) {
		
		/*

			++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			++++++++++++++++++++++++First Implementation Below++++++++++++++++++++++++++++++
			++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		*/

		Server[] sortedServers = sortByTime(xmlServers);

		// Iterate through the sorted servers and check for the server's available
		// resources and if the server has sufficient amount of resources, assign
		// the job to the server by returning the server which is then passed to
		// the ds-server.
		for (Server serv : sortedServers) {
			for (Server serv2 : servers) {
				if ((serv.type).equals(serv2.type)) {
					if (serv2.coreCount >= job.cpuCores && serv2.disk >= job.disk && serv2.memory >= job.memory && serv2.availableTime >= job.estRuntime
							&& (serv.state == 0 || serv.state == 1 || serv.state == 2 || serv.state == 3)) {
						return serv2;
					}
				}
			}
		}
		// For when there aren't any good fit to for job-server
		// iterate through the whole arrayList of servers and find the next active
		// server that can run the job.
		Server temp = null;
		for (Server serv : xmlServers) {
			
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory) {
				temp = serv;
				temp.id = 0; // If this isn't zero, server thinks it doesn't exist.
				return temp;
			}
		}
		return null;

		/*

			++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			+++++++++++++++++++++++Second Implementation Below++++++++++++++++++++++++++++++
			++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		*/

		// int minAvail = Integer.MAX_VALUE;
		// Server newServer = null;

		// for (Server serv : servers) {
		// 	if ((serv.coreCount >= job.cpuCores && serv.memory >= job.memory && serv.disk >= job.disk && serv.availableTime < minAvail 
		// 		&& (serv.state == 0 || serv.state == 1 || serv.state == 2 || serv.state == 3))) {
		// 		// The fitness value of a job to a server is defined as the difference between the number of cores the job requires and that in the server.
		// 			minAvail = serv.availableTime;
		// 			// found = true;
		// 			newServer = serv;
		// 			return newServer;
		// 	}

		// }
		// // We only want to get here if there is nothing calculated above.
		// Server servAlt = null;
		// for (Server serv : xmlServers) {
		// 	// int fitnessValue = serv.coreCount - job.cpuCores;
		// 	if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory) {
		// 		servAlt = serv;
		// 		servAlt.id = 0; // If this isn't zero, server thinks it doesn't exist.
		// 		return servAlt;
		// 	}
		// }
		// return null;

	}

	/*
	 * Bubble sort function, based off GeeksForGeeks implementation Takes in an
	 * arrayList of servers which are sorted by the coreCount, which dictate the
	 * serverType and size.
	 */
	public Server[] sortByID(Server[] servArr) {
		int n = servArr.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (servArr[j].coreCount > servArr[j + 1].coreCount) {
					Server temp = servArr[j];
					servArr[j] = servArr[j + 1];
					servArr[j + 1] = temp;
				}
			}
		}
		return servArr;
	}

	//sort by Time
	public Server[] sortByTime(Server[] servArr) {
		int n = servArr.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (servArr[j].availableTime > servArr[j + 1].availableTime) {
					Server temp = servArr[j];
					servArr[j] = servArr[j + 1];
					servArr[j + 1] = temp;
				}
			}
		}
		return servArr;
	}

	/*
	 * Worst-fit algorithm implemented by Mark Smith. This algorithm iterates
	 * through the ArrayList and looks for the server that will be have the 'worst'
	 * value for the server, and hence likely have the costliest result. This
	 * implementation uses a tracked fitness value to compare the servers to the
	 * given job, returning the one with the largest gap.
	 */
	public Server worstFit(Job job) {
		// Establish flags and fit variables to track fitness scores and servers.
		int worstFit = Integer.MIN_VALUE;
		int altFit = Integer.MIN_VALUE;
		Server worst = null;
		Server next = null;
		Boolean worstFound = false;
		Boolean nextFound = false;

		//For each server
		for (Server s : servers) {
			if (s.coreCount >= job.cpuCores && s.disk >= job.disk && s.memory >= job.memory && (s.state == 0 || s.state == 2 || s.state == 3)) {
				//calculate the fitness value
				int fitValue = s.coreCount - job.cpuCores;
				//if fitness > worstFit is available then set worstFit
				if (fitValue > worstFit && (s.availableTime == -1 || s.availableTime == job.submitTime)) {
					worstFit = fitValue;
					worstFound = true;
					worst = s;
				//otherwise set altFit
				} else if (fitValue > altFit && s.availableTime >= 0) {
					altFit = fitValue;
					nextFound = true;
					next = s;
				}
			}
		}
		// if worstFit, return it
		if (worstFound) {
			return worst;
		//otherwise, if altFit, return it
		} else if (nextFound) {
			return next;
		}

		//Return the worst-fit active server based on initial resource capcity
		int lowest = Integer.MIN_VALUE;
		Server curServer = null;
		for (Server s : xmlServers) {
			int fit = s.coreCount - job.cpuCores;
			if (fit > lowest && s.disk >= job.disk && s.memory >= job.memory) {
				lowest = fit;
				curServer = s;
			}
		}
		curServer.id = 0; //The server doesn't think it exists unless its 0.
		return curServer;
	}


}

