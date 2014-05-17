#!/usr/bin/env python
import json

__author__ = 'ferdous'

import os
import urllib2
import xml.etree.ElementTree as ET


def run(folder_name="/Users/ferdous/projects/digitalcandy/liresolr/data/images/"):
    for file_name in os.listdir(folder_name):
        if file_name.endswith("xml"):
            print 'posting file to solr: ', file_name
            save(folder_name, file_name)
        
def save(folder_name, file_name):
        xml_file = folder_name + file_name
        try:
            tree = ET.parse(xml_file)
            fields = tree.findall("doc/field")
            params = [{'id': file_name, 'title': file_name}]

            for field in fields:
                key = field.get('name')
                if key != 'id':
                    params[0][key] = field.text
            #params[0]['id'] = file_name            
            url = 'http://localhost:8888/solr/MediaItems/update/json?commit=true'
            data = json.dumps(params)
            print data
            req = urllib2.Request(url)
            req.add_header('Content-type', 'application/json')
            req.data = data
            r = urllib2.urlopen(req)
            print r.read()
            r.close()
        except Exception, ex:
            pass


if __name__ == "__main__":
    run()
