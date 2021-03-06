package simulation;

import java.util.ArrayList;

/**
 *	Queue that stores calls until they can be handled on a csa
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Queue implements CallAcceptor
{
	/** List in which the calls are kept */
	private ArrayList<Call> row;
	/** Requests from csa that will be handling the calls */
	private ArrayList<CSA> requests;

	/**
	 * number of CSA corporate to kept idle to handle incoming corporate calls
	 */
	public int k;

	/**
	*	Initializes the queue and introduces a dummy csa
	*	the csa has to be specified later
	*/
	public Queue()
	{
		row = new ArrayList<>();
		requests = new ArrayList<>();
	}

	/**
	*	Asks a queue to give a call to a csa
	*	True is returned if a call could be delivered; false if the request is queued
	*/
	public boolean askCall(CSA csa)
	{
		int cnt = 0;
		for (CSA c : this.getRequests()){
			if (c.getType() == 1) cnt +=1;
		}
		for (CSA c : this.getRequests()){
			if (c.getType() == 1) cnt +=1;
		}
		System.out.println(cnt + " "+CSA.corpCsaIdleCounter);

		//System.out.println("Ask Call");
		// This is only possible with a non-empty queue
		if(row.size()>0)
		{
			// If the csa accepts the call
			if(csa.giveCall(row.get(0)))
			{
				row.remove(0);// Remove it from the queue
				return true;
			}
			else{
				//System.out.println(csa.getName()+" call request rejected");
				return false; // csa rejected; don't queue request
			}
		}
		else
		{
			if (!requests.contains(csa)){
				requests.add(csa);
			}
			return false; // queue request
		}
	}
	
	/**
	*	Offer a call to the queue
	*	It is investigated whether a csa wants the call, otherwise it is stored
	*/
	public boolean giveCall(Call p)
	{
		// Check if there are any call requests
		if(requests.size()<1)
			row.add(p); // Otherwise store it
		else
		{
			boolean delivered = false;

			ArrayList<CSA> list_rejected = new ArrayList<>();
			while(!delivered & (requests.size()>0))
			{
				delivered = requests.get(0).giveCall(p);
				// remove the request regardless of whether or not the call has been accepted
				CSA removed = requests.remove(0);
				if(!delivered)
					list_rejected.add(removed);// Otherwise store it
			}
			if(!delivered)
				row.add(p); // Otherwise store it

			for (CSA csa : list_rejected){
				if (!requests.contains(csa)){
					requests.add(csa);
				}
			}
		}
		return true;
	}

	public ArrayList<CSA> getRequests(){
		return requests;
	}

}