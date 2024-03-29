package com.example.exercise.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exercise.GestureActivity;
import com.example.exercise.LogManager;
import com.example.exercise.MainActivity;
import com.example.exercise.Page4;
import com.example.exercise.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;
import com.skt.Tmap.BizCategory;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.BizCategoryListenerCallback;
import com.skt.Tmap.TMapData.ConvertGPSToAddressListenerCallback;
import com.skt.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skt.Tmap.TMapData.FindAroundNamePOIListenerCallback;
import com.skt.Tmap.TMapData.FindPathDataAllListenerCallback;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapLabelInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapView.MapCaptureImageListenerCallback;
import com.skt.Tmap.TMapView.TMapLogoPositon;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class MapActivity extends BaseActivity implements onLocationChangedCallback
{
	PermissionManager permissionManager = null; // 권한요청 관리자

	//TMapGpsManager Tmapgps = new TMapGpsManager(this);

	@Override
	public void onLocationChange(Location location) {

		LogManager.printLog("onLocationChange :::> " + location.getLatitude() +  " " + location.getLongitude() + " " + location.getSpeed() + " " + location.getAccuracy());
		if(m_bTrackingMode) 
		{
			mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
		}
	}

	private TMapView		mMapView = null;
	
	private Context 		mContext;
	private ArrayList<Bitmap> mOverlayList;
	private ImageOverlay mOverlay;

	public static String mApiKey = "32e67b95-524c-44c7-93ec-d51f7efb67dd"; // 발급받은 SKT AppKey


	private static final int[] mArrayMapButton2 = {
		R.id.btnOverlay,
		R.id.btnAnimateTo,
		R.id.btnZoomIn,
		R.id.btnZoomOut,
		R.id.btnGetZoomLevel,
		R.id.btnSetZoomLevel,
		R.id.btnSetMapType,
		R.id.btnGetLocationPoint,
		R.id.btnSetLocationPoint,
		R.id.btnSetIcon,
		R.id.btnSetCompassMode,
		R.id.btnGetIsCompass,
		R.id.btnSetSightVisible,
		R.id.btnSetTrackIngMode,
		R.id.btnGetIsTracking,
		R.id.btnAddTMapCircle,
		R.id.btnRemoveTMapCircle, 
		R.id.btnMarkerPoint,
		R.id.btnRemoveMarker,
		R.id.btnMoveFrontMarker,
		R.id.btnMoveBackMarker,
		R.id.btnDrawPolyLine,
		R.id.btnErasePolyLine,		
		R.id.btnDrawPolygon,
		R.id.btnErasePolygon,
		R.id.btnMapPath, 
		R.id.btnRemoveMapPath,
		R.id.btnDisplayMapInfo,
		R.id.btnNaviGuide,
		R.id.btnCarPath,
		R.id.btnPedestrian_Path,
		R.id.btnGetCenterPoint,
		R.id.btnFindAllPoi,
		R.id.btnConvertToAddress,
		R.id.btnGetAroundBizPoi,
		R.id.btnTileType,
		R.id.btnCapture,
		R.id.btnDisalbeZoom,
		R.id.btnInvokeRoute,
		R.id.btnInvokeSetLocation,
		R.id.btnInvokeSearchPortal, 
		R.id.btnTimeMachine, 
		R.id.btnTMapInstall,
		R.id.btnMarkerPoint2,
	};

	private 	int 		m_nCurrentZoomLevel = 0;
	private 	double 		m_Latitude  = 0;
	private     double  	m_Longitude = 0;
	private 	boolean 	m_bShowMapIcon = false;

	private 	boolean 	m_bTrafficeMode = false;
	private 	boolean 	m_bSightVisible = false;

	//트렉킹 시작
	private 	boolean 	m_bTrackingMode = false;
	
	private 	boolean 	m_bOverlayMode = false;
	
	ArrayList<String>		mArrayID;
	
	ArrayList<String>		mArrayCircleID;
	private static 	int 	mCircleID;
	
	ArrayList<String>		mArrayLineID;
	private static 	int 	mLineID;
	
	ArrayList<String>		mArrayPolygonID;
	private static  int 	mPolygonID;

	ArrayList<String>       mArrayMarkerID;
	private static int 		mMarkerID;

	//private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<>();
	//private ArrayList<MapPoint2> m_mapPoint2 = new ArrayList<>();

	TMapGpsManager gps = null;



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		permissionManager.setResponse(requestCode, grantResults); // 권한요청 관리자에게 결과 전달
	}


	/**
	 * onCreate() 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		mContext = this;

		permissionManager = new PermissionManager(this); // 권한요청 관리자

		//RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
		//relativeLayout.addView(mMapView);

		mMapView = new TMapView(this);
		addView(mMapView);


		configureMapView();

		initView();
		
		mArrayID = new ArrayList<String>();

		mArrayCircleID = new ArrayList<String>();
		mCircleID = 0;

		mArrayLineID = new ArrayList<String>();
		mLineID = 0;

		mArrayPolygonID = new ArrayList<String>();
		mPolygonID = 0;

		mArrayMarkerID	= new ArrayList<String>();
		mMarkerID = 0;

		permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
			@Override
			public void granted() {
				gps = new TMapGpsManager(MapActivity.this);
				gps.setMinTime(1000);
				gps.setMinDistance(5);
				gps.setProvider(gps.GPS_PROVIDER);
				gps.OpenGps();
				//TMapPoint pointpresent = gps.getLocation();
				gps.setProvider(gps.NETWORK_PROVIDER);
				gps.OpenGps();
			}

			@Override
			public void denied() {
				Log.w("LOG", "위치정보 접근 권한이 필요합니다.");
			}
		});

		mMapView.setTMapLogoPosition(TMapLogoPositon.POSITION_BOTTOMRIGHT);

		//addPoint();


		mMapView.setCompassMode(true);
		mMapView.setIconVisibility(true);

		mMapView.setTrackingMode(true);
		mMapView.setSightVisible(true);
		//findAllPoi();


		//showMarkerPoint();

		//getLocationPoint();
		//setLocationPoint();

	}

	//public void addPoint(){

		//m_mapPoint2.add(new MapPoint2("서울시립대학교",37.51035,127.066847));
	//}


    public void clicksearch(View v){
		findAllPoi();


		//MaterialTextView Text1 = (MaterialTextView) findViewById(R.id.resultbox);
		//Text1.setText("서울시립대학교");
		//TMapPOIItem item1=new TMapPOIItem();
		//String name1;
		//String name2;
		//Intent intent = getIntent();
		//name2=item1.getPOIName();
//		name1=intent.getExtras().getString("poiname");
		//MaterialAutoCompleteTextView textview1 = findViewById(R.id.resultbox);
		//
		// textview1.setText(name2);

		//"POI Name: " + item.getPOIName().toString()
		//Intent intent = new Intent(getApplicationContext(), Page4.class);
        //startActivity(intent);
    }
    public void clickTracking(View v){
		setTrackingMode();
		getIsTracking();
		convertToAddress();
		//TextInputEditText editText1 = (TextInputEditText)findViewById(R.id.nowPosition);
		//editText1.setText("서울시립대학교") ;
	}

	/**
	 * setSKTMapApiKey()에 ApiKey를 입력 한다.
	 */
	private void configureMapView() {
		mMapView.setSKTMapApiKey(mApiKey);
	}

	/**
	 * initView - 버튼에 대한 리스너를 등록한다. 
	 */
	private void initView() {

		//Button ViewButton = (Button)findViewById(R.id.btnTracking);
		//ViewButton.setOnClickListener(this);

		//for (int btnMapView : mArrayMapButton) {
			//Button ViewButton = (Button)findViewById(btnMapView);
			//ViewButton.setOnClickListener(this);
		//}

		mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
			@Override
			public void SKTMapApikeySucceed() {
				LogManager.printLog("MapActivity SKTMapApikeySucceed");
			}
			
			@Override
			public void SKTMapApikeyFailed(String errorMsg) {
				LogManager.printLog("MapActivity SKTMapApikeyFailed " + errorMsg);
			}
		});

		
		mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
			@Override
			public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
				LogManager.printLog("MapActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
			}
		});

		mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
			@Override
			public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
				LogManager.printLog("MapActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
			}
		});
		
		mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
			@Override
			public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
				LogManager.printLog("MapActivity onPressUpEvent " + markerlist.size());
				return false;
			}
			
			@Override
			public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
				LogManager.printLog("MapActivity onPressEvent " + markerlist.size());

				for (int i = 0; i < markerlist.size(); i++) {
					TMapMarkerItem item = markerlist.get(i);
					LogManager.printLog("MapActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
				}
				return false;
			}
		});
		
		mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
			@Override
			public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point) {
				LogManager.printLog("MapActivity onLongPressEvent " + markerlist.size());
			}
		});
		
		mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
			@Override
			public void onCalloutRightButton(TMapMarkerItem markerItem) {
				String strMessage = "";
				strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
				Common.showAlertDialog(MapActivity.this, "Callout Right Button", strMessage);
			}
		});
		
		mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
			@Override
			public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
				if(findReverseLabel != null) {
					LogManager.printLog("MapActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat
							 + " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName);

				}
			}
		});
		
		m_nCurrentZoomLevel = -1;
		m_bShowMapIcon = false;
		m_bTrafficeMode = false;
		m_bSightVisible = false;
		m_bTrackingMode = false;	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( gps != null ) {
			gps.CloseGps();
		}
		if(mOverlayList != null){
			mOverlayList.clear();
		}
	}
	
	/**
	 * onClick Event 
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			//case R.id.btnTracking  : 	setTrackingMode();		break;
		case R.id.btnOverlay		  : 	overlay(); 				break;
		case R.id.btnAnimateTo		  : 	animateTo(); 			break;
		case R.id.btnZoomIn			  : 	mapZoomIn(); 			break;
		case R.id.btnZoomOut		  : 	mapZoomOut(); 			break;
		case R.id.btnGetZoomLevel	  :  	getZoomLevel(); 		break;
		case R.id.btnSetZoomLevel	  :  	setZoomLevel(); 		break;
		case R.id.btnSetMapType		  :		setMapType(); 			break;
		case R.id.btnGetLocationPoint : 	getLocationPoint(); 	break;	
		case R.id.btnSetLocationPoint : 	setLocationPoint(); 	break;
		case R.id.btnSetIcon		  : 	setMapIcon(); 			break;
		case R.id.btnSetCompassMode	  : 	setCompassMode();		break;
		case R.id.btnGetIsCompass     :		getIsCompass();			break;
		case R.id.btnSetSightVisible  : 	setSightVisible();		break;
		case R.id.btnSetTrackIngMode  : 	setTrackingMode();		break;
		case R.id.btnGetIsTracking	  : 	getIsTracking();		break;
		case R.id.btnAddTMapCircle	  : 	addTMapCircle();		break;
		case R.id.btnRemoveTMapCircle : 	removeTMapCircle();		break;
		case R.id.btnMarkerPoint	  :     showMarkerPoint(); 		break;
		case R.id.btnRemoveMarker     : 	removeMarker(); 		break;
		case R.id.btnMoveFrontMarker  :     moveFrontMarker(); 		break;
		case R.id.btnMoveBackMarker   :     moveBackMarker();		break;
		case R.id.btnDrawPolyLine     :     drawLine();			 	break;
		case R.id.btnErasePolyLine	  : 	erasePolyLine();		break;
		case R.id.btnDrawPolygon	  : 	drawPolygon(); 			break;
		case R.id.btnErasePolygon     :     removeTMapPolygon(); 	break;
		case R.id.btnMapPath		  : 	drawMapPath();			break;
		case R.id.btnRemoveMapPath    :     removeMapPath(); 		break;
		case R.id.btnDisplayMapInfo   :     displayMapInfo(); 		break;		
		case R.id.btnNaviGuide		  :     naviGuide();			break;				
		case R.id.btnCarPath		  :     drawCarPath(); 			break;
		case R.id.btnPedestrian_Path  :     drawPedestrianPath();   break;
		case R.id.btnGetCenterPoint   :     getCenterPoint();		break;
		case R.id.btnFindAllPoi		  :     findAllPoi();			break;
		case R.id.btnConvertToAddress :     convertToAddress(); 	break;
		case R.id.btnGetAroundBizPoi  :     getAroundBizPoi(); 		break;
		case R.id.btnTileType		  : 	setTileType();			break;
		case R.id.btnInvokeRoute	  :     invokeRoute();			break;
		case R.id.btnInvokeSetLocation: 	invokeSetLocation();    break;
		case R.id.btnInvokeSearchPortal: 	invokeSearchProtal(); 	break;
		case R.id.btnCapture		  :     captureImage(); 		break;
		case R.id.btnDisalbeZoom	  : 	disableZoom();			break;
		case R.id.btnTimeMachine	  :   	timeMachine(); 			break;
		case R.id.btnTMapInstall	  :     tmapInstall(); 			break;
		case R.id.btnMarkerPoint2	  :     showMarkerPoint2(); 	break;
		}
	} 
	
	public TMapPoint randomTMapPoint() {
		double latitude = ((double)Math.random() ) * (37.575113-37.483086) + 37.483086;
	    double longitude = ((double)Math.random() ) * (127.027359-126.878357) + 126.878357;    

	    latitude = Math.min(37.575113, latitude);
	    latitude = Math.max(37.483086, latitude);
	    
	    longitude = Math.min(127.027359, longitude);
	    longitude = Math.max(126.878357, longitude);

	    LogManager.printLog("randomTMapPoint" + latitude + " " + longitude);
	    
		TMapPoint point = new TMapPoint(latitude, longitude);
		
		return point;
	}
	
	public void overlay() {
		m_bOverlayMode = !m_bOverlayMode;
		if(m_bOverlayMode) {
			mMapView.setZoomLevel(6);
			
			if(mOverlay == null){
				mOverlay = new ImageOverlay(this, mMapView);
			}
			
			mOverlay.setLeftTopPoint(new TMapPoint(45.640171, 114.9652948));
			mOverlay.setRightBottomPoint(new TMapPoint(29.2267177, 138.7206798));
			mOverlay.setImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test_image));
			
			if(mOverlayList == null){
				mOverlayList = new ArrayList<Bitmap>();
				mOverlayList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test_image));
				mOverlayList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ani1));
				mOverlayList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ani2));
			}
			
			mOverlay.setAnimationIcons(mOverlayList);
			mOverlay.setAniDuration(10000);
			mOverlay.startAnimation();
			mMapView.addTMapOverlayID(0, mOverlay);
		} else {
			mOverlay.stopAnimation();
			mMapView.removeTMapOverlayID(0);
		}
	}
	
	public void animateTo() {
		TMapPoint point = randomTMapPoint();
		mMapView.setCenterPoint(point.getLongitude(), point.getLatitude(), true);
	}

	public Bitmap overlayMark(Bitmap bmp1, Bitmap bmp2, int width, int height) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
		
		int marginLeft = 7;
		int marginTop = 5;

		if(width >= 1500 || height > 1500) {
			bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 40, bmp1.getHeight() - 50, true);
			marginLeft = 20;
			marginTop = 10;
		} else if(width >= 1200 || height > 1200) {
			bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 22, bmp1.getHeight() - 35, true);
			marginLeft = 11;
			marginTop = 7;
		} else {
			bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 15, bmp1.getHeight() - 25, true);
		}
		
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, 0, 0, null);
		canvas.drawBitmap(bmp2, marginLeft, marginTop, null);
		return bmOverlay;
	}
	
	/**
	 * mapZoomIn
	 * 지도를 한단계 확대한다. 
	 */
	public void mapZoomIn() {
		mMapView.MapZoomIn();   
	}
	
	/**
	 * mapZoomOut
	 * 지도를 한단계 축소한다. 
	 */
	public void mapZoomOut() {
		mMapView.MapZoomOut();
	}
	
	/**
	 * getZoomLevel
	 * 현재 줌의 레벨을 가지고 온다. 
	 */
	public void getZoomLevel() {
		int nCurrentZoomLevel = mMapView.getZoomLevel();
		Common.showAlertDialog(this, "", "현재 Zoom Level : " + Integer.toString(nCurrentZoomLevel));
	}
	
	/**
	 * setZoomLevel
	 * Zoom Level을 설정한다. 
	 */
	public void setZoomLevel() {
    	final String[] arrString = getResources().getStringArray(R.array.a_zoomlevel);
		AlertDialog dlg = new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Select Zoom Level")
			.setSingleChoiceItems(R.array.a_zoomlevel, m_nCurrentZoomLevel, new DialogInterface.OnClickListener() {						
				@Override
				public void onClick(DialogInterface dialog, int item) {							
					m_nCurrentZoomLevel = item;
					dialog.dismiss();
					mMapView.setZoomLevel(Integer.parseInt(arrString[item]));					
				}
			}).show();		
    }
    
    /**
     * seetMapType  
     * Map의 Type을 설정한다.
     */
	public void setMapType() {
    	AlertDialog dlg = new AlertDialog.Builder(this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle("Select MAP Type")
		.setSingleChoiceItems(R.array.a_maptype, -1, new DialogInterface.OnClickListener() {						
			@Override
			public void onClick(DialogInterface dialog, int item) {							
				LogManager.printLog("Set Map Type " + item);
				dialog.dismiss();

				mMapView.setMapType(item);
			}
		}).show();		
    }
    
    /**
     * getLocationPoint
     * 현재위치로 표시될 좌표의 위도, 경도를 반환한다. 
     */
	public void getLocationPoint() {
		//TMapPoint point = mMapView.getLocationPoint();
		TMapPoint point = gps.getLocation();
		double Latitude = point.getLatitude();
		double Longitude = point.getLongitude();
		
		m_Latitude  = Latitude;
		m_Longitude = Longitude;
		
		LogManager.printLog("Latitude " + Latitude + " Longitude " + Longitude);
		
		String strResult = String.format("Latitude = %f Longitude = %f", Latitude, Longitude);
		
		Common.showAlertDialog(this, "", strResult);
	}
	
	/**
	 * setLocationPoint
	 * 현재위치로 표시될 좌표의 위도,경도를 설정한다. 
	 */
	public void setLocationPoint() {
		//ouble 	Latitude  = 37.58396081597457;
		//double  Longitude = 127.05902532617992;

		double Latitude = m_Latitude;
		double Longitude = m_Longitude;
		
		LogManager.printLog("setLocationPoint " + Latitude + " " + Longitude);
		
		mMapView.setLocationPoint(Latitude, Longitude);
	}
	
	/**
	 * setMapIcon
	 * 현재위치로 표시될 아이콘을 설정한다. 
	 */
	public void setMapIcon() {
		m_bShowMapIcon = !m_bShowMapIcon;

		if (m_bShowMapIcon) {
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_launcher);
			mMapView.setIcon(bitmap);
		}
		mMapView.setIconVisibility(m_bShowMapIcon);
	}
	
	/**
	 * setCompassMode
	 * 단말의 방항에 따라 움직이는 나침반모드로 설정한다. 
	 */
	public void setCompassMode() {
		mMapView.setCompassMode(!mMapView.getIsCompass());
	}
	
	/**
	 * getIsCompass
	 * 나침반모드의 사용여부를 반환한다. 
	 */
	public void getIsCompass() {
		Boolean bGetIsCompass = mMapView.getIsCompass();
		Common.showAlertDialog(this, "", "현재 나침반 모드는 : " + bGetIsCompass.toString() );
	}
	
	/**
	 * setSightVisible
	 * 시야표출여부를 설정한다. 
	 */
	public void setSightVisible() {
		m_bSightVisible = !m_bSightVisible;
		mMapView.setSightVisible(m_bSightVisible);
	}
	
	/**
	 * setTrackingMode
	 * 화면중심을 단말의 현재위치로 이동시켜주는 트래킹모드로 설정한다. 
	 */
	public void setTrackingMode() {
		m_bTrackingMode = !m_bTrackingMode;
		mMapView.setTrackingMode(m_bTrackingMode);
	}
	
	/**
	 * getIsTracking
	 * 트래킹모드의 사용여부를 반환한다. 
	 */
	public void getIsTracking() {
		Boolean bIsTracking = mMapView.getIsTracking();
		LogManager.printLog("현재 트래킹모드 사용 여부  : " + bIsTracking.toString());
		//Common.showAlertDialog(this, "", "현재 트래킹모드 사용 여부  : " + bIsTracking.toString() );
	}
	
	/**
	 * addTMapCircle()
	 * 지도에 서클을 추가한다. 
	 */
	public void addTMapCircle() {
		TMapCircle circle = new TMapCircle();
		
		circle.setRadius(300);
		circle.setLineColor(Color.BLUE);
		circle.setAreaAlpha(50);
		circle.setCircleWidth((float)10);
		circle.setRadiusVisible(true);
		
		TMapPoint point = randomTMapPoint();
		circle.setCenterPoint(point);
		
		String strID = String.format("circle%d", mCircleID++);
		mMapView.addTMapCircle(strID, circle);
		mArrayCircleID.add(strID);
	}
	
	/**
	 * removeTMapCircle
	 * 지도상의 해당 서클을 제거한다. 
	 */
	public void removeTMapCircle() {
		if(mArrayCircleID.size() <= 0 )
			return;
		
		String strCircleID = mArrayCircleID.get(mArrayCircleID.size() - 1 );
		mMapView.removeTMapCircle(strCircleID);
		mArrayCircleID.remove(mArrayCircleID.size() - 1);
	}
	
	public void showMarkerPoint2() {
		ArrayList<Bitmap> list = null;
		for(int i = 0; i < 50; i++) {
			
			MarkerOverlay marker1 = new MarkerOverlay(this, mMapView);
			String strID = String.format("%02d", i);
			
			marker1.setID(strID);
			marker1.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red));		
			marker1.setTMapPoint(randomTMapPoint());
			
			if (list == null) {
				 list = new ArrayList<Bitmap>();
			}
			
			list.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.map_pin_red));
			list.add(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.end));
			
			marker1.setAnimationIcons(list);
			mMapView.addMarkerItem2(strID, marker1);
		}
				
		mMapView.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
			
			@Override
			public void onCalloutMarker2ClickEvent(String id, TMapMarkerItem2 markerItem2) {
				LogManager.printLog("ClickEvent " + " id " + id + " " + markerItem2.latitude + " " +  markerItem2.longitude);
				
				String strMessage = "ClickEvent " + " id " + id + " " + markerItem2.latitude + " " +  markerItem2.longitude;
				
				Common.showAlertDialog(MapActivity.this, "TMapMarker2", strMessage);
			}
		});
	}
	
	/**
	 * showMarkerPoint
	 * 지도에 마커를 표출한다. 
	 */
	public void showMarkerPoint() {
		//findAllPoi();

		Bitmap bitmap = null;
		//getLocationPoint();
		//setLocationPoint();
		//TMapPoint point = new TMapPoint(m_Latitude, m_Longitude);

		TMapPoint point = new TMapPoint(37.58395362, 127.05901319);
		//TMapPoint point = new TMapPoint(37.566474, 126.985022);
		TMapMarkerItem item1 = new TMapMarkerItem();
		
		bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.i_location);
				
		item1.setTMapPoint(point);
		//item1.setName("SKT타워");
		item1.setName("서울시립대학교");
		item1.setVisible(item1.VISIBLE);

		item1.setIcon(bitmap);
		LogManager.printLog("bitmap " + bitmap.getWidth() + " " + bitmap.getHeight());
		
		bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.i_location);		
		item1.setCalloutTitle("서울시립대학교");
		item1.setCalloutSubTitle("서울시립대학교 정문");
		item1.setCanShowCallout(true);
		item1.setAutoCalloutVisible(true);
		
		Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_go);

		item1.setCalloutRightButtonImage(bitmap_i);

		String strID = String.format("pmarker%d", mMarkerID++);
		
		mMapView.addMarkerItem(strID, item1);
		mArrayMarkerID.add(strID);
		///////////

		///////////////
		point = new TMapPoint(37.55102510077652, 126.98789834976196);
		TMapMarkerItem item2 = new TMapMarkerItem();

		item2.setTMapPoint(point);
		item2.setName("N서울타워");
		item2.setVisible(item2.VISIBLE);
		item2.setCalloutTitle("청호타워 4층");
		
		item2.setCanShowCallout(true);
		
		bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_go);		
		item2.setCalloutRightButtonImage(bitmap_i);
				
		bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.pin_tevent);
		item2.setIcon(bitmap);

		strID = String.format("pmarker%d", mMarkerID++);
		
		mMapView.addMarkerItem(strID, item2);
		mArrayMarkerID.add(strID);
		
		
		point = new TMapPoint(37.58102510077652, 126.98789834976196);
		item2 = new TMapMarkerItem();

		item2.setTMapPoint(point);
		item2.setName("N서울타워");
		item2.setVisible(item2.VISIBLE);
		item2.setCalloutTitle("창덕궁 청호타워 4층");
		
		item2.setCalloutSubTitle("을지로입구역 500M");
		item2.setCanShowCallout(true);
		
			
		bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_go);		
		item2.setCalloutRightButtonImage(bitmap_i);
				
		bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.map_pin_red);
		item2.setIcon(bitmap);

		strID = String.format("pmarker%d", mMarkerID++);
		
		mMapView.addMarkerItem(strID, item2);
		mArrayMarkerID.add(strID);
				
		point = new TMapPoint(37.58102510077652, 126.99789834976196);
		item2 = new TMapMarkerItem();

		item2.setTMapPoint(point);
		item2.setName("N서울타워");
		item2.setVisible(item2.VISIBLE);
		item2.setCalloutTitle("대학로 혜화역111111");
				
		item2.setCanShowCallout(true);
				
		item2.setCalloutLeftImage(bitmap);
		
		bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_go);		
		item2.setCalloutRightButtonImage(bitmap_i);
				
		
		bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.end);
		item2.setIcon(bitmap);

		strID = String.format("pmarker%d", mMarkerID++);
		
		mMapView.addMarkerItem(strID, item2);
		mArrayMarkerID.add(strID);
	
		for(int i = 4; i < 10; i++) {
			TMapMarkerItem item3 = new TMapMarkerItem();
			
			item3.setID(strID);
			item3.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red));

			item3.setTMapPoint(randomTMapPoint());
			item3.setCalloutTitle(">>>>" + strID + "<<<<<");
			item3.setCanShowCallout(true);
			
			strID = String.format("pmarker%d", mMarkerID++);
			
			mMapView.addMarkerItem(strID, item2);
			mArrayMarkerID.add(strID);
		}
	}
	
	public void removeMarker() {
		if(mArrayMarkerID.size() <= 0 )
			return;
		
		String strMarkerID = mArrayMarkerID.get(mArrayMarkerID.size() - 1);
		mMapView.removeMarkerItem(strMarkerID);
		mArrayMarkerID.remove(mArrayMarkerID.size() - 1);
	}
	
	/**
	 * moveFrontMarker
	 * 마커를 맨 앞으로 표시 하도록 한다. 
	 * showMarkerPoint() 함수를 먼저 클릭을 한 후, 클릭을 해야 함.
	 */
	public void moveFrontMarker() {
		TMapMarkerItem item = mMapView.getMarkerItemFromID("1");
		mMapView.bringMarkerToFront(item);
	}
	
	/**
	 * moveBackMarker
	 * 마커를 맨 뒤에 표시하도록 한다. 
	 * showMarkerPoint() 함수를 먼저 클릭을 한 후, 클릭을 해야 함.
	 */
	public void moveBackMarker() {
		TMapMarkerItem item = mMapView.getMarkerItemFromID("1");
		mMapView.sendMarkerToBack(item);
	}
	
	/**
	 * drawLine
	 * 지도에 라인을 추가한다. 
	 */
	public void drawLine() {
		TMapPolyLine polyLine = new TMapPolyLine();
		polyLine.setLineColor(Color.BLUE);
		polyLine.setLineWidth(5);

		for (int i = 0; i < 5; i++) {
			TMapPoint point = randomTMapPoint();
			polyLine.addLinePoint(point);
		}
		
		String strID = String.format("line%d", mLineID++);
		mMapView.addTMapPolyLine(strID, polyLine);
		mArrayLineID.add(strID);
	}
	
	/**
	 * erasePolyLine
	 * 지도에 라인을 제거한다. 
	 */
	public void erasePolyLine() {
		if(mArrayLineID.size() <= 0)
			return;
		
		String strLineID = mArrayLineID.get(mArrayLineID.size() - 1 );
		mMapView.removeTMapPolyLine(strLineID);
		mArrayLineID.remove(mArrayLineID.size() - 1);
	}

	/**
	 * drawPolygon
	 * 지도에 폴리곤에 그린다. 
	 */
	public void drawPolygon() {		
		int Min = 3;
		int Max = 10;
		int rndNum = (int)(Math.random() * ( Max - Min ));
		
		LogManager.printLog("drawPolygon" + rndNum);
		
		TMapPolygon polygon = new TMapPolygon();
		polygon.setLineColor(Color.BLUE);
		polygon.setPolygonWidth((float)4);
		polygon.setAreaAlpha(2);
		    
		TMapPoint point = null;
		
		if (rndNum < 3) {
			rndNum = rndNum + (3 - rndNum);
		}

		for (int i = 0; i < rndNum; i++) {
			point = randomTMapPoint(); 
			polygon.addPolygonPoint(point);
		}
				
		String strID = String.format("polygon%d", mPolygonID++);
		mMapView.addTMapPolygon(strID, polygon);
		mArrayPolygonID.add(strID);
	}
	
	/**
	 * erasePolygon
	 * 지도에 그려진 폴리곤을 제거한다. 
	 */
	public void removeTMapPolygon() {
		if(mArrayPolygonID.size() <= 0)
			return;
		
		String strPolygonID = mArrayPolygonID.get(mArrayPolygonID.size() - 1 );
		
		LogManager.printLog("erasePolygon " + strPolygonID);
		
		mMapView.removeTMapPolygon(strPolygonID);
		mArrayPolygonID.remove(mArrayPolygonID.size() - 1);
	}

	/**
	 * drawMapPath
	 * 지도에 시작-종료 점에 대해서 경로를 표시한다. 
	 */
	public void drawMapPath() {
		TMapPoint point1 = mMapView.getLocationPoint();
		TMapPoint point2 = randomTMapPoint();
		
		TMapData tmapdata = new TMapData();
			
		tmapdata.findPathData(point1, point2, new FindPathDataListenerCallback() {
			
			@Override
			public void onFindPathData(TMapPolyLine polyLine) {
				mMapView.addTMapPath(polyLine);
			}
		});
	}
	
	private String getContentFromNode(Element item, String tagName){
		NodeList list = item.getElementsByTagName(tagName);
		if (list.getLength() > 0) {
			if (list.item(0).getFirstChild() != null) {
				return list.item(0).getFirstChild().getNodeValue();
			}
		}
		return null;
	}
	
	/**
	 * displayMapInfo()
	 * POI들이 모두 표시될 수 있는 줌레벨 결정함수와 중심점리턴하는 함수
	 */
	public void displayMapInfo() {	
		/*
		TMapPoint point1 = mMapView.getCenterPoint();		
		TMapPoint point2 = randomTMapPoint();
		*/
		TMapPoint point1 = new TMapPoint(37.541642248630524, 126.99599611759186);
		TMapPoint point2 = new TMapPoint(37.541243493556976, 126.99659830331802);
		TMapPoint point3 = new TMapPoint(37.540909826755524, 126.99739581346512);
		TMapPoint point4 = new TMapPoint(37.541080713272095, 126.99874675273895);
					
		ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
		
		point.add(point1);
		point.add(point2);
		point.add(point3);
		point.add(point4);
		
		TMapInfo info = mMapView.getDisplayTMapInfo(point);
		
		String strInfo = "Center Latitude" + info.getTMapPoint().getLatitude() + "Center Longitude" + info.getTMapPoint().getLongitude() + 
						"Level " + info.getTMapZoomLevel();
		
		Common.showAlertDialog(this, "", strInfo );
	}
	
	/**
	 * removeMapPath
	 * 경로 표시를 삭제한다. 
	 */
	public void removeMapPath() {	
		mMapView.removeTMapPath();
	}
	
	/**
	 * naviGuide
	 * 길안내 
	 */
	public void naviGuide() {		
		TMapPoint point1 = mMapView.getCenterPoint();
		TMapPoint point2 = randomTMapPoint();
		
		TMapData tmapdata = new TMapData();
		
		tmapdata.findPathDataAll(point1, point2, new FindPathDataAllListenerCallback() {
			@Override
			public void onFindPathDataAll(Document doc) {
				LogManager.printLog("onFindPathDataAll: " + doc);
			}
		});
	}
	
	public void drawCarPath() {
		
		
		TMapPoint point1 = mMapView.getCenterPoint();
		TMapPoint point2 = randomTMapPoint();
		
		TMapData tmapdata = new TMapData();
		
		tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, point1, point2, new FindPathDataListenerCallback() {
			@Override
			public void onFindPathData(TMapPolyLine polyLine) {
				mMapView.addTMapPath(polyLine);
			}
		});
	}
	
	public void drawPedestrianPath() {				
		TMapPoint point1 = mMapView.getCenterPoint();
		TMapPoint point2 = randomTMapPoint();
		
		TMapData tmapdata = new TMapData();
		
		tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, point1, point2, new FindPathDataListenerCallback() {
			@Override
			public void onFindPathData(TMapPolyLine polyLine) {
				polyLine.setLineColor(Color.BLUE);
				mMapView.addTMapPath(polyLine);
			}
		});
	}
	
	/**
	 * getCenterPoint
	 * 지도의 중심점을 가지고 온다. 
	 */
	public void getCenterPoint() {
		TMapPoint point = mMapView.getCenterPoint();
		
		Common.showAlertDialog(this, "", "지도의 중심 좌표는 " + point.getLatitude() + " " + point.getLongitude() );
	}
	
	/**
	 * findAllPoi
	 * 통합검색 POI를 요청한다. 
	 */
	public void findAllPoi() {
		//final String[] PoiName = new String[1];
		//final String[] PoiPoint = new String[1];

		//TMapData tmapdata2 = new TMapData();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("목적지 검색");

		final EditText input = new EditText(this);
		builder.setView(input);
		//final TextView con1 = new TextView(this);
		//builder.setView(con1);

		final ArrayList<String> LIST_POI = new ArrayList<>();
		final ArrayList<TMapPoint> LIST_POI_pos = new ArrayList<>();
		final ArrayList<String> LIST_navi = new ArrayList<>();
		final ArrayList<TMapPoint> LIST_navi_pos = new ArrayList<>();

		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String strData = input.getText().toString();
				TMapData tmapdata = new TMapData();

				tmapdata.findAllPOI(strData, new FindAllPOIListenerCallback() {
					@Override
					public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
						for (int i = 0; i < poiItem.size(); i++) {
							TMapPOIItem item = poiItem.get(i);
							//PoiName[i] = item.getPOIName().toString();
							//PoiPoint[i] =item.getPOIPoint().toString();
							//con1.setText(item.getPOIName());

							//MaterialTextView textview1 = (MaterialTextView) findViewById(R.id.resultbox);
							//textview1.setText(item.getPOIName());

							LIST_POI.add(item.getPOIName());
							LIST_POI_pos.add(item.getPOIPoint());

							//Intent intent = new Intent(getApplicationContext(), MapActivity.class);
							//intent.putExtra("poiname",item.getPOIName());

							LogManager.printLog("POI Name: " + item.getPOIName().toString() + ", " +
									"Address: " + item.getPOIAddress().replace("null", "") + ", " +
									"Point: " + item.getPOIPoint().toString());
						}

					}
				});


				dialog.cancel();

			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		//builder.setView(con1);

		builder.show();


		//listview로 데이터 보이기
		Context context = getApplicationContext();
		ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, LIST_POI);
		//ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, LIST_navi);
		//ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1,LIST_POI);
		final ListView listview = (ListView) findViewById(R.id.list_poi);
		//final ListView listview2 = (ListView) findViewById(R.id.list_navi);
		listview.setAdapter(adapter);
		//listview2.setAdapter(adapter2);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
				//while(isrightpath==0){

				// get TextView's Text.
				String strText = (String) parent.getItemAtPosition(position);
				TextInputEditText editText2 = (TextInputEditText) findViewById(R.id.toPosition);
				editText2.setText(strText);

				TMapPoint point = LIST_POI_pos.get(LIST_POI.indexOf(strText));

				//Bitmap bitmap = null;
				TMapMarkerItem item1 = new TMapMarkerItem();
				//bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_location);

				item1.setTMapPoint(point);
				item1.setName(strText);
				item1.setVisible(item1.VISIBLE);

				//item1.setIcon(bitmap);
				//LogManager.printLog("bitmap " + bitmap.getWidth() + " " + bitmap.getHeight());

				//bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_location);
				item1.setCalloutTitle(strText);
				item1.setCalloutSubTitle(point.toString());
				item1.setCanShowCallout(true);
				item1.setAutoCalloutVisible(true);

				//Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.i_go);
				//item1.setCalloutRightButtonImage(bitmap_i);
				String strID = String.format("pmarker%d", mMarkerID++);
				//mMapView.addMarkerItem(strID, item1);
				mArrayMarkerID.add(strID);

				// TODO : use strText


				TMapPoint point1 = mMapView.getCenterPoint();
				TMapPoint point2 = point;

				TMapData tmapdata = new TMapData();


				tmapdata.findPathDataAll(point1, point2, new FindPathDataAllListenerCallback() {

					@Override
					public void onFindPathDataAll(Document doc) {
						//boolean warning = false;
						LogManager.printLog("onFindPathDataAll: " + doc);
						Element root = doc.getDocumentElement();
						//루트의 자식노드값 가져오기
						NodeList all = root.getChildNodes();
						Node node = all.item(1);
						NodeList all2 = node.getChildNodes();

						for (int i = 0; i < all2.getLength(); i++) {
							Node node2 = all2.item(i);
							//System.out.println("자식노드수 : " + all2.getLength());
							//System.out.println("자식노드 : " + node2.getNodeName());
							//System.out.println("자식노드타입 : " + node2.getNodeType());
							if (node2.getNodeType() == 1) {
								//System.out.println("노드 값 : " + node2.getTextContent());
								NodeList all3 = node2.getChildNodes();
								for (int j = 0; j < all3.getLength(); j++) {
									Node node3 = all3.item(j);
									//System.out.println("노드이름 : " + node3.getNodeName());
									if (j == 7) {
										System.out.println("노드 값 : " + node3.getTextContent());
										LIST_navi.add(node3.getTextContent());
									}
									System.out.println("노드 값 : " +j+ node3.getTextContent());
									//System.out.println("자식노드 : " + node3.getNodeName());
									//System.out.println("자식노드타입 : " + node3.getNodeType());
									//System.out.println("노드 값 : " + node3.getTextContent());
								}
							}
						}
						MaterialTextView textview1 = findViewById(R.id.text_navi);
						for (int k = 0; k < LIST_navi.size(); k++) {
							System.out.println("리스트값: " + LIST_navi.get(k));
							mMapView.setZoomLevel(17);
							//getCenterPoint();
							setCompassMode();
							setSightVisible();
							//mMapView.getCenterPoint();
							textview1.setText(k + ". " + LIST_navi.get(k));
							if(k==6){
								Intent intent = new Intent(getApplicationContext(), Page4.class);
								//intent.putExtra("description",LIST_navi);
								startActivity(intent);
								//System.out.println("k=6되었음");
								//textview1.setText(" 경로를 이탈하였습니다. " );
								//textview1.setTextSize(30);
								//getLocationPoint();
								//naviGuide();
								//LinearLayout baseview = findViewById(R.id.baseview);

								//ImageView warnning = findViewById(R.id.warning);
								//ListView list2 = findViewById(R.id.list_poi);
								//list2.addFooterView(warnning);
								//AlertDialog.Builder builder = new AlertDialog.Builder(g);
								//builder.setTitle("경고");
								//builder.show();
								//naviGuide();
								break;

							}
							try {
								Thread.sleep(5000);
								//getCenterPoint();
								setCompassMode();
								setSightVisible();
								mMapView.getCenterPoint();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						//if(warning==true){
							///System.out.println("break 뛰쳐나옴");
						//}
						//System.out.println(LIST_navi.get(LIST_navi.size()-1));
						if (LIST_navi.get(LIST_navi.size() - 1).equals("도착")) {
							//System.out.println(LIST_navi.get(LIST_navi.size()-1));
							System.out.println("여기서 끝");
							//isrightpath==1;
						} else {
							System.out.println("error");

						}
					}
				});

				tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, point1, point2, new FindPathDataListenerCallback() {
					@Override
					public void onFindPathData(TMapPolyLine polyLine) {
						mMapView.addTMapPath(polyLine);
					}
				});
				//Intent intent = new Intent(getApplicationContext(), Page4.class);
				//intent.putExtra("description",LIST_navi);
				//startActivity(intent);
				}
			//}

		});

		//Context context = getApplicationContext();
		//ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, LIST_navi);
		//final ListView listview2 = (ListView) findViewById(R.id.list_navi);
		//listview2.setAdapter(adapter2);


	}
	/*
	*
	* Context context = getApplicationContext();
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("경고");
							final ImageView image = new ImageView(context);
							image.setImageDrawable(getResources().getDrawable(R.drawable.icon_warning_ww));
							final TextView input = new TextView(context);
							input.setText("경로를 벗어났습니다. 경로를 재탐색하시려면 화면을 밀어주세요.");
							builder.setView(input);

							builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									final String strData = input.getText().toString();
									TMapData tmapdata = new TMapData();
									tmapdata.findAllPOI(strData, new FindAllPOIListenerCallback() {
										@Override
										public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
										}
									});
									dialog.cancel();
								}
							}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
							//builder.setView(con1);
							builder.show();
	*
	* */
	
	/**
	 * convertToAddress
	 * 지도에서 선택한 지점을 주소를 변경요청한다. 
	 */
	public void convertToAddress() {			
		TMapPoint point = mMapView.getCenterPoint();
		
	    TMapData tmapdata = new TMapData();
	      
		if (mMapView.isValidTMapPoint(point)) {
		    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
				@Override
				public void onConvertToGPSToAddress(String strAddress) {
					LogManager.printLog("선택한 위치의 주소는 " + strAddress);
					TextInputEditText editText1 = (TextInputEditText)findViewById(R.id.nowPosition);
					editText1.setText(strAddress) ;
				}
			});

//		    tmapdata.geoCodingWithAddressType("F02", "서울시", "구로구", "새말로", "6", "", new GeoCodingWithAddressTypeListenerCallback() {
//		    	
//				@Override
//				public void onGeoCodingWithAddressType(TMapGeocodingInfo geocodingInfo) {
//					LogManager.printLog(">>> strMatchFlag : " + geocodingInfo.strMatchFlag);
//					LogManager.printLog(">>> strLatitude : " + geocodingInfo.strLatitude);
//					LogManager.printLog(">>> strLongitude : " + geocodingInfo.strLongitude);
//					LogManager.printLog(">>> strCity_do : " + geocodingInfo.strCity_do);
//					LogManager.printLog(">>> strGu_gun : " + geocodingInfo.strGu_gun);
//					LogManager.printLog(">>> strLegalDong : " + geocodingInfo.strLegalDong);
//					LogManager.printLog(">>> strAdminDong : " + geocodingInfo.strAdminDong);
//					LogManager.printLog(">>> strBunji : " + geocodingInfo.strBunji);
//					LogManager.printLog(">>> strNewMatchFlag : " + geocodingInfo.strNewMatchFlag);
//					LogManager.printLog(">>> strNewLatitude : " + geocodingInfo.strNewLatitude);
//					LogManager.printLog(">>> strNewLongitude : " + geocodingInfo.strNewLongitude);
//					LogManager.printLog(">>> strNewRoadName : " + geocodingInfo.strNewRoadName);
//					LogManager.printLog(">>> strNewBuildingIndex : " + geocodingInfo.strNewBuildingIndex);
//					LogManager.printLog(">>> strNewBuildingName : " + geocodingInfo.strNewBuildingName);
//				}
//			});
	    }
	}    
	
	/**
	 * getBizCategory
	 * 업종별 category를 요청한다. 
	 */
	public void getBizCategory() {	
		TMapData tmapdata = new TMapData();
		
        tmapdata.getBizCategory(new BizCategoryListenerCallback() {
			@Override
			public void onGetBizCategory(ArrayList<BizCategory> poiItem) {
				for (int i = 0; i < poiItem.size(); i++) {
		        	BizCategory item = poiItem.get(i);
		        	LogManager.printLog("UpperBizCode " + item.upperBizCode + " " + "UpperBizName " + item.upperBizName );
		        	LogManager.printLog("MiddleBizcode " + item.middleBizCode + " " + "MiddleBizName " + item.middleBizName);
		        }
			}
		});
	}
	
	/**
	 * getAroundBizPoi
	 * 업종별 주변검색 POI 데이터를 요청한다. 
	 */
	public void getAroundBizPoi() {				
		TMapData tmapdata = new TMapData();
		 
		TMapPoint point = mMapView.getCenterPoint();
		
		tmapdata.findAroundNamePOI(point, "편의점;은행", 1, 99, new FindAroundNamePOIListenerCallback() {
			@Override
			public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
				for (int i = 0; i < poiItem.size(); i++) {
					TMapPOIItem item = poiItem.get(i);
					LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
							+ item.getPOIAddress().replace("null", ""));
				}
			}
		});
	}
	
	public void setTileType() {
    	AlertDialog dlg = new AlertDialog.Builder(this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle("Select MAP Tile Type")
		.setSingleChoiceItems(R.array.a_tiletype, -1, new DialogInterface.OnClickListener() {						
			@Override
			public void onClick(DialogInterface dialog, int item) {							
				LogManager.printLog("Set Map Tile Type " + item);
				dialog.dismiss();
//				mMapView.setTileType(item);

				Resources res = getResources();
				String[] arrTileType = res.getStringArray(R.array.a_tiletype);
				switch (arrTileType[item]) {
					case "NORMALTILE":
						mMapView.setTileType(TMapView.TILETYPE_NORMALTILE);
						break;
					case "HDTILE":
						mMapView.setTileType(TMapView.TILETYPE_HDTILE);
						break;
				}
			}
		}).show();	
	}
	
	public void invokeRoute() {	
		final TMapPoint point = mMapView.getCenterPoint();
		TMapData tmapdata = new TMapData();
				
		if(mMapView.isValidTMapPoint(point)) {
			tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
				@Override
				public void onConvertToGPSToAddress(String strAddress) {
					TMapTapi tmaptapi = new TMapTapi(MapActivity.this);
					float fY = (float)point.getLatitude();
					float fX = (float)point.getLongitude();
					tmaptapi.invokeNavigate(strAddress, fX, fY, 0, true);
				}
			});
		}
	}
	
	public void invokeSetLocation() {
		final TMapPoint point = mMapView.getCenterPoint();
		TMapData tmapdata = new TMapData();

		tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
			@Override
			public void onConvertToGPSToAddress(String strAddress) {
				TMapTapi tmaptapi = new TMapTapi(MapActivity.this);
				float fY = (float) point.getLatitude();
				float fX = (float) point.getLongitude();
				tmaptapi.invokeSetLocation(strAddress, fX, fY);
			}
		});
	}
	
	public void invokeSearchProtal() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("T MAP 통합 검색");

		final EditText input = new EditText(this);
		builder.setView(input);

		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String strSearch = input.getText().toString();

				new Thread() {
					@Override
					public void run() {
						TMapTapi tmaptapi = new TMapTapi(MapActivity.this);
						if (strSearch.trim().length() > 0)
							tmaptapi.invokeSearchPortal(strSearch);
					}
				}.start();
			}
		});
		builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}
	
	public void tmapInstall() {
		new Thread() {
			@Override
			public void run() {
				TMapTapi tmaptapi = new TMapTapi(MapActivity.this);
				Uri uri = Uri.parse(tmaptapi.getTMapDownUrl().get(0));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}

		}.start();
	}


	
	public void captureImage() {
		mMapView.getCaptureImage(20, new MapCaptureImageListenerCallback() {
			
			@Override
			public void onMapCaptureImage(final Bitmap bitmap) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    // 외부 저장공간이 사용가능하다면
					permissionManager.request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionManager.PermissionListener() {
						@Override
						public void granted() {
							File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMAPOpenAPI");
							if (!file.exists()) {
								file.mkdirs();
							}

							if (file.exists()) {
								OutputStream out = null;
								String fileName = System.currentTimeMillis() + ".png";
								File fileCacheItem = new File(file.toString() + File.separator + fileName);
								try {
									fileCacheItem.createNewFile();
									out = new FileOutputStream(fileCacheItem);
									bitmap.compress(CompressFormat.JPEG, 90, out);
									out.flush();
									Toast.makeText(MapActivity.this, "Saved :" + fileCacheItem.getAbsolutePath(), Toast.LENGTH_LONG).show();
								} catch (Exception e) {
									Toast.makeText(MapActivity.this, "캡처 이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
									e.printStackTrace();
								}
								finally {
									if(out != null) {
										try {
											out.close();
										}catch(Exception e1) {}
									}
								}
							}
							else {
								Toast.makeText(MapActivity.this, "캡쳐 디렉터리 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void denied() {

						}
					});
                }
			}
		});
	}
	
	private boolean bZoomEnable = false;
	
	public void disableZoom() {
		bZoomEnable = !bZoomEnable;
		mMapView.setUserScrollZoomEnable(bZoomEnable);
	}
	
	public void timeMachine() {
		TMapData tmapdata = new TMapData();
		
		HashMap<String, String> pathInfo = new HashMap<String, String>();
		pathInfo.put("rStName", "T Tower");
		pathInfo.put("rStlat", Double.toString(37.566474));
		pathInfo.put("rStlon", Double.toString(126.985022));
		pathInfo.put("rGoName", "신도림");
		pathInfo.put("rGolat", "37.50861147");
		pathInfo.put("rGolon", "126.8911457");
		pathInfo.put("type", "arrival");
		
		Date currentTime = new Date();
        tmapdata.findTimeMachineCarPath(pathInfo, currentTime, null, new TMapData.FindTimeMachineCarPathListenerCallback() {
			@Override
			public void onFindTimeMachineCarPath(Document document) {
				LogManager.printLog("onFindTimeMachineCarPath: " + document);
			}
		});
	}
}

