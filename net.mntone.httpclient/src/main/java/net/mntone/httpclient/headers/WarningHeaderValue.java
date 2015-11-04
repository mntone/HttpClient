package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpParseResult;
import net.mntone.httpclient.HttpRuleParser;

import java.util.Date;

import javax.xml.ws.Holder;

public final class WarningHeaderValue implements Cloneable
{
	private final short _code;
	private final String _agent;
	private final String _text;
	private final Date _date;

	private WarningHeaderValue(final WarningHeaderValue source)
	{
		this._code = source._code;
		this._agent = source._agent;
		this._text = source._text;
		this._date = source._date;
	}

	public WarningHeaderValue(final short code, final String agent, final String text)
	{
		this(code, agent, text, null);
	}

	public WarningHeaderValue(final short code, final String agent, final String text, final Date date)
	{
		if (code < 0 || code > 999) throw new IllegalArgumentException();

		final Holder<Integer> index = new Holder<Integer>(0);
		final Holder<String> host = new Holder<String>();
		if (agent == null || agent.isEmpty() || HttpRuleParser.getHostLength(agent, index, true, host) != agent.length()) throw new IllegalArgumentException();

		HttpHeaderUtils.checkValidQuotedString(text);

		this._code = code;
		this._agent = agent;
		this._text = text;
		this._date = date;
	}

	@Override
	public WarningHeaderValue clone()
	{
		try
		{
			final WarningHeaderValue result = (WarningHeaderValue)super.clone();
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
		if (!(obj instanceof WarningHeaderValue)) return false;

		final WarningHeaderValue that = (WarningHeaderValue)obj;
		if (this._code != that._code) return false;
		if (this._agent == null || !this._agent.equalsIgnoreCase(that._agent)) return false;
		if (this._text == null || !this._text.equalsIgnoreCase(that._text)) return false;
		if (this._date == null) return true;
		return this._date.getTime() == that._date.getTime();
	}

	@Override
	public int hashCode()
	{
		int hashCode = this._code ^ this._agent.toLowerCase().hashCode() ^ this._text.hashCode();
		if (this._date != null) hashCode ^= this._date.getTime();
		return hashCode;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		this.appendString(builder);
		return builder.toString();
	}

	void appendString(final StringBuilder builder)
	{
		builder.append(String.format("%3d", this._code));
		builder.append(' ');
		builder.append(this._agent);
		builder.append(' ');
		builder.append(this._text);
		builder.append(' ');
		if (this._date != null)
		{
			builder.append(" \"");
			builder.append(HttpRuleParser.dateToString(this._date));
			builder.append('"');
		}
	}


	public static WarningHeaderValue parse(final String input)
	{
		return (WarningHeaderValue)HttpGenericHeaderParser.SingleValueWarningParser.parseValue(input);
	}

	public static boolean tryParse(final String input, final Holder<WarningHeaderValue> parsedValue)
	{
		final Holder<Object> obj = new Holder<Object>();
		if (!HttpGenericHeaderParser.SingleValueWarningParser.tryParseValue(input, obj)) return false;

		parsedValue.value = (WarningHeaderValue)obj.value;
		return true;
	}

	static int getWarningLength(final String input, final Holder<Integer> index, final Holder<WarningHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final Holder<Short> code = new Holder<Short>((short)0);
		if (!tryParseCode(input, index2, code)) return 0;

		final Holder<String> agent = new Holder<String>();
		if (!tryParseAgent(input, index2, agent)) return 0;

		final int startIndex3 = index2.value;
		final Holder<Integer> length2 = new Holder<Integer>(0);
		if (HttpRuleParser.getQuotedStringLength(input, startIndex3, length2) != HttpParseResult.Parsed) return 0;

		index2.value += length2.value;
		final String text = input.substring(startIndex3, index2.value);

		final Holder<Date> date = new Holder<Date>();
		if (!tryParseDate(input, index2, date)) return 0;

		final WarningHeaderValue warningHeaderValue = new WarningHeaderValue(code.value, agent.value, text, date.value);
		parsedValue.value = warningHeaderValue;

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}

	private static boolean tryParseCode(final String input, final Holder<Integer> index, final Holder<Short> code)
	{
		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int numberLength = HttpRuleParser.getNumberLength(input, index2, false);
		if (numberLength == 0 || numberLength >= 3) return false;
		if (!HttpHeaderUtils.tryParseInt16(input.substring(index.value, index2.value), code)) return false;

		final int whitespaceLength = HttpRuleParser.getWhitespaceLength(input, index2);
		if (whitespaceLength == 0 || index2.value != input.length()) return false;

		index.value = index2.value;
		return true;
	}

	private static boolean tryParseAgent(final String input, final Holder<Integer> index, final Holder<String> agent)
	{
		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int hostLength = HttpRuleParser.getHostLength(input, index2, true, agent);
		if (hostLength == 0) return false;

		final int whitespaceLength = HttpRuleParser.getWhitespaceLength(input, index2);
		if (whitespaceLength == 0 || index2.value != input.length()) return false;

		index.value = index2.value;
		return true;
	}

	private static boolean tryParseDate(final String input, final Holder<Integer> index, final Holder<Date> date)
	{
		final int length = input.length();

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		int whitespaceLength = HttpRuleParser.getWhitespaceLength(input, index2);
		if (index2.value < length && input.charAt(index2.value) == '"')
		{
			if (whitespaceLength == 0) return false;

			++index2.value;

			final int pos = index2.value;
			while (index2.value < length && input.charAt(index2.value) == '"') ++index2.value;
			if (index2.value == length || index2.value == pos) return false;

			if (!HttpRuleParser.tryStringToDate(input.substring(pos, index2.value), date)) return false;

			++index2.value;

			HttpRuleParser.getWhitespaceLength(input, index2);
		}

		index.value = index2.value;
		return true;
	}
}