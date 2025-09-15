package com.valmet.watermark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:watermark-settings.properties")
@ConfigurationProperties(prefix = "watermark.settings")
@Data
public class WatermarkSettings {
    private float opacity;
    private float logoOpacity;
    private String colorCode;
    private int xAxis;
    private int yAxis;
    private String fontName;
    private String fontStyle;
    
    
    
	public int getxAxis() {
		return xAxis;
	}
	public void setxAxis(int xAxis) {
		this.xAxis = xAxis;
	}
	public int getyAxis() {
		return yAxis;
	}
	public void setyAxis(int yAxis) {
		this.yAxis = yAxis;
	}
//	public float getxAxis() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
    
    
}
