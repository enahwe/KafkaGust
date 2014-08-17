[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donate for KafkaGust")]
(https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8CHMFNQF6VJL2)

![](/KafkaGust.png "KafkaGust")

* **KafkaGust** is easy to use, it has been developped for producing high volumes of messages (based on message templates) on any kind of Kafka infrastructure, by creating statistic files
* By passing a set of arguments (e.g, number of gusts, number of messages in each gust, size of each message, the compression codec, ...) it becomes easier and quicker to launch producers and testing campaigns onto any Kafka environment by calling a single script responsible for sending gusts of messages and the production of statistics continuously
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

## Getting started (start the engine)
* Go to the directory of KafkaGust installation
* Consider the default shell file `ProducersSample` as a reference to copy
* Copy-paste the shell file `ProducersSample` to your prefered file (e.g, `MyFirstProducers`)
* Edit your new shell file and change at the minimum the URI argument (`127.0.0.1:9092`) by the URI of your Kafka broker (or the URIs list of your Kafka brokers separated by a comma)
* Ensure that your Kafka server (or cluster) is running and, **launch your shell**, it will start to strafe gusts of messages towards your Kafka broker(s) and will show you the producing statistics in real time
* Go to the sub-directory `./log/producers` and you will discover two new usefull files :
 * a TXT file related on the standard output
 * a CSV file recalling the statistics of all the gusts of messages
* Stop your shell machine gun with Ctrl-C

## Command details to run producers
### Command pattern
```
# ./bin/Producers campaignName nbrProducers brokerUris topic msgModel msgSize msgKeyDef nbrGusts nbrMsgsPerGust gustsWindowSize compressionCodec listSize sleep pause nbrMsgsToSkip maxTime syncAsync ackLevel preHash
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
* ***topic*** : The topic's name (e.g : *myTopic*)
* ***msgModel*** : The message model name that is the message template prefix (e.g : *TextMsg*, *JsonMsg*, ...)
* ***msgSize*** : The size (in bytes) of every messsage to send
* ***msgKeyDef*** : This is the definition of the message key (or the Kafka key) that will be hashed, in order to load-balance every message towards the correct Kafka partition (by using the well-known algorithm of round-robin)
 * `-2` : The messages keys are null, all the messages will be load-balanced to the partitions in a random way
 * `-1` : The messages keys are based on a absolute counter
 * `I:D` : This definition must use the file '`[msgModel]-datas.txt`', where Index I>=0 and Direction D={-1;0;1} (-1=Backward; 0=Random; 1=Forward)
 * E.g, The definition `0:0` means that the message key will corresponds in the file '`[msgModel]-datas.txt`' to the column at position 0 and will be picked up in a Random way
 * E.g, The definition `1:-1` means that the message key will corresponds in the file '`[msgModel]-datas.txt`' to the column at position 1 and will be picked up in a Backward way
* ***nbrGusts*** : The number of gusts (blocks of messages) to send
* ***nbrMsgsPerGust*** : The number of messages per gust to send
* ***gustsWindowSize*** :  The number of gusts necessary for the statistic window calculations
 * `-1<=N<=1` : No effect, all the statistics will be calculated from the beginning
 * `N>1` : The statistics will be calculated since the last N gusts
* ***compressionCodec*** : The compression codec to compress the messages :
 * `none` or `0`: No compression
 * `gzip` or `1` : Gzip codec
 * `snappy` or `2` : Snappy codec
 * [Note] : This parameter overloads the two properties `"compression.codec"` and `"compressed.topics"` from the Kafka producer's native configuration
* ***listSize*** : It's possible to send a group or a list of messages in once, rather to send all the messages one by one. This can help to improve significantly the producing throughput.
 * `-1<=N<=1` : No effect, all the messages will be sent one by one to Kafka
 * `N>1` : The messages will be sent by groups or lists of N messages to Kafka
 * [Note] : This feature use the native method '`send()`' in charge of sending lists of 'KeyedMessage'
* ***sleep*** : The time sleep (in ms) between every message.This can be useful to slow down willingly the producing throughput
 * `0` : No slowdown
 * `T>0` : The producers will wait for `T` ms between every sending
* ***pause*** : The time to wait for at the starting before to send the first messages. This can be usefull to synchronize the launching with any Kafka consumers
 * `0` : No pause
 * `T>0` : The producers will wait for `T` ms at the starting before to send the first messages
* ***nbrMsgsToSkip*** : The number of messages to skip at the starting for statisitic calculations (even if those messages are sent anyway). This can be usefull if we want to fire only one gust with lot of messages without to depend at the starting of the gradual increase in speed of Kafka 
 * `0` : No message skipped at all
 * `N>0` : The `N` first messages will be skipped for the statistic calculations
* ***maxTime*** : The time (in ms) or the timeout after which the producers will automatically stop
 * `-1` : No effect, the producers will never stop
 * `0` : The producers will stop immediatly
 * `T>0` : The producers will stop after `T` ms
* ***syncAsync*** : The Synchronous/Asynchronous mode.  By setting the producers to `async` we allow batching together of requests (which is great for throughput) but open the possibility of a failure of the client machine dropping unsent data
 * `sync` or `0` : Synchronous send
 * `async` or `1` : Asynchronous send, the messages are sent asynchronously in a background thread
 * [Note] : This parameter overloads the property `"producer.type"` from the Kafka producer's native configuration
* ***ackLevel*** : The Kafka acknowledgement level. This value controls when a produce request is considered completed
 * `-1` : All in-sync
 * `0` : No ack at all
 * `1` : Leader ack only
 * [Note] : This parameter overloads the property `"request.required.acks"` from the Kafka producer's native configuration
* ***preHash*** : It's possible to transform every message's key into a MD5 or SHA hexadecimal string before to submit it to kafka (before the hashing phase). In some cases it's possible by using this trick, to increase two times better the homogeneous load-balancing of messages towards the Kafka partitions
 * `none` or `0` : No transformation
 * `sha` or `1` : Transforms the message key into a SHA hexadecimal string before to submit it to kafka
 * `md5` or `2` : Transforms the message key into a MD5 hexadecimal string before to submit it to kafka

## Message template for producers
* The sub-directory `./model` contains two samples of models : "*TextMsg*" and "*JsonMsg*"
* Each file suffixed by `-template.txt` contains the structure of messages to send, enriched with variables
* Each file suffixed by `-keys.txt` contains a list of keys that it's possible to use as Kafka keys to load-balance the messages to the Kafka partitions (see the ***msgKeyDef*** argument)
* Each file suffixed by `-content.txt` contains the raw text to add in your messages, in addition to the variables

**You can customize your own model by adding inside your template file the variables you want, as described bellow :**

### Global variables (same for all messages)
* ${CAMPAIGN_NAME} : The ***campaignName*** argument
* ${NUMBER_PRODUCERS} : The ***nbrProducers*** argument
* ${BROKER_URIS} : The ***brokerUris*** argument
* ${TOPIC} : The ***topic*** argument
* ${MESSAGE_MODEL} : The ***msgModel*** argument
* ${MESSAGE_SIZE} : The ***msgSize*** argument
* ${MESSAGE_KEY_DEF} : The ***msgKeyDef*** argument
* ${NUMBER_GUSTS} : The ***nbrGusts*** argument
* ${NUMBER_MESSAGES_PER_GUST} : The ***nbrMsgsPerGust*** argument
* ${GUSTS_WINDOW_SIZE} : The ***gustsWindowSize*** argument
* ${COMPRESSION_CODEC} : The ***compressionCodec*** argument
* ${LIST_SIZE} : The ***listSize*** argument
* ${SLEEP} : The ***sleep*** argument
* ${PAUSE} : The ***pause*** argument
* ${NUMBER_MESSAGES_TO_SKIP} : The ***nbrMsgsToSkip*** argument
* ${MAX_TIME} : The ***maxTime*** argument
* ${SYNC_ASYNC} : The ***syncAsync*** argument
* ${ACK_LEVEL} : The ***ackLevel*** argument
* ${PRE_HASH} : The ***preHash*** argument

### Message variables (specific for each message)
* ${MESSAGE_KEY} : The message's Kafka key (depends on the ***msgKeyDef*** argument)
* ${MESSAGE_CREATION_TIME} : The absolute time (ms) when the message has been created
* ${MESSAGE_CREATION_RELATIVE_TIME} : The relative time (ms) when the message has been created
* ${GUST_COUNTER} : The gust's counter
* ${GUST_START_TIME} : The absolute time (ms) when the gust has been started
* ${MESSAGE_COUNTER_IN_GUST} : The message counter inside the gust
* ${ABSOLUTE_MESSAGE_COUNTER} : The message (absolute) counter since the producer has started
* ${MESSAGE_CONTENT} : The content duplicated and inserted into every message (see the file suffixed by `-content.txt`)

## Producers examples
Send continuously gusts of 1000 messages 10KB based on template 'TextMsg', with an "all in-sync" Kafka acknowledgment :

```
# ./bin/Producers MyCampaign 1 127.0.0.1:9092 myTopic TextMsg 10240 -1 -1 1000 -1 none -1 0 0 0 -1 sync -1 none
```

Send continuously gusts of 1000 ***compressed*** (snappy) messages 10KB based on template 'TextMsg', with an "all in-sync" Kafka acknowledgment :

```
# ./bin/Producers MyCampaign 1 127.0.0.1:9092 myTopic TextMsg 10240 -1 -1 1000 -1 snappy -1 0 0 0 -1 sync -1 none
```
