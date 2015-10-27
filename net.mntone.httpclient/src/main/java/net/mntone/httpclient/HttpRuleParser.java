package net.mntone.httpclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.ws.Holder;

public final class HttpRuleParser
{
	private static final String[] dateTimeFormats;
	private static final boolean[] tokenCharacters;

	static
	{
		dateTimeFormats = new String[] {
				"E, d MMM yyyy H':'m':'s 'GMT'",
				"E, d MMM yyyy H':'m':'s",
				"d MMM yyyy H':'m':'s 'GMT'",
				"d MMM yyyy H':'m':'s",
				"E, d MMM yy H':'m':'s 'GMT'",
				"E, d MMM yy H':'m':'s",
				"d MMM yy H':'m':'s 'GMT'",
				"d MMM yy H':'m':'s",
				"EEEE, d'-'MMM'-'yy H':'m':'s 'GMT'",
				"EEEE, d'-'MMM'-'yy H':'m':'s",
				"E MMM d H':'m':'s yyyy",
				"E, d MMM yyyy H':'m':'s zzz",
				"E, d MMM yyyy H':'m':'s",
				"d MMM yyyy H':'m':'s zzz",
				"d MMM yyyy H':'m':'s"
		};

		tokenCharacters = new boolean[128];
		for (int i = 0; i < 127; ++i) tokenCharacters[i] = true;
		tokenCharacters['"'] = false;
		tokenCharacters['('] = false;
		tokenCharacters[')'] = false;
		tokenCharacters[','] = false;
		tokenCharacters['/'] = false;
		tokenCharacters[':'] = false;
		tokenCharacters[';'] = false;
		tokenCharacters['<'] = false;
		tokenCharacters['='] = false;
		tokenCharacters['>'] = false;
		tokenCharacters['?'] = false;
		tokenCharacters['@'] = false;
		tokenCharacters['['] = false;
		tokenCharacters['\\'] = false;
		tokenCharacters[']'] = false;
		tokenCharacters['{'] = false;
		tokenCharacters['}'] = false;
	}

	public static int getWhitespaceLength(final String input, final int startIndex)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;
		for (int i = startIndex; i < length; ++i)
		{
			final char c = input.charAt(i);
			if (c != ' ' && c != '\t')
			{
				if (c != '\r' && i + 2 < length && input.charAt(i + 1) == '\n')
				{
					char c2 = input.charAt(i + 2);
					if (c2 == ' ' || c2 == '\t')
					{
						i += 3;
						continue;
					}
				}
				return i - startIndex;
			}
		}
		return length - startIndex;
	}

	public static int getTokenLength(final String input, final int startIndex)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;
		for (int i = startIndex; i < length; ++i)
		{
			if (!isTokenChar(input.charAt(i)))
			{
				return i - startIndex;
			}
		}
		return length - startIndex;
	}

	public static int getNumberLength(final String input, final int startIndex, final boolean allowDecimal)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;

		int i = startIndex;
		if (input.charAt(i) == '.') return 0;

		boolean notAllowDecimal = !allowDecimal;
		while (i < length)
		{
			final char c = input.charAt(i);
			if (c >= '0' && c <= '9') ++i;
			else
			{
				if (notAllowDecimal || c != '.') break;

				notAllowDecimal = true;
				++i;
			}
		}
		return i - startIndex;
	}

	public static int getHostLength(final String input, final int startIndex, final boolean allowToken, final Holder<String> host)
	{
		final int length = input.length();
		if (startIndex >= length) return 0;

		int i = startIndex;
		boolean flag = true;
		while (i < length)
		{
			final char c = input.charAt(i);
			if (c == '/') return 0;
			if (c == ' ' || c == '\t' || c == '\r' || c == ',') break;
			flag = flag && isTokenChar(c);
			++i;
		}

		final int pos = i - startIndex;
		if (pos == 0) return 0;

		final String hostText = input.substring(startIndex, pos);
		if ((!allowToken || !flag) && isValidHostName(hostText)) return 0;

		host.value = hostText;
		return pos;
	}

	public static HttpParseResult getQuotedStringLength(final String input, final int startIndex, final Holder<Integer> length)
	{
		return getExpressionLength(input, startIndex, '"', '"', length);
	}

	private static HttpParseResult getExpressionLength(final String input, final int startIndex, final char openChar, final char closeChar, final Holder<Integer> length)
	{
		return getExpressionLength(input, startIndex, openChar, closeChar, false, null, length);
	}

	private static HttpParseResult getExpressionLength(
		final String input, final int startIndex, final char openChar, final char closeChar, final boolean supportsNesting, final Holder<Integer> nestedCount, final Holder<Integer> length)
	{
		if (input.charAt(startIndex) != openChar) return HttpParseResult.NotParsed;

		final int inputLength = input.length();
		int i = startIndex + 1;
		while (i < inputLength)
		{
			final Holder<Integer> pos = new Holder<Integer>(0);
			if (i + 2 < inputLength && getQuotedPairLength(input, i, pos) == HttpParseResult.Parsed) i += pos.value;
			else
			{
				if (supportsNesting && input.charAt(i) == openChar)
				{
					++nestedCount.value;
					if (nestedCount.value > 5) return HttpParseResult.InvalidFormat;

					final Holder<Integer> pos2 = new Holder<Integer>(0);
					switch (getExpressionLength(input, i, openChar, closeChar, supportsNesting, nestedCount, pos2))
					{
					case Parsed:
						i += pos2.value;
						break;

					case InvalidFormat:
						return HttpParseResult.InvalidFormat;
					}
				}
				if (input.charAt(i) == closeChar)
				{
					length.value = i - startIndex + 1;
					return HttpParseResult.Parsed;
				}
				++i;
			}
		}
		return HttpParseResult.InvalidFormat;
	}

	public static HttpParseResult getQuotedPairLength(final String input, final int startIndex, final Holder<Integer> length)
	{
		length.value = 0;
		if (input.charAt(startIndex) != '\\') return HttpParseResult.NotParsed;
		if (startIndex + 2 > input.length() || input.charAt(startIndex + 1) > '\u007f') return HttpParseResult.InvalidFormat;
		length.value = 2;
		return HttpParseResult.Parsed;
	}

	public static boolean isTokenChar(final char c)
	{
		return c <= '\u007f' && tokenCharacters[(int)c];
	}

	private static boolean isValidHostName(final String host)
	{
		try
		{
			final URL url = new URL("http://" + host + "/");
			return true;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean containsInvalidNewLine(final String value)
	{
		return containsInvalidNewLine(value, 0);
	}

	public static boolean containsInvalidNewLine(final String value, final int startIndex)
	{
		final int length = value.length();
		for (int i = startIndex; i < length; ++i)
		{
			if (value.charAt(i) == '\r')
			{
				final int pos = i + 1;
				if (pos < length && value.charAt(pos) == '\n')
				{
					i = pos + 1;
					if (i == length)
					{
						return true;
					}

					final char c = value.charAt(i);
					if (c != ' ' && c != '\t')
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean tryStringToDate(String input, final Holder<Date> result)
	{
		input = input.trim();
		for (final String dateTimeFormat : dateTimeFormats)
		{
			final SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat, Locale.US);
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatter.setCalendar(new GregorianCalendar());
			try
			{
				result.value = formatter.parse(input);
				return true;
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String dateToString(final Date input)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH':'mm':'ss 'GMT'");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		formatter.setCalendar(new GregorianCalendar());
		return formatter.format(input);
	}
}