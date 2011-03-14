SleepArchiver Mac readme


The program should run on OS X starting from version 10.6.


Java

	Program requires Java 1.6.0_10 or latter to run.

	You can check which version you have installed:
	http://java.com/en/download/installed.jsp?detect=jre&try=1
	(or by using "java -version" command)


Connector driver

	If you want to use a connection cable to import data from the watch, 
	you should download and install a driver from FTDI official site:
		http://www.ftdichip.com/Drivers/VCP.htm


Serial port configuration

	You may need to run "configure-serial.sh" script to enable data transfer.

Portable mode
  
	If local "settings" directory exists, program stores all settings 
	in that directory instead of user profile.

	You may also copy "jre" directory of the JRE installation 
	into the program directory to run Java in portable mode.


Starting the program

	Use "./SleepArchiver.sh" to start the program.

	(or manually run "java -Djava.library.path=. -jar SleepArchiver.jar")

	Open "Tutorial.xmz" with the program to see the tutorial.

	
Feedback

	If you want to suggest a feature, report a bug or get help,
	please feel free to contact me: mail@pavelfatin.com

  
http://pavelfatin.com