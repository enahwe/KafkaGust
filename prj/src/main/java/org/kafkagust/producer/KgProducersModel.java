package org.kafkagust.producer;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import org.kafkagust.common.KgConfig;

/**
 * The KafkaGust Producers Model
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducersModel {

	// Global variables (that corresponds to the console arguments)
	protected final static String CAMPAIGN_NAME = "${CAMPAIGN_NAME}";
	protected final static String NUMBER_PRODUCERS = "${NUMBER_PRODUCERS}";
	protected final static String BROKER_URIS = "${BROKER_URIS}";
	protected final static String TOPIC = "${TOPIC}";
	protected final static String MESSAGE_MODEL = "${MESSAGE_MODEL}";
	protected final static String MESSAGE_SIZE = "${MESSAGE_SIZE}";
	protected final static String MESSAGE_KEY_DEF = "${MESSAGE_KEY_DEF}";
	protected final static String NUMBER_GUSTS = "${NUMBER_GUSTS}";
	protected final static String NUMBER_MESSAGES_PER_GUST = "${NUMBER_MESSAGES_PER_GUST}";
	protected final static String GUSTS_WINDOW_SIZE = "${GUSTS_WINDOW_SIZE}";
	protected final static String COMPRESSION_CODEC = "${COMPRESSION_CODEC}";
	protected final static String LIST_SIZE = "${LIST_SIZE}";
	protected final static String SLEEP = "${SLEEP}";
	protected final static String PAUSE = "${PAUSE}";
	protected final static String NUMBER_MESSAGES_TO_SKIP = "${NUMBER_MESSAGES_TO_SKIP}";
	protected final static String MAX_TIME = "${MAX_TIME}";
	protected final static String SYNC_ASYNC = "${SYNC_ASYNC}";
	protected final static String ACK_LEVEL = "${ACK_LEVEL}";
	protected final static String PRE_HASH = "${PRE_HASH}";
	protected final static String EOL = "${EOL}";

	// Message template that contains the global variables (not the messages variables and the data either)
	private String messagesTemplate = null;

	// Duplicated content for all the messages
	private StringBuffer messagesDuplicatedContent = null;
	
	// Size of the original content (not duplicated)
	private int messagesOriginalContentSize = -1;
	
	// Keys for all the messages
	private List<String> messagesKeys = null;

	/**
	 * Init
	 */
	public void init(KgProducersParams params) {
		// = Load and prepare the messages template =
		Path messageTemplatePath = Paths.get(KgConfig.getInstance().getKafkaGustHome() + "/model/", params.getMessageModelName() + "-template.txt");
		try {
			this.messagesTemplate = new String(Files.readAllBytes(messageTemplatePath), Charset.forName("UTF-8"));
			// Feeds the global variables
			this.messagesTemplate = replaceGlobalVariables(params, this.messagesTemplate);
		}
		catch(Exception e) {
			messageAndExit("Problem to load the messages template file '" + messageTemplatePath.toString() + "' ! " + e.toString());
		}
		
		// = Load and prepare the messages content =
		String messageContentString = null;
		Path messageContentPath = Paths.get(KgConfig.getInstance().getKafkaGustHome() + "/model/", params.getMessageModelName() + "-content.txt");
		try {
			messageContentString = new String(Files.readAllBytes(messageContentPath), Charset.forName("UTF-8"));
			// Size
			messagesOriginalContentSize = messageContentString.length();
		}
		catch(Exception e) {
			messageAndExit("Problem to load the messages content file '" + messageContentPath.toString() + "' ! " + e.toString());
		}
		// Prepare the duplicated content that will be truncated and injected into every message
		this.messagesDuplicatedContent = new StringBuffer();
		while (messagesDuplicatedContent.length() < params.getMessageSize()) {
			messagesDuplicatedContent.append(messageContentString);
		}
		
		// = Load the messages keys list =
		Path productIdListPath = Paths.get(KgConfig.getInstance().getKafkaGustHome() + "/model/", params.getMessageModelName() + "-keys.txt");
		try {
			messagesKeys = new ArrayList<String>();
			messagesKeys = Files.readAllLines(productIdListPath, Charset.forName("UTF-8"));
		}
		catch(Exception e) {
			messageAndExit("Problem to load the messages keys file '" + productIdListPath.toString() + "' ! " + e.toString());
		}
	}
	
	/**
	 * Replace the global variables
	 */
	private static String replaceGlobalVariables(KgProducersParams params, String messagesTemplate) {
		// Campaign name
		messagesTemplate = messagesTemplate.replace(CAMPAIGN_NAME, params.getCampaignName());
		// Number of producers (or threads)
		messagesTemplate = messagesTemplate.replace(NUMBER_PRODUCERS, String.valueOf(params.getNumberOfProducers()));
		// List of brokers
		if (messagesTemplate.indexOf(BROKER_URIS) > -1) {
			StringBuffer brokerUris = new StringBuffer();
			for (int i=0; i<params.getBrokerUris().size(); i++) {
				if (i>0) brokerUris.append(",");
				brokerUris.append(params.getBrokerUris().get(i)[0]).append(":").append(params.getBrokerUris().get(i)[1]);
			}
			messagesTemplate = messagesTemplate.replace(BROKER_URIS, brokerUris.toString());
		}
		// The topic
		messagesTemplate = messagesTemplate.replace(TOPIC, params.getTopic());
		// The message model name
		messagesTemplate = messagesTemplate.replace(MESSAGE_MODEL, params.getMessageModelName());
		// The message size
		messagesTemplate = messagesTemplate.replace(MESSAGE_SIZE, String.valueOf(params.getMessageSize()));
		// The message key def
		if (params.getMessageKeyDefIndex() == -1) {
			messagesTemplate = messagesTemplate.replace(MESSAGE_KEY_DEF, String.valueOf(params.getMessageKeyDefIndex()));
		}
		else {
			messagesTemplate = messagesTemplate.replace(MESSAGE_KEY_DEF, String.valueOf(params.getMessageKeyDefIndex()) + ":" + String.valueOf(params.getMessageKeyDefDirection()));
		}
		// The number of gusts
		messagesTemplate = messagesTemplate.replace(NUMBER_GUSTS, String.valueOf(params.getNumberOfGusts()));
		// The number of messages per gust
		messagesTemplate = messagesTemplate.replace(NUMBER_MESSAGES_PER_GUST, String.valueOf(params.getNumberOfMessagesPerGust()));
		// The window size in gusts for statistic calculations
		messagesTemplate = messagesTemplate.replace(GUSTS_WINDOW_SIZE, String.valueOf(params.getGustsWindowSize()));
		// The compression codec
		messagesTemplate = messagesTemplate.replace(COMPRESSION_CODEC, String.valueOf(params.getCompressionCodec()));
		// The size of list of messages
		messagesTemplate = messagesTemplate.replace(LIST_SIZE, String.valueOf(params.getListSize()));
		// The sleep time value (ms)
		messagesTemplate = messagesTemplate.replace(SLEEP, String.valueOf(params.getSleep()));
		// The pause time value (ms)
		messagesTemplate = messagesTemplate.replace(PAUSE, String.valueOf(params.getPause()));
		// The number of messages to skip at the begining
		messagesTemplate = messagesTemplate.replace(NUMBER_MESSAGES_TO_SKIP, String.valueOf(params.getNumberOfMessagesToSkip()));
		// The maximum time (ms)
		messagesTemplate = messagesTemplate.replace(MAX_TIME, String.valueOf(params.getMaxTime()));
		// The synchronous/asynchronous mode
		messagesTemplate = messagesTemplate.replace(SYNC_ASYNC, String.valueOf(params.getSyncAsync()));
		// The ack level
		messagesTemplate = messagesTemplate.replace(ACK_LEVEL, String.valueOf(params.getAckLevel()));
		// The pre-hash algo
		messagesTemplate = messagesTemplate.replace(PRE_HASH, String.valueOf(params.getPreHash()));
		// The End Of Line
		messagesTemplate = messagesTemplate.replace(EOL, System.getProperty("line.separator"));
		// Return the message model
		return messagesTemplate;
	}
	
	/**
	 * Print an error message and exits
	 */
	private static void messageAndExit(String message) {
		System.out.println(message);
		System.exit(0);
	}
	
	/**
	 * Returns the template's text enriched with the global variables
	 */
	public String getMessagesTemplate() {
		return messagesTemplate;
	}
	
	/**
	 * Returns the duplicated content (nearly the same size of the message to send)
	 */
	public StringBuffer getMessagesDuplicatedContent() {
		return messagesDuplicatedContent;
	}
	
	/**
	 * Returns the size of the content not duplicated (the text size inside the file 'XXX-content.txt')
	 */
	public int getMessagesOriginalContentSize() {
		return messagesOriginalContentSize;
	}
	
	/**
	 * Returns the list of rows keys (the rows from the file 'XXX-keys.txt'))
	 */
	public List<String> getMessagesKeys() {
		return messagesKeys;
	}
}
