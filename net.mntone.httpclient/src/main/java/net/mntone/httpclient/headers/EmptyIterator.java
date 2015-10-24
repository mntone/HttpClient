package net.mntone.httpclient.headers;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class EmptyIterator implements Iterator
{
	private static final EmptyIterator _instance = new EmptyIterator();

	public static EmptyIterator getInstance()
	{
		return _instance;
	}

	private EmptyIterator()
	{ }

	@Override
	public boolean hasNext()
	{
		return false;
	}

	@Override
	public Object next()
	{
		throw new NoSuchElementException();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}