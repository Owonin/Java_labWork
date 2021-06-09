package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {
    private final int screenSize;
    private final JImageDisplay display;
    private final FractalGenerator generator;
    private final Rectangle2D.Double range;

    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(400);
        fractalExplorer.creatAndShowGUI();
        fractalExplorer.drawFractal();
    }

    FractalExplorer(int size){
        screenSize = size;
        range = new Rectangle2D.Double();
        generator = new Mandelbrot();
        generator.getInitialRange(range);
        display = new JImageDisplay(size,size);

    }
    private void drawFractal(){
        for(int i = 0; i < screenSize; i++){
            for(int j = 0; j < screenSize; j++){
                double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width, screenSize, i);
                double yCoord = FractalGenerator.getCoord(range.y,range.y + range.height, screenSize, j);
                int iter = generator.numIterations(xCoord,yCoord);
                if (iter == -1)display.drawPixel(i,j,0);
                else {
                    float hue = 0.7f + (float)iter / 200f;
                    display.drawPixel(i,j,Color.HSBtoRGB(hue,1f,1f));
                }
            }
        }
        display.repaint();
    }

    private void creatAndShowGUI(){
        JFrame frame = new JFrame("Fractal");
        JButton button = new JButton("Reset");
        ResetEvent resetEvent = new ResetEvent();
        MouseHandler mouseHandler = new MouseHandler();
        display.addMouseListener(mouseHandler);
        button.addActionListener(resetEvent);
        display.setLayout(new BorderLayout());
        frame.add(display,BorderLayout.CENTER);
        frame.add(button,BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void createAndShowGPU(){
        JFrame jFrame = new JFrame();
        BorderLayout borderLayout = new BorderLayout();
        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setResizable(false);

    }

    private class ResetEvent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            generator.getInitialRange(range);
            drawFractal();
        }
    }
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, screenSize, x);
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, screenSize, y);
            generator.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }



}
