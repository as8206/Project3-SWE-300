package debugger;

import java.io.IOException;
import java.util.List;

import javax.swing.plaf.synth.SynthSplitPaneUI;
import javax.swing.tree.VariableHeightLayoutCache;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import virtualMachineLauncher.VirtualMachineLauncher;



public class TestClassRunner {

	static ReferenceType ref;
	static Field testStart;
	static Field testEnd;
	static VirtualMachine vm;
	
	public static void main(String[] args) throws VMStartException, IllegalConnectorArgumentsException, IOException, InterruptedException {
		final String cp = "/home/andrew/git/Project3-SWE-300/Project3_SWE300/bin/";
		final String main = "rangeClasses.MyJunit";
		boolean variableModifcationFlag = false;
		
		VirtualMachineLauncher launcher = new VirtualMachineLauncher()
				.setOptions("-cp " + cp).setMain(main);
		
		vm = launcher.launch();
		
		
		EventRequestManager mgr = vm.eventRequestManager();
		MethodExitRequest exitRequest = mgr.createMethodExitRequest();
		exitRequest.addClassFilter("rangeClasses.MyJunit");
		
		
		ClassPrepareRequest cpreq = mgr.createClassPrepareRequest();
		cpreq.enable();
		
		
//		ModificationWatchpointRequest modRequest = 
//				mgr.createModificationWatchpointRequest("testStart");

		exitRequest.enable();
		EventQueue queue = vm.eventQueue();
		
		while(true)
		{
			EventSet set = queue.remove();
			
			for (Event e : set)
			{
				if (e instanceof ModificationWatchpointEvent)
				{
					variableModifcationFlag = true;
				}
				if (e instanceof ClassPrepareEvent) 
				{
					ClassPrepareEvent cpe = (ClassPrepareEvent) e;
					ref = cpe.referenceType();
					if (ref.name().contains("MyJunit"))
					{
						System.out.println(ref);
						testStart = ref.fieldByName("testStart");
						testEnd = ref.fieldByName("testEnd");
						ModificationWatchpointRequest modRequestStart = 
								mgr.createModificationWatchpointRequest(testStart);
						ModificationWatchpointRequest modRequestEnd = 
								mgr.createModificationWatchpointRequest(testEnd);
						modRequestStart.enable();
						modRequestEnd.enable();	
					}
				}
				if (e instanceof MethodExitEvent && variableModifcationFlag==true)
				{
					process((MethodExitEvent) e);
					variableModifcationFlag = false;
				}
			}
			set.resume();
		}
	}

	private static void process(Event e) {
		e.thread.frames(0).thisObject();
	}

}
