package virtualMachineLauncher;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;

import java.io.*;
import java.util.Map;

public class VirtualMachineLauncher {

    class StreamRedirectThread extends Thread {

    	private static final int BUFFER_SIZE = 2048;
    	
        private final Reader in;
        private final Writer out;

        public StreamRedirectThread(String name, InputStream in, OutputStream out) {
            super(name);
            this.in = new InputStreamReader(in);
            this.out = new OutputStreamWriter(out);
            setPriority(Thread.MAX_PRIORITY - 1);
        }

        @Override
        public void run() {
            try {
                char[] cbuf = new char[BUFFER_SIZE];
                int count;
                while ((count = in.read(cbuf, 0, BUFFER_SIZE)) >= 0) {
                    out.write(cbuf, 0, count);
                }
                out.flush();
            }
            catch (IOException exc) {
                System.err.println("Child I/O Transfer - " + exc);
            }
        }
    }

    private LaunchingConnector connector;
    private Map<String, Connector.Argument> args;
    private OutputStream outputStream = System.out;
    private OutputStream errorStream = System.err;

    
    public VirtualMachineLauncher() {
        for (LaunchingConnector connector : Bootstrap.virtualMachineManager().launchingConnectors()) {
            if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
                this.connector = connector;
                this.args = connector.defaultArguments();
            }
        }
        if(null == this.connector) {
        	throw new RuntimeException("Could not locate necessary Launching Connector (com.sun.jdi.CommandLineLaunch)");
        }
    }

    public VirtualMachineLauncher setHome(String home) {
        Connector.StringArgument arg = (Connector.StringArgument) args.get("home");
        arg.setValue(home);
        return this;
    }

    public VirtualMachineLauncher setOptions(String options) {
        Connector.StringArgument arg = (Connector.StringArgument) args.get("options");
        arg.setValue(options);
        return this;
    }

    public VirtualMachineLauncher setMain(String main) {
        Connector.StringArgument arg = (Connector.StringArgument) args.get("main");
        arg.setValue(main);
        return this;
    }

    public VirtualMachineLauncher setSuspend(boolean suspend) {
        Connector.BooleanArgument arg = (Connector.BooleanArgument) args.get("suspend");
        arg.setValue(suspend);
        return this;
    }

    public VirtualMachineLauncher setQuote(String quote) {
        Connector.StringArgument arg = (Connector.StringArgument) args.get("quote");
        arg.setValue(quote);
        return this;
    }

    public VirtualMachineLauncher setVMExec(String vmexec) {
        Connector.StringArgument arg = (Connector.StringArgument) args.get("vmexec");
        arg.setValue(vmexec);
        return this;
    }

    public VirtualMachineLauncher setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public VirtualMachineLauncher setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
        return this;
    }

    public VirtualMachine launch() throws VMStartException, IllegalConnectorArgumentsException, IOException {
        VirtualMachine vm = connector.launch(args);
        new StreamRedirectThread("redirect stdout", vm.process().getInputStream(), outputStream).start();
        new StreamRedirectThread("redirect stderr", vm.process().getErrorStream(), errorStream).start();
        return vm;
    }
}

