//package edu.cmu.cs.cimds.geogame.client.ui.map; 
//import com.google.gwt.user.client.DOM;
//public class MyGOverlay extends GAbstractOverlay { 
//        protected GMap2 map;
//        protected GLatLng pos;
//        protected String label; 
//        public static int number = 100; 
//        public MyGOverlay(String label, GLatLng pos) 
//        { 
//                super(); 
//                this.pos = pos; 
//                this.label = label; 
//                setElement(new Label(this.label).getElement()); 
//                                //Make sure you call init() after 
//you've set the Element... if you don't 
//                                //this won't work. 
//                init(); 
//        } 
//        public GMapPane defaultMapPane() { 
//                return GMapPane.G_MAP_MARKER_PANE(); 
//        } 
//        public GOverlay onCopy() { 
//                return new MyGOverlay(label, pos); 
//        } 
//        public void onInitialize(GMap2 map) { 
//                this.map = map; 
//                DOM.setStyleAttribute(getElement(), "zIndex", 
//GOverlay.getZIndex(pos.lat())+""); 
//        } 
//        public void onRedraw(boolean force) { 
//        // We only need to redraw if the coordinate system has 
//                // changed 
//        if (!force) return; 
//        // Calculate the DIV coordinates of two opposite corners of 
//                // our bounds to 
//        // get the size and position of our rectangle 
//        GPoint point = map.fromLatLngToDivPixel(pos); 
//        // Now position our DIV based on the DIV coordinates of our 
//bounds 
//        DOM.setStyleAttribute(getElement(), "left", point.getX() + 
//"px"); 
//        DOM.setStyleAttribute(getElement(), "top", point.getY() + 
//"px"); 
//        DOM.setStyleAttribute(getElement(), "position", "absolute"); 
//        } 
//        public void onRemove() { 
//                // TODO Auto-generated method stub 
//        } 