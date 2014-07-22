# KafkaGust

* **KafkaGust** has been written for producing and consuming of high volumes of messages (based on message templates) on any kind of Kafka infrastructure.
* KafkaGust uses the native Kafka library and can be executed directly from any kind of Operating System (e.g, Windows, Mac, Linux).
* KafkaGust is also useful for comparing the performances results with other Kafka clients implemented in any programming language (e.g : Java, .NET C# API, Python).
* By defining a set of features (e.g, number of messages, template of message, size of each message, message variables, messages keys, batch mode, ...) it becomes easy and quick to bench all Kafka environments by simply creating single scripts (from a parent shell) each containing a campaign of tests.

## Requirements
* Need to use a JDK V1.7 at the minimum
* The JAVA_HOME environment variable must be installed

## Installation
* Download the KafkaGust file 'KafkaGust-V1.x.tar.gz'
* Uncompress the file to your prefered folder (by default a sub-directory 'KafkaGust' containing all the files will be created)
* From Linux : $ tar -xvf KafkaGust.tar.gz
* From Windows : Uncompress the 'KafkaGust-V1.x.tar.gz' (e.g : with 7zip)

## Producer command : Usage
### Pattern
```
# ./bin/Producer nbrThreads campaignTitle uriList topic msgTemplate nbrMsgs msgSize batch sleep pause nbrMsgsSkipped timeout producerType producerAck preHash
```

### Parameters
* **[nbrThreads]** : The number of threads (or producers) to execute
* **[campaignTitle]** : The campaign's title, only used as information. (e.g : *TESTDEV*)
* **[uriList]** : The URL list of brokers (e.g : *10.190.8.163:9092,10.190.8.164:9092*)
* **[topic]** : The topic's name (e.g : *test*)
* **[msgTemplate]** : The message template to send (e.g : *Default*, *CDiscountPrdRef* for productRef as Kafka key, 'CDiscountPrdId' for productId as Kafka key)
* **[nbrMsgs]** : The number of messages top send
* **[msgSize]** : The size (in bytes) for every messsage
* **[batch]** : Two values : *0* to send every message one by one, *1* to send a list of messages (list size=nbrMsgs)
* **[sleep]** : The sleep time between two messages, usefull when we want to decrease the throughput (e.g : *10*)
* **[pause]** : The time to wait before to send the first message, can be usefull to synchronize the launching of consumers
* **[nbrMsgsSkipped]** : The number of first messages to skip (for statisitics only, the first messages will be sent anyway)
* **[timeout]** : If = *0*, the application will stop after all the messages has been sent. If > *0*, the application will stop after this value (ms). If = *-1*, then the application will never stop.
* **[producerType]** : Two values : *sync* means synchronous, *async* means asynchronous (see the 'producer.type' Kafka property for more information)
* **[producerAck]** : Three values : *-1* for all in-sync, *0* for no ack at all, *1* for leader ack only (see the 'request.required.acks' Kafka property for more information)
* **[preHash]** : Three values : *-1* for SHA pre-hash, *0* for no pre-hash, *1* for MD5 pre-hash.
The pre-hash consists to pre-calculate from a Kafka key a new Kafka key formated MD5 or SHA.
This trick should increase two times better the homogeneous scattering of messages towards the multi-partitions topic (Kafka modulo Round-Robin).
For example, by submitting a MD5 hexadecimal key (e.g : New_Kafka_key = HEX(MD5(Previous_Kafka_key)))
Kafka will hash that new hexadecimal key and its routages will be more balanced.

### Example
```
# ./bin/Producer 1 TESTDEV 192.168.253.134:9092 test CDiscountPrdRef 20000 10240 0 0 0 1000 0 sync -1 0
```

## Producer template : Message variables
The sub-directory 'template' contains the two templates 'Default' and 'CDiscount'.
It's possible to adapt the template to use by inserting the variables as described below.

### Global variables (see the descriptions above)
* ${CAMPAIGN_TITLE}
* ${URI_LIST}
* ${TOPIC}
* ${MSG_TEMPLATE}
* ${NUMBER_MESSAGES}
* ${MESSAGE_SIZE}
* ${SLEEP}
* ${PAUSE}
* ${NUMBER_MESSAGES_SKIPPED}
* ${TIMEOUT}
* ${PRODUCER_TYPE}
* ${PRODUCER_ACK}
* ${PRE_HASH}

### Message variables
* ${MESSAGE_KEY} : The message's Kafka key
* ${MESSAGE_CREATION_TIME} : The absolute time (ms) when the message has been created
* ${MESSAGE_CREATION_RELATIVE_TIME} : The relative time (ms) when the message has been created
* ${BLOCK_COUNTER} : The block's counter
* ${BLOCK_CREATION_TIME} : The absolute time (ms) when the block of messages has been created
* ${MESSAGE_CONTENT} : The message's content (e.g : n times the letter 'D' according the size of the message we want)

### Extended variables 
* ${REFERENCE_ID} : The "reference_id" field from a Product
* ${PRODUCT_ID} : The "product_id" field from a Product
