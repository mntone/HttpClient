package net.mntone.httpclient.headers;

import net.mntone.httpclient.GenericHelpers;
import net.mntone.httpclient.HttpRuleParser;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.Holder;

public abstract class HttpHeaders implements Iterable<Map.Entry<String, String[]>>
{
	private HashMap<String, HeaderStoreItemInfo> _headerStore;
	private Map<String, HttpHeaderParser> _parserStore;
	private Set<String> _invalidHeaders;

	protected HttpHeaders()
	{
	}

	void setConfiguration(final Map<String, HttpHeaderParser> parserStore, final Set<String> invalidHeaders)
	{
		this._parserStore = parserStore;
		this._invalidHeaders = invalidHeaders;
	}

	public final void clear()
	{
		if (this._headerStore != null)
		{
			this._headerStore.clear();
		}
	}

	public final boolean containsKey(final Object key)
	{
		final String name;
		try
		{
			name = this.checkHeaderName(key);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return false;
		}
		if (this._headerStore == null) return false;

		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		return this.tryGetAndParseHeaderInfo(name, info);
	}

	final boolean containsParsedValue(final String name, final Object value)
	{
		if (this._headerStore == null) return false;

		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		if (!this.tryGetAndParseHeaderInfo(name, info)) return false;

		if (info.value._parsedValue == null) return false;

		final HttpHeaderParser.EqualityComparer comparer = info.value._parser.getComparer();
		if (info.value._parsedValue instanceof List<?>)
		{
			final List<Object> list = (List<Object>)info.value._parsedValue;
			for (final Object current : list)
			{
				if (areEquals(value, current, comparer)) return true;
			}
			return false;
		}

		return areEquals(value, info.value._parsedValue, comparer);
	}

	final Object getParsedValue(final String name)
	{
		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		if (!this.tryGetAndParseHeaderInfo(name, info))
		{
			return null;
		}
		return info.value._parsedValue;
	}

	public final void put(final String name, final String value) throws IllegalAccessException
	{
		this.checkHeaderName(name);

		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		final boolean flag = this.prepareHeaderInfoForPut(name, info);
		parseAndAddHeaderValue(name, info.value, value);
		if (flag && info.value._parsedValue != null)
		{
			this.addHeaderInfoToStore(name, info.value);
		}
	}

	public final void put(final String name, final String[] values) throws IllegalAccessException
	{
		if (values == null) throw new IllegalAccessException();
		this.checkHeaderName(name);

		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		final boolean flag = this.prepareHeaderInfoForPut(name, info);
		for (final String value : values)
		{
			parseAndAddHeaderValue(name, info.value, value);
		}
		if (flag && info.value._parsedValue != null)
		{
			this.addHeaderInfoToStore(name, info.value);
		}
	}

	final void putParsedValue(final String name, final Object value)
	{
		addValue(this.getOrCreateHeaderInfo(name, true), value, StoreLocation.Parsed);
	}

	private void putHeaderValue(final String name, final HeaderStoreItemInfo sourceInfo)
	{
		final HeaderStoreItemInfo info = this.createAndAddHeaderInfoToStore(name);
		if (info._parser == null)
		{
			info._parsedValue = cloneHeaderInfo(sourceInfo._parsedValue);
			return;
		}
		info._invalidValue = cloneHeaderInfo(sourceInfo._invalidValue);
		if (sourceInfo._parsedValue != null)
		{
			if (sourceInfo._parsedValue instanceof List<?>)
			{
				final List<Object> sourceList = (List<Object>)sourceInfo._parsedValue;
				for (final Object sourceItem : sourceList)
				{
					cloneAndAddValue(info, sourceItem);
				}
			}
			else
			{
				cloneAndAddValue(info, sourceInfo._parsedValue);
			}
		}
	}

	private static Object cloneHeaderInfo(final Object source)
	{
		if (source == null) return null;
		if (!(source instanceof List<?>)) return source;

		final List<Object> sourceList = (List<Object>)source;
		return new ArrayList<Object>(sourceList);
	}

	private interface CloneablePublic
	{
		Object clone();
	}

	private static void cloneAndAddValue(final HeaderStoreItemInfo info, final Object source)
	{
		if (source instanceof Cloneable)
		{
			final CloneablePublic cloneable = (CloneablePublic)source;
			addValue(info, cloneable.clone(), StoreLocation.Parsed);
		}
		else
		{
			addValue(info, source, StoreLocation.Parsed);
		}
	}

	final boolean tryParseAndPutValue(final String name, final String value)
	{
		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		final boolean flag = this.prepareHeaderInfoForPut(name, info);
		final boolean flag2 = tryParseAndAddHeaderValue(name, info.value, value, false);
		if (flag && flag2 && info.value._parsedValue != null)
		{
			this.addHeaderInfoToStore(name, info.value);
		}
		return flag2;
	}

	final void setOrRemoveParsedValue(final String name, final Object value)
	{
		if (value == null) this.remove(name);
		else this.setParsedValue(name, value);
	}

	final void setParsedValue(final String name, final Object value)
	{
		final HeaderStoreItemInfo info = this.getOrCreateHeaderInfo(name, true);
		info._rawValue = null;
		info._parsedValue = null;
		info._invalidValue = null;
		addValue(info, value, StoreLocation.Parsed);
	}

	public final String[] remove(final Object key)
	{
		final String name;
		try
		{
			name = this.checkHeaderName(key);
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}

		return this.remove(name);
	}

	public final String[] remove(final String name)
	{
		if (this._headerStore == null) return null;

		final HeaderStoreItemInfo info = this._headerStore.remove(name);
		if (info == null) return null;

		return getValuesAsString(info);
	}

	final boolean removeParsedValue(final String name, final Object value)
	{
		if (this._headerStore == null) return false;

		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		if (!this.tryGetAndParseHeaderInfo(name, info)) return false;

		if (info.value._parsedValue == null) return false;

		boolean result = false;
		final HttpHeaderParser.EqualityComparer comparer = info.value._parser.getComparer();
		if (info.value._parsedValue instanceof List<?>)
		{
			final List<Object> list = (List<Object>)info.value._parsedValue;
			for (final Object current : list)
			{
				if (areEquals(value, current, comparer))
				{
					result = list.remove(current);
					break;
				}
			}
			if (list.size() == 0)
			{
				info.value._parsedValue = null;
			}
		}
		else
		{
			if (areEquals(value, info.value._parsedValue, comparer))
			{
				info.value._parsedValue = null;
				result = true;
			}
		}

		if (info.value.isEmpty()) this.remove(name);

		return result;
	}

	private static boolean areEquals(final Object x, final Object y, final HttpHeaderParser.EqualityComparer comparer)
	{
		if (comparer != null) return comparer.equals(x, y);
		return x.equals(y);
	}

	private static String[] getValuesAsString(final HeaderStoreItemInfo info)
	{
		return getValuesAsString(info, null);
	}

	private static String[] getValuesAsString(final HeaderStoreItemInfo info, final Object exclude)
	{
		final int count = getValueCount(info);
		String[] array = new String[count];
		if (count > 0)
		{
			final Holder<Integer> current = new Holder<Integer>(0);
			readStoreValues(array, info._rawValue, null, null, current, String.class);
			readStoreValues(array, info._parsedValue, info._parser, exclude, current, Object.class);
			readStoreValues(array, info._invalidValue, null, null, current, String.class);
			if (current.value < count)
			{
				final String[] array2 = new String[current.value];
				System.arraycopy(array, 0, array2, 0, array2.length);
				array = array2;
			}
		}
		return array;
	}

	private static int getValueCount(final HeaderStoreItemInfo info)
	{
		final Holder<Integer> result = new Holder<Integer>(0);
		updateValueCount(info._rawValue, result);
		updateValueCount(info._invalidValue, result);
		updateValueCount(info._parsedValue, result);
		return result.value;
	}

	private static void updateValueCount(final Object valueStore, final Holder<Integer> count)
	{
		if (valueStore == null) return;

		if (valueStore instanceof List<?>)
		{
			final List<?> list = (List<?>)valueStore;
			count.value += list.size();
			return;
		}
		count.value += 1;
	}

	private static <T> void readStoreValues(
		final String[] values, final Object storeValue, final HttpHeaderParser parser, final T exclude, final Holder<Integer> currentIndex, final Class<T> genericClass)
	{
		if (GenericHelpers.isSafetyGenericsList(storeValue, genericClass))
		{
			final List<T> list = (List<T>)storeValue;
			for (final Object currentValue : list)
			{
				if (shouldAdd(currentValue, parser, exclude))
				{
					values[currentIndex.value] = parser != null ? parser.toString(currentValue) : currentValue.toString();
					++currentIndex.value;
				}
			}
		}
		else if (storeValue != null)
		{
			if (shouldAdd(storeValue, parser, exclude))
			{
				values[currentIndex.value] = parser != null ? parser.toString(storeValue) : storeValue.toString();
				++currentIndex.value;
			}
		}
	}

	private static <T> boolean shouldAdd(final Object storeValue, final HttpHeaderParser parser, final T exclude)
	{
		boolean result = true;
		if (parser != null && exclude != null)
		{
			result = areEquals(exclude, storeValue, parser.getComparer());
		}
		return result;
	}

	private HeaderStoreItemInfo getOrCreateHeaderInfo(final String name, final boolean parseRawValues)
	{
		final Holder<HeaderStoreItemInfo> result = new Holder<HeaderStoreItemInfo>();
		boolean flag;
		if (parseRawValues)
		{
			flag = this.tryGetAndParseHeaderInfo(name, result);
		}
		else
		{
			flag = this.tryGetHeaderInfo(name, result);
		}

		if (!flag)
		{
			result.value = this.createAndAddHeaderInfoToStore(name);
		}
		return result.value;
	}

	private HeaderStoreItemInfo createAndAddHeaderInfoToStore(final String name)
	{
		final HeaderStoreItemInfo info = new HeaderStoreItemInfo(this.getParser(name));
		this.addHeaderInfoToStore(name, info);
		return info;
	}

	private void addHeaderInfoToStore(final String name, final HeaderStoreItemInfo info)
	{
		if (this._headerStore == null)
		{
			this._headerStore = new HashMap<String, HeaderStoreItemInfo>();
		}
		this._headerStore.put(name, info);
	}

	private boolean prepareHeaderInfoForPut(final String name, final Holder<HeaderStoreItemInfo> info)
	{
		if (!this.tryGetAndParseHeaderInfo(name, info))
		{
			info.value = new HeaderStoreItemInfo(this.getParser(name));
			return true;
		}
		return false;
	}

	private HttpHeaderParser getParser(final String name)
	{
		if (this._parserStore == null || !this._parserStore.containsKey(name))
		{
			return null;
		}
		return this._parserStore.get(name);
	}

	//region Check Header Name methods

	private String checkHeaderName(final Object name) throws IllegalAccessException
	{
		if (!(name instanceof String))
		{
			throw new IllegalArgumentException();
		}
		final String stringifyName = (String)name;
		this.checkHeaderName(stringifyName);
		return stringifyName;
	}

	private void checkHeaderName(final String name) throws IllegalAccessException
	{
		if (name == null || name.isEmpty() || HttpRuleParser.getTokenLength(name, 0) != name.length())
		{
			throw new IllegalArgumentException();
		}
		if (this._invalidHeaders != null && this._invalidHeaders.contains(name))
		{
			throw new IllegalAccessException();
		}
	}

	private boolean tryCheckHeaderName(final String name)
	{
		return name != null && !name.isEmpty() && HttpRuleParser.getTokenLength(name, 0) == name.length() && (this._invalidHeaders == null || !this._invalidHeaders.contains(name));
	}

	//endregion


	private boolean tryGetAndParseHeaderInfo(final String name, final Holder<HeaderStoreItemInfo> info)
	{
		return this.tryGetHeaderInfo(name, info) && this.parseRawHeaderValues(name, info.value, true);
	}

	private boolean tryGetHeaderInfo(final String name, final Holder<HeaderStoreItemInfo> info)
	{
		if (this._headerStore == null || !this._headerStore.containsKey(name))
		{
			info.value = null;
			return false;
		}
		info.value = this._headerStore.get(name);
		return true;
	}

	private boolean parseRawHeaderValues(final String name, final HeaderStoreItemInfo info, final boolean removeEmptyHeader)
	{
		final Object rawValue = info.getRawValue();
		if (rawValue != null)
		{
			if (GenericHelpers.isSafetyGenericsList(rawValue, String.class))
			{
				parseMultipleRawHeaderValue(name, info);
			}
			else if (rawValue instanceof String)
			{
				parseSingleRawHeaderValue(name, info);
			}
			info._rawValue = null;
			if (info._invalidValue == null && info._parsedValue == null)
			{
				if (removeEmptyHeader)
				{
					this._headerStore.remove(name);
				}
				return false;
			}
		}
		return true;
	}

	private static void parseMultipleRawHeaderValue(final String name, final HeaderStoreItemInfo info)
	{
		final List<String> rawValueList = (List<String>)info._rawValue;
		if (info._parser == null)
		{
			for (final String rawValueListItem : rawValueList)
			{
				if (!HttpRuleParser.containsInvalidNewLine(rawValueListItem))
				{
					addValue(info, rawValueListItem, StoreLocation.Parsed);
				}
			}
		}
		else
		{
			for (final String rawValueListItem : rawValueList)
			{
				tryParseAndAddHeaderValue(name, info, rawValueListItem, true);
			}
		}
	}

	private static void parseSingleRawHeaderValue(final String name, final HeaderStoreItemInfo info)
	{
		final String rawValueString = (String)info._rawValue;
		if (info._parser == null)
		{
			if (!HttpRuleParser.containsInvalidNewLine(rawValueString))
			{
				info._parsedValue = rawValueString;
			}
		}
		else
		{
			tryParseAndAddHeaderValue(name, info, rawValueString, true);
		}
	}

	private static void addValue(final HeaderStoreItemInfo info, final Object value, final StoreLocation location)
	{
		final Holder<Object> obj = new Holder<Object>();
		switch (location)
		{
		case Raw:
			obj.value = info._rawValue;
			addValueToStoreValue(value, obj, String.class);
			info._rawValue = obj.value;
			break;

		case Invalid:
			obj.value = info._invalidValue;
			addValueToStoreValue(value, obj, String.class);
			info._invalidValue = obj.value;
			break;

		case Parsed:
			obj.value = info._parsedValue;
			addValueToStoreValue(value, obj, Object.class);
			info._parsedValue = obj.value;
			break;
		}
	}

	private static <T> void addValueToStoreValue(final Object value, final Holder<Object> currentStoreValue, final Class<T> storeClass)
	{
		if (currentStoreValue.value == null)
		{
			currentStoreValue.value = value;
			return;
		}

		final List<T> list;
		if (GenericHelpers.isSafetyGenericsList(currentStoreValue.value, storeClass))
		{
			list = (List<T>)currentStoreValue.value;
		}
		else
		{
			list = new ArrayList<T>(2);
			list.add((T)currentStoreValue.value);
			currentStoreValue.value = list;
		}
		list.add((T)value);
	}

	private static void parseAndAddHeaderValue(final String name, final HeaderStoreItemInfo info, final String value)
	{
		if (info._parser == null)
		{
			HttpRuleParser.containsInvalidNewLine(value);
			addValue(info, value != null ? value : "", StoreLocation.Parsed);
		}
		if (!info.canAddValue()) throw new IllegalArgumentException();

		final Holder<Integer> index = new Holder<Integer>(0);
		Object obj = info._parser.parseValue(value, info._parsedValue, index);
		if (value == null || index.value == value.length())
		{
			if (obj != null)
			{
				addValue(info, obj, StoreLocation.Parsed);
			}
			return;
		}

		final List<Object> list = new ArrayList<Object>();
		if (obj != null) list.add(obj);
		while (index.value < value.length())
		{
			obj = info._parser.parseValue(value, info._parsedValue, index);
			if (obj != null) list.add(obj);
		}
		for (final Object listItem : list)
		{
			addValue(info, listItem, StoreLocation.Parsed);
		}
	}

	private static boolean tryParseAndAddHeaderValue(final String name, final HeaderStoreItemInfo info, final String value, final boolean addWhenInvalid)
	{
		if (!info.canAddValue())
		{
			if (addWhenInvalid)
			{
				addValue(info, value != null ? value : "", StoreLocation.Invalid);
			}
			return false;
		}

		final Holder<Integer> index = new Holder<Integer>(0);
		final Holder<Object> obj = new Holder<Object>();
		if (!info._parser.tryParseValue(value, info._parsedValue, index, obj))
		{
			if (!HttpRuleParser.containsInvalidNewLine(value) && addWhenInvalid)
			{
				addValue(info, value != null ? value : "", StoreLocation.Invalid);
			}
			return false;
		}
		if (value == null || index.value == value.length())
		{
			if (obj.value != null)
			{
				addValue(info, obj.value, StoreLocation.Parsed);
			}
			return true;
		}

		final List<Object> list = new ArrayList<Object>();
		if (obj.value != null) list.add(obj.value);
		while (index.value < value.length())
		{
			if (!info._parser.tryParseValue(value, info._parsedValue, index, obj))
			{
				if (!HttpRuleParser.containsInvalidNewLine(value) && addWhenInvalid)
				{
					addValue(info, value, StoreLocation.Invalid);
				}
				return false;
			}
			if (obj.value != null) list.add(obj.value);
		}
		for (final Object listItem : list)
		{
			addValue(info, listItem, StoreLocation.Parsed);
		}
		return true;
	}

	public final boolean tryAddWithoutValidation(final String name, final String[] values)
	{
		if (values == null) throw new IllegalArgumentException();
		if (!this.tryCheckHeaderName(name)) return false;

		final HeaderStoreItemInfo info = this.getOrCreateHeaderInfo(name, false);
		for (final String value : values)
		{
			addValue(info, value != null ? value : "", StoreLocation.Raw);
		}
		return true;
	}


	@Override
	public final Iterator<Map.Entry<String, String[]>> iterator()
	{
		if (this._headerStore == null) return EmptyIterator.getInstance();

		final ArrayList<Map.Entry<String, String[]>> list = new ArrayList<Map.Entry<String, String[]>>();
		List<String> removeList = null;
		for (final Map.Entry<String, HeaderStoreItemInfo> v : this._headerStore.entrySet())
		{
			final String key = v.getKey();
			final HeaderStoreItemInfo info = v.getValue();
			if (!this.parseRawHeaderValues(key, info, false))
			{
				if (removeList == null) removeList = new ArrayList<String>();
				removeList.add(key);
			}
			else
			{
				final String[] valuesAsString = getValuesAsString(info);
				list.add(new AbstractMap.SimpleImmutableEntry<String, String[]>(key, valuesAsString));
			}
		}

		if (removeList != null)
		{
			for (final String i : removeList)
			{
				this._headerStore.remove(i);
			}
		}
		return list.iterator();
	}

	//region Header Strings Generator Methods

	public final Iterator<Map.Entry<String, String>> getHeaderStrings()
	{
		if (this._headerStore == null) return EmptyIterator.getInstance();

		final ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
		for (final Map.Entry<String, HeaderStoreItemInfo> v : this._headerStore.entrySet())
		{
			final String key = v.getKey();
			final HeaderStoreItemInfo info = v.getValue();
			final String headerStrings = getHeaderString(info);
			list.add(new AbstractMap.SimpleImmutableEntry<String, String>(key, headerStrings));
		}
		return list.iterator();
	}

	String getHeaderString(final String name)
	{
		return getHeaderString(name, null);
	}

	String getHeaderString(final String name, final Object exclude)
	{
		final Holder<HeaderStoreItemInfo> info = new Holder<HeaderStoreItemInfo>();
		if (!this.tryGetHeaderInfo(name, info)) return "";
		return getHeaderString(info.value, exclude);
	}

	private static String getHeaderString(final HeaderStoreItemInfo info)
	{
		return getHeaderString(info, null);
	}

	private static String getHeaderString(final HeaderStoreItemInfo info, final Object exclude)
	{
		final String result;
		final String[] values = getValuesAsString(info, exclude);
		if (values.length == 1) result = values[0];
		else
		{
			String separator = ", ";
			if (info._parser != null && info._parser.isSupportsMultipleValues())
			{
				separator = info._parser.getSeparator();
			}
			result = String.join(separator, values);
		}
		return result;
	}

	//endregion

	void addHeaders(final HttpHeaders sourceHeaders)
	{
		if (sourceHeaders._headerStore == null) return;

		List<String> list = null;
		for (final Map.Entry<String, HeaderStoreItemInfo> v : this._headerStore.entrySet())
		{
			final String key = v.getKey();
			if (this._headerStore == null || !this._headerStore.containsKey(key))
			{
				final HeaderStoreItemInfo info = v.getValue();
				if (!this.parseRawHeaderValues(key, info, false))
				{
					if (list == null) list = new ArrayList<String>();
					list.add(key);
				}
				else
				{
					this.putHeaderValue(key, info);
				}
			}
		}
		if (list != null)
		{
			for (final String i : list)
			{
				sourceHeaders._headerStore.remove(i);
			}
		}
	}

	//region Store Entities

	private enum StoreLocation
	{
		Raw,
		Invalid,
		Parsed;
	}

	private static class HeaderStoreItemInfo
	{
		private final HttpHeaderParser _parser;

		private Object _rawValue;
		private Object _invalidValue;
		private Object _parsedValue;

		public HeaderStoreItemInfo(final HttpHeaderParser parser)
		{
			this._parser = parser;
		}

		public HttpHeaderParser getParser()
		{
			return this._parser;
		}

		public Object getRawValue()
		{
			return this._rawValue;
		}

		public void setRawValue(final Object rawValue)
		{
			this._rawValue = rawValue;
		}

		public Object getInvalidValue()
		{
			return this._invalidValue;
		}

		public void setInvalidValue(final Object invalidValue)
		{
			this._invalidValue = invalidValue;
		}

		public Object getParsedValue()
		{
			return this._parsedValue;
		}

		public void setParsedValue(final Object parsedValue)
		{
			this._parsedValue = parsedValue;
		}

		public boolean canAddValue()
		{
			return (this._invalidValue == null && this._parsedValue == null);
		}

		public boolean isEmpty()
		{
			return this._rawValue == null && this._invalidValue == null && this._parsedValue == null;
		}
	}

	//endregion
}