### Knowing

The **Knowing** framework is designed for modular, data mining applications which run in an OSGi environment. Furthermore Knowing should help to scale data mining task into a cloud system. Project main goals are:

* Wrappper data mining API for existing libraries (e.g. WEKA)
* Data mining-process structure
* Distributed / Parallel execution

Further information at [Knowing Wiki](https://github.com/knowing/eclipse_medmon/wiki/)

## Usage

1. Use Knowing as a framework for consumer applications
2. Implement / migrate algorithms to use them in OSGi environments

### Consumer applications

You want to create a modular data mining application? Knowing helps you to plugin in new algorithms by using a OSGi architecture. Algorithms are just services and you can register and deregistern them. This keeps your application small and flexible. You can also run the algorithms on a java application server!

### Implement / migrate algorithms

Knowing offers an API which makes use of some java.util.concurrent features to reach a high level of scalability and realiability. Migrating your existing algorithms means just to implement two or three interfaces to get them OSGi ready.

## Building and Running

At the moment Knowing is at a very ALPHA stadium. However it actually works. To build and run all Knowing plugins your have following requirements:

* java 1.6
* OSGi Environment (Equinox, Felix,..)

Checkout the source and open it with your favorite Java IDE. Take care that the Bundle-Manifest is include when building. Start the OSGi Framework or create an OSGi-launchconfiguration in Eclipse and install/start the following bundles:

* nz.ac.waikato.cs.weka
* de.lmu.ifi.dbs.knowing.core

The plugin in _knowing.test_ includes examples how to run example configurations.

## Eclipse Plugin

As soon as possible we will start developing an eclipse plugin, offering the possibility to develope, deploy and test your algorithms immediately. 

