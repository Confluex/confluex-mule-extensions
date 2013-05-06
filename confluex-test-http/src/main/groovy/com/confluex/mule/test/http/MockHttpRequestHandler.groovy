package com.confluex.mule.test.http

import com.confluex.mule.test.http.captor.DefaultRequestCaptor
import com.confluex.mule.test.http.captor.RequestCaptor
import com.confluex.mule.test.http.event.DefaultEventLatch
import com.confluex.mule.test.http.expectations.Expectation
import org.mortbay.jetty.handler.AbstractHandler
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.junit.Assert.*

@Mixin(DefaultEventLatch)
class MockHttpRequestHandler extends AbstractHandler {
    RequestCaptor currentMapping;
    RequestCaptor defaultMapping = new DefaultRequestCaptor()
    Map<String, RequestCaptor> mappings = [:]

    MockHttpRequestHandler when(String uri) {
        currentMapping = new DefaultRequestCaptor()
        mappings[uri] = currentMapping
        return this
    }

    MockHttpRequestHandler thenReturnResource(String path) {
        return thenReturnResource(new ClassPathResource(path))
    }

    MockHttpRequestHandler thenReturnResource(Resource resource) {
        currentMapping.resource = resource
        return this
    }

    MockHttpRequestHandler thenReturnText(String text) {
        currentMapping.text = text
        return this
    }

    MockHttpRequestHandler withStatus(Integer code) {
        currentMapping.status = code
        return this
    }

    MockHttpRequestHandler withHeader(String key, String value) {
        currentMapping.headers[key] = value
        return this
    }

    void handle(String uri, HttpServletRequest request, HttpServletResponse response, int dispatch) {
        addEvent()
        def mapping = mappings[uri] ?: defaultMapping
        mapping.render(request, response)
    }

    MockHttpRequestHandler verify(String uri, Expectation... expectations) {
        def uriRequests = getRequests(uri)
        uriRequests.each { req ->
            expectations.eachWithIndex { expectation, i ->
                if (!expectation.verify(req)) {
                    fail("Expectation: ${expectation} failed for request #${i + 1}: ${req}")
                }
            }
        }
        return this
    }

    List<ClientRequest> getRequests(String uri) {
        return mappings[uri]?.requests ?: defaultMapping.requests
    }

}