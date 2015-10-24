package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.ws.Holder;

public abstract class MediaTypeHeaderValueBase implements Cloneable
{
	private static final String CHARSET = "charset";

	private String _mediaType;
	ArrayList<NameValueHeaderValue> _parameters;

	MediaTypeHeaderValueBase()
	{ }

	protected MediaTypeHeaderValueBase(final MediaTypeHeaderValueBase source)
	{
		this._mediaType = source._mediaType;
		if (this._parameters != null)
		{
			this._parameters = new ArrayList<NameValueHeaderValue>();
			for (final NameValueHeaderValue item : this._parameters)
			{
				this._parameters.add(item);
			}
		}
	}

	public MediaTypeHeaderValueBase(final String mediaType)
	{
		if (mediaType == null || mediaType.isEmpty()) throw new IllegalArgumentException();

		this._mediaType = mediaType;
	}

	@Override
	public MediaTypeHeaderValueBase clone()
	{
		try
		{
			final MediaTypeHeaderValueBase result = (MediaTypeHeaderValueBase)super.clone();
			return result;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof MediaTypeHeaderValueBase)) return false;

		final MediaTypeHeaderValueBase that = (MediaTypeHeaderValueBase)obj;
		return this._mediaType != null && this._mediaType.equalsIgnoreCase(that._mediaType) && NameValueHeaderValue.areEquals(this._parameters, that._parameters);
	}

	@Override
	public int hashCode()
	{
		return this._mediaType.toLowerCase().hashCode() ^ NameValueHeaderValue.hashCode(this._parameters);
	}

	@Override
	public String toString()
	{
		return this._mediaType;
	}

	public final String getMediaType()
	{
		return this._mediaType;
	}

	public final Collection<NameValueHeaderValue> getParameters()
	{
		if (this._parameters == null)
		{
			this._parameters = new ArrayList<NameValueHeaderValue>();
		}
		return this._parameters;
	}

	public final String getCharset()
	{
		final NameValueHeaderValue nameValueHeaderValue = NameValueHeaderValue.find(this._parameters, CHARSET);
		if (nameValueHeaderValue == null) return null;
		return nameValueHeaderValue.getValue();
	}

	public final void setCharset(final String value)
	{
		final NameValueHeaderValue nameValueHeaderValue = NameValueHeaderValue.find(this._parameters, CHARSET);
		if (value == null || value.isEmpty())
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value);
			else this.getParameters().add(new NameValueHeaderValue(CHARSET, value));
		}
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