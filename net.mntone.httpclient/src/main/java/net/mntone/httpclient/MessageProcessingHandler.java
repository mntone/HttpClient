package net.mntone.httpclient;

import jersey.repackaged.jsr166e.CompletableFuture;

public abstract class MessageProcessingHandler extends DelegatingHandler
{
	protected MessageProcessingHandler()
	{
	}

	protected MessageProcessingHandler(HttpMessageHandler innerHandler)
	{
		super(innerHandler);
	}

	protected abstract HttpRequestMessage ProcessRequest(HttpRequestMessage request);

	protected abstract HttpResponseMessage ProcessResponse(HttpResponseMessage response);

	@Override
	final CompletableFuture<HttpResponseMessage> sendAsync(final HttpRequestMessage request)
	{
		if (request == null)
		{
			throw new IllegalArgumentException();
		}

		final HttpRequestMessage request2 = this.ProcessRequest(request);
		return super.sendAsync(request2).thenApplyAsync(new CompletableFuture.Fun<HttpResponseMessage, HttpResponseMessage>()
		{
			@Override
			public HttpResponseMessage apply(final HttpResponseMessage response)
			{
				final HttpResponseMessage result = MessageProcessingHandler.this.ProcessResponse(response);
				return result;
			}
		});
	}
}