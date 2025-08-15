package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    // Colores modernos
    private final Color PRIMARY_COLOR = new Color(3, 36, 239); // Azul moderno
    private final Color SECONDARY_COLOR = new Color(228, 18, 18); // Rojo moderno
    private final Color BACKGROUND_COLOR = new Color(243, 165, 35); // Fondo claro

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
        showWelcomeMessage();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel principal con márgenes
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(mainPanel);

        // Configuración de la tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        customizeTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones con GridBagLayout para mejor control
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Botón Agregar con estilo moderno y redondeado
        JButton addBtn = createRoundedButton("Agregar Contacto", PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddContactDialog());
        buttonPanel.add(addBtn, gbc);

        gbc.gridx = 1;
        // Botón Eliminar con estilo moderno y redondeado
        JButton deleteBtn = createRoundedButton("Eliminar Contacto", SECONDARY_COLOR);
        deleteBtn.addActionListener(e -> deleteSelectedContact());
        buttonPanel.add(deleteBtn, gbc);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createRoundedButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += 30;
                size.height = 40;
                return size;
            }
        };

        // Estilo del botón
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(color, 20, 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
                button.setBorder(new RoundedBorder(color.darker().darker(), 20, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setBorder(new RoundedBorder(color, 20, 2));
            }
        });

        return button;
    }

    // Clase para bordes redondeados
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius, radius/2, radius);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = radius;
            insets.top = insets.bottom = radius/2;
            return insets;
        }
    }

    private void customizeTable() {
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);

        table.setFont(tableFont);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(19, 19, 19));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(25, 24, 24));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Encabezado de tabla personalizado
        JTableHeader header = table.getTableHeader();
        header.setFont(tableFont.deriveFont(Font.BOLD));
        header.setBackground(new Color(237, 237, 237));
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
            showToast("Contacto agregado con éxito!", PRIMARY_COLOR);
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un contacto para eliminar",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmar eliminación del contacto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            showToast("Contacto eliminado con éxito!", SECONDARY_COLOR);
        }
    }

    private void showToast(String message, Color color) {
        JDialog toast = new JDialog();
        toast.setUndecorated(true);
        toast.setSize(300, 50);
        toast.setLocationRelativeTo(this);
        toast.setShape(new RoundRectangle2D.Double(0, 0, 300, 50, 20, 20));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(color, 20, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(color);
        panel.add(label, BorderLayout.CENTER);

        toast.add(panel);

        Timer timer = new Timer(2000, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();

        toast.setVisible(true);
    }

    private void showWelcomeMessage() {
        JOptionPane.showMessageDialog(
                this,
                "<html><div style='text-align: center;'>"
                        + "<h2 style='color: " + colorToHex(PRIMARY_COLOR) + ";'>Bienvenido a la Agenda</h2>"
                        + "<p>Gestiona tus contactos de manera fácil y eficiente</p>"
                        + "</div></html>",
                "Bienvenida",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}