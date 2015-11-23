import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class RankHotels {
	private static ArrayList<Hotel> allHotels;

	public static void main(String[] args) throws FileNotFoundException {
		allHotels = new ArrayList<RankHotels.Hotel>();
		User sampleUser = new User(0); // user with ID 0
		FileReader fr = new FileReader(new File("/sampleData.txt"));
		Scanner in = new Scanner(fr);
		
		int t = in.nextInt();
		in.nextLine();
		while (t-- > 0) {
			long time;
			String[] s = in.nextLine().split("\\s|,");
			time = Long.parseLong(s[0]);
			Hotel hh = new Hotel();
			for (int i = 1; i < s.length; i++) {
				if (s[i].equals(" ") || s[i].isEmpty()) continue;
				hh.addFacility(s[i].toLowerCase());
			}
			Booking bb = new Booking(time, hh);
			sampleUser.addBooking(bb);
		}
		
		t = in.nextInt();
		in.nextLine();
		while (t-- > 0) {
			String line = in.nextLine();
			Hotel h = new Hotel();
			for (String fac : line.split("\\s|,")) {
				h.addFacility(fac.toLowerCase());
			}
			allHotels.add(h);
		}

		in.close();
		sampleUser.getSuggestions();
	}

	static class User {
		int ID, numberOfBookings;
		ArrayList<Booking> bookings;
		HashMap<String, Double> templateHotel;

		public User(int ID) {
			this.ID = ID;
			numberOfBookings = 0;
			bookings = new ArrayList<RankHotels.Booking>();
		}

		public void addBooking(Booking booking) {
			this.numberOfBookings++;
			bookings.add(booking);
		}

		public void removeBooking(Booking booking) {
			if (this.bookings.contains(booking)) {
				this.bookings.remove(booking);
			}
		}

		// method to create a template of a recommended hotel
		public void createTemplate() {
			templateHotel = new HashMap<String, Double>();
			
			for (Booking booking : bookings) {
				// will need to calculate the time for each booking based on this
				double score = 0.1;
				// booking.timeInDays - how many days ago was the booking made
				score += (booking.timeInDays >= 24*30) ? 0.0 : 1 - ((double) (booking.timeInDays) / (24.0 * 30.0)); 
				
				for (String facility : booking.hotel.facilities) {
					if (!templateHotel.containsKey(facility)) {
						templateHotel.put(facility, score);
					} else {
						templateHotel.put(facility,
								templateHotel.get(facility) + score);
					}
				}
				
				double maxScore = 0.0;
				for (String fac : templateHotel.keySet()) {
					maxScore = Math.max(maxScore, templateHotel.get(fac));
				}
				for (String fac : templateHotel.keySet()) {
					templateHotel.put(fac, templateHotel.get(fac) / maxScore);
				}
			}
		}

		private double rankHotel(Hotel hotel) {
			double rank = 0.0;
			for (String facility : hotel.facilities) {
				if (templateHotel.containsKey(facility))
					rank += templateHotel.get(facility);
			}
			return rank;
		}

		public ArrayList<Hotel> getSuggestions() {
			// will return the list of 10 suggested hotels
			int numberOfSuggestions = 10;
			this.createTemplate();
			System.out.println("User preference of facilities: \n");
			for (String fac : templateHotel.keySet()){
				System.out.printf("Score: %5f     Facility: %s%n", templateHotel.get(fac), fac);
			}
			System.out.println();

			ArrayList<RankedHotel> suggestions = new ArrayList<RankHotels.User.RankedHotel>();
			double rankOfLast = -1.0;

			for (Hotel hotel : allHotels) {
				RankedHotel rankedHotel = new RankedHotel(hotel,
						this.rankHotel(hotel));

				if (suggestions.size() >= numberOfSuggestions
						&& rankedHotel.rank < rankOfLast)
					continue;
				suggestions.add(rankedHotel);
				Collections.sort(suggestions);
				rankOfLast = suggestions.get(suggestions.size() - 1).rank;
			}

			System.out.println("The best fit hotel suggestions (each line represents hotel): \n");
			ArrayList<Hotel> hotelSuggestions = new ArrayList<RankHotels.Hotel>();
			for (RankedHotel rh : suggestions) {
				System.out.println(rh.hotel);
				hotelSuggestions.add(rh.hotel);
			}
			return hotelSuggestions;
		}

		private class RankedHotel implements Comparable<RankedHotel> {
			Hotel hotel;
			double rank;

			public RankedHotel(Hotel hotel, double rank) {
				this.hotel = hotel;
				this.rank = rank;
			}

			@Override
			public int compareTo(RankedHotel rh) {
				return Double.compare(rh.rank, this.rank);
			}
		}
	}

	static class Booking {
		long timeInDays; //booking.timeInDays - how many days ago was the booking made
		Hotel hotel;

		public Booking(long time, Hotel hotel) {
			this.timeInDays = time;
			this.hotel = hotel;
		}
	}

	static class Hotel {
		HashSet<String> facilities;

		public Hotel() {
			facilities = new HashSet<String>();
		}

		public void addFacility(String facility) {
			facilities.add(facility);
		}

		public void removeFacility(String facility) {
			if (facilities.contains(facility)) {
				facilities.remove(facility);
			}
		}

		public String toString() {
			return facilities.toString();
		}
	}
}
