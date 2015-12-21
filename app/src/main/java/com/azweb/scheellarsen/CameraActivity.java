package com.azweb.scheellarsen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.azweb.scheellarsen.fragments.ShareFragmentDialog;
import com.azweb.scheellarsen.widgets.CameraSurface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thaind on 11/16/2015.
 */
public class CameraActivity extends FragmentActivity implements View.OnClickListener {
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 0.5f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    private CameraActivity mActivity;
    private RelativeLayout mLayout;
    private CameraSurface mCameraSurface;
    private ImageView mBtnCapture;
    private ImageView mBtnSave;
    private ImageView mBtnRefesh;
    private Camera mCamera;
    private ImageView mImageView;
    private ImageView mImageView1;
    private byte[] byteArray;
    private Bitmap mBitmap;
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    private boolean stateTake = false;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private ImageView mBtnBack;
    private ShareFacebookListener shareFacebookListener = new ShareFacebookListener() {
        @Override
        public void accept() {
            loginManager = LoginManager.getInstance();
            List<String> permissionNeeds = Arrays.asList("publish_actions");
            loginManager.logInWithPublishPermissions(mActivity, permissionNeeds);

            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    sharePhotoToFacebook();
                }

                @Override
                public void onCancel() {
                    System.out.println("onCancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    System.out.println("onError");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_camera_layout);
        mActivity = this;
        initViews(savedInstanceState);

    }

    private void sharePhotoToFacebook() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(mBitmap)
                .setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .setContentUrl(Uri.parse("http://www.scheel-larsen.dk/"))
                .addPhoto(photo)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(mActivity,"Deling succes",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mActivity,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    private void initViews(Bundle savedInstanceState) {

        mLayout = (RelativeLayout) findViewById(R.id.llMainContainer);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mBtnBack = (ImageView) findViewById(R.id.btnBack);
        byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mImageView.setImageBitmap(bmp);
        mBtnBack.setOnClickListener(this);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:

                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });
        mCameraSurface = new CameraSurface(mActivity,
                (SurfaceView) findViewById(R.id.surfaceView));
        mCameraSurface.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLayout.addView(mCameraSurface);
        mCameraSurface.setKeepScreenOn(true);

        mBtnCapture = (ImageView) findViewById(R.id.btn_capture);
        mBtnCapture.setOnClickListener(this);
        mBtnRefesh = (ImageView) findViewById(R.id.btn_refesh);
        mBtnRefesh.setOnClickListener(this);
        mBtnSave = (ImageView) findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        float imageWidth = mImageView.getDrawable().getIntrinsicWidth();
        float imageHeight = mImageView.getDrawable().getIntrinsicHeight();
        RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
        RectF viewRect = new RectF(0, 0, mImageView.getWidth(),
                mImageView.getHeight());
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        mImageView.setImageMatrix(matrix);
        mImageView.invalidate();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mImageView1.setVisibility(View.VISIBLE);
            mImageView1.setImageBitmap(handleCrop(bitmap));
            //resetCam();
        }
    };

    private Bitmap handleCrop(Bitmap bitmap) {
        int orientation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        switch (orientation) {
            case 0:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }

    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    private void resetCam() {
        mCamera.startPreview();
        mCameraSurface.setCamera(mCamera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        mActivity.sendBroadcast(mediaScanIntent);
    }

    @SuppressLint("DefaultLocale")
    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/ScheelLarsen/Images");
                dir.mkdirs();

                String fileName = String.format(System.currentTimeMillis() + ".jpg",
                        System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();
                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHandler.postDelayed(updateUI, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {

                mCamera = Camera.open(0);
                surfaceChanged(mActivity);
                mCamera.startPreview();
                mCameraSurface.setCamera(mCamera);
            } catch (RuntimeException ex) {

            }
        }
    }

    public void surfaceChanged(Context context) {

        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_270) {

            mCamera.setDisplayOrientation(180);
        }

    }

    @Override
    public void onPause() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCameraSurface.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                mBtnSave.setVisibility(View.VISIBLE);
                mBtnCapture.setVisibility(View.GONE);
                mBtnRefesh.setVisibility(View.VISIBLE);
                stateTake = true;
                break;
            case R.id.btn_refesh:
                resetCam();
                mImageView1.setVisibility(View.GONE);
                mBtnRefesh.setVisibility(View.GONE);
                mBtnSave.setVisibility(View.GONE);
                mBtnCapture.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_save:
                mImageView1.buildDrawingCache();
                mImageView.buildDrawingCache();
                Bitmap bitmap = mImageView1.getDrawingCache();
                Bitmap bitmap2 = mImageView.getDrawingCache();
                putOverlay(bitmap, bitmap2);
                break;
            case R.id.btnBack:
                finish();

                break;
        }
    }

    public void putOverlay(Bitmap bmp1, Bitmap bmp2) {

        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
                bmp1.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawARGB(0x00, 0, 0, 0);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);

        BitmapDrawable dr = new BitmapDrawable(bmOverlay);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        mBitmap = dr.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        progressBar.setVisibility(View.VISIBLE);
        new SaveImageTask().execute(byteArray);
    }

    private final Runnable updateUI = new Runnable() {
        public void run() {

            progressBar.setVisibility(View.GONE);
            ShareFragmentDialog dialog = ShareFragmentDialog.newInstance();
            dialog.setData(shareFacebookListener);
            dialog.show(getFragmentManager(), "share_facebook");
        }
    };

    public interface ShareFacebookListener {
        public void accept();
    }
}
