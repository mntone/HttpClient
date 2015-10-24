package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

final class MediaTypeWithQualityHeaderParser extends BaseHeaderParser
{
	public static final MediaTypeWithQualityHeaderParser SingleValueParser = new MediaTypeWithQualityHeaderParser(false);
	public static final MediaTypeWithQualityHeaderParser MultipleValuesParser = new MediaTypeWithQualityHeaderParser(true);

	private MediaTypeWithQualityHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		final Holder<MediaTypeWithQualityHeaderValue> mediaTypeHeaderValue = new Holder<MediaTypeWithQualityHeaderValue>();
		final int mediaTypeLength = MediaTypeHeaderValueBase.getMediaTypeLength(value, startIndex, mediaTypeHeaderValue, MediaTypeWithQualityHeaderValue.class);
		parsedValue.value = mediaTypeHeaderValue.value;
		return mediaTypeLength;
	}
}