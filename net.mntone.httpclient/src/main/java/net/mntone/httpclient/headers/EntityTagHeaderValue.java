package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpParseResult;
import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

public class EntityTagHeaderValue implements Cloneable
{
	public static EntityTagHeaderValue getAny()
	{
		if (ANY == null)
		{
			ANY = new EntityTagHeaderValue();
			ANY._tag = "*";
			ANY._weak = false;
		}
		return ANY;
	}
	private static EntityTagHeaderValue ANY;

	private String _tag;
	private boolean _weak;

	private EntityTagHeaderValue()
	{ }

	private EntityTagHeaderValue(final EntityTagHeaderValue source)
	{
		this._tag = source._tag;
		this._weak = source._weak;
	}

	public EntityTagHeaderValue(final String tag)
	{
		this(tag, false);
	}

	public EntityTagHeaderValue(final String tag, final boolean weak)
	{
		if (tag == null || tag.isEmpty()) throw new IllegalArgumentException();

		this._tag = tag;
		this._weak = weak;
	}

	@Override
	public EntityTagHeaderValue clone()
	{
		try
		{
			final EntityTagHeaderValue result = (EntityTagHeaderValue)super.clone();
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
		if (!(obj instanceof EntityTagHeaderValue)) return false;

		final EntityTagHeaderValue that = (EntityTagHeaderValue)obj;
		if (this._weak != that._weak) return false;
		return this._tag != null && this._tag.equals(that._tag);
	}

	@Override
	public int hashCode()
	{
		return this._tag.hashCode() ^ (this._weak ? 1 : 0);
	}

	@Override
	public String toString()
	{
		if (this._weak)
		{
			return "W/" + this._tag;
		}
		return this._tag;
	}

	public String getTag()
	{
		return this._tag;
	}

	public boolean isWeak()
	{
		return this._weak;
	}

	public static EntityTagHeaderValue parse(final String input)
	{
		return (EntityTagHeaderValue)HttpGenericHeaderParser.SingleValueEntityTagParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<EntityTagHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!HttpGenericHeaderParser.SingleValueEntityTagParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (EntityTagHeaderValue)obj.value;
		return true;
	}

	static int getEntityTagLength(final String input, final int startIndex, final Holder<EntityTagHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;

		int i = startIndex;
		boolean flag = false;
		final char c = input.charAt(i);
		if (c == '*')
		{
			parsedValue.value = ANY;
			++i;
		}
		else
		{
			if (c == 'W' || c == 'w')
			{
				++i;
				if (i + 2 >= length || input.charAt(i) != '/') return 0;
				flag = true;
				++i;
				i += HttpRuleParser.getWhitespaceLength(input, i);
			}

			final int startIndex2 = i;
			final Holder<Integer> i2 = new Holder<Integer>(0);
			if (HttpRuleParser.getQuotedStringLength(input, i, i2) != HttpParseResult.Parsed) return 0;

			parsedValue.value = new EntityTagHeaderValue();
			if (i2.value == length)
			{
				parsedValue.value._tag = input;
				parsedValue.value._weak = false;
			}
			else
			{
				parsedValue.value._tag = input.substring(startIndex2, i2.value);
				parsedValue.value._weak = flag;
			}
			i += i2.value;
		}
		i += HttpRuleParser.getWhitespaceLength(input, i);
		return i - startIndex;
	}
}