package com.dbcool.api.liht;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Transient {

	static class SpecialSerial implements Serializable{
		transient int y=7;
		static  int z=9;
	}
	public static void main(String[] args) {
		SpecialSerial s = new SpecialSerial();
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("myFile"));
			os.writeObject(s);
			os.close();
			System.out.println(++s.z +" ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
