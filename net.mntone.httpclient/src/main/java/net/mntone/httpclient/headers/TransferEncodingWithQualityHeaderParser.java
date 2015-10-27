package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

final class TransferEncodingWithQualityHeaderParser extends BaseHeaderParser
{
	public static final TransferEncodingWithQualityHeaderParser SingleValueParser = new TransferEncodingWithQualityHeaderParser(false);
	public static final TransferEncodingWithQualityHeaderParser MultipleValuesParser = new TransferEncodingWithQualityHeaderParser(true);

	private TransferEncodingWithQualityHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		final Holder<Integer> index = new Holder<Integer>(startIndex);
		final Holder<TransferEncodingWithQualityHeaderValue> transferEncodingWithQualityHeaderValue = new Holder<TransferEncodingWithQualityHeaderValue>();
		final int transferEncodingLength = TransferEncodingWithQualityHeaderValue.getTransferEncodingLength(value,
		                                                                                                    index,
		                                                                                                    transferEncodingWithQualityHeaderValue,
		                                                                                                    TransferEncodingWithQualityHeaderValue.class);
		parsedValue.value = transferEncodingWithQualityHeaderValue.value;
		return transferEncodingLength;
	}
}