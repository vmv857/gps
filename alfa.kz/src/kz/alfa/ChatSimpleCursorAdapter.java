package kz.alfa;

import kz.alfa.util.Log;
import kz.alfa.util.Uid;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatSimpleCursorAdapter extends SimpleCursorAdapter {
	private static String LOG_TAG = "ChatSimpleCursorAdapter";

	public ChatSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View res = super.getView(position, convertView, parent);
		try {
			LinearLayout item = (LinearLayout) res;
			//Log.e(LOG_TAG, "item.getChildAt(1)=" + item.getChildAt(1));
			TextView tvWho = (TextView) item.getChildAt(0);
			String who = (String) tvWho.getText();
			if ((Uid.get()!=null)&&(who.equalsIgnoreCase(Uid.get()))){
				tvWho.setTextColor(Color.GREEN);
			} else
				tvWho.setTextColor(Color.BLACK);
			TextView tvWhat = (TextView) item.getChildAt(1);
			//Log.e(LOG_TAG, "tvWhat=" + tvWhat);
			String txt = (String) tvWhat.getText();
			//Log.e(LOG_TAG, "txt=" + txt);
			if (txt.indexOf("#") >= 0)
				tvWhat.setTextColor(Color.RED);
			else
				tvWhat.setTextColor(Color.GREEN);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception=" + e.toString());
		}
		return res;
	}

}
/*


    Color.parseColor (Manual) (like LEX uses)

    text.setTextColor(Color.parseColor("#FFFFFF"));

    Color.rgb and Color.argb (Manual rgb) (Manual argb) (like Ganapathy uses)

    holder.text.setTextColor(Color.rgb(200,0,0));
    holder.text.setTextColor(Color.argb(0,200,0,0));

    And of course, if you want to define your color in an XML file, you can do this:

    <color name="errorColor">#f00</color>

    and then use this code to show it:

    textView.setTextColor(getResources().getColor(R.color.errorColor));

    You can also insert plain HEX, like so:

    myTextView.setTextColor(0xAARRGGBB);

    Where you have an alpha-channel first, then the color value.

Check out the complete m
*/

