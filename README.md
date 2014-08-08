[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donate for KafkaGust")](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8CHMFNQF6VJL2)

# KafkaGust

* **KafkaGust** has been developped for producing high volumes of messages (based on message templates) by creating statistic files, on any kind of Kafka infrastructure
* By defining a set of features (e.g, number of messages, template of message, size of each message, message variables, message key, compression, batch mode, ...) it becomes easy and quick to bench any Kafka environment by simply creating a single script containing a test campaign for sending gusts (or blocks) of messages
* KafkaGust can also be useful for comparing the performances results with other Kafka client applications implemented in any programming language (e.g : Java, .NET C# API, Python)
* KafkaGust uses the Java native Kafka library and can be executed directly from any kind of Operating System (e.g, Windows, Mac, Linux)

## Requirements
* A JDK version 1.7 minimum must be installed
* The JAVA_HOME environment variable must be set

## Installation
* Download the last KafkaGust install 'KafkaGust-Vx.y.tar.gz'
* Uncompress the file to your prefered folder :
  * From Linux, Mac, Unix :
    * $ tar -xvf KafkaGust-Vx.y.tar.gz
  * From Windows :
    * Uncompress 'KafkaGust-Vx.y.tar.gz' twice (e.g : with 7zip)
 * Note : By default a sub-directory 'KafkaGust' containing all the files will be created

## KafkaGust Producer command
### Command's pattern
```
# ./bin/Producer campaignName nbrProducers brokerUris topic msgTemplate nbrGusts nbrMsgsPerGust msgSize compressionCodec listSize sleep pause nbrMsgsToSkip maxTime syncAsync ackLevel preHash gustsWindowSize
```
### Command example
```
# ./bin/Producer MyCampaign 1 127.0.0.1:9092 myTopic DefaultMsg -1 1000 10240 none 1 0 0 0 -1 sync -1 none -1
```

### Parameters
* ***campaignName*** : The campaign's name (e.g : *MyCampaign*)
 * [Note] : This parameter overloads the property `"client.id"` from the Kafka producer's native configuration
* ***nbrProducers*** : The number of producers (or threads) to execute
* ***brokerUris*** : The URI list of brokers (e.g : *192.168.1.1:9092,192.168.1.2:9092*)
 * [Note] : This parameter overloads the property `"metadata.broker.list"` from the Kafka producer's native configuration
* ***topic*** : The topic's name (e.g : *myTestTopic*)
* ***msgTemplate*** : The template used to send the messages (e.g : *DefaultMsg*, *JsonMsg*, ...)
* ***nbrGusts*** : The number of gusts (blocks of messages) to send
* ***nbrMsgsPerGust*** : The number of messages to send for every gust
* ***msgSize*** : The size (in bytes) for every messsage
* ***compressionCodec*** : The compression codec to compress the messages. Three values :
 * `none` or `0`: No compression
 * `gzip` or `1` : Gzip codec
 * `snappy` or `2` : Snappy codec
 * [Note] : This parameter overloads the two properties `"compression.codec"` and `"compressed.topics"` from the Kafka producer's native configuration
* ***listSize*** : Allows to send lists of messages in once, rather to send messages one by one (can help to improve the throughput)
 * `-1<=N<=1` : No effect, all the messages will be sent one by one to Kafka
 * `N>1` : The messages will be sent by list of N messages to Kafka
 * [Note] : This feature use the 'send()' native method for sending a list of 'KeyedMessage'
* ***sleep*** : The time sleep (in ms) between every message (can be useful to slow down willingly the throughput)
 * `0` : No slowdown
 * `T>0` : The producer will wait for T ms between every send
* ***pause*** : The time to wait before to send the first message (can be usefull to synchronize the launching of consumers)
* ***nbrMsgsSkipped*** : The number of first messages to skip (for statisitics only, the first messages will be sent anyway)
* ***maxTime*** : The time (in ms) after which the producer will automatically stop
 * `-1` : No effect, the producer will never stop
 * `>=0` : The producer will stop after this value
* ***syncAsync*** : The Synchronous/Asynchronous mode
 * `sync` or `0` : Synchronous send
 * `async` or `1` : Asynchronous send
 * [Note] : This parameter overloads the property `"producer.type"` from the Kafka producer's native configuration
* ***ackLevel*** : The Kafka acknowledgement level
 * `-1` : All in-sync
 * `0` : No ack at all
 * `1` : Leader ack only
 * [Note] : This parameter overloads the property `"request.required.acks"` from the Kafka producer's native configuration
* ***preHash*** : The pre-hashing algorithm to hash each message's key
 * `none` or `0` : No pre-hashing at all (each message's key will be submitted to Kafka as is)
 * `sha` or `1` : SHA pre-hashing
 * `md5` or `2` : MD5 pre-hashing
 * [Note] : The pre-hash consists to pre-calculate from a Kafka key a new Kafka key formated MD5 or SHA. This trick should increase two times better the homogeneous scattering of messages towards the multi-partitions topic (Kafka modulo Round-Robin). For example, by submitting a MD5 hexadecimal key (e.g : New_Kafka_key = HEX(MD5(Previous_Kafka_key))) Kafka will hash that new hexadecimal key and its routages will be more balanced.
* ***gustsWindowSize*** :  The last N gusts (window) necessary for statistic calculations
 * `-1<=N<=1` : No effect, all the statistics will be calculated from the beginning
 * `N>1` : The statistics will be calculated from the last N gusts

## KafkaGust Producer messages
The sub-directory 'template' contains the two example templates "*DefaultMsg*" and "*JsonMsg*".

Also, you can create your own format templates by inserting inside it the following variables :

### Variables commons to all messages
* ${NUMBER_THREADS} : The ***nbrThreads*** parameter
* ${CAMPAIGN_TITLE} : The ***campaignTitle*** parameter
* ${URI_LIST} : The ***uriList*** parameter
* ${TOPIC} : The ***topic*** parameter
* ${MSG_TEMPLATE} : The ***msgTemplate*** parameter
* ${NUMBER_MESSAGES} : The ***nbrMsgs*** parameter
* ${MESSAGE_SIZE} : The ***msgSize*** parameter
* ${BATCH} : The ***batch*** parameter
* ${SLEEP} : The ***sleep*** parameter
* ${PAUSE} : The ***pause*** parameter
* ${NUMBER_MESSAGES_SKIPPED} : The ***nbrMsgsSkipped*** parameter
* ${TIMEOUT} : The ***timeout*** parameter
* ${PRODUCER_TYPE} : The ***producerType*** parameter
* ${PRODUCER_ACK} : The ***producerAck*** parameter
* ${PRE_HASH} : The ***preHash*** parameter

### Variables for each message
Standard variables
* ${MESSAGE_KEY} : The message's Kafka key
* ${MESSAGE_CREATION_TIME} : The absolute time (ms) when the message has been created
* ${MESSAGE_CREATION_RELATIVE_TIME} : The relative time (ms) when the message has been created
* ${GUST_COUNTER} : The gust's counter
* ${GUST_CREATION_TIME} : The absolute time (ms) when the gust of messages has been created
* ${MESSAGE_CONTENT} : The message's content (e.g : n times the letter 'D' according the size of the message we want)

Extended variables
* ${REFERENCE_ID} : The "reference_id" field from a Product
* ${PRODUCT_ID} : The "product_id" field from a Product
