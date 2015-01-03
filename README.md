PSOSSL is...
======

A Platform for searches of optimized solutions for the satellites layout problem.

It's written in Java with JavaFX and designed to be simple, easy to use and expansive with a simple way to add new optimization algorithms to test.

Building
-------

Regardless of your operating system, you must have the following things installed on it and included in your path:

JRE 1.7 or greater
JavaFX 2.0 or greater
Maven 3.x or greater
Git

Open a system shell and check out the sources into some directory. Then 'cd' into that directory and type:

'mvn clean package'

The resulting built artifacts will be located under the target directory. 

To launch it, run:

'java -jar ./target/psossl-"version".jar'

Pay atention in the version of the jar in order to execute the right file.

That's it.
