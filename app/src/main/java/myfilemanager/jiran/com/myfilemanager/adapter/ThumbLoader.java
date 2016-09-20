package myfilemanager.jiran.com.myfilemanager.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import myfilemanager.jiran.com.myfilemanager.R;


public class ThumbLoader {
	boolean WEBMODE;
	boolean mVIEW;
	Animation fadeanim;
	String mDATE;

	final int stub_id = R.drawable.stub;
	Context mContext;
	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ThumbLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}
	
	public void DisplayImage(Context context, String url, String date, String orientation, ImageView imageView, Boolean mode, Boolean view) {
		//Log.e("getview loader", url );

		WEBMODE = mode;
		mVIEW = view;
		mDATE = date;
		imageViews.put(imageView, url + mDATE);
		mContext = context;
		fadeanim = AnimationUtils.loadAnimation(mContext, R.anim.image_fade_in_100);

		Bitmap bitmap = memoryCache.get(url + mDATE);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			Log.e("DisplayImage", "memoryCache");
		} else {
			Log.e("DisplayImage", "loadind......");
			queuePhoto(url, mDATE, orientation, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, String date, String ori, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, date, ori, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmapByLocal(String path, String orientation) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bmp;

		options.inSampleSize = 2;
		//Log.d("jiran path",path);
		bmp = BitmapFactory.decodeFile(path);

		/*if (orientation == null) {

		} else {
			try {
				bmp = Bitmap.createScaledBitmap(
						rotate(bmp, Integer.valueOf(orientation)),bmp.getWidth(), bmp.getHeight(), false);
			} catch (Exception e) { // �̹��ڰ� ���� ���
				java.io.InputStream is;
				is = mContext.getResources().openRawResource(R.drawable.file_s_image);
				bmp = BitmapFactory.decodeStream(is);
			}

		}*/

		return bmp;
	}

	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}
	
//	private Bitmap getBitmapByWeb(String url, String date, String ori) {
//		File f = fileCache.getFile(url + date);
//
//		// from SD cache
//		Bitmap b;
//		try {
//			b = BitmapFactory.decodeFile(f.getPath());
//		} catch (Exception e) {
//			InputStream is;
//			is = mContext.getResources().openRawResource(R.drawable.file_s_image);
//			b = BitmapFactory.decodeStream(is);
//		}
//
//		if (b != null){
//			Log.e("DisplayImage", "fileCache......");
//			return b;
//		}
//
//		// from web
//		try {
//			int count;
//
//
//
//			HttpResponse httpResponse;
//			try {
//				httpResponse =  doDown(url);
//
//				int status = httpResponse.getStatusLine().getStatusCode();
//
//				if (status == HttpStatus.SC_OK) {
//
//					InputStream input = httpResponse.getEntity().getContent();
//
//					OutputStream output = new FileOutputStream(f);
//
//					byte data[] = new byte[1024];
//					long total = 0;
//
//					while ((count = input.read(data)) != -1) {
//						//Log.d("jiran ", total +"");
//						total += count;
//						output.write(data, 0, count);
//
//					}
//					output.flush();
//					output.close();
//					input.close();
//				} else if (status == HttpStatus.SC_NOT_FOUND)
//					throw new Exception("");
//				else
//					throw new Exception("");
//			} catch (Exception e) {
//				Log.e("jiran 0",e.toString());
//				return null;
//			}
//
//
//
//
//
//			f = fileCache.getFile(url + date);
//
//
//
//
//
//
//
//
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 1;
//			options.inJustDecodeBounds = false;
//			Bitmap orgImage = BitmapFactory.decodeFile(f.getPath(), options);
//
//			return orgImage;
//		} catch (Throwable ex) {
//			ex.printStackTrace();
//			if (ex instanceof OutOfMemoryError)
//				memoryCache.clear();
//			return null;
//		} finally {
//
//		}
//
//	}
	
	
//	public HttpResponse doDown(String url) {
//		DLog.d("jiran ~", url);
//		HttpResponse httpResponse = null;
//		HttpGet httpget = new HttpGet(url);
//		httpget.setHeader("Connection", "Keep-Alive");
//
//
//		try {
//			HttpClient httpclient = HttpUtils.getClient().getHttpClient();
//
//			HttpContext localContext = new BasicHttpContext();
//			localContext.setAttribute(ClientContext.COOKIE_STORE,HttpUtils.getCookie());
//
//			// 속도가 빨라진다고 함..ㅋㅋ
//			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//			httpclient.getParams().setParameter("http.connection.timeout", 60000);
//			httpclient.getParams().setParameter("http.socket.timeout", 60000);
//
//			httpResponse = httpclient.execute(httpget,localContext);
//
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return httpResponse;
//	}
	
//	public static HttpClient _httpclient = null;
//
//	public HttpClient getNewHttpClient() {
//		try {
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(null, null);
//			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			HttpParams params = new BasicHttpParams();
//			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//			SchemeRegistry registry = new SchemeRegistry();
//			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			registry.register(new Scheme("https", sf, 443));
//			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//			_httpclient = new DefaultHttpClient(ccm, params);
//			return new DefaultHttpClient(ccm, params);
//		} catch (Exception e) {
//			return new DefaultHttpClient();
//		}
//	}
	

	/*private Bitmap getBitmapByWeb(String url, String ori) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b;
		try {
			b = BitmapFactory.decodeFile(f.getPath());
		} catch (Exception e) {
			java.io.InputStream is;
			is = mContext.getResources().openRawResource(R.drawable.file_s_image);
			b = BitmapFactory.decodeStream(is);
		}

		if (b != null)
			return b;
		
		// from web
		try {
			
			
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			
			f = fileCache.getFile(url);
			
			if (ori != null && !ori.equals("0")){ // ori�� ������
				rotateAndSave(f.getPath(), ori);
			}

			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			Bitmap orgImage = BitmapFactory.decodeFile(f.getPath(), options);
			
			return orgImage;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		} finally {

		}
		
	}*/

	
	public void rotateAndSave(String path, String ori) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		int w = options.outWidth;
		int h = options.outHeight;
		options.inSampleSize = 1;
		options.inJustDecodeBounds = false;

		Bitmap src = BitmapFactory.decodeFile(path, options);
		Bitmap inputbmp = rotate(src, Integer.valueOf(ori));


		try {
			FileOutputStream out = new FileOutputStream(path);
			inputbmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}


	public void CopyStream(InputStream is, OutputStream os) { // �̹��� �δ� ī�� ��Ʈ��
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				// Log.w("jiran copy stream", count +"");
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public String date;
		public String orientation;
		public ImageView imageView;

		public PhotoToLoad(String u, String d, String o, ImageView i) {
			url = u;
			date = d;
			orientation = o;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			Bitmap bmp = null;
			if (WEBMODE) { // ���� ����Ʈ
				//bmp = getBitmapByWeb(photoToLoad.url,photoToLoad.date,photoToLoad.orientation );
/*				if (photoToLoad.orientation != null && !photoToLoad.orientation.equals("0")){ // ori�� ������
					Log.e("jiran �����ִ�",photoToLoad.orientation );
					bmp = Bitmap.createScaledBitmap(rotate(bmp, Integer.valueOf(photoToLoad.orientation)), bmp.getHeight(), bmp.getWidth(), false);
				}*/

			} else { // ���ε� ����Ʈ
				bmp = getBitmapByLocal(photoToLoad.url, photoToLoad.orientation);
			}

			memoryCache.put(photoToLoad.url + photoToLoad.date, bmp);

			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(mContext, bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url + photoToLoad.date))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		Context mmContext;

		public BitmapDisplayer(Context c, Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
			mmContext = c;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				if (mVIEW) {

					
					photoToLoad.imageView.startAnimation(fadeanim);
					photoToLoad.imageView.setScaleType(ScaleType.FIT_CENTER);
					photoToLoad.imageView.setImageBitmap(bitmap);
					//photoToLoad.imageView.setLayoutParams(new RelativeLayout.LayoutParams(backW, backH));

				} else {
					if(WEBMODE){
						photoToLoad.imageView.startAnimation(fadeanim);
						photoToLoad.imageView.setImageBitmap(bitmap);	
					} else {
						//photoToLoad.imageView.startAnimation(fadeanim);
						photoToLoad.imageView.setImageBitmap(bitmap);
						photoToLoad.imageView.setScaleType(ScaleType.CENTER_CROP);
					}
				
					
				}

			} else {
				photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
