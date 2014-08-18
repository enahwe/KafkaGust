package org.kafkagust.producer;

import java.util.ArrayList;
import java.util.List;

/**
 * The KafkaGust Producers Console
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgProducersConsole {
	
	// The producers parameters depending on the 'args' (used for all the producer threads)
	private KgProducersParams params = null;
	
	// The message model for all producers
	private KgProducersModel model = null;
	
	// List of producers (one for each producer thread)
	private List<KgProducer> producersList = new ArrayList<KgProducer>();

	/**
	 * Main method to call from the Producers shell script
	 */
	public static void main(String[] args) {
		new KgProducersConsole(args);
	}

	/**
	 * Constructor
	 */
	private KgProducersConsole(String[] args) {
		// Init the parameters for all producers
		params = new KgProducersParams();
		params.init(args);
		// Init the message model for all producers
		model = new KgProducersModel(); 
		model.init(params);
		// Create the producers (or threads) and start them
		for (int n=0; n<params.getNumberOfProducers(); n++) {
			KgProducer aProducer = new KgProducer(params, model, n);
			producersList.add(aProducer);
			aProducer.start();
		}
	}
}
