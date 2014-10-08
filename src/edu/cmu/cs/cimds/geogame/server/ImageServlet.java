package edu.cmu.cs.cimds.geogame.server;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = 8799420962977621681L;
	
	private String requestAction;

	@Override
	public void init(ServletConfig config) throws ServletException {
		requestAction = config.getInitParameter("requestAction");
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.process(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.process(req, resp);
	}
	
	/**
	 * <p>Perform the standard request processing for this request, and create
	 * the corresponding response.</p>
	 *
	 * @param request  The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @throws IOException	  if an input/output error occurs
	 * @throws ServletException if a servlet exception is thrown
	 */
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String imgFilename = "";
		URL imgURL = null;
		try {
			URI requestURI = new URI(request.getRequestURL().toString());
			imgFilename = requestURI.getPath().substring(0, requestURI.getPath().lastIndexOf('.'));
			String urlFilename = imgFilename;
			
			String requestQuery = requestURI.getQuery();
			if(requestQuery!=null && requestQuery.length()==0) {
//			if(!StringUtils.isEmpty(requestURI.getQuery())) {
				urlFilename += "?" + requestURI.getQuery();
			}
			String requestFragment = requestURI.getFragment();
			if(requestFragment!=null && requestFragment.length()==0) {
//			if(!StringUtils.isEmpty(requestURI.getFragment())) {
				urlFilename += "#" + requestURI.getFragment();
			}
			imgURL = new URL(requestURI.getScheme(), requestURI.getHost(), requestURI.getPort(), urlFilename);
		} catch(URISyntaxException ex) {
			//Ignore - filename remains empty and URI remains null.
		}
		if(this.requestAction.equals("transparent")) {
			getTransparentImage(imgURL, response);
		} else if(this.requestAction.equals("text2img")) {
			String renderText = request.getParameter("text");
			String backColorString = request.getParameter("backColor");
			Color backColor = getColorFromHex(backColorString);
			renderText(renderText, backColor, response);
		} else if(this.requestAction.equals("replaceColor")) {
			String searchColorString = request.getParameter("searchColor");
			String replaceColorString = request.getParameter("replaceColor");
			Color searchColor = getColorFromHex(searchColorString);
			Color replaceColor = getColorFromHex(replaceColorString);
			
			if(searchColor == null || replaceColor == null) {
				getOriginalImage(imgURL, response);
			} else {
				replaceColorInImage(imgURL, searchColor, replaceColor, response);
			}
		} else if(this.requestAction.equals("paintColor")) {
			String colorString = request.getParameter("color");
			Color color = getColorFromHex(colorString);
			if(color==null) {
				getOriginalImage(imgURL, response);
			} else {
				paintColorInImage(imgURL, color, response);
			}
		} else if(this.requestAction.equals("hueColor")) {
			String colorString = request.getParameter("color");
			Color color = getColorFromHex(colorString);
			if(color==null) {
				getOriginalImage(imgURL, response);
			} else {
				hueImage(imgURL, color, response);
			}
		}
	}
	
	protected static Color getColorFromHex(String colorHexString) {
		if(colorHexString.charAt(0)=='#') {
			colorHexString = colorHexString.substring(1);
		}
		if(colorHexString.charAt(0)=='#') {
			colorHexString = colorHexString.substring(1);
		}

		try {
			return Color.decode(Integer.valueOf(colorHexString, 16).toString());
		} catch(NumberFormatException ex) {
			return null;
		}
	}
	
	protected static void getOriginalImage(URL imgURL, HttpServletResponse response) throws IOException, ServletException {
		BufferedImage img = ImageIO.read(imgURL);
		writePngToStream(img, response.getOutputStream());
	}

	protected static void renderText(String renderText, Color backColor, HttpServletResponse response) throws IOException, ServletException {
		
		BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = img.createGraphics();
		Font font = new Font("sansserif", Font.BOLD, 14);
		g2D.setFont(font);
		
		FontMetrics fm = g2D.getFontMetrics();

		int imgWidth=4+fm.stringWidth(renderText);
		int imgHeight=4+fm.getHeight();
		img.flush();
		
		img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		g2D = img.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setFont(font);
		
		Color transpBackColor = new Color(backColor.getRed(),backColor.getGreen(),backColor.getBlue(),0xaa);
//		g2D.setPaint(transpBackColor);
//		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, imgWidth, imgHeight);
//		g2D.fill(rect);
		
		Color translucentWhite = new Color(Color.WHITE.getRed(),Color.WHITE.getGreen(),Color.WHITE.getBlue(),0xbb);
		g2D.setPaint(Color.WHITE);
		g2D.setPaintMode();
		g2D.drawString(renderText, 2-1, imgHeight-3-1);
		g2D.drawString(renderText, 2-1, imgHeight-3+1);
		g2D.drawString(renderText, 2+1, imgHeight-3-1);
		g2D.drawString(renderText, 2+1, imgHeight-3+1);
		
		g2D.setPaint(Color.BLACK);
		g2D.setPaintMode();
		g2D.drawString(renderText, 2, imgHeight-3);
		writePngToStream(img, response.getOutputStream());
	}

	protected static void getTransparentImage(URL imgURL, HttpServletResponse response) throws IOException, ServletException {
		BufferedImage img = ImageIO.read(imgURL); 
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		BufferedImage transparentImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		//Clear image with transparent alpha by drawing a rectangle
		Graphics2D g2D = transparentImage.createGraphics();
		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, imgWidth, imgHeight);
		g2D.fill(rect);

		writePngToStream(transparentImage, response.getOutputStream());
	}
	
	protected static void replaceColorInImage(URL imgURL, Color searchColor, Color replaceColor, HttpServletResponse response) throws IOException, ServletException {
		BufferedImage img = ImageIO.read(imgURL); 
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2D = newImage.createGraphics();
		g2D.drawImage(img, 0, 0, null);
		for(int x=0 ; x<imgWidth ; x++) {
			for(int y=0 ; y<imgHeight ; y++) {
				int pixelRgb = newImage.getRGB(x, y);
				Color pixelColor = new Color(pixelRgb, true);
				if(pixelColor.getRed()==searchColor.getRed() && pixelColor.getGreen()==searchColor.getGreen() && pixelColor.getBlue()==searchColor.getBlue()) {
					newImage.setRGB(x, y, replaceColor.getRGB());
				}
			}
		}
		writePngToStream(newImage, response.getOutputStream());
	}

	protected static void paintColorInImage(URL imgURL, Color paintColor, HttpServletResponse response) throws IOException, ServletException {
		BufferedImage img = ImageIO.read(imgURL); 
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2D = newImage.createGraphics();
		g2D.drawImage(img, 0, 0, null);
		for(int x=0 ; x<imgWidth ; x++) {
			for(int y=0 ; y<imgHeight ; y++) {
				int pixelRgb = newImage.getRGB(x, y);
				Color pixelColor = new Color(pixelRgb, true);
				if(pixelColor.getAlpha()==0xff) {
					newImage.setRGB(x, y, paintColor.getRGB());
				}
			}
		}
		writePngToStream(newImage, response.getOutputStream());
	}

	protected static void hueImage(URL imgURL, Color hueColor, HttpServletResponse response) throws IOException, ServletException {
		BufferedImage img = ImageIO.read(imgURL); 
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2D = newImage.createGraphics();
		g2D.drawImage(img, 0, 0, null);
		float[] hueHSBValues = new float[3];
		Color.RGBtoHSB(hueColor.getRed(), hueColor.getGreen(), hueColor.getBlue(), hueHSBValues);
		float[] pixelHSBValues = new float[3];
		for(int x=0 ; x<imgWidth ; x++) {
			for(int y=0 ; y<imgHeight ; y++) {
				int pixelRgb = newImage.getRGB(x, y);
				Color pixelColor = new Color(pixelRgb, true);
				if(pixelColor.getAlpha() > 0) {
					Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelHSBValues);
					
					int newRGB = Color.HSBtoRGB(hueHSBValues[0], pixelHSBValues[1], hueHSBValues[2]);
					newImage.setRGB(x, y, newRGB);
				}
			}
		}
		writePngToStream(newImage, response.getOutputStream());
	}

	private static void writePngToStream(RenderedImage image, OutputStream outputStream) throws IOException {
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
		ImageIO.write(image, "png", bufferedOutputStream);
	}
}