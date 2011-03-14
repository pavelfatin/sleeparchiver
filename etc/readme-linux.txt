SleepArchiver Linux readme


Please, ensure that you have selected a proper version of the program:
	- use x86 version if you have 32-bit Java runtime
	- use x64 version if you have 64-bit Java runtime (64-bit OS only)

Installation

	To install all the dependencies in Ubuntu,
	execute "sudo ./install.sh" command.

	Use "./SleepArchiver.sh" to start the program.
	

Java

	Program requires Java 1.6.0_10 or latter to run.

	You can check which version you have installed:
	http://java.com/en/download/installed.jsp?detect=jre&try=1
	(or by using "java -version" command)

	See your Linux distributive documentation to
	discover a method to install Java.
	(for Ubuntu: execute "sudo apt-get install sun-java6-bin",
	 or see https://help.ubuntu.com/community/Java)

	 
Fonts

	Program looks better with standard Microsoft TrueType fonts,
	see http://corefonts.sourceforge.net/ for details.
	(for Ubuntu: type "sudo apt-get install msttcorefonts" or use
	 Synaptic to install "msttcorefonts" package)


Connector driver

	If you want to use a connection cable to import data from the watch, 
	you should load "ftdi_sio" kernel module.
	(Ubuntu loads it by default)

	Type "sudo sh -c 'echo ftdi_sio >> /etc/modules'" to add it 
	to the modules list. 

	Use "sudo modprobe ftdi_sio" to load the module immediately.

	You may download and compile a driver from FTDI official site:
		http://www.ftdichip.com/Drivers/VCP.htm


Portable mode
  
	If local "settings" directory exists, program stores all settings 
	in that directory instead of user profile.

	You may also copy "jre" directory of the JRE installation 
	into the program directory to run Java in portable mode.

	
Starting the program

	Execute "./SleepArchiver.sh" to start the program
	(open "Tutorial.xmz" with the program to see the tutorial).

	
Feedback

	If you want to suggest a feature, report a bug or get help,
	please feel free to contact me: mail@pavelfatin.com

  
http://pavelfatin.com