package neiu.edu;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaDStream;

public class FraudDetectionStream {

    public static void main(String[] args) throws InterruptedException {
        // Spark Configuration
        SparkConf conf = new SparkConf()
                .setAppName("RealTimeFraudDetection")
                .setMaster("local[*]"); // Use all cores locally

        // Create Java Streaming Context with 5-second batch interval
        JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // Connect to Netcat stream (run: nc -lk 9999 in another terminal) // sets up to read data from the socket
        JavaReceiverInputDStream<String> lines = ssc.socketTextStream("127.0.0.1", 9999);

        // Process each RDD in the DStream (Discretized Stream = Stream of RDDs)
        lines.foreachRDD(rdd -> {  //foreachRDD- used to perform operations on each RDD(each batch in the stream)

            System.out.println("=== New RDD Received ==="); //helps to see if Spark receive and process the stream
            System.out.println("RDD count: " + rdd.count()); //count the records in th RDD(just to monitor how many records(lines) are being processed in each batch)

            rdd.foreach(line -> { // now for each line of the batch(RDD) the code does the following
                // Skip header or bad rows(if it's less then 12 fields)
                if (line.startsWith("id") || line.split(",").length < 12) return;

                // splits the line(transaction) into individual components based on comma. ==>each filed is separated and placed into an array
                String[] parts = line.split(",");
                try { //extract these individual values from above array and assigns them to a variable
                    String transactionID = parts[0];
                    String date = parts[1];
                    String clientID = parts[2];
                    String cardID = parts[3];
                    double amount = Double.parseDouble(parts[4]);
                    boolean useChip = parts[5].trim().equals("1");
                    String merchantID = parts[6];
                    String merchantCity = parts[7];
                    String merchantState = parts[8];
                    String zip = parts[9];
                    String mcc = parts[10];
                    String errors = parts[11];

                    boolean isFraud = false;
                    String reason = ""; //will be filled later with flagged transactions

                    // Rule 1: High transaction amount
                    if (amount > 99.0) {
                        isFraud = true;
                        reason += "[High Amount] ";
                    }

                    // Rule 2: Transaction has errors -- if error field is not empty it indicates an issue --> flagged as a fraud
                    if (!errors.trim().isEmpty()) {
                        isFraud = true;
                        reason += "[Transaction Errors] ";
                    }

                    // Rule 3: Suspicious location --other than hometown
                    if (merchantCity.equalsIgnoreCase("Miami")) {
                        isFraud = true;
                        reason += "[Suspicious Location] ";
                    }

                    // Rule 4: Chip not used
                    if (!useChip) {
                        isFraud = true;
                        reason += "[No Chip Used] ";
                    }

                    if (isFraud) {  //if any of the fraud rules are triggered - print details and reason
                        System.out.println("🚨 FRAUD DETECTED! 🚨 TransactionID: " + transactionID +
                                ", Amount: $" + amount + ", CardID: " + cardID + ", Reason: " + reason);
                    } else {
                        System.out.println(" Legit Transaction: " + transactionID + " | $" + amount);
                    }

                } catch (Exception e) {
                    System.err.println(" Skipped malformed line: " + line);
                }
            });
            // **Print when the chunk is completed**
            System.out.println("**Completed processing chunk with " + rdd.count() + " records.**"); // gives an indication of how many transactions were processed
        });
    // Start streaming
        ssc.start(); //starts the Spark streaming context, which will beguine processing the data from the stream
        ssc.awaitTermination(); // keeps the application running to continue processing data
    }
}
