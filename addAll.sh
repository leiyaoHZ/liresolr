#!/bin/sh

curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary @/Users/ferdous/projects/digitalcandy/datam/data/solr_xml/0.jpg_solr.xml
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary @add1.xml
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary @add2.xml
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary @add3.xml
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<commit/>"