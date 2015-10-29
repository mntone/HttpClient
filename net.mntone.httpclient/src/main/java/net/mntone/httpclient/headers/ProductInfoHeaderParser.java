package net.mntone.httpclient.headers;

import javax.xml.ws.Holder;

final class ProductInfoHeaderParser extends HttpHeaderParser
{
	public static final ProductInfoHeaderParser SingleValueParser = new ProductInfoHeaderParser(false);
	public static final ProductInfoHeaderParser MultipleValueParser = new ProductInfoHeaderParser(true);

	public ProductInfoHeaderParser(final boolean supportsMultipleValues)
	{
		super(supportsMultipleValues);
	}

	@Override
	public boolean tryParseValue(final String input, final Object storeValue, final Holder<Integer> index, final Holder<Object> parsedValue)
	{
		if (input == null || input.isEmpty()) return false;

		final int length = input.length();
		if (index.value == length) return false;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final Holder<ProductInfoHeaderValue> productInfoHeaderValue = new Holder<ProductInfoHeaderValue>();
		final int productInfoLength = ProductInfoHeaderValue.getProductInfoLength(input, index2, productInfoHeaderValue);
		if (productInfoLength == 0) return false;

		if (index2.value < length)
		{
			final char c = input.charAt(index2.value - 1);
			if (c != ' ' && c != '\t') return false;
		}

		index.value = index2.value;
		parsedValue.value = productInfoHeaderValue.value;
		return true;
	}
}