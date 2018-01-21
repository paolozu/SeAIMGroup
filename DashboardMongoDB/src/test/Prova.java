package test;

import java.sql.Timestamp;

public class Prova {

	public static void main(String[] args) {
		
		System.out.println(new Timestamp(new Timestamp(System.currentTimeMillis()).getTime() - 10800000));
		System.out.println(new Timestamp(System.currentTimeMillis()).getTime());
	}

}
