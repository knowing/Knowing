## Situation

Every user produces more and more data. There are some data mining frameworks or tools, e.g [WEKA](http://www.cs.waikato.ac.nz/ml/weka/) or [RapidMiner](http://rapid-i.com/), which help to gain valuable knowledge out of this data. Both applications based on Java don't make use of modular technology like OSGi, which we see as a big shortcoming.

## Goals

We don't want to provide a new data mining framework which should replace the olds. Instead we want to add an abstraction layer above existing frameworks. 

* Easy deployment to OSGi environments
* Easy integration into existing systems via a Service Orientated Architecture (SOA)
* Scala API for more powerful and expressiv code (along with Java API)
* Highly parallel execution via Actor based programming with [Akka](http://akka.io)
* Wrappper data mining API for existing libraries (e.g. WEKA)
* Developer tools to easly generate portable data mining tasks

## Use Cases

There are two typical use-cases:

1. Use _Knowing_ as a framework for consumer applications
2. Implement / migrate algorithms to use them in OSGi backend environments

### Consumer applications

You want to create a modular data mining application? Knowing helps you to plugin in new algorithms by using a OSGi architecture. Algorithms are just services and you can register and deregistern them. This keeps your application small and flexible. You can also run the algorithms on a java application server which means you can use fancy UI-Systems like [Vaadin](http://vaadin.com/) to provide data mining tasks right on your website.

### Implement / migrate algorithms

Knowing offers an API which makes it easy to migrate your old algorithms or implement new ones. Currently there's only a WEKA wrapping API which wraps existing WEKA classifier, clusterer and filter into services which can be used right away.

## Building and Running

At the moment Knowing is ALPHA stadium. However it actually works. To build and run all Knowing plugins your have following requirements:

* java 1.6
* scala 2.9.0.1
* OSGi Environment (Equinox, Felix,..)

Checkout the source and open it with your favorite Java IDE. Take care that the Bundle-Manifest is include when building. Start the OSGi Framework or create an OSGi-launchconfiguration in Eclipse and install/start the following bundles:

* de.lmu.ifi.dbs.knowing.core
* All bundles here: [https://github.com/knowing/Knowing/tree/master/lib](knowing-libs)

The plugin in _knowing.test_ includes examples how to run example configurations. 
__knowing.test__ is an eclipse RCP application. You can build it with the following
dependencies

* de.lmu.ifi.dbs.knowing.core.swt
* de.lmu.ifi.dbs.knowing.core.swt.charts

## Eclipse Plugin

There is a small Editor for Eclipse Helios which let's you define your own DPUs. However it is not yet released, because some features must be completed first. [Here](http://www.youtube.com/watch?v=rglFwZCVZ9Y&) is a little video, where you can get check out how far we've got.

