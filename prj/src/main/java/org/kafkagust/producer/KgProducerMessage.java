package org.kafkagust.producer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * The KafkaGust Producer Message
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducerMessage {
	
	// Message variables
	protected final static String PRODUCER_NUMBER = "${PRODUCER_NUMBER}";
	protected final static String MESSAGE_KEY = "${MESSAGE_KEY}";
	protected final static String MESSAGE_CREATION_TIME = "${MESSAGE_CREATION_TIME}";
	protected final static String MESSAGE_CREATION_RELATIVE_TIME = "${MESSAGE_CREATION_RELATIVE_TIME}";
	protected final static String GUST_COUNTER = "${GUST_COUNTER}";
	protected final static String GUST_START_TIME = "${GUST_START_TIME}";
	protected final static String MESSAGE_COUNTER_IN_GUST = "${MESSAGE_COUNTER_IN_GUST}";
	protected final static String ABSOLUTE_MESSAGE_COUNTER = "${ABSOLUTE_MESSAGE_COUNTER}";
	protected final static String MESSAGE_CONTENT = "${MESSAGE_CONTENT}";
	protected final static int MESSAGE_CONTENT_VAR_LENGTH = MESSAGE_CONTENT.length();
	
	// The producer number
	private int producerNumber = -1;
	
	// Producer start time
	private long producerStartTime = 0;
	
	// The KafkaGust producers parameters
	private KgProducersParams params = null;
	
	// The message model for all producers
	private KgProducersModel model = null;
	
	// Full ready message that contains all the variables and the data
	protected String currentMessage = null;
	
	// Key used to load-balance the message to the Kafka producer
	protected String currentMessageKey = null;
	
	// Absolute messages counter
	private long absoluteMessagesCounter = 0;
	
	// Keys row index
	private int keysRowIndex = -1;
	
	// Random generator
	private Random randomGenerator = null;
	
	/**
	 * Init
	 */
	public void init(int producerNumber, long producerStartTime, KgProducersParams params, KgProducersModel model) throws IOException {
		// The producer number
		this.producerNumber = producerNumber;
		// Producer start time
		this.producerStartTime = producerStartTime;
		// KafkaGust producers parameters
		this.params = params;
		// The messages model
		this.model = model;
		// Init the message key index
		if (params.getMessageKeyDefIndex() > -1) {
			// Backward init
			if (params.getMessageKeyDefDirection() == -1) {
				keysRowIndex = model.getMessagesKeys().size() - 1;
			}
			// Forward init
			else if (params.getMessageKeyDefDirection() == 1) {
				keysRowIndex = 0;
			}
		}
		// Init the random generator
		randomGenerator = new Random();
	}
	
	/**
	 * Replace the message variables
	 */
	public void replaceMessageVariables(long messageCounterInGust, long gustCounter, long gustStartTime) throws Exception {
		// Message creation time
		long messageCreationTime = new Date().getTime();
		
		// Make a copy the of the messages template
		currentMessage = new String(this.model.getMessagesTemplate());
		
		// Producer number
		currentMessage = currentMessage.replace(PRODUCER_NUMBER, String.valueOf(this.producerNumber));
		
		// Message key
		currentMessageKey = getMessageKey(model.getMessagesKeys(), params.getMessageKeyDefIndex(), params.getMessageKeyDefDirection());
		currentMessage = currentMessage.replace(MESSAGE_KEY, currentMessageKey);
		
		// Message creation absolute time
		currentMessage = currentMessage.replace(MESSAGE_CREATION_TIME, String.valueOf(messageCreationTime));

		// Message creation relative time
		currentMessage = currentMessage.replace(MESSAGE_CREATION_RELATIVE_TIME, String.valueOf(messageCreationTime - this.producerStartTime));
		
		// The gust counter
		currentMessage = currentMessage.replace(GUST_COUNTER, String.valueOf(gustCounter));
		
		// The gust start time
		currentMessage = currentMessage.replace(GUST_START_TIME, String.valueOf(gustStartTime));
		
		// Message counter in gust
		currentMessage = currentMessage.replace(MESSAGE_COUNTER_IN_GUST, String.valueOf(messageCounterInGust));
		
		// Absolute messages counter
		currentMessage = currentMessage.replace(ABSOLUTE_MESSAGE_COUNTER, String.valueOf(absoluteMessagesCounter));
		
		// Inject the data by sizing the message with the right global length
		int currentMessageLength = currentMessage.length() - MESSAGE_CONTENT_VAR_LENGTH;
		int miminumAcceptableLength = currentMessageLength + model.getMessagesOriginalContentSize();
		if (miminumAcceptableLength > params.getMessageSize()) { // The minimum message size exceeds the expected message size
			throw new Exception("The message size is too high (" + miminumAcceptableLength + "), please increase the message size and/or decrease the property 'producers.messages.content_to_duplicate' !");
		}
		currentMessage = currentMessage.replace(MESSAGE_CONTENT, model.getMessagesDuplicatedContent().substring(0, params.getMessageSize() - currentMessageLength));
		
		// Increments the absolute messages counter
		absoluteMessagesCounter++;
	}
	
	/**
	 * Returns the message key
	 */
	private String getMessageKey(List<String> messagesKeys, int keyIndex, int direction) {
		int listSize = messagesKeys.size();
		String key = null;
		// Key is null
		if (keyIndex == -2) {
			key = null;
		}
		// Key is a counter
		else if (keyIndex == -1) {
			key = String.valueOf(absoluteMessagesCounter);
		}
		// Key comes from the list of keys (from the file)
		else if (keyIndex > -1 && (direction==-1 || direction==0 || direction==1)) {
			// Random access
			if (direction == 0) {
				int randomKeysRowIndex = randomGenerator.nextInt(listSize);
				key = getKeyFromRow(messagesKeys, randomKeysRowIndex, keyIndex);
			}
			else {
				key = getKeyFromRow(messagesKeys, keysRowIndex, keyIndex);
				// Backward access
				if (direction == -1) {
					keysRowIndex --;
					if (keysRowIndex < 0) {
						keysRowIndex = listSize - 1;
					}
				}
				// Forward access
				else if (direction == 1) {
					keysRowIndex ++;
					if (keysRowIndex >= listSize) {
						keysRowIndex = 0;
					}
				}
			}
		}
		return key;
	}
	
	/**
	 * Return a key from a row of keys
	 */
	private String getKeyFromRow(List<String> messagesKeys, int rowIndex, int keyIndex) {
		String keysRow = messagesKeys.get(rowIndex).trim();
		String[] keys = keysRow.split(",");
		String key = keys[keyIndex].trim();
		return key;
	}
	
	/**
	 * Get the full message to send
	 */
	public String getMessageToSend() {
		return currentMessage;
	}
	
	/**
	 * Get the key of the message
	 */
	public String getMessageKey() {
		return currentMessageKey;
	}
}
