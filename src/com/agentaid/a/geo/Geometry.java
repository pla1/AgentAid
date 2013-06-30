
package com.agentaid.a.geo;


public class Geometry{
   	private Bounds bounds;
   	private Location location;
   	private String location_type;
   	private Viewport viewport;

 	public Bounds getBounds(){
		return this.bounds;
	}
	public void setBounds(Bounds bounds){
		this.bounds = bounds;
	}
 	public Location getLocation(){
		return this.location;
	}
	public void setLocation(Location location){
		this.location = location;
	}
 	public String getLocation_type(){
		return this.location_type;
	}
	public void setLocation_type(String location_type){
		this.location_type = location_type;
	}
 	public Viewport getViewport(){
		return this.viewport;
	}
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
}
