package utilities;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class Settings {

    // private Settings settings=new Settings();
    // settings.Numvalidator(jTextField5);

    public void numvalidatorInt(JTextField txtField)// Number validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }

    public void numvalidatorFloat(JTextField txtField)// Number validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == '.') || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }

    public void numvalidatorDate(JTextField txtField)// Number validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == '/') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }

    public void numvalidatorFloatWithSign(JTextField txtField)// Number validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == '.') || (c == '-') || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }
    
    public void numvalidatorAlphabet(JTextField txtField)// Character validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isAlphabetic(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }
    
    public void numvalidatorAlphabetNSpace(JTextField txtField)// Character & Space validator for key-input
    {
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isAlphabetic(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_SPACE)))
                {
                    e.consume();
                }
            }
        });
    }
    
    public void numvalidatorSimplification(JTextField txtField)// Number validator for key-input
    {
        // Pattern like: 10/20.5*16+2.5-15 should be allowed
        txtField.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || (c == '.') || (c == '+') || (c == '-') || (c == '*') 
                        || (c == '/') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))
                {
                    e.consume();
                }
            }
        });
    }

}
