package net.freehal.ui.shell;

import java.io.IOException;

import net.freehal.core.util.LogUtils;
import net.freehal.ui.common.Configuration;
import net.freehal.ui.common.Extension;
import net.freehal.ui.common.Extensions;
import net.freehal.ui.common.MainLoopListener;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class Loader implements Extension, MainLoopListener {

	private InteractiveShell shell = null;

	@SuppressWarnings("static-access")
	@Override
	public void addOptions(Options options) {
		options.addOption(OptionBuilder.withLongOpt("shell")
				.withDescription("opens an interactive shell for answering").create("s"));
	}

	@Override
	public void parseConfig(Configuration line) {
		if (line.hasOption("shell")) {
			shell = new InteractiveShell();
			Extensions.registerMainLoop(this);
		}
	}

	@Override
	public void loop() {
		if (shell != null) {
			try {
				shell.loop(System.in, System.out);
			} catch (IOException ex) {
				LogUtils.e(ex);
			}
		}
	}
}
