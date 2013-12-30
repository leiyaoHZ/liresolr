#!/bin/sh

curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<delete><query>{!raw f=id}file:/Users/ferdous/projects/digitalcandy/datam/data/thumbnails/.DS_Store</query></delete>"
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<commit/>"