/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;
import java.io.IOException;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public CSA mach;

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //roster: agents for each shift 6-14, 14-22, 22-6
        //1. agent -> consumer CSA
        //2. agent -> corporate only CSA
        //3. agent -> flexible corporate CSA
        int[][] roster = {{2,0,2},{2,0,2},{2,0,2}};

        String strategy_name = "Strategy1";

        //Strategy2 leaves the amount of corporate agents specified in the second column idle, in case the queue of corporate customers is empty
        if (roster[0][1]>0 || roster[1][1]>0 || roster[2][1]>0  ) {
            strategy_name = "Strategy2";
        }

        //Strategy1 uses only flexible corporate CSAs, so the second column of the roster matrix is 0 for all rows




        // n is the number of runs
        int n = 1;
        //number of days a single simulation is run
        int days = 10;
        for (int i = 0; i < n; i++) {
            int start_time = 6 * 60 * 60;
            int sim_duration = days * 24 *60 * 60;
            int shift_duration = 8 * 60 * 60;

            // Create an eventlist
            CEventList l = new CEventList(start_time); // start time is at 6 o clock first day

            // Two queues for the csa- one for consumers, one for the corporate
            Queue q_con = new Queue();
            Queue q_cor = new Queue();

            //type 0 means consumer calls and type 1 means corporate calls
            Source calls_consumer = new Source(q_con, l, "Source 1", 0);
            Source calls_corporate = new Source(q_cor, l, "Source 2", 1);

            // A sink
            Sink si = new Sink("Sink 1");

            //init the shifts
            int amount_shifts = sim_duration / shift_duration + 1;
            int shift_end = start_time;
            for(int j = 0; j < amount_shifts; j++){
                shift_end += shift_duration;
                int shift_type = j % 3;
                l.add(new Shift(q_con, q_cor, si, l, shift_end, roster[shift_type][0], roster[shift_type][1], roster[shift_type][2]),0,shift_end - shift_duration);
            }

            // start the eventlist
            l.start(start_time + sim_duration);

            //save the data
            si.toMatrixFile(strategy_name+" informationCalls" +  i + ".csv");
            si.toWaitTimeFileConsumer(strategy_name+" waitingTimesConsumer" + i + ".csv");
            si.toWaitTimeFileCorporate(strategy_name+" waitingTimesCorporate" + i + ".csv");

            int cost = (roster[0][0]  + roster[1][0] + roster[2][0])* 8 * 35 + (roster[0][1] + roster[1][1] + roster[2][1] +roster[0][2] + roster[1][2]+ roster[2][2]) * 8 * 60;
            System.out.println("________________________"+"Cost per day: "+cost+"________________________");

        }

    }
}
