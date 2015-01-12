package me.noip.vmv857.loc;

import android.location.Location;

public class LocationEx extends Location {
	public LocationEx(String provider) {
		super(provider);
	}
	
	@Override
	public String toString(){
		return "_id="+_id+" idwho="+idwho+" d_t="+d_t+" Loc="+super.toString();
	}
	public long _id;  // id на сервере
	public String idwho; // источник
	public String d_t; // время на сервере
}
