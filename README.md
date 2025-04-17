  # 💳 Real-Time Fraud Detection with Apache Spark Streaming (Java + Netcat)
This project demonstrates how to implement a **real-time fraud detection system** using **Apache Spark Streaming** with **Java**.
The system listens to streaming data via a socket (simulated with Netcat) and flags potentially fraudulent transactions based on simple rules.

## What the Java Code Does

The Spark job:

1. Connects to a local socket stream at `localhost:9999`.
2. Reads each transaction line as a string.
3. Applies custom logic to determine whether it is **fraudulent** (via the `FraudDetectionStream` class).
4. Logs suspicious transactions to the console.

---

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

 3. Configure the variable Dependencies (pom.xml)
- Because we choose Maven, a file called pom.xml was created by default.
- Go in pom.xml and all the way at the bottom, after the </property> add the following dependencies:
`
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
</dependencies>`
<pre> ```xml <dependencies> <!-- Apache Spark Core --> <dependency> <groupId>org.apache.spark</groupId> <artifactId>spark-core_2.12</artifactId> <version>3.5.5</version> </dependency> <!-- Apache Spark SQL --> <dependency> <groupId>org.apache.spark</groupId> <artifactId>spark-sql_2.12</artifactId> <version>3.5.5</version> </dependency> </dependencies> ``` </pre>

 4. Build and Run the Project
        a.open the terminal --> navigate to the root directory of your project that contains the pom.xml file.
           *        you navigate using cd command: cd path/to/your/project: eg cd/apache-spark/spark-3.5.5-bin-hadoop3/java-work/flight-delay-analysis
        b. compiling the project: run "mvn clean compile"
        c. package the project into a JAR file: run "mvn clean package"
           *         this command generate a JAR file from the compiled class in newly created  /target folder in our flight-delay-analysis directory
           *         Check your work by going to the root directory, and check for the jar file that should be called flight-delay-analysis-1.0-SNAPSHOT.jar
