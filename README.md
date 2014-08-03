# KafkaGust

* **KafkaGust** has been written for producing high volumes of messages (based on message templates) by creating statistic files, and by using any kind of Kafka infrastructure.
* By defining a set of features (e.g, number of messages, template of message, size of each message, message variables, message key, compression, batch mode, ...) it becomes easy and quick to bench any Kafka environment by simply creating a single script containing a test campaign for sending gusts (or blocks) of messages.
* KafkaGust can also be useful for comparing the performances results with other Kafka client applications implemented in any programming language (e.g : Java, .NET C# API, Python).
* KafkaGust uses the Java native Kafka library and can be executed directly from any kind of Operating System (e.g, Windows, Mac, Linux).

## Requirements
* A JDK version 1.7 minimum must be installed
* The JAVA_HOME environment variable must be set

## Installation
* Download the last KafkaGust install 'KafkaGust-Vx.y.tar.gz'
* Uncompress the file to your prefered folder :
 * (by default a sub-directory 'KafkaGust' containing all the files will be created)
  * From Linux, Mac, Unix :
    * $ tar -xvf KafkaGust-Vx.y.tar.gz
  * From Windows :
    * Uncompress 'KafkaGust-Vx.y.tar.gz' twice (e.g : with 7zip)

## KafkaGust Producer command
### Command's pattern
```
# ./bin/Producer nbrThreads campaignName brokerUris topic msgTemplate nbrMsgs msgSize compression listSize sleep pause nbrMsgsSkipped maxTime asyncMode ackLevel preHash
```
### Command example
```
# ./bin/Producer 1 MyCampaign 127.0.0.1:9092 myTopic DefaultMsg 1000 10240 0 1 0 0 0 -1 sync -1 0
```

### Parameters
* ***nbrThreads*** : The number of producers (or threads) to execute
* ***campaignName*** : The campaign's name (e.g : *MyCampaign*)
 * Info : Overloads the property "*client.id*" from the Kafka producer's native configuration
* ***brokerUris*** : The URI list of brokers (e.g : *192.168.1.1:9092,192.168.1.2:9092*)
 * Info : Overloads the property "*metadata.broker.list*" from the Kafka producer's native configuration
* ***topic*** : The topic's name (e.g : *myTestTopic*)
* ***msgTemplate*** : The template used to send the messages (e.g : *DefaultMsg*, *JsonMsg*, ...)
* ***nbrMsgs*** : The number of messages to send in every gust (block of messages)
 * If ***timeout=-1*** then several gusts of messages will be sent
* ***msgSize*** : The size (in bytes) for every messsage
* ***compression*** : The compression codec to compress the messages. Three values :
 * `none` or `0`: No compression
 * `gzip` or `1` : Gzip codec
 * `snappy` or `2` : Snappy codec
 * Info : Overloads the two properties "*compression.codec*" and "*compressed.topics*" from the Kafka producer's native configuration
* ***listSize*** : Two values :
 * `0` : Send every message, one by one
 * `1` : Send the list of messages
* ***sleep*** : The time sleep (in ms) between every message, useful to decrease the throughput
 * Note : A time sleep of zero won't cause any slowdown
* ***pause*** : The time to wait before to send the first message, can be usefull to synchronize the launching of consumers
* ***nbrMsgsSkipped*** : The number of first messages to skip (for statisitics only, the first messages will be sent anyway)
* ***maxTime*** :
 * `0` : No effect
 * If `>0` : The application will stop after this value (in ms)
* ***asyncMode*** : Two values :
 * `sync` or `0` : Synchronous send
 * `async` or `1` : Asynchronous send
 * Info : Overloads the property "*producer.type*" from the Kafka producer's native configuration
* ***producerAck*** : Three values :
 * `-1` : All in-sync
 * `0` : No ack at all
 * `1` : Leader ack only
 * Info : Overloads the property "*request.required.acks*" from the Kafka producer's native configuration
* ***preHash*** : Three values :
 * `-1` : SHA pre-hash
 * `0` : No pre-hash at all (the message key is submitted to Kafka as is)
 * `1` : MD5 pre-hash
 * The pre-hash consists to pre-calculate from a Kafka key a new Kafka key formated MD5 or SHA. This trick should increase two times better the homogeneous scattering of messages towards the multi-partitions topic (Kafka modulo Round-Robin). For example, by submitting a MD5 hexadecimal key (e.g : New_Kafka_key = HEX(MD5(Previous_Kafka_key))) Kafka will hash that new hexadecimal key and its routages will be more balanced.

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
