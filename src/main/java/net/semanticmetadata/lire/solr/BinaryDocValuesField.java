package net.semanticmetadata.lire.solr;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.util.Base64;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Base64 -> DocValues implementation used for the Solr Plugin. Using this field one can index byte[] values by
 * sending them to Solr base64 encoded. In case of the LIRE plugin, the fields get read linearly, so they need to be
 * extremely fast, which is the case with the DocValues.
 * @author Mathias Lux, mathias@juggle.at, 12.08.2013
 */
public class BinaryDocValuesField extends FieldType {

    private String toBase64String(ByteBuffer buf) {
        return Base64.byteArrayToBase64(buf.array(), buf.position(), buf.limit() - buf.position());
    }

    @Override
    public void write(TextResponseWriter writer, String name, IndexableField f) throws IOException {
        writer.writeStr(name, toBase64String(toObject(f)), false);
    }

    @Override
    public SortField getSortField(SchemaField field, boolean top) {
        throw new RuntimeException("Cannot sort on a Binary field");
    }


    @Override
    public String toExternal(IndexableField f) {
        return toBase64String(toObject(f));
    }

    @Override
    public ByteBuffer toObject(IndexableField f) {
        BytesRef bytes = f.binaryValue();
        return  ByteBuffer.wrap(bytes.bytes, bytes.offset, bytes.length);
    }

    @Override
    public IndexableField createField(SchemaField field, Object val, float boost) {
        if (val == null) return null;
        if (!field.stored()) {
            return null;
        }
        byte[] buf = null;
        int offset = 0, len = 0;
        if (val instanceof byte[]) {
            buf = (byte[]) val;
            len = buf.length;
        } else if (val instanceof ByteBuffer && ((ByteBuffer)val).hasArray()) {
            ByteBuffer byteBuf = (ByteBuffer) val;
            buf = byteBuf.array();
            offset = byteBuf.position();
            len = byteBuf.limit() - byteBuf.position();
        } else {
            String strVal = val.toString();
            //the string has to be a base64 encoded string
            buf = Base64.base64ToByteArray(strVal);
            offset = 0;
            len = buf.length;
        }

        Field f = new org.apache.lucene.document.BinaryDocValuesField(field.getName(), new BytesRef(buf, offset, len));
//        Field f = new org.apache.lucene.document.StoredField(field.getName(), buf, offset, len);
        f.setBoost(boost);
        return f;
    }
}
