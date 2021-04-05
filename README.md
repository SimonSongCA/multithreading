# udemy-multithreading

This is a repository including some projects of multithreading, concurrency, and performance optimization in Java. Contents of the repository are subject to change.

Projects are wraaped with individual folders under the 'src' folder, and the current running envrionment of the projects is IntelliJ version 2019.3.

To implement the word count feature in ThroughputHttpServer.java:
1. Run the file ThroughputHttpServer.java in an IDE.
2. Open a web browser and type http://localhost:8000/search?word= in the search bar. 
3. To query the total appearances of a specific word in the text file under the 'resources' folder(currently war_and_peace.txt), append the word that you would like to search after the equal('=') sign at the end of the search bar content.
4. Hit enter in the search bar. The web page will return the total number of appearences of that specific word. 

To test the throughput of ThroughputHttpServer.java:
1. Start the code of the java file as described in step-1 above.
2. Go to the folder apache-jmeter-5.4/bin and run jmter.exe
3. Open the configuration file 'performance_test_plan.jmx' of JMeter in the directory /src/performanceOptimization/resources/performance_test_plan.jmx
4. Click the ‘Start' button of the JMeter app. The real-time test result will show in the 'Summary Report' section under the 'While Controller' tab.
