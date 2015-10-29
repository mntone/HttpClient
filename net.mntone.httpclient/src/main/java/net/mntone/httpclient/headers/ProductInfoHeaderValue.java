package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpParseResult;
import net.mntone.httpclient.HttpRuleParser;

import javax.xml.ws.Holder;

public final class ProductInfoHeaderValue implements Cloneable
{
	private final ProductHeaderValue _product;
	private final String _comment;

	private ProductInfoHeaderValue(final ProductInfoHeaderValue source)
	{
		this._product = source._product;
		this._comment = source._comment;
	}

	public ProductInfoHeaderValue(final ProductHeaderValue product)
	{
		if (product == null) throw new IllegalArgumentException();

		this._product = product;
		this._comment = null;
	}

	public ProductInfoHeaderValue(final String name, final String version)
	{
		this(new ProductHeaderValue(name, version));
	}

	public ProductInfoHeaderValue(final String comment)
	{
		HttpHeaderUtils.checkValidComment(comment);

		this._product = null;
		this._comment = comment;
	}

	@Override
	public ProductInfoHeaderValue clone()
	{
		try
		{
			final ProductInfoHeaderValue result = (ProductInfoHeaderValue)super.clone();
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
		if (!(obj instanceof ProductInfoHeaderValue)) return false;

		final ProductInfoHeaderValue that = (ProductInfoHeaderValue)obj;
		if (this._product == null)
		{
			if (that._product == null)
			{
				return this._comment != null && this._comment.equals(that._comment);
			}
			return false;
		}
		return this._product.equals(that._product);
	}

	@Override
	public int hashCode()
	{
		if (this._product == null) return this._comment.hashCode();
		return this._product.hashCode();
	}

	@Override
	public String toString()
	{
		if (this._product == null) return this._comment;
		return this._product.toString();
	}

	public static ProductHeaderValue parse(final String input)
	{
		return (ProductHeaderValue)ProductInfoHeaderParser.SingleValueParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<ProductHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!ProductInfoHeaderParser.SingleValueParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (ProductHeaderValue)obj.value;
		return true;
	}

	static int getProductInfoLength(final String input, final Holder<Integer> index, final Holder<ProductInfoHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		if (input.charAt(index2.value) == '(')
		{
			final Holder<Integer> length2 = new Holder<Integer>(0);
			if (HttpRuleParser.getCommentLength(input, index2.value, length2) != HttpParseResult.Parsed) return 0;

			index2.value += length2.value;

			final String comment = input.substring(index.value, index2.value);

			HttpRuleParser.getWhitespaceLength(input, index2);

			final ProductInfoHeaderValue productInfoHeaderValue = new ProductInfoHeaderValue(comment);
			parsedValue.value = productInfoHeaderValue;

			final int a = index2.value - index.value;
			index.value = index2.value;
			return a;
		}

		final Holder<ProductHeaderValue> productHeaderValue = new Holder<ProductHeaderValue>();
		final int productLength = ProductHeaderValue.getProductLength(input, index2, productHeaderValue);
		if (productLength == 0) return 0;

		final ProductInfoHeaderValue productInfoHeaderValue = new ProductInfoHeaderValue(productHeaderValue.value);
		parsedValue.value = productInfoHeaderValue;

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}
}