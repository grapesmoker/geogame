geogame
=======

The original GeoGame built in GWT.

## What is it?

The GeoGame is a foraging task in which players are asked to find items spread out across a map. Players are connected to each other via a communication network (whose structure they do not know); in order to help them with their task, players ask questions of their network, and receive replies, which may or may not be useful. Networks are not closed, meaning that the network of your neighbor is different from your network.

The goal of the GeoGame was to study how information propagation in networks influences task performance. The results were published in [this paper (PDF)](http://www.david-reitter.com/pub/reitter2011policies.pdf).

## Stack

The GeoGame is built on the Google Web Toolkit (GWT), which compiles Java down to JavaScript for client-side operations. Storage is MySQL with a Hibernate ORM layer.

## Credit

A great deal of this code (probably around 60%) was written by Antonio Juarez (cited as co-author on the paper linked above); that code was in turn based on a core written by a previous author whose name escapes me. My own contribution is probably around 25% of the total codebase.
