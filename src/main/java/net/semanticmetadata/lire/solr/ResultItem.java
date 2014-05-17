package net.semanticmetadata.lire.solr;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

/**
 * Created by ferdous on 5/17/14.
 */
class ResultItem {
	private LireFeature feature;
	private String id;

	ResultItem(LireFeature feature, String id) {
		this.feature = feature;
		this.id = id;
	}
}
