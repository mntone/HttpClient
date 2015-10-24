package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

final class MediaTypeHeaderParser extends BaseHeaderParser
{
	public static final MediaTypeHeaderParser SingleValueParser = new MediaTypeHeaderParser(false);

	private MediaTypeHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		final Holder<MediaTypeHeaderValueBase> mediaTypeHeaderValue = new Holder<MediaTypeHeaderValueBase>();
		final int mediaTypeLength = MediaTypeHeaderValueBase.getMediaTypeLength(value, startIndex, mediaTypeHeaderValue, MediaTypeHeaderValueBase.class);
		parsedValue.value = mediaTypeHeaderValue.value;
		return mediaTypeLength;
	}
}