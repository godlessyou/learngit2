package com.tmkoo.searchapi.util;

public class Cmd {

	public static boolean runBat(String batFile, String argStrings) {

		boolean success = false;

		Runtime rt = Runtime.getRuntime();
		Process p = null;

		// String command = "cmd.exe /C start /b" + " " + batFile;

		String command = batFile;

		if (argStrings != null) {
			command += argStrings;
		}

		try {
			p = rt.exec(command);

			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(),
					"Error");
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(),
					"Output");
			errorGobbler.start();
			outputGobbler.start();

			int exitValue = p.waitFor();

			success = true;

			if (exitValue == 0) {
				success = true;
				System.out.println("执行完成.");
			} else {
				System.out.println("执行失败.");
			}

			// p.destroy();

		} catch (Exception e) {
			try {
				p.getErrorStream().close();
				p.getInputStream().close();
				p.getOutputStream().close();
			} catch (Exception ee) {

			}
		}

		return success;
	}

	public static void main(String[] args) {
		String batFile = "C:/support/data/import-data.bat";
		runBat(batFile, null);

	}

}
