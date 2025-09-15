package com.valmet.watermark.service.impl;

import static java.lang.Math.PI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfPatternCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Tiling;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.Property;
import com.valmet.watermark.config.WatermarkSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


/*
 * @author BJIT
 */
@Service
public class AddWaterMarkToPdfServiceImpl {
    public static String WATERMARK_SEPERATOR = "_watermark_";
    private static SimpleDateFormat dateFormat;
    private static String fileSeparator;
    private static Image img;
    private static String strWaterMarkImageFile = "H://Users/Rafiq/uploads/watermark.png";
    private static final String OPACITY = "OPACITY";
    private static final String LOGO_OPACITY = "LOGO_OPACITY";
    private static final String COLOR_CODE = "COLOR_CODE";
    private static final String X_AXIS = "X_AXIS";
    private static final String Y_AXIS = "Y_AXIS";
    private static final String FONT_NAME = "FONT_NAME";
    private static final String FONT_STYLE = "FONT_STYLE";
    static Tiling tiling = null;
    private static int logoWidth = 0;
    private static int logoHeight = 0;
    private static float rotationInRadians = (float) (PI / 180 * 45f);

    static {
        dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        fileSeparator = System.getProperty("file.separator");
    }

    private WatermarkSettings watermarkSettings = null;

    public AddWaterMarkToPdfServiceImpl(WatermarkSettings watermarkSettings) {
        this.watermarkSettings = watermarkSettings;
    }


    /*
     * This method adds a Water marked image to an existing PDF file.
     *
     * @param document This is the document that has to be written.
     *
     * @param pageIndex This is the Page number.
     *
     * @param img This is the Water marked image.
     *
     * @param graphicState This is the PdfExtGState
     */
    @SuppressWarnings("resource")
    private synchronized void addWatermarkToExistingPDF(Document document, int pageIndex, String strWatermark,
                                                        PdfExtGState graphicState, Image imgLogoWatermark) throws IOException {
        PdfDocument pdfDocument = document.getPdfDocument();
        PdfPage pdfPage = pdfDocument.getPage(pageIndex);
        PageSize pageSize = (PageSize) pdfPage.getPageSizeWithRotation();
        PdfCanvas over = new PdfCanvas(pdfDocument.getPage(pageIndex));
        over.saveState();
        over.setExtGState(graphicState);

        float pageX = pageSize.getLeft() + document.getLeftMargin();
        float pageY = pageSize.getBottom();
        if (pageIndex == 1) {
            img = null;
            logoWidth = 450;
            logoHeight = 400;
            float fontSize = (pageSize.getWidth() + pageSize.getHeight() * 0.8f) / 100;
            if (fontSize >= 45) {
                fontSize = 40;
                logoWidth = 750;
                logoHeight = 700;
            } else if (fontSize >= 40) {
                fontSize = 40;
                logoWidth = 700;
                logoHeight = 650;
            } else if (fontSize >= 35) {
                fontSize = 30;
                logoWidth = 650;
                logoHeight = 600;
            } else if (fontSize >= 30) {
                fontSize = 25;
                logoWidth = 550;
                logoHeight = 500;
            } else if (fontSize >= 20) {
                fontSize = 20;
                logoWidth = 500;
                logoHeight = 450;
            } else if (fontSize <= 13) {
                fontSize = 12;
                logoWidth = 350;
                logoHeight = 300;
            }
            if(!strWatermark.isEmpty()){
                img = getWaterMarkedImageByPdfFontSize(strWatermark, (int) fontSize);
            }

            imgLogoWatermark.setRotationAngle(rotationInRadians);
            tiling = new Tiling(new Rectangle(logoWidth, logoHeight));
            new Canvas(new PdfPatternCanvas(tiling, pdfDocument), tiling.getBBox())
                    .add(imgLogoWatermark);
        }
        if(!strWatermark.isEmpty()){
            img.setFixedPosition(pageIndex, pageX + watermarkSettings.getxAxis(),
                    pageY + watermarkSettings.getyAxis());
            document.add(img);
        }


        new PdfCanvas(pdfPage.newContentStreamAfter(), pdfPage.getResources(), pdfDocument)
                .saveState()
                .setFillColor(new PatternColor(tiling))
                .rectangle(pdfPage.getCropBox())
                .fill().restoreState();
    }


    /*
     * This method creates water marked PDF file.
     *
     * @param inputPdfFilePath This is the input PDF file path.
     *
     * @param outputPdf This is the output PDF file path.
     *
     * @param user This is the System logged user.
     *
     * @param strBaseUrl This is the PDM base URL.
     */
    public synchronized void addWatermarkToExistingPdf(String inputPdf, String outputPdf, String strBaseUrl, String strUserName, String downloadMessage) throws IOException {
        String personNumber = "";
        String strWaterMark = "";
        Map<String, String> mapPdfCustomProperties = null;
        System.out.println("input path==" + inputPdf + ",outputPdf==" + outputPdf + "," + strBaseUrl);
        if (!inputPdf.equalsIgnoreCase(outputPdf)) {
            boolean bIsCorrupted = false;
            try {
                @SuppressWarnings({"unused", "resource"})
                PdfReader pdfReader = new PdfReader(inputPdf);
            } catch (Exception e) {
                bIsCorrupted = true;
                InputStream inputStream = new FileInputStream(inputPdf);
                int read = 0;
                byte[] bytes = new byte[2048];
                OutputStream os = new FileOutputStream(outputPdf);
                while ((read = inputStream.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                os.flush();
                os.close();
                inputStream.close();
            }
            if (!bIsCorrupted) {
                String strKeyWords = "";
                try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf), new PdfWriter(outputPdf))) {
                    PdfDocumentInfo info = pdfDocument.getDocumentInfo();
                    mapPdfCustomProperties = new HashMap<>();
                    ImageData imageData = ImageDataFactory
                            .create(strBaseUrl);
                    Image imgLogoWatermark = new Image(imageData);
                    imgLogoWatermark.setOpacity(watermarkSettings.getLogoOpacity());

                    String strCurrentDate = dateFormat.format(new Date());

                    strKeyWords = "Download Date: " + strCurrentDate;
                    if(strUserName != null && !strUserName.trim().isEmpty()) {
                        strWaterMark = downloadMessage + " " +strUserName;
                        mapPdfCustomProperties.put(downloadMessage, strUserName);
                        strKeyWords = strCurrentDate + ", "+strWaterMark;
                    }
                    System.out.println("Watermark message:"+strWaterMark);
                    mapPdfCustomProperties.put("Download Date", strCurrentDate);
                    info.setMoreInfo(mapPdfCustomProperties);
                    info.setKeywords(strKeyWords);
                    Document document = new Document(pdfDocument);
                    PdfExtGState transparentGraphicState = new PdfExtGState().setFillOpacity(0.5f);
                    for (int i = 1; i <= document.getPdfDocument().getNumberOfPages(); i++) {
                        addWatermarkToExistingPDF(document, i, strWaterMark, transparentGraphicState,
                                imgLogoWatermark);
                    }
                    File file = new File(strWaterMarkImageFile);
                    if (file.exists()) {
                        file.delete();
                    }
                    pdfDocument.close();
                }
            }
        }
    }

    /*
     * This method creates an image with text.
     *
     * @param strWatermark This is the water mark text.
     */
    private void createTextToImage(String strWatermark, int fontSize) {
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = image.createGraphics();
        Font font = new Font(watermarkSettings.getFontName(), getFontStyle(watermarkSettings.getFontStyle()), fontSize);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        int width = fontmetrics.stringWidth(strWatermark);
        int height = fontmetrics.getHeight();

        graphics2d.dispose();

        image = new BufferedImage(width + 8, height, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, watermarkSettings.getOpacity());
        graphics2d.setComposite(alphaChannel);
        graphics2d.setFont(font);
        Color color = Color.decode(watermarkSettings.getColorCode());
        fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(color);
        graphics2d.drawString(strWatermark, 0, fontmetrics.getAscent());
        graphics2d.dispose();
        try {
            ImageIO.write(image, "PNG", new File(strWaterMarkImageFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private int getFontStyle(String style) {
        int intStyle;
        switch (style.toUpperCase()) {
            case "LAYOUT_LEFT_TO_RIGHT":
                intStyle = Font.LAYOUT_LEFT_TO_RIGHT;
                break;
            case "BOLD":
                intStyle = Font.BOLD;
                break;
            case "ITALIC":
                intStyle = Font.ITALIC;
                break;
            case "LAYOUT_RIGHT_TO_LEFT":
                intStyle = Font.LAYOUT_RIGHT_TO_LEFT;
                break;
            case "BOLDITALIC":
            case "BOLD_ITALIC":
                intStyle = Font.BOLD | Font.ITALIC;
                break;
            default:
                intStyle = Font.PLAIN;
        }
        return intStyle;
    }

    private Image getWaterMarkedImageByPdfFontSize(String strWatermark, int fontSize) {
        try {
            createTextToImage(strWatermark, fontSize);
            ImageData imageData = ImageDataFactory.create(strWaterMarkImageFile);
            Image img = new Image(imageData);
            img.setProperty(Property.POSITION, LayoutPosition.FIXED);
            img.setProperty(Property.FLUSH_ON_DRAW, true);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String transferFileToServerpath(MultipartFile file, String uploadDir) {
        try {

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            System.out.println("Line 301 originalFileName==" + originalFileName);
            Path uploadPath = Paths.get(uploadDir + originalFileName);
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            return originalFileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!", e);
        }
    }
}
