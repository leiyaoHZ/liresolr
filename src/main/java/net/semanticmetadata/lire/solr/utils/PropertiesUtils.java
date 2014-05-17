package net.semanticmetadata.lire.solr.utils;

import org.apache.solr.core.SolrCore;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class PropertiesUtils {

	public static Properties getProperties(SolrCore core) {
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(core.getCoreDescriptor().getInstanceDir() + "conf/liresolr.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
