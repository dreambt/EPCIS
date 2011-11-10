package org.fosstrak.epcis.captureclient;

import org.fosstrak.epcis.captureclient.CaptureClientHelper.EpcisEventType;
import org.fosstrak.epcis.captureclient.CaptureClientHelper.ExampleEvents;
import org.fosstrak.epcis.captureclient.CaptureEvent.BizTransaction;
import org.fosstrak.epcis.gui.AuthenticationOptionsChangeEvent;
import org.fosstrak.epcis.gui.AuthenticationOptionsChangeListener;
import org.fosstrak.epcis.gui.AuthenticationOptionsPanel;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * GUI class for the EPCIS Capture Interface Client. Implements the GUI and the
 * creation of XML from the GUI data.
 */
public class CaptureClientGui extends WindowAdapter implements ActionListener, AuthenticationOptionsChangeListener {

    /**
     * The client through which the EPCISEvents will be sent to the repository's
     * Capture Operations Module.
     */
    private CaptureClient client;

    /*
     * These lists hold the input fields for the BizTransactions. The lists are
     * modified by the user to allow for as many arguments as the user wants.
     * Objects may be deleted from these lists by pressing the "-" Button.
     */
    private ArrayList<JTextField> mwBizTransTypeFields;
    private ArrayList<JTextField> mwBizTransIDFields;
    private ArrayList<JButton> mwBizTransButtons;

    /* main window */
    private JFrame mainWindow;
    private JPanel mwMainPanel;
    private AuthenticationOptionsPanel mwAuthOptions;
    private JPanel mwConfigPanel;
    private JPanel mwEventTypePanel;
    private JPanel mwEventDataPanel;
    private JPanel mwEventDataInputPanel;
    private JPanel mwEventDataExamplesPanel;
    private JPanel mwButtonPanel;
    private JLabel mwServiceUrlLabel;

    private JTextField mwServiceUrlTextField;
    private JComboBox mwEventTypeChooserComboBox;
    private JCheckBox mwShowDebugWindowCheckBox;

    /* the BizTransaction field */
    private JPanel mwBizTransactionPanel;
    private JButton mwBizTransactionPlus;

    /* the event time field */
    private JLabel mwEventTimeLabel;
    private JTextField mwEventTimeTextField;
    private JLabel mwEventTimeZoneOffsetLabel;
    private JTextField mwEventTimeZoneOffsetTextField;

    /* the action type drop-down box */
    private JLabel mwActionLabel;
    private JComboBox mwActionComboBox;

    /* fields for the various URIs */
    private JLabel mwBizStepLabel;
    private JTextField mwBizStepTextField;
    private JLabel mwDispositionLabel;
    private JTextField mwDispositionTextField;
    private JLabel mwReadPointLabel;
    private JTextField mwReadPointTextField;
    private JLabel mwBizLocationLabel;
    private JTextField mwBizLocationTextField;
    private JLabel mwBizTransactionLabel;
    private JTextField mwBizTransactionTextField;

    /* associated EPCs for object events */
    private JLabel mwEpcListLabel;
    private JTextField mwEpcListTextField;

    /* parent EPC field for aggregation events */
    private JLabel mwParentIDLabel;
    private JTextField mwParentIDTextField;

    /* associated EPCs for aggregation events */
    private JLabel mwChildEPCsLabel;
    private JTextField mwChildEPCsTextField;

    /* EPC class for quantity events */
    private JLabel mwEpcClassLabel;
    private JTextField mwEpcClassTextField;

    /* quantity for quantity events */
    private JLabel mwQuantityLabel;
    private JTextField mwQuantityTextField;

    /* buttons */
    private JButton mwFillInExampleButton;
    private JButton mwGenerateEventButton;

    /**
     * example selection window.
     */
    private JFrame exampleWindow;

    private JPanel ewMainPanel;
    private JPanel ewListPanel;
    private JPanel ewButtonPanel;
    private JList ewExampleList;
    private JScrollPane ewExampleScrollPane;
    private JButton ewOkButton;

    /* debug window */
    private JFrame debugWindow;

    private JTextArea dwOutputTextArea;
    private JScrollPane dwOutputScrollPane;
    private JPanel dwButtonPanel;
    private JButton dwClearButton;

    /**
     * Constructs a new CaptureClientGui initialized with a default address.
     */
    public CaptureClientGui() {
        this(null);
    }

    /**
     * Constructs a new CaptureClientGui initialized with the given address.
     *
     * @param address The address to which the CaptureClient should sent its capture
     *                events.
     */
    public CaptureClientGui(final String address) {
        this.client = new CaptureClient(address);
        initWindow();
    }

    /**
     * Initializes the GUI window.
     */
    private void initWindow() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        mainWindow = new JFrame("EPCIS 捕获接口演示");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);

        mwMainPanel = new JPanel();
        mwMainPanel.setLayout(new BoxLayout(mwMainPanel, BoxLayout.PAGE_AXIS));
        mwMainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mwConfigPanel = new JPanel(new GridBagLayout());
        mwMainPanel.add(mwConfigPanel);
        mwEventTypePanel = new JPanel();
        mwMainPanel.add(mwEventTypePanel);
        mwEventDataPanel = new JPanel();
        mwEventDataPanel.setLayout(new BoxLayout(mwEventDataPanel, BoxLayout.PAGE_AXIS));
        mwMainPanel.add(mwEventDataPanel);
        mwButtonPanel = new JPanel();
        mwMainPanel.add(mwButtonPanel);

        mwConfigPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("基本配置"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwEventTypePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("事件类型"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwEventDataPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("事件参数"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwServiceUrlLabel = new JLabel("捕获接口 URL: ");
        mwServiceUrlTextField = new JTextField(client.getCaptureUrl(), 75);
        mwAuthOptions = new AuthenticationOptionsPanel(this);

        mwServiceUrlTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
            }

            public void insertUpdate(DocumentEvent e) {
                configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
            }

            public void removeUpdate(DocumentEvent e) {
                configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
            }

            public boolean isComplete() {
                String url = mwServiceUrlTextField.getText();
                return url != null && url.length() > 0;
            }

        });

        mwShowDebugWindowCheckBox = new JCheckBox("显示 Debug 窗口", false);
        mwShowDebugWindowCheckBox.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 0);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwConfigPanel.add(mwServiceUrlLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        mwConfigPanel.add(mwServiceUrlTextField, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mwConfigPanel.add(mwAuthOptions, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        mwConfigPanel.add(mwShowDebugWindowCheckBox, c);

        mwEventTypeChooserComboBox = new JComboBox(EpcisEventType.guiNames());
        mwEventTypeChooserComboBox.addActionListener(this);
        mwEventTypePanel.add(mwEventTypeChooserComboBox);

        mwGenerateEventButton = new JButton("产生事件");
        mwGenerateEventButton.addActionListener(this);
        mwButtonPanel.add(mwGenerateEventButton);

        // instantiate all event data input fields, their default values and
        // descriptions
        mwEventTimeLabel = new JLabel("时间");
        mwEventTimeTextField = new JTextField(CaptureClientHelper.format(Calendar.getInstance()).substring(0, 23));
        mwEventTimeTextField.setToolTipText(CaptureClientHelper.toolTipDate);

        mwEventTimeZoneOffsetLabel = new JLabel("时区");
        mwEventTimeZoneOffsetTextField = new JTextField(CaptureClientHelper.getTimeZone(Calendar.getInstance()));

        mwActionLabel = new JLabel("操作");
        mwActionComboBox = new JComboBox(CaptureClientHelper.ACTIONS);

        mwBizStepLabel = new JLabel("业务步骤");
        mwBizStepTextField = new JTextField();
        mwBizStepTextField.setToolTipText(CaptureClientHelper.toolTipUri + CaptureClientHelper.toolTipOptional);

        mwDispositionLabel = new JLabel("状态");
        mwDispositionTextField = new JTextField();
        mwDispositionTextField.setToolTipText(CaptureClientHelper.toolTipUri + CaptureClientHelper.toolTipOptional);

        mwReadPointLabel = new JLabel("阅读点");
        mwReadPointTextField = new JTextField();
        mwReadPointTextField.setToolTipText(CaptureClientHelper.toolTipUri + CaptureClientHelper.toolTipOptional);

        mwBizLocationLabel = new JLabel("业务位置");
        mwBizLocationTextField = new JTextField();
        mwBizLocationTextField.setToolTipText(CaptureClientHelper.toolTipUri + CaptureClientHelper.toolTipOptional);

        mwBizTransactionLabel = new JLabel("业务事务");
        mwBizTransactionTextField = new JTextField();
        mwBizTransactionTextField.setToolTipText(CaptureClientHelper.toolTipUri + CaptureClientHelper.toolTipOptional);

        mwEpcListLabel = new JLabel("EPCs");
        mwEpcListTextField = new JTextField();
        mwEpcListTextField.setToolTipText(CaptureClientHelper.toolTipUris);

        mwParentIDLabel = new JLabel("父 EPC");
        mwParentIDTextField = new JTextField();
        mwParentIDTextField.setToolTipText(CaptureClientHelper.toolTipUri);

        mwChildEPCsLabel = new JLabel("子 EPCs");
        mwChildEPCsTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(CaptureClientHelper.toolTipUris + CaptureClientHelper.toolTipOptional);

        mwEpcClassLabel = new JLabel("EPC 分类");
        mwEpcClassTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(CaptureClientHelper.toolTipUri);

        mwQuantityLabel = new JLabel("质量");
        mwQuantityTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(CaptureClientHelper.toolTipInteger);

        mwBizTransTypeFields = new ArrayList<JTextField>();
        mwBizTransIDFields = new ArrayList<JTextField>();
        mwBizTransButtons = new ArrayList<JButton>();

        mwBizTransactionPanel = new JPanel(new GridBagLayout());
        ImageIcon tempImageIcon = CaptureClientHelper.getImageIcon("new10.gif");
        mwBizTransactionPlus = new JButton(tempImageIcon);
        mwBizTransactionPlus.setMargin(new Insets(0, 0, 0, 0));
        mwBizTransactionPlus.addActionListener(this);

        addBizTransactionRow();

        mwEventDataInputPanel = new JPanel(new GridBagLayout());
        mwEventDataExamplesPanel = new JPanel(new BorderLayout());
        mwEventDataPanel.add(mwEventDataInputPanel);
        mwEventDataPanel.add(mwEventDataExamplesPanel);

        drawEventDataPanel(EpcisEventType.ObjectEvent);
        mwFillInExampleButton = new JButton("载入例子");
        mwFillInExampleButton.addActionListener(this);
        mwEventDataExamplesPanel.add(mwFillInExampleButton, BorderLayout.EAST);

        /* draw window */
        mainWindow.getContentPane().add(mwMainPanel);
        mainWindow.pack();
        mainWindow.setVisible(true);

        drawDebugWindow();
    }

    private void drawEventDataPanel(EpcisEventType eventType) {
        mwEventDataInputPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 5, 0);

        c.gridy = 0;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwEventTimeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeTextField, c);

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwEventTimeZoneOffsetLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeZoneOffsetTextField, c);

        if (EpcisEventType.ObjectEvent == eventType || EpcisEventType.TransactionEvent == eventType) {
            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwEpcListLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwEpcListTextField, c);
        }

        if (EpcisEventType.AggregationEvent == eventType || EpcisEventType.TransactionEvent == eventType) {
            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwParentIDLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwParentIDTextField, c);
        }

        if (EpcisEventType.AggregationEvent == eventType) {
            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwChildEPCsLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwChildEPCsTextField, c);
        }

        if (EpcisEventType.QuantityEvent == eventType) {
            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwEpcClassLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwEpcClassTextField, c);

            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwQuantityLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwQuantityTextField, c);
        } else {
            c.gridy++;
            c.weightx = 0;
            c.gridx = 0;
            mwEventDataInputPanel.add(mwActionLabel, c);
            c.weightx = 1;
            c.gridx = 1;
            mwEventDataInputPanel.add(mwActionComboBox, c);
        }

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwBizStepLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizStepTextField, c);

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwDispositionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwDispositionTextField, c);

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwReadPointLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwReadPointTextField, c);

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwBizLocationLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizLocationTextField, c);

        c.gridy++;
        c.weightx = 0;
        c.gridx = 0;
        mwEventDataInputPanel.add(mwBizTransactionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizTransactionPanel, c);
    }

    /**
     * Sets up the window used to show the debug output.
     */
    private void drawDebugWindow() {
        debugWindow = new JFrame("Debug 输出信息");
        debugWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        debugWindow.addWindowListener(this);
        debugWindow.setLocation(200, 50);
        debugWindow.setSize(700, 800);

        dwOutputTextArea = new JTextArea();
        dwOutputScrollPane = new JScrollPane(dwOutputTextArea);
        debugWindow.add(dwOutputScrollPane);

        dwButtonPanel = new JPanel();
        debugWindow.add(dwButtonPanel, BorderLayout.AFTER_LAST_LINE);

        dwClearButton = new JButton("清空");
        dwClearButton.addActionListener(this);
        dwButtonPanel.add(dwClearButton);
    }

    /**
     * Sets up the window used to show the list of examples. Can only be open
     * once.
     */
    private void drawExampleWindow() {
        if (exampleWindow != null) {
            exampleWindow.setVisible(true);
            return;
        }
        exampleWindow = new JFrame("载入例子");
        exampleWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        exampleWindow.setLocation(500, 100);
        exampleWindow.setSize(350, 400);

        ewMainPanel = new JPanel();
        ewMainPanel.setLayout(new BoxLayout(ewMainPanel, BoxLayout.PAGE_AXIS));
        exampleWindow.add(ewMainPanel);

        ewListPanel = new JPanel();
        ewListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        ewListPanel.setLayout(new BoxLayout(ewListPanel, BoxLayout.PAGE_AXIS));

        ewButtonPanel = new JPanel();
        ewButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        ewButtonPanel.setLayout(new BoxLayout(ewButtonPanel, BoxLayout.LINE_AXIS));

        ewMainPanel.add(ewListPanel);
        ewMainPanel.add(ewButtonPanel);

        ewExampleList = new JList();
        ewExampleScrollPane = new JScrollPane(ewExampleList);
        ewListPanel.add(ewExampleScrollPane);
        ewExampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] exampleList = new String[ExampleEvents.getExamples().size()];
        for (int i = 0; i < ExampleEvents.getExamples().size(); i++) {
            exampleList[i] = ExampleEvents.getExamples().get(i).getDescription();
        }
        ewExampleList.setListData(exampleList);

        ewOkButton = new JButton("载入");
        ewOkButton.addActionListener(this);
        ewButtonPanel.add(Box.createHorizontalGlue());
        ewButtonPanel.add(ewOkButton);
        ewButtonPanel.add(Box.createHorizontalGlue());

        //exampleWindow.pack();
        exampleWindow.setVisible(true);
    }

    /**
     * Event dispatcher. Very simple events may be processed directly within
     * this method.
     *
     * @param e for the Action
     */
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == mwEventTypeChooserComboBox) {
            mwEventTypeChooserComboBoxChanged();
            return;
        }
        if (e.getSource() == mwGenerateEventButton) {
            mwGenerateEventButtonPressed();
            return;
        }
        if (e.getSource() == mwFillInExampleButton) {
            drawExampleWindow();
            return;
        }
        if (e.getSource() == ewOkButton) {
            ewOkButtonPressed();
            return;
        }
        if (e.getSource() == dwClearButton) {
            dwOutputTextArea.setText("");
        }
        if (e.getSource() == mwShowDebugWindowCheckBox) {
            debugWindow.setVisible(mwShowDebugWindowCheckBox.isSelected());
            return;
        }
        if (e.getSource() == mwBizTransactionPlus) {
            addBizTransactionRow();
        }
        // check, if it is a JButton and second, if its name starts with
        // "removeBizTransNumber<Number>".
        if (((JButton) e.getSource()).getName() != null
                && ((JButton) e.getSource()).getName().startsWith("removeBizTransNumber")) {
            removeBizTransactionRow((JButton) e.getSource());
        }
    }

    /**
     * The user pushed the Ok button in the example window. Apply the chosen
     * example to the GUI.
     */
    private void ewOkButtonPressed() {
        int selected = ewExampleList.getSelectedIndex();
        if (selected >= 0) {
            CaptureEvent ex = ExampleEvents.getExamples().get(selected);

            mwEventTimeTextField.setText(ex.getEventTime());
            mwEventTimeZoneOffsetTextField.setText(ex.getEventTimeZoneOffset());
            if (ex.getAction() >= 0 && ex.getAction() <= 3) {
                mwActionComboBox.setSelectedIndex(ex.getAction());
            }
            mwBizStepTextField.setText(ex.getBizStep());
            mwDispositionTextField.setText(ex.getDisposition());
            mwReadPointTextField.setText(ex.getReadPoint());
            mwBizLocationTextField.setText(ex.getBizLocation());

            ArrayList<BizTransaction> bizTrans = new ArrayList<BizTransaction>();
            bizTrans = ex.getBizTransaction();
            // erase all what has been.
            mwBizTransTypeFields = new ArrayList<JTextField>();
            mwBizTransIDFields = new ArrayList<JTextField>();
            mwBizTransButtons = new ArrayList<JButton>();
            int i = 0;
            for (BizTransaction transaction : bizTrans) {
                addBizTransactionRow();
                mwBizTransTypeFields.get(i).setText(transaction.getBizTransType());
                mwBizTransIDFields.get(i).setText(transaction.getBizTransID());
                i++;
            }
            drawBizTransaction();

            mwEpcListTextField.setText(ex.getEpcList());
            mwChildEPCsTextField.setText(ex.getChildEPCs());
            mwParentIDTextField.setText(ex.getParentID());
            mwEpcClassTextField.setText(ex.getEpcClass());
            if (ex.getQuantity() >= 0) {
                mwQuantityTextField.setText((new Integer(ex.getQuantity())).toString());
            }
            exampleWindow.setVisible(false);
            mwEventTypeChooserComboBox.setSelectedIndex(ex.getType());
        }
    }

    /**
     * The user changed the type of event. Update GUI accordingly.
     */
    private void mwEventTypeChooserComboBoxChanged() {
        /* show the corresponding input mask */
        switch (mwEventTypeChooserComboBox.getSelectedIndex()) {
            case 0:
                drawEventDataPanel(EpcisEventType.ObjectEvent);
                break;
            case 1:
                drawEventDataPanel(EpcisEventType.AggregationEvent);
                break;
            case 2:
                drawEventDataPanel(EpcisEventType.QuantityEvent);
                break;
            case 3:
                drawEventDataPanel(EpcisEventType.TransactionEvent);
                break;
            default:
        }
        /* update graphics */
        mainWindow.pack();
    }

    /**
     * The user pushed the Generate event-button. This method converts the data
     * from the user interface to XML, POSTs it to the server and displays the
     * answer to the user. It also does some simple client-side checks to see if
     * all necessary fields are filled.
     */
    private void mwGenerateEventButtonPressed() {
        dwOutputTextArea.setText("");
        /* used later for user interaction */
        JFrame frame = new JFrame();

        try {
            /* DOM-tree stuff */
            Document document = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            /* temporary element variable */
            Element element = null;

            /* create empty document and fetch root */
            document = impl.createDocument("urn:epcglobal:epcis:xsd:1", "epcis:EPCISDocument", null);
            Element root = document.getDocumentElement();

            root.setAttribute("creationDate", CaptureClientHelper.format(Calendar.getInstance()));
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xmlns:epcis", "urn:epcglobal:epcis:xsd:1");
            root.setAttribute("schemaVersion", "1.0");
            element = document.createElement("EPCISBody");
            root.appendChild(element);
            root = element;
            element = document.createElement("EventList");
            root.appendChild(element);
            root = element;
            element = document.createElement(EpcisEventType.values()[mwEventTypeChooserComboBox.getSelectedIndex()].name());
            root.appendChild(element);
            root = element;

            // eventTime
            if (!CaptureClientHelper.addEventTime(document, root, mwEventTimeTextField.getText())) {
                JOptionPane.showMessageDialog(frame, "请指定事件发生时间 "
                        + "(e.g. 2011-07-18T17:33:20.231Z)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // eventTimeZoneOffset
            if (!CaptureClientHelper.addEventTimeZoneOffset(document, root, mwEventTimeZoneOffsetTextField.getText())) {
                JOptionPane.showMessageDialog(frame, "请指定时区 "
                        + "(e.g. +00:00 or -06:30)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // recordTime is set by the capture-Interface
            int index = mwEventTypeChooserComboBox.getSelectedIndex();
            if (EpcisEventType.fromGuiIndex(index) == EpcisEventType.ObjectEvent) {
                if (!CaptureClientHelper.addEpcList(document, root, mwEpcListTextField.getText())) {
                    JOptionPane.showMessageDialog(frame, "请指定至少一个 EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CaptureClientHelper.addAction(document, root, (String) mwActionComboBox.getSelectedItem());
                CaptureClientHelper.addBizStep(document, root, mwBizStepTextField.getText());
                CaptureClientHelper.addDisposition(document, root, mwDispositionTextField.getText());
                CaptureClientHelper.addReadPoint(document, root, mwReadPointTextField.getText());
                CaptureClientHelper.addBizLocation(document, root, mwBizLocationTextField.getText());
                CaptureClientHelper.addBizTransactions(document, root, fromGui(mwBizTransIDFields, mwBizTransTypeFields));
            } else if (EpcisEventType.fromGuiIndex(index) == EpcisEventType.AggregationEvent) {
                if (!CaptureClientHelper.addParentId(document, root, mwParentIDTextField.getText()) && !mwActionComboBox.getSelectedItem().equals("OBSERVE")) {
                    JOptionPane.showMessageDialog(frame, "因为操作是 OBSERVE, 因此需要指定 父EPC"
                            , "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!CaptureClientHelper.addChildEpcList(document, root, mwChildEPCsTextField.getText())) {
                    JOptionPane.showMessageDialog(frame, "请指定至少一个 EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CaptureClientHelper.addAction(document, root, (String) mwActionComboBox.getSelectedItem());
                CaptureClientHelper.addBizStep(document, root, mwBizStepTextField.getText());
                CaptureClientHelper.addDisposition(document, root, mwDispositionTextField.getText());
                CaptureClientHelper.addReadPoint(document, root, mwReadPointTextField.getText());
                CaptureClientHelper.addBizLocation(document, root, mwBizLocationTextField.getText());
                CaptureClientHelper.addBizTransactions(document, root, fromGui(mwBizTransIDFields, mwBizTransTypeFields));
            } else if (EpcisEventType.fromGuiIndex(index) == EpcisEventType.QuantityEvent) {
                if (!CaptureClientHelper.addEpcClass(document, root, mwEpcClassTextField.getText())) {
                    JOptionPane.showMessageDialog(frame, "请指定 EPC 分类(URI)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!CaptureClientHelper.addQuantity(document, root, mwQuantityTextField.getText())) {
                    JOptionPane.showMessageDialog(frame, "请指定质量值(int)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CaptureClientHelper.addBizStep(document, root, mwBizStepTextField.getText());
                CaptureClientHelper.addDisposition(document, root, mwDispositionTextField.getText());
                CaptureClientHelper.addReadPoint(document, root, mwReadPointTextField.getText());
                CaptureClientHelper.addBizLocation(document, root, mwBizLocationTextField.getText());
                CaptureClientHelper.addBizTransactions(document, root, fromGui(mwBizTransIDFields, mwBizTransTypeFields));
            } else if (EpcisEventType.fromGuiIndex(index) == EpcisEventType.TransactionEvent) {
                if (!CaptureClientHelper.addBizTransactions(document, root, fromGui(mwBizTransIDFields, mwBizTransTypeFields))) {
                    JOptionPane.showMessageDialog(frame, "请指定至少一个业务事务"
                            + "(ID, Type)", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CaptureClientHelper.addParentId(document, root, mwParentIDTextField.getText());
                if (!CaptureClientHelper.addEpcList(document, root, mwEpcListTextField.getText())) {
                    JOptionPane.showMessageDialog(frame, "请指定至少一个 EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CaptureClientHelper.addAction(document, root, (String) mwActionComboBox.getSelectedItem());
                CaptureClientHelper.addBizStep(document, root, mwBizStepTextField.getText());
                CaptureClientHelper.addDisposition(document, root, mwDispositionTextField.getText());
                CaptureClientHelper.addReadPoint(document, root, mwReadPointTextField.getText());
                CaptureClientHelper.addBizLocation(document, root, mwBizLocationTextField.getText());
            }

            DOMSource domsrc = new DOMSource(document);

            StringWriter out = new StringWriter();
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.transform(domsrc, streamResult);

            String eventXml = out.toString();
            String postData = eventXml;

            dwOutputTextArea.append("发送 POST 数据:\n");
            dwOutputTextArea.append(postData);

            /* connect the service, write out xml and get response */
            int response = client.capture(postData);

            if (response == 200) {
                JOptionPane.showMessageDialog(frame, "捕获请求成功.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "捕获请求失败 (HTTP response code " + response + ").",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (TransformerException te) {
            JOptionPane.showMessageDialog(frame, te.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            te.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(frame, pce.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            pce.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (CaptureClientException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            e.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        }
    }

    private Map<String, String> fromGui(ArrayList<JTextField> bizTransID, ArrayList<JTextField> bizTransType) {
        if (bizTransID == null || bizTransType == null) {
            return null;
        }
        Map<String, String> bizTransMap = new HashMap<String, String>(bizTransID.size());
        for (int i = 0; i < bizTransID.size(); i++) {
            if (i < bizTransID.size() && i < bizTransType.size()) {
                bizTransMap.put(bizTransID.get(i).getText(), bizTransType.get(i).getText());
            }
        }
        return bizTransMap;
    }

    /**
     * Event handler for window manager closing events. Overrides the default,
     * empty method.
     *
     * @param e The WindowEvent.
     * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(final WindowEvent e) {
        if (e.getSource() == debugWindow) {
            mwShowDebugWindowCheckBox.setSelected(false);
            return;
        }
    }

    /**
     * Adds another row at the end of the Business Transactions.
     */
    private void addBizTransactionRow() {
        JTextField bizTransID = new JTextField();
        bizTransID.setToolTipText(CaptureClientHelper.toolTipBizTransID);
        JTextField bizTransType = new JTextField();
        bizTransType.setToolTipText(CaptureClientHelper.toolTipBizTransType);

        ImageIcon tempDelIcon = CaptureClientHelper.getImageIcon("delete10.gif");
        JButton minus = new JButton(tempDelIcon);

        minus.setMargin(new Insets(0, 0, 0, 0));
        minus.addActionListener(this);

        mwBizTransTypeFields.add(bizTransType);
        mwBizTransIDFields.add(bizTransID);
        mwBizTransButtons.add(minus);

        drawBizTransaction();
    }

    /**
     * Removes a row from the Business Transactions.
     *
     * @param button The JButton which generated the event.
     */
    private void removeBizTransactionRow(final JButton button) {
        int toRemove = Integer.parseInt(button.getName().substring(button.getName().length() - 1,
                button.getName().length()));
        mwBizTransTypeFields.remove(toRemove);
        mwBizTransIDFields.remove(toRemove);
        mwBizTransButtons.remove(toRemove);

        drawBizTransaction();
    }

    /**
     * After having added or deleted a Row from the BusinessTransactions, it has
     * to be re-drawn.
     */
    private void drawBizTransaction() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 5, 0);

        mwBizTransactionPanel.removeAll();

        int i = 0;
        c.weightx = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        for (JButton j : mwBizTransButtons) {
            // every name from minus-Buttons has
            // to start with "removeBizTransNumber<Number>"
            j.setName("removeBizTransNumber" + i);
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }

        i = 0;
        c.weightx = 1;
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        for (JTextField j : mwBizTransIDFields) {
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }
        i = 0;
        c.weightx = 1;
        c.gridx = 2;
        c.fill = GridBagConstraints.BOTH;
        for (JTextField j : mwBizTransTypeFields) {
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = i + 1;
        c.fill = GridBagConstraints.NONE;
        mwBizTransactionPanel.add(mwBizTransactionPlus, c);

        // in case of having no BusinessTransactionfields,
        // we need to insert two "null"-elements that the
        // plus-Button is left-aligned.
        if (i == 0) {
            c.weightx = 1;
            c.gridx = 1;
            c.gridy = i + 1;
            c.fill = GridBagConstraints.BOTH;
            mwBizTransactionPanel.add(new JPanel(), c);
            c.weightx = 1;
            c.gridx = 2;
            c.gridy = i + 1;
            mwBizTransactionPanel.add(new JPanel(), c);
        }

        mainWindow.pack();
    }

    /**
     * Instantiates a new CaptureClientGui using a look-and-feel that matches
     * the operating system.
     *
     * @param args The address to which the CaptureClient should send the capture
     *             events. If omitted, a default address will be provided.
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (args != null && args.length > 0) {
            // a default url is given
            new CaptureClientGui(args[0]);
        } else {
            new CaptureClientGui();
        }
    }

    public void configurationChanged(AuthenticationOptionsChangeEvent ace) {
        if (ace.isComplete()) {
            mwGenerateEventButton.setEnabled(true);
            client = new CaptureClient(mwServiceUrlTextField.getText(), mwAuthOptions.getAuthenticationOptions());
        } else {
            mwGenerateEventButton.setEnabled(false);
        }
    }
}
