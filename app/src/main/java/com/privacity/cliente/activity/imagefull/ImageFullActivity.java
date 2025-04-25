package com.privacity.cliente.activity.imagefull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.common.BroadcastConstant;

public  class ImageFullActivity extends CustomAppCompatActivity implements ObservadoresPasswordGrupo, View.OnTouchListener {


    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    private ImageView view;
    String idMessageToMap;
    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);

        initActionBar();

        Observers.passwordGrupo().suscribirse(this);
        Bundle b = getIntent().getExtras();

        //Bitmap index = b.getParcelable("imagen");

        idMessageToMap = getIntent().getStringExtra("idMessageToMap");

        view  = (ImageView)findViewById(R.id.iv_imagefull_image);
        //Bitmap bit = convert(imagen);
        view .setImageBitmap(SingletonValues.getInstance().getImagenFull());

        view.setOnTouchListener(this);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY)
                        ||action.equals(BroadcastConstant.BROADCAST__FINISH_IMAGE_FULL_ACTIVITY)
                        ||action.equals(BroadcastConstant.BROADCAST__FINISH_ACTIVITY)
                        || action.equals(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES)) {
                    myFinish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_IMAGE_FULL_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null) return;
        actionBar.setTitle(getString(R.string.imagefull_activity__title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.imagefull_save, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        int id = itemMenu.getItemId();

        if ( id ==R.id.menu_descargar){

            Message m = ObserverMessage.getInstance().getMensajesPorId(idMessageToMap);
//            m.getMedia().isDownloadable()
           Grupo g = ObserverGrupo.getInstance().getGrupoById(m.getIdGrupo());
            if (!m.getMedia().isDownloadable()
            ){

                Toast tost = Toast.makeText(getBaseContext(), getString(R.string.imagefull_activity__validation__download__not_allowed), Toast.LENGTH_SHORT);
                tost.show();

                return true;
            }

            view.buildDrawingCache();
            Bitmap bmap = view.getDrawingCache();

            //guardar imagen
            Save savefile = new Save();
            savefile.SaveImage(this, bmap);

            Toast tost = Toast.makeText(getBaseContext(), getString(R.string.imagefull_activity__validation__download__success), Toast.LENGTH_SHORT);
            tost.show();
        }else {
            myFinish();
        }


        return true;
    }

    public void myFinish() {
        SingletonValues.getInstance().setImagenFull(null);
        Observers.passwordGrupo().remove(this);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String[] names = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
    @Override
    public void finish() {
        super.finish();
        Observers.passwordGrupo().remove(this);
    }
    @Override
    public void passwordExpired(Grupo g) {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())){
            this.finish();
        }
    }

    @Override
    public void passwordSet(Grupo g) {

    }

    @Override
    public void deleteExtraEncrypt(Grupo g) {

    }

    @Override
    public void lock(Grupo g) {

    }
}
