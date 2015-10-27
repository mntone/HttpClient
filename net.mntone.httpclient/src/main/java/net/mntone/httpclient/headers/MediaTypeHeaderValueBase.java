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

	static <T extends MediaTypeHeaderValueBase> int getMediaTypeLength(final String input, final int startIndex, final Holder<T> parsedValue, Class<T> targetType)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;

		final Holder<String> mediaType = new Holder<String>();
		final int mediaTypeExpressionLength = getMediaTypeExpressionLength(input, startIndex, mediaType);
		if (mediaTypeExpressionLength == 0) return 0;

		int i = startIndex + mediaTypeExpressionLength;
		i += HttpRuleParser.getWhitespaceLength(input, i);

		if (i >= length || input.charAt(i) != ';')
		{
			try
			{
				final MediaTypeHeaderValueBase mediaTypeHeaderValue = targetType.newInstance();
				mediaTypeHeaderValue._mediaType = mediaType.value;
				parsedValue.value = (T)mediaTypeHeaderValue;
				return i - startIndex;
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
				return 0;
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				return 0;
			}
		}
		++i;

		try
		{
			final MediaTypeHeaderValueBase mediaTypeHeaderValue = targetType.newInstance();
			mediaTypeHeaderValue._mediaType = mediaType.value;

			final int nameValueListLength = NameValueHeaderValue.getNameValueCollectionLength(input, i, ';', mediaTypeHeaderValue.getParameters(), NameValueHeaderValue.class);
			if (nameValueListLength == 0) return 0;

			i += nameValueListLength;

			parsedValue.value = (T)mediaTypeHeaderValue;
			return i - startIndex;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return 0;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	private static int getMediaTypeExpressionLength(final String input, final int startIndex, final Holder<String> mediaType)
	{
		final int tokenLength = HttpRuleParser.getTokenLength(input, startIndex);
		if (tokenLength == 0) return 0;

		int i = startIndex + tokenLength;
		i += HttpRuleParser.getWhitespaceLength(input, i);

		final int tokenLength2 = HttpRuleParser.getTokenLength(input, i);
		if (tokenLength2 == 0) return 0;

		final int i2 = i + tokenLength2 - startIndex;
		if (tokenLength + tokenLength2 + 1 == i2)
		{
			mediaType.value = input.substring(startIndex, i2);
		}
		else
		{
			mediaType.value = input.substring(startIndex, tokenLength) + "/" + input.substring(i2, tokenLength2);
		}
		return i2;
	}
}