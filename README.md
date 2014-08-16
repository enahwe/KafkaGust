[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donate for KafkaGust")]
(https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8CHMFNQF6VJL2)

![](/KafkaGust.png "KafkaGust")

* **KafkaGust** has been developped for producing high volumes of messages (based on message templates) on any kind of Kafka infrastructure, by creating statistic files
* By passing a set of arguments (e.g, number of gusts, number of messages in each gust, size of each message, the message's model, the compression codec, the batch mode, ...) it becomes easier and quicker to launch producers and testing campaigns onto any Kafka environment by calling a single script responsible for sending gusts of messages and the production of statistics continuously
* KafkaGust can also be useful for comparing the performances results with any kind of Kafka client applications developped in different programming languages (e.g : C#, Python...)
* KafkaGust uses the Java native Kafka library and can be executed directly from any kind of Operating System (e.g, Windows, Mac, Linux)

## Requirements
* A JDK version 1.7 minimum must be installed on the same machine
* The JAVA_HOME environment variable must be set

## Installation
* Download the latest KafkaGust installation file (e.g, `KafkaGust-Vx.y.tar.gz`)
* Uncompress the file to your prefered folder :
  * From Linux, Mac, Unix :
    * ```$ tar -xvf KafkaGust-Vx.y.tar.gz```
  * From Windows :
    * Use your prefered tool to uncompress the installation file (e.g : with 7zip)
* By default, a sub-directory named 'KafkaGust' and containing all the files will be created

## Getting started
* Go to the directory of KafkaGust installation
* Consider the default shell file `ProducersSample` as a reference to copy
* Copy-paste the shell file `ProducersSample` to your prefered file (e.g, `MyFirstProducers`)
* Edit your new shell file and change at the minimum the URI (`127.0.0.1:9092`) by the URI of your Kafka broker (or the URIs list of your Kafka brokers separated by a comma)
* Ensure that your Kafka server (or cluster) is running and launch your shell, it will start to strafe gusts of messages towards your Kafka broker(s) and will show you the producing statistics in real time
* Go to the sub-directory `./log/producers` and you will discover two actually usefull files :
 * a TXT file related on the standard output
 * a CSV file recalling the statistics of all the gusts of messages
* Stop your shell with Ctrl-C

## Command for to run producers
### Command pattern
```
# ./bin/Producers campaignName nbrProducers brokerUris topic msgModel msgSize msgKey nbrGusts nbrMsgsPerGust gustsWindowSize compressionCodec listSize sleep pause nbrMsgsToSkip maxTime syncAsync ackLevel preHash
```
### Command example
```
# ./bin/Producers MyCampaign 1 127.0.0.1:9092 myTopic DefaultMsg 10240 1:0 -1 1000 -1 none -1 0 0 0 -1 sync -1 none
```

### Arguments (19)
* ***campaignName*** : The campaign's name (e.g : *MyCampaign*)
 * [Note] : This parameter overloads the property `"client.id"` from the Kafka producer's native configuration
* ***nbrProducers*** : The number of producers (or threads) to execute
* ***brokerUris*** : The URI list of brokers (e.g : *192.168.1.1:9092,192.168.1.2:9092*)
 * [Note] : This parameter overloads the property `"metadata.broker.list"` from the Kafka producer's native configuration
* ***topic*** : The topic's name (e.g : *myTestTopic*)
* ***msgModel*** : The message model or the message template prefix (e.g : *TextMsg*, *JsonMsg*, ...)
* ***msgSize*** : The size (in bytes) for every messsage
* ***msgKey*** : This is the definition of the message key (or the Kafka key) that will be hashed, in order to load-balance every message towards the correct Kafka partition (by using the well-known algorithm of round-robin)
 * `-1` : No definition, all the messages will be load-balanced to the partitions in a random way
 * `I:D` : Must use the file '`[msgModel]-datas.txt`', with Index I>=0 and Direction D={-1;0;1} (-1=Backward; 0=Random; 1=Forward)
 * E.g, `0:0` Means that the message key will corresponds in the file to the column at position 0 and will be picked up in a Random way
 * E.g, `1:-1` Means that the message key will corresponds in the file to the column at position 1 and will be picked up in a Backward way
* ***nbrGusts*** : The number of gusts (blocks of messages) to send
* ***nbrMsgsPerGust*** : The number of messages to send for every gust
* ***gustsWindowSize*** :  The last N gusts (window) necessary for statistic calculations
 * `-1<=N<=1` : No effect, all the statistics will be calculated from the beginning
 * `N>1` : The statistics will be calculated from the last N gusts
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
* ***preHash*** : It's possible to transform every message's key into a MD5 or SHA hexadecimal string before to submit it to kafka (before the hashing phase)
 * `none` or `0` : No transformation
 * `sha` or `1` : Transforms the message key into a SHA hexadecimal string before to submit it to kafka
 * `md5` or `2` : Transforms the message key into a MD5 hexadecimal string before to submit it to kafka
 * [Note] : In some cases it's possible by using this trick, to increase two times better the homogeneous load-balancing of messages towards the Kafka partitions


## Message template for producers
The sub-directory 'template' contains the two example templates "*DefaultMsg*" and "*JsonMsg*".

Also, you can create your own format templates by inserting inside it the following variables :

### Common variables (same for all messages)
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

### Message variables (specific for each message)
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
