package si.gos.transpiler.core.builder;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import si.gos.eclipse.exec.ExecutionResponseAdapter;

public class ConsoleResponseHandler extends ExecutionResponseAdapter {

	private MessageConsole console;
	
	public ConsoleResponseHandler() {
		console = findConsole("Transpiler");
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// no console found, so create a new one
		MessageConsole console = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { console });
		return console;
	}
	
	public void executionAboutToStart() {
		console.clearConsole();
	}
	
	public void executionMessage(String message) {
		logMessage(message);
	}

	public void executionFailed(String response, Exception e) {
		logError("Transpiling failed: " + e.getMessage());
	}
	
	public void executionError(String message) {
		logError("Error while transpiling: " + message);
	}
	
	private void logError(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageConsoleStream out = console.newMessageStream();
				out.setColor(new Color(Display.getDefault(), 255, 0, 0));
				out.println(message);
			}
		});
	}
	
	private void logMessage(String message) {
		MessageConsoleStream out = console.newMessageStream();
		out.println(message);
	}

}
