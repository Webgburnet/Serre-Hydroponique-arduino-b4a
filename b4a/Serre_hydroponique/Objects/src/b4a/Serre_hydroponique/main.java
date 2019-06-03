package b4a.Serre_hydroponique;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.Serre_hydroponique", "b4a.Serre_hydroponique.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.Serre_hydroponique", "b4a.Serre_hydroponique.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.Serre_hydroponique.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _adressepc = "";
public anywheresoftware.b4a.objects.ButtonWrapper _connexion = null;
public anywheresoftware.b4a.objects.EditTextWrapper _ip_arduino_edittext = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _brumisateur = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _chauffage = null;
public anywheresoftware.b4a.objects.LabelWrapper _connexion_label = null;
public anywheresoftware.b4a.objects.PanelWrapper _connexion_panel = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _electroaimant = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _electrovanne = null;
public anywheresoftware.b4a.objects.LabelWrapper _emission_data = null;
public anywheresoftware.b4a.objects.PanelWrapper _emission_panel = null;
public anywheresoftware.b4a.objects.LabelWrapper _ip_arduino_label = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _lampe = null;
public anywheresoftware.b4a.objects.LabelWrapper _reception = null;
public anywheresoftware.b4a.objects.PanelWrapper _reception_panel = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _ventillateur = null;
public anywheresoftware.b4a.objects.ButtonWrapper _acquerir = null;
public anywheresoftware.b4a.objects.LabelWrapper _donnee = null;
public b4a.example.udp _udp = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _acquerir_click() throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 73;BA.debugLine="Sub Acquerir_Click";
 //BA.debugLineNum = 74;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 75;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 76;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 77;BA.debugLine="message=\"Acquer\"";
_message = "Acquer";
 //BA.debugLineNum = 78;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 79;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"layout1\")";
mostCurrent._activity.LoadLayout("layout1",mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="UDP.Initialise(3200)";
mostCurrent._udp._initialise(mostCurrent.activityBA,(int) (3200));
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _arrete_click() throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 64;BA.debugLine="Sub Arrete_Click";
 //BA.debugLineNum = 65;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 66;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 67;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 68;BA.debugLine="message=\"Arrete\"";
_message = "Arrete";
 //BA.debugLineNum = 69;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 70;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _brumisateur_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 93;BA.debugLine="Sub Brumisateur_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 94;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 95;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 96;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 97;BA.debugLine="If Checked=True Then message=\"BrumiOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "BrumiOFF";};
 //BA.debugLineNum = 98;BA.debugLine="If Checked=False Then message=\"BrumiON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "BrumiON";};
 //BA.debugLineNum = 100;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 101;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _chauffage_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 137;BA.debugLine="Sub Chauffage_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 138;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 139;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 140;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 141;BA.debugLine="If Checked=True Then message=\"ChauffageOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "ChauffageOFF";};
 //BA.debugLineNum = 142;BA.debugLine="If Checked=False Then message=\"ChauffageON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "ChauffageON";};
 //BA.debugLineNum = 144;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 145;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
public static String  _connexion_click() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Connexion_Click";
 //BA.debugLineNum = 44;BA.debugLine="Adressepc=IP_Arduino_EditText.text";
_adressepc = mostCurrent._ip_arduino_edittext.getText();
 //BA.debugLineNum = 45;BA.debugLine="If (Adressepc=\"192.168.2.234\") Then";
if (((_adressepc).equals("192.168.2.234"))) { 
 //BA.debugLineNum = 46;BA.debugLine="IP_Arduino_Label.Text=\"Connexion réussie\"";
mostCurrent._ip_arduino_label.setText((Object)("Connexion réussie"));
 }else {
 //BA.debugLineNum = 48;BA.debugLine="IP_Arduino_Label.Text=\"Connexion échouer\"";
mostCurrent._ip_arduino_label.setText((Object)("Connexion échouer"));
 };
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _electroaimant_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 115;BA.debugLine="Sub Electroaimant_CheckedChange(Checked As Boolean";
 //BA.debugLineNum = 116;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 117;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 118;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 119;BA.debugLine="If Checked=True Then message=\"AimantOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "AimantOFF";};
 //BA.debugLineNum = 120;BA.debugLine="If Checked=False Then message=\"AimantON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "AimantON";};
 //BA.debugLineNum = 122;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 123;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _electrovanne_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 104;BA.debugLine="Sub Electrovanne_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 105;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 106;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 107;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 108;BA.debugLine="If Checked=True Then message=\"VanneOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "VanneOFF";};
 //BA.debugLineNum = 109;BA.debugLine="If Checked=False Then message=\"VanneON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "VanneON";};
 //BA.debugLineNum = 111;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 112;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private Connexion As Button";
mostCurrent._connexion = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private IP_Arduino_EditText As EditText";
mostCurrent._ip_arduino_edittext = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private Brumisateur As ToggleButton";
mostCurrent._brumisateur = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private Chauffage As ToggleButton";
mostCurrent._chauffage = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private Connexion_label As Label";
mostCurrent._connexion_label = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private Connexion_Panel As Panel";
mostCurrent._connexion_panel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Electroaimant As ToggleButton";
mostCurrent._electroaimant = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private Electrovanne As ToggleButton";
mostCurrent._electrovanne = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private Emission_data As Label";
mostCurrent._emission_data = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private Emission_Panel As Panel";
mostCurrent._emission_panel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private IP_Arduino_Label As Label";
mostCurrent._ip_arduino_label = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private Lampe As ToggleButton";
mostCurrent._lampe = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private Reception As Label";
mostCurrent._reception = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private Reception_Panel As Panel";
mostCurrent._reception_panel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Ventillateur As ToggleButton";
mostCurrent._ventillateur = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private Acquerir As Button";
mostCurrent._acquerir = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private Donnee As Label";
mostCurrent._donnee = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _lampe_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 126;BA.debugLine="Sub Lampe_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 127;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 128;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 129;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 130;BA.debugLine="If Checked=True Then message=\"EclairageOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "EclairageOFF";};
 //BA.debugLineNum = 131;BA.debugLine="If Checked=False Then message=\"EclairageON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "EclairageON";};
 //BA.debugLineNum = 133;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 134;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.udp._process_globals();
main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim Adressepc As String";
_adressepc = "";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _udp_packetarrived(anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _packet) throws Exception{
String _message = "";
 //BA.debugLineNum = 52;BA.debugLine="Sub UDP_PacketArrived (packet As UDPPacket)";
 //BA.debugLineNum = 53;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 54;BA.debugLine="message=UDP.reception(packet)";
_message = mostCurrent._udp._reception(mostCurrent.activityBA,_packet);
 //BA.debugLineNum = 55;BA.debugLine="If message=\"acknowledged\" Then";
if ((_message).equals("acknowledged")) { 
 //BA.debugLineNum = 56;BA.debugLine="Donnee.Text=message";
mostCurrent._donnee.setText((Object)(_message));
 }else {
 //BA.debugLineNum = 58;BA.debugLine="Donnee.Text=message";
mostCurrent._donnee.setText((Object)(_message));
 //BA.debugLineNum = 59;BA.debugLine="Donnee.TextColor=Colors.RGB(234, 54, 54)";
mostCurrent._donnee.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (234),(int) (54),(int) (54)));
 };
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _ventillateur_checkedchange(boolean _checked) throws Exception{
byte[] _emission = null;
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
String _message = "";
 //BA.debugLineNum = 82;BA.debugLine="Sub Ventillateur_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 83;BA.debugLine="Dim emission() As Byte";
_emission = new byte[(int) (0)];
;
 //BA.debugLineNum = 84;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 85;BA.debugLine="Dim message As String";
_message = "";
 //BA.debugLineNum = 86;BA.debugLine="If Checked=True Then message=\"VentiOFF\"";
if (_checked==anywheresoftware.b4a.keywords.Common.True) { 
_message = "VentiOFF";};
 //BA.debugLineNum = 87;BA.debugLine="If Checked=False Then message=\"VentiON\"";
if (_checked==anywheresoftware.b4a.keywords.Common.False) { 
_message = "VentiON";};
 //BA.debugLineNum = 89;BA.debugLine="emission=bc.StringToBytes(message,\"ASCII\")";
_emission = _bc.StringToBytes(_message,"ASCII");
 //BA.debugLineNum = 90;BA.debugLine="UDP.emission(Adressepc,5500,emission)";
mostCurrent._udp._emission(mostCurrent.activityBA,_adressepc,(int) (5500),_emission);
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
}
