package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

final class HttpGenericHeaderParser extends BaseHeaderParser
{
	private interface ParsedValueLengthGetter
	{
		int apply(final String value, final int startIndex, final Holder<Object> parsedValue);
	}

	public static final HttpHeaderParser HostParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final Holder<Integer> index = new Holder<Integer>(startIndex);
			final Holder<String> host = new Holder<String>();
			final int hostLength = HttpRuleParser.getHostLength(value, index, false, host);
			parsedValue.value = host.value;
			return hostLength;
		}
	}, HttpHeaderUtils.getIgnoreCaseStringComparer());

	public static final HttpHeaderParser TokenListParser = new HttpGenericHeaderParser(true, new ParsedValueLengthGetter()
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final int tokenLength = HttpRuleParser.getTokenLength(value, startIndex);
			parsedValue.value = value.substring(startIndex, tokenLength);
			return tokenLength;
		}
	}, HttpHeaderUtils.getIgnoreCaseStringComparer());

	public static final HttpHeaderParser SingleValueEntityTagParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final Holder<Integer> index = new Holder<Integer>(startIndex);
			final Holder<EntityTagHeaderValue> entityTagHeaderValue = new Holder<EntityTagHeaderValue>();
			final int entityTagLength = EntityTagHeaderValue.getEntityTagLength(value, index, entityTagHeaderValue);
			if (entityTagHeaderValue.value == EntityTagHeaderValue.getAny()) return 0;
			parsedValue.value = entityTagHeaderValue.value;
			return entityTagLength;
		}
	});

	public static final HttpHeaderParser MultipleValueEntityTagParser = new HttpGenericHeaderParser(true, new ParsedValueLengthGetter()
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final Holder<Integer> index = new Holder<Integer>(startIndex);
			final Holder<EntityTagHeaderValue> entityTagHeaderValue = new Holder<EntityTagHeaderValue>();
			final int entityTagLength = EntityTagHeaderValue.getEntityTagLength(value, index, entityTagHeaderValue);
			parsedValue.value = entityTagHeaderValue.value;
			return entityTagLength;
		}
	});

	private static class NameValueParser implements ParsedValueLengthGetter
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final Holder<Integer> index = new Holder<Integer>(startIndex);
			final Holder<NameValueHeaderValue> nameValueHeaderValue = new Holder<NameValueHeaderValue>();
			final int nameValueLength = NameValueHeaderValue.getNameValueLength(value, index, nameValueHeaderValue, NameValueHeaderValue.class);
			parsedValue.value = nameValueHeaderValue.value;
			return nameValueLength;
		}
	}

	public static final HttpHeaderParser SingleValueNameValueParser = new HttpGenericHeaderParser(false, new NameValueParser());
	public static final HttpHeaderParser MultipleValueNameValueParser = new HttpGenericHeaderParser(true, new NameValueParser());

	public static final HttpHeaderParser ContentDispositionParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
	{
		@Override
		public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
		{
			final Holder<Integer> index = new Holder<Integer>(startIndex);
			final Holder<ContentDispositionHeaderValue> contentDispositionHeaderValue = new Holder<ContentDispositionHeaderValue>();
			final int dispositionTypeLength = ContentDispositionHeaderValue.getDispositionTypeLength(value, index, contentDispositionHeaderValue);
			parsedValue.value = contentDispositionHeaderValue.value;
			return dispositionTypeLength;
		}
	});


	private final ParsedValueLengthGetter _lengthGetter;
	private final EqualityComparer _comparer;

	public HttpGenericHeaderParser(final boolean supportsMultipleValues, ParsedValueLengthGetter lengthGetter)
	{
		this(supportsMultipleValues, lengthGetter, null);
	}

	public HttpGenericHeaderParser(final boolean supportsMultipleValues, ParsedValueLengthGetter lengthGetter, EqualityComparer comparer)
	{
		super(supportsMultipleValues);
		this._lengthGetter = lengthGetter;
		this._comparer = comparer;
	}

	@Override
	protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
	{
		return this._lengthGetter.apply(value, startIndex, parsedValue);
	}

	@Override
	public EqualityComparer getComparer()
	{
		return this._comparer;
	}
}