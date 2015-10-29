package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Date;

import javax.xml.ws.Holder;

public final class ContentDispositionHeaderValue extends HttpParameterValueBase
{
	private static final String FILENAME = "filename";
	private static final String NAME = "name";
	private static final String FILENAME_STAR = "filename*";
	private static final String CREATION_DATE = "creation-date";
	private static final String MODIFICATION_DATE = "modification-date";
	private static final String READ_DATE = "read-date";
	private static final String SIZE = "size";

	private String _dispositionType;

	private ContentDispositionHeaderValue()
	{ }

	protected ContentDispositionHeaderValue(final ContentDispositionHeaderValue source)
	{
		super(source);
		this._dispositionType = source._dispositionType;
	}

	public ContentDispositionHeaderValue(final String dispositionType)
	{
		this._dispositionType = dispositionType;
	}

	@Override
	public ContentDispositionHeaderValue clone()
	{
		return (ContentDispositionHeaderValue)super.clone();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof ContentDispositionHeaderValue)) return false;

		final ContentDispositionHeaderValue that = (ContentDispositionHeaderValue)obj;
		return this._dispositionType != null && this._dispositionType.equalsIgnoreCase(that._dispositionType) && super.equalsCollection(that.getParameters());
	}

	@Override
	public int hashCode()
	{
		return this._dispositionType.toLowerCase().hashCode() ^ super.hashCode();
	}

	@Override
	public String toString()
	{
		return this._dispositionType + super.toString();
	}

	public final String getFilename()
	{
		return this.getName(FILENAME);
	}
	public final void setFilename(final String value)
	{
		this.setName(FILENAME, value);
	}

	public final String getName()
	{
		return this.getName(NAME);
	}
	public final void setName(final String value)
	{
		this.setName(NAME, value);
	}

	public final String getFilenameStar()
	{
		return this.getName(FILENAME_STAR);
	}
	public final void setFilenameStar(final String value)
	{
		this.setName(FILENAME_STAR, value);
	}

	public final String getDispositionType()
	{
		return this._dispositionType;
	}
	public final void setDispositionType(final String value)
	{
		checkDispositionTypeFormat(value);
		this._dispositionType = value;
	}

	public final Date getCreationDate()
	{
		return super.getDate(CREATION_DATE);
	}
	public final void setCreationDate(final Date value)
	{
		super.setDate(CREATION_DATE, value);
	}

	public final Date getModificationDate()
	{
		return super.getDate(MODIFICATION_DATE);
	}
	public final void setModificationDate(final Date value)
	{
		super.setDate(MODIFICATION_DATE, value);
	}

	public final Date getReadDate()
	{
		return super.getDate(READ_DATE);
	}
	public final void setReadDate(final Date value)
	{
		super.setDate(READ_DATE, value);
	}

	public final Long getSize()
	{
		return super.getLong(SIZE);
	}
	public final void setSize(final Long value)
	{
		super.setLong(SIZE, value, LONG_INTEGER_VALIDATOR);
	}

	private String getName(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = super.get(name);
		if (nameValueHeaderValue == null) return null;

		final String text = nameValueHeaderValue.getValue();
		final Holder<String> result = new Holder<String>();
		if (name.endsWith("*"))
		{
			if (tryDecode5987(text, result)) return result.value;
			return null;
		}

		if (tryDecodeMime(text, result)) return result.value;
		return text;
	}
	private void setName(final String name, final String value)
	{
		final NameValueHeaderValue nameValueHeaderValue = super.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this.getParameters().remove(nameValueHeaderValue);
		}
		else
		{
			final String text = name.endsWith("*") ? encode5987(value) : encodeAndQuoteMime(value);
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(text);
			else this.getParameters().add(new NameValueHeaderValue(name, text));
		}
	}

	static int getDispositionTypeLength(final String input, final Holder<Integer> index, final Holder<ContentDispositionHeaderValue> parsedValue)
	{
		final int length = input.length();
		if (index.value >= length) return 0;

		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final Holder<String> dispositionType = new Holder<String>();
		final int dispositionTypeExpressionLength = getDispositionTypeExpressionLength(input, index2, dispositionType);
		if (dispositionTypeExpressionLength == 0) return 0;

		HttpRuleParser.getWhitespaceLength(input, index2);

		final ContentDispositionHeaderValue contentDispositionHeaderValue = new ContentDispositionHeaderValue();
		contentDispositionHeaderValue._dispositionType = dispositionType.value;
		if (index2.value > length || input.charAt(index2.value) != ';')
		{
			parsedValue.value = contentDispositionHeaderValue;

			final int a = index2.value - index.value;
			index.value = index2.value;
			return a;
		}
		++index2.value;

		final int nameValueListLength = NameValueHeaderValue.getNameValueCollectionLength(input, index2, ';', contentDispositionHeaderValue.getParameters(), NameValueHeaderValue.class);
		if (nameValueListLength == 0) return 0;
		parsedValue.value = contentDispositionHeaderValue;

		final int a = index2.value - index.value;
		index.value = index2.value;
		return a;
	}

	private static int getDispositionTypeExpressionLength(final String input, final Holder<Integer> index, final Holder<String> dispositionType)
	{
		final Holder<Integer> index2 = new Holder<Integer>(index.value);
		final int tokenLength = HttpRuleParser.getTokenLength(input, index2);
		if (tokenLength == 0) return 0;
		dispositionType.value = input.substring(index.value, index2.value);

		index.value = index2.value;
		return tokenLength;
	}

	private static void checkDispositionTypeFormat(final String dispositionType)
	{
		if (dispositionType == null || dispositionType.isEmpty()) throw new IllegalArgumentException();

		final Holder<Integer> index = new Holder<Integer>(0);
		final Holder<String> parsedDispositionType = new Holder<String>();
		if (getDispositionTypeExpressionLength(dispositionType, index, parsedDispositionType) == 0 || parsedDispositionType.value.length() != dispositionType.length())
		{
			throw new IllegalArgumentException();
		}
	}

	private static boolean isQuoted(final String input)
	{
		return input.length() > 1 && input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"';
	}
	private static boolean tryDecodeMime(final String input, final Holder<String> parsedValue)
	{
		if (!isQuoted(input) || input.length() < 10) return false;

		final String[] array = input.split("\\?");
		if (array.length != 5 || !"\"=".equals(array[0]) || !"=\"".equals(array[4]) || "b".equalsIgnoreCase(array[2])) return false;

		try
		{
			final Charset encoding = Charset.forName(array[1]);
			final byte[] bytes = Base64.decodeBase64(array[3]);
			parsedValue.value = new String(bytes, encoding);
			return true;
		}
		catch (final UnsupportedCharsetException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	private static boolean tryDecode5987(final String input, final Holder<String> parsedValue)
	{
		final String[] array = input.split("'");
		if (array.length != 3) return false;

		final StringBuilder builder = new StringBuilder();
		try
		{
			final Charset encoding = Charset.forName(array[0]);
			final String text = array[2];
			final int length = text.length();

			final byte[] byteArray = new byte[length];
			int left = 0;
			for (int i = 0; i < length; ++i)
			{
				if (isHexEncoding(input, i))
				{
					final Holder<Integer> index = new Holder<Integer>(i);
					byteArray[left++] = (byte)hexUnescape(input, index);
					i = index.value - 1;
				}
				else
				{
					if (left > 0)
					{
						builder.append(new String(byteArray, 0, left, encoding));
						left = 0;
					}
					builder.append(text.charAt(i));
				}
			}
			if (left > 0)
			{
				builder.append(new String(byteArray, 0, left, encoding));
			}
		}
		catch (final UnsupportedCharsetException e)
		{
			e.printStackTrace();
			return false;
		}
		parsedValue.value = builder.toString();
		return true;
	}

	protected static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static boolean requireEncodings(final String input)
	{
		final int length = input.length();
		for (int i = 0; i < length; ++i)
		{
			if (input.charAt(i) > '\u007f') return true;
		}
		return false;
	}
	private static String encodeMime(final String input)
	{
		return "=?utf-8?B?" + Base64.encodeBase64String(UTF8_CHARSET.encode(input).array()) + "?=";
	}
	private static String encodeAndQuoteMime(String input)
	{
		boolean flag = false;
		if (isQuoted(input))
		{
			flag = true;
			input = input.substring(1, input.length() - 1);
		}
		if (input.indexOf("\"", 0) >= 0) throw new IllegalArgumentException();
		if (requireEncodings(input))
		{
			flag = true;
			input = encodeMime(input);
		}
		else if (!flag && HttpRuleParser.getTokenLength(input) != input.length())
		{
			flag = true;
		}
		if (flag) input = '"' + input + '"';
		return input;
	}
	private static String encode5987(final String input)
	{
		final int length = input.length();
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; ++i)
		{
			final char c = input.charAt(i);
			if (c > '\u007f')
			{
				final byte[] byteArray = UTF8_CHARSET.encode(Character.toString(c)).array();
				for (int j = 0; j < byteArray.length; ++j)
				{
					appendHexEscape(builder, (char)byteArray[j]);
				}
			}
			else if (!HttpRuleParser.isTokenChar(c) || c == '*' || c == '\'' || c == '%')
			{
				appendHexEscape(builder, c);
			}
			else
			{
				builder.append(c);
			}
		}
		return builder.toString();
	}

	//region Hex encodings

	private static final char[] UPPER_CHAR_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static boolean isHexEncoding(final String input, final int startIndex)
	{
		return input.length() - startIndex >= 3 && (input.charAt(startIndex) == '%' && escapeAscii(input.charAt(startIndex + 1), input.charAt(startIndex + 2)) != '\uFFFF');
	}
	private static char hexUnescape(final String input, final Holder<Integer> index)
	{
		final int length = input.length();
		if (index.value < 0 || index.value >= length) throw new IllegalArgumentException();

		final char c = input.charAt(index.value);
		if (c == '%' && length - index.value >= 3)
		{
			final char c2 = escapeAscii(input.charAt(index.value + 1), input.charAt(index.value + 2));
			if (c2 != '\uFFFF')
			{
				index.value += 3;
				return c2;
			}
		}

		index.value += 1;
		return c;
	}
	private static char escapeAscii(final char first, final char next)
	{
		if ((first < '0' || first > '9') && (first < 'A' || first > 'F') && (first < 'a' || first > 'f')) return '\uFFFF';
		if ((next < '0' || next > '9') && (next < 'A' || next > 'F') && (next < 'a' || next > 'f')) return '\uFFFF';
		return (char)(((first <= '9' ? first - '0' : ((first <= 'F' ? first - 'A' : first - 'a') + '\n')) << 4) + (next <= '9' ? next - '0' : ((next <= 'F' ? next - 'A' : next - 'a') + '\n')));
	}
	private static void appendHexEscape(final StringBuilder builder, final char c)
	{
		if (c > '\u00ff') throw new IllegalArgumentException();
		builder.append('%');
		builder.append(UPPER_CHAR_TABLE[(c & '\u00f0') >> 4]);
		builder.append(UPPER_CHAR_TABLE[c & '\u000f']);
	}

	//endregion
}