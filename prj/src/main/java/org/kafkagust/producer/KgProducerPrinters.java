package org.kafkagust.producer;


import java.io.File;

import org.kafkagust.common.KgConfig;
import org.kafkagust.common.KgPrinter;

/**
 * The KafkaGust Producer Printers
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducerPrinters {
	
	// The printer for the normal output (that is able to print both to the standard output and to a file)
	private KgPrinter outPrinter = null;
	
	// The printer for the statistic (that is able to print both to the standard output and to a file)
	private KgPrinter statPrinter = null;
	
	/**
	 * Constructor
	 */
	public KgProducerPrinters(KgProducersParams params, int threadNumber) {
		init(params, threadNumber);
	}
	
	/**
	 * Init the context of the KafkaGust producer
	 */
	private void init(KgProducersParams params, int threadNumber) {
		// Create the producer outputs file
		String producerOutputDir = KgConfig.getInstance().getProducerOutputDir();
		createDirIfNotExists(producerOutputDir);
		String producerOutputSuffix = KgConfig.getInstance().getProducerOutputFilenameSuffix();
		File producerOutputFile = new File(producerOutputDir, "Producer_" + threadNumber + "-" + params.getLogFilesTimeStamp() + producerOutputSuffix);
		
		// Create the printer for outputs
		this.outPrinter = new KgPrinter(producerOutputFile, "Producer " + threadNumber + " : ");
		this.outPrinter.setToStdout(KgConfig.getInstance().getProducerOutputStdoutEnabled());
		this.outPrinter.setToFile(KgConfig.getInstance().getProducerOutputFileEnabled());
		
		// Create the producer statistics file
		String producerStatDir = KgConfig.getInstance().getProducerStatisticDir();
		createDirIfNotExists(producerStatDir);
		String producerStatisticSuffix = KgConfig.getInstance().getProducerStatisticFilenameSuffix();
		File producerStatFile = new File(producerStatDir, "Producer_" + threadNumber + "-" + params.getLogFilesTimeStamp() + producerStatisticSuffix);
		
		// Create the printer for statistics
		this.statPrinter = new KgPrinter(producerStatFile);
		this.statPrinter.setToStdout(KgConfig.getInstance().getProducerStatisticStdoutEnabled());
		this.statPrinter.setToFile(KgConfig.getInstance().getProducerStatisticFileEnabled());
	}
	
	/**
	 * Creates a directory if not exists, and exits if it's impossible to create it
	 */
	private void createDirIfNotExists(String dir) {
		File directory = new File(dir);
		if (!directory.exists()) {
			try {directory.mkdirs();} catch (Exception e) {}
		}
		if (!directory.exists()) {
			System.err.println("Problem to create the directory '" + dir + " !");
			System.exit(0);
		}
	}

	/**
	 * Get the printer for normal output
	 */
	public KgPrinter getOutPrinter() {
		return outPrinter;
	}

	/**
	 * Get the printer for statistic
	 */
	public KgPrinter getStatPrinter() {
		return statPrinter;
	}
}
