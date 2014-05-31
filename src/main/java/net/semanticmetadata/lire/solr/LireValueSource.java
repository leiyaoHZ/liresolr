package net.semanticmetadata.lire.solr;

import net.semanticmetadata.lire.imageanalysis.*;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DocTermsIndexDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.util.Base64;

import java.io.IOException;
import java.util.Map;

/**
 * A query function for sorting results based on the LIRE CBIR functions.
 * Implementation based partially on the outdated guide given on http://www.supermind.org/blog/756,
 * comments on the mailing list provided from Chris Hostetter, and the 4.4 Solr & Lucene source.
 */
public class LireValueSource extends ValueSource {
	String field = "cl_hi";  //
	byte[] histogramData;
	LireFeature feature, tmpFeature;
	double maxDistance = -1;

	/**
	 * @param featureField
	 * @param hist
	 * @param maxDistance  the distance value returned if there is no distance calculation possible.
	 */
	public LireValueSource(String featureField, byte[] hist, double maxDistance) {
		if (featureField != null) field = featureField;
		if (!field.endsWith("_hi")) field += "_hi";
		this.histogramData = hist;
		this.maxDistance = maxDistance;

		if (field == null) {
			feature = new EdgeHistogram();
			tmpFeature = new EdgeHistogram();
		} else {
			if (field.equals("cl_hi")) {
				feature = new ColorLayout();
				tmpFeature = new ColorLayout();
			} else if (field.equals("jc_hi")) {
				feature = new JCD();
				tmpFeature = new JCD();
			} else if (field.equals("ph_hi")) {
				feature = new PHOG();
				tmpFeature = new PHOG();
			} else if (field.equals("oh_hi")) {
				feature = new OpponentHistogram();
				tmpFeature = new OpponentHistogram();
			} else {
				feature = new EdgeHistogram();
				tmpFeature = new EdgeHistogram();
			}
		}
		// debug ...
		System.out.println("Setting " + feature.getClass().getName() + " to " + Base64.byteArrayToBase64(hist, 0, hist.length));
		feature.setByteArrayRepresentation(hist);
	}

	public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
		final FieldInfo fieldInfo = readerContext.reader().getFieldInfos().fieldInfo(field);
		if (fieldInfo != null && fieldInfo.getDocValuesType() == FieldInfo.DocValuesType.BINARY) {
			final BinaryDocValues binaryValues = FieldCache.DEFAULT.getTerms(readerContext.reader(), field, true);
			return new FunctionValues() {
				BytesRef tmp = new BytesRef();

				@Override
				public boolean exists(int doc) {
					return bytesVal(doc, tmp);
				}

				@Override
				public boolean bytesVal(int doc, BytesRef target) {
					binaryValues.get(doc, target);
					return target.length > 0;
				}

				// This is the actual value returned
				@Override
				public float floatVal(int doc) {
					binaryValues.get(doc, tmp);
					if (tmp.length > 0) {
						tmpFeature.setByteArrayRepresentation(tmp.bytes, tmp.offset, tmp.length);
						return tmpFeature.getDistance(feature);
					} else return -1f;
				}

				@Override
				public Object objectVal(int doc) {
					return floatVal(doc);
				}

				@Override
				public String toString(int doc) {
					return description() + '=' + strVal(doc);
				}

				@Override
				/**
				 * This method has to be implemented to support sorting!
				 */
				public double doubleVal(int doc) {
					return (double) floatVal(doc);
				}
			};
		} else {
			// there is no DocVal to sort by. Therefore we need to set the function value to -1 and everything without DocVal gets ranked first?
			return new DocTermsIndexDocValues(this, readerContext, field) {
				@Override
				protected String toTerm(String readableValue) {
					return Double.toString(maxDistance);
				}

				@Override
				public Object objectVal(int doc) {
					return maxDistance;
				}

				@Override
				public String toString(int doc) {
					return description() + '=' + strVal(doc);
				}


				public double doubleVal(int doc) {
					return maxDistance;
				}
			};
		}
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String description() {
		return "distance to a given feature vector";
	}


}
