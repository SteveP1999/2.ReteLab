package hu.bme.mit.yakindu.analysis.workhere;

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
}
