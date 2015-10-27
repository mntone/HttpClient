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

	static int getEntityTagLength(final String input, final Holder<Integer> index, final Holder<EntityTagHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		boolean flag = false;
		final char c = input.charAt(index2.value);
		if (c == '*')
		{
			parsedValue.value = ANY;
			++index2.value;
		}
		else
		{
			if (c == 'W' || c == 'w')
			{
				++index2.value;
				if (index2.value + 2 >= length || input.charAt(index2.value) != '/') return 0;
				flag = true;
				++index2.value;
				HttpRuleParser.getWhitespaceLength(input, index2);
			}

			final int startIndex2 = index2.value;
			final Holder<Integer> length2 = new Holder<Integer>(0);
			if (HttpRuleParser.getQuotedStringLength(input, index2.value, length2) != HttpParseResult.Parsed) return 0;

			parsedValue.value = new EntityTagHeaderValue();
			if (length2.value == length)
			{
				parsedValue.value._tag = input;
				parsedValue.value._weak = false;
			}
			else
			{
				parsedValue.value._tag = input.substring(startIndex2, length2.value);
				parsedValue.value._weak = flag;
			}
			index2.value += length2.value;
		}

		HttpRuleParser.getWhitespaceLength(input, index2);

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}
}