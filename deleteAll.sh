#!/bin/sh

curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<delete><query>*:*</query></delete>"
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<commit/>"