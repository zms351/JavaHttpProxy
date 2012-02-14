package com.meituan.tools.proxy;

public enum StatusCode {

    Accepted(202),//Accepted 指示请求已被接受做进一步处理。
    Ambiguous(300),//Ambiguous 指示请求的信息有多种表示形式。默认操作是将此状态视为重定向，并遵循与此响应关联的 Location 头的内容。

    BadGateway(502),//BadGateway 指示中间代理服务器从另一代理或原始服务器接收到错误响应。
    BadRequest(400),//BadRequest 指示服务器未能识别请求。如果没有其他适用的错误，或者如果不知道准确的错误或错误没有自己的错误代码，则发送 BadRequest。
    Conflict(409),//Conflict 指示由于服务器上的冲突而未能执行请求。
    Continue(100),//Continue 指示客户端可能继续其请求。
    Created(201),//Created 指示请求导致在响应被发送前创建新资源。
    ExpectationFailed(417),//ExpectationFailed 指示服务器未能符合 Expect 头中给定的预期值。
    Forbidden(403),//Forbidden 指示服务器拒绝满足请求。
    Found(302),//Found 指示请求的信息位于 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。原始请求方法为 POST 时，重定向的请求将使用 GET 方法。

    GatewayTimeout(504),//GatewayTimeout 指示中间代理服务器在等待来自另一个代理或原始服务器的响应时已超时。
    Gone(410),//Gone 指示请求的资源不再可用。
    HttpVersionNotSupported(505),//HttpVersionNotSupported 指示服务器不支持请求的 HTTP 版本。
    InternalServerError(500),//InternalServerError 指示服务器上发生了一般错误。
    LengthRequired(411),//LengthRequired 指示缺少必需的 Content-length 头。
    MethodNotAllowed(405),//MethodNotAllowed 指示请求的资源上不允许请求方法（POST 或 GET）。
    Moved(301),//Moved 指示请求的信息已移到 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。原始请求方法为 POST 时，重定向的请求将使用 GET 方法。

    MovedPermanently(301),//MovedPermanently 指示请求的信息已移到 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。

    MultipleChoices(300),//MultipleChoices 指示请求的信息有多种表示形式。默认操作是将此状态视为重定向，并遵循与此响应关联的 Location 头的内容。

    NoContent(204),//NoContent 指示已成功处理请求并且响应已被设定为无内容。
    NonAuthoritativeInformation(203),//NonAuthoritativeInformation 指示返回的元信息来自缓存副本而不是原始服务器，因此可能不正确。
    NotAcceptable(406),//NotAcceptable 指示客户端已用 Accept 头指示将不接受资源的任何可用表示形式。
    NotFound(404),//NotFound 指示请求的资源不在服务器上。
    NotImplemented(501),//NotImplemented 指示服务器不支持请求的函数。
    NotModified(304),//NotModified 指示客户端的缓存副本是最新的。未传输此资源的内容。
    OK(200),//OK 指示请求成功，且请求的信息包含在响应中。这是最常接收的状态代码。
    PartialContent(206),//PartialContent 指示响应是包括字节范围的 GET 请求所请求的部分响应。
    PaymentRequired(402),//保留 PaymentRequired 以供将来使用。
    PreconditionFailed(412),//PreconditionFailed 指示为此请求设置的条件失败，且无法执行此请求。条件是用条件请求标头（如 If-Match、If-None-Match 或 If-Unmodified-Since）设置的。
    ProxyAuthenticationRequired(407),//ProxyAuthenticationRequired 指示请求的代理要求身份验证。Proxy-authenticate 头包含如何执行身份验证的详细信息。
    Redirect(302),//Redirect 指示请求的信息位于 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。原始请求方法为 POST 时，重定向的请求将使用 GET 方法。

    RedirectKeepVerb(307),//RedirectKeepVerb 指示请求信息位于 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。原始请求方法为 POST 时，重定向的请求还将使用 POST 方法。

    RedirectMethod(303),//作为 POST 的结果，RedirectMethod 将客户端自动重定向到 Location 头中指定的 URI。用 GET 生成对 Location 头所指定的资源的请求。

    RequestedRangeNotSatisfiable(416),//RequestedRangeNotSatisfiable 指示无法返回从资源请求的数据范围，因为范围的开头在资源的开头之前，或因为范围的结尾在资源的结尾之后。
    RequestEntityTooLarge(413),//RequestEntityTooLarge 指示请求太大，服务器无法处理。
    RequestTimeout(408),//RequestTimeout 指示客户端没有在服务器期望请求的时间内发送请求。
    RequestUriTooLong(414),//RequestUriTooLong 指示 URI 太长。
    ResetContent(205),//ResetContent 指示客户端应重置（或重新加载）当前资源。
    SeeOther(303),//作为 POST 的结果，SeeOther 将客户端自动重定向到 Location 头中指定的 URI。用 GET 生成对 Location 头所指定的资源的请求。

    ServiceUnavailable(503),//ServiceUnavailable 指示服务器暂时不可用，通常是由于过多加载或维护。
    SwitchingProtocols(101),//SwitchingProtocols 指示正在更改协议版本或协议。
    TemporaryRedirect(307),//TemporaryRedirect 指示请求信息位于 Location 头中指定的 URI 处。接收到此状态时的默认操作为遵循与响应关联的 Location 头。原始请求方法为 POST 时，重定向的请求还将使用 POST 方法。

    Unauthorized(401),//Unauthorized 指示请求的资源要求身份验证。WWW-Authenticate 头包含如何执行身份验证的详细信息。
    UnsupportedMediaType(415),//UnsupportedMediaType 指示请求是不支持的类型。
    Unused(306),//Unused 是未完全指定的 HTTP/1.1 规范的建议扩展。
    UseProxy(305),//UseProxy 指示请求应使用位于 Location 头中指定的 URI 的代理服务器。
    ;

    private int code;

    private StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
