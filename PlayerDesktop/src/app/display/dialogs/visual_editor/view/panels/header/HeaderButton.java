package app.display.dialogs.visual_editor.view.panels.header;

import app.display.dialogs.visual_editor.handler.Handler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HeaderButton extends JButton {

    public Color ACTIVE_COLOR;
    public Color INACTIVE_COLOR;
    public Color HOVER_COLOR;

    public ImageIcon ACTIVE_ICON;
    public ImageIcon INACTIVE_ICON;
    public ImageIcon HOVER_ICON;

    private boolean selectable;
    private boolean active;

    public HeaderButton(ImageIcon activeIcon, ImageIcon inactiveIcon, ImageIcon hoverIcon, String text, boolean active, boolean selectable)
    {
        super(text);
        this.active = active;
        this.selectable = selectable;

        this.ACTIVE_ICON = activeIcon;
        this.INACTIVE_ICON = inactiveIcon;
        this.HOVER_ICON = hoverIcon;

        this.ACTIVE_COLOR = Handler.currentPalette().HEADER_BUTTON_ACTIVE_COLOR();
        this.INACTIVE_COLOR = Handler.currentPalette().HEADER_BUTTON_INACTIVE_COLOR();
        this.HOVER_COLOR = Handler.currentPalette().HEADER_BUTTON_HOVER_COLOR();

        if(active)
            setActive();
        else
            setInactive();

        setFont(new Font("Roboto Bold", Font.PLAIN, 12));

        // make transparent
        setBorder(BorderFactory.createEmptyBorder());
        setFocusPainted(false);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);

        addMouseListener(hoverMouseListener);
    }


    public void updateDP()
    {
        if(active)
            setActive();
        else
            setInactive();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        if(enabled) setActive();
        else setInactive();
    }
    public void setActive()
    {
        active = true;
        setForeground(ACTIVE_COLOR);
        setIcon(ACTIVE_ICON);
        repaint();
    }

    public void setInactive()
    {
        active = false;
        setForeground(INACTIVE_COLOR);
        setIcon(INACTIVE_ICON);
        repaint();
    }

    public void setHover()
    {
        setForeground(HOVER_COLOR);
        setIcon(HOVER_ICON);
        repaint();
    }

    MouseListener hoverMouseListener = new MouseAdapter()
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (!active)
            {
                if (selectable) setActive();
            }
            else
            {
                if (selectable)
                {
                    setInactive();
                    Handler.deactivateSelectionMode();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            super.mouseReleased(e);
        }

        public void mouseEntered(MouseEvent e)
        {
            if((selectable && !active) || (!selectable && active))
            {
                setHover();
            }
        }

        public void mouseExited(MouseEvent e)
        {
            if(!active)
            {
                setInactive();
            }
            else
            {
                setActive();
            }
        }
    };

    public boolean isActive() {
        return active;
    }
}
