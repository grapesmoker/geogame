package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ProgressBar2 extends HorizontalPanel {
	private AbsolutePanel bar = new AbsolutePanel();
	
	private final static int NUM_BARS = 50;
	private AbsolutePanel[] prog = new AbsolutePanel[NUM_BARS];
	
	public ProgressBar2(){
		this.initialize();
	}
	
	public void initialize(){
		this.setSize("100px", "30px");
		this.setBorderWidth(1);
		
		this.bar.setSize("100px", "30px");
		this.add(this.bar);
		
		for(int i = 0;i<this.prog.length;i++){
			this.prog[i] = new AbsolutePanel();
			this.prog[i].setSize("2px", "30px");
			this.bar.add(this.prog[i],i*2,0);
			this.prog[i].setStylePrimaryName("progresspanel");
		}
	}
	
	public void setProgress(double percentage) {
		int numFullPanels = (int)Math.ceil((percentage+1)*NUM_BARS/100);
		int numEmptyPanels = NUM_BARS - numFullPanels;
		
		for(int i = 0; i < numEmptyPanels; i++){
			this.prog[i].setStylePrimaryName("progresspanel2");
		}
		for(int i = numEmptyPanels;i<NUM_BARS;i++){
			this.prog[i].setStylePrimaryName("progresspanel");
		}
	}
}