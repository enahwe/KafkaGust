package org.kafkagust.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The KafkaGust Printer (allow to print to the standard output and to a file by flushing a set of datas)
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgPrinter {

	// End Of Line
	private static String EOL = System.getProperty("line.separator");
	
	// Prefix for each data
	private String datasPrefix = null;
	
	// File where to print the datas
	private File file = null;
	
	// Buffer containing the set of datas to flush
	private StringBuffer printBuffer = null;
	
	// If 'true', enable the datas prefix
	private boolean isDatasPrefix = true;
	
	// If 'true', enable the printing to the standard output
	private boolean isToStdout = true;
	
	// If 'true', enable the printing to the file
	private boolean isToFile = true;
	
	/**
	 * Constructor 1 param
	 */
	public KgPrinter(File file) {
		this.file = file;
		printBuffer = new StringBuffer();
	}
	
	/**
	 * Constructor 2 params
	 */
	public KgPrinter(File file, String datasPrefix) {
		this(file);
		this.datasPrefix = datasPrefix;
	}
	
	/**
	 * Get the datas prefix
	 */
	public String getDatasPrefix() {
		return datasPrefix;
	}

	/**
	 * Set the datas prefix
	 */
	public void setDatasPrefix(String datasPrefix) {
		this.datasPrefix = datasPrefix;
	}

	/**
	 * Print a (int) data
	 */
	public KgPrinter print(int data) {
		print(String.valueOf(data));
		return this;
	}
	
	/**
	 * Print a (long) data
	 */
	public KgPrinter print(long data) {
		print(String.valueOf(data));
		return this;
	}
	
	/**
	 * Print a (float) data
	 */
	public KgPrinter print(float data) {
		print(String.valueOf(data));
		return this;
	}
	
	/**
	 * Print a (String) data
	 */
	public KgPrinter print(String data) {
		print(data, false);
		return this;
	}
	
	/**
	 * Println a (int) data
	 */
	public KgPrinter println(int data) {
		println(String.valueOf(data));
		return this;
	}
	
	/**
	 * Println a (long) data
	 */
	public KgPrinter println(long data) {
		print(String.valueOf(data));
		return this;
	}
	
	/**
	 * Println a (float) data
	 */
	public KgPrinter println(float data) {
		print(String.valueOf(data));
		return this;
	}
	
	/**
	 * Println a (String) data
	 */
	public KgPrinter println(String data) {
		print(data, true);
		return this;
	}
	
	/**
	 * Println an End Of Line
	 */
	public KgPrinter println() {
		printBuffer.append(EOL);
		return this;
	}
	
	/**
	 * Flush the buffer of datas to the standard output and to the file
	 */
	public KgPrinter flush() {
		// Print and flush the buffer to the standard output
		if (isToStdout) {
			System.out.print(printBuffer);
		}
		// Print and flush the buffer to the file
		if (isToFile) {
			appendToFile();
		}
		// Reset the buffer
		printBuffer = new StringBuffer();
		return this;
	}
	
	/**
	 * Returns 'true' if datas are prefixed
	 */
	public boolean isDatasPrefix() {
		return isDatasPrefix;
	}

	/**
	 * If 'true', enable the datas prefix
	 */
	public KgPrinter setDatasPrefix(boolean isDatasPrefix) {
		this.isDatasPrefix = isDatasPrefix;
		return this;
	}
	
	/**
	 * Returns 'true' if printing to the standard output is enabled
	 */
	public boolean isToStdout() {
		return isToStdout;
	}

	/**
	 * If 'true', enable the printing to the standard output
	 */
	public KgPrinter setToStdout(boolean isToStdout) {
		this.isToStdout = isToStdout;
		return this;
	}
	
	/**
	 * Returns 'true' if printing to the file is enabled
	 */
	public boolean isToFile() {
		return isToFile;
	}

	/**
	 * If 'true', enable the printing to the file
	 */
	public KgPrinter setToFile(boolean isToFile) {
		this.isToFile = isToFile;
		return this;
	}
	
	/**
	 * Print a data with or without End Of Line
	 */
	private KgPrinter print(String data, boolean isEOL) {
		if (isDatasPrefix && datasPrefix != null) {
			printBuffer.append(datasPrefix);
		}
		printBuffer.append(data);
		if (isEOL) {
			printBuffer.append(EOL);
		}
		return this;
	}
	
	/**
	 * Append the buffer of datas to the file
	 */
	private void appendToFile() {
		PrintWriter out = null;
		try {
		    out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.print(printBuffer);
		    out.flush();
		}
		catch (IOException e) {
		    System.err.println("Error : Writing problem to the file '" + file.getName() + "' ! : " + e.toString());
		}
		finally {
		    if (out != null) {
		        out.close();
		    }
		} 
	}
}
