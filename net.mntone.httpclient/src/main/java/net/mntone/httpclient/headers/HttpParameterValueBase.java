package net.mntone.httpclient.headers;

import net.mntone.httpclient.HttpRuleParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.ws.Holder;

public abstract class HttpParameterValueBase implements Cloneable
{
	public interface ValueValidator<T>
	{
		boolean check(final T value);
	}

	public static final ValueValidator<Long> LONG_INTEGER_VALIDATOR = new ValueValidator<Long>()
	{
		@Override
		public boolean check(final Long value)
		{
			return value < 0;
		}
	};
	public static final ValueValidator<Double> DOUBLE_0TO1_VALIDATOR = new ValueValidator<Double>()
	{
		@Override
		public boolean check(final Double value)
		{
			return value < 0.0 || value > 1.0;
		}
	};

	private ArrayList<NameValueHeaderValue> _parameters = null;

	protected HttpParameterValueBase()
	{ }

	protected HttpParameterValueBase(final HttpParameterValueBase source)
	{
		this._parameters = new ArrayList<NameValueHeaderValue>();
		for (final NameValueHeaderValue item : source._parameters)
		{
			this._parameters.add(item);
		}
	}

	@Override
	public HttpParameterValueBase clone()
	{
		try
		{
			final HttpParameterValueBase result = (HttpParameterValueBase)super.clone();
			return result;
		}
		catch (final CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode()
	{
		return NameValueHeaderValue.hashCode(this.getParameters());
	}

	@Override
	public String toString()
	{
		return NameValueHeaderValue.toString(this.getParameters(), ';', true);
	}

	protected final NameValueHeaderValue get(final String name)
	{
		return NameValueHeaderValue.find(this.getParameters(), name);
	}

	protected final String getString(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue == null) return null;
		return nameValueHeaderValue.getValue();
	}
	protected final void setString(final String name, final String value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null || value.isEmpty())
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value);
			else this.getParameters().add(new NameValueHeaderValue(name, value));
		}
	}

	protected final Integer getInteger(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue == null) return null;

		try
		{
			return Integer.parseInt(nameValueHeaderValue.getValue());
		}
		catch (final NumberFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	protected final void setInteger(final String name, final Integer value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}
	protected final void setInteger(final String name, final Integer value, final ValueValidator<Integer> validator)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (validator.check(value)) throw new IllegalArgumentException();
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}

	protected final Long getLong(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue == null) return null;

		try
		{
			return Long.parseLong(nameValueHeaderValue.getValue());
		}
		catch (final NumberFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	protected final void setLong(final String name, final Long value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}
	protected final void setLong(final String name, final Long value, final ValueValidator<Long> validator)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (validator.check(value)) throw new IllegalArgumentException();
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}

	protected final Float getFloat(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue == null) return null;

		try
		{
			return Float.parseFloat(nameValueHeaderValue.getValue());
		}
		catch (final NumberFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	protected final void setFloat(final String name, final Float value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}
	protected final void setFloat(final String name, final Float value, final ValueValidator<Float> validator)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (validator.check(value)) throw new IllegalArgumentException();
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}

	protected final Double getDouble(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue == null) return null;

		try
		{
			return Double.parseDouble(nameValueHeaderValue.getValue());
		}
		catch (final NumberFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	protected final void setDouble(final String name, final Double value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}
	protected final void setDouble(final String name, final Double value, final ValueValidator<Double> validator)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			if (validator.check(value)) throw new IllegalArgumentException();
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(value.toString());
			else this._parameters.add(new NameValueHeaderValue(name, value.toString()));
		}
	}

	private static boolean isQuoted(final String input)
	{
		return input.length() > 1 && input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"';
	}
	protected final Date getDate(final String name)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (nameValueHeaderValue != null)
		{
			String value = nameValueHeaderValue.getValue();
			if (isQuoted(value)) value = value.substring(1, value.length() - 1);

			final Holder<Date> result = new Holder<Date>();
			if (HttpRuleParser.tryStringToDate(value, result))
			{
				return result.value;
			}
		}
		return null;
	}
	protected final void setDate(final String name, final Date value)
	{
		final NameValueHeaderValue nameValueHeaderValue = this.get(name);
		if (value == null)
		{
			if (nameValueHeaderValue != null) this._parameters.remove(nameValueHeaderValue);
		}
		else
		{
			final String text = '"' + HttpRuleParser.dateToString(value) + '"';
			if (nameValueHeaderValue != null) nameValueHeaderValue.setValue(text);
			else this._parameters.add(new NameValueHeaderValue(name, text));
		}
	}

	public final Collection<NameValueHeaderValue> getParameters()
	{
		if (this._parameters == null)
		{
			this._parameters = new ArrayList<NameValueHeaderValue>();
		}
		return this._parameters;
	}

	protected boolean equalsCollection(final Collection<NameValueHeaderValue> other)
	{
		return HttpHeaderUtils.equalsCollection(this._parameters, other);
	}
}