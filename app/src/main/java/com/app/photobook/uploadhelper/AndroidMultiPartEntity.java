package com.app.photobook.uploadhelper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@SuppressWarnings("deprecation")
public class AndroidMultiPartEntity extends MultipartEntity{

	private final ProgressListener listener;
	HttpClient httpclient;
	HttpPost httppost;

	public AndroidMultiPartEntity(final ProgressListener listener,HttpClient httpclient,HttpPost httppost) {
		super();
		this.listener = listener;
		this.httpclient  = httpclient;
		this.httppost = httppost;
	}
	
	public AndroidMultiPartEntity(final ProgressListener listener) {
		super();
		this.listener = listener;
	}

	public AndroidMultiPartEntity(final HttpMultipartMode mode,
			final ProgressListener listener) {
		super(mode);
		this.listener = listener;
	}

	public AndroidMultiPartEntity(HttpMultipartMode mode,
			final String boundary, final Charset charset,
			final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener,httpclient,httppost));
	}

	public interface ProgressListener {
		void transferred(long num, HttpClient httpclient, HttpPost httppost);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;
		
		HttpClient httpclient;
		HttpPost httppost;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener,HttpClient httpclient,
		HttpPost httppost) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
			this.httpclient  = httpclient;
			this.httppost = httppost;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred,httpclient,httppost);
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred,httpclient,httppost);
		}
	}
}
