package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

final class TransferEncodingHeaderParser extends BaseHeaderParser
{
	public static final TransferEncodingHeaderParser SingleValueParser = new TransferEncodingHeaderParser(false);
	public static final TransferEncodingHeaderParser MultipleValuesParser = new TransferEncodingHeaderParser(true);

	private TransferEncodingHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		final Holder<Integer> index = new Holder<Integer>(startIndex);
		final Holder<TransferEncodingHeaderValue> transferEncodingHeaderValue = new Holder<TransferEncodingHeaderValue>();
		final int transferEncodingLength = TransferEncodingHeaderValue.getTransferEncodingLength(value, index, transferEncodingHeaderValue, TransferEncodingHeaderValue.class);
		parsedValue.value = transferEncodingHeaderValue.value;
		return transferEncodingLength;
	}
}