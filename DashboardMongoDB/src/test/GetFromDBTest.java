package test;

import java.util.ArrayList;
import applicationCore.Robot;
import database.dao.concrete.ClusterDAO;
/*
 * 
 * 
 *  CLASS FOR TEST PURPOSE
 * 
 * 
 */
public class GetFromDBTest {
	public static void main(String[] args) {

		ArrayList<ArrayList<Robot>> robots = new ArrayList<>();
		ArrayList<Robot> robot = new ClusterDAO().getRobots(91);
		
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < 100; i++) {
			robot = new ClusterDAO().getRobots(i);
			robots.add(robot);
		}
		
		long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println(robots);
        System.out.println(robots.size());
        System.out.println(robots.get(0).size());
        System.out.println(totalTime);
	}
}
