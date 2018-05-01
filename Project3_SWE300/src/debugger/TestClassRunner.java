package debugger;

import java.io.IOException;
import java.util.List;

import javax.swing.plaf.synth.SynthSplitPaneUI;
import javax.swing.tree.VariableHeightLayoutCache;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.ObjectReference;
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
	static ObjectReference obj;
	
	public static void main(String[] args) throws VMStartException, IllegalConnectorArgumentsException, IOException, InterruptedException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
		final String cp = "/home/andrew/git/Project3-SWE-300/Project3_SWE300/bin/";
		final String main = "rangeClasses.MyJunit";
		
		boolean variableModifcationFlag = false;
		boolean prepped = false;
		
		VirtualMachineLauncher launcher = new VirtualMachineLauncher()
				.setOptions("-cp " + cp).setMain(main);
		
		vm = launcher.launch();
		EventRequestManager mgr = vm.eventRequestManager();
		
		ClassPrepareRequest cpreq = mgr.createClassPrepareRequest();
		cpreq.enable();
		
		EventQueue queue = vm.eventQueue();
		
		while(true)
		{
			EventSet set = queue.remove();
			
			for (Event e : set)
			{
				if (prepped == false && e instanceof ClassPrepareEvent) 
				{
					ClassPrepareEvent cpe = (ClassPrepareEvent) e;
					ref = cpe.referenceType();
					
					if (ref.name().contains("MyJunit"))
					{						
						testStart = ref.fieldByName("testStart");
						testEnd = ref.fieldByName("testEnd");
						
						ModificationWatchpointRequest modRequestStart = 
								mgr.createModificationWatchpointRequest(testStart);
						ModificationWatchpointRequest modRequestEnd = 
								mgr.createModificationWatchpointRequest(testEnd);
						
						MethodExitRequest exitRequest = mgr.createMethodExitRequest();
						exitRequest.addClassFilter("rangeClasses.MyJunit");
						
						modRequestStart.enable();
						modRequestEnd.enable();	
						exitRequest.enable();
						prepped = true;
					}
				}
				
				if (e instanceof ModificationWatchpointEvent)
				{
					variableModifcationFlag = true;
				}
				
				if (variableModifcationFlag==true &&  e instanceof MethodExitEvent)
				{
					if (((MethodExitEvent)e).method().name().startsWith("test"))
					{
						process((MethodExitEvent) e);
						variableModifcationFlag = false;
					}
				}
			}
			set.resume();
		}
	}
/** 
 * Resets the testStart and testEnd back to their original values for further testing
 * @param e : the method exit event
 * @throws InvalidTypeException
 * @throws ClassNotLoadedException
 * @throws IncompatibleThreadStateException
 */
	private static void process(MethodExitEvent e) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException
	{
		ObjectReference obj = e.thread().frame(0).thisObject();
		
		IntegerValue startInt = vm.mirrorOf(100);
		IntegerValue endInt = vm.mirrorOf(200);
		
		obj.setValue(testStart, startInt);
		obj.setValue(testEnd, endInt);
	}

}
