package com.in28minutes.rest.webservices.restfulwebservices.helloworld;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.in28minutes.rest.webservices.restfulwebservices.jwt.JwtTokenService;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.valmet.watermark.service.AddWaterMarkToPdfService;

@RestController
@RequestMapping("/api")

public class HelloWorldController {

	
	private static final double PI = 3.1416;

	@GetMapping(path = "/basicauth")
	public String basicAuthCheck() {
		return "Success"; 
	}
	private final JwtTokenService tokenService;
	


	public HelloWorldController(JwtTokenService tokenService) {
		super();
		this.tokenService = tokenService;
	}

	@GetMapping(path = "/hello-world")
	public ResponseEntity<String> helloWorld(@RequestHeader("Authorization") String token) {
//		if (!tokenService.isAccessToken(token)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token type");
//        }
		
		return  new ResponseEntity<String>("Hello World v2", null, 200); 
	}
	
//	@GetMapping(path = "/hello-world-bean")
//	public HelloWorldBean helloWorldBean() {
//		return new HelloWorldBean("Hello World Bean v1"); 
//	}
	
	@GetMapping(path = "/hello-world/path-variable/{name}")
	public HelloWorldBean helloWorldPathVariable(@PathVariable String name ) {

		System.out.println("addWatermarkToExistingPdf");
//		try {
//			addWatermarkToExistingPdf();
//		} catch (IOException | java.io.IOException e) {
//			e.printStackTrace();
//		}
		
	 return new HelloWorldBean(String.format("Hello World, %s", "BJIT")+ " "+generation("tell me about BJIT LTD")); 
//		return new HelloWorldBean(String.format("Hello World, %s", "BJIT")); 

	}	
	
    private ChatClient chatClient;

    @Bean
    public ChatClient HelloWorldController(ChatClient.Builder chatClientBuilder) {
         chatClient = chatClientBuilder.build();
         return chatClient;
    }

   // @GetMapping("/ai")
    String generation(String userInput) {
        return this.chatClient.prompt()
            .user(userInput)
            .call()
            .content();
    }
    @Bean
    public AddWaterMarkToPdfService addWaterMarkToPdfService() {
        return new AddWaterMarkToPdfService(null);
    }

	
	public void addWatermarkToExistingPDF(Document document, int pageIndex,
			  Paragraph paragraph, PdfExtGState graphicState, float verticalOffset) throws MalformedURLException {
			    
			    PdfDocument pdfDocument = document.getPdfDocument();
			   
			    PdfPage pdfPage = pdfDocument.getPage(pageIndex);
			   // pdfPage.setRotation(360);
			    PageSize pageSize = (PageSize) pdfPage.getPageSizeWithRotation();
			    //pageSize.s
			    //pageSize.rotate();
			    float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
			    float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
			    
			    PdfCanvas over = new PdfCanvas(pdfDocument.getPage(pageIndex));
			    over.saveState();
			    over.setExtGState(graphicState);
			    float xOffset = 14 / 2;
			  //  float rotationInRadians = (float) (PI / 180 * 45f);
			    float rotationInRadians =0;
			    
				System.out.println("rotationInRadians"+rotationInRadians);
			    
				System.out.println("xOffset"+xOffset);
				System.out.println("x"+x);
				System.out.println("y"+y);
				System.out.println("pageIndex"+pageIndex);

//			    
//			    document.showTextAligned(paragraph, x-692, y-792, 
//			      pageIndex, TextAlignment.CENTER, VerticalAlignment.BOTTOM, rotationInRadians);
			    
			    //document.
			    
			    
			       ImageData imageData = ImageDataFactory.create( 
			    		   "D:\\Sample.jpg"); 
			           
			           // Creating imagedata from image on disk(from given 
			           // path) using ImageData object 
			       

			       
			           //Image img = new Image(imageData,x - xOffset, y + verticalOffset, 350f);
			      Image img = new Image(imageData);
			   img= getWatermarkedImage(pdfDocument,img, "");
			    //   Image img = new Image(imageData,850, 50, 750f);
			   
			   img.setProperty(Property.LEFT, 850);
			   img.setProperty(Property.BOTTOM, 50);
			   img.setWidth(750f);
			   img.setProperty(Property.POSITION, LayoutPosition.FIXED);
			   img. setProperty(Property.FLUSH_ON_DRAW, true);

			    document.add(img);
			  //  com.itextpdf.kernel.pdf.PdfOutputIntent sdf=new PdfOutputIntent();
			    //document.getPdfDocument().setXmpMetadata(null)
			    
			    
			    document.flush();
			    over.restoreState();
			    over.release();
			    
			}
	
	public void addWatermarkToExistingPdf() throws IOException, FileNotFoundException, java.io.IOException {
	    
	    String inputPdf = "D:\\RAUZ262080.PDF";
	    
        String text = "Downloaded by User 10310/matiul.alam@valmetpartners.com";
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);// Represents an image with 8-bit RGBA color components packed into integer pixels.
        Graphics2D graphics2d = image.createGraphics();
        Font font = new Font("TimesNewRoman", Font.LAYOUT_LEFT_TO_RIGHT, 50);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        int width = fontmetrics.stringWidth(text);
        int height = fontmetrics.getHeight();
        graphics2d.dispose();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setFont(font);
        fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.gray);
        graphics2d.drawString(text, 0, fontmetrics.getAscent());
        graphics2d.dispose();
        try {
            ImageIO.write(image, "png", new File("D:/Sample.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	    
	    
	    int count=0;
	    String outputPdf = "D:\\RAUZ262080_"+(++count)+".PDF";

	    File f=new File(outputPdf);
	    
	    while( f.exists()) {
		     outputPdf = "D:\\RAUZ262080_"+(++count)+".PDF";
		     f=new File(outputPdf);
	    }

	  //  String watermark = "Downloaded by User 10310/matiul.alam@valmetpartners.com";
	    
	    try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf), 
	      new PdfWriter(outputPdf))) {
	        Document document = new Document(pdfDocument);
	        Paragraph paragraph = createWatermarkParagraph("tests");
	        PdfExtGState transparentGraphicState = new PdfExtGState().setFillOpacity(0.5f);
	        for (int i = 1; i <= document.getPdfDocument().getNumberOfPages(); i++) {
	            addWatermarkToExistingPDF(document, i, paragraph, 
	              transparentGraphicState, 0f);
	        }

	        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
//		    info.setTitle("The Strange Case of Dr. Jekyll and Mr. Hyde");
//		    info.setAuthor("Robert Louis Stevenson");
//		    info.setSubject("A novel");
//		    info.setKeywords("Dr. Jekyll, Mr. Hyde");
//		    info.setCreator("A simple tutorial example");
		    info.setMoreInfo("Downloaded By", "matiul.alam/10310");
	        
	        
	    }
	    

	}
    private AddWaterMarkToPdfService addWaterMarkToPdfService = null;

    @PostMapping(value = "/watermark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                         @RequestParam(value = "userName", required = false) String strUserName) throws IOException {
        System.out.println("Line 20 Calll.............file size=" + files.size() + "," + strUserName);
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return addWaterMarkToPdfService.getWatermarkedPdf(files, strUserName);

    }
	
	public Paragraph createWatermarkParagraph(String watermark) throws IOException, java.io.IOException {
	    
	    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

	    Text text = new Text(watermark);
	    text.setFont(font);
	    text.setFontSize(30);
	    text.setOpacity(0.25f);
	    return new Paragraph(text);
	}
	
    private static Image getWatermarkedImage(PdfDocument pdfDoc, Image img, String watermark) {
        float width = img.getImageScaledWidth();
        float height = img.getImageScaledHeight();
        PdfFormXObject template = new PdfFormXObject(new Rectangle(width, height));
        new Canvas(template, pdfDoc)
                .add(img)
                .setFontColor(DeviceGray.WHITE)
                .showTextAligned(watermark, width / 2, height / 2, TextAlignment.CENTER, (float) Math.PI / 6);
                //.close();
        return new Image(template);
    }
}
