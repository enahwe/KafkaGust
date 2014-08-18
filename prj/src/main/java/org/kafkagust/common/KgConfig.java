package org.kafkagust.common;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * The KafkaGust Configuration
 * @copyright : KafkaGust - Philippe.ROSSIGNOL (enahwe@gmail.com)
 */
public class KgConfig {
	
	// KafkaGust singleton
	private static KgConfig kgConfig = null;
	
	// KafkaGust Home
	private String kafkaGustHome = null;
	
	// KafkaGust config properties
	private Properties properties = null;
	
	// Default properties values for output
	private static boolean DEFAULT_PRODUCERS_OUTPUT_STDOUT_ENABLED = true;
	private static boolean DEFAULT_PRODUCERS_OUTPUT_FILE_ENABLED = true;
	private static String DEFAULT_PRODUCERS_OUTPUT_DIR = "${env.KAFKAGUST_HOME}/log/producers";
	private static String DEFAULT_PRODUCERS_OUTPUT_FILENAME_SUFFIX = "-outputs.log";
	
	// Default properties values for statistic
	private static boolean DEFAULT_PRODUCERS_STATISTIC_STDOUT_ENABLED = false;
	private static boolean DEFAULT_PRODUCERS_STATISTIC_FILE_ENABLED = true;
	private static String DEFAULT_PRODUCERS_STATISTIC_DIR = "${env.KAFKAGUST_HOME}/log/producers";
	private static String DEFAULT_PRODUCERS_STATISTIC_FILENAME_SUFFIX = "-statistics.csv";
	private static String DEFAULT_PRODUCERS_STATISTIC_COLUMN_SEPARATOR = ",";

	// Properties values for output
	private boolean producerOutputStdoutEnabled = false;
	private boolean producerOutputFileEnabled = false;
	private String producerOutputDir = null;
	private String producerOutputFilenameSuffix = null;
	
	// Properties values for statistic
	private boolean producerStatisticStdoutEnabled = false;
	private boolean producerStatisticFileEnabled = false;
	private String producerStatisticDir = null;
	private String producerStatisticFilenameSuffix = null;
	private String producerStatisticColumnSeparator = null;
	
	/**
	 * The singleton constructor
	 */
	private KgConfig() {
		this.init();
	}
	
	/**
	 * Initializes the singleton
	 */
	private void init() {
		// Retrieve the home variable from the environment variable
		kafkaGustHome = System.getenv("KAFKAGUST_HOME");
		
		// Notify an error if the home variable hasn't been found
		if (kafkaGustHome == null || kafkaGustHome.trim().equals("")) {
			systemErrExit("Error : Can't find the environment variable KAFKAGUST_HOME !");
		}
		
		// Canonizes the path of the home
		try {
			kafkaGustHome = new File(kafkaGustHome).getCanonicalPath().replace('\\', '/');
		}
		catch (IOException e) {
			systemErrExit("Error : Problem to canonize the variable KafkaGust home : " + e.toString());
		}
		
		// Loads the KafkaGust config file
		properties = new Properties();
		try {
			properties.load(new FileReader(kafkaGustHome + "/conf/KafkaGust.conf"));
		}
		catch (FileNotFoundException e) {
			systemErrExit("Error : Can't find the KafkaGust config file : " + e.toString());
		}
		catch (IOException e) {
			systemErrExit("Error : Problem to load the KafkaGust config file : " + e.toString());
		}
		
		// Replaces environment variables by its value inside of all the other KafkaGust properties
		Properties newProperties = new Properties();
		for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            
            // Replaces every environment variable (e.g, ${env.KAFKAGUST_HOME}) by its value inside the KafkaGust property value
            String enrichedValue = enrichPropValWithEnvVars(value);
           
			// Add the enriched value to the new properties list
			newProperties.setProperty(key, enrichedValue);
        }
		// New properties list
		properties = newProperties;
		
		// Load explicitly the properties for the output (file and stdout)
		producerOutputStdoutEnabled = getBooleanProperty("producers.output.stdout.enabled", DEFAULT_PRODUCERS_OUTPUT_STDOUT_ENABLED);
		producerOutputFileEnabled = getBooleanProperty("producers.output.file.enabled", DEFAULT_PRODUCERS_OUTPUT_FILE_ENABLED);
		producerOutputDir = enrichPropValWithEnvVars(getProperty("producers.output.dir", DEFAULT_PRODUCERS_OUTPUT_DIR)).trim();
		producerOutputFilenameSuffix = enrichPropValWithEnvVars(getProperty("producers.output.filename.suffix", DEFAULT_PRODUCERS_OUTPUT_FILENAME_SUFFIX)).trim();
		
		// Load explicitly the properties for the statistic (file and stdout)
		producerStatisticStdoutEnabled = getBooleanProperty("producers.statistic.stdout.enabled", DEFAULT_PRODUCERS_STATISTIC_STDOUT_ENABLED);
		producerStatisticFileEnabled = getBooleanProperty("producers.statistic.file.enabled", DEFAULT_PRODUCERS_STATISTIC_FILE_ENABLED);
		producerStatisticDir = enrichPropValWithEnvVars(getProperty("producers.statistic.dir", DEFAULT_PRODUCERS_STATISTIC_DIR)).trim();
		producerStatisticFilenameSuffix = enrichPropValWithEnvVars(getProperty("producers.statistic.filename.suffix", DEFAULT_PRODUCERS_STATISTIC_FILENAME_SUFFIX)).trim();
		producerStatisticColumnSeparator = enrichPropValWithEnvVars(getProperty("producers.statistic.column.separator", DEFAULT_PRODUCERS_STATISTIC_COLUMN_SEPARATOR)).trim();
		if (producerStatisticColumnSeparator.equals("/t") || producerStatisticColumnSeparator.equals("\\t")) {
			producerStatisticColumnSeparator = String.valueOf('\t');
		}
	}
	
	/**
	 * Returns the singleton instance
	 */
	public static KgConfig getInstance() {
		if (kgConfig==null) {
			kgConfig = new KgConfig();
		}
		return kgConfig;
	}
	
	/**
	 * Returns the KafkaGust home
	 */
	public String getKafkaGustHome() {
		return kafkaGustHome;
	}
	
	/**
	 * Returns the producer output stdout enabled
	 */
	public boolean getProducerOutputStdoutEnabled() {
		return producerOutputStdoutEnabled;
	}
	
	/**
	 * Returns the producer output file enabled
	 */
	public boolean getProducerOutputFileEnabled() {
		return producerOutputFileEnabled;
	}
	
	/**
	 * Returns the producer output directory
	 */
	public String getProducerOutputDir() {
		return producerOutputDir;
	}
	
	/**
	 * Returns the producer output filename suffix
	 */
	public String getProducerOutputFilenameSuffix() {
		return producerOutputFilenameSuffix;
	}
	
	/**
	 * Returns the producer statistic stdout enabled
	 */
	public boolean getProducerStatisticStdoutEnabled() {
		return producerStatisticStdoutEnabled;
	}
	
	/**
	 * Returns the producer statistic file enabled
	 */
	public boolean getProducerStatisticFileEnabled() {
		return producerStatisticFileEnabled;
	}
	
	/**
	 * Returns the producer statistic directory
	 */
	public String getProducerStatisticDir() {
		return producerStatisticDir;
	}
	
	/**
	 * Returns the producer statistic filename suffix
	 */
	public String getProducerStatisticFilenameSuffix() {
		return producerStatisticFilenameSuffix;
	}
	
	/**
	 * Returns the producer statistic column separator
	 */
	public String getProducerStatisticColumnSeparator() {
		return producerStatisticColumnSeparator;
	}
	
	/**
	 * Returns the KafkaGust properties
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * Gets a KafkaGust property
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	/**
	 * Gets a KafkaGust property with a default value
	 */
	private String getProperty(String key, String defaultValue) {
		String value = null;
		try {value = properties.getProperty(key);} catch (Exception e) {value = null;}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	/**
	 * Gets a KafkaGust boolean property with a default value
	 */
	private boolean getBooleanProperty(String key, boolean defaultValue) {
		boolean booleanValue = defaultValue;
		try {
			booleanValue = Boolean.parseBoolean(properties.getProperty(key).trim());
		}
		catch (Exception e) {
			booleanValue = defaultValue;
		}
		return booleanValue;
	}
	
	/**
	 * Replaces every environment variable (e.g, ${env.MY_VAR}) by its true value inside the property
	 */
	private String enrichPropValWithEnvVars(String value) {
		if (value != null) {
			int i = -1;
			int offset = 0; // ${env.x}
			while((i = value.indexOf("${env", offset)) > -1) {
				char s = value.charAt(i+5); // .
				int e = value.indexOf("}", i+7);
				if (e>-1) {
					String envKey = value.substring(i+6, e);
					String envValue = System.getenv(envKey);
        			value = value.replace("${env" + s + envKey + "}", envValue);
				}
				offset = i + 4;
			}
		}
        return value;
	}
	
	/**
	 * Prints an error message and exits
	 */
	private static void systemErrExit(String message) {
		System.err.println(message);
		System.exit(0);
	}
}
