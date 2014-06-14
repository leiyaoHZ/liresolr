#!/usr/bin/env python

import json

__author__ = 'ferdous'

import os
import urllib2
import xml.etree.ElementTree as ET
from urllib import quote_plus

def run(folder_name="/data/digitalcandy/ml/images/"):
    for file_name in os.listdir(folder_name):
        if file_name.endswith("xml"):
            save(folder_name, file_name)
        else:
            print '[not an xml file] ', file_name
        
def save(folder_name, file_name):
        xml_file = folder_name + file_name
        try:
            tree = ET.parse(xml_file)
            fields = tree.findall("field")
            params = [{'id': file_name, 'title': file_name, 'url': 'http://localhost/images/' + quote_plus(file_name[:-9])}]

            for field in fields:
              key = field.get('name')
              params[0][key] = field.text
            url = 'http://localhost:8888/solr/Media/update/json?commit=true'
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
