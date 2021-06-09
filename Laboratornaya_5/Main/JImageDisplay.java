package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class JImageDisplay extends JComponent {
    private final BufferedImage bufferedImage;

    JImageDisplay(int width, int height){
        this.bufferedImage = new BufferedImage(width, height, 8);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent (Graphics g){
        super.paintComponent(g);
        g.drawImage (bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        //return g;
    }
    public void clearImage(){
        for(int i = 0; i < bufferedImage.getHeight(); i++){
            for(int j = 0; j < bufferedImage.getWidth(); j++){
                bufferedImage.setRGB(i,j,0);
            }
        }
    }

    public void drawPixel(int x, int y, int rgbColor){
        bufferedImage.setRGB(x,y,rgbColor);
    }

    public RenderedImage getBufferedImage() {
        return bufferedImage;
    }

}
