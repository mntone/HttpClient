package net.mntone.httpclient.headers;

import net.mntone.httpclient.GenericHelpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class HttpHeaderValueCollection<T> implements Collection<T>
{
	private final String _headerName;
	private final HttpHeaders _store;
	private final T _specialValue;
	private final Validator<T> _validator;
	private final Class<T> _genericClass;

	public interface Validator<T>
	{
		void valid(HttpHeaderValueCollection<T> self, T item);
	}

	HttpHeaderValueCollection(final String headerName, final HttpHeaders store, final Class<T> genericClass)
	{
		this(headerName, store, GenericHelpers.getDefault(genericClass), null, genericClass);
	}

	HttpHeaderValueCollection(final String headerName, final HttpHeaders store, final Validator<T> validator, final Class<T> genericClass)
	{
		this(headerName, store, GenericHelpers.getDefault(genericClass), validator, genericClass);
	}

	HttpHeaderValueCollection(final String headerName, final HttpHeaders store, final T specialValue, final Class<T> genericClass)
	{
		this(headerName, store, specialValue, null, genericClass);
	}

	HttpHeaderValueCollection(final String headerName, final HttpHeaders store, final T specialValue, final Validator<T> validator, final Class<T> genericClass)
	{
		this._headerName = headerName;
		this._store = store;
		this._specialValue = specialValue;
		this._validator = validator;
		this._genericClass = genericClass;
	}

	@Override
	public int size()
	{
		return this.getCount();
	}

	@Override
	public boolean isEmpty()
	{
		return this.getCount() == 0;
	}

	@Override
	public void clear()
	{
		this._store.remove(this._headerName);
	}

	@Override
	public boolean contains(final Object item)
	{
		this.checkObjectValue(item);
		return this._store.containsParsedValue(this._headerName, item);
	}

	@Override
	public boolean containsAll(final Collection<?> items)
	{
		boolean result = true;
		for (final Object i : items)
		{
			if (!this.contains(i))
			{
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean add(final T item)
	{
		this.checkValue(item);
		this._store.putParsedValue(this._headerName, item);
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends T> items)
	{
		for (final T item : items) this.add(item);
		return true;
	}

	@Override
	public boolean remove(final Object item)
	{
		this.checkObjectValue(item);
		return this._store.removeParsedValue(this._headerName, item);
	}

	@Override
	public boolean removeAll(final Collection<?> items)
	{
		boolean result = true;
		for (final Object item : items)
		{
			if (!this.remove(item)) result = false;
		}
		return result;
	}

	@Override
	public boolean retainAll(final Collection<?> items)
	{
		throw new NotImplementedException();
	}

	@Override
	public Iterator<T> iterator()
	{
		final Object parsedValue = this._store.getParsedValue(this._headerName);
		if (parsedValue == null) return EmptyIterator.getInstance();
		if (!GenericHelpers.isSafetyGenericsList(parsedValue, this._genericClass)) return new SingleIterator<T>((T)parsedValue);

		final List<T> list = (List<T>)parsedValue;
		return list.iterator();
	}

	@Override
	public Object[] toArray()
	{
		final Object parsedValue = this._store.getParsedValue(this._headerName);
		if (parsedValue == null)
		{
			return null;
		}
		if (!GenericHelpers.isSafetyGenericsList(parsedValue, this._genericClass))
		{
			return Arrays.asList((T)parsedValue).toArray();
		}

		final List<T> list = (List<T>)parsedValue;
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		final Object parsedValue = this._store.getParsedValue(this._headerName);
		if (parsedValue == null)
		{
			if (a == null) return null;
			for (int i = 0; i < a.length; ++i) a[i] = null;
			return a;
		}
		if (!GenericHelpers.isSafetyGenericsList(parsedValue, this._genericClass))
		{
			if (a == null || a.length < 1) return Arrays.asList((T)parsedValue).toArray(a);
			a[0] = (T)parsedValue;
			for (int i = 1; i < a.length; ++i) a[i] = null;
			return a;
		}

		final List<T> list = (List<T>)parsedValue;
		return list.toArray(a);
	}

	public void parseAdd(final String input) throws IllegalAccessException
	{
		this._store.put(this._headerName, input);
	}

	public boolean tryParseAdd(final String input)
	{
		return this._store.tryParseAndPutValue(this._headerName, input);
	}

	private void checkValue(final T item)
	{
		if (item == null) throw new IllegalArgumentException();
		if (this._validator != null) this._validator.valid(this, item);
	}

	private void checkObjectValue(final Object item)
	{
		if (!(this._genericClass.isAssignableFrom(item.getClass()))) throw new IllegalArgumentException();
		if (this._validator != null) this._validator.valid(this, (T)item);
	}

	private int getCount()
	{
		final Object parsedValue = this._store.getParsedValue(this._headerName);
		if (parsedValue == null) return 0;
		if (!GenericHelpers.isSafetyGenericsList(parsedValue, Object.class)) return 1;

		final List<Object> list = (List<Object>)parsedValue;
		return list.size();
	}

	boolean isSpecialValueSet()
	{
		return this._specialValue != null && this._store.containsParsedValue(this._headerName, this._specialValue);
	}

	void setSpecialValue()
	{
		if (!this._store.containsParsedValue(this._headerName, this._specialValue))
		{
			this._store.putParsedValue(this._headerName, this._specialValue);
		}
	}

	void removeSpecialValue()
	{
		this._store.removeParsedValue(this._headerName, this._specialValue);
	}
}