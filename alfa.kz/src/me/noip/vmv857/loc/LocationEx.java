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
	public long _id;  // id �� �������
	public String idwho; // ��������
	public String d_t; // ����� �� �������
}
