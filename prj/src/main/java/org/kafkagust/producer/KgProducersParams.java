package org.kafkagust.producer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.kafkagust.common.KgConfig;

/**
 * The KafkaGust Producers Parameters
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducersParams {
	
	// The description of args
	// msgKey : -1=no key(lb=random), n=position, n:m=position with m (-1=random, 0=forward, 1=backward)
	private static String argsDescriptions = "campaignName nbrProducers brokerUris topic msgModel msgSize msgKeyDef nbrGusts nbrMsgsPerGust gustsWindowSize compressionCodec listSize sleep pause nbrMsgsToSkip maxTime syncAsync ackLevel preHash";
	
	// Number of arguments
	private int numberOfArgs = 18;
	
	// The args
	private String[] args = null;
	
	// The args in a string
	private String argsStr = null;
	
	// Timestamp used to name the log files
	private String logFilesTimeStamp = null;
	
	// The arguments
	private String campaignName = null; // The campaign name
	private int numberOfProducers = 1; // Number of producers (or threads)
	private List<String[]> brokerUris = null; // The list of brokers
	private String topic = "test"; // The topic name
	private String messageModelName = "Default"; // The name of the message model
	private int messageSize = 1024; // Size of every message
	private int messageKeyDefIndex, messageKeyDefDirection = -1; // Message key definition (I for index, D for guidance)
	private int numberOfGusts = 1; // Number of gusts to send (0 means no send, -1 means infinite)
	private int numberOfMessagesPerGust = 1000; // Number of messages to send in each gust
	private int gustsWindowSize = -1; // The number of last gusts (window) to calculate the statistics (default=-1 means stats since the starting)
	private int compressionCodec = 0; // The codec for compression
	private int listSize = 1; // List size (must be >0; if '1' then send messages one by one)
	private int sleep = 0; // Sleep time between every sendings
	private int pause = 0; // Pause to wait for just after the starting
	private int numberOfMessagesToSkip = 0; // Number of messages to skip just after the starting (and the pause)
	private int maxTime = 0; // Timeout in milliseconds (-1 <=> infinite, 0 <=> ends when all messages has been sent)
	private int syncAsync = 0; // Synchronous/Asynchronous mode (0=sync, 1=async)
	private int ackLevel = 0; // Ack level
	private int preHash = 0; // Pre-hash algo
		
	/**
	 * Init the KafkaGust producers parameters from the args
	 */
	protected void init(String[] args) {
		this.args = args;
		
		// Init the log files timeStamp
		logFilesTimeStamp = (new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss")).format(new Date());
		
		// No argument
		if (args == null || args.length == 0) {
			messageAndExit("No arguments !");
		}
		
		// Build the string containing the args
		argsStr = getArgsStr(args);
		
		// Not enough argument
		if (args.length < numberOfArgs) {
			messageAndExit("Not enough arguments: The number of arguments should be " + numberOfArgs + " instead of " + args.length + " !");
		}
		
		// Instanciate the list of broker URIs
		this.brokerUris = new ArrayList<String[]>();
		
		// Scan the arguments
		for (int i = 0; i < numberOfArgs; i++) {
			
			int argCounter = 0;
			
			// =================
			// = Campaign name =
			// =================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Campaign name) is missing !");
				}
				campaignName = args[i].trim();
			}
			argCounter++;
			
			// ====================================
			// = Number of producers (or threads) =
			// ====================================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (Number of producers) is missing !");
				}
				try {
					numberOfProducers = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The number of producers (arg#" + i + ") must be numerical !");
				}
				if (numberOfProducers < 1) {
					messageAndExit("The number of producers (arg#" + i + ") must be '>0' !");
				}
			}
			argCounter++;

			// ===============
			// = Broker URIs =
			// ===============
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (list of broker URIs) is missing !");
				}
				String[] uris = args[i].split(",");
				for (int j = 0; j < uris.length; j++) {
					String uri = uris[j].trim();
					int sepindex = uri.indexOf(":");
					if (sepindex < 0) {
						messageAndExit("The URI(" + j + ") \"" + uri + "\" from args#" + i + " must contains the separator ':' !");
					}
					String host = uri.substring(0, sepindex).trim();
					String port = uri.substring(sepindex + 1).trim();
					try {
						Integer.parseInt(port);
					}
					catch (NumberFormatException e) {
						messageAndExit("The port of URI number " + j + " (arg#"	+ i + ") must be numerical !");
					}
					String[] anURI = new String[2];
					anURI[0] = host;
					anURI[1] = port;
					brokerUris.add(anURI);
				}
			}
			argCounter++;
			
			// ==============
			// = Topic name =
			// ==============
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Topic name) is missing !");
				}
				topic = args[i].trim();
			}
			argCounter++;
			
			// ====================
			// = Message model =
			// ====================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Message model) is missing !");
				}
				messageModelName = args[i].trim();
			}
			argCounter++;

			// ================
			// = Message size =
			// ================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Message size) is missing !");
				}
				try {
					messageSize = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The message size (arg#" + i + ") must be numerical !");
				}
				// Size must be >= 40
				if (messageSize<40) {
					messageAndExit("The message size (arg#" + i + ") must be >= 40 bytes !");
				}
			}
			argCounter++;
			
			// ==========================
			// = Message key definition =
			// ==========================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Message key definition) is missing !");
				}
				int p = args[i].indexOf(":");
				// In one part
				try {
					if (p == -1) {
						messageKeyDefIndex = Integer.parseInt(args[i].trim());
					}
				}
				catch (NumberFormatException e) {
					messageAndExit("The message key definition (arg#" + i + ") must be numerical (in one or two parts). -1 or I:D where Index I>=0 and Direction D={-1;0;1} (-1=Backward; 0=Random; 1=Forward) !");
				}
				if (p == -1 && messageKeyDefIndex != -1 && messageKeyDefIndex != -2) {
					messageAndExit("The message key definition (arg#" + i + ") when it is alone must be '-1' or '-2' !");
				}
				// In two parts (the Index)
				try {
					if (p > -1) {
						messageKeyDefIndex = Integer.parseInt(args[i].substring(0, p).trim());
					}
				}
				catch (NumberFormatException e) {
					messageAndExit("The 'Index' or the first part of the message key definition (arg#" + i + ") must be numerical !");
				}
				if (p > -1 && messageKeyDefIndex < 0) {
					messageAndExit("The 'Index' or the first part of the message key definition (arg#" + i + ") must be >=0 !");
				}
				// In two parts (the Direction)
				try {
					if (p > -1) {
						messageKeyDefDirection = Integer.parseInt(args[i].substring(p+1).trim());
					}
				}
				catch (NumberFormatException e) {
					messageAndExit("The 'Direction' or the second part of the message key definition (arg#" + i + ") must be numerical !");
				}
				if (p > -1 && messageKeyDefDirection !=-1 && messageKeyDefDirection != 0 && messageKeyDefDirection != 1) {
					messageAndExit("The 'Direction' or the second part of the message key definition (arg#" + i + ") must be '-1', '0' or '1' !");
				}
			}
			argCounter++;			

			// ===================
			// = Number of gusts =
			// ===================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Number of gusts) is missing !");
				}
				try {
					numberOfGusts = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The number of gusts (arg#" + i + ") must be numerical !");
				}
				if (numberOfGusts < -1) {
					messageAndExit("The number of gusts (arg#" + i + ") must be '-1', '0', or '>0' !");
				}
			}
			argCounter++;

			// ===============================
			// = Number of messages per gust =
			// ===============================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Number of messages per gust) is missing !");
				}
				try {
					numberOfMessagesPerGust = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The number of messages per gust (arg#" + i + ") must be numerical !");
				}
				if (numberOfMessagesPerGust < 1) {
					messageAndExit("The number of messages per gust (arg#" + i + ") must be '>0' !");
				}
			}
			argCounter++;

			// ====================================
			// = Gusts window size for statistics =
			// ====================================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (gusts window size) is missing !");
				}
				try {
					gustsWindowSize = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The gusts window size (arg#" + i	+ ") must be numerical !");
				}
				if (gustsWindowSize < -1) {
					messageAndExit("The gusts window size (arg#" + i + ") must be '-1', '0', or '>0' !");
				}
			}
			argCounter++;
			
			// =====================
			// = Compression codec =
			// =====================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Compression codec) is missing !");
				}
				String s = args[i].trim().toLowerCase();
				if (s.equals("none") || s.equals("0")) {
					compressionCodec = 0;
				}
				else if (s.equals("gzip") || s.equals("1")) {
					compressionCodec = 1;
				}
				else if (s.equals("snappy") || s.equals("2")) {
					compressionCodec = 2;
				}
				else {
					messageAndExit("The compression codec (arg#" + i + ") must be 'none' or '0', or 'gzip' or '1', or 'snappy' or '2' !");
				}
			}
			argCounter++;
			
			// =============
			// = List size =
			// =============
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (List size) is missing !");
				}
				try {
					listSize = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The list size (arg#" + i	+ ") must be numerical !");
				}
				if (listSize < -1) {
					messageAndExit("The list size (arg#" + i + ") must be >= '-1' !");
				}
				if (listSize > numberOfMessagesPerGust) {
					messageAndExit("The list size (arg#" + i + ") must be lower or equals to the 'Number of messages' argument !");
				}
			}
			argCounter++;
			
			// =============================
			// = Sleep between two message =
			// =============================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (Sleep time between two messages) is missing !");
				}
				try {
					sleep = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The sleep time between two messages (arg#" + i	+ ") must be numerical !");
				}
			}
			argCounter++;
			
			// =========
			// = Pause =
			// =========
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (Pause) is missing !");
				}
				try {
					pause = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The pause (arg#" + i	+ ") must be numerical !");
				}
			}
			argCounter++;
			
			// ==============================
			// = Number of messages to skip =
			// ==============================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (Number of messages to skip) is missing !");
				}
				try {
					numberOfMessagesToSkip = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The number of messages to skip (arg#" + i	+ ") must be numerical !");
				}
				if (numberOfMessagesToSkip >= numberOfMessagesPerGust) {
					messageAndExit("The number of messages to skip (arg#" + i	+ ") must be lower than the 'Number of messages' argument !");
				}
			}
			argCounter++;
			
			// ================
			// = Maximum time =
			// ================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (MaxTime in ms) is missing !");
				}
				try {
					maxTime = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The maximum time in ms (arg#" + i	+ ") must be numerical !");
				}
				if (maxTime < -1) {
					messageAndExit("The maximum time in ms (arg#" + i + ") must be '-1', '0', or '>0' !");
				}
			}
			argCounter++;
			
			// =================================
			// = Synchronous/Asynchronous mode =
			// =================================
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Sync/Async mode) is missing !");
				}
				String s = args[i].trim().toLowerCase();
				if (s.equals("sync") || s.equals("0")) {
					syncAsync = 0;
				}
				else if (s.equals("async") || s.equals("1")) {
					syncAsync = 1;
				}
				else {
					messageAndExit("The sync/async mode (arg#" + i + ") must be 'sync' or '0', or 'async' or '1' !");
				}
			}
			argCounter++;
			
			// =============
			// = Ack level =
			// =============
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i + " (Ack level) is missing !");
				}
				try {
					ackLevel = Integer.parseInt(args[i].trim());
				}
				catch (NumberFormatException e) {
					messageAndExit("The ack level (arg#" + i	+ ") must be numerical !");
				}
				if (ackLevel != -1 && ackLevel != 0 && ackLevel != 1) {
					messageAndExit("The ack level (arg#" + i + ") must be '-1', '0' or '1' !");
				}
			}
			argCounter++;
			
			// ============
			// = Pre-hash =
			// ============
			if (i == argCounter) {
				if (args[i] == null || args[i].trim().equals("")) {
					messageAndExit("The args#" + i	+ " (Pre-hash) is missing !");
				}
				String s = args[i].trim().toLowerCase();
				if (s.equals("none") || s.equals("0")) {
					preHash = 0;
				}
				else if (s.equals("sha") || s.equals("1")) {
					preHash = 1;
				}
				else if (s.equals("md5") || s.equals("2")) {
					preHash = 2;
				}
				else {
					messageAndExit("The pre-hash (arg#" + i + ") must be 'none' or '0', or 'sha' or '1', or 'md5' or '2' !");
				}
			}
			argCounter++;
		}
	}

	/**
	 * Print an error message and exits
	 */
	private static void messageAndExit(String message) {
		System.out.println(message);
		System.out.println("Usage : $ ./bin/Producer " + argsDescriptions);
		System.exit(0);
	}
	
	/**
	 * Get the properties for all the Kafka producers
	 */
	public Properties getKafkaProducerProperties() {
		Properties kafkaProducerProperties = new Properties();
		
		// Client Id
		kafkaProducerProperties.put("client.id", this.getCampaignName());
		
		// List of brokers
		StringBuffer brokerList = new StringBuffer();
		for (int i=0; i<this.getBrokerUris().size(); i++) {
			if (i>0) brokerList.append(",");
			brokerList.append(this.getBrokerUris().get(i)[0]).append(":").append(this.getBrokerUris().get(i)[1]);
		}
		kafkaProducerProperties.put("metadata.broker.list", brokerList.toString());
		
		// Serializer
		kafkaProducerProperties.put("serializer.class", "kafka.serializer.StringEncoder");
		
		// Asynchronous/Synchronous mode
		if (this.getSyncAsync() == 1) {
			kafkaProducerProperties.put("producer.type", "async"); // Asynchronous mode
		}
		else {
			kafkaProducerProperties.put("producer.type", "sync"); // Synchronous mode
		}
		
		// Ack level
		kafkaProducerProperties.put("request.required.acks", String.valueOf(this.getAckLevel()));
		
		// Compression
		if (this.getCompressionCodec()>0) {
			kafkaProducerProperties.put("compression.codec", String.valueOf(this.getCompressionCodec()));
			kafkaProducerProperties.put("compressed.topics", this.getTopic());
		}
		
		// Overloading of the rest of Kafka properties
		overloadsKafkaProperty(kafkaProducerProperties, "producers.request.timeout.ms");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.message.send.max.retries");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.retry.backoff.ms");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.topic.metadata.refresh.interval.ms");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.queue.buffering.max.ms");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.queue.buffering.max.messages");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.queue.enqueue.timeout.ms");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.batch.num.messages");
		overloadsKafkaProperty(kafkaProducerProperties, "producers.send.buffer.bytes");

		// Return the Kafka producer properties
		return kafkaProducerProperties;
	}
	
	/**
	 * Get the number of args
	 */
	public int getNumberOfArgs() {
		return numberOfArgs;
	}

	/**
	 * Get the campaign name
	 */
	public String getCampaignName() {
		return campaignName;
	}
	
	/**
	 * Get the number of producers
	 */
	public int getNumberOfProducers() {
		return numberOfProducers;
	}
	
	/**
	 * Get the broker URI list
	 */
	public List<String[]> getBrokerUris() {
		return brokerUris;
	}
	
	/**
	 * Get the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Get the message model name
	 */
	public String getMessageModelName() {
		return messageModelName;
	}
	
	/**
	 * Get the number of gusts
	 */
	public int getNumberOfGusts() {
		return numberOfGusts;
	}
	
	/**
	 * Get the number of messages per gust
	 */
	public int getNumberOfMessagesPerGust() {
		return numberOfMessagesPerGust;
	}

	/**
	 * Get the message size
	 */
	public int getMessageSize() {
		return messageSize;
	}
	
	/**
	 * Get the message key Index definition
	 */
	public int getMessageKeyDefIndex() {
		return messageKeyDefIndex;
	}

	/**
	 * Get the message key Direction definition
	 */
	public int getMessageKeyDefDirection() {
		return messageKeyDefDirection;
	}
	
	/**
	 * Get the compression codec
	 */
	public int getCompressionCodec() {
		return compressionCodec;
	}
	
	/**
	 * Get the size of list
	 */
	public int getListSize() {
		return listSize;
	}

	/**
	 * Get the sleep value (ms)
	 */
	public int getSleep() {
		return sleep;
	}
	
	/**
	 * Get the pause value (ms)
	 */
	public int getPause() {
		return pause;
	}
	
	/**
	 * Get the number of messages to skip (only at the starting)
	 */
	public int getNumberOfMessagesToSkip() {
		return numberOfMessagesToSkip;
	}

	/**
	 * Get the maxTime
	 */
	public int getMaxTime() {
		return maxTime;
	}
	
	/**
	 * Get the synchronous/asynchronous mode
	 */
	public int getSyncAsync() {
		return syncAsync;
	}
	
	/**
	 * Get the ack level
	 */
	public int getAckLevel() {
		return ackLevel;
	}
	
	/**
	 * Get the pre-hash algo
	 */
	public int getPreHash() {
		return preHash;
	}
	
	/**
	 * Get the number of gusts for the window statistics (default is : since the starting of producer)
	 */
	public int getGustsWindowSize() {
		return gustsWindowSize;
	}
	
	/**
	 * Returns the the timestamp used to name the log files
	 */
	public String getLogFilesTimeStamp() {
		return logFilesTimeStamp;
	}
	
	/**
	 * Get the description of args in a string
	 */
	public static String getArgsDescriptions() {
		return argsDescriptions;
	}
	
	/**
	 * Get the args
	 */
	public String[] getArgs() {
		return args;
	}
	
	/**
	 * Get the args in a string
	 */
	public String getArgsStr() {
		return argsStr;
	}
	
	/**
	 * Method to overload a Kafka producer property
	 */
	private static boolean overloadsKafkaProperty(Properties kafkaProducerProperties, String kafkaGustKey) {
		if (!kafkaGustKey.startsWith("producers.")) {
			return false;
		}
		// Get the KafkaGust property value
		String value = KgConfig.getInstance().getProperty(kafkaGustKey);
		// Overloads the Kafka property if != null
		if (value !=null && !value.trim().equals("")) {
			String kafkaKey = kafkaGustKey.substring(kafkaGustKey.indexOf('.') + 1);
			kafkaProducerProperties.put(kafkaKey, value);
			return true;
		}
		return false;
	}
	
	/**
	 * Transforms the array of args into a string
	 */
	private static String getArgsStr(String[] argsList) {
		String args = null;
		if (argsList != null) {
			StringBuffer sbf = new StringBuffer();
			for (int i = 0; i < argsList.length; i++) {
				String arg = argsList[i];
				if (arg != null) {
					sbf.append(arg).append(" ");
				}
			}
			args = sbf.toString().trim();
		}
		return args;
	}
}
