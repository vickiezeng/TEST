package com.hp.snap.evaluation.imdb.business.cases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.couchbase.client.core.message.kv.MutationToken;
import com.couchbase.client.java.document.AbstractDocument;

public class ByteJsonDocument extends AbstractDocument<byte[]> implements Serializable {

	private static final long serialVersionUID = 375731014642624275L;

    /**
     * Creates a {@link ByteJsonDocument} which the document id.
     *
     * @param id the per-bucket unique document id.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id) {
        return new ByteJsonDocument(id, 0, null, 0, null);
    }

    /**
     * Creates a {@link ByteJsonDocument} which the document id and JSON content.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id, byte[] content) {
        return new ByteJsonDocument(id, 0, content, 0, null);
    }

    /**
     * Creates a {@link ByteJsonDocument} which the document id, JSON content and the CAS value.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @param cas the CAS (compare and swap) value for optimistic concurrency.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id, byte[] content, long cas) {
        return new ByteJsonDocument(id, 0, content, cas, null);
    }

    /**
     * Creates a {@link ByteJsonDocument} which the document id, JSON content and the expiration time.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @param expiry the expiration time of the document.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id, int expiry, byte[] content) {
        return new ByteJsonDocument(id, expiry, content, 0, null);
    }

    /**
     * Creates a {@link ByteJsonDocument} which the document id, JSON content, CAS value, expiration time and status code.
     *
     * This factory method is normally only called within the client library when a response is analyzed and a document
     * is returned which is enriched with the status code. It does not make sense to pre populate the status field from
     * the user level code.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @param cas the CAS (compare and swap) value for optimistic concurrency.
     * @param expiry the expiration time of the document.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id, int expiry, byte[] content, long cas) {
        return new ByteJsonDocument(id, expiry, content, cas, null);
    }

    /**
     * Creates a {@link ByteJsonDocument} which the document id, JSON content, CAS value, expiration time and status code.
     *
     * This factory method is normally only called within the client library when a response is analyzed and a document
     * is returned which is enriched with the status code. It does not make sense to pre populate the status field from
     * the user level code.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @param cas the CAS (compare and swap) value for optimistic concurrency.
     * @param expiry the expiration time of the document.
     * @return a {@link ByteJsonDocument}.
     */
    public static ByteJsonDocument create(String id, int expiry, byte[] content, long cas, MutationToken mutationToken) {
        return new ByteJsonDocument(id, expiry, content, cas, mutationToken);
    }

    /**
     * Creates a copy from a different {@link ByteJsonDocument}, but changes the document ID and content.
     *
     * @param doc the original {@link ByteJsonDocument} to copy.
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @return a copied {@link ByteJsonDocument} with the changed properties.
     */
    public static ByteJsonDocument from(ByteJsonDocument doc, String id, byte[] content) {
        return ByteJsonDocument.create(id, doc.expiry(), content, doc.cas(), doc.mutationToken());
    }

    /**
     * Creates a copy from a different {@link ByteJsonDocument}, but changes the CAS value.
     *
     * @param doc the original {@link ByteJsonDocument} to copy.
     * @param cas the CAS (compare and swap) value for optimistic concurrency.
     * @return a copied {@link ByteJsonDocument} with the changed properties.
     */
    public static ByteJsonDocument from(ByteJsonDocument doc, long cas) {
        return ByteJsonDocument.create(doc.id(), doc.expiry(), doc.content(), cas, doc.mutationToken());
    }

    /**
     * Private constructor which is called by the static factory methods eventually.
     *
     * @param id the per-bucket unique document id.
     * @param content the content of the document.
     * @param cas the CAS (compare and swap) value for optimistic concurrency.
     * @param expiry the expiration time of the document.
     */
    private ByteJsonDocument(String id, int expiry, byte[] content, long cas, MutationToken mutationToken) {
        super(id, expiry, content, cas, mutationToken);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        writeToSerializedStream(stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        readFromSerializedStream(stream);
    }
}
