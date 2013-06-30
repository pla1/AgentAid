package com.agentaid.a.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.Size;

public class MapPanel extends VerticalPanel {
	private GoogleMap map;
	private final MapOptions mapOptions = MapOptions.create();
	private final LatLng centerCoordinates = LatLng.create(33.994626, -81.045025);
	private final VerticalPanel panel = this;
	private final int height = Window.getClientHeight();
	private final int width = Window.getClientWidth();

	public MapPanel() {
		panel.setHeight((int) (height * .80) + "px");
		panel.setWidth((int) width * .95 + "px");
		mapOptions.setCenter(centerCoordinates);
		mapOptions.setZoom(8.0);
		mapOptions.setMapTypeId(MapTypeId.ROADMAP);
		map = GoogleMap.create(panel.getElement(), mapOptions);
		map.setTilt(45);
		addAgents();
	}

	private String toHtml(Agent agent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='https://plus.google.com/");
		sb.append(agent.getProfileId());
		sb.append("' target=");
		sb.append(agent.getProfileId());
		sb.append(">");
		sb.append(agent.getName());
		sb.append("</a><br />");
		sb.append("Agent: ").append(agent.getUserName()).append("<br />");
		sb.append("Level: ").append(agent.getLevel()).append("<br />");
		return sb.toString();
	}

	private void addAgents() {
		Main.setWorking(true);
		Main.getService().getAllAgents(new AsyncCallback<ArrayList<Agent>>() {

			public void onSuccess(ArrayList<Agent> agents) {
				for (Agent agent : agents) {
					if (agent.getLatitude() != 0) {
						MarkerOptions markerOptions = MarkerOptions.create();
						LatLng latLng = LatLng.create(agent.getLatitude(), agent.getLongitude());
						markerOptions.setPosition(latLng);
						markerOptions.setMap(map);
						markerOptions.setTitle(agent.getName());
						markerOptions.setIcon(agent.getImageUrl());
						Marker myMarker = Marker.create(markerOptions);
						final InfoWindow infoWindow = InfoWindow.create();
						infoWindow.setPosition(latLng);
						infoWindow.setContent(toHtml(agent));
						InfoWindowOptions infoWindowOptions = InfoWindowOptions.create();
						infoWindowOptions.setPixelOffset(Size.create(0, -15));
						infoWindow.setOptions(infoWindowOptions);
						myMarker.addClickListener(new Marker.ClickHandler() {
							public void handle(MouseEvent event) {
								infoWindow.open(map);
							}
						});
					}
				}
				Main.setWorking(false);
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}
}
