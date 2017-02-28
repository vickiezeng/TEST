package com.hp.snap.evaluation.imdb.business.cases;

import com.couchbase.client.core.lang.Tuple;
import com.couchbase.client.core.lang.Tuple2;
import com.couchbase.client.core.message.ResponseStatus;
import com.couchbase.client.core.message.kv.MutationToken;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.java.error.TranscodingException;
import com.couchbase.client.java.transcoder.AbstractTranscoder;
import com.couchbase.client.java.transcoder.TranscoderUtils;

public class ByteJsonTranscoder extends AbstractTranscoder<ByteJsonDocument, byte[]> {
	@Override
    protected Tuple2<ByteBuf, Integer> doEncode(ByteJsonDocument document) throws Exception {
        return Tuple.create(
            Unpooled.wrappedBuffer(document.content()),
            TranscoderUtils.JSON_COMPAT_FLAGS
        );
    }

    @Override
    protected ByteJsonDocument doDecode(String id, ByteBuf content, long cas, int expiry, int flags,
        ResponseStatus status) throws Exception {
        if (!TranscoderUtils.hasJsonFlags(flags)) {
            throw new TranscodingException("Flags (0x" + Integer.toHexString(flags) + ") indicate non-JSON document for "
                + "id " + id + ", could not decode.");
        }

        byte[] converted = new byte[content.readableBytes()];
        content.readBytes(converted);
        return newDocument(id, expiry, converted, cas);
    }

  
    public ByteJsonDocument newDocument(String id, int expiry, byte[] content, long cas) {
        return ByteJsonDocument.create(id, expiry, content, cas);
    }

    @Override
    public ByteJsonDocument newDocument(String id, int expiry, byte[] content, long cas,
        MutationToken mutationToken) {
        return ByteJsonDocument.create(id, expiry, content, cas, mutationToken);
    }


    public Class<ByteJsonDocument> documentType() {
        return ByteJsonDocument.class;
    }
}
