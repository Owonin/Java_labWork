package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class FractalExplorer {
    private final int screenSize;
    JButton resetButton;
    JButton saveButton;
    JComboBox<FractalGenerator> comboBox;
    private int rowRemaining;
    private final JImageDisplay display;
    private FractalGenerator generator;
    private final Rectangle2D.Double range;

    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(400);
        fractalExplorer.creatAndShowGUI();
        fractalExplorer.drawFractal();
    }

    FractalExplorer(int size) {
        screenSize = size;
        range = new Rectangle2D.Double();
        generator = new Mandelbrot();
        generator.getInitialRange(range);
        display = new JImageDisplay(size, size);

    }

    private void drawFractal() {
        rowRemaining = screenSize;
        enableUI(false);
        for(int i = 0; i < screenSize; i++){
            FractalWorker fractalWorker = new FractalWorker(i);
            fractalWorker.execute();
        }
    }

    private void creatAndShowGUI() {
        Mandelbrot mandelbrot = new Mandelbrot();
        Tricorn tricorn = new Tricorn();
        BurningShip burningShip = new BurningShip();

        resetButton = new JButton("Reset");
        saveButton = new JButton("Save Image");
        comboBox = new JComboBox<FractalGenerator>();
        JFrame frame = new JFrame("Fractal");
        JLabel label = new JLabel("Fractal:");
        comboBox = new JComboBox<>();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        comboBox.addItem(mandelbrot);
        comboBox.addItem(tricorn);
        comboBox.addItem(burningShip);

        resetButton.setActionCommand("reset");
        saveButton.setActionCommand("save");

        ActionListener listener = e -> {
            Object object = e.getSource();
            if (object instanceof JComboBox) {
                generator = (FractalGenerator) ((JComboBox) object).getSelectedItem();
                generator.getInitialRange(range);
                drawFractal();
            } else if (object instanceof JButton) {
                JButton button = (JButton) object;
                if (button.getActionCommand().equals("reset")) {
                    generator.getInitialRange(range);
                    drawFractal();
                } else if (button.getActionCommand().equals("save")) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    if (fileChooser.showSaveDialog(button.getParent())
                            != JFileChooser.APPROVE_OPTION) return;
                    try {
                        ImageIO.write(display.getBufferedImage(), "png",
                                fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(button.getParent(), ex.getMessage(),
                                "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(rowRemaining != 0)return;
                int x = e.getX();
                int y = e.getY();
                double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, screenSize, x);
                double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, screenSize, y);
                generator.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
                drawFractal();
            }
        };

        display.addMouseListener(mouseAdapter);
        resetButton.addActionListener(listener);
        saveButton.addActionListener(listener);
        comboBox.addActionListener(listener);

        panel1.add(label);
        panel1.add(comboBox);
        panel2.add(saveButton);
        panel2.add(resetButton);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(display, BorderLayout.CENTER);
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.SOUTH);
        frame.setSize(screenSize, screenSize + 100);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void enableUI(boolean val){
        resetButton.setEnabled(val);
        saveButton.setEnabled(val);
        comboBox.setEnabled(val);
    }

    private class FractalWorker extends SwingWorker<Object, Object>{
        private final int y;
        private int[] pixelRow;

        FractalWorker(int yCoord){
            y = yCoord;
        }


        @Override
        protected Object doInBackground() throws Exception {
            pixelRow = new int[screenSize];
            for(int i = 0; i < screenSize; i++){
                double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width, screenSize, i);
                double yCoord = FractalGenerator.getCoord(range.y,range.y + range.height, screenSize, y);
                int iter = generator.numIterations(xCoord,yCoord);
                if (iter == -1)pixelRow[i] = 0;
                else {
                    float hue = 0.7f + (float) iter / 200f;
                    pixelRow[i] = Color.HSBtoRGB(hue, 1f, 1f);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            for(int i = 0; i < screenSize; i++){
                display.drawPixel(i,y,pixelRow[i]);
            }
            display.repaint(0,0,y,screenSize,1);
            rowRemaining--;
            if(rowRemaining == 0) enableUI(true);

        }
    }

}
