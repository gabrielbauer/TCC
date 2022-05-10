package app.display.dialogs.visual_editor.view.panels.settings;

import app.display.dialogs.visual_editor.LayoutManagement.LayoutManager.LayoutHandler;
import app.display.dialogs.visual_editor.view.panels.IGraphPanel;

import javax.swing.*;
import java.awt.*;

public class LayoutSettingsPanel extends JPanel
{
    private final JSlider dSl;
    private final JSlider oSl;
    private final JSlider sSl;
    private final LayoutHandler lh;

    public LayoutSettingsPanel(IGraphPanel graphPanel)
    {
        lh = graphPanel.getLayoutHandler();

        dSl = new JSlider(0, 100);
        oSl = new JSlider(0, 100);
        sSl = new JSlider(0, 100);

        JLabel distanceText = new JLabel("Distance: " + getSliderValue(dSl));
        JLabel offsetText = new JLabel("Offset: " + getSliderValue(oSl));
        JLabel spreadText = new JLabel("Spread: " + getSliderValue(sSl));

        Button redraw = new Button("Redraw");
        JCheckBox auto = new JCheckBox("Redraw automatically");

        redraw.addActionListener(e -> {
            updateWeights();
            executeDFSLayout(graphPanel);
        });

        dSl.addChangeListener(e -> {
            distanceText.setText("Distance: " + getSliderValue(dSl));
            if (auto.isSelected()){
                updateWeights();
                executeDFSLayout(graphPanel);
            }
        });

        oSl.addChangeListener(e -> {
            offsetText.setText("Offset: " + getSliderValue(oSl));
            if (auto.isSelected()){
                updateWeights();
                executeDFSLayout(graphPanel);
            }
        });

        sSl.addChangeListener(e -> {
            spreadText.setText("Spread: " + getSliderValue(sSl));
            if (auto.isSelected()){
                updateWeights();
                executeDFSLayout(graphPanel);
            }
        });

        add(distanceText);
        add(dSl);

        add(offsetText);
        add(oSl);

        add(spreadText);
        add(sSl);
        add(redraw);
        add(auto);
    }

    private void updateWeights()
    {
        lh.updateDFSWeights(getSliderValue(oSl),
                getSliderValue(dSl),
                getSliderValue(sSl));
    }

    private double getSliderValue(JSlider slider) {return slider.getValue() / 100.0;}

    private void executeDFSLayout(IGraphPanel graphPanel)
    {
        graphPanel.getLayoutHandler().setLayoutMethod(1);
        graphPanel.getLayoutHandler().executeLayout();
        graphPanel.drawGraph(graphPanel.getGraph());
    }

    public static void getSettingsFrame(IGraphPanel graphPanel)
    {
        JFrame frame = new JFrame("Layout Settings");
        frame.setSize(300, 400);
        frame.add(new LayoutSettingsPanel(graphPanel));
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
