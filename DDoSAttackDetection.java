import java.io.IOException;
import java.util.ArrayList;

public class DDoSAttackDetection {
	private static long THRES_NORMAL = 100; // this is standard amount of traffic (we can't flag a DDoS event below this threshold).
	private static long DDOS_THRES= 1000; // this is threshold that tells us that the current change has been drastic and we are under DDoS
	
	private static ArrayList<Epoch> data = null;
	private static ArrayList<DDoS> detectedAttacks = null;
	
	public void solve() throws IOException {
		 /*  we will iterate throughout the epochs while storing few statistics about the data up to the current point.
		   The statistics are explained below. We also have to take care not to compute these using values during DDoS
		   since this would bias them. 
		   Note: this approach could be useful for real-time detection with huge data */

		
		  /* current weightened mean at index i will be equal to d[i] / 2 + d[i - 1] / 4 + d[i - 2] / 8 + ...
 		   Note that sum of weights = 1/2 + 1/4 + 1/8 + ... = 1.
 		   Current standard deviation is weightened in the same way.
 		   This will take into consideration current trend of requests */
		
		/* we will calculate the initial values based on the firs two request amounts. 
		   TODO: this is unsafe since these could be already under DDoS
		   and so the long-term values of data should be used instead */
		double a = (double) data.get(0).requests;
		double b = (double) data.get(1).requests;
		
		double mean = (a + b) / 2.0;
		double dev = Math.sqrt(((a - mean) * (a - mean) + (b - mean) * (b - mean)) / 2.0);
		
		/* these are used to try and calculate NEW mean and dev */
		double meanCandidate, devCandidate;
		/* flags whether we are under attack */
		boolean attack = false;
		
		System.out.println("Log [ID : DDoS Value]");
		for (Epoch current : data) {
			double r = (double) current.requests;
			meanCandidate = (r + mean) / 2.0;
			devCandidate = Math.sqrt((dev * dev + (r - mean) * (r - mean)) / 2.0);
			
			/* this value represents the extent of a current change. For given sample
			   values during DDoS are very large, while normal values are  < 10) */
			long DDOSVALUE = (long) ((meanCandidate * (1 + devCandidate)) / (mean * (1 + dev)));
			
			System.out.println("ID " + current.ID + " : " + DDOSVALUE);
			if (r > THRES_NORMAL && DDOSVALUE > DDOS_THRES) {
				
				/* flag current attack and add him to detectedAttacks */
				if (!attack) {
					detectedAttacks.add(new DDoS(current.ID, current.ID));
				}
				else {
					detectedAttacks.get(detectedAttacks.size() - 1).end = current.ID;
				}
				attack = true;
			}
			else {
				attack = false;
				mean = meanCandidate;
				dev = devCandidate;
			}	
		}
		System.out.println("Detected DDoS attacks: ");
		System.out.println(detectedAttacks);
	}
	
	/* converts string with data */
	public static void parseData(String s) {
		data = new ArrayList<Epoch>();
		detectedAttacks = new ArrayList<DDoS>();
		
		String[] stripped = s.replaceAll("[^0-9,]", "").split(",");
		for (int i = 0; i < stripped.length / 2; i++) {
			data.add(new Epoch(Integer.parseInt(stripped[2 * i]), Long.parseLong(stripped[2 * i + 1])));
		}
	}
	
	public static void main(String[] args) throws IOException {
		// simple test case 
		String sample = "[[123456, 45],[123457, 46],[123458, 1000],"
				+ "[123459, 1129],[123460, 999],[123461, 47],[123462, 50],[123463, 67],[123464,35]"
				+ ",[123465, 50],[123466, 10000],[123467, 5000],[123468,60]]";
		parseData(sample);
		
		new DDoSAttackDetection().solve();
	}
	
	static class Epoch {
		int ID;
		long requests;
		
		public Epoch(int i, long r) {
			this.ID = i;
			this.requests = r;
		}
		
		public String toString() {
			return "[" + ID + ", " + requests + "]";
		}
	}
	
	static class DDoS {
		int start, end;
		
		public DDoS() {
			this.start = -1;
			this.end = -1;
		}
		
		public DDoS(int s, int e) {
			this.start = s;
			this.end = e;
		}
		
		public String toString() {
			return "[" + start + ", " + end + "]";
		}
	}
}
