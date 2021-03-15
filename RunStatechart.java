package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;



public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		//ExampleStatemachine s = new ExampleStatemachine();
		//s.setTimer(new TimerService());
		//RuntimeService.getInstance().registerStatemachine(s, 200);
		//s.init();
		//s.enter();
		//s.runCycle();
		//print(s);
		//s.raiseStart();
		//s.runCycle();
		//System.in.read();
		//s.raiseWhite();
		//s.runCycle();
		//print(s);
		//System.exit(0);
		
		
		boolean condition = true;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ExampleStatemachine s1 = new ExampleStatemachine();
		s1.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s1, 200);
		s1.init();
		s1.enter();
		s1.runCycle();
		while(condition == true)
		{
			String str = reader.readLine();
			if(str.equals("start"))
			{
				s1.raiseStart();
				s1.runCycle();
				print(s1);
			}
			if(str.equals("white"))
			{
				s1.raiseWhite();
				s1.runCycle();
				print(s1);
			}
			if(str.equals("black"))
			{
				s1.raiseBlack();
				s1.runCycle();
				print(s1);
			}
			if(str.equals("exit"))
			{
				condition = false;
				print(s1);
				System.exit(0);
			}
		}
	}
	
	

	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
	}
}
