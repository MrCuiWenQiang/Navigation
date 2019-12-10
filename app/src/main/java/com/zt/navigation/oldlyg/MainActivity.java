package com.zt.navigation.oldlyg;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private GraphicsLayer mGraphicsLayer;
    private GraphicsLayer hiddenSegmentsLayer;
    private RouteTask mRouteTask = null;
    final SpatialReference wm = SpatialReference.create(4490);
    private RouteResult mResults = null;
    private Route curRoute = null;
    public static ArrayList<String> curDirections = new ArrayList<>();

    private double lat;
    private double lon;

    final Handler mHandler = new Handler();
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        mMapView = (MapView) findViewById(R.id.mapview);

        ArcGISDynamicMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.mapUrl);
        mMapView.addLayer(arcGISTiledMapServiceLayer);

        try {
            mRouteTask =  RouteTask
                    .createOnlineRouteTask(
                            Urls.mapNaviUrl,
                            null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);
        hiddenSegmentsLayer = new GraphicsLayer();
        mMapView.addLayer(hiddenSegmentsLayer);


        query();
        QueryDirections(new Point(119.40169524800001,34.749215468000045),new Point(119.40528823400007,34.746217347000027));

        location();
    }

    private void location() {
        LocationDisplayManager locationDisplayManager = mMapView.getLocationDisplayManager();
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.COMPASS);
        locationDisplayManager.setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String bdlat=location.getLatitude()+"";
                String bdlon=location.getLongitude()+"";
                if (bdlat.indexOf("E")==-1|bdlon.indexOf("E")==-1){
                    //这里做个判断是因为，可能因为gps信号问题，定位出来的经纬度不正常。
                    Log.i("定位",lat+"?"+lon);
                    lat = location.getLatitude();//纬度
                    lon = location.getLongitude();//经度
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
        locationDisplayManager.start();
    }

    protected void onResume() {
        super.onResume();
        mMapView.unpause();
    }

    private void query(){

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float v, float v1) {
                Point point = mMapView.toMapPoint(v, v1);

                AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
                asyncQueryTask.execute(Urls.searchUrl, null, "name LIKE '%0%'");

                asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
                    @Override
                    public void onReturnData(FeatureResult result) {
                        if (result != null) {
                            Iterator<Object> iterator = result.iterator();
                            if (iterator.hasNext()) {
                                Feature feature = (Feature) iterator.next();
                                Map<String, Object> attributes = feature.getAttributes();
                                Symbol symbol = feature.getSymbol();
                                Geometry geometry = feature.getGeometry();
                                printAttributes(attributes);
                                Point point1;
                                if (geometry instanceof Point){
                                    point1 = (Point) geometry;
                                }else {
                                    return;
                                }
                                //自定义高亮显示样式
                                //SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.RED, 3, SimpleLineSymbol.STYLE.SOLID);
                                //SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.parseColor("#ECC8AC"));
                                // fillSymbol.setOutline(lineSymbol);

                                Graphic graphic = new Graphic(geometry, symbol, attributes);

                                //设置高亮显示的颜色
                                SimpleRenderer sr = new SimpleRenderer(new SimpleFillSymbol(Color.RED));
                                mGraphicsLayer.setRenderer(sr);

                                mGraphicsLayer.removeAll();
                                mGraphicsLayer.addGraphic(graphic);

                            }
                        }

                    }
                });
            }
        });

    }

    private void printAttributes(Map<String, Object> attributes) {
        Set<String> set = attributes.keySet();
        for (String key : set) {
            Log.e("xyh", "key==" + key + ";  value==" + attributes.get(key));
        }
    }

        private void QueryDirections(final Point mLocation, final Point p) {


            // Spawn the request off in a new thread to keep UI responsive
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        // Start building up routing parameters
                        RouteParameters rp = mRouteTask
                                .retrieveDefaultRouteTaskParameters();
                        NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();
                        // Convert point to EGS (decimal degrees)
                        // Create the stop points (start at our location, go
                        // to pressed location)
                        StopGraphic point1 = new StopGraphic(mLocation);
                        StopGraphic point2 = new StopGraphic(p);
                        rfaf.setFeatures(new Graphic[] { point1, point2 });
                        rp.setReturnDirections(false);
//                        rfaf.setCompressedRequest(true);
                        rp.setStops(rfaf);
                        // Set the routing service output SR to our map
                        // service's SR
                        rp.setOutSpatialReference(wm);

                        mResults = mRouteTask.solve(rp);
                        mHandler.post(mUpdateResults);
                    } catch (Exception e) {
                       Log.e("error",e.getStackTrace().toString());
                    }
                }
            };
            t.start();
        }
    SimpleLineSymbol segmentHider = new SimpleLineSymbol(Color.WHITE, 5);
    private void updateUI() {
        if (mResults == null) {
            Toast.makeText(MainActivity.this, "......",
                    Toast.LENGTH_LONG).show();
            return;
        }
        curRoute = mResults.getRoutes().get(0);
        SimpleLineSymbol routeSymbol = new SimpleLineSymbol(Color.BLUE, 3);
        PictureMarkerSymbol destinationSymbol = new PictureMarkerSymbol(
                this, getResources().getDrawable(
                R.mipmap.ic_launcher));

        for (RouteDirection rd : curRoute.getRoutingDirections()) {
            HashMap<String, Object> attribs = new HashMap<String, Object>();
            attribs.put("text", rd.getText());
            attribs.put("time", Double.valueOf(rd.getMinutes()));
            attribs.put("length", Double.valueOf(rd.getLength()));
            curDirections.add(String.format("%s%n%.1f minutes (%.1f miles)",
                    rd.getText(), rd.getMinutes(), rd.getLength()));
            Graphic routeGraphic = new Graphic(rd.getGeometry(), segmentHider,
                    attribs);
            hiddenSegmentsLayer.addGraphic(routeGraphic);
        }

        Graphic routeGraphic = new Graphic(curRoute.getRouteGraphic()
                .getGeometry(), routeSymbol);
        Graphic endGraphic = new Graphic(
                ((Polyline) routeGraphic.getGeometry()).getPoint(((Polyline) routeGraphic
                        .getGeometry()).getPointCount() - 1), destinationSymbol);
        mGraphicsLayer.addGraphics(new Graphic[] { routeGraphic, endGraphic });
        mMapView.setExtent(curRoute.getEnvelope(), 250);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }
}
