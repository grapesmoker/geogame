package edu.cmu.cs.cimds.geogame.client.util;

import java.util.Comparator;
import edu.cmu.cs.cimds.geogame.client.model.db.User;

public class UserDegreeComparator implements Comparator<User> {

	@Override
	public int compare(User user1, User user2) {
		int d1 = user1.getNeighbors().size();
		int d2 = user2.getNeighbors().size();
		
		if (d1 > d2) {
			return 1;
		}
		else if (d1 < d2) {
			return -1;
		}
		else {
			return 0;
		}
	}
};
