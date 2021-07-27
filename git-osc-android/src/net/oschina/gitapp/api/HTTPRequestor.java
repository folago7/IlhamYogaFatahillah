package net.oschina.gitapp.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.URLs;

/**
 * gitlabApi网络请求类
 * Gitlab HTTP Requestor
 *
 * Responsible for handling HTTP requests to the Gitlab API
 *
 * @author @timols
 * 最后更新时间：2014-05-16
 * 更新者：火蚁
 */
public class HTTPRequestor {
	
	public static final byte GET_METHOD = 0x00;
	public static final byte POST_METHOD = 0x01;
	public static final byte PUT_METHOD = 0x02;
	public static final byte PATCH_METHOD = 0x03;
	public static final byte DELETE_METHOD = 0x04;
	public static final byte HEAD_METHOD = 0x05;
	public static final byte OPTIONS_METHOD = 0x06;
	public static final byte TRACE_METHOD = 0x07;
	
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	public final static int TIMEOUT_CONNECTION = 20000;// 连接超时时间
	public final static int TIMEOUT_SOCKET = 20000;// socket超时
	
	// 网络请求agent
	private static String appUserAgent;
	
	// 请求client
	private HttpClient _httpClient;
	// 请求方法类型
	private byte _methodType;
    // 请求方法
    private HttpMethod _method;
    //private String _method = "GET"; // 默认用GET方式请求
    private Map<String, Object> _data = new HashMap<String, Object>();// 请求表单参数
    // 是否忽略证书错误
    private boolean isIgnoreCertificateErrors = false;
    
    private enum METHOD {
        GET, PUT, POST, PATCH, DELETE, HEAD, OPTIONS, TRACE;

        public static String prettyValues() {
            METHOD[] methods = METHOD.values();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < methods.length; i++) {
                METHOD method = methods[i];
                builder.append(method.toString());

                if (i != methods.length - 1) {
                    builder.append(", ");
                }
            }
            return builder.toString();
        }
    }
    
    /**
     * Sets the HTTP Request method for the request.
     *
     * Has a fluent api for method chaining.
     *
     * @param   method    The HTTP method
     * @return  this
     */
    public HTTPRequestor init(AppContext appContext, byte methodType, String url) {
    	_methodType = methodType;
        _httpClient = getHttpClient();
        
        String urser_agent = appContext != null ? getUserAgent(appContext) : "";
        _method = getMethod(methodType, url, urser_agent);
        return this;
    }
    
	/**
	 * 获得UserAgent
	 * @param appContext
	 * @return
	 */
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本  
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
    
	/**
	 * 获得一个http连接
	 * @return
	 */
	private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		//httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}
	
	private static HttpMethod getMethod(byte methodType, String url, String userAgent) {
		
		HttpMethod httpMethod = null;
		switch (methodType) {
		case GET_METHOD:
			httpMethod = new GetMethod(url);
			break;
		case POST_METHOD:
			httpMethod = new PostMethod(url);
			break;
		case PUT_METHOD:
			httpMethod = new PutMethod(url);
			break;
		case PATCH_METHOD:
			//httpMethod = new 
			break;
		case DELETE_METHOD:
			httpMethod = new DeleteMethod(url);
			break;
		case HEAD_METHOD:
			httpMethod = new HeadMethod(url);
			break;
		case OPTIONS_METHOD:
			httpMethod = new OptionsMethod(url);
			break;
		case TRACE_METHOD:
			httpMethod = new TraceMethod(url);
			break;
		default:
			break;
		}
		if (null == httpMethod) {
			// 抛出一个不支持的请求方法
			throw new IllegalArgumentException("Invalid HTTP Method: UnKonwn" + ". Must be one of " + METHOD.prettyValues());
		}
		// 设置 请求超时时间
		httpMethod.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpMethod.setRequestHeader("Host", URLs.HOST);
		httpMethod.setRequestHeader("Accept-Encoding", "gzip");
		httpMethod.setRequestHeader("Connection","Keep-Alive");
		httpMethod.setRequestHeader("User-Agent", userAgent);
		return httpMethod;
	}
	
	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		
		try 
		{
			httpClient = getHttpClient();
			httpGet = (GetMethod)getMethod(GET_METHOD, url, "");
			int statusCode = httpClient.executeMethod(httpGet);
			if (statusCode != HttpStatus.SC_OK) {
				throw AppException.http(statusCode);
			}
			
	        InputStream inStream = new ByteArrayInputStream(httpGet.getResponseBody());
	        bitmap = BitmapFactory.decodeStream(inStream);
	        inStream.close();
	        
		} catch (HttpException e) {
			
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			e.printStackTrace();
			throw AppException.http(e);
		} catch (IOException e) {
			
			// 发生网络异常
			e.printStackTrace();
			throw AppException.network(e);
		} finally {
			// 释放连接
			httpGet.releaseConnection();
			httpClient = null;
		}
		return bitmap;
	}

    /**
     * 设置拼接请求的参数
     *
     * @param   key
     * @param   value
     * @return  this
     */
	public HTTPRequestor with(String key, Object value) {
        if (value != null && key != null) {
            _data.put(key, value);
        }
        return this;
    }
    
    public InputStream getResponseBodyStream() throws AppException {
    	
    	// 设置请求参数
    	if (hasOutput()) {
            submitData();
        }
    	
    	InputStream responseBodyStream = null;
		try 
		{
			int statusCode = _httpClient.executeMethod(_method);
			if (statusCode != HttpStatus.SC_OK && !String.valueOf(statusCode).startsWith("2")) {
				throw AppException.http(statusCode);
			}
			Header header = _method.getResponseHeader("Content-Encoding");
			if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
				responseBodyStream = new GZIPInputStream(_method.getResponseBodyAsStream());
			} else {
				responseBodyStream = new ByteArrayInputStream(_method.getResponseBody());
			}
			
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			throw AppException.http(e);
		} catch (IOException e) {
			// 发生网络异常
			throw AppException.network(e);
		} finally {
			// 释放连接
			_method.releaseConnection();
			_httpClient = null;
		}
		return responseBodyStream;
    }
    
    public String getResponseBodyString() throws AppException {
    	
    	// 设置请求参数
    	if (hasOutput()) {
            submitData();
        }
    	
    	String responseBodyString = null;
    	GZIPInputStream gis = null;
		try 
		{
			int statusCode = _httpClient.executeMethod(_method);
			if (statusCode != HttpStatus.SC_OK && !String.valueOf(statusCode).startsWith("2")) {
				throw AppException.http(statusCode);
			}
			Header header = _method.getResponseHeader("Content-Encoding");
			if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
				try {
					gis = new GZIPInputStream(_method.getResponseBodyAsStream());
					responseBodyString = IOUtils.toString(gis);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (gis != null) {
						try {
							gis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				responseBodyString = _method.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			throw AppException.http(e);
		} catch (IOException e) {
			// 发生网络异常
			throw AppException.network(e);
		} finally {
			// 释放连接
			_method.releaseConnection();
			_httpClient = null;
			
		}
		return responseBodyString;
    }

    public <T> T to(T instance) throws AppException {
        return to(null, instance);
    }

    public <T> T to(Class<T> type) throws AppException {
        return to(type, null);
    }

    /**
     * 获取单个对象
     * @param type
     * @param instance
     * @return
     * @throws AppException
     */
    public <T> T to(Class<T> type, T instance) throws AppException {
        
    	InputStream inputStream = getResponseBodyStream();
    	try {
			return parse(inputStream, type, instance);
		} catch (IOException e) {
			throw AppException.io(e);
		}
    }
    
    /**
     * 获取一个列表数据
     * @param type
     * @return
     * @throws AppException
     */
    public <T> List<T> getList(Class<T[]> type) throws AppException {
    	List<T> results = new ArrayList<T>();
    	InputStream inputStream = getResponseBodyStream();
    	if (inputStream == null) {
    		// 抛出一个网络异常
    		throw AppException.http(_method.getStatusLine().getStatusCode());
    	}
		try {
			T[] _next = parse(inputStream, type, null);
			results.addAll(Arrays.asList(_next));
		} catch (IOException e) {
			throw AppException.io(e);
		}
    	
        return results;
    }
    
    /**
     * 表单参数处理
     */
    private void submitData(){
		Part[] parts = new Part[_data.size()];
		int i = 0;
        if(_data != null)
        for(String name : _data.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(_data.get(name)), UTF_8);
        }
    	if (_method instanceof PostMethod) {
    		((PostMethod)_method).setRequestEntity(new MultipartRequestEntity(parts,_method.getParams()));
    	}
    	if (_method instanceof PutMethod) {
    		((PutMethod)_method).setRequestEntity(new MultipartRequestEntity(parts,_method.getParams()));
    	}
    }
    
    /**
     * 判断是否需要设置表单参数
     * @param methodType
     * @return
     */
    private boolean hasOutput() {
    	return _methodType == POST_METHOD || _methodType == PUT_METHOD;
    }
    
    /**
     * 对获取到的网络数据进行处理
     * @param connection
     * @param type
     * @param instance
     * @return
     * @throws IOException
     */
    private <T> T parse(InputStream inputStream, Class<T> type, T instance) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream);
            String data = IOUtils.toString(reader);

            if (type != null) {
                return ApiClient.MAPPER.readValue(data, type);
            } else if (instance != null) {
                return ApiClient.MAPPER.readerForUpdating(instance).readValue(data);
            } else {
                return null;
            }
        } catch (SSLHandshakeException e) {
            throw new SSLHandshakeException("You can disable certificate checking by setting ignoreCertificateErrors on GitlabHTTPRequestor");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * 处理错误异常
     * @param e
     * @param connection
     * @throws Exception 
     *//*
    private AppException handleAPIError(Exception e, InputStream errorInputStream) {
    	AppException exception = null;
    	
    	if (e instanceof FileNotFoundException) {
    		return AppException.file(e);    // pass through 404 Not Found to allow the caller to handle it intelligently
        } else if (e instanceof UnknownHostException || e instanceof ConnectException) {
        	return AppException.network(e);
        }

        InputStream es = null;
		try {
			es = wrapStream(connection, errorInputStream);
			//int code = connection.getResponseCode();
			if (es != null) {
                exception = AppException.io((IOException) new IOException(IOUtils.toString(es, "UTF-8")).initCause(e), code);
            } else {
            	exception = AppException.run(e);
            }
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			IOUtils.closeQuietly(es);
		}
    	return exception;
    }*/
    
    /**
     * 忽略证书错误
     */
    private void ignoreCertificateErrors() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            // Ignore it
        }
    }
}
