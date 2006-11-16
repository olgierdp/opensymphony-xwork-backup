/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.apt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

/**
 * AptRunner -- Wraps a call to the Annotation Processing Tool
 * 
 * @author Nils Hartmann <nils@nilshartmann.net>
 * @author Rainer Hermanns
 * @version $Id$
 */
public class AptRunner {

	public final static String NEW_LINE = System.getProperty("line.separator");

	private String executable;
	private File destDir;
	private String classpath;
	private String additionalClasspath;
	private String factoryName;
	private List<String> sourceFiles;

	public AptRunner() {

	}

	public List<String> getSourceFiles() {
		if (sourceFiles == null) {
			sourceFiles = new LinkedList<String>();
		}
		return sourceFiles;
	}

	public void addSourceDir(File sourceDir) {
		Assert.assertNotNull(sourceDir);
		Assert.assertTrue("sourceDir: " + sourceDir, sourceDir.exists());
		Assert.assertTrue(sourceDir.isDirectory());

		List<String> sourceFiles = getSourceFiles(sourceDir);
		getSourceFiles().addAll(sourceFiles);

	}

	public void addSourceFiles(List<String> sourceFiles) {
		Assert.assertNotNull(sourceFiles);
		getSourceFiles().addAll(sourceFiles);
	}

	public void addSourceFile(String sourceFile) {
		getSourceFiles().add(sourceFile);
	}

	public String getClasspath() {
		return (classpath == null ? System.getProperty("java.class.path")
				: classpath);
	}

	/**
	 * Sets the classpath for the apt-call. If no classpath is specified, the
	 * classpath of the current vm will be used (as specified in
	 * <code>java.class.path</code> system property
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}


    public String getAdditionalClasspath() {
        return additionalClasspath;
    }

    public void setAdditionalClasspath(String additionalClasspath) {
        this.additionalClasspath = additionalClasspath;
    }

    public File getDestDir() {
		return destDir;
	}

	public void setDestDir(File destDir) {
		this.destDir = destDir;
	}

	public String getExecutable() {
		return (executable == null ? "apt" : executable);
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public AptRunnerResult run() throws Exception {
		Assert.assertNotNull("Dest directory must be set", getDestDir());
		Assert.assertNotNull("A factory name must be set", getFactoryName());
		Assert.assertTrue("At least one source file must be specified",
				getSourceFiles().size() > 0);

		List<String> args = new LinkedList<String>();
		args.add(getExecutable());
		args.add("-s");
		args.add(getDestDir().getAbsolutePath());
		args.add("-classpath");
        if ( additionalClasspath != null) {
            args.add(getClasspath() + System.getProperty("path.separator") + additionalClasspath);
        } else {
            args.add(getClasspath());
        }
        args.add("-nocompile");
		args.add("-factory");
		args.add(getFactoryName());
		args.addAll(getSourceFiles());

		StringBuffer command =new StringBuffer();
		for (String s : args) {
			command.append(s);
			command.append(' ');
		}

		//System.out.println("Running apt with '" + command + "'");
		ProcessBuilder processBuilder = new ProcessBuilder(args);
	
		Process aptProcess = processBuilder.start(); 
		StreamReader inputReader = new StreamReader("output", aptProcess.getInputStream());
		StreamReader errorReader = new StreamReader("error", aptProcess.getErrorStream());
		inputReader.start();
		errorReader.start();
		int result = aptProcess.waitFor();
		
		AptRunnerResult runnerResult = new AptRunnerResult(result, command.toString(), inputReader.getBuffer(), errorReader.getBuffer());
		return runnerResult;
	}

	class StreamReader extends Thread {

		private final BufferedReader reader;

		private final StringBuffer output;

		private boolean shouldStop = false;

		public StreamReader(String name, InputStream is) {
			super(name);
			Assert.assertNotNull(is);
			this.reader = new BufferedReader(new InputStreamReader(is));
			this.output = new StringBuffer();
		}

		public void run() {
			String line;
			try {
				while ((line = reader.readLine()) != null && !shouldStop) {
					output.append(line);
					output.append(NEW_LINE);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				output.append(ioe.toString());
			}
			//System.out.println(" ---> " + getName() + "-reader quit. buffer: " + output);
		}

		//
		// public void stopReader() {
		// this.shouldStop = false;
		// }

		public String getBuffer() {
			return this.output.toString();
		}
	}

	/**
	 * Gets a list with all ".java"-files in sourceDir
	 */
	protected List<String> getSourceFiles(File sourceDir) {

		final List<String> sourceFiles = new LinkedList<String>();

		final File[] files = sourceDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (!file.getName().equals(".svn")
						&& !file.getName().equals("CVS")) {
					sourceFiles.addAll(getSourceFiles(file));
				}
			} else {
				if (file.getName().endsWith(".java")) {
					sourceFiles.add(file.getAbsolutePath());
				}
			}
		}

		return sourceFiles;
	}

}
