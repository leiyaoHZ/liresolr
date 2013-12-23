#!/bin/sh

curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<delete><query>"id:\"file:/Users/ferdous/projects/digitalcandy/datam/data/thumbnails/.jpg\""</query></delete>"
curl http://localhost:8888/solr/images/update  -H "Content-Type: text/xml" --data-binary "<commit/>"