  # 💳 Real-Time Fraud Detection with Apache Spark Streaming (Java + Netcat)
This project demonstrates how to implement a **real-time fraud detection system** using **Apache Spark Streaming** with **Java**.
The system listens to streaming data via a socket (simulated with Netcat) and flags potentially fraudulent transactions based on simple rules.

## What the Java Code Does
The Spark job:

1. Connects to a local socket stream at `localhost:9999`.
2. Reads each transaction line as a string.
3. Applies custom logic to determine whether it is **fraudulent** (via the `FraudDetectionStream` class).
4. Logs suspicious transactions to the console.



## Environment & Tools
This project was developed and tested using the following setup on macOS:
* Java JDK 11+ — Programming language used to build the Spark streaming job
* Apache Spark 3.5.5 — Distributed processing engine for real-time data
* IntelliJ IDEA — IDE used for Java development
* Maven — Project management and build automation tool (used to handle dependencies and build the JAR)
* Netcat (nc) — Lightweight network utility (used to simulate a real time streaming data into Spark). macOS comes with Netcat pre-installed, so no extra installation is needed. If not found, you can install it via Homebrew:
`brew install netcat`


## ⚙️ Prerequisites

Ensure the following are installed:
1. **Java 8 or higher**:
   - Download and install from [Adoptium](https://adoptium.net/).

2. **Apache Spark**:
   - Download Spark from the [official website](https://spark.apache.org/downloads.html).
   - Unzip the folder and set a root folder for the Spark. Keep the location in mind as we will need it later
   - Set the `SPARK_HOME` environment variable: Allow you to run Spark, Hadoop, and Java commands globally
        * open the terminal (Cmd + space bar --> type terminal) - computer terminal will open
        * type `nano ~/.zshrc` - this will open and edit the zee shell that macOS uses(also knows as bash- previously name)
        * in the newly open window type the following but make sure you change the path to your own
<pre>```export SPARK_HOME=/Users/anedelcu/apache-spark/spark-3.5.5-bin-hadoop3
export PATH=$PATH:$SPARK_HOME/bin:$PATH
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.14.jdk/Contents/Home
export HADOOP_HOME=/Users/anedelcu/hadoop-install/hadoop-3.4.1
export PATH=$PATH:$HADOOP_HOME/bin:$JAVA_HOME/bin``` </pre>

        * Save and exit (Ctrl + X, then Y, then Enter).


3. **Maven**:
   - Install Maven using Homebrew:
     ```zee shell
     brew install maven


4. **Dataset**:
   - Download the flight delay dataset ([Bank Transactions Dataset](https://www.kaggle.com/datasets/ealaxi/paysim1)).
   - Save the dataset as a CSV file (e.g., `transactions_data.csv`).

 ## Create the Java class with the variable dependencies: ##
 1. Set Up Your Java Project in IntelliJ
    - a. open IntelliJ -->File --> New --> Project --> choose a project name (bank-transaction-analysis) and location. Select Maven as the build system. 
    - b.Once it opens up, in `src/main/java` directory(left hand side) use the package called `org.example` / or you can create your own
    - c.Right click on the package --> new -->Java class. Name it FraudDetectionStream

 2. Configure the variable Dependencies (pom.xml)
- Because we choose Maven, a file called pom.xml was created by default.
- Go in pom.xml and all the way at the bottom, after the </property> add the following dependencies:
```
 <dependencies>
 <!-- Apache Spark Core -->
  <dependency>
  <groupId>org.apache.spark</groupId>
  <artifactId>spark-core_2.12</artifactId>
  <version>3.5.5</version>
  </dependency>
  
  <!-- Apache Spark SQL -->
  <dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>3.5.5</version>
  </dependency>
</dependencies>   
```

 3. Build and Run the Project
    - a. open the terminal --> navigate to the root directory of your project that contains the pom.xml file.
      - * you navigate using cd command: `cd path/to/your/project` eg: cd/apache-spark/java-work/bank-transaction-analysis
    - b. compiling the project by running in the terminal `mvn clean compile`
    - c. package the project into a JAR file by running in the terminal `mvn clean package`
      - * this command generate a JAR file from the compiled class in newly created  /target folder in our bank-transaction-analysis directory
      - * check your work by going to the root directory, and check for the jar file that should be called bank-transactions-analysis-1.0-SNAPSHOT.jar
       
## Split the Data into Chunks
We simulate real-time data streaming by breaking a large CSV file(transactions_data.csv) into smaller pieces (chunks). Each chunk will represent a batch of records sent at regular intervals to the Spark Streaming application. This mimics how data might arrive in a real-world scenario.  
Split the dataset as follow:  
* navigate to ypur dataset directory using `cd path/to/your/saved/directory` , mine is
   cd /Users/anedelcu/Lavinia_Nedelcu/School/datasets-for-project/Stream-processing
* split the dataset(transactions_data.csv) into chunks of 1,000 rows each by typing in the terminal:
 `split -l 1000 transactions.csv chunk_

## Create a Shell Script to feed the chunks via Netcat
We'll simulate a data stream by sending one chunk at a time over a local socket using Netcat.  
a.  Open the terminal and type `nano send_chunks.sh`  
b.  In the send_chunk.sh copy and paste the code. This will send a chunk every 5 seconds to localhost:9999 <pre>
#!/bin/bash

PORT=9999  # Port you're using with socketTextStream
HOST=localhost
CHUNKS_DIR="/Users/anedelcu/Lavinia_Nedelcu/School/datasets-for-project/Stream-processing"
//Loop through each .txt chunk file and feed them to the socket
for chunk in "$CHUNKS_DIR"/chunk_*; do
    echo "📤 Sending: $chunk"
    if [ -f "$chunk" ]; then  # Check if it's a file before trying to send
        nc $HOST $PORT < "$chunk"
        echo "✅ Sent: $chunk"
        sleep 5  # Wait before sending the next one
    else
        echo "❌ No matching files found for $chunk"
    fi
done
</pre>  
c. Make the script executable: back in the open terminal type `chmod +x send_chunks.sh` to give your operating system permisiion to run this file as a program  

## FUTURE WORK  
Now that your Java project is built and your dataset is split into chunks, the next steps will bring the pipeline together so you can simulate real-time fraud detection.  
1. Open a Terminal for Netcat to Listen
2. Run the Spark Streaming job <pre>spark-submit \
  --class neiu.edu.FraudDetectionStream \
  --master "local[*]" \
  /Users/anedelcu/apache-spark/java-work/bank-transactions-analysis/target/bank-transactions-analysis-1.0-SNAPSHOT.jar </pre>  
3. Send Chunks of Data to simulate real-time data processing
`

