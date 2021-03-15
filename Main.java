package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;
import org.yakindu.sct.model.stext.stext.impl.EventDefinitionImpl;
import org.yakindu.sct.model.stext.stext.impl.VariableDefinitionImpl;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		List<String> event = new ArrayList<String>();
		List<String> var = new ArrayList<String>();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				for(Transition t : state.getOutgoingTransitions())
				{
					int i = 0;
					State out = (State) t.getTarget();
					System.out.println(state.getName() + "->" + out.getName() + "\t");
					if(out.getOutgoingTransitions().isEmpty() == true)
					{
						System.out.println("Ez egy csapda állapot: " + out.getName());
					}
					if(out.getName() == "")
					{
						System.out.println("Legyen jelen állapot neve mondjuk: NewState" + i);
						i++;
					}
				}
			}
			else if(content instanceof EventDefinitionImpl)
			{
				EventDefinitionImpl edi = (EventDefinitionImpl) content;
				System.out.println("Bemenő esemény: " + edi.getName());
				event.add(edi.getName());
			}
			else if (content instanceof VariableDefinition)
			{
				VariableDefinitionImpl vdi = (VariableDefinitionImpl) content;
				System.out.println("Belső változó" + vdi.getName());
				var.add(vdi.getName());
			}			
		}
		System.out.println(print(var));
		System.out.println(generator(event, var));
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	static String print(List<String> var)
	{
		String printedText = null;
		printedText += "public static void print(IExampleStatemachine s) {";
		for(int i=0; i<var.size(); i++)
		{
			printedText += "System.out.println(\"W = \" + s.getSCInterface().get" + var.get(i) + "(()";
		}
		printedText += "}";
		return printedText;
	}
	
	public static String generator(List<String> events, List<String> var) 
	{
		String result = "";
		result += "public class RunStatechart {\n\n";
		result += "\tpublic static void main(String[] args) throws IOException {\n";
		result += "\t\tboolean condition = true;\n";
		result += "\t\tBufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\n";
		result += "\t\tExampleStatemachine s1 = new ExampleStatemachine();\n";
		result += "\t\ts1.setTimer(new TimerService());\n";
		result += "\t\tRuntimeService.getInstance().registerStatemachine(s1, 200);\n";	
		result += "\t\ts1.init();\n";
		result += "\t\ts1.enter();\n";
		result += "\t\ts1.runCycle();\n";
		result += "\t\twhile(condition == true)\n";
		result += "\t\t\t{\n";
		result += "\t\t\tString str = reader.readLine();\n";
		result += "\t\t\tif(str.equals(\"start\"))\n";
		result += "\t\t\t{\n";
		result += "\t\t\t\ts1.raiseStart();\n";
		result += "\t\t\t\ts1.runCycle();\n";
		result += "\t\t\t\tprint(s1);\n";
		result += "\t\t\t}\n";
		for (int i = 0; i < events.size(); i++) {
			result += "\t\t\t\tcase \"" + events.get(i) + "();\n";
			result += "\t\t\t\ts1.raiseStart" + events.get(i) + "\":\n";
			result += "\t\t\t\ts1.runCycle();\n";
			result += "\t\t\t\tprint(s1);\n";
		}
		result += "\t\t\tif(str.equals(\"white\"))\n";
		result += "\t\t\t{\n";
		result += "\t\t\t\ts1.raiseWhite();\n";
		result += "\t\t\t\ts1.runCycle();\n";
		result += "\t\t\t\tprint(s1);\n";
		result += "\t\t\t}\n";
		result += "\t\t\tif(str.equals(\"black\"))\n";
		result += "\t\t\t{\n";
		result += "\t\t\t\ts1.raiseBlack();\n";
		result += "\t\t\t\ts1.runCycle();\n";
		result += "\t\t\t\tprint(s1);\n";
		result += "\t\t\t}\n";
		result += "\t\t\tif(str.equals(\"exit\"))\n";
		result += "\t\t\t{\n";
		result += "\t\t\tcondition = false;\n";
		result += "\t\t\tSystem.exit(0);\n";
		result += "\t\t\t}\n";
		result += "\t\t}\n";
		result += "\t}\n";	
		return result;
		}
	}

