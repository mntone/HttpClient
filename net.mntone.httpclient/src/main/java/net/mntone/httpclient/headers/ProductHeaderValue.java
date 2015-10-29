package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

public final class ProductHeaderValue implements Cloneable
{
	private final String _name;
	private final String _version;

	private ProductHeaderValue(final ProductHeaderValue source)
	{
		this._name = source._name;
		this._version = source._version;
	}

	public ProductHeaderValue(final String name)
	{
		this(name, null);
	}

	public ProductHeaderValue(final String name, final String version)
	{
		HttpHeaderUtils.checkValidToken(name);
		if (version != null && !version.isEmpty())
		{
			HttpHeaderUtils.checkValidToken(version);
			this._version = version;
		}
		else
		{
			this._version = null;
		}
		this._name = name;
	}

	@Override
	public ProductHeaderValue clone()
	{
		try
		{
			final ProductHeaderValue result = (ProductHeaderValue)super.clone();
			return result;
		}
		catch (final CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof ProductHeaderValue)) return false;

		final ProductHeaderValue that = (ProductHeaderValue)obj;
		return this._name.equalsIgnoreCase(that._name) && this._version != null && this._version.equalsIgnoreCase(that._version);
	}

	@Override
	public int hashCode()
	{
		int hashCode = this._name.toLowerCase().hashCode();
		if (this._version != null && !this._version.isEmpty())
		{
			hashCode ^= this._version.toLowerCase().hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString()
	{
		if (this._version != null && !this._version.isEmpty())
		{
			return this._name;
		}
		return this._name + '/' + this._version;
	}

	public static ProductHeaderValue parse(final String input)
	{
		return (ProductHeaderValue)HttpGenericHeaderParser.SingleValueProductParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<ProductHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!HttpGenericHeaderParser.SingleValueProductParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (ProductHeaderValue)obj.value;
		return true;
	}

	static int getProductLength(final String input, final Holder<Integer> index, final Holder<ProductHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int nameLength = HttpRuleParser.getTokenLength(input, index2);
		if (nameLength == 0) return 0;

		final String name = input.substring(index.value, index2.value);

		HttpRuleParser.getWhitespaceLength(input, index2);

		if (index2.value == length || input.charAt(index2.value) != '/')
		{
			final ProductHeaderValue productInfoHeaderValue = new ProductHeaderValue(name);
			parsedValue.value = productInfoHeaderValue;

			final int a = index2.value - index.value;
			index.value = index2.value;
			return a;
		}
		++index2.value;

		HttpRuleParser.getWhitespaceLength(input, index2);

		final int versionStartIndex = index2.value;
		final int versionLength = HttpRuleParser.getTokenLength(input, index2);
		if (versionLength == 0) return 0;

		final String version = input.substring(versionStartIndex, index2.value);

		HttpRuleParser.getWhitespaceLength(input, index2);

		final ProductHeaderValue productInfoHeaderValue = new ProductHeaderValue(name, version);
		parsedValue.value = productInfoHeaderValue;

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}
}