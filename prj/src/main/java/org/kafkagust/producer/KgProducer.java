package org.kafkagust.producer;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.kafkagust.common.KgConfig;
import org.kafkagust.common.KgPrinter;

/**
 * The KafkaGust Producer
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducer extends Thread {
	
	// The parameters for all the KafkaGust producers
	private KgProducersParams params = null;
	
	// The messages model for all the producers
	private KgProducersModel model = null;
	
	// The producer number (or the thread)
	private int producerNumber = -1;
	
	// The printers of this KafkaGust producer
	private KgProducerPrinters printers = null;
	
	// The printer for the normal output
	private KgPrinter outPrinter = null;
	
	// The printer for the statistic
	private KgPrinter statPrinter = null;
	
	// The column separator to separate the statistic fields
	private String statColumnSep = null;
	
	// The message digest for MD5 or SHA pre-hashing Kafka key
	private MessageDigest messageDigest = null;
	
	// The Kafka producer
	private Producer<String, String> kafkaProducer;
	
	/**
	 * Constructor
	 */
	public KgProducer(KgProducersParams params, KgProducersModel model, int producerNumber) {
		// The KafkaGust producers parameters
		this.params = params;
		// The messages model
		this.model = model;
		// The producer number
		this.producerNumber = producerNumber;
		// Init the statistic column separator
		this.statColumnSep = KgConfig.getInstance().getProducerStatisticColumnSeparator();
		// Create the KafkaGust producer printers
		this.printers = new KgProducerPrinters(params, producerNumber);
		// Create the output printer
		this.outPrinter = printers.getOutPrinter();
		// Create the statistic printer
		this.statPrinter = printers.getStatPrinter();
	}

	/**
	 * Starting the thread
	 */
	public void run() {
		
		// Start time related on the begining of this thread
		long producerStartTime = new Date().getTime();
		
		// First informations at the beginning to the output
		outPrinter.print((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(producerStartTime));
		outPrinter.setDatasPrefix(false).print(" - Start the producer").setDatasPrefix(true);
		outPrinter.println().println();
		
		outPrinter.print("Command pattern -> ");
		outPrinter.setDatasPrefix(false).print("$ ./bin/Producer ").print(KgProducersParams.getArgsDescriptions()).setDatasPrefix(true);
		outPrinter.println().println();
		
		outPrinter.print("Command -> ");
		outPrinter.setDatasPrefix(false).print("$ ./bin/Producer ").print(params.getArgsStr()).setDatasPrefix(true);
		outPrinter.println().println();
		
		outPrinter.print("Window max size for statistics = ").setDatasPrefix(false);
		if (params.getGustsWindowSize() > 1) {
			outPrinter.print(params.getGustsWindowSize());
		}
		else {
			outPrinter.print("all");
		}
		outPrinter.print(" gusts").setDatasPrefix(true);
		outPrinter.println().println();
		
		outPrinter.flush();
		
		// Prepare the KafkaGust producer messages model
		KgProducerMessage kafkaGustProducerMsg = new KgProducerMessage();
		try {
			kafkaGustProducerMsg.init(producerNumber, producerStartTime, params, model);
		}
		catch (IOException e1) {
			outPrinter.println("Problem to init the message model named '" + params.getMessageModelName() + "' !" + e1.toString()).flush();
			System.exit(0);
		}
		
		// Prepare the algorithm that will allows to calculate the pre-hash (if the pre-hash has been asked)
		String preHashAlgoName = "NOP";
		if  (params.getPreHash() == 1) {
			preHashAlgoName = "SHA";
		}
		else if (params.getPreHash() == 2) {
			preHashAlgoName = "MD5";
		}
		if (preHashAlgoName.equals("SHA") || preHashAlgoName.equals("MD5")) {
			try {
				messageDigest = MessageDigest.getInstance(preHashAlgoName);
			}
			catch (NoSuchAlgorithmException e) {
				outPrinter.println("MessageDigest Algorithm problem, pre-hash will not be calculated : " + e.toString()).flush();
			}
		}
		
		// Create the Kafka producer config
		ProducerConfig kafkaConfig = new ProducerConfig(params.getKafkaProducerProperties());
		
		// Instanciate the Kafka producer
		kafkaProducer = new Producer<String, String>(kafkaConfig);
		
		// Unlimited gusts parameter
		boolean isNbreGustsUnlimited = false;
		if (params.getNumberOfGusts() < 0) {
			isNbreGustsUnlimited = true;
		}
		
		// Sleep parameter
		boolean isSleep = false;
		if (params.getSleep() > 0) {
			isSleep = true;
		}
		
		// If the 'pause' parameter is > 0, then will wait for till the end of 'pause'
		if (params.getPause() > 0) {
			outPrinter.println("Wait for " + params.getPause() + " ms (pause)").flush();
			try {
				Thread.currentThread();
				Thread.sleep(params.getPause());
			}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// Number of messages skipped parameter
		boolean isMgsSkipped = false;
		if (params.getNumberOfMessagesToSkip() > 0) {
			isMgsSkipped = true;
		}
		
		// MaxTime parameters
		boolean isMaxTime = false;
		if (params.getMaxTime() >= 0) {
			isMaxTime = true;
		}
		
		// List size of messages to send (default = 1 : messages are sending one by one)
		List<KeyedMessage<String, String>> kafkaMessages = null;
		boolean isListMode = false;
		if (params.getListSize() > 1) {
			isListMode = true;
			kafkaMessages = new ArrayList<KeyedMessage<String, String>>();
		}
		
		// Gust throughput list
		List<Float> gustThroughputList = new ArrayList<Float>();
		
		// Start time for statistics
		long windowStartTime = new Date().getTime();
		
		// Total of messages from the beginning
		long nbreMessagesInWindow = 0;
		
		// Gust start time
		long gustStartTime = windowStartTime;
		
		// Gust start times list
		List<Long> gustStartTimeList = new ArrayList<Long>();
		gustStartTimeList.add(new Long(gustStartTime));
		
		// Gust messages counter
		long nbreMessagesInGust = 0;
		
		// Gust time spent
		long gustTimeSpent = 0;
		
		// Gusts counter
		long gustCounter = 0;
		
		// Minimum gust throughput
		int minGustThroughput = -1;
		
		// Maximum gust throughput
		int maxGustThroughput = 0;
		
		// ===========================
		// = Loop message by message =
		// ===========================
		while (true) {
			
			// STOPPING : If the number of gusts sent has been exceeded
			if (!isNbreGustsUnlimited && gustCounter >= params.getNumberOfGusts()) {
				outPrinter.print((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date())).setDatasPrefix(false).print(" - ");
				outPrinter.print("Stopping the producer - The maximum number of gusts (") .print(params.getNumberOfGusts()).print(") has been reached.");
				outPrinter.println().flush().setDatasPrefix(true);
				break;
			}
			
			// STOPPING : If 'maxTime' is activated, then will stop the producer after 'maxTime' ms
			if (isMaxTime && (new Date().getTime() - producerStartTime) > params.getMaxTime()) {
				outPrinter.print((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date())).setDatasPrefix(false).print(" - ");
				outPrinter.print("Stopping the producer - The maximum time (") .print(params.getMaxTime()).print(" ms) has been exceeded.");
				outPrinter.println().flush().setDatasPrefix(true);
				break;
			}
						
			// Create the new KafkaGust message to send, throws an exception if the minimum message size exceeds the expected message size
			try {
				kafkaGustProducerMsg.replaceMessageVariables(nbreMessagesInGust, gustCounter, gustStartTime);
			}
			catch (Exception e) {
				outPrinter.println(e.toString()).flush();
				System.exit(0);
			}
			
			// Prepare the Kafka message with the new KafkaGust message
			KeyedMessage<String, String> kafkaMessage = new KeyedMessage<String, String>(
				params.getTopic(),
				this.getPreHashKey(messageDigest, kafkaGustProducerMsg.getMessageKey()),
				kafkaGustProducerMsg.getMessageToSend()
			);
			
			// If the list mode is set, then add the message into that list
			if (isListMode) {
				// Add the message into the list of messages
				kafkaMessages.add(kafkaMessage);
			}
			// Otherwise send the message to the Kafka producer (messages are sent one by one)
			else {
				// Send the single message to Kafka
				kafkaProducer.send(kafkaMessage);
			}
			
			// Increments the gust messages counter
			nbreMessagesInGust++;
			
			// If the list mode is set and its size is reached, the list is sent
			if (isListMode && (nbreMessagesInGust % params.getListSize() == 0)) {
				// Send the list of messages to Kafka
				kafkaProducer.send(kafkaMessages);
				// Reset the list of messages
				kafkaMessages = new ArrayList<KeyedMessage<String, String>>();
			}
			
			// Mode 'Messages skipped'
			if (isMgsSkipped && nbreMessagesInGust == params.getNumberOfMessagesToSkip()) {
				windowStartTime = new Date().getTime();
				gustStartTime = windowStartTime;
				gustStartTimeList.set(gustStartTimeList.size()-1, new Long(gustStartTime));
				nbreMessagesInGust = nbreMessagesInGust - params.getNumberOfMessagesToSkip();
				isMgsSkipped = false;
				outPrinter.println(params.getNumberOfMessagesToSkip() + " first messages skipped").println().flush();
			}
			
			// If 'sleep' is activated, then will wait for 'sleep' ms between every message
			if (isSleep) {
				try {Thread.sleep(params.getSleep());} catch (InterruptedException e) {e.printStackTrace();}
			}
			
			// ---------------------
			// - Loop gust by gust -
			// ---------------------
			// (all the messages of the gust have been sent, or stored in the list)
			if (nbreMessagesInGust == params.getNumberOfMessagesPerGust()) {
				
				// If the list mode is enabled, then finish to send the rest of messages to the Kafka producer
				if (isListMode && !kafkaMessages.isEmpty()) {
					kafkaProducer.send(kafkaMessages);
					// Reset the list of messages
					kafkaMessages = new ArrayList<KeyedMessage<String, String>>();
				}
				
				// Gust end time
				long gustEndTime = new Date().getTime();
				
				// Gust time spent
				gustTimeSpent = gustEndTime - gustStartTime;
				
				// Total of messages sent since the beginning of the window
				nbreMessagesInWindow += nbreMessagesInGust;
				
				// Output printer
				outPrinter.println("Gust number = " + gustCounter);
				outPrinter.println("Gust start time = " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(gustStartTime)));
				outPrinter.println("Number of messages sent in the gust = " + nbreMessagesInGust);
				
				// Statistic printer
				if (gustCounter == 0) {
					statPrinter.print("Campaign name").print(statColumnSep);
					statPrinter.print("Object").print(statColumnSep);
					statPrinter.print("Producer number").print(statColumnSep);
					statPrinter.print("Gust number").print(statColumnSep);
					statPrinter.print("Gust start time (ms)").print(statColumnSep);
					statPrinter.print("Number messages in the gust").print(statColumnSep);
					statPrinter.print("Message size (bytes)").print(statColumnSep);
					statPrinter.print("Gust time spent (ms)").print(statColumnSep);
					statPrinter.print("Gust throughput (msgs/s)").print(statColumnSep);
					statPrinter.print("Min gust throughput (msgs/s)").print(statColumnSep);
					statPrinter.print("Max gust throughput (msgs/s)").print(statColumnSep);
					statPrinter.print("Median of gust throughputs (msgs/s)").print(statColumnSep);
					statPrinter.print("Mean of gust throughputs (msgs/s)").print(statColumnSep);
					statPrinter.print("Mean throughput (msgs/s)").print(statColumnSep);
					statPrinter.println();
				}
				statPrinter.print(params.getCampaignName());
				statPrinter.print(statColumnSep).print("Producer");
				statPrinter.print(statColumnSep).print(producerNumber);
				statPrinter.print(statColumnSep).print(gustCounter);
				statPrinter.print(statColumnSep).print(gustStartTime - windowStartTime);
				statPrinter.print(statColumnSep).print(nbreMessagesInGust);
				
				// Output printer
				outPrinter.println("Size of each message = " + params.getMessageSize() + " bytes");
				outPrinter.println("Time spent to send the messages = " + gustTimeSpent + " ms");
				
				// Statistic printer
				statPrinter.print(statColumnSep).print(params.getMessageSize());
				statPrinter.print(statColumnSep).print(gustTimeSpent);
				
				// Add the current throughput into the list of throughputs
				int gustThroughput = Math.round((float) nbreMessagesInGust / ((float) gustTimeSpent / 1000));
				gustThroughputList.add(new Float(gustThroughput));
				
				// Resize the list of gust throughputs with almost the window size
				if (params.getGustsWindowSize() > 1) {
					while (gustThroughputList.size() > params.getGustsWindowSize())  { // Normally takes only one iteration
						gustThroughputList.remove(0);
					}
				}
				
				// Calculate the minimum throughput in the list
				minGustThroughput = Math.round(Collections.min(gustThroughputList));
				
				// Calculate the maximum throughput in the list
				maxGustThroughput = Math.round(Collections.max(gustThroughputList));
				
				// Output printer
				outPrinter.println("Gust throughput = <<" + gustThroughput + ">> messages / second");
				
				// Statistic printer
				statPrinter.print(statColumnSep).print(gustThroughput);
				
				// If more than one gust, then print the statistics (e.g : means, medians, ...)
				if (gustCounter > 0) {
					
					// Calculate the mean of the gust throughputs
					int gustThroughputsMean = 0;
					if (gustThroughputList.size()>1) {
						float throughputSum = 0;
						for (int i=0; i<gustThroughputList.size(); i++) {
							throughputSum += gustThroughputList.get(i);
						}
						gustThroughputsMean = Math.round((float) (throughputSum / gustThroughputList.size()));
					}
					
					// Calculate the true mean of throughput (since the starting)
					int trueMeanThroughput = Math.round((float) nbreMessagesInWindow / ((float) (gustEndTime - windowStartTime) / 1000));
					
					// Output printer
					outPrinter.println("Minimum gust throughput = " + minGustThroughput + " messages / second");
					outPrinter.println("Maximum gust throughput = " + maxGustThroughput + " messages / second");
					int gustThroughputsMedian = Math.round((float) ((minGustThroughput + maxGustThroughput) / 2));
					outPrinter.println("Median of gust throughputs = " + gustThroughputsMedian + " messages / second");
					outPrinter.println("Mean of gust throughputs = " + gustThroughputsMean + " messages / second");
					outPrinter.println("Mean throughput = " + trueMeanThroughput + " messages / second");
					
					// Statistic printer
					statPrinter.print(statColumnSep).print(minGustThroughput);
					statPrinter.print(statColumnSep).print(maxGustThroughput);
					statPrinter.print(statColumnSep).print(gustThroughputsMedian);
					statPrinter.print(statColumnSep).print(gustThroughputsMean);
					statPrinter.print(statColumnSep).print(trueMeanThroughput);
				}
				// Flushs
				outPrinter.println().flush();
				statPrinter.println().flush();
				
				// Increment the gusts counter
				gustCounter++;
				
				// Reset the gust messages counter
				nbreMessagesInGust = 0;
				
				// Reset the start time for the next gust
				gustStartTime = new Date().getTime();
				
				// *********************************************************************************
				// * For the next step : Resize the list of start times (if the window size is >1) *
				// *********************************************************************************
				if (params.getGustsWindowSize() > 1) {
					
					// Add the next start time to the list
					gustStartTimeList.add(new Long(gustStartTime));
					
					// Resize the list of start times with almost the window size
					boolean removed = false;
					while (gustStartTimeList.size() > params.getGustsWindowSize())  { // Normally takes only one iteration
						gustStartTimeList.remove(0);
						removed = true;
					}

					// The window start time becomes the oldest value of the list
					windowStartTime = gustStartTimeList.get(0).longValue();
					
					// Resize the number of messages of the window
					if (removed) {
						// Get rid of the number of messages from the first gust in the window
						nbreMessagesInWindow = nbreMessagesInWindow - params.getNumberOfMessagesPerGust();
						// If the first gust of the window was index 0 and contained messages to skip, then add the number of messages to skip
						if ((gustCounter - params.getGustsWindowSize() == 0) && params.getNumberOfMessagesToSkip() > 0) {
							nbreMessagesInWindow = nbreMessagesInWindow + params.getNumberOfMessagesToSkip();
						}
					}
				}
				// ************************************************************
			}
			// ---------------------------
			// - End of the current gust -
			// ---------------------------
		}
		// Closes the Kafka producer
		kafkaProducer.close();
	}
	
	/**
	 * Calculate an hexadecimal hash key (MD5 or SHA)
	 * This function can be usefull to pre-hash every message key, in order to improve the load-balancing of all the messages to the Kafka partitions
	 */
	private String getPreHashKey(MessageDigest messageDigest, String key) {
		String preHashKey = key;
		// Calculate the pre-hash
		if (messageDigest != null) {
			messageDigest.reset();
			messageDigest.update(preHashKey.getBytes());
			byte[] b = messageDigest.digest();
			// Convert the pre-hash to hex format
			StringBuffer sb = new StringBuffer();
			for (int x=0; x<b.length; x++) {
				sb.append(Integer.toString((b[x] & 0xff) + 0x100, 16).substring(1));
			}
			preHashKey = sb.toString();
		}
		return preHashKey;
	}
}
