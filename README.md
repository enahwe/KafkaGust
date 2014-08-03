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
# ./bin/Producer nbrThreads campaignName brokerUris topic msgTemplate nbrMsgs msgSize compression listSize sleep pause nbrMsgsSkipped timeout asyncMode ackLevel preHash
```
### Command example
```
# ./bin/Producer 1 MyCampaign 127.0.0.1:9092 myTopic DefaultMsg 1000 10240 0 1 0 0 0 -1 sync -1 0
```

### Parameters
* ***nbrThreads*** : The number of producers (or threads) to execute
* ***campaignName*** : The campaign's name (e.g : *MyCampaign*)
 * Info : Overloads the property "*client.id*" from the Kafka producer's native configuration
* ***uriList*** : The URL list of brokers (e.g : *192.168.1.1:9092,192.168.1.2:9092*)
 * Info : Overloads the property "*metadata.broker.list*" from the Kafka producer's native configuration
* ***topic*** : The topic's name (e.g : *myTestTopic*)
* ***msgTemplate*** : The template used to send the messages (e.g : *DefaultMsg*, *JsonMsg*, ...)
* ***nbrMsgs*** : The number of messages to send in every gust (block of messages)
 * If ***timeout=-1*** then several gusts of messages will be sent
* ***msgSize*** : The size (in bytes) for every messsage
* ***batch*** : Two values :
 * *0* to send every message one by one,
 * *1* to send a list of messages (list size=nbrMsgs)
* ***sleep*** : The sleep time between two messages, usefull when we want to decrease the throughput (e.g : *10*)
* ***pause*** : The time to wait before to send the first message, can be usefull to synchronize the launching of consumers
* ***nbrMsgsSkipped*** : The number of first messages to skip (for statisitics only, the first messages will be sent anyway)
* ***timeout*** :
 * If = *0*, the application will stop after the gust of messages has been sent.
 * If > *0*, the application will stop after this value (ms).
 * If = *-1*, then the application will never stop to send gusts of messages.
* ***producerType*** : Two values :
 * *sync* means synchronous,
 * *async* means asynchronous (see the 'producer.type' Kafka property for more information)
* ***producerAck*** : Three values :
 * *-1* for all in-sync,
 * *0* for no ack at all,
 * *1* for leader ack only
 * Info : Overloads the property "*request.required.acks*" from the Kafka producer's native configuration
* ***preHash*** : Three values :
 * *-1* for SHA pre-hash,
 * *0* for no pre-hash,
 * *1* for MD5 pre-hash.
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
