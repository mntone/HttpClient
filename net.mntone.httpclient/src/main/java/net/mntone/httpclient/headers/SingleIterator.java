package net.mntone.httpclient.headers;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SingleIterator<T> implements Iterator<T>
{
	private final T _item;

	private boolean _advanced = false;

	public SingleIterator(final T item)
	{
		this._item = item;
	}

	@Override
	public boolean hasNext()
	{
		return !this._advanced;
	}

	@Override
	public T next()
	{
		if (this._advanced) throw new NoSuchElementException();

		this._advanced = true;
		return this._item;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}