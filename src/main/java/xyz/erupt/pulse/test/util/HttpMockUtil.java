package xyz.erupt.pulse.test.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class HttpMockUtil {

    public static void mock(String res, Runnable runnable) {
        try (MockedStatic<HttpUtil> httpUtilMock = Mockito.mockStatic(HttpUtil.class)) {
            HttpRequest mockHttpRequest = Mockito.mock(HttpRequest.class);

            when(mockHttpRequest.header(Mockito.<String>any(), Mockito.any())).thenReturn(mockHttpRequest);
            when(mockHttpRequest.form(anyString(), anyString())).thenReturn(mockHttpRequest);
            when(mockHttpRequest.form(anyString(), (Object) any())).thenReturn(mockHttpRequest);
            when(mockHttpRequest.form(anyString(), any(Long.class))).thenReturn(mockHttpRequest);
            when(mockHttpRequest.form(anyString(), any(File.class))).thenReturn(mockHttpRequest);
            when(mockHttpRequest.body(anyString())).thenReturn(mockHttpRequest);

            httpUtilMock.when(() -> HttpUtil.createPost(anyString())).thenReturn(mockHttpRequest);
            httpUtilMock.when(() -> HttpUtil.createGet(anyString())).thenReturn(mockHttpRequest);
            httpUtilMock.when(() -> HttpUtil.downloadBytes(anyString())).thenReturn("test".getBytes());
            HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
            when(mockHttpRequest.execute()).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getStatus()).thenReturn(200);
            when(mockHttpResponse.body()).thenReturn(res);
            runnable.run();
        }
    }

}
