import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * Listing 10.10 CombinedChannelDuplexHandler<I,O>
 *
 * @author JavaEdge
 */

public class CombinedByteCharCodec extends
    CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CombinedByteCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
}
