package net.mntone.httpclient;

public enum HttpStatusCode
{
	Continue(100),
	SwitchingProtocols(101),
	Processing(102),

	OK(200),
	Created(201),
	Accepted(202),
	NonAuthoritativeInformation(203),
	NoContent(204),
	ResetContent(205),
	PartialContent(206),
	MultiStatus(207),
	AlreadyReported(208),
	IMUsed(226),

	MultipleChoices(300),
	Ambiguous(300),
	MovedPermanently(301),
	Moved(301),
	Found(302),
	Redirect(302),
	SeeOther(303),
	RedirectMethod(303),
	NotModified(304),
	UseProxy(305),
	SwitchProxy(306),
	TemporaryRedirect(307),
	RedirectKeepVerb(307),
	PermanentRedirect(308),

	BadRequest(400),
	Unauthorized(401),
	PaymentRequired(402),
	Forbidden(403),
	NotFound(404),
	MethodNotAllowed(405),
	NotAcceptable(406),
	ProxyAuthenticationRequired(407),
	RequestTimeout(408),
	Conflict(409),
	Gone(410),
	LengthRequired(411),
	PreconditionFailed(412),
	PayloadTooLarge(413),
	RequestEntityTooLarge(413),
	RequestUriTooLong(414),
	UnsupportedMediaType(415),
	RequestedRangeNotSatisfiable(416),
	ExpectationFailed(417),
	ImATeapot(418),
	MisdirectedRequest(421),
	UnprocessableEntity(422),
	Locked(423),
	FailedDependency(424),
	UpgradeRequired(426),
	PreconditionRequired(428),
	TooManyRequests(429),
	RequestHeaderFieldsTooLarge(431),

	InternalServerError(500),
	NotImplemented(501),
	BadGateway(502),
	ServiceUnavailable(503),
	GatewayTimeout(504),
	HttpVersionNotSupported(505),
	VariantAlsoNegotiates(506),
	InsufficientStorage(507),
	LoopDetected(508),
	NotExtended(510),
	NetworkAuthenticationRequired(511);

	private final int _statusCode;

	HttpStatusCode(final int statusCode)
	{
		this._statusCode = statusCode;
	}

	public int toInt()
	{
		return this._statusCode;
	}

	public static HttpStatusCode fromInt(final int statusCode)
	{
		switch (statusCode)
		{
		case 100:
			return Continue;
		case 101:
			return SwitchingProtocols;
		case 102:
			return Processing;

		case 200:
			return OK;
		case 201:
			return Created;
		case 202:
			return Accepted;
		case 203:
			return NonAuthoritativeInformation;
		case 204:
			return NoContent;
		case 205:
			return ResetContent;
		case 206:
			return PartialContent;
		case 207:
			return MultiStatus;
		case 208:
			return AlreadyReported;
		case 226:
			return IMUsed;

		case 300:
			return MultipleChoices;
		case 301:
			return MovedPermanently;
		case 302:
			return Found;
		case 303:
			return SeeOther;
		case 304:
			return NotModified;
		case 305:
			return UseProxy;
		case 306:
			return SwitchProxy;
		case 307:
			return TemporaryRedirect;
		case 308:
			return PermanentRedirect;

		case 400:
			return BadRequest;
		case 401:
			return Unauthorized;
		case 402:
			return PaymentRequired;
		case 403:
			return Forbidden;
		case 404:
			return NotFound;
		case 405:
			return MethodNotAllowed;
		case 406:
			return NotAcceptable;
		case 407:
			return ProxyAuthenticationRequired;
		case 408:
			return RequestTimeout;
		case 409:
			return Conflict;
		case 410:
			return Gone;
		case 411:
			return LengthRequired;
		case 412:
			return PreconditionFailed;
		case 413:
			return PayloadTooLarge;
		case 414:
			return RequestUriTooLong;
		case 415:
			return UnsupportedMediaType;
		case 416:
			return RequestedRangeNotSatisfiable;
		case 417:
			return ExpectationFailed;
		case 418:
			return ImATeapot;
		case 421:
			return MisdirectedRequest;
		case 422:
			return UnprocessableEntity;
		case 423:
			return Locked;
		case 424:
			return FailedDependency;
		case 426:
			return UpgradeRequired;
		case 428:
			return PreconditionRequired;
		case 429:
			return TooManyRequests;
		case 431:
			return RequestHeaderFieldsTooLarge;

		case 500:
			return InternalServerError;
		case 501:
			return NotImplemented;
		case 502:
			return BadGateway;
		case 503:
			return ServiceUnavailable;
		case 504:
			return GatewayTimeout;
		case 505:
			return HttpVersionNotSupported;
		case 506:
			return VariantAlsoNegotiates;
		case 507:
			return InsufficientStorage;
		case 508:
			return LoopDetected;
		case 510:
			return NotExtended;
		case 511:
			return NetworkAuthenticationRequired;
		}

		throw new IllegalArgumentException();
	}
}