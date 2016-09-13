/**
 *
 */
package com.lucrus.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lucrus.main.synchro.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * @author luca.russo
 */
public class Utility {

    /**
     *
     */
    private Utility() {
        super();
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    private static String deviceId;

    public static String getDeviceId(Context ctx) {
        if (deviceId == null) {
            try {
                TelephonyManager deviceCode = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = deviceCode.getDeviceId();
            } catch (Throwable t) {
            }
        }

        if (deviceId == null) {
            try {
                deviceId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
            } catch (Throwable t) {
            }
        }

        if (deviceId == null) {
            String di = Utility.loadUserPreference(ctx, "__DEVICE_ID__", "");
            if (di.length() == 0) {
                di = "" + new Random().nextLong();
                if (di.length() < 9) {
                    for (int i = di.length(); i <= 9; i++) {
                        di = "0" + di;
                    }
                }
                Utility.saveUserPreference(ctx, "__DEVICE_ID__", di);
            }
            deviceId = di;
        }
        return deviceId;
    }

    public static String getAppVersion(Context ctx) {
        PackageInfo pinfo;
        try {
            pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pinfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getAppVersionCode(Context ctx) {
        PackageInfo pinfo;
        try {
            pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pinfo.versionCode;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*
        public static float getScaleFactor(boolean vertical) {
            if (vertical) {
                float y = getDisplaySize(null).y;
                return y / ApplicationData.SP_HEIGHT;
            } else {
                float x = getDisplaySize(null).x;
                return x / ApplicationData.SP_WIDTH;
            }
        }
    */
    private static Float density = null;

    public static float getDensity(Activity act) {
        if (density == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            density = metrics.density;
            //this.density = (float)((metrics.densityDpi*1.0)/(metrics.widthPixels*1.0));
        }
        return density.floatValue();
    }

    private static Point size;

    public static Point getDisplaySize(Activity act) {
        if (size == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            size = new Point(metrics.widthPixels, metrics.heightPixels);
            //this.density = (float)((metrics.densityDpi*1.0)/(metrics.widthPixels*1.0));
        }
        return size;
    }

    public static int getActualSize(int designSize, int size, int sizeReal) {
        return size * sizeReal / designSize;
    }

    public static void saveUserPreference(Context ctx, String key, String value) {
        String encValue;
        try {
            if (value != null && value.trim().length() > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(value.getBytes());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                IoUtils.encrypt(bis, bos);
                encValue = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            } else {
                encValue = value;
            }
            String name = ctx.getResources().getString(R.string.app_name).toUpperCase();
            name = name.replaceAll(" ", "_");
            Editor editor = ctx.getSharedPreferences(name, Context.MODE_APPEND).edit();
            editor.putString(key, encValue);
            editor.commit();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String loadUserPreference(Context ctx, String key) {
        return loadUserPreference(ctx, key, "");
    }

    public static String loadUserPreference(Context ctx, String key, String defValue) {
        try {
            if (defValue != null && defValue.trim().length() > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(defValue.getBytes());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                IoUtils.encrypt(bis, bos);
                defValue = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            }
            String name = ctx.getResources().getString(R.string.app_name).toUpperCase();
            name = name.replaceAll(" ", "_");
            String encValue = ctx.getSharedPreferences(name, Context.MODE_APPEND).getString(key, defValue);
            if (encValue == null || encValue.trim().length() == 0) {
                return encValue;
            }
            byte[] buf = Base64.decode(encValue, Base64.DEFAULT);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IoUtils.decrypt(bis, bos);
            String value = new String(bos.toByteArray());
            return value;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date truncDate(Date date) {
        if (date == null) {
            return null;
        }
        String format = "dd-MM-yyyy";
        Date retDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            retDate = (Date) sdf.parse(sdf.format(date));
        } catch (Exception e) {
        }
        return retDate;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, String place, int status, long startDate, long endDate, boolean needReminder, boolean needMailService) {
        /***************** Event: note(without alert) *******************/

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", 1); // id, We need to choose from
        // our mobile for primary
        // its 1

        if (title.equalsIgnoreCase(addInfo)) {
            addInfo = "";
        }
        eventValues.put("title", title);
        eventValues.put("description", addInfo);
        eventValues.put("eventLocation", place);

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);

        // values.put("allDay", 1); //If it is bithday alarm or such
        // kind (which should remind me for whole day) 0 for false, 1
        // for true
        eventValues.put("eventStatus", status); // This information is
        // sufficient for most
        // entries tentative (0),
        // confirmed (1) or canceled
        // (2):
        //		    eventValues.put("visibility", 0); // visibility to default (0),
        // confidential (1), private
        // (2), or public (3):
        //		    eventValues.put("transparency", 0); // You can control whether
        // an event consumes time
        // opaque (0) or transparent
        // (1).
        eventValues.put("hasAlarm", 1); // 0 for false, 1 for true
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        eventValues.put("eventTimezone", cal.get(Calendar.ZONE_OFFSET));
        Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        if (needReminder) {
            /***************** Event: Reminder(with alert) Adding reminder to event *******************/

            String reminderUriString = "content://com.android.calendar/reminders";

            ContentValues reminderValues = new ContentValues();

            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 5); // Default value of the
            // system. Minutes is a
            // integer
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),
            // SMS(3)

			/*Uri reminderUri =*/
            curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        }

        /***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

        if (needMailService) {
            String attendeuesesUriString = "content://com.android.calendar/attendees";

            /********
             * To add multiple attendees need to insert ContentValues multiple
             * times
             ***********/
            ContentValues attendeesValues = new ContentValues();

            attendeesValues.put("event_id", eventID);
            attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
            attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee
            // E
            // mail
            // id
            attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
            // Relationship_None(0),
            // Organizer(2),
            // Performer(3),
            // Speaker(4)
            attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
            // Required(2), Resource(3)
            attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
            // Decline(2),
            // Invited(3),
            // Tentative(4)

			/*Uri attendeuesesUri =*/
            curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
        }

        return eventID;

    }

    public static long findEventInCalendarByStartDate(Activity act, Date date) {
        try {
            Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");// getCalendarUriBase();
            Cursor cursors = act.getContentResolver().query(CALENDAR_URI, new String[]{"_id", "title", "description", "dtstart", "dtend", "eventLocation"}, "deleted = ?", new String[]{"0"}, null);


            cursors.moveToFirst();
            //		String[] CalNames = new String[cursors.getCount()];
            //		int[] CalIds = new int[cursors.getCount()];
            while (true) {
                //Log.i("CALENDAR", new Date(cursors.getLong(3)) + " - " +cursors.getString(1));
                //			for (int i = 0; i < CalNames.length; i++) {
                //				CalIds[i] = cursors.getInt(0);
                //				CalNames[i] = "Event"+cursors.getInt(0)+": \nTitle: "+ cursors.getString(1)+"\nDescription: "+cursors.getString(2)+"\nStart Date: "+new Date(cursors.getLong(3))+"\nEnd Date : "+new Date(cursors.getLong(4))+"\nLocation : "+cursors.getString(5);

                //			Date mDate = new Date(cursors.getLong(3));
                //			Date nDate = new Date(cursors.getLong(4));

                //			long mTime = mDate.getTime();
                //			long lTime = nDate.getTime();
                if (Math.abs(date.getTime() - cursors.getLong(3)) < 1000) {
                    String eid = cursors.getString(0);

                    int eID = Integer.parseInt(eid);

                    //				String desc = cursors.getString(2);
                    //				String title = cursors.getString(1);
                    cursors.close();
                    return eID;
                }
                //			}
                if (cursors.isLast()) {
                    break;
                }
                cursors.moveToNext();
            }
            cursors.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return 0;
    }


    private static Uri getCalendarUriBase() {
        Uri CALENDAR_URI;
        String uri;
        if (Build.VERSION.SDK_INT >= 8 || Build.VERSION.SDK_INT <= 13) {
            uri = "content://com.android.calendar/events";
            CALENDAR_URI = Uri.parse(uri);
            //		} else if(Integer.parseInt(Build.VERSION.SDK) >= 14){
            //
            //			CALENDAR_URI = CalendarContract.Events.CONTENT_URI;

        } else {
            uri = "content://calendar/events";
            CALENDAR_URI = Uri.parse(uri);
        }
        return CALENDAR_URI;
    }

    public static int deleteCalendarEntry(Activity act, long calendarId) {
        //		int iNumRowsDeleted = 0;
        //
        //		Uri eventsUri = getCalendarUriBase();
        //		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
        //		iNumRowsDeleted = act.getContentResolver().delete(eventUri, null, null);
        //
        //
        //		return iNumRowsDeleted;
        int res = 0;
        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");//getCalendarUriBase();
        Uri eventUri = ContentUris.withAppendedId(CALENDAR_URI, calendarId);
        res = act.getContentResolver().delete(eventUri, null, null);
        return res;

        //		Cursor cursors = act.getContentResolver().query(CALENDAR_URI, new String[]{ "_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null,null,null);
        //		cursors.moveToFirst();
        //
        //		while(!cursors.isLast()){
        //			if(cursors.getLong(0)==calendarId){
        //				 long eventId = cursors.getLong(cursors.getColumnIndex("_id"));
        //			     res = act.getContentResolver().delete(ContentUris.withAppendedId(CALENDAR_URI, eventId), null, null);
        ////				String eid = cursors.getString(0);
        //
        ////				int eID = Integer.parseInt(eid);
        //
        //				//				String desc = cursors.getString(2);
        //				//				String title = cursors.getString(1);
        ////				return eID;
        //			     break;
        //			}
        ////			}
        //			cursors.moveToNext();
        //		}
        //		cursors.close();
        //		return res;


        //		int res = 0;
        //		Cursor cursor;
        //	    Uri eventsUri = getCalendarUriBase();
        //		ContentResolver resolver = act.getContentResolver();
        //		if (android.os.Build.VERSION.SDK_INT <= 7) { //up-to Android 2.1
        //	        cursor = resolver.query(eventsUri, new String[]{ "_id" }, "Calendars._id=" + calendarId, null, null);
        //	    } else { //8 is Android 2.2 (Froyo) (http://developer.android.com/reference/android/os/Build.VERSION_CODES.html)
        //	        cursor = resolver.query(eventsUri, new String[]{ "_id" }, "calendar_id=" + calendarId, null, null);
        //	    }
        //	    while(cursor.moveToNext()) {
        //	        long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
        //	        res = resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
        //	    }
        //	    cursor.close();
        //	    return res;
    }


    public static String getContentType(String file) {
        file = file.toLowerCase();
        if (file.endsWith("pdf")) {
            return "application/pdf";
        } else if (file.endsWith("mp4")) {
            return "video/mp4";
        } else if (file.endsWith("jpeg")) {
            return "image/jpg";
        } else if (file.endsWith("jpg")) {
            return "image/jpg";
        } else if (file.endsWith("png")) {
            return "image/png";
        } else if (file.endsWith("xls")) {
            return "application/vnd.ms-excel";
        } else if (file.endsWith("xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (file.endsWith("apk")) {
            return "application/vnd.android.package-archive";
        }

        return "*/*";
    }

    public static ProgressDialog showWaitDialog(Context ctx, boolean indeterminate) {
//      ProgressDialog progress = (ProgressDialog)
//              new ProgressDialog.Builder(ctx)
//                 .setTitle(R.string.app_name)
//                 .setMessage(R.string.wait)
//                 .create();
//      progress.setIndeterminate(indeterminate);
//      progress.show();
        return ProgressDialog.show(ctx, ctx.getString(R.string.app_name), ctx.getString(R.string.wait), indeterminate);

        //return pd;
    }

    public static void showAlert(Context ctx, int messageId) {
        showAlert(ctx, messageId, null);
    }

    public static void showAlert(Context ctx, String message) {
        //if(listener==null){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        //}
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    public static void showAlert(Context ctx, int messageId, DialogInterface.OnClickListener listener) {
        if (listener == null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage(messageId)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    public static AlertDialog showInputDialog(Context ctx, int messageId, View customView, DialogInterface.OnClickListener listener) {
        return showInputDialog(ctx, messageId, customView, listener, 2);
    }

    public static AlertDialog showInputDialog(Context ctx, int messageId, View customView, DialogInterface.OnClickListener listener, int tasti) {
        DialogInterface.OnClickListener listenerKo = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        if (listener == null) {
            listener = listenerKo;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage(messageId);

        if (tasti == 3) {
            b.setNegativeButton(android.R.string.cancel, listenerKo);
        } else {
            if (tasti > 0) {
                b.setPositiveButton(android.R.string.ok, listener);
                if (tasti > 1) {
                    b.setNegativeButton(android.R.string.cancel, listenerKo);
                }
            }
        }

        if (customView != null) {
            b.setView(customView);
        }
        return b.show();

    }

    public static void showInputDialog(Context ctx, int messageId, View customView, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listenerKo) {
        DialogInterface.OnClickListener listenerNull = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        if (listenerKo == null) {
            listenerKo = listenerNull;
        }

        if (listener == null) {
            listener = listenerKo;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage(messageId)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listenerKo);

        if (customView != null) {
            b.setView(customView);
        }
        b.show();
    }

    public static void showInputDialog(Context ctx, String messageId, View customView, DialogInterface.OnClickListener listener) {
        DialogInterface.OnClickListener listenerKo = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        if (listener == null) {
            listener = listenerKo;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage(messageId)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listenerKo);

        if (customView != null) {
            b.setView(customView);
        }
        b.show();
    }

    public static int dammiAnno(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    public static int dammiGiorno(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private static final boolean LOG_ON_FILE = false;

    public static void log(Context ctx, String msg) {
        if (LOG_ON_FILE) {
            try {
                String file = Environment.getExternalStorageDirectory() + "/" + ctx.getString(R.string.app_name) + ".log";
                File f = new File(file);
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileWriter fw = new FileWriter(f, true);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                fw.write(sdf.format(new Date()));
                fw.write(":");
                fw.write(msg);
                fw.write("\n");
                fw.flush();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("LOG", msg);
        }
    }

    public static void log(Context ctx, Throwable ex) {
        if (LOG_ON_FILE) {
            try {
                String appName = "ExInEx EVO";
                if (ctx != null) {
                    appName = ctx.getString(R.string.app_name);
                }
                String file = Environment.getExternalStorageDirectory() + "/" + appName + ".log";
                File f = new File(file);
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileWriter fw = new FileWriter(f, true);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                fw.write(sdf.format(new Date()));
                fw.write(":");
                ex.printStackTrace(new PrintWriter(fw));
                fw.write("\n");
                fw.flush();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ERROR", ex.getMessage(), ex);
        }
    }

    public static double round(double d) {
        BigDecimal bd = new BigDecimal("" + d);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }


}
