package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

public abstract class MediaTypeHeaderValueBase extends HttpParameterValueBase
{
	private static final String CHARSET = "charset";

	private String _mediaType;

	MediaTypeHeaderValueBase()
	{ }

	protected MediaTypeHeaderValueBase(final MediaTypeHeaderValueBase source)
	{
		super(source);
		this._mediaType = source._mediaType;
	}

	public MediaTypeHeaderValueBase(final String mediaType)
	{
		if (mediaType == null || mediaType.isEmpty()) throw new IllegalArgumentException();

		this._mediaType = mediaType;
	}

	@Override
	public MediaTypeHeaderValueBase clone()
	{
		return (MediaTypeHeaderValueBase)super.clone();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof MediaTypeHeaderValueBase)) return false;

		final MediaTypeHeaderValueBase that = (MediaTypeHeaderValueBase)obj;
		return this._mediaType != null && this._mediaType.equalsIgnoreCase(that._mediaType) && super.equalsCollection(that.getParameters());
	}

	@Override
	public int hashCode()
	{
		return this._mediaType.toLowerCase().hashCode() ^ super.hashCode();
	}

	@Override
	public String toString()
	{
		return this._mediaType + super.toString();
	}

	public final String getMediaType()
	{
		return this._mediaType;
	}

	public final String getCharset()
	{
		return super.getString(CHARSET);
	}
	public final void setCharset(final String value)
	{
		super.setString(CHARSET, value);
	}

	static <T extends MediaTypeHeaderValueBase> int getMediaTypeLength(final String input, final Holder<Integer> index, final Holder<T> parsedValue, final Class<T> targetType)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final Holder<String> mediaType = new Holder<String>();
		final int mediaTypeExpressionLength = getMediaTypeExpressionLength(input, index2, mediaType);
		if (mediaTypeExpressionLength == 0) return 0;

		HttpRuleParser.getWhitespaceLength(input, index2);

		if (index2.value >= length || input.charAt(index2.value) != ';')
		{
			try
			{
				final MediaTypeHeaderValueBase mediaTypeHeaderValue = targetType.newInstance();
				mediaTypeHeaderValue._mediaType = mediaType.value;
				parsedValue.value = (T)mediaTypeHeaderValue;

				final int a = index2.value - index.value;
				index.value = index2.value;
				return a;
			}
			catch (final InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (final IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return 0;
		}
		++index2.value;

		try
		{
			final MediaTypeHeaderValueBase mediaTypeHeaderValue = targetType.newInstance();
			mediaTypeHeaderValue._mediaType = mediaType.value;

			final int nameValueListLength = NameValueHeaderValue.getNameValueCollectionLength(input, index2, ';', mediaTypeHeaderValue.getParameters(), NameValueHeaderValue.class);
			if (nameValueListLength == 0) return 0;
			parsedValue.value = (T)mediaTypeHeaderValue;

			final int a = index2.value - index.value;
			index.value = index2.value;
			return a;
		}
		catch (final InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	private static int getMediaTypeExpressionLength(final String input, final Holder<Integer> index, final Holder<String> mediaType)
	{
		final Holder<Integer> index2 = new Holder<Integer>(index.value);

		final int tokenLength = HttpRuleParser.getTokenLength(input, index2);
		if (tokenLength == 0) return 0;

		HttpRuleParser.getWhitespaceLength(input, index2);

		if (index2.value >= input.length() || input.charAt(index2.value) != '/') return 0;
		++index2.value;

		HttpRuleParser.getWhitespaceLength(input, index2);

		final int tokenLength2 = HttpRuleParser.getTokenLength(input, index2);
		if (tokenLength2 == 0) return 0;

		if (index.value + tokenLength + 1 + tokenLength2 == index2.value)
		{
			mediaType.value = input.substring(index.value, index2.value);
		}
		else
		{
			final int startIndex2 = index.value + tokenLength + 1;
			mediaType.value = input.substring(index.value, index.value + tokenLength) + '/' + input.substring(startIndex2 + tokenLength2);
		}

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}
}